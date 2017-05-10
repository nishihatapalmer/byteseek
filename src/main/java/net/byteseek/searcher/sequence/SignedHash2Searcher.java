/*
 * Copyright Matt Palmer 2017, All rights reserved.
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

import net.byteseek.io.reader.WindowReader;
import net.byteseek.io.reader.windows.Window;
import net.byteseek.matcher.sequence.ByteSequenceMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.utils.ArgUtils;
import net.byteseek.utils.collections.BytePermutationIterator;
import net.byteseek.utils.factory.ObjectFactory;
import net.byteseek.utils.lazy.DoubleCheckImmutableLazyObject;
import net.byteseek.utils.lazy.LazyObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

//TODO :rewrite this.
//      This is most similar to signed wu-manber and uses the hashing from q-gram filtering (which
//      took the hashing technique from Lecroq, who also adapted wu-manber for single pattern searching.
//      They're all related - but this isn't actually a version of Lecroq's algorithm.
/**
 * An implementation of the SignedHash algorithm by Matt Palmer.
 * This is essentially the Wu-Manber algorithm applied to a single pattern instead of multiple patterns.  It uses
 * a hash of a q-gram to look up a safe shift.  Since q-grams appear less frequently than single bytes (as in Horspool),
 * the corresponding shifts can be bigger than in Horspool (which only shifts based on a single byte).  The bigger shifts
 * offset the additional cost of reading more bytes for the q-gram and calculating its hash, leading to better performance.
 *
 * <p>
 * The core algorithm permits q-grams of different lengths to be used.  This implementation uses a q-gram of length 2.
 * Note that if a pattern shorter than the qgram length is passed in, this algorithm cannot search for it,
 * and a different algorithm (ShiftOr) will be substituted, which is generally fastest for short patterns.
 * ShiftOr creates a table of 256 elements, which in most cases will be the same or smaller
 * than the table used by this searcher, and whose pre-processing time is usually faster.
 *
 * Created by Matt Palmer on 06/05/17.
 */
public final class SignedHash2Searcher extends AbstractSequenceWindowSearcher<SequenceMatcher> {

    /*************
     * Constants *
     *************/

    /**
     * The length of q-grams processed by this searcher.
     */
    private final static int QLEN = 2;

    //TODO: validate best default shift for most cases, profile searching.
    /**
     * The default bit shift used by the hash function.  The bitshift and the length of the qgram determine
     * the table size used by the algorithm, with the formula TABLESIZE = 1 << (QLEN * BITSHIFT)
     */
    private final static int DEFAULT_SHIFT = 5;


    /**********
     * Fields *
     **********/

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
     * An array of shifts is used to determine how far it is safe to shift given a qgram seen in the text.
     */
    private final LazyObject<int[]> forwardSearchInfo;
    private final LazyObject<int[]> backwardSearchInfo;

    /**
     * A replacement searcher for sequences whose length is less than the qgram length, which this searcher cannot search for.
     */
    private final SequenceSearcher<SequenceMatcher> shortSearcher;


    /****************
     * Constructors *
     ****************/

    /**
     * Constructs a searcher given a {@link SequenceMatcher} to search for.
     *
     * @param sequence The SequenceMatcher to search for.
     */
    public SignedHash2Searcher(final SequenceMatcher sequence) {
        this(sequence, DEFAULT_SHIFT);
    }

