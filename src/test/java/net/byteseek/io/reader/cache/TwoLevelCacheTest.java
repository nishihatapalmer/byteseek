/*
 * Copyright Matt Palmer 2017-18, All rights reserved.
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
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.junit.Assert.*;

public class TwoLevelCacheTest {



    @Test(expected=IllegalArgumentException.class)
    public void testCreateNullCaches() throws Exception {
        new TwoLevelCache(null, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCreateNullPrimaryCache() throws Exception {
        new TwoLevelCache(null, new NoCache());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCreateNullSecondaryCache() throws Exception {
        new TwoLevelCache(new NoCache(),null);
    }

    @Test
    public void testCreateNoPrimarySecondaryOK() throws Exception {
        new TwoLevelCache(new NoCache(),new NoCache());
    }

    @Test
    public void testTwoLevelCacheEviction() throws Exception {
        // Set up two level cache with NoCache primary and secondary, and subscribe to it:
        WindowCache cache = new TwoLevelCache(new NoCache(),new NoCache());
        LastWindowObserver observer = new LastWindowObserver();
        cache.subscribe(observer);

        Window window = createWindow(0L, 1024, (byte) 4);
        cache.addWindow(window);

        // Window should no longer be in TwoLevelCache, and should have been evicted from it to our observer:
        assertNull(cache.getWindow(window.getWindowPosition()));
        assertNotNull(observer.cacheFrom);
        assertEquals(cache, observer.cacheFrom);
        assertNotNull(observer.evictedWindow);
        assertEquals(window.getWindowPosition(), observer.evictedWindow.getWindowPosition());
        assertArrayEquals(window.getArray(), observer.evictedWindow.getArray());
    }

    @Test
    public void testAddGetPrimaryWindow() throws Exception {
        WindowCache cache = new TwoLevelCache(new AllWindowsCache(), new NoCache());
        Window window = createWindow(0L, 1024, (byte) 6);

        assertNull(cache.getWindow(0));
        cache.addWindow(window);
        Window cachedWindow = cache.getWindow(0);
        assertNotNull(cachedWindow);
        assertEquals(window.getWindowPosition(), cachedWindow.getWindowPosition());
        assertEquals(window.length(), cachedWindow.length());
        assertArrayEquals(window.getArray(), cachedWindow.getArray());
    }

    @Test
    public void testAddGetSecondaryWindow() throws Exception {
        WindowCache cache = new TwoLevelCache(new NoCache(), new AllWindowsCache());
        Window window = createWindow(0L, 1024, (byte) 6);

        assertNull(cache.getWindow(0));
        cache.addWindow(window);
        Window cachedWindow = cache.getWindow(0);
        assertNotNull(cachedWindow);
        assertEquals(window.getWindowPosition(), cachedWindow.getWindowPosition());
        assertEquals(window.length(), cachedWindow.length());
        assertArrayEquals(window.getArray(), cachedWindow.getArray());
    }

    @Test
    public void testGetNullWindows() throws Exception {
        WindowCache cache = new TwoLevelCache(new AllWindowsCache(), new NoCache());
        assertNull(cache.getWindow(0));
        assertNull(cache.getWindow(4096));
        assertNull(cache.getWindow(-1));
        assertNull(cache.getWindow(1000000000));
    }

    @Test
    public void testRead() throws Exception {
        WindowCache cache1 = new LeastRecentlyAddedCache(1);
        WindowCache cache2 = new AllWindowsCache();
        WindowCache cache = new TwoLevelCache(cache1, cache2);
        byte[] array = new byte[4096];

        Window window1 = createWindow(0, 4096, (byte) 81);
        cache.addWindow(window1);
        assertEquals(4096, cache1.read(0, 0, array, 0));
        assertEquals(0, cache2.read(0, 0, array, 0));
        assertEquals(4096, cache.read(0, 0, array, 0));

        Window window2 = createWindow( 4096, 4096, (byte) 22);
        cache.addWindow(window2);

        assertEquals(0, cache1.read(0, 0, array, 0));
        assertEquals(4096, cache1.read(4096, 0, array, 0));

        assertEquals(4096, cache2.read(0, 0, array, 0));
        assertEquals(0, cache2.read(4096, 0, array, 0));

        assertEquals(4096, cache.read(0, 0, array, 0));
    }

    @Test
    public void testReadBuffer() throws Exception {
        WindowCache cache1 = new LeastRecentlyAddedCache(1);
        WindowCache cache2 = new AllWindowsCache();
        WindowCache cache = new TwoLevelCache(cache1, cache2);

        Window window1 = createWindow(0, 4096, (byte) 81);
        cache.addWindow(window1);

        ByteBuffer buffer = ByteBuffer.wrap(new byte[4096]);
        assertEquals(4096, cache1.read(0, 0, buffer));

        buffer = ByteBuffer.wrap(new byte[4096]);
        assertEquals(0, cache2.read(0, 0, buffer));
        assertEquals(4096, cache.read(0, 0, buffer));

        Window window2 = createWindow( 4096, 4096, (byte) 22);
        cache.addWindow(window2);

        buffer = ByteBuffer.wrap(new byte[4096]);
        assertEquals(0, cache1.read(0, 0, buffer));
        assertEquals(4096, cache1.read(4096, 0, buffer));

        buffer = ByteBuffer.wrap(new byte[4096]);
        assertEquals(4096, cache2.read(0, 0, buffer));
        buffer = ByteBuffer.wrap(new byte[4096]);
        assertEquals(0, cache2.read(4096, 0, buffer));

        assertEquals(4096, cache.read(0, 0, buffer));
    }

    @Test
    public void testClearPrimary() throws Exception {
        WindowCache cache = new TwoLevelCache(new AllWindowsCache(), new NoCache());
        final long winPos = 4096;

        assertNull(cache.getWindow(winPos));
        Window window = createWindow(winPos, 4096, (byte) 127);
        cache.addWindow(window);

        Window cachedWindow = cache.getWindow(winPos);
        assertNotNull(cachedWindow);

        cache.clear();
        assertNull(cache.getWindow(winPos));
    }

    @Test
    public void testClearSecondary() throws Exception {
        WindowCache cache = new TwoLevelCache(new NoCache(), new AllWindowsCache());
        final long winPos = 4096;

        assertNull(cache.getWindow(winPos));
        Window window = createWindow(winPos, 4096, (byte) 127);
        cache.addWindow(window);

        Window cachedWindow = cache.getWindow(winPos);
        assertNotNull(cachedWindow);

        cache.clear();
        assertNull(cache.getWindow(winPos));
    }

    @Test
    public void testClearPrimaryAndSecondary() throws Exception {
        WindowCache cache = new TwoLevelCache(new LeastRecentlyAddedCache(1), new AllWindowsCache());

        Window window1 = createWindow(0, 1024, (byte)63);
        assertNull(cache.getWindow(0));
        Window window2 = createWindow(1024, 1024, (byte) 57);
        assertNull(cache.getWindow(1024));

        cache.addWindow(window1); // window1 is in primary cache.
        cache.addWindow(window2); // window1 evicted to secondary cache, window2 in primary cache.

        Window cachedWindow = cache.getWindow(0); // fetches window1 from secondary cache, adds it back to primary cache.
        assertNotNull(cachedWindow);

        cache.clear();
        assertNull(cache.getWindow(0)); // this window was in both primary and secondary caches, but is cleared.
        assertNull(cache.getWindow(1024)); // this window was now in the secondary, but is also cleared.
    }

    @Test
    public void testGetPrimaryCache() throws Exception {
        WindowCache cache = new NoCache();
        TwoLevelCache twol = new TwoLevelCache(cache, new NoCache());
        assertEquals(cache, twol.getPrimaryCache());
    }

    @Test
    public void testGetSecondaryCache() throws Exception {
        WindowCache cache = new NoCache();
        TwoLevelCache twol = new TwoLevelCache(new NoCache(), cache);
        assertEquals(cache, twol.getSecondaryCache());
    }

    @Test
    public void testToString() throws Exception {
        String twol = new TwoLevelCache(new NoCache(), new NoCache()).toString();
        assertTrue(twol.contains(TwoLevelCache.class.getSimpleName()));
        assertTrue(twol.contains("primary"));
        assertTrue(twol.contains("secondary"));
    }

    private Window createWindow(long position, int length, byte value) {
        byte[] array = new byte[length];
        Arrays.fill(array, value);
        return new HardWindow(array, position, length);
    }

    private static class LastWindowObserver implements WindowCache.WindowObserver {
        public WindowCache cacheFrom;
        public Window evictedWindow;
        @Override
        public void windowFree(Window window, WindowCache fromCache) throws IOException {
            evictedWindow = window;
            cacheFrom = fromCache;
        }
    }

}