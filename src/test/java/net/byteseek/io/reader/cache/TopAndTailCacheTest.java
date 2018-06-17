/*
 * Copyright Matt Palmer 2015, All rights reserved.
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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
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
        return Arrays.asList(new Object[][]{ // Parameters are top cache size and tail cache size.
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
    public void testSingleValueConstructor() {
        TopAndTailCache c2 = new TopAndTailCache(topCacheSize);
        assertEquals("top cache size", topCacheSize, c2.getTopCacheSize());
        assertEquals("tail cache size", topCacheSize, c2.getTailCacheSize());
    }

    @Test
    public void testCacheSizeCorrect() {
        assertEquals("top cache size", topCacheSize, cache.getTopCacheSize());
        assertEquals("tail cache size", tailCacheSize, cache.getTailCacheSize());
    }

    @Test
    public void testGetNullWindows() throws Exception {
        assertNull(cache.getWindow(0));
        assertNull(cache.getWindow(4096));
        assertNull(cache.getWindow(-1));
        assertNull(cache.getWindow(1000000000));
    }

    //TODO: test read()

    @Test
    public void testWindowCachedCorrectlyInOrder() throws Exception {
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
        //TODO: test reading in order and random access.h
    }

    @Test
    public void testClear() throws Exception {
        addWindow(0);
        addWindow(4096);
        cache.clear();
        assertNull(cache.getWindow(0));
        assertNull(cache.getWindow(4096));
    }

    private void addWindow(long position) throws IOException {
        cache.addWindow(new HardWindow(array, position, array.length));
    }


}