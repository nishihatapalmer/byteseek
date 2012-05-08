/*
 * Copyright Matt Palmer 2011-2012, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 
 *  * The names of its contributors may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package net.domesdaybook.matcher.sequence;

import net.domesdaybook.reader.ByteArrayReader;
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
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests all the constructors and public methods of the ByteArrayMatcher class.
 * 
 * @author Matt Palmer
 */
public class ByteArrayMatcherTest {
    
    //////////////////
    // test setup   //
    //////////////////
    
    private final static Random rand = new Random();   
    
    private FileReader reader;
    private byte[] bytes;
    
    public ByteArrayMatcherTest() {
    }
  
    
    /**
     * Generates a random number to use in randomising tests where complete
     * coverage takes too long.  The seed is output to the console to give a
     * fighting chance of replicating a failing test - but I'm not really 
     * convinced this is a very pleasant way of testing.  Still, it gives more
     * complete coverage of the code than purely fixed tests.
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
     * Creates a file reader and a byte array from an ASCII test file.
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        reader = new FileReader(getFile("/TestASCII.txt"));
        bytes = reader.getWindow(0).getArray();
    }
    
    
        
    ///////////////////////////
    //   constructor tests   //
    ///////////////////////////
    
    
    /**
     * 
     * Construct all possible single byte value sequences.  Tests are:
     * 
     * - the length is one.
     * - the number of bytes matched by it is one.
     * - the value of the byte matcher is the one it was constructed with.
     * - the matcher matches that byte in a byte array and a reader.
     */
    @Test
    public void testConstructSingleByte() throws IOException {
        for (int byteValue = 0; byteValue < 256; byteValue++) {
            
            final ByteArrayMatcher matcher = new ByteArrayMatcher((byte) byteValue);
            assertEquals("length:1, byte value:" + Integer.toString(byteValue), 1, matcher.length());
            
            final byte[] matchingBytes = matcher.getMatcherForPosition(0).getMatchingBytes();
            assertEquals("number of bytes matched=1", 1, matchingBytes.length);
            assertEquals("byte value:" + Integer.toString(byteValue), byteValue, matchingBytes[0] & 0xFF);
            
            final byte[] testArray = new byte[] {(byte) byteValue};
            assertTrue("matches that byte value in an array", matcher.matches(testArray, 0));
            
            final ByteArrayReader reader = new ByteArrayReader(testArray);
            assertTrue("matches that byte value in a reader", matcher.matches(reader, 0));
        }
    }
    
    
    /**
     * Construct using random repeated byte values for all byte values.  Tests are:
     * 
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
     * Construct using random lists of byte sequence matchers.  Tests are:
     * 
     * - the length of an assembled matcher is correct.
     * - each position in the assembled matcher matches only one byte.
     * - each byte in the assembled matcher is correct.
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
    
    
    //////////////////////////////////
    //  construction failure tests  //
    //////////////////////////////////

    
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
    
    
    ///////////////////////////////
    //   reader matching tests   //
    ///////////////////////////////
    
    
    /**
     * Test of matches method, of class ByteArrayMatcher.
     * @throws FileNotFoundException 
     * @throws IOException 
     */
    @Test
    public void testMatches_ByteReader_long() throws FileNotFoundException, IOException {
        SequenceMatcher matcher = new ByteArrayMatcher((byte) 0x2A, 3); 
        runTestMatchesAround(matcher, 0, 61, 1017);
        
        matcher = new ByteArrayMatcher((byte) 0x2A, 3).reverse(); 
        runTestMatchesAround(matcher, 0, 61, 1017);
        
        matcher = new ByteArrayMatcher("Here"); 
        runTestMatchesAround(matcher, 28200, 60836, 64481);
        
        matcher = matcher.subsequence(1, 4);
        runTestMatchesAround(matcher, 28201, 60837, 64482);
        
        matcher = new ByteArrayMatcher("ereH").reverse();
        runTestMatchesAround(matcher, 28200, 60836, 64481);
        
        matcher = matcher.subsequence(1, 3);
        runTestMatchesAround(matcher, 28201, 60837, 64482);        
        
        matcher = new ByteArrayMatcher(new byte[] {0x2e, 0x0d, 0x0a});
        runTestMatchesAround(matcher, 196, 42004, 112277);
        
        matcher = matcher.subsequence(1);
        runTestMatchesAround(matcher, 197, 42005, 112278);
        
        matcher = new ByteArrayMatcher(new byte[] {0x0a, 0x0d, 0x2e}).reverse();
        runTestMatchesAround(matcher, 196, 42004, 112277);
        
        matcher = matcher.subsequence(1);
        runTestMatchesAround(matcher, 197, 42005, 112278);
    }
    
    
    /**
     * Test of matches reader method, of class ByteArrayMatcher, matching over
     * a window boundary.
     * 
     * @throws FileNotFoundException 
     * @throws IOException 
     */
    @Test
    public void testMatchesOverBoundary_ByteReader_long() throws FileNotFoundException, IOException {
        // Test around a window boundary at 4096
        SequenceMatcher matcher = new ByteArrayMatcher("be");
        runTestMatchesAround(matcher, 4095);
        
        matcher = new ByteArrayMatcher("eb").reverse();
        runTestMatchesAround(matcher, 4095);
        
        matcher = new ByteArrayMatcher("Gutenberg");
        runTestMatchesAround(matcher, 4090);
        
        matcher = new ByteArrayMatcher("grebnetuG").reverse();
        runTestMatchesAround(matcher, 4090);
    }        
    
    
    ////////////////////////////////////
    //   reader out of bounds tests   //
    ////////////////////////////////////    
    
    
    @Test
    public void testMatchesReaderOutOfBoundsNegative() throws IOException {
        SequenceMatcher matcher = new ByteArrayMatcher("xxx");
        assertFalse("negative position", matcher.matches(reader, -1));
        assertFalse("past end", matcher.matches(reader, 10000000));
        
        matcher = matcher.reverse();
        assertFalse("reverse negative position", matcher.matches(reader, -1));
        assertFalse("reverse past end", matcher.matches(reader, 10000000));
    }
    

