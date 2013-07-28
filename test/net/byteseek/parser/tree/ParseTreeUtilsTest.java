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

package net.byteseek.parser.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import net.byteseek.parser.ParseException;
import net.byteseek.parser.tree.node.BaseNode;
import net.byteseek.parser.tree.node.ByteNode;
import net.byteseek.parser.tree.node.ChildrenNode;
import net.byteseek.parser.tree.node.IntNode;
import net.byteseek.parser.tree.node.StringNode;
import net.byteseek.util.bytes.ByteUtilities;

import org.junit.Test;

public class ParseTreeUtilsTest {

	@Test
	public final void testGetFirstChild() throws ParseException {
		ChildrenNode parent = new ChildrenNode(ParseTreeType.SET);
		parent.addChild(new IntNode(1));
		parent.addChild(new StringNode("A string"));
		
		ParseTree firstChild = ParseTreeUtils.getFirstChild(parent);
		assertEquals("First child is integer", ParseTreeType.INTEGER, firstChild.getParseTreeType());
		assertEquals("First child value is 1", 1, firstChild.getIntValue());

		parent = new ChildrenNode(ParseTreeType.SET);
		parent.addChild(new StringNode("A string"));
		parent.addChild(new IntNode(1));
		
		firstChild = ParseTreeUtils.getFirstChild(parent);
		assertEquals("First child is String", ParseTreeType.STRING, firstChild.getParseTreeType());
		assertEquals("First child value is 'A string'", "A string", firstChild.getTextValue());
		
		try {
			firstChild = ParseTreeUtils.getFirstChild(null);
			fail("Expected a null pointeger exception");
		} catch (NullPointerException expected) {
		}
		
		try {
			parent = new ChildrenNode(ParseTreeType.SET);
			firstChild = ParseTreeUtils.getFirstChild(parent);
			fail("Expected a parse exception if there are no children");
		} catch (ParseException expected) {
		}
		
	}
	
	
	@Test
	public final void testGetLastChild() throws ParseException {
		ChildrenNode parent = new ChildrenNode(ParseTreeType.SET);
		parent.addChild(new StringNode("A string"));
		parent.addChild(new IntNode(1));
		
		ParseTree lastChild = ParseTreeUtils.getLastChild(parent);
		assertEquals("Last child is integer", ParseTreeType.INTEGER, lastChild.getParseTreeType());
		assertEquals("Last child value is 1", 1, lastChild.getIntValue());

		parent = new ChildrenNode(ParseTreeType.SET);
		parent.addChild(new IntNode(1));
		parent.addChild(new StringNode("A string"));
		
		lastChild = ParseTreeUtils.getLastChild(parent);
		assertEquals("Last child is String", ParseTreeType.STRING, lastChild.getParseTreeType());
		assertEquals("Last child value is 'A string'", "A string", lastChild.getTextValue());
		
		try {
			lastChild = ParseTreeUtils.getLastChild(null);
			fail("Expected a null pointeger exception");
		} catch (NullPointerException expected) {
		}
		
		try {
			parent = new ChildrenNode(ParseTreeType.SET);
			lastChild = ParseTreeUtils.getLastChild(parent);
			fail("Expected a parse exception if there are no children");
		} catch (ParseException expected) {
		}
	}

