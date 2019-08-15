/*
 * Copyright Matt Palmer 2012-19, All rights reserved.
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
package net.byteseek.parser;

import net.byteseek.utils.ArgUtils;
import net.byteseek.utils.ByteUtils;

/**
 * A class which can be used by parsers to read from a string, maintaining
 * a position in the string.  Hex bytes, strings and integers can be read
 * from the string directly.  Various utility methods are also provided
 * allowing look ahead, read up to a position, and returning the last read
 * character.
 *
 * @author Matt Palmer
 */
public class StringParseReader implements ParseInfo {

    private static final int MAXINT10 = Integer.MAX_VALUE / 10;

    protected final String string;
    protected final int length;
    protected int nextPosition; // The next position to read.  Last read position is one behind this.

    /**
     * Constructs a StringParseReader from a string.  On construction,
     * the position will default to zero.
     *
     * @param string The string to read with a StringParseReader.
     * @throws IllegalArgumentException if the string passed in is null.
     */
    public StringParseReader(final String string) {
        ArgUtils.checkNullString(string);
        this.string = string;
        this.length = string.length();
    }

    /**
     * Returns the position which was last read.  If no reads have been made, it will be -1.
     *
     * @return The position in the string the last read.
     */
    public final int getPosition() {
        return nextPosition - 1;
    }

    /**
     * Returns the string being read by this instance.
     *
     * @return The string being read by this instance.
     */
    @Override
    public final String getString() {
        return string;
    }

    /**
     * Returns the length of the string being read by this instance.
     *
     * @return The length of the string being read by this instance.
     */
    public final int getLength() {
        return length;
    }

    /**
     * Returns true if we have read past the end of the string.
     *
     * @return True if the position is equal to or greater than the length of the string.
     */
    public final boolean atEnd() {
        return nextPosition >= length;
    }

    /**
     * Reads a character from the string at the current position.
     * The position will be incremented after calling this method.
     * If the position is not within the bounds of the string, then -1 is returned.
     *
     * @return The character at the current position, or -1 if we are past the end of the string.
     */
    public final int read() {
        return nextPosition < length ? string.charAt(nextPosition++) : -1;
    }

    /**
     * Returns the character just behind the one we are about to read, or -1 if we have yet to read anything.
     *
     * @return The character just behind the one we are about to read, or -1 if we have yet to read anything.
     */
    public final int peekBehind() {
        return nextPosition > 0 ? string.charAt(nextPosition - 1) : -1;
    }

    /**
     * Returns the character we are about to read, or -1 if we have already read the last character.
     *
     * @return The character we are about to read, or -1 if we have already read the last character.
     */
    public final int peekAhead() {
        return nextPosition < length ? string.charAt(nextPosition) : -1;
    }

    /**
     * Reads a byte from the string, advancing the position, encoded in hex or binary.
     * If there is not a byte readable at the current position, a ParseException will be thrown.
     * The byte can be specified as two hex digits (optionally prefixed by 0x), or it can be
     * written as a binary byte value of 8 zeros or ones, which must be prefixed by 0i.
     *
     * @return The byte value of the hex byte read from the string.
     * @throws ParseException If a hex byte could not be read from the string.
     */
    public final byte readByte() throws ParseException {
        return readByte(readBoundsChecked());
    }

    /**
     * Reads a byte from the string, given the first character already read, advancing the position.
     * If there is not a byte readable at the current position, a ParseException will be thrown.
     * The byte can be specified as two hex digits (optionally prefixed by 0x), or it can be
     * written as a binary byte value of 8 zeros or ones, which must be prefixed by 0i.
     *
     * @param firstHexChar The value of the first hex byte read from the string.
     * @return The byte value of the entire hex byte (the first and second digits)
     * @throws ParseException If a hex byte could not be read from the string.
     */
    public final byte readByte(final int firstHexChar) throws ParseException {
        int secondHexChar = readBoundsChecked();
        if (firstHexChar == '0') {
            switch (secondHexChar) {
                case 'x':
                    final int hexByteValue = ByteUtils.hexByteValue((char) readBoundsChecked(), (char) readBoundsChecked());
                    if (hexByteValue < 0) {
                        throw new ParseException("Not a hex byte.", this);
                    }
                    return (byte) hexByteValue;
                case 'i':
                    return readBinaryValue();
            }
        }
        final int hexByteValue = ByteUtils.hexByteValue((char) firstHexChar, (char) secondHexChar);
        if (hexByteValue < 0) {
            throw new ParseException("Not a hex byte.", this);
        }
        return (byte) hexByteValue;
    }