    @Test
    public void testMatchesReaderOutOfBoundsCrossingEnd() throws IOException {
        SequenceMatcher matcher = new  ByteArrayMatcher(new byte[] {0x65, 0x2e, 0x0d, 0x0a, 0x00});
        assertFalse("longer than end", matcher.matches(reader, 112276));
        
        matcher = new ByteArrayMatcher(new byte[] {0x00, 0x0a, 0x0d, 0x2e, 0x65}).reverse();
        assertFalse("reverse longer than end", matcher.matches(reader, 112276));        
    }
   
    
    /////////////////////////////////
    //   byte array matches tests  //
    /////////////////////////////////      
    
    
    /**
     * Test of matches byte array method, of class ByteArrayMatcher.
     */
    @Test
    public void testMatches_byteArr_int() {
        SequenceMatcher matcher = new ByteArrayMatcher((byte) 0x2A, 3); 
        runTestMatchesAroundArray(matcher, 0, 61, 1017);

        matcher = new ByteArrayMatcher((byte) 0x2A, 3).reverse(); 
        runTestMatchesAroundArray(matcher, 0, 61, 1017); 
        
        //TODO: more byte array matching tests.
    }
    

    /**
     * Test of matchesNoBoundsCheck byte array method, of class ByteArrayMatcher.
     */
    @Test
    public void testMatchesNoBoundsCheck_byteArr_int() {
        ByteArrayMatcher matcher = new ByteArrayMatcher((byte) 0x2A, 3); 
        testMatchesAroundArrayNoCheck(matcher, 61);
        testMatchesAroundArrayNoCheck(matcher, 1017);
        
        //TODO: more no bounds check byte array matching tests.
    }    
    
    
    
