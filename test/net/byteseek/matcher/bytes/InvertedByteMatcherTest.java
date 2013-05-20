/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.byteseek.matcher.bytes;

import net.byteseek.matcher.bytes.InvertedByteMatcher;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class InvertedByteMatcherTest {

    /**
     * 
     */
    public InvertedByteMatcherTest() {
    }


    /**
     * Tests every possible byte value against every other non-matching
     * byte value.
     */
    @Test
    public void testMatcher() {
 
        for (int i = 0; i < 256; i++) {
            final byte theByte = (byte) i;
            final InvertedByteMatcher matcher = new InvertedByteMatcher(theByte);
            assertEquals("matches", false, matcher.matches(theByte));
            assertEquals("255 byte matches", 255, matcher.getNumberOfMatchingBytes());
            
            byte[] matchingBytes = new byte[255];
            int bytePos = 0;
            for (int q = 0; q < 256; q++) {
                if (q != i) matchingBytes[bytePos++] = (byte) q;
            }
   
            assertArrayEquals("matching bytes", matchingBytes, matcher.getMatchingBytes());
            
            final String regularExpression = String.format("[^%02x]", theByte);
            assertEquals("regular expression", regularExpression, matcher.toRegularExpression(false));
            for (int x = 0; x < 256; x++) {
                if (x != i) {
                    final byte match = (byte) x;
                    assertEquals("matches", true, matcher.matches(match));
                }
            }
            if (i % 32 == 0) {
                String message = String.format("Matching byte %d", i);
                SimpleTimer.timeMatcher(message, matcher);
            }
        }

    }

}
