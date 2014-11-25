/*
 * Copyright Matt Palmer 2014, All rights reserved.
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

import net.byteseek.io.reader.Window;

import java.util.*;

/**
 * A cache which holds on to the first X bytes and the last Y bytes.
 */
public final class TopAndTailCache extends AbstractCache {

    private final Map<Long, Window> cache;
    private final List<Window> tailCacheEntries;
    private final int firstCacheSize;
    private final int secondCacheSize;
    private long lastPositionSeen;
    private int nextTailCacheToCheck;

    public TopAndTailCache(final int cacheSize) {
        this(cacheSize, cacheSize);
    }

    public TopAndTailCache(final int firstCacheSize, final int secondCacheSize) {
        this.cache = new HashMap<Long, Window>();
        this.tailCacheEntries = new ArrayList<Window>();
        this.firstCacheSize  = firstCacheSize;
        this.secondCacheSize = secondCacheSize;
    }

    @Override
    public Window getWindow(final long position) {
        return cache.get(position);
    }

    @Override
    public void addWindow(final Window window) {
        final long windowPosition = window.getWindowPosition();
        final long windowEnd      = window.getWindowEndPosition();
        if (windowEnd > lastPositionSeen) {
            lastPositionSeen = windowEnd;
        }
        final long tailCacheStart = lastPositionSeen - secondCacheSize + 1;
        if (windowPosition < firstCacheSize) {
            cache.put(windowPosition, window);
        } else if (windowEnd >= tailCacheStart) {
            cache.put(windowPosition, window);
            tailCacheEntries.add(window);
            checkNonTailWindows(tailCacheStart);
        }
    }

    public void clear() {
        cache.clear();
        tailCacheEntries.clear();
    }

    /**
     * Every time we add a window which is further on than the last position we saw,
     * check up to two windows to see if they should be evicted from the cache.
     * The index of the window to check in the tail cache is kept the same if we
     * evict the window at that position, or advanced if we don't.  This ensures that
     * we eventually cycle round the tail cache and check earlier windows, because for each
     * window we add, we can remove up to two windows which should no longer be cached.
     *
     * @param tailCacheStart The start of the tail cache.
     */
    private void checkNonTailWindows(final long tailCacheStart) {
        for (int repeat = 0; repeat < 2; repeat++){
            int numberOfCacheEntries = tailCacheEntries.size();
            if (numberOfCacheEntries > 1) {
                final int nextToCheck = nextTailCacheToCheck;
                final Window maybeTail = tailCacheEntries.get(nextToCheck);
                if (maybeTail.getWindowEndPosition() < tailCacheStart) {
                    cache.remove(maybeTail);
                    tailCacheEntries.remove(nextToCheck);
                } else {
                    nextTailCacheToCheck = (nextToCheck + 1) % numberOfCacheEntries;
                }
            }
        }
    }

}
