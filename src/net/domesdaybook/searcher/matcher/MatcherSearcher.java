/*
 * Copyright Matt Palmer 2011-2012, All rights reserved.
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

package net.domesdaybook.searcher.matcher;

import java.io.IOException;
import java.util.List;
import net.domesdaybook.matcher.Matcher;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.reader.Window;
import net.domesdaybook.searcher.AbstractSearcher;
import net.domesdaybook.searcher.SearchUtils;
import net.domesdaybook.searcher.SearchResult;


/**
 * A Searcher which looks for an underlying {@link Matcher} in the simplest manner
 * possible: by trying to match at every possible position until a match is 
 * found or not.
 * <p>
 * The performance of this Searcher is generally poor (although it may compare
 * favorably for very short searches due to its essential simplicity).
 * Combining this search with a fast multi-sequence matcher, for example, 
 * a {@link TrieMatcher} may perform reasonably well where there are a very large
 * number of sequences to find, or if there are very short sequences to match 
 * (which can limit the advantage of more sophisticated shift-based searchers).
 * <p>
 * It can search for any matcher at all, with no knowledge of its implementation,
 * but consequently is less optimal than a searcher designed for a specific
 * type of matcher, which can exploit knowledge of the structure of the matcher 
 * to search more efficiently. It is also only capable of saying that a Matcher
 * was found, not which component of a matcher (if, for example, a multi sequence
 * matcher was used).
 * <p>
 * Thread safety: this class is immutable, so it is safe to use this
 * searcher in multiple threads simultaneously. However, note that {@link Reader}
 * implementations passed in to search methods may not be thread-safe.  If byte
 * arrays are being searched, they must not be modified during searching.
 * 
 * @author Matt Palmer
 */
public final class MatcherSearcher extends AbstractSearcher<Matcher> {

    private final Matcher matcher;
    
    /**
     * Constructor for a MatcherSearcher, taking the matcher to be searched for
     * as a parameter.
     * 
     * @param matcher The Matcher to search for.
     */
    public MatcherSearcher(final Matcher matcher) {
        if (matcher == null) {
            throw new IllegalArgumentException("Null matcher passed in to MatcherSearcher.");
        }
        this.matcher = matcher;
    }
    
    
    /**
     * {@inheritDoc}
     * <p>
     * This implementation of searchFowards is complicated by not knowing
     * the length of the Matcher (some matchers may match variable lengths),
     * and we avoid looking at the length of the reader in order to be 
     * stream-friendly when processing (discovering the length of the stream 
     * would necessitate reading in the entire stream before searching).
     * <p>
     * It uses the presence of a Window at a given position to indicate whether
     * there is more to search, and searches within the limits of each window,
     * the end of the final window, or up to the specified "to" position, 
     * whichever comes first.  If there are more windows left, then they are
     * searched in turn.  
     * 
     * @throws IOException if a problem occurred reading bytes from the Reader.
     */
    @Override
    public List<SearchResult<Matcher>> searchForwards(final Reader reader, final long fromPosition, 
           final long toPosition) throws IOException {
        
        // Initialise search:
        final Matcher theMatcher = matcher;    
        long searchPosition = fromPosition > 0? 
                              fromPosition : 0;
        
        // As long as there is more data to search in:
        Window window;        
        while (searchPosition <= toPosition && 
               (window = reader.getWindow(searchPosition))!= null) {
            
            // Calculate search bounds for searching in this window:
            final int searchLength = window.length() - reader.getWindowOffset(searchPosition);
            final long searchEndPosition = searchPosition + searchLength - 1;
            final long finalPosition = toPosition < searchEndPosition?
                                       toPosition : searchEndPosition;
            
            // Search forwards in the window:
            while (searchPosition <= finalPosition) {
                if (theMatcher.matches(reader, searchPosition)) {
                    return SearchUtils.singleResult(searchPosition, theMatcher);
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
    public List<SearchResult<Matcher>> searchForwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        
        // Use a local reference to the matcher for performance reasons:
        final Matcher theMatcher = matcher;
        
        // Calculate safe bounds for searching in the byte array:
        final int arrayEndPosition = bytes.length - 1;
        final int searchEndPosition = toPosition < arrayEndPosition? 
                                      toPosition : arrayEndPosition;
        int searchPosition = fromPosition > 0?
                              fromPosition : 0;           
        
        // Search forwards:
        while (searchPosition <= searchEndPosition) {
            if (theMatcher.matches(bytes, searchPosition)) {
                return SearchUtils.singleResult(searchPosition, theMatcher);
            }
            searchPosition++;
        }
        return SearchUtils.noResults();
    }
  
   
    /**
     * {@inheritDoc}
     */
    @Override
    public List<SearchResult<Matcher>> searchBackwards(final Reader reader, final long fromPosition, 
           final long toPosition) throws IOException {
        
        // Initialise search:
        final Matcher theMatcher = matcher;        
        final long endSearchPosition = toPosition > 0? 
                                       toPosition : 0;
        long searchPosition = withinLength(reader, fromPosition);
        
        // Search backwards:
        while (searchPosition >= endSearchPosition) {
            if (theMatcher.matches(reader, searchPosition)) {
                return SearchUtils.singleResult(searchPosition, theMatcher);
            }
            searchPosition--;
        }
        return SearchUtils.noResults();
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<SearchResult<Matcher>> searchBackwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        // Initialise search:
        final Matcher theMatcher = matcher;
        final int lastPossiblePosition = bytes.length - 1;
        final int endSearchPosition = toPosition > 0? 
                                      toPosition : 0;
        int searchPosition = fromPosition < lastPossiblePosition? 
                             fromPosition : lastPossiblePosition;
        
        // Search backwards:
        while (searchPosition >= endSearchPosition) {
            if (theMatcher.matches(bytes, searchPosition)) {
                return SearchUtils.singleResult(searchPosition, theMatcher);
            }
            searchPosition--;
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
    
    
    /**
     * Returns a string representation of this searcher.
     * The precise format returned is subject to change, but in general it will
     * return the type of searcher and the sequence being searched for.
     * 
     * @return String a representation of the searcher.
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '(' + matcher + ')';
    }        

    
}
