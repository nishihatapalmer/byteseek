/*
 * Copyright Matt Palmer 2011-2018, All rights reserved.
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

import net.byteseek.utils.ArgUtils;
import net.byteseek.utils.collections.LongLinkedHashMap;
import net.byteseek.io.reader.windows.Window;

import java.io.IOException;

/**
 * A {@link WindowCache} which holds on to the {@link net.byteseek.io.reader.windows.Window}
 * objects which were most recently added. The least recently added objects will be
 * removed once capacity is reached. The number of Windows which will be cached
 * is configurable by its capacity.
 * <p><b>Warning</b>
 * This cache caches on the basis of the number of windows cached, not how much memory it holds.
 * Clearly, if small Windows are used less memory will be cached than if large Windows are used.
 * Other caches are configured on the basis of the amount of memory held at any time.
 * 
 * @author Matt Palmer
 */
public final class LeastRecentlyAddedCache extends AbstractMemoryCache {

    private final Cache cache;
    
    /**
     * Creates a LeastRecentlyAddedCache using the provided capacity.
     * 
     * @param capacity The number of Window objects to cache.
     * @throws IllegalArgumentException if the capacity is less than 1.
     */
    public LeastRecentlyAddedCache(final int capacity) {
        ArgUtils.checkPositiveInteger(capacity, "capacity");
        cache = new Cache(capacity);
    }

    @Override
    public Window getWindow(final long position) throws IOException {
        return cache.get(position);
    }

    @Override
    public void addWindow(final Window window) throws IOException {
        final long windowPosition = window.getWindowPosition();
        if (!cache.containsKey(windowPosition)) {
            cache.put(windowPosition, window);
            // Horrible Hack: check whether we got an IOException during the put() operation,
            // which might have triggered removeEldestEntry(), which in turn can get an IOException
            // during its call to notifyWindowFree().  If we got one, retrhow it:
            cache.checkIOException();
        }
    }

    @Override
    public void clear() {
        cache.clear();
    }

    /**
     * A simple least recently added cache, which extends {@link net.byteseek.utils.collections.LongLinkedHashMap}
     * to provide caching services, and also provides notification to any
     * {@link WindowObserver}s who are subscribed when a {@link net.byteseek.io.reader.windows.Window} leaves it.
     * <p><b>Warning - horrible hack:</b>
     * There is a horrible hack to catch and rethrow any IOExceptions which may occur during removeEldestEntry,
     * which notifies observers when a Window is leaving this Cache.
     * Any code which calls put() on this Cache should call checkIOException() immediately afterwards to
     * rethrow any IOexception which occurred.  This is because the collection interface doesn't support
     * IOExceptions, but we can get them during IO caching operations, such as when a Window leaves this
     * cache and is added to another.  Throwing a RuntimeException is also not acceptable, as this would
     * prevent the Cache from removing its eldest entries.
     * This code is private to the LeastRecentlyUsedCache, so it's only necessary for byteseek maintainers
     * to be aware of this.
     */
    private class Cache extends LongLinkedHashMap<Window> {

        private final int capacity;
        private IOException exception = null; // hack to allow throwing an IOException later from removeEldestEntry.

        private Cache(final int capacity) {
            super(capacity);
            this.capacity = capacity;
        }
        
        @Override
        protected boolean removeEldestEntry(final MapEntry<Window> eldest) {
            final boolean remove = size() > capacity;
            if (remove) {
                try {
                    notifyWindowFree(eldest.getValue(), LeastRecentlyAddedCache.this);
                } catch (IOException ex) {
                    // Horrible Hack:
                    // If we get an IOException during notifyWindowFree above, we can't throw it here in
                    // removeEldestEntry, as that collection interface doesn't support IOExceptions.
                    // Hold on to the exception.  Code that calls *put()* on this Cache (which can
                    // trigger removeEldestEntry), must call checkIOException() afterwards which will
                    // rethrow it if one was triggered.  In any case, if we threw a RuntimeException instead
                    // here, it would prevent the entry from being removed from this cache, in the
                    // private checkEldestEntry() method of LongLinkedHashMap, as the code to
                    // remove the eldest item from the cache is necessarily
                    // *after* the call to this method - and only executes if we return *true* from here.
                    // It is much more important that the Cache manages its entries correctly, and that
                    // IOExceptions are not swallowed, than the exception is thrown from the exact
                    // place and time it occurred, or that the code is elegant.
                    // Less ugly solutions would be welcome however...
                    exception = ex;
                }
            }
            return remove;
        }

        /**
         * Horrible Hack:
         *
         * Checks whether an IOException was thrown (and caught) during removeEldestEntry.
         * @throws IOException If one was thrown previously during removeEldestEntry.
         */
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
