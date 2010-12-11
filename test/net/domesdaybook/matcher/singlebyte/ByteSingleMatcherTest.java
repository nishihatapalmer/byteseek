/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.matcher.singlebyte;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class ByteSingleMatcherTest {

    public ByteSingleMatcherTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of matchess method, of class ByteSingleMatcher.
     */
    @Test
    public void testmatchess() {
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of matches method, of class ByteSingleMatcher.
     */
    @Test
    public void testmatches() {
        ByteMatcher matcher = new ByteMatcher((byte)00);
        assertEquals("zero byte matches zero byte", true, matcher.matches((byte) 0));
        assertEquals("zero byte does not match minimum byte", false, matcher.matches(Byte.MIN_VALUE));
        assertEquals("zero byte does not match maximum byte", false, matcher.matches(Byte.MAX_VALUE));

        matcher = new ByteMatcher(Byte.MIN_VALUE);
        assertEquals("min byte does not matche zero byte", false, matcher.matches((byte) 0));
        assertEquals("min byte does match minimum byte", true, matcher.matches(Byte.MIN_VALUE));
        assertEquals("min byte does not match maximum byte", false, matcher.matches(Byte.MAX_VALUE));

        matcher = new ByteMatcher(Byte.MAX_VALUE);
        assertEquals("max byte does not matche zero byte", false, matcher.matches((byte) 0));
        assertEquals("max byte does match minimum byte", false, matcher.matches(Byte.MIN_VALUE));
        assertEquals("max byte does not match maximum byte", true, matcher.matches(Byte.MAX_VALUE));
    }

    /**
     * Test of getBytesMatchingAt method, of class ByteSingleMatcher.
     */
    @Test
    public void testGetBytesMatchingAt() {
        ByteMatcher matcher = new ByteMatcher((byte) 20);
        byte[] result1 = {20};
//        assertArrayEquals("bytes matching at has correct value", result1, matcher.getBytesMatchingAt(0));
//        assertEquals("bytes matching at has one value", 1, matcher.getBytesMatchingAt(0).length);

        matcher = new ByteMatcher((byte) 00);
        byte[] result2 = {00};
//        assertArrayEquals("bytes matching at has correct value", result2, matcher.getBytesMatchingAt(0));
//        assertEquals("bytes matching at has one value", 1, matcher.getBytesMatchingAt(0).length);

        matcher = new ByteMatcher(Byte.MAX_VALUE);
        byte[] result3 = {Byte.MAX_VALUE};
//        assertArrayEquals("bytes matching at has correct value", result3, matcher.getBytesMatchingAt(0));
//        assertEquals("bytes matching at has one value", 1, matcher.getBytesMatchingAt(0).length);

        matcher = new ByteMatcher(Byte.MIN_VALUE);
        byte[] result4 = {Byte.MIN_VALUE};
//        assertArrayEquals("bytes matching at has correct value", result4, matcher.getBytesMatchingAt(0));
//        assertEquals("bytes matching at has one value", 1, matcher.getBytesMatchingAt(0).length);
    }


    /**
     * Test of toRegularExpression method, of class ByteSingleMatcher.
     */
    @Test
    public void testToRegularExpression() {
        ByteMatcher matcher = new ByteMatcher((byte) 15);
        assertEquals("single byte matcher 15 regular expression", "0f", matcher.toRegularExpression(false));
        assertEquals("single byte matcher 15 pretty regular expression", "0f ", matcher.toRegularExpression(true));

        matcher = new ByteMatcher((byte) 0);
        assertEquals("single byte matcher 00 regular expression", "00", matcher.toRegularExpression(false));
        assertEquals("single byte matcher 00 pretty regular expression", "00 ", matcher.toRegularExpression(true));

        matcher = new ByteMatcher((byte) 127);
        assertEquals("single byte matcher 127 regular expression", "7f", matcher.toRegularExpression(false));
        assertEquals("single byte matcher 127 pretty regular expression", "7f ", matcher.toRegularExpression(true));

    }

}