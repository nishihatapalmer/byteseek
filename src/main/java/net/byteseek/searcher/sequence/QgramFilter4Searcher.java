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
import java.util.Arrays;

import net.byteseek.io.reader.WindowReader;
import net.byteseek.io.reader.windows.Window;
import net.byteseek.matcher.sequence.ByteSequenceMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.utils.ArgUtils;
import net.byteseek.utils.collections.BytePermutationIterator;
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
 * The length of q-grams QLEN = 4, and the bit shift parameter defaults to SHIFT = 3,
 * which gives a table size of 4096 elements, each of which encodes 4 (QLEN) possible
 * 'phases' in which the qgram appears as a bitmask.
 * <p>
 * Other bit shifts (which imply different table sizes) are possible to specify.
 * Larger tables are only advised if the pattern is particularly long (in the
 * thousands of bytes) or have large byte classes, particularly when within a q-gram
 * of one another, as this massively multiplies the permutations of qgrams the
 * filter matches, leading to longer scans on average.
 * <p>
 * This implementation does an initial q-gram hash and test for a match before setting up an inner loop
 * to test the other q-grams.  This unrolling of the loop allows us to avoid calculating the
 * pattern start pos on the first test, or doing any other loop initialisation most of the time.
 * However, it makes the algorithm look more complex than it really is.  There is no essential difference
 * between the first qgram test made and the subsequent inner loop if the first qgram matches.
 * It is possible to rewrite this in a simpler way with a single main loop
 * (and no initial q-gram match test outside of the inner loop).
 * The unrolled method given here is how the algorithm is presented in the original paper.
 *
 * @author Matt Palmer
 */

//TODO: examine performance with large byte classes, grouped and separated.
//TODO: extend to search less than qgram by using naive search (what's left when there's no qgrams).
//      this will remove any limits on what can be searched for.

public final class QgramFilter4Searcher extends AbstractSequenceWindowSearcher<SequenceMatcher> {

    /**
     * The length of q-grams processed by this searcher.
     */
    private final static int QLEN = 4;

    /**
     * The default table size for this searcher is 4096 elements.
     * For most purposes this table size gives good results.  For very long patterns (e.g.
     * in the thousands), or patterns which contain byte classes which match a lot of bytes,
     * then a larger table size may be required.  Smaller table sizes can be efficient for
     * very short simple patterns, however very short patterns are better served by different algorithms
     * such as ShiftOr.  The Qgram filtering algorithm was specifically designed for longer patterns.
     */
    private final static QF4TableSize DEFAULT_TABLE_SIZE = QF4TableSize.SIZE_4K;

    /**
     * The number of bits to shift when calculating the qgram hash.
     */
    private final int SHIFT;

    /**
     * The size of the hash table.
     */
    private final int TABLE_SIZE;

    /**
     * A lazy object which can create the information needed to search.
     * An array of bitmasks is used to determine whether a particular q-gram appears in the pattern.
     */
    private final LazyObject<int[]> searchInfo;

    /**
     * Constructs a searcher given a {@link SequenceMatcher}
     * to search for.
     *
     * @param sequence The SequenceMatcher to search for.
     */
    public QgramFilter4Searcher(final SequenceMatcher sequence) {
        this(sequence, DEFAULT_TABLE_SIZE);
    }

    /**
     * Constructs a searcher given a {@link SequenceMatcher}
     * to search for.
     *
     * @param sequence The SequenceMatcher to search for.
     * @param tableSize The size to use for the bitmask filter table.
     */
    public QgramFilter4Searcher(final SequenceMatcher sequence, final QF4TableSize tableSize) {
        super(sequence);
        ArgUtils.checkNullObject(tableSize, "tableSize");
        SHIFT = tableSize.getShift();
        TABLE_SIZE = tableSize.getTableSize();
        searchInfo = new DoubleCheckImmutableLazyObject<int[]>(new SearchInfoFactory());
    }

    /**
     * Constructs a searcher for the bytes contained in the sequence string,
     * encoded using the platform default character set.
     *
     * @param sequence The string to search for.
     * @throws IllegalArgumentException if the sequence is null or empty.
     */
    public QgramFilter4Searcher(final String sequence) {
        this(sequence, Charset.defaultCharset(), DEFAULT_TABLE_SIZE);
    }

