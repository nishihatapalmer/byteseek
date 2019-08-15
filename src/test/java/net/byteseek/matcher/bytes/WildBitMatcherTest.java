/*
 * Copyright Matt Palmer 2019, All rights reserved.
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

import net.byteseek.io.reader.InputStreamReader;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.utils.ByteUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class WildBitMatcherTest extends BaseMatcherTest {

    @Test
    public void matches() throws Exception {
        for (int mask = 0; mask < 256; mask++) {
            for (int value = 0; value < 256; value++) {
                testMatches((byte) value, (byte) mask);
            }
        }
    }

    private void testMatches(final byte value, final byte mask) throws Exception {
        WildBitMatcher matcher = new WildBitMatcher(value, mask);
        WildBitMatcher inverted = new WildBitMatcher(value, mask, true);
        testAbstractMethods(matcher);
        testAbstractMethods(inverted);
        int matchCount = 0;
        for (int byteToTest = 0; byteToTest < 256; byteToTest++) {
            final byte byteValue = (byte) byteToTest;
            final byte valueToMatch = (byte) (byteValue & mask);
            final byte matchingValue = (byte) (value & mask);
            final boolean shouldMatch = valueToMatch == matchingValue;
            assertEquals(shouldMatch, matcher.matches(byteValue));
            assertEquals(shouldMatch, matcher.matches(BYTE_VALUES, byteToTest));
            assertEquals(shouldMatch, matcher.matchesNoBoundsCheck(BYTE_VALUES, byteToTest));
            assertEquals(shouldMatch, matcher.matches(reader, byteToTest));

            assertNotEquals(shouldMatch, inverted.matches(byteValue));
            assertNotEquals(shouldMatch, inverted.matches(BYTE_VALUES, byteToTest));
            assertNotEquals(shouldMatch, inverted.matchesNoBoundsCheck(BYTE_VALUES, byteToTest));
            assertNotEquals(shouldMatch, inverted.matches(reader, byteToTest));

            if (shouldMatch) {
                matchCount++;
            }
        }
        final int numberWildBits = ByteUtils.countUnsetBits(mask);
        final int expectedCount  = 1 << numberWildBits;
        assertEquals(expectedCount, matchCount);
    }

    @Test
    public void getMatchingBytes() throws Exception {
        for (int mask = 0; mask < 256; mask++) {
            for (int value = 0; value < 256; value++) {
                testMatchingBytes((byte) value, (byte) mask);
            }
        }
    }

    private void testMatchingBytes(final byte value, final byte mask) {
        WildBitMatcher matcher = new WildBitMatcher(value, mask);
        WildBitMatcher inverted = new WildBitMatcher(value, mask, true);

        final int numberWildBits = ByteUtils.countUnsetBits(mask);
        final int expectedCount  = 1 << numberWildBits;
        assertEquals(expectedCount, matcher.getNumberOfMatchingBytes());
        assertEquals(256 - expectedCount, inverted.getNumberOfMatchingBytes());

        byte[] matchingBytes = matcher.getMatchingBytes();
        byte[] invertedBytes = inverted.getMatchingBytes();

        assertEquals(expectedCount, matchingBytes.length);
        assertEquals(256 - expectedCount, invertedBytes.length);

        for (int byteToTest = 0; byteToTest < matchingBytes.length; byteToTest++) {
            final byte byteValue = matchingBytes[byteToTest];
            assertTrue(matcher.matches(byteValue));
            assertFalse(inverted.matches(byteValue));
        }

        for (int byteToTest = 0; byteToTest < invertedBytes.length; byteToTest++) {
            final byte byteValue = invertedBytes[byteToTest];
            assertFalse(matcher.matches(byteValue));
            assertTrue(inverted.matches(byteValue));
        }
    }

    @Test
    public void toRegularExpression() throws Exception {
        for (int mask = 0; mask < 256; mask++) {
            for (int value = 0; value < 256; value++) {
                testRegex((byte) value, (byte) mask, false);
                testRegex((byte) value, (byte) mask, true);
            }
        }
    }

    private void testRegex(final byte value, final byte mask, final boolean inverted) {
        WildBitMatcher matcher = new WildBitMatcher(value, mask, inverted);
        String regex = matcher.toRegularExpression(true);
        String regex2 = matcher.toRegularExpression(false);
        assertEquals(regex, regex2); // no difference for pretty print.
        String reg = getRegex(value, mask, inverted);
        assertEquals(getRegex(value, mask, inverted), regex);
    }

    private String getRegex(final byte value, final byte mask, final boolean inverted) {
        switch (mask) {
            case 0: {
                return inverted? "^__" : "__";
            }
            case (byte) 0xF0: { // second nibble don't care:
                return inverted? String.format("^%x_", (value >>> 4) & 0x0F) :
                                 String.format("%x_", (value >>> 4) & 0x0F);
            }
            case (byte) 0x0F: { // first nibble don't care:
                return inverted? String.format("^_%x", value & 0xF) : String.format("_%x", value & 0xF);
            }
            case (byte) 0xFF: { // no mask, just return value.
                return inverted? String.format("^%02x", value & 0xFF) : String.format("%02x", value & 0xFF);
            }
            default : { // mixture of values:
                final int maskValue = mask & 0xFF;
                final int valueVal  = value & 0xFF;
                String retVal = inverted? "^0i" : "0i";
                for (int i = 7; i >= 0; i--) {
                    final int bitValue = 1 << i;
                    if ((maskValue & bitValue) == bitValue) { // mask bit is set, so we need value bit:
                        if ((valueVal & bitValue) == bitValue) {
                            retVal += "1";
                        } else {
                            retVal += '0';
                        }
                    } else {
                        retVal += "_";
                    }
                }
                return retVal;
            }
        }
    }

    @Test
    public void testToString() throws Exception {
        WildBitMatcher matcher = new WildBitMatcher((byte) 10, (byte) 20);
        assertTrue(matcher.toString().contains(matcher.getClass().getSimpleName()));
    }

    @Test
    public void testEqualsAndHashCode() {
        // Not equal to a different matcher type, even though they match the same things:
        WildBitMatcher matcher = new WildBitMatcher((byte) 0, (byte) 0);
        assertFalse(AnyByteMatcher.ANY_BYTE_MATCHER.equals(matcher));
        assertFalse(matcher.equals(AnyByteMatcher.ANY_BYTE_MATCHER));

        // Equal to a different matcher with the same values:
        WildBitMatcher matcher2 = new WildBitMatcher((byte) 0xF0, (byte) 0x0F);
        WildBitMatcher matcher3 = new WildBitMatcher((byte) 0xF0, (byte) 0x0F);
        assertTrue(matcher2.equals(matcher3));
        assertTrue(matcher3.equals(matcher2));
        assertEquals(matcher2.hashCode(), matcher3.hashCode());

        // But not if one of them is inverted:
        matcher3 = new WildBitMatcher((byte) 0xF0, (byte) 0x0F, true);
        assertFalse(matcher2.equals(matcher3));
        assertFalse(matcher3.equals(matcher2));

        // Equal to a different matcher with different values that are don't cares (so matches identically):
        WildBitMatcher matcher4 = new WildBitMatcher((byte) 0x00, (byte) 0x0F);
        assertTrue(matcher2.equals(matcher4));
        assertTrue(matcher4.equals(matcher2));
        assertEquals(matcher2.hashCode(), matcher4.hashCode());

        // But not if one of them is inverted:
        matcher2 = new WildBitMatcher((byte) 0xF0, (byte) 0x0F, true);
        assertFalse(matcher2.equals(matcher4));
        assertFalse(matcher4.equals(matcher2));

        // Not equal if they would match different values:
        WildBitMatcher matcher5 = new WildBitMatcher((byte) 0x9C, (byte) 0x73);
        WildBitMatcher matcher6 = new WildBitMatcher((byte) 0x32, (byte) 0xF1);
        assertFalse(matcher5.equals(matcher6));
        assertFalse(matcher6.equals(matcher5));

        // Still don't match if one of them is inverted:
        matcher6 = new WildBitMatcher((byte) 0x32, (byte) 0xF1, true);
        assertFalse(matcher5.equals(matcher6));
        assertFalse(matcher6.equals(matcher5));

        // Or if the other is inverted:
        matcher5 = new WildBitMatcher((byte) 0x9C, (byte) 0x73, true);
        assertFalse(matcher5.equals(matcher6));
        assertFalse(matcher6.equals(matcher5));
    }


}