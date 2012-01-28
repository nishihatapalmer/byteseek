/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.bytes;

import net.domesdaybook.matcher.bytes.ByteMatcher;
import net.domesdaybook.matcher.bytes.CaseInsensitiveByteMatcher;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class CaseInsensitiveByteMatcherTest {

    /**
     * 
     */
    public CaseInsensitiveByteMatcherTest() {
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
     * Test of matches method, of class CaseInsensitiveByteMatcher.
     */
    @Test
    public void testMatches_byte() {
        for (char value = 0; value < 128; value++) { // only support ASCII chars.
            CaseInsensitiveByteMatcher matcher = new CaseInsensitiveByteMatcher(value);
            if ((value >= 'a' && value <= 'z') ||
                (value >= 'A' && value <= 'Z')) {
                validateMatchesCase(matcher, value);
            } else {
                validateOnlyMatches(matcher, value);
            }
            if (value % 8 == 0) {
                final String message = String.format("Case insensitive byte value %c", value);
                SimpleTimer.timeMatcher(message, matcher);
            }
        }
    }

    private void validateOnlyMatches(ByteMatcher matcher, char value) {
        assertEquals(true, matcher.matches((byte) value));
        for (char noMatch = 0; noMatch < value; noMatch++) {
            assertEquals(false, matcher.matches((byte) noMatch));
        }
        char nextChar = (char) (value + (char) 1);
        for (char noMatch = nextChar; noMatch < (char) 128; noMatch++) {
            assertEquals(false, matcher.matches((byte) noMatch));
        }
    }

    private void validateMatchesCase(ByteMatcher matcher, char value) {
        char lowerCaseValue = Character.toLowerCase(value);
        char upperCaseValue = Character.toUpperCase(value);
        assertEquals(true, matcher.matches((byte) lowerCaseValue));
        assertEquals(true, matcher.matches((byte) upperCaseValue));
        for (char noMatch = 0; noMatch < 128; noMatch++) {
            if (noMatch != lowerCaseValue && noMatch != upperCaseValue) {
                assertEquals(false, matcher.matches((byte) noMatch));
            }
        }
    }
    

    /**
     * Test of getMatchingBytes method, of class CaseInsensitiveByteMatcher.
     */
    @Test
    public void testGetMatchingBytes() {
        for (char value = 0; value < 128; value++) { // only support ASCII chars.
            CaseInsensitiveByteMatcher matcher = new CaseInsensitiveByteMatcher(value);
            byte[] expected;
            if ((value >= 'a' && value <= 'z') ||
                (value >= 'A' && value <= 'Z')) {
                expected = new byte[] {(byte) Character.toLowerCase(value), (byte) Character.toUpperCase(value)};
            } else {
                expected = new byte[] {(byte) value};
            }
            assertArrayEquals(expected, matcher.getMatchingBytes());
        }
    }

    /**
     * Test of toRegularExpression method, of class CaseInsensitiveByteMatcher.
     */
    @Test
    public void testToRegularExpression() {
        for (char value = 0; value < 128; value++) { // only support ASCII chars.
            CaseInsensitiveByteMatcher matcher = new CaseInsensitiveByteMatcher(value);
            String expected = String.format("`%c`",value);
            assertEquals(expected, matcher.toRegularExpression(false));
        }
    }

    /**
     * Test of getNumberOfMatchingBytes method, of class CaseInsensitiveByteMatcher.
     */
    @Test
    public void testGetNumberOfMatchingBytes() {
        for (char value = 0; value < 128; value++) { // only support ASCII chars.
            CaseInsensitiveByteMatcher matcher = new CaseInsensitiveByteMatcher(value);
            int numMatching = 1;
            if ((value >= 'a' && value <= 'z') ||
                (value >= 'A' && value <= 'Z')) {
                numMatching = 2;
            }
            assertEquals(numMatching, matcher.getNumberOfMatchingBytes());
        }
    }

}