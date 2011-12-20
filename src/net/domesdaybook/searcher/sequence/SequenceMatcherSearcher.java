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
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.reader.Window;

/**
 * SequenceSearcher searches for a sequence by trying for a match in each position.
 * In its worst case, where no match is found, if the sequence is m bytes long,
 * and the bytes being searched are n bytes long, it will take O(n * m) to
 * determine there is no match.
 *
 * @author Matt Palmer
 */
public final class SequenceMatcherSearcher extends AbstractSequenceSearcher {


    /**
     * 
     * @param sequence
     */
    public SequenceMatcherSearcher(final SequenceMatcher sequence) {
        super(sequence);
    }


    /**
     * {@inheritDoc}
     * @throws IOException 
     */
    @Override
    public final long searchForwards(final Reader reader, final long fromPosition, 
            final long toPosition) throws IOException {
        final SequenceMatcher pattern = matcher;  
        long currentPosition = fromPosition > 0? fromPosition : 0;
        Window window = reader.getWindow(currentPosition);
        while (window != null) {
            final byte[] array = window.getArray();
            
            
            
            final int availableSpace = window.length() - reader.getWindowOffset(currentPosition);
            final long endWindowPosition = currentPosition + availableSpace;
            final long lastPosition = endWindowPosition < toPosition?
                                      endWindowPosition : toPosition;
            while (currentPosition <= lastPosition) {
                if (pattern.matches(reader, currentPosition)) {
                    return currentPosition;
                }
                currentPosition++;
            }
            window = reader.getWindow(currentPosition);
        }
        return NOT_FOUND;
    }
    
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public int searchForwards(byte[] bytes, int fromPosition, int toPosition) {
        // Get objects needed for the search:
        final SequenceMatcher theMatcher = getMatcher();
        
        // Calculate safe bounds for the search:
        final int lastPossiblePosition = bytes.length - theMatcher.length();
        final int lastPosition = toPosition < lastPossiblePosition?
                toPosition : lastPossiblePosition;
        int searchPosition = fromPosition < 0? 0 : fromPosition;        
        
        // Search forwards
        while (searchPosition <= lastPosition) {
            if (theMatcher.matchesNoBoundsCheck(bytes, searchPosition)) {
                return searchPosition;
            }
            searchPosition++;
        }
        return NOT_FOUND;    
    }    

    
    /**
     * {@inheritDoc}
     * @throws IOException 
     */
    @Override
    public final long searchBackwards(final Reader reader, final long fromPosition, 
            final long toPosition) throws IOException {
        // Get objects needed for the search:
        final SequenceMatcher theMatcher = getMatcher();
        
        // Calculate safe bounds for the search:
        final long lastPosition = toPosition < 0? 0 : toPosition;        
        final long firstPossiblePosition = reader.length() - theMatcher.length();
        long searchPosition = fromPosition < firstPossiblePosition?
                fromPosition : firstPossiblePosition;
        
        // Search backwards:
        while (searchPosition >= lastPosition) {
            if (theMatcher.matchesNoBoundsCheck(reader, searchPosition)) {
                return searchPosition;
            }
            searchPosition--;
        }
        return NOT_FOUND;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int searchBackwards(byte[] bytes, int fromPosition, int toPosition) {
        // Get objects needed for the search:
        final SequenceMatcher theMatcher = getMatcher();
        
        // Calculate safe bounds for the search:
        final int lastPosition = toPosition < 0? 0 : toPosition;
        final int firstPossiblePosition = bytes.length - theMatcher.length();
        int searchPosition = fromPosition < firstPossiblePosition?
                fromPosition : firstPossiblePosition;
        
        // Search backwards:
        while (searchPosition >= lastPosition) {
            if (theMatcher.matchesNoBoundsCheck(bytes, searchPosition)) {
                return searchPosition;
            }
            searchPosition--;
        }
        return NOT_FOUND;
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public void prepareForwards() {
        // no preparation necessary.
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public void prepareBackwards() {
        // no preparation necessary.
    }

}