	@Test
	public final void testGetFirstRangeValue() throws ParseException {
		ChildrenNode parent = new ChildrenNode(ParseTreeType.RANGE);
		parent.addChild(ByteNode.valueOf((byte) 1));
		parent.addChild(ByteNode.valueOf((byte) 1));
		assertEquals("Value is 1", 1, ParseTreeUtils.getFirstRangeValue(parent));
		
		parent = new ChildrenNode(ParseTreeType.RANGE);
		parent.addChild(ByteNode.valueOf((byte) 242));
		parent.addChild(ByteNode.valueOf((byte) 10));
		assertEquals("Value is 242", 242, ParseTreeUtils.getFirstRangeValue(parent));
		
		for (ParseTreeType type : ParseTreeType.values()) {
			if (type != ParseTreeType.RANGE) {
				try {
					ParseTreeUtils.getFirstRangeValue(new ChildrenNode(type));
					fail("Expected a ParseException if the range node type is not RANGE.  Type is " + type);
				} catch (ParseException expected) {
				}
			}

			if (type != ParseTreeType.BYTE) {
				parent = new ChildrenNode(ParseTreeType.RANGE);
				parent.addChild(new BaseNode(type));
				parent.addChild(ByteNode.valueOf((byte) 96));
				try {
					ParseTreeUtils.getFirstRangeValue(parent);
					fail("Expected a ParseException if the child type is not a BYTE.  Type is " + type);
				} catch (ParseException excepted) {
				}
			}
		}
		
		
		try {
			parent = new ChildrenNode(ParseTreeType.RANGE);
			parent.addChild(ByteNode.valueOf((byte) 54));
			ParseTreeUtils.getFirstRangeValue(parent);
			fail("Expected a ParseException with only one child");
		} catch (ParseException expected) {
		}
		
		try {
			parent = new ChildrenNode(ParseTreeType.RANGE);
			parent.addChild(ByteNode.valueOf((byte) 0));
			parent.addChild(ByteNode.valueOf((byte) 64));
			parent.addChild(ByteNode.valueOf((byte) 92));
			ParseTreeUtils.getFirstRangeValue(parent);
			fail("Expected a ParseException with three children");
		} catch (ParseException expected) {
		}

	}
	

	@Test
	public final void testGetSecondRangeValue() throws ParseException {
		ChildrenNode parent = new ChildrenNode(ParseTreeType.RANGE);
		parent.addChild(ByteNode.valueOf((byte) 1));
		parent.addChild(ByteNode.valueOf((byte)1));
		assertEquals("Value is 1", 1, ParseTreeUtils.getSecondRangeValue(parent));
		
		parent = new ChildrenNode(ParseTreeType.RANGE);
		parent.addChild(ByteNode.valueOf((byte) 242));
		parent.addChild(ByteNode.valueOf((byte) 10));
		assertEquals("Value is 10", 10, ParseTreeUtils.getSecondRangeValue(parent));
		
		for (ParseTreeType type : ParseTreeType.values()) {
			if (type != ParseTreeType.RANGE) {
				try {
					ParseTreeUtils.getSecondRangeValue(new ChildrenNode(type));
					fail("Expected a ParseException if the type was not Range.  Type is " + type);
				} catch (ParseException expected) {
				}
			}

			if (type != ParseTreeType.BYTE) {
				parent = new ChildrenNode(ParseTreeType.RANGE);
				parent.addChild(ByteNode.valueOf((byte)96));
				parent.addChild(new BaseNode(type));
				try {
					ParseTreeUtils.getSecondRangeValue(parent);
					fail("Expected a ParseException if the second child type has is not a BYTE.  Type is " + type);
				} catch (ParseException excepted) {
				}
			}
		}
		
	
		try {
			parent = new ChildrenNode(ParseTreeType.RANGE);
			parent.addChild(ByteNode.valueOf((byte) 255));
			ParseTreeUtils.getSecondRangeValue(parent);
			fail("Expected a ParseException with only one child");
		} catch (ParseException expected) {
		}
		
		try {
			parent = new ChildrenNode(ParseTreeType.RANGE);
			parent.addChild(ByteNode.valueOf((byte) 0));
			parent.addChild(ByteNode.valueOf((byte) 64));
			parent.addChild(ByteNode.valueOf((byte) 92));
			ParseTreeUtils.getSecondRangeValue(parent);
			fail("Expected a ParseException with three children");
		} catch (ParseException expected) {
		}
	}

