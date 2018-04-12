package net.byteseek.io.reader.cache;

import net.byteseek.io.reader.windows.HardWindow;
import net.byteseek.io.reader.windows.Window;
import net.byteseek.io.reader.windows.WindowMissingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.*;

public class TempFileCacheTest {

    private byte[] data1, data2;
    private Window testWindow1, testWindow2;
    private TempFileCache tempFileCache;

    @Before
    public void setup() {
        data1 = new byte[4096];
        data2 = new byte[4096];
        byte value1 = 1;
        byte value2 = 2;
        testWindow1 = new HardWindow(data1,0, data1.length);
        Arrays.fill(data1, value1);
        Arrays.fill(data2, value2);
        testWindow2 = new HardWindow(data2, 4096, data2.length);
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
        assertEquals("Window has length " + data1.length, data1.length, window.length());
        assertArrayEquals(data1, window.getArray());

        assertNull("No window yet added at 4096", tempFileCache.getWindow(4096));
        tempFileCache.addWindow(testWindow2);
        window = tempFileCache.getWindow(4096);
        assertEquals("Window is at position 4096", 4096L, window.getWindowPosition());
        assertEquals("Window has length " + data2.length, data2.length, window.length());
        assertArrayEquals(data2, window.getArray());

        assertNotNull("Window still exists at position 0", tempFileCache.getWindow(0));
    }


    @Test
    public void testClear() throws Exception {
        // Add windows
        tempFileCache.addWindow(testWindow1);
        tempFileCache.addWindow(testWindow2);
        assertNotNull(tempFileCache.getWindow(0));
        assertNotNull(tempFileCache.getWindow(4096));
        File file = tempFileCache.getTempFile();
        assertNotNull("File is not null", file);

        // Clear cache, no more windows or temp file.
        tempFileCache.clear();
        assertNull("File is null after clearing", tempFileCache.getTempFile());
        assertNull(tempFileCache.getWindow(0));
        assertNull(tempFileCache.getWindow(4096));

        // Add windows again
        tempFileCache.addWindow(testWindow1);
        tempFileCache.addWindow(testWindow2);
        assertNotNull(tempFileCache.getWindow(0));
        assertNotNull(tempFileCache.getWindow(4096));
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
        assertArrayEquals(testWindow1.getArray(), bytes);

        tempFileCache.addWindow(testWindow2);
        bytes = tempFileCache.reloadWindowBytes(testWindow2);
        assertArrayEquals(testWindow2.getArray(), bytes);
    }

    @Test
    public void testRead() throws Exception {
        byte[] bytes = new byte[4096];

        assertEquals("no bytes read when nothing cached", 0, tempFileCache.read(0, 0, bytes, 0));
        tempFileCache.addWindow(testWindow1);
        assertEquals("4096 bytes read from pos 0 after caching it", 4096, tempFileCache.read(0, 0, bytes, 0));
        assertArrayEquals("byte arrays are the same", testWindow1.getArray(), bytes);

        assertEquals("no bytes read at 4096 when nothing cached", 0, tempFileCache.read(4096, 0, bytes, 0));
        tempFileCache.addWindow(testWindow2);
        assertEquals("4096 bytes read from pos 0 after caching it", 4096, tempFileCache.read(4096, 0, bytes, 0));
        assertArrayEquals("byte arrays are the same", testWindow2.getArray(), bytes);
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

    private File getFile(final String resourceName) {
        return new File(getFilePath(resourceName));
    }

    private String getFilePath(final String resourceName) {
        return this.getClass().getResource(resourceName).getPath();
    }

}