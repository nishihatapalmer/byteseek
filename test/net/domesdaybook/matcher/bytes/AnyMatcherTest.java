/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.bytes;

import net.domesdaybook.matcher.bytes.AnyByteMatcher;
import net.domesdaybook.bytes.ByteUtilities;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class AnyMatcherTest {

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
    public void testMatches_byte() {
        for (int count = 0; count < 256; count++) {
            AnyByteMatcher matcher = new AnyByteMatcher();
            String description = String.format("matches: 0x%02x", count);
            assertEquals(description, true, matcher.matches(b(count)));
        }
        SimpleTimer.timeMatcher("AnyMatcher", new AnyByteMatcher());
    }

    /**
     * Test of getMatchingBytes method, of class AnyByteMatcher.
     */
    @Test
    public void testGetMatchingBytes() {
       AnyByteMatcher matcher = new AnyByteMatcher();
       byte[] allBytes = ByteUtilities.getAllByteValues();
       assertArrayEquals(allBytes, matcher.getMatchingBytes());
    }

    /**
     * Test of toRegularExpression method, of class AnyByteMatcher.
     */
    @Test
    public void testToRegularExpression() {
        AnyByteMatcher matcher = new AnyByteMatcher();
        assertEquals(".", matcher.toRegularExpression(false));
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