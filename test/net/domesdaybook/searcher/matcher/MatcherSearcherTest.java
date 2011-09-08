/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.domesdaybook.searcher.matcher;

import net.domesdaybook.reader.ArrayProvider;
import net.domesdaybook.reader.ByteReader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class MatcherSearcherTest {
    
    public MatcherSearcherTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of searchForwards method, of class MatcherSearcher.
     */
    @Test
    public void testSearchForwards_3args_1() {
        System.out.println("searchForwards");
        ArrayProvider provider = null;
        long fromPosition = 0L;
        long toPosition = 0L;
        MatcherSearcher instance = null;
        long expResult = 0L;
        long result = instance.searchForwards(provider, fromPosition, toPosition);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of searchForwards method, of class MatcherSearcher.
     */
    @Test
    public void testSearchForwards_3args_2() {
        System.out.println("searchForwards");
        ByteReader reader = null;
        long fromPosition = 0L;
        long toPosition = 0L;
        MatcherSearcher instance = null;
        long expResult = 0L;
        long result = instance.searchForwards(reader, fromPosition, toPosition);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of searchForwards method, of class MatcherSearcher.
     */
    @Test
    public void testSearchForwards_3args_3() {
        System.out.println("searchForwards");
        byte[] bytes = null;
        int fromPosition = 0;
        int toPosition = 0;
        MatcherSearcher instance = null;
        int expResult = 0;
        int result = instance.searchForwards(bytes, fromPosition, toPosition);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of searchBackwards method, of class MatcherSearcher.
     */
    @Test
    public void testSearchBackwards_3args_1() {
        System.out.println("searchBackwards");
        ArrayProvider provider = null;
        long fromPosition = 0L;
        long toPosition = 0L;
        MatcherSearcher instance = null;
        long expResult = 0L;
        long result = instance.searchBackwards(provider, fromPosition, toPosition);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of searchBackwards method, of class MatcherSearcher.
     */
    @Test
    public void testSearchBackwards_3args_2() {
        System.out.println("searchBackwards");
        ByteReader reader = null;
        long fromPosition = 0L;
        long toPosition = 0L;
        MatcherSearcher instance = null;
        long expResult = 0L;
        long result = instance.searchBackwards(reader, fromPosition, toPosition);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of searchBackwards method, of class MatcherSearcher.
     */
    @Test
    public void testSearchBackwards_3args_3() {
        System.out.println("searchBackwards");
        byte[] bytes = null;
        int fromPosition = 0;
        int toPosition = 0;
        MatcherSearcher instance = null;
        int expResult = 0;
        int result = instance.searchBackwards(bytes, fromPosition, toPosition);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of prepareForwards method, of class MatcherSearcher.
     */
    @Test
    public void testPrepareForwards() {
        System.out.println("prepareForwards");
        MatcherSearcher instance = null;
        instance.prepareForwards();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of prepareBackwards method, of class MatcherSearcher.
     */
    @Test
    public void testPrepareBackwards() {
        System.out.println("prepareBackwards");
        MatcherSearcher instance = null;
        instance.prepareBackwards();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
