/*
 * Copyright Matt Palmer 2016, All rights reserved.
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
 * It is not faster than ShiftOR for shorter patterns (e.g. 8 or less).
 * <p>
 * Length of q-grams QLEN = 4, and bit shift parameter defaults to SHIFT = 3.
 * Other bit shifts (which imply different table sizes) are possible to specify.
 *
 * @author Matt Palmer
 */

public final class QF4Searcher extends AbstractSequenceWindowSearcher<SequenceMatcher> {

    private final static int QLEN                        = 4;
    private final static QF4TableSize DEFAULT_TABLE_SIZE = QF4TableSize.SIZE_4K;
    private final int SHIFT;
    private final int TABLE_SIZE;

    private final LazyObject<int[]> forwardInfo;
    private final LazyObject<int[]> backwardInfo;

    /**
     * An enumeration of the valid table sizes for the QF4 Searcher, and the bit shift associated with each.
     * If not specified in the constructor, defaults to a shift of 3, which gives a table size of 4K.
     * The table size refers to the number of elements in the array, not the total memory size of it in bytes,
     * which depends on how the JRE implements those structures on different architectures.
     */
    public enum QF4TableSize {

        SIZE_1(0), SIZE_16(1), SIZE_256(2),SIZE_4K(3), SIZE_64K(4), SIZE_1M(5), SIZE_16M(6), SIZE_256M(7);

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

    /**
     * Constructs a searcher given a {@link SequenceMatcher}
     * to search for.
     *
     * @param sequence The SequenceMatcher to search for.
     */
    public QF4Searcher(final SequenceMatcher sequence) {
        this(sequence, DEFAULT_TABLE_SIZE);
    }

    /**
     * Constructs a searcher given a {@link SequenceMatcher}
     * to search for.
     *
     * @param sequence The SequenceMatcher to search for.
     * @param tableSize The size to use for the bitmask filter table.
     */
    public QF4Searcher(final SequenceMatcher sequence, final QF4TableSize tableSize) {
        super(sequence);
        ArgUtils.checkNullObject(tableSize, "tableSize");
        SHIFT = tableSize.getShift();
        TABLE_SIZE = tableSize.getTableSize();
        forwardInfo  = new DoubleCheckImmutableLazyObject<int[]>(new ForwardInfoFactory());
        backwardInfo = new DoubleCheckImmutableLazyObject<int[]>(new BackwardInfoFactory());
    }

