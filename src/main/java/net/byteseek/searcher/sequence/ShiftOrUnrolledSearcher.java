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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import net.byteseek.io.reader.windows.Window;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.bytes.ByteMatcher;
import net.byteseek.matcher.sequence.ByteSequenceMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.utils.MathUtils;
import net.byteseek.utils.lazy.DoubleCheckImmutableLazyObject;
import net.byteseek.utils.lazy.LazyObject;
import net.byteseek.utils.factory.ObjectFactory;

/**
 * An implementation of the Shift OR search algorithm, extended to work with byte classes at
 * any position.  It is linear in the size of the data being searched, it does not skip over parts of the data.
 * <p>
 * This implementation unrolls the search into blocks of 16, avoiding loop overhead and match checking logic.
 * It is inspired by the technique described in the paper "Average-Optimal String Matching", by Kimmo Fredriksson and
 * Szymon Grabowski using the concept of an overflow area for unrolled matches.  The rest of the algorithm is the same
 * as the other ShiftOR implementation in byteseek.  The other optimisations described in the above paper did not make search
 * faster in byteseek and the algorithm in the paper has a couple of bugs (does not use all available bits: off by one,
 * misses initial matches due to not setting zeros in front of the pattern for the initial state).
 * <p>
 * It is very fast when matching short patterns, e.g. 8 or less in length, and large byte classes
 * make no difference to its performance. It examines every position in the data.  Despite its name
 * there is no shifting the search position more than one, although bits are shifted with bitwise operators.
 * The core of the algorithm is very simple and uses bit-parallelism to determine where matches exist.
 * For this reason it generally outperforms shifting algorithms for short patterns, since they cannot obtain
 * large shifts with short patterns.  When patterns become longer, search position shifts also tend to become
 * longer, and so these sorts of algorithm outperform ShiftOR, since they don't need to examine every byte in the data.
 * <p>
 * This implementation can also handle patterns longer than the maximum word-length used for the bit patterns.
 * When a pattern is longer than this, the normal SHIFT-OR algorithm is used to verify the first part of the
 * pattern with bit-parallelism, then a SequenceMatcher is used to manually verify the rest if required.
 *
 * @author Matt Palmer
 */

public final class ShiftOrUnrolledSearcher extends AbstractSequenceSearcher<SequenceMatcher> {

    // number of bits to use for the shift-or algorithm bitmasks.
    private static final int WORD_LENGTH = 63;  // Can't use last bit of 64 bit word due to signed numbers.
    private static final int UNROLL      = 16;  // amount to unroll the main loop.

    // forward and backwards search info, with lazy initialisation.
    private final LazyObject<SearchInfo> forwardInfo;
    private final LazyObject<SearchInfo> backwardInfo;

    /**
     * Constructs a searcher given a {@link SequenceMatcher} to search for.
     *
     * @param sequence The SequenceMatcher to search for.
     * @throws IllegalArgumentException if the sequence is null or empty. //, or longer than 63 bytes.
     */
    public ShiftOrUnrolledSearcher(final SequenceMatcher sequence) {
        super(sequence);
        forwardInfo  = new DoubleCheckImmutableLazyObject<SearchInfo>(new ForwardInfoFactory());
        backwardInfo = new DoubleCheckImmutableLazyObject<SearchInfo>(new BackwardInfoFactory());
    }

    /**
     * Constructs a searcher for the bytes contained in the sequence string,
     * encoded using the platform default character set.
     *
     * @param sequence The string to search for.
     * @throws IllegalArgumentException if the sequence is null or empty, or longer than 63 bytes.
     */
    public ShiftOrUnrolledSearcher(final String sequence) {
        this(sequence, Charset.defaultCharset());
    }

