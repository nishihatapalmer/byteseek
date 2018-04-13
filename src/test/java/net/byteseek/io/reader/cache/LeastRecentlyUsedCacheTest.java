/*
 * Copyright Matt Palmer 2018, All rights reserved.
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class LeastRecentlyUsedCacheTest {

    private static long WINDOW1POS = 0;
    private static long WINDOW2POS = 4096;

    private LeastRecentlyUsedCache cache;
    private byte[] testData;
    private Window testWindow1, testWindow2;

    @Before
    public void setupTest() {
        cache = new LeastRecentlyUsedCache(3);
        testData = new byte[4096];
        testWindow1 = new HardWindow(testData, WINDOW1POS, 4096);
        testWindow2 = new HardWindow(testData, WINDOW2POS, 4096);
    }

    @After
    public void closeDownTest() {
        cache.clear();
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNegativeCapacity() {
        new LeastRecentlyAddedCache(-1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testZeroCapacity() {
        new LeastRecentlyAddedCache(0);
    }

    @Test
    public void testGetNullWindows() throws Exception {
        assertNull(cache.getWindow(0));
        assertNull(cache.getWindow(4096));
        assertNull(cache.getWindow(-1));
        assertNull(cache.getWindow(1000000000));
    }

    @Test
    public void testAddThenGetWindow() throws Exception {
        cache.addWindow(testWindow1);
        assertEquals("Get window added at 0", testWindow1, cache.getWindow(WINDOW1POS));
        assertNull("No window at 1", cache.getWindow(1));
        assertNull("No window at negative position", cache.getWindow(-1));
    }

    @Test
    public void testClear() throws Exception {
        cache.addWindow(testWindow1);
        cache.addWindow(testWindow2);
        assertNotNull("Window present in cache.", cache.getWindow(WINDOW1POS));
        assertNotNull("Window present in cache.", cache.getWindow(WINDOW2POS));

        cache.clear();

        assertNull("Window no longer present in cache.", cache.getWindow(WINDOW1POS));
        assertNull("Window no longer present in cache.", cache.getWindow(WINDOW2POS));
    }


    // If no windows are accessed, then the order of eviction for this cache will be the same as
    // the least recently added cache.
    @Test
    public void testEvictLeastRecentlyAddedNoWindowAccess() throws Exception {
        for (int capacity = 1; capacity <= 10; capacity++) {
            testEvictLeastRecentlyAdded(capacity);
        }
    }

    @Test
    public void testEvictLeastRecentlyUsedAfterAccess() throws Exception {
        for (int capacity = 1; capacity <= 10; capacity++) {
            testEvictLeastRecentlyUsed(capacity);
        }
    }

    private void testEvictLeastRecentlyAdded(int capacity) throws Exception {

        // Create a cache with the right capacity:
        cache = new LeastRecentlyUsedCache(capacity);

        // Make an observer create a list of windows which were freed by the cache:
        final List<Window> evictedWindows = new ArrayList<Window>();
        WindowCache.WindowObserver observer = new WindowCache.WindowObserver() {
            @Override
            public void windowFree(Window window, WindowCache fromCache) throws IOException {
                evictedWindows.add(window);
            }
        };
        cache.subscribe(observer);

        // Add windows up to the capacity (no windows should yet be evicted):
        for (int addNum = 1; addNum <= capacity; addNum++) {
            final long winPos = (addNum - 1) * 4096;
            Window win = new HardWindow(testData, winPos, 4096);
            cache.addWindow(win);
        }
        assertEquals("No windows yet evicted", 0, evictedWindows.size());

        // Add windows going over the capacity:
        for (int moreNum = capacity + 1; moreNum < capacity * 2; moreNum++) {
            final long winPos = (moreNum - 1) * 4096;
            Window win = new HardWindow(testData, winPos, 4096);
            cache.addWindow(win);

            assertEquals("Num windows evicted correct:", moreNum - capacity, evictedWindows.size());
            Window ewin = evictedWindows.get(evictedWindows.size() - 1);
            final long ePos = (moreNum - capacity - 1) * 4096;
            assertEquals("Evicted window correct at position " + ePos, ePos, ewin.getWindowPosition());
        }

    }

    private void testEvictLeastRecentlyUsed(int capacity) throws Exception {

        // Create a cache with the right capacity:
        cache = new LeastRecentlyUsedCache(capacity);

        // Make an observer create a list of windows which were freed by the cache:
        final List<Window> evictedWindows = new ArrayList<Window>();
        WindowCache.WindowObserver observer = new WindowCache.WindowObserver() {
            @Override
            public void windowFree(Window window, WindowCache fromCache) throws IOException {
                evictedWindows.add(window);
            }
        };
        cache.subscribe(observer);

        // Add windows up to the capacity (no windows should yet be evicted):
        for (int addNum = 1; addNum <= capacity; addNum++) {
            final long winPos = (addNum - 1) * 4096;
            Window win = new HardWindow(testData, winPos, 4096);
            cache.addWindow(win);
        }
        assertEquals("No windows yet evicted", 0, evictedWindows.size());

        // Access the windows added in reverse order (making the first window added the most recently accessed)
        for (int addNum = capacity; addNum > 0; addNum--) {
            final long winPos = (addNum - 1) * 4096;
            Window win = cache.getWindow(winPos);
        }

        // Add windows going over the capacity - evicted windows should appear in reverse order:
        for (int moreNum = capacity + 1; moreNum <= capacity * 2; moreNum++) {
            final long winPos = (moreNum - 1) * 4096;
            Window win = new HardWindow(testData, winPos, 4096);
            cache.addWindow(win);

            assertEquals("Num windows evicted correct:", moreNum - capacity, evictedWindows.size());
            Window ewin = evictedWindows.get(evictedWindows.size() - 1);
            final long ePos = (capacity - (moreNum - capacity)) * 4096;
            assertEquals("Evicted window correct at position " + ePos, ePos, ewin.getWindowPosition());
        }
    }

    //TODO: test subscribe unsubscribe.


    @Test
    public void testToString() throws Exception {
        String description = cache.toString();
        assertTrue(description.contains(cache.getClass().getSimpleName()));
        assertTrue(description.contains("capacity"));
    }

    private class WindowObserver {
    }
}