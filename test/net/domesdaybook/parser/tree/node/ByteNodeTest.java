package net.domesdaybook.parser.tree.node;

import static org.junit.Assert.*;

import net.domesdaybook.parser.ParseException;
import net.domesdaybook.parser.tree.ParseTree;
import net.domesdaybook.parser.tree.ParseTreeType;

import org.junit.Test;

public class ByteNodeTest {

	@Test
	public final void testAllByteValues() {
		for (int byteValue = 0; byteValue < 256; byteValue++) {
			final byte value = (byte) byteValue;
			
			ByteNode node = new ByteNode(value);
			testNode(node, value, false);
			
			node = new ByteNode(value, false);
			testNode(node, value, false);
			
			node = new ByteNode(value, true);
			testNode(node, value, true);
		}
	}
	
	private void testNode(ByteNode node, byte value, boolean isInverted) {
		assertEquals("ByteNode has correct type: ", ParseTreeType.BYTE, node.getParseTreeType());
		try {
			assertEquals("ByteNode has correct value:" + value, value, node.getByteValue());
		} catch (ParseException e1) {
			fail("ByteNode should not throw a ParseException if asked for the byte value.");
		}
		
		try {
			assertEquals("ByteNode value is correct inversion: " + isInverted, isInverted, node.isValueInverted());
		} catch (ParseException e) {
			fail("ByteNode should not throw a ParseException if asked if the value is inverted.");
		}
		
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
