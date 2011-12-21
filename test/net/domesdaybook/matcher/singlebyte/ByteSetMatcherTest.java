/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.bytes.ByteUtilities;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class ByteSetMatcherTest {
    
    Random randomGenerator = new Random();
    
    /**
     * 
     */
    public ByteSetMatcherTest() {
    }

    
    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullBitSetMatcher() {
        new ByteSetBitSetMatcher(null, false);
    }
    
    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullBinarySearchMatcher() {
        new ByteSetBinarySearchMatcher(null, false);
    }
    
    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyBitSetMatcher() {
        new ByteSetBitSetMatcher(new LinkedHashSet<Byte>(), false);
    }
    
    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyBinarySearchMatcher() {
        new ByteSetBinarySearchMatcher(new LinkedHashSet<Byte>(), false);
    }    
    

    /**
     * Test of matches method, of class ByteSetBitSetMatcher.
     * 
     * Can't build all possible subsets of a byte set = 2^256 possible sets,
     * so generates a large number of random byte sets and tests them.
     */
    @Test
    public void testByteSet() {
        int numberOfTests = 4096;
        for (int testnum = 0; testnum <= numberOfTests; testnum++) {
            Set<Byte> bytesToTest = buildRandomByteSet();
            writeTestDefinition(testnum, numberOfTests, bytesToTest);
            testSet(bytesToTest);
        }
    }
    

    private void writeTestDefinition(int testnum, int totalTests, Set<Byte> bytesToTest) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Test %d of %d\t{", testnum, totalTests));
        for (Byte b : bytesToTest) {
            builder.append(String.format("%02x ", b));
        }
        builder.append("}");
        System.out.println(builder.toString());
    }
    
    
    private void testSet(Set<Byte> testSet) {
        Set<Byte> otherBytes = ByteUtilities.invertedSet(testSet);
        
        ByteSetBitSetMatcher matcherNotInverted = new ByteSetBitSetMatcher(testSet, InvertibleMatcher.NOT_INVERTED);
        testMatcher("BitSetMatcher", matcherNotInverted, testSet, otherBytes);
        
        ByteSetBitSetMatcher matcherInverted = new ByteSetBitSetMatcher(testSet, InvertibleMatcher.INVERTED);
        testMatcher("BitSetMatcher", matcherInverted, otherBytes, testSet);
        
        ByteSetBinarySearchMatcher matcher2NotInverted = new ByteSetBinarySearchMatcher(testSet, InvertibleMatcher.NOT_INVERTED);
        testMatcher("BinarySearchMatcher", matcher2NotInverted, testSet, otherBytes);
        
        ByteSetBinarySearchMatcher matcherInverted2 = new ByteSetBinarySearchMatcher(testSet, InvertibleMatcher.INVERTED);
        testMatcher("BinarySearchMatcher", matcherInverted2, otherBytes, testSet);
    }
    
    private void testMatcher(String description, SingleByteMatcher matcher, Set<Byte> bytesMatched, Set<Byte> bytesNotMatched) {
        assertEquals("Matches correct number of bytes", bytesMatched.size(), matcher.getNumberOfMatchingBytes());
        for (Byte byteShouldMatch : bytesMatched) {
            assertEquals(String.format("%s: Byte %02x should match:", description, byteShouldMatch), true, matcher.matches(byteShouldMatch));
        }
        for (Byte byteShouldNotMatch : bytesNotMatched) {
            assertEquals(String.format("%s: Byte %02x should not match:", description, byteShouldNotMatch), false, matcher.matches(byteShouldNotMatch));
        }
    }
    
    
    Set<Byte> buildRandomByteSet() {
        int numberOfElements = randomGenerator.nextInt(255) + 1;
        Set<Byte> randomSet = new TreeSet<Byte>();
        for (int i = 0; i < numberOfElements; i++) {
            int value = randomGenerator.nextInt(255);
            randomSet.add((byte) value);
        }
        return randomSet;
    }

}