    /**
     * Constructs a searcher for the bytes contained in the sequence string,
     * encoded using the platform default character set.
     *
     * @param sequence The string to search for.
     * @throws IllegalArgumentException if the sequence is null or empty.
     */
    public QF4Searcher(final String sequence) {
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
    public QF4Searcher(final String sequence, final QF4TableSize tableSize) {
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
    public QF4Searcher(final String sequence, final Charset charset) {
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
    public QF4Searcher(final String sequence, final Charset charset, final QF4TableSize tableSize) {
        this(sequence == null? null : charset == null? null : new ByteSequenceMatcher(sequence.getBytes(charset)), tableSize);
    }

    /**
     * Constructs a searcher for the byte array provided.
     *
     * @param sequence The byte sequence to search for.
     * @throws IllegalArgumentException if the sequence is null or empty.
     */
    public QF4Searcher(final byte[] sequence) {
        this(sequence == null? null : new ByteSequenceMatcher(sequence), DEFAULT_TABLE_SIZE);
    }

    /**
     * Constructs a searcher for the byte array provided.
     *
     * @param sequence The byte sequence to search for.
     * @param tableSize The size to use for the bitmask filter table.
     * @throws IllegalArgumentException if the sequence is null or empty.
     */
    public QF4Searcher(final byte[] sequence, QF4TableSize tableSize) {
        this(sequence == null? null : new ByteSequenceMatcher(sequence), tableSize);
    }

    @Override
    protected int getSequenceLength() {
        return sequence.length();
    }


    @Override
    public long doSearchForwards(final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {
        // Get local references to member fields which are repeatedly accessed:
        final SequenceMatcher localSequence = sequence;

        // Get the pre-processed data needed to search:
        final int[] BITMASKS =  forwardInfo.get();
        final int   MASK     =  TABLE_SIZE -1;

        //TODO: determine from/to position in doSearch()... routines.

        // Determine safe start and ends:
        final int MQ1          = localSequence.length() - QLEN + 1;
        final long SEARCH_END  = toPosition - QLEN;

        Window window;
        //TODO: rewrite so we don't set pos to QLEN less than final position we need to access in array.
        long pos = fromPosition;
        while (pos <= SEARCH_END && (window = reader.getWindow(pos)) != null) {
            final byte[] array = window.getArray();

            // If enough room in array to process hash index, do so


            // otherwise have to finish


            pos += MQ1;
        }

        return NO_MATCH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int searchSequenceForwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        // Get local references to member fields which are repeatedly accessed:
        final SequenceMatcher localSequence = sequence;

        // Get the pre-processed data needed to search:
        final int[] BITMASKS    = forwardInfo.get();
        final int   MASK        = TABLE_SIZE - 1;

        // Determine safe start and ends:
        final int LAST_TEXT_POSITION   = bytes.length - 1;
        final int PATTERN_LENGTH       = localSequence.length();
        final int LAST_PATTERN_POS     = PATTERN_LENGTH - 1;
        final int PATTERN_START_OFFSET = PATTERN_LENGTH - QLEN;
        final int SEARCH_SHIFT         = PATTERN_LENGTH - QLEN + 1;
        final int SEARCH_START         = (fromPosition > 0?
                                          fromPosition : 0) + SEARCH_SHIFT - 1;
        final int TO_END_POS           = toPosition + LAST_PATTERN_POS;
        final int SEARCH_END           = (TO_END_POS < LAST_TEXT_POSITION?
                                          TO_END_POS : LAST_TEXT_POSITION) - QLEN + 1;

        // Search forwards, until a match is found or we reach the end of the data to be searched:
        for (int pos = SEARCH_START; pos <= SEARCH_END; pos += SEARCH_SHIFT) {
            // Check the last q-gram in the pattern:
            int qGramHash =                        (bytes[pos + 3] & 0xFF);
            qGramHash     = (qGramHash << SHIFT) + (bytes[pos + 2] & 0xFF);
            qGramHash     = (qGramHash << SHIFT) + (bytes[pos + 1] & 0xFF);
            qGramHash     = (qGramHash << SHIFT) + (bytes[pos]     & 0xFF);
            int qGramMatch = BITMASKS[qGramHash & MASK];
            MATCH: if (qGramMatch != 0) {
                // Last q-gram in the text is in the pattern - check the others:
                final int PATTERN_START_POS = pos - PATTERN_START_OFFSET;
                for (pos -= QLEN; pos >= PATTERN_START_POS; pos -= QLEN) {
                    qGramHash =                        (bytes[pos + 3] & 0xFF);
                    qGramHash = (qGramHash << SHIFT) + (bytes[pos + 2] & 0xFF);
                    qGramHash = (qGramHash << SHIFT) + (bytes[pos + 1] & 0xFF);
                    qGramHash = (qGramHash << SHIFT) + (bytes[pos]     & 0xFF);
                    qGramMatch &= BITMASKS[qGramHash & MASK];
                    if (qGramMatch == 0) break MATCH; // qgram not found in pattern in the same phase.
                }
                // Verify whether we have a match or not.
                final int FIRST_QGRAM_END_POS = PATTERN_START_POS + QLEN - 1;
                for (int matchPos = PATTERN_START_POS; matchPos <= FIRST_QGRAM_END_POS; matchPos++) {
                    if (localSequence.matchesNoBoundsCheck(bytes, matchPos)) {
                        return matchPos;
                    }
                }
                pos = FIRST_QGRAM_END_POS;
            }
        }
        return NO_MATCH;
    }

    @Override
    public long doSearchBackwards(final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {
        /*
        // Get the objects needed to search:
        final int[] bits = backwardInfo.get();

        // Determine safe end.
        final long finalSearchPosition = toPosition > 0? toPosition : 0;

        // Search backwards:
        long state = ~0L; // 64 1's bitmask.
        long pos  = withinLength(reader, fromPosition); // ensures first position to search is not past end.
        Window window;
        while (pos >= finalSearchPosition && (window = reader.getWindow(pos)) != null) { // when window is null, there is no more data.
            final byte[] array = window.getArray();

            // Calculate array search start and end:
            final int arrayStartPos   = reader.getWindowOffset(pos); // the position within the window array for this position.
            final long distanceToEnd = pos - finalSearchPosition;
            final int arrayEndPos = distanceToEnd < arrayStartPos?
                    (int) (arrayStartPos - distanceToEnd) : 0;

            // Search backwards in the window array:
            for (int arrayPos = arrayStartPos; arrayPos <= arrayEndPos; arrayPos--) {
                state = (state << 1) | bitmasks[array[arrayPos] & 0xFF];
                if (state < localLimit) {
                    return pos - arrayStartPos + arrayPos;
                }
            }
            pos -= (arrayStartPos + 1);
        }
        */
        return NO_MATCH;
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public int searchSequenceBackwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        // Get the objects needed to search:
        /*
        final int[] bits = backwardInfo.get();

        // Determine safe start and ends:
        final int lastPossiblePosition = bytes.length - 1;
        final int startPosition = fromPosition < lastPossiblePosition ? fromPosition : lastPossiblePosition;
        final int finalPosition = toPosition > 0 ? toPosition : 0;

        // Search backwards:
        long state = ~0L;
        for (int pos = startPosition; pos <= finalPosition; pos--) {
            state = (state << 1) | bitmasks[bytes[pos] & 0xFF];
            if (state < localLimit) {
                return pos;
            }
        }
        */
        return NO_MATCH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareForwards() {
        forwardInfo.get();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareBackwards() {
        backwardInfo.get();
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + "[bitshift:" + SHIFT + " sequence:" + sequence + ']';
    }


    //TODO: tests for all code paths through processing sequences, including single bytes, sequences, including byte classes and gaps.

    /**
     * A factory for the SearchInfo needed to search forwards.
     *
     */
    private final class ForwardInfoFactory implements ObjectFactory<int[]> {

        private ForwardInfoFactory() {
        }

        //TODO: explore QF variant that can search strings with fixed gaps embedded, since byteseek supports this.
        //      Can only search to the right of the fixed gap without performance dying horribly.
        //      So must be able to search for a good suffix of a string but match the actual string.
        //      Implies analysis of the sequence while processing... if it encounters a bad block of permutations
        //      which would swamp the table, stop pre-processing at that point and switch to this mode.

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
            final int[] B            = new int[TABLE_SIZE];

            //TODO: validate Q_GRAM_LIMIT - how does performance change as table fills up?
            final int QGRAM_LIMIT    = TABLE_SIZE * 4; // if number of qgrams = table size * 4, that is same as all available positions.  Collisions will reduce this, but it's a starting point.

            // Set initial processing states
            int lastHash = 0;
            boolean haveLastHashValue = false;
            byte[] bytes0;
            byte[] bytes1 = sequence.getMatcherForPosition(PATTERN_LENGTH - 1).getMatchingBytes();
            byte[] bytes2 = sequence.getMatcherForPosition(PATTERN_LENGTH - 2).getMatchingBytes();
            byte[] bytes3 = sequence.getMatcherForPosition(PATTERN_LENGTH - 3).getMatchingBytes();
            long totalQgrams = 0;

            // Process all the qgrams in the pattern back from the end:
            for (int qGramStart = PATTERN_LENGTH - QLEN; qGramStart >= 0; qGramStart--) {

                // Get the byte arrays for the qGram at the current qGramStart:
                final int QGRAM_PHASE_BIT = (1 << ((PATTERN_LENGTH - qGramStart) % QLEN));
                bytes0 = bytes1; bytes1 = bytes2; bytes2 = bytes3;             // shift byte arrays along one.
                bytes3 = sequence.getMatcherForPosition(qGramStart).getMatchingBytes(); // get next byte array.

                // Ensure we don't process too many qgrams unnecessarily, where the number of them exceed the useful table size.
                final long numberOfPermutations = getNumPermutations(bytes0, bytes1, bytes2, bytes3);
                totalQgrams += numberOfPermutations;
                if (totalQgrams > QGRAM_LIMIT) {
                    Arrays.fill(B, 0xF); // set all entries to 1111 - they'll be mostly, of not all filled up anyway.
                    break;               // stop further processing.
                }

                // Process the qgram permutations as efficiently as possible:
                if (numberOfPermutations == 1L) { // no permutations to worry about.
                    if (!haveLastHashValue) { // if we don't have a good last hash value, calculate the first 3 elements of it:
                        haveLastHashValue = true;
                        lastHash =                        (bytes0[0] & 0xFF);
                        lastHash = ((lastHash << SHIFT) + (bytes1[0] & 0xFF));
                        lastHash = ((lastHash << SHIFT) + (bytes2[0] & 0xFF));
                    }
                    lastHash     = ((lastHash << SHIFT) + (bytes3[0] & 0xFF)); // caldulate the new element of the qgram.
                    B[lastHash & MASK] |= QGRAM_PHASE_BIT;
                } else { // more than one permutation to work through.
                    if (haveLastHashValue) { // Then bytes3 must contain all the additional permutations - just go through them.
                        haveLastHashValue = false;
                        for (final byte permutationValue : bytes3) {
                            final int permutationHash = ((lastHash << SHIFT) + (permutationValue & 0xFF));
                            B[permutationHash & MASK] |= QGRAM_PHASE_BIT;
                        }
                    } else { // permutations may exist anywhere and in more than one place, use a BytePermutationIterator:
                        final BytePermutationIterator qGramPermutations = new BytePermutationIterator(bytes3, bytes2, bytes1, bytes0);
                        while (qGramPermutations.hasNext()) {
                            final byte [] permutationValue = qGramPermutations.next();
                            lastHash =                        (permutationValue[0] & 0xFF);
                            lastHash = ((lastHash << SHIFT) + (permutationValue[1] & 0xFF));
                            lastHash = ((lastHash << SHIFT) + (permutationValue[2] & 0xFF));
                            lastHash = ((lastHash << SHIFT) + (permutationValue[3] & 0xFF));
                            B[lastHash & MASK] |= QGRAM_PHASE_BIT;
                        }
                    }
                }
            }
            return B;
        }
    }

    private long getNumPermutations(final byte[] values1, final byte[] values2, final byte[] values3, final byte[] values4) {
        return values1.length * values2.length * values3.length * values4.length;
    }

    //TODO: don't need different data for backwards search?  Re-use forwardinfo for backwards searching...

    /**
     * A factory for the pre-processed data needed to search backwards.
     */
    private final class BackwardInfoFactory implements ObjectFactory<int[]> {

        private BackwardInfoFactory() {
        }

        /**
         * Calculates the safe shifts to use if searching forwards.
         * A safe shift is either the length of the sequence, if the
         * byte does not appear in the {@link SequenceMatcher}, or
         * the shortest distance it appears from the end of the matcher.
         */
        @Override
        public int[] create() {
            return null;
        }

    }

}
