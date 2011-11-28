/*
 * Copyright Matt Palmer 2011, All rights reserved.
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
 * 
 */

package net.domesdaybook.reader.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import net.domesdaybook.reader.Window;

/**
 * @author matt
 */
public final class MostRecentlyUsedCache extends AbstractCache  {

    private final static boolean ACCESS_ORDER = true;    
    
    private final Cache cache;
    
    /**
     * 
     * @param capacity
     */
    public MostRecentlyUsedCache(final int capacity) {
        cache = new Cache(capacity + 1, 1.1f, ACCESS_ORDER);
    }
    
    
    /**
     * 
     * @param position
     * @return
     */
    @Override
    public Window getWindow(final long position) {
        return cache.get(position);
    }

    
    /**
     * 
     * @param window
     */
    @Override
    public void addWindow(final Window window) {
        final long windowPosition = window.getWindowPosition();
        if (!cache.containsKey(windowPosition)) {
            cache.put(windowPosition, window);
            notifyWindowAdded(window, this);
        }
    }
    
   
    /**
     * 
     */
    @Override
    public void clear() {
        cache.clear();
    }
    
    
    private class Cache extends LinkedHashMap<Long, Window> {

        private final int capacity;
        
        private Cache(int capacity, float loadFactor, boolean accessOrder) {
            super(capacity, loadFactor, accessOrder);
            this.capacity = capacity;
        }
        
        @Override
        protected boolean removeEldestEntry(final Map.Entry eldest) {
            final boolean remove = size() > capacity;
            if (remove) {
                notifyWindowRemoved((Window) eldest.getValue(), MostRecentlyUsedCache.this);
            }
            return remove;
        }   
    }    
        
    
}
