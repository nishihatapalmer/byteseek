/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 *
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

package net.domesdaybook.searcher.matcher;

import java.io.IOException;
import net.domesdaybook.matcher.Matcher;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.searcher.AbstractSearcher;
import net.domesdaybook.searcher.Searcher;


/**
 * A Searcher which looks for an underlying {@link Matcher} in the simplest manner
 * possible: by trying to match at every possible position until a match is 
 * found or not.
 * <p/>
 * The performance of this Searcher is generally poor (although it may compare
 * favorably for very, very short searches due to its essential simplicity). 
 * <p/>
 * Because it is not optimised for particular kinds of matcher, 
 * it can search for any matcher at all, with no knowledge of its implementation.
 * This also means that it can't directly use Window-based searching.  
 * Without knowing the maximum length of a matcher, it must fall back on the 
 * underlying Matcher to interact with the Reader.
 * <p/>
 * Design question: can we provide an interface for querying max / min lengths
 * of all Matchers without polluting it too badly...?  Some Matchers will have
 * no limit to their possible length (e.g. a reg ex using a .* ). Others may
 * have definable limits, but which are expensive to compute.  At present, limits
 * are only defined on sub-interfaces of Matcher which actually have them.
 * 
 * @author Matt Palmer
 */
public class MatcherSearcher extends AbstractSearcher {

    private final Matcher matcher;
    
    MatcherSearcher(final Matcher matcher) {
        this.matcher = matcher;
    }
    
    
   
    /**
     * @inheritDoc
     */
    @Override
    public long searchForwards(final Reader reader, final long fromPosition, 
           final long toPosition) throws IOException {
        long searchPosition = fromPosition > 0? fromPosition : 0;
        while (searchPosition <= toPosition) {
            if (matcher.matches(reader, searchPosition)) {
                return searchPosition;
            }
            searchPosition++;
        }
        return Searcher.NOT_FOUND;
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public int searchForwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        final int lastPossiblePosition = bytes.length - 1;
        final int upToPosition = toPosition < lastPossiblePosition? toPosition : lastPossiblePosition;
        int currentPosition = fromPosition > 0? fromPosition : 0;
        final Matcher localMatcher = matcher;
        while (currentPosition <= upToPosition) {
            if (localMatcher.matches(bytes, currentPosition)) {
                return currentPosition;
            }
            currentPosition++;
        }
        return Searcher.NOT_FOUND;
    }
  
    
    
    /**
     * @inheritDoc
     */
    @Override
    public long searchBackwards(final Reader reader, final long fromPosition, 
           final long toPosition) throws IOException {
        final long lastPossiblePosition = reader.length() - 1;
        final long upToPosition = toPosition > 0? toPosition : 0;
        long currentPosition = fromPosition < lastPossiblePosition? fromPosition : lastPossiblePosition;
        final Matcher localMatcher = matcher;
        while (currentPosition >= upToPosition) {
            if (localMatcher.matches(reader, currentPosition)) {
                return currentPosition;
            }
            currentPosition--;
        }
        
        return Searcher.NOT_FOUND;
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public int searchBackwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        final int lastPossiblePosition = bytes.length - 1;
        final int upToPosition = toPosition > 0? toPosition : 0;
        int currentPosition = fromPosition < lastPossiblePosition? fromPosition : lastPossiblePosition;
        final Matcher localMatcher = matcher;
        while (currentPosition >= upToPosition) {
            if (localMatcher.matches(bytes, currentPosition)) {
                return currentPosition;
            }
            currentPosition--;
        }
        
        return Searcher.NOT_FOUND;
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
