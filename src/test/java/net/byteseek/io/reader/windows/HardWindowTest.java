/*
 * Copyright Matt Palmer 2019, All rights reserved.
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
package net.byteseek.io.reader.windows;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class HardWindowTest {

    private Random random = new Random();
    private int arrayLength;
    private int windowLength;
    private long position;
    private byte[] data;
    private HardWindow window;

    public HardWindowTest(Integer arrayLength, Integer windowLength, Integer position) {
        this.arrayLength = arrayLength;
        this.windowLength = windowLength;
        this.position = position;
    }

    @Parameterized.Parameters
    public static Collection windowData() {
        return Arrays.asList(new Object[][]{
                {4096, 4096, 0},
                {4096, 1, 23},
                {2043, 2000, 0},
                {8192, 8191, 8192},
                {8192, 128, 3333}
        });
    }

    @Before
    public void setUp() {
        data = new byte[arrayLength];
        random.nextBytes(data);
        window = new HardWindow(data, position, windowLength);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testGetNegativeByte() {
        window.getByte(-1);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testGetTooLargePosByte() {
        window.getByte(arrayLength);
    }

    @Test
    public void testGetByte() throws Exception {
        for (int i = 0; i < arrayLength; i++) {
            Assert.assertEquals(data[i], window.getByte(i));
        }
    }

    @Test
    public void testGetArray() throws Exception {
        Assert.assertArrayEquals(data, window.getArray());
        Assert.assertEquals(data, window.getArray());
    }

    @Test
    public void testGetWindowPosition() throws Exception {
        Assert.assertEquals(position, window.getWindowPosition());
    }

    @Test
    public void testGetWindowEndPosition() throws Exception {
        Assert.assertEquals(position + windowLength - 1, window.getWindowEndPosition());
    }

    @Test
    public void testGetNextWindowPosition() throws Exception {
        Assert.assertEquals(position + windowLength, window.getNextWindowPosition());
    }

    @Test
    public void testLength() throws Exception {
        Assert.assertEquals(windowLength, window.length());
    }

    @Test
    public void testToString() throws Exception {
        Assert.assertTrue(window.toString().contains(HardWindow.class.getSimpleName()));
    }
}