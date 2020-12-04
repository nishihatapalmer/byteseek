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

import net.byteseek.utils.ByteUtils;

import net.byteseek.matcher.sequence.SequenceMatcher;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class OneByteInvertedMatcherTest extends BaseMatcherTest {

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfNegativeInteger() {
        OneByteInvertedMatcher.valueOf(-1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfIntegerGreaterThan255() {
        OneByteInvertedMatcher.valueOf(256);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfCharBiggerThan255() {
        char theChar = (char) 256;
        OneByteInvertedMatcher.valueOf(theChar);
    }

    /**
     * Tests every possible byte value against every other non-matching
     * byte value.
     */
    @Test
    public void testMatcher() throws Exception {
 
        for (int i = 0; i < 256; i++) {
            final byte theByte = (byte) i;
            OneByteInvertedMatcher matcher = new OneByteInvertedMatcher(theByte);
            testMatcher(matcher, theByte, i);

            matcher = OneByteInvertedMatcher.valueOf(theByte);
            testMatcher(matcher, theByte, i);

            int intValue = theByte & 0xFF;
            matcher = OneByteInvertedMatcher.valueOf(intValue);
            testMatcher(matcher, theByte, i);

            char charValue = (char) intValue;
            matcher = OneByteInvertedMatcher.valueOf(charValue);
            testMatcher(matcher, theByte, i);

            String hexByte = ByteUtils.byteToString(false, i);
            hexByte = hexByte.toLowerCase();
            matcher = new OneByteInvertedMatcher(hexByte);
            testMatcher(matcher, theByte, i);

            hexByte = hexByte.toUpperCase();
            matcher = new OneByteInvertedMatcher(hexByte);
            testMatcher(matcher, theByte, i);

            testEquals(matcher, theByte);
        }
    }

    private void testEquals(OneByteInvertedMatcher matcher, byte theByte) {
        // Doesn't match null
        assertFalse(matcher.equals(null));

        // Does match an equivalent matcher of the same type
        OneByteInvertedMatcher same = new OneByteInvertedMatcher(theByte);
        assertTrue(matcher.equals(same));
        assertTrue(same.equals(matcher));
        assertEquals(matcher.hashCode(), same.hashCode());

        // Doesn't match a different matcher of the same type
        final byte differentByte = (byte) (((theByte & 0xFF) + 1) % 256);
        OneByteInvertedMatcher different = new OneByteInvertedMatcher(differentByte);
        assertFalse(matcher.equals(different));
        assertFalse(different.equals(matcher));

        // different object that matches the same bytes
        TwoByteInvertedMatcher differentObject = new TwoByteInvertedMatcher(theByte, theByte);
        assertFalse(matcher.equals(differentObject));
    }

    private void testMatcher(OneByteInvertedMatcher matcher, byte theByte, int index) throws Exception {
        // test methods from abstract superclass
        testAbstractMethods(matcher);

        assertEquals(theByte, matcher.getNonMatchingByteValue());

        // test methods implemented here.
        assertFalse("no matches byte value",      matcher.matches(theByte));
        assertFalse("no matches window reader",   matcher.matches(reader, index));
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
        assertTrue(toString.contains(OneByteInvertedMatcher.class.getSimpleName()));
        assertTrue(toString.contains(String.format("%02x", theByte & 0xFF)));

        SequenceMatcher repeated = matcher.repeat(1);
        assertEquals("repeat once is the same class", OneByteInvertedMatcher.class, repeated.getClass());
        final int REPEAT_NUM = 10;
        repeated = matcher.repeat(REPEAT_NUM);
        assertEquals("Repeated ten times length is correct", REPEAT_NUM, repeated.length());
        for (int i = 0; i < REPEAT_NUM; i++) {
            ByteMatcher bm = repeated.getMatcherForPosition(i);
            assertEquals("matcher matches 255 bytes", 255, bm.getNumberOfMatchingBytes());
        }

    }

}