    /**
     * Reads a byte value expressed as binary in 8 zeros or ones, with no whitespace between them.
     *
     * @return The byte value expressed as a binary string.
     * @throws ParseException If the binary value does not contain 8 zero or one characters.
     */
    public final byte readBinaryValue() throws ParseException {
        int value = 0;
        for (int i = 0; i < 8; i++) {
            value <<= 1;
            final int binaryChar = readBoundsChecked();
            if (binaryChar == '1') {
                value |= 1;
            } else if (binaryChar != '0') {
                throw new ParseException("The binary value contained a character not zero or one: " + (char) binaryChar,
                        this);
            }
        }
        return (byte) value;
    }

    /**
     * A simple mutable record class that holds the specification of a wild byte.
     */
    public static final class WildByteSpec {
        public byte value;
        public byte mask;
        public boolean hasWildBits;
    }

    /**
     * Parses a wild byte in hex format, where each digit can either be a hex digit, or an underscore.
     * @param firstHexChar The first character of the hex byte.
     * @param secondHexChar The second character of the hex byte.
     * @param parseSpec A WildByteSpec object to hold the result of parsing.
     * @return true if the hex byte has wild nibbles, false if it's just a hex byte.
     * @throws ParseException If the characters are not hex digits or an underscore.
     */
    public final boolean parseWildHex(final int firstHexChar, final int secondHexChar,
                                      final WildByteSpec parseSpec) throws ParseException {
        // Default values:
        int value = 0;
        int mask = 0xFF;
        parseSpec.hasWildBits = false;

        // Determine what the hex value and mask are for the first nibble:
        if (firstHexChar == '_') { // first wild nibble.
            mask = 0x0F;
            parseSpec.hasWildBits = true;
        } else {                   // just a straight hex value.
            final int hexDigit = ByteUtils.hexDigitValue((char) firstHexChar);
            if (hexDigit < 0) {
                throw new ParseException("Not a hex digit value." + (char) firstHexChar, this);
            }
            value = hexDigit << 4;
        }

        // Determine what the hex value and mask are for the second nibble:
        if (secondHexChar == '_') { // second wild nibble
            mask &= 0xF0;
            parseSpec.hasWildBits = true;
        } else {
            final int hexDigit = ByteUtils.hexDigitValue((char) secondHexChar);
            if (hexDigit < 0) {
                throw new ParseException("Not a hex digit value." + (char) secondHexChar, this);
            }
            value += hexDigit;
        }

        // Set the mask and values, and return whether it has any wild bits.
        parseSpec.mask = (byte) mask;
        parseSpec.value = (byte) value;
        return parseSpec.hasWildBits;
    }

    /**
     * Reads a wild byte in hex or binary format, where each character is either a hex digit,
     * as either two hex digits, or "0x" followed by two hex digits, or "oi" followed by 8 binary digits.
     * @param parseSpec A WildByteSpec object to hold the result of parsing.
     * @return true if the byte has wild bits.
     * @throws ParseException If the string is not a valid wild byte in hex or binary.
     */
    public final boolean readWildByte(final WildByteSpec parseSpec) throws ParseException {
        return readWildByte(readBoundsChecked(), parseSpec);
    }

    /**
     * Reads a wild byte in hex or binary format, where each character is either a hex digit o4 underscore.
     * There can be two hex digits, "0x" followed by two hex digits, or "0i" followed by 8 binary digits or underscores.
     * @param firstChar The first character of the wild byte.
     * @param parseSpec A WildByteSpec object to hold the result of parsing.
     * @return true if the byte has wild bits.
     * @throws ParseException If the string is not a valid wild byte in hex or binary.
     */
    public final boolean readWildByte(final int firstChar,
                                      final WildByteSpec parseSpec) throws ParseException {
        int secondHexChar = readBoundsChecked();
        if (firstChar == '0') {
            switch (secondHexChar) {
                case 'x':
                    return parseWildHex(readBoundsChecked(), readBoundsChecked(), parseSpec);
                case 'i':
                    return readWildBinary(parseSpec);
            }
        }
        return parseWildHex(firstChar, secondHexChar, parseSpec);
    }

