/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class AnyByteMatcherTest {

    public AnyByteMatcherTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of matches method, of class AnyMatcher.
     */
    @Test
    public void testMatches_byte() {
        for (int count = 0; count < 256; count++) {
            AnyMatcher matcher = new AnyMatcher();
            String description = String.format("matches: 0x%02x", count);
            assertEquals(description, true, matcher.matches(b(count)));
        }
        SimpleTimer.timeMatcher("AnyMatcher", new AnyMatcher());
    }

    /**
     * Test of getMatchingBytes method, of class AnyMatcher.
     */
    @Test
    public void testGetMatchingBytes() {
       AnyMatcher matcher = new AnyMatcher();
       byte[] allBytes = ByteUtilities.getAllByteValues();
       assertArrayEquals(allBytes, matcher.getMatchingBytes());
    }

    /**
     * Test of toRegularExpression method, of class AnyMatcher.
     */
    @Test
    public void testToRegularExpression() {
        AnyMatcher matcher = new AnyMatcher();
        assertEquals(".", matcher.toRegularExpression(false));
    }


    /**
     * Test of getNumberOfMatchingBytes method, of class AnyMatcher.
     */
    @Test
    public void testGetNumberOfMatchingBytes() {
        AnyMatcher matcher = new AnyMatcher();
        assertEquals(256, matcher.getNumberOfMatchingBytes());
    }

    private byte b(int i) {
        return (byte) i;
    }

}