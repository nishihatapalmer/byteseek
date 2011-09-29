/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 * http://www.opensource.org/licenses/BSD-3-Clause
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
 *  * Neither the "byteseek" name nor the names of its contributors 
 *    may be used to endorse or promote products derived from this software 
 *    without specific prior written permission. 
 *  
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
 * 
 */

package net.domesdaybook.searcher.sequence;

import java.io.IOException;
import java.util.Arrays;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.searcher.AbstractSearcher;
import net.domesdaybook.searcher.Searcher;

/**
 *
 * @author matt
 */
public class SundayQuickSearcher extends AbstractSearcher {
    
    // Volatile arrays are usually a bad idea, as volatile applies to the array
    // reference, not to the contents of the array.  However, we will never change
    // the array contents once it is initialised, so this is safe.
    @SuppressWarnings("VolatileArrayField")
    private volatile int[] shiftForwardFunction;
    @SuppressWarnings("VolatileArrayField")
    private volatile int[] shiftBackwardFunction;
    private final SequenceMatcher matcher;


    /**
     * Constructs a BoyerMooreHorspool searcher given a {@link SequenceMatcher}
     * to search for.
     * 
     * @param matcher A {@link SequenceMatcher} to search for.
     */
    public SundayQuickSearcher(final SequenceMatcher sequence) {
        if (sequence == null) {
            throw new IllegalArgumentException("Null sequence passed in to QuickSequenceMatcherSearcher.");
        }        
        this.matcher = sequence;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final long searchForwards(final Reader reader, 
            final long fromPosition, final long toPosition ) throws IOException {
        
        // Get the objects needed to search:
        final int[] safeShifts = getForwardShifts();
        final SequenceMatcher theMatcher = getMatcher();
        
        // Calculate safe bounds for the search:
        final int length = theMatcher.length();
        final long finalPosition = reader.length() - length;
        final long lastPossibleLoopPosition = finalPosition - 1;
        final long lastPosition = toPosition < lastPossibleLoopPosition?
                toPosition : lastPossibleLoopPosition;
        long searchPosition = fromPosition < 0? 0 : fromPosition ;
        
        // Search forwards:
        while (searchPosition <= lastPosition) {
            if (theMatcher.matchesNoBoundsCheck(reader, searchPosition)) {
                return searchPosition;
            }
            searchPosition += safeShifts[reader.readByte(searchPosition + length) & 0xFF];
        }
        
        // Check the final position if necessary:
        if (searchPosition == finalPosition && toPosition >= finalPosition &&
            theMatcher.matches(reader, finalPosition)) {
            return finalPosition;
        }

        return Searcher.NOT_FOUND;
    }



    /**
     * {@inheritDoc}
     */    
    @Override
    public int searchForwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        
        // Get the objects needed to search:
        final int[] safeShifts = getForwardShifts();
        final SequenceMatcher theMatcher = getMatcher();
        
        // Calculate safe bounds for the search:
        final int length = theMatcher.length();
        final int finalPosition = bytes.length - length;
        final int lastPossibleLoopPosition = finalPosition - 1;
        final int lastPosition = toPosition < lastPossibleLoopPosition?
                toPosition : lastPossibleLoopPosition;
        int searchPosition = fromPosition < 0? 0 : fromPosition ;
        
        // Search forwards:
        while (searchPosition <= lastPosition) {
            if (theMatcher.matchesNoBoundsCheck(bytes, searchPosition)) {
                return searchPosition;
            }
            searchPosition += safeShifts[bytes[searchPosition + length] & 0xFF];
        }
        
        // Check the final position if necessary:
        if (searchPosition == finalPosition && toPosition >= finalPosition &&
            theMatcher.matches(bytes, finalPosition)) {
            return finalPosition;
        }

        return Searcher.NOT_FOUND;
    }    

    
    /**
     * {@inheritDoc}
     */
    @Override
    public final long searchBackwards(final Reader reader, 
            final long fromPosition, final long toPosition ) throws IOException {
        
        // Get objects needed to search:
        final int[] safeShifts = getBackwardShifts();
        final SequenceMatcher theMatcher = getMatcher();
        
        // Calculate safe bounds for the search:
        final long lastLoopPosition = toPosition > 0 ? toPosition : 1;
        final long firstPossiblePosition = reader.length() - theMatcher.length();
        long searchPosition = fromPosition < firstPossiblePosition ?
                fromPosition : firstPossiblePosition;
        
        // Search backwards:
        while (searchPosition >= lastLoopPosition) {
            if (theMatcher.matchesNoBoundsCheck(reader, searchPosition)) {
                return searchPosition;
            }
            searchPosition -= safeShifts[reader.readByte(searchPosition - 1) & 0xFF];             
        }
        
        // Check for first position if necessary:
        if (searchPosition == 0 && toPosition < 1 &&
            theMatcher.matches(reader, 0)) {
            return 0;
        }

        return Searcher.NOT_FOUND;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public int searchBackwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        
        // Get objects needed to search:
        final int[] safeShifts = getBackwardShifts();
        final SequenceMatcher theMatcher = getMatcher();
        
        // Calculate safe bounds for the search:
        final int lastLoopPosition = toPosition > 0 ? toPosition : 1;
        final int firstPossiblePosition = bytes.length - theMatcher.length();
        int searchPosition = fromPosition < firstPossiblePosition ?
                fromPosition : firstPossiblePosition;
        
        // Search backwards:
        while (searchPosition >= lastLoopPosition) {
            if (theMatcher.matchesNoBoundsCheck(bytes, searchPosition)) {
                return searchPosition;
            }
            searchPosition -= safeShifts[bytes[searchPosition - 1] & 0xFF];             
        }
        
        // Check for first position if necessary:
        if (searchPosition == 0 && toPosition < 1 &&
            theMatcher.matches(bytes, 0)) {
            return 0;
        }

        return Searcher.NOT_FOUND;
    }    

    
    /**
     *
     * Uses Single-Check lazy initialisation.  This can result in the field
     * being initialised more than once, but this doesn't really matter.
     * 
     * @return A 256-element array of integers, giving the safe shift
     * for a given byte when searching forwards.
     */
    private int[] getForwardShifts() {
        int[] result = shiftForwardFunction;
        if (result == null) {
            shiftForwardFunction = result = createForwardShifts();
        }
        return result;
    }


