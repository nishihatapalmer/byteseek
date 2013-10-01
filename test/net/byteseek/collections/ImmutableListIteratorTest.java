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
package net.byteseek.collections;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;

public class ImmutableListIteratorTest {

	@SuppressWarnings("unused")
	@Test
	public final void testImmutableListIterator() {
		try {
			ImmutableListIterator<Integer> test = new ImmutableListIterator<Integer>(null);
			fail("Expected an IllegalArgumentException");
		} catch (IllegalArgumentException expected) {};
		
		ImmutableListIterator<Integer> test = new ImmutableListIterator<Integer>(new ArrayList<Integer>());
			
		List<Integer> testList = new ArrayList<Integer>();
		testList.add(1);
		testList.add(257);
		test = new ImmutableListIterator<Integer>(testList);
	}

	@SuppressWarnings("unused")
	@Test
	public final void testHasNext() {
		ImmutableListIterator<Integer> test = new ImmutableListIterator<Integer>(new ArrayList<Integer>());
		assertFalse("Empty list does not have next", test.hasNext());
		
		List<Integer> testList = new ArrayList<Integer>();
		testList.add(1);
		testList.add(257);
		testList.add(32);
		testList.add(99999999);
		test = new ImmutableListIterator<Integer>(testList);
		for (Integer value : testList) {
			assertTrue("Iterator has next", test.hasNext());
			test.next();
		}
		assertFalse("Iterated list does not have next", test.hasNext());
	}

	@Test
	public final void testNext() {
		ImmutableListIterator<Integer> test = new ImmutableListIterator<Integer>(new ArrayList<Integer>());
		try {
			test.next();
			fail("Expected a NoSuchElementException");
		} catch (NoSuchElementException expected) {};
		
		test = new ImmutableListIterator<Integer>(new ArrayList<Integer>());
		assertFalse("Empty list does not have next", test.hasNext());
		
		List<Integer> testList = new ArrayList<Integer>();
		testList.add(1);
		testList.add(257);
		testList.add(32);
		testList.add(99999999);
		test = new ImmutableListIterator<Integer>(testList);
		for (Integer value : testList) {
			assertEquals("Values are correct", value, test.next());
		}
		assertFalse("Iterated list does not have next", test.hasNext());
	}

	@SuppressWarnings("unused")
	@Test
	public final void testRemove() {
		ImmutableListIterator<Integer> test = new ImmutableListIterator<Integer>(new ArrayList<Integer>());
		try {
			test.remove();
			fail("Expected an UnsupportedOperationException");
		} catch (UnsupportedOperationException expected) {};
		
		test = new ImmutableListIterator<Integer>(new ArrayList<Integer>());
		try {
			test.remove();
			fail("Expected an UnsupportedOperationException");
		} catch (UnsupportedOperationException expected) {};

		
		List<Integer> testList = new ArrayList<Integer>();
		testList.add(1);
		testList.add(257);
		testList.add(32);
		testList.add(99999999);
		test = new ImmutableListIterator<Integer>(testList);
		for (Integer value : testList) {
			try {
				test.remove();
				fail("Expected an UnsupportedOperationException");
			} catch (UnsupportedOperationException expected) {};
			test.next();
		}
	}
}
