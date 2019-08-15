package net.byteseek.matcher.bytes;

import net.byteseek.utils.ByteUtils;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class WildBitAnyMatcherTest extends BaseMatcherTest {

    @Test
    public void matches() throws Exception {
        for (int mask = 0; mask < 256; mask++) {
            for (int value = 0; value < 256; value++) {
                testMatches((byte) value, (byte) mask);
            }
        }
    }

    private void testMatches(final byte value, final byte mask) throws Exception {
        WildBitAnyMatcher matcher = new WildBitAnyMatcher(value, mask);
        WildBitAnyMatcher inverted = new WildBitAnyMatcher(value, mask, true);
        testAbstractMethods(matcher);
        testAbstractMethods(inverted);
        int matchCount = 0;
        final byte valueToNotMatch = (byte) ((~value) & mask);
        for (int byteToTest = 0; byteToTest < 256; byteToTest++) {
            final byte byteValue = (byte) byteToTest;
            final byte matchingValue = (byte) (byteValue & mask);
            final boolean shouldMatch = mask == 0? true : valueToNotMatch != matchingValue;
            assertEquals(value + " " + mask, shouldMatch, matcher.matches(byteValue));
            assertEquals(value + " " + mask,shouldMatch, matcher.matches(BYTE_VALUES, byteToTest));
            assertEquals(value + " " + mask,shouldMatch, matcher.matchesNoBoundsCheck(BYTE_VALUES, byteToTest));
            assertEquals(value + " " + mask,shouldMatch, matcher.matches(reader, byteToTest));

            assertNotEquals(value + " " + mask, shouldMatch, inverted.matches(byteValue));
            assertNotEquals(value + " " + mask, shouldMatch, inverted.matches(BYTE_VALUES, byteToTest));
            assertNotEquals(value + " " + mask, shouldMatch, inverted.matchesNoBoundsCheck(BYTE_VALUES, byteToTest));
            assertNotEquals(value + " " + mask, shouldMatch, inverted.matches(reader, byteToTest));

            if (shouldMatch) {
                matchCount++;
            }
        }
        final int numberWildBits = ByteUtils.countUnsetBits(mask);
        final int expectedCount  = mask == 0? 256 : 256 - (1 << numberWildBits);
        assertEquals(expectedCount, matchCount);
        assertEquals(expectedCount, matcher.getNumberOfMatchingBytes());
        assertEquals(256-expectedCount, inverted.getNumberOfMatchingBytes());
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
        WildBitAnyMatcher matcher = new WildBitAnyMatcher(value, mask);
        WildBitAnyMatcher inverted = new WildBitAnyMatcher(value, mask, true);

        final int numberWildBits = ByteUtils.countUnsetBits(mask);
        final int expectedCount  = mask == 0? 256 : 256 - (1 << numberWildBits);
        assertEquals(matcher.toString(), expectedCount, matcher.getNumberOfMatchingBytes());
        assertEquals(inverted.toString(), 256 - expectedCount, inverted.getNumberOfMatchingBytes());

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
        WildBitAnyMatcher matcher = new WildBitAnyMatcher(value, mask, inverted);
        String regex = matcher.toRegularExpression(true);
        String regex2 = matcher.toRegularExpression(false);
        assertEquals(regex, regex2); // no difference for pretty print.
        String reg = getRegex(value, mask, inverted);
        assertEquals(getRegex(value, mask, inverted), regex);
    }

    private String getRegex(final byte value, final byte mask, final boolean inverted) {
        switch (mask) {
            case 0: {
                return inverted? "^~__" : "~__";
            }
            case (byte) 0xF0: { // second nibble don't care:
                return inverted? String.format("^~%x_", (value >>> 4) & 0x0F) :
                        String.format("~%x_", (value >>> 4) & 0x0F);
            }
            case (byte) 0x0F: { // first nibble don't care:
                return inverted? String.format("^~_%x", value & 0xF) :
                        String.format("~_%x", value & 0xF);
            }
            case (byte) 0xFF: { // no wild bits - just return value.
                return inverted? String.format("^~%02x", value) : String.format("~%02x", value);
            }
            default : { // mixture of values:
                final int maskValue = mask & 0xFF;
                final int valueVal  = value & 0xFF;
                String retVal = inverted? "^~0i" : "~0i";
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
        WildBitAnyMatcher matcher = new WildBitAnyMatcher((byte) 10, (byte) 20);
        assertTrue(matcher.toString().contains(matcher.getClass().getSimpleName()));
    }

    @Test
    public void testEqualsAndHashCode() {
        // Not equal to a different matcher type, even though they match the same things:
        WildBitAnyMatcher matcher = new WildBitAnyMatcher((byte) 0, (byte) 0);
        assertFalse(AnyByteMatcher.ANY_BYTE_MATCHER.equals(matcher));
        assertFalse(matcher.equals(AnyByteMatcher.ANY_BYTE_MATCHER));

        // Equal to a different matcher with the same values:
        WildBitAnyMatcher matcher2 = new WildBitAnyMatcher((byte) 0xF0, (byte) 0x0F);
        WildBitAnyMatcher matcher3 = new WildBitAnyMatcher((byte) 0xF0, (byte) 0x0F);
        assertTrue(matcher2.equals(matcher3));
        assertTrue(matcher3.equals(matcher2));
        assertEquals(matcher2.hashCode(), matcher3.hashCode());

        // But not if one of them is inverted:
        matcher3 = new WildBitAnyMatcher((byte) 0xF0, (byte) 0x0F, true);
        assertFalse(matcher2.equals(matcher3));
        assertFalse(matcher3.equals(matcher2));

        // Equal to a different matcher with different values that are don't cares (so matches identically):
        WildBitAnyMatcher matcher4 = new WildBitAnyMatcher((byte) 0x00, (byte) 0x0F);
        assertTrue(matcher2.equals(matcher4));
        assertTrue(matcher4.equals(matcher2));
        assertEquals(matcher2.hashCode(), matcher4.hashCode());

        // But not if one of them is inverted:
        matcher2 = new WildBitAnyMatcher((byte) 0xF0, (byte) 0x0F, true);
        assertFalse(matcher2.equals(matcher4));
        assertFalse(matcher4.equals(matcher2));

        // Not equal if they would match different values:
        WildBitAnyMatcher matcher5 = new WildBitAnyMatcher((byte) 0x9C, (byte) 0x73);
        WildBitAnyMatcher matcher6 = new WildBitAnyMatcher((byte) 0x32, (byte) 0xF1);
        assertFalse(matcher5.equals(matcher6));
        assertFalse(matcher6.equals(matcher5));

        // Still don't match if one of them is inverted:
        matcher6 = new WildBitAnyMatcher((byte) 0x32, (byte) 0xF1, true);
        assertFalse(matcher5.equals(matcher6));
        assertFalse(matcher6.equals(matcher5));

        // Or if the other is inverted:
        matcher5 = new WildBitAnyMatcher((byte) 0x9C, (byte) 0x73, true);
        assertFalse(matcher5.equals(matcher6));
        assertFalse(matcher6.equals(matcher5));
    }

}