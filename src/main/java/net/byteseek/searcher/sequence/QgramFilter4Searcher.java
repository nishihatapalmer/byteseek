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
 * <p>
 * The algorithm permits q-grams of different lengths to be used.  This implementation uses a q-gram of length 4.
 * Note that if a pattern shorter than the qgram length of 4 is passed in, this algorithm cannot search for it,
 * and a different algorithm (ShiftOr) will be substituted, which is generally fastest for short patterns.
 * ShiftOr creates a table of 256 elements, which in almost all cases will be smaller, or no larger,
 * than the table produced by the QgramFilter4Searcher. It also requires less pre-processing time on average.
 *
 * @author Matt Palmer
 */

//TODO: examine performance with large byte classes, grouped and separated.
//TODO: tests for all code paths through processing sequences, including single bytes, sequences, including byte classes and gaps.

public final class QgramFilter4Searcher extends AbstractSequenceWindowSearcher<SequenceMatcher> {

    /*************
     * Constants *
     *************/

    /**
     * The length of q-grams processed by this searcher.
     */
    public final static int QLEN = 4;

    /**
     * The maximum bitshift supported by this algorithm.
     */
    public final static int MAX_SHIFT = 6; // the maximum shift supported by this algorithm.


    /**
     * The maximum number of qgrams the algorithm can process.
     */
    public final static int MAX_QGRAMS = (3 << (MAX_SHIFT * QLEN));

    /**********
     * Fields *
     **********/

    /**
     * The number of bits to shift when calculating the qgram hash.
     * Along with the qgram length, this determines the size of the hash table needed.
     */
    private final int SHIFT;

    /**
     * The size of the hash table.
     */
    private final int TABLE_SIZE;

    /**
     * A lazy object which can create the information needed to search with a factory when required.
     * An array of bitmasks is used to determine whether a particular q-gram does not appear in the pattern,
     * similar to a bloom filter, but also including the alignment of the qgram in the pattern.
     */
    private final LazyObject<int[]> searchInfo;

    /**
     * A replacement searcher for sequences whose length is less than the qgram length, which this searcher cannot search for.
     */
    private final SequenceSearcher<SequenceMatcher> shortSearcher;


    /*********************************
     * Public static utility methods *
     *********************************/

    //TODO: take into account alphabet size - this influences how fast the table will fill up with distinct qgrams.
    //TODO: or is it that small alphabets simply have a lower number of valid qgrams...?  may need longer qgrams.

    /**
     * Recommends a shift value which will give good performance for this algorithm given a SequenceMatcher.
     * If there is no good shift for the pattern given, zero is returned, which indicates that the algorithm will
     * use the replacement searcher already defined for short patterns (the ShiftOR searcher).
     *
     * @param sequence The SequenceMatcher to recommend a shift for.
     * @return A shift value which should give reasonable performance for that matcher, or
     *         zero which indicates that the replacement searcher will be used instead.
     * @throws IllegalArgumentException if the sequence is null.
     */
    public static int recommendShift(final SequenceMatcher sequence) {
        ArgUtils.checkNullObject(sequence, "sequence");
        final int length = sequence.length();
        if (length < QLEN) { // replacement sequence searcher will be used instead.
            return 0;
        }
        return recommendShift(calculateTotalQgrams(sequence));
    }

    /**
     * Recommends a shift value which will give good performance for this algorithm given a String.
     * If there is no good shift for the pattern given, zero is returned, which indicates that the algorithm will
     * use the replacement searcher already defined for short patterns (the ShiftOR searcher).
     *
     * @param sequence The String to recommend a shift for.
     * @return A shift value which should give reasonable performance for that String, or
     *         zero which indicates that the replacement searcher will be used instead.
     * @throws IllegalArgumentException if the string is null or empty.
     */
    public static int recommendShift(final String sequence) {
        ArgUtils.checkNullOrEmptyString(sequence, "sequence");
        final int length = sequence.length();
        if (length < QLEN) { // replacement sequence searcher will be used instead.
            return 0;
        }
        return recommendShift(length - QLEN + 1);
    }

