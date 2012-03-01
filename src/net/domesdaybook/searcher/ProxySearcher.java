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
package net.domesdaybook.searcher;

import java.io.IOException;
import java.util.List;
import net.domesdaybook.reader.Reader;

/**
 *
 * @author Matt Palmer
 */
public class ProxySearcher<T> extends AbstractSearcher<T> {
    
    private final Searcher<T> realSearcher;
    
    
    public ProxySearcher(final Searcher<T> realSearcher) {
        this.realSearcher = realSearcher;
    }
    
    
    /**
     * @inheritDoc
     */
    @Override
    public void prepareForwards() {
        realSearcher.prepareForwards();
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public void prepareBackwards() {
        realSearcher.prepareBackwards();
    }

    
    @Override
    public List<SearchResult<T>> searchForwards(final Reader reader, 
                                                final long searchPosition, 
                                                final long lastSearchPosition) 
            throws IOException {
        return realSearcher.searchForwards(reader, searchPosition, lastSearchPosition);
    }
    

    @Override
    public List<SearchResult<T>> searchBackwards(final Reader reader, 
                                                 final long searchPosition, 
                                                 final long lastSearchPosition) 
            throws IOException {
        return realSearcher.searchBackwards(reader, searchPosition, lastSearchPosition);
    }
    

    @Override
    public List<SearchResult<T>> searchForwards(final byte[] bytes, 
                                                final int fromPosition, 
                                                final int toPosition) {
        return realSearcher.searchForwards(bytes, fromPosition, toPosition);
    }
    

    @Override
    public List<SearchResult<T>> searchBackwards(final byte[] bytes, 
                                                 final int fromPosition, 
                                                 final int toPosition) {
        return realSearcher.searchBackwards(bytes, fromPosition, toPosition);
    }
    
        
    @Override
    public String toString() {
        return getClass().getSimpleName() + '(' + realSearcher.toString() + ')';
    }
        
    
}
