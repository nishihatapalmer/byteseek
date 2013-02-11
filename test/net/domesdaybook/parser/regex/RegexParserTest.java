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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Random;

import net.domesdaybook.parser.ParseException;
import net.domesdaybook.parser.tree.ParseTree;
import net.domesdaybook.parser.tree.ParseTreeType;
import net.domesdaybook.util.bytes.ByteUtilities;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * 
 * @author Matt Palmer
 *
 */
public class RegexParserTest {

	Random random;
	RegexParser parser;
	
	@Before
	public void setUp() {
		random = new Random();
		parser = new RegexParser();
	}
	
	@After
	public void tearDown() {
		parser = null;
	}
	

	@Test
	/**
	 * Test null or empty input.
	 * 
	 * A null or empty input should throw a ParseException.
	 */	
	public final void testNullOrEmpty() {
		expectParseException("Null input",	null);
		expectParseException("Empty input",	"");
	}
	
	
	@Test
	/**
	 * Test whitespace. 
	 * 
	 * Whitespace consists of space, tab, new line and carriage return.
	 * 
	 * Whitespace should be stripped out between any other elements 
	 * by the parser, leaving nothing to produce a ParseTree with. 
	 * In turn this should cause a ParseException to be thrown.
	 * 
	 * Note: still unsure about throwing a ParseException when the
	 * syntax of the expression is perfectly valid.  It's just that
	 * it results in a null ParseTree if that is all there is.
	 */	
	public final void testWhiteSpace() {
		expectParseException("Tab", 				"\t");
		expectParseException("Tabs",				"\t\t\t");
		expectParseException("Newline", 			"\n");
		expectParseException("Newlines",			"\n\n");
		expectParseException("Carriage return",		"\r");
		expectParseException("Carriage returns",	"\r\r");
		expectParseException("Space",				" ");
		expectParseException("Spaces",				"    ");
		expectParseException("Spaces newline",		"      \n");
		expectParseException("Lots of whitespace",	"  \n\t\r   \t \n \t \n ");
	}

	
	@Test
	/**
	 * Test comments.
	 * 
	 * Comments are indicated by a # symbol anywhere on a line.  
	 * Everything up to the end of that line is regarded as comment.
	 * 
	 * Comments should be stripped out by the parser, leaving
	 * nothing to produce a ParseTree with.  In turn this should
	 * cause a ParseException to be thrown.
	 * 
	 * Note: still unsure about throwing a ParseException when the
	 * syntax of the expression is perfectly valid.  It's just that
	 * it results in a null ParseTree if that is all there is. 
	 */	
	public final void testComments() {
		expectParseException("Single line comment",				"#Just a comment");
		expectParseException("Comment with new line",			"#Just a comment\n");
		expectParseException("Comments on several lines",		"\n    #Just a comment  \n\r  #Another comment\n");
		expectParseException("Comments no ending new line",		"\n    #Just a comment  \n\r \t #Another comment");
	}
	
	
	@Test
	/**
	 * Test ParseTreeType.BYTE
	 * 
	 * Byte nodes contain a single byte value and have no child nodes.
	 * 
	 * They are parsed from a two-digit hex string, with any case allowed.
	 * Whitespace is not allowed between the hex digits.
	 */	
	public final void testByte() throws ParseException {
		expectParseException("Spaced hex",  						" a a");
		expectParseException("Newline hex", 						"0\n1");
		expectParseException("Spaced digits", 						"0 1");
		expectParseException("Single digit", 						"0");
		expectParseException("Space single digit ", 				" 1");
		expectParseException("Tabs single hex", 					"\t\ta");
		expectParseException("Illegal hex char",					"1g");
		expectParseException("Whitespace and illegal hex chars",	" xy\t");

		testByte("01", 						(byte) 0x01);
		testByte("  01  ", 					(byte) 0x01); 
		testByte("  01  # 0x01", 			(byte) 0x01); 
		testByte("FF", 						(byte) 0xFF);
		testByte("\tFF", 					(byte) 0xFF); 
		testByte("00", 						(byte) 0x00);
		testByte("\n\r00", 					(byte) 0x00);
		testByte("cd", 						(byte) 0xcd); 
		testByte("cd\t \n", 				(byte) 0xcd);
		testByte("d4", 						(byte) 0xD4);
		testByte(" \t   d4\t   ", 			(byte) 0xd4); 
		testByte("fe", 						(byte) 0xfe);
		testByte("fe   \r\t\n", 			(byte) 0xFE);  
		testByte("fe   \r\t\n # a comment", (byte) 0xFE);  
		testByte("7e", 						(byte) 0x7e);
		testByte("7e            ",			(byte) 0x7e);
		testByte("# a comment\ndd\t ",      (byte) 0xdd);
	}
	
