/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.reader.ByteReader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class AllBitMaskMatcherTest {

    public AllBitMaskMatcherTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }


    /**
     * Test of matches method, of class AllBitMaskMatcher.
     */
    @Test
    public void testMatches_byte() {
        AllBitMaskMatcher matcher = new AllBitMaskMatcher(b(255));
        assertEquals("0xFF matches 0xFF", true, matcher.matches(b(255)));
        for (int count = 0; count < 255; count++) {
            final String message = String.format("0xFF does not match %d", count);
            assertEquals(message, false, matcher.matches(b(count)));
        }

        matcher = new AllBitMaskMatcher(b(0));
        for (int count = 0; count < 256; count++) {
            final String message = String.format("0x00 matches %d", count);
            assertEquals(message, true, matcher.matches(b(count)));
        }

        

    }



    /**
     * Test of toRegularExpression method, of class AllBitMaskMatcher.
     */
    @Test
    public void testToRegularExpression() {
        AllBitMaskMatcher matcher = new AllBitMaskMatcher(b(255));
        assertEquals("0xFF equals &ff", "&ff", matcher.toRegularExpression(false));

        matcher = new AllBitMaskMatcher(b(0));
        assertEquals("0xFF equals &00", "&00", matcher.toRegularExpression(false));
    }



    /**
     * Test of getMatchingBytes method, of class AllBitMaskMatcher.
     */
    @Test
    public void testGetMatchingBytes() {
        AllBitMaskMatcher matcher = new AllBitMaskMatcher(b(255));
        assertArrayEquals("0xFF matches [0xFF] only",
                new byte[] {b(255)},
                matcher.getMatchingBytes());

        matcher = new AllBitMaskMatcher(b(0));
        assertArrayEquals("0x00 matches all bytes", ByteUtilities.getAllByteValues(), matcher.getMatchingBytes());


    }

    /**
     * Test of getNumberOfMatchingBytes method, of class AllBitMaskMatcher.
     */
    @Test
    public void testGetNumberOfMatchingBytes() {
        AllBitMaskMatcher matcher = new AllBitMaskMatcher(b(255));
        assertEquals("0xFF matches one byte", 1, matcher.getNumberOfMatchingBytes());

        matcher = new AllBitMaskMatcher(b(0));
        assertEquals("0x00 matches 256 bytes", 256, matcher.getNumberOfMatchingBytes());

    }

    private byte b(int i) {
        return (byte) i;
    }

}