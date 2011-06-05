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
public class ByteSetBinarySearchMatcherTest {

    public ByteSetBinarySearchMatcherTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of matches method, of class ByteSetBinarySearchMatcher.
     */
    @Test
    public void testMatches_byte() {
        System.out.println("matches");
        byte theByte = 0;
        ByteSetBinarySearchMatcher instance = null;
        boolean expResult = false;
        boolean result = instance.matches(theByte);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMatchingBytes method, of class ByteSetBinarySearchMatcher.
     */
    @Test
    public void testGetMatchingBytes() {
        System.out.println("getMatchingBytes");
        ByteSetBinarySearchMatcher instance = null;
        byte[] expResult = null;
        byte[] result = instance.getMatchingBytes();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNumberOfMatchingBytes method, of class ByteSetBinarySearchMatcher.
     */
    @Test
    public void testGetNumberOfMatchingBytes() {
        System.out.println("getNumberOfMatchingBytes");
        ByteSetBinarySearchMatcher instance = null;
        int expResult = 0;
        int result = instance.getNumberOfMatchingBytes();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toRegularExpression method, of class ByteSetBinarySearchMatcher.
     */
    @Test
    public void testToRegularExpression() {
        System.out.println("toRegularExpression");
        boolean prettyPrint = false;
        ByteSetBinarySearchMatcher instance = null;
        String expResult = "";
        String result = instance.toRegularExpression(prettyPrint);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}