	private void testByte(String expression, byte value) throws ParseException {
		testByte(parser.parse(expression), value);
	}	
	
	private void testByte(ParseTree node, byte value) throws ParseException {
		assertEquals("Node [" + node + "] has type BYTE",
					 ParseTreeType.BYTE, node.getParseTreeType());
		testByteValue(node, value);
	}
	
	private void testByteValue(ParseTree node, byte value) throws ParseException {
		assertEquals("Node " + node + " has byte value " + value,
                	 value, node.getByteValue());
		assertFalse("Node " + node + " is not inverted", node.isValueInverted());
		assertNotNull("Node children is not null",
				      node.getChildren());
		assertEquals("Node " + node + " has no children",
	                 0, node.getChildren().size());
	}
	

	@Test
	/**
	 * Test ParseTreeType.ALL_BITMASK
	 */
	public final void testAllBitmask() throws ParseException {
		expectParseException("No hex value following", "&");
		expectParseException("Partial hex value following", "&d");
		expectParseException("Bad hex value following", "&hex");
		expectParseException("Space between", "& 01");
		
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
		testAllBitmask(parser.parse(expression), value);
	}
	
	private void testAllBitmask(ParseTree node, byte value) throws ParseException {
		assertEquals("Node [" + node + "] type is ParseTreeType.ALL_BITMASK",
                ParseTreeType.ALL_BITMASK, node.getParseTreeType());
		testByteValue(node, value);
	}
	
	
	@Test
	/**
	 * Test ParseTreeType.ANY_BITMASK
	 */	
	public final void testAnyBitmask() throws ParseException {
		expectParseException("No hex value following", "~");
		expectParseException("Partial hex value following", "~d");
		expectParseException("Bad hex value following", "~hex");
		expectParseException("Space between", "~ 01");
		
		testAnyBitmask("~01",     (byte) 0x01);
		testAnyBitmask(" ~21 ",   (byte) 0x21);
		testAnyBitmask("~FF ",    (byte) 0xFF);
		testAnyBitmask("\t~00",   (byte) 0x00);
		testAnyBitmask("~cd\n",   (byte) 0xcd);
		testAnyBitmask("\n~d4\r", (byte) 0xD4);
		testAnyBitmask("~fe \t",  (byte) 0xfe);
	}
		
	private void testAnyBitmask(String expression, byte value) throws ParseException {
		testAnyBitmask(parser.parse(expression), value);
	}	
	
	
	private void testAnyBitmask(ParseTree node, byte value) throws ParseException {
		assertEquals("Node [" + node + "] type is ParseTreeType.ANY_BITMASK",
                ParseTreeType.ANY_BITMASK, node.getParseTreeType());
		testByteValue(node, value);
	}
	
	@Test
	/**
	 * Test ParseTreeType.STRING
	 */	
	//TODO: string nodes cannot be inverted by the parser, even if an inverted string is specified.
	public final void testString() throws ParseException {
		expectParseException("Empty string", "''");
		expectParseException("Unclosed string", "'a string");
		expectParseException("Unopened string", "abc'");
		expectParseException("Unopened string", "An unopened'");
		expectParseException("Mixed case quotes", "'Closed with case insensitive`");

		testString("' '"); 	
		testString("'X'"); 	
		testString("'0'"); 	
		testString("'one two three four'"); 
		testString("'some words\nwith a new line'");
	}
	
	private void testString(String expression) throws ParseException {
		testString(parser.parse(expression), stripQuotes(expression));
	}
	
	private void testString(ParseTree node, String value) throws ParseException {
		assertEquals("Node " + node + " type is ParseTreeType.STRING",
				  ParseTreeType.STRING, node.getParseTreeType());
		assertEquals("Node " + node + " value is: " + value, 
					 value, node.getTextValue());
		assertEquals("Node " + node + " has no children",
				     0, node.getChildren().size());
	}
	

	@Test
	/**
	 * Test ParseTreeType.CASE_INSENSITIVE_STRING
	 */	
	public final void testCaseInsensitiveString() throws ParseException {
		expectParseException("Empty string", "``");
		expectParseException("Unclosed string", "`a string");
		expectParseException("Unopened string", "abc`");
		expectParseException("Unopened string", "An unopened`");
		expectParseException("Mixed case quotes", "`Closed with case sensitive'");

		testCaseInsensitiveString("` `"); 
		testCaseInsensitiveString("`q`"); 	
		testCaseInsensitiveString("`7`"); 		
		testCaseInsensitiveString("`one two three four`"); 			
		testCaseInsensitiveString("`some words\nwith a new line`");	
	}

	private void testCaseInsensitiveString(String expression) throws ParseException {
		testCaseInsensitiveString(parser.parse(expression), stripQuotes(expression));
	}
	