    /**
     * Constructs a searcher given a {@link SequenceMatcher} to search for, given a shift, which determines
     * the table size used.
     * <b>Shifts and table sizes</b>
     * <p>Shift 1 = table size of 2 elements</p>
     * <p>Shift 2 = table size of 16 elements</p>
     * <p>Shift 3 = table size of 64 elements</p>
     * <p>SHift 4 = table size of 256 elements</p>
     * <p>Shift 5 = table size of 1024 elements</p>
     * <p>Shift 6 = table size of 4096 elements</p>
     * <p>Shift 7 = table size of 16384 elements</p>
     * <p>Shift 8 = table size of 65536 elements</p>
     * <p>Shift 9 = table size of 262144 elements</p>
     * <p>Shift 10 = table size of 1048576 elements</p>
     * <p>For most purposes a shift of 4, 5 or 6will be sufficient.</p>
     *
     * @param sequence The SequenceMatcher to search for.
     * @param shift    The bitshift to use for the hash function.  Determines the table size = 1 << (shift * 2)
     * @throws IllegalArgumentException if the sequence is null or empty or the shift is less than 1 or greater than 10.
     */
    public SignedHash2Searcher(final SequenceMatcher sequence, final int shift) {
        super(sequence);
        ArgUtils.checkRangeInclusive(shift, 1, 10, "shift");
        if (sequence.length() >= QLEN) { // equal or bigger to qgram length - searchable by this algorithm
            SHIFT              = shift;
            TABLE_SIZE         = 1 << (shift * QLEN);
            forwardSearchInfo  = new DoubleCheckImmutableLazyObject<int[]>(new ForwardSearchInfoFactory());
            backwardSearchInfo = new DoubleCheckImmutableLazyObject<int[]>(new BackwardSearchInfoFactory());
            shortSearcher      = null;
        } else {                         // smaller than a qgram length - use a different searcher.
            SHIFT = TABLE_SIZE = 0;
            forwardSearchInfo  = null;
            backwardSearchInfo = null;
            shortSearcher      = new ShiftOrSearcher(sequence);
        }
    }

    /**
     * Constructs a searcher for the bytes contained in the sequence string,
     * encoded using the platform default character set.
     *
     * @param sequence The string to search for.
     * @throws IllegalArgumentException if the sequence is null or empty.
     */
    public SignedHash2Searcher(final String sequence) {
        this(sequence, Charset.defaultCharset(), DEFAULT_SHIFT);
    }

