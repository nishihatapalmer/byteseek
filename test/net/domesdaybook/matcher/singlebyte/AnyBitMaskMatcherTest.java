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
public class AnyBitMaskMatcherTest {

    public AnyBitMaskMatcherTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }


    /**
     * Test of matches method, of class AnyBitMaskMatcher.
     */
    @Test
    public void testMatches_byte() {
        AnyBitMaskMatcher matcher = new AnyBitMaskMatcher(b(255));
        validateMatchInRange(matcher, 1, 255);
        validateNoMatchInRange(matcher, 0, 0);

        matcher = new AnyBitMaskMatcher(b(0));
        validateNoMatchInRange(matcher, 0, 255);

        matcher = new AnyBitMaskMatcher(b(254));
        validateMatchInRange(matcher, 2, 255);
        validateNoMatchInRange(matcher, 0, 1);

        matcher = new AnyBitMaskMatcher(b(128));
        validateMatchInRange(matcher, 128, 255);
        validateNoMatchInRange(matcher, 0, 127);

        // test all bit masks using different methods.
        for (int mask = 0; mask < 256; mask++) {
            matcher = new AnyBitMaskMatcher(b(mask));
            validateMatchBitsSet(matcher, b(mask));
            validateNoMatchBitsNotSet(matcher, b(mask));
        }
    }

    private void validateNoMatchInRange(AnyBitMaskMatcher matcher, int from, int to) {
        String description = String.format("%d:%d", from, to);
        for (int count = from; count <= to; count++) {
            String d2 = String.format("%s(%d)", description, count);
            assertEquals(d2, false, matcher.matches(b(count)));
        }
    }

    private void validateMatchInRange(AnyBitMaskMatcher matcher, int from, int to) {
        String description = String.format("%d:%d", from, to);
        for (int count = from; count <= to; count++) {
            String d2 = String.format("%s(%d)", description, count);
            assertEquals(d2, true, matcher.matches(b(count)));
        }
    }

    private void validateMatchBitsSet(AnyBitMaskMatcher matcher, int bitmask) {
        if (bitmask > 0) { // This test won't work for a zero bitmask.
            String description = String.format("0x%02x", bitmask);
            for (int count = 0; count < 256; count++) {
                String d2 = String.format("%s(%d)", description, count);
                byte value = (byte) (count | bitmask);
                assertEquals(d2, true, matcher.matches(value));
            }
        }
    }

    private void validateNoMatchBitsNotSet(AnyBitMaskMatcher matcher, int bitmask) {
        String description = String.format("0x%02x", bitmask);
        final int invertedMask = bitmask ^ 0xFF;
        for (int count = 0; count < 256; count++) { // zero byte matches everything.
            String d2 = String.format("%s(%d)", description, count);
            byte value = (byte) (count & invertedMask);
            assertEquals(d2, false, matcher.matches(value));
        }
    }


    /**
     * Test of toRegularExpression method, of class AnyBitMaskMatcher.
     */
    @Test
    public void testToRegularExpression() {
        for (int count = 0; count < 256; count++) {
            AnyBitMaskMatcher matcher = new AnyBitMaskMatcher(b(count));
            String expected = String.format("~%02x", count);
            assertEquals(expected, matcher.toRegularExpression(false));
        }
    }

    /**
     * Test of getMatchingBytes method, of class AnyBitMaskMatcher.
     */
    @Test
    public void testGetMatchingBytes() {
        AnyBitMaskMatcher matcher = new AnyBitMaskMatcher(b(255));
        byte[] expected = ByteUtilities.getBytesInRange(1, 255);
        assertArrayEquals("0xFF matches all bytes except zero",
                expected, matcher.getMatchingBytes());

        matcher = new AnyBitMaskMatcher(b(0));
        assertArrayEquals("0x00 matches no bytes", new byte[0], matcher.getMatchingBytes());

        matcher = new AnyBitMaskMatcher(b(254));
        expected = ByteUtilities.getBytesInRange(2, 255);
        assertArrayEquals("0xFE matches everything except 1 and 0", expected, matcher.getMatchingBytes());

        matcher = new AnyBitMaskMatcher(b(128));
        expected = ByteUtilities.getBytesInRange(128, 255);
        assertArrayEquals("0x80 matches all bytes from 128 to 255", expected, matcher.getMatchingBytes());
    }

    /**
     * Test of getNumberOfMatchingBytes method, of class AnyBitMaskMatcher.
     */
    @Test
    public void testGetNumberOfMatchingBytes() {
        AnyBitMaskMatcher matcher = new AnyBitMaskMatcher(b(255));
        assertEquals("0xFF matches 255 bytes", 255, matcher.getNumberOfMatchingBytes());

        matcher = new AnyBitMaskMatcher(b(0));
        assertEquals("0x00 matches 0 bytes", 0, matcher.getNumberOfMatchingBytes());

        matcher = new AnyBitMaskMatcher(b(1));
        assertEquals("0x01 matches 128 bytes", 128, matcher.getNumberOfMatchingBytes());

        matcher = new AnyBitMaskMatcher(b(254));
        assertEquals("0xFE matches 254 bytes", 254, matcher.getNumberOfMatchingBytes());

        matcher = new AnyBitMaskMatcher(b(3));
        assertEquals("0x03 matches 192 bytes", 192, matcher.getNumberOfMatchingBytes());

        matcher = new AnyBitMaskMatcher(b(128));
        assertEquals("0x80 matches 128 bytes", 128, matcher.getNumberOfMatchingBytes());

    }

    private byte b(int i) {
        return (byte) i;
    }

}