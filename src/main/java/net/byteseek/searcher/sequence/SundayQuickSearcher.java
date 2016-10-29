/*
 * Copyright Matt Palmer 2011-2016, All rights reserved.
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
 *
 * @author Matt Palmer
 */
public final class SundayQuickSearcher extends AbstractSequenceWindowSearcher<SequenceMatcher> {

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
    public long doSearchForwards(final WindowReader reader,
            final long fromPosition, final long toPosition ) throws IOException {

        // Initialise
        final int[] safeShifts = forwardInfo.get();
        final SequenceMatcher theSequence = sequence;
        final int length = theSequence.length();
        long searchPosition = fromPosition;

        // While there is a window to search in...
        // If there is no window immediately after the sequence,
        // then there is no match, since this is only invoked if the 
        // sequence is already crossing into another window.         
        Window window;
        while (searchPosition <= toPosition &&
                (window = reader.getWindow(searchPosition + length)) != null) {

            // Initialise array search:
            final byte[] array = window.getArray();
            final int arrayStartPosition = reader.getWindowOffset(searchPosition + length);
            final int arrayEndPosition = window.length() - 1;
            final long distanceToEnd = toPosition - window.getWindowPosition() + length;
            final int finalPosition = distanceToEnd < arrayEndPosition ?
                    (int) distanceToEnd : arrayEndPosition;
            int arraySearchPosition = arrayStartPosition;

            // Search fowards in the array using the reader interface to match.
            // The loop does not check the final position, as we shift on the byte
            // after the sequence (so would get an IndexOutOfBoundsException in the final position).
            while (arraySearchPosition < finalPosition) {
                if (theSequence.matches(reader, searchPosition)) {
                    return searchPosition;
                }
                final int shift = safeShifts[array[arraySearchPosition] & 0xFF];
                searchPosition += shift;
                arraySearchPosition += shift;
            }

            // Check final position if necessary:
            if (arraySearchPosition == finalPosition ||
                    searchPosition == toPosition) {
                if (theSequence.matches(reader, searchPosition)) {
                    return searchPosition;
                }
                searchPosition += safeShifts[array[arraySearchPosition] & 0xFF];
            }
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
    public long doSearchBackwards(final WindowReader reader,
            final long fromPosition, final long toPosition ) throws IOException {
        
         // Initialise 
        final int[] safeShifts = forwardInfo.get();
        final SequenceMatcher theSequence = sequence;
        long searchPosition = fromPosition;
        
        // While there is a window to search in...
        // If there is no window immediately before the sequence,
        // then there is no match, since this is only invoked if the 
        // sequence is already crossing into another window.        
        Window window;
        while (searchPosition >= toPosition &&
               (window = reader.getWindow(searchPosition - 1)) != null) {
            
            // Initialise array search:
            final byte[] array = window.getArray();
            final int arrayStartPosition = reader.getWindowOffset(searchPosition - 1);
            
            // Search to the beginning of the array, or the final search position,
            // whichver comes first.
            final long endRelativeToWindow = toPosition - window.getWindowPosition();
            final int arrayEndSearchPosition = endRelativeToWindow > 0?
                                         (int) endRelativeToWindow : 0;
            int arraySearchPosition = arrayStartPosition;
            
            // Search backwards in the array using the reader interface to match.
            // The loop does not check the final position, as we shift on the byte
            // before it.
            while (arraySearchPosition > arrayEndSearchPosition) {
                if (theSequence.matches(reader, searchPosition)) {
                    return searchPosition;
                }
                final int shift = safeShifts[array[arraySearchPosition] & 0xFF];
                searchPosition -= shift;
                arraySearchPosition -= shift;
            }

            // Check final position if necessary:
            if (arraySearchPosition == arrayEndSearchPosition ||
                searchPosition == toPosition) {
                if (theSequence.matches(reader, searchPosition)) {
                    return searchPosition;
                }
                searchPosition -= safeShifts[array[arraySearchPosition] & 0xFF];
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
            // First set the default shift to the length of the sequence plus one.
            final int[] shifts = new int[256];
            final int numBytes = sequence.length();
            Arrays.fill(shifts, numBytes + 1);

            //TODO: deal with pathological cases where we have embedded fixed gaps or any bytes that
            //      fundamentally limit the max shift to the position they exist at.

            // Now set specific byte shifts for the bytes actually in
            // the sequence itself.  The shift is the distance of each character
            // from the end of the sequence, where the last position equals 1.
            // Each position can match more than one byte (e.g. if a byte class appears).
            for (int sequenceByteIndex = 0; sequenceByteIndex < numBytes; sequenceByteIndex++) {
                final ByteMatcher aMatcher = sequence.getMatcherForPosition(sequenceByteIndex);
                final byte[] matchingBytes = aMatcher.getMatchingBytes();
                final int distanceFromEnd = numBytes - sequenceByteIndex;
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
            // First set the default shift to the length of the sequence
            // (negative if search direction is reversed)
            final int[] shifts = new int[256];
            final int numBytes = sequence.length();
            Arrays.fill(shifts, numBytes + 1);

            //TODO: deal with pathological cases where we have embedded fixed gaps or any bytes that
            //      fundamentally limit the max shift to the position they exist at.

            // Now set specific byte shifts for the bytes actually in
            // the sequence itself.  The shift is the distance of each character
            // from the start of the sequence, where the first position equals 1.
            // Each position can match more than one byte (e.g. if a byte class appears).
            for (int sequenceByteIndex = numBytes - 1; sequenceByteIndex >= 0; sequenceByteIndex--) {
                final ByteMatcher aMatcher = sequence.getMatcherForPosition(sequenceByteIndex);
                final byte[] matchingBytes = aMatcher.getMatchingBytes();
                final int distanceFromStart = sequenceByteIndex + 1;
                for (final byte b : matchingBytes) {
                    shifts[b & 0xFF] = distanceFromStart;
                }
            }

            return shifts;
        }
    }
    
}