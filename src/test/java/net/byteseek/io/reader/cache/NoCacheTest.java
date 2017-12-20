/*
 * Copyright Matt Palmer 2017, All rights reserved.
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

import net.byteseek.io.reader.windows.HardWindow;
import net.byteseek.io.reader.windows.Window;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class NoCacheTest {

    private Window window;
    private NoCache noCache;

    @Before
    public void setup() {
        window = new HardWindow(new byte[1024], 0, 1024);
        noCache = new NoCache();
    }


    @Test
    public void testGetWindow() throws Exception {
        assertNull(noCache.getWindow(4));
        assertNull(noCache.getWindow(0));
        assertNull(noCache.getWindow(-123123123));
        assertNull(noCache.getWindow(16));
    }

    @Test
    public void testAddThenGetNullWindow() throws Exception {
        assertNull(noCache.getWindow(0));
        noCache.addWindow(window);
        assertNull(noCache.getWindow(0));
    }

    @Test
    public void testRead() throws Exception {
        assertEquals(0, noCache.read(0, 0, new byte[1024], 0));
        noCache.addWindow(window);
        assertEquals(0, noCache.read(0, 0, new byte[1024], 0));
    }

    @Test
    public void testAddImmediateFreeWindow() throws Exception {
        final Window[] result = new Window[1];
        WindowCache.WindowObserver observer = new WindowCache.WindowObserver() {
            @Override
            public void windowFree(Window win, WindowCache fromCache) throws IOException {
                 result[0] = win;
            }
        };
        noCache.subscribe(observer);
        noCache.addWindow(window);
        assertTrue(window == result[0]);
        assertNull(noCache.getWindow(0));
    }

    @Test
    public void testToString() throws Exception {
        assertTrue(NoCache.NO_CACHE.toString().contains(NoCache.NO_CACHE.getClass().getSimpleName()));
    }

    @Test
    public void testClear() throws Exception {
        noCache.addWindow(window); // If it's behaving properly, adding a window won't actually add it.
        try {
            // not much to test here since it doesn't hold on to anything - but if it throws anything there's a problem.
            noCache.clear();
        } catch (Exception e) {
            fail("Clearing the cache threw the exception: " + e);
        }
        assertNull(noCache.getWindow(0)); // If it returns anything but null here there's a problem.
    }
}