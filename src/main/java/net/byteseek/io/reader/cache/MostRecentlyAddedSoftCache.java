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

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map;

import net.byteseek.io.reader.windows.Window;


/**
 * A {@link WindowCache} which holds on to the {@link net.byteseek.io.reader.windows.Window}
 * objects which were most recently added, as long as there is sufficient memory.
 * The number of Windows which will be cached is configurable by its capacity.
 * <p>
 * If memory is short, then unused cached windows will be freed automatically by the garbage
 * collector.  Because this can happen without warning, this cache does not
 * support window free notification, and an UnsupportedOperationException will be
 * thrown if an attempt is made to subscribe or unsubscribe for these events.
 * <p>
 * Do not use this cache if there is no other way of retrieving the window again
 * if it may be required.  This type of cache may suit a FileReader, since the file
 * can always be read again to obtain a window.  It will not suit an InputStreamReader,
 * as it is impossible to obtain an old window from a stream again once read.
 *
 * @author Matt Palmer
 */
public final class MostRecentlyAddedSoftCache extends AbstractNoFreeNotificationCache {

    private final static boolean ACCESS_ORDER = false;

    private final Map<Long, SoftReference<Window>> cache;

    /**
     * Creates a MostRecentlyAddedSoftCache using the provided capacity.
     *
     * @param capacity The number of Window objects to cache.
     */
    public MostRecentlyAddedSoftCache(final int capacity) {
        cache = new LinkedHashMap<Long, SoftReference<Window>>(capacity + 1, 1.1f, ACCESS_ORDER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Window getWindow(final long position) {
        final SoftReference<Window> windowRef = cache.get(position);
        if (windowRef != null) {
            final Window window = windowRef.get();
            if (window != null) {
                return window;
            }
            cache.remove(position);
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void addWindow(final Window window) {
        final long windowPosition = window.getWindowPosition();
        final SoftReference<Window> windowRef = cache.get(windowPosition);
        if (windowRef == null || windowRef.get() == null) {
            cache.put(windowPosition, new SoftReference<Window>(window));
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        cache.clear();
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + "[size: " + cache.size() + ']';
    }


}
