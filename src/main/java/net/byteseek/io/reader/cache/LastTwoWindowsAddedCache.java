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
 * A simple cache that holds on to the last two windows added, and assumes that the previous window
 * will most often be the next one requested.  It would not be a typical pattern to repeatedly ask for
 * the same window as last requested (as most code obtains a window and processes everything in it before
 * asking for the next window. This cache checks for the previous window first, before the current one,
 * on the grounds that needing access to the same window last added is unusual, it is more likely to need
 * the previous window.
 */
public class LastTwoWindowsAddedCache extends AbstractMemoryCache {

    private Window currentWindow, previousWindow;

    /**
     * Constructs a LastTwoWindowsAddedCache.
     *
     */
    public LastTwoWindowsAddedCache() {
    }

    @Override
    public Window getWindow(final long position) throws IOException {
        Window localWindow = previousWindow;
        if (localWindow != null && localWindow.getWindowPosition() == position) {
            return localWindow;
        }
        localWindow = currentWindow;
        if (localWindow != null && localWindow.getWindowPosition() == position) {
            return localWindow;
        }
        return null;
    }

    @Override
    public void addWindow(final Window window) throws IOException {
        if (window != null) {
            if (previousWindow != null) {
                notifyWindowFree(previousWindow, this);
            }
            previousWindow = currentWindow;
            currentWindow = window;
        }
    }

    @Override
    public void clear() throws IOException {
        currentWindow = null;
        previousWindow = null;
    }
}
