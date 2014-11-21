/*
 * Copyright Matt Palmer 2011-2012, All rights reserved.
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


/**
 * A interface for classes which cache {@link net.byteseek.io.reader.Window} objects.
 * It also provides the ability to subscribe for notifications that a
 * Window object is leaving the cache.
 * 
 * @author Matt Palmer
 */
public interface WindowCache {
    
    
    /**
     * Returns the {@link net.byteseek.io.reader.Window} at the position specified.
     * The position must be one at which a Window object begins.  It will not return
     * a Window for a position which simply exists within a Window.  If no Window
     * exists in the cache at the exact position specified, then null is returned.
     * 
     * @param position The position at which a Window begins in the cache.
     * @return A Window for the specified starting position, or null if the Window does not exist.
     */
    public Window getWindow(final long position);
    
    
    /**
     * Adds a {@link net.byteseek.io.reader.Window} to the cache.
     * 
     * @param window The Window to add to the cache.
     */
    public void addWindow(final Window window);
    
    
    /**
     * Clears all {@link net.byteseek.io.reader.Window}s from the cache.
     */
    public void clear();
    
    
    /**
     * Subscribes a {@link WindowObserver} to this cache for notification when a
     * {@link net.byteseek.io.reader.Window} leaves it.
     * 
     * @param observer The observer who wants notification that a Window is leaving the cache.
     */
    public void subscribe(final WindowObserver observer);
    
    
    /**
     * Unsubscribes a {@link WindowObserver} from this cache.
     * 
     * @param observer The observer who no longer wants notification that a Window is leaving the cache.
     * @return boolean True if the observer was unsubcribed from the cache.  If false, then it
     *                 is likely that the observer was never subscribed in the first place, or
     *                 has already been unsubscribed.
     */
    public boolean unsubscribe(final WindowObserver observer);
    
    
    /**
     * An interface for objects which want notification when a {@link net.byteseek.io.reader.Window}
     * is leaving a cache.
     */
    public interface WindowObserver {
        
        /**
         * A method which is called on the WindowObserver when a {@link net.byteseek.io.reader.Window}
         * leaves a cache.
         * @param window The Window which is leaving a cache.
         * @param fromCache The cache that the Window is leaving.
         * 
         */
        public void windowFree(final Window window, WindowCache fromCache);
        
    }
    
    
}