	@Test
	public final void testGetFirstRepeatValue() throws ParseException {
		ChildrenNode parent = new ChildrenNode(ParseTreeType.REPEAT);
		parent.addChild(new IntNode(1));
		assertEquals("Value is 1", 1, ParseTreeUtils.getFirstRepeatValue(parent));
		
		parent = new ChildrenNode(ParseTreeType.REPEAT_MIN_TO_MAX);
		parent.addChild(new IntNode(2420));
		parent.addChild(new IntNode(10));
		assertEquals("Value is 2420", 2420, ParseTreeUtils.getFirstRepeatValue(parent));
		
		for (ParseTreeType type : ParseTreeType.values()) {
			if (type != ParseTreeType.REPEAT &&
				type != ParseTreeType.REPEAT_MIN_TO_MANY &&
				type != ParseTreeType.REPEAT_MIN_TO_MAX) {
				try {
					parent = new ChildrenNode(type);
					parent.addChild(new IntNode(1));
					ParseTreeUtils.getFirstRepeatValue(new ChildrenNode(type));
					fail("Expected a ParseException if the repeat node type is not a repeating node.  Type is " + type);
				} catch (ParseException expected) {
				}
			}

			if (type != ParseTreeType.INTEGER) {
				parent = new ChildrenNode(ParseTreeType.REPEAT_MIN_TO_MANY);
				parent.addChild(new BaseNode(type));
				parent.addChild(new IntNode(96));
				try {
					ParseTreeUtils.getFirstRepeatValue(parent);
					fail("Expected a ParseException if the child type has no integer value.  Type is " + type);
				} catch (ParseException excepted) {
				}
			}
		}
		
		try {
			parent = new ChildrenNode(ParseTreeType.REPEAT);
			parent.addChild(new IntNode(-1));
			parent.addChild(new IntNode(30));
			ParseTreeUtils.getFirstRepeatValue(parent);
			fail("Expected a ParseException if a value is less than 0");
		} catch (ParseException expected) {
		}
	}

	@Test
	public final void testGetSecondRepeatValue() throws ParseException {
		ChildrenNode parent = new ChildrenNode(ParseTreeType.REPEAT_MIN_TO_MAX);
		parent.addChild(new IntNode(1));
		parent.addChild(new IntNode(10));
		assertEquals("Value is 10", 10, ParseTreeUtils.getSecondRepeatValue(parent));
		
		parent = new ChildrenNode(ParseTreeType.REPEAT_MIN_TO_MAX);
		parent.addChild(new IntNode(2420));
		parent.addChild(new IntNode(10));
		assertEquals("Value is 10", 10, ParseTreeUtils.getSecondRepeatValue(parent));
		
		for (ParseTreeType type : ParseTreeType.values()) {
			if (type != ParseTreeType.REPEAT &&
				type != ParseTreeType.REPEAT_MIN_TO_MANY &&
				type != ParseTreeType.REPEAT_MIN_TO_MAX) {
				try {
					parent.addChild(new IntNode(1));
					parent = new ChildrenNode(type);
					ParseTreeUtils.getSecondRepeatValue(new ChildrenNode(type));
					fail("Expected a ParseException if the repeat node type is not a repeating node.  Type is " + type);
				} catch (ParseException expected) {
				}
			}

			if (type != ParseTreeType.INTEGER) {
				parent = new ChildrenNode(ParseTreeType.REPEAT_MIN_TO_MAX);
				parent.addChild(new IntNode(96));
				parent.addChild(new BaseNode(type));
				try {
					ParseTreeUtils.getSecondRepeatValue(parent);
					fail("Expected a ParseException if the child type has no integer value.  Type is " + type);
				} catch (ParseException excepted) {
				}
			}
		}
		
		try {
			parent = new ChildrenNode(ParseTreeType.REPEAT_MIN_TO_MAX);
			parent.addChild(new IntNode(30));
			parent.addChild(new IntNode(-1));
			ParseTreeUtils.getSecondRepeatValue(parent);
			fail("Expected a ParseException if a value is less than 0");
		} catch (ParseException expected) {
		}
	}

	@Test
	public final void testAddByteValues() throws ParseException {
		for (int i = 0; i < 256; i++) {
			testAddByteValues((byte) i);
		}
	}
	
