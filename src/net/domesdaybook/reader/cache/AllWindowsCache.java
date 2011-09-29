/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.reader.cache;

import java.util.HashMap;
import java.util.Map;
import net.domesdaybook.reader.Window;

/**
 *
 * @author matt
 */
public final class AllWindowsCache extends AbstractObservableCache {

    private final Map<Long, Window> cache = new HashMap<Long, Window>();
    
    @Override
    public Window getWindow(final long position) {
        return cache.get(position);
    }

    
    @Override
    public void addWindow(final Window window) {
        cache.put(window.getWindowPosition(), window);
        notifyWindowAdded(window, this);
    }

    
    @Override
    public void clear() {
        cache.clear();
    }
    
}
