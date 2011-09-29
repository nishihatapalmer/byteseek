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

package net.domesdaybook.searcher.matcher;

import java.io.IOException;
import net.domesdaybook.matcher.Matcher;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.searcher.AbstractSearcher;
import net.domesdaybook.searcher.Searcher;

/**
 *
 * @author matt
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
    //TODO: update to use Windows rather than knowing length.
    //public long searchForwards(final Reader reader, final long fromPosition, 
           final long toPosition) throws IOException {
        final long lastPossiblePosition = reader.length() - 1;
        final long upToPosition = toPosition < lastPossiblePosition? toPosition : lastPossiblePosition;
        long currentPosition = fromPosition > 0? fromPosition : 0;
        final Matcher localMatcher = matcher;
        while (currentPosition <= upToPosition) {
            if (localMatcher.matches(reader, currentPosition)) {
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
