/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.reader.cache;

import java.util.HashMap;
import net.domesdaybook.reader.Window;

/**
 *
 * @author matt
 */
public final class AllWindowsCache extends HashMap<Long, Window> implements WindowCache {

    
    @Override
    public Window getWindow(final long position) {
        return get(position);
    }

    
    @Override
    public void addWindow(final Window window) {
        final long position = window.getWindowPosition();
        put(position, window);
    }

    @Override
    public WindowCache newInstance() {
        return new AllWindowsCache();
    }
    
}