    /**
     * Constructs a searcher for the bytes contained in the sequence string,
     * encoded using the platform default character set.
     *
     * @param sequence The string to search for.
     * @param tableSize The size to use for the bitmask filter table.
     * @throws IllegalArgumentException if the sequence is null or empty.
     */
    public QgramFilter4Searcher(final String sequence, final QF4TableSize tableSize) {
        this(sequence, Charset.defaultCharset(), tableSize);
    }

    /**
     * Constructs a searcher for the bytes contained in the sequence string,
     * encoded using the charset provided.
     *
     * @param sequence The string to search for.
     * @param charset The charset to encode the string in.
     * @throws IllegalArgumentException if the sequence is null or empty, or the charset is null.
     */
    public QgramFilter4Searcher(final String sequence, final Charset charset) {
        this(sequence == null? null : charset == null? null : new ByteSequenceMatcher(sequence.getBytes(charset)));
    }

    /**
     * Constructs a searcher for the bytes contained in the sequence string,
     * encoded using the charset provided.
     *
     * @param sequence The string to search for.
     * @param charset The charset to encode the string in.
     * @param tableSize The size to use for the bitmask filter table.
     * @throws IllegalArgumentException if the sequence is null or empty, or the charset is null.
     */
    public QgramFilter4Searcher(final String sequence, final Charset charset, final QF4TableSize tableSize) {
        this(sequence == null? null : charset == null? null : new ByteSequenceMatcher(sequence.getBytes(charset)), tableSize);
    }

    /**
     * Constructs a searcher for the byte array provided.
     *
     * @param sequence The byte sequence to search for.
     * @throws IllegalArgumentException if the sequence is null or empty.
     */
    public QgramFilter4Searcher(final byte[] sequence) {
        this(sequence == null? null : new ByteSequenceMatcher(sequence), DEFAULT_TABLE_SIZE);
    }

    /**
     * Constructs a searcher for the byte array provided.
     *
     * @param sequence The byte sequence to search for.
     * @param tableSize The size to use for the bitmask filter table.
     * @throws IllegalArgumentException if the sequence is null or empty.
     */
    public QgramFilter4Searcher(final byte[] sequence, final QF4TableSize tableSize) {
        this(sequence == null? null : new ByteSequenceMatcher(sequence), tableSize);
    }

