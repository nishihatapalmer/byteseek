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
public final class NoCache implements WindowCache {

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
    

    @Override
    public void subscribe(CacheObserver observer) {
        // nothing ever leaves or enters... so don't bother adding any observers.
    }

    
    @Override
    public boolean unsubscribe(CacheObserver observer) {
        return false;  // the observer was never present, following the contract
        // used by List, where remove() returns true if the item was present.
    }

    
}
