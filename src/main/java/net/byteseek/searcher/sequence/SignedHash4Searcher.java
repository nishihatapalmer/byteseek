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
import net.byteseek.searcher.SearchIndexSize;
import net.byteseek.utils.ByteUtils;
import net.byteseek.utils.collections.BytePermutationIterator;
import net.byteseek.utils.factory.ObjectFactory;
import net.byteseek.utils.lazy.DoubleCheckImmutableLazyObject;
import net.byteseek.utils.lazy.LazyObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * An implementation of the SignedHash online search algorithm by Matt Palmer.
 * <p>
 * This is essentially the Signed Wu-Manber algorithm applied to a single pattern instead of multiple patterns.
 * It uses a hash of a q-gram to look up a safe shift.  Since q-grams appear less frequently than single bytes,
 * the corresponding shifts can be bigger, for longer or more complex patterns.
 * The bigger shifts then offset the additional cost of reading more bytes for the q-gram and
 * calculating its hash, leading to better performance.
 * <p>
 * The algorithm was inspired by Lecroq's adaptation of Wu-Manber for single pattern searching described in
 * "Fast Exact String Matching Algorithms".  However there are some significant differences - it doesn't require
 * a sentinel pattern at the end, it handles byte classes, it uses the signed-searching technique, it uses
 * multiply-shift hashing (which allows a greater choice of hash table sizes), and it contains a method to select
 * a good hash table size given the pattern to be searched for.
 * <p>
 * Performance depends on the size of the hash table selected.  Too small and there are too many false positives
 * and small shifts. Too large and we incur unnecessary cache misses in memory.  By default the algorithm will
 * select the smallest hash table which gives good performance for the number of q-grams in the pattern, from a
 * minimum of 32 up to a maximum size of 64k elements.  It dynamically adjusts to the maximum available size,
 * so it won't process parts of a pattern which can't contribute to better performance.
 * This also defends against denial of service attacks where pathologically complex patterns might be submitted by external users.
 * You can also change the maximum permitted size, or specify the exact size of the table, as a power of two.
 * <p>
 * The core algorithm permits q-grams of different lengths to be used.  This implementation uses a q-gram of length 4.
 * Note that if a pattern shorter than the qgram length is passed in, this algorithm cannot search for it,
 * and a different algorithm (ShiftOr) will be substituted, which is generally fastest for short patterns.
 * The substitute will also be used if the pattern is equal to the length of the qgram, as this only gives a maximum
 * shift of one - which is then just a more expensive way of looking at every single position (i.e. the naive search).
 * ShiftOr creates a table of 256 elements, which in most cases will be the same or smaller
 * than the table used by this searcher, and whose pre-processing time is also faster.
 */
public final class SignedHash4Searcher extends AbstractSequenceFallbackSearcher {

    /*************
     * Constants *
     *************/

    /**
     * The length of q-gram processed by this searcher.
     */
    private final static int QLEN = 4;


    /**********
     * Fields *
     **********/

    /**
     * A lazy object which can create the information needed to search forwards.
     * An array of integers is used to determine how far it is safe to shift given a qgram seen in the text.
     */
    private final LazyObject<SearchInfo> forwardSearchInfo;

