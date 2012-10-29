package net.domesdaybook.parser.tree.node;

import static org.junit.Assert.*;

import net.domesdaybook.parser.ParseException;
import net.domesdaybook.parser.tree.ParseTreeType;

import org.junit.Test;

public class StringNodeTest {

	@Test
	public final void testTextValues() {
		testNodes("");
		testNodes("                                                                           ");
		testNodes("Oberon");
		testNodes("Titania");
		testNodes("I know a bank where the wild thyme grows,");
		testNodes(getAllCharsFromZeroTo255());
	}
	
	private String getAllCharsFromZeroTo255() {
		StringBuilder builder = new StringBuilder(256);
		for (char c = 0; c < 256; c++) {
			builder.append(c);
		}
		return builder.toString();
	}
	
	private void testNodes(String value) {
		testNode("Default case sensitive: ",     new StringNode(value),         false, value);
		testNode("Specified case sensitive: ",   new StringNode(value, false), false, value);
		testNode("Specified case insensitive: ", new StringNode(value, true),  true, value);
	}
	
	private void testNode(String description, StringNode node, boolean isCaseSensitive, String value) {
		testNodeAttributes(description + "(original value) ", node, isCaseSensitive, value);
		node.setTextValue(value + value);
		testNodeAttributes(description + "(doubled value) ", node, isCaseSensitive, value + value);
		node.setTextValue("");    
		testNodeAttributes(description + "(blank value) ", node, isCaseSensitive, "");
	}
	
	private void testNodeAttributes(String description, StringNode node, boolean isCaseSensitive, String value) {
		
		ParseTreeType expectedType = isCaseSensitive? ParseTreeType.STRING : ParseTreeType.CASE_INSENSITIVE_STRING;
		assertEquals(description + "Node is case sensitive?: " + isCaseSensitive, expectedType, node.getParseTreeType());
		assertEquals(description + "Node agrees its case sensitive status:" + isCaseSensitive, isCaseSensitive, node.isCaseSensitive());
		
		try {
			assertEquals(description + "Node has correct string value: [" + value + ']', value, node.getTextValue());
		} catch (ParseException e) {
			fail(description + "Should not throw ParseException requesting text value.");
		}
		
		try { 
			assertFalse(description + "Node is not inverted.", node.isValueInverted());
		} catch (ParseException e) {
			fail(description + "Should not throw ParseException requesting inversion status.");
		}		
		
		try { 
			node.getByteValue();
			fail(description + "Expected a ParseException if asked for the byte value");
		} catch (ParseException allIsFine) {};
		
		try { 
			node.getIntValue();
			fail(description + "Expected a ParseException if asked for the int value");
		} catch (ParseException allIsFine) {};
		
		assertNotNull(description + "Child list is not null", node.getChildren());
		assertTrue(description + "Child list is empty", node.getChildren().isEmpty());		
	}

}