    /**
     *
     * Uses Single-Check lazy initialisation.  This can result in the field
     * being initialised more than once, but this doesn't really matter.
     * 
     * @return A 256-element array of integers, giving the safe shift
     * for a given byte when searching backwards.
     */
    private int[] getBackwardShifts() {
        int[] result = shiftBackwardFunction;
        if (result == null) {
            shiftBackwardFunction = result = createBackwardShifts();
        }
        return result;
    }
    
    
    
    /**
     * Calculates the safe shifts to use if searching backwards.
     * A safe shift is either the length of the sequence, if the
     * byte does not appear in the {@link SequenceMatcher}, or
     * the shortest distance it appears from the beginning of the matcher.
     */
    private int[] createBackwardShifts() {
        // First set the default shift to the length of the sequence
        // (negative if search direction is reversed)
        final int[] shifts = new int[256];
        final SequenceMatcher theMatcher = getMatcher();
        final int numBytes = theMatcher.length();
        Arrays.fill(shifts, numBytes + 1);
        
        // Now set specific byte shifts for the bytes actually in
        // the sequence itself.  The shift is the distance of each character
        // from the start of the sequence, where the first position equals 1.
        // Each position can match more than one byte (e.g. if a byte class appears).
        for (int sequenceByteIndex = numBytes - 1; sequenceByteIndex >= 0; sequenceByteIndex++) {
            final SingleByteMatcher aMatcher = theMatcher.getByteMatcherForPosition(sequenceByteIndex);
            final byte[] matchingBytes = aMatcher.getMatchingBytes();
            final int distanceFromStart = sequenceByteIndex + 1;
            for (final byte b : matchingBytes) {
                shifts[b & 0xFF] = distanceFromStart;
            }
        }
        
        return shifts;
    }


    /**
     * Calculates the safe shifts to use if searching forwards.
     * A safe shift is either the length of the sequence plus one, if the
     * byte does not appear in the {@link SequenceMatcher}, or
     * the shortest distance it appears from the end of the matcher.
     */
    private int[] createForwardShifts() {
        // First set the default shift to the length of the sequence plus one.
        final int[] shifts = new int[256];
        final SequenceMatcher theMatcher = getMatcher();
        final int numBytes = theMatcher.length();
        Arrays.fill(shifts, numBytes + 1);

        // Now set specific byte shifts for the bytes actually in
        // the sequence itself.  The shift is the distance of each character
        // from the end of the sequence, where the last position equals 1.
        // Each position can match more than one byte (e.g. if a byte class appears).
        for (int sequenceByteIndex = 0; sequenceByteIndex < numBytes; sequenceByteIndex++) {
            final SingleByteMatcher aMatcher = theMatcher.getByteMatcherForPosition(sequenceByteIndex);
            final byte[] matchingBytes = aMatcher.getMatchingBytes();
            final int distanceFromEnd = numBytes - sequenceByteIndex;
            for (final byte b : matchingBytes) {
                shifts[b & 0xFF] = distanceFromEnd;
            }
        }
        
        return shifts;
    }

    /**
     *
     * @return The underlying {@link SequenceMatcher} to search for.
     */
    public final SequenceMatcher getMatcher() {
        return matcher;
    }
    

    /**
     * @inheritDoc
     */
    @Override
    public void prepareForwards() {
        getForwardShifts();
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public void prepareBackwards() {
        getBackwardShifts();
    }
    
}