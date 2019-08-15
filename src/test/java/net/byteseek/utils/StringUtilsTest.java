package net.byteseek.utils;

import net.byteseek.compiler.matcher.ByteMatcherCompiler;
import net.byteseek.matcher.bytes.AllBitmaskMatcher;
import net.byteseek.matcher.bytes.AnyBitmaskMatcher;
import net.byteseek.matcher.bytes.ByteMatcher;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class StringUtilsTest {

    @Test
    public void testConvertToV3Regex() {

        /* NO ALL OR ANY BITMASKS
         * Test no conversion happens for expressions which don't contain any or all bitmasks:
         */
        testNoConversion("simple sequence", "0102030405");
        testNoConversion("mixed sequence", "0102^[0304] #comment\n 05 03-4f 'and text' `case` ");

        /* V3 SYNTAX PRESENT
         * Test that no conversion happens when v3 syntax is present, even with any or all bitmasks.
         */
        testNoConversion("Any bitmask but with v3 hex bytes", "01 ~02 0x03");
        testNoConversion("Any bitmask but with v3 binary bytes", "01 ~02 0i00001111");
        testNoConversion("Any bitmask but with v3 hex wild nibble", "01 ~02 0_");
        testNoConversion("All bitmask but with v3 hex bytes", "01 &02 0x03");
        testNoConversion("All bitmask but with v3 binary bytes", "01 &02 0i00001111");
        testNoConversion("All bitmask but with v3 hex wild nibble", "01 &02 0_");

        /* STRINGS
         * Test that no conversion happens if the convertable syntax is inside a string,
         * and that conversion happens after a valid string is closed.
         */
        testNoConversion("Any bitmask inside a string", "01 '~02' 03");
        testNoConversion("Any bitmask inside a case sensitive string", "01 `~02` 03");
        testNoConversion("All bitmask inside a string", "01 '&02' 03");
        testNoConversion("All bitmask inside a case sensitive string", "01 `&02` 03");

        testConversion("Any bitmask before and after a string", "01 ~02 'a string' ~03", "01 ~0i______1_ 'a string' ~0i______11");
        testConversion("All bitmask before and after a string", "01 &02 'a string' &03", "01 0i______1_ 'a string' 0i______11");
        testConversion("Any bitmask before and after a case string", "01 ~02 `a string` ~03", "01 ~0i______1_ `a string` ~0i______11");
        testConversion("All bitmask before and after a case string", "01 &02 `a string` &03", "01 0i______1_ `a string` 0i______11");


        /* COMMENTS
         * Test that comments do not prevent conversion, even if they contain V3 syntax chars in them,
         * and that expressions inside comments are not converted.
         */
        testNoConversion("Any bitmask inside a comment", "01 # ~03\n 04");
        testNoConversion("All bitmask inside a comment", "01 # &03\n 04");
        testConversion("Comment with ix_ in it", "01 #ix_\n &F0", "01 #ix_\n f_");

        /* NO v3 SPECIFIC SYNTAX
         * Test conversion happens when there is an all or any bitmask, and no other v3 syntax present.
         */
        testConversion("All bitmask to wildbit", "01 &02", "01 0i______1_");
        testConversion("Any bitmask to wildbit", "f0 ~10 'text'", "f0 ~0i___1____ 'text'");

        /* INVALID SYNTAX CONVERTS
         *
         * Test conversion of invalid byteseek expressions occurs, if there is nothing blocking the conversion
         * of an apparent any or all bitmask (no byteseek v3 syntax and no other problems converting).
         * Remember - this method doesn't validate byteseek expressions.  It just converts syntactic
         * elements where it finds them as long as nothing interferes with that process.
         */
        testConversion("converted with string not closed", "01 ~02 'not closed", "01 ~0i______1_ 'not closed");
        testConversion("converted with string not closed", "01 &02 'not closed", "01 0i______1_ 'not closed");

        testConversion("converted with case string not closed", "01 ~02 `not closed", "01 ~0i______1_ `not closed");
        testConversion("converted with case string not closed", "01 &02 `not closed", "01 0i______1_ `not closed");

        testConversion("absolutely not byteseek +] ,,, &01", "absolutely not byteseek +] ,,, &01", "absolutely not byteseek +] ,,, 0i_______1");

        /* INVALID SYNTAX DOESN'T CONVERT
         *
         * Test that invalid syntax that prevents conversion returns the original string.
         */
        testNoConversion("Only one hex digit all bitmask", "01 &3 'something else'");
        testNoConversion("Only one hex digit any bitmask", "01 ~3 'something else'");
        testNoConversion("Invalid hex digit all bitmask", "01 &3g 'something else'");
        testNoConversion("Invalid hex digit any bitmask", "01 ~3z 'something else'");

        testNoConversion("Unclosed string", "01 'something else ~3a");
        testNoConversion("Unclosed string", "01 'something else &3a");
        testNoConversion("Unclosed case string", "01 `something else ~3a");
        testNoConversion("Unclosed case string", "01 `something else &3a");

        testNoConversion("Only one hex digit all bitmask", "01 &3");
        testNoConversion("Only one hex digit any bitmask", "01 ~3");
        testNoConversion("Invalid hex digit all bitmask", "01 &3g");
        testNoConversion("Invalid hex digit any bitmask", "01 ~3z");

    }

    private void testConversion(String message, String regex, String converted) {
        assertEquals(message, converted, StringUtils.convertToByteseekV3Regex(regex, false));
    }

    private void testNoConversion(String message, String regex) {
        assertEquals(message, regex, StringUtils.convertToByteseekV3Regex(regex, false));
    }

    @Test
    public void testAllBitmasksConvertCorrectly() throws Exception {
        for (int i = 0; i < 256; i++) {
            String regex = String.format("&%02x", i);
            AllBitmaskMatcher allMatcher = new AllBitmaskMatcher((byte) i);
            String wildRegex = StringUtils.convertToByteseekV3Regex(regex, false);
            ByteMatcher matcher = ByteMatcherCompiler.compileFrom(wildRegex);
            assertEquals(allMatcher.getNumberOfMatchingBytes(), matcher.getNumberOfMatchingBytes());
            Set<Byte> allMatching = ByteUtils.toSet(allMatcher.getMatchingBytes());
            Set<Byte> wildMatching = ByteUtils.toSet(matcher.getMatchingBytes());
            assertEquals(allMatching.size(), wildMatching.size());
            for (Byte b : allMatching) {
                assertTrue(wildMatching.contains(b));
            }
        }
    }

    @Test
    public void testAnyBitmasksConvertCorrectly() throws Exception {
        for (int i = 0; i < 256; i++) {
            String regex = String.format("~%02x", i);
            AnyBitmaskMatcher anyMatcher = new AnyBitmaskMatcher((byte) i);
            String wildRegex = StringUtils.convertToByteseekV3Regex(regex, false);
            ByteMatcher matcher = ByteMatcherCompiler.compileFrom(wildRegex);
            assertEquals(anyMatcher.getNumberOfMatchingBytes(), matcher.getNumberOfMatchingBytes());
            Set<Byte> allMatching = ByteUtils.toSet(anyMatcher.getMatchingBytes());
            Set<Byte> wildMatching = ByteUtils.toSet(matcher.getMatchingBytes());
            assertEquals(allMatching.size(), wildMatching.size());
            for (Byte b : allMatching) {
                assertTrue(wildMatching.contains(b));
            }
        }
    }

    @Test
    public void testWarningOnAnyBitmaskConversion() {
        String converted = StringUtils.convertToByteseekV3Regex("~02", false);
        assertFalse(converted.contains("#WARNING:"));

        converted = StringUtils.convertToByteseekV3Regex("~02", true);
        assertTrue(converted.contains("#WARNING:"));

        converted = StringUtils.convertToByteseekV3Regex("&02", false);
        assertFalse(converted.contains("#WARNING:"));

        converted = StringUtils.convertToByteseekV3Regex("&02", true);
        assertFalse(converted.contains("#WARNING:"));
    }

    @Test
    public void testToWildByteRegexNoWildbits() {
        for (int value = 0; value < 256; value++) {
            assertEquals(String.format("%02x", value), StringUtils.toWildByteRegex((byte) value, (byte) 0xFF));

            StringBuilder builder = new StringBuilder();
            StringUtils.appendWildByteRegex(builder, (byte) value, (byte) 0xFF);
            assertEquals(String.format("%02x", value), builder.toString());
        }
    }

    @Test
    public void testToWildByteRegexAllWildbits() {
        for (int value = 0; value < 256; value++) {
            assertEquals(String.format("__", value), StringUtils.toWildByteRegex((byte) value, (byte) 0x00));

            StringBuilder builder = new StringBuilder();
            StringUtils.appendWildByteRegex(builder, (byte) value, (byte) 0x00);
            assertEquals(String.format("__", value), builder.toString());
        }
    }

    @Test
    public void testWildFirstNibble() {
        for (int value = 0; value < 256; value++) {
            assertEquals(String.format("_%x", value & 0x0F), StringUtils.toWildByteRegex((byte) value, (byte) 0x0F));

            StringBuilder builder = new StringBuilder();
            StringUtils.appendWildByteRegex(builder, (byte) value, (byte) 0x0F);
            assertEquals(String.format("_%x", value & 0x0F), builder.toString());
        }
    }

    @Test
    public void testWildSecondNibble() {
        for (int value = 0; value < 256; value++) {
            assertEquals(String.format("%x_", value >>> 4), StringUtils.toWildByteRegex((byte) value, (byte) 0xF0));

            StringBuilder builder = new StringBuilder();
            StringUtils.appendWildByteRegex(builder, (byte) value, (byte) 0xF0);
            assertEquals(String.format("%x_", value >>> 4), builder.toString());
        }
    }

    @Test
    public void testBinaryWildbits() {
        for (int value = 0; value < 256; value++) {
            for (int mask = 0; mask < 256; mask++) {
                String regex = getRegex((byte) value, (byte) mask, false);
                assertEquals(regex, StringUtils.toWildByteRegex((byte) value, (byte) mask));
                StringBuilder builder = new StringBuilder();
                StringUtils.appendWildByteRegex(builder, (byte) value, (byte) mask);
                assertEquals(regex, builder.toString());
            }
        }
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
    public void testEncodeString() throws Exception {
        // Empty string
        testEncoded("", "''");

        // A space
        testEncoded(" ", "' '");

        // No quotes in the string
        testEncoded("No quotes in this sentence.", "'No quotes in this sentence.'");

        // Already quoted:
        testEncoded("'already quoted string'", "27 'already quoted string' 27");

        // One quote in the string
        testEncoded("that's", "'that' 27 's'");

        // Two quotes in the string:
        testEncoded("that's all Matt's strings.", "'that' 27 's all Matt' 27 's strings.'");

        // Two contiguous quotes in the string:
        testEncoded("that''s", "'that' 27 27 's'");

        // Four contiguous quotes in the string:
        testEncoded("that''''s", "'that' 27 27 27 27 's'");
    }

    private void testEncoded(String toEncode, String expected) {
        assertEquals(toEncode, expected, StringUtils.encodeByteseekString(toEncode));

        // Make case insensitive version of the string
        toEncode = toEncode.replace('\'', '`');
        expected = expected.replace('\'', '`').replace("27", "60");
        assertEquals(toEncode, expected, StringUtils.encodeCaseInsensitiveString(toEncode));

        String replace = " *anything*";
        char quoteChar = '&';
        toEncode = toEncode.replace('`', quoteChar);
        expected = expected.replace('`', quoteChar).replace("60", replace);
        assertEquals(toEncode, expected, StringUtils.quoteString(toEncode, quoteChar, replace));
    }


}