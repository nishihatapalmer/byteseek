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

import net.byteseek.io.reader.windows.Window;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.bytes.ByteMatcher;
import net.byteseek.matcher.sequence.ByteSequenceMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.utils.lazy.DoubleCheckImmutableLazyObject;
import net.byteseek.utils.lazy.LazyObject;
import net.byteseek.utils.factory.ObjectFactory;

/**
 * An implementation of the Shift OR search algorithm, extended to work with byte classes at
 * any position.  It is linear in the size of the data being searched, it does not skip over parts of the data.
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

public final class ShiftOrSearcher extends AbstractSequenceSearcher<SequenceMatcher> {

    // number of bits to use for the shift-or algorithm bitmasks.
    private static final int WORD_LENGTH = 63; // can't use all 64 bits of a long due to being signed.

    private final LazyObject<SearchInfo> forwardInfo;
    private final LazyObject<SearchInfo> backwardInfo;

    /**
     * Constructs a searcher given a {@link SequenceMatcher} to search for.
     *
     * @param sequence The SequenceMatcher to search for.
     * @throws IllegalArgumentException if the sequence is null or empty. //, or longer than 63 bytes.
     */
    public ShiftOrSearcher(final SequenceMatcher sequence) {
        super(sequence);
        //if (sequence.length() > MAX_LENGTH) {
        //    throw new IllegalArgumentException("The ShiftOrSearcher cannot search sequences longer than " + MAX_LENGTH);
        //}
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
    public ShiftOrSearcher(final String sequence) {
        this(sequence, Charset.defaultCharset());
    }

    /**
     * Constructs a searcher for the bytes contained in the sequence string,
     * encoded using the charset provided.
     *
     * @param sequence The string to search for.
     * @param charset The charset to encode the string in.
     * @throws IllegalArgumentException if the sequence is null or empty, or the charset is null, or the sequence is longer than 63 bytes.
     */
    public ShiftOrSearcher(final String sequence, final Charset charset) {
        this(sequence == null? null : charset == null? null : new ByteSequenceMatcher(sequence.getBytes(charset)));
    }

    /**
     * Constructs a searcher for the byte array provided.
     *
     * @param sequence The byte sequence to search for.
     * @throws IllegalArgumentException if the sequence is null or empty, or is longer than 63 bytes.
     */
    public ShiftOrSearcher(final byte[] sequence) {
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
            final byte[] array = window.getArray();
            final int arrayStartPos = reader.getWindowOffset(pos);
            final int arrayWindowEndPos   = window.length() - 1;
            final long distanceToEnd = toPositionEndPos - pos;
            final int arrayEndPos = distanceToEnd < arrayWindowEndPos?
                              (int) distanceToEnd : arrayWindowEndPos;
            for (int arrayPos = arrayStartPos; arrayPos <= arrayEndPos; arrayPos++) {
                state = (state << 1) | bitmasks[array[arrayPos] & 0xFF];
                if (state < localLimit) {
                    return pos + arrayPos - arrayStartPos - lastMatcherPosition;
                }
            }
            pos += (arrayEndPos - arrayStartPos + 1);
        }

        return NO_MATCH;
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
        final long startPosition = fromPosition > 0 ? fromPosition : 0;
        final long toPositionEndPos = toPosition < Long.MAX_VALUE - WORD_LENGTH?
                                      toPosition + WORD_LENGTH : Long.MAX_VALUE - verifier.length();

        // Search forwards:
        long state = ~0L; // 64 1's bitmask.
        long pos   = startPosition;
        Window window;
        while (pos < toPositionEndPos && (window = reader.getWindow(pos)) != null) { // when window is null, there is no more data.
            final byte[] array = window.getArray();
            final int arrayStartPos = reader.getWindowOffset(pos);
            final int arrayWindowEndPos   = window.length() - 1;
            final long distanceToEnd = toPositionEndPos - pos;
            final int arrayEndPos = distanceToEnd < arrayWindowEndPos?
                    (int) distanceToEnd : arrayWindowEndPos;
            for (int arrayPos = arrayStartPos; arrayPos <= arrayEndPos; arrayPos++) {
                state = (state << 1) | bitmasks[array[arrayPos] & 0xFF];
                if (state < localLimit) {
                    final long nextPos = pos + arrayPos - arrayStartPos + 1;
                    if (verifier.matches(reader, nextPos)) {
                        return nextPos - WORD_LENGTH;
                    }
                }
            }
            pos += (arrayEndPos - arrayStartPos + 1);
        }

        return NO_MATCH;
    }

    /**
     * {@inheritDoc}
     */
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
        final int finalPosition = toPositionEndPos < lastPossiblePosition ? toPositionEndPos : lastPossiblePosition;

        // Search forwards:
        long state = ~0L;
        for (int pos = startPosition; pos <= finalPosition; pos++) {
            state = (state << 1) | bitmasks[bytes[pos] & 0xFF];
            if (state < localLimit) {
                return pos - lastMatcherPosition;
            }
        }

        return NO_MATCH;
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
        final int startPosition = fromPosition > 0 ? fromPosition : 0;
        final int lastMatcherPosition = sequence.length() - 1;
        final int toPositionEndPos = toPosition < Integer.MAX_VALUE - WORD_LENGTH? // avoid integer overflows.
                                     toPosition + WORD_LENGTH : Integer.MAX_VALUE;
        final int lastPossiblePosition = bytes.length - verifier.length(); // leave room for verifying rest of pattern.
        final int finalPosition = toPositionEndPos < lastPossiblePosition ? toPositionEndPos : lastPossiblePosition;

        // Search forwards:
        long state = ~0L;
        for (int pos = startPosition; pos <= finalPosition; pos++) {
            state = (state << 1) | bitmasks[bytes[pos] & 0xFF];
            if (state < localLimit &&                                  // first part of sequence found using bit-parallelism.
                    verifier.matchesNoBoundsCheck(bytes, pos + 1)) {   // verify rest manually with a SequenceMatcher.
                return pos - lastMatcherPosition;
            }
        }

        return NO_MATCH;
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
        long pos  = withinLength(reader, fromPositionStart); // ensures first position to search is not past end.
        Window window;
        while (pos >= finalSearchPosition && (window = reader.getWindow(pos)) != null) { // when window is null, there is no more data.
            final byte[] array = window.getArray();

            // Calculate array search start and end:
            final int arrayStartPos   = reader.getWindowOffset(pos); // the position within the window array for this position.
            final long distanceToEnd = pos - finalSearchPosition;
            final int arrayEndPos = distanceToEnd < arrayStartPos?
                              (int) (arrayStartPos - distanceToEnd) : 0;

            // Search backwards in the window array:
            for (int arrayPos = arrayStartPos; arrayPos >= arrayEndPos; arrayPos--) {
                state = (state << 1) | bitmasks[array[arrayPos] & 0xFF];
                if (state < localLimit) {
                    return pos - arrayStartPos + arrayPos;
                }
            }
            pos -= (arrayStartPos + 1);
        }

        return NO_MATCH;
    }

    private long searchLongSequenceBackwards(final SearchInfo info, final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {
        // Get the objects needed to search:
        final SequenceMatcher verifier = info.getVerifier();
        final long localLimit = info.getLocalLimit();
        final long[] bitmasks = info.getBitmasks();

        // Determine safe end.
        final long finalSearchPosition = toPosition > 0? toPosition : 0;
        final long fromPositionStart   = fromPosition < Long.MAX_VALUE - WORD_LENGTH?
                                         fromPosition + WORD_LENGTH : Long.MAX_VALUE - verifier.length();

        // Search backwards:
        long state = ~0L; // 64 1's bitmask.
        long pos  = withinLength(reader, fromPositionStart); // ensures first position to search is not past end.
        Window window;
        while (pos >= finalSearchPosition && (window = reader.getWindow(pos)) != null) { // when window is null, there is no more data.
            final byte[] array = window.getArray();

            // Calculate array search start and end:
            final int arrayStartPos   = reader.getWindowOffset(pos); // the position within the window array for this position.
            final long distanceToEnd = pos - finalSearchPosition;
            final int arrayEndPos = distanceToEnd < arrayStartPos?
                    (int) (arrayStartPos - distanceToEnd) : 0;

            // Search backwards in the window array:
            for (int arrayPos = arrayStartPos; arrayPos >= arrayEndPos; arrayPos--) {
                state = (state << 1) | bitmasks[array[arrayPos] & 0xFF];
                if (state < localLimit) {
                    final long currentPos = pos - arrayStartPos + arrayPos;
                    if (verifier.matches(reader, currentPos + WORD_LENGTH)) {
                        return currentPos;
                    }
                }
            }
            pos -= (arrayStartPos + 1);
        }

        return NO_MATCH;
    }

    /**
     * {@inheritDoc}
     */
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

        // Search backwards:
        long state = ~0L;
        for (int pos = startPosition; pos >= finalPosition; pos--) {
            state = (state << 1) | bitmasks[bytes[pos] & 0xFF];
            if (state < localLimit) {
                return pos;
            }
        }

        return NO_MATCH;
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
        final int fromPositionEndPos = fromPosition < Integer.MAX_VALUE - WORD_LENGTH?
                                       fromPosition + WORD_LENGTH : Integer.MAX_VALUE;
        final int startPosition = fromPositionEndPos < lastPossiblePosition ?
                                  fromPositionEndPos : lastPossiblePosition;
        final int finalPosition = toPosition > 0 ? toPosition : 0;

        // Search backwards:
        long state = ~0L;
        for (int pos = startPosition; pos >= finalPosition; pos--) {
            state = (state << 1) | bitmasks[bytes[pos] & 0xFF];
            if (state < localLimit && verifier.matchesNoBoundsCheck(bytes, pos + WORD_LENGTH)) {
                return pos;
            }
        }

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

            if (SEQUENCE_LENGTH > WORD_LENGTH) {
                VERIFIER = sequence.subsequence(WORD_LENGTH);
                SCAN_LIMIT = WORD_LENGTH;
            } else {
                VERIFIER = null;
                SCAN_LIMIT = SEQUENCE_LENGTH;
            }

            // Set the default bitmask to ~0L:
            final long[] bitmasks = new long[256];
            Arrays.fill(bitmasks, ~0L);

            long localLimit = 0;
            long bitValue = 1L;
            for (int patternPos = 0; patternPos < SCAN_LIMIT; patternPos++, bitValue <<= 1) {
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

            if (SEQUENCE_LENGTH > WORD_LENGTH) {
                VERIFIER = sequence.subsequence(WORD_LENGTH);
                SCAN_LIMIT = WORD_LENGTH;
            } else {
                VERIFIER = null;
                SCAN_LIMIT = SEQUENCE_LENGTH;
            }

            // Set the default bitmask to ~0L:
            final long[] bitmasks = new long[256];
            Arrays.fill(bitmasks, ~0L);

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
