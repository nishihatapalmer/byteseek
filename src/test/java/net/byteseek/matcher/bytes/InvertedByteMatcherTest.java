/*
 * Copyright Matt Palmer 2011-2016, All rights reserved.
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
import net.byteseek.utils.ByteUtils;
import net.byteseek.io.reader.WindowReader;

import net.byteseek.matcher.sequence.SequenceMatcher;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class InvertedByteMatcherTest {

    private WindowReader reader;

    private static byte[] BYTE_VALUES; // an array where each position contains the byte value corresponding to it.

    static {
        BYTE_VALUES = new byte[256];
        for (int i = 0; i < 256; i++) {
            BYTE_VALUES[i] = (byte) i;
        }
    }

    /**
     *
     */
    public InvertedByteMatcherTest() {
    }

    @Before
    public void setup() {
        reader = new InputStreamReader(new ByteArrayInputStream(BYTE_VALUES));
    }

    /**
     * Tests every possible byte value against every other non-matching
     * byte value.
     */
    @Test
    public void testMatcher() throws IOException {
 
        for (int i = 0; i < 256; i++) {
            final byte theByte = (byte) i;
            InvertedByteMatcher matcher = new InvertedByteMatcher(theByte);
            testMatcher(matcher, theByte, i);

            String hexByte = ByteUtils.byteToString(false, i);
            hexByte = hexByte.toLowerCase();
            matcher = new InvertedByteMatcher(hexByte);
            testMatcher(matcher, theByte, i);

            hexByte = hexByte.toUpperCase();
            matcher = new InvertedByteMatcher(hexByte);
            testMatcher(matcher, theByte, i);
        }

    }

    private void testMatcher(ByteMatcher matcher, byte theByte, int index) throws IOException {
        // test methods from abstract superclass
        testAbstractMethods(matcher);

        // test methods implemented here.
        assertFalse("no matche byte value",      matcher.matches(theByte));
        assertFalse("no matche window reader",   matcher.matches(reader, index));
        assertFalse("no matches array",           matcher.matches(BYTE_VALUES, index));
        assertFalse("no match array -1",      matcher.matches(BYTE_VALUES, -1));
        assertFalse("no match array 256",     matcher.matches(BYTE_VALUES, 256));
        assertFalse("no matches no bounds check", matcher.matchesNoBoundsCheck(BYTE_VALUES, index));
        try {
            matcher.matchesNoBoundsCheck(BYTE_VALUES, -1);
            fail("Expected an ArrayIndexOutOfBoundsException at pos -1");
        } catch(ArrayIndexOutOfBoundsException expectedIgnore) {}
        try {
            matcher.matchesNoBoundsCheck(BYTE_VALUES, 256);
            fail("Expected an ArrayIndexOutOfBoundsException at pos 256");
        } catch(ArrayIndexOutOfBoundsException expectedIgnore) {}
        assertEquals("255 byte matches", 255, matcher.getNumberOfMatchingBytes());

        byte[] bytesMatched = matcher.getMatchingBytes();
        assertEquals("255 byte matches in array", 255, bytesMatched.length);
        Set<Byte> bytesFound = new HashSet<Byte>();
        for (byte b : bytesMatched) {
            bytesFound.add(b);
        }
        assertEquals("255 distinct values matched", 255, bytesFound.size());
        assertFalse("does not contain " + theByte, bytesFound.contains(theByte));

        final String regularExpression = String.format("^%02x", theByte);
        assertEquals("regular expression", regularExpression, matcher.toRegularExpression(false));

        for (int x = 0; x < 256; x++) {
            if (x != index) {
                final byte nomatch = (byte) x;
                assertTrue("match byte value", matcher.matches(nomatch));
                assertTrue("match reader", matcher.matches(reader, x));
                assertTrue("match array", matcher.matches(BYTE_VALUES, x));
                assertTrue("match array", matcher.matchesNoBoundsCheck(BYTE_VALUES, x));
            }
        }
        if (index % 32 == 0) {
            String message = String.format("Matching byte %d", index);
            SimpleTimer.timeMatcher(message, matcher);
        }
        String toString = matcher.toString();
        assertTrue(toString.contains(InvertedByteMatcher.class.getSimpleName()));
        assertTrue(toString.contains(String.format("%02x", theByte & 0xFF)));

        SequenceMatcher repeated = matcher.repeat(1);
        assertEquals("repeat once is the same class", InvertedByteMatcher.class, repeated.getClass());
        final int REPEAT_NUM = 10;
        repeated = matcher.repeat(REPEAT_NUM);
        assertEquals("Repeated ten times length is correct", REPEAT_NUM, repeated.length());
        for (int i = 0; i < REPEAT_NUM; i++) {
            ByteMatcher bm = repeated.getMatcherForPosition(i);
            assertEquals("matcher matches 255 bytes", 255, bm.getNumberOfMatchingBytes());
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