	private void testCaseInsensitiveString(ParseTree node, String value) throws ParseException {
		assertEquals("Node " + node + " has type ParseTreeType.CASE_INSENSITIVE_STRING",
					 ParseTreeType.CASE_INSENSITIVE_STRING, node.getParseTreeType());
		assertEquals("Node " + node + " has value: " + value, 
					 value, node.getTextValue());
		assertEquals("Node " + node + " has no children",
					 0, node.getChildren().size());
	}		
	
	
	@Test
	/*
	 * Test ParseTreeType.ANY
	 */
	public void testAny() throws ParseException {
		testAny(".");
		testAny("   .   ");
		testAny("(.)");
		testAny(" (.) ");
	}
	
	private void testAny(String expression) throws ParseException {
		testAny(parser.parse(expression));
	}
	
	private void testAny(ParseTree node) {
		assertEquals("Node " + node + " has ParseTreeType.ANY",
				     ParseTreeType.ANY, node.getParseTreeType());
		assertNotNull("Node " + node + " children not null", node.getChildren());
		assertFalse("Node " + node + " is not inverted", node.isValueInverted());
		assertEquals("Node " + node + " has zero children", 
					 0, node.getChildren().size());
	}
	

	@Test
	/* 
	 * Test ParseTreeType.SET
	 */
	public void testSet() throws ParseException {
		testSet("[01]", new byte[] {(byte) 01});
	}
	
	private void testSet(String expression, byte[] values) throws ParseException {
		testSet(parser.parse(expression), values);
	}
	
	private void testSet(ParseTree node, byte[] values) throws ParseException {
		fail("not yet implemented");//todo: test set nodes.
	}

	@Test
	/**
	 * Test simple sequences of nodes containing a single byte value.  This includes the types:
	 * ParseTreeType.BYTE, ParseTreeType.ALL_BITMASK, ParseTreeType.ANY_BITMASK
	 */	
	public void testByteSequence() throws ParseException {
		expectParseException("Sequence with partial value", "00 01 0");
		expectParseException("Sequence with partial value", "00 0 02");
		
		byte[] values = ByteUtilities.toArray((byte) 0, (byte) 1);
		testByteSequence("0001", values);
		testByteSequence("00 01", values);
		testByteSequence("00\n\t01", values);
		testByteSequence("00 # zero byte\n\t01\t# one byte", values);

		values = ByteUtilities.toArray((byte)0xca, (byte)0xfe, (byte)0xbe,(byte)0xef);
		testByteSequence("Cafebeef", values);
		testByteSequence("Ca fe be ef", values);
		testByteSequence("\nCa\tfe  \t  \rbe ef", values);
		
		values = ByteUtilities.toArray((byte) 0x00, (byte) 0x7f, (byte) 0x45);
		testByteSequence(" 00 &7f ~45", values);
		
		values = ByteUtilities.toArray((byte) 0x09, (byte) 0x0a, (byte) 0x0b,
							  		   (byte) 0x0c, (byte) 0x0d, (byte) 0x1e);
		testByteSequence(" \\t \\n \\v \\f \\r \\e", values);

		for (int testNumber = 0; testNumber < 50; testNumber++) {
			testRandomByteSequence();
		}
	}
	
	private void testByteSequence(String expression, byte[] values) throws ParseException {
		testByteSequence(parser.parse(expression), values);
	}
	
	private void testByteSequence(ParseTree node, byte[] values) throws ParseException {
		assertEquals("Node " + node + " has type ParseTreeType.SEQUENCE", 
				  ParseTreeType.SEQUENCE, node.getParseTreeType());
		assertEquals("Node " + node + " has " + values.length + " children nodes",
					values.length, node.getChildren().size());
		testByteSequenceValues(node.getChildren(), values);
	}
	
	private void testRandomByteSequence() throws ParseException {
		// a sequence has to have at least two elements.
		int length = random.nextInt(100) + 2; 
		byte[] values = new byte[length];
		StringBuilder builder = new StringBuilder(length *2);
		for (int index = 0; index < length; index++) {
			values[index] = (byte) (random.nextInt(256));
			builder.append(hexString(values[index]));
		}
		testByteSequence(builder.toString(), values);
	}
	