	private void testAddByteValues(byte value) throws ParseException {
		ParseTree byteNode = new ByteNode(value);
		Set<Byte> set = new HashSet<Byte>();
		ParseTreeUtils.addByteValues(byteNode, set);
		assertEquals("Single byte value only has one value", 1, set.size());
		assertTrue("Single byte value has correct value for byte " + value, set.contains(value));

		byteNode = new ByteNode(value, false);
		set = new HashSet<Byte>();
		ParseTreeUtils.addByteValues(byteNode, set);
		assertEquals("Single byte value not inverted only has one value", 1, set.size());
		assertTrue("Single byte value not inverted has correct value for byte " + value, set.contains(value));
		
		byteNode = new ByteNode(value, true);
		set = new HashSet<Byte>();
		ParseTreeUtils.addByteValues(byteNode, set);
		assertEquals("Single byte value inverted has 255 values", 255, set.size());
		assertFalse("Single byte value inverted does not have original value " + value, set.contains(value));
	}
	
	@Test
	public final void testGetRangeValues() throws ParseException {
		testRangeValues(0,0);
		testRangeValues(0, 255);
		testRangeValues(255, 255);
		testRangeValues(1,2);
		testRangeValues(254,255);
		testRangeValues(32,127);
		for (int i = 0; i < 256; i++) {
			testRangeValues(0,i);
			testRangeValues(i, 255-i);
		}
	}
	
	private void testRangeValues(int value1, int value2) throws ParseException {
		ParseTree rangeNode = getRangeNode(value1, value2, false);
		Set<Byte> bytes = new HashSet<Byte>(256);
		ParseTreeUtils.addRangeBytes(rangeNode, bytes);
		validateRangeValues(bytes, value1, value2, false);
		
		rangeNode = getRangeNode(value2, value1, false);
		bytes = new HashSet<Byte>(256);
		ParseTreeUtils.addRangeBytes(rangeNode, bytes);
		validateRangeValues(bytes, value1, value2, false);
		
		rangeNode = getRangeNode(value2, value1, true);
		bytes = new HashSet<Byte>(256);
		ParseTreeUtils.addRangeBytes(rangeNode, bytes);
		validateRangeValues(bytes, value1, value2, true);
		
		rangeNode = getRangeNode(value1, value2, true);
		bytes = new HashSet<Byte>(256);
		ParseTreeUtils.addRangeBytes(rangeNode, bytes);
		validateRangeValues(bytes, value1, value2, true);
	}

	private ParseTree getRangeNode(int start, int end, boolean inverted) {
		ParseTree rangeNode = new ChildrenNode(ParseTreeType.RANGE, inverted);
		rangeNode.addChild(ByteNode.valueOf((byte) start));
		rangeNode.addChild(ByteNode.valueOf((byte) end));
		return rangeNode;
	}
	
	private void validateRangeValues(Set<Byte> values, int range1, int range2, boolean inverted) {
		if (inverted) {
			if (range1 > range2) {
				for (int value = 0; value < range2; value++) {
					Byte b = Byte.valueOf((byte) value);
					assertTrue("Byte is in range " + range1 + ":" + range2, values.contains(b));
					values.remove(b);
				}
				for (int value = range1 + 1; value < 256; value++) {
					Byte b = Byte.valueOf((byte) value);
					assertTrue("Byte is in range " + range1 + ":" + range2, values.contains(b));
					values.remove(b);
				}
				assertTrue("No more bytes in values set", values.isEmpty());				
			} else {
				for (int value = 0; value < range1; value++) {
					Byte b = Byte.valueOf((byte) value);
					assertTrue("Byte is in range " + range1 + ":" + range2, values.contains(b));
					values.remove(b);
				}
				for (int value = range2 + 1; value < 256; value++) {
					Byte b = Byte.valueOf((byte) value);
					assertTrue("Byte is in range " + range1 + ":" + range2, values.contains(b));
					values.remove(b);
				}
				assertTrue("No more bytes in values set", values.isEmpty());
			}
		} else {
			if (range1 > range2) {
				for (int value = range2; value <= range1; value++) {
					Byte b = Byte.valueOf((byte) value);
					assertTrue("Byte is in range " + range1 + ":" + range2, values.contains(b));
					values.remove(b);
				}
				assertTrue("No more bytes in values set", values.isEmpty());
			} else {
				for (int value = range1; value <= range2; value++) {
					Byte b = Byte.valueOf((byte) value);
					assertTrue("Byte is in range " + range1 + ":" + range2, values.contains(b));
					values.remove(b);
				}
				assertTrue("No more bytes in values set", values.isEmpty());
			}
		}
	}

