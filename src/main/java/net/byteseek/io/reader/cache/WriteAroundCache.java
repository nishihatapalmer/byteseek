/*
 * Copyright Matt Palmer 2018, All rights reserved.
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

import net.byteseek.io.reader.windows.Window;
import net.byteseek.utils.ArgUtils;

import java.io.IOException;

/**
 * A specialised cache which stores data in two caches.  The memory cache is checked
 * first.  If the data is not in the memory cache, the persistent cache is
 * checked.  If the data is in the persistent cache, it is re-added to the
 * memory cache.  When a window is first added to this cache, it is only added to the
 * persistent cache, writing "around" the primary memory cache.
 * <p>
 * The use case for this cache is to allow holding Windows in a fast
 * in-memory primary soft cache, which can evict data under low memory conditions.
 * The secondary cache should be a persistent cache which can always retrieve the
 * data.  If data has been evicted from the memory cache, it can always be safely
 * retrieved from the slower permanent cache.  This window is then re-added
 * to the fast memory cache when accessed.
 * <p>
 * For example, when using an InputStreamReader where we want to be able to
 * always retrieve old data, but also want to support faster access to multiple
 * Windows (memory permitting), this cache allows both requirements to be satisfied.
 * </p>
 * This cache technically supports free notification, but will never notify
 * of a window being evicted, since the persistent cache should never actually
 * release a window.  Care must be taken to use appropriate caches with this
 * cache.
 *
 * @author Matt Palmer
 */
public final class WriteAroundCache extends AbstractFreeNotificationCache {

    private final WindowCache memoryCache;
    private final WindowCache persistentCache;

    /**
     * Constructs a WriteAroundCache given a memory cache and a persistent cache.
     *
     * @param memoryCache     A fast cache which may evict data.
     * @param persistentCache A persistent cache which can always retrieve the data.
     * @throws IllegalArgumentException if the memoryCache or persistentCache is null.
     */
    public WriteAroundCache(final WindowCache memoryCache, final WindowCache persistentCache) {
        ArgUtils.checkNullObject(memoryCache);
        ArgUtils.checkNullObject(persistentCache);
        this.memoryCache     = memoryCache;
        this.persistentCache = persistentCache;
    }

    @Override
    public Window getWindow(final long position) throws IOException {
        Window window = memoryCache.getWindow(position);
        if (window == null) {
            window = persistentCache.getWindow(position);
            if (window != null) {
                memoryCache.addWindow(window);
            }
        }
        return window;
    }

    @Override
    public void addWindow(final Window window) throws IOException {
        // Write around the memory cache.
        // A write around cache only adds the window to the persistent cache when it is added.
        // Windows are only added to the memory cache if they are subsequently fetched from persistent storage.
        // This avoids holding on to windows in memory as they are added initially (e.g. when a stream is read to the end)
        // since most of those windows will not be re-read at the time they are added.
        persistentCache.addWindow(window);
    }

    @Override
    public int read(long windowPos, int offset, byte[] readInto, int readIntoPos) throws IOException {
        int bytesRead = memoryCache.read(windowPos, offset, readInto, readIntoPos);
        if (bytesRead == 0) {
            bytesRead = persistentCache.read(windowPos, offset, readInto, readIntoPos);
        }
        return bytesRead;
    }

    /**
     * Clears both the memory and persistent caches, using whatever
     * mechanisms they use to clear themselves.
     */
    @Override
    public void clear() throws IOException {
        IOException memCacheException = null;
        try {
            memoryCache.clear();
        } catch (IOException ex) {
            memCacheException = ex;
        }
        persistentCache.clear();
        if (memCacheException != null) {
            throw memCacheException;
        }
    }

    /**
     * Returns the memory cache used by this DoubleCache.
     *
     * @return WindowCache The memory cache used by this DoubleCache.
     */
    public WindowCache getMemoryCache() {
        return memoryCache;
    }

    /**
     * Returns the persistent cache used by this DoubleCache.
     *
     * @return WindowCache the persistent cache used by this DoubleCache.
     */
    public WindowCache getPersistentCache() {
        return persistentCache;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "(memory cache: " + memoryCache +
                ", persistent cache: " + persistentCache + ')';
    }

}
