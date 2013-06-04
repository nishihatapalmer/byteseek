/*
 * Copyright Matt Palmer 2011-2012, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 
 *  * The names of its contributors may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package net.byteseek.io.reader.cache;

import net.byteseek.io.reader.Window;


/**
 * A {@link WindowCache} which holds on to no {@link net.byteseek.io.reader.Window}
 * objects.
 * 
 * @author Matt Palmer
 */
public final class NoCache extends AbstractCache {

    /**
     * A static NoCache object, as this object has no state, so can be
     * safely re-used by any class which requires a NoCache.
     */
    public static final NoCache NO_CACHE = new NoCache();
    
    
    /**
     * Always returns null, as no Windows are cached.
     * 
     * @param position The position for which a {@link net.byteseek.io.reader.Window} is requested.
     * @return Window null in all cases.
     */
    @Override
    public Window getWindow(long position) {
        return null;
    }

    
    /**
     * Does not actually add the {@link net.byteseek.io.reader.Window} to the
     * cache, as the NoCache object performs no caching.
     * 
     * @param window A Window to add (which it will not be).
     */
    @Override
    public void addWindow(Window window) {
        notifyWindowFree(window, this);
    }

    
    /**
     * Does nothing, as the NoCache object does not cache anything.
     */
    @Override
    public void clear() {
        // nothing to do
    }
    
    
}