    /**
     * Constructs a searcher for the bytes contained in the sequence string,
     * encoded using the platform default character set, with the specified shift which determines the table size.
     * <b>Shifts and table sizes</b>
     * <p>Shift 1 = table size of 2 elements</p>
     * <p>Shift 2 = table size of 16 elements</p>
     * <p>Shift 3 = table size of 64 elements</p>
     * <p>SHift 4 = table size of 256 elements</p>
     * <p>Shift 5 = table size of 1024 elements</p>
     * <p>Shift 6 = table size of 4096 elements</p>
     * <p>Shift 7 = table size of 16384 elements</p>
     * <p>Shift 8 = table size of 65536 elements</p>
     * <p>Shift 9 = table size of 262144 elements</p>
     * <p>Shift 10 = table size of 1048576 elements</p>
     * <p>For most purposes a shift of 4, 5 or 6 will be sufficient.</p>
     *
     * @param sequence The string to search for.
     * @param shift    The bitshift to use for the hash function.  Determines the table size = 1 << (shift * 2)
     * @throws IllegalArgumentException if the sequence is null or empty or the shift is less than 1 or greater than 10.
     */
    public SignedHash2Searcher(final String sequence, final int shift) {
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
    public SignedHash2Searcher(final String sequence, final Charset charset) {
        this(sequence == null? null : charset == null? null : new ByteSequenceMatcher(sequence.getBytes(charset)));
    }

    /**
     * Constructs a searcher for the bytes contained in the sequence string,
     * encoded using the charset provided, and the shift which determines the table size to use:
     * <b>Shifts and table sizes</b>
     * <p>Shift 1 = table size of 2 elements</p>
     * <p>Shift 2 = table size of 16 elements</p>
     * <p>Shift 3 = table size of 64 elements</p>
     * <p>SHift 4 = table size of 256 elements</p>
     * <p>Shift 5 = table size of 1024 elements</p>
     * <p>Shift 6 = table size of 4096 elements</p>
     * <p>Shift 7 = table size of 16384 elements</p>
     * <p>Shift 8 = table size of 65536 elements</p>
     * <p>Shift 9 = table size of 262144 elements</p>
     * <p>Shift 10 = table size of 1048576 elements</p>
     * <p>For most purposes a shift of 4, 5 or 6will be sufficient.</p>
     *
     * @param sequence The string to search for.
     * @param charset The charset to encode the string in.
     * @param shift    The bitshift to use for the hash function.  Determines the table size = 1 << (shift * 2)
     * @throws IllegalArgumentException if the sequence is null or empty, or the charset is null.
     */
    public SignedHash2Searcher(final String sequence, final Charset charset, final int shift) {
        this(sequence == null? null : charset == null? null : new ByteSequenceMatcher(sequence.getBytes(charset)), shift);
    }

    /**
     * Constructs a searcher for the byte array provided.
     *
     * @param sequence The byte sequence to search for.
     * @throws IllegalArgumentException if the sequence is null or empty.
     */
    public SignedHash2Searcher(final byte[] sequence) {
        this(sequence == null? null : new ByteSequenceMatcher(sequence), DEFAULT_SHIFT);
    }

    /**
     * Constructs a searcher for the byte array provided and the shift which determines the table size to use.
     * <b>Shifts and table sizes</b>
     * <p>Shift 1 = table size of 2 elements</p>
     * <p>Shift 2 = table size of 16 elements</p>
     * <p>Shift 3 = table size of 64 elements</p>
     * <p>SHift 4 = table size of 256 elements</p>
     * <p>Shift 5 = table size of 1024 elements</p>
     * <p>Shift 6 = table size of 4096 elements</p>
     * <p>Shift 7 = table size of 16384 elements</p>
     * <p>Shift 8 = table size of 65536 elements</p>
     * <p>Shift 9 = table size of 262144 elements</p>
     * <p>Shift 10 = table size of 1048576 elements</p>
     * <p>For most purposes a shift of 4, 5 or 6 will be sufficient.</p>
     *
     * @param sequence The byte sequence to search for.
     * @param shift    The bitshift to use for the hash function.  Determines the table size = 1 << (shift * 2)
     * @throws IllegalArgumentException if the sequence is null or empty, or the charset is null.
     */
    public SignedHash2Searcher(final byte[] sequence, final int shift) {
        this(sequence == null? null : new ByteSequenceMatcher(sequence), shift);
    }


    /******************
     * Search Methods *
     ******************/

    @Override
    public int searchSequenceForwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        // Do we need to use the short searcher (sequence is less than qgram length)?
        if (shortSearcher != null) {
            return shortSearcher.searchSequenceForwards(bytes, fromPosition, toPosition);
        }

        // Get local copies of member fields
        final SequenceMatcher localSequence = sequence;
        final int             localshift    = SHIFT;

        // Get the pre-processed data needed to search:
        final int[] SHIFTS = forwardSearchInfo.get();
        final int   MASK   = TABLE_SIZE - 1;

        // Determine safe shifts, starts and ends:
        final int LAST_PATTERN_POS     = localSequence.length() - 1;
        final int DATA_END_POS         = bytes.length - 1;
        final int LAST_SEARCH_POS      = toPosition + LAST_PATTERN_POS;
        final int SEARCH_END           = LAST_SEARCH_POS < DATA_END_POS?
                LAST_SEARCH_POS : DATA_END_POS;
        final int SEARCH_START         = fromPosition > 0?
                fromPosition : 0;

        // Search forwards:
        int searchPos = SEARCH_START + LAST_PATTERN_POS; // look at the end of the pattern to determine shift.
        while (searchPos <= SEARCH_END) {

            // Calculate hash of qgram:
            int hash =                        (bytes[searchPos - 1] & 0xFF);
            hash     = (hash << localshift) + (bytes[searchPos    ] & 0xFF);

            // Get the shift for this qgram:
            final int shift = SHIFTS[hash & MASK];
            if (shift > 0) {        // If we have a positive shift,
                searchPos += shift; // just shift forwards - no match here.
            } else {                // A negative shift means the last qgram may match.
                final int matchPos = searchPos - LAST_PATTERN_POS;
                if (localSequence.matchesNoBoundsCheck(bytes, matchPos)) { // validate whether we have a match.
                    return matchPos;
                }
                searchPos -= shift; // shift forwards by subtracting the negative shift.
            }
        }
        return NO_MATCH;
    }

