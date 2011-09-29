/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.reader.cache;

import java.util.ArrayList;
import java.util.List;
import net.domesdaybook.reader.Window;
import net.domesdaybook.reader.cache.WindowCache.CacheObserver;

/**
 *
 * @author matt
 */

public final class TwoLevelCache implements WindowCache, CacheObserver {

    public static TwoLevelCache create(final WindowCache primaryCache, final WindowCache secondaryCache) {
        final TwoLevelCache twoLevels = new TwoLevelCache(primaryCache, secondaryCache);
        primaryCache.subscribe(twoLevels);
        secondaryCache.subscribe(twoLevels);
        return twoLevels;
    }
    
    private final WindowCache primaryCache;
    private final WindowCache secondaryCache;

    private TwoLevelCache(final WindowCache primaryCache, final WindowCache secondaryCache) {
        this.primaryCache = primaryCache;
        this.secondaryCache = secondaryCache;
    }
    
    
    @Override
    public Window getWindow(final long position) {
        Window window = primaryCache.getWindow(position);
        if (window == null) {
            window = secondaryCache.getWindow(position);
            if (window != null) {
                addWindow(window);
            }
        }
        return window;
    }

    
    @Override
    public void addWindow(final Window window) {
        primaryCache.addWindow(window);
    }

    
    @Override
    public void clear() {
        primaryCache.clear();
        secondaryCache.clear();
    }

    
    @Override
    public void subscribe(final CacheObserver observer) {
        primaryCache.subscribe(observer);
        secondaryCache.subscribe(observer);
    }

    
    @Override
    public boolean unsubscribe(final CacheObserver observer) {
        // Use of bitwise | OR here (not boolean ||) as we always 
        // want to run both unsubscribe calls, but return true if the observer
        // was subscribed to either of them.
        return primaryCache.unsubscribe(observer) | secondaryCache.unsubscribe(observer);
    }
    
    
    @Override
    public void windowRemoved(final Window window, final WindowCache fromCache) {
        if (fromCache == primaryCache) {
            secondaryCache.addWindow(window);
        }
    }
    
    
    @Override
    public void windowAdded(final Window window, final WindowCache toCache) {
        // don't care about windows being added to our own caches.
    }
    
    
    public WindowCache getPrimaryCache() {
        return primaryCache;
    }
    
    
    public WindowCache getSecondaryCache() {
        return secondaryCache;
    }
    
    
}
