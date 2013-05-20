/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.byteseek.matcher.bytes;

import net.byteseek.matcher.bytes.OneByteMatcher;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class OneByteMatcherTest {

    /**
     * 
     */
    public OneByteMatcherTest() {
    }


    /**
     * Tests every possible byte value against every other non-matching
     * byte value.
     */
    @Test
    public void testMatcher() {
        for (int i = 0; i < 256; i++) {
            final byte theByte = (byte) i;
            final OneByteMatcher matcher = new OneByteMatcher(theByte);
            assertEquals("matches", true, matcher.matches(theByte));
            assertEquals("1 byte matches", 1, matcher.getNumberOfMatchingBytes());
            assertArrayEquals("matching bytes", new byte[] {theByte}, matcher.getMatchingBytes());
            final String regularExpression = String.format("%02x", theByte);
            assertEquals("regular expression", regularExpression, matcher.toRegularExpression(false));
            for (int x = 0; x < 256; x++) {
                if (x != i) {
                    final byte nomatch = (byte) x;
                    assertEquals("no match", false, matcher.matches(nomatch));
                }
            }
            if (i % 32 == 0) {
                String message = String.format("Matching byte %d", i);
                SimpleTimer.timeMatcher(message, matcher);
            }
        }

    }

}