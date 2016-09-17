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
 * any position.
 * <p>
 * It is very fast when matching short patterns, e.g. 8 or less in length.   It examines
 * every position in the data (there is no shifting), but the core of the algorithm is
 * very simple and uses bit-parallellism to determine where matches exist.  For this reason
 * it generally outperforms shifting algorithms for short patterns, since they cannot obtain
 * large shifts with short patterns.  When patterns become longer, shifts also tend to become
 * longer, and so they outperform ShiftOR since they don't need to examine every byte in the data.
 *
 * @author Matt Palmer
 */

public final class ShiftOrSearcher extends AbstractSequenceSearcher<SequenceMatcher> {

    private static final int MAX_LENGTH = 63; //TODO: 64?

    private final LazyObject<SearchInfo> forwardInfo;
    private final LazyObject<SearchInfo> backwardInfo;


    /**
     * Constructs a searcher given a {@link SequenceMatcher}
     * to search for.
     *
     * @param sequence The SequenceMatcher to search for.
     * @throws IllegalArgumentException if the sequence is null or empty, or longer than 63 bytes.
     */
    public ShiftOrSearcher(final SequenceMatcher sequence) {
        super(sequence);
        if (sequence.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("The ShiftOrSearcher cannot search sequences longer than " + MAX_LENGTH);
        }
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
    public long searchSequenceForwards(final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {
        // Get the objects needed to search:
        final SearchInfo info = forwardInfo.get();
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
     * {@inheritDoc}
     */
    @Override
    public int searchSequenceForwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        // Get the objects needed to search:
        final SearchInfo info = forwardInfo.get();
        final long localLimit = info.getLocalLimit();
        final long[] bitmasks = info.getBitmasks();

        // Determine safe start and ends:
        final int lastMatcherPosition = sequence.length() - 1;
        final int startPosition = fromPosition > 0 ? fromPosition : 0;
        final int lastPossiblePosition = bytes.length - 1;
        final int toPositionEndPos = toPosition < Integer.MAX_VALUE - lastMatcherPosition?
                                     toPosition + lastMatcherPosition : Integer.MAX_VALUE; // shift or must scan up to the end of the pattern.
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

    @Override
    public long searchSequenceBackwards(final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {
        // Get the objects needed to search:
        final SearchInfo info = backwardInfo.get();
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



    /**
     * {@inheritDoc}
     */
    @Override
    public int searchSequenceBackwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        // Get the objects needed to search:
        final SearchInfo info = backwardInfo.get();
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

        private long[] bitmasks;
        private long localLimit;

        private SearchInfo(long[] bitmasks, long localLimit) {
            this.bitmasks = bitmasks;
            this.localLimit = localLimit;
        }

        public long getLocalLimit() {
            return localLimit;
        }

        public long[] getBitmasks() {
            return bitmasks;
        }
    }

    private final class ForwardInfoFactory implements ObjectFactory<SearchInfo> {

        private ForwardInfoFactory() {
        }

        /**
         * Calculates the safe shifts to use if searching forwards.
         * A safe shift is either the length of the sequence, if the
         * byte does not appear in the {@link SequenceMatcher}, or
         * the shortest distance it appears from the end of the matcher.
         */
        @Override
        public SearchInfo create() {
            // Get info about the matcher:
            final int sequenceLength = sequence.length();

            // Set the default bitmask to ~0L:
            final long[] bitmasks = new long[256];
            Arrays.fill(bitmasks, ~0L);

            long localLimit = 0;
            long bitValue = 1L;
            for (int patternPos = 0; patternPos < sequenceLength; patternPos++, bitValue <<= 1) {
                final ByteMatcher aMatcher = sequence.getMatcherForPosition(patternPos);
                final byte[] matchingBytes = aMatcher.getMatchingBytes();
                for (final byte b : matchingBytes) {
                    bitmasks[b & 0xFF] &= ~bitValue;
                }
                localLimit |= bitValue;
            }
            localLimit = ~(localLimit >> 1);

            return new SearchInfo(bitmasks, localLimit);
        }
    }

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
            // Get info about the matcher:
            final int sequenceLength = sequence.length();
            final int lastPosition = sequenceLength - 1;

            // Set the default bitmask to ~0L:
            final long[] bitmasks = new long[256];
            Arrays.fill(bitmasks, ~0L);

            long localLimit = 0;
            long bitValue = 1L;
            for (int patternPos = lastPosition; patternPos >=0; patternPos--, bitValue <<= 1) {
                final ByteMatcher aMatcher = sequence.getMatcherForPosition(patternPos);
                final byte[] matchingBytes = aMatcher.getMatchingBytes();
                for (final byte b : matchingBytes) {
                    bitmasks[b & 0xFF] &= ~bitValue;
                }
                localLimit |= bitValue;
            }
            localLimit = ~(localLimit >> 1);

            return new SearchInfo(bitmasks, localLimit);
        }

    }

}
