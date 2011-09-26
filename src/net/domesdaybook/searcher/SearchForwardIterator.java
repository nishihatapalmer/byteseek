/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
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
public class SearchForwardIterator implements Iterator {
    
    // immutable fields:
    private final byte[] bytes;
    private final Reader reader;
    private final long fromPosition;
    private final long toPosition;
    private final Searcher searcher;
    
    // private state:
    private long searchPosition;
    private boolean searchedForNext;
    private long matchPosition = Searcher.NOT_FOUND;
    
    
    public SearchForwardIterator(final Searcher searcher, final Reader reader) throws IOException {
        this(searcher, 0, Long.MAX_VALUE, reader);
    }
    
    
    public SearchForwardIterator(final Searcher searcher, final long fromPosition, 
                                 final long toPosition, final Reader reader) {
        if (searcher == null || reader == null) {
            throw new IllegalArgumentException("Null searcher or byte reader.");
        }        
        this.searcher = searcher;
        this.reader = reader;
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        this.bytes = null;
        this.searchPosition = fromPosition;
    }        
   
    
    
    public SearchForwardIterator(final Searcher searcher, final byte[] bytes) {
        this(searcher, 0, bytes.length - 1, bytes);
    }
    
    
    public SearchForwardIterator(final Searcher searcher, final long fromPosition, 
                                 final long toPosition, final byte[] bytes) {
        if (searcher == null || bytes == null) {
            throw new IllegalArgumentException("Null searcher or byte array.");
        }
        this.searcher = searcher;
        this.bytes = bytes;
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        this.reader = null;
        this.searchPosition = fromPosition;  
    }        
    
    
    @Override
    public boolean hasNext() {
        if (!searchedForNext) {
            try {
                matchPosition = getNextMatchPosition();
                searchedForNext = true;
            } catch (IOException ex) {
                return false;
            }
        }
        return matchPosition >= 0;
    }

    
    @Override
    public Long next() {
        if (hasNext()) {
            searchPosition = matchPosition + 1;
            searchedForNext = false;
            return matchPosition;
        }
        throw new NoSuchElementException();
    }

    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Cannot remove search results.");
    }
    
    
    private long getNextMatchPosition() throws IOException {
        long nextMatchingPosition = Searcher.NOT_FOUND;
        if (reader != null) {
            nextMatchingPosition = searcher.searchForwards(reader, searchPosition, toPosition);
        } else if (bytes != null) {
            nextMatchingPosition = searcher.searchForwards(bytes, (int) searchPosition, (int) toPosition);
        }
        return nextMatchingPosition;
    }
    
}