    /**
     * Recommends a shift value which will give good performance for this algorithm given a byte array.
     * If there is no good shift for the pattern given, zero is returned, which indicates that the algorithm will
     * use the replacement searcher already defined for short patterns (the ShiftOR searcher).
     *
     * @param sequence The byte array to recommend a shift for.
     * @return A shift value which should give reasonable performance for that byte array, or
     *         zero which indicates that the replacement searcher will be used instead.
     * @throws IllegalArgumentException if the byte array is null or empty.
     */
    public static int recommendShift(final byte[] sequence) {
        ArgUtils.checkNullOrEmptyByteArray(sequence, "sequence");
        final int length = sequence.length;
        if (length < QLEN) { // replacement sequence searcher will be used instead.
            return 0;
        }
        return recommendShift(length - QLEN + 1);
    }

    /**
     * Recommends a shift value for a number of qgrams which will give good performance.
     * Returns zero if there is no good shift value which indicates that the replacement searcher will be
     * used instead.
     *
     * @param numQgrams The number of qgrams to be processed.
     * @return A shift value which will give good performance for the algorithm, or zero if there is no good shift value.
     */
    public static int recommendShift(final int numQgrams) {
        if (numQgrams < 1 || numQgrams > MAX_QGRAMS) {
            return 0;
        }
        // Full table size = numQgrams / 4 (since we have QLEN=4 bit positions at each table position)
        // We will target a table which is no more than 50% full to get good performance. //TODO: validate by profiling.
        final int halfFullTable = (numQgrams * 2) / QLEN;
        for (int shift = 1; shift <= MAX_SHIFT; shift++) {
            if (tableSize(shift) >= halfFullTable) {
                return shift;
            }
        }
        return 0; // did not find a good shift value - return zero instead.
    }

    /**
     * Returns the table size produced for a given shift.
     *
     * @param shift The shift to use.
     * @return The table size for that shift.
     */
    public static int tableSize(final int shift) {
        return 1 << (shift * QLEN);
    }

    /**
     * Calculates the total number of qgrams in a SequenceMatcher.
     *
     * @param matcher The SequenceMatcher to calculate the total number of qgrams for.
     * @return The total number of qgrams in the sequence matcher.
     */
    public static int calculateTotalQgrams(final SequenceMatcher matcher) {
        int totalQgrams = 0;
        int qgram0;
        int qgram1 = matcher.getNumBytesAtPosition(0);
        int qgram2 = matcher.getNumBytesAtPosition(1);
        int qgram3 = matcher.getNumBytesAtPosition(2);
        final int length = matcher.length();
        for (int qGramEnd = QLEN - 1; qGramEnd < length; qGramEnd++) {
            qgram0 = qgram1; qgram1 = qgram2; qgram2 = qgram3;
            qgram3 = matcher.getNumBytesAtPosition(qGramEnd);
            totalQgrams += (qgram0 * qgram1 * qgram2 * qgram3);
        }
        return totalQgrams;
    }

    /****************
     * Constructors *
     ****************/

    /**
     * Constructs a searcher given a {@link SequenceMatcher}
     * to search for.
     *
     * @param sequence The SequenceMatcher to search for.
     */
    public QgramFilter4Searcher(final SequenceMatcher sequence) {
        this(sequence, recommendShift(sequence));
    }

