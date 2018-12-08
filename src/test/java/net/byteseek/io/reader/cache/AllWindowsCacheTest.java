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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.junit.Assert.*;

public class AllWindowsCacheTest {

    private static long WINDOW1POS = 0;
    private static long WINDOW2POS = 4096;
    private final static byte VALUE1 = 1;
    private final static byte VALUE2 = 2;

    private AllWindowsCache cache;
    private byte[] data1, data2;
    private Window testWindow1, testWindow2;
    private int testWindow1Length = 4096;
    private int testWindow2Length = 4096;
    private int testData1Length   = 4096;
    private int testData2Length   = 4096;

    @Before
    public void setupTest() {
        cache = new AllWindowsCache();
        data1 = new byte[testData1Length];
        Arrays.fill(data1, VALUE1);
        data2 = new byte[testData2Length];
        Arrays.fill(data2, VALUE2);
        testWindow1 = new HardWindow(data1, WINDOW1POS, testWindow1Length);
        testWindow2 = new HardWindow(data2, WINDOW2POS, testWindow2Length);
    }

    @After
    public void close() {
        cache.clear();
    }

    @Test
    public void constructWithPositiveCapacitiesNoException() {
        for (int i = 1; i < 100; i++) {
            AllWindowsCache cache = new AllWindowsCache(i);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeCapacity() {
        AllWindowsCache cache = new AllWindowsCache(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroCapacity() {
        AllWindowsCache cache = new AllWindowsCache(0);
    }

    @Test
    public void testGetNullWindows() throws Exception {
        assertNull(cache.getWindow(0));
        assertNull(cache.getWindow(4096));
        assertNull(cache.getWindow(-1));
        assertNull(cache.getWindow(1000000000));
    }

    @Test
    public void testGetAddWindow() throws Exception {
        assertNull(cache.getWindow(0));
        assertNull(cache.getWindow(1024));
        assertNull(cache.getWindow(-1));
        assertNull(cache.getWindow(1));
        assertNull(cache.getWindow(1023));
        assertNull(cache.getWindow(1025));

        cache.addWindow(testWindow1);
        assertNotNull(cache.getWindow(WINDOW1POS));
        assertNull(cache.getWindow(WINDOW2POS));
        assertNull(cache.getWindow(-1));
        assertNull(cache.getWindow(1));
        assertNull(cache.getWindow(1023));
        assertNull(cache.getWindow(1025));

        cache.addWindow(testWindow2);
        assertNotNull(cache.getWindow(WINDOW1POS));
        assertNotNull(cache.getWindow(WINDOW2POS));
        assertNull(cache.getWindow(-1));
        assertNull(cache.getWindow(1));
        assertNull(cache.getWindow(1023));
        assertNull(cache.getWindow(1025));
    }

    @Test
    public void testCachesAllWindows() throws Exception {
        final int[] freeCount = new int[1];
        WindowCache.WindowObserver observer = new WindowCache.WindowObserver() {
            @Override
            public void windowFree(Window window, WindowCache fromCache) throws IOException {
                freeCount[0] = freeCount[0] + 1;
            }
        };
        cache.subscribe(observer);
        for (int i = 0; i < 10000; i++) {
            Window window = new HardWindow(data1, i, 1);
            cache.addWindow(window);
        }
        assertEquals(0, freeCount[0]);
        for (int i = 0; i < 10000; i++) {
            assertNotNull(cache.getWindow(i));
        }
    }

    @Test
    public void testClear() throws Exception {
        cache.addWindow(testWindow1);
        cache.addWindow(testWindow2);
        assertNotNull(cache.getWindow(WINDOW1POS));
        assertNotNull(cache.getWindow(WINDOW2POS));
        assertNull(cache.getWindow(-1));
        assertNull(cache.getWindow(1));
        assertNull(cache.getWindow(1023));
        assertNull(cache.getWindow(1025));
        cache.clear();
        assertNull(cache.getWindow(0));
        assertNull(cache.getWindow(1024));
        assertNull(cache.getWindow(-1));
        assertNull(cache.getWindow(1));
        assertNull(cache.getWindow(1023));
        assertNull(cache.getWindow(1025));
    }

    @Test
    public void testToString() throws Exception {
        assertTrue(cache.toString().contains(cache.getClass().getSimpleName()));
        assertTrue(cache.toString().contains("size"));
        assertTrue(cache.toString().contains("capacity"));
    }

    @Test
    public void testRead() throws Exception {
        cache         = new AllWindowsCache(5);

        byte[] bytes1 = new byte[testData1Length];
        assertEquals("no bytes read when nothing cached", 0,
                cache.read(0, 0, bytes1, 0));
        cache.addWindow(testWindow1);
        assertEquals(testWindow1Length + "bytes read after caching it", testWindow1Length,
                cache.read(0, 0, bytes1, 0));
        assertArrayValue(bytes1, VALUE1, 0, testWindow1Length);


        byte[] bytes2 = new byte[testData2Length];
        assertEquals("no bytes read when nothing cached", 0,
                cache.read(testWindow1Length, 0, bytes2, 0));
        cache.addWindow(testWindow2);
        assertEquals(testWindow2Length + " bytes read after caching it", testWindow2Length,
                cache.read(testWindow1Length, 0, bytes2, 0));
        assertArrayValue(bytes2, VALUE2, 0, testWindow2Length);

        // Read halfway through the first window
        assertEquals(testData1Length, cache.read(0, testWindow1Length / 2, bytes1, 0));
        assertArrayValue(bytes1, VALUE1, 0, testWindow1Length / 2);
        assertArrayValue(bytes1, VALUE2, testWindow1Length / 2, testWindow1Length / 2);
    }

    @Test
    public void testReadByteBuffer() throws Exception {
        ByteBuffer buffer1 = ByteBuffer.wrap(new byte[testData1Length]);
        cache         = new AllWindowsCache(5);

        assertEquals("no bytes read when nothing cached", 0,
                cache.read(0, 0, buffer1));
        cache.addWindow(testWindow1);
        assertEquals(testWindow1Length + "bytes read after caching it", testWindow1Length,
                cache.read(0, 0, buffer1));
        assertArrayValue(buffer1.array(), VALUE1, 0, testWindow1Length);


        ByteBuffer buffer2 = ByteBuffer.wrap(new byte[testData2Length]);
        assertEquals("no bytes read when nothing cached", 0,
                cache.read(testWindow1Length, 0, buffer2));
        cache.addWindow(testWindow2);
        assertEquals(testWindow2Length + " bytes read after caching it", testWindow2Length,
                cache.read(testWindow1Length, 0, buffer2));
        assertArrayValue(buffer2.array(), VALUE2, 0, testWindow2Length);

        // Read halfway through the first window
        ByteBuffer buffer3 = ByteBuffer.wrap(new byte[testData1Length]);
        assertEquals(testData1Length, cache.read(0, testWindow1Length / 2, buffer3));
        assertArrayValue(buffer3.array(), VALUE1, 0, testWindow1Length / 2);
        assertArrayValue(buffer3.array(), VALUE2, testWindow1Length / 2, testWindow1Length / 2);
    }


    private void assertArrayValue(final byte[] array, final byte value, final int offset, final int length) {
        for (int i = offset; i < length; i++) {
            assertTrue(array[i] == value);
        }
    }
}