/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.sequence;

import java.io.IOException;
import org.junit.BeforeClass;
import java.io.FileNotFoundException;
import net.domesdaybook.reader.FileReader;
import org.junit.Before;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.net.URL;
import java.io.File;
import net.domesdaybook.matcher.bytes.ByteMatcher;
import net.domesdaybook.reader.Reader;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class ByteSequenceMatcherTest {
    
    private final static Random rand = new Random();   
    
    private FileReader reader;
        
    /**
     * 
     */
    public ByteSequenceMatcherTest() {
    }
  
    
    /**
     * 
     * @throws Exception
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
        final long seed = System.currentTimeMillis();
        // final long seed = ?
        rand.setSeed(seed);
        System.out.println("Seeding random number generator with: " + Long.toString(seed));
        System.out.println("To repeat these exact tests, set the seed to the value above.");
    }
    
    
    /**
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        reader = new FileReader(getFile("/TestASCII.txt"));
    }
    
    
    /**
     * 
     * Construct all possible single byte value sequences.
     * 
     */
    @Test
    public void testConstructSingleByte() {
        for (int byteValue = 0; byteValue < 256; byteValue++) {
            final ByteArrayMatcher matcher = new ByteArrayMatcher((byte) byteValue);
            assertEquals("length:1, byte value:" + Integer.toString(byteValue), 1, matcher.length());
            final ByteMatcher sbm = matcher.getMatcherForPosition(0);
            final byte[] matchingBytes = sbm.getMatchingBytes();
            assertEquals("number of bytes matched=1", 1, matchingBytes.length);
            assertEquals("byte value:" + Integer.toString(byteValue), byteValue, matchingBytes[0] & 0xFF);
        }
    }
    
    
    /**
     * Construct using random repeated byte values for all byte values.
     */
    @Test
    public void testConstructRepeatedBytes() {
        for (int byteValue = 0; byteValue < 256; byteValue++) {
            final int repeats = rand.nextInt(1024) + 1;
            final ByteArrayMatcher matcher = new ByteArrayMatcher((byte) byteValue, repeats);
            assertEquals("length:" + Integer.toString(repeats) + ", byte value:" + Integer.toString(byteValue), repeats, matcher.length());
            for (int pos = 0; pos < repeats; pos++) {
                final ByteMatcher sbm = matcher.getMatcherForPosition(pos);
                final byte[] matchingBytes = sbm.getMatchingBytes();
                assertEquals("number of bytes matched=1", 1, matchingBytes.length);
                assertEquals("byte value:" + Integer.toString(byteValue), byteValue, matchingBytes[0] & 0xFF);
            }
        }
    }
    
        
    /**
     * Construct using random arrays of bytes.
     */
    @Test
    public void testConstructByteArray() {
        for (int testNo = 0; testNo < 1000; testNo++) {
            final byte[] array = createRandomArray(1024);
            final ByteArrayMatcher matcher = new ByteArrayMatcher(array);
            assertEquals("length:" + Integer.toString(array.length), array.length, matcher.length());
            for (int pos = 0; pos < array.length; pos++) {
                final ByteMatcher sbm = matcher.getMatcherForPosition(pos);
                final byte[] matchingBytes = sbm.getMatchingBytes();
                final byte matchingValue = array[pos];
                assertEquals("number of bytes matched=1", 1, matchingBytes.length);
                assertEquals("byte value:" + Integer.toString(matchingValue), matchingValue, matchingBytes[0]);
            }
        }
    }
 

    /**
     * Construct using random lists of byte sequence matchers:
     */
    @Test
    public void testConstructByteSequenceMatcherList() {
        for (int testNo = 0; testNo < 1000; testNo++) {
            final List<ByteArrayMatcher> list = createRandomList(32);
            int totalLength = 0;
            for (final SequenceMatcher matcher : list) {
                totalLength += matcher.length();
            }
            final ByteArrayMatcher matcher = new ByteArrayMatcher(list);
            assertEquals("length:" + Integer.toString(totalLength), totalLength, matcher.length());
            int localPos = -1;
            int matchIndex = 0;
            SequenceMatcher currentMatcher = list.get(matchIndex);
            for (int pos = 0; pos < totalLength; pos++) {
                final ByteMatcher sbm = matcher.getMatcherForPosition(pos);
                final byte[] matchingBytes = sbm.getMatchingBytes();
                localPos++;
                if (localPos == currentMatcher.length()) {
                    matchIndex++;
                    currentMatcher = list.get(matchIndex);
                    localPos = 0;
                }
                final ByteMatcher sbm2 = currentMatcher.getMatcherForPosition(localPos);
                final byte[] matchingBytes2 = sbm2.getMatchingBytes();
                assertEquals("number of bytes matched source=1", 1, matchingBytes2.length);
                assertEquals("number of bytes matched=1", 1, matchingBytes.length);
                assertEquals("byte value:" + Integer.toString(matchingBytes2[0]), matchingBytes2[0], matchingBytes[0]);
            }
        }
    }
    
    
    // Test expected construction failures:
    
    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructNoRepeats() {
        new ByteArrayMatcher((byte) 0x8f, 0);
    }
    
    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullArray() {
        new ByteArrayMatcher((byte[]) null);
    }
    
    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructEmptyArray() {
        new ByteArrayMatcher(new byte[0]);
    }    
    
    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullCollection() {
        new ByteArrayMatcher((ArrayList<Byte>) null);
    }   
    
    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructEmptyCollection() {
        new ByteArrayMatcher(new ArrayList<Byte>());
    }       
    
    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullList() {
        new ByteArrayMatcher((ArrayList<ByteArrayMatcher>) null);
    }        

    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructEmptyList() {
        new ByteArrayMatcher(new ArrayList<ByteArrayMatcher>());
    }        
    
    
    /**
     * Test of matches method, of class ByteArrayMatcher.
     * @throws FileNotFoundException 
     * @throws IOException 
     */
    @Test
    public void testMatches_ByteReader_long() throws FileNotFoundException, IOException {
        ByteArrayMatcher matcher = new ByteArrayMatcher((byte) 0x2A, 3); // ***
        testMatchesAround(matcher, 0);
        testMatchesAround(matcher, 61);
        testMatchesAround(matcher, 1017);
        
        matcher = new ByteArrayMatcher(new byte[] {0x48, 0x65, 0x72, 0x65}); // Here
        testMatchesAround(matcher, 28200);
        testMatchesAround(matcher, 60836);
        testMatchesAround(matcher, 64481);
        
        matcher = new ByteArrayMatcher(new byte[] {0x2e, 0x0d, 0x0a}); // . <LF> <CR>
        testMatchesAround(matcher, 196);
        testMatchesAround(matcher, 42004);
        testMatchesAround(matcher, 112277);
    }

    
    private void testMatchesAround(ByteArrayMatcher matcher, long pos) throws IOException {
        String matchDesc = matcher.toRegularExpression(true);
        assertTrue(matchDesc + " at pos " + Long.toString(pos), matcher.matches(reader, pos));
        assertFalse(matchDesc + " at pos " + Long.toString(pos - 1), matcher.matches(reader, pos - 1));
        assertFalse(matchDesc + " at pos " + Long.toString(pos + 1), matcher.matches(reader, pos + 1));
    }
    
    /**
     * Test of matches method, of class ByteArrayMatcher.
     */
    @Test
    public void testMatches_byteArr_int() {
        System.out.println("matches");
        byte[] bytes = null;
        int matchFrom = 0;
        ByteArrayMatcher instance = null;
        boolean expResult = false;
        boolean result = instance.matches(bytes, matchFrom);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }



    /**
     * Test of matchesNoBoundsCheck method, of class ByteArrayMatcher.
     */
    @Test
    public void testMatchesNoBoundsCheck_byteArr_int() {
        System.out.println("matchesNoBoundsCheck");
        byte[] bytes = null;
        int matchPosition = 0;
        ByteArrayMatcher instance = null;
        boolean expResult = false;
        boolean result = instance.matchesNoBoundsCheck(bytes, matchPosition);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }



    /**
     * Test of toRegularExpression method, of class ByteArrayMatcher.
     */
    @Test
    public void testToRegularExpression() {
        System.out.println("toRegularExpression");
        boolean prettyPrint = false;
        ByteArrayMatcher instance = null;
        String expResult = "";
        String result = instance.toRegularExpression(prettyPrint);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getByteMatcherForPosition method, of class ByteArrayMatcher.
     */
    @Test
    public void testGetByteMatcherForPosition() {
        System.out.println("getByteMatcherForPosition");
        int position = 0;
        ByteArrayMatcher instance = null;
        ByteMatcher expResult = null;
        ByteMatcher result = instance.getMatcherForPosition(position);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of reverse method, of class ByteArrayMatcher.
     */
    @Test
    public void testReverse() {
        System.out.println("reverse");
        ByteArrayMatcher instance = null;
        ByteArrayMatcher expResult = null;
        ByteArrayMatcher result = instance.reverse();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
    
    private File getFile(final String resourceName) {
        URL url = this.getClass().getResource(resourceName);
        return new File(url.getPath());
    }  
    
    
    private byte[] createRandomArray(final int maxLength) {
        final int length = rand.nextInt(maxLength) + 1;
        final byte[] array = new byte[length];
        for (int pos = 0; pos < length; pos++) {
            array[pos] = (byte) rand.nextInt(256);
        }
        return array;
    }

    private List<ByteArrayMatcher> createRandomList(final int maxNum) {
        final int noOfMatchers = rand.nextInt(maxNum) + 1;
        final List<ByteArrayMatcher> matchers = new ArrayList<ByteArrayMatcher>();
        for (int num = 0; num < noOfMatchers; num++) {
            final int matchType = rand.nextInt(3);
            ByteArrayMatcher matcher;
            switch (matchType) {
                case 0: {
                    final int byteValue = rand.nextInt(256);
                    matcher = new ByteArrayMatcher((byte) byteValue);
                    break;
                }
                case 1: {
                    final byte[] values = createRandomArray(256);
                    matcher = new ByteArrayMatcher(values);
                    break;
                }
                case 2: {
                    final int byteValue = rand.nextInt(256);
                    final int repeats = rand.nextInt(256) + 1;
                    matcher = new ByteArrayMatcher((byte) byteValue, repeats);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Invalid matcher type");
                }
            }
            matchers.add(matcher);
        }
        return matchers;
    }

    
    
}
