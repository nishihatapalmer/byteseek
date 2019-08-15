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
package net.byteseek.utils;

/**
 * A static utility class containing string methods useful in byteseek.
 */
public final class StringUtils {

    /**
     * Converts an earlier byteseek expression to version 3 syntax, if possible.
     * <p>
     * If it doesn't need to do anything, or if it encounters any problem with the conversion,
     * for example incompatible syntax in the same expression, it will just return the original string passed in.
     * <p>
     * This method does <i>not</i> validate that the string is a valid byteseek expression, even if the string
     * was converted "successfully".  To validate that it is a real byteseek expression, you still need to parse it.
     * <p>
     * What it does guarantee is that any valid byteseek expression from earlier versions will always work the same in v3,
     * once it is converted by this function.
     * <p>
     * <b>When is it needed?</b> It is only needed if your expressions contain syntax for matching either the
     * All bitmasks (with the &amp; symbol), or Any bitmasks (with the ~ symbol).  If you know that you do not use
     * bitmask matching in your v1 or v2 expressions, then they are already fully v3 compatible.
     * These are unusual types of regex matching, and it's quite likely that they are not being used.
     * However, if you are not sure, there is no harm in running them through this method.
     * <p>
     * <b>Warning</b>  There is syntactic ambiguity between v3 and earlier versions of byteseek in how the "any bit" matching
     * operator <b>~</b> works. If you accidentally pass in a <b>V3</b> expression instead of an earlier one, and it uses the specific
     * Wildbit Any form <b>~<i>{hexbyte}</i></b>, for example, <b>~A1 ~01 ~F0</b>, without any other V3 specific syntax in it,
     * then these will be interpreted as Any bitmask matchers from an earlier version.  These would then be converted into a different Wildbit
     * matcher (V2 Any bitmask -&gt; V3 WildbitAny).  This would certainly alter the meaning of the expression.
     * <p>
     * If you specify <i>"warnOnAnyBitmaskConversion"</i> to be true, then if such a conversion happens,
     * a warning comment will be appended to the end of the returned regex, reading:
     * <b>#WARNING: Any Bitmask ~{byte} was converted to byteseek V3 Wildbit format.</b>
     * <p>
     * This particular form of V3 Wildbit Any matching (any bits from a single defined byte, with no wild bits at all)
     * should be rare in practice, as it is an unusual requirement (which can and often will be better represented by other
     * syntax).  Wildbit matching will almost always specify some wild bits (bits whose value we don't care about), using
     * the V3 underscore symbol (_).
     *
     * <p>
     * <b>Bottom line</b>  Don't pass V3 expressions into this method.  If you do, it will almost always be fine, except in the
     * one rare case described above.  You can obtain a warning as a comment in the returned regex if you require it.
     *
     * @param byteseekV1orV2Regex A byteseek expression defined prior to V3 syntax.
     * @param warnOnAnyBitmaskConversion if true, adds a warning comment to any converted regex that converted an Any bitmask.
     * @return A string with earlier syntactic elements converted to V3 syntax, or the original string if there were no
     *         such elements, or it wasn't possible to convert the string for any other reason.
     */
    public static String convertToByteseekV3Regex(final String byteseekV1orV2Regex, final boolean warnOnAnyBitmaskConversion) {

        // Initialise the constants and variables we need:
        final int length = byteseekV1orV2Regex.length();
        StringBuilder builder = null; // will only instantiate a StringBuilder if there is a need.
        boolean convertedAnyBitmask = false; // records whether we have performed an any bitmask conversion.

        // Process each character in the expression, throwing an exception if we detect a problem,
        // or returning the original string if we know it can't be earlier than a v3 expression.
        // If we manage to process all the characters, then we return the converted string (or the original, if
        // no conversions needed to be made).
        for (int charPos = 0; charPos < length; charPos++) {

            // Process the current character:
            final char theChar = byteseekV1orV2Regex.charAt(charPos);
            switch (theChar) {

                /******************************************************************************************
                 * Detect V3 syntax - if found, just return the original - it can't be an earlier byteseek expression.
                 */
                case 'i': // Detect V3 syntax for hex or binary or wild "don't care" bits:
                case 'x':
                case '_': {
                    return byteseekV1orV2Regex; // It can't be earlier than V3, so just return the original expression.
                }

                /******************************************************************************************
                 * Detect earlier syntax and convert it.
                 */
                case '&' : { // All Bitmask - convert to wildbit format.
                    if (builder == null) { // instantiate builder if necessary.
                        builder = new StringBuilder(length + 32);
                        for (int pos = 0; pos < charPos; pos++) {
                            builder.append(byteseekV1orV2Regex.charAt(pos));
                        }
                    }

                    // As long as there are at least two more hex digits to process:
                    if (charPos + 2 < length) {
                        final int byteValue = ByteUtils.hexByteValue(byteseekV1orV2Regex.charAt(charPos + 1), byteseekV1orV2Regex.charAt(charPos + 2));
                        if (byteValue < 0) {
                            return byteseekV1orV2Regex; // It isn't a valid byteseek format, so don't try to parse or convert it.  Just return the original.
                        }
                        StringUtils.appendWildByteRegex(builder, (byte) byteValue, (byte) byteValue);
                    } else {
                        return byteseekV1orV2Regex; // It isn't a valid byteseek format, so don't try to parse or convert it.  Just return the original.
                    }
                    charPos += 2;
                    break;
                }

                case '~' : { // Any Bitmask - convert to wildbit any format.
                    if (builder == null) { // instantiate builder if necessary.
                        builder = new StringBuilder(length + 32);
                        for (int pos = 0; pos < charPos; pos++) {
                            builder.append(byteseekV1orV2Regex.charAt(pos));
                        }
                    }

                    // As long as there are at least two more hex digits to process:
                    if (charPos + 2 < length) {
                        final int byteValue = ByteUtils.hexByteValue(byteseekV1orV2Regex.charAt(charPos + 1), byteseekV1orV2Regex.charAt(charPos + 2));
                        if (byteValue < 0) {
                            return byteseekV1orV2Regex; // It isn't a valid byteseek format, so don't try to parse or convert it.  Just return the original.
                        }
                        if (byteValue == 0) {
                            // An any mask of zero ~00 matches nothing, which can't be achieved by
                            // a standard wildbit any matcher, so we output something else instead.
                            builder.append("^__"); // Not Everything = Nothing.
                        } else { // Other masks that have at least one bit to match will convert exactly.
                            builder.append('~');
                            StringUtils.appendWildByteRegex(builder, (byte) byteValue, (byte) byteValue);
                        }
                    } else {
                        return byteseekV1orV2Regex; // It isn't a valid byteseek format, so don't try to parse or convert it.  Just return the original.
                    }
                    convertedAnyBitmask = true;
                    charPos += 2;
                    break;
                }

                /**************************************************************************************
                 * Process comments - scan to the next new line or end of regex.
                 */
                case '#': { // Open comment - scan to next new line or end of regex.
                    for (charPos = charPos; charPos < length; charPos++) {
                        if (byteseekV1orV2Regex.charAt(charPos) == '\n') {
                            break;
                        }
                    }
                    break;
                }

                /**************************************************************************************
                 * Process strings and case insensitive strings - scan to the next quote character.
                 */

                case '\'' : { // Open string - scan to past string.
                    if (builder != null) { // add the string to the builder
                        builder.append('\'');
                        for (charPos = charPos + 1; charPos < length; charPos++) {
                            char stringChar = byteseekV1orV2Regex.charAt(charPos);
                            builder.append(stringChar);
                            if (stringChar == '\'') {
                                break;
                            }
                        }
                    } else { // just find the end of the string.
                        charPos = byteseekV1orV2Regex.indexOf('\'', charPos + 1);
                        if (charPos < 0) {
                            charPos = length;
                        }
                    }
                    break;
                }

                case '`' : { // Open case insensitive string - scan to past string.
                    if (builder != null) { // add the string to the builder
                        builder.append('`');
                        for (charPos = charPos + 1; charPos < length; charPos++) {
                            char stringChar = byteseekV1orV2Regex.charAt(charPos);
                            builder.append(stringChar);
                            if (stringChar == '`') {
                                break;
                            }
                        }
                    } else { // just find the end of the string.
                        charPos = byteseekV1orV2Regex.indexOf('`', charPos + 1);
                        if (charPos < 0) {
                            charPos = length;
                        }
                    }
                    break;
                }

                /*************************************************************************************
                 * Add any other character to a string builder if instantiated:
                 */
                default: {
                    if (builder != null) {
                        builder.append(theChar);
                    }
                }
            }
        }

        // Add a warning if an Anybitmask was converted and the user specified they wanted the warning.
        if (convertedAnyBitmask && warnOnAnyBitmaskConversion) {
            // builder will never be null if an any bitmask has been converted.
            builder.append("\n#WARNING: Any Bitmask ~{byte} was converted to byteseek V3 Wildbit format.\n");
        }

        // Return either the original expression, or the newly built expression if it exists.
        return builder == null? byteseekV1orV2Regex : builder.toString();
    }

