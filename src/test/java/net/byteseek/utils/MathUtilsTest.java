/*
 * Copyright Matt Palmer 2017, All rights reserved.
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
package net.byteseek.utils;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

/**
 * Created by matt on 07/08/17.
 */
public class MathUtilsTest {

    @Test
    public void testFloorLogBase2() {
        // Number must be positive.
        try {
            MathUtils.floorLogBaseTwo(0);
            fail("Expected an illegal argument exception");
        } catch (IllegalArgumentException expected) {}
        try {
            MathUtils.floorLogBaseTwo(-1);
            fail("Expected an illegal argument exception");
        } catch (IllegalArgumentException expected) {}
        try {
            MathUtils.floorLogBaseTwo(-65537);
            fail("Expected an illegal argument exception");
        } catch (IllegalArgumentException expected) {}

        assertEquals("floor log base 2", 0, MathUtils.floorLogBaseTwo(1));
        assertEquals("floor log base 2", 1, MathUtils.floorLogBaseTwo(2));
        assertEquals("floor log base 2", 1, MathUtils.floorLogBaseTwo(3));
        assertEquals("floor log base 2", 2, MathUtils.floorLogBaseTwo(4));
        assertEquals("floor log base 2", 2, MathUtils.floorLogBaseTwo(5));
        assertEquals("floor log base 2", 4, MathUtils.floorLogBaseTwo(31));
        assertEquals("floor log base 2", 5, MathUtils.floorLogBaseTwo(32));
        assertEquals("floor log base 2", 5, MathUtils.floorLogBaseTwo(33));
        assertEquals("floor log base 2", 5, MathUtils.floorLogBaseTwo(63));
        assertEquals("floor log base 2", 6, MathUtils.floorLogBaseTwo(64));
        assertEquals("floor log base 2", 6, MathUtils.floorLogBaseTwo(65));
        assertEquals("floor log base 2", 6, MathUtils.floorLogBaseTwo(127));
        assertEquals("floor log base 2", 7, MathUtils.floorLogBaseTwo(128));
        assertEquals("floor log base 2", 7, MathUtils.floorLogBaseTwo(129));
        assertEquals("floor log base 2", 7, MathUtils.floorLogBaseTwo(255));
    }

    @Test
    public void testLongFloorLogBase2() {
        // Number must be positive.
        try {
            MathUtils.floorLogBaseTwo(0L);
            fail("Expected an illegal argument exception");
        } catch (IllegalArgumentException expected) {}
        try {
            MathUtils.floorLogBaseTwo(-1L);
            fail("Expected an illegal argument exception");
        } catch (IllegalArgumentException expected) {}
        try {
            MathUtils.floorLogBaseTwo(-65537L);
            fail("Expected an illegal argument exception");
        } catch (IllegalArgumentException expected) {}

        assertEquals("floor log base 2", 0, MathUtils.floorLogBaseTwo(1L));
        assertEquals("floor log base 2", 1, MathUtils.floorLogBaseTwo(2L));
        assertEquals("floor log base 2", 1, MathUtils.floorLogBaseTwo(3L));
        assertEquals("floor log base 2", 2, MathUtils.floorLogBaseTwo(4L));
        assertEquals("floor log base 2", 2, MathUtils.floorLogBaseTwo(5L));
        assertEquals("floor log base 2", 4, MathUtils.floorLogBaseTwo(31L));
        assertEquals("floor log base 2", 5, MathUtils.floorLogBaseTwo(32L));
        assertEquals("floor log base 2", 5, MathUtils.floorLogBaseTwo(33L));
        assertEquals("floor log base 2", 5, MathUtils.floorLogBaseTwo(63L));
        assertEquals("floor log base 2", 6, MathUtils.floorLogBaseTwo(64L));
        assertEquals("floor log base 2", 6, MathUtils.floorLogBaseTwo(65L));
        assertEquals("floor log base 2", 6, MathUtils.floorLogBaseTwo(127L));
        assertEquals("floor log base 2", 7, MathUtils.floorLogBaseTwo(128L));
        assertEquals("floor log base 2", 7, MathUtils.floorLogBaseTwo(129L));
        assertEquals("floor log base 2", 7, MathUtils.floorLogBaseTwo(255L));
    }

