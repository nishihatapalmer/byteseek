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

package net.byteseek.parser.tree.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;

import net.byteseek.parser.ParseException;
import net.byteseek.parser.tree.ParseTree;
import net.byteseek.parser.tree.ParseTreeType;

import org.junit.Test;

public class BaseNodeTest {
	
	@Test
	public final void testAllParseTreeTypes() {
		for (ParseTreeType type : ParseTreeType.values()) {
			testNode(new BaseNode(type), type);
		}
	}
	
	private void testNode(BaseNode node, ParseTreeType type) {
		assertEquals("BaseNode has correct type: " + type, node.getParseTreeType(), type);
		assertFalse("BaseNode inversion should be false.", node.isValueInverted());
		
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
		
		try {
			node.addChild(new BaseNode(ParseTreeType.ALL_BITMASK));
			fail("Expected an UnsupportedOperationException when adding a child");
		} catch (UnsupportedOperationException expected) {}
		
		try {
			node.removeChild(0);
			fail("Expected an UnsupportedOperationException when removing a child");
		} catch (UnsupportedOperationException expected) {}
		
		try {
			node.getChild(0);
			fail("Expected an IndexOutOfBoundsException when getting a child");
		} catch (IndexOutOfBoundsException expected) {}
		
		Iterator<ParseTree> iterator = node.iterator();
		assertFalse("Iterator has no child nodes", iterator.hasNext());
		
		assertEquals("Child list is empty", 0, node.getNumChildren());
		assertTrue("toString contains class name", node.toString().contains(node.getClass().getSimpleName()));
	}
	
}
