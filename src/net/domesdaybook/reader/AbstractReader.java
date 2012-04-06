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
 */

package net.domesdaybook.reader;

import net.domesdaybook.reader.cache.WindowCache;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import net.domesdaybook.reader.cache.WindowCache.WindowObserver;


/**
 *
 * @author Matt Palmer
 */
public abstract class AbstractReader implements Reader, Iterable<Window> {

    protected final static int NO_BYTE_AT_POSITION = -1;
    protected final static int DEFAULT_WINDOW_SIZE = 4096;
    protected final static int DEFAULT_CAPACITY = 32;
    
    /**
     * 
     */
    protected final int windowSize;
    protected final WindowCache cache;
    private Window lastWindow;
    
    
    /**
     * 
     * @param cache
     */
    public AbstractReader(final WindowCache cache) {
        this(DEFAULT_WINDOW_SIZE, cache);
    }
    
    
    /**
     * 
     * @param windowSize
     * @param cache
     */
    public AbstractReader(final int windowSize, final WindowCache cache) {
        if (windowSize < 1) {
            throw new IllegalArgumentException("Window size must be at least one.");
        }
        if (cache == null) {
            throw new IllegalArgumentException("Window cache cannot be null.");
        }
        this.windowSize = windowSize;
        this.cache = cache;
    }
    
    
    /**
     * Reads a byte in the file at the given position.
     *
     * @param position The position in the reader to read a byte from.
     * @return The byte at the given position (0-255), or -1 if there is no
     *         byte at the position specified.
     * @throws IOException if an error occurs reading the byte.
     */
    @Override
    public int readByte(final long position) throws IOException {
        final Window window = getWindow(position);
        final int offset = (int) position % windowSize;
        if (window == null || offset >= window.length()) {
            return NO_BYTE_AT_POSITION;
        }
        return window.getByte(offset) & 0xFF;
    }
    
    
    /**
     * 
     * @return A Window backed by a byte array onto the data for a given position.
     *         If a window can't be provided for the given position, null is returned.
     * @throws IOException if an IO error occurred trying to create a new window.
     */
    @Override
    public Window getWindow(final long position) throws IOException {
        if (position >= 0) {
            Window window = null;
            final int offset = (int) position % windowSize;
            final long windowStart =  position - offset;
            if (lastWindow != null && lastWindow.getWindowPosition() == windowStart) {
                window = lastWindow;
            } else {
                window = cache.getWindow(windowStart);
                if (window != null) {
                    lastWindow = window;
                } else {
                    window = createWindow(windowStart);
                    if (window != null) {
                        lastWindow = window;
                        cache.addWindow(window);
                    }
                } 
            }
            // Finally, if the position requested is outside the window limit,
            // don't return a window. The position itself is invalid, even though
            // that position is part of a window which has valid positions.
            if (window != null && offset >= window.length()) {
                window = null;
            }
            return window;
        }
        return null;
    }
    
    
    @Override
    public Iterator<Window> iterator() {
        return new WindowIterator();
    }

    
    @Override
    public void close() throws IOException {
        cache.clear();    
    }
    
    
    @Override
    public int getWindowOffset(final long position) {
        return (int) position % windowSize;
    }
    
    
    abstract Window createWindow(final long windowStart) throws IOException;
    
    
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
