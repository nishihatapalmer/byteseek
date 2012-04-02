/*
 * Copyright Matt Palmer 2012, All rights reserved.
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

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import net.domesdaybook.matcher.multisequence.MultiSequenceMatcher;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.reader.Window;
import net.domesdaybook.searcher.SearchUtils;
import net.domesdaybook.searcher.SearchResult;

/**
 *
 * @author Matt Palmer
 */
public class MultiSequenceMatcherSearcher extends AbstractMultiSequenceSearcher {

    public MultiSequenceMatcherSearcher(final MultiSequenceMatcher matcher) {
        super(matcher);
    }
    
    
    @Override
    protected List<SearchResult<SequenceMatcher>> doSearchForwards(final Reader reader,
        final long fromPosition, final long toPosition) throws IOException {
        // Initialise:
        final MultiSequenceMatcher matcher = sequences;  
        long searchPosition = fromPosition > 0? 
                              fromPosition : 0;
        
        // While there is data still to search in:
        Window window;
        while (searchPosition <= toPosition &&
               (window = reader.getWindow(searchPosition)) != null) {

            // Calculate bounds for searching over this window:
            final int availableSpace = window.length() - reader.getWindowOffset(searchPosition);
            final long endWindowPosition = searchPosition + availableSpace;
            final long lastPosition = endWindowPosition < toPosition?
                                      endWindowPosition : toPosition;
            
            // Search forwards up to the end of this window:
            while (searchPosition <= lastPosition) {
                final Collection<SequenceMatcher> matches = matcher.allMatches(reader, searchPosition);
                if (!matches.isEmpty()) {
                    return SearchUtils.resultsAtPosition(searchPosition, matches);
                }
                searchPosition++;
            }
            
        }
        return SearchUtils.noResults();
    }


    @Override
    public List<SearchResult<SequenceMatcher>> searchForwards(final byte[] bytes, 
        final int fromPosition, final int toPosition) {
        
        // Initialise:
        final MultiSequenceMatcher matcher = sequences;
        
        // Calculate bounds for the search:
        final int lastPossiblePosition = bytes.length - sequences.getMinimumLength();
        final int lastPosition = toPosition < lastPossiblePosition?
                                 toPosition : lastPossiblePosition;
        int searchPosition = fromPosition > 0?
                             fromPosition : 0;
        
        // Search forwards up to the last possible position:
        while (searchPosition <= lastPosition) {
            final Collection<SequenceMatcher> matches = matcher.allMatches(bytes, searchPosition);
            if (!matches.isEmpty()) {
                return SearchUtils.resultsAtPosition(searchPosition, matches);
            }
            searchPosition++;
        }
        return SearchUtils.noResults();           
    }


    @Override
    protected List<SearchResult<SequenceMatcher>> doSearchBackwards(final Reader reader, 
        final long fromPosition, final long toPosition) throws IOException {
        // Initialise:
        final MultiSequenceMatcher matcher = sequences;
        long searchPosition = withinLength(reader, fromPosition);
        
        // While there is data to search in:
        Window window;
        while (searchPosition >= toPosition &&
               (window = reader.getWindow(searchPosition)) != null) {
            
            // Calculate bounds for searching back across this window:
            final long windowStartPosition = window.getWindowPosition();
            final long lastSearchPosition = toPosition > windowStartPosition?
                                            toPosition : windowStartPosition;
            
            // Search backwards:
            while (searchPosition >= lastSearchPosition) {
                final Collection<SequenceMatcher> matches = matcher.allMatches(reader, searchPosition);
                if (!matches.isEmpty()) {
                    return SearchUtils.resultsAtPosition(searchPosition, matches);
                }
                searchPosition--;
            }
        }
        
        return SearchUtils.noResults();
    }


    @Override
    public List<SearchResult<SequenceMatcher>> searchBackwards(final byte[] bytes, 
        final int fromPosition, final int toPosition) {
        
        // Initialise:
        final MultiSequenceMatcher matcher = sequences;
        
        // Calculate safe bounds for the search:
        final int lastPosition = toPosition > 0?
                                 toPosition : 0;
        final int firstPossiblePosition = bytes.length - sequences.getMinimumLength();
        int searchPosition = fromPosition < firstPossiblePosition?
                             fromPosition : firstPossiblePosition;
        
        // Search backwards:
        while (searchPosition >= lastPosition) {
            final Collection<SequenceMatcher> matches = matcher.allMatches(bytes, searchPosition);            
            if (!matches.isEmpty()) {
                return SearchUtils.resultsAtPosition(searchPosition, matches);
            }
            searchPosition--;
        }
        return SearchUtils.noResults();
    }

    
    @Override
    public void prepareForwards() {
        //  nothing to prepare.
    }

    
    @Override
    public void prepareBackwards() {
        // nothing to prepare.
    }
    
}
