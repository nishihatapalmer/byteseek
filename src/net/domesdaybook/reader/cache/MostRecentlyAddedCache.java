/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.reader.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import net.domesdaybook.reader.Window;

/**
 * @author matt
 */
public final class MostRecentlyAddedCache extends AbstractObservableCache  {

    private final static boolean INSERTION_ORDER = false;    
    
    private final Cache cache;
    
    public MostRecentlyAddedCache(final int capacity) {
        cache = new Cache(capacity + 1, 1.1f, INSERTION_ORDER);
    }
    
    
    @Override
    public Window getWindow(final long position) {
        return cache.get(position);
    }

    
    @Override
    public void addWindow(final Window window) {
        final long windowPosition = window.getWindowPosition();
        if (!cache.containsKey(windowPosition)) {
            cache.put(windowPosition, window);
            notifyWindowAdded(window, this);
        }
    }

    
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
                notifyWindowRemoved((Window) eldest.getValue(), MostRecentlyAddedCache.this);
            }
            return remove;
        }   
    }    
        
    
}
