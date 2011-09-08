/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.sequence;

import org.junit.BeforeClass;
import java.io.FileNotFoundException;
import net.domesdaybook.reader.FileByteReader;
import org.junit.Before;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.net.URL;
import java.io.File;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.reader.ByteReader;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class ByteSequenceMatcherTest {
    
    private final static Random rand = new Random();   
    
    private FileByteReader reader;
        
    public ByteSequenceMatcherTest() {
    }
  
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        final long seed = System.currentTimeMillis();
        // final long seed = ?
        rand.setSeed(seed);
        System.out.println("Seeding random number generator with: " + Long.toString(seed));
        System.out.println("To repeat these exact tests, set the seed to the value above.");
    }
    
    
    @Before
    public void setUp() throws Exception {
        reader = new FileByteReader(getFile("/TestASCII.txt"));
    }
    
    
    /**
     * 
     * Construct all possible single byte value sequences.
     * 
     */
    @Test
    public void testConstructSingleByte() {
        for (int byteValue = 0; byteValue < 256; byteValue++) {
            final ByteSequenceMatcher matcher = new ByteSequenceMatcher((byte) byteValue);
            assertEquals("length:1, byte value:" + Integer.toString(byteValue), 1, matcher.length());
            final SingleByteMatcher sbm = matcher.getByteMatcherForPosition(0);
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
            final ByteSequenceMatcher matcher = new ByteSequenceMatcher((byte) byteValue, repeats);
            assertEquals("length:" + Integer.toString(repeats) + ", byte value:" + Integer.toString(byteValue), repeats, matcher.length());
            for (int pos = 0; pos < repeats; pos++) {
                final SingleByteMatcher sbm = matcher.getByteMatcherForPosition(pos);
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
            final ByteSequenceMatcher matcher = new ByteSequenceMatcher(array);
            assertEquals("length:" + Integer.toString(array.length), array.length, matcher.length());
            for (int pos = 0; pos < array.length; pos++) {
                final SingleByteMatcher sbm = matcher.getByteMatcherForPosition(pos);
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
            final List<ByteSequenceMatcher> list = createRandomList(32);
            int totalLength = 0;
            for (final SequenceMatcher matcher : list) {
                totalLength += matcher.length();
            }
            final ByteSequenceMatcher matcher = new ByteSequenceMatcher(list);
            assertEquals("length:" + Integer.toString(totalLength), totalLength, matcher.length());
            int localPos = -1;
            int matchIndex = 0;
            SequenceMatcher currentMatcher = list.get(matchIndex);
            for (int pos = 0; pos < totalLength; pos++) {
                final SingleByteMatcher sbm = matcher.getByteMatcherForPosition(pos);
                final byte[] matchingBytes = sbm.getMatchingBytes();
                localPos++;
                if (localPos == currentMatcher.length()) {
                    matchIndex++;
                    currentMatcher = list.get(matchIndex);
                    localPos = 0;
                }
                final SingleByteMatcher sbm2 = currentMatcher.getByteMatcherForPosition(localPos);
                final byte[] matchingBytes2 = sbm2.getMatchingBytes();
                assertEquals("number of bytes matched source=1", 1, matchingBytes2.length);
                assertEquals("number of bytes matched=1", 1, matchingBytes.length);
                assertEquals("byte value:" + Integer.toString(matchingBytes2[0]), matchingBytes2[0], matchingBytes[0]);
            }
        }
    }
    
    
    // Test expected construction failures:
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructNoRepeats() {
        ByteSequenceMatcher matcher = new ByteSequenceMatcher((byte) 0x8f, 0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullArray() {
        ByteSequenceMatcher matcher = new ByteSequenceMatcher((byte[]) null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructEmptyArray() {
        ByteSequenceMatcher matcher = new ByteSequenceMatcher(new byte[0]);
    }    
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullCollection() {
        ByteSequenceMatcher matcher = new ByteSequenceMatcher((ArrayList<Byte>) null);
    }   
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructEmptyCollection() {
        ByteSequenceMatcher matcher = new ByteSequenceMatcher(new ArrayList<Byte>());
    }       
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullList() {
        ByteSequenceMatcher matcher = new ByteSequenceMatcher((ArrayList<ByteSequenceMatcher>) null);
    }        

    @Test(expected = IllegalArgumentException.class)
    public void testConstructEmptyList() {
        ByteSequenceMatcher matcher = new ByteSequenceMatcher(new ArrayList<ByteSequenceMatcher>());
    }        
    
    
    /**
     * Test of matches method, of class ByteSequenceMatcher.
     */
    @Test
    public void testMatches_ByteReader_long() throws FileNotFoundException {
        ByteSequenceMatcher matcher = new ByteSequenceMatcher((byte) 0x2A, 3); // ***
        testMatchesAround(matcher, 0);
        testMatchesAround(matcher, 61);
        testMatchesAround(matcher, 1017);
        
        matcher = new ByteSequenceMatcher(new byte[] {0x48, 0x65, 0x72, 0x65}); // Here
        testMatchesAround(matcher, 28200);
        testMatchesAround(matcher, 60836);
        testMatchesAround(matcher, 64481);
        
        matcher = new ByteSequenceMatcher(new byte[] {0x2e, 0x0d, 0x0a}); // . <LF> <CR>
        testMatchesAround(matcher, 196);
        testMatchesAround(matcher, 42004);
        testMatchesAround(matcher, 112277);
    }

    
    private void testMatchesAround(ByteSequenceMatcher matcher, long pos) {
        String matchDesc = matcher.toRegularExpression(true);
        assertTrue(matchDesc + " at pos " + Long.toString(pos), matcher.matches(reader, pos));
        assertFalse(matchDesc + " at pos " + Long.toString(pos - 1), matcher.matches(reader, pos - 1));
        assertFalse(matchDesc + " at pos " + Long.toString(pos + 1), matcher.matches(reader, pos + 1));
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
    
    
    private byte[] createRandomArray(final int maxLength) {
        final int length = rand.nextInt(maxLength) + 1;
        final byte[] array = new byte[length];
        for (int pos = 0; pos < length; pos++) {
            array[pos] = (byte) rand.nextInt(256);
        }
        return array;
    }

    private List<ByteSequenceMatcher> createRandomList(final int maxNum) {
        final int noOfMatchers = rand.nextInt(maxNum) + 1;
        final List<ByteSequenceMatcher> matchers = new ArrayList<ByteSequenceMatcher>();
        for (int num = 0; num < noOfMatchers; num++) {
            final int matchType = rand.nextInt(3);
            ByteSequenceMatcher matcher;
            switch (matchType) {
                case 0: {
                    final int byteValue = rand.nextInt(256);
                    matcher = new ByteSequenceMatcher((byte) byteValue);
                    break;
                }
                case 1: {
                    final byte[] values = createRandomArray(256);
                    matcher = new ByteSequenceMatcher(values);
                    break;
                }
                case 2: {
                    final int byteValue = rand.nextInt(256);
                    final int repeats = rand.nextInt(256) + 1;
                    matcher = new ByteSequenceMatcher((byte) byteValue, repeats);
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
