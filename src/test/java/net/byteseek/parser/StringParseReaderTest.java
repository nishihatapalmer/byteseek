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
		    final int expectedPosition = charCount - 1;
			assertEquals("Position " + expectedPosition + " in string [" + string + ']', expectedPosition, testReader.getPosition());
			testReader.read();
		}
		assertTrue("Position is one less than length", testReader.getPosition() == testReader.getLength() - 1);
		testReader.read();
		assertTrue("Position is still one less than length after another read", testReader.getPosition() == testReader.getLength() - 1);
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
			final int expectedPosition = charCount - 1;
			assertEquals("Position is " + expectedPosition  + " in string [" + string + ']', expectedPosition, testReader.getPosition());
			char nextChar = string.charAt(charCount);
			assertEquals("Next char is " + nextChar + " in string [" + string + ']', nextChar, peeked);
			int read = testReader.read();
			assertEquals("Position is " + (charCount) + " in string [" + string + ']', charCount, testReader.getPosition());
			assertEquals("Char read is " + read + " in string [" + string + ']', read, peeked);
		}
		assertEquals("Peeked at end of string [" + string + "] at position " + testReader.getPosition() + " is  -1", -1, testReader.peekAhead());
		assertEquals("Reading past end of string [" + string + "] at position " + testReader.getPosition() + " is -1", -1,   testReader.read());
		assertEquals("Peeked past end of string [" + string + "] at position " + testReader.getPosition() + " is  -1", -1, testReader.peekAhead());
	}

	@Test(expected=ParseException.class)
	public void testEmptyString() throws Exception {
		StringParseReader reader = new StringParseReader("''");
		reader.read();
		reader.readString('\'');
	}

	@Test(expected=ParseException.class)
	public void testUnclosedString() throws Exception {
		StringParseReader reader = new StringParseReader("'some text with no closing quote");
		reader.read();
		reader.readString('\'');
	}

	@Test
	public void testReadWildBinary() throws Exception {
		testReadWildBinary("00000000", 0x00, 0xFF);
		testReadWildBinary("11111111", 0xFF, 0xFF);

		testReadWildBinary("0000000_", 0x00, 0xFE);
		testReadWildBinary("1111111_", 0xFE, 0xFE);

		testReadWildBinary("_______0", 0x00, 0x01);
		testReadWildBinary("_______1", 0x01, 0x01);

		testReadWildBinary("____1111", 0x0F, 0x0F);
		testReadWildBinary("____0001", 0x01, 0x0F);

		testReadWildBinary("1111____", 0xF0, 0xF0);
		testReadWildBinary("1000____", 0x80, 0xF0);
	}

	private void testReadWildBinary(String string, int value, int mask) throws ParseException {
		StringParseReader reader = new StringParseReader(string);
		StringParseReader.WildByteSpec parseSpec = new StringParseReader.WildByteSpec();
		reader.readWildBinary(parseSpec);
		assertEquals(value, parseSpec.value & 0xFF);
		assertEquals(mask, parseSpec.mask & 0xFF);
		if (parseSpec.mask != (byte) 0xFF) {
			assertTrue(parseSpec.hasWildBits);
		} else {
			assertFalse(parseSpec.hasWildBits);
		}
	}

	@Test
	public void testReadBinaryByte() throws ParseException {
		for (int i = 0; i < 256; i++) {
			String s = String.format("%8s", Integer.toBinaryString(i)).replace(' ', '0');
			testReadBinaryByte(s, (byte) i);
		}
	}

	@Test
	public final void testBadBinaryByte() {
		testReadBadBinaryByte("0000000");
		testReadBadBinaryByte("0000000l");
		testReadBadBinaryByte("0000O001");
		testReadBadBinaryByte("1111111");
		testReadBadBinaryByte("1111111l");
		testReadBadBinaryByte("1111O110");
		testReadBadBinaryByte("1");
	}

	private void testReadBadBinaryByte(String s) {
		StringParseReader reader = new StringParseReader(s);
		try {
			reader.readBinaryValue();
			fail("Should have thrown ParseException when reading binary value " + s);
		} catch (ParseException expectedDoNothing) {}
	}

	private void testReadBinaryByte(String string, byte b) throws ParseException {
		// Read as just 8 binary digits:
		StringParseReader reader = new StringParseReader(string);
		byte read = reader.readBinaryValue();
		assertEquals(b, read);

		// Read as 8 binary digits with 0i in front of it.
		String x = "0i" + string;
		reader = new StringParseReader(x);
		read = reader.readByte();
		assertEquals(b, read);
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
	
	private void testReadHexByte(StringParseReader reader, String string) throws ParseException {
		assertEquals("String [" + string + "] is the same.", string, reader.getString());
		
		byte read = reader.readByte();
		byte converted = (byte) (Integer.valueOf(string.substring(string.length() - 2), 16).intValue());
		assertEquals("Byte value read " + read + " is equal to " + converted + " in string [" + string + ']',
				     read, converted);
	}

	private void testReadHexByte(String string) throws ParseException {
		testReadHexByte(new StringParseReader(string), string);
		testReadHexByte(new StringParseReader("0x" + string), "0x" + string);
	}
	
	private void expectHexByteParseException(String string) {
		StringParseReader testReader = new StringParseReader(string);
		try {
			testReader.readByte();
			fail("Should have thrown a ParseException trying to read a hex byte from string " + string);
		} catch (ParseException expected) {
		}
	}
	
	@Test
	public final void testReadWildByte() throws ParseException {
		testReadWildHexByte("01", 0x01, 0xFF);
		testReadWildHexByte("10", 0x10, 0xFF);

		testReadWildHexByte("0D", 0x0D, 0xFF);
		testReadWildHexByte("D0", 0xD0, 0xFF);

		testReadWildHexByte("D1", 0xD1, 0xFF);
		testReadWildHexByte("1D", 0x1D, 0xFF);

		testReadWildHexByte("FF", 0xFF, 0xFF);

		testReadWildHexByte("_1", 0x01, 0x0F);
		testReadWildHexByte("1_", 0x10, 0xF0);

		testReadWildHexByte("__", 0x00, 0x00);
	}

	private void testReadWildHexByte(String string, int value, int mask) throws ParseException {
		testReadWildByte(string, value, mask);
		testReadWildByte("0x" + string, value, mask);
	}

	private void testReadWildByte(String string, int value, int mask) throws ParseException {
		StringParseReader reader = new StringParseReader(string);
		int firstChar = reader.read();
		StringParseReader.WildByteSpec parseSpec = new StringParseReader.WildByteSpec();

		reader.readWildByte(firstChar, parseSpec);
		assertEquals(value, parseSpec.value & 0xFF);
		assertEquals(mask, parseSpec.mask & 0xFF);
		if ((parseSpec.mask & 0xFF) != 0xFF) {
			assertTrue(parseSpec.hasWildBits);
		} else {
			assertFalse(parseSpec.hasWildBits);
		}

        reader = new StringParseReader(string);
        reader.readWildByte(parseSpec);
        assertEquals(value, parseSpec.value & 0xFF);
        assertEquals(mask, parseSpec.mask & 0xFF);
        if ((parseSpec.mask & 0xFF) != 0xFF) {
            assertTrue(parseSpec.hasWildBits);
        } else {
            assertFalse(parseSpec.hasWildBits);
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
		byte read = testReader.readByte(firstChar);
		byte converted = (byte) (Integer.valueOf(string, 16).intValue());
		assertEquals("Byte value read " + read + " is equal to " + converted + " in string [" + string + ']',
				     read, converted);
	}

	@Test(expected=ParseException.class)
    public void testReadInvalidHexSecondByte() throws Exception {
	    StringParseReader reader = new StringParseReader("0x0h");
	    reader.readByte();
    }

    @Test(expected=ParseException.class)
    public void testReadInvalidHexFirstByte() throws Exception {
        StringParseReader reader = new StringParseReader("0xOA");
        reader.readByte();
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
		testReadString("up to the closing bracket) only", "up to the closing bracket", ')');

		expectReadStringParseException("Empty string", '\'');
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
		testReadPastChar("", '$', -1);
		testReadPastChar("0123456789A", '9', 9);
		testReadPastChar("0123456789A", 'X', 10);
		testReadPastChar(".......|*******", '|', 7);
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
