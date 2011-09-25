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
    
    Window getWindow(final long position);
    
    void addWindow(final Window window);
    
    void clear();
    
    WindowCache newInstance();
}
