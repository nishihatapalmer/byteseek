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

package net.domesdaybook.searcher;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import net.domesdaybook.reader.Reader;

/**
 *
 * @author matt
 */
public class BackwardSearchIterator<T> implements Iterator<SearchResult<T>> {
    
    // immutable fields:
    private final byte[] bytes;
    private final Reader reader;
    private final long toPosition;
    private final Searcher<T> searcher;
    
    // private state:
    private long searchPosition;
    private boolean searchedForNext = false;
    private SearchResult<T> matchResult = SearchResult.noMatch();
   
    
    /**
     * 
     * @param searcher
     * @param reader
     * @throws IOException
     */
    public BackwardSearchIterator(final Searcher searcher, final Reader reader) throws IOException {
        this(searcher, reader.length() - 1, 0, reader);
    }
    
    
    /**
     * 
     * @param searcher
     * @param fromPosition
     * @param toPosition
     * @param reader
     */
    public BackwardSearchIterator(final Searcher<T> searcher, final long fromPosition, 
                                 final long toPosition, final Reader reader) {
        if (searcher == null || reader == null) {
            throw new IllegalArgumentException("Null searcher or byte reader.");
        }        
        this.searcher = searcher;
        this.reader = reader;
        this.toPosition = toPosition;
        this.bytes = null;
        this.searchPosition = fromPosition;
    }        
   
    
    
    /**
     * 
     * @param searcher
     * @param bytes
     */
    public BackwardSearchIterator(final Searcher searcher, final byte[] bytes) {
        this(searcher, bytes.length - 1, 0, bytes);
    }
    
    
    /**
     * 
     * @param searcher
     * @param fromPosition
     * @param toPosition
     * @param bytes
     */
    public BackwardSearchIterator(final Searcher searcher, final long fromPosition, 
                                 final long toPosition, final byte[] bytes) {
        if (searcher == null || bytes == null) {
            throw new IllegalArgumentException("Null searcher or byte array.");
        }
        this.searcher = searcher;
        this.bytes = bytes;
        this.toPosition = toPosition;
        this.reader = null;
        this.searchPosition = fromPosition;  
    }        
    
    
    @Override
    public boolean hasNext() {
        if (!searchedForNext) {
            try {
                matchResult = getNextMatchPosition();
                searchedForNext = true;
            } catch (IOException ex) {
                return false;
            }
        }
        return matchResult.matched();
    }

    
    @Override
    public SearchResult<T> next() {
        if (hasNext()) {
            searchPosition = matchResult.getMatchPosition() - 1;
            searchedForNext = false;
            return matchResult;
        }
        throw new NoSuchElementException();
    }

    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Cannot remove search results.");
    }
    
    
    private SearchResult<T> getNextMatchPosition() throws IOException {
        SearchResult<T> nextMatchingPosition = SearchResult.noMatch();
        if (reader != null) {
            nextMatchingPosition = searcher.searchBackwards(reader, searchPosition, toPosition);
        } else if (bytes != null) {
            nextMatchingPosition = searcher.searchBackwards(bytes, (int) searchPosition, (int) toPosition);
        }
        return nextMatchingPosition;
    }
    
}