    @Override
    public long searchSequenceForwards(final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {
        return shortSearcher == null?         super.searchSequenceForwards(reader, fromPosition, toPosition)
                : shortSearcher.searchSequenceForwards(reader, fromPosition, toPosition);
    }

    @Override
    protected long doSearchForwards(final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {
        // Get local copies of member fields
        final SequenceMatcher localSequence = sequence;
        final int             localshift    = SHIFT;

        // Get the pre-processed data needed to search:
        final int[] SHIFTS = forwardSearchInfo.get();
        final int   MASK   = TABLE_SIZE - 1;

        // Determine safe shifts, starts and ends:
        final int LAST_PATTERN_POS = localSequence.length() - 1;
        final int LAST_QGRAM_POS   = QLEN - 1;
        final long SEARCH_END      = toPosition + LAST_PATTERN_POS;
        long searchPos             = (fromPosition > 0?
                fromPosition : 0) + LAST_PATTERN_POS;
        // Search forwards:
        Window window;
        while (searchPos <= SEARCH_END && (window = reader.getWindow(searchPos)) != null) {

            // Get window array info:
            final byte[] array       = window.getArray();
            final int    arrayEndPos = window.length() - 1;
            int          arrayPos    = reader.getWindowOffset(searchPos);

            // Calculate array search end positions:
            final long DISTANCE_TO_END   = SEARCH_END - searchPos;
            final int REMAINING_IN_ARRAY = arrayEndPos - arrayPos;
            final int LAST_ARRAY_POS     = DISTANCE_TO_END < REMAINING_IN_ARRAY?
                                     (int) DISTANCE_TO_END + arrayPos : arrayEndPos;

            // Search forwards if there is still anything to search in this array:
            while (arrayPos <= LAST_ARRAY_POS) {

                // Calculate hash:
                int hash;
                if (arrayPos <= LAST_QGRAM_POS) {
                    hash =                            reader.readByte(searchPos - 1);
                    hash     = (hash << localshift) + (array[arrayPos] & 0xFF);
                } else {
                    hash =                        (array[arrayPos - 1] & 0xFF);
                    hash = (hash << localshift) + (array[arrayPos] & 0xFF);
                }

                // Get shift and either shift forwards, or verify then shift
                final int shift = SHIFTS[hash & MASK];
                if (shift > 0) {
                    arrayPos  += shift;
                    searchPos += shift;
                } else {
                    final long matchPos = searchPos - LAST_PATTERN_POS;
                    if (localSequence.matches(reader, matchPos)) {
                        return matchPos;
                    }
                    arrayPos  -= shift;
                    searchPos -= shift;
                }
            }
        }
        return NO_MATCH;
    }

    @Override
    public int searchSequenceBackwards(byte[] bytes, int fromPosition, int toPosition) {
        // Do we need to use the short searcher (sequence is less than qgram length)?
        if (shortSearcher != null) {
            return shortSearcher.searchSequenceBackwards(bytes, fromPosition, toPosition);
        }

        // Get local copies of member fields
        final SequenceMatcher localSequence = sequence;
        final int             localshift    = SHIFT;

        // Get the pre-processed data needed to search:
        final int[] SHIFTS = backwardSearchInfo.get();
        final int   MASK   = TABLE_SIZE - 1;

        // Determine safe shifts, starts and ends:
        final int LAST_MATCH_POS = bytes.length - localSequence.length();
        final int SEARCH_START   = fromPosition < LAST_MATCH_POS?
                fromPosition : LAST_MATCH_POS;
        final int SEARCH_END     = toPosition > 0?
                toPosition : 0;

        // Search backwards:
        int searchPos = SEARCH_START;
        while (searchPos >= SEARCH_END) {

            // Calculate hash of qgram:
            int hash =                        (bytes[searchPos + 1] & 0xFF);
            hash     = (hash << localshift) + (bytes[searchPos    ] & 0xFF);

            // Get the shift for this qgram:
            final int shift = SHIFTS[hash & MASK];
            if (shift > 0) {        // If we have a positive shift,
                searchPos -= shift; // just shift backwards - no match here.
            } else {                // A negative shift means the last qgram may match.
                if (localSequence.matchesNoBoundsCheck(bytes, searchPos)) { // validate whether we have a match.
                    return searchPos;
                }
                searchPos += shift; // shift backwards by adding the negative shift.
            }
        }
        return NO_MATCH;
    }

    @Override
    public long searchSequenceBackwards(final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {
        return shortSearcher == null?         super.searchSequenceBackwards(reader, fromPosition, toPosition)
                : shortSearcher.searchSequenceBackwards(reader, fromPosition, toPosition);
    }

    @Override
    protected long doSearchBackwards(final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {
        // Get local copies of member fields
        final SequenceMatcher localSequence = sequence;
        final int             localshift    = SHIFT;

        // Get the pre-processed data needed to search:
        final int[] SHIFTS = backwardSearchInfo.get();
        final int   MASK   = TABLE_SIZE - 1;

        // Determine safe shifts, starts and ends:
        final long SEARCH_START = fromPosition; // TODO: withinLength?  needed for doSearchBackwards?
        final long SEARCH_END   = toPosition > 0?
                toPosition : 0;

        // Search forwards:
        Window window;
        long searchPos = SEARCH_START;
        while (searchPos >= SEARCH_END && (window = reader.getWindow(searchPos)) != null) {

            // Get window array info:
            final byte[] array               = window.getArray();
            final int    WINDOW_LENGTH       = window.length();

            // Calculate safe starts and ends:
            final int    CROSSOVER_QGRAM_POS = WINDOW_LENGTH - QLEN + 1;
            final long DISTANCE_TO_END       = SEARCH_END - window.getWindowPosition();
            final int  LAST_ARRAY_POS        = DISTANCE_TO_END > 0?
                    (int) DISTANCE_TO_END : 0;

            // Search backwards if there is still anything to search in this array:
            int arrayPos = reader.getWindowOffset(searchPos);
            while (arrayPos >= LAST_ARRAY_POS) {

                // Calculate hash:
                int hash;
                if (arrayPos >= CROSSOVER_QGRAM_POS) { // crosses over into next window?
                    hash =                         reader.readByte(searchPos + 1);
                    hash = (hash << localshift) + (array[arrayPos] & 0xFF);
                } else {
                    hash =                        (array[arrayPos + 1] & 0xFF);
                    hash = (hash << localshift) + (array[arrayPos] & 0xFF);
                }

                // Get shift and either shift forwards, or verify then shift
                final int shift = SHIFTS[hash & MASK];
                if (shift > 0) {
                    arrayPos  -= shift;
                    searchPos -= shift;
                } else {
                    if (localSequence.matches(reader, searchPos)) {
                        return searchPos;
                    }
                    arrayPos  += shift;
                    searchPos += shift;
                }
            }
        }
        return NO_MATCH;
    }


    @Override
    public void prepareForwards() {
        forwardSearchInfo.get();
    }

    @Override
    public void prepareBackwards() {
        backwardSearchInfo.get();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[tablesize:" + TABLE_SIZE + " sequence:" + sequence + ']';
    }

    @Override
    protected int getSequenceLength() {
        return sequence.length();
    }


    /*******************
     * Private classes *
     *******************/

    /**
     * A factory for a short sequence searcher, to fill in for sequences with a length less than the Qgram length.
     * <p>
     * <b>Design Note</b>
     * <p>
     * This allows a developer to pass any valid pattern into this search algorithm without error.  The alternative is
     * to throw an IllegalArgumentException in the constructor if the pattern is too short, but this could easily
     * lead to errors in user applications, since patterns are commonly supplied by the user, not the programmer.
     * <p>
     * While this decision violates the principle that these search algorithms are primitives and should not make high
     * level decisions on behalf of the programmer, it seems the lesser of two evils to make it safe to use any search
     * algorithm with any valid pattern, even if occasionally you don't quite get the algorithm you thought you specified.
     * <p>
     * Given this, we choose to supply the fastest known algorithm for short patterns (ShiftOr),
     * rather than one which is more spiritually similar to this algorithm (e.g. the SignedHorspoolSearcher), or the
     * simplest possible algorithm (e.g. the SequenceMatcherSearcher).
     */
    private final class ShortSearcherFactory implements ObjectFactory<SequenceSearcher<SequenceMatcher>> {

        @Override
        public SequenceSearcher<SequenceMatcher> create() {
            return new ShiftOrSearcher(sequence);  // the fastest searcher for short patterns.
        }
    }

    /**
     * A factory for the shift table needed to search forwards.
     */
    private final class ForwardSearchInfoFactory implements ObjectFactory<int[]> {

        /**
         * Calculates the shift table for forwards searching.
         */
        @Override
        public int[] create() {

            // Initialise constants
            final int   PATTERN_LENGTH   = sequence.length();
            final int   MASK             = TABLE_SIZE - 1;
            final int[] SHIFTS           = new int[TABLE_SIZE];
            final int   MAX_PERMUTATIONS = TABLE_SIZE; // TODO: validate.

            // Initialise shift table to the max possible shift
            // The maximum shift isn't the pattern length, as in Horspool, because we are reading qgrams.
            // The max shift will align the start of the pattern to the position one past the start of the last qgram read.
            // TODO: check for pathological cases where there's no point in processing bits of the pattern
            //       by scanning back from the end to find high permutation values.
            final int LAST_PATTERN_POS = PATTERN_LENGTH - 1;
            final int MAX_SHIFT        = PATTERN_LENGTH - QLEN + 1;
            Arrays.fill(SHIFTS, MAX_SHIFT);

            // Set initial processing states
            int lastHash = 0;
            boolean haveLastHashValue = false;
            byte[] bytes0;
            byte[] bytes1 = sequence.getMatcherForPosition(0).getMatchingBytes();

            // Process all the qgrams in the pattern from the start to one before the end of the pattern.
            for (int qGramEnd = QLEN - 1; qGramEnd < LAST_PATTERN_POS; qGramEnd++) {

                // Calcluate shift for qgrams at this position:
                final int CURRENT_SHIFT = LAST_PATTERN_POS - qGramEnd;

                // Get the byte arrays for the qGram at the current qGramStart:
                bytes0 = bytes1;                                                      // shift byte arrays along one.
                bytes1 = sequence.getMatcherForPosition(qGramEnd).getMatchingBytes(); // get next byte array.

                // Ensure we don't process too many permutations in pathological cases where multiple byte classes
                // are adjacent to each other.  If there are too many, just set all shifts to the current value.
                final long numberOfPermutations = getNumPermutations(bytes0, bytes1);
                if (numberOfPermutations > MAX_PERMUTATIONS) {  // too many permutations to bother processing.
                    Arrays.fill(SHIFTS, CURRENT_SHIFT);         // just set the entire table to the current shift.
                    haveLastHashValue = false;
                } else {
                    // Process the qgram permutations as efficiently as possible:
                    if (numberOfPermutations == 1L) { // no permutations to worry about.
                        if (!haveLastHashValue) { // if we don't have a good last hash value, calculate the first 3 elements of it:
                            lastHash =                        (bytes0[0] & 0xFF);
                            haveLastHashValue = true;
                        }
                        lastHash = ((lastHash << SHIFT) + (bytes1[0] & 0xFF)); // calculate the new element of the qgram.
                        SHIFTS[lastHash & MASK] = CURRENT_SHIFT;
                    } else { // more than one permutation to work through.
                        if (haveLastHashValue) { // Then bytes1 must contain all the additional permutations - just go through them.
                            for (final byte permutationValue : bytes1) {
                                final int permutationHash = ((lastHash << SHIFT) + (permutationValue & 0xFF));
                                SHIFTS[permutationHash & MASK] = CURRENT_SHIFT;
                            }
                            haveLastHashValue = false; // after processing the permutations, we don't have a single last hash value.
                        } else { // permutations may exist anywhere and in more than one place, use a BytePermutationIterator:
                            final BytePermutationIterator qGramPermutations = new BytePermutationIterator(bytes0, bytes1);
                            while (qGramPermutations.hasNext()) {
                                final byte[] permutationValue = qGramPermutations.next();
                                lastHash =                        (permutationValue[0] & 0xFF);
                                lastHash = ((lastHash << SHIFT) + (permutationValue[1] & 0xFF));
                                SHIFTS[lastHash & MASK] = CURRENT_SHIFT;
                            }
                        }
                    }
                }
            }

            // Make shifts for the last qgrams in the pattern negative:

            // Get byte arrays for last q-gram:
            bytes0 = bytes1;                                                              // shift byte arrays along one.
            bytes1 = sequence.getMatcherForPosition(LAST_PATTERN_POS).getMatchingBytes(); // get last byte array.

            // Ensure number of permutations aren't excessive:
            final long numberOfPermutations = getNumPermutations(bytes0, bytes1);
            if (numberOfPermutations > MAX_PERMUTATIONS) {
                // too many permutations to bother processing, all entries become negative:
                for (int tableIndex = 0; tableIndex < SHIFTS.length; tableIndex++) {
                    SHIFTS[tableIndex] = -SHIFTS[tableIndex];
                }
            } else {
                // Process the qgram permutations as efficiently as possible:
                if (numberOfPermutations == 1L) { // no permutations to worry about.
                    if (!haveLastHashValue) {     // if we don't have a good last hash value, calculate the first 3 elements of it:
                        lastHash =                        (bytes0[0] & 0xFF);
                    }
                    lastHash = ((lastHash << SHIFT) + (bytes1[0] & 0xFF)); // calculate the new element of the qgram.
                    SHIFTS[lastHash & MASK] = -SHIFTS[lastHash & MASK];
                } else { // more than one permutation to work through.
                    if (haveLastHashValue) { // Then bytes3 must contain all the additional permutations - just go through them.
                        for (final byte permutationValue : bytes1) {
                            final int permutationHash = ((lastHash << SHIFT) + (permutationValue & 0xFF));
                            final int currentShift = SHIFTS[permutationHash & MASK];
                            if (currentShift > 0) {
                                SHIFTS[permutationHash & MASK] = -currentShift;
                            }
                        }
                    } else { // permutations may exist anywhere and in more than one place, use a BytePermutationIterator:
                        final BytePermutationIterator qGramPermutations = new BytePermutationIterator(bytes0, bytes1);
                        while (qGramPermutations.hasNext()) {
                            final byte[] permutationValue = qGramPermutations.next();
                            lastHash =                        (permutationValue[0] & 0xFF);
                            lastHash = ((lastHash << SHIFT) + (permutationValue[1] & 0xFF));
                            final int currentShift = SHIFTS[lastHash & MASK];
                            if (currentShift > 0) {
                                SHIFTS[lastHash & MASK] = -currentShift;
                            }
                        }
                    }
                }
            }
            return SHIFTS;
        }
    }

    /**
     * A factory for the shift table needed to search forwards.
     */
    private final class BackwardSearchInfoFactory implements ObjectFactory<int[]> {


        //TODO: finish - just a copy of the forward search info right now.
        /**
         * Calculates the shift table for backwards searching.
         */
        @Override
        public int[] create() {

            // Initialise constants
            final int   PATTERN_LENGTH   = sequence.length();
            final int   MASK             = TABLE_SIZE - 1;
            final int[] SHIFTS           = new int[TABLE_SIZE];
            final int   MAX_PERMUTATIONS = TABLE_SIZE; // TODO: validate.

            // Initialise shift table to the max possible shift
            // The maximum shift isn't the pattern length, as in Horspool, because we are reading qgrams.
            // The max shift will align the start of the pattern to the position one past the start of the last qgram read.
            // TODO: check for pathological cases where there's no point in processing bits of the pattern
            //       by scanning back from the end to find high permutation values.
            final int LAST_PATTERN_POS = PATTERN_LENGTH - 1;
            final int MAX_SHIFT        = PATTERN_LENGTH - QLEN + 1;
            Arrays.fill(SHIFTS, MAX_SHIFT);

            // Set initial processing states
            int lastHash = 0;
            boolean haveLastHashValue = false;
            byte[] bytes0;
            byte[] bytes1 = sequence.getMatcherForPosition(LAST_PATTERN_POS    ).getMatchingBytes();

            // Process all the qgrams in the pattern from the end to one after the start of the pattern.
            for (int qGramEnd = PATTERN_LENGTH - QLEN; qGramEnd > 0; qGramEnd--) {

                // Calcluate shift for qgrams at this position:
                final int CURRENT_SHIFT = qGramEnd; // TODO: check calculation for backwards match.

                // Get the byte arrays for the qGram at the current qGramStart:
                bytes0 = bytes1;                                                      // shift byte arrays along one.
                bytes1 = sequence.getMatcherForPosition(qGramEnd).getMatchingBytes(); // get next byte array.

                // Ensure we don't process too many permutations in pathological cases where multiple byte classes
                // are adjacent to each other.  If there are too many, just set all shifts to the current value.
                final long numberOfPermutations = getNumPermutations(bytes0, bytes1);
                if (numberOfPermutations > MAX_PERMUTATIONS) {  // too many permutations to bother processing.
                    Arrays.fill(SHIFTS, CURRENT_SHIFT);         // just set the entire table to the current shift.
                    haveLastHashValue = false;
                } else {
                    // Process the qgram permutations as efficiently as possible:
                    if (numberOfPermutations == 1L) { // no permutations to worry about.
                        if (!haveLastHashValue) { // if we don't have a good last hash value, calculate the first 3 elements of it:
                            lastHash =                        (bytes0[0] & 0xFF);
                            haveLastHashValue = true;
                        }
                        lastHash = ((lastHash << SHIFT) + (bytes1[0] & 0xFF)); // calculate the new element of the qgram.
                        SHIFTS[lastHash & MASK] = CURRENT_SHIFT;
                    } else { // more than one permutation to work through.
                        if (haveLastHashValue) { // Then bytes1 must contain all the additional permutations - just go through them.
                            for (final byte permutationValue : bytes1) {
                                final int permutationHash = ((lastHash << SHIFT) + (permutationValue & 0xFF));
                                SHIFTS[permutationHash & MASK] = CURRENT_SHIFT;
                            }
                            haveLastHashValue = false; // after processing the permutations, we don't have a single last hash value.
                        } else { // permutations may exist anywhere and in more than one place, use a BytePermutationIterator:
                            final BytePermutationIterator qGramPermutations = new BytePermutationIterator(bytes0, bytes1);
                            while (qGramPermutations.hasNext()) {
                                final byte[] permutationValue = qGramPermutations.next();
                                lastHash =                        (permutationValue[0] & 0xFF);
                                lastHash = ((lastHash << SHIFT) + (permutationValue[1] & 0xFF));
                                SHIFTS[lastHash & MASK] = CURRENT_SHIFT;
                            }
                        }
                    }
                }
            }

            // Make shifts for the last qgrams in the pattern negative:

            // Get byte arrays for last q-gram:
            bytes0 = bytes1;                                               // shift byte arrays along one.
            bytes1 = sequence.getMatcherForPosition(0).getMatchingBytes(); // get first byte array.

            // Ensure number of permutations aren't excessive:
            final long numberOfPermutations = getNumPermutations(bytes0, bytes1);
            if (numberOfPermutations > MAX_PERMUTATIONS) {
                // too many permutations to bother processing, all entries become negative:
                for (int tableIndex = 0; tableIndex < SHIFTS.length; tableIndex++) {
                    SHIFTS[tableIndex] = -SHIFTS[tableIndex];
                }
            } else {
                // Process the qgram permutations as efficiently as possible:
                if (numberOfPermutations == 1L) { // no permutations to worry about.
                    if (!haveLastHashValue) {     // if we don't have a good last hash value, calculate the first 3 elements of it:
                        lastHash = (bytes0[0] & 0xFF);
                    }
                    lastHash = ((lastHash << SHIFT) + (bytes1[0] & 0xFF)); // calculate the new element of the qgram.
                    SHIFTS[lastHash & MASK] = -SHIFTS[lastHash & MASK];
                } else { // more than one permutation to work through.
                    if (haveLastHashValue) { // Then bytes1 must contain all the additional permutations - just go through them.
                        for (final byte permutationValue : bytes1) {
                            final int permutationHash = ((lastHash << SHIFT) + (permutationValue & 0xFF));
                            final int currentShift = SHIFTS[permutationHash & MASK];
                            if (currentShift > 0) {
                                SHIFTS[permutationHash & MASK] = -currentShift;
                            }
                        }
                    } else { // permutations may exist anywhere and in more than one place, use a BytePermutationIterator:
                        final BytePermutationIterator qGramPermutations = new BytePermutationIterator(bytes0, bytes1);
                        while (qGramPermutations.hasNext()) {
                            final byte[] permutationValue = qGramPermutations.next();
                            lastHash =                        (permutationValue[0] & 0xFF);
                            lastHash = ((lastHash << SHIFT) + (permutationValue[1] & 0xFF));
                            final int currentShift = SHIFTS[lastHash & MASK];
                            if (currentShift > 0) {
                                SHIFTS[lastHash & MASK] = -currentShift;
                            }
                        }
                    }
                }
            }
            return SHIFTS;
        }
    }

    /*******************
     * Utility methods *
     *******************/

    private long getNumPermutations(final byte[] values1, final byte[] values2) {
        return values1.length * values2.length;
    }

}
