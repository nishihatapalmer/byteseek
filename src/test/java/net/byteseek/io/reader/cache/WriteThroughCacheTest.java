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
import java.util.Arrays;

import static org.junit.Assert.*;

public class WriteThroughCacheTest {

    private static long WINDOW1POS = 0;
    private static long WINDOW2POS = 4096;

    private AllWindowsCache cache1, cache2;
    private WriteThroughCache cache;
    private byte[] testData;
    private Window testWindow1, testWindow2;

    @Before
    public void setupTest() {
        cache1 = new AllWindowsCache();
        cache2 = new AllWindowsCache();
        cache = new WriteThroughCache(cache1, cache2);
        testData = new byte[4096];
        Arrays.fill(testData, (byte) 0x81);
        testWindow1 = new HardWindow(testData, WINDOW1POS, 4096);
        testWindow2 = new HardWindow(testData, WINDOW2POS, 4096);
    }

    @After
    public void closeDownTest() {
        try {
            cache.clear();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    // Test construction

    @Test(expected=IllegalArgumentException.class)
    public void testNullPrimaryCache() {
        new WriteThroughCache(null, new NoCache());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNullSecondaryCache() {
        new WriteThroughCache(new NoCache(), null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNullCache() {
        new WriteThroughCache(null, null);
    }

    // Test cache methods


    @Test
    public void testAddGetWindow() throws Exception {
        //No windows at any position:
        assertNull(cache.getWindow(WINDOW1POS));
        assertNull(cache.getWindow(WINDOW2POS));

        cache.addWindow(testWindow1);
        assertNotNull(cache1.getWindow(WINDOW1POS));
        assertNull(cache1.getWindow(WINDOW2POS));
        assertNotNull(cache2.getWindow(WINDOW1POS));
        assertNull(cache2.getWindow(WINDOW2POS));
        assertNotNull(cache.getWindow(WINDOW1POS));
        assertNull(cache.getWindow(WINDOW2POS));

        cache.addWindow(testWindow2);
        assertNotNull(cache1.getWindow(WINDOW1POS));
        assertNotNull(cache1.getWindow(WINDOW2POS));
        assertNotNull(cache2.getWindow(WINDOW1POS));
        assertNotNull(cache2.getWindow(WINDOW2POS));
        assertNotNull(cache.getWindow(WINDOW1POS));
        assertNotNull(cache.getWindow(WINDOW2POS));
    }

    @Test
    public void testClear() throws Exception {
        //No windows at any position:
        assertNull(cache.getWindow(WINDOW1POS));
        assertNull(cache.getWindow(WINDOW2POS));

        cache.addWindow(testWindow2);
        cache.addWindow(testWindow1);
        assertNotNull(cache1.getWindow(WINDOW1POS));
        assertNotNull(cache1.getWindow(WINDOW2POS));
        assertNotNull(cache2.getWindow(WINDOW1POS));
        assertNotNull(cache2.getWindow(WINDOW2POS));
        assertNotNull(cache.getWindow(WINDOW1POS));
        assertNotNull(cache.getWindow(WINDOW2POS));

        cache.clear();
        //No windows at any position:
        assertNull(cache.getWindow(WINDOW1POS));
        assertNull(cache.getWindow(WINDOW2POS));
    }

    @Test
    public void testGetNotInMemoryCache() throws Exception {
        cache.addWindow(testWindow1);

        cache1.clear();
        assertNull(cache1.getWindow(WINDOW1POS));

        Window w1 = cache2.getWindow(WINDOW1POS);
        assertNotNull(w1);

        Window w2 = cache.getWindow(WINDOW1POS);
        assertNotNull(w2);
        assertEquals(w1, w2);

        // should have added the window back into cache1 now it's been obtained from cache2:
        Window w3 = cache1.getWindow(WINDOW1POS);
        assertEquals(w3, w1);
    }


    @Test
    public void testReadFromCache() throws Exception {
        byte[] array = new byte[4096];

        // Nothing is read:
        assertEquals( 0, cache.read(1, 0, array, 0));
        assertEquals( 0, cache.read(0, 0, array, 0));

        // Add a window:
        cache.addWindow(testWindow1);
        assertEquals( 0, cache.read(1, 0, array, 0));
        assertEquals( 4096, cache.read(0, 0, array, 0));
        assertArrayEquals(array, testData);

        // Read offset into 100
        assertEquals(4000, cache.read(0, 0, array, 96));

        cache.clear();
        // Nothing is read:
        assertEquals( 0, cache.read(1, 0, array, 0));
        assertEquals( 0, cache.read(0, 0, array, 0));
    }

    @Test
    public void testReadBufferFromCache() throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[4096]);

        // Nothing is read:
        assertEquals( 0, cache.read(1, 0, buffer));
        assertEquals( 0, cache.read(0, 0, buffer));

        // Add a window:
        cache.addWindow(testWindow1);
        assertEquals( 0, cache.read(1, 0, buffer));
        assertEquals( 4096, cache.read(0, 0, buffer));
        assertArrayEquals(buffer.array(), testData);

        cache.clear();
        // Nothing is read:
        assertEquals( 0, cache.read(1, 0, buffer));
        assertEquals( 0, cache.read(0, 0, buffer));
    }

    @Test
    public void testReadNotInMemoryCache() throws Exception {
        byte[] array = new byte[4096];

        // Nothing is read:
        assertEquals( 0, cache.read(1, 0, array, 0));
        assertEquals( 0, cache.read(0, 0, array, 0));

        // Add a window:
        cache.addWindow(testWindow1);

        // Remove window from first cache (leaving it in persistent cache):
        cache1.clear();
        assertEquals( 0, cache1.read(1, 0, array, 0));
        assertEquals( 0, cache1.read(0, 0, array, 0));

        // Reading from cache still gets it from persistent cache:
        assertEquals( 0, cache.read(1, 0, array, 0));
        assertEquals( 4096, cache.read(0, 0, array, 0));
        assertArrayEquals(array, testData);

        // Read offset into 100
        assertEquals(4000, cache.read(0, 0, array, 96));

        cache.clear();
        // Nothing is read:
        assertEquals( 0, cache.read(1, 0, array, 0));
        assertEquals( 0, cache.read(0, 0, array, 0));
    }

    @Test
    public void testReadBufferNotInMemoryCache() throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[4096]);

        // Nothing is read:
        assertEquals( 0, cache.read(1, 0, buffer));
        assertEquals( 0, cache.read(0, 0, buffer));

        // Add a window:
        cache.addWindow(testWindow1);

        // Remove window from first cache (leaving it in persistent cache):
        cache1.clear();
        assertEquals( 0, cache1.read(1, 0, buffer));
        assertEquals( 0, cache1.read(0, 0, buffer));

        // Reading from cache still gets it from persistent cache:
        assertEquals( 0, cache.read(1, 0, buffer));
        assertEquals( 4096, cache.read(0, 0, buffer));
        assertArrayEquals(buffer.array(), testData);

        cache.clear();
        // Nothing is read:
        assertEquals( 0, cache.read(1, 0, buffer));
        assertEquals( 0, cache.read(0, 0, buffer));
    }

    @Test
    public void testGetMemoryCache() throws Exception {
        WindowCache mem = cache.getMemoryCache();
        assertEquals(mem, cache1);
    }

    @Test
    public void testGetPersistentCache() throws Exception {
        WindowCache pers = cache.getPersistentCache();
        assertEquals(pers, cache2);
    }

    // Test misc

    @Test
    public void testToString() throws Exception {
        WriteThroughCache cache = new WriteThroughCache(new NoCache(), new NoCache());
        assertTrue(cache.toString().contains(cache.getClass().getSimpleName()));
        assertTrue(cache.toString().contains("memory"));
        assertTrue(cache.toString().contains("persistent"));
    }


    @Test
    public void testClearMemWithException() throws Exception {
        WindowCache d = new AllWindowsCache();
        WriteThroughCache c = new WriteThroughCache(new ClearExceptionCache(), d);
        c.addWindow(testWindow1);
        assertNotNull(d.getWindow(WINDOW1POS));

        boolean OK = false;
        try {
            c.clear();
        } catch (IOException expected) {
            assertNull(d.getWindow(WINDOW1POS));
            OK = true;
        }
        assertTrue(OK);
    }

    private class ClearExceptionCache extends AbstractMemoryCache {

        @Override
        public Window getWindow(long position) throws IOException {
            return null;
        }

        @Override
        public void addWindow(Window window) throws IOException {

        }

        @Override
        public void clear() throws IOException {
            throw new IOException("Exception on clear");
        }
    }
}