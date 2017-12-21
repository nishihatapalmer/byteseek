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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class AllWindowsCacheTest {

    private final byte[] array = new byte[1024];
    private final Window window1 = new HardWindow(array, 0, array.length);
    private final Window window2 = new HardWindow(array, 1024, array.length);

    private AllWindowsCache allWindowsCache;

    @Before
    public void setup() {
        allWindowsCache = new AllWindowsCache();
    }

    @After
    public void close() {
        allWindowsCache.clear();
    }

    @Test
    public void testGetAddWindow() throws Exception {
        assertNull(allWindowsCache.getWindow(0));
        assertNull(allWindowsCache.getWindow(1024));
        assertNull(allWindowsCache.getWindow(-1));
        assertNull(allWindowsCache.getWindow(1));
        assertNull(allWindowsCache.getWindow(1023));
        assertNull(allWindowsCache.getWindow(1025));

        allWindowsCache.addWindow(window2);
        assertNull(allWindowsCache.getWindow(0));
        assertNotNull(allWindowsCache.getWindow(1024));
        assertNull(allWindowsCache.getWindow(-1));
        assertNull(allWindowsCache.getWindow(1));
        assertNull(allWindowsCache.getWindow(1023));
        assertNull(allWindowsCache.getWindow(1025));

        allWindowsCache.addWindow(window1);
        assertNotNull(allWindowsCache.getWindow(0));
        assertNotNull(allWindowsCache.getWindow(1024));
        assertNull(allWindowsCache.getWindow(-1));
        assertNull(allWindowsCache.getWindow(1));
        assertNull(allWindowsCache.getWindow(1023));
        assertNull(allWindowsCache.getWindow(1025));
    }

    @Test
    public void testCachesAllWindows() {
        final int[] freeCount = new int[1];
        WindowCache.WindowObserver observer = new WindowCache.WindowObserver() {
            @Override
            public void windowFree(Window window, WindowCache fromCache) throws IOException {
                freeCount[0] = freeCount[0] + 1;
            }
        };
        allWindowsCache.subscribe(observer);
        for (int i = 0; i < 10000; i++) {
            Window window = new HardWindow(array, i, 1);
            allWindowsCache.addWindow(window);
        }
        assertEquals(0, freeCount[0]);
        for (int i = 0; i < 10000; i++) {
            assertNotNull(allWindowsCache.getWindow(i));
        }
    }

    @Test
    public void testClear() throws Exception {
        allWindowsCache.addWindow(window1);
        allWindowsCache.addWindow(window2);
        assertNotNull(allWindowsCache.getWindow(0));
        assertNotNull(allWindowsCache.getWindow(1024));
        assertNull(allWindowsCache.getWindow(-1));
        assertNull(allWindowsCache.getWindow(1));
        assertNull(allWindowsCache.getWindow(1023));
        assertNull(allWindowsCache.getWindow(1025));
        allWindowsCache.clear();
        assertNull(allWindowsCache.getWindow(0));
        assertNull(allWindowsCache.getWindow(1024));
        assertNull(allWindowsCache.getWindow(-1));
        assertNull(allWindowsCache.getWindow(1));
        assertNull(allWindowsCache.getWindow(1023));
        assertNull(allWindowsCache.getWindow(1025));
    }

    @Test
    public void testToString() throws Exception {
        assertTrue(allWindowsCache.toString().contains(allWindowsCache.getClass().getSimpleName()));
        assertTrue(allWindowsCache.toString().contains("size"));
        assertTrue(allWindowsCache.toString().contains("capacity"));
    }

}