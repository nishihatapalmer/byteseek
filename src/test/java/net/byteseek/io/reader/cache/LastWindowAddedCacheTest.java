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

import net.byteseek.io.reader.windows.HardWindow;
import net.byteseek.io.reader.windows.Window;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class LastWindowAddedCacheTest {

    private static long WINDOW1POS = 0;
    private static long WINDOW2POS = 4096;

    private final static byte VALUE1 = 1;
    private final static byte VALUE2 = 2;

    private LastWindowAddedCache cache;
    private byte[] data1, data2;
    private Window testWindow1, testWindow2;
    private int testWindow1Length = 4096;
    private int testWindow2Length = 4096;
    private int testData1Length   = 4096;
    private int testData2Length   = 4096;

    @Before
    public void setupTest() {
        cache = new LastWindowAddedCache();
        data1 = new byte[testData1Length];
        Arrays.fill(data1, VALUE1);
        data2 = new byte[testData2Length];
        Arrays.fill(data2, VALUE2);
        testWindow1 = new HardWindow(data1, WINDOW1POS, testWindow1Length);
        testWindow2 = new HardWindow(data2, WINDOW2POS, testWindow2Length);
    }

    @Test
    public void testAddTwoWindows() throws Exception {

        // First window is added and cached.
        cache.addWindow(testWindow1);
        assertEquals(testWindow1, cache.getWindow(WINDOW1POS));
        assertNull(cache.getWindow(WINDOW2POS));

        // Second window is added and cached, the previous window is no longer cached.
        cache.addWindow(testWindow2);
        assertNull(cache.getWindow(WINDOW1POS));
        assertEquals(testWindow2, cache.getWindow(WINDOW2POS));
    }

    @Test
    public void testClear() throws Exception {
        cache.addWindow(testWindow1);
        assertEquals(testWindow1, cache.getWindow(WINDOW1POS));

        cache.clear();
        assertNull(cache.getWindow(WINDOW1POS));
    }

    @Test
    public void testDoesNotAddNullWindow() throws Exception {
        assertNull(cache.getWindow(WINDOW1POS));

        cache.addWindow(testWindow1);
        assertEquals(testWindow1, cache.getWindow(WINDOW1POS));

        cache.addWindow(null);
        assertEquals(testWindow1, cache.getWindow(WINDOW1POS));
    }

    @Test
    public void testSubscription() throws Exception {
        final List<Window> evictedWindows = new ArrayList<Window>();
        WindowCache.WindowObserver rememberEvictions = new WindowCache.WindowObserver() {
            @Override
            public void windowFree(Window window, WindowCache fromCache) throws IOException {
                evictedWindows.add(window);
            }
        };
        cache.subscribe(rememberEvictions);

        cache.addWindow(testWindow1);
        assertEquals(0, evictedWindows.size());

        cache.addWindow(testWindow2);
        assertEquals(1, evictedWindows.size());
        assertEquals(testWindow1, evictedWindows.get(0));

        cache.addWindow(testWindow1);
        assertEquals(2, evictedWindows.size());
        assertEquals(testWindow2, evictedWindows.get(1));

        cache.unsubscribe(rememberEvictions);
        evictedWindows.clear();

        cache.addWindow(testWindow2);
        assertEquals(0, evictedWindows.size());

        cache.addWindow(testWindow1);
        assertEquals(0, evictedWindows.size());
    }



}