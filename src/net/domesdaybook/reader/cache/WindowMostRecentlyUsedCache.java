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
public final class WindowMostRecentlyUsedCache extends LinkedHashMap<Long, Window> implements WindowCache  {
    
    private final static boolean ORDER_BY_ACCESS = true;
    
    private final int capacity;

    
    public WindowMostRecentlyUsedCache(final int capacity) {
        super(capacity + 1, 1.1f, ORDER_BY_ACCESS);
        this.capacity = capacity;
    }
    
    
    public Window getWindow(final long position) {
        return get(position);
    }

    
    public void addWindow(final Window window) {
        put(window.getWindowPosition(), window);
    }


    @Override
    protected boolean removeEldestEntry(final Map.Entry eldest) {
        return size() > capacity;
    }    
    
    @Override
    public WindowCache newInstance() {
        return new WindowMostRecentlyUsedCache(capacity);
    }    
    
}
