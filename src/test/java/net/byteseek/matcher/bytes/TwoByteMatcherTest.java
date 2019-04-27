/*
 * Copyright Matt Palmer 2016, All rights reserved.
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

import net.byteseek.io.reader.InputStreamReader;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.sequence.ByteMatcherSequenceMatcher;
import net.byteseek.matcher.sequence.ByteSequenceMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class TwoByteMatcherTest {

    private WindowReader reader;

    private static byte[] BYTE_VALUES; // an array where each position contains the byte value corresponding to it.

    static {
        BYTE_VALUES = new byte[256];
        for (int i = 0; i < 256; i++) {
            BYTE_VALUES[i] = (byte) i;
        }
    }

    @Before
    public void setup() {
        reader = new InputStreamReader(new ByteArrayInputStream(BYTE_VALUES));
    }

    @Test
    public void testTwoByteMatcher() throws IOException {
        for (int first = 0; first < 256; first++) {
            for (int second = 0; second < 256; second++) {
                ByteMatcher matcher = new TwoByteMatcher((byte) first, (byte) second);
                testTwoByteMatcher(matcher, first, second);

                matcher = new TwoByteMatcher(String.format("%02x", first), String.format("%02x", second));
                testTwoByteMatcher(matcher, first, second);

                List<Byte> bytes = new ArrayList<Byte>();
                bytes.add((byte)first);
                bytes.add((byte)second);
                matcher = new TwoByteMatcher(bytes);
                testTwoByteMatcher(matcher, first, second);
            }
        }
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


    private void testTwoByteMatcher(ByteMatcher matcher, int first, int second) throws IOException {
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



    private void testAbstractMethods(ByteMatcher matcher) {
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

        assertEquals("reversed is identical", matcher, matcher.reverse());
        assertEquals("subsequence of 0 is identical", matcher, matcher.subsequence(0));
        try {
            matcher.subsequence(-1);
            fail("expected an IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expectedIgnore) {}

        try {
            matcher.subsequence(1);
            fail("expected an IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expectedIgnore) {}
        assertEquals("subsequence of 0,1 is identical", matcher, matcher.subsequence(0,1));
        try {
            matcher.subsequence(-1, 1);
            fail("expected an IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expectedIgnore) {}

        try {
            matcher.subsequence(0, 2);
            fail("expected an IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expectedIgnore) {}

        int count = 0;
        for (ByteMatcher itself : matcher) {
            count++;
            assertEquals("Iterating returns same matcher", matcher, itself);
        }
        assertEquals("Count of iterated matchers is one", 1, count);

        Iterator<ByteMatcher> it = matcher.iterator();
        try {
            it.remove();
            fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException expectedIgnore) {}


        it = matcher.iterator();
        try {
            assertTrue(it.hasNext());
            it.next();
            assertFalse(it.hasNext());
            it.next();
            fail("Expected NoSuchElementException");
        } catch (NoSuchElementException expectedIgnore) {}
    }

}