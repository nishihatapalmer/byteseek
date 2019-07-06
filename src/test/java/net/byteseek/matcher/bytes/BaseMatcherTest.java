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

import net.byteseek.compiler.matcher.ByteMatcherCompiler;
import net.byteseek.io.reader.InputStreamReader;
import net.byteseek.io.reader.WindowReader;
import org.junit.Before;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

public class BaseMatcherTest {

    protected WindowReader reader;

    protected static byte[] BYTE_VALUES; // an array where each position contains the byte value corresponding to it.

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

    protected void testAbstractMethods(ByteMatcher matcher) throws Exception {
        // test methods from abstract superclass
        assertEquals("length is one", 1, matcher.length());

        assertEquals("matcher for position 0 is this", matcher, matcher.getMatcherForPosition(0));

        assertFalse(matcher.equals(null));
        Object something = new Object();
        assertFalse(matcher.equals(something));

        try {
            matcher.getMatcherForPosition(-1);
            fail("expected an IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expectedIgnore) {}

        try {
            matcher.getMatcherForPosition(1);
            fail("expected an IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expectedIgnore) {}

        try {
            matcher.getNumBytesAtPosition(-1);
            fail("expected an IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expectedIgnore) {}

        try {
            matcher.getNumBytesAtPosition(1);
            fail("expected an IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expectedIgnore) {}

        assertEquals(matcher.getNumBytesAtPosition(0), matcher.getNumberOfMatchingBytes());

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

        testRoundTrip(matcher);
    }

    protected void testRoundTrip(ByteMatcher matcher) throws Exception {
        if (!isDeprecated(matcher)) {
            String regex = matcher.toRegularExpression(false);

            ByteMatcher compiled = ByteMatcherCompiler.compileFrom(regex);

            String message = matcher.toString() + " " + regex;
            assertEquals(message, matcher.getNumberOfMatchingBytes(), compiled.getNumberOfMatchingBytes());

            byte[] match1 = matcher.getMatchingBytes();
            byte[] match2 = compiled.getMatchingBytes();
            assertEquals(message, match1.length, match2.length);
            for (int i = 0; i < match1.length; i++) {
                byte byte1 = match1[i];
                boolean found = false;
                for (int j = 0; j < match2.length; j++) {
                    if (byte1 == match2[j]) {
                        found = true;
                        break;
                    }
                }
                assertTrue(message, found);
            }

            if (compiled.getClass() == matcher.getClass()) {
                assertTrue(message, matcher.equals(compiled));
            }
            ;
        }
    }

    private boolean isDeprecated(ByteMatcher matcher) {
        return matcher.getClass() == AnyBitmaskMatcher.class ||
               matcher.getClass() == AllBitmaskMatcher.class;
    }

}