	@Test
	public final void testGetAllBitmaskValues() throws ParseException {
		for (int i = 0; i < 256; i++) {
			testAllBitmaskValues((byte) i);
		}
	}
	
	private void testAllBitmaskValues(byte bitmask) throws ParseException {
		ParseTree allbitmask = new ByteNode(ParseTreeType.ALL_BITMASK, bitmask);
		Set<Byte> allbitmaskBytes1 = new HashSet<Byte>();
		Set<Byte> allbitmaskBytes2 = new HashSet<Byte>(); 
		ParseTreeUtils.addBytesMatchingAllBitmask(allbitmask, allbitmaskBytes1);
		ByteUtilities.addBytesMatchingAllBitMask(bitmask, allbitmaskBytes2);
		assertEquals("All bitmask sets for bitmask byte " + bitmask, allbitmaskBytes1, allbitmaskBytes2);

		allbitmask = new ByteNode(ParseTreeType.ALL_BITMASK, bitmask, false);
		allbitmaskBytes1.clear();
		allbitmaskBytes2.clear();
		ParseTreeUtils.addBytesMatchingAllBitmask(allbitmask, allbitmaskBytes1);
		ByteUtilities.addBytesMatchingAllBitMask(bitmask, allbitmaskBytes2);
		assertEquals("All bitmask sets for explicitly not inverted bitmask byte " + bitmask, allbitmaskBytes1, allbitmaskBytes2);
		
		allbitmask = new ByteNode(ParseTreeType.ALL_BITMASK, bitmask, true);
		allbitmaskBytes1.clear();
		allbitmaskBytes2.clear();
		ParseTreeUtils.addBytesMatchingAllBitmask(allbitmask, allbitmaskBytes1);
		ByteUtilities.addBytesNotMatchingAllBitMask(bitmask, allbitmaskBytes2);
		assertEquals("All bitmask sets for inverted bitmask byte " + bitmask, allbitmaskBytes1, allbitmaskBytes2);
	}

	@Test
	public final void testGetAnyBitmaskValues() throws ParseException {
		for (int i = 0; i < 256; i++) {
			testAnyBitmaskValues((byte) i);
		}
	}
	
	private void testAnyBitmaskValues(byte bitmask) throws ParseException {
		ParseTree allbitmask = new ByteNode(ParseTreeType.ALL_BITMASK, bitmask);
		Set<Byte> allbitmaskBytes1 = new HashSet<Byte>();
		Set<Byte> allbitmaskBytes2 = new HashSet<Byte>(); 
		ParseTreeUtils.addBytesMatchingAnyBitmask(allbitmask, allbitmaskBytes1);
		ByteUtilities.addBytesMatchingAnyBitMask(bitmask, allbitmaskBytes2);
		assertEquals("Any bitmask sets for bitmask byte " + bitmask, allbitmaskBytes1, allbitmaskBytes2);

		allbitmask = new ByteNode(ParseTreeType.ALL_BITMASK, bitmask, false);
		allbitmaskBytes1.clear();
		allbitmaskBytes2.clear();
		ParseTreeUtils.addBytesMatchingAnyBitmask(allbitmask, allbitmaskBytes1);
		ByteUtilities.addBytesMatchingAnyBitMask(bitmask, allbitmaskBytes2);
		assertEquals("Any bitmask sets for explicitly not inverted bitmask byte " + bitmask, allbitmaskBytes1, allbitmaskBytes2);
		
		allbitmask = new ByteNode(ParseTreeType.ALL_BITMASK, bitmask, true);
		allbitmaskBytes1.clear();
		allbitmaskBytes2.clear();
		ParseTreeUtils.addBytesMatchingAnyBitmask(allbitmask, allbitmaskBytes1);
		ByteUtilities.addBytesNotMatchingAnyBitMask(bitmask, allbitmaskBytes2);
		assertEquals("Any bitmask sets for inverted bitmask byte " + bitmask, allbitmaskBytes1, allbitmaskBytes2);
	}

