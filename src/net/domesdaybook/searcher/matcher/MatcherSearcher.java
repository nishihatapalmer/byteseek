/*
 * Copyright Matt Palmer 2011, All rights reserved.
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

package net.domesdaybook.searcher.matcher;

import java.io.IOException;
import net.domesdaybook.matcher.Matcher;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.reader.Window;
import net.domesdaybook.searcher.AbstractSearcher;


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
 * This also means that it can't directly use Window-based searching (although
 * it can use it to indicate there is no more data, when the Window is null.)
 * Without knowing the maximum length of a matcher, it must fall back on the 
 * underlying Matcher to interact with the Reader.
 * 
 * @author Matt Palmer
 */
public final class MatcherSearcher extends AbstractSearcher {

    private final Matcher matcher;
    
    MatcherSearcher(final Matcher matcher) {
        this.matcher = matcher;
    }
    
    
    /**
     * @throws IOException 
     * @inheritDoc
     */
    @Override
    public long searchForwards(final Reader reader, final long fromPosition, 
           final long toPosition) throws IOException {
        final Matcher localMatcher = matcher;  
        long currentPosition = fromPosition > 0? 
                               fromPosition : 0;
        Window window = reader.getWindow(currentPosition);
        while (window != null) {
            final int availableSpace = window.getLimit() - reader.getWindowOffset(currentPosition);
            final long endWindowPosition = currentPosition + availableSpace;
            final long lastPosition = endWindowPosition < toPosition?
                                      endWindowPosition : toPosition;
            while (currentPosition <= lastPosition) {
                if (localMatcher.matches(reader, currentPosition)) {
                    return currentPosition;
                }
                currentPosition++;
            }
            window = reader.getWindow(currentPosition);
        }
        return NOT_FOUND;
    }
    
    
    /**
     * @inheritDoc
     */
    @Override
    public int searchForwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        final Matcher localMatcher = matcher;
        final int lastPossiblePosition = bytes.length - 1;
        final int upToPosition = toPosition < lastPossiblePosition? 
                                 toPosition : lastPossiblePosition;
        int currentPosition = fromPosition > 0?
                              fromPosition : 0;
        while (currentPosition <= upToPosition) {
            if (localMatcher.matches(bytes, currentPosition)) {
                return currentPosition;
            }
            currentPosition++;
        }
        return NOT_FOUND;
    }
  
 
    
    //REVIEW:
    
    // is this vulnerable to fromPosition being smaller than toPosition?
    // is the same true in reverse for searchForwards?
  
    
    /**
     * @throws IOException 
     * @inheritDoc
     */
    @Override
    public long searchBackwards(final Reader reader, final long fromPosition, 
           final long toPosition) throws IOException {
        final Matcher localMatcher = matcher;
        final long upToPosition = toPosition > 0? 
                                  toPosition : 0;
        long currentPosition = withinLength(reader, fromPosition);
        Window window = reader.getWindow(currentPosition);
        while (window != null) {
            final int availableSpace = reader.getWindowOffset(currentPosition);
            final long startWindowPosition = currentPosition - availableSpace;
            final long finalPosition = startWindowPosition > upToPosition?
                                       startWindowPosition : upToPosition;
            while (currentPosition >= finalPosition) {
                if (localMatcher.matches(reader, currentPosition)) {
                    return currentPosition;
                }
                currentPosition--;
            }
            window = reader.getWindow(currentPosition);
        }
        return NOT_FOUND;
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public int searchBackwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        final Matcher localMatcher = matcher;
        final int lastPossiblePosition = bytes.length - 1;
        final int upToPosition = toPosition > 0? 
                                 toPosition : 0;
        int currentPosition = fromPosition < lastPossiblePosition? 
                              fromPosition : lastPossiblePosition;
        while (currentPosition >= upToPosition) {
            if (localMatcher.matches(bytes, currentPosition)) {
                return currentPosition;
            }
            currentPosition--;
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
