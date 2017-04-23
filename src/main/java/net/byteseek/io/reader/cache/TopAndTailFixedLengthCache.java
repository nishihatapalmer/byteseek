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

import net.byteseek.io.reader.windows.Window;
import org.apache.mahout.math.map.AbstractLongObjectMap;
import org.apache.mahout.math.map.OpenLongObjectHashMap;

/**
 * A cache which holds on to a number of bytes at the top and tail of a reader.
 * <p>
 * This cache needs to know the total length of the reader when initialised, thus it is
 * not suitable for use with InputStreamReaders, as the length is unknown, and
 * determining the length causes the entire stream to be read.
 *
 * Created by matt on 17/09/15.
 */
public final class TopAndTailFixedLengthCache extends AbstractFreeNotificationCache {

    final long topCacheEnd;
    final long tailCacheStart;
    final AbstractLongObjectMap<Window> cache;

    public TopAndTailFixedLengthCache(final long length, final long topTailBytes) {
        this(length, topTailBytes, topTailBytes);
    }

    public TopAndTailFixedLengthCache(final long length, final long topCacheBytes, final long tailCacheBytes) {
        this.topCacheEnd    = topCacheBytes;
        this.tailCacheStart = length - tailCacheBytes - 1;
        this.cache          = new OpenLongObjectHashMap<Window>();
    }

    @Override
    public Window getWindow(final long position) {
        return cache.get(position);
    }

    @Override
    public void addWindow(final Window window) {
        final long windowPos = window.getWindowPosition();
        if (windowPos < topCacheEnd || windowPos > tailCacheStart) {
            cache.put(windowPos, window);
        }
    }

    @Override
    public void clear() {
        cache.clear();
    }
}
