/*
 * Copyright Matt Palmer 2011-12, All rights reserved.
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

package net.domesdaybook.searcher.multisequence;

import net.domesdaybook.matcher.bytes.ByteMatcher;
import net.domesdaybook.matcher.multisequence.MultiSequenceMatcher;
import net.domesdaybook.object.LazyObject;
import net.domesdaybook.reader.Reader;

/**
 *
 * @author matt
 */
public class SetHorspoolSearcher extends AbstractMultiSequenceSearcher {

    private final LazyObject<SearchInfo> forwardInfo;
    private final LazyObject<SearchInfo> backwardInfo;
    
    
    public SetHorspoolSearcher(final MultiSequenceMatcher sequences) {
        super(sequences);
        forwardInfo = new ForwardSearchInfo();
        backwardInfo = new BackwardSearchInfo();
    }
    
    
    @Override
    protected long doSearchForwards(Reader reader, long searchPosition, long lastSearchPosition) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    @Override
    protected long doSearchBackwards(Reader reader, long searchPosition, long lastSearchPosition) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    public int searchForwards(byte[] bytes, int fromPosition, int toPosition) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    public int searchBackwards(byte[] bytes, int fromPosition, int toPosition) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    @Override
    public void prepareForwards() {
        forwardInfo.get();
    }

    
    @Override
    public void prepareBackwards() {
        backwardInfo.get();
    }
    
    
    private static class SearchInfo {
        public int[] shifts;
        public ByteMatcher matcher;
    }    
    
    
    private class ForwardSearchInfo extends LazyObject<SearchInfo> {

        public ForwardSearchInfo() {
        }
        
        /**
         * Calculates the safe shifts to use if searching forwards.
         * A safe shift is either the length of the sequence, if the
         * byte does not appear in the {@link SequenceMatcher}, or
         * the shortest distance it appears from the end of the matcher.
         */        
        @Override
        protected SearchInfo create() {
            // Get info about the matcher:
            final SequenceMatcher sequence = getMatcher();            
            final int sequenceLength = sequence.length();            
            
            // Create the search info object:
            final SearchInfo info = new SearchInfo();
            final int lastPosition = sequenceLength - 1;
            info.matcher = sequence.getMatcherForPosition(lastPosition);
            if (lastPosition == 0) {
                info.verifier = info.matcher;
            } else {
                info.verifier = sequence.subsequence(0, lastPosition);
            }
            info.shifts = new int[256];            

            // Set the default shift to the length of the sequence
            Arrays.fill(info.shifts, sequenceLength);

            // Now set specific shifts for the bytes actually in
            // the sequence itself.  The shift is the distance of a position
            // from the end of the sequence, but we do not create a shift for
            // the very last position.
            for (int sequencePos = 0; sequencePos < lastPosition; sequencePos++) {
                final ByteMatcher aMatcher = sequence.getMatcherForPosition(sequencePos);
                final byte[] matchingBytes = aMatcher.getMatchingBytes();
                final int distanceFromEnd = sequenceLength - sequencePos - 1;
                for (final byte b : matchingBytes) {
                    info.shifts[b & 0xFF] = distanceFromEnd;
                }
            }

            return info;
        }
    }
    
    
    private class BackwardSearchInfo extends LazyObject<SearchInfo> {

        public BackwardSearchInfo() {
        }
        
        /**
         * Calculates the safe shifts to use if searching backwards.
         * A safe shift is either the length of the sequence, if the
         * byte does not appear in the {@link SequenceMatcher}, or
         * the shortest distance it appears from the beginning of the matcher, with
         * zero being the value of the first position in the sequence.
         */        
        @Override
        protected SearchInfo create() {
            // Get info about the matcher:
            final SequenceMatcher sequence = getMatcher();            
            final int sequenceLength = sequence.length();            
            
            // Create the search info object:
            final SearchInfo info = new SearchInfo();
            final int lastPosition = sequenceLength - 1;
            info.matcher = sequence.getMatcherForPosition(lastPosition);
            if (lastPosition == 0) {
                info.verifier = info.matcher;
            } else {
                info.verifier = sequence.subsequence(1, sequenceLength);
            }
            info.shifts = new int[256];            

            // Set the default shift to the length of the sequence
            Arrays.fill(info.shifts, sequenceLength);

            // Now set specific byte shifts for the bytes actually in
            // the sequence itself.  The shift is the position in the sequence,
            // but we do not create a shift for the first position 0.
            for (int sequencePos = lastPosition; sequencePos > 0; sequencePos--) {
                final ByteMatcher aMatcher = sequence.getMatcherForPosition(sequencePos);
                final byte[] matchingBytes = aMatcher.getMatchingBytes();
                for (final byte b : matchingBytes) {
                    info.shifts[b & 0xFF] = sequencePos;
                }
            }
            return info;
        }
        
    }        
    
}
