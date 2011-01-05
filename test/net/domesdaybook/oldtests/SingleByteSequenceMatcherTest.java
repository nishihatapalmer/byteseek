/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.matcher.sequence;

import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.reader.ByteReader;
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
public class SingleByteSequenceMatcherTest {

    public SingleByteSequenceMatcherTest() {
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
     * Test of matches method, of class SingleByteSequenceMatcher.
     */
    @Test
    public void testMatches() {
        System.out.println("matches");
        ByteReader reader = null;
        long matchFrom = 0L;
        SingleByteSequenceMatcher instance = null;
        boolean expResult = false;
        boolean result = instance.matches(reader, matchFrom);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getByteMatcherForPosition method, of class SingleByteSequenceMatcher.
     */
    @Test
    public void testGetByteMatcherForPosition() {
        System.out.println("getByteMatcherForPosition");
        int position = 0;
        SingleByteSequenceMatcher instance = null;
        SingleByteMatcher expResult = null;
        SingleByteMatcher result = instance.getByteMatcherForPosition(position);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of length method, of class SingleByteSequenceMatcher.
     */
    @Test
    public void testLength() {
        System.out.println("length");
        SingleByteSequenceMatcher instance = null;
        int expResult = 0;
        int result = instance.length();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toRegularExpression method, of class SingleByteSequenceMatcher.
     */
    @Test
    public void testToRegularExpression() {
        System.out.println("toRegularExpression");
        boolean prettyPrint = false;
        SingleByteSequenceMatcher instance = null;
        String expResult = "";
        String result = instance.toRegularExpression(prettyPrint);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}