	private void testByteSequenceValues(List<ParseTree> sequence, byte[] values) throws ParseException {
		int position = 0;
		for (ParseTree member : sequence) {
			assertEquals("Byte sequence node position " + position + " value is " + values[position],
						 values[position++], member.getByteValue());
		}	
	}
	

	
	@Test
	/**
	 * Test ParseTreeType.RANGE
	 */
	public void testRange() throws ParseException {
		expectParseException("Unclosed range hex byte", "01-");
		expectParseException("Unclosed range character", "'q'- ");
		expectParseException("Unclosed range bad comment", "01-#bad comment 02");
		expectParseException("More than one char", "'aa'-'f'");
		expectParseException("More than one char", "01-'more'");
		expectParseException("Inverted first range value", "^01-02");
		expectParseException("Inverted second range value", "01-^02");
		
		testRange("01-02",     (byte) 0x01, (byte) 0x02);
		testRange("01 - 02",   (byte) 0x01, (byte) 0x02);
		testRange("01\n-\n02", (byte) 0x01, (byte) 0x02);
		testRange("'a'-'z'",   (byte) 'a',  (byte) 'z');
		testRange("'a'-ff",    (byte) 'a',  (byte) 0xff);
		testRange("ff-'a'",    (byte) 0xff, (byte) 'a');
		
		testRangeSequence("01-0203",                       (byte) 0x01, (byte) 0x02, 0);
		testRangeSequence("0201 - 02",                     (byte) 0x01, (byte) 0x02, 1);
		testRangeSequence("0201\n-\n0201",                 (byte) 0x01, (byte) 0x02, 1);
		testRangeSequence("'word1' 'word2' 'a'-'z' 02 03", (byte) 'a',  (byte) 'z',  2);
		testRangeSequence("01 02 03 04 'a'-ff",            (byte) 'a',  (byte) 0xff, 4);
		testRangeSequence("fe'fe'ff-'a'023f",              (byte) 0xff, (byte) 'a',  2);
		
		testRangeSet("[01-02]",                (byte) 0x01, (byte) 0x02, 0);
		testRangeSet("[ 01 01 - 02 ]",         (byte) 0x01, (byte) 0x02, 1);
		testRangeSet("[01\n-\n02 03]",         (byte) 0x01, (byte) 0x02, 0);
		testRangeSet("['xyz' 'a'-'z' '123']",  (byte) 'a',  (byte) 'z',  1);
		testRangeSet("['a' 'f' 'a'-ff]",       (byte) 'a',  (byte) 0xff, 2);
		testRangeSet("[ff-'a']",               (byte) 0xff, (byte) 'a',  0);
	}
	
	private void testRange(String expression, byte value1, byte value2) throws ParseException {
		testRange(parser.parse(expression), value1, value2);
	}
	
	private void testRangeSequence(String expression, byte value1, byte value2, int index) throws ParseException {
		final ParseTree sequenceRangeValue = parser.parse(expression).getChildren().get(index);
		testRange(sequenceRangeValue, value1, value2);
	}
	
	private void testRangeSet(String expression, byte value1, byte value2, int index) throws ParseException {
		final ParseTree setRangeValue = parser.parse(expression).getChildren().get(index);
		testRange(setRangeValue, value1, value2);
	}

	private void testRange(ParseTree rangeNode, byte value1, byte value2) throws ParseException {
		assertEquals("Node " + rangeNode + " has type ParseTreeType.RANGE", 
			  	 ParseTreeType.RANGE, rangeNode.getParseTreeType());
		testRangeValueNode(rangeNode.getChildren().get(0), value1);
		testRangeValueNode(rangeNode.getChildren().get(1), value2);
	}
	
	private void testRangeValueNode(ParseTree rangeValueNode, byte value) throws ParseException {
		if (rangeValueNode.getParseTreeType() == ParseTreeType.BYTE) {
			assertEquals("Node " + rangeValueNode + " has byte value " + value,
					     value, rangeValueNode.getByteValue());
		} else if (rangeValueNode.getParseTreeType() == ParseTreeType.STRING) {
			final char charValue = rangeValueNode.getTextValue().charAt(0);
			assertEquals("Node " + rangeValueNode + " has byte value " + value,
				     value, (byte) charValue);
		}
	}
	
	@Test
	/**
	 * Test ParseTreeType.ZERO_TO_MANY
	 */
	public void testZeroToMany() throws ParseException {
		expectParseException("nothing to quantify", "*");
		expectParseException("double many", "01**");
		
		testByte(testZeroToMany("01*"), (byte) 01);
		testByte(testZeroToMany("(01)*"), (byte) 01);
		testByte(testZeroToMany("(01 *)"), (byte) 01);
		
		testAllBitmask(testZeroToMany("&fe*"), (byte) 0xfe);
		testAllBitmask(testZeroToMany("( &e1 )*"), (byte) 0xe1);
		
		testAnyBitmask(testZeroToMany("~34*"), (byte) 0x34);
		testAnyBitmask(testZeroToMany("( ~99     )   *  "), ( byte) 0x99);
		
		testAny(testZeroToMany(".*"));
		testAny(testZeroToMany("(.)*"));
		
		testString(testZeroToMany("'a string'*"), "a string");
		testString(testZeroToMany("( ' ')*"), " ");
		
		testCaseInsensitiveString(testZeroToMany("`abcdefghijklmnopqrstuvwxyz`*"), "abcdefghijklmnopqrstuvwxyz");
		testCaseInsensitiveString(testZeroToMany("  (`   `)    *"), "   ");
		
		testByteSequence(testZeroToMany("(01 02 03)*"),
				 						new byte[] {(byte) 0x01, (byte) 0x02, (byte) 0x03});
		testByteSequence(testZeroToMany("(ce ff 00 97 &32 ~22)*"),
									    new byte[] {(byte) 0xce, (byte) 0xff, (byte) 0x00,
									    			 (byte) 0x97, (byte) 0x32, (byte) 0x22});

	}

