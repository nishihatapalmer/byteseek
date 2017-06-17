/*
 * Copyright Matt Palmer 2016-17, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 
 *  * The names of its contributors may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package net.byteseek.searcher.sequence;

import java.io.IOException;
import java.nio.charset.Charset;

import net.byteseek.io.reader.WindowReader;
import net.byteseek.io.reader.windows.Window;
import net.byteseek.matcher.sequence.ByteSequenceMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.searcher.SearchIndexSize;
import net.byteseek.utils.lazy.DoubleCheckImmutableLazyObject;
import net.byteseek.utils.lazy.LazyObject;
import net.byteseek.utils.factory.ObjectFactory;

/**
 * An implementation of the QF (Qgram-Filtering) algorithm by
 * Branislav Durian, Hannu Peltola, Leena Salmela and Jorma Tarhio.
 * <p>
 * This algorithm is generally very fast for longer patterns, and quite
 * reasonable for shorter ones.  It will often be the fastest, or very
 * close to the fastest across a variety of alphabet sizes and pattern lengths.
 * It is not faster than ShiftOr for shorter patterns (e.g. 8 or less).
 * <p>
 * The algorithm will default to selecting a hash table size for the pattern passed in up to a maximum of 64K elements.
 * It modifies the original bit-shift hash algorithm specified in the original Qgram-Filtering to allow selecting any
 * power of two size for a hash table.  The original algorithm was limited to few choices of size, which grew rapidly.
 * A trade off is that parts of a qgram will carry less weight in the hash algorithm for certain table sizes.
 * Being able to specify the hash table size lets memory consumption be controlled more precisely, and allows a fairer
 * comparison with other search algorithms.
 * <p>
 * This implementation does an initial q-gram hash and test for a match before setting up an inner loop
 * to test the other q-grams.  This unrolling of the loop allows us to avoid calculating the
 * pattern start pos on the first test, or doing any other loop initialisation most of the time.
 * However, it makes the algorithm look more complex than it really is.  There is no essential difference
 * between the first qgram test made and the subsequent inner loop if the first qgram matches.
 * It is possible to rewrite this in a simpler way with a single main loop
 * (and no initial q-gram match test outside of the inner loop).
 * The unrolled method given here is how the algorithm is presented in the original paper.
 * <p>
 * The algorithm permits q-grams of different lengths to be used.  This implementation uses a q-gram of length 3.
 * Note that if a pattern shorter than the qgram length of 4 is passed in, this algorithm cannot search for it,
 * and a different algorithm (ShiftOr) will be substituted, which is generally fastest for short patterns.
 * ShiftOr creates a table of 256 elements, which in almost all cases will be smaller, or no larger,
 * than the table produced by the QgramFilter3Searcher. It also requires less pre-processing time on average.
 *
 * @author Matt Palmer
 */

public final class QgramFilter3Searcher extends AbstractQgramSearcher {

    /*************
     * Constants *
     *************/

    /**
     * The length of q-grams processed by this searcher.
     */
    public final static int QLEN = 3;


    /**********
     * Fields *
     **********/

    /**
     * A lazy object which can create the information needed to search forwards.
     */
    private final LazyObject<SearchInfo> forwardSearchInfo;

    /**
     * A lazy object which can create the information needed to search backwards.
     */
    private final LazyObject<SearchInfo> backwardSearchInfo;


    /****************
     * Constructors *
     ****************/

    /**
     * Constructs a searcher given a {@link SequenceMatcher} to search for.
     *
     * @param sequence The SequenceMatcher to search for.
     */
    public QgramFilter3Searcher(final SequenceMatcher sequence) {
        this(sequence, DEFAULT_SEARCH_INDEX_SIZE);
    }

    /**
     * Constructs a searcher given a {@link SequenceMatcher} to search for, and the size of the search index to use.
     * <p>
     * If a pattern is too complex to be adequately represented by the available table size,
     * a replacement searcher will be used in place of this algorithm (which is ShiftOR).  This is because if the
     * hash table is too small, the available shifts will be very small too and searching will consequently be
     * very slow.  While ShiftOR isn't particularly fast, it is faster than using this algorithm poorly and
     * does not suffer at all from complexity in the patterns.
     *
     * @param sequence      The SequenceMatcher to search for.
     * @param searchIndexSize  Determines the size of the hash table used by the search algorithm.
     * @throws IllegalArgumentException if the sequence is null or empty, or the searchIndexSize is null.
     */
    public QgramFilter3Searcher(final SequenceMatcher sequence, final SearchIndexSize searchIndexSize) {
        super(sequence, searchIndexSize);
        forwardSearchInfo  = new DoubleCheckImmutableLazyObject<SearchInfo>(new ForwardSearchInfoFactory());
        backwardSearchInfo = new DoubleCheckImmutableLazyObject<SearchInfo>(new BackwardSearchInfoFactory());
    }

