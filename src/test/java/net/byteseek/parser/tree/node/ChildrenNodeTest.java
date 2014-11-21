/*
 * Copyright Matt Palmer 2012-2013, All rights reserved.
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

package net.byteseek.parser.tree.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.byteseek.parser.ParseException;
import net.byteseek.parser.tree.ParseTree;
import net.byteseek.parser.tree.ParseTreeType;

import org.junit.Test;

/**
 * @author Matt Palmer
 *
 */
public class ChildrenNodeTest {

	@Test
	public final void testDifferentNumbersOfChildren() {
		for (int numChildren = 0; numChildren < 10; numChildren++) {
			runNumChildrenTests(numChildren);
		}
	}
	
	@Test
	public final void testParameterConstructors() {
		ChildrenNode node = new ChildrenNode(ParseTreeType.SET, BaseNode.ANY_NODE, BaseNode.ANY_NODE);
		testNode("Two parameters", node, ParseTreeType.SET, 2, false);
		
		node = new ChildrenNode(ParseTreeType.SET, BaseNode.ANY_NODE, BaseNode.ANY_NODE, BaseNode.ANY_NODE);
		testNode("Three parameters", node, ParseTreeType.SET, 3, false);

		node = new ChildrenNode(ParseTreeType.SET, false, BaseNode.ANY_NODE, BaseNode.ANY_NODE);
		testNode("Two parameters not inverted", node, ParseTreeType.SET, 2, false);
		
		node = new ChildrenNode(ParseTreeType.SET, false, BaseNode.ANY_NODE, BaseNode.ANY_NODE, BaseNode.ANY_NODE);
		testNode("Three parameters not inverted", node, ParseTreeType.SET, 3, false);
		
		node = new ChildrenNode(ParseTreeType.SET, true, BaseNode.ANY_NODE, BaseNode.ANY_NODE);
		testNode("Two parameters inverted", node, ParseTreeType.SET, 2, true);
		
		node = new ChildrenNode(ParseTreeType.SET, true, BaseNode.ANY_NODE, BaseNode.ANY_NODE, BaseNode.ANY_NODE);
		testNode("Three parameters inverted", node, ParseTreeType.SET, 3, true);
	}
	
	private void runNumChildrenTests(int numChildren) {
		List<ParseTree> children = new ArrayList<ParseTree>();
		for (int i  = 0; i < numChildren; i++) {
			children.add(new BaseNode(ParseTreeType.ANY));
		}
		runTests(children, numChildren);
	}
	
	private void runTests(List<ParseTree> children, int numChildren) {
		for (ParseTreeType type : ParseTreeType.values()) {
			testConstruction(type, children, numChildren);
			//testChangingChildren(type, children, numChildren);
		}		
	}
	
	private void testConstruction(ParseTreeType type, List<ParseTree> children, int numChildren) {
		ChildrenNode node = new ChildrenNode(type);
		testNode("Just a type", node, type, 0, false);
		
		node = new ChildrenNode(type, true);
		testNode("Type and inverted", node, type, 0, true);
		
		node = new ChildrenNode(type, (List<ParseTree>) null);
		testNode("Type and null children", node, type, 0, false);
		
		node = new ChildrenNode(type, children);
		testNode("Default false inversion: ", node, type, numChildren, false);

		node = new ChildrenNode(type, children, false);
		testNode("Specified false inversion: ", node, type, numChildren, false);
		
		node = new ChildrenNode(type, children, true);
		testNode("Specified true inversion: ", node, type, numChildren, true);
		
		node = new ChildrenNode(type, (ParseTree) null);
		testNode("Specified a null child", node, type, 0, false);
		
		node = new ChildrenNode(type, (ParseTree) null, false);
		testNode("Specified a null uninverted child", node, type, 0, false);

		node = new ChildrenNode(type, (ParseTree) null, true);
		testNode("Specified a null inverted child", node, type, 0, true);
		
		node = new ChildrenNode(type, BaseNode.ANY_NODE);
		testNode("Specified an ANY node child", node, type, 1, false);

		node = new ChildrenNode(type, BaseNode.ANY_NODE, false);
		testNode("Specified an uninverted ANY node child", node, type, 1, false);

		node = new ChildrenNode(type, BaseNode.ANY_NODE, true);
		testNode("Specified an inverted ANY node child", node, type, 1, true);
		
	}
	
	private void testNode(String description, ChildrenNode node, ParseTreeType type, int numChildren, boolean isInverted) {
		assertEquals(description + "ChildrenNode has correct type: " + type, node.getParseTreeType(), type);
		assertEquals(description + "ChildrenNode value is correct inversion: " + isInverted, isInverted, node.isValueInverted());
		
		try { 
			node.getByteValue();
			fail(description + "Expected a ParseException if asked for the byte value");
		} catch (ParseException allIsFine) {};
		
		try { 
			node.getIntValue();
			fail(description + "Expected a ParseException if asked for the int value");
		} catch (ParseException allIsFine) {};
		
		try { 
			node.getTextValue();
			fail(description + "Expected a ParseException if asked for the text value");
		} catch (ParseException allIsFine) {};
		
		assertEquals(description + "Child list has correct number of children " + numChildren, numChildren, node.getNumChildren());
		for (int i = 0; i < numChildren; i++) {
			ParseTree child = node.getChild(i);
			assertEquals("Child node is ANY type", ParseTreeType.ANY, child.getParseTreeType());
		}
		
		Iterator<ParseTree> iterator = node.iterator();
		while (iterator.hasNext()) {
			@SuppressWarnings("unused")
			final ParseTree child = iterator.next();
			try {
				iterator.remove();
				fail("Expected an unsupportedoperationexception");
			} catch (UnsupportedOperationException expected) {};
		}
		
		int i = 0;
		for (ParseTree child : node) {
			assertEquals("Child node is ANY type", ParseTreeType.ANY, child.getParseTreeType());
			i++;
		}
		assertEquals("Count of iterated parse trees is the number of children", i, numChildren);
		
		try {
			node.getChild(-1);
			fail("Expected an indexoutofbounds exception");
		} catch (IndexOutOfBoundsException expected) {};
		
		try {
			node.getChild(numChildren);
			fail("Expected an indexoutofbounds exception");
		} catch (IndexOutOfBoundsException expected) {};
		
		try {
			node.getChild(numChildren + 1);
			fail("Expected an indexoutofbounds exception");
		} catch (IndexOutOfBoundsException expected) {};
			
		assertTrue("toString contains class name", node.toString().contains(node.getClass().getSimpleName()));
	}

}
 