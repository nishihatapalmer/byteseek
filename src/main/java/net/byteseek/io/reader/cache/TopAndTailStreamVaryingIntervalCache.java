/*
 * Copyright Matt Palmer 2015-18, All rights reserved.
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
import java.util.*;

/**
 * A cache which holds on to the first X and last Y bytes of a stream as it is read.
 * <p>
 * No assumption is made about the size of each individual window as it is added, or the
 * position intervals that exist between them.  Windows can exist at any position, as long
 * as the positions and lengths join up as the windows are added sequentially.
 * In practice, most readers allocate windows with fixed intervals, as this means they don't
 * have to maintain a map of the positions.  This cache is suitable for readers which don't
 * provide the guarantee of fixed interval windows.  If the stream reader does provide known fixed
 * window intervals, the TopAndTailStreamCache would be a better choice.
 * <p>
 * There is an assumption that windows can only be added in sequential order (as
 * they are being read from a stream). If windows are added in random access order,
 * then this cache will fail to operate correctly.
 *
 * Created by matt on 17/09/15.
 */
public final class TopAndTailStreamVaryingIntervalCache extends AbstractMemoryCache {

    final long topCacheBytes;
    final long tailCacheBytes;
    final PositionHashMap<Window> cache;
    final List<Window> tailEntries;

    public TopAndTailStreamVaryingIntervalCache(final long topTailBytes) {
        this(topTailBytes, topTailBytes);
    }

    public TopAndTailStreamVaryingIntervalCache(final long topBytes, final long tailBytes) {
        this.topCacheBytes  = topBytes;
        this.tailCacheBytes = tailBytes;
        cache               = new PositionHashMap<Window>();
        tailEntries         = new ArrayList<Window>();
    }

    @Override
    public Window getWindow(final long position) {
        return cache.get(position);
    }

    @Override
    public void addWindow(final Window window) throws IOException {
        final PositionHashMap<Window> localCache = cache;
        final long windowPos = window.getWindowPosition();

        // If we're still in the top cache, just add it:
        if (windowPos < topCacheBytes) {
            localCache.put(windowPos, window);
        } else {

            // Is there any tail cache to worry about?
            final long tailLength = tailCacheBytes;
            if (tailLength > 0) {

                // The last window added will always be in the tail - windows are added sequentially in a stream.
                localCache.put(windowPos, window);

                // Check for tail cached windows which shouldn't be cached any more.
                final List<Window> localTailEntries = tailEntries;
                final long tailCacheStart = window.getNextWindowPosition() - tailLength;
                final int numTailEntries = localTailEntries.size();
                int removeEntry = 0;
                for (int i = 0; i < numTailEntries; i++) {
                    final Window tailEntry = localTailEntries.get(i);
                    if (tailEntry.getNextWindowPosition() <= tailCacheStart) {
                        removeEntry = i + 1;
                    } else {
                        break;
                    }
                }
                // If we found any to remove, remove them.
                if (removeEntry > 0) {
                    for (int i = 0; i < removeEntry; i++) {
                        final Window toRemove = localTailEntries.get(i);
                        localCache.remove(toRemove.getWindowPosition());
                        notifyWindowFree(toRemove, this);
                    }
                    localTailEntries.subList(0, removeEntry).clear();
                }
                // add this window to our tail entries.
                localTailEntries.add(window);
            }
        }
    }

    @Override
    public void clear() {
        cache.clear();
        tailEntries.clear();
    }
}
