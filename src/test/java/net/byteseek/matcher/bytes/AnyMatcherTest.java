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

import net.byteseek.utils.ByteUtils;
import net.byteseek.io.reader.ByteArrayReader;
import net.byteseek.io.reader.WindowReader;

import net.byteseek.matcher.sequence.FixedGapMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class AnyMatcherTest {


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
        reader = new ByteArrayReader(BYTE_VALUES);
    }

    /**
     * 
     */
    public AnyMatcherTest() {
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
     * Test of matches method, of class AnyByteMatcher.
     */
    @Test
    public void testMatches_byte() throws IOException {
        for (int count = 0; count < 256; count++) {
            AnyByteMatcher matcher = new AnyByteMatcher();
            String description = String.format("matches: 0x%02x", count);
            assertEquals(description, true, matcher.matches(b(count)));
            assertEquals(description, true, matcher.matches(reader, count));
            assertEquals(description, true, matcher.matches(BYTE_VALUES, count));
            assertEquals(description, true, matcher.matchesNoBoundsCheck(BYTE_VALUES, count));

        }
        SimpleTimer.timeMatcher("AnyMatcher", new AnyByteMatcher());
    }

    @Test
    public void testLength() {
        assertEquals("length is one", 1, new AnyByteMatcher().length());
    }

    @Test
    public void testToString() {
        String toString = new AnyByteMatcher().toString();
        assertTrue(toString.contains(AnyByteMatcher.class.getSimpleName()));
    }

    @Test
    public void testRepeat() {
        SequenceMatcher matcher = new AnyByteMatcher().repeat(1);
        assertEquals(AnyByteMatcher.class, matcher.getClass());

        for (int i = 2; i < 10; i++) {
            matcher = new AnyByteMatcher().repeat(i);
            assertEquals("Length is " + i, i, matcher.length());
            assertEquals("Class is a fixed gap", FixedGapMatcher.class, matcher.getClass());
        }
    }

    /**
     * Test of getMatchingBytes method, of class AnyByteMatcher.
     */
    @Test
    public void testGetMatchingBytes() {
       AnyByteMatcher matcher = new AnyByteMatcher();
       byte[] allBytes = ByteUtils.getAllByteValues();
       assertArrayEquals(allBytes, matcher.getMatchingBytes());
    }

    /**
     * Test of toRegularExpression method, of class AnyByteMatcher.
     */
    @Test
    public void testToRegularExpression() {
        AnyByteMatcher matcher = new AnyByteMatcher();
        assertEquals(".", matcher.toRegularExpression(false));
        assertEquals(".", matcher.toRegularExpression(true));
    }


    /**
     * Test of getNumberOfMatchingBytes method, of class AnyByteMatcher.
     */
    @Test
    public void testGetNumberOfMatchingBytes() {
        AnyByteMatcher matcher = new AnyByteMatcher();
        assertEquals(256, matcher.getNumberOfMatchingBytes());
    }

    private byte b(int i) {
        return (byte) i;
    }

}