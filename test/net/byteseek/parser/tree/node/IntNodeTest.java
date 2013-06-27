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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Random;

import net.byteseek.parser.ParseException;
import net.byteseek.parser.tree.ParseTreeType;
import net.byteseek.parser.tree.node.IntNode;

import org.junit.Test;

/**
 * @author Matt Palmer
 */
public class IntNodeTest {

	@Test
	public final void test256RandomIntValues() {
		Random random = new Random();
		for (int testNo = 0; testNo < 256; testNo++) {
			testPosNegValues(random.nextInt());
		}
	}
	
	@Test
	public final void testCommonIntValues() {
		testPosNegValues(0);
		testPosNegValues(1);
		testPosNegValues(2);
		testPosNegValues(15);			
		testPosNegValues(16);		
		testPosNegValues(255);
		testPosNegValues(256);
		testPosNegValues(1023);
		testPosNegValues(1024);
		testPosNegValues(10);
		testPosNegValues(100);
		testPosNegValues(1000);
		testNode(new IntNode(Integer.MAX_VALUE), Integer.MAX_VALUE);
		testNode(new IntNode(Integer.MIN_VALUE), Integer.MIN_VALUE);
	}
	
	private void testPosNegValues(int value) {
		testNode(new IntNode(value), value);
		testNode(new IntNode(-value), -value);
	}
	
	private void testNode(IntNode node, int value) {
		assertEquals("IntNode has correct type: ", ParseTreeType.INTEGER, node.getParseTreeType());
		try {
			assertEquals("IntNode has correct value:" + value, value, node.getIntValue());
		} catch (ParseException e1) {
			fail("IntNode should not throw a ParseException if asked for the byte value.");
		}
		
		assertFalse("IntNode value is not inverted: " , node.isValueInverted());
		
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
		assertTrue("toString contains class name", node.toString().contains(node.getClass().getSimpleName()));
	}
}
