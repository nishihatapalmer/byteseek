/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.reader.cache;

import net.domesdaybook.reader.Window;

/**
 *
 * @author matt
 */
public interface WindowCache {
    
    
    public Window getWindow(final long position);
    
    
    public void addWindow(final Window window);
    
    
    public void clear();
    
    
    public void subscribe(final CacheObserver observer);
    
    
    public boolean unsubscribe(final CacheObserver observer);
    
    
    public interface CacheObserver {
        void windowAdded(final Window window, final WindowCache toCache);
        void windowRemoved(final Window window, final WindowCache fromCache);
    }
    
    
}
