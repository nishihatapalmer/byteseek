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

import net.byteseek.io.reader.InputStreamReader;
import net.byteseek.io.reader.WindowReader;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class ByteRangeMatcherTest {

    private final static Random rand = new Random();

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

    /**
     * 
     * @throws Exception
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
        final long seed = System.currentTimeMillis();
        // final long seed = ?
        rand.setSeed(seed);
        System.out.println("Seeding random number generator with: " + Long.toString(seed));
        System.out.println("To repeat these exact tests, set the seed to the value above.");
    }     
    
    /**
     * 
     */
    public ByteRangeMatcherTest() {
    }


    /**
     * Test of matches method, of class ByteRangeMatcher.
     */
    @Test
    public void testByteRange() throws IOException {
        
        for (int testRun = 1; testRun <= 5; testRun++) {
            int start = rand.nextInt(256);
            System.out.println(String.format("%d of 5\tTesting byte ranges starting with %d", testRun, start));
            for (int end = 0; end < 256; end++) {
                ByteRangeMatcher matcherNotInverted = new ByteRangeMatcher(start, end, InvertibleMatcher.NOT_INVERTED);
                ByteRangeMatcher matcherInverted = new ByteRangeMatcher(start, end, InvertibleMatcher.INVERTED);
                validateMatcher(matcherNotInverted, start, end);
                validateMatcher(matcherInverted, start, end);
            }
        }
    }

    private void validateMatcher(ByteRangeMatcher matcher, int start, int end) throws IOException {
        // test methods from abstract superclass
       testAbstractMethods(matcher);

        assertFalse("No match in reader pos -1", matcher.matches(reader, -1));
        assertFalse("No match in reader past length", matcher.matches(reader, reader.length() + 1));
        assertFalse("No match in array pos -1", matcher.matches(BYTE_VALUES, -1));
        assertFalse("NO match in array past length", matcher.matches(BYTE_VALUES, 256));
        int startValue, endValue;
        if (start > end) {
            startValue = end;
            endValue = start;
        } else {
            startValue = start;
            endValue = end;
        }
        String toString = matcher.toString();
        assertTrue(toString.contains(ByteRangeMatcher.class.getSimpleName()));
        assertTrue(toString.contains("inverted"));
        assertTrue(toString.contains("start"));
        assertTrue(toString.contains("end"));

        String regex = String.format("%s%02x-%02x", matcher.isInverted()? "^" : "", startValue, endValue);
        assertEquals(regex, regex, matcher.toRegularExpression(false));
        String isInverted = matcher.isInverted()? "is" : "is not";
        int numberOfBytes = matcher.isInverted()? 255 - endValue + startValue : endValue - startValue + 1;
        assertEquals(String.format("Number of bytes for %d-%d, matcher %s inverted\t", start, end, isInverted), numberOfBytes, matcher.getNumberOfMatchingBytes());
        List<Byte> byteList = new ArrayList<Byte>();

        String message = "Testing value %d on range %d-%d, matcher %s inverted\t";
        for (int testvalue = 0; testvalue < startValue; testvalue++) {
            String testmessage = String.format(message, testvalue, start, end, isInverted);
            boolean matched = matcher.matches((byte) testvalue);
            assertEquals("match same for reader", matched, matcher.matches(reader, testvalue));
            assertEquals("match same for array", matched, matcher.matches(BYTE_VALUES, testvalue));
            assertEquals("match same for array no bounds check", matched, matcher.matchesNoBoundsCheck(BYTE_VALUES, testvalue));
            if (matched) { byteList.add((byte) testvalue); }
            assertEquals(testmessage, matcher.isInverted(), matched);
        }

        for (int testvalue = startValue; testvalue <= endValue; testvalue++) {
            String testmessage = String.format(message, testvalue, start, end, isInverted);
            boolean matched = matcher.matches((byte) testvalue);
            assertEquals("match same for reader", matched, matcher.matches(reader, testvalue));
            assertEquals("match same for array", matched, matcher.matches(BYTE_VALUES, testvalue));
            assertEquals("match same for array no bounds check", matched, matcher.matchesNoBoundsCheck(BYTE_VALUES, testvalue));
            if (matched) { byteList.add((byte) testvalue); }
            assertEquals(testmessage, !matcher.isInverted(), matched);
        }

        for (int testvalue = endValue+1; testvalue < 256; testvalue++) {
            String testmessage = String.format(message, testvalue, start, end, isInverted);
            boolean matched = matcher.matches((byte) testvalue);
            assertEquals("match same for reader", matched, matcher.matches(reader, testvalue));
            assertEquals("match same for array", matched, matcher.matches(BYTE_VALUES, testvalue));
            assertEquals("match same for array no bounds check", matched, matcher.matchesNoBoundsCheck(BYTE_VALUES, testvalue));
            if (matched) { byteList.add((byte) testvalue); }
            assertEquals(testmessage, matcher.isInverted(), matched);
        }

        byte[] bytes = new byte[byteList.size()];
        int pos = 0;
        for (Byte b : byteList) {
            bytes[pos++] = b;
        }
        message = String.format("Testing byte array on range %d-%d, matcher %s inverted", start, end, matcher.isInverted()? "" : " not");
        assertArrayEquals(message, bytes, matcher.getMatchingBytes() );
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