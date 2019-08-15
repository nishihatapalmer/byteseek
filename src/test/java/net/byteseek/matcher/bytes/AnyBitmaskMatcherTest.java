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

import net.byteseek.compiler.matcher.ByteMatcherCompiler;
import net.byteseek.io.reader.InputStreamReader;
import net.byteseek.utils.ByteUtils;
import net.byteseek.io.reader.WindowReader;

import net.byteseek.utils.StringUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class AnyBitmaskMatcherTest extends BaseMatcherTest {

    /**
     * Test of matches method, of class AnyBitmaskMatcher.
     */
    @Test
    public void testMatches_byte() throws Exception {
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
            testAbstractMethods(matcher);
            validateMatchBitsSet(matcher, b(mask));
            validateNoMatchBitsNotSet(matcher, b(mask));
        }
    }

    @Test
    public void testEqualsAndHashCode() {
        for (int count = 0; count < 256; count++) {
            AnyBitmaskMatcher matcher = new AnyBitmaskMatcher(b(count));
            assertFalse(matcher.equals(null));

            // Check same objects are equal with same hashcode.
            AnyBitmaskMatcher same    = new AnyBitmaskMatcher(b(count));
            assertTrue(matcher.equals(same));
            assertTrue(same.equals(matcher));
            assertEquals(matcher.hashCode(), same.hashCode());

            // Check not the same as an inverted version
            AnyBitmaskMatcher inverted = new AnyBitmaskMatcher(b(count), true);
            assertFalse(matcher.equals(inverted));
            assertFalse(inverted.equals(matcher));

            // Check different objects are not equal.
            AnyBitmaskMatcher different = new AnyBitmaskMatcher(b((count + 1) % 256));
            assertFalse(matcher.equals(different));
            assertFalse(different.equals(matcher));

            OneByteMatcher other = new OneByteMatcher(b(count));
            assertFalse(matcher.equals(other));
        }
    }

    /**
     * Test of matches method, of class AnyBitmaskMatcher.
     */
    @Test
    public void testMatchesInvertedByte() throws IOException {
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

    private void validateNoMatchInRange(AnyBitmaskMatcher matcher, int from, int to) throws IOException {
        String description = String.format("%d:%d", from, to);
        for (int count = from; count <= to; count++) {
            String d2 = String.format("%s(%d)", description, count);
            assertEquals(d2, false, matcher.matches(b(count)));
            assertEquals(d2, false, matcher.matches(reader, count));
            assertEquals(d2, false, matcher.matches(BYTE_VALUES, count));
            assertEquals(d2, false, matcher.matchesNoBoundsCheck(BYTE_VALUES, count));
        }
    }

    private void validateMatchInRange(AnyBitmaskMatcher matcher, int from, int to) throws IOException {
        String description = String.format("%d:%d", from, to);
        for (int count = from; count <= to; count++) {
            String d2 = String.format("%s(%d)", description, count);
            assertEquals(d2, true, matcher.matches(b(count)));
            assertEquals(d2, true, matcher.matches(reader, count));
            assertEquals(d2, true, matcher.matches(BYTE_VALUES, count));
            assertEquals(d2, true, matcher.matchesNoBoundsCheck(BYTE_VALUES, count));
        }
    }

    private void validateMatchBitsSet(AnyBitmaskMatcher matcher, int bitmask) throws IOException {
        if (bitmask > 0) { // This test won't work for a zero bitmask.
            String description = String.format("0x%02x", bitmask);
            for (int count = 0; count < 256; count++) {
                String d2 = String.format("%s(%d)", description, count);
                byte value = (byte) (count | bitmask);
                assertEquals(d2, true, matcher.matches(value));
                assertEquals(d2, true, matcher.matches(reader, value & 0xFF));
                assertEquals(d2, true, matcher.matches(BYTE_VALUES, value & 0xFF));
                assertEquals(d2, true, matcher.matchesNoBoundsCheck(BYTE_VALUES, value & 0xFF));
            }
        }
    }

    private void validateInvertedMatchBitsSet(AnyBitmaskMatcher matcher, int bitmask) throws IOException {
        if (bitmask > 0) { // This test won't work for a zero bitmask.
            String description = String.format("0x%02x", bitmask);
            for (int count = 0; count < 256; count++) {
                String d2 = String.format("%s(%d)", description, count);
                byte value = (byte) (count | bitmask);
                assertEquals(d2, false, matcher.matches(value));
                assertEquals(d2, false, matcher.matches(reader, value & 0xFF));
                assertEquals(d2, false, matcher.matches(BYTE_VALUES, value & 0xFF));
                assertEquals(d2, false, matcher.matchesNoBoundsCheck(BYTE_VALUES, value & 0xFF));
            }
        }
    }

    private void validateNoMatchBitsNotSet(AnyBitmaskMatcher matcher, int bitmask) throws IOException {
        String description = String.format("0x%02x", bitmask);
        final int invertedMask = bitmask ^ 0xFF;
        for (int count = 0; count < 256; count++) { // zero byte matches everything.
            String d2 = String.format("%s(%d)", description, count);
            byte value = (byte) (count & invertedMask);
            assertEquals(d2, false, matcher.matches(value));
            assertEquals(d2, false, matcher.matches(reader, value & 0xFF));
            assertEquals(d2, false, matcher.matches(BYTE_VALUES, value & 0xFF));
            assertEquals(d2, false, matcher.matchesNoBoundsCheck(BYTE_VALUES, value & 0xFF));
        }
    }

    private void validateInvertedNoMatchBitsNotSet(AnyBitmaskMatcher matcher, int bitmask) throws IOException {
        String description = String.format("0x%02x", bitmask);
        final int invertedMask = bitmask ^ 0xFF;
        for (int count = 0; count < 256; count++) { // zero byte matches everything.
            String d2 = String.format("%s(%d)", description, count);
            byte value = (byte) (count & invertedMask);
            assertEquals(d2, true, matcher.matches(value));
            assertEquals(d2, true, matcher.matches(reader, value & 0xFF));
            assertEquals(d2, true, matcher.matches(BYTE_VALUES, value & 0xFF));
            assertEquals(d2, true, matcher.matchesNoBoundsCheck(BYTE_VALUES, value & 0xFF));
        }
    }


    /**
     * Test of toRegularExpression method, of class AnyBitmaskMatcher.
     */
    @Test
    public void testToRegularExpression() {
        AnyBitmaskMatcher matcher = new AnyBitmaskMatcher((byte) 0);
        assertEquals("^__", matcher.toRegularExpression(true));
        assertEquals("^__", matcher.toRegularExpression(false));

        matcher = new AnyBitmaskMatcher((byte) 0, true);
        assertEquals("__", matcher.toRegularExpression(true));
        assertEquals("__", matcher.toRegularExpression(false));

        for (int count = 1; count < 256; count++) {
            matcher = new AnyBitmaskMatcher(b(count));
            String expected = '~' + StringUtils.toWildByteRegex((byte) count, (byte) count);
            assertEquals(expected, matcher.toRegularExpression(false));
            assertEquals(expected, matcher.toRegularExpression(true));

            matcher = new AnyBitmaskMatcher(b(count), true);
            expected = "^" + expected;
            assertEquals(expected, matcher.toRegularExpression(false));
            assertEquals(expected, matcher.toRegularExpression(true));
        }
    }

    @Test
    public void testEquivalentToWildBit()  throws Exception {
        for (int i = 0; i < 256; i++) {
            AnyBitmaskMatcher any = new AnyBitmaskMatcher((byte) i);
            String regex = any.toRegularExpression(false);
            ByteMatcher compiled = ByteMatcherCompiler.compileFrom(regex);
            assertEquals(any.getNumberOfMatchingBytes(), compiled.getNumberOfMatchingBytes());
            testEquivalentBytes(any, compiled);

            any = new AnyBitmaskMatcher((byte) i, true);
            regex = any.toRegularExpression(false);
            compiled = ByteMatcherCompiler.compileFrom(regex);
            assertEquals(any.getNumberOfMatchingBytes(), compiled.getNumberOfMatchingBytes());
            testEquivalentBytes(any, compiled);
        }
    }

    private void testEquivalentBytes(AnyBitmaskMatcher any, ByteMatcher compiled) {
        byte[] matchAny = any.getMatchingBytes();
        byte[] matchComp = compiled.getMatchingBytes();
        for (byte b : matchAny) {
            boolean found = false;
            for (int i = 0; i < matchComp.length; i++) {
                if (b == matchComp[i]) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                fail(any.toString() + " " + compiled.toString() + "Needed to find byte " + b + " in the compiled version");
            }
        }
    }


    @Test
    public void testToString() {
        AnyBitmaskMatcher matcher = new AnyBitmaskMatcher((byte) 0);
        String toString = matcher.toString();
        assertTrue(toString.contains(AnyBitmaskMatcher.class.getSimpleName()));
        assertTrue("^__", toString.contains("^__"));

        matcher = new AnyBitmaskMatcher((byte) 0, true);
        toString = matcher.toString();assertTrue(toString.contains(AnyBitmaskMatcher.class.getSimpleName()));
        assertTrue("__", toString.contains("__"));
        assertFalse(toString.contains("^"));

        for (int count = 1; count < 256; count++) {
            matcher = new AnyBitmaskMatcher(b(count));
            toString = matcher.toString();
            toString.contains(AnyBitmaskMatcher.class.getSimpleName());
            String expected = StringUtils.toWildByteRegex((byte) count, (byte) count);
            assertTrue(toString.contains(expected));

            matcher = new AnyBitmaskMatcher(b(count), true);
            toString = matcher.toString();
            assertTrue(toString.contains(AnyBitmaskMatcher.class.getSimpleName()));
            assertTrue(toString.contains(expected));
            assertTrue(toString.contains("^"));
        }
    }

    /**
     * Test of getMatchingBytes method, of class AnyBitmaskMatcher.
     */
    @Test
    public void testGetMatchingBytes() {
        AnyBitmaskMatcher matcher = new AnyBitmaskMatcher(b(255));
        byte[] expected = ByteUtils.getBytesInRange(1, 255);
        assertArrayEquals("0xFF matches all bytes except zero", expected, matcher.getMatchingBytes());

        matcher = new AnyBitmaskMatcher(b(255), true);
        expected = new byte[] {(byte) 0};
        assertArrayEquals("inverted 0xFF matches zero only", expected, matcher.getMatchingBytes());

        matcher = new AnyBitmaskMatcher(b(0));
        assertArrayEquals("0x00 matches no bytes", new byte[0], matcher.getMatchingBytes());

        matcher = new AnyBitmaskMatcher(b(0), true);
        expected = ByteUtils.getAllByteValues();
        assertArrayEquals("inverted 0x00 matches all bytes", expected, matcher.getMatchingBytes());

        matcher = new AnyBitmaskMatcher(b(254));
        expected = ByteUtils.getBytesInRange(2, 255);
        assertArrayEquals("0xFE matches everything except 1 and 0", expected, matcher.getMatchingBytes());

        matcher = new AnyBitmaskMatcher(b(254), true);
        expected = ByteUtils.getBytesInRange(0, 1);
        assertArrayEquals("inverted 0xFE matches 1 and 0", expected, matcher.getMatchingBytes());

        matcher = new AnyBitmaskMatcher(b(128));
        expected = ByteUtils.getBytesInRange(128, 255);
        assertArrayEquals("0x80 matches all bytes from 128 to 255", expected, matcher.getMatchingBytes());

        matcher = new AnyBitmaskMatcher(b(128), true);
        expected = ByteUtils.getBytesInRange(0, 127);
        assertArrayEquals("inverted 0x80 matches all bytes from 0 to 127", expected, matcher.getMatchingBytes());
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

        matcher = new AnyBitmaskMatcher(b(255));
        assertEquals("0xFF matches 255 bytes", 255, matcher.getNumberOfMatchingBytes());

        matcher = new AnyBitmaskMatcher(b(0), true);
        assertEquals("inverted 0x00 matches 256 bytes", 256, matcher.getNumberOfMatchingBytes());

        matcher = new AnyBitmaskMatcher(b(1), true);
        assertEquals("inverted 0x01 matches 128 bytes", 128, matcher.getNumberOfMatchingBytes());

        matcher = new AnyBitmaskMatcher(b(254), true);
        assertEquals("inverted 0xFE matches 2 bytes", 2, matcher.getNumberOfMatchingBytes());

        matcher = new AnyBitmaskMatcher(b(3), true);
        assertEquals("inverted 0x03 matches 64 bytes", 64, matcher.getNumberOfMatchingBytes());

        matcher = new AnyBitmaskMatcher(b(128), true);
        assertEquals("inverted 0x80 matches 128 bytes", 128, matcher.getNumberOfMatchingBytes());
    }

    private byte b(int i) {
        return (byte) i;
    }

}