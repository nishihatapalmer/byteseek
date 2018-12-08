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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class LeastRecentlyAddedCacheTest {

    private static long WINDOW1POS = 0;
    private static long WINDOW2POS = 4096;
    private final static byte VALUE1 = 1;
    private final static byte VALUE2 = 2;

    private LeastRecentlyAddedCache cache;
    private byte[] data1, data2;
    private Window testWindow1, testWindow2;
    private int testWindow1Length = 4096;
    private int testWindow2Length = 4096;
    private int testData1Length   = 4096;
    private int testData2Length   = 4096;

    @Before
    public void setupTest() {
        cache = new LeastRecentlyAddedCache(3);
        data1 = new byte[testData1Length];
        Arrays.fill(data1, VALUE1);
        data2 = new byte[testData2Length];
        Arrays.fill(data2, VALUE2);
        testWindow1 = new HardWindow(data1, WINDOW1POS, testWindow1Length);
        testWindow2 = new HardWindow(data2, WINDOW2POS, testWindow2Length);
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

    @Test
    public void testRead() throws Exception {
        cache         = new LeastRecentlyAddedCache(5);

        byte[] bytes1 = new byte[testData1Length];
        assertEquals("no bytes read when nothing cached", 0,
                cache.read(0, 0, bytes1, 0));
        cache.addWindow(testWindow1);
        assertEquals(testWindow1Length + "bytes read after caching it", testWindow1Length,
                cache.read(0, 0, bytes1, 0));
        assertArrayValue(bytes1, VALUE1, testWindow1Length);


        byte[] bytes2 = new byte[testData2Length];
        assertEquals("no bytes read when nothing cached", 0,
                cache.read(testWindow1Length, 0, bytes2, 0));
        cache.addWindow(testWindow2);
        assertEquals(testWindow2Length + " bytes read after caching it", testWindow2Length,
                cache.read(testWindow1Length, 0, bytes2, 0));
        assertArrayValue(bytes2, VALUE2, testWindow2Length);
    }

    @Test
    public void testReadByteBuffer() throws Exception {
        ByteBuffer buffer1 = ByteBuffer.wrap(new byte[testData1Length]);
        cache         = new LeastRecentlyAddedCache(5);

        assertEquals("no bytes read when nothing cached", 0,
                cache.read(0, 0, buffer1));
        cache.addWindow(testWindow1);
        assertEquals(testWindow1Length + "bytes read after caching it", testWindow1Length,
                cache.read(0, 0, buffer1));
        assertArrayValue(buffer1.array(), VALUE1, testWindow1Length);


        ByteBuffer buffer2 = ByteBuffer.wrap(new byte[testData2Length]);
        assertEquals("no bytes read when nothing cached", 0,
                cache.read(testWindow1Length, 0, buffer2));
        cache.addWindow(testWindow2);
        assertEquals(testWindow2Length + " bytes read after caching it", testWindow2Length,
                cache.read(testWindow1Length, 0, buffer2));
        assertArrayValue(buffer2.array(), VALUE2, testWindow2Length);
    }

    @Test
    public void testSubscription() throws Exception {

        final List<Window> evictedWindows = new ArrayList<Window>();
        WindowCache.WindowObserver observer = new WindowCache.WindowObserver() {
            @Override
            public void windowFree(Window window, WindowCache fromCache) throws IOException {
                evictedWindows.add(window);
            }
        };

        cache.subscribe(observer);
        Window toEvict = addWindow(cache, 0, 4096);
        assertEquals(0, evictedWindows.size());

        addWindow(cache, 4096, 4096);
        assertEquals(0, evictedWindows.size());

        addWindow(cache, 8192, 4096);
        assertEquals(0, evictedWindows.size());

        addWindow(cache, 12288, 4096);
        assertEquals(toEvict, evictedWindows.get(0));

        addWindow(cache, 16536, 4096);
        assertEquals(2, evictedWindows.size());

        cache.unsubscribe(observer);
        addWindow(cache, 20632, 4096);
        assertEquals(2, evictedWindows.size());
    }

    private Window addWindow(final WindowCache cache, final long windowPosition, int length) throws IOException {
        Window window = new HardWindow(data1, windowPosition, length);
        cache.addWindow(window);
        return window;
    }

    @Test
    public void testEvictLeastRecentlyAdded() throws Exception {
        for (int capacity = 1; capacity <= 10; capacity++) {
            testEvictLeastRecentlyAdded(capacity);
        }
    }

    private void testEvictLeastRecentlyAdded(int capacity) throws Exception {

        // Create a cache with the right capacity:
        cache = new LeastRecentlyAddedCache(capacity);

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
            Window win = new HardWindow(data1, winPos, 4096);
            cache.addWindow(win);
        }
        assertEquals("No windows yet evicted", 0, evictedWindows.size());

        // Access the windows added in reverse order - this should make no difference to least recently added cache:
        for (int addNum = capacity; addNum > 0; addNum--) {
            final long winPos = (addNum - 1) * 4096;
            assertNotNull(cache.getWindow(winPos));
        }

        // Add windows going over the capacity:
        for (int moreNum = capacity + 1; moreNum < capacity * 2; moreNum++) {
            final long winPos = (moreNum - 1) * 4096;
            Window win = new HardWindow(data1, winPos, 4096);
            cache.addWindow(win);

            assertEquals("Num windows evicted correct:", moreNum - capacity, evictedWindows.size());
            Window ewin = evictedWindows.get(evictedWindows.size() - 1);
            final long ePos = (moreNum - capacity - 1) * 4096;
            assertEquals("Evicted window correct at position " + ePos, ePos, ewin.getWindowPosition());
        }

    }

    @Test
    public void testToString() throws Exception {
        String description = cache.toString();
        assertTrue(description.contains(cache.getClass().getSimpleName()));
        assertTrue(description.contains("capacity"));
    }

    private void assertArrayValue(final byte[] array, final byte value, final int length) {
        for (int i = 0; i < length; i++) {
            assertTrue(array[i] == value);
        }
    }
}