    /**
     * Constructs a searcher for the bytes contained in the sequence string,
     * encoded using the platform default character set.
     *
     * @param sequence The string to search for.
     * @throws IllegalArgumentException if the sequence is null or empty.
     */
    public QgramFilter3Searcher(final String sequence) {
        this(sequence, Charset.defaultCharset(), DEFAULT_SEARCH_INDEX_SIZE);
    }

    /**
     * Constructs a searcher for the bytes contained in the sequence string,
     * encoded using the platform default character set, and the size of the search index to use.
     * <p>
     * If a pattern is too complex to be adequately represented by the available table size,
     * a replacement searcher will be used in place of this algorithm (which is ShiftOR).  This is because if the
     * hash table is too small, the available shifts will be very small too and searching will consequently be
     * very slow.  While ShiftOR isn't particularly fast, it is faster than using this algorithm poorly and
     * does not suffer at all from complexity in the patterns.
     *
     * @param sequence The string to search for.
     * @param searchIndexSize  Determines the size of the hash table used by the search algorithm.
     * @throws IllegalArgumentException if the sequence is null or empty or the powerTwoSize is less than -28 or greater than 28.
     */
    public QgramFilter3Searcher(final String sequence, final SearchIndexSize searchIndexSize) {
        this(sequence, Charset.defaultCharset(), searchIndexSize);
    }

    /**
     * Constructs a searcher for the bytes contained in the sequence string,
     * encoded using the charset provided.
     *
     * @param sequence The string to search for.
     * @param charset The charset to encode the string in.
     * @throws IllegalArgumentException if the sequence is null or empty, or the charset is null.
     */
    public QgramFilter3Searcher(final String sequence, final Charset charset) {
        this(sequence == null? null : charset == null? null : new ByteSequenceMatcher(sequence.getBytes(charset)));
    }

    /**
     * Constructs a searcher for the bytes contained in the sequence string,
     * encoded using the charset provided, and the size of the search index to use.
     * <p>
     * If a pattern is too complex to be adequately represented by the available table size,
     * a replacement searcher will be used in place of this algorithm (which is ShiftOR).  This is because if the
     * hash table is too small, the available shifts will be very small too and searching will consequently be
     * very slow.  While ShiftOR isn't particularly fast, it is faster than using this algorithm poorly and
     * does not suffer at all from complexity in the patterns.
     *
     * @param sequence The string to search for.
     * @param charset The charset to encode the string in.
     * @param searchIndexSize  Determines the size of the hash table used by the search algorithm.
     * @throws IllegalArgumentException if the sequence is null or empty, or the charset is null.
     */
    public QgramFilter3Searcher(final String sequence, final Charset charset, final SearchIndexSize searchIndexSize) {
        this(sequence == null? null : charset == null? null : new ByteSequenceMatcher(sequence.getBytes(charset)), searchIndexSize);
    }

    /**
     * Constructs a searcher for the byte array provided.
     *
     * @param sequence The byte sequence to search for.
     * @throws IllegalArgumentException if the sequence is null or empty.
     */
    public QgramFilter3Searcher(final byte[] sequence) {
        this(sequence == null? null : new ByteSequenceMatcher(sequence), DEFAULT_SEARCH_INDEX_SIZE);
    }

    /**
     * Constructs a searcher for the byte array provided, and the size of the search index to use.
     * <p>
     * If a pattern is too complex to be adequately represented by the available table size,
     * a replacement searcher will be used in place of this algorithm (which is ShiftOR).  This is because if the
     * hash table is too small, the available shifts will be very small too and searching will consequently be
     * very slow.  While ShiftOR isn't particularly fast, it is faster than using this algorithm poorly and
     * does not suffer at all from complexity in the patterns.
     *
     * @param sequence The byte sequence to search for.
     * @param searchIndexSize Determines the size of the hash table used by the search algorithm.
     * @throws IllegalArgumentException if the sequence is null or empty, or the charset is null.
     */
    public QgramFilter3Searcher(final byte[] sequence, final SearchIndexSize searchIndexSize) {
        this(sequence == null? null : new ByteSequenceMatcher(sequence), searchIndexSize);
    }


    /******************
     * Search Methods *
     ******************/