    /**
     * Constructs a searcher for the bytes contained in the sequence string, encoded using the charset provided.
     *
     * @param sequence The string to search for.
     * @param charset The charset to encode the string in.
     * @throws IllegalArgumentException if the sequence is null or empty, or the charset is null, or the sequence is longer than 63 bytes.
     */
    public ShiftOrUnrolledSearcher(final String sequence, final Charset charset) {
        this(sequence == null? null : charset == null? null : new ByteSequenceMatcher(sequence.getBytes(charset)));
    }

    /**
     * Constructs a searcher for the byte array provided.
     *
     * @param sequence The byte sequence to search for.
     * @throws IllegalArgumentException if the sequence is null or empty, or is longer than 63 bytes.
     */
    public ShiftOrUnrolledSearcher(final byte[] sequence) {
        this(sequence == null? null : new ByteSequenceMatcher(sequence));
    }

    @Override
    protected int getSequenceLength() {
        return sequence.length();
    }

    @Override
    public long searchSequenceForwards(final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {
        // Get the objects needed to search:
        final SearchInfo info = forwardInfo.get();

        // Check for long sequence searching, where sequence length is greater than the available bits in a word.
        if (info.getVerifier() != null) {
            return searchLongSequenceForwards(info, reader, fromPosition, toPosition);
        }

        final long localLimit = info.getLocalLimit();
        final long[] bitmasks = info.getBitmasks();

        // Determine safe start and ends - do not know final length (input can be a stream).
        final int lastMatcherPosition = sequence.length() - 1;
        final long startPosition = fromPosition > 0 ? fromPosition : 0;
        final long toPositionEndPos = toPosition < Long.MAX_VALUE - lastMatcherPosition?
                                      toPosition + lastMatcherPosition : Long.MAX_VALUE;

        // Search forwards:
        long state = ~0L; // 64 1's bitmask.
        long pos   = startPosition;
        Window window;
        while (pos < toPositionEndPos && (window = reader.getWindow(pos)) != null) { // when window is null, there is no more data.
            final byte[] array            = window.getArray();
            final int arrayStartPos       = reader.getWindowOffset(pos);
            final int arrayWindowEndPos   = window.length() - 1;
            final long distanceToEnd      = toPositionEndPos - pos;
            final int arrayEndPos         = distanceToEnd < arrayWindowEndPos? (int) distanceToEnd : arrayWindowEndPos;
            final int arrayMainLoopEndPos = arrayEndPos - UNROLL + 1;

            int arrayPos;
            for (arrayPos = arrayStartPos; arrayPos <= arrayMainLoopEndPos; arrayPos += UNROLL) {

                // Unroll search by 16: this must correspond to the value defined in UNROLL:
                state = (state << 1) | bitmasks[array[arrayPos]      & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 1]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 2]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 3]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 4]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 5]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 6]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 7]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 8]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 9]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 10] & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 11] & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 12] & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 13] & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 14] & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 15] & 0xFF];

                // Check for any match:
                if (state < localLimit) {
                    final long matchState = (~state) >>> lastMatcherPosition; // invert and shift so we have a bitmask where 1s represent a match.
                    final int posOffset = UNROLL - 1 - MathUtils.floorLogBaseTwo(matchState); // Find the left most bit set to give us the position of the first match.
                    return pos + arrayPos - arrayStartPos - lastMatcherPosition + posOffset;
                }
            }

            // Deal with any remainder from the unrolled loop:
            for (; arrayPos <= arrayEndPos; arrayPos++) {
                state = (state << 1) | bitmasks[array[arrayPos] & 0xFF];
                if (state < localLimit) {
                    return pos + arrayPos - arrayStartPos - lastMatcherPosition;
                }
            }

            pos += (arrayEndPos - arrayStartPos + 1);
        }

        return NO_MATCH_SAFE_SHIFT;
    }

    /**
     * Long pattern variant:
     * Searches using SHIFT-OR, but verifies the part of the pattern longer than WORD_LENGTH using a SequenceMatcher.
     */
    private long searchLongSequenceForwards(final SearchInfo info, final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {
        // Get the objects needed to search:
        final SequenceMatcher verifier = info.getVerifier();
        final long localLimit = info.getLocalLimit();
        final long[] bitmasks = info.getBitmasks();

        // Determine safe start and ends - do not know final length (input can be a stream).
        final int LAST_WORD_POS = WORD_LENGTH - UNROLL;
        final long startPosition = fromPosition > 0 ? fromPosition : 0;
        final long toPositionEndPos = toPosition < Long.MAX_VALUE - LAST_WORD_POS?
                                      toPosition + LAST_WORD_POS : Long.MAX_VALUE - verifier.length();

        // Search forwards across windows:
        long state = ~0L; // 64 1's bitmask.
        long pos   = startPosition;
        Window window;
        while (pos < toPositionEndPos && (window = reader.getWindow(pos)) != null) { // when window is null, there is no more data.

            // Get the array for this window and its safe start and ends:
            final byte[] array            = window.getArray();
            final int arrayStartPos       = reader.getWindowOffset(pos);
            final int arrayWindowEndPos   = window.length() - 1;
            final long distanceToEnd      = toPositionEndPos - pos;
            final int arrayEndPos         = distanceToEnd < arrayWindowEndPos? (int) distanceToEnd : arrayWindowEndPos;
            final int arrayMainLoopEndPos = arrayEndPos - UNROLL + 1;

            // Search forwards in the array:
            int arrayPos;
            for (arrayPos = arrayStartPos; arrayPos <= arrayMainLoopEndPos; arrayPos += UNROLL) {

                // Unroll search by 16: this must correspond to the value defined in UNROLL:
                state = (state << 1) | bitmasks[array[arrayPos]      & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 1]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 2]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 3]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 4]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 5]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 6]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 7]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 8]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 9]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 10] & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 11] & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 12] & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 13] & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 14] & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos + 15] & 0xFF];

                // Check for a match:
                if (state < localLimit) {
                    long matchBit = 1L << (UNROLL - 1);
                    final long matchState = (~state) >>> LAST_WORD_POS;
                    final long currentPos = pos + arrayPos - arrayStartPos;
                    // Go through all the positions a match could have occurred at, and verify a match when necessary:
                    for (int posOffset = 0; posOffset < UNROLL; posOffset++, matchBit >>>= 1) {
                        if ((matchState & matchBit) == matchBit) {
                            if (verifier.matches(reader, currentPos + 1 + posOffset)) {
                                return currentPos - LAST_WORD_POS + posOffset;
                            }
                            state |= (matchBit << LAST_WORD_POS); // reset the match bit back to one - it is no longer a match.
                        }
                    }
                }
            }

            // Deal with any remainder from the unrolled loop:
            for (; arrayPos <= arrayEndPos; arrayPos++) {
                state = (state << 1) | bitmasks[array[arrayPos] & 0xFF];
                if (state < localLimit && verifier.matches(reader, pos + arrayPos - arrayStartPos + 1)) {
                    return pos + arrayPos - arrayStartPos - LAST_WORD_POS;
                }
            }

            // Increment search position by the amount we've scanned in the array:
            pos += (arrayEndPos - arrayStartPos + 1);

        }
        return NO_MATCH_SAFE_SHIFT;
    }

    @Override
    public int searchSequenceForwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        // Get info needed to search:
        final SearchInfo info = forwardInfo.get();

        // If we have a verifier, then use the long sequence searcher, as the sequence is greater than the word length.
        if (info.getVerifier() != null) {
            return searchLongSequenceForwards(info, bytes, fromPosition, toPosition);
        }

        // Get local copies of info needed to search:
        final long localLimit = info.getLocalLimit();
        final long[] bitmasks = info.getBitmasks();

        // Determine safe start and ends:
        final int startPosition = fromPosition > 0 ? fromPosition : 0;
        final int lastMatcherPosition = sequence.length() - 1;
        final int toPositionEndPos = toPosition < Integer.MAX_VALUE - lastMatcherPosition? // avoid integer overflows.
                                     toPosition + lastMatcherPosition : Integer.MAX_VALUE;
        final int lastPossiblePosition = bytes.length - 1;
        final int finalPosition = toPositionEndPos < lastPossiblePosition? toPositionEndPos : lastPossiblePosition;
        final int mainLoopFinalPosition = finalPosition - UNROLL + 1;

        // Search forwards, unrolling loop to do UNROLL shifts within the main loop:
        long state = ~0L;
        int pos;
        for (pos = startPosition; pos <= mainLoopFinalPosition; pos += UNROLL) {
            // Unroll search by 16: this must correspond to the value defined in UNROLL:
            state = (state << 1) | bitmasks[bytes[pos]      & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 1]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 2]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 3]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 4]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 5]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 6]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 7]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 8]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 9]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 10] & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 11] & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 12] & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 13] & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 14] & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 15] & 0xFF];

            if (state < localLimit) {
                final long matchState = (~state) >>> lastMatcherPosition;
                final int posOffset = UNROLL - 1 - MathUtils.floorLogBaseTwo(matchState);
                return pos - lastMatcherPosition + posOffset;
            }
        }

        // Deal with any remainder from the unrolled loop:
        for (; pos <= finalPosition; pos++) {
            state = (state << 1) | bitmasks[bytes[pos] & 0xFF];
            if (state < localLimit) {
                return pos - lastMatcherPosition;
            }
        }

        return NO_MATCH_SAFE_SHIFT;
    }

    /**
     * Long pattern variant:
     * Searches using SHIFT-OR, but verifies the part of the pattern longer than WORD_LENGTH using a SequenceMatcher.
     */
    private int searchLongSequenceForwards(final SearchInfo info, final byte[] bytes, final int fromPosition, final int toPosition) {
        // Get local copies of info needed to search:
        final SequenceMatcher verifier = info.getVerifier();
        final long localLimit = info.getLocalLimit();
        final long[] bitmasks = info.getBitmasks();

        // Determine safe start and ends:
        final int LAST_WORD_POS = WORD_LENGTH - UNROLL;
        final int startPosition = fromPosition > 0 ? fromPosition : 0;
        final int toPositionEndPos = toPosition < Integer.MAX_VALUE - LAST_WORD_POS? // avoid integer overflows.
                                     toPosition + LAST_WORD_POS : Integer.MAX_VALUE;
        final int lastPossiblePosition = bytes.length - verifier.length(); // leave room for verifying rest of pattern.
        final int finalPosition = toPositionEndPos < lastPossiblePosition ? toPositionEndPos : lastPossiblePosition;
        final int mainLoopFinalPosition = finalPosition - UNROLL + 1;

        // Search forwards, unrolling loop to do UNROLL shifts within the main loop:
        long state = ~0L;
        int pos;
        for (pos = startPosition; pos <= mainLoopFinalPosition; pos += UNROLL) {

            // Unroll search by 16: this must correspond to the value defined in UNROLL:
            state = (state << 1) | bitmasks[bytes[pos]      & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 1]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 2]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 3]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 4]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 5]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 6]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 7]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 8]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 9]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 10] & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 11] & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 12] & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 13] & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 14] & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos + 15] & 0xFF];

            // Check for a match in the last UNROLL bits (we invert the state, so looking for one's not zeros to indicate a match.
            if (state < localLimit) {
                long matchBit = 1L << (UNROLL - 1);
                final long matchState = (~state) >>> LAST_WORD_POS;
                // Go through all the positions a match could have occurred at, and verify a match when necessary:
                for (int posOffset = 0; posOffset < UNROLL; posOffset++, matchBit >>>= 1) {
                    if ((matchState & matchBit) == matchBit) {
                        if (verifier.matchesNoBoundsCheck(bytes, pos + 1 + posOffset)) {
                            return pos - LAST_WORD_POS + posOffset;
                        }
                        state |= (matchBit << LAST_WORD_POS); // reset the match bit back to one - it is no longer a match.
                    }
                }
            }
        }

        // Deal with any remainder from the unrolled loop:
        for (; pos <= finalPosition; pos++) {
            state = (state << 1) | bitmasks[bytes[pos] & 0xFF];
            if (state < localLimit && verifier.matchesNoBoundsCheck(bytes, pos + 1)) {
                return pos - LAST_WORD_POS;
            }
        }

        return NO_MATCH_SAFE_SHIFT;
    }

    @Override
    public long searchSequenceBackwards(final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {
        // Get the objects needed to search:
        final SearchInfo info = backwardInfo.get();

        // Check to see if we have to use the long search algorithm for patterns longer than the available bits in a word.
        if (info.getVerifier() != null) {
            return searchLongSequenceBackwards(info, reader, fromPosition, toPosition);
        }

        final long localLimit = info.getLocalLimit();
        final long[] bitmasks = info.getBitmasks();

        // Determine safe end.
        final long finalSearchPosition = toPosition > 0? toPosition : 0;
        final int lastSequencePos = sequence.length() - 1;
        final long fromPositionStart   = fromPosition < Long.MAX_VALUE - lastSequencePos?
                                         fromPosition + lastSequencePos : Long.MAX_VALUE;

        // Search backwards:
        long state = ~0L; // 64 1's bitmask.
        //TODO: withinLength - if start position is negative gives a possible start position?  Write test for this scenario.
        long pos  = withinLength(reader, fromPositionStart); // ensures first position to search is not past end.
        Window window;
        while (pos >= finalSearchPosition && (window = reader.getWindow(pos)) != null) { // when window is null, there is no more data.
            final byte[] array = window.getArray();

            // Calculate array search start and end:
            final int arrayStartPos   = reader.getWindowOffset(pos); // the position within the window array for this position.
            final long distanceToEnd = pos - finalSearchPosition;
            final int arrayEndPos = distanceToEnd < arrayStartPos? (int) (arrayStartPos - distanceToEnd) : 0;
            final int arrayMainLoopEndPos = arrayEndPos + UNROLL - 1;

            // Search backwards in the window array:
            int arrayPos;
            for (arrayPos = arrayStartPos; arrayPos >= arrayMainLoopEndPos; arrayPos -= UNROLL) {

                // Unroll search by 16: this must correspond to the value defined in UNROLL:
                state = (state << 1) | bitmasks[array[arrayPos]      & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 1]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 2]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 3]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 4]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 5]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 6]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 7]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 8]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 9]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 10] & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 11] & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 12] & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 13] & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 14] & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 15] & 0xFF];

                // Check for a match:
                if (state < localLimit) {
                    final long matchState = (~state) >>> lastSequencePos;
                    final int posOffset = UNROLL - 1 - MathUtils.floorLogBaseTwo(matchState);
                    return pos - arrayStartPos + arrayPos - posOffset;
                }
            }

            // Deal with any remainder from the unrolled loop:
            for (; arrayPos >= arrayEndPos; arrayPos--) {
                state = (state << 1) | bitmasks[array[arrayPos] & 0xFF];
                if (state < localLimit) {
                    return pos - arrayStartPos + arrayPos;
                }
            }

            pos -= (arrayStartPos + 1);
        }

        return NO_MATCH_SAFE_SHIFT;
    }

    private long searchLongSequenceBackwards(final SearchInfo info, final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {
        // Get the objects needed to search:
        final SequenceMatcher verifier = info.getVerifier();
        final long localLimit = info.getLocalLimit();
        final long[] bitmasks = info.getBitmasks();

        // Determine safe end.
        final int LAST_WORD_POS = WORD_LENGTH - UNROLL;
        final long finalSearchPosition = toPosition > 0? toPosition : 0;
        final long fromPositionStart   = fromPosition < Long.MAX_VALUE - LAST_WORD_POS?
                                         fromPosition + LAST_WORD_POS : Long.MAX_VALUE - verifier.length();

        // Search backwards:
        long state = ~0L; // 64 1's bitmask.
        long pos  = withinLength(reader, fromPositionStart); // ensures first position to search is not past end.
        Window window;
        while (pos >= finalSearchPosition && (window = reader.getWindow(pos)) != null) { // when window is null, there is no more data.
            final byte[] array = window.getArray();

            // Calculate array search start and end:
            final int arrayStartPos   = reader.getWindowOffset(pos); // the position within the window array for this position.
            final long distanceToEnd = pos - finalSearchPosition;
            final int arrayEndPos = distanceToEnd < arrayStartPos? (int) (arrayStartPos - distanceToEnd) : 0;
            final int mainLoopEndPos = arrayEndPos + UNROLL - 1;

            // Search backwards in the window array:
            int arrayPos;
            for (arrayPos = arrayStartPos; arrayPos >= mainLoopEndPos; arrayPos -= UNROLL) {

                // Unroll search by 16: this must correspond to the value defined in UNROLL:
                state = (state << 1) | bitmasks[array[arrayPos]      & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 1]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 2]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 3]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 4]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 5]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 6]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 7]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 8]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 9]  & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 10] & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 11] & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 12] & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 13] & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 14] & 0xFF];
                state = (state << 1) | bitmasks[array[arrayPos - 15] & 0xFF];

                // Check for a match:
                if (state < localLimit) {
                    final long currentPos = pos - arrayStartPos + arrayPos;
                    // Go through all the positions a match could have occurred at, and verify a match when necessary:
                    long matchBit = 1L << (UNROLL - 1);
                    final long matchState = (~state) >>> LAST_WORD_POS;
                    for (int posOffset = 0; posOffset < UNROLL; posOffset++, matchBit >>>= 1) {
                        if ((matchState & matchBit) == matchBit) {
                            if (verifier.matches(reader, currentPos - posOffset + LAST_WORD_POS + 1)) {
                                return currentPos - posOffset;
                            }
                            state |= (matchBit << LAST_WORD_POS); // reset the match bit back to one - it is no longer a match.
                        }
                    }
                }
            }

            // Deal with any remainder from the unrolled loop:
            for (; arrayPos >= arrayEndPos; arrayPos--) {
                state = (state << 1) | bitmasks[array[arrayPos] & 0xFF];
                if (state < localLimit && verifier.matches(reader, pos + arrayPos - arrayStartPos + LAST_WORD_POS + 1)) {
                    return pos + arrayPos - arrayStartPos;
                }
            }

            // Increment search position by the amount we've scanned in the array:
            pos -= (arrayStartPos - arrayEndPos + 1);
        }

        return NO_MATCH_SAFE_SHIFT;
    }

    @Override
    public int searchSequenceBackwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        // Get the objects needed to search:
        final SearchInfo info = backwardInfo.get();

        // If we have a verifier, then use the long sequence searcher, as the sequence is greater than the word length.
        if (info.getVerifier() != null) {
            return searchLongSequenceBackwards(info, bytes, fromPosition, toPosition);
        }

        final long localLimit = info.getLocalLimit();
        final long[] bitmasks = info.getBitmasks();

        // Determine safe start and ends:
        final int lastPossiblePosition = bytes.length - 1;
        final int lastSequencePos = sequence.length() - 1;
        final int fromPositionEndPos = fromPosition < Integer.MAX_VALUE - lastSequencePos?
                                       fromPosition + lastSequencePos : Integer.MAX_VALUE;
        final int startPosition = fromPositionEndPos < lastPossiblePosition ? fromPositionEndPos : lastPossiblePosition;
        final int finalPosition = toPosition > 0 ? toPosition : 0;
        final int mainLoopFinalPos = finalPosition + UNROLL - 1;

        // Search backwards:
        long state = ~0L;
        int pos;
        for (pos = startPosition; pos >= mainLoopFinalPos; pos -= UNROLL) {

            // Unroll search by 16: this must correspond to the value defined in UNROLL:
            state = (state << 1) | bitmasks[bytes[pos]      & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 1]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 2]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 3]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 4]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 5]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 6]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 7]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 8]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 9]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 10] & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 11] & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 12] & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 13] & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 14] & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 15] & 0xFF];

            // Check for a match:
            if (state < localLimit) {
                final long matchState = (~state) >>> lastSequencePos;
                final int posOffset = UNROLL - 1 - MathUtils.floorLogBaseTwo(matchState);
                return pos - posOffset;
            }
        }

        // Deal with any remainder from the unrolled loop:
        for (; pos >= finalPosition; pos--) {
            state = (state << 1) | bitmasks[bytes[pos] & 0xFF];
            if (state < localLimit) {
                return pos;
            }
        }

        return NO_MATCH_SAFE_SHIFT;
    }

    /**
     * Long pattern variant:
     * Searches using SHIFT-OR, but verifies the part of the pattern longer than WORD_LENGTH using a SequenceMatcher.
     */
    private int searchLongSequenceBackwards(final SearchInfo info, final byte[] bytes, final int fromPosition, final int toPosition) {
        // Get the objects needed to search:
        final SequenceMatcher verifier = info.getVerifier();
        final long localLimit = info.getLocalLimit();
        final long[] bitmasks = info.getBitmasks();

        // Determine safe start and ends:
        final int lastPossiblePosition = bytes.length - verifier.length();
        final int LAST_WORD_POS = WORD_LENGTH - UNROLL;
        final int fromPositionEndPos = fromPosition < Integer.MAX_VALUE - LAST_WORD_POS?
                                       fromPosition + LAST_WORD_POS : Integer.MAX_VALUE;
        final int startPosition = fromPositionEndPos < lastPossiblePosition? fromPositionEndPos : lastPossiblePosition;
        final int finalPosition = toPosition > 0 ? toPosition : 0;
        final int finalMainLoopPos = finalPosition + UNROLL - 1;

        // Search backwards:
        long state = ~0L;
        int pos;
        for (pos = startPosition; pos >= finalMainLoopPos; pos -= UNROLL) {

            // Unroll search by 16: this must correspond to the value defined in UNROLL:
            state = (state << 1) | bitmasks[bytes[pos]      & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 1]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 2]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 3]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 4]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 5]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 6]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 7]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 8]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 9]  & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 10] & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 11] & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 12] & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 13] & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 14] & 0xFF];
            state = (state << 1) | bitmasks[bytes[pos - 15] & 0xFF];

            // Check for a match:
            if (state < localLimit) {

                // Go through all the positions a match could have occurred at, and verify a match when necessary:
                long matchBit = 1L << (UNROLL - 1);
                final long matchState = (~state) >>> LAST_WORD_POS;
                for (int posOffset = 0; posOffset < UNROLL; posOffset++, matchBit >>>= 1) {
                    if ((matchState & matchBit) == matchBit) {
                        if (verifier.matchesNoBoundsCheck(bytes, pos - posOffset + LAST_WORD_POS + 1)) {
                            return pos - posOffset;
                        }
                        state |= (matchBit << LAST_WORD_POS); // reset the match bit back to one - it is no longer a match.
                    }
                }
            }
        }


        // Deal with any remainder from the unrolled loop:
        for (; pos >= finalPosition; pos--) {
            state = (state << 1) | bitmasks[bytes[pos] & 0xFF];
            if (state < localLimit && verifier.matchesNoBoundsCheck(bytes, pos + LAST_WORD_POS + 1)) {
                return pos;
            }
        }

        return NO_MATCH_SAFE_SHIFT;
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

    private static final class SearchInfo {

        private final long[] bitmasks;
        private final long localLimit;
        private final SequenceMatcher verifier;

        private SearchInfo(long[] bitmasks, long localLimit, SequenceMatcher verifier) {
            this.bitmasks   = bitmasks;
            this.localLimit = localLimit;
            this.verifier   = verifier;
        }

        public long getLocalLimit() {
            return localLimit;
        }

        public long[] getBitmasks() {
            return bitmasks;
        }

        public SequenceMatcher getVerifier() {
            return verifier;
        }
    }

    private final class ForwardInfoFactory implements ObjectFactory<SearchInfo> {

        private ForwardInfoFactory() {
        }

        /**
         * Calculates the bitmasks used to detect a match.
         */
        @Override
        public SearchInfo create() {
            // Get info about the matcher:
            final int SEQUENCE_LENGTH = sequence.length();
            final SequenceMatcher VERIFIER;
            final int SCAN_LIMIT;

            //TODO: verify this...
            final int MAX_LENGTH =  WORD_LENGTH - UNROLL + 1;
            if (SEQUENCE_LENGTH > MAX_LENGTH) {
                VERIFIER = sequence.subsequence(MAX_LENGTH);
                SCAN_LIMIT = MAX_LENGTH;
            } else {
                VERIFIER = null;
                SCAN_LIMIT = SEQUENCE_LENGTH;
            }

            final long[] bitmasks = new long[256];
            final long mask = (1L << SCAN_LIMIT) - 1;
            Arrays.fill(bitmasks, mask);

            for (int patternPos = 0; patternPos < SCAN_LIMIT; patternPos++) {
                final ByteMatcher aMatcher = sequence.getMatcherForPosition(patternPos);
                final byte[] matchingBytes = aMatcher.getMatchingBytes();
                final long maskValue = ~(1L << patternPos);
                for (final byte b : matchingBytes) {
                    bitmasks[b & 0xFF] &= maskValue;
                }
            }

            // Local limit is the value below which we have a match (a zero bit has crossed over).
            final long localLimit = ~(mask >> 1);

            return new SearchInfo(bitmasks, localLimit, VERIFIER);
        }
    }

    private final class BackwardInfoFactory implements ObjectFactory<SearchInfo> {

        private BackwardInfoFactory() {
        }

        /**
         * Calculates the bitmasks used to detect a match.
         */
        @Override
        public SearchInfo create() {
            // Get info about the matcher:
            final int SEQUENCE_LENGTH = sequence.length();
            final SequenceMatcher VERIFIER;
            final int SCAN_LIMIT;

            final int MAX_LENGTH =  WORD_LENGTH - UNROLL + 1;
            if (SEQUENCE_LENGTH > MAX_LENGTH) {
                VERIFIER = sequence.subsequence(MAX_LENGTH);
                SCAN_LIMIT = MAX_LENGTH;
            } else {
                VERIFIER = null;
                SCAN_LIMIT = SEQUENCE_LENGTH;
            }

            // Set the default bitmask to ~0L:
            final long[] bitmasks = new long[256];
            final long mask = (1L << SCAN_LIMIT) - 1;
            Arrays.fill(bitmasks, mask);

            long localLimit = 0;
            long bitValue = 1L;
            for (int patternPos = SCAN_LIMIT - 1; patternPos >= 0; patternPos--, bitValue <<= 1) {
                final ByteMatcher aMatcher = sequence.getMatcherForPosition(patternPos);
                final byte[] matchingBytes = aMatcher.getMatchingBytes();
                for (final byte b : matchingBytes) {
                    bitmasks[b & 0xFF] &= ~bitValue;
                }
                localLimit |= bitValue;
            }
            localLimit = ~(localLimit >> 1);

            return new SearchInfo(bitmasks, localLimit, VERIFIER);
        }

    }

}
