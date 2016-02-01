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

import org.junit.BeforeClass;
import java.util.Random;

import net.byteseek.matcher.bytes.ByteRangeMatcher;
import net.byteseek.matcher.bytes.InvertibleMatcher;

import java.util.List;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class ByteRangeMatcherTest {

     private final static Random rand = new Random();   
     
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
     */
    public ByteRangeMatcherTest() {
    }


    /**
     * Test of matches method, of class ByteRangeMatcher.
     */
    @Test
    public void testByteRange() {
        
        for (int testRun = 1; testRun <= 5; testRun++) {
            int start = rand.nextInt(256);
            System.out.println(String.format("%d of 5\tTesting byte ranges starting with %d", testRun, start));
            for (int end = 0; end < 256; end++) {
                ByteRangeMatcher matcherNotInverted = new ByteRangeMatcher(start, end, InvertibleMatcher.NOT_INVERTED);
                ByteRangeMatcher matcherInverted = new ByteRangeMatcher(start, end, InvertibleMatcher.INVERTED);
                validateMatcher(matcherNotInverted, start, end);
                validateMatcher(matcherInverted, start, end);
            }
        }
    }

    private void validateMatcher(ByteRangeMatcher matcher, int start, int end) {
        int startValue, endValue;
        if (start > end) {
            startValue = end;
            endValue = start;
        } else {
            startValue = start;
            endValue = end;
        }
        String regex = String.format("%s%02x-%02x", matcher.isInverted()? "^" : "", startValue, endValue);
        assertEquals(regex, regex, matcher.toRegularExpression(false));
        String isInverted = matcher.isInverted()? "is" : "is not";
        int numberOfBytes = matcher.isInverted()? 255 - endValue + startValue : endValue - startValue + 1;
        assertEquals(String.format("Number of bytes for %d-%d, matcher %s inverted\t", start, end, isInverted), numberOfBytes, matcher.getNumberOfMatchingBytes());
        List<Byte> byteList = new ArrayList<Byte>();
        String message = "Testing value %d on range %d-%d, matcher %s inverted\t";
        for (int testvalue = 0; testvalue < startValue; testvalue++) {
            String testmessage = String.format(message, testvalue, start, end, isInverted);
            boolean matched = matcher.matches((byte) testvalue);
            if (matched) { byteList.add((byte) testvalue); }
            assertEquals(testmessage, matcher.isInverted(), matched);
        }
        for (int testvalue = startValue; testvalue <= endValue; testvalue++) {
            String testmessage = String.format(message, testvalue, start, end, isInverted);
            boolean matched = matcher.matches((byte) testvalue);
            if (matched) { byteList.add((byte) testvalue); }
            assertEquals(testmessage, !matcher.isInverted(), matched);
        }
        for (int testvalue = endValue+1; testvalue < 256; testvalue++) {
            String testmessage = String.format(message, testvalue, start, end, isInverted);
            boolean matched = matcher.matches((byte) testvalue);
            if (matched) { byteList.add((byte) testvalue); }
            assertEquals(testmessage, matcher.isInverted(), matched);
        }
        byte[] bytes = new byte[byteList.size()];
        int pos = 0;
        for (Byte b : byteList) {
            bytes[pos++] = b;
        }
        message = String.format("Testing byte array on range %d-%d, matcher %s inverted", start, end, matcher.isInverted()? "" : " not");
        assertArrayEquals(message, bytes, matcher.getMatchingBytes() );
    }

}