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

import net.byteseek.io.reader.WindowReader;
import net.byteseek.io.reader.windows.Window;
import net.byteseek.matcher.sequence.ByteSequenceMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.utils.collections.BytePermutationIterator;
import net.byteseek.utils.lazy.DoubleCheckImmutableLazyObject;
import net.byteseek.utils.lazy.LazyObject;
import net.byteseek.utils.factory.ObjectFactory;

/**
 * An implementation of the QF (Qgram-Filtering) algorithm by
 * Branislav Durian, Hannu Peltola, Leena Salmela and Jorma Tarhio
 * <p>
 * Length of q-grams QLEN = 4, and bit shift parameter SHIFT = 3, so this is QF43.
 *
 * @author Matt Palmer
 */

public final class QF43Searcher extends AbstractSequenceMatcherSearcher {

    private final static int QLEN       = 4;
    private final static int SHIFT      = 3; //TODO: choose this parameter dynamically to obtain good table size and mask.
    private final static int TABLE_SIZE = (1 << (QLEN * SHIFT)); // two to the power QLEN * SHIFT = 2^12 = 4096.

    private final LazyObject<SearchInfo> forwardInfo;
    private final LazyObject<SearchInfo> backwardInfo;

    /**
     * Constructs a searcher given a {@link SequenceMatcher}
     * to search for.
     *
     * @param sequence The SequenceMatcher to search for.
     */
    public QF43Searcher(final SequenceMatcher sequence) {
        super(sequence);
        forwardInfo  = new DoubleCheckImmutableLazyObject<SearchInfo>(new ForwardInfoFactory());
        backwardInfo = new DoubleCheckImmutableLazyObject<SearchInfo>(new BackwardInfoFactory());
    }

    /**
     * Constructs a searcher for the bytes contained in the sequence string,
     * encoded using the platform default character set.
     *
     * @param sequence The string to search for.
     * @throws IllegalArgumentException if the sequence is null or empty.
     */
    public QF43Searcher(final String sequence) {
        this(sequence, Charset.defaultCharset());
    }

    /**
     * Constructs a searcher for the bytes contained in the sequence string,
     * encoded using the charset provided.
     *
     * @param sequence The string to search for.
     * @param charset The charset to encode the string in.
     * @throws IllegalArgumentException if the sequence is null or empty, or the charset is null.
     */
    public QF43Searcher(final String sequence, final Charset charset) {
        this(sequence == null? null : charset == null? null : new ByteSequenceMatcher(sequence.getBytes(charset)));
    }

    /**
     * Constructs a searcher for the byte array provided.
     *
     * @param sequence The byte sequence to search for.
     * @throws IllegalArgumentException if the sequence is null or empty.
     */
    public QF43Searcher(final byte[] sequence) {
        this(sequence == null? null : new ByteSequenceMatcher(sequence));
    }