    /**
     * Returns a string representing a byteseek wildbit regular expression.
     * <p>
     * A wildbit byte is a byte to match, where certain bits we don't care about.
     * The bits we care about are containined in the mask.  Any bit set to 1 in the mask means
     * we care about the corresponding bit in the value.  If a bit is set to 0 in the mask, it
     * means we don't care about that bit in the value.
     * <p>
     * This method will return the most compact form of the representation.  If the mask applies
     * to a nibble of a byte, it will return a 2 digit hex byte, with the masked nibble digit as an underscore.
     * If the mask is more complex, it will return a binary value with the digits we don't care about
     * represented by underscores.  Note: the prefix for binary values in byteseek is 0i.
     *
     * @param value The value to match (excluding the bits whose value is zero in the mask).
     * @param mask  A bitmask with zero for bits we don't care about, and one for bits we do care about.
     * @return A string containing the most compact wildbit representation.
     */
    public static String toWildByteRegex(final byte value, final byte mask) {
        final StringBuilder builder = new StringBuilder(10);
        appendWildByteRegex(builder, value, mask);
        return builder.toString();
    }

    /**
     * Appends a string representing a byteseek wildbit regular expression to a StringBuilder.
     * <p>
     * A wildbit byte is a byte to match, where certain bits we don't care about.
     * The bits we care about are containined in the mask.  Any bit set to 1 in the mask means
     * we care about the corresponding bit in the value.  If a bit is set to 0 in the mask, it
     * means we don't care about that bit in the value.
     * <p>
     * This method will return the most compact form of the representation.  If the mask applies
     * to a nibble of a byte, it will return a 2 digit hex byte, with the masked nibble digit as an underscore.
     * If the mask is more complex, it will return a binary value with the digits we don't care about
     * represented by underscores.  Note: the prefix for binary values in byteseek is 0i.
     *
     * @param builder The StringBuilder to which this wildbit string will be appended.
     * @param value The value to match (excluding the bits whose value is zero in the mask).
     * @param mask  A bitmask with zero for bits we don't care about, and one for bits we do care about.
     */
    public static void appendWildByteRegex(final StringBuilder builder, final byte value, final byte mask) {
        switch (mask & 0xFF) {
            case(0) : { // all wildbits
                builder.append('_').append('_');
                break;
            }

            case(0x0F) : { // first nibble wild, second nibble not.
                builder.append('_');
                builder.append(String.format("%01x", value & 0x0F));
                break;
            }

            case(0xF0) : { // second nibble wild, first nibble not.
                builder.append(String.format("%01x", (value >>> 4) & 0x0F));
                builder.append('_');
                break;
            }

            case(0xFF) : { // no wildbits - just a hex byte:
                builder.append(String.format("%02x", value));
                break;
            }

            default: { // If not one of these, then we have binary wildbits.
                builder.append("0i"); // Append binary prefix.
                for (int bitPos = 7; bitPos >= 0; bitPos--) {
                    final int bit = 1 << bitPos;
                    if ((mask & bit) == bit) { // we care about this bit - it's the same as the value bit.
                        builder.append((value & bit) == bit? '1' : '0');
                    } else { // we don't care about this bit - it's a wild bit:
                        builder.append('_');
                    }
                }
            }
        }
    }

