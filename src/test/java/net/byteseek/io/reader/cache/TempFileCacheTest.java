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
import net.byteseek.io.reader.windows.WindowMissingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class TempFileCacheTest {

    private final static byte VALUE1 = 1;
    private final static byte VALUE2 = 2;

    private byte[] data1, data2;
    private Window testWindow1, testWindow2;
    private int testWindow1Length, testWindow2Length;
    private int testData1Length, testData2Length;
    private TempFileCache tempFileCache;

    public TempFileCacheTest(Integer data1Length, Integer window1Length, Integer data2Length, Integer window2Length) {
        testData1Length = data1Length;
        testWindow1Length = window1Length;
        testData2Length = data2Length;
        testWindow2Length = window2Length;
    }

    @Parameterized.Parameters
    public static Collection lengths() {
        return Arrays.asList(new Object[][]{
                {4096, 4096, 4096, 4096},
                {4096, 4096, 4096, 543},
                {1024, 367, 789, 523},
                {4096, 4095, 4096, 1},
                {1024, 1023, 1023, 1022}
        });
    }

    @Before
    public void setup() {
        data1 = new byte[testData1Length];
        Arrays.fill(data1, VALUE1);
        testWindow1 = new HardWindow(data1,0, testWindow1Length);

        data2 = new byte[testData2Length];
        Arrays.fill(data2, VALUE2);
        testWindow2 = new HardWindow(data2, testWindow1Length, testWindow2Length);

        tempFileCache = new TempFileCache();
    }

    @After
    public void closeDown() throws IOException {
        tempFileCache.clear();
    }

    @Test
    public void testDirectoryNoException() {
        new TempFileCache(getFile("/")); // OK to instantiate with a directory
    }

    @Test
    public void testNullDirectoryOK() throws IOException {
        doSomeCacheOperations(new TempFileCache(null)); // should default to system temp file area and work OK.
    }

    @Test
    public void testNegativeCapacityOK() throws IOException {
        doSomeCacheOperations(new TempFileCache(null, -1024)); // should still work and pick a sensible capacity itself.
    }

    @Test
    public void testZeroCapacityOK() throws IOException {
        doSomeCacheOperations(new TempFileCache(null, 0)); // should still work and pick a sensible capacity itself.
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDirectoryNotAFileException() {
        new TempFileCache(getFile("/romeoandjuliet.txt")); // throw exception if it isn't a directory.
    }

    @Test
    public void testAddThenGetWindow() throws Exception {
        assertNull("No window yet added", tempFileCache.getWindow(0));
        tempFileCache.addWindow(testWindow1);
        Window window = tempFileCache.getWindow(0);
        assertEquals("Window is at position 0", 0L, window.getWindowPosition());
        assertEquals("Window has length " + testWindow1Length, testWindow1Length, window.length());
        assertArrayValue(window.getArray(), VALUE1);

        assertNull("No window yet added at " + testWindow1Length, tempFileCache.getWindow(testWindow1Length));
        tempFileCache.addWindow(testWindow2);
        window = tempFileCache.getWindow(testWindow1Length);
        assertEquals("Window is at position " + testWindow1Length, testWindow1Length, window.getWindowPosition());
        assertEquals("Window has length " + testWindow2Length, testWindow2Length, window.length());
        assertArrayValue(window.getArray(), VALUE2);

        assertNotNull("Window still exists at position 0", tempFileCache.getWindow(0));
    }

    @Test
    public void testGetNullWindows() throws Exception {
        assertNull(tempFileCache.getWindow(0));
        assertNull(tempFileCache.getWindow(4096));
        assertNull(tempFileCache.getWindow(-1));
        assertNull(tempFileCache.getWindow(1000000000));
    }

    @Test
    public void testClear() throws Exception {
        // Add windows
        tempFileCache.addWindow(testWindow1);
        tempFileCache.addWindow(testWindow2);
        assertNotNull(tempFileCache.getWindow(0));
        assertNotNull(tempFileCache.getWindow(testWindow1Length));
        File file = tempFileCache.getTempFile();
        assertNotNull("File is not null", file);

        // Clear cache, no more windows or temp file.
        tempFileCache.clear();
        assertNull("File is null after clearing", tempFileCache.getTempFile());
        assertNull(tempFileCache.getWindow(0));
        assertNull(tempFileCache.getWindow(testWindow1Length));

        // Add windows again
        tempFileCache.addWindow(testWindow1);
        tempFileCache.addWindow(testWindow2);
        assertNotNull(tempFileCache.getWindow(0));
        assertNotNull(tempFileCache.getWindow(testWindow1Length));
        file = tempFileCache.getTempFile();
        assertNotNull("File is not null", file);
    }

    @Test(expected=WindowMissingException.class)
    public void testWindowMissingIfNoCache() throws IOException {
        tempFileCache.reloadWindowBytes(testWindow1);
    }

    @Test(expected=WindowMissingException.class)
    public void testWindowMissingIfNoWindow() throws IOException {
        tempFileCache.addWindow(testWindow1);
        tempFileCache.reloadWindowBytes(testWindow2);
    }

    @Test
    public void testReloadWindowBytes() throws Exception {
        tempFileCache.addWindow(testWindow1);
        byte[] bytes = tempFileCache.reloadWindowBytes(testWindow1);
        assertArrayValue(testWindow1.getArray(), VALUE1);

        tempFileCache.addWindow(testWindow2);
        bytes = tempFileCache.reloadWindowBytes(testWindow2);
        assertArrayValue(testWindow2.getArray(), VALUE2);
    }

    @Test
    public void testRead() throws Exception {
        byte[] bytes1 = new byte[testData1Length];

        assertEquals("no bytes read when nothing cached", 0, tempFileCache.read(0, 0, bytes1, 0));
        tempFileCache.addWindow(testWindow1);
        assertEquals(testWindow1Length + "bytes read after caching it", testWindow1Length,
                      tempFileCache.read(0, 0, bytes1, 0));
        assertArrayValue(bytes1, VALUE1, testWindow1Length);


        byte[] bytes2 = new byte[testData2Length];
        assertEquals("no bytes read when nothing cached", 0,
                      tempFileCache.read(testWindow1Length, 0, bytes2, 0));
        tempFileCache.addWindow(testWindow2);
        assertEquals(testWindow2Length + " bytes read after caching it", testWindow2Length,
                tempFileCache.read(testWindow1Length, 0, bytes2, 0));
        assertArrayValue(bytes2, VALUE2, testWindow2Length);
    }

    @Test
    public void testReadByteBuffer() throws Exception {
        ByteBuffer buffer1 = ByteBuffer.wrap(new byte[testData1Length]);

        assertEquals("no bytes read when nothing cached", 0,
                tempFileCache.read(0, 0, buffer1));
        tempFileCache.addWindow(testWindow1);
        assertEquals(testWindow1Length + "bytes read after caching it", testWindow1Length,
                      tempFileCache.read(0, 0, buffer1));
        assertArrayValue(buffer1.array(), VALUE1, testWindow1Length);


        ByteBuffer buffer2 = ByteBuffer.wrap(new byte[testData2Length]);
        assertEquals("no bytes read when nothing cached", 0,
                     tempFileCache.read(testWindow1Length, 0, buffer2));
        tempFileCache.addWindow(testWindow2);
        assertEquals(testWindow2Length + " bytes read after caching it", testWindow2Length,
                      tempFileCache.read(testWindow1Length, 0, buffer2));
        assertArrayValue(buffer2.array(), VALUE2, testWindow2Length);
    }

    @Test
    public void testToString() throws Exception {
        assertTrue(tempFileCache.toString().contains(tempFileCache.getClass().getSimpleName()));
    }

    private void doSomeCacheOperations(TempFileCache cache) throws IOException {
        cache.addWindow(testWindow1);
        cache.addWindow(testWindow2);
        cache.clear();
    }

    private void assertArrayValue(final byte[] array, final byte value) {
        assertArrayValue(array, value, array.length);
    }

    private void assertArrayValue(final byte[] array, final byte value, final int length) {
        for (int i = 0; i < length; i++) {
            assertTrue(array[i] == value);
        }
    }

    private File getFile(final String resourceName) {
        return new File(getFilePath(resourceName));
    }

    private String getFilePath(final String resourceName) {
        return this.getClass().getResource(resourceName).getPath();
    }

}