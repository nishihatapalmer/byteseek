/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

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
     * @throws ReaderException if an IOException occurs reading the file.
     */
    @Override
    public byte readByte(final long position) throws ReaderException {
        final Window window = getWindow(position);
        if (window == null) {
            throw new ReaderException("No bytes can be read from this position:" + position);
        }
        return window.getByte((int) (position % windowSize));
    }
    
    
    /**
     * 
     * @return A Window containing a byte array and the offset into it for a given position.
     *         If a window can't be provided for the given position, null is returned.
     */
    @Override
    public Window getWindow(final long position) throws ReaderException {
        final long readPos =  position - (position % windowSize);
        if (lastWindow != null && lastWindow.getWindowPosition() == readPos) {
            return lastWindow;
        }
        if (position >= 0) {
            Window window = cache.getWindow(readPos);
            if (window == null) {
                window = createWindow(readPos);
                cache.addWindow(window);
                lastWindow = window;
            }
            return window;
        }
        return null;
    }
    
    
    abstract Window createWindow(final long readPos) throws ReaderException;
    

    @Override
    public Iterator<Window> iterator() {
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
    public int getWindowSize() {
        return windowSize;
    }
    
    
    private class WindowIterator implements Iterator<Window> {

        private int position = 0;
        
        @Override
        public boolean hasNext() {
            return position < length();
        }

        @Override
        public Window next() {
            final Window window = getWindow(position);
            if (window == null) {
                throw new NoSuchElementException();
            }
            position += windowSize;
            return window;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove a window from a reader.");
        }
    }
    
}
