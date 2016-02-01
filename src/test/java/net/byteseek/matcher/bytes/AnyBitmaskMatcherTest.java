/*
 * Copyright Matt Palmer 2009-2016, All rights reserved.
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

package net.byteseek.matcher.bytes;

import net.byteseek.bytes.ByteUtils;
import net.byteseek.matcher.bytes.AnyBitmaskMatcher;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class AnyBitmaskMatcherTest {

    /**
     * 
     */
    public AnyBitmaskMatcherTest() {
    }

    /**
     * 
     * @throws Exception
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    /**
     * 
     * @throws Exception
     */
    @AfterClass
    public static void tearDownClass() throws Exception {
    }


    /**
     * Test of matches method, of class AnyBitmaskMatcher.
     */
    @Test
    public void testMatches_byte() {
        AnyBitmaskMatcher matcher = new AnyBitmaskMatcher(b(255));
        validateMatchInRange(matcher, 1, 255);
        validateNoMatchInRange(matcher, 0, 0);

        SimpleTimer.timeMatcher("Bitmask Any 255", matcher);

        matcher = new AnyBitmaskMatcher(b(0));
        validateNoMatchInRange(matcher, 0, 255);

        SimpleTimer.timeMatcher("Bitmask Any 0", matcher);

        matcher = new AnyBitmaskMatcher(b(254));
        validateMatchInRange(matcher, 2, 255);
        validateNoMatchInRange(matcher, 0, 1);

        SimpleTimer.timeMatcher("Bitmask Any 254", matcher);

        matcher = new AnyBitmaskMatcher(b(128));
        validateMatchInRange(matcher, 128, 255);
        validateNoMatchInRange(matcher, 0, 127);

        SimpleTimer.timeMatcher("Bitmask Any 128", matcher);

        // test all bit masks using different methods.
        for (int mask = 0; mask < 256; mask++) {
            matcher = new AnyBitmaskMatcher(b(mask));
            validateMatchBitsSet(matcher, b(mask));
            validateNoMatchBitsNotSet(matcher, b(mask));
        }
    }


    /**
     * Test of matches method, of class AnyBitmaskMatcher.
     */
    @Test
    public void testMatchesInvertedByte() {
        AnyBitmaskMatcher matcher = new AnyBitmaskMatcher(b(255), true);
        validateMatchInRange(matcher, 0, 0);
        validateNoMatchInRange(matcher, 1, 255);

        SimpleTimer.timeMatcher("Bitmask Any 255", matcher);

        matcher = new AnyBitmaskMatcher(b(0), true);
        validateMatchInRange(matcher, 0, 255);

        SimpleTimer.timeMatcher("Bitmask Any 0", matcher);

        matcher = new AnyBitmaskMatcher(b(254), true);
        validateMatchInRange(matcher,  0, 1);
        validateNoMatchInRange(matcher, 2, 255);

        SimpleTimer.timeMatcher("Bitmask Any 254", matcher);

        matcher = new AnyBitmaskMatcher(b(128), true);
        validateMatchInRange(matcher, 0, 127);
        validateNoMatchInRange(matcher, 128, 255);

        SimpleTimer.timeMatcher("Bitmask Any 128", matcher);

        // test all bit masks using different methods.
        for (int mask = 0; mask < 256; mask++) {
            matcher = new AnyBitmaskMatcher(b(mask), true);
            validateInvertedMatchBitsSet(matcher, b(mask));
            validateInvertedNoMatchBitsNotSet(matcher, b(mask));
        }
    }

    private void validateNoMatchInRange(AnyBitmaskMatcher matcher, int from, int to) {
        String description = String.format("%d:%d", from, to);
        for (int count = from; count <= to; count++) {
            String d2 = String.format("%s(%d)", description, count);
            assertEquals(d2, false, matcher.matches(b(count)));
        }
    }

    private void validateMatchInRange(AnyBitmaskMatcher matcher, int from, int to) {
        String description = String.format("%d:%d", from, to);
        for (int count = from; count <= to; count++) {
            String d2 = String.format("%s(%d)", description, count);
            assertEquals(d2, true, matcher.matches(b(count)));
        }
    }

    private void validateMatchBitsSet(AnyBitmaskMatcher matcher, int bitmask) {
        if (bitmask > 0) { // This test won't work for a zero bitmask.
            String description = String.format("0x%02x", bitmask);
            for (int count = 0; count < 256; count++) {
                String d2 = String.format("%s(%d)", description, count);
                byte value = (byte) (count | bitmask);
                assertEquals(d2, true, matcher.matches(value));
            }
        }
    }

    private void validateInvertedMatchBitsSet(AnyBitmaskMatcher matcher, int bitmask) {
        if (bitmask > 0) { // This test won't work for a zero bitmask.
            String description = String.format("0x%02x", bitmask);
            for (int count = 0; count < 256; count++) {
                String d2 = String.format("%s(%d)", description, count);
                byte value = (byte) (count | bitmask);
                assertEquals(d2, false, matcher.matches(value));
            }
        }
    }

    private void validateNoMatchBitsNotSet(AnyBitmaskMatcher matcher, int bitmask) {
        String description = String.format("0x%02x", bitmask);
        final int invertedMask = bitmask ^ 0xFF;
        for (int count = 0; count < 256; count++) { // zero byte matches everything.
            String d2 = String.format("%s(%d)", description, count);
            byte value = (byte) (count & invertedMask);
            assertEquals(d2, false, matcher.matches(value));
        }
    }

    private void validateInvertedNoMatchBitsNotSet(AnyBitmaskMatcher matcher, int bitmask) {
        String description = String.format("0x%02x", bitmask);
        final int invertedMask = bitmask ^ 0xFF;
        for (int count = 0; count < 256; count++) { // zero byte matches everything.
            String d2 = String.format("%s(%d)", description, count);
            byte value = (byte) (count & invertedMask);
            assertEquals(d2, true, matcher.matches(value));
        }
    }


    /**
     * Test of toRegularExpression method, of class AnyBitmaskMatcher.
     */
    @Test
    public void testToRegularExpression() {
        for (int count = 0; count < 256; count++) {
            AnyBitmaskMatcher matcher = new AnyBitmaskMatcher(b(count));
            String expected = String.format("~%02x", count);
            assertEquals(expected, matcher.toRegularExpression(false));
            assertEquals(expected, matcher.toRegularExpression(true));

            matcher = new AnyBitmaskMatcher(b(count), true);
            expected = String.format("^~%02x", count);
            assertEquals(expected, matcher.toRegularExpression(false));
            assertEquals(expected, matcher.toRegularExpression(true));
        }
    }

    /**
     * Test of getMatchingBytes method, of class AnyBitmaskMatcher.
     */
    @Test
    public void testGetMatchingBytes() {
        AnyBitmaskMatcher matcher = new AnyBitmaskMatcher(b(255));
        byte[] expected = ByteUtils.getBytesInRange(1, 255);
        assertArrayEquals("0xFF matches all bytes except zero",
                expected, matcher.getMatchingBytes());

        matcher = new AnyBitmaskMatcher(b(0));
        assertArrayEquals("0x00 matches no bytes", new byte[0], matcher.getMatchingBytes());

        matcher = new AnyBitmaskMatcher(b(254));
        expected = ByteUtils.getBytesInRange(2, 255);
        assertArrayEquals("0xFE matches everything except 1 and 0", expected, matcher.getMatchingBytes());

        matcher = new AnyBitmaskMatcher(b(128));
        expected = ByteUtils.getBytesInRange(128, 255);
        assertArrayEquals("0x80 matches all bytes from 128 to 255", expected, matcher.getMatchingBytes());
    }

    /**
     * Test of getNumberOfMatchingBytes method, of class AnyBitmaskMatcher.
     */
    @Test
    public void testGetNumberOfMatchingBytes() {
        AnyBitmaskMatcher matcher = new AnyBitmaskMatcher(b(255));
        assertEquals("0xFF matches 255 bytes", 255, matcher.getNumberOfMatchingBytes());

        matcher = new AnyBitmaskMatcher(b(0));
        assertEquals("0x00 matches 0 bytes", 0, matcher.getNumberOfMatchingBytes());

        matcher = new AnyBitmaskMatcher(b(1));
        assertEquals("0x01 matches 128 bytes", 128, matcher.getNumberOfMatchingBytes());

        matcher = new AnyBitmaskMatcher(b(254));
        assertEquals("0xFE matches 254 bytes", 254, matcher.getNumberOfMatchingBytes());

        matcher = new AnyBitmaskMatcher(b(3));
        assertEquals("0x03 matches 192 bytes", 192, matcher.getNumberOfMatchingBytes());

        matcher = new AnyBitmaskMatcher(b(128));
        assertEquals("0x80 matches 128 bytes", 128, matcher.getNumberOfMatchingBytes());

    }

    private byte b(int i) {
        return (byte) i;
    }

}