	@Test
	public final void testAddStringAsSet() throws ParseException {
		testAddStringAsSet("123", new byte[] {'1', '2', '3'});
		testAddStringAsSet("a", new byte[] {'a'});
		testAddStringAsSet("abcdefghijklmnopqrstuvwxyz", new byte[] {'a', 'b','c', 'd', 'e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'});
	}
	
	private void testAddStringAsSet(String string, byte[] bytes) throws ParseException {
		ParseTree node = new StringNode(string);
		Set<Byte> byteSet = new HashSet<Byte>();
		ParseTreeUtils.addStringBytes(node, byteSet);
		assertEquals("String " + string + " has correct bytes", ByteUtilities.toSet(bytes), byteSet);
	}
	

	@Test
	public final void testAddCaseStringAsSet() throws ParseException {
		testAddCaseStringAsSet("123", new byte[] {'1', '2', '3'});
		testAddCaseStringAsSet("a", new byte[] {'a', 'A'});
		testAddCaseStringAsSet("abcdefghijklmnopqrstuvwxyz", 
							  new byte[] {'a','b','c','d','e','f','g','h','i','j','k','l','m',
										  'n','o','p','q','r','s','t','u','v','w','x','y','z', 
										  'A','B','C','D','E','F','G','H','I','J','K','L','M',
										  'N','O','P','Q','R','S','T','U','V','W','X','Y','Z'});
	}
	
	private void testAddCaseStringAsSet(String string, byte[] bytes) throws ParseException {
		ParseTree node = new StringNode(string);
		Set<Byte> byteSet = new HashSet<Byte>();
		ParseTreeUtils.addCaseInsensitiveStringBytes(node, byteSet);
		assertEquals("String " + string + " has correct bytes", ByteUtilities.toSet(bytes), byteSet);
	}

	@Test
	public final void testIllegalSetTypes() {
		for (ParseTreeType type : ParseTreeType.values()) {
			if (type != ParseTreeType.BYTE &&
				type != ParseTreeType.SET &&
				type != ParseTreeType.RANGE &&
				type != ParseTreeType.ALL_BITMASK &&
				type != ParseTreeType.ANY_BITMASK) {
				ParseTree node = new ChildrenNode(ParseTreeType.SET);
				node.addChild(new BaseNode(type));
				try {
					ParseTreeUtils.getSetValues(node);
					fail("getSetValues: expected a parse exception for set child node of type " + type);
				} catch (ParseException expected) {}
				try {
					ParseTreeUtils.calculateSetValues(node);
					fail("calculateSetValues: expected a parse exception for set child node of type " + type);
				} catch (ParseException expected) {}
				try {
					ParseTreeUtils.addSetValues(node, new HashSet<Byte>());
					fail("addSetValues: expected a parse exception for set child node of type " + type);
				} catch (ParseException expected) {}
			}
		}
	}
	
	@Test
	public final void testByteSetValues() throws ParseException {
		testByteSet(new byte[] {(byte) 0x00});
		testByteSet(new byte[] {(byte) 0x00, (byte) 0xFF});
		testByteSet(new byte[] {(byte) 0x00, (byte) 0xFF});
		testByteSet(new byte[] {(byte) 0xFF, (byte) 0x00});
		testByteSet(new byte[] {(byte) 0xFF, (byte) 0xde, (byte) 0xad, (byte) 0xbe, (byte) 0xef});
	}

	@Test
	public final void testRangeSetValues() throws ParseException {
		testRangeSet(0, 0);
		testRangeSet(1, 128);
		testRangeSet(1, 20);
		for (int i = 0; i < 256; i++) {
			testRangeSet(i, 255 - i);
		}
	}

	@Test
	public final void testAllBitmaskSet() throws ParseException {
		for (int i = 0; i < 256; i++) {
			testAllBitmaskSet(i);
		}
	}
	