    /**
     * Constructs a searcher given a {@link SequenceMatcher} to search for, and the shift which determines
     * the table size used by the searcher.
     * <b>Shifts and table sizes</b>
     * <p>Shift 0 = use replacement searcher instead (ShiftOr)</p>
     * <p>Shift 1 = table size of 16 elements</p>
     * <p>Shift 2 = table size of 256 elements</p>
     * <p>Shift 3 = table size of 4096 elements</p>
     * <p>SHift 4 = table size of 65536 elements</p>
     * <p>Shift 5 = table size of 1048576 elements</p>
     * <p>Shift 6 = table size of 16777216 elements</p>
     * <p>For most purposes a shift of 2 or 3 will be sufficient.</p>
     *    Note that each table element contains 4 bit positions, so the effective storage is 4 * table size.</p>
     *
     * @param sequence The SequenceMatcher to search for.
     * @param shift    The bitshift to use for the hash function.  Determines the table size = 1 << (shift * 4)
     * @throws IllegalArgumentException if the sequence is null or empty or the shift is less than 0 or greater than 6.
     */
    public QgramFilter4Searcher(final SequenceMatcher sequence, final int shift) {
        super(sequence);
        ArgUtils.checkRangeInclusive(shift, 0, MAX_SHIFT, "shift");
        if (sequence.length() >= QLEN && shift > 0) {  // equal to or bigger than a qgram, and shift is valid.
            SHIFT         = shift;
            TABLE_SIZE    = 1 << (shift * QLEN);
            searchInfo    = new DoubleCheckImmutableLazyObject<int[]>(new SearchInfoFactory());
            shortSearcher = null;
        } else {                          // smaller than a qgram - use the shiftOr searcher.
            SHIFT         = 0;
            TABLE_SIZE    = 0;
            searchInfo    = null;
            shortSearcher = new ShiftOrSearcher(sequence);
        }
    }

    /**
     * Constructs a searcher for the bytes contained in the sequence string,
     * encoded using the platform default character set.
     *
     * @param sequence The string to search for.
     * @throws IllegalArgumentException if the sequence is null or empty.
     */
    public QgramFilter4Searcher(final String sequence) {
        this(sequence, Charset.defaultCharset(), recommendShift(sequence));
    }