	private ParseTree testZeroToMany(String expression) throws ParseException {
		ParseTree node = parser.parse(expression);
		assertEquals("Expression " + expression + " has type ParseTreeType.ZERO_TO_MANY", 
				  	 ParseTreeType.ZERO_TO_MANY, node.getParseTreeType());
		assertEquals("Expression " + expression + " has one child node",
			         1, node.getChildren().size());		
		return node.getChildren().get(0);		
	}
	
	
	@Test
	/**
	 * Test ParseTreeType.ONE_TO_MANY
	 */
	public void testOneToMany() throws ParseException {
		expectParseException("nothing to quantify", "+");
		expectParseException("double many", "01++");
		
		testByte(testOneToMany("01+"), (byte) 01);
		testByte(testOneToMany("(01)+"), (byte) 01);
		testByte(testOneToMany("(01 +)"), (byte) 01);
		
		testAllBitmask(testOneToMany("&fe+"), (byte) 0xfe);
		testAllBitmask(testOneToMany("( &e1 )+"), (byte) 0xe1);
		
		testAnyBitmask(testOneToMany("~34+"), (byte) 0x34);
		testAnyBitmask(testOneToMany("( ~99     )   +  "), ( byte) 0x99);
		
		testAny(testOneToMany(".+"));
		testAny(testOneToMany("(.)+"));
		
		testString(testOneToMany("'a string'+"), "a string");
		testString(testOneToMany("( ' ')+"), " ");
		
		testCaseInsensitiveString(testOneToMany("`abcdefghijklmnopqrstuvwxyz`+"), "abcdefghijklmnopqrstuvwxyz");
		testCaseInsensitiveString(testOneToMany("  (`   `)  #comment\n  +"), "   ");
		
		testByteSequence(testOneToMany("(01 02 03)+"),
				 						new byte[] {(byte) 0x01, (byte) 0x02, (byte) 0x03});
		testByteSequence(testOneToMany("(ce ff 00 97 &32 ~22)+"),
									    new byte[] {(byte) 0xce, (byte) 0xff, (byte) 0x00,
									    			 (byte) 0x97, (byte) 0x32, (byte) 0x22});
	}

	private ParseTree testOneToMany(String expression) throws ParseException {
		ParseTree node = parser.parse(expression);
		assertEquals("Expression " + expression + " has type ParseTreeType.ONE_TO_MANY", 
				  	 ParseTreeType.ONE_TO_MANY, node.getParseTreeType());
		assertEquals("Expression " + expression + " has one child node",
			         1, node.getChildren().size());		
		return node.getChildren().get(0);		
	}
	
	
	@Test
	/**
	 * Test ParseTreeType.OPTIONAL
	 */
	public void testOptional() throws ParseException {
		expectParseException("nothing to make optional", "?");
		expectParseException("double optional", "01??");
		
		testByte(testOptional("01?"), (byte) 01);
		testByte(testOptional("(01)?"), (byte) 01);
		testByte(testOptional("(01 ?)"), (byte) 01);
		
		testAllBitmask(testOptional("&fe?"), (byte) 0xfe);
		testAllBitmask(testOptional("( &e1 )?"), (byte) 0xe1);
		
		testAnyBitmask(testOptional("~34?"), (byte) 0x34);
		testAnyBitmask(testOptional("( ~99     )   ?  "), ( byte) 0x99);
		
		testAny(testOptional(".?"));
		testAny(testOptional("(.)?"));
		
		testString(testOptional("'a string'?"), "a string");
		testString(testOptional("( ' ')?"), " ");
		
		testCaseInsensitiveString(testOptional("`abcdefghijklmnopqrstuvwxyz`?"), "abcdefghijklmnopqrstuvwxyz");
		testCaseInsensitiveString(testOptional("  (`   `)  #comment\n  ?"), "   ");
		
		testByteSequence(testOptional("(01 02 03)?"),
				 						new byte[] {(byte) 0x01, (byte) 0x02, (byte) 0x03});
		testByteSequence(testOptional("(ce ff 00 97 &32 ~22)?"),
									    new byte[] {(byte) 0xce, (byte) 0xff, (byte) 0x00,
									    			 (byte) 0x97, (byte) 0x32, (byte) 0x22});
	}

