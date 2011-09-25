/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.domesdaybook.reader.cache;

import net.domesdaybook.reader.Window;

/**
 *
 * @author matt
 */
public class WindowTwoLevelCache implements WindowCache {

    WindowCache primaryCache;
    WindowCache secondaryCache;

    
    private WindowTwoLevelCache(WindowCache primaryCache, WindowCache secondaryCache) {
        this.primaryCache = primaryCache;
        this.secondaryCache = secondaryCache;
    }
    
    
    public Window getWindow(long position) {
        Window window = primaryCache.getWindow(position);
        if (window == null) {
            window = secondaryCache.getWindow(position);
        }
        return window;
    }

    
    public void addWindow(Window window) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    public void clear() {
        primaryCache.clear();
        secondaryCache.clear();
    }

    
    public WindowCache newInstance() {
        return new WindowTwoLevelCache(primaryCache, secondaryCache);
    }
    
}
