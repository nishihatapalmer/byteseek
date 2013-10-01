/*
 * Copyright Matt Palmer 2013, All rights reserved.
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
package net.byteseek.util.collections;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import net.byteseek.collections.IdentityHashSet;

import org.junit.Test;

public class IdentityHashSetTest {

	@Test
	public final void testConstructor() {
		IdentityHashSet<Byte> test = new IdentityHashSet<Byte>();
		assertTrue("Set is empty", test.isEmpty());
		assertEquals("Set size is zero", 0, test.size());

		IdentityHashSet<Integer> test2 = new IdentityHashSet<Integer>();
		assertTrue("Set is empty", test2.isEmpty());
		assertEquals("Set size is zero", 0, test2.size());
		
		List<Integer> list = new ArrayList<Integer>();
		Integer one = new Integer(1);
		Integer two = new Integer(257);
		Integer three = new Integer(1000000);
		list.add(one);
		list.add(two);
		list.add(three);
		
		test2 = new IdentityHashSet<Integer>(list);
		assertEquals("Hash set is same size as constructed list", list.size(), test2.size());
		for (Integer value : list) {
			assertTrue("Value is in the set", test2.contains(value));
		}
		
		List<Integer> list2 = new ArrayList<Integer>();
		Integer one2 = new Integer(1);
		Integer two2 = new Integer(257);
		Integer three2 = new Integer(1000000);
		list.add(one2);
		list.add(two2);
		list.add(three2);
		for (Integer value : list2) {
			assertFalse("Value is not in the set", test2.contains(value));
		}
	}

	@Test
	public void testAdd() {
		fail("Not implemented");
	}
	
	@Test
	public void testRemove() {
		fail("Not implemented");
	}
	
	@Test
	public void testSize() {
		fail("Not implemented");
	}
	
	@Test
	public void testContains() {
		fail("Not implemented");
	}
	
	@Test
	public void testIterator() {
		fail("Not implemented");
	}
	
	@Test
	public void testClone() {
		fail("Not implemented");
	}
}
