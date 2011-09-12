/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

import java.util.HashMap;

/**
 *
 * @author matt
 */
public final class WindowCacheAll extends HashMap<Long, Window> implements WindowCache {

    @Override
    public Window getWindow(final long position) {
        return get(position);
    }

    
    @Override
    public void addWindow(final Window window) {
        put(window.getWindowPosition(), window);
    }
    
    
}
