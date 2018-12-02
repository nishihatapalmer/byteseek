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
import net.byteseek.utils.collections.PositionHashMap;

import java.io.IOException;

/**
 * A cache which holds on to the first X and last Y bytes of a stream as it is read.
 * It is not necessary to know how long the stream is up-front.
 * <p>
 * There is an assumption that windows are placed in a fixed interval,
 * i.e. the windows are always a fixed size (and hence exist at fixed intervals from each other).
 * The last window in a stream may of course be shorter than the others,
 * but this will not affect the cache strategy.
 * <p>
 * There is an assumption that windows can only be added in sequential order (as
 * they are being read from a stream). If windows are added in random access order,
 * then this cache will fail to operate correctly.
 *
 * Created by matt on 17/09/15.
 */
public final class TopAndTailStreamCache extends AbstractMemoryCache {

    final long topCacheBytes;
    final long tailCacheBytes;
    final int posOffsetToRemove;
    final PositionHashMap<Window> cache;

    /**
     * Constructs a Top and Tail Stream Cache given a single size (for both top and tail caches),
     * and the interval at which windows are added.
     *
     * @param topTailBytes   The cache size for both the top and tail caches.
     * @param windowInterval The position interval at which new windows are added.
     */
    public TopAndTailStreamCache(final long topTailBytes, final int windowInterval) {
        this(topTailBytes, topTailBytes, windowInterval);
    }

    /**
     * Constructs a Top and Tail Stream Cache given a single size (for both top and tail caches),
     * and the interval at which windows are added.
     *
     * @param topBytes       The size of the top cache.
     * @param tailBytes      The size of the tail cache.
     * @param windowInterval The position interval at which new windows are added.
     */
    public TopAndTailStreamCache(final long topBytes, final long tailBytes, final int windowInterval) {
        this.topCacheBytes  = topBytes;
        this.tailCacheBytes = tailBytes;
        posOffsetToRemove   = (int) Math.ceil(tailBytes / windowInterval) * windowInterval;
        cache               = new PositionHashMap<Window>();
    }

    @Override
    public Window getWindow(final long position) {
        return cache.get(position);
    }

    @Override
    public void addWindow(final Window window) throws IOException {
        final long windowPos = window.getWindowPosition();
        final long localTopCacheSize = topCacheBytes; // grab a local reference to the member to avoid getting it repeatedly.

        // If we're still in the top cache, just add it:
        if (windowPos < localTopCacheSize) {
            cache.put(windowPos, window);
        } else {

            // Is there any tail cache to worry about?
            if (tailCacheBytes > 0) {

                // The last window added will always be in the tail - windows are added sequentially in a stream.
                final PositionHashMap localCache = cache; // grab a local reference to the member to avoid getting it repeatedly.
                localCache.put(windowPos, window);

                // If there's a previous window to remove as we stream along past the top cache, get rid of it:
                final long windowPosToRemove = windowPos - posOffsetToRemove;
                if (windowPosToRemove >= localTopCacheSize) {
                    localCache.remove(windowPosToRemove);
                }
            }
        }
    }

    @Override
    public void clear() {
        cache.clear();
    }
}
