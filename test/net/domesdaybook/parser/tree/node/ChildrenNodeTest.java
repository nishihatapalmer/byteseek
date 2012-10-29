/**
 * 
 */
package net.domesdaybook.parser.tree.node;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import net.domesdaybook.parser.ParseException;
import net.domesdaybook.parser.tree.ParseTree;

import org.junit.Test;
import net.domesdaybook.parser.tree.ParseTreeType;
import net.domesdaybook.parser.tree.node.ChildrenNode.ListStrategy;

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
			testConstructors(type, children, numChildren);
			testChangingChildren(type, children, numChildren);
		}		
	}
	
	private void testConstructors(ParseTreeType type, List<ParseTree> children, int numChildren) {
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
		ChildrenNode copyNode    = new ChildrenNode(type, childrenToTest, ListStrategy.COPY_LIST);
		ChildrenNode givenNode   = new ChildrenNode(type, childrenToTest, ListStrategy.USE_GIVEN_LIST);

		assertEquals("Before change: Default behaviour is to copy the child list.", numChildren, defaultNode.getChildren().size());
		assertEquals("Before change: Specified copy behaviour also copies the list.", numChildren, copyNode.getChildren().size());
		assertEquals("Before change: Specified use given behaviour has same children now.", numChildren, givenNode.getChildren().size());
		
		ParseTree nodeToAdd = new StringNode("Node to add");
		childrenToTest.add(nodeToAdd);
		
		assertEquals("Add to test list: Default behaviour is to copy the child list.", numChildren, defaultNode.getChildren().size());
		assertEquals("Add to test list: Specified copy behaviour also copies the list.", numChildren, copyNode.getChildren().size());
		assertEquals("Add to test list: Specified use given behaviour has more children now.", numChildren + 1, givenNode.getChildren().size());
		
		ParseTree nodeToAdd2 = new StringNode("Second node to add");
		defaultNode.addChild(nodeToAdd2);
		copyNode.addChild(nodeToAdd2);
		givenNode.addChild(nodeToAdd2);
		
		assertEquals("Add to default node: Default behaviour is to copy the child list.", numChildren + 1, defaultNode.getChildren().size());
		assertEquals("Add to copy node: Specified copy behaviour also copies the list.", numChildren + 1, copyNode.getChildren().size());
		assertEquals("Add to given node: Specified use given behaviour has more children now.", numChildren + 2, givenNode.getChildren().size());
		assertEquals("Add to given node: test list is also increased", numChildren + 2, childrenToTest.size());
		
		childrenToTest.remove(nodeToAdd);
		
		assertEquals("Remove node from test list: Default behaviour is to copy the child list.", numChildren + 1, defaultNode.getChildren().size());
		assertEquals("Remove node from test list: Specified copy behaviour also copies the list.", numChildren + 1, copyNode.getChildren().size());
		assertEquals("Remove node from test list: Specified use given behaviour has less children now.", numChildren + 1, givenNode.getChildren().size());

		defaultNode.removeChild(nodeToAdd2);
		copyNode.removeChild(nodeToAdd2);
		givenNode.removeChild(nodeToAdd2);
		
		assertEquals("Remove from default node: Default behaviour is is back to start", numChildren, defaultNode.getChildren().size());
		assertEquals("Remove from copy node: Specified copy behaviour is back to start.", numChildren, copyNode.getChildren().size());
		assertEquals("Remove from given node: Specified use given behaviour is back to start.", numChildren, givenNode.getChildren().size());
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
