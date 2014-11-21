package net.byteseek.io.reader.cache;

import net.byteseek.io.reader.FileReader;
import net.byteseek.io.reader.InputStreamReader;
import net.byteseek.io.reader.Window;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class TopAndTailCacheTest {

    private static byte[] array = new byte[4096];

    private TopAndTailCache cache;
    private int topCacheSize;
    private int tailCacheSize;

    public TopAndTailCacheTest(Integer topCacheSize, Integer tailCacheSize) {
        this.topCacheSize = topCacheSize;
        this.tailCacheSize = tailCacheSize;
    }

    @Parameterized.Parameters
    public static Collection cacheSizes() {
        return Arrays.asList(new Object[][]{
                {4096, 4096},
                {4096, 0},
                {0, 4096},
                {128, 8192},
                {8192, 128}
        });
    }

    @Before
    public void setUp() {
        cache = new TopAndTailCache(topCacheSize, tailCacheSize);
    }

    @Test
    public void testGetNullWindows() throws Exception {
        assertNull(cache.getWindow(0));
        assertNull(cache.getWindow(4096));
        assertNull(cache.getWindow(-1));
        assertNull(cache.getWindow(1000000000));
    }


    @Test
    public void testWindowCachedCorrectly() throws Exception {
        final long[] testCases = new long[] {0, 4096, 8192, 32768};
        for (int count = 0; count < testCases.length; count++) {
            long position = testCases[count];
            addWindow(position);
            final long lengthSoFar = position + 4096;
            final Window existing = cache.getWindow(position);
            if (position < topCacheSize) {
                assertNotNull(existing);
                assertEquals(position, existing.getWindowPosition());
            } else if ( position + (existing == null? 0 : existing.length()) > lengthSoFar - tailCacheSize) {
                assertNotNull(existing);
                assertEquals(position, existing.getWindowPosition());
            } else {
                assertNull(existing);
            }
        }
    }

    @Test
    public void testSimulatedStreamReading() throws Exception {

    }

    @Test
    public void testClear() throws Exception {
        addWindow(0);
        addWindow(4096);
        cache.clear();
        assertNull(cache.getWindow(0));
        assertNull(cache.getWindow(4096));
    }

    private void addWindow(long position) {
        cache.addWindow(new Window(array, position, array.length));
    }
}