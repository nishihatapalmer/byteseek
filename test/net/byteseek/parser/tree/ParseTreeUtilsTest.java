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

import static org.junit.Assert.*;

import net.byteseek.parser.ParseException;
import net.byteseek.parser.tree.node.BaseNode;
import net.byteseek.parser.tree.node.ChildrenNode;
import net.byteseek.parser.tree.node.IntNode;
import net.byteseek.parser.tree.node.StringNode;

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
	public final void testGetFirstRangeValue() throws ParseException {
		ChildrenNode parent = new ChildrenNode(ParseTreeType.RANGE);
		parent.addChild(new IntNode(1));
		parent.addChild(new IntNode(1));
		assertEquals("Value is 1", 1, ParseTreeUtils.getFirstRangeValue(parent));
		
		parent = new ChildrenNode(ParseTreeType.RANGE);
		parent.addChild(new IntNode(242));
		parent.addChild(new IntNode(10));
		assertEquals("Value is 257", 242, ParseTreeUtils.getFirstRangeValue(parent));
		
		for (ParseTreeType type : ParseTreeType.values()) {
			if (type != ParseTreeType.RANGE) {
				try {
					ParseTreeUtils.getFirstRangeValue(new ChildrenNode(type));
					fail("Expected a ParseException if the range node type is not RANGE.  Type is " + type);
				} catch (ParseException expected) {
				}
			}

			if (type != ParseTreeType.INTEGER) {
				parent = new ChildrenNode(ParseTreeType.RANGE);
				parent.addChild(new BaseNode(type));
				parent.addChild(new IntNode(96));
				try {
					ParseTreeUtils.getFirstRangeValue(parent);
					fail("Expected a ParseException if the child type has no integer value.  Type is " + type);
				} catch (ParseException excepted) {
				}
			}
		}
		
		try {
			parent = new ChildrenNode(ParseTreeType.RANGE);
			parent.addChild(new IntNode(-1));
			parent.addChild(new IntNode(30));
			ParseTreeUtils.getFirstRangeValue(parent);
			fail("Expected a ParseException if a value is less than 0");
		} catch (ParseException expected) {
		}

		try {
			parent = new ChildrenNode(ParseTreeType.RANGE);
			parent.addChild(new IntNode(256));
			parent.addChild(new IntNode(30));
			ParseTreeUtils.getFirstRangeValue(parent);
			fail("Expected a ParseException if a value is greater than 255");
		} catch (ParseException expected) {
		}
		
		try {
			parent = new ChildrenNode(ParseTreeType.RANGE);
			parent.addChild(new IntNode(256));
			ParseTreeUtils.getFirstRangeValue(parent);
			fail("Expected a ParseException with only one child");
		} catch (ParseException expected) {
		}
		
		try {
			parent = new ChildrenNode(ParseTreeType.RANGE);
			parent.addChild(new IntNode(0));
			parent.addChild(new IntNode(64));
			parent.addChild(new IntNode(92));
			ParseTreeUtils.getFirstRangeValue(parent);
			fail("Expected a ParseException with three children");
		} catch (ParseException expected) {
		}

	}
	

	@Test
	public final void testGetSecondRangeValue() throws ParseException {
		ChildrenNode parent = new ChildrenNode(ParseTreeType.RANGE);
		parent.addChild(new IntNode(1));
		parent.addChild(new IntNode(1));
		assertEquals("Value is 1", 1, ParseTreeUtils.getSecondRangeValue(parent));
		
		parent = new ChildrenNode(ParseTreeType.RANGE);
		parent.addChild(new IntNode(242));
		parent.addChild(new IntNode(10));
		assertEquals("Value is 10", 10, ParseTreeUtils.getSecondRangeValue(parent));
		
		for (ParseTreeType type : ParseTreeType.values()) {
			if (type != ParseTreeType.RANGE) {
				try {
					ParseTreeUtils.getSecondRangeValue(new ChildrenNode(type));
					fail("Expected a ParseException if the type was not Range.  Type is " + type);
				} catch (ParseException expected) {
				}
			}

			if (type != ParseTreeType.INTEGER) {
				parent = new ChildrenNode(ParseTreeType.RANGE);
				parent.addChild(new IntNode(96));
				parent.addChild(new BaseNode(type));
				try {
					ParseTreeUtils.getSecondRangeValue(parent);
					fail("Expected a ParseException if the second child type has no integer value.  Type is " + type);
				} catch (ParseException excepted) {
				}
			}
		}
		
		try {
			parent = new ChildrenNode(ParseTreeType.RANGE);
			parent.addChild(new IntNode(30));
			parent.addChild(new IntNode(-1));
			ParseTreeUtils.getSecondRangeValue(parent);
			fail("Expected a ParseException if a value is less than 0");
		} catch (ParseException expected) {
		}

		try {
			parent = new ChildrenNode(ParseTreeType.RANGE);
			parent.addChild(new IntNode(30));
			parent.addChild(new IntNode(256));
			ParseTreeUtils.getSecondRangeValue(parent);
			fail("Expected a ParseException if a value is greater than 255");
		} catch (ParseException expected) {
		}
		
		try {
			parent = new ChildrenNode(ParseTreeType.RANGE);
			parent.addChild(new IntNode(256));
			ParseTreeUtils.getSecondRangeValue(parent);
			fail("Expected a ParseException with only one child");
		} catch (ParseException expected) {
		}
		
		try {
			parent = new ChildrenNode(ParseTreeType.RANGE);
			parent.addChild(new IntNode(0));
			parent.addChild(new IntNode(64));
			parent.addChild(new IntNode(92));
			ParseTreeUtils.getSecondRangeValue(parent);
			fail("Expected a ParseException with three children");
		} catch (ParseException expected) {
		}
	}

	@Test
	public final void testGetFirstRepeatValue() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetSecondRepeatValue() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetNodeToRepeat() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetRangeValues() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetAllBitmaskValues() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetAnyBitmaskValues() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetStringAsSet() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetCaseInsensitiveStringAsSet() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testCalculateSetValues() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetSetValues() {
		fail("Not yet implemented"); // TODO
	}


}
