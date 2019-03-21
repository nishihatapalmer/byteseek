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
 * A simple cache that holds on to the last Window added to it.
 * <p>
 * There are probably few access patterns that need this cache, as window processing
 * code tends to get a different window from the last one added.
 * <p>
 * Where repeated calls to {@link net.byteseek.io.reader.WindowReader#readByte(long)} on a
 * WindowReader are made, it may be useful to use this cache.
 */
public class LastWindowAddedCache extends AbstractMemoryCache {

    private Window lastWindow;

    /**
     * Constructs a LastWindowAddedCache.
     */
    public LastWindowAddedCache() {
    }

    @Override
    public Window getWindow(final long position) throws IOException {
        final Window localWindow = lastWindow;
        if (localWindow != null && localWindow.getWindowPosition() == position) {
            return localWindow;
        }
        return null;
    }

    @Override
    public void addWindow(final Window window) throws IOException {
        if (window != null) {
            final Window localWindow = lastWindow;
            if (localWindow != null && localWindow.getWindowPosition() != window.getWindowPosition()) {
                notifyWindowFree(localWindow, this);
            }
            lastWindow = window;
        }
    }

    @Override
    public void clear() throws IOException {
        lastWindow = null;
    }

}
