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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.domesdaybook.parser.ParseException;
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
		
		assertEquals("ByteNode value is correct inversion: " + isInverted, isInverted, node.isValueInverted());
		
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
