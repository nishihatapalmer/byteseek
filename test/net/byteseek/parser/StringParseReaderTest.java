/*
 * Copyright Matt Palmer 2013, All rights reserved.
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

import static org.junit.Assert.*;

import org.junit.Test;

public class StringParseReaderTest {

	@SuppressWarnings("unused")
	@Test
	public final void testStringParseReader() {
		new StringParseReader("");
		new StringParseReader("A longer string");
		new StringParseReader("1");
	}
	
	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public final void testStringParseReaderNullString() {
		new StringParseReader(null);
	}
	
	@Test
	public final void testGetPosition() {
		testGetPosition("");
		testGetPosition(" ");
		testGetPosition("Test");
		testGetPosition("A much longer string to test all of the positions for");
	}
	
	private void testGetPosition(String string) {
		StringParseReader testReader = new StringParseReader(string);
		assertEquals("String [" + string + "] is the same.", string, testReader.getString());
		for (int charCount = 0; charCount < testReader.getLength(); charCount++) {
			assertEquals("Position " + charCount + " in string [" + string + ']', charCount, testReader.getPosition());
			testReader.read();
		}
		assertTrue("Position is equal to length", testReader.getPosition() == testReader.getLength());
		testReader.read();
		assertTrue("Position is still equal to length after another read", testReader.getPosition() == testReader.getLength());
	}

	@Test
	public final void testAtEnd() {
		testAtEnd("");
		testAtEnd("-");
		testAtEnd("Some longer string to test");
	}

	private void testAtEnd(String string) {
		StringParseReader testReader = new StringParseReader(string);
		assertEquals("String [" + string + "] is the same.", string, testReader.getString());
		for (int charCount = 0; charCount < testReader.getLength(); charCount++) {
			assertFalse("Not at end at position " + charCount + " in string [" + string + ']', testReader.atEnd());
			testReader.read();
		}
		assertTrue("Test reader for string [" + string + "] is at end", testReader.atEnd());
	}
	
	@Test
	public final void testRead() {
		testRead("");
		testRead("(");
		testRead("45");
		testRead("longer string to test");
	}

	private void testRead(String string) {
		StringParseReader testReader = new StringParseReader(string);
		assertEquals("String [" + string + "] is the same.", string, testReader.getString());
		for (int charCount = 0; charCount < testReader.getLength(); charCount++) {
			final char currentChar = string.charAt(charCount);
			final int currentInt = testReader.read();
			assertEquals("Position " + charCount + " in string [" + string + "] is correct.", currentChar, currentInt);
		}
		assertEquals("Read past end gives -1", -1, testReader.read());
	}
	
	@Test
	public final void testLastRead() {
		testLastRead("");
		testLastRead("X");
		testLastRead(".,");
		testLastRead("A longer string to test.");
	}

	private void testLastRead(String string) {
		StringParseReader testReader = new StringParseReader(string);
		assertEquals("String [" + string + "] is the same.", string, testReader.getString());
		assertEquals("Last read is -1 before reading anything for string [" + string + ']', -1, testReader.peekBehind());
		
		int lastRead = -1;
		for (int charCount = 0; charCount < testReader.getLength(); charCount++) {
			lastRead = testReader.read();
			assertEquals("Last read correct in string [" + string + "] at position " + charCount, lastRead, testReader.peekBehind()); 
		}
		
		assertEquals("Reading past end of string [" + string + "] at position " + testReader.getPosition() + " is -1", -1,   testReader.read());
		assertEquals("Last read past end of string [" + string + "] at position " + testReader.getPosition() + " is " + lastRead, lastRead, testReader.peekBehind());
	}
	
	@Test
	public final void testPeekAhead() {
		testPeekAhead("");
		testPeekAhead("X");
		testPeekAhead(".,");
		testPeekAhead("A longer string to test.");
	}
	
	private void testPeekAhead(String string) {
		StringParseReader testReader = new StringParseReader(string);
		assertEquals("String [" + string + "] is the same.", string, testReader.getString());
		if (string.length() == 0) {
			assertEquals("Peek ahead is -1 for a string of length 0 [" + string + ']', -1, testReader.peekAhead());	
		}
		
		for (int charCount = 0; charCount < testReader.getLength(); charCount++) {
			int peeked = testReader.peekAhead();
			assertEquals("Position is " + charCount + " in string [" + string + ']', charCount, testReader.getPosition());
			char nextChar = string.charAt(charCount);
			assertEquals("Next char is " + nextChar + " in string [" + string + ']', nextChar, peeked);
			int read = testReader.read();
			assertEquals("Position is " + (charCount + 1) + " in string [" + string + ']', charCount + 1, testReader.getPosition());
			assertEquals("Char read is " + read + " in string [" + string + ']', read, peeked);
		}
		assertEquals("Peeked at end of string [" + string + "] at position " + testReader.getPosition() + " is  -1", -1, testReader.peekAhead());
		assertEquals("Reading past end of string [" + string + "] at position " + testReader.getPosition() + " is -1", -1,   testReader.read());
		assertEquals("Peeked past end of string [" + string + "] at position " + testReader.getPosition() + " is  -1", -1, testReader.peekAhead());
	}

	@Test
	public final void testReadHexByte() throws ParseException {
		testReadHexByte("00");
		testReadHexByte("ff");
		testReadHexByte("Aa");
		testReadHexByte("7f");
		testReadHexByte("bc");
		testReadHexByte("34");
		testReadHexByte("eB");
		
		expectHexByteParseException("");
		expectHexByteParseException("0");
		expectHexByteParseException("a");
		expectHexByteParseException(" a");
		expectHexByteParseException("A");
		expectHexByteParseException("Ag");
		expectHexByteParseException("0*");
		expectHexByteParseException("0J");
		expectHexByteParseException("J0");
		expectHexByteParseException("XX");
	}
	
	private void testReadHexByte(String string) throws ParseException {
		StringParseReader testReader = new StringParseReader(string);
		assertEquals("String [" + string + "] is the same.", string, testReader.getString());
		
		byte read = testReader.readHexByte();
		byte converted = (byte) (Integer.valueOf(string, 16).intValue());
		assertEquals("Byte value read " + read + " is equal to " + converted + " in string [" + string + ']',
				     read, converted);
	}
	
	private void expectHexByteParseException(String string) {
		StringParseReader testReader = new StringParseReader(string);
		try {
			testReader.readHexByte();
			fail("Should have thrown a ParseException trying to read a hex byte from string " + string);
		} catch (ParseException expected) {
		}
	}
	

	@Test
	public final void testReadHexByteInt() throws ParseException {
		testReadHexByteInt("00");
		testReadHexByteInt("ff");
		testReadHexByteInt("Aa");
		testReadHexByteInt("7f");
		testReadHexByteInt("bc");
		testReadHexByteInt("34");
		testReadHexByteInt("eB");
	}

	private void testReadHexByteInt(String string) throws ParseException {
		StringParseReader testReader = new StringParseReader(string);
		assertEquals("String [" + string + "] is the same.", string, testReader.getString());
		
		int firstChar = testReader.read();
		byte read = testReader.readHexByte(firstChar);
		byte converted = (byte) (Integer.valueOf(string, 16).intValue());
		assertEquals("Byte value read " + read + " is equal to " + converted + " in string [" + string + ']',
				     read, converted);
	}
	
	@Test
	public final void testReadInt() throws ParseException {
		testReadInt("1",   1);
		testReadInt("01",  1);
		testReadInt("01A", 1);
		testReadInt("999", 999);
		testReadInt("999 123", 999);
		
		expectReadIntParseException("");
		expectReadIntParseException(" ");
		expectReadIntParseException(" 1");
		expectReadIntParseException("A1");
		expectReadIntParseException("99999999999999999999999999");
	}

	private void expectReadIntParseException(String string) {
		StringParseReader testReader = new StringParseReader(string);
		try {
			testReader.readInt();
			fail("Should have thrown a ParseException trying to read an integer from string " + string);
		} catch (ParseException expected) {
		}
	}
	
	private void testReadInt(String string, int value) throws ParseException {
		StringParseReader testReader = new StringParseReader(string);
		int result = testReader.readInt();
		assertEquals("Integer read from string [" + string + "] is " + value, value, result);
	}

	@Test
	public final void testReadString() throws ParseException {
		testReadString("'", "", '\'');
		testReadString("up to the closing bracket) only", "up to the closing bracket", ')');
		
		expectReadStringParseException("", ' ');
		expectReadStringParseException("Wrong closing quote'", '"');
		expectReadStringParseException("Wrong closing quote' with something else on the end", '"');
	}
	
	private void testReadString(String string, String expected, char closingChar) throws ParseException {
		StringParseReader testReader = new StringParseReader(string);
		assertEquals("String [" + string + "] with closing char " + closingChar + " has expected value [" + expected + ']',
				     expected, testReader.readString(closingChar));
	}
	
	private void expectReadStringParseException(String string, char closingChar) {
		StringParseReader testReader = new StringParseReader(string);
		try {
			testReader.readString(closingChar);
			fail("Reading a string in [" + string + "] with closing char " + closingChar + " should have thrown a ParseException");
		} catch (ParseException expected) {
		}
	}

	@Test
	public final void testReadPastChar() {
		testReadPastChar("", '$', 0);
		testReadPastChar("0123456789A", '9', 10);
		testReadPastChar("0123456789A", 'X', 11);
		testReadPastChar(".......|*******", '|', 8);
	}
	
	private void testReadPastChar(String string, char pastChar, int expectedPosition) {
		StringParseReader testReader = new StringParseReader(string);
		testReader.readPastChar(pastChar);
		assertEquals("Position in string [" + string + "] after reading past char " 
				     + pastChar + " is expected to be " + expectedPosition, 
				     expectedPosition, testReader.getPosition());
	}

	@Test
	public final void testReadBoundsChecked() throws ParseException {
		testReadBoundsChecked("");
		testReadBoundsChecked("0123456789");
		testReadBoundsChecked("a");
	}
	
	private void testReadBoundsChecked(String string) throws ParseException {
		StringParseReader testReader = new StringParseReader(string);
		for (int i = 0; i < testReader.getLength(); i++) {
			final int thechar = testReader.readBoundsChecked();
			assertEquals("Char is correct at position " + i + " in string [" + string + ']',
					     thechar, string.charAt(i));
		}
		
		try {
			testReader.readBoundsChecked();
			fail("Should have thrown ParseException when reading past the end of string [" + string + ']');
		} catch (ParseException expected) {
		}
	}

	@Test
	public final void testToString() {
		StringParseReader testReader = new StringParseReader("Something");
		boolean containsClassName = testReader.toString().contains(testReader.getClass().getSimpleName());
		boolean containsData = testReader.toString().contains("Something");
		assertTrue("To string contains class name and data", containsClassName && containsData);
	}
	

}
