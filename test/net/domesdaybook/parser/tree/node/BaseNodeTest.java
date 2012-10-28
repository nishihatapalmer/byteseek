package net.domesdaybook.parser.tree.node;

import static org.junit.Assert.*;

import org.junit.Test;

import net.domesdaybook.parser.ParseException;
import net.domesdaybook.parser.tree.ParseTree;
import net.domesdaybook.parser.tree.ParseTreeType;

public class BaseNodeTest {
	
	@Test
	public final void testBaseNode() {
		for (ParseTreeType type : ParseTreeType.values()) {
			ParseTree node = new BaseNode(type);
			testNode(node, type, false);

			node = new BaseNode(type, false);
			testNode(node, type, false);
			
			node = new BaseNode(type, true);
			testNode(node, type, true);
		}
	}
	
	private void testNode(ParseTree node, ParseTreeType type, boolean isInverted) {
		assertEquals("BaseNode has correct type: " + type, node.getParseTreeType(), type);
		try {
			assertEquals("BaseNode value is correct inversion: " + isInverted, isInverted, node.isValueInverted());
		} catch (ParseException e) {
			fail("BaseNode should not throw a ParseException if asked if the value is inverted.");
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
		assertTrue("Child list is empty", node.getChildren().isEmpty());
	}
	
}