    @Override
    protected int doSearchSequenceForwards(final byte[] bytes, final int fromPosition, final int toPosition) {

        // Get local references to member fields which are repeatedly accessed:
        final SequenceMatcher localSequence = sequence;

        // Get the pre-processed data needed to search:
        final SearchInfo info     = forwardSearchInfo.get();
        final int[] BITMASKS      = info.table;
        final int   SHIFT         = info.shift;
        final int   SEARCH_LENGTH = info.finalQgramPos + 1; // length of pattern to search with is one greater than the final qgram position.
        final int   MASK          = BITMASKS.length - 1;    // BITMASKS will always be a power of two size.

        // Determine safe shifts, starts and ends:
        final int SLEN_MINUS_QLEN    = SEARCH_LENGTH - QLEN;
        final int SEARCH_SHIFT       = SLEN_MINUS_QLEN + 1;
        final int SEARCH_START       = (fromPosition > 0? fromPosition : 0) + SLEN_MINUS_QLEN;
        final int TO_END_POS         = toPosition + SEARCH_LENGTH - 1;
        final int LAST_MATCH_POS     = bytes.length - localSequence.length(); // length may not be same as pattern_length for searching.
        final int LAST_TEXT_POSITION = LAST_MATCH_POS + SEARCH_LENGTH - 1;
        final int SEARCH_END         = (TO_END_POS < LAST_TEXT_POSITION? TO_END_POS : LAST_TEXT_POSITION) - QLEN + 1;

        // Search forwards.
        for (int pos = SEARCH_START; pos <= SEARCH_END; pos += SEARCH_SHIFT) {

            // Get the hash for the q-gram in the text aligned with the end of the pattern:
            int qGramHash =                        (bytes[pos + 2] & 0xFF);
            qGramHash     = (qGramHash << SHIFT) + (bytes[pos + 1] & 0xFF);
            qGramHash     = (qGramHash << SHIFT) + (bytes[pos]     & 0xFF);

            // If there is any match to this q-gram in the pattern continue checking, otherwise shift past it.
            int qGramMatch = BITMASKS[qGramHash & MASK];
            MATCH: if (qGramMatch != 0) {

                // Scan back across the other q-grams in the text to see if they also appear in the pattern:
                final int PATTERN_START_POS   = pos - SLEN_MINUS_QLEN;
                final int FIRST_QGRAM_END_POS = PATTERN_START_POS + QLEN - 1;
                for (pos -= QLEN; pos > FIRST_QGRAM_END_POS; pos -= QLEN) {

                    // Get the hash for the q-gram in the text aligned with the next position back:
                    qGramHash =                        (bytes[pos + 2] & 0xFF);
                    qGramHash = (qGramHash << SHIFT) + (bytes[pos + 1] & 0xFF);
                    qGramHash = (qGramHash << SHIFT) + (bytes[pos]     & 0xFF);

                    // If there is no match to the q-gram (in the same phase as the current q-gram match), shift past it.
                    qGramMatch &= BITMASKS[qGramHash & MASK];
                    if (qGramMatch == 0) break MATCH;
                }

                // All complete q-grams in the text matched one somewhere in the pattern.
                // Verify whether we have an actual match in any of the qgram start positions,
                // without going past the last position a match can occur at.
                final int LAST_VERIFY_POS = FIRST_QGRAM_END_POS < LAST_MATCH_POS? FIRST_QGRAM_END_POS : LAST_MATCH_POS;
                for (int matchPos = PATTERN_START_POS; matchPos <= LAST_VERIFY_POS; matchPos++) {
                    if (localSequence.matchesNoBoundsCheck(bytes, matchPos)) {
                        return matchPos;
                    }
                }

                // No match - shift one past the positions we have just verified (loop will add SEARCH_SHIFT)
                pos = FIRST_QGRAM_END_POS;
            }
        }
        return NO_MATCH;
    }