    @Test
    public void testCeilLogBase2() {
        // Number must be positive.
        try {
            MathUtils.ceilLogBaseTwo(0);
            fail("Expected an illegal argument exception");
        } catch (IllegalArgumentException expected) {}
        try {
            MathUtils.ceilLogBaseTwo(-1);
            fail("Expected an illegal argument exception");
        } catch (IllegalArgumentException expected) {}
        try {
            MathUtils.ceilLogBaseTwo(-65537);
            fail("Expected an illegal argument exception");
        } catch (IllegalArgumentException expected) {}

        assertEquals("ceil log base 2", 0, MathUtils.ceilLogBaseTwo(1));
        assertEquals("ceil log base 2", 1, MathUtils.ceilLogBaseTwo(2));
        assertEquals("ceil log base 2", 2, MathUtils.ceilLogBaseTwo(3));
        assertEquals("ceil log base 2", 2, MathUtils.ceilLogBaseTwo(4));
        assertEquals("ceil log base 2", 3, MathUtils.ceilLogBaseTwo(5));
        assertEquals("ceil log base 2", 5, MathUtils.ceilLogBaseTwo(31));
        assertEquals("ceil log base 2", 5, MathUtils.ceilLogBaseTwo(32));
        assertEquals("ceil log base 2", 6, MathUtils.ceilLogBaseTwo(33));
        assertEquals("ceil log base 2", 6, MathUtils.ceilLogBaseTwo(63));
        assertEquals("ceil log base 2", 6, MathUtils.ceilLogBaseTwo(64));
        assertEquals("ceil log base 2", 7, MathUtils.ceilLogBaseTwo(65));
        assertEquals("ceil log base 2", 7, MathUtils.ceilLogBaseTwo(127));
        assertEquals("ceil log base 2", 7, MathUtils.ceilLogBaseTwo(128));
        assertEquals("ceil log base 2", 8, MathUtils.ceilLogBaseTwo(129));
        assertEquals("ceil log base 2", 8, MathUtils.ceilLogBaseTwo(255));
    }

    @Test
    public void testCeilLongLogBase2() {
        // Number must be positive.
        try {
            MathUtils.ceilLogBaseTwo(0L);
            fail("Expected an illegal argument exception");
        } catch (IllegalArgumentException expected) {}
        try {
            MathUtils.ceilLogBaseTwo(-1L);
            fail("Expected an illegal argument exception");
        } catch (IllegalArgumentException expected) {}
        try {
            MathUtils.ceilLogBaseTwo(-65537L);
            fail("Expected an illegal argument exception");
        } catch (IllegalArgumentException expected) {}

        assertEquals("ceil log base 2", 0, MathUtils.ceilLogBaseTwo(1L));
        assertEquals("ceil log base 2", 1, MathUtils.ceilLogBaseTwo(2L));
        assertEquals("ceil log base 2", 2, MathUtils.ceilLogBaseTwo(3L));
        assertEquals("ceil log base 2", 2, MathUtils.ceilLogBaseTwo(4L));
        assertEquals("ceil log base 2", 3, MathUtils.ceilLogBaseTwo(5L));
        assertEquals("ceil log base 2", 5, MathUtils.ceilLogBaseTwo(31L));
        assertEquals("ceil log base 2", 5, MathUtils.ceilLogBaseTwo(32L));
        assertEquals("ceil log base 2", 6, MathUtils.ceilLogBaseTwo(33L));
        assertEquals("ceil log base 2", 6, MathUtils.ceilLogBaseTwo(63L));
        assertEquals("ceil log base 2", 6, MathUtils.ceilLogBaseTwo(64L));
        assertEquals("ceil log base 2", 7, MathUtils.ceilLogBaseTwo(65L));
        assertEquals("ceil log base 2", 7, MathUtils.ceilLogBaseTwo(127L));
        assertEquals("ceil log base 2", 7, MathUtils.ceilLogBaseTwo(128L));
        assertEquals("ceil log base 2", 8, MathUtils.ceilLogBaseTwo(129L));
        assertEquals("ceil log base 2", 8, MathUtils.ceilLogBaseTwo(255L));
    }

    @Test
    public void testIsPowerOfTwo() {
        int[] powersOfTwo = new int[] {1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768,
                65536, 131072, 262144, 524288, 1048576, 2097152, 4194304, 8388608, 16777216,
                33554432, 67108864, 134217728, 268435456, 536870912, 1073741824};

        for (int power : powersOfTwo) {
            assertTrue("Number is power of two " + power, MathUtils.isPowerOfTwo(power));
            if (power != 2) {
                assertFalse("Number minus one is not power of two " + power, MathUtils.isPowerOfTwo(power - 1));
            }
            if (power != 1) {
                assertFalse("Number plus one is not power of two " + power, MathUtils.isPowerOfTwo(power + 1));
            }
        }
    }

    @Test
    public void testNextHighestPowerOfTwo() {
        int[] powersOfTwo = new int[] {1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768,
                65536, 131072, 262144, 524288, 1048576, 2097152, 4194304, 8388608, 16777216,
                33554432, 67108864, 134217728, 268435456, 536870912, 1073741824};
        final int HIGHEST_POWER = 1073741824;
        for (int index = 0; index < powersOfTwo.length; index++) {
            final int power = powersOfTwo[index];
            if (power > 1) {
                assertEquals("Next highest power of two below power " + power, power, MathUtils.nextHighestPowerOfTwo(power - 1));
            }
            if (power < HIGHEST_POWER) {
                assertEquals("Next highest power of power is next entry " + power, powersOfTwo[index+1], MathUtils.nextHighestPowerOfTwo(power));
                if (power > 1) {
                    assertEquals("Next highest power of power + 1 is next entry " + power, powersOfTwo[index+1], MathUtils.nextHighestPowerOfTwo(power + 1));
                }
            }
        }
    }
}
