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
public class LastWindowCache implements WindowCache {

    private Window lastWindow;
    
    @Override
    public Window getWindow(long position) {
        if (lastWindow != null) {
            final long winPosition = lastWindow.getWindowPosition(); 
            if (position >= winPosition &&
                position < winPosition + lastWindow.getLimit()) {
                return lastWindow;
            }
        }
        return null;
    }

    
    @Override
    public void addWindow(Window window) {
        lastWindow = window;
    }

    
    @Override
    public void clear() {
        lastWindow = null;
    }
    
    @Override
    public WindowCache newInstance() {
        return new LastWindowCache();
    }    
    
}
