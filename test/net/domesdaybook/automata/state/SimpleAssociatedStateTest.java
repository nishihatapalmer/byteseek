/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.domesdaybook.automata.state;

import java.util.Collection;
import java.util.Map;
import net.domesdaybook.object.copy.DeepCopy;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Matt
 */
public class SimpleAssociatedStateTest {
    
    public SimpleAssociatedStateTest() {
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
     * Test of deepCopy method, of class SimpleAssociatedState.
     */
    @Test
    public void testDeepCopy_0args() {
        System.out.println("deepCopy");
        SimpleAssociatedState instance = new SimpleAssociatedState();
        SimpleAssociatedState expResult = null;
        SimpleAssociatedState result = instance.deepCopy();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deepCopy method, of class SimpleAssociatedState.
     */
    @Test
    public void testDeepCopy_Map() {
        System.out.println("deepCopy");
        Map<DeepCopy, DeepCopy> oldToNewObjects = null;
        SimpleAssociatedState instance = new SimpleAssociatedState();
        SimpleAssociatedState expResult = null;
        SimpleAssociatedState result = instance.deepCopy(oldToNewObjects);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAssociations method, of class SimpleAssociatedState.
     */
    @Test
    public void testGetAssociations() {
        System.out.println("getAssociations");
        SimpleAssociatedState instance = new SimpleAssociatedState();
        Collection expResult = null;
        Collection result = instance.getAssociations();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addObject method, of class SimpleAssociatedState.
     */
    @Test
    public void testAddObject() {
        System.out.println("addObject");
        Object object = null;
        SimpleAssociatedState instance = new SimpleAssociatedState();
        instance.addObject(object);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeObject method, of class SimpleAssociatedState.
     */
    @Test
    public void testRemoveObject() {
        System.out.println("removeObject");
        Object object = null;
        SimpleAssociatedState instance = new SimpleAssociatedState();
        boolean expResult = false;
        boolean result = instance.removeObject(object);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setAssociations method, of class SimpleAssociatedState.
     */
    @Test
    public void testSetAssociations() {
        System.out.println("setAssociations");
        Collection associations = null;
        SimpleAssociatedState instance = new SimpleAssociatedState();
        instance.setAssociations(associations);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
