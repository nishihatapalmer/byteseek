/*
 * Copyright Matt Palmer 2011-2019, All rights reserved.
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

package net.byteseek.searcher;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import net.byteseek.io.reader.windows.Window;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.MatchResult;
import net.byteseek.matcher.Matcher;
import net.byteseek.utils.ArgUtils;

/**
 * A Searcher which looks for an underlying {@link Matcher} in the simplest manner
 * possible: by trying to match at every possible position until a match is 
 * found or not.
 * <p>
 * The performance of this Searcher is generally poor (although it may compare
 * favorably for very short searches due to its essential simplicity).
 * Combining this search with a fast multi-sequence matcher, for example, 
 * a {@link net.byteseek.incubator.matcher.multisequence.TrieMultiSequenceMatcher} may perform reasonably well where there are a very large
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
 * searcher in multiple threads simultaneously. However, note that {@link WindowReader}
 * implementations passed in to search methods may not be thread-safe.  If byte
 * arrays are being searched, they must not be modified during searching.
 * 
 * @author Matt Palmer
 */
public final class MatcherSearcher extends AbstractSearcher {

    private final Matcher matcher;
    
    /**
     * Constructor for a MatcherSearcher, taking the matcher to be searched for
     * as a parameter.
     * 
     * @param matcher The Matcher to search for.
     */
    public MatcherSearcher(final Matcher matcher) {
        ArgUtils.checkNullObject(matcher, "matcher");
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
     * @throws IOException if a problem occurred reading bytes from the WindowReader.
     */
    @Override
    public int searchForwards(final WindowReader reader,
                               final long fromPosition, final long toPosition,
                               final Collection<MatchResult> results) throws IOException {
        
        // Initialise search:
        final Matcher theMatcher = matcher;    
        long searchPosition = fromPosition > 0? fromPosition : 0;
        
        // As long as there is more data to search in:
        Window window;        
        while (searchPosition <= toPosition && 
               (window = reader.getWindow(searchPosition))!= null) {
            
            // Calculate search bounds for searching in this window:
            final int searchLength = window.length() - reader.getWindowOffset(searchPosition);
            final long searchEndPosition = searchPosition + searchLength - 1;
            final long finalPosition = toPosition < searchEndPosition? toPosition : searchEndPosition;
            
            // Search forwards in the window:
            while (searchPosition <= finalPosition) {
                final int numMatches = theMatcher.matches(reader, searchPosition, results);
                if (numMatches > 0) {
                    return numMatches;
                }
                searchPosition++;
            }
        }
        return NO_RESULTS_FOUND;
    }

    @Override
    public int searchForwards(final byte[] bytes,
                               final int fromPosition, final int toPosition,
                               final Collection<MatchResult> results) {
        
        // Use a local reference to the matcher for performance reasons:
        final Matcher theMatcher = matcher;
        
        // Calculate safe bounds for searching in the byte array:
        final int arrayEndPosition = bytes.length - 1;
        final int searchEndPosition = toPosition < arrayEndPosition? toPosition : arrayEndPosition;
        int searchPosition = fromPosition > 0? fromPosition : 0;
        
        // Search forwards:
        while (searchPosition <= searchEndPosition) {
            final int numMatches = theMatcher.matches(bytes, searchPosition, results);
            if (numMatches > 0) {
                return numMatches;
            }
            searchPosition++;
        }
        return NO_RESULTS_FOUND;
    }

    @Override
    public int searchBackwards(final WindowReader reader,
                               final long fromPosition, final long toPosition,
                               final Collection<MatchResult> results) throws IOException {
        
        // Initialise search:
        final Matcher theMatcher = matcher;        
        final long endSearchPosition = Math.max(0L, toPosition);
        long searchPosition = withinLength(reader, fromPosition);
        
        // Search backwards:
        while (searchPosition >= endSearchPosition) {
            final int numMatches = theMatcher.matches(reader, searchPosition, results);
            if (numMatches > 0) {
                return numMatches;
            }
            searchPosition--;
        }
        return NO_RESULTS_FOUND;
    }

    @Override
    public int searchBackwards(final byte[] bytes,
                                final int fromPosition, final int toPosition,
                                final Collection<MatchResult> results) {
        // Initialise search:
        final Matcher theMatcher = matcher;
        final int lastPossiblePosition = bytes.length - 1;
        final int endSearchPosition = toPosition > 0? 
                                      toPosition : 0;
        int searchPosition = fromPosition < lastPossiblePosition? 
                             fromPosition : lastPossiblePosition;
        
        // Search backwards:
        while (searchPosition >= endSearchPosition) {
            final int numMatches = theMatcher.matches(bytes, searchPosition, results);
            if (numMatches > 0) {
                return numMatches;
            }
            searchPosition--;
        }
        return NO_RESULTS_FOUND;
    }

    @Override
    public void prepareForwards() {
        // no preparation necessary.
    }

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
