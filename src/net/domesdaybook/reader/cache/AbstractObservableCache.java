/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */


package net.domesdaybook.reader.cache;

import java.util.ArrayList;
import java.util.List;
import net.domesdaybook.reader.Window;

/**
 *
 * @author matt
 */
public abstract class AbstractObservableCache implements WindowCache {
    
    private final List<CacheObserver> observers; 
    
    public AbstractObservableCache() {
        // we assume very few people actually want to observe the caches.
        observers = new ArrayList<CacheObserver>(1); 
    }
    
    @Override
    public void subscribe(CacheObserver observer) {
        observers.add(observer);
    }

    
    @Override
    public boolean unsubscribe(CacheObserver observer) {
        return observers.remove(observer);
    }
    
    
    protected final void notifyWindowRemoved(final Window window, final WindowCache fromCache) {
        for (final CacheObserver observer : observers) {
            observer.windowRemoved(window, fromCache);
        }
    }
    
    protected final void notifyWindowAdded(final Window window, final WindowCache toCache) {
        for (final CacheObserver observer : observers) {
            observer.windowAdded(window, toCache);
        }
    }
    
    
}
