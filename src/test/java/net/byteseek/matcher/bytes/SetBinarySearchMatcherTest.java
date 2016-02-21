/*
 * Copyright Matt Palmer 2009-2016, All rights reserved.
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

package net.byteseek.matcher.bytes;

import net.byteseek.bytes.ByteUtils;
import net.byteseek.matcher.bytes.ByteMatcher;
import net.byteseek.matcher.bytes.InvertibleMatcher;
import net.byteseek.matcher.bytes.SetBinarySearchMatcher;
import net.byteseek.matcher.bytes.SetBitsetMatcher;

import java.util.*;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class SetBinarySearchMatcherTest {
    
    Random randomGenerator = new Random();
    
    /**
     * 
     */
    public SetBinarySearchMatcherTest() {
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
    public void testEmptyBinarySearchMatcher() {
        new SetBinarySearchMatcher(new LinkedHashSet<Byte>(), false);
    }    
    

    /**
     * Test of matches method, of class SetBinarySearchMatcher.
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

    @Test
    public void testRegularExpressions() {
        int numberOfTests = 5;
        for (int testnum = 0; testnum <= numberOfTests; testnum++) {
            Set<Byte> bytesToTest = buildRandomByteSet();
            writeTestDefinition(testnum, numberOfTests, bytesToTest);
            testRegularExpression(bytesToTest);
        }
    }


    @Test
    public void testSetToString() {
        Set<Byte> bytes = new HashSet<Byte>();
        bytes.add((byte) 0);
        bytes.add((byte) 255);
        ByteMatcher matcher = new SetBinarySearchMatcher(bytes, InvertibleMatcher.NOT_INVERTED);
        String toString = matcher.toString();
        assertTrue("Matcher contains class name", toString.contains(matcher.getClass().getSimpleName()));
        assertTrue("Matcher contains inversion", toString.contains("inverted"));
        assertTrue("Matcher contains byte 0", toString.contains("0"));
        assertTrue("Matcher contains byte 255", toString.contains("-1"));
    }


    private void testRegularExpression(Set<Byte> bytesToTest) {

        SetBinarySearchMatcher matcher2NotInverted = new SetBinarySearchMatcher(bytesToTest, InvertibleMatcher.NOT_INVERTED);
        testExpression("BinarySearchMatcher", matcher2NotInverted, bytesToTest);

        SetBinarySearchMatcher matcherInverted2 = new SetBinarySearchMatcher(bytesToTest, InvertibleMatcher.INVERTED);
        testExpression("BinarySearchMatcher", matcherInverted2, bytesToTest);
    }

    private void testSet(Set<Byte> testSet) {
        Set<Byte> otherBytes = ByteUtils.invertedSet(testSet);
        
        SetBinarySearchMatcher matcher2NotInverted = new SetBinarySearchMatcher(testSet, InvertibleMatcher.NOT_INVERTED);
        testMatcher("BinarySearchMatcher", matcher2NotInverted, testSet, otherBytes);
        
        SetBinarySearchMatcher matcherInverted2 = new SetBinarySearchMatcher(testSet, InvertibleMatcher.INVERTED);
        testMatcher("BinarySearchMatcher", matcherInverted2, otherBytes, testSet);
    }
    
    private void testMatcher(String description, ByteMatcher matcher, Set<Byte> bytesMatched, Set<Byte> bytesNotMatched) {
        int numberOfMatchingBytes = matcher.getNumberOfMatchingBytes();
        assertEquals("Matches correct number of bytes", bytesMatched.size(), numberOfMatchingBytes);

        byte[] matchingBytes = matcher.getMatchingBytes();
        for (byte b : matchingBytes) {
            assertTrue("Contains byte " + b, bytesMatched.contains(b));
        }

        for (Byte byteShouldMatch : bytesMatched) {
            assertEquals(String.format("%s: Byte %02x should match:", description, byteShouldMatch), true, matcher.matches(byteShouldMatch));
        }

        for (Byte byteShouldNotMatch : bytesNotMatched) {
            assertEquals(String.format("%s: Byte %02x should not match:", description, byteShouldNotMatch), false, matcher.matches(byteShouldNotMatch));
        }
    }

    private void testExpression(String description, InvertibleMatcher matcher, Set<Byte> bytesMatched) {
        String expression = matcher.toRegularExpression(false);
        assertEquals("Inversion of expression correct.", matcher.isInverted(), expression.startsWith("^"));

        expression = matcher.toRegularExpression(true);
        assertEquals("Inversion of expression correct.", matcher.isInverted(), expression.startsWith("^"));
        if (bytesMatched.size() > 1) {
            assertTrue("Spaces within the set", expression.contains(" "));
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