	private void testByteSet(byte[] values) throws ParseException {
		// Test a straight set of bytes
		ParseTree byteSet = new ChildrenNode(ParseTreeType.SET);
		for (byte value : values) {
			byteSet.addChild(new ByteNode(value));
		}
		Set<Byte> expected = ByteUtilities.toSet(values);
		testSetMethods(expected, byteSet);
		
		// Test an inverted set of bytes
		byteSet = new ChildrenNode(ParseTreeType.SET, true);
		for (byte value : values) {
			byteSet.addChild(new ByteNode(value));
		}
		expected = ByteUtilities.invertedSet(ByteUtilities.toSet(values));
		testInvertedSetMethods(expected, byteSet);
		
		// Test a set of inverted bytes
		byteSet = new ChildrenNode(ParseTreeType.SET);
		expected = new HashSet<Byte>(320);
		for (byte value : values) {
			byteSet.addChild(new ByteNode(value, true));
			expected.addAll(ByteUtilities.invertedSet(value));
		}
		testSetMethods(expected, byteSet);

		// Test an inverted set of inverted bytes
		byteSet = new ChildrenNode(ParseTreeType.SET, true);
		expected = new HashSet<Byte>(320);
		for (byte value : values) {
			byteSet.addChild(new ByteNode(value, true));
			expected.addAll(ByteUtilities.invertedSet(value));
		}
		expected = ByteUtilities.invertedSet(expected);
		testInvertedSetMethods(expected, byteSet);
	}

	private void testSetMethods(Set<Byte> expected, ParseTree byteSet) throws ParseException {
		Set<Byte> result = ParseTreeUtils.getSetValues(byteSet);
		assertEquals("Get set of bytes are equal " + expected, expected, result);
		
		result = ParseTreeUtils.calculateSetValues(byteSet);
		assertEquals("Calculate set of bytes are equal " + expected, expected, result);

		result.clear();
		ParseTreeUtils.addSetValues(byteSet, result);
		assertEquals("Add set of bytes are equal " + expected, expected, result);
	}

	private void testInvertedSetMethods(Set<Byte> expected, ParseTree byteSet) throws ParseException {
		Set<Byte> result = ParseTreeUtils.getSetValues(byteSet);
		assertFalse("Inverted get set of bytes are not equal " + expected, expected.equals(result));
		
		result = ParseTreeUtils.calculateSetValues(byteSet);
		assertEquals("Inverted calculate set of bytes are equal " + expected, expected, result);
		
		result.clear();
		ParseTreeUtils.addSetValues(byteSet, result);
		assertEquals("Inverted add set of bytes are equal " + expected, expected, result);
	}
	
	private void testRangeSet(int start, int end) throws ParseException {
		// Test a straight set of one range:
		ParseTree byteSet = getSingleChildSet(getRangeNode(start, end, false), false);
		
		Set<Byte> expected = new HashSet<Byte>(256);
		ByteUtilities.addBytesInRange(start,  end, expected);
		testSetMethods(expected, byteSet);
		
		// Test an inverted set of one range:
		byteSet = getSingleChildSet(getRangeNode(start, end, false), true);
		
		expected = new HashSet<Byte>(256);
		ByteUtilities.addBytesNotInRange(start,  end, expected);
		testInvertedSetMethods(expected, byteSet);

		// Test a set of an inverted range:
		byteSet = getSingleChildSet(getRangeNode(start, end, true), false);
		
		expected = new HashSet<Byte>(256);
		ByteUtilities.addBytesNotInRange(start,  end, expected);
		testSetMethods(expected, byteSet);
		
		// Test an inverted set of an inverted range:
		byteSet = getSingleChildSet(getRangeNode(start, end, true), true);
		
		expected = new HashSet<Byte>(256);
		ByteUtilities.addBytesInRange(start,  end, expected);
		testInvertedSetMethods(expected, byteSet);
	}
	
	private ParseTree getSingleChildSet(final ParseTree child, boolean inverted) {
		ParseTree set = new ChildrenNode(ParseTreeType.SET, inverted);
		set.addChild(child);
		return set;
	}
	
	private void testAllBitmaskSet(final int bitmask) throws ParseException {
		ParseTree set = getSingleChildSet(new ByteNode(ParseTreeType.ALL_BITMASK, (byte) bitmask), false);
		fail("not implemented");
		
	}
	
}
