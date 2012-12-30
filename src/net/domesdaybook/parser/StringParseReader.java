/**
 * 
 */
package net.domesdaybook.parser;

/**
 * @author Matt Palmer
 *
 */
public class StringParseReader {
	
	private final String string;
	private final int length;
	private int position;
	
	public StringParseReader(final String string) {
		this.string = string;
		this.length = string.length();
	}
	
	
	public int getPosition() {
		return position;
	}
	
	public boolean atEnd() {
		return position >= length;
	}
	
	public int read() {
		return position < length? string.charAt(position++) : -1;
	}
	
	public int lastRead() {
		return position > 0 && position < length? string.charAt(position - 1) : -1;
	}
	
	public int peekAhead() {
		return position < length? string.charAt(position) : -1;
	}
	
	public byte readHexByte() throws ParseException {
		final int firstHexChar = readBoundsChecked();
		final int secondHexChar = readBoundsChecked();
		return (byte) hexByteValue(firstHexChar, secondHexChar);
	}
	
	public byte readHexByte(final int firstHexChar) throws ParseException {
		return (byte) hexByteValue(firstHexChar, readBoundsChecked());
	}
	
	public int readInt() throws ParseException {
		int value = 0;
		int digit = readBoundsChecked();
		int numDigits = 0;
		while (digit >= '0' && digit <= '9') {
			value = (value * 10) + digit - '0';
			numDigits++;
			digit = readBoundsChecked();
		}
		if (numDigits > 0) {
			position--; // move back one, as we read a character which wasn't a digit.
			return value;
		}
		throw new ParseException("No digits found at position " + position);
	}
	
	public String readString(final char closingChar) throws ParseException {
		final int endStringPosition = string.indexOf(closingChar, position);
		if (endStringPosition >= 0) {
			final String result = string.substring(position, endStringPosition);
			if (result.isEmpty()) {
				throw new ParseException("Strings cannot be empty for string at position " + position);
			}
			position = endStringPosition + 1;
			return result;
		}
		throw new ParseException("A closing string marker [" + closingChar + "] was not found after position " + position);
	}
	
	public void readToChar(final char endingChar) {
		final int commentEnd = string.indexOf(endingChar, position);
		if (commentEnd >= 0) {
			position = commentEnd + 1;
		} else {
			position = length;
		}
	}
	
	public int findNextChar(final char firstChar, final char secondChar) {
		while (position < length) {
			final int theChar = string.charAt(position);
			if (theChar == firstChar || theChar == secondChar) {
				return theChar;
			}
			position++;
		}
		return -1;
	}

	public int readBoundsChecked() throws ParseException {
		if (position < length) {
			return string.charAt(position++);
		}
		throw new ParseException("Reached the end of the string unexpectedly at position " + position);
	}
	
	
	public String toString() {
		return "StringParseReader[" + string + ']';
	}
	
	
	private int hexByteValue(int firstHexChar, int secondHexChar) throws ParseException {
		return (hexDigitValue(firstHexChar) << 4 ) + hexDigitValue(secondHexChar);
	}
	
	private int hexDigitValue(int digit) throws ParseException {
		if (digit >= '0' && digit <= '9') {
			return digit - '0';
		}
		if (digit >= 'a' && digit <= 'f')  {
			return digit - 'a' + 10;
		}
		if (digit >= 'A' && digit <= 'F') {
			return digit - 'A' + 10;
		}
		throw new ParseException("The character " + digit + " is not a hex digit.");
	}
}
