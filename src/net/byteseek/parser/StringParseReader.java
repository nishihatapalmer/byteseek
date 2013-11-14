/*
 * Copyright Matt Palmer 2012-13, All rights reserved.
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

import net.byteseek.object.ArgUtils;

/**
 * A class which can be used by parsers to read from a string, maintaining
 * a position in the string.  Hex bytes, strings and integers can be read
 * from the string directly.  Various utility methods are also provided 
 * allowing look ahead, read up to a position, and returning the last read
 * character.
 * 
 * @author Matt Palmer
 *
 */
public class StringParseReader {
	
	protected final String string;
	protected final int length;
	protected int position;
	
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
	 * Returns the position in the instance at which the next read will be made.
	 * 
	 * @return The position in the string of the next read.
	 */
	public final int getPosition() {
		return position;
	}
	
	/**
	 * Returns the string being read by this instance.
	 * 
	 * @return The string being read by this instance.
	 */
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
		return position >= length;
	}
	
	/**
	 * Reads a character from the string at the current position.
	 * The position will be incremented after calling this method.
	 * If the position is not within the bounds of the string, then -1 is returned.
	 * 
	 * @return The character at the current position, or -1 if we are past the end of the string.
	 */
	public final int read() {
		return position < length? string.charAt(position++) : -1;
	}
	
	/**
	 * Returns the character just behind the one we are about to read, or -1 if we have yet to read anything.
	 * 
	 * @return The character just behind the one we are about to read, or -1 if we have yet to read anything.
	 */
	public final int peekBehind() {
		return position > 0? string.charAt(position - 1) : -1;
	}
	
	/**
	 * Returns the character we are about to read, or -1 if we have already read the last character.
	 * 
	 * @return The character we are about to read, or -1 if we have already read the last character.
	 */
	public final int peekAhead() {
		return position < length? string.charAt(position) : -1;
	}
	
	/**
	 * Reads a two digit hex byte from the string, advancing the position.
	 * If there is not a hex byte readable at the current position, a ParseException will be thrown.
	 * 
	 * @return The byte value of the hex byte read from the string.
	 * 
	 * @throws ParseException If a hex byte could not be read from the string.
	 */
	public final byte readHexByte() throws ParseException {
		return (byte) hexByteValue(readBoundsChecked(), readBoundsChecked());
	}
	
	/**
	 * Reads the second digit of a two digit hex byte, given the first byte.
	 * This is useful for parsers which have already read a character, realised it
	 * is a hex byte, and want to read the next byte of the hex byte.
	 * 
	 * @param firstHexChar The value of the first hex byte read from the string.
	 * @return The byte value of the entire hex byte (the first and second digits)
	 * @throws ParseException If a hex byte could not be read from the string.
	 */
	public final byte readHexByte(final int firstHexChar) throws ParseException {
		return (byte) hexByteValue(firstHexChar, readBoundsChecked());
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
			value = (value * 10) + digit - '0';
			if (value > Integer.MAX_VALUE) {
				throw new ParseException("Integer overflow - the digits ending at position " + position + " are bigger than Integer.MAX_VALUE");
			}
			numDigits++;
			digit = read();
		}
		if (numDigits > 0) {
			// If the last read char wasn't -1 (read past end), then move back a char as we read something that wasn't a digit.
			if (digit >= 0) {
				position--; 
			}
			return (int) value;
		}
		throw new ParseException("No digits found at position " + position);
	}
	
	/**
	 * Reads a string from the string, up to the character that closes the string.
	 * It is assumed that the opening string character has already been read.
	 * @param closingChar The character that closes the string.
	 * @return The string up to the next closing character.
	 * @throws ParseException if a closing string character was not found
	 */
	public final String readString(final char closingChar) throws ParseException {
		final int endStringPosition = string.indexOf(closingChar, position);
		if (endStringPosition >= 0) {
			final String result = string.substring(position, endStringPosition);
			position = endStringPosition + 1;
			return result;
		}
		throw new ParseException("A closing string marker [" + closingChar + "] was not found up to position " + position);
	}
	
	/**
	 * Reads past the next occurrence of the character specified. 
	 * If the character doesn't appear, then we have read past the end of the string. 
	 * 
	 * @param endingChar The character to read past.
	 */
	public final void readPastChar(final char toChar) {
		final int charIndex = string.indexOf(toChar, position);
		if (charIndex >= 0) {
			position = charIndex + 1;
		} else {
			position = length;
		}
	}
	
	/**
	 * Reads the next character from the string, advancing the position by one.
	 * 
	 * @return The next character from the string, advancing the position by one.
	 * @throws ParseException If the position is not within the length of the string.
	 */
	public final int readBoundsChecked() throws ParseException {
		if (position < length) {
			return string.charAt(position++);
		}
		throw new ParseException("The end of the string has been reached at position " + position);
	}
	
	@Override
	public String toString() {
		return "StringParseReader[" + string + ']';
	}

	

	
	/**
	 * Returns an integer containing the decimal value (0-255) defined by two hex digits.
	 * 
	 * @param firstHexChar The first hex byte digit ('0'-'9' 'a'-'f' | 'A' - 'F')
	 * @param secondHexChar The second hex byte digit ('0'-'9' 'a'-'f' | 'A' - 'F')
	 * @return The decimal value (0-255) defined by the two hex digits. 
	 * @throws ParseException If the digits are not hex digits.
	 */
	protected final int hexByteValue(final int firstHexChar, final int secondHexChar) throws ParseException {
		return (hexDigitValue(firstHexChar) << 4 ) + hexDigitValue(secondHexChar);
	}
	
	/**
	 * Returns an integer containing the decimal value (0-15) defined by a single hex digit.
	 * 
	 * @param digit The hex byte digit ('0'-'9' 'a'-'f' | 'A' - 'F')
	 * @return The decimal value (0-15) defined by the hex digit.
	 * @throws ParseException If the digit is not a hex digit.
	 */
	protected final int hexDigitValue(final int digit) throws ParseException {
		if (digit >= '0' && digit <= '9') {
			return digit - '0';
		}
		if (digit >= 'a' && digit <= 'f')  {
			return digit - 'a' + 10;
		}
		if (digit >= 'A' && digit <= 'F') {
			return digit - 'A' + 10;
		}
		throw new ParseException("The character " + digit + " is not a hex digit at position " + position + " in expression " + string);
	}
}
