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


package net.domesdaybook.io.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.domesdaybook.io.Window;

/**
 * An AbstractCache implements the {@link net.domesdaybook.io.cache.WindowCache.WindowObserver} part of a {@link WindowCache},
 * providing subscription, unsubscription and notification services.
 * <p>
 * Observers can receive notifications that a Window is leaving a WindowCache.
 * <p>
 * This is not thread-safe - there is no synchronisation in the list of observers
 * or access to the list.
 * 
 * @author Matt Palmer
 */
public abstract class AbstractCache implements WindowCache {
    
    private List<WindowObserver> windowObservers; 
    
    /**
     * Constructs a WindowCache with an empty list of cache observers.
     */
    public AbstractCache() {
        windowObservers = Collections.emptyList(); 
    }
    
    
    /**
     * Subscribes a {@link net.domesdaybook.io.cache.WindowCache.WindowObserver} to this {@link WindowCache}.
     * 
     * @param observer The WindowObserver to subscribe.
     */
    @Override
    public void subscribe(WindowObserver observer) {
        if (windowObservers.isEmpty()) {
            windowObservers = new ArrayList<WindowObserver>(1);
        }
        windowObservers.add(observer);
    }
    
    
    /**
     * Unsubscribes a {@link net.domesdaybook.io.cache.WindowCache.WindowObserver} from this {@link WindowCache}.
     * 
     * @param observer The WindowObserver to unsubscribe.
     * @return boolean True if the WindowObserver was unsubscribed.
     */
    @Override
    public boolean unsubscribe(final WindowObserver observer) {
        boolean removed = windowObservers.remove(observer);
        if (windowObservers.isEmpty()) {
            windowObservers = Collections.emptyList();
        }
        return removed;
    }
    
    
    /**
     * Notifies a {@link net.domesdaybook.io.cache.WindowCache.WindowObserver} that a {@link Window} was removed from a
     * {@link WindowCache}.
     * 
     * @param window The Window which was removed from this cache.
     * @param fromCache The WindowCache from which the Window was removed.
     */
    protected final void notifyWindowFree(final Window window, final WindowCache fromCache) {
        for (final WindowObserver observer : windowObservers) {
            observer.windowFree(window, fromCache);
        }
    }
    

}