	private ParseTree testOptional(String expression) throws ParseException {
		ParseTree node = parser.parse(expression);
		assertEquals("Expression " + expression + " has type ParseTreeType.OPTIONAL", 
				  	 ParseTreeType.OPTIONAL, node.getParseTreeType());
		assertEquals("Expression " + expression + " has one child node",
			         1, node.getChildren().size());		
		return node.getChildren().get(0);		
	}
	
	
	@Test
	/**
	 * Test ParseTreeType.REPEAT
	 */
	public void testRepeat() throws ParseException {
		expectParseException("Nothing to repeat", "{4}");
		expectParseException("Empty repeat", "ab{}");
		expectParseException("Zero repeat", "00{0}");
		expectParseException("Unclosed repeat", "fe{99 ");
		expectParseException("Unopened repeat", "fe 99}");
		expectParseException("Whitespace inside repeat", "fe{ 99}");
		expectParseException("Whitespace inside repeat", "fe{99 }");
		
		testByte(testRepeats("01{3}", 3), (byte) 0x01);
		testByte(testRepeats("  01  \n  {3}", 3), (byte) 0x01);
		
		testAllBitmask(testRepeats("&b3{999}", 999), (byte) 0xb3);
		testAllBitmask(testRepeats("&b3\t{999}", 999), (byte) 0xb3);
		
		testAnyBitmask(testRepeats("~48{1}", 1), (byte) 0x48);
		testAnyBitmask(testRepeats("~48  {1}", 1), (byte) 0x48);
		
		testString(testRepeats("'Titania'{5}", 5),   "Titania");
		testString(testRepeats("'Titania'   {5}", 5),   "Titania");
		
		testCaseInsensitiveString(testRepeats("`html`{45}", 45), "html");
		testCaseInsensitiveString(testRepeats("`html`\r{45}", 45), "html");
		
		testByteSequence(testRepeats("(01 02 03){32}", 32), new byte[] {(byte)01, (byte)02, (byte)03});
		testByteSequence(testRepeats(" ( 01 02 03 )   {32} ", 32), new byte[] {(byte)01, (byte)02, (byte)03});
		
		testAny(testRepeats(".{3}", 3));
		testAny(testRepeats(".   {3}", 3));
		testAny(testRepeats("( . )   {67}", 67));
	}
	
	private ParseTree testRepeats(String expression, int repeats) throws ParseException {
		ParseTree node = parser.parse(expression);
		assertEquals("Expression " + expression + " has type ParseTreeType.REPEAT", 
				  	 ParseTreeType.REPEAT, node.getParseTreeType());
		assertEquals("Expression " + expression + " has two child nodes",
			     2, node.getChildren().size());		
		assertEquals("Expression " + expression + " has repeat value " + repeats,
					 repeats, node.getChildren().get(0).getIntValue());
		return node.getChildren().get(1);
	}
	
	
	@Test
	/**
	 * Test ParseTreeType.REPEAT_MIN_TO_MANY
	 */
	public void testRepeatMinToMany() throws ParseException {
		expectParseException("Nothing to repeat", "{99,*}");
		expectParseException("Unclosed repeat", "fe{99,* ");
		expectParseException("Unopened repeat", "fe 99,*}");
		expectParseException("Whitespace inside repeat", "fe{ 99,*}");
		expectParseException("Whitespace inside repeat", "fe{99,* }");
		expectParseException("Whitespace inside repeat", "fe{99 , *}");
		
		testByte(testRepeatMinToMany("01{3,*}", 3), (byte) 0x01);
		testByte(testRepeatMinToMany("  01  \n  {3,*}", 3), (byte) 0x01);
		
		testAllBitmask(testRepeatMinToMany("&b3{999,*}", 999), (byte) 0xb3);
		testAllBitmask(testRepeatMinToMany("&b3\t{999,*}", 999), (byte) 0xb3);
		
		testAnyBitmask(testRepeatMinToMany("~48{1,*}", 1), (byte) 0x48);
		testAnyBitmask(testRepeatMinToMany("~48  {1,*}", 1), (byte) 0x48);
		
		testString(testRepeatMinToMany("'Titania'{5,*}", 5),   "Titania");
		testString(testRepeatMinToMany("'Titania'   {5,*}", 5),   "Titania");
		
		testCaseInsensitiveString(testRepeatMinToMany("`html`{45,*}", 45), "html");
		testCaseInsensitiveString(testRepeatMinToMany("`html`\r{45,*}", 45), "html");
		
		testByteSequence(testRepeatMinToMany("(01 02 03){32,*}", 32), new byte[] {(byte)01, (byte)02, (byte)03});
		testByteSequence(testRepeatMinToMany(" ( 01 02 03 )   {32,*} ", 32), new byte[] {(byte)01, (byte)02, (byte)03});
		
		testAny(testRepeatMinToMany(".{3,*}", 3));
		testAny(testRepeatMinToMany(".   {3,*}", 3));
		testAny(testRepeatMinToMany("( . )   {67,*}", 67));
	}
	
