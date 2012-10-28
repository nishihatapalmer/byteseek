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

/**
 * @author Matt Palmer
 *
 */
public class StructuralNodeTest {

	//TODO: add tests for setChildren, addChild, removeChild
	//      and constructors which create their own lists of children.
	
	@Test
	public final void testStructuralNode() {
		List<ParseTree> children = new ArrayList<ParseTree>();
		runTests(children, 0);
		
		children.add(new BaseNode(ParseTreeType.ANY));
		runTests(children, 1);
		
		children.add(new BaseNode(ParseTreeType.BYTE));
		runTests(children, 2);
	}
	
	
	private void runTests(List<ParseTree> children, int numChildren) {
		for (ParseTreeType type : ParseTreeType.values()) {
			ParseTree node = new StructuralNode(type, children);
			testNode(node, type, numChildren, false);

			node = new StructuralNode(type, children, false);
			testNode(node, type, numChildren, false);
			
			node = new StructuralNode(type, children, true);
			testNode(node, type, numChildren, true);
		}		
	}
	
	private void testNode(ParseTree node, ParseTreeType type, int numChildren, boolean isInverted) {
		assertEquals("StructuralNode has correct type: " + type, node.getParseTreeType(), type);
		try {
			assertEquals("StructuralNode value is correct inversion: " + isInverted, isInverted, node.isValueInverted());
		} catch (ParseException e) {
			fail("StructuralNode should not throw a ParseException if asked if the value is inverted.");
		}
		
		try { 
			node.getByteValue();
			fail("Expected a ParseException if asked for the byte value");
		} catch (ParseException allIsFine) {};
		
		try { 
			node.getIntValue();
			fail("Expected a ParseException if asked for the int value");
		} catch (ParseException allIsFine) {};
		
		try { 
			node.getTextValue();
			fail("Expected a ParseException if asked for the text value");
		} catch (ParseException allIsFine) {};
		
		assertNotNull("Child list is not null", node.getChildren());
		assertEquals("Child list has correct number of children " + numChildren, numChildren, node.getChildren().size());
	}

}
