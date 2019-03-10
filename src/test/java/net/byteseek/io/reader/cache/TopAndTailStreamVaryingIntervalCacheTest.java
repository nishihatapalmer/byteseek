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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.fail;

public class TopAndTailStreamVaryingIntervalCacheTest {

    private static int TOP_CACHE_SIZE = 100;
    private static int TAIL_CACHE_SIZE = 100;
    private static int WINDOW_SIZE     = 30;
    private Random random = new Random();

    @Test
    public void testZeroCaching() throws Exception {
        TopAndTailStreamVaryingIntervalCache cache = new TopAndTailStreamVaryingIntervalCache(0);
        int windowLength = 1000;
        for (int position = 0; position < 10000; position += windowLength) {
            testShouldNotCache(cache, WINDOW_SIZE, 0);
            windowLength = random.nextInt(1000) + 1;
        }
    }

    @Test
    public void testTopCaching() throws Exception {
        TopAndTailStreamVaryingIntervalCache cache = new TopAndTailStreamVaryingIntervalCache(TOP_CACHE_SIZE, 0);
        int windowLength = WINDOW_SIZE;
        List<Integer> positionsAdded = new ArrayList<Integer>();

        // Test all windows up to the top cache size get cached.
        int position = 0;
        while (position < TOP_CACHE_SIZE) {
            positionsAdded.add(position);
            testShouldCache(cache, windowLength, position);
            position += windowLength;
            windowLength = random.nextInt(WINDOW_SIZE) + 1;
        }

        // Any position after or at the top cache size should not be cached.
        while (position < TOP_CACHE_SIZE * 2) {
            testShouldNotCache(cache, windowLength, position);
            position += windowLength;
            windowLength = random.nextInt(WINDOW_SIZE) + 1;
        }

        // Validate that the original top cache are still there:
        for (Integer pos : positionsAdded) {
            assertNotNull(cache.getWindow(pos));
        }
    }

    @Test
    public void testTailCaching() throws Exception {
        TopAndTailStreamVaryingIntervalCache cache = new TopAndTailStreamVaryingIntervalCache(TOP_CACHE_SIZE, TAIL_CACHE_SIZE);
        int windowLength = WINDOW_SIZE;
        List<Integer> positionsAdded = new ArrayList<Integer>();
        int position = 0;

        // Test all windows up to the top cache size get cached.
        while (position < TOP_CACHE_SIZE) {
            positionsAdded.add(position);
            testShouldCache(cache, windowLength, position);
            position += windowLength;
            windowLength = random.nextInt(WINDOW_SIZE) + 1;
        }

        int startTailCacheIndex = positionsAdded.size();

        // Any position up to the top cache size plus the tail cache size should also be cached:
        position = TOP_CACHE_SIZE;
        while (position < TOP_CACHE_SIZE + TAIL_CACHE_SIZE) {
            testShouldCache(cache, windowLength, position);
            positionsAdded.add(position);
            position += windowLength;
            windowLength = random.nextInt(WINDOW_SIZE) + 1;
        }


        // Test adding windows past both top and tail cache causes a window to be evicted at the boundary.
        long finalLength = position;
        while (position < TOP_CACHE_SIZE + TAIL_CACHE_SIZE * 2) {
            testShouldCache(cache, windowLength, position);
            positionsAdded.add(position);
            finalLength += windowLength;
            position += windowLength;
            windowLength = random.nextInt(WINDOW_SIZE) + 1;
        }
        assertNull(cache.getWindow(positionsAdded.get(startTailCacheIndex)));

        // Validate that the top and tail cache are still there:
        for (Integer pos : positionsAdded) {
            if (pos < TOP_CACHE_SIZE || pos >= finalLength - TAIL_CACHE_SIZE) {
                assertNotNull(cache.getWindow(pos));
            }
        }
    }


    @Test
    public void testClear() throws Exception {
        TopAndTailStreamVaryingIntervalCache cache = new TopAndTailStreamVaryingIntervalCache(TOP_CACHE_SIZE, 0);

        // Test all windows up to the top cache size get cached.
        for (int position = 0; position < TOP_CACHE_SIZE; position += WINDOW_SIZE) {
            testShouldCache(cache, WINDOW_SIZE, position);
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

    //TODO: test read array and buffer


    private void testShouldCache(WindowCache cache, int windowLength, long windowPos) throws Exception {
        addWindow(cache, windowLength, windowPos);
        assertNotNull("Window should be cached.", cache.getWindow(windowPos));
    }

    private void testShouldNotCache(WindowCache cache, int windowLength, long windowPos) throws Exception {
        addWindow(cache, windowLength, windowPos);
        assertNull("Window should not be cached", cache.getWindow(windowPos));
    }

    private void addWindow(WindowCache cache, int windowLength, long windowPos) throws Exception {
        Window window = new HardWindow(new byte[windowLength], windowPos, windowLength);
        cache.addWindow(window);
    }



}