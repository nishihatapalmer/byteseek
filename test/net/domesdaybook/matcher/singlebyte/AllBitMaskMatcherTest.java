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
        validateMatch(matcher, 255, 255);
        validateNoMatch(matcher, 0, 254);
        
        matcher = new AllBitMaskMatcher(b(0));
        validateMatch(matcher, 0, 256);
        
        matcher = new AllBitMaskMatcher(b(254));
        validateMatch(matcher, 254, 255);
        validateNoMatch(matcher, 0, 253);

        matcher = new AllBitMaskMatcher(b(128));
        validateMatch(matcher, 128, 255);
        validateNoMatch(matcher, 0, 127);

    }

    private void validateNoMatch(AllBitMaskMatcher matcher, int from, int to) {
        for (int count = from; count <= to; count++) {
            assertEquals(false, matcher.matches(b(count)));
        }
    }

    private void validateMatch(AllBitMaskMatcher matcher, int from, int to) {
        for (int count = from; count <= to; count++) {
            assertEquals(true, matcher.matches(b(count)));
        }
    }



    /**
     * Test of toRegularExpression method, of class AllBitMaskMatcher.
     */
    @Test
    public void testToRegularExpression() {
        for (int count = 0; count < 256; count++) {
            AllBitMaskMatcher matcher = new AllBitMaskMatcher(b(count));
            String expected = String.format("&%02x", count);
            assertEquals(expected, matcher.toRegularExpression(false));
        }
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

        matcher = new AllBitMaskMatcher(b(254));
        byte[] expected = new byte[] {b(254), b(255)};
        assertArrayEquals("0xFE matches 0xFF and 0xFE only", expected, matcher.getMatchingBytes());

        matcher = new AllBitMaskMatcher(b(128));
        expected = ByteUtilities.getBytesInRange(128, 255);
        assertArrayEquals("0x80 matches all bytes from 128 to 255", expected, matcher.getMatchingBytes());

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

        matcher = new AllBitMaskMatcher(b(254));
        assertEquals("0xFE matches 2 bytes", 2, matcher.getNumberOfMatchingBytes());

        matcher = new AllBitMaskMatcher(b(128));
        assertEquals("0x80 matches 128 bytes", 128, matcher.getNumberOfMatchingBytes());

    }

    private byte b(int i) {
        return (byte) i;
    }

}