/*
 * Copyright Matt Palmer 2016-19, All rights reserved.
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

import net.byteseek.matcher.sequence.ByteMatcherSequenceMatcher;
import net.byteseek.matcher.sequence.ByteSequenceMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.utils.ByteUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TwoByteMatcherTest extends BaseMatcherTest {

    private int ASCII_CASE_GAP = 'a' - 'A';

    @Test
    public void testValueOf() throws IOException {
        for (int first = 0; first < 256; first++) {
            for (int second = 0; second < 256; second++) {
                ByteMatcher matcher = TwoByteMatcher.valueOf((byte) first, (byte) second);
                ByteMatcher another = TwoByteMatcher.valueOf((byte) first, (byte) second);

                if (first == second) {
                    assertEquals(OneByteMatcher.class, matcher.getClass());
                    assertTrue(matcher == another);
                } else {
                    assertEquals(TwoByteMatcher.class, matcher.getClass());

                    if (isCaseSensitive(first, second) || ByteUtils.isLineBreak((byte) first, (byte) second)) {
                        assertTrue(matcher == another); // same object if case sensitive.
                    } else {
                        assertFalse(matcher == another);
                    }
                }
            }
        }
    }

    private boolean isCaseSensitive(int first, int second) {
        if (first >= 'A' && first <= 'Z' && first + ASCII_CASE_GAP == second) {
            return true;
        }
        if (second >= 'A' && second <= 'Z' && second + ASCII_CASE_GAP == first) {
            return true;
        }
        return false;
    }

    private boolean isCaseSensitive(int value) {
        return (value >= 'A' && value <= 'Z') || (value >= 'a' && value <= 'z');
    }

    @Test
    public void testCaseSensitive() throws IOException {
        for (int value = 0; value < 256; value++) {
            boolean isCaseSensitive = isCaseSensitive(value);
            ByteMatcher matcher = TwoByteMatcher.caseInsensitive((byte) value);
            if (isCaseSensitive(value)) {
                assertEquals(TwoByteMatcher.class, matcher.getClass());
                ByteMatcher other = TwoByteMatcher.caseInsensitive((byte) value);
                assertTrue(matcher == other);
            } else {
                assertEquals(OneByteMatcher.class, matcher.getClass());
            }
        }
    }

    @Test
    public void testCaseSensitiveChar() throws IOException {
        for (char value = 0; value < 256; value++) {
            boolean isCaseSensitive = isCaseSensitive(value);
            ByteMatcher matcher = TwoByteMatcher.caseInsensitive(value);
            if (isCaseSensitive(value)) {
                assertEquals(TwoByteMatcher.class, matcher.getClass());
                ByteMatcher other = TwoByteMatcher.caseInsensitive(value);
                assertTrue(matcher == other);
            } else {
                assertEquals(OneByteMatcher.class, matcher.getClass());
            }
        }

        try {
            char big = 1000;
            ByteMatcher matcher = TwoByteMatcher.caseInsensitive(big);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expectedDoNothing) {}
    }

    @Test
    public void testTwoByteMatcher() throws Exception {
        for (int first = 0; first < 256; first++) {
            for (int second = 0; second < 256; second++) {
                TwoByteMatcher matcher = new TwoByteMatcher((byte) first, (byte) second);
                testTwoByteMatcher(matcher, first, second);

                matcher = new TwoByteMatcher(String.format("%02x", first), String.format("%02x", second));
                testTwoByteMatcher(matcher, first, second);

                List<Byte> bytes = new ArrayList<Byte>();
                bytes.add((byte)first);
                bytes.add((byte)second);
                matcher = new TwoByteMatcher(bytes);
                testTwoByteMatcher(matcher, first, second);

                testEquals(matcher, (byte) first, (byte) second);
            }
        }
    }


    private void testEquals(TwoByteMatcher matcher, byte first, byte second) {
        // Doesn't match null
        assertFalse(matcher.equals(null));

        // Does match an equivalent matcher of the same type
        TwoByteMatcher same = new TwoByteMatcher(first, second);
        assertTrue(matcher.equals(same));
        assertTrue(same.equals(matcher));
        assertEquals(matcher.hashCode(), same.hashCode());

        // And matches even if the bytes are given in the other order:
        same = new TwoByteMatcher(second, first);
        assertTrue(matcher.equals(same));
        assertTrue(same.equals(matcher));
        assertEquals(matcher.hashCode(), same.hashCode());

        // Doesn't match a different matcher of the same type
        final byte diffFirst = (byte) (((first & 0xFF) + 1) % 256);
        final byte diffSecond = (byte) (((second & 0xFF) + 1) % 256);

        TwoByteMatcher different = new TwoByteMatcher(diffFirst, diffSecond);
        assertFalse(matcher.equals(different));
        assertFalse(different.equals(matcher));

        // different object that
        TwoByteInvertedMatcher differentObject = new TwoByteInvertedMatcher(first, second);
        assertFalse(matcher.equals(differentObject));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyCollection() {
        ByteMatcher matcher = new TwoByteMatcher(new ArrayList<Byte>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullCollection() {
        ByteMatcher matcher = new TwoByteMatcher(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCollectionOneByte() {
        List<Byte> list = new ArrayList<Byte>();
        list.add((byte) 0);
        ByteMatcher matcher = new TwoByteMatcher(list);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCollectionThreeBytes() {
        List<Byte> list = new ArrayList<Byte>();
        list.add((byte) 0);
        list.add((byte) 20);
        list.add((byte) 82);
        ByteMatcher matcher = new TwoByteMatcher(list);
    }


    private void testTwoByteMatcher(ByteMatcher matcher, int first, int second) throws Exception {
        testAbstractMethods(matcher);

        int different = (first + 1) % 256;
        if (different == second) {
            different = (second + 1) % 256;
        }

        assertTrue(matcher.matches((byte) first));
        assertTrue(matcher.matches((byte) second));
        assertFalse(matcher.matches((byte) different));

        assertTrue(matcher.matches(reader, first));
        assertTrue(matcher.matches(reader, second));
        assertFalse(matcher.matches(reader, different));
        assertFalse(matcher.matches(reader, -1));
        assertFalse(matcher.matches(reader, 256));

        assertTrue(matcher.matches(BYTE_VALUES, first));
        assertTrue(matcher.matches(BYTE_VALUES, second));
        assertFalse(matcher.matches(BYTE_VALUES, different));
        assertFalse(matcher.matches(BYTE_VALUES, -1));
        assertFalse(matcher.matches(BYTE_VALUES, 256));

        assertTrue(matcher.matchesNoBoundsCheck(BYTE_VALUES, first));
        assertTrue(matcher.matchesNoBoundsCheck(BYTE_VALUES, second));
        assertFalse(matcher.matchesNoBoundsCheck(BYTE_VALUES, different));

        int numMatches = first == second? 1 : 2;
        assertEquals(numMatches, matcher.getNumberOfMatchingBytes());

        byte[] matchingBytes = matcher.getMatchingBytes();
        assertEquals(numMatches, matchingBytes.length);

        String toString = matcher.toString();
        assertTrue(toString.contains(TwoByteMatcher.class.getSimpleName()));
        assertTrue(toString.contains(String.format("%02x", first)));
        assertTrue(toString.contains(String.format("%02x", second)));

        String regex = matcher.toRegularExpression(false);
        assertTrue(regex.contains(String.format("%02x", first)));
        if (numMatches == 2) {
            assertTrue(regex.startsWith("["));
            assertTrue(regex.endsWith("]"));
            assertTrue(regex.contains(String.format("%02x", second)));
        }

        SequenceMatcher repeated = matcher.repeat(1);
        assertEquals(matcher, repeated);
        repeated = matcher.repeat(2);
        if (numMatches == 1) {
            assertEquals(ByteSequenceMatcher.class, repeated.getClass());
        } else if (numMatches == 2) {
            assertEquals(ByteMatcherSequenceMatcher.class, repeated.getClass());
        } else {
            fail("Must match either one or two bytes, have: " + numMatches);
        }
    }

}