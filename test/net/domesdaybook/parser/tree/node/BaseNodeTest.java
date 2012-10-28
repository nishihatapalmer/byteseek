package net.domesdaybook.parser.tree.node;

import static org.junit.Assert.*;

import org.junit.Test;

import net.domesdaybook.parser.ParseException;
import net.domesdaybook.parser.tree.ParseTree;
import net.domesdaybook.parser.tree.ParseTreeType;

public class BaseNodeTest {
	
	@Test
	public final void testAllParseTreeTypes() {
		for (ParseTreeType type : ParseTreeType.values()) {
			testNode(new BaseNode(type), type);
		}
	}
	
	private void testNode(ParseTree node, ParseTreeType type) {
		assertEquals("BaseNode has correct type: " + type, node.getParseTreeType(), type);
		try {
			assertFalse("BaseNode inversion should be false.", node.isValueInverted());
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
