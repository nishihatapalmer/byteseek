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

import net.byteseek.io.reader.windows.Window;
import net.byteseek.utils.ArgUtils;
import net.byteseek.utils.collections.PositionHashMap;

/**
 * A {@link WindowCache} which holds on to all {@link net.byteseek.io.reader.windows.Window} objects.
 * <p>
 * Because it never lets go of windows, there will never be a window free notification.  So while it is
 * possible for observers to subscribe for free notifications, they will never receive any.
 * <p>
 * Note that if SoftWindows are used rather than HardWindows, then the JRE can reclaim memory under
 * low memory conditions, while the cache will retain the window metadata (and the SoftWindow can reload
 * itself if required).
 * 
 * @author Matt Palmer
 */
public final class AllWindowsCache extends AbstractMemoryCache {

    private static final int DEFAULT_CAPACITY = 32;
    private final PositionHashMap<Window> cache;

    /**
     * Constructs an AllWindowsCache with an initial capacity of 32.
     *  It can thus store 32 windows before the underlying cache needs to resize itself.
     */
    public AllWindowsCache() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Constructs an AllWindowsCache with an initial capacity provided.
     *
     * @param initialCapacity The initial capacity of the cache.
     * @throws IllegalArgumentException if the initial capacity is zero or less.
     */
    public AllWindowsCache(final int initialCapacity) {
        ArgUtils.checkGreaterThanZero(initialCapacity, "initialCapacity");
        cache = new PositionHashMap<Window>(initialCapacity);
    }

    @Override
    public Window getWindow(final long position) {
        return cache.get(position);
    }

    @Override
    public void addWindow(final Window window) {
        cache.put(window.getWindowPosition(), window);
    }

    @Override
    public void clear() {
        cache.clear();
    }
    
	@Override
	public String toString() {
		return getClass().getSimpleName() + "(cache:" + cache + ')';
	}
    
}