    @Override
    protected long doSearchForwards(final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {
        // Get local references to member fields which are repeatedly accessed:
        final SequenceMatcher localSequence = sequence;

        // Get the pre-processed data needed to search:
        final SearchInfo info     = forwardSearchInfo.get();
        final int[] BITMASKS      = info.table;
        final int   SHIFT         = info.shift;
        final int   SEARCH_LENGTH = info.finalQgramPos + 1; // length  of pattern to search with is one greater than the final qgram pos we use.
        final int   MASK          = BITMASKS.length - 1; // BITMASKS is always a power of two size.

        // Initialise window search:
        final int SLEN_MINUS_QLEN = SEARCH_LENGTH - QLEN;
        final int SEARCH_SHIFT    = SLEN_MINUS_QLEN + 1;
        final long SEARCH_START   = (fromPosition > 0? fromPosition : 0) + SLEN_MINUS_QLEN;
        final long TO_END_POS     = toPosition + SLEN_MINUS_QLEN;

        // Search forwards.
        Window window;
        long pos;
        for (pos = SEARCH_START;
             (window = reader.getWindow(pos)) != null && pos <= TO_END_POS;
             pos += SEARCH_SHIFT) {

            // Get array for current window
            byte[] array = window.getArray();
            final int LAST_WINDOW_POS = window.length() - 1;

            int arrayPos = reader.getWindowOffset(pos);
            int qGramHash;

            // calculate qgram hash, possibly crossing window boundaries if the pos is at the end of a window already.
            switch (LAST_WINDOW_POS - arrayPos) {
                case 0 : { // two hash bytes lie in the next window:
                    if ((qGramHash = reader.readByte(pos + 2)) < 0) {
                        return NO_MATCH; // no window at this furthest position
                    }
                    qGramHash = (qGramHash << SHIFT) + (reader.readByte(pos + 1));
                    break;
                }
                case 1 : { // one hash bytes lie in the next window:
                    if ((qGramHash = reader.readByte(pos + 2)) < 0) {
                        return NO_MATCH; // no window at this furthest position
                    }
                    qGramHash = (qGramHash << SHIFT) + (array[arrayPos + 1] & 0xFF);
                    break;
                }
                default: { // all bytes of qgram are in this window:
                    qGramHash =                        (array[arrayPos + 2] & 0xFF);
                    qGramHash = (qGramHash << SHIFT) + (array[arrayPos + 1] & 0xFF);
                    break;
                }
            }

            // first byte of qgram is always within the current window (position we originally obtained):
            qGramHash = (qGramHash << SHIFT) + (array[arrayPos] & 0xFF);

            // If there is any match to this q-gram in the pattern continue checking, otherwise shift past it (main loop)
            int qGramMatch = BITMASKS[qGramHash & MASK];
            MATCH: if (qGramMatch != 0) {

                // Scan back across the other q-grams in the text to see if they also appear in the pattern:
                final long PATTERN_START_POS   = pos - SLEN_MINUS_QLEN;
                final long FIRST_QGRAM_END_POS = PATTERN_START_POS + QLEN - 1;

                while (pos > FIRST_QGRAM_END_POS) { // Process qgrams while it is safe to go back another qgram.

                    // Calculate the last position we can search in the current window array:
                    // TODO: can a position within the pattern be before the search end...?
                    final long DISTANCE_TO_FIRST_QGRAM_END_POS = pos - FIRST_QGRAM_END_POS;
                    //TODO: bug - we go BACK a qlen if we're at this position, so last position must be QLEN?

                    final int  LAST_ARRAY_SEARCHPOS = DISTANCE_TO_FIRST_QGRAM_END_POS <= arrayPos?
                            (int) (arrayPos - DISTANCE_TO_FIRST_QGRAM_END_POS) : 0;

                    // Search back in the current array for matching q-grams:
                    for (pos -= QLEN, arrayPos -= QLEN; arrayPos >= LAST_ARRAY_SEARCHPOS; pos -= QLEN, arrayPos -= QLEN) {

                        // Get the hash for the q-gram in the text aligned with the next position back:
                        // No hashes here can cross a window boundary since the array search goes back to zero
                        // (or the end of the search), and we already dealt with a potential cross in the first hash.
                        qGramHash =                        (array[arrayPos + 2] & 0xFF);
                        qGramHash = (qGramHash << SHIFT) + (array[arrayPos + 1] & 0xFF);
                        qGramHash = (qGramHash << SHIFT) + (array[arrayPos] & 0xFF);

                        // If there is no match to the q-gram (in the same phase as the current q-gram match), shift past it.
                        qGramMatch &= BITMASKS[qGramHash & MASK];
                        if (qGramMatch == 0) break MATCH;
                    }

                    // If we still have some filtering to go in the previous window...
                    if (pos > FIRST_QGRAM_END_POS) {

                        // Get the previous window:
                        window = reader.getWindow(pos);
                        if (window == null) { // Should not happen given how this method is called, but still test.
                            break MATCH; // cannot match here if there is no previous window to match in.
                        }
                        array = window.getArray();
                        arrayPos = reader.getWindowOffset(pos);

                        // Check for a qgram which crosses into next window:
                        if (window.length() - arrayPos < QLEN) { // qgram crosses into next window:
                            qGramHash =                         reader.readByte(pos + 2);
                            qGramHash = (qGramHash << SHIFT) + (reader.readByte(pos + 1));
                            qGramHash = (qGramHash << SHIFT) + (array[arrayPos] & 0xFF);
                            qGramMatch &= BITMASKS[qGramHash & MASK];
                            if (qGramMatch == 0) break MATCH;
                            pos -= QLEN;
                            arrayPos -= QLEN;
                        }
                    }
                }

                // All complete q-grams in the text matched one somewhere in the pattern.
                // Verify whether we have an actual match in any of the qgram start positions,
                for (long matchPos = PATTERN_START_POS; matchPos <= FIRST_QGRAM_END_POS; matchPos++) {
                    if (localSequence.matches(reader, matchPos)) {
                        return matchPos;
                    }
                }

                // No match - shift one past the positions we have just verified (main loop adds SEARCH_SHIFT)
                pos = FIRST_QGRAM_END_POS;
            }
        }
        return NO_MATCH;
    }

    @Override
    protected int doSearchSequenceBackwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        // Get local references to member fields which are repeatedly accessed:
        final SequenceMatcher localSequence = sequence;

        // Get the pre-processed data needed to search:
        final SearchInfo info   = backwardSearchInfo.get();
        final int[] BITMASKS    = info.table;
        final int   SHIFT       = info.shift;
        final int SEARCH_LENGTH = info.finalQgramPos + 1;
        final int   MASK        = BITMASKS.length - 1; // BITMASKS is always a power of two size.

        // Determine safe shifts, starts and ends:
        final int SLEN_MINUS_QLEN     = SEARCH_LENGTH - QLEN;
        final int SEARCH_SHIFT        = SLEN_MINUS_QLEN + 1;
        final int TWO_QGRAMS_FROM_END = SLEN_MINUS_QLEN - QLEN;
        final int LAST_MATCH_POSITION = bytes.length - localSequence.length();
        final int SEARCH_START        = fromPosition < LAST_MATCH_POSITION? fromPosition : LAST_MATCH_POSITION;
        final int SEARCH_END          = toPosition > 0? toPosition : 0;

        // Search backwards.  pos = place aligned with very start of pattern in the text (beginning of first q-gram).
        for (int pos = SEARCH_START; pos >= SEARCH_END; pos -= SEARCH_SHIFT) {

            // Get the hash for the q-gram in the text aligned with the end of the pattern:
            int qGramHash =                        (bytes[pos    ] & 0xFF);
            qGramHash     = (qGramHash << SHIFT) + (bytes[pos + 1] & 0xFF);
            qGramHash     = (qGramHash << SHIFT) + (bytes[pos + 2] & 0xFF);

            // If there is any match to this q-gram in the pattern continue checking, otherwise shift past it.
            int qGramMatch = BITMASKS[qGramHash & MASK];
            MATCH: if (qGramMatch != 0) {

                // Scan forwards across the other complete sequential q-grams in the text to see if they also appear in the pattern:
                final int PATTERN_START_POS = pos;
                final int LAST_FILTER_POS   = pos + TWO_QGRAMS_FROM_END;

                // Loop needs to stop when there are no more complete q-grams to process.  So pos must be the start position of the
                // last *complete* q-gram in the pattern, and last filter pos is the last position it's safe to add another QLEN.
                for (pos += QLEN; pos <= LAST_FILTER_POS; pos += QLEN) {

                    // Get the hash for the q-gram in the text aligned with the next position back:
                    qGramHash =                        (bytes[pos    ] & 0xFF);
                    qGramHash = (qGramHash << SHIFT) + (bytes[pos + 1] & 0xFF);
                    qGramHash = (qGramHash << SHIFT) + (bytes[pos + 2] & 0xFF);

                    // If there is no match to the q-gram (in the same phase as the current q-gram match), shift past it.
                    qGramMatch &= BITMASKS[qGramHash & MASK];
                    if (qGramMatch == 0) break MATCH;
                }

                // All complete q-grams in the text matched one somewhere in the pattern.
                // Verify whether we have an actual match in any of the qgram start positions:
                final int LAST_VERIFY_POS = PATTERN_START_POS - QLEN + 1; // tests are BACK from the start of the pattern.
                final int LAST_MATCH_POS  = LAST_VERIFY_POS > SEARCH_END? LAST_VERIFY_POS : SEARCH_END;
                for (int matchPos = PATTERN_START_POS; matchPos >= LAST_MATCH_POS; matchPos--) {
                    if (localSequence.matchesNoBoundsCheck(bytes, matchPos)) {
                        return matchPos;
                    }
                }

                // No match - shift one back past the positions we have just verified
                pos = LAST_MATCH_POS - 1 + SEARCH_SHIFT; // main loop then substracts SEARCH_SHIFT, so we have LAST_MATCH_POS - 1.
            }
        }
        return NO_MATCH;
    }

