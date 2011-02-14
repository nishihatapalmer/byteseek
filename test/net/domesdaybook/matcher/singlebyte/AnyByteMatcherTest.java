/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
     * Test of matches method, of class AnyByteMatcher.
     */
    @Test
    public void testMatches_byte() {
        for (int count = 0; count < 256; count++) {
            AnyByteMatcher matcher = new AnyByteMatcher();
            String description = String.format("matches: 0x%02x", count);
            assertEquals(description, true, matcher.matches(b(count)));
        }
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