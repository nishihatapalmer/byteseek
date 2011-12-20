/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
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
 * 
 */

package net.domesdaybook.searcher.sequence;

import java.io.IOException;
import java.util.Arrays;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.reader.Window;
import net.domesdaybook.searcher.ShiftInfo;


/**
 * BoyerMooreHorspoolSearcher searches for a sequence using the
 * Boyer-Moore-Horspool algorithm.
 * <p>
 * This type of search algorithm does not need to examine every byte in 
 * the bytes being searched.  It is sub-linear, in general needing to
 * examine less bytes than actually occur in the bytes being searched.
 * <p>
 * It pre-computes a table of minimum safe shifts for the search pattern. 
 * Given a byte in the bytes being searched, the shift tells us how many 
 * bytes we can safely shift ahead without missing a possible match.  
 * <p>
 * It proceeds by searching for the search pattern backwards,
 * from the last position in the pattern to the first.  The safe shift is looked 
 * up in the table using the value of the byte in the search text at the current
 * position.  If the shift is greater than zero, we know that we can move the
 * current position along by that amount.  If the shift is zero, this means it
 * is not safe to shift, and we must validate that the pattern actually occurs 
 * at this position.  A zero shift just means that the last position of the
 * pattern matches the search text at that point - so it is possible (but not
 * by any means certain) that the rest of the pattern matches.
 * <p>
 * A simple example is looking for the bytes 'XYZ' in the sequence 'ABCDEFGXYZ'.
 * The first attempt is to match 'Z', and we find the byte 'C'.  Since 'C' does
 * not appear anywhere in 'XYZ', we can safely shift 3 bytes ahead and not risk
 * missing a possible match.  In general, the safe shift is either the length of
 * the pattern, if that byte does not appear in the pattern, or the shortest 
 * distance from the end of the pattern where that byte appears.
 * <p>
 * One initially counter-intuitive consequence of this type of search is that
 * the longer the pattern you are searching for, the better the performance
 * usually is, as the possible shifts will be correspondingly bigger.
 * 
 * @author Matt Palmer
 */
public final class BoyerMooreHorspoolSearcher extends AbstractSequenceSearcher {

    private final ShiftInfo shiftInfo;


    /**
     * Constructs a BoyerMooreHorspool searcher given a {@link SequenceMatcher}
     * to search for.
     * 
     * @param sequence 
     */
    public BoyerMooreHorspoolSearcher(final SequenceMatcher sequence) {
        super(sequence);
        shiftInfo = new BoyerMooreHorspoolShiftInfo();
    }
=    
    
    protected long searchForwardsReader(final Reader reader, final long fromPosition, final long toPosition) {
            // Search forwards using the window in which the end of the sequence appears:
            long scanPosition = searchPosition + lastSequencePosition;
            final Window sequenceEndWindow = reader.getWindow(scanPosition);
            while (sequenceEndWindow != null && scanPosition <= lastSearchPosition) {
               
                final int lastArrayPosition = sequenceEndWindow.length() - 1;
                final int arrayMaxPosition = 
                        
                int arraySearchPosition = reader.getWindowOffset(scanPosition);
                
                final byte[] array = sequenceEndWindow.getArray();                
                while (arrayScanPosition <= 
                
                
            }
           
            
            
            // Search in the window byte array for shifts, using the Reader
            // interface for verifiying the sequence with the matcher.
            
            int arraySearchPosition = arrayStartPosition;
            while (arraySearchPosition <= arrayMaxPosition) {
                final int shift = safeShifts[array[arraySearchPosition] & 0xff];
                if (shift == 0) { // a match on the last byte exists, check for a full match:
                    final long matchPosition = searchPosition + arraySearchPosition - arrayStartPosition;
                    if (sequence.matches(reader, matchPosition)) {
                        return matchPosition; // match found.
                    }
                    arraySearchPosition++; // no match was found - only safe to shift by one.
                } else { // skip forwards by the safe shift.
                    arraySearchPosition += shift;
                }
            }
            
            searchPosition += arraySearchPosition - arrayStartPosition;
            window = reader.getWindow(searchPosition);
        

        return NOT_FOUND;        
    }
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public int searchForwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        
        // Get the objects needed to search:
        final int[] safeShifts = shiftInfo.getForwardShifts();
        final SequenceMatcher theMatcher = getMatcher();
        
        // Calculate safe bounds for the start of the search:
        final int lastMatcherPosition = theMatcher.length() - 1;
        int searchPosition = fromPosition > 0?
                             fromPosition + lastMatcherPosition : lastMatcherPosition;
        
        // Calculate safe bounds for the end of the search:
        final int lastPossiblePosition = bytes.length - 1;
        final int finalPosition = toPosition < lastPossiblePosition?
                                  toPosition : lastPossiblePosition;
        
        // Search forwards:
        while (searchPosition <= finalPosition) {
            
            // Get the distance it's safe to shift for the current byte:
            final int shift = safeShifts[bytes[searchPosition] & 0xff];
            
            // If a match on the last byte exists, check for a full match:
            if (shift == 0) { 
                final int startMatchPosition = searchPosition - lastMatcherPosition;
                if (theMatcher.matchesNoBoundsCheck(bytes, startMatchPosition)) {
                    return startMatchPosition; // match found.
                }
                
                // No match was found - only safe to shift by one.
                searchPosition++; 
            } else { 
                // Skip forwards by the safe shift.
                searchPosition += shift;
            }
        }
        
        return NOT_FOUND;
    }    

    
    /**
     * {@inheritDoc}
     * @throws IOException 
     */
    @Override
    public long searchBackwardsReader(final Reader reader, 
            final long fromPosition, final long toPosition ) throws IOException {
        
        // Get the objects needed to search:
        final SequenceMatcher sequence = matcher;
        final int[] safeShifts = shiftInfo.getBackwardShifts();
        
        // Calculate safe bounds for the end of the search:
        final long lastPosition = toPosition > 0?
                                  toPosition : 0;
        
        // Search backwards across the windows:
        long searchPosition = fromPosition;
        Window window = reader.getWindow(searchPosition);
        while (window != null && searchPosition >= lastPosition) {
            
            // Initialise the window search:
            final byte[] array = window.getArray();
            final int arrayStartPosition = reader.getWindowOffset(searchPosition);            
            int arraySearchPosition = arrayStartPosition;
            
            // Search using the byte array for shifts, using the Reader
            // for verifiying the sequence with the matcher:          
            while (arraySearchPosition >= 0) {
                
                // Get the distance it's safe to shift for the current byte:
                final int shift = safeShifts[array[arraySearchPosition] & 0xff];
                
                // If a match on the first byte exists, check for a full match:
                if (shift == 0) { 
                    final long matchPosition = searchPosition - arraySearchPosition + arrayStartPosition;
                    if (sequence.matches(reader, matchPosition)) {
                        return matchPosition; // match found.
                    }
                    // No match was found - only safe to shift back by one.
                    arraySearchPosition--; 
                } else { 
                    // Skip backwards by the safe shift.
                    arraySearchPosition -= shift;
                }
            }
            
            // Move on to the next window search position:
            searchPosition -= arraySearchPosition + arrayStartPosition;
            window = reader.getWindow(searchPosition);
        }

        return NOT_FOUND;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public int searchBackwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        
        // Get objects needed for the search:
        final int[] safeShifts = shiftInfo.getBackwardShifts();
        final SequenceMatcher theMatcher = getMatcher();
        
        // Calculate safe bounds for the start of the search:
        final int firstPossiblePosition = bytes.length - theMatcher.length();        
        int searchPosition = fromPosition < firstPossiblePosition?
                             fromPosition : firstPossiblePosition;
        
        // Calculate safe bounds for the end of the search:
        final int lastPosition = toPosition > 0?
                                 toPosition : 0;
        
        // Search backwards:
        while (searchPosition >= lastPosition) {
            final int shift = safeShifts[bytes[searchPosition] & 0xff];
            if (shift == 0) { // a match on the first position exists, check for a full match:
                if (theMatcher.matchesNoBoundsCheck(bytes, searchPosition)) {
                    return searchPosition; // match found.
                }
                searchPosition--; // no match was found - only safe to shift back by one.
            } else { // skip backwards by the safe shift.
                searchPosition -= shift;
            }            
        }
        return NOT_FOUND;
    }

    
    @Override
    public void prepareForwards() {
        shiftInfo.getForwardShifts();
    }
    

