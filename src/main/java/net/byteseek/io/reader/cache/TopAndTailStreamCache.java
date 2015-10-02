/*
 * Copyright Matt Palmer 2015, All rights reserved.
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
import net.byteseek.io.reader.Window;

import java.util.*;

/**
 * A cache which holds on to the first X and last Y bytes of a stream as it is read.
 * <p>
 * There is an assumption that windows can only be added in sequential order (as
 * they are being read from a stream). If windows are added in random access order,
 * then this cache will fail to operate correctly.
 *
 * Created by matt on 17/09/15.
 */
public final class TopAndTailStreamCache extends AbstractFreeNotificationCache {


    final long topCacheBytes;
    final long tailCacheBytes;
    final Map<Long, Window> cache;
    final List<Window> tailEntries;
    long lastSeenPosition;

    public TopAndTailStreamCache(final long topTailBytes) {
        this(topTailBytes, topTailBytes);
    }

    public TopAndTailStreamCache(final long topBytes, final long tailBytes) {
        this.topCacheBytes  = topBytes;
        this.tailCacheBytes = tailBytes;
        cache               = new HashMap<Long, Window>();
        tailEntries         = new ArrayList<Window>();

    }

    @Override
    public Window getWindow(final long position) {
        return cache.get(position);
    }

    @Override
    public void addWindow(final Window window) {
        final long windowPos = window.getWindowPosition();
        cache.put(windowPos, window);
        // past top bytes, into tail bytes.
        if (windowPos >= topCacheBytes) {
            // Check for tail cached windows which shouldn't be cached any more.
            final long tailCacheStart = window.getNextWindowPosition() - tailCacheBytes;
            final Iterator<Window> it = tailEntries.iterator();
            while (it.hasNext()) {
                final Window cachedWin = it.next();
                if (cachedWin.getNextWindowPosition() <= tailCacheStart) {
                   it.remove();
                   cache.remove(cachedWin.getWindowPosition());
                   notifyWindowFree(cachedWin, this);
                } else {
                    break;
                }
            }
            // add this window to our tail entries.
            tailEntries.add(window);
        }
    }

    @Override
    public void clear() {
        cache.clear();
        tailEntries.clear();
    }
}