	private ParseTree testRepeatMinToMany(String expression, int repeats) throws ParseException {
		ParseTree node = parser.parse(expression);
		assertEquals("Expression " + expression + " has type ParseTreeType.REPEAT_MIN_TO_MANY", 
				  	 ParseTreeType.REPEAT_MIN_TO_MANY, node.getParseTreeType());
		assertEquals("Expression " + expression + " has two child nodes",
			     2, node.getChildren().size());		
		assertEquals("Expression " + expression + " has repeat value " + repeats,
					 repeats, node.getChildren().get(0).getIntValue());
		return node.getChildren().get(1);
	}

	
	@Test
	/**
	 * Test ParseTreeType.REPEAT_MIN_TO_MAX
	 */
	public void testRepeatMinToMax() throws ParseException {
		expectParseException("Nothing to repeat", "{99,100}");
		expectParseException("Unclosed repeat", "fe{99,100 ");
		expectParseException("Unopened repeat", "fe 99,100}");
		expectParseException("Whitespace inside repeat", "fe{ 99,98}");
		expectParseException("Whitespace inside repeat", "fe{99,100 }");
		expectParseException("Whitespace inside repeat", "fe{99 , 45}");
		
		testByte(testRepeatMinToMax("01{3,6}", 3, 6), (byte) 0x01);
		testByte(testRepeatMinToMax("  01  \n  {3,5}", 3, 5), (byte) 0x01);
		
		testAllBitmask(testRepeatMinToMax("&b3{999,1000}", 999, 1000), (byte) 0xb3);
		testAllBitmask(testRepeatMinToMax("&b3\t{999,2000}", 999, 2000), (byte) 0xb3);
		
		testAnyBitmask(testRepeatMinToMax("~48{1,5}", 1, 5), (byte) 0x48);
		testAnyBitmask(testRepeatMinToMax("~48  {1,98}", 1, 98), (byte) 0x48);
		
		testString(testRepeatMinToMax("'Titania'{5,5}", 5, 5),   "Titania");
		testString(testRepeatMinToMax("'Titania'   {5,0}", 5, 0),   "Titania");
		
		testCaseInsensitiveString(testRepeatMinToMax("`html`{45,3}", 45, 3), "html");
		testCaseInsensitiveString(testRepeatMinToMax("`html`\r{45,54}", 45, 54), "html");
		
		testByteSequence(testRepeatMinToMax("(01 02 03){32,987}", 32, 987), new byte[] {(byte)01, (byte)02, (byte)03});
		testByteSequence(testRepeatMinToMax(" ( 01 02 03 )   {32,23} ", 32, 23), new byte[] {(byte)01, (byte)02, (byte)03});
		
		testAny(testRepeatMinToMax(".{3,6}", 3, 6));
		testAny(testRepeatMinToMax(".   {3,10}", 3, 10));
		testAny(testRepeatMinToMax("( . )   {67,256}", 67, 256));
	}
	
	private ParseTree testRepeatMinToMax(String expression, int minRepeat, int maxRepeat) throws ParseException {
		ParseTree node = parser.parse(expression);
		assertEquals("Expression " + expression + " has type ParseTreeType.REPEAT_MIN_TO_MAX", 
				  	 ParseTreeType.REPEAT_MIN_TO_MAX, node.getParseTreeType());
		assertEquals("Expression " + expression + " has three child nodes",
			     3, node.getChildren().size());		
		assertEquals("Expression " + expression + " has min repeat value " + minRepeat,
					 minRepeat, node.getChildren().get(0).getIntValue());
		assertEquals("Expression " + expression + " has max repeat value " + minRepeat,
					 maxRepeat, node.getChildren().get(1).getIntValue());
		return node.getChildren().get(2);
	}
	

