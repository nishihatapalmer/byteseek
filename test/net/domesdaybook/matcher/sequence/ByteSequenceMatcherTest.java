/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.domesdaybook.matcher.sequence;

import net.domesdaybook.reader.FileArrayProvider;
import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.domesdaybook.reader.FileByteReader;
import java.net.URL;
import net.domesdaybook.test.Utilities;
import java.io.File;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.reader.ByteReader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class ByteSequenceMatcherTest {
    
    public ByteSequenceMatcherTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of matches method, of class ByteSequenceMatcher.
     */
    @Test
    public void testMatches_ByteReader_long() {
        File file = getFile("/A Midsommer Night's Dreame.txt");
    }

    /**
     * Test of matches method, of class ByteSequenceMatcher.
     */
    @Test
    public void testMatches_byteArr_int() {
        System.out.println("matches");
        byte[] bytes = null;
        int matchFrom = 0;
        ByteSequenceMatcher instance = null;
        boolean expResult = false;
        boolean result = instance.matches(bytes, matchFrom);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of matchesNoBoundsCheck method, of class ByteSequenceMatcher.
     */
    @Test
    public void testMatchesNoBoundsCheck_ByteReader_long() {
        System.out.println("matchesNoBoundsCheck");
        ByteReader reader = null;
        long matchPosition = 0L;
        ByteSequenceMatcher instance = null;
        boolean expResult = false;
        boolean result = instance.matchesNoBoundsCheck(reader, matchPosition);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of matchesNoBoundsCheck method, of class ByteSequenceMatcher.
     */
    @Test
    public void testMatchesNoBoundsCheck_byteArr_int() {
        System.out.println("matchesNoBoundsCheck");
        byte[] bytes = null;
        int matchPosition = 0;
        ByteSequenceMatcher instance = null;
        boolean expResult = false;
        boolean result = instance.matchesNoBoundsCheck(bytes, matchPosition);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of length method, of class ByteSequenceMatcher.
     */
    @Test
    public void testLength() {
        System.out.println("length");
        ByteSequenceMatcher instance = null;
        int expResult = 0;
        int result = instance.length();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toRegularExpression method, of class ByteSequenceMatcher.
     */
    @Test
    public void testToRegularExpression() {
        System.out.println("toRegularExpression");
        boolean prettyPrint = false;
        ByteSequenceMatcher instance = null;
        String expResult = "";
        String result = instance.toRegularExpression(prettyPrint);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getByteMatcherForPosition method, of class ByteSequenceMatcher.
     */
    @Test
    public void testGetByteMatcherForPosition() {
        System.out.println("getByteMatcherForPosition");
        int position = 0;
        ByteSequenceMatcher instance = null;
        SingleByteMatcher expResult = null;
        SingleByteMatcher result = instance.getByteMatcherForPosition(position);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of reverse method, of class ByteSequenceMatcher.
     */
    @Test
    public void testReverse() {
        System.out.println("reverse");
        ByteSequenceMatcher instance = null;
        ByteSequenceMatcher expResult = null;
        ByteSequenceMatcher result = instance.reverse();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
    
    private File getFile(final String resourceName) {
        URL url = this.getClass().getResource(resourceName);
        return new File(url.getPath());
    }  
    

    
    
}