    @Override
    protected long doSearchBackwards(final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {
        // Get local references to member fields which are repeatedly accessed:
        final SequenceMatcher localSequence = sequence;

        // Get the pre-processed data needed to search:
        final SearchInfo info   = backwardSearchInfo.get();
        final int[] BITMASKS    = info.table;
        final int   SHIFT       = info.shift;
        final int SEARCH_LENGTH = info.finalQgramPos + 1;
        final int   MASK        = BITMASKS.length - 1; // BITMASKS is always a power of two size.

        // Initialise window search:
        final int SLEN_MINUS_QLEN = SEARCH_LENGTH - QLEN;
        final int SEARCH_SHIFT    = SLEN_MINUS_QLEN + 1;
        final int TWO_QGRAMS_BACK = SLEN_MINUS_QLEN - QLEN;
        final long SEARCH_END     = toPosition > 0? toPosition : 0;

        // Search backwards, pos is aligned with very start of pattern in the text.
        Window window;
        long pos;
        for (pos = fromPosition;
             (window = reader.getWindow(pos)) != null && pos >= SEARCH_END;
             pos -= SEARCH_SHIFT) {

            // Get array for current window
            byte[] array = window.getArray();
            int arrayPos = reader.getWindowOffset(pos);
            int lastWindowPos = window.length() - 1;

            // calculate qgram hash, possibly crossing into next window:
            // first byte of qgram is always within the current window (position we originally obtained):
            int qGramHash = array[arrayPos] & 0xFF;
            switch (lastWindowPos - arrayPos) {
                case 0 : { // two hash bytes lie in the next window:
                    qGramHash = (qGramHash << SHIFT) + (reader.readByte(pos + 1));
                    qGramHash = (qGramHash << SHIFT) + (reader.readByte(pos + 2));
                    break;
                }
                case 1 : { // one hash bytes lie in the next window:
                    qGramHash = (qGramHash << SHIFT) + (array[arrayPos + 1] & 0xFF);
                    qGramHash = (qGramHash << SHIFT) + (reader.readByte(pos + 2));
                    break;
                }
                default: { // all bytes of qgram are in this window:
                    qGramHash = (qGramHash << SHIFT) + (array[arrayPos + 1] & 0xFF);
                    qGramHash = (qGramHash << SHIFT) + (array[arrayPos + 2] & 0xFF);
                    break;
                }
            }

            // If there is any match to this q-gram in the pattern continue checking, otherwise shift past it (main loop)
            int qGramMatch = BITMASKS[qGramHash & MASK];
            MATCH: if (qGramMatch != 0) {

                // Scan forward across the other q-grams in the text to see if they also appear in the pattern:
                final long PATTERN_START_POS = pos;
                final long LAST_FILTER_POS   = PATTERN_START_POS + TWO_QGRAMS_BACK;

                while (pos <= LAST_FILTER_POS) { // Process all qgrams up to the last position where it's safe to add another QLEN.

                    final int  LAST_SAFE_ARRAY_POS   = lastWindowPos - QLEN - QLEN + 1; // can only search in array up to QLEN - 1 from end.
                    final int  AVAILABLE_IN_ARRAY    = LAST_SAFE_ARRAY_POS - arrayPos;
                    final long REMAINING_SEARCH      = LAST_FILTER_POS - pos;
                    final int  ARRAY_SEARCH_END      = REMAINING_SEARCH < AVAILABLE_IN_ARRAY?
                                     (int) (arrayPos + REMAINING_SEARCH) : LAST_SAFE_ARRAY_POS;

                    // Search forwards in the current array for matching q-grams:
                    for (pos += QLEN, arrayPos += QLEN; arrayPos <= ARRAY_SEARCH_END; pos += QLEN, arrayPos += QLEN) {

                        // Get the hash for the q-gram in the text aligned with the next position back:
                        qGramHash =                        (array[arrayPos    ] & 0xFF);
                        qGramHash = (qGramHash << SHIFT) + (array[arrayPos + 1] & 0xFF);
                        qGramHash = (qGramHash << SHIFT) + (array[arrayPos + 2] & 0xFF);

                        // If there is no match to the q-gram (in the same phase as the current q-gram match), shift past it.
                        qGramMatch &= BITMASKS[qGramHash & MASK];
                        if (qGramMatch == 0) break MATCH;
                    }

                    // Finished processing this window - is there any more to do?
                    if (pos <= LAST_FILTER_POS) {

                        // Check for a qgram which crosses into next window:
                        if (arrayPos <= lastWindowPos) { // qgram crosses into previous window:

                            qGramHash =                         reader.readByte(pos    );
                            qGramHash = (qGramHash << SHIFT) + (reader.readByte(pos + 1));
                            qGramHash = (qGramHash << SHIFT) + (reader.readByte(pos + 2));
                            qGramMatch &= BITMASKS[qGramHash & MASK];
                            if (qGramMatch == 0) break MATCH;
                            pos += QLEN;
                        }

                        // Now we are in another window - get window details:
                        window = reader.getWindow(pos);
                        if (window == null) { // Should not happen given how this method is called, but still test.
                            break MATCH; // cannot match here if there is no next window to match in.
                        }
                        array         = window.getArray();
                        arrayPos      = reader.getWindowOffset(pos);
                        lastWindowPos = window.length() - 1;
                    }
                }

                // All complete q-grams in the text matched one somewhere in the pattern.
                // Verify whether we have an actual match in any of the qgram start positions,
                final long LAST_MATCH_POS = PATTERN_START_POS - QLEN + 1;
                for (long matchPos = PATTERN_START_POS; matchPos >= LAST_MATCH_POS; matchPos--) {
                    if (localSequence.matches(reader, matchPos)) {
                        return matchPos;
                    }
                }

                // No match - shift one past the positions we have just verified (main loop subtracts SEARCH_SHIFT)
                pos = LAST_MATCH_POS - 1 + SEARCH_SHIFT;
            }
        }
        return NO_MATCH;
    }


    /******************
     * Public Methods *
     ******************/

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "(index size:"  + searchIndexSize +
                " forward search info:" + getForwardSearchDescription(forwardSearchInfo) +
                " backward search info:" + getForwardSearchDescription(backwardSearchInfo) +
                " sequence:"    + sequence + ')';
    }