    /**
     * Reads a wild binary string, consisting of 8 digits which are either 0, 1 or an underscore.
     * @param parseSpec A WildByteSpec object to hold the result of parsing.
     * @return true if the binary value has wild bits.
     * @throws ParseException If any of the 8 characters are not 0, 1 or underscore.
     */
    public final boolean readWildBinary(final WildByteSpec parseSpec) throws ParseException {
        int value = 0;
        int mask = 0xFF;
        parseSpec.hasWildBits = false;
        for (int i = 7; i >= 0; i--) {
            final int binaryChar = readBoundsChecked();
            switch (binaryChar) {
                case '0':
                    break; // nothing to do for a zero value.
                case '1': {
                    value |= (1 << i); // set the value bit at this position to 1.
                    break;
                }
                case '_': {
                    mask &= ~(1 << i); // zero out the bit in the mask with a wild value.
                    parseSpec.hasWildBits = true;
                    break;
                }
                default:
                    throw new ParseException("The binary value contained a character not zero or one: " + (char) binaryChar,
                            this);
            }
        }
        parseSpec.value = (byte) value;
        parseSpec.mask = (byte) mask;
        return parseSpec.hasWildBits;
    }

    /**
     * Reads a sequence of digits '0' to '9' from the string at this point,
     * returning the integer value.
     *
     * @return The integer value of the stream of digits.
     * @throws ParseException If there were no digits at the current position or the digits are
     *                        a number bigger than the Integer.MAX_VALUE.
     */
    public final int readInt() throws ParseException {
        long value = 0;
        int digit = read();
        int numDigits = 0;
        while (digit >= '0' && digit <= '9') {
            if (value >= MAXINT10) {
                throw new ParseException("Integer overflow - the digits are bigger than Integer.MAX_VALUE", this);
            }
            value = (value * 10) + digit - '0';
            numDigits++;
            digit = read();
        }
        if (numDigits > 0) {
            // If the last read char wasn't -1 (read past end), then move back a char as we read something that wasn't a digit.
            if (digit >= 0) {
                nextPosition--;
            }
            return (int) value;
        }
        throw new ParseException("No digits found.", this);
    }

    /**
     * Reads a string from the string, up to the character that closes the string.
     * It is assumed that the opening string character has already been read.
     *
     * @param closingChar The character that closes the string.
     * @return The string up to the next closing character.
     * @throws ParseException if a closing string character was not found, or the string is empty.
     */
    public final String readString(final char closingChar) throws ParseException {
        final int endStringPosition = string.indexOf(closingChar, nextPosition);
        if (endStringPosition >= 0) {
            final String result = string.substring(nextPosition, endStringPosition);
            if (result.isEmpty()) {
                throw new ParseException("The string is empty.", this);
            }
            nextPosition = endStringPosition + 1;
            return result;
        }
        throw new ParseException("A closing string marker (" + closingChar + ") was not found", this);
    }

    /**
     * Reads past the next occurrence of the character specified.
     * If the character doesn't appear, then we have read past the end of the string.
     *
     * @param toChar The character to read past.
     */
    public final void readPastChar(final char toChar) {
        final int charIndex = string.indexOf(toChar, nextPosition);
        if (charIndex >= 0) {
            nextPosition = charIndex + 1;
        } else {
            nextPosition = length;
        }
    }

    /**
     * Reads the next character from the string, advancing the position by one.
     *
     * @return The next character from the string, advancing the position by one.
     * @throws ParseException If the position is not within the length of the string.
     */
    public final int readBoundsChecked() throws ParseException {
        if (nextPosition < length) {
            return string.charAt(nextPosition++);
        }
        throw new ParseException("No more characters.", this);
    }

    @Override
    public String toString() {
        return "StringParseReader(position: " + getPosition() + " string:" + string + ')';
    }


}
