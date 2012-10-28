/**
 * 
 */
package net.domesdaybook.parser.tree.node;

import static org.junit.Assert.*;

import java.util.Random;

import net.domesdaybook.parser.ParseException;
import net.domesdaybook.parser.tree.ParseTree;
import net.domesdaybook.parser.tree.ParseTreeType;

import org.junit.Test;

/**
 * @author Matt Palmer
 */
public class IntNodeTest {

	@Test
	public final void testByteValue() {
		Random random = new Random();
		for (int testNo = 0; testNo < 256; testNo++) {
			final int value = random.nextInt();
			final ParseTree node = new IntNode(value);
			testNode(node, value);
		}
	}
	
	private void testNode(ParseTree node, int value) {
		assertEquals("IntNode has correct type: ", ParseTreeType.INTEGER, node.getParseTreeType());
		try {
			assertEquals("IntNode has correct value:" + value, value, node.getIntValue());
		} catch (ParseException e1) {
			fail("IntNode should not throw a ParseException if asked for the byte value.");
		}
		
		try {
			assertFalse("IntNode value is not inverted: " , node.isValueInverted());
		} catch (ParseException e) {
			fail("IntNode should not throw a ParseException if asked if the value is inverted.");
		}
		
		try { 
			node.getByteValue();
			fail("Expected a ParseException if asked for the byte value");
		} catch (ParseException allIsFine) {};
		
		try { 
			node.getTextValue();
			fail("Expected a ParseException if asked for the text value");
		} catch (ParseException allIsFine) {};
		
		assertNotNull("Child list is not null", node.getChildren());
		assertTrue("Child list is empty", node.getChildren().isEmpty());		
	}
}