    /*********************
     * Protected methods *
     *********************/

    @Override
    protected boolean fallbackForwards() {
        return forwardSearchInfo.get().table == null;
    }

    @Override
    protected boolean fallbackBackwards() {
        return backwardSearchInfo.get().table == null;
    }


    /*******************
     * Private classes *
     *******************/

    /**
     * A factory for the shift table and hash bitshift needed to search forwards.
     */
    private final class ForwardSearchInfoFactory implements ObjectFactory<SearchInfo> {

        /**
         * Calculates the info required for forwards searching.
         * Determines the optimum hash table size and hash bit shift.
         * Defends against patterns which are too short or which have large qgram classes which would overwhelm the algorithm.
         * Either falls back to the fallback searcher, or chooses to use a smaller part of the pattern in those cases.
         */
        @Override
        public SearchInfo create() {
            // Get local copies of fields:
            final SequenceMatcher localSequence = sequence;

            // If the pattern is shorter than one qgram, or equal to it in length, the fallback searcher will be used instead.
            final int PATTERN_LENGTH = localSequence.length();
            if (PATTERN_LENGTH <= QLEN) {
                return NO_SEARCH_INFO; // no shifts to calculate - fallback searcher will be used if no shifts exist.
            }

            // Calculate how many qgrams we have, but stop if we get to more than we can handle with good performance.
            final int MAX_HASH_POWER_TWO_SIZE = searchIndexSize.getPowerTwoSize();
            final int MAX_QGRAMS = 4 << MAX_HASH_POWER_TWO_SIZE; // 4 times the max table size gives 98% of slots filled.

            int num0;
            int num1 = localSequence.getNumBytesAtPosition(0);
            int num2 = localSequence.getNumBytesAtPosition(1);

            // Scan forwards along the pattern counting qgrams as we go.  If there are too many, we halt processing
            // giving a shorter pattern to search with and a shorter maximum shift.
            int totalQgrams = 0;
            int finalQgramPos = 0;
            for (int qGramEndPos = QLEN - 1; qGramEndPos < PATTERN_LENGTH; qGramEndPos++) {
                // Calculate total qgrams as we scan along:
                num0 = num1; num1 = num2           ;                     // shift byte counts along.
                num2 = localSequence.getNumBytesAtPosition(qGramEndPos); // get next count.
                totalQgrams += (num0 * num1 * num2);

                // If we go beyond the max qgrams, stop further processing.
                if (totalQgrams > MAX_QGRAMS) {
                    finalQgramPos = qGramEndPos - 1; // don't process qgrams that take us beyond the max value.
                    break; // no further value, halt processing of further qgrams. avoids pathological byte classes.
                }
            }

            // If we exceeded max qgrams at the first qgram value, there is nothing we can usefully process
            // with this search algorithm, use fallback searcher instead.
            if (finalQgramPos < QLEN) {
                return NO_SEARCH_INFO; // no shifts to calculate - fallback searcher will be used instead.
            }

            // We have all needed parameters, and aren't falling back - build the search info.
            return buildSearchInfo(getTableSize(totalQgrams), finalQgramPos);
        }

