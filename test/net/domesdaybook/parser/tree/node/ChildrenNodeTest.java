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

package net.domesdaybook.parser.tree.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import net.domesdaybook.parser.ParseException;
import net.domesdaybook.parser.tree.ParseTree;
import net.domesdaybook.parser.tree.ParseTreeType;

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
			testChangingChildren(type, children, numChildren);
		}		
	}
	
	private void testConstruction(ParseTreeType type, List<ParseTree> children, int numChildren) {
		ChildrenNode node = new ChildrenNode(type, children);
		testNode("Default false inversion: ", node, type, numChildren, false);

		node = new ChildrenNode(type, children, false);
		testNode("Specified false inversion: ", node, type, numChildren, false);
		
		node = new ChildrenNode(type, children, true);
		testNode("Specified true inversion: ", node, type, numChildren, true);		
	}
	
	private void testChangingChildren(ParseTreeType type, List<ParseTree> children, int numChildren) {
		List<ParseTree> childrenToTest = new ArrayList<ParseTree>(children);
		
		ChildrenNode defaultNode = new ChildrenNode(type, childrenToTest); 

		assertEquals("Before change, size is correct: ", numChildren, defaultNode.getChildren().size());
		
		ParseTree nodeToAdd = new StringNode("Node to add");
		childrenToTest.add(nodeToAdd);
		
		assertEquals("Add to test list: size is unchanged.", numChildren, defaultNode.getChildren().size());
		
		ParseTree nodeToAdd2 = new StringNode("Second node to add");
		defaultNode.addChild(nodeToAdd2);
		
		assertEquals("Add default node: size is correct: ", numChildren + 1, defaultNode.getChildren().size());
		
		childrenToTest.remove(nodeToAdd);
		
		assertEquals("Remove node from test list: size is correct: ", numChildren + 1, defaultNode.getChildren().size());

		defaultNode.removeChild(nodeToAdd2);
		
		assertEquals("Remove from default node: Default behaviour is is back to start", numChildren, defaultNode.getChildren().size());
		assertEquals("Remove from given node: test list is back to start", numChildren, childrenToTest.size());
	}
	
	private void testNode(String description, ChildrenNode node, ParseTreeType type, int numChildren, boolean isInverted) {
		assertEquals(description + "ChildrenNode has correct type: " + type, node.getParseTreeType(), type);
		try {
			assertEquals(description + "ChildrenNode value is correct inversion: " + isInverted, isInverted, node.isValueInverted());
		} catch (ParseException e) {
			fail(description + "ChildrenNode should not throw a ParseException if asked if the value is inverted.");
		}
		
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
		
		assertNotNull(description + "Child list is not null", node.getChildren());
		assertEquals(description + "Child list has correct number of children " + numChildren, numChildren, node.getChildren().size());
	}

}