    ////////////////////////////////////////
    //   byte array out of bounds tests   //
    ////////////////////////////////////////  
    
    
    @Test
    public void testMatches_outOfBoundsNegative() {
        ByteArrayMatcher matcher = new ByteArrayMatcher("Titania");
        assertFalse("matches at negative pos", matcher.matches(bytes, -1));
    }
    
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testMatchesNoBoundsCheck_outOfBoundsNegative() {
        ByteArrayMatcher matcher = new  ByteArrayMatcher("Oberon");
        matcher.matchesNoBoundsCheck(bytes, -1);
    }
    
    
    @Test
    public void testMatches_outOfBoundsPastEnd() {
        ByteArrayMatcher matcher = new ByteArrayMatcher("Bottom");
        assertFalse("matches past end", matcher.matches(bytes, 4096));
    }    
    
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testMatchesNoBoundsCheck_outOfBoundsPastEnd() {
        ByteArrayMatcher matcher = new  ByteArrayMatcher("Puck");
        matcher.matchesNoBoundsCheck(bytes, 4096);
    }   
    
    
    @Test
    public void testMatches_outOfBoundsCrossingEnd() {
        ByteArrayMatcher matcher = new  ByteArrayMatcher("be");
        assertFalse("matches crossing end", matcher.matches(bytes, 4095));
    }
    
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testMatchesNoBoundsCheck_outOfBoundsCrossingEnd() {
        ByteArrayMatcher matcher = new  ByteArrayMatcher("be");
        matcher.matchesNoBoundsCheck(bytes, 4095);
    }      
    
    
    ///////////////////////////////////
    //  representation test methods  //
    ///////////////////////////////////   
    
    
    /**
     * Test of toRegularExpression method, of class ByteArrayMatcher.
     */
    @Test
    public void testToRegularExpression() {
        ByteArrayMatcher matcher = new ByteArrayMatcher("abc");
        assertEquals("reg ex abc", " 'abc' ", matcher.toRegularExpression(true));
        
        //TODO: more reg ex tests.
    }

    
    /**
     * Test of getByteMatcherForPosition method, of class ByteArrayMatcher.
     */
    @Test
    public void testGetByteMatcherForPosition() {
        testMatchersForSequence("abc");
        testMatchersForSequence("x");
        testMatchersForSequence("Midsommer");
        testMatchersForSequence("testGetByteMatcherForPosition");
    }
    
    
    /////////////////////////
    //  view test methods  //
    /////////////////////////    
    
    
    /**
     * Test of reverse method, of class ByteArrayMatcher.
     */
    @Test
    public void testReverse() {
        testReversed("a");
        testReversed("abcdefg");
        testReversed("xx");
        testReversed("1234567890abcdefghijklmnopqrstuvwxyz");
    }
    

    /**
     * Test of subsequence method, of class ByteArrayMatcher.
     */
    @Test
    public void testSubsequence() {
         ByteArrayMatcher matcher = new ByteArrayMatcher("abc");
         SequenceMatcher sub = matcher.subsequence(1);
         assertEquals("abc length", 2, sub.length());
         
         //TODO: lots more subsequence tests.
    }    

    
    ////////////////////////////
    //  private test methods  //
    ////////////////////////////
    