        /**
         * Builds the search info for forwards searching, given the table size to create and the position at
         * which the search info should be calculated from in the pattern.
         *
         * @param TABLE_SIZE     The table size to create.
         * @param qGramEndPos    The position of the end of the qgram to start building the search info from.
         * @return search info for forwards searching.
         */
        private SearchInfo buildSearchInfo(final int TABLE_SIZE, final int qGramEndPos) {
            // Get local copies of fields:
            final SequenceMatcher localSequence = sequence;

            // Determine bit shift for bit shift hash algorithm
            final int HASH_SHIFT = getHashShift(TABLE_SIZE, QLEN);

            // Set up the hash table.
            final int[] BITMASKS = new int[TABLE_SIZE];

            // Set up the key values for hashing as we go along the pattern:
            byte[] bytes0; // first step of processing shifts all the key values along one, so bytes0 = bytes1, ...
            byte[] bytes1 = localSequence.getMatcherForPosition(qGramEndPos    ).getMatchingBytes();
            byte[] bytes2 = localSequence.getMatcherForPosition(qGramEndPos - 1).getMatchingBytes();

            // Process all the qgrams in the pattern from the qGram start pos to one before the end of the pattern.
            int hashValue = -1;
            for (int qGramEnd = qGramEndPos - QLEN + 1; qGramEnd >= 0; qGramEnd--) {
                // Get the byte arrays for the qGram at the current qGramStart:
                bytes0 = bytes1; bytes1 = bytes2;                                          // shift byte arrays along one.
                bytes2 = localSequence.getMatcherForPosition(qGramEnd).getMatchingBytes(); // get next byte array.

                // Calculate the hash value and OR the bitmask with the qgram phase bit:
                final int QGRAM_PHASE_BIT = 1 << (qGramEnd % QLEN);
                hashValue = processQ3Hash(OR_VALUE, QGRAM_PHASE_BIT, BITMASKS, hashValue, HASH_SHIFT,
                                          bytes0, bytes1, bytes2);
            }

            return new SearchInfo(BITMASKS, HASH_SHIFT, qGramEndPos);
        }
    }

