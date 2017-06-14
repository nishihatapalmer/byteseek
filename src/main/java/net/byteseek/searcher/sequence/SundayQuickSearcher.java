/*
 * Copyright Matt Palmer 2011-2017, All rights reserved.
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
import net.byteseek.matcher.sequence.ByteSequenceMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.utils.lazy.DoubleCheckImmutableLazyObject;
import net.byteseek.utils.lazy.LazyObject;
import net.byteseek.utils.factory.ObjectFactory;


/**
 * An implementation of the Sunday Quick searcher algorithm described in
 * "A very fast substring search algorithm", by SUNDAY D.M., 1990.
 * <p>
 * It is a modification of the Horspool search algorithm.  Sunday realised that you can shift on the
 * position one past the end of the pattern, not just on the end of the pattern.  Although this leads to larger
 * shifts, in practice the algorithm does not obtain better performance than Horspool.
 *
 * @author Matt Palmer
 */
public final class SundayQuickSearcher extends AbstractWindowSearcher<SequenceMatcher> {

    private final LazyObject<int[]> forwardInfo;
    private final LazyObject<int[]> backwardInfo;

    /**
    * Constructs a searcher for the bytes contained in the sequence string,
    * encoded using the platform default character set.
    *
    * @param sequence The string to search for.
    * @throws IllegalArgumentException if the sequence is null or empty.
    */
    public SundayQuickSearcher(final String sequence) {
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
    public SundayQuickSearcher(final String sequence, final Charset charset) {
        this(sequence == null? null : charset == null? null : new ByteSequenceMatcher(sequence.getBytes(charset)));
    }

    /**
     * Constructs a searcher for the byte array provided.
     *
     * @param sequence The byte sequence to search for.
     * @throws IllegalArgumentException if the sequence is null or empty.
     */
    public SundayQuickSearcher(final byte[] sequence) {
        this(sequence == null? null : new ByteSequenceMatcher(sequence));
    }

    /**
     * Constructs a Sunday Quick searcher given a {@link SequenceMatcher}
     * to search for.
     * 
     * @param sequence The sequence to search for.
     */
    public SundayQuickSearcher(final SequenceMatcher sequence) {
        super(sequence);
        forwardInfo  = new DoubleCheckImmutableLazyObject<int[]>(new ForwardInfoFactory());
        backwardInfo = new DoubleCheckImmutableLazyObject<int[]>(new BackwardInfoFactory());
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

        // Get the objects needed to search:
        final int[] safeShifts = forwardInfo.get();
        final SequenceMatcher theSequence = sequence;

        // Calculate safe bounds for the search:
        final int length = theSequence.length();
        final int finalPosition = bytes.length - length;
        final int lastLoopPosition = finalPosition - 1;
        final int lastPosition = toPosition < lastLoopPosition ?
                                 toPosition : lastLoopPosition;
        int searchPosition = fromPosition > 0 ?
                             fromPosition : 0;

        // Search forwards.  The loop does not check for the final
        // position, as we shift on the byte after the sequence.
        while (searchPosition <= lastPosition) {
            if (theSequence.matchesNoBoundsCheck(bytes, searchPosition)) {
                return searchPosition;
            }
            searchPosition += safeShifts[bytes[searchPosition + length] & 0xFF];
        }

        // Check the final position if necessary:
        if (searchPosition == finalPosition &&
                toPosition >= finalPosition &&
                theSequence.matches(bytes, finalPosition)) {
            return finalPosition;
        }

        return NO_MATCH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long doSearchForwards(final WindowReader reader, final long fromPosition, final long toPosition ) throws IOException {

        // Initialise
        final int[] safeShifts = forwardInfo.get();
        final SequenceMatcher theSequence = sequence;
        final int length  = theSequence.length();
        int arrayPosition = 0;
        int windowLength  = 0;
        byte[] array      = null;
        Window window;

        // Search forwards:
        long searchPosition = fromPosition;
        while (searchPosition <= toPosition) {

            // Check for a match at the search position:
            if (theSequence.matches(reader, searchPosition)) {
                return searchPosition;
            }

            // If we need a window to get a shift for at the position one past the sequence:
            if (arrayPosition >= windowLength) {
                window = reader.getWindow(searchPosition + length);
                if (window == null) { // no further data, so no further match possible.
                    return NO_MATCH;
                }
                array = window.getArray();
                arrayPosition = reader.getWindowOffset(searchPosition + length);
                windowLength = window.length();
            }

            // Shift the search position on by the safe shift:
            final int shift = safeShifts[array[arrayPosition] & 0xFF];
            searchPosition += shift;
            arrayPosition += shift;
        }

        return NO_MATCH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int searchSequenceBackwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        
        // Get objects needed to search:
        final int[] safeShifts = backwardInfo.get();
        final SequenceMatcher theSequence = sequence;
        
        // Calculate safe bounds for the search:
        final int lastLoopPosition = toPosition > 1?
                                     toPosition : 1;
        final int firstPossiblePosition = bytes.length - sequence.length();
        int searchPosition = fromPosition < firstPossiblePosition ?
                             fromPosition : firstPossiblePosition;
        
        // Search backwards.  The loop does not check the
        // first position in the array, because we shift on the byte
        // immediately before the current search position.
        while (searchPosition >= lastLoopPosition) {
            if (theSequence.matchesNoBoundsCheck(bytes, searchPosition)) {
                return searchPosition;
            }
            searchPosition -= safeShifts[bytes[searchPosition - 1] & 0xFF];             
        }
        
        // Check for first position if necessary:
        if (searchPosition == 0 &&
            toPosition < 1 &&
                theSequence.matches(bytes, 0)) {
            return 0;
        }

        return NO_MATCH;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public long doSearchBackwards(final WindowReader reader, final long fromPosition, final long toPosition ) throws IOException {

        // Initialise
        final int[] safeShifts = backwardInfo.get();
        final SequenceMatcher theSequence = sequence;
        int arrayPosition = -1;
        byte[] array      = null;
        Window window;

        // Search backwards:
        long searchPosition = fromPosition;
        while (searchPosition >= toPosition) {

            // Check for a match at the search position:
            if (theSequence.matches(reader, searchPosition)) {
                return searchPosition;
            }

            // If we need a window to get a shift for at the position one before the sequence:
            if (arrayPosition < 0) {
                window = reader.getWindow(searchPosition - 1);
                if (window == null) { // no further data, so no further match possible.
                    return NO_MATCH;
                }
                array = window.getArray();
                arrayPosition = reader.getWindowOffset(searchPosition - 1);
            }

            // Shift the search position back by the safe shift:
            final int shift = safeShifts[array[arrayPosition] & 0xFF];
            searchPosition -= shift;
            arrayPosition -= shift;
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


    private final static int MAX_BYTES = 1024; // four times the table length fills 98% of positions with random selection.

    private final class ForwardInfoFactory implements ObjectFactory<int[]> {

        private ForwardInfoFactory() {
        }

        /**
         * Calculates the safe shifts to use if searching forwards.
         * A safe shift is either the length of the sequence plus one, if the
         * byte does not appear in the {@link SequenceMatcher}, or
         * the shortest distance it appears from the end of the matcher.
         * The last character in the pattern is not processed.
         */
        @Override
        public int[] create() {
            final SequenceMatcher localSequence = sequence;
            final int sequenceLength = localSequence.length();

            // Find the max shift possible by scanning back and counting the bytes matched.
            // If we find 256 bytes in one place then nothing can match past that position.
            // If we exceed four times the table size, 98% of positions would be filled assuming a uniform random distribution.
            // This is not optimal - many complicated patterns could cause a bit more processing than strictly required,
            // but it does avoid denial of service and completely unnecessary processing.
            // Note: The final position gives the same max shift as not looking at all, so we don't process that one.
            int posToProcessFrom = 0;
            int totalBytes = 0;
            for (int position = sequenceLength - 1; position > 0; position--) {
                final int numBytes = localSequence.getNumBytesAtPosition(position);
                totalBytes += numBytes;
                // Stop if we execeed the max bytes, or the bytes for the current position would overwrite everything after it.
                if (totalBytes > MAX_BYTES || numBytes == 256 ) {
                    posToProcessFrom = position;
                    break;
                }
            }

            // Set the default shift to the max shift allowable given the final pos processed.
            final int maxShift = sequenceLength - posToProcessFrom + 1;
            final int[] shifts = new int[256];
            Arrays.fill(shifts, maxShift);

            // Now set specific byte shifts for the bytes actually in
            // the sequence itself.  The shift is the distance of each character
            // from the end of the sequence, where the last position equals 1.
            // Each position can match more than one byte (e.g. if a byte class appears).
            // We start processing from the posToProcessFrom, which is the last position deemed to be worth processing
            // given large byte classes or very long sequences which would swamp the shift table.
            for (int position = posToProcessFrom; position < sequenceLength; position++) {
                final byte[] matchingBytes = localSequence.getMatcherForPosition(position).getMatchingBytes();
                final int distanceFromEnd = sequenceLength - position;
                for (final byte b : matchingBytes) {
                    shifts[b & 0xFF] = distanceFromEnd;
                }
            }

            return shifts;
        }
    }
    
    
    private final class BackwardInfoFactory implements ObjectFactory<int[]> {

        private BackwardInfoFactory() {
        }
        
        /**
         * Calculates the safe shifts to use if searching backwards.
         * A safe shift is either the length of the sequence plus one, if the
         * byte does not appear in the {@link SequenceMatcher}, or
         * the shortest distance it appears from the beginning of the matcher.
         */        
        @Override
        public int[] create() {
            // Get basic info about the sequence:
            final SequenceMatcher localSequence = sequence;
            final int sequenceLength = localSequence.length();

            // Find the max shift possible by scanning back and counting the bytes matched.
            // If we find 256 bytes in one place then nothing can match past that position.
            // If we exceed four times the table size, 98% of positions would be filled assuming a uniform random distribution.
            // This is not optimal - many complicated patterns could cause a bit more processing than strictly required,
            // but it does avoid denial of service and completely unnecessary processing.
            // Note: The final position gives the same max shift as not looking at all, so we don't process that one.
            int posToProcessFrom = sequenceLength - 1;
            int totalBytes = 0;
            for (int position = 0; position < sequenceLength - 1; position++) {
                final int numBytes = localSequence.getNumBytesAtPosition(position);
                totalBytes += numBytes;
                // Stop if we execeed the max bytes, or the bytes for the current position would overwrite everything after it.
                if (totalBytes > MAX_BYTES || numBytes == 256 ) {
                    posToProcessFrom = position;
                    break;
                }
            }

            // Set the default shift to the max shift allowable given the final pos processed.
            final int maxShift = posToProcessFrom + 2;
            final int[] shifts = new int[256];
            Arrays.fill(shifts, maxShift);

            // Now set specific byte shifts for the bytes actually in
            // the sequence itself.  The shift is the distance of each character
            // from the start of the sequence, where the first position equals 1.
            // Each position can match more than one byte (e.g. if a byte class appears).
            for (int position = posToProcessFrom; position >= 0; position--) {
                final byte[] matchingBytes = localSequence.getMatcherForPosition(position).getMatchingBytes();
                final int distanceFromStart = position + 1;
                for (final byte b : matchingBytes) {
                    shifts[b & 0xFF] = distanceFromStart;
                }
            }

            return shifts;
        }
    }
    
}