    private void runTestMatchesAround(SequenceMatcher matcher, long... positions) throws IOException {
        runTestMatchesAroundOriginal(matcher, positions);
        runTestMatchesAroundDoubleReversed(matcher, positions);
    }
    
    
    private void runTestMatchesAroundOriginal(SequenceMatcher matcher, long... positions) throws IOException {
        for (long position : positions) {
            testMatchesAroundReader(matcher, position);
        }
    }
    
    
    private void runTestMatchesAroundDoubleReversed(SequenceMatcher matcher, long... positions) throws IOException {
        SequenceMatcher doubleReversed = matcher.reverse().reverse();
        for (long position : positions) {
            testMatchesAroundReader(doubleReversed, position);
        }
    }  
    
    
    private void runTestMatchesAroundArray(SequenceMatcher matcher, int... positions) {
        runTestMatchesAroundOriginalArray(matcher, positions);
        runTestMatchesAroundDoubleReversedArray(matcher, positions);
    }
    
    
    private void runTestMatchesAroundOriginalArray(SequenceMatcher matcher, int... positions) {
        for (int position : positions) {
            testMatchesAroundArray(matcher, position);
        }
    }
    
    
    private void runTestMatchesAroundDoubleReversedArray(SequenceMatcher matcher, int... positions) {
        SequenceMatcher doubleReversed = matcher.reverse().reverse();
        for (int position : positions) {
            testMatchesAroundArray(doubleReversed, position);
        }
    }

    
    private void testReversed(String sequence) {
        ByteArrayMatcher matcher = new ByteArrayMatcher(sequence);
        SequenceMatcher reversed = matcher.reverse();
        int matcherLength = matcher.length();
        assertEquals(sequence + " length", matcherLength, reversed.length());
        for (int index = 0; index < matcherLength; index++) {
            byte[] matcherbytes = matcher.getMatcherForPosition(index).getMatchingBytes();
            assertEquals(sequence + " matches one byte at index" + index, 1, matcherbytes.length);
            byte[] reversebytes = reversed.getMatcherForPosition(reversed.length() - index - 1).getMatchingBytes();
            assertArrayEquals(sequence + " bytes match", matcherbytes, reversebytes);
        }
    }
    
    
    private void testMatchesAroundReader(SequenceMatcher matcher, long pos) throws IOException {
        String matchDesc = matcher.toRegularExpression(true);
        assertTrue(matchDesc + " at pos " + Long.toString(pos), matcher.matches(reader, pos));
        assertFalse(matchDesc + " at pos " + Long.toString(pos - 1), matcher.matches(reader, pos - 1));
        assertFalse(matchDesc + " at pos " + Long.toString(pos + 1), matcher.matches(reader, pos + 1));
    }
    

    
    private void testMatchesAroundArray(SequenceMatcher matcher, int pos) {
        String matchDesc = matcher.toRegularExpression(true);
        assertTrue(matchDesc + " at pos " + Long.toString(pos), matcher.matches(bytes, pos));
        assertFalse(matchDesc + " at pos " + Long.toString(pos - 1), matcher.matches(bytes, pos - 1));
        assertFalse(matchDesc + " at pos " + Long.toString(pos + 1), matcher.matches(bytes, pos + 1));
    }
    
    
    private void testMatchesAroundArrayNoCheck(SequenceMatcher matcher, int pos) {
        String matchDesc = matcher.toRegularExpression(true);
        assertTrue(matchDesc + " at pos " + Long.toString(pos), matcher.matchesNoBoundsCheck(bytes, pos));
        assertFalse(matchDesc + " at pos " + Long.toString(pos - 1), matcher.matchesNoBoundsCheck(bytes, pos - 1));
        assertFalse(matchDesc + " at pos " + Long.toString(pos + 1), matcher.matchesNoBoundsCheck(bytes, pos + 1));
    }    
    

    private void testMatchersForSequence(String sequence) {
        // test forwards matcher
        ByteArrayMatcher matcher = new ByteArrayMatcher(sequence);
        testByteMatcherForPosition(sequence, matcher);

        // test the reversed matcher
        SequenceMatcher reversed = matcher.reverse();
        String reverseSequence = new StringBuffer(sequence).reverse().toString();
        testByteMatcherForPosition(reverseSequence, reversed);        
    }

    
    private void testByteMatcherForPosition(String sequence, SequenceMatcher m) {
        for (int position = 0; position < sequence.length(); position++) {
            byte[] onebytes = m.getMatcherForPosition(position).getMatchingBytes();
            assertEquals(sequence + " length", 1, onebytes.length);
            assertEquals(sequence + "value", sequence.charAt(position), onebytes[0] & 0xFF);
        }
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
