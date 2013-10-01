/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.byteseek.matcher.bytes;

import net.byteseek.bytes.ByteUtils;
import net.byteseek.matcher.bytes.ByteMatcher;
import net.byteseek.matcher.bytes.InvertibleMatcher;
import net.byteseek.matcher.bytes.SetBinarySearchMatcher;
import net.byteseek.matcher.bytes.SetBitsetMatcher;

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
    @SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
    public void testNullBitSetMatcher() {
        new SetBitsetMatcher(null, false);
    }
    
    /**
     * 
     */
    @SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
    public void testNullBinarySearchMatcher() {
        new SetBinarySearchMatcher(null, false);
    }
    
    /**
     * 
     */
    @SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
    public void testEmptyBitSetMatcher() {
        new SetBitsetMatcher(new LinkedHashSet<Byte>(), false);
    }
    
    /**
     * 
     */
    @SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
    public void testEmptyBinarySearchMatcher() {
        new SetBinarySearchMatcher(new LinkedHashSet<Byte>(), false);
    }    
    

    /**
     * Test of matches method, of class SetBitsetMatcher.
     * 
     * Can't build all possible subsets of a byte set = 2^256 possible sets,
     * so generates a large number of random byte sets and tests them.
     */
    @Test
    public void testByteSet() {
        int numberOfTests = 100;
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
        Set<Byte> otherBytes = ByteUtils.invertedSet(testSet);
        
        SetBitsetMatcher matcherNotInverted = new SetBitsetMatcher(testSet, InvertibleMatcher.NOT_INVERTED);
        testMatcher("BitSetMatcher", matcherNotInverted, testSet, otherBytes);
        
        SetBitsetMatcher matcherInverted = new SetBitsetMatcher(testSet, InvertibleMatcher.INVERTED);
        testMatcher("BitSetMatcher", matcherInverted, otherBytes, testSet);
        
        SetBinarySearchMatcher matcher2NotInverted = new SetBinarySearchMatcher(testSet, InvertibleMatcher.NOT_INVERTED);
        testMatcher("BinarySearchMatcher", matcher2NotInverted, testSet, otherBytes);
        
        SetBinarySearchMatcher matcherInverted2 = new SetBinarySearchMatcher(testSet, InvertibleMatcher.INVERTED);
        testMatcher("BinarySearchMatcher", matcherInverted2, otherBytes, testSet);
    }
    
    private void testMatcher(String description, ByteMatcher matcher, Set<Byte> bytesMatched, Set<Byte> bytesNotMatched) {
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