    /**
     * A factory for the shift table and hash bitshift needed to search backwards.
     */
    private final class BackwardSearchInfoFactory implements ObjectFactory<SearchInfo> {

        /**
         * Calculates the info required for forwards searching.
         * Determines the optimum hash table size and hash bit shift.
         * Defends against patterns which are too short or which have large qgram classes which would overwhelm the algorithm.
         * Either falls back to the fallback searcher, or chooses to use a smaller part of the pattern in those cases.
         */
        @Override
        public SearchInfo create() {
            // Get local copies of fields:
            final SequenceMatcher localSequence = sequence;

            // If the pattern is shorter than one qgram, or equal to it in length, the fallback searcher will be used instead.
            final int PATTERN_LENGTH = localSequence.length();
            if (PATTERN_LENGTH <= QLEN) {
                return NO_SEARCH_INFO; // no shifts to calculate - fallback searcher will be used if no shifts exist.
            }

            // Calculate how many qgrams we have, but stop if we get to more than we can handle with good performance.
            final int MAX_HASH_POWER_TWO_SIZE = searchIndexSize.getPowerTwoSize();
            final int MAX_QGRAMS = 4 << MAX_HASH_POWER_TWO_SIZE; // 4 times the max table size gives 98% of slots filled.

            int num0;
            int num1 = localSequence.getNumBytesAtPosition(0);
            int num2 = localSequence.getNumBytesAtPosition(1);

            // Scan forwards along the pattern, counting qgrams as we go, stop if there are too many.
            int totalQgrams = 0;
            int finalQgramPos = PATTERN_LENGTH - 1;
            for (int qGramEndPos = QLEN - 1; qGramEndPos < PATTERN_LENGTH; qGramEndPos++) {
                // Calculate total qgrams as we scan along:
                num0 = num1; num1 = num2;                                // shift byte counts along.
                num2 = localSequence.getNumBytesAtPosition(qGramEndPos); // get next count.
                totalQgrams += (num0 * num1 * num2);

                // If we go beyond the max qgrams, stop further processing.
                if (totalQgrams > MAX_QGRAMS) {
                    finalQgramPos = qGramEndPos - 1; // don't process qgrams that take us beyond the max value.
                    break; // no further value, halt processing of further qgrams. avoids pathological byte classes.
                }
            }

            // If we exceeded max qgrams at the first qgram value, there is nothing we can usefully process
            // with this search algorithm, use fallback searcher instead.
            if (finalQgramPos < QLEN - 1) {
                return NO_SEARCH_INFO; // no shifts to calculate - fallback searcher will be used instead.
            }

            // We have all needed parameters, and aren't falling back - build the search info.
            return buildSearchInfo(getTableSize(totalQgrams), finalQgramPos);
        }

        /**
         * Builds the search info for forwards searching, given the table size to create and the position at
         * which the search info should be calculated from in the pattern.
         *
         * @param TABLE_SIZE   The table size to create.
         * @param qGramEndPos  The qgram position to start building the search info from.
         * @return search info for forwards searching.
         */
        private SearchInfo buildSearchInfo(final int TABLE_SIZE, final int qGramEndPos) {
            // Get local copies of fields:
            final SequenceMatcher localSequence = sequence;

            // Determine bit shift for bit shift hash algorithm
            final int HASH_SHIFT = getHashShift(TABLE_SIZE, QLEN);

            // Set up the hash table.
            final int[] BITMASKS = new int[TABLE_SIZE];

            // Set up the byte values for hashing as we go along the pattern:
            byte[] bytes0;
            byte[] bytes1 = localSequence.getMatcherForPosition(0).getMatchingBytes();
            byte[] bytes2 = localSequence.getMatcherForPosition(1).getMatchingBytes();

            // Process all the qgrams in the pattern from the qGram start pos to the qgram end pos (last qgram we processed).
            int hashValue = -1;
            for (int qGramEnd = QLEN - 1; qGramEnd <= qGramEndPos; qGramEnd++) {
                // Get the byte arrays for the qGram at the current qGramStart:
                bytes0 = bytes1; bytes1 = bytes2;                                          // shift byte arrays along one.
                bytes2 = localSequence.getMatcherForPosition(qGramEnd).getMatchingBytes(); // get next byte array.

                // Calculate the hash value and OR the bitmask with the qgram phase bit:
                final int QGRAM_PHASE_BIT = 1 << (qGramEnd % QLEN);
                hashValue = processQ3Hash(OR_VALUE, QGRAM_PHASE_BIT, BITMASKS, hashValue, HASH_SHIFT,
                                          bytes0, bytes1, bytes2);
            }

            return new SearchInfo(BITMASKS, HASH_SHIFT, qGramEndPos);
        }
    }
}