    @Override
    public long doSearchForwards(final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {
        // Get local references to member fields which are repeatedly accessed:
        final SequenceMatcher localSequence = sequence;

        // Get the pre-processed data needed to search:
        final SearchInfo info   = forwardInfo.get();
        final int[] bitmasks    = info.getBitmasks();
        final int   MASK        = info.getMask();

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
        final SearchInfo info   = forwardInfo.get();
        final int[] BITMASKS    = info.getBitmasks();
        final int   MASK        = info.getMask();

        // Determine safe start and ends:
        final int LAST_POSITION    = bytes.length - 1;
        final int PATTERN_LENGTH   = localSequence.length();
        final int LAST_PATTERN_POS = PATTERN_LENGTH - 1;
        final int PATTERN_START_OFFSET = PATTERN_LENGTH - QLEN;
        final int SEARCH_SHIFT     = PATTERN_LENGTH - QLEN + 1;
        final int SEARCH_START     = (fromPosition > 0?
                                      fromPosition : 0) + SEARCH_SHIFT - 1;
        final int TO_END_POS       = toPosition + LAST_PATTERN_POS;
        final int SEARCH_END       = (TO_END_POS < LAST_POSITION?
                                      TO_END_POS : LAST_POSITION) - QLEN + 1;

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
                    if (qGramMatch == 0) break MATCH;
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

    /*
    public int searchSequenceForwardsExperiment(final byte[] bytes, final int fromPosition, final int toPosition) {
        // Get local references to member fields which are repeatedly accessed:
        final SequenceMatcher localSequence = sequence;

        // Get the pre-processed data needed to search:
        final SearchInfo info   = forwardInfo.get();
        final int[] BITMASKS    = info.getBitmasks();
        final int   MASK        = info.getMask();

        // Determine safe start and ends:
        final int LASTPOSITION = bytes.length - 1;
        final int MQ1          = localSequence.length() - QLEN + 1;
        final int SEARCH_START = fromPosition > 0 ? fromPosition + MQ1 - 1: MQ1 - 1;

        //TODO: minus QLEN, or minus QLEN + 1...?  seems to miss last position.
        final int SEARCH_END   = toPosition < LASTPOSITION?
                toPosition - QLEN : LASTPOSITION - QLEN;


        // Search forwards, until a match is found or we reach the end of the data to be searched:
        int qGramMatch = ~0;
        for (int pos = SEARCH_START; pos <= SEARCH_END; pos += MQ1, qGramMatch = ~0) {
            final int j = pos - MQ1 + QLEN;
            while ((qGramMatch &= BITMASKS[(((((((bytes[pos + 3] & 0xFF)  << SHIFT) +
                                                 (bytes[pos + 2] & 0xFF)) << SHIFT) +
                                                 (bytes[pos + 1] & 0xFF)) << SHIFT) +
                                                 (bytes[pos    ] & 0xFF)) & MASK]) != 0 && pos > j - QLEN) {
                pos -= QLEN;
            }
            if (pos < j) {
                for (int matchPos = j - QLEN + 1; matchPos <= j; matchPos++) {
                    if (localSequence.matches(bytes, matchPos)) { //TODO: matchesNoBoundsCheck?  Is this safe here?
                        return matchPos;
                    }
                }

            }
        }
        return NO_MATCH;
    }
    */


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
        return getClass().getSimpleName() + "[sequence:" + sequence + ']';
    }

    /**
     * A private class encapsulating the pre-processed data needed by this searcher.
     */
    private static final class SearchInfo {
        private final int[] bitmasks;
        private final int   mask;

        public SearchInfo(int[] bitmasks, int mq1, int mask) {
            this.bitmasks = bitmasks;
            this.mask     = mask;
        }

        public int[] getBitmasks() {return bitmasks;}
        public int getMask() {return mask;}
    }

    private static final class ParameterInfo {


    }


    private final class ParameterFactory implements ObjectFactory<ParameterInfo> {

        private ParameterFactory() {
        }

        @Override
        public ParameterInfo create() {
            return null;
        }
    }

    /**
     * A factory for the SearchInfo needed to search forwards.
     *
     */
    private final class ForwardInfoFactory implements ObjectFactory<SearchInfo> {

        private ForwardInfoFactory() {
        }

        /**
         * Calculates the bitmask table which tells us if a particular qgram in the text does not appear in the pattern.
         * It acts rather like a Bloom filter - it can tell us that a qgram is definitely not in the pattern, but not
         * whether it is in the pattern - false positives are possible.
         * As soon as we see a qgram which is definitely not in the pattern, we can shift right past it.
         */
        @Override
        public SearchInfo create() {

            //TODO: determine table size and SHIFT parameter dynamically from the pattern.

            //TODO: deal with case where number of permutations would cause unacceptable and pointless pre-processing.
            //      For example, 3 any bytes in a row (...) would give us 2^24 permutations to process.
            //      Over some limit, may as well set all table entries to 1111 and be done with it.
            //      search performance will be awful, but nothing is gained pointlessly processing 2^32 hash table
            //      entries in a 4096 byte table in the pre-processing phase.  Once a set limit is exceeded, set
            //      everything to one and stop any further pre-processing.

            final int PATTERN_LENGTH = sequence.length();
            final int MASK           = TABLE_SIZE - 1;
            final int MQ1            = PATTERN_LENGTH - QLEN + 1;
            final int[] B            = new int[TABLE_SIZE];

            int lastHash = 0;
            boolean haveLastHashValue = false;
            byte[] bytes0;
            byte[] bytes1 = sequence.getMatcherForPosition(PATTERN_LENGTH - 1).getMatchingBytes();
            byte[] bytes2 = sequence.getMatcherForPosition(PATTERN_LENGTH - 2).getMatchingBytes();
            byte[] bytes3 = sequence.getMatcherForPosition(PATTERN_LENGTH - 3).getMatchingBytes();

            for (int qGramStart = PATTERN_LENGTH - QLEN; qGramStart >= 0; qGramStart--) {
                bytes0 = bytes1; bytes1 = bytes2; bytes2 = bytes3;             // shift byte arrays along one.
                bytes3 = sequence.getMatcherForPosition(qGramStart).getMatchingBytes(); // get next byte array.
                final int QGRAM_PHASE_BIT = (1 << ((PATTERN_LENGTH - qGramStart) % QLEN));
                if (getNumPermutations(bytes0, bytes1, bytes2, bytes3) == 1L) { // no permutations to worry about.
                    if (!haveLastHashValue) {
                        haveLastHashValue = true;
                        lastHash =                        (bytes0[0] & 0xFF);
                        lastHash = ((lastHash << SHIFT) + (bytes1[0] & 0xFF));
                        lastHash = ((lastHash << SHIFT) + (bytes2[0] & 0xFF));
                    }
                    lastHash     = ((lastHash << SHIFT) + (bytes3[0] & 0xFF));
                    B[lastHash & MASK] |= QGRAM_PHASE_BIT;
                } else { // more than one permutation to work through
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
            return new SearchInfo(B, MQ1, MASK);
        }
    }

    private long getNumPermutations(final byte[] values1, final byte[] values2, final byte[] values3, final byte[] values4) {
        return values1.length * values2.length * values3.length * values4.length;
    }

    /**
     * A factory for the pre-processed data needed to search backwards.
     */
    private final class BackwardInfoFactory implements ObjectFactory<SearchInfo> {

        private BackwardInfoFactory() {
        }

        /**
         * Calculates the safe shifts to use if searching forwards.
         * A safe shift is either the length of the sequence, if the
         * byte does not appear in the {@link SequenceMatcher}, or
         * the shortest distance it appears from the end of the matcher.
         */
        @Override
        public SearchInfo create() {
            return new SearchInfo(null, 0, 0);
        }

    }

}
