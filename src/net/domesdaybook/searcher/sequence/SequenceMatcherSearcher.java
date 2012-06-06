/*
 * Copyright Matt Palmer 2009-2012, All rights reserved.
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
 */

package net.domesdaybook.searcher.sequence;

import java.io.IOException;
import java.util.List;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.reader.Window;
import net.domesdaybook.searcher.SearchUtils;
import net.domesdaybook.searcher.SearchResult;

/**
 * SequenceMatcherSearcher searches for a sequence by trying for a match in each position.
 * In its worst case, where no match is found, if the sequence is m bytes long,
 * and the bytes being searched are n bytes long, it can take O(n * m) to
 * determine there is no match.
 * <p>
 * The algorithm used by this search is exactly the same as that used for the
 * {@link net.domesdaybook.searcher.matcher.MatcherSearcher} searcher.  However, since we know that we are looking for
 * a sequence with a defined length, the search can be more efficiently partitioned
 * between searching directly in byte arrays when the sequence fits, only using
 * the less efficient reader interface when the sequence crosses over windows.
 * <p>
 * Thread safety: this class is immutable, so it is safe to use this
 * searcher in multiple threads simultaneously. However, note that {@link Reader}
 * implementations passed in to search methods may not be thread-safe.  If byte
 * arrays are being searched, they must not be modified during searching.
 *
 * @author Matt Palmer
 */
public final class SequenceMatcherSearcher extends AbstractSequenceSearcher {


    /**
     * Constructs a SequenceMatcherSearcher given a {@link SequenceMatcher}.
     * 
     * @param sequence The SequenceMatcher to search for.
     */
    public SequenceMatcherSearcher(final SequenceMatcher sequence) {
        super(sequence);
    }


    /**
     * {@inheritDoc}
     */    
    @Override
    public List<SearchResult<SequenceMatcher>> searchForwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        // Initialise:
        final SequenceMatcher sequence = matcher;
        
        // Calculate safe bounds for the search:
        final int lastPossiblePosition = bytes.length - sequence.length();
        final int lastPosition = toPosition < lastPossiblePosition?
                                 toPosition : lastPossiblePosition;
        int searchPosition = fromPosition > 0?
                             fromPosition : 0;
        
        // Search forwards
        while (searchPosition <= lastPosition) {
            if (sequence.matchesNoBoundsCheck(bytes, searchPosition)) {
                return SearchUtils.singleResult(searchPosition, sequence);
            }
            searchPosition++;
        }
        return SearchUtils.noResults();    
    }    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<SearchResult<SequenceMatcher>> doSearchForwards(final Reader reader, final long fromPosition, 
            final long toPosition) throws IOException {
        // Initialise:
        final SequenceMatcher sequence = matcher;  
        long searchPosition = fromPosition > 0? 
                              fromPosition : 0;
        
        // While there is data still to search in:
        Window window;
        while (searchPosition <= toPosition &&
               (window = reader.getWindow(searchPosition)) != null) {

            // Calculate bounds for searching over this window:
            final int searchLength = window.length() - reader.getWindowOffset(searchPosition);
            final long endWindowPosition = searchPosition + searchLength - 1;
            final long lastPosition = endWindowPosition < toPosition?
                                      endWindowPosition : toPosition;
            
            // Search forwards up to the end of this window:
            while (searchPosition <= lastPosition) {
                if (sequence.matches(reader, searchPosition)) {
                    return SearchUtils.singleResult(searchPosition, sequence);
                }
                searchPosition++;
            }
        }
        return SearchUtils.noResults();
    }
    
   
    /**
     * {@inheritDoc}
     */
    @Override
    public List<SearchResult<SequenceMatcher>> searchBackwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        // Initialise:
        final SequenceMatcher sequence = matcher;
        
        // Calculate safe bounds for the search:
        final int lastPosition = toPosition > 0?
                                 toPosition : 0;
        final int firstPossiblePosition = bytes.length - sequence.length();
        int searchPosition = fromPosition < firstPossiblePosition?
                             fromPosition : firstPossiblePosition;
        
        // Search backwards:
        while (searchPosition >= lastPosition) {
            if (sequence.matchesNoBoundsCheck(bytes, searchPosition)) {
                return  SearchUtils.singleResult(searchPosition, sequence);
            }
            searchPosition--;
        }
        return SearchUtils.noResults();
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<SearchResult<SequenceMatcher>> doSearchBackwards(final Reader reader, final long fromPosition, 
            final long toPosition) throws IOException {
        // Initialise:
        final SequenceMatcher sequence = matcher;
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
                if (sequence.matches(reader, searchPosition)) {
                    return SearchUtils.singleResult(searchPosition, sequence);
                }
                searchPosition--;
            }
        }
        return SearchUtils.noResults();
    }

   
    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareForwards() {
        // no preparation necessary.
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareBackwards() {
        // no preparation necessary.
    }
    
}