    /**
     * A lazy object which can create the information needed to search backwards.
     * An array of integers is used to determine how far it is safe to shift given a qgram seen in the text.
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
    public SignedHash4Searcher(final SequenceMatcher sequence) {
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
    public SignedHash4Searcher(final SequenceMatcher sequence, final SearchIndexSize searchIndexSize) {
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
    public SignedHash4Searcher(final String sequence) {
        this(sequence, Charset.defaultCharset(), DEFAULT_SEARCH_INDEX_SIZE);
    }

    /**
     * Constructs a searcher for the bytes contained in the sequence string,
     * encoded using the platform default character set, and a powerTwoSize which determines
     * the size of the hash table used, up to maximum of 31, = 2^31, the largest possible array in Java.
     * <p>
     * If the powerTwoSize is set to zero, then the smallest table size which could give good performance for the
     * pattern will be automatically selected, although it may result in very large table sizes for some complex
     * patterns.
     * <p>
     * If the powerTwoSize is negative, then the smallest good performing table size will also be automatically
     * selected, up to a maximum size given by the positive value, e.g. -12 = 2^12 = no bigger than 4096 elements.
     * If the pattern is too complex to be adequately represented by the available table size,
     * a replacement searcher will be used in place of this algorithm (which is ShiftOR).  This is because if the
     * hash table is too small, the available shifts will be very small too and searching will consequently be
     * very slow.  While ShiftOR isn't particularly fast, it is faster than using this algorithm poorly and
     * does not suffer at all from complexity in the patterns.
     *
     * @param sequence The string to search for.
     * @param searchIndexSize  Determines the size of the hash table used by the search algorithm.
     * @throws IllegalArgumentException if the sequence is null or empty or the powerTwoSize is less than -28 or greater than 28.
     */
    public SignedHash4Searcher(final String sequence, final SearchIndexSize searchIndexSize) {
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
    public SignedHash4Searcher(final String sequence, final Charset charset) {
        this(sequence == null? null : charset == null? null : new ByteSequenceMatcher(sequence.getBytes(charset)));
    }

    /**
     * Constructs a searcher for the bytes contained in the sequence string,
     * encoded using the charset provided, and a powerTwoSize which determines
     * the size of the hash table used, up to maximum of 31, = 2^31, the largest possible array in Java.
     * <p>
     * If the powerTwoSize is set to zero, then the smallest table size which could give good performance for the
     * pattern will be automatically selected, although it may result in very large table sizes for some complex
     * patterns.
     * <p>
     * If the powerTwoSize is negative, then the smallest good performing table size will also be automatically
     * selected, up to a maximum size given by the positive value, e.g. -12 = 2^12 = no bigger than 4096 elements.
     * If the pattern is too complex to be adequately represented by the available table size,
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
    public SignedHash4Searcher(final String sequence, final Charset charset, final SearchIndexSize searchIndexSize) {
        this(sequence == null? null : charset == null? null : new ByteSequenceMatcher(sequence.getBytes(charset)), searchIndexSize);
    }

    /**
     * Constructs a searcher for the byte array provided.
     *
     * @param sequence The byte sequence to search for.
     * @throws IllegalArgumentException if the sequence is null or empty.
     */
    public SignedHash4Searcher(final byte[] sequence) {
        this(sequence == null? null : new ByteSequenceMatcher(sequence), DEFAULT_SEARCH_INDEX_SIZE);
    }

    /**
     * Constructs a searcher for the byte array provided and a powerTwoSize which determines
     * the size of the hash table used, up to maximum of 31, = 2^31, the largest possible array in Java.
     * <p>
     * If the powerTwoSize is set to zero, then the smallest table size which could give good performance for the
     * pattern will be automatically selected, although it may result in very large table sizes for some complex
     * patterns.
     * <p>
     * If the powerTwoSize is negative, then the smallest good performing table size will also be automatically
     * selected, up to a maximum size given by the positive value, e.g. -12 = 2^12 = no bigger than 4096 elements.
     * If the pattern is too complex to be adequately represented by the available table size,
     * a replacement searcher will be used in place of this algorithm (which is ShiftOR).  This is because if the
     * hash table is too small, the available shifts will be very small too and searching will consequently be
     * very slow.  While ShiftOR isn't particularly fast, it is faster than using this algorithm poorly and
     * does not suffer at all from complexity in the patterns.
     *
     * @param sequence The byte sequence to search for.
     * @param searchIndexSize Determines the size of the hash table used by the search algorithm.
     * @throws IllegalArgumentException if the sequence is null or empty, or the charset is null.
     */
    public SignedHash4Searcher(final byte[] sequence, final SearchIndexSize searchIndexSize) {
        this(sequence == null? null : new ByteSequenceMatcher(sequence), searchIndexSize);
    }


    /******************
     * Search Methods *
     ******************/

    @Override
    protected int doSearchSequenceForwards(final byte[] bytes, final int fromPosition, final int toPosition) {

        // Get the pre-processed data needed to search:
        final SearchInfo searchInfo = forwardSearchInfo.get();
        final int[] SHIFTS          = searchInfo.shifts;
        final int HASH_SHIFT        = searchInfo.bitshift;

        // Get local copies of member fields and constants:
        final SequenceMatcher localSequence = sequence;
        final int MASK = SHIFTS.length - 1;             // SHIFTS is always a power of two in length.

        // Determine safe shifts, starts and ends:
        final int LAST_PATTERN_POS = localSequence.length() - 1;
        final int DATA_END_POS     = bytes.length - 1;
        final int LAST_SEARCH_POS  = toPosition + LAST_PATTERN_POS;
        final int SEARCH_END       = LAST_SEARCH_POS < DATA_END_POS?
                LAST_SEARCH_POS : DATA_END_POS;
        final int SEARCH_START     = fromPosition > 0?
                fromPosition : 0;

        // Search forwards:
        int searchPos = SEARCH_START + LAST_PATTERN_POS; // look at the end of the pattern to determine shift.
        while (searchPos <= SEARCH_END) {

            // Calculate hash of qgram:
            int hash =                        (bytes[searchPos - 3] & 0xFF);
            hash     = (hash << HASH_SHIFT) + (bytes[searchPos - 2] & 0xFF);
            hash     = (hash << HASH_SHIFT) + (bytes[searchPos - 1] & 0xFF);
            hash     = (hash << HASH_SHIFT) + (bytes[searchPos    ] & 0xFF);

            // Get the shift for this qgram:
            final int shift = SHIFTS[hash & MASK];

            // If we have a positive shift:
            if (shift > 0) {
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
    protected boolean fallbackForwards() {
        return forwardSearchInfo.get().shifts == null;
    }

    @Override
    protected long doSearchForwards(final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {

        // Get the pre-processed data needed to search:
        final SearchInfo searchInfo = forwardSearchInfo.get();
        final int[] SHIFTS          = searchInfo.shifts;
        final int   HASH_SHIFT      = searchInfo.bitshift;
        final int MASK              = SHIFTS.length - 1; // SHIFTS is always a power of two in length.

        // Get local copies of member fields
        final SequenceMatcher localSequence = sequence;

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
                    hash =                         reader.readByte(searchPos - 3);
                    hash = (hash << HASH_SHIFT) +  reader.readByte(searchPos - 2);
                    hash = (hash << HASH_SHIFT) +  reader.readByte(searchPos - 1);
                    hash = (hash << HASH_SHIFT) + (array[arrayPos] & 0xFF);
                } else {
                    hash =                        (array[arrayPos - 3] & 0xFF);
                    hash = (hash << HASH_SHIFT) + (array[arrayPos - 2] & 0xFF);
                    hash = (hash << HASH_SHIFT) + (array[arrayPos - 1] & 0xFF);
                    hash = (hash << HASH_SHIFT) + (array[arrayPos    ] & 0xFF);
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
    protected int doSearchSequenceBackwards(byte[] bytes, int fromPosition, int toPosition) {

        // Get the pre-processed data needed to search:
        final SearchInfo searchInfo = backwardSearchInfo.get();
        final int[] SHIFTS          = searchInfo.shifts;
        final int   HASH_SHIFT      = searchInfo.bitshift;
        final int   MASK            = SHIFTS.length - 1;   // SHIFTS is always a power of two in length.

        // Get local copies of member fields
        final SequenceMatcher localSequence = sequence;

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
            int hash =                        (bytes[searchPos + 3] & 0xFF);
            hash     = (hash << HASH_SHIFT) + (bytes[searchPos + 2] & 0xFF);
            hash     = (hash << HASH_SHIFT) + (bytes[searchPos + 1] & 0xFF);
            hash     = (hash << HASH_SHIFT) + (bytes[searchPos    ] & 0xFF);

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
    protected boolean fallbackBackwards() {
        return backwardSearchInfo.get().shifts == null;
    }

    @Override
    protected long doSearchBackwards(final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {
        // Get local copies of member fields
        final SequenceMatcher localSequence = sequence;

        // Get the pre-processed data needed to search:
        final SearchInfo searchInfo = backwardSearchInfo.get();
        final int[] SHIFTS          = searchInfo.shifts;
        final int HASH_SHIFT        = searchInfo.bitshift;
        final int MASK              = SHIFTS.length - 1; // SHIFTS is always a power of two in length.

        // Determine safe shifts, starts and ends:
        final long SEARCH_END   = toPosition > 0?
                                  toPosition : 0;

        // Search forwards:
        Window window;
        long searchPos = fromPosition;
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
                    hash =                        reader.readByte(searchPos + 3);
                    hash = (hash << HASH_SHIFT) + reader.readByte(searchPos + 2);
                    hash = (hash << HASH_SHIFT) + reader.readByte(searchPos + 1);
                    hash = (hash << HASH_SHIFT) + (array[arrayPos    ] & 0xFF);
                } else {
                    hash =                        (array[arrayPos + 3] & 0xFF);
                    hash = (hash << HASH_SHIFT) + (array[arrayPos + 2] & 0xFF);
                    hash = (hash << HASH_SHIFT) + (array[arrayPos + 1] & 0xFF);
                    hash = (hash << HASH_SHIFT) + (array[arrayPos    ] & 0xFF);
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


    /******************
     * Public methods *
     ******************/

    @Override
    public void prepareForwards() {
        if (forwardSearchInfo.get().shifts == null) {
            fallbackSearcher.get().prepareForwards();
        }
    }

    @Override
    public void prepareBackwards() {
        if (backwardSearchInfo.get().shifts == null) {
            fallbackSearcher.get().prepareBackwards();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "(index size:"     + searchIndexSize +
                " forward info:"   + (forwardSearchInfo.created()?
                forwardSearchInfo.get().shifts != null?
                        forwardSearchInfo : fallbackSearcher.get() : forwardSearchInfo) +
                " backward info: " + (backwardSearchInfo.created()?
                backwardSearchInfo.get().shifts != null?
                        backwardSearchInfo : fallbackSearcher : backwardSearchInfo) +
                " sequence:"       + sequence + ')';
    }


    /*******************
     * Private methods *
     *******************/

    /**
     * A simple data class containing the shifts for searching and the bitshift needed for the hash-multiply hash function.
     */
    private final static class SearchInfo {
        public final int[] shifts;
        public final int bitshift;
        public SearchInfo(final int[] shifts, final int bitshift) {
            this.shifts   = shifts;
            this.bitshift = bitshift;
        }
        @Override
        public String toString() {
            return getClass().getSimpleName() + "(tableSize:" + (shifts == null? 0 : shifts.length) + ")";
        }
    }

    private final static SearchInfo NULL_SEARCH_INFO = new SearchInfo(null, 0);


    /**
     * A factory for the shift table and hash bitshift needed to search forwards.
     */
    private final class ForwardSearchInfoFactory implements ObjectFactory<SearchInfo> {

        /**
         * Calculates the shift table for forwards searching.
         */
        @Override
        public SearchInfo create() {
            // Get local copies of fields:
            final SequenceMatcher localSequence = sequence;

            // If the pattern is shorter than one qgram, or equal to it, the fallback searcher will be used instead.
            final int PATTERN_LENGTH = localSequence.length();
            if (PATTERN_LENGTH <= QLEN) {
                return NULL_SEARCH_INFO; // no shifts to calculate - fallback searcher will be used if no shifts exist.
            }

            final int MAX_HASH_POWER_TWO_SIZE = searchIndexSize.getPowerTwoSize();

            // Calculate how many qgrams we have, but stop if we get to more than we can handle with good performance.
            // This will give us the size of the hash table and the starting position of the qgram to calculate shifts for,
            // and the maximum shift we can make.  Normally this will be simply be the length of the pattern, but if more qgrams exist than can be
            // reasonably processed, the useful qgram start could be further along the pattern, so the max shift will be shorter.
            final int MAX_TABLE_SIZE = 1 << MAX_HASH_POWER_TWO_SIZE;
            int num0;
            int num1 = localSequence.getNumBytesAtPosition(PATTERN_LENGTH - 1);
            int num2 = localSequence.getNumBytesAtPosition(PATTERN_LENGTH - 2);
            int num3 = localSequence.getNumBytesAtPosition(PATTERN_LENGTH - 3);
            int totalQgrams = 0;
            int qGramStartPos;
            for (qGramStartPos = PATTERN_LENGTH - QLEN; qGramStartPos >= 0; qGramStartPos--) {
                num0 = num1; num1 = num2; num2 = num3; // shift byte counts along.
                num3 = localSequence.getNumBytesAtPosition(qGramStartPos); // get next count.
                totalQgrams += (num0 * num1 * num2 * num3);

                //TODO: might need to step back if we get a huge byte class... no point in processing it in the main loop.
                //      at the moment, we stop when we exceed the max size... at the point we cross it, which might be a lot.
                // If we go beyond the max table load, stop further processing.
                if ((totalQgrams >> 2) >= MAX_TABLE_SIZE) { // if there's four times as many qgrams as max table size (quarter bigger than max):
                    qGramStartPos--; // make the sums add up, will be re-added when loop ends.
                    break; // no further value, halt processing of further qgrams. avoids pathological byte classes.
                }
            }
            qGramStartPos++; // qGram start is now one less than the last successful loop - add one.

            // Determine final size of hash table:
            final int HASH_POWER_TWO_SIZE;
            if (searchIndexSize.getSizeMethod() == SearchIndexSize.Method.EXACTLY) {       // specified by user - must use this size exactly.
                HASH_POWER_TWO_SIZE = MAX_HASH_POWER_TWO_SIZE; // total qgram processing above still useful to avoid pathological byte classes (qGramStartPos).
            } else {
                //TODO: or should it be the power of two *one higher* than ceilLogBase2 of total qgrams? What effective margin do we want?
                final int qGramPowerTwoSize = ByteUtils.ceilLogBaseTwo(totalQgrams); // the power of two size bigger or equal to total qgrams.
                HASH_POWER_TWO_SIZE = MAX_HASH_POWER_TWO_SIZE < qGramPowerTwoSize?
                                      MAX_HASH_POWER_TWO_SIZE : qGramPowerTwoSize > MIN_POWER_TWO_SIZE? // but not bigger than the maximum allowed,
                                                                qGramPowerTwoSize : MIN_POWER_TWO_SIZE; // and not smaller than the minimum allowed.
            }
            final int TABLE_SIZE = 1 << HASH_POWER_TWO_SIZE;

            // Determine bit shift for bit shift hash algorithm
            // Find a bitshift which would give a table size equal or bigger than the hash table size we're using:
            final int HASH_SHIFT = getHashShift(TABLE_SIZE, QLEN);

            // Determine max search shift allowed by the qGramStartPos.
            // If we bailed out early due to to many qgrams, then this will be further along than the start of the pattern,
            // and consequently the maximum shift we can support is lower than the full pattern would allow.
            final int MAX_SEARCH_SHIFT = PATTERN_LENGTH - QLEN - qGramStartPos + 1;

            // Set up the hash table and initialize to the maximum shift allowed given qGramStartPos.
            final int[] SHIFTS = new int[TABLE_SIZE];
            Arrays.fill(SHIFTS, MAX_SEARCH_SHIFT);

            // Set up the key values for hashing as we go along the pattern:
            byte[] bytes0; // first step of processing shifts all the key values along one, so bytes0 = bytes1, ...
            byte[] bytes1 = localSequence.getMatcherForPosition(qGramStartPos    ).getMatchingBytes();
            byte[] bytes2 = localSequence.getMatcherForPosition(qGramStartPos + 1).getMatchingBytes();
            byte[] bytes3 = localSequence.getMatcherForPosition(qGramStartPos + 2).getMatchingBytes();

            int hashValue = 0;
            boolean haveLastHashValue = false;
            final int MASK = TABLE_SIZE - 1;

            // Process all the qgrams in the pattern from the qGram start pos to one before the end of the pattern.
            final int LAST_PATTERN_POS = PATTERN_LENGTH - 1;
            for (int qGramEnd = qGramStartPos + QLEN - 1; qGramEnd < LAST_PATTERN_POS; qGramEnd++) {

                // Calcluate shift for qgrams at this position:
                final int CURRENT_SHIFT = LAST_PATTERN_POS - qGramEnd;

                // Get the byte arrays for the qGram at the current qGramStart:
                bytes0 = bytes1; bytes1 = bytes2; bytes2 = bytes3;                         // shift byte arrays along one.
                bytes3 = localSequence.getMatcherForPosition(qGramEnd).getMatchingBytes(); // get next byte array.

                // Process the qgram permutations as efficiently as possible:
                final long numberOfPermutations = getNumPermutations(bytes0, bytes1, bytes2, bytes3);
                if (numberOfPermutations == 1L) { // no permutations to worry about:
                    if (!haveLastHashValue) { // if we don't have a good last hash value, calculate the first 2 elements of it:
                        hashValue =                             (bytes0[0] & 0xFF);
                        hashValue = (hashValue << HASH_SHIFT) + (bytes1[0] & 0xFF);
                        hashValue = (hashValue << HASH_SHIFT) + (bytes2[0] & 0xFF);
                        haveLastHashValue = true;
                    }
                    hashValue = (hashValue << HASH_SHIFT) + (bytes3[0] & 0xFF);
                    SHIFTS[hashValue & MASK] = CURRENT_SHIFT;
                } else { // more than one permutation to work through.
                    if (haveLastHashValue) { // Then bytes3 must contain all the additional permutations - just go through them.
                        hashValue = hashValue << HASH_SHIFT;
                        for (final byte permutationValue : bytes3) {
                            final int permutationHash = hashValue + (permutationValue & 0xFF);
                            SHIFTS[permutationHash & MASK] = CURRENT_SHIFT;
                        }
                        haveLastHashValue = false; // after processing the permutations, we don't have a single last key value.
                    } else { // permutations may exist anywhere and in more than one place, use a BytePermutationIterator:
                        final BytePermutationIterator qGramPermutations = new BytePermutationIterator(bytes0, bytes1, bytes2, bytes3);
                        while (qGramPermutations.hasNext()) {
                            // Calculate the hash value:
                            final byte[] permutationValue = qGramPermutations.next();
                            hashValue =                             (permutationValue[0] & 0xFF);
                            hashValue = (hashValue << HASH_SHIFT) + (permutationValue[1] & 0xFF);
                            hashValue = (hashValue << HASH_SHIFT) + (permutationValue[2] & 0xFF);
                            hashValue = (hashValue << HASH_SHIFT) + (permutationValue[3] & 0xFF);
                            SHIFTS[hashValue & MASK] = CURRENT_SHIFT;
                        }
                    }
                }
            }

            // Make shifts for the last qgrams in the pattern negative:

            // Get byte arrays for last q-gram:
            bytes0 = bytes1; bytes1 = bytes2; bytes2 = bytes3;    ;                            // shift byte arrays along one.
            bytes3 = localSequence.getMatcherForPosition(LAST_PATTERN_POS).getMatchingBytes(); // get last byte array.

            // Process the last qgram permutations as efficiently as possible:
            final long numberOfPermutations = getNumPermutations(bytes0, bytes1, bytes2, bytes3);
            if (numberOfPermutations == 1L) { // no permutations to worry about:
                if (!haveLastHashValue) { // if we don't have a good last key value, calculate the first 3 elements of it:
                    hashValue =                             (bytes0[0] & 0xFF);
                    hashValue = (hashValue << HASH_SHIFT) + (bytes1[0] & 0xFF);
                    hashValue = (hashValue << HASH_SHIFT) + (bytes2[0] & 0xFF);
                }
                hashValue = ((hashValue << HASH_SHIFT) + (bytes3[0] & 0xFF)) & MASK;
                SHIFTS[hashValue] = -SHIFTS[hashValue];
            } else { // more than one permutation to work through.
                if (haveLastHashValue) { // Then bytes3 must contain all the additional permutations - just go through them.
                    hashValue = hashValue << HASH_SHIFT;
                    for (final byte permutationValue : bytes3) {
                        final int permutationHash = (hashValue + (permutationValue & 0xFF)) & MASK;
                        // If the current shift is positive make it negative.
                        final int CURRENT_SHIFT = SHIFTS[permutationHash];
                        if (CURRENT_SHIFT > 0) {
                            SHIFTS[permutationHash] = -CURRENT_SHIFT;
                        }
                    }
                } else { // permutations may exist anywhere and in more than one place, use a BytePermutationIterator:
                    final BytePermutationIterator qGramPermutations = new BytePermutationIterator(bytes0, bytes1, bytes2, bytes3);
                    while (qGramPermutations.hasNext()) {
                        // Calculate the hash value:
                        final byte[] permutationValue = qGramPermutations.next();
                        hashValue =                              (permutationValue[0] & 0xFF);
                        hashValue = (hashValue << HASH_SHIFT)  + (permutationValue[1] & 0xFF);
                        hashValue = (hashValue << HASH_SHIFT)  + (permutationValue[2] & 0xFF);
                        hashValue = ((hashValue << HASH_SHIFT) + (permutationValue[3] & 0xFF)) & MASK;

                        // If the current shift is positive make it negative.
                        final int CURRENT_SHIFT = SHIFTS[hashValue];
                        if (CURRENT_SHIFT > 0) {
                            SHIFTS[hashValue] = -CURRENT_SHIFT;
                        }
                    }
                }
            }

            return new SearchInfo(SHIFTS, HASH_SHIFT);
        }
    }

    /**
     * A factory for the shift table needed to search backwards.
     */
    private final class BackwardSearchInfoFactory implements ObjectFactory<SearchInfo> {

        /**
         * Calculates the shift table for backwards searching.
         */
        @Override
        public SearchInfo create() {
            // Get local copies of fields:
            final SequenceMatcher localSequence = sequence;

            // If the pattern is shorter than one qgram, or equal to it, the fallback searcher will be used instead.
            final int PATTERN_LENGTH = localSequence.length();
            if (PATTERN_LENGTH <= QLEN) {
                return NULL_SEARCH_INFO; // no shifts to calculate.
            }

            // Determine the maximum size of the hash table:
            final int MAX_HASH_POWER_TWO_SIZE = searchIndexSize.getPowerTwoSize();

            // Calculate how many qgrams we have, but stop if we get to more than we can handle with good performance.
            // This will give us the size of the hash table (if automatically selected) and the starting position of
            // the qgram to calculate shifts for, which gives us the maximum distance we can shift.
            // Normally this position is the start of the pattern, but if we discover pathologically large adjacent byte
            // classes, the permutations will swamp the hash table.  In that case, the first qgram we process shifts for
            // isn't the start of the pattern, it's the one at which almost no better values would remain in the hash table.
            final int MAX_TABLE_SIZE = 1 << MAX_HASH_POWER_TWO_SIZE;
            int num0;
            int num1 = localSequence.getNumBytesAtPosition(0);
            int num2 = localSequence.getNumBytesAtPosition(1);
            int num3 = localSequence.getNumBytesAtPosition(2);
            int totalQgrams = 0;
            int qGramStartPos;
            for (qGramStartPos = QLEN - 1; qGramStartPos < PATTERN_LENGTH; qGramStartPos++) {
                num0 = num1; num1 = num2; num2 = num3; // shift byte counts along.
                num3 = localSequence.getNumBytesAtPosition(qGramStartPos); // get next count.
                totalQgrams += (num0 * num1 * num2 * num3);

                //TODO: might need to step back if we get a huge byte class... no point in processing it in the main loop.
                //      at the moment, we stop when we exceed the max size... at the point we cross it, which might be a lot.
                // If we go beyond the max table load, stop further processing.
                if ((totalQgrams >> 2) >= MAX_TABLE_SIZE) { // if there's four times as many qgrams as max table size (quarter bigger than max):
                    qGramStartPos++; // make the sums add up, will be re-subtracted when loop ends.
                    break; // no further value, halt processing of further qgrams. avoids pathological byte classes.
                }
            }
            qGramStartPos--; // qGram start is now one past the last successful loop - subtract one.

            // Determine final size of hash table:
            final int HASH_POWER_TWO_SIZE;
            if (searchIndexSize.getSizeMethod() == SearchIndexSize.Method.EXACTLY) {       // specified by user - must use this size exactly.
                HASH_POWER_TWO_SIZE = MAX_HASH_POWER_TWO_SIZE; // total qgram processing above still useful to avoid pathological byte classes (qGramStartPos).
            } else { // it's UP_TO the max size - pick an appropriate size:
                //TODO: or should it be the power of two *one higher* than ceilLogBase2 of total qgrams? What effective margin do we want?
                final int qGramPowerTwoSize = ByteUtils.ceilLogBaseTwo(totalQgrams); // the power of two size bigger or equal to total qgrams.
                HASH_POWER_TWO_SIZE = MAX_HASH_POWER_TWO_SIZE < qGramPowerTwoSize?
                                      MAX_HASH_POWER_TWO_SIZE : qGramPowerTwoSize > MIN_POWER_TWO_SIZE? // but not bigger than the maximum allowed,
                                                                qGramPowerTwoSize : MIN_POWER_TWO_SIZE; // and not smaller than the minimum allowed.
            }
            final int TABLE_SIZE = 1 << HASH_POWER_TWO_SIZE;

            // Determine bit shift for multiply-shift hash algorithm:
            final int HASH_SHIFT = getHashShift(TABLE_SIZE, QLEN);

            // Determine max search shift allowed by the qGramEndPos.
            // If we bailed out early due to too many qgrams, then this will be further along than the start of the pattern,
            // and consequently the maximum shift we can support is lower than the full pattern would allow.
            final int MAX_SEARCH_SHIFT = qGramStartPos - QLEN + 2;

            // Set up the hash table and initialize to the maximum shift allowed given qGramStartPos.
            final int[] SHIFTS = new int[TABLE_SIZE];
            Arrays.fill(SHIFTS, MAX_SEARCH_SHIFT);

            // Set up the key values for hashing as we go along the pattern:
            byte[] bytes0; // first step of processing shifts all the key values along one, so bytes0 = bytes1, ...
            byte[] bytes1 = localSequence.getMatcherForPosition(qGramStartPos    ).getMatchingBytes();
            byte[] bytes2 = localSequence.getMatcherForPosition(qGramStartPos - 1).getMatchingBytes();
            byte[] bytes3 = localSequence.getMatcherForPosition(qGramStartPos - 2).getMatchingBytes();

            int hashValue = 0;
            boolean haveLastHashValue = false;
            final int MASK = TABLE_SIZE - 1;

            // Process all the qgrams in the pattern from the qGram end pos to one after the start of the pattern.
            final int LAST_PATTERN_POS = PATTERN_LENGTH - 1;
            for (int qGramEnd = qGramStartPos - QLEN + 1; qGramEnd > 0; qGramEnd--) {

                // Calcluate shift for qgrams at this position:
                final int CURRENT_SHIFT = qGramEnd;

                // Get the byte arrays for the qGram at the current qGramStart:
                bytes0 = bytes1; bytes1 = bytes2; bytes2 = bytes3;                         // shift byte arrays along one.
                bytes3 = localSequence.getMatcherForPosition(qGramEnd).getMatchingBytes(); // get next byte array.

                // Process the qgram permutations as efficiently as possible:
                final long numberOfPermutations = getNumPermutations(bytes0, bytes1, bytes2, bytes3);
                if (numberOfPermutations == 1L) { // no permutations to worry about:
                    if (!haveLastHashValue) { // if we don't have a good last key value, calculate the first 3 elements of it:
                        hashValue =                             (bytes0[0] & 0xFF);
                        hashValue = (hashValue << HASH_SHIFT) + (bytes1[0] & 0xFF);
                        hashValue = (hashValue << HASH_SHIFT) + (bytes2[0] & 0xFF);
                        haveLastHashValue = true;
                    }
                    hashValue = ((hashValue << HASH_SHIFT) + (bytes3[0] & 0xFF));
                    SHIFTS[hashValue & MASK] = CURRENT_SHIFT;

                } else { // more than one permutation to work through.
                    if (haveLastHashValue) { // Then bytes3 must contain all the additional permutations - just go through them.
                        hashValue = hashValue << HASH_SHIFT;
                        for (final byte permutationValue : bytes3) {
                            final int permutationHash = hashValue + (permutationValue & 0xFF);
                            SHIFTS[permutationHash & MASK] = CURRENT_SHIFT;
                        }
                        haveLastHashValue = false; // after processing the permutations, we don't have a single last key value.
                    } else { // permutations may exist anywhere and in more than one place, use a BytePermutationIterator:
                        final BytePermutationIterator qGramPermutations = new BytePermutationIterator(bytes0, bytes1, bytes2, bytes3);
                        while (qGramPermutations.hasNext()) {
                            // Calculate the hash value:
                            final byte[] permutationValue = qGramPermutations.next();
                            hashValue =                             (permutationValue[0] & 0xFF);
                            hashValue = (hashValue << HASH_SHIFT) + (permutationValue[1] & 0xFF);
                            hashValue = (hashValue << HASH_SHIFT) + (permutationValue[2] & 0xFF);
                            hashValue = (hashValue << HASH_SHIFT) + (permutationValue[3] & 0xFF);
                            SHIFTS[hashValue & MASK] = CURRENT_SHIFT;
                        }
                    }
                }
            }

            // Make shifts for the first qgrams in the pattern negative:

            // Get byte arrays for first q-gram:
            bytes0 = bytes1; bytes1 = bytes2; bytes2 = bytes3;                  // shift byte arrays along one.
            bytes3 = localSequence.getMatcherForPosition(0).getMatchingBytes(); // get last byte array.

            // Process the last qgram permutations as efficiently as possible:
            final long numberOfPermutations = getNumPermutations(bytes0, bytes1, bytes2, bytes3);
            if (numberOfPermutations == 1L) { // no permutations to worry about:
                if (!haveLastHashValue) { // if we don't have a good last hash value, calculate the first 3 elements of it:
                    hashValue =                             (bytes0[0] & 0xFF);
                    hashValue = (hashValue << HASH_SHIFT) + (bytes1[0] & 0xFF);
                    hashValue = (hashValue << HASH_SHIFT) + (bytes2[0] & 0xFF);
                }
                hashValue = ((hashValue << HASH_SHIFT) + (bytes3[0] & 0xFF)) & MASK;
                SHIFTS[hashValue] = -SHIFTS[hashValue];
            } else { // more than one permutation to work through.
                if (haveLastHashValue) { // Then bytes2 must contain all the additional permutations - just go through them.
                    hashValue = hashValue << HASH_SHIFT;
                    for (final byte permutationValue : bytes3) {
                        final int permutationHash = (hashValue + (permutationValue & 0xFF)) & MASK;

                        // If the current shift is positive make it negative.
                        final int CURRENT_SHIFT = SHIFTS[permutationHash];
                        if (CURRENT_SHIFT > 0) {
                            SHIFTS[permutationHash] = -CURRENT_SHIFT;
                        }
                    }

                } else { // permutations may exist anywhere and in more than one place, use a BytePermutationIterator:
                    final BytePermutationIterator qGramPermutations = new BytePermutationIterator(bytes0, bytes1, bytes2, bytes3);
                    while (qGramPermutations.hasNext()) {
                        // Calculate the key value:
                        final byte[] permutationValue = qGramPermutations.next();
                        hashValue =                               permutationValue[0] & 0xFF;
                        hashValue = (hashValue << HASH_SHIFT)  + (permutationValue[1] & 0xFF);
                        hashValue = (hashValue << HASH_SHIFT)  + (permutationValue[2] & 0xFF);
                        hashValue = ((hashValue << HASH_SHIFT) + (permutationValue[3] & 0xFF)) & MASK;

                        // If the current shift is positive make it negative.
                        final int CURRENT_SHIFT = SHIFTS[hashValue];
                        if (CURRENT_SHIFT > 0) {
                            SHIFTS[hashValue] = -CURRENT_SHIFT;
                        }
                    }
                }
            }
            return new SearchInfo(SHIFTS, HASH_SHIFT);
        }
    }

    /*******************
     * Utility methods *
     *******************/

    private long getNumPermutations(final byte[] values1, final byte[] values2, final byte[] values3, final byte[] values4) {
        return values1.length * values2.length * values3.length * values4.length;
    }

}