    @Override
    public void prepareBackwards() {
        shiftInfo.getBackwardShifts();
    }

    
    private class BoyerMooreHorspoolShiftInfo extends ShiftInfo {

        public BoyerMooreHorspoolShiftInfo() {
        }
        
        /**
         * Calculates the safe shifts to use if searching forwards.
         * A safe shift is either the length of the sequence, if the
         * byte does not appear in the {@link SequenceMatcher}, or
         * the shortest distance it appears from the end of the matcher.
         */        
        @Override
        protected int[] createForwardShifts() {
            final int[] shifts = new int[256];
            final SequenceMatcher theMatcher = getMatcher();
            final int numBytes = theMatcher.length();
            // Set the default shift to the length of the sequence
            Arrays.fill(shifts, numBytes);

            // Now set specific shifts for the bytes actually in
            // the sequence itself.  The shift is the distance of a position
            // from the end of the sequence, as a zero-indexed offset (zero is
            // the value for the last position in the sequence).
            // Each position can match more than one byte (e.g. if a byte class appears),
            // so we set the distance for each byte which can appear in that position.
            for (int sequencePos = 0; sequencePos < numBytes; sequencePos++) {
                final SingleByteMatcher aMatcher = theMatcher.getByteMatcherForPosition(sequencePos);
                final byte[] matchingBytes = aMatcher.getMatchingBytes();
                final int distanceFromStart = numBytes - sequencePos - 1;
                for (final byte b : matchingBytes) {
                    shifts[b & 0xFF] = distanceFromStart;
                }
            }
            return shifts;
        }

        /**
         * Calculates the safe shifts to use if searching backwards.
         * A safe shift is either the length of the sequence, if the
         * byte does not appear in the {@link SequenceMatcher}, or
         * the shortest distance it appears from the beginning of the matcher, with
         * zero being the value of the first position in the sequence.
         */        
        @Override
        protected int[] createBackwardShifts() {
            // First set the default shift to the length of the sequence
            final int[] shifts = new int[256];
            final SequenceMatcher theMatcher = getMatcher();
            final int numBytes = theMatcher.length();
            Arrays.fill(shifts, numBytes);

            // Now set specific byte shifts for the bytes actually in
            // the sequence itself.  The shift is the position in the sequence,
            // with zero being the first position in the sequence.
            for (int sequencePos = numBytes - 1; sequencePos >= 0; sequencePos--) {
                final SingleByteMatcher aMatcher = theMatcher.getByteMatcherForPosition(sequencePos);
                final byte[] matchingBytes = aMatcher.getMatchingBytes();
                for (final byte b : matchingBytes) {
                    shifts[b & 0xFF] = sequencePos;
                }
            }
            return shifts;
        }
        
    }    

}
