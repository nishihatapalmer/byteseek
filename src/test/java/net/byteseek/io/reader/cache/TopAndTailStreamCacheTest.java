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

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.fail;

public class TopAndTailStreamCacheTest {

    private static int WINDOW_SIZE = 10;
    private static int TOP_CACHE_SIZE = 100;
    private static int TAIL_CACHE_SIZE = 100;

    @Test
    public void testZeroCaching() throws Exception {
        TopAndTailStreamCache cache = new TopAndTailStreamCache(0, WINDOW_SIZE);
        testShouldNotCache(cache, 0);
    }

    @Test
    public void testTopCaching() throws Exception {
        TopAndTailStreamCache cache = new TopAndTailStreamCache(TOP_CACHE_SIZE, 0, WINDOW_SIZE);

        // Test all windows up to the top cache size get cached.
        for (int position = 0; position < TOP_CACHE_SIZE; position += WINDOW_SIZE) {
            testShouldCache(cache, position);
        }

        // Any position after or at the top cache size should not be cached.
        for (int position = TOP_CACHE_SIZE; position < TOP_CACHE_SIZE * 2; position += WINDOW_SIZE) {
            testShouldNotCache(cache, position);
        }

        // Validate that the original top cache are still there:
        for (int position = 0; position < TOP_CACHE_SIZE; position += WINDOW_SIZE) {
            assertNotNull(cache.getWindow(position));
        }
    }

    @Test
    public void testTailCaching() throws Exception {
        TopAndTailStreamCache cache = new TopAndTailStreamCache(TOP_CACHE_SIZE, TAIL_CACHE_SIZE, WINDOW_SIZE);

        // Test all windows up to the top cache size get cached.
        for (int position = 0; position < TOP_CACHE_SIZE; position += WINDOW_SIZE) {
            testShouldCache(cache, position);
        }

        // Any position up to the top cache size plus the tail cache size should also be cached:
        for (int position = TOP_CACHE_SIZE; position < TOP_CACHE_SIZE + TAIL_CACHE_SIZE; position += WINDOW_SIZE) {
            testShouldCache(cache, position);
        }

        // Test adding a window past both top and tail cache causes a window to be evicted at the boundary.
        assertNotNull(cache.getWindow(TOP_CACHE_SIZE));
        testShouldCache(cache, TOP_CACHE_SIZE + TAIL_CACHE_SIZE);
        assertNull(cache.getWindow(TOP_CACHE_SIZE));

        // Validate that the original top cache are still there:
        for (int position = 0; position < TOP_CACHE_SIZE; position += WINDOW_SIZE) {
            assertNotNull(cache.getWindow(position));
        }

        // Validate that the remaining tail cache entries are still there:
        for (int position = TOP_CACHE_SIZE + WINDOW_SIZE; position <= TOP_CACHE_SIZE + TAIL_CACHE_SIZE; position += WINDOW_SIZE) {
            assertNotNull(cache.getWindow(position));
        }

        // Test adding another window causes a window to be evicted at the boundary.
        assertNotNull(cache.getWindow(TOP_CACHE_SIZE + WINDOW_SIZE));
        testShouldCache(cache, TOP_CACHE_SIZE + TAIL_CACHE_SIZE + WINDOW_SIZE);
        assertNull(cache.getWindow(TOP_CACHE_SIZE + WINDOW_SIZE));
    }


    @Test
    public void testClear() throws Exception {
        TopAndTailStreamCache cache = new TopAndTailStreamCache(TOP_CACHE_SIZE, 0, WINDOW_SIZE);

        // Test all windows up to the top cache size get cached.
        for (int position = 0; position < TOP_CACHE_SIZE; position += WINDOW_SIZE) {
            testShouldCache(cache, position);
        }

        // Validate that the original top cache are still there:
        for (int position = 0; position < TOP_CACHE_SIZE; position += WINDOW_SIZE) {
            assertNotNull(cache.getWindow(position));
        }

        cache.clear();

        // Validate that the original top cache are no longer there:
        for (int position = 0; position < TOP_CACHE_SIZE; position += WINDOW_SIZE) {
            assertNull(cache.getWindow(position));
        }
    }

    private void testShouldCache(WindowCache cache, long windowPos) throws Exception {
        addWindow(cache, windowPos);
        assertNotNull("Window should be cached.", cache.getWindow(windowPos));
    }

    private void testShouldNotCache(WindowCache cache, long windowPos) throws Exception {
        addWindow(cache, windowPos);
        assertNull("Window should not be cached", cache.getWindow(windowPos));
    }

    private void addWindow(WindowCache cache,long windowPos) throws Exception {
        Window window = new HardWindow(new byte[WINDOW_SIZE], windowPos, WINDOW_SIZE);
        cache.addWindow(window);
    }

    //TODO: test read array and buffer


}