    /**
     * Constructs a searcher for the bytes contained in the sequence string,
     * encoded using the platform default character set, and the shift which determines the table size.
     * <b>Shifts and table sizes</b>
     * <p>Shift 0 = use replacement searcher instead (ShiftOr)</p>
     * <p>Shift 1 = table size of 16 elements</p>
     * <p>Shift 2 = table size of 256 elements</p>
     * <p>Shift 3 = table size of 4096 elements</p>
     * <p>SHift 4 = table size of 65536 elements</p>
     * <p>Shift 5 = table size of 1048576 elements</p>
     * <p>Shift 6 = table size of 16777216 elements</p>
     * <p>For most purposes a shift of 2 or 3 will be sufficient.</p>
     *    Note that each table element contains 4 bit positions, so the effective storage is 4 * table size.</p>
     *
     * @param sequence The string to search for.
     * @param shift    The bitshift to use for the hash function.  Determines the table size = 1 << (shift * 4)
     * @throws IllegalArgumentException if the sequence is null or empty or the shift is less than 0 or greater than 6.
     */
    public QgramFilter4Searcher(final String sequence, final int shift) {
        this(sequence, Charset.defaultCharset(), shift);
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
     * encoded using the charset provided, and the shift which determines the table size.
     * <b>Shifts and table sizes</b>
     * <p>Shift 0 = use replacement searcher instead (ShiftOr)</p>
     * <p>Shift 1 = table size of 16 elements</p>
     * <p>Shift 2 = table size of 256 elements</p>
     * <p>Shift 3 = table size of 4096 elements</p>
     * <p>SHift 4 = table size of 65536 elements</p>
     * <p>Shift 5 = table size of 1048576 elements</p>
     * <p>Shift 6 = table size of 16777216 elements</p>
     * <p>For most purposes a shift of 2 or 3 will be sufficient.</p>
     * Note that each table element contains 4 bit positions, so the effective storage is 4 * table size.</p>
     *
     * @param sequence The string to search for.
     * @param charset The charset to encode the string in.
     * @param shift    The bitshift to use for the hash function.  Determines the table size = 1 << (shift * 4)
     * @throws IllegalArgumentException if the sequence is null or empty, or the charset is null,
     *                                  or the shift is less than 0 or greater than 6.
     */
    public QgramFilter4Searcher(final String sequence, final Charset charset, final int shift) {
        this(sequence == null? null : charset == null? null : new ByteSequenceMatcher(sequence.getBytes(charset)), shift);
    }

    /**
     * Constructs a searcher for the byte array provided.
     *
     * @param sequence The byte sequence to search for.
     * @throws IllegalArgumentException if the sequence is null or empty.
     */
    public QgramFilter4Searcher(final byte[] sequence) {
        this(sequence == null? null : new ByteSequenceMatcher(sequence), recommendShift(sequence));
    }

    /**
     * Constructs a searcher for the byte array provided and the shift which determines the table size.
     *
     * <b>Shifts and table sizes</b>
     * <p>Shift 0 = use replacement searcher instead (ShiftOr)</p>
     * <p>Shift 1 = table size of 16 elements</p>
     * <p>Shift 2 = table size of 256 elements</p>
     * <p>Shift 3 = table size of 4096 elements</p>
     * <p>SHift 4 = table size of 65536 elements</p>
     * <p>Shift 5 = table size of 1048576 elements</p>
     * <p>Shift 6 = table size of 16777216 elements</p>
     * <p>For most purposes a shift of 2 or 3 will be sufficient.</p>
     * Note that each table element contains 4 bit positions, so the effective storage is 4 * table size.</p>
     *
     * @param sequence The byte sequence to search for.
     * @param shift    The bitshift to use for the hash function.  Determines the table size = 1 << (shift * 4)
     * @throws IllegalArgumentException if the sequence is null or empty, or the charset is null, the shift is less than
     *                                  zero or greater than 6.
     */
    public QgramFilter4Searcher(final byte[] sequence, final int shift) {
        this(sequence == null? null : new ByteSequenceMatcher(sequence), shift);
    }


     /******************
     * Search Methods *
     ******************/

    /**
     * {@inheritDoc}
     */
    @Override
    public int searchSequenceForwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        // If we need to use the short searcher (sequence smaller than a qgram), use it instead:
        if (shortSearcher != null) {
            return shortSearcher.searchSequenceForwards(bytes, fromPosition, toPosition);
        }

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
    public long searchSequenceForwards(final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {
        if (shortSearcher != null) {
            return shortSearcher.searchSequenceForwards(reader, fromPosition, toPosition);
        }
        return super.searchSequenceForwards(reader, fromPosition, toPosition);
    }

    @Override
    public long doSearchForwards(final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {
        // Get local references to member fields which are repeatedly accessed:
        final SequenceMatcher localSequence = sequence;

        // Get the pre-processed data needed to search:
        final int[] BITMASKS  = searchInfo.get();
        final int   MASK      = TABLE_SIZE - 1;

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
            int qGramHash;

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
        // If we need to use the short searcher (sequence smaller than a qgram), use it instead:
        if (shortSearcher != null) {
            return shortSearcher.searchSequenceBackwards(bytes, fromPosition, toPosition);
        }

        // Get local references to member fields which are repeatedly accessed:
        final SequenceMatcher localSequence = sequence;
        final int PATTERN_LENGTH        = localSequence.length();

        // Get the pre-processed data needed to search:
        final int[] BITMASKS    = searchInfo.get();
        final int   MASK        = TABLE_SIZE - 1;

        // Determine safe shifts, starts and ends:
        final int PATTERN_MINUS_QLEN      = PATTERN_LENGTH - QLEN;
        final int SEARCH_SHIFT            = PATTERN_MINUS_QLEN + 1;
        final int TWO_QGRAMS_BACK_AND_ONE = SEARCH_SHIFT - QLEN;
        final int LAST_MATCH_POSITION     = bytes.length - PATTERN_LENGTH;
        final int SEARCH_START            = fromPosition < LAST_MATCH_POSITION?
                                            fromPosition : LAST_MATCH_POSITION;
        final int SEARCH_END              = toPosition > 0?
                                            toPosition : 0;

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
                final int LAST_PATTERN_SEARCH_POS = pos + TWO_QGRAMS_BACK_AND_ONE;

                // Loop needs to stop when there are no more complete q-grams to process.  So pos must be the start position of the
                // last *complete* q-gram in the pattern.
                for (pos += QLEN; pos <= LAST_PATTERN_SEARCH_POS; pos += QLEN) {

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
    public long searchSequenceBackwards(final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {
        if (shortSearcher != null) {
            return shortSearcher.searchSequenceBackwards(reader, fromPosition, toPosition);
        }
        return super.searchSequenceBackwards(reader, fromPosition, toPosition);
    }

    @Override
    public long doSearchBackwards(final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {
        // Get local references to member fields which are repeatedly accessed:
        final SequenceMatcher localSequence = sequence;

        // Get the pre-processed data needed to search:
        final int[] BITMASKS = searchInfo.get();
        final int   MASK     = TABLE_SIZE - 1;

        // Initialise window search:
        final int PATTERN_LENGTH     = localSequence.length();
        final int PATTERN_MINUS_QLEN = PATTERN_LENGTH - QLEN;
        final int SEARCH_SHIFT       = PATTERN_MINUS_QLEN + 1;
        final int TWO_QGRAMS_BACK_AND_ONE = SEARCH_SHIFT - QLEN;
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
            int qGramHash;
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
                final long LAST_PATTERN_SEARCH_POS = PATTERN_START_POS + TWO_QGRAMS_BACK_AND_ONE;

                while (pos <= LAST_PATTERN_SEARCH_POS) { // Process all qgrams up to just moving into the last qgram:

                    final int  LAST_ARRAY_SEARCH_POS = lastWindowPos - QLEN + 1; // can only search in array up to QLEN - 1 from end.
                    final int  AVAILABLE_IN_ARRAY    = LAST_ARRAY_SEARCH_POS - arrayPos;
                    final long REMAINING_SEARCH      = LAST_PATTERN_SEARCH_POS - pos;
                    final int  ARRAY_SEARCH_END      = REMAINING_SEARCH < AVAILABLE_IN_ARRAY?
                                     (int) (arrayPos + REMAINING_SEARCH) : LAST_ARRAY_SEARCH_POS;

                    // Search forwards in the current array for matching q-grams:
                    for (pos += QLEN, arrayPos += QLEN;
                         arrayPos <= ARRAY_SEARCH_END;
                         pos += QLEN, arrayPos += QLEN) {

                        // Get the hash for the q-gram in the text aligned with the next position back:
                        qGramHash = (array[arrayPos + 3] & 0xFF);
                        qGramHash = (qGramHash << SHIFT) + (array[arrayPos + 2] & 0xFF);
                        qGramHash = (qGramHash << SHIFT) + (array[arrayPos + 1] & 0xFF);
                        qGramHash = (qGramHash << SHIFT) + (array[arrayPos] & 0xFF);

                        // If there is no match to the q-gram (in the same phase as the current q-gram match), shift past it.
                        qGramMatch &= BITMASKS[qGramHash & MASK];
                        if (qGramMatch == 0) break MATCH;
                    }

                    // Finished processing this window - is there any more to do?
                    if (pos <= LAST_PATTERN_SEARCH_POS) {

                        // Check for a qgram which crosses into next window:
                        if (arrayPos <= lastWindowPos) { // qgram crosses into previous window:
                            qGramHash = reader.readByte(pos + 3);
                            if (qGramHash < 0) {
                                return NO_MATCH; // No window beyond this one - no match possible.
                            }
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
        return getClass().getSimpleName() + "[tablesize:" + TABLE_SIZE + " sequence:" + sequence + ']';
    }


    @Override
    protected int getSequenceLength() {
        return sequence.length();
    }


    /**
     * A factory for the SearchInfo needed to search forwards.
     *
     */
    private final class SearchInfoFactory implements ObjectFactory<int[]> {

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
            final int QGRAM_LIMIT    = TABLE_SIZE * QLEN;

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
                if (totalQgrams > QGRAM_LIMIT) {
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
                        final BytePermutationIterator qGramPermutations = new BytePermutationIterator(bytes0, bytes1, bytes2, bytes3);
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