	@Test
	/**
	 * Test ParseTreeType.ALTERNATIVES
	 */
	public void testByteSequenceAlternatives() throws ParseException {
		expectParseException("No left alternative", "   |02");
		expectParseException("No right alternative", "01|   ");
		
		byte[] values1 = ByteUtilities.toArray((byte) 0x00);
		byte[] values2 = ByteUtilities.toArray((byte) 0x01);
		testByteSequenceAlternatives("00|01", values1, values2);
		testByteSequenceAlternatives("01|00", values2, values1);
		testByteSequenceAlternatives("(01|00)", values2, values1);
		
		byte[] values3 = ByteUtilities.toArray((byte) 0x7f, (byte) 0x7f, (byte) 0x80, (byte) 0xff);
		testByteSequenceAlternatives("00|01|7f 7f 80 ff", values1, values2, values3);
		testByteSequenceAlternatives("7f 7f 80 ff|01|00", values3, values2, values1);
		testByteSequenceAlternatives("(7f 7f 80 ff|01|00)", values3, values2, values1);
		
		byte[] values4 = ByteUtilities.toArray((byte) 0xde, (byte) 0xad, (byte) 0xff);
		testByteSequenceAlternatives("00|deadff|7f 7f 80 ff", values1, values4, values3);
		testByteSequenceAlternatives("deadff|00|7f 7f 80 ff|01|00", values4, values1, values3, values2, values1);
		testByteSequenceAlternatives("(00|deadff|7f 7f 80 ff)", values1, values4, values3);
		testByteSequenceAlternatives("(deadff|00|7f 7f 80 ff|01|00)", values4, values1, values3, values2, values1);
		
		byte[] values5 = ByteUtilities.toArray((byte) 0x32);
		byte[] values6 = ByteUtilities.toArray((byte) 0x6c, (byte) 0xff, (byte) 0xee, (byte) 0xdd);
		testByteSequenceAlternatives("deadff|&32|  ~6cffeedd|01", values4, values5, values6, values2);
		testByteSequenceAlternatives("(deadff|&32|  ~6cffeedd|01)", values4, values5, values6, values2);
	}
	
	private void testByteSequenceAlternatives(String expression, byte[]... values) throws ParseException {
		ParseTree node = parser.parse(expression);
		assertEquals("Expression " + expression + " has type ParseTreeType.ALTERNATIVES",
				     ParseTreeType.ALTERNATIVES, node.getParseTreeType());
		assertEquals("Expression " + expression + " has " + values.length + " children nodes",
					 values.length, node.getChildren().size());
		testByteSequenceAlternativesValues(node.getChildren(), values);
	}
	
	private void testByteSequenceAlternativesValues(List<ParseTree> alternatives, byte[][] values) throws ParseException {
		int position = 0;
		for (ParseTree alternative : alternatives) {
			byte[] value = values[position++];
			if (alternative.getParseTreeType() == ParseTreeType.SEQUENCE) {
				testByteSequenceValues(alternative.getChildren(), value);
			} else if (alternative.getChildren().size() == 0) {
				testByteValue(alternative, value[0]);
			} else {
				throw new ParseException("Not a sequence but has children: [" + alternative + "]");
			}
		}
	}
	
	
	@Test
	/**
	 * Test Groups ()
	 */
	public void testGroups() throws ParseException {
		expectParseException("Empty group", 							"()");
		expectParseException("Empty group with whitespace",				"(    )");
		expectParseException("Empty group comments and whitespace",		" (  #open\n \t  ) ");
		
		testByte("  (01)",   (byte) 1);
		testByte(" ((01))",  (byte) 1);
		testByte("(((01)))", (byte) 1);
		
		byte[] values1 = ByteUtilities.toArray((byte) 1, (byte) 2, (byte) 3);
		testByteSequence(" (01) (02) (03)",  values1);
		testByteSequence("  01  (02)  03",   values1);
		testByteSequence(" (01   02   03)",  values1);
		testByteSequence("((01) (02)  03)",  values1);
		testByteSequence("((01) (02) (03))", values1);
		testByteSequence("((01)  02  (03))", values1);
		
		//TODO: need tests for embedded sequences / alternatives.
		//      embedded sequences stay as sequences: could be quantified in the next parse step. 
		//testByteSequence("(01 (02 03))", 3, values1);
		//testByteSequence("((01 02) 03)", 3, values1);
	}
	
	
	@Test
	/**
	 * Test complicated expressions with lots of different moving parts which should parse correctly,
	 * but for which we won't validate that the AST itself is correct (too complex to bother testing all nodes).
	 */
	public void testComplexExpressions() throws ParseException {
		parser.parse("((01)* 02 03 'a string')? ^[20-40 ^[45-6a 5f]]");
		//TODO: add more complex expressions which test a good mixture of all available options.
		//      try to break the parser by doing the unexpected.
	}
	
	/*
	 * Utility methods
	 */
	

	private String hexString(int value) {
		final String hexString = Integer.toString(value & 0xFF, 16);
		return hexString.length() == 1? '0' + hexString : hexString;
	}

	private String stripQuotes(String fromString) {
		return fromString.substring(1, fromString.length() - 1);
	}
	
	
	private void expectParseException(String description, String expression) {
		try {
			parser.parse(expression);
			fail(description + " with expression " + expression + " was expected to generate a ParseException");
		} catch (ParseException expected) {}
	}
	

	
}
