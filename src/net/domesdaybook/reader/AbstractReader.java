/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author matt
 */
public abstract class AbstractReader implements Reader, Iterable<Window> {

    protected final static int DEFAULT_WINDOW_SIZE = 4096;
    
    protected final int windowSize;
    protected final WindowCache cache;
    private Window lastWindow = null;

    
    public AbstractReader(final WindowCache cache) {
        this(DEFAULT_WINDOW_SIZE, cache);
    }
    

    public AbstractReader(final int windowSize, final WindowCache cache) {
        this.windowSize = windowSize;
        this.cache = cache;
    }
    
    
    /**
     * Reads a byte in the file at the given position.
     *
     * @param position The position in the reader to read a byte from.
     * @return The byte at the given position.
     * @throws ReaderException if an error occurs reading the byte.
     * @throws IndexOutOfBoundsException If there are no bytes at the position given. 
     */
    @Override
    public final byte readByte(final long position) throws IOException {
        final Window window = getWindow(position);
        final int offset = (int) position % windowSize;
        if (window == null || offset >= window.getLimit()) {
            throw new IndexOutOfBoundsException("No bytes can be read from this position:" + position);
        }
        return window.getByte(offset);
    }
    
    
    /**
     * 
     * @return A Window backed by a byte array onto the data for a given position.
     *         If a window can't be provided for the given position, null is returned.
     * @throws IOException if an IO error occurred trying to create a new window.
     */
    @Override
    public final Window getWindow(final long position) throws IOException {
        if (position >= 0) {
            final long windowStart =  position - (position % windowSize);
            if (lastWindow != null && lastWindow.getWindowPosition() == windowStart) {
                return lastWindow;
            }
            Window window = cache.getWindow(windowStart);
            if (window != null) {
                lastWindow = window;
            } else {
                window = createWindow(windowStart);
                if (window != null) {
                    lastWindow = window;
                    cache.addWindow(window);
                }
            } 
            return window;
        }
        return null;
    }
    
    
    abstract Window createWindow(final long windowStart) throws IOException;
    

    @Override
    public final Iterator<Window> iterator() {
        return new WindowIterator();
    }

    
    @Override
    public void close() {
        cache.clear();        
    }
    
    
    @Override
    public void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    
    @Override
    public void clearCache() {
        cache.clear();
    }
    
    
    @Override
    public final int getWindowSize() {
        return windowSize;
    }
    
    
    private class WindowIterator implements Iterator<Window> {

        private int position = 0;
        
        
        @Override
        public boolean hasNext(){
            try {
                return getWindow(position) != null;
            } catch (IOException ex) {
                return false;
            }
        }

        
        @Override
        public Window next() {
            try {
                final Window window = getWindow(position);
                if (window != null) {
                    position += windowSize;
                    return window;
                }
            } catch (final IOException dropDownToNoSuchElementException) {
            }
            throw new NoSuchElementException();            
        }

        
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove a window from a reader.");
        }
    }
    
}
