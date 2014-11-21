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
    private long lengthSoFar;

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
        final long windowEnd      = windowPosition + window.length();
        if (windowEnd > lengthSoFar) {
            lengthSoFar = windowEnd;
            removeNonTailWindows();
        }
        if (windowPosition < firstCacheSize) {
            cache.put(windowPosition, window);
        } else if (windowEnd > lengthSoFar - secondCacheSize) {
            cache.put(windowPosition, window);
            tailCacheEntries.add(window);
        }
    }

    public void clear() {
        cache.clear();
        tailCacheEntries.clear();
    }


    private void removeNonTailWindows() {
        final long secondCacheStart = lengthSoFar - secondCacheSize;
        final Iterator<Window> tailEntryIterator = tailCacheEntries.iterator();
        while (tailEntryIterator.hasNext()) {
            final Window tailEntry = tailEntryIterator.next();
            final long windowEnd = tailEntry.getWindowPosition() + tailEntry.length();
            if (windowEnd < secondCacheStart) {
                tailEntryIterator.remove();
            }
        }
    }

}
