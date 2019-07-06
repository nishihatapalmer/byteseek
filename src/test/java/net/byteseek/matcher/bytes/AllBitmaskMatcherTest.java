/*
 * Copyright Matt Palmer 2009-2019, All rights reserved.
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

import net.byteseek.utils.ByteUtils;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class AllBitmaskMatcherTest extends BaseMatcherTest {

       /**
     * Test of matches method, of class AllBitmaskMatcher.
     */
    @Test
    public void testMatches_byte() throws Exception {
        AllBitmaskMatcher matcher = new AllBitmaskMatcher(b(255));
        validateMatchInRange(matcher, 255, 255);
        validateNoMatchInRange(matcher, 0, 254);

        SimpleTimer.timeMatcher("Bitmask All 255", matcher);

        matcher = new AllBitmaskMatcher(b(0));
        validateMatchInRange(matcher, 0, 256);

        SimpleTimer.timeMatcher("Bitmask All 0", matcher);

        matcher = new AllBitmaskMatcher(b(254));
        validateMatchInRange(matcher, 254, 255);
        validateNoMatchInRange(matcher, 0, 253);

        SimpleTimer.timeMatcher("Bitmask All 254", matcher);

        matcher = new AllBitmaskMatcher(b(128));
        validateMatchInRange(matcher, 128, 255);
        validateNoMatchInRange(matcher, 0, 127);

        SimpleTimer.timeMatcher("Bitmask All 128", matcher);
        
        // test all bit masks using different methods.
        for (int mask = 0; mask < 256; mask++) {
            matcher = new AllBitmaskMatcher(b(mask));
            testAbstractMethods(matcher);
            validateMatchBitsSet(matcher, b(mask));
            validateNoMatchBitsNotSet(matcher, b(mask));
        }
    }

    /**
     * Test of matches method, of class AllBitmaskMatcher.
     */
    @Test
    public void testMatchesInvertedByte() throws IOException {
        AllBitmaskMatcher matcher = new AllBitmaskMatcher(b(255), true);
        validateMatchInRange(matcher, 0, 254);
        validateNoMatchInRange(matcher, 255, 255);

        SimpleTimer.timeMatcher("Bitmask All 255", matcher);

        matcher = new AllBitmaskMatcher(b(0), true);
        validateNoMatchInRange(matcher, 0, 256);

        SimpleTimer.timeMatcher("Bitmask All 0", matcher);

        matcher = new AllBitmaskMatcher(b(254), true);
        validateMatchInRange(matcher,  0, 253);
        validateNoMatchInRange(matcher,254, 255);

        SimpleTimer.timeMatcher("Bitmask All 254", matcher);

        matcher = new AllBitmaskMatcher(b(128), true);
        validateMatchInRange(matcher, 0, 127);
        validateNoMatchInRange(matcher, 128, 255);

        SimpleTimer.timeMatcher("Bitmask All 128", matcher);

        // test all bit masks using different methods.
        for (int mask = 0; mask < 256; mask++) {
            matcher = new AllBitmaskMatcher(b(mask), true);
            // test methods from abstract superclass
            assertEquals("length is one", 1, matcher.length());
            assertEquals("matcher for position 0 is this", matcher, matcher.getMatcherForPosition(0));
            try {
                matcher.getMatcherForPosition(-1);
                fail("expected an IndexOutOfBoundsException");
            } catch (IndexOutOfBoundsException expectedIgnore) {}
            try {
                matcher.getMatcherForPosition(1);
                fail("expected an IndexOutOfBoundsException");
            } catch (IndexOutOfBoundsException expectedIgnore) {}

            validateInvertedMatchBitsSet(matcher, b(mask));
            validateInvertedNoMatchBitsNotSet(matcher, b(mask));
        }
    }


    private void validateNoMatchInRange(AllBitmaskMatcher matcher, int from, int to) {
        for (int count = from; count <= to; count++) {
            assertEquals(false, matcher.matches(b(count)));
        }
    }

    private void validateMatchInRange(AllBitmaskMatcher matcher, int from, int to) {
        for (int count = from; count <= to; count++) {
            assertEquals(true, matcher.matches(b(count)));
        }
    }

    private void validateMatchBitsSet(AllBitmaskMatcher matcher, int bitmask) throws IOException {
        String description = String.format("0x%02x", bitmask);
        for (int count = 0; count < 256; count++) {
            byte value = (byte) (count | bitmask);
            assertEquals(description, true, matcher.matches(value));
            assertEquals(description, true, matcher.matches(reader, value & 0xFF));
            assertEquals(description, true, matcher.matches(BYTE_VALUES, value & 0xFF));
            assertEquals(description, true, matcher.matchesNoBoundsCheck(BYTE_VALUES, value & 0xFF));

        }
    }

    private void validateInvertedMatchBitsSet(AllBitmaskMatcher matcher, int bitmask) throws IOException {
        String description = String.format("0x%02x", bitmask);
        for (int count = 0; count < 256; count++) {
            byte value = (byte) (count | bitmask);
            assertEquals(description, false, matcher.matches(value));
            assertEquals(description, false, matcher.matches(reader, value & 0xFF));
            assertEquals(description, false, matcher.matches(BYTE_VALUES, value & 0xFF));
            assertEquals(description, false, matcher.matchesNoBoundsCheck(BYTE_VALUES, value & 0xFF));
        }
    }

    private void validateNoMatchBitsNotSet(AllBitmaskMatcher matcher, int bitmask) throws IOException {
        if (bitmask > 0) { // This test won't work for a zero bitmask.
            String description = String.format("0x%02x", bitmask);
            final int invertedMask = bitmask ^ 0xFF;
            for (int count = 0; count < 256; count++) { // zero byte matches everything.
                byte value = (byte) (count & invertedMask);
                assertEquals(description, false, matcher.matches(value));
                assertEquals(description, false, matcher.matches(reader, value & 0xFF));
                assertEquals(description, false, matcher.matches(BYTE_VALUES, value & 0xFF));
                assertEquals(description, false, matcher.matchesNoBoundsCheck(BYTE_VALUES, value & 0xFF));
            }
        }
    }

    private void validateInvertedNoMatchBitsNotSet(AllBitmaskMatcher matcher, int bitmask) throws IOException {
        if (bitmask > 0) { // This test won't work for a zero bitmask.
            String description = String.format("0x%02x", bitmask);
            final int invertedMask = bitmask ^ 0xFF;
            for (int count = 0; count < 256; count++) { // zero byte matches everything.
                byte value = (byte) (count & invertedMask);
                assertEquals(description, true, matcher.matches(value));
                assertEquals(description, true, matcher.matches(reader, value & 0xFF));
                assertEquals(description, true, matcher.matches(BYTE_VALUES, value & 0xFF));
                assertEquals(description, true, matcher.matchesNoBoundsCheck(BYTE_VALUES, value & 0xFF));
            }
        }
    }
    

    /**
     * Test of toRegularExpression method, of class AllBitmaskMatcher.
     */
    @Test
    public void testToRegularExpression() {
        for (int count = 0; count < 256; count++) {
            AllBitmaskMatcher matcher = new AllBitmaskMatcher(b(count));
            String expected = String.format("&%02x", count);
            assertEquals(expected, matcher.toRegularExpression(false));
            assertEquals(expected, matcher.toRegularExpression(true));

            matcher = new AllBitmaskMatcher(b(count), true);
            expected = String.format("^&%02x", count);
            assertEquals(expected, matcher.toRegularExpression(false));
            assertEquals(expected, matcher.toRegularExpression(true));
        }
    }


    @Test
    public void testToString() {
        for (int count = 0; count < 256; count++) {
            AllBitmaskMatcher matcher = new AllBitmaskMatcher(b(count));
            String toString = matcher.toString();
            assertTrue(toString.contains(AllBitmaskMatcher.class.getSimpleName()));
            String expected = String.format("%02x", count);
            assertTrue(toString.contains(expected));
            assertTrue(toString.contains("inverted"));

            matcher = new AllBitmaskMatcher(b(count), true);
            toString = matcher.toString();
            assertTrue(toString.contains(AllBitmaskMatcher.class.getSimpleName()));
            assertTrue(toString.contains(expected));
            assertTrue(toString.contains("inverted"));
        }
    }

    @Test
    public void testEqualsAndHashCode() {
        for (int count = 0; count < 256; count++) {
            AllBitmaskMatcher matcher = new AllBitmaskMatcher(b(count));
            assertFalse(matcher.equals(null));

            // Check same objects are equal with same hashcode.
            AllBitmaskMatcher same    = new AllBitmaskMatcher(b(count));
            assertTrue(matcher.equals(same));
            assertTrue(same.equals(matcher));
            assertEquals(matcher.hashCode(), same.hashCode());

            // Check not the same as an inverted version
            AllBitmaskMatcher inverted = new AllBitmaskMatcher(b(count), true);
            assertFalse(matcher.equals(inverted));
            assertFalse(inverted.equals(matcher));

            // Check different objects are not equal.
            AllBitmaskMatcher different = new AllBitmaskMatcher(b((count + 1) % 256));
            assertFalse(matcher.equals(different));
            assertFalse(different.equals(matcher));

            OneByteMatcher other = new OneByteMatcher(b(count));
            assertFalse(matcher.equals(other));
        }
    }

    /**
     * Test of getMatchingBytes method, of class AllBitmaskMatcher.
     */
    @Test
    public void testGetMatchingBytes() {
        AllBitmaskMatcher matcher = new AllBitmaskMatcher(b(255));
        assertArrayEquals("0xFF matches [0xFF] only",
                new byte[] {b(255)},
                matcher.getMatchingBytes());

        matcher = new AllBitmaskMatcher(b(0));
        assertArrayEquals("0x00 matches all bytes", ByteUtils.getAllByteValues(), matcher.getMatchingBytes());

        matcher = new AllBitmaskMatcher(b(254));
        byte[] expected = new byte[] {b(254), b(255)};
        assertArrayEquals("0xFE matches 0xFF and 0xFE only", expected, matcher.getMatchingBytes());

        matcher = new AllBitmaskMatcher(b(3));
        expected = new byte[64];
        for (int count = 0; count < 64; count++) {
            expected[count] = (byte)((count << 2) | 3);
        }
        assertArrayEquals("0x03 matches 64 bytes with first two bits set", expected, matcher.getMatchingBytes());

        matcher = new AllBitmaskMatcher(b(128));
        expected = ByteUtils.getBytesInRange(128, 255);
        assertArrayEquals("0x80 matches all bytes from 128 to 255", expected, matcher.getMatchingBytes());
    }

    /**
     * Test of getNumberOfMatchingBytes method, of class AllBitmaskMatcher.
     */
    @Test
    public void testGetNumberOfMatchingBytes() {
        AllBitmaskMatcher matcher = new AllBitmaskMatcher(b(255));
        assertEquals("0xFF matches one byte", 1, matcher.getNumberOfMatchingBytes());

        matcher = new AllBitmaskMatcher(b(0));
        assertEquals("0x00 matches 256 bytes", 256, matcher.getNumberOfMatchingBytes());

        matcher = new AllBitmaskMatcher(b(254));
        assertEquals("0xFE matches 2 bytes", 2, matcher.getNumberOfMatchingBytes());

        matcher = new AllBitmaskMatcher(b(3));
        assertEquals("0x03 matches 64 bytes", 64, matcher.getNumberOfMatchingBytes());
        
        matcher = new AllBitmaskMatcher(b(128));
        assertEquals("0x80 matches 128 bytes", 128, matcher.getNumberOfMatchingBytes());
    }

    private byte b(int i) {
        return (byte) i;
    }

}