    @Override
    protected int getSequenceLength() {
        return sequence.length();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int searchSequenceForwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        // Get local references to member fields which are repeatedly accessed:
        final SequenceMatcher localSequence = sequence;

        // Get the pre-processed data needed to search:
        final int[] BITMASKS    = searchInfo.get();
        final int   MASK        = TABLE_SIZE - 1;

        // Determine safe shifts, starts and ends:
        final int PATTERN_LENGTH       = localSequence.length();
        final int PATTERN_MINUS_QLEN   = PATTERN_LENGTH - QLEN;
        final int SEARCH_SHIFT         = PATTERN_MINUS_QLEN + 1;
        final int SEARCH_START         = (fromPosition > 0?
                fromPosition : 0) + PATTERN_MINUS_QLEN;
        final int TO_END_POS           = toPosition + PATTERN_LENGTH - 1;
        final int LAST_TEXT_POSITION   = bytes.length - 1;
        final int LAST_MATCH_POS       = bytes.length - PATTERN_LENGTH;
        final int SEARCH_END           = (TO_END_POS < LAST_TEXT_POSITION?
                TO_END_POS : LAST_TEXT_POSITION) - QLEN + 1;

        // Search forwards.
        for (int pos = SEARCH_START; pos <= SEARCH_END; pos += SEARCH_SHIFT) {

            // Get the hash for the q-gram in the text aligned with the end of the pattern:
            int qGramHash =                        (bytes[pos + 3] & 0xFF);
            qGramHash     = (qGramHash << SHIFT) + (bytes[pos + 2] & 0xFF);
            qGramHash     = (qGramHash << SHIFT) + (bytes[pos + 1] & 0xFF);
            qGramHash     = (qGramHash << SHIFT) + (bytes[pos]     & 0xFF);

            // If there is any match to this q-gram in the pattern continue checking, otherwise shift past it.
            int qGramMatch = BITMASKS[qGramHash & MASK];
            MATCH: if (qGramMatch != 0) {

                // Scan back across the other q-grams in the text to see if they also appear in the pattern:
                final int PATTERN_START_POS   = pos - PATTERN_MINUS_QLEN;
                final int FIRST_QGRAM_END_POS = PATTERN_START_POS + QLEN - 1;
                for (pos -= QLEN; pos >= FIRST_QGRAM_END_POS; pos -= QLEN) {

                    // Get the hash for the q-gram in the text aligned with the next position back:
                    qGramHash =                        (bytes[pos + 3] & 0xFF);
                    qGramHash = (qGramHash << SHIFT) + (bytes[pos + 2] & 0xFF);
                    qGramHash = (qGramHash << SHIFT) + (bytes[pos + 1] & 0xFF);
                    qGramHash = (qGramHash << SHIFT) + (bytes[pos]     & 0xFF);

                    // If there is no match to the q-gram (in the same phase as the current q-gram match), shift past it.
                    qGramMatch &= BITMASKS[qGramHash & MASK];
                    if (qGramMatch == 0) break MATCH;
                }

                // All complete q-grams in the text matched one somewhere in the pattern.
                // Verify whether we have an actual match in any of the qgram start positions,
                // without going past the last position a match can occur at.
                final int LAST_VERIFY_POS = FIRST_QGRAM_END_POS < LAST_MATCH_POS?
                        FIRST_QGRAM_END_POS : LAST_MATCH_POS;
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
    public long doSearchForwards(final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {
        // Get local references to member fields which are repeatedly accessed:
        final SequenceMatcher localSequence = sequence;

        // Get the pre-processed data needed to search:
        final int[] BITMASKS =  searchInfo.get();
        final int   MASK     =  TABLE_SIZE - 1;

        // Initialise window search:
        final int PATTERN_LENGTH     = localSequence.length();
        final int PATTERN_MINUS_QLEN = PATTERN_LENGTH - QLEN;
        final int SEARCH_SHIFT       = PATTERN_MINUS_QLEN + 1;
        final long SEARCH_START      = (fromPosition > 0?
                                        fromPosition : 0) + PATTERN_MINUS_QLEN;
        final long TO_END_POS        = toPosition + PATTERN_MINUS_QLEN;

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
            int qGramHash = 0;

            // calculate qgram hash, possibly crossing window boundaries if the pos is at the end of a window already.
            switch (LAST_WINDOW_POS - arrayPos) {
                case 0 : { // three hash bytes lie in the next window:
                    if ((qGramHash = reader.readByte(pos + 3)) < 0) {
                        return NO_MATCH; // no window at this furthest position
                    }
                    qGramHash = (qGramHash << SHIFT) + (reader.readByte(pos + 2));
                    qGramHash = (qGramHash << SHIFT) + (reader.readByte(pos + 1));
                    break;
                }
                case 1 : { // two hash bytes lie in the next window:
                    if ((qGramHash = reader.readByte(pos + 3)) < 0) {
                        return NO_MATCH; // no window at this furthest position
                    }
                    qGramHash = (qGramHash << SHIFT) + (reader.readByte(pos + 2));
                    qGramHash = (qGramHash << SHIFT) + (array[arrayPos + 1] & 0xFF);
                    break;
                }
                case 2 : { // one hash byte lies in the next window:
                    if ((qGramHash = reader.readByte(pos + 3)) < 0) {
                        return NO_MATCH; // no window at this furthest position
                    }
                    qGramHash = (qGramHash << SHIFT) + (array[arrayPos + 2] & 0xFF);
                    qGramHash = (qGramHash << SHIFT) + (array[arrayPos + 1] & 0xFF);
                    break;
                }
                default: { // all bytes of qgram are in this window:
                    qGramHash =                        (array[arrayPos + 3] & 0xFF);
                    qGramHash = (qGramHash << SHIFT) + (array[arrayPos + 2] & 0xFF);
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
                final long PATTERN_START_POS   = pos - PATTERN_MINUS_QLEN;
                final long FIRST_QGRAM_END_POS = PATTERN_START_POS + QLEN - 1;

                while (pos >= FIRST_QGRAM_END_POS) { // Process all qgrams back to the first qgram end pos:

                    // Calculate the last position we can search in the current window array:
                    // TODO: check this works - what about short patterns, pos already smaller that first qgramendpos?
                    // TODO: can a position within the pattern be before the search end...?
                    final long DISTANCE_TO_FIRST_QGRAM_END_POS = pos - FIRST_QGRAM_END_POS;
                    final int  LAST_ARRAY_SEARCHPOS = DISTANCE_TO_FIRST_QGRAM_END_POS <= arrayPos?
                                                (int) (arrayPos - DISTANCE_TO_FIRST_QGRAM_END_POS) : 0;

                    // Search back in the current array for matching q-grams:
                    for (pos -= QLEN, arrayPos -= QLEN;
                         arrayPos >= LAST_ARRAY_SEARCHPOS;
                         pos -= QLEN, arrayPos -= QLEN) {

                        // Get the hash for the q-gram in the text aligned with the next position back:
                        // No hashes here can cross a window boundary since the array search goes back to zero
                        // (or the end of the search), and we already dealt with a potential cross in the first hash.
                        qGramHash = (array[arrayPos + 3] & 0xFF);
                        qGramHash = (qGramHash << SHIFT) + (array[arrayPos + 2] & 0xFF);
                        qGramHash = (qGramHash << SHIFT) + (array[arrayPos + 1] & 0xFF);
                        qGramHash = (qGramHash << SHIFT) + (array[arrayPos] & 0xFF);

                        // If there is no match to the q-gram (in the same phase as the current q-gram match), shift past it.
                        qGramMatch &= BITMASKS[qGramHash & MASK];
                        if (qGramMatch == 0) break MATCH;
                    }

                    // If we still have some filtering to go in the previous window...
                    if (pos >= FIRST_QGRAM_END_POS) {

                        // Get the previous window:
                        window = reader.getWindow(pos);
                        if (window == null) { // Should not happen given how this method is called, but still test.
                            break MATCH; // cannot match here if there is no previous window to match in.
                        }
                        array = window.getArray();
                        arrayPos = reader.getWindowOffset(pos);

                        // Check for a qgram which crosses into next window:
                        if (window.length() - arrayPos < QLEN) { // qgram crosses into next window:
                            qGramHash =                         reader.readByte(pos + 3);
                            qGramHash = (qGramHash << SHIFT) + (reader.readByte(pos + 2));
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


    /**
     * {@inheritDoc}
     */
    @Override
    public int searchSequenceBackwards(final byte[] bytes, final int fromPosition, final int toPosition) {

        // Get local references to member fields which are repeatedly accessed:
        final SequenceMatcher localSequence = sequence;

        // Get the pre-processed data needed to search:
        final int[] BITMASKS    = searchInfo.get();
        final int   MASK        = TABLE_SIZE - 1;

        // Determine safe shifts, starts and ends:
        final int PATTERN_LENGTH        = localSequence.length();
        final int PATTERN_MINUS_QLEN    = PATTERN_LENGTH - QLEN;
        final int LAST_QGRAM_START_POS  = (PATTERN_LENGTH & 0xFFFFFFFC) - QLEN; //TODO: make the intent of this clear.  unnecessary optimisation.
        final int SEARCH_SHIFT          = PATTERN_MINUS_QLEN + 1;
        final int LAST_MATCH_POSITION   = bytes.length - PATTERN_LENGTH;
        final int SEARCH_START          = fromPosition < LAST_MATCH_POSITION?
                                          fromPosition : LAST_MATCH_POSITION;
        final int SEARCH_END            = toPosition > 0?
                                          toPosition : 0;

        //TODO: short byte array may crash (e.g. 3 bytes long, can't fit first qgram).

        // Search backwards.  pos = place aligned with very start of pattern in the text (beginning of first q-gram).
        for (int pos = SEARCH_START; pos >= SEARCH_END; pos -= SEARCH_SHIFT) {

            // Get the hash for the q-gram in the text aligned with the end of the pattern:
            int qGramHash =                        (bytes[pos + 3] & 0xFF);
            qGramHash     = (qGramHash << SHIFT) + (bytes[pos + 2] & 0xFF);
            qGramHash     = (qGramHash << SHIFT) + (bytes[pos + 1] & 0xFF);
            qGramHash     = (qGramHash << SHIFT) + (bytes[pos]     & 0xFF);

            // If there is any match to this q-gram in the pattern continue checking, otherwise shift past it.
            int qGramMatch = BITMASKS[qGramHash & MASK];
            MATCH: if (qGramMatch != 0) {

                // Scan forwards across the other complete sequential q-grams in the text to see if they also appear in the pattern:
                final int patternStartPos = pos;
                final int lastQgramStartPos = pos + LAST_QGRAM_START_POS;

                // Loop needs to stop when there are no more complete q-grams to process.  So pos must be the start position of the
                // last *complete* q-gram in the pattern.
                for (pos += QLEN; pos <= lastQgramStartPos; pos += QLEN) { //TODO: <= or <

                    // Get the hash for the q-gram in the text aligned with the next position back:
                    qGramHash =                        (bytes[pos + 3] & 0xFF);
                    qGramHash = (qGramHash << SHIFT) + (bytes[pos + 2] & 0xFF);
                    qGramHash = (qGramHash << SHIFT) + (bytes[pos + 1] & 0xFF);
                    qGramHash = (qGramHash << SHIFT) + (bytes[pos]     & 0xFF);

                    // If there is no match to the q-gram (in the same phase as the current q-gram match), shift past it.
                    qGramMatch &= BITMASKS[qGramHash & MASK];
                    if (qGramMatch == 0) break MATCH;
                }

                // All complete q-grams in the text matched one somewhere in the pattern.
                // Verify whether we have an actual match in any of the qgram start positions:
                final int lastTestPos = patternStartPos - QLEN + 1; // tests are BACK from the start of the pattern.
                final int lastMatchPos = lastTestPos > SEARCH_END?
                                         lastTestPos : SEARCH_END;
                for (int matchPos = patternStartPos; matchPos >= lastMatchPos; matchPos--) {
                    if (localSequence.matchesNoBoundsCheck(bytes, matchPos)) {
                        return matchPos;
                    }
                }

                // No match - shift one back past the positions we have just verified (main loop then substracts SEARCH_SHIFT)
                pos = lastMatchPos - 1 + SEARCH_SHIFT;
            }
        }
        return NO_MATCH;
    }


    @Override
    public long doSearchBackwards(final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {
        // Get local references to member fields which are repeatedly accessed:
        final SequenceMatcher localSequence = sequence;

        // Get the pre-processed data needed to search:
        final int[] BITMASKS =  searchInfo.get();
        final int   MASK     =  TABLE_SIZE - 1;

        // Initialise window search:
        final int PATTERN_LENGTH     = localSequence.length();
        final int PATTERN_MINUS_QLEN = PATTERN_LENGTH - QLEN;
        final int LAST_QGRAM_START   = (PATTERN_LENGTH & 0xFFFFFFFC) - QLEN; //TODO: make the intent of this clear.  unnecessary optimisation.
        final int SEARCH_SHIFT       = PATTERN_MINUS_QLEN + 1;
        final long SEARCH_START      = fromPosition; // TODO: do we really need another constant for this?  withinLength()?
        final long SEARCH_END        = toPosition > 0?
                                       toPosition : 0;

        // Search backwards, pos is aligned with very start of pattern in the text.
        Window window;
        long pos;
        for (pos = SEARCH_START;
             (window = reader.getWindow(pos)) != null && pos >= SEARCH_END;
             pos -= SEARCH_SHIFT) {

            // Get array for current window
            byte[] array = window.getArray();
            int arrayPos = reader.getWindowOffset(pos);
            int lastWindowPos = window.length() - 1;

            // calculate qgram hash, possibly crossing into next window:
            int qGramHash = 0;
            switch (lastWindowPos - arrayPos) {
                case 0 : { // three hash bytes lie in the next window:
                    if ((qGramHash = reader.readByte(pos + 3)) < 0) { // no window at this furthest position
                        return NO_MATCH;
                    }
                    qGramHash = (qGramHash << SHIFT) + (reader.readByte(pos + 2));
                    qGramHash = (qGramHash << SHIFT) + (reader.readByte(pos + 1));
                    break;
                }
                case 1 : { // two hash bytes lie in the next window:
                    if ((qGramHash = reader.readByte(pos + 3)) < 0) { // no window at this furthest position
                        return NO_MATCH;
                    }
                    qGramHash = (qGramHash << SHIFT) + (reader.readByte(pos + 2));
                    qGramHash = (qGramHash << SHIFT) + (array[arrayPos + 1] & 0xFF);
                    break;
                }
                case 2 : { // one hash byte lies in the next window:
                    if ((qGramHash = reader.readByte(pos + 3)) < 0) { // no window at this furthest position
                        return NO_MATCH;
                    }
                    qGramHash = (qGramHash << SHIFT) + (array[arrayPos + 2] & 0xFF);
                    qGramHash = (qGramHash << SHIFT) + (array[arrayPos + 1] & 0xFF);
                    break;
                }
                default: { // all bytes of qgram are in this window:
                    qGramHash =                        (array[arrayPos + 3] & 0xFF);
                    qGramHash = (qGramHash << SHIFT) + (array[arrayPos + 2] & 0xFF);
                    qGramHash = (qGramHash << SHIFT) + (array[arrayPos + 1] & 0xFF);
                    break;
                }
            }

            // first byte of qgram is always within the current window (position we originally obtained):
            qGramHash = (qGramHash << SHIFT) + (array[arrayPos] & 0xFF);

            // If there is any match to this q-gram in the pattern continue checking, otherwise shift past it (main loop)
            int qGramMatch = BITMASKS[qGramHash & MASK];
            MATCH: if (qGramMatch != 0) {

                // Scan forward across the other q-grams in the text to see if they also appear in the pattern:
                final long PATTERN_START_POS    = pos;
                final long LAST_QGRAM_START_POS = PATTERN_START_POS + LAST_QGRAM_START;

                //TODO: <= or < ... ?
                while (pos <= LAST_QGRAM_START_POS) { // Process all qgrams up to the last qgram start pos:

                    final int  LAST_ARRAY_SEARCH_POS = lastWindowPos - QLEN + 1; // can only search in array up to QLEN - 1 from end.
                    final int  AVAILABLE_IN_ARRAY    = LAST_ARRAY_SEARCH_POS - arrayPos;
                    final long REMAINING_SEARCH      = LAST_QGRAM_START_POS - pos;
                    final int  ARRAY_SEARCH_END      = REMAINING_SEARCH < AVAILABLE_IN_ARRAY?
                                     (int) (arrayPos + REMAINING_SEARCH) : LAST_ARRAY_SEARCH_POS;

                    // Search forwards in the current array for matching q-grams:
                    for (pos += QLEN, arrayPos += QLEN;
                         arrayPos <= ARRAY_SEARCH_END;  //TODO: <= or <?
                         pos += QLEN, arrayPos += QLEN) {

                        try {
                            // Get the hash for the q-gram in the text aligned with the next position back:
                            qGramHash = (array[arrayPos + 3] & 0xFF);
                            qGramHash = (qGramHash << SHIFT) + (array[arrayPos + 2] & 0xFF);
                            qGramHash = (qGramHash << SHIFT) + (array[arrayPos + 1] & 0xFF);
                            qGramHash = (qGramHash << SHIFT) + (array[arrayPos] & 0xFF);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            int i = 0;
                        }

                        // If there is no match to the q-gram (in the same phase as the current q-gram match), shift past it.
                        qGramMatch &= BITMASKS[qGramHash & MASK];
                        if (qGramMatch == 0) break MATCH;
                    }

                    // Finished processing this window - is there any more to do?
                    if (pos <= LAST_QGRAM_START_POS) {  //TODO: <= or =?

                        // Check for a qgram which crosses into next window:
                        if (arrayPos <= lastWindowPos) { // qgram crosses into previous window:
                            qGramHash =                         reader.readByte(pos + 3);
                            qGramHash = (qGramHash << SHIFT) + (reader.readByte(pos + 2));
                            qGramHash = (qGramHash << SHIFT) + (reader.readByte(pos + 1));
                            qGramHash = (qGramHash << SHIFT) + (reader.readByte(pos));
                            qGramMatch &= BITMASKS[qGramHash & MASK];
                            if (qGramMatch == 0) break MATCH;
                            pos += QLEN;
                        }

                        // Now we are in another window - get window details:
                        window = reader.getWindow(pos);
                        if (window == null) { // Should not happen given how this method is called, but still test.
                            break MATCH; // cannot match here if there is no previous window to match in.
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






    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareForwards() {
        searchInfo.get();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareBackwards() {
        searchInfo.get();
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + "[bitshift:" + SHIFT + " sequence:" + sequence + ']';
    }


    /**
     * An enumeration of the valid table sizes for the QF4 Searcher, and the bit shift associated with each.
     * If not specified in the constructor, defaults to a shift of 3, which gives a table size of 4K.
     * The table size refers to the number of elements in the array, not the total memory size of it in bytes,
     * which depends on how the JRE implements those structures on different architectures.
     */
    public enum QF4TableSize {

        //TODO: validate performance assertions in javadoc.
        //TODO: calculate table load for different common string / byte class arrangements to predict
        //      performance (validate against actual tests).

        /**
         * A single element table, giving incredibly poor performance - but using almost no additional memory.
         * If a pattern is long with many byte classes such that it swamps all larger feasible tables, then it may make
         * sense to pick this, since the larger table will contain essentially no more information, but will take a
         * lot more space.  However, there are probably other algorithms which would perform better in this circumstance,
         * e.g. ShiftOR.
         */
        SIZE_1(0),

        /**
         * A very small table of only 16 elements, whose performance is poor even for short patterns.
         * There are few circumstances where this would be an appropriate choice.
         */
        SIZE_16(1),

        /**
         * A small table of 256 elements, which should perform reasonably for short patterns with very small or no byte classes.
         */
        SIZE_256(2),

        /**
         * A good sized table of 4096 elements, giving excellent performance even for long patterns (e.g. 2000), or for
         * shorter patterns containing some byte classes.
         */
        SIZE_4K(3),

        /**
         * A large table of 65536 elements. //TODO: how does this perform?
         */
        SIZE_64K(4),

        /**
         * A very large table of about a million elements.  //TODO: how does this perform?
         */
        SIZE_1M(5),

        /**
         * An extremely large table of roughly 16 million elements. //TODO: how does this perform?  should we even keep it?
         */
        SIZE_16M(6),

        /**
         * A huge table of 256 million elements.  This could easily take a gigabyte of memory or more
         * to just store the table.  //TODO: how does this perform?  should we even keep it?
         */
        SIZE_256M(7);

        private final int shift;

        QF4TableSize(final int shift) {
            this.shift = shift;
        }

        public int getTableSize() {
            return 1 << (QLEN * shift);
        }

        public int getShift() {
            return shift;
        }
    }

    //TODO: tests for all code paths through processing sequences, including single bytes, sequences, including byte classes and gaps.

    /**
     * A factory for the SearchInfo needed to search forwards.
     *
     */
    private final class SearchInfoFactory implements ObjectFactory<int[]> {

        private SearchInfoFactory() {
        }

        /**
         * Calculates the bitmask table which tells us if a particular qgram in the text does not appear in the pattern.
         * It can tell us that a qgram is definitely not in the pattern, but not whether it is in the pattern - false positives are possible.
         * This is like a bloom filter, but using only a single hash function.
         * As soon as we see a qgram which is definitely not in the pattern, we can shift right past it.
         */
        @Override
        public int[] create() {

            // Initialise constants
            final int PATTERN_LENGTH = sequence.length();
            final int MASK           = TABLE_SIZE - 1;
            final int[] BITMASKS     = new int[TABLE_SIZE];

            //TODO: validate Q_GRAM_LIMIT - how does performance change as table fills up?
            //                            - and how does table fill up at this limit?
            // if number of qgrams = table size * 4, that is same as all available positions for all 4 bits.
            // Collisions will reduce this, but it's a starting point for a place where the table starts to become
            // fairly useless.
            final int QGRAM_LIMIT    = TABLE_SIZE * 4;

            // Set initial processing states
            int lastHash = 0;
            boolean haveLastHashValue = false;
            byte[] bytes0;
            byte[] bytes1 = sequence.getMatcherForPosition(PATTERN_LENGTH - 1).getMatchingBytes();
            byte[] bytes2 = sequence.getMatcherForPosition(PATTERN_LENGTH - 2).getMatchingBytes();
            byte[] bytes3 = sequence.getMatcherForPosition(PATTERN_LENGTH - 3).getMatchingBytes();
            long totalQgrams = 0;

            // Process all the qgrams in the pattern back from the end (shouldn't actually make a difference,
            for (int qGramStart = PATTERN_LENGTH - QLEN; qGramStart >= 0; qGramStart--) {

                // Get the byte arrays for the qGram at the current qGramStart:
                final int QGRAM_PHASE_BIT = (1 << ((PATTERN_LENGTH - qGramStart) % QLEN));
                bytes0 = bytes1; bytes1 = bytes2; bytes2 = bytes3;             // shift byte arrays along one.
                bytes3 = sequence.getMatcherForPosition(qGramStart).getMatchingBytes(); // get next byte array.

                // Ensure we don't process too many qgrams unnecessarily, where the number of them exceed the useful table size.
                final long numberOfPermutations = getNumPermutations(bytes0, bytes1, bytes2, bytes3);
                totalQgrams += numberOfPermutations;
                if (totalQgrams > QGRAM_LIMIT) { //TODO: check below - set entries to 1111 or 0000 as the code has it.
                    Arrays.fill(BITMASKS, 0xF); // set all entries to 1111 - they'll be mostly, if not all filled up anyway.
                    break;               // stop further processing.
                }

                // Process the qgram permutations as efficiently as possible:
                if (numberOfPermutations == 1L) { // no permutations to worry about.
                    if (!haveLastHashValue) { // if we don't have a good last hash value, calculate the first 3 elements of it:
                        lastHash =                        (bytes0[0] & 0xFF);
                        lastHash = ((lastHash << SHIFT) + (bytes1[0] & 0xFF));
                        lastHash = ((lastHash << SHIFT) + (bytes2[0] & 0xFF));
                        haveLastHashValue = true;
                    }
                    lastHash     = ((lastHash << SHIFT) + (bytes3[0] & 0xFF)); // calculate the new element of the qgram.
                    BITMASKS[lastHash & MASK] |= QGRAM_PHASE_BIT;
                } else { // more than one permutation to work through.
                    if (haveLastHashValue) { // Then bytes3 must contain all the additional permutations - just go through them.
                        for (final byte permutationValue : bytes3) {
                            final int permutationHash = ((lastHash << SHIFT) + (permutationValue & 0xFF));
                            BITMASKS[permutationHash & MASK] |= QGRAM_PHASE_BIT;
                        }
                        haveLastHashValue = false; // after processing the permutations, we don't have a single last hash value.
                    } else { // permutations may exist anywhere and in more than one place, use a BytePermutationIterator:
                        final BytePermutationIterator qGramPermutations = new BytePermutationIterator(bytes3, bytes2, bytes1, bytes0);
                        while (qGramPermutations.hasNext()) {
                            final byte [] permutationValue = qGramPermutations.next();
                            lastHash =                        (permutationValue[0] & 0xFF);
                            lastHash = ((lastHash << SHIFT) + (permutationValue[1] & 0xFF));
                            lastHash = ((lastHash << SHIFT) + (permutationValue[2] & 0xFF));
                            lastHash = ((lastHash << SHIFT) + (permutationValue[3] & 0xFF));
                            BITMASKS[lastHash & MASK] |= QGRAM_PHASE_BIT;
                        }
                    }
                }
            }
            return BITMASKS;
        }
    }

    private long getNumPermutations(final byte[] values1, final byte[] values2, final byte[] values3, final byte[] values4) {
        return values1.length * values2.length * values3.length * values4.length;
    }

}
