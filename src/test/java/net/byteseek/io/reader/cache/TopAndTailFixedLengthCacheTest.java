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

public class TopAndTailFixedLengthCacheTest {

    @Test
    public void testWindowCrossesTailStart() throws Exception {
        testShouldCache(100, 20, 20, 61);
    }

    @Test
    public void testWindowNotCrossingTailStart() throws Exception {
        testShouldNotCache(100, 20, 20, 60);
    }

    @Test
    public void testWindowCrossesTopEnd() throws Exception {
        testShouldCache(100, 20, 20, 19);
    }

    @Test
    public void testWindowNotCrossingTopEnd() throws Exception {
        testShouldNotCache(100, 20, 20, 20);
    }

    @Test
    public void testWindowAlwaysCached() throws Exception {
        final int readerLength = 100;
        final int cacheSize    = 50;
        final int windowSize   = 20;
        for (int startPos = 0; startPos < readerLength; startPos++) {
            testShouldCache(readerLength, cacheSize, windowSize, startPos);
        }
    }

    @Test
    public void testNoCaching() throws Exception {
        final int readerLength = 100;
        final int cacheSize    = 0;
        final int windowSize   = 1;
        for (int startPos = 0; startPos < readerLength; startPos++)  {
            testShouldNotCache(readerLength, cacheSize, windowSize, startPos);
        }
    }

    @Test
    public void testClear() throws Exception {
        TopAndTailFixedLengthCache cache = new TopAndTailFixedLengthCache(100, 20);
        addWindow(cache, 10, 0);
        addWindow(cache, 10, 90);
        assertNotNull(cache.getWindow(0));
        assertNull(cache.getWindow(45));
        assertNotNull(cache.getWindow(90));
        cache.clear();
        assertNull(cache.getWindow(0));
        assertNull(cache.getWindow(45));
        assertNull(cache.getWindow(90));
    }

    private void testShouldCache(long readerLength, int bytesToCacheOnEnds, int windowLength, long windowPos) throws Exception {
        TopAndTailFixedLengthCache cache = new TopAndTailFixedLengthCache(readerLength, bytesToCacheOnEnds);
        addWindow(cache, windowLength, windowPos);
        assertNotNull("Window should be cached.", cache.getWindow(windowPos));
    }

    private void testShouldNotCache(long readerLength, int bytesToCacheOnEnds, int windowLength, long windowPos) throws Exception {
        TopAndTailFixedLengthCache cache = new TopAndTailFixedLengthCache(readerLength, bytesToCacheOnEnds);
        addWindow(cache, windowLength, windowPos);
        assertNull("Window should not be cached", cache.getWindow(windowPos));
    }

    private void addWindow(WindowCache cache, int windowLength, long windowPos) throws Exception {
        Window window = new HardWindow(new byte[windowLength], windowPos, windowLength);
        cache.addWindow(window);
    }




}