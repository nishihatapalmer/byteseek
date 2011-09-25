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
public class SearchIterator implements Iterator {
    
    public static enum Direction {
        FORWARDS,
        BACKWARDS
    }
   
    private final byte[] bytes;
    private final Reader reader;
    private final long fromPosition;
    private final long toPosition;
    private final Searcher searcher;
    private final Direction direction;
    
    private long searchPosition;
    private boolean searchedForNext = false;
    private long matchPosition = Searcher.NOT_FOUND;
    
    public SearchIterator(final Searcher searcher, final Reader reader) throws IOException {
        this(searcher, Direction.FORWARDS, reader);
    }
    
    
    public SearchIterator(final Searcher searcher, final Direction direction,
            final Reader reader) throws IOException {
        if (searcher == null || reader == null) {
            throw new IllegalArgumentException("Null searcher or byte reader.");
        }            
        this.searcher = searcher;
        this.reader = reader;
        this.direction = direction;
        if (direction == Direction.FORWARDS) {
            this.fromPosition = 0;
            this.toPosition = Long.MAX_VALUE;
        } else {
            this.fromPosition = reader.length() - 1;
            this.toPosition = 0;
        }
        this.bytes = null;
        this.searchPosition = fromPosition;
    }
    
    
    public SearchIterator(final Searcher searcher, final Direction direction,
                          final long fromPosition, final long toPosition,
                          final Reader reader) {
        if (searcher == null || reader == null) {
            throw new IllegalArgumentException("Null searcher or byte reader.");
        }        
        this.searcher = searcher;
        this.reader = reader;
        this.direction = direction;
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        this.bytes = null;
        this.searchPosition = fromPosition;
    }        
   
    
    public SearchIterator(final Searcher searcher, final byte[] bytes) {
        this(searcher, Direction.FORWARDS, bytes);
    }
    
    
    public SearchIterator(final Searcher searcher, final Direction direction,
            final byte[] bytes) {
        if (searcher == null || bytes == null) {
            throw new IllegalArgumentException("Null searcher or byte array.");
        }        
        this.searcher = searcher;
        this.bytes = bytes;
        this.direction = direction;
        if (direction == Direction.FORWARDS) {
            this.fromPosition = 0;
            this.toPosition = bytes.length - 1;
        } else {
            this.fromPosition = bytes.length - 1;
            this.toPosition = 0;
        }
        this.reader = null;
        this.searchPosition = fromPosition;
    }
    
    
    public SearchIterator(final Searcher searcher, final Direction direction,
                          final long fromPosition, final long toPosition,
                          final byte[] bytes) {
        if (searcher == null || bytes == null) {
            throw new IllegalArgumentException("Null searcher or byte array.");
        }
        this.searcher = searcher;
        this.bytes = bytes;
        this.direction = direction;
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
            if (direction == Direction.FORWARDS) {
                searchPosition = matchPosition + 1;
            } else {
                searchPosition = matchPosition - 1;
            }
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
        if (direction == Direction.FORWARDS) {
            if (reader != null) {
                nextMatchingPosition = searcher.searchForwards(reader, searchPosition, toPosition);
            } else if (bytes != null) {
                nextMatchingPosition = searcher.searchForwards(bytes, (int) searchPosition, (int) toPosition);
            }
        } else {
            if (reader != null) {
                nextMatchingPosition = searcher.searchBackwards(reader, searchPosition, toPosition);
            } else if (bytes != null) {
                nextMatchingPosition = searcher.searchBackwards(bytes, (int) searchPosition, (int) toPosition);
            }
        }
        return nextMatchingPosition;
    }
    
}