    /**
	 * Single quotes within the string passed in will be encoded as 0x27, the ASCII byte value of the quote,
	 * with the remaining parts of the string enclosed in single quotes.
	 *
	 * @param string  A string to encode as a byteseek regular expression string.
	 * @return A string encoded as a byteseek regex expression.
	 * @throws IllegalArgumentException if the string passed in is null.
	 */
	public static String encodeByteseekString(final String string) {
 		return quoteString(string, '\'', "27");
	}

    /**
	 * Public static utility method to encode case insensitive strings in byteseek regex format.
	 * Backticks within the string passed in will be encoded as 0x60, the ASCII hex byte value of the backtick,
	 * with the remaining parts of the string encoded in backticks.
	 *
	 * @param string A string to encode as a byteseek regular expression case insensitive string.
	 * @return A string encoded as a byteseek regular expression case insensitive string.
	 * @throws IllegalArgumentException if the string passed in is null.
	 */
	public static String encodeCaseInsensitiveString(final String string) {
		return quoteString(string, '`', "60");
	}

    /**
     * Encoding method which finds a "quote" character within a string, and encodes it as a string
     * value, enclosing the rest of the string within the quote character.
     *
     * @param string     The string to encode.
     * @param quoteChar The "quote" character which needs encoding.
     * @param replaceValue   The string to replace the quote character with.
     * @return           A string with the encodeChar replaced by its hex value, and the remaining string
     *                   enclosed by the quote char.
     * @throws IllegalArgumentException if the string or the replace value passed in is null
     */
    public static String quoteString(final String string, final char quoteChar, final String replaceValue) {
        ArgUtils.checkNullString(string, "string");
        ArgUtils.checkNullString(replaceValue, "replaceValue");

        final int length = string.length();
        final StringBuilder encoded = new StringBuilder(length + 32);
        boolean quoteOpen = false;
        for (int charPos = 0; charPos < length; charPos++) {
            final char currentChar = string.charAt(charPos);
            if (currentChar == quoteChar) { // a quote in the string - encode it as a byte value.
                if (quoteOpen) {
                    encoded.append(quoteChar); // close the existing quote in the string
                    quoteOpen = false;
                }
                if (charPos > 0) { // If we're past the first character and we're replacing, prepend a space.
                    encoded.append(' ');
                }
                encoded.append(replaceValue);
            } else {
                if (!quoteOpen) {
                    if (charPos > 0) {
                        encoded.append(' ');
                    }
                    encoded.append(quoteChar); // open  quotes on the string
                    quoteOpen = true;
                }
                encoded.append(currentChar);
            }
        }
        if (quoteOpen) encoded.append(quoteChar);
        if (encoded.length() == 0) { // If we have an empty string, just give quoted empty string.
            encoded.append(quoteChar).append(quoteChar);
        }
        return encoded.toString();
    }
}
