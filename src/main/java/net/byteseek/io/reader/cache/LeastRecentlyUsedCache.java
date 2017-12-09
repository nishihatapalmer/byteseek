/*
 * Copyright Matt Palmer 2011-2017, All rights reserved.
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

package net.byteseek.io.reader.cache;

import net.byteseek.utils.collections.LongLinkedHashMap;
import net.byteseek.io.reader.windows.Window;

import java.io.IOException;

/**
 * A {@link WindowCache} which holds on to the {@link net.byteseek.io.reader.windows.Window}
 * objects which were most recently used. The number of Windows which will be cached
 * is configurable by its capacity.
 * 
 * @author Matt Palmer
 */
public final class LeastRecentlyUsedCache extends AbstractMemoryCache {

    private final Cache cache;

    /**
     * Creates a LeastRecentlyUsedCache using the provided capacity.
     * 
     * @param capacity The number of Window objects to cache.
     */
    public LeastRecentlyUsedCache(final int capacity) {
        cache = new Cache(capacity);
    }

    @Override
    public Window getWindow(final long position) {
        return cache.get(position);
    }

    @Override
    public void addWindow(final Window window) throws IOException {
        final long windowPosition = window.getWindowPosition();
        if (!cache.containsKey(windowPosition)) {
            cache.put(windowPosition, window);
            cache.checkIOException();
        }
    }

    @Override
    public void clear() {
        cache.clear();
    }

    /**
     * A simple least recently used cache, which extends {@link java.util.LinkedHashMap}
     * to provide caching services, and also provides notification to any
     * {@link WindowObserver}s who are subscribed when a {@link net.byteseek.io.reader.windows.Window} leaves it.
     */    
    private class Cache extends LongLinkedHashMap<Window> {

        private final int capacity;
        private IOException exception = null;
        
        private Cache(int capacity) {
            super(capacity, true);
            this.capacity = capacity;
        }
        
        @Override
        protected boolean removeEldestEntry(final MapEntry<Window> eldest) {
            final boolean remove = size() > capacity;
            if (remove) {
                try {
                    notifyWindowFree(eldest.getValue(), LeastRecentlyUsedCache.this);
                } catch (IOException ex) {
                    exception = ex;
                }
            }
            return remove;
        }

        public void checkIOException() throws IOException {
            if (exception != null) {
                final IOException ex = exception;
                exception = null;
                throw ex;
            }
        }
    }    

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(size: " + cache.size() + " capacity: " + cache.capacity + ')';
	}
    
}
