/*
 * Copyright Matt Palmer 2019, All rights reserved.
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

import net.byteseek.io.reader.windows.Window;
import net.byteseek.io.reader.windows.WindowFactory;
import net.byteseek.utils.ArgUtils;

import java.io.IOException;

/**
 * A simple cache that holds on to the last Window returned by the wrapped Cache.
 */
public class LastWindowCache extends AbstractMemoryCache {

    private final WindowCache wrappedCache;
    private Window lastWindow;

    /**
     * Constructs a LastTwoWindowsCache given a WindowCache to wrap.
     *
     * @param cache The WindowCache wrapped by this LastTwoWindowsCache.
     */
    public LastWindowCache(final WindowCache cache) {
        ArgUtils.checkNullObject(cache, "cache");
        wrappedCache = cache;
    }

    @Override
    public Window getWindow(final long position) throws IOException {
        if (lastWindow != null && lastWindow.getWindowPosition() == position) {
            return lastWindow;
        }
        final Window nextWindow = wrappedCache.getWindow(position);
        if (nextWindow != null) {
            lastWindow = nextWindow;
        }
        return nextWindow;
    }

    @Override
    public void addWindow(final Window window) throws IOException {
        wrappedCache.addWindow(window);
    }

    @Override
    public void clear() throws IOException {
        lastWindow = null;
        wrappedCache.clear();
    }

    @Override
    public void setWindowFactory(WindowFactory factory) {
        // Does not itself create windows, just wraps another cache.
    }
}
