/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

/**
 *
 * @author matt
 */
public interface WindowCache {
    
    Window getWindow(final long position);
    
    void addWindow(final Window window);
    
    void clear();
}
