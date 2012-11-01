/*
 * Copyright Matt Palmer 2012, All rights reserved.
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

package net.domesdaybook.parser.regex;

import static org.junit.Assert.assertEquals;
import net.domesdaybook.parser.ParseException;
import net.domesdaybook.parser.tree.ParseTree;
import net.domesdaybook.parser.tree.ParseTreeType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.fail;


/**
 * @author Matt Palmer
 *
 */
public class RegexParserTest {

	RegexParser parser;
	
	@Before
	public void setUp() {
		parser = new RegexParser();
	}
	
	@After
	public void tearDown() {
		parser = null;
	}
	
	/*
	 * Tests for null or whitespace only:
	 */
	
	@Test(expected=net.domesdaybook.parser.ParseException.class)
	public final void testNull() throws ParseException {
		parser.parse(null);
	}
	
	@Test(expected=net.domesdaybook.parser.ParseException.class)
	public final void testEmpty() throws ParseException {
		parser.parse("");
	}
	
	@Test(expected=net.domesdaybook.parser.ParseException.class)
	public final void testTab() throws ParseException {
		parser.parse("\t");
	}	
	
	@Test(expected=net.domesdaybook.parser.ParseException.class)
	public final void testTabs() throws ParseException {
		parser.parse("\t\t\t");
	}
	
	@Test(expected=net.domesdaybook.parser.ParseException.class)
	public final void testNewLine() throws ParseException {
		parser.parse("\n");
	}	
	
	@Test(expected=net.domesdaybook.parser.ParseException.class)
	public final void testNewLines() throws ParseException {
		parser.parse("\n\n");
	}		
	
	@Test(expected=net.domesdaybook.parser.ParseException.class)
	public final void testSpace() throws ParseException {
		parser.parse(" ");
	}
	
	@Test(expected=net.domesdaybook.parser.ParseException.class)
	public final void testSpaces() throws ParseException {
		parser.parse("      ");
	}
	
	@Test(expected=net.domesdaybook.parser.ParseException.class)
	public final void testSpacesNewLine() throws ParseException {
		parser.parse("      \n");
	}	
	
	@Test(expected=net.domesdaybook.parser.ParseException.class)
	public final void testWhitespaceLines() throws ParseException {
		parser.parse("  \n\t\r   \t \n \t \n ");
	}		
	
	/*
	 * Tests for comments.
	 */
	
	@Test(expected=net.domesdaybook.parser.ParseException.class)
	public final void testCommentNoEndingNewLine() throws ParseException {
		parser.parse("#Just a comment");
	}
	
	@Test(expected=net.domesdaybook.parser.ParseException.class)
	public final void testCommentNewLine() throws ParseException {
		parser.parse("#Just a comment\n");
	}	
	
	@Test(expected=net.domesdaybook.parser.ParseException.class)
	public final void testCommentsNewLines() throws ParseException {
		parser.parse("\n    #Just a comment  \n\r  #Another comment\n");
	}		
	
	@Test(expected=net.domesdaybook.parser.ParseException.class)
	public final void testCommentsNoEndingNewLine() throws ParseException {
		parser.parse("\n    #Just a comment  \n\r \t #Another comment");
	}		
	
	/*
	 * Tests for ParseTreeType.BYTE
	 */
	
	@Test
	public final void testByte() throws ParseException {
		testByte("01", 				(byte) 0x01);
		testByte("  01  ", 			(byte) 0x01); 
		testByte("FF", 				(byte) 0xFF);
		testByte("\tFF", 			(byte) 0xFF); 
		testByte("00", 				(byte) 0x00);
		testByte("\n\r00", 			(byte) 0x00);
		testByte("cd", 				(byte) 0xcd); 
		testByte("cd\t \n", 		(byte) 0xcd);
		testByte("d4", 				(byte) 0xD4);
		testByte(" \t   d4\t   ", 	(byte) 0xd4); 
		testByte("fe", 				(byte) 0xfe);
		testByte("fe   \r\t\n", 	(byte) 0xFE);  
		testByte("7e", 				(byte) 0x7e);
		testByte("7e", 				(byte) 0x7e);   	
		
		testBad("0");
		testBad(" 1");
		testBad("\t\ta");
		testBad("1g");
		testBad(" xy\t");
	}
	
	private void testByte(String expression, byte value) throws ParseException {
		ParseTree node = parser.parse(expression);
		assertEquals("Expression" + expression + " has type BYTE", ParseTreeType.BYTE, node.getParseTreeType());
		assertEquals("Expression" + expression + " has value" + value, value, node.getByteValue());
		assertEquals("Expression" + expression + " has no children", 0, node.getChildren().size());
	}	
	
