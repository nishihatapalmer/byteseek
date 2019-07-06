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

package net.byteseek.matcher.bytes;

import net.byteseek.matcher.sequence.ByteMatcherSequenceMatcher;
import net.byteseek.matcher.sequence.ByteSequenceMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TwoByteInvertedMatcherTest extends BaseMatcherTest {

    private int ASCII_CASE_GAP = 'a' - 'A';

    @Test
    public void testValueOf() throws IOException {
        for (int first = 0; first < 256; first++) {
            for (int second = 0; second < 256; second++) {
                ByteMatcher matcher = TwoByteInvertedMatcher.valueOf((byte) first, (byte) second);
                ByteMatcher another = TwoByteInvertedMatcher.valueOf((byte) first, (byte) second);

                if (first == second) {
                    assertEquals(OneByteInvertedMatcher.class, matcher.getClass());
                    assertTrue(matcher == another);
                } else {
                    assertEquals(TwoByteInvertedMatcher.class, matcher.getClass());

                    if (isCaseSensitive(first, second) || TwoByteMatcher.isLineBreak((byte) first, (byte) second)) {
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
            ByteMatcher matcher = TwoByteInvertedMatcher.caseInsensitive((byte) value);
            if (isCaseSensitive(value)) {
                assertEquals(TwoByteInvertedMatcher.class, matcher.getClass());
                ByteMatcher other = TwoByteInvertedMatcher.caseInsensitive((byte) value);
                assertTrue(matcher == other);
            } else {
                assertEquals(OneByteInvertedMatcher.class, matcher.getClass());
            }
        }
    }

    @Test
    public void testCaseSensitiveChar() throws IOException {
        for (char value = 0; value < 256; value++) {
            boolean isCaseSensitive = isCaseSensitive(value);
            ByteMatcher matcher = TwoByteInvertedMatcher.caseInsensitive(value);
            if (isCaseSensitive(value)) {
                assertEquals(TwoByteInvertedMatcher.class, matcher.getClass());
                ByteMatcher other = TwoByteInvertedMatcher.caseInsensitive(value);
                assertTrue(matcher == other);
            } else {
                assertEquals(OneByteInvertedMatcher.class, matcher.getClass());
            }
        }

        try {
            char big = 1000;
            ByteMatcher matcher = TwoByteInvertedMatcher.caseInsensitive(big);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expectedDoNothing) {}
    }

    @Test
    public void testTwoByteInvertedMatcher() throws Exception {
        for (int first = 0; first < 256; first++) {
            for (int second = 0; second < 256; second++) {
                TwoByteInvertedMatcher matcher = new TwoByteInvertedMatcher((byte) first, (byte) second);
                testTwoByteInvertedMatcher(matcher, first, second);

                matcher = new TwoByteInvertedMatcher(String.format("%02x", first), String.format("%02x", second));
                testTwoByteInvertedMatcher(matcher, first, second);

                List<Byte> bytes = new ArrayList<Byte>();
                bytes.add((byte)first);
                bytes.add((byte)second);
                matcher = new TwoByteInvertedMatcher(bytes);
                testTwoByteInvertedMatcher(matcher, first, second);

                testEquals(matcher, (byte) first, (byte) second);
            }
        }
    }

    private void testEquals(TwoByteInvertedMatcher matcher, byte first, byte second) {
        // Doesn't match null
        assertFalse(matcher.equals(null));

        // Does match an equivalent matcher of the same type
        TwoByteInvertedMatcher same = new TwoByteInvertedMatcher(first, second);
        assertTrue(matcher.equals(same));
        assertTrue(same.equals(matcher));
        assertEquals(matcher.hashCode(), same.hashCode());

        // And matches even if the bytes are given in the other order:
        same = new TwoByteInvertedMatcher(second, first);
        assertTrue(matcher.equals(same));
        assertTrue(same.equals(matcher));
        assertEquals(matcher.hashCode(), same.hashCode());

        // Doesn't match a different matcher of the same type
        final byte diffFirst = (byte) (((first & 0xFF) + 1) % 256);
        final byte diffSecond = (byte) (((second & 0xFF) + 1) % 256);

        TwoByteInvertedMatcher different = new TwoByteInvertedMatcher(diffFirst, diffSecond);
        assertFalse(matcher.equals(different));
        assertFalse(different.equals(matcher));

        // different object that
        TwoByteMatcher differentObject = new TwoByteMatcher(first, second);
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


    private void testTwoByteInvertedMatcher(TwoByteInvertedMatcher matcher, int first, int second) throws Exception {
        testAbstractMethods(matcher);

        int different = (first + 1) % 256;
        if (different == second) {
            different = (second + 1) % 256;
        }

        assertFalse(matcher.matches((byte) first));
        assertFalse(matcher.matches((byte) second));
        assertTrue(matcher.matches((byte) different));

        assertFalse(matcher.matches(reader, first));
        assertFalse(matcher.matches(reader, second));
        assertTrue(matcher.matches(reader, different));

        assertFalse(matcher.matches(reader, -1));
        assertFalse(matcher.matches(reader, 256));

        assertFalse(matcher.matches(BYTE_VALUES, first));
        assertFalse(matcher.matches(BYTE_VALUES, second));
        assertTrue(matcher.matches(BYTE_VALUES, different));

        assertFalse(matcher.matches(BYTE_VALUES, -1));
        assertFalse(matcher.matches(BYTE_VALUES, 256));

        assertFalse(matcher.matchesNoBoundsCheck(BYTE_VALUES, first));
        assertFalse(matcher.matchesNoBoundsCheck(BYTE_VALUES, second));
        assertTrue(matcher.matchesNoBoundsCheck(BYTE_VALUES, different));

        int numMatches = first == second? 255 : 254;
        assertEquals(numMatches, matcher.getNumberOfMatchingBytes());

        byte[] matchingBytes = matcher.getMatchingBytes();
        assertEquals(numMatches, matchingBytes.length);

        String toString = matcher.toString();
        assertTrue(toString.contains(TwoByteInvertedMatcher.class.getSimpleName()));
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
        if (numMatches == 255) {
            assertEquals(ByteMatcherSequenceMatcher.class, repeated.getClass());
        } else if (numMatches == 254) {
            assertEquals(ByteMatcherSequenceMatcher.class, repeated.getClass());
        } else {
            fail("Must match either one or two bytes, have: " + numMatches);
        }
    }

    @Test
    public void testRepeatOnce() throws Exception {
        TwoByteInvertedMatcher matcher = new TwoByteInvertedMatcher((byte) 0x01, (byte) 0x04);
        assertTrue(matcher == matcher.repeat(1));

        matcher = new TwoByteInvertedMatcher((byte) 0x0A, (byte) 0x0A);
        assertTrue(matcher == matcher.repeat(1));
    }

    @Test
    public void testRepeatInvertedSingleByte() throws Exception {
        TwoByteInvertedMatcher matcher = new TwoByteInvertedMatcher((byte) 0x0A, (byte) 0x0A);
        SequenceMatcher sequence = matcher.repeat(10);
        for (int i = 0; i < 10; i++) {
            ByteMatcher aMatcher = sequence.getMatcherForPosition(i);
            assertEquals(255, aMatcher.getNumberOfMatchingBytes());
        }
    }

    @Test
    public void testRepeatInvertedTwoBytes() throws Exception {
        TwoByteInvertedMatcher matcher = new TwoByteInvertedMatcher((byte) 0x01, (byte) 0x04);
        SequenceMatcher sequence = matcher.repeat(10);
        for (int i = 0; i < 10; i++) {
            ByteMatcher aMatcher = sequence.getMatcherForPosition(i);
            assertEquals(254, aMatcher.getNumberOfMatchingBytes());
        }
    }

    @Test
    public void testhashCode() throws Exception {
    }

    @Test
    public void equals() throws Exception {
    }

    @Test
    public void testtoString() throws Exception {
    }

}