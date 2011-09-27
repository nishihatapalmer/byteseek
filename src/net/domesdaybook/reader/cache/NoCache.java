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
public class NoCache implements WindowCache {

    public static final NoCache NO_CACHE = new NoCache();
    
    @Override
    public Window getWindow(long position) {
        return null;
    }

    @Override
    public void addWindow(Window window) {
        // nothing to do
    }

    @Override
    public void clear() {
        // nothing to do
    }

    public WindowCache newInstance() {
        return NO_CACHE; // They are all the same, so just return the static default.
    }
    
}