	/*
	 * Tests for ParseTreeType.ALL_BITMASK
	 */
	
	@Test
	public final void testAllBitmask() throws ParseException {
		testAllBitmask("&01", 				(byte) 0x01);
		testAllBitmask("  &01   ", 			(byte) 0x01);
		testAllBitmask("&FF",				(byte) 0xFF);
		testAllBitmask("\t&ff", 			(byte) 0xff);
		testAllBitmask("&00", 				(byte) 0x00);
		testAllBitmask("\n\r     &00", 		(byte) 0x00);
		testAllBitmask("&cd", 				(byte) 0xcd);
		testAllBitmask("\n\r\n\r&cD\t   ",	(byte) 0xCd);
		testAllBitmask("&d4",				(byte) 0xD4);
		testAllBitmask("  &D4\t",			(byte) 0xd4);
		testAllBitmask("&fe", 				(byte) 0xfe);
		testAllBitmask(" \t\t \t&fE\r",		(byte) 0xfE);
	}
	
	private void testAllBitmask(String expression, byte value) throws ParseException {
		ParseTree node = parser.parse(expression);
		assertEquals("Expression " + expression + " type is ParseTreeType.ALL_BITMASK",  ParseTreeType.ALL_BITMASK, node.getParseTreeType());
		assertEquals("Expression " + expression + " value is: " + value, value, node.getByteValue());
	}
	
	
	/*
	 * Tests for ParseTreeType.ANY_BITMASK
	 */
	
	@Test
	public final void testAnyBitmask() throws ParseException {
		testAnyBitmask("~01", (byte) 0x01);
		testAnyBitmask("~FF", (byte) 0xFF);
		testAnyBitmask("~00", (byte) 0x00);
		testAnyBitmask("~cd", (byte) 0xcd);
		testAnyBitmask("~d4", (byte) 0xD4);
		testAnyBitmask("~fe", (byte) 0xfe);
	}
		
	private void testAnyBitmask(String expression, byte value) throws ParseException {
		ParseTree node = parser.parse(expression);
		assertEquals("Expression " + expression + " type is ParseTreeType.ANY_BITMASK", ParseTreeType.ANY_BITMASK, node.getParseTreeType());
		assertEquals("Expression " + expression + " value is: " + value, value, node.getByteValue());
	}	
	
	/*
	 * Tests for ParseTreeType.STRING
	 */
	
	@Test
	public final void testString() throws ParseException {
		testString("''");  
		testString("' '"); 	
		testString("'X'"); 	
		testString("'0'"); 	
		testString("'one two three four'"); 
		testString("'some words\nwith a new line'");
	}
	
	private void testString(String expression) throws ParseException {
		ParseTree node = parser.parse(expression);
		assertEquals("Expression " + expression + " type is ParseTreeType.STRING", ParseTreeType.STRING, node.getParseTreeType());
		assertEquals("Expression " + expression + " value is: " + stripQuotes(expression), stripQuotes(expression), node.getTextValue());
		assertEquals("Expression " + expression + " has no children", 0, node.getChildren().size());
	}
	
	private String stripQuotes(String fromString) {
		return fromString.substring(1, fromString.length() - 1);
	}
	
	/*
	 * Tests for ParseTreeType.CASE_INSENSITIVE_STRING
	 */
	
	@Test
	public final void testCaseInsensitiveString() throws ParseException {
		testCaseInsensitiveString("``");
		testCaseInsensitiveString("` `"); 
		testCaseInsensitiveString("`q`"); 	
		testCaseInsensitiveString("`7`"); 		
		testCaseInsensitiveString("`one two three four`"); 			
		testCaseInsensitiveString("`some words\nwith a new line`");	
	}

	private void testCaseInsensitiveString(String expression) throws ParseException {
		ParseTree node = parser.parse(expression);
		assertEquals("Expression " + expression + " has type ParseTreeType.CASE_INSENSITIVE_STRING", ParseTreeType.CASE_INSENSITIVE_STRING, node.getParseTreeType());
		assertEquals("Expression " + expression + " has value: " + stripQuotes(expression), stripQuotes(expression), node.getTextValue());
		assertEquals("Expression " + expression + " has no children", 0, node.getChildren().size());
	}		
	
	/*
	 * Tests for ParseTreeType.ANY
	 */
	
	
	
	/* 
	 * Tests for ParseTreeType.SET
	 */
	
	
	
	/*
	 * Tests for ParseTreeType.SEQUENCE
	 */


	private void testBad(String expression) {
		try {
			parser.parse(expression);
			fail("Expression " + expression + " was expected to generate a ParseException");
		} catch (ParseException expected) {}
	}
	

	
}
