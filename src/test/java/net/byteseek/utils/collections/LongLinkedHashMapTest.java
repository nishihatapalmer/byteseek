/*
 * Copyright Matt Palmer 2017, All rights reserved.
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
package net.byteseek.utils.collections;

import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class LongLinkedHashMapTest {

    private LongLinkedHashMap<Integer> test;
    private Integer testObject1 = new Integer(1);
    private Integer testObject2 = new Integer(2);
    private Integer testObject3 = new Integer(3);

    @Before
    public void setup() {
        test = new LongLinkedHashMap<Integer>();
    }

    @Test
    public void testGet() throws Exception {
        assertNull("No object in map.", test.get(0));

        test.put(1, testObject1);
        assertNull("No object for wrong key", test.get(0));
        assertEquals("Correct object for right key", testObject1, test.get(1));

        test.put(1, testObject2);
        assertEquals("Replacing object gets right object", testObject2, test.get(1));

        test.put(2, testObject2);
        assertEquals("New object gets right object", testObject2, test.get(2));

        assertNull("Still no object for wrong key", test.get(5));
    }

    @Test
    public void testPut() throws Exception {
        assertNull("Object returned by first put is correct", test.put(1, testObject1));

        assertEquals("Object returned by second put is correct", testObject1, test.put(1, testObject2));

        assertNull("Object returned by third put is correct", test.put(2, testObject2));

        assertEquals("Object returned by fourth put is correct", testObject2, test.put(2, testObject3));
    }

    @Test
    public void testRemove() throws Exception {
        assertNull("Removing from non key is null", test.remove(1));

        test.put(0, testObject1);
        assertNull("Removing from non key after put is null", test.remove(1));

        assertEquals("Removing from existing key returns correct object", testObject1, test.remove(0));

        assertNull("Removing from previous key returns null", test.remove(0));

        test.put(1, testObject1);
        test.put(2, testObject2);
        test.put(3, testObject2);
        test.remove(3);
        test.remove(1);
        test.remove(2);
        assertTrue("Map is empty after removing head, tail and middle", test.isEmpty());
    }

    @Test
    public void testSize() throws Exception {
        assertEquals("Starts zero size", 0, test.size());

        test.remove(0);
        assertEquals("Removing from an empty map size stays zero", 0, test.size());

        test.put(1, testObject1);
        assertEquals("Size is 1", 1, test.size());

        test.put(1, testObject1);
        assertEquals("Update existing key with existing object does not increase size", 1, test.size());

        test.put(1, testObject2);
        assertEquals("Update existing key with new object does not increase size", 1, test.size());

        test.put(2, testObject2);
        assertEquals("Add a new key with same value increases size", 2, test.size());

        test.put(3, testObject3);
        assertEquals("Add a new key with a different value increases size", 3, test.size());

        test.remove(4);
        assertEquals("Removing a non existent key does not decrease size", 3, test.size());

        test.remove(2);
        assertEquals("Removing an existing key decreases size", 2, test.size());

        test.remove(2);
        assertEquals("Removing the same key twice does not decrease size", 2, test.size());

        test.remove(1);
        test.remove(3);
        assertEquals("Removing remaining keys returns to zero size", 0, test.size());

        test.remove(1);
        assertEquals("Removing from an empty map size stays zero", 0, test.size());
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertTrue("Empty is empty", test.isEmpty());

        test.put(0, testObject1);
        assertFalse("Added objects not empty", test.isEmpty());

        test.put(1, testObject1);
        assertFalse("Added second object not empty", test.isEmpty());

        test.remove(1);
        test.remove(0);
        assertTrue("Removing both now empty again", test.isEmpty());
    }

    @Test
    public void testClear() throws Exception {
        test.clear();
        assertEquals("Size after empty clear is zero", 0, test.size());

        test.put(0, testObject1);
        test.put(1, testObject2);
        test.clear();
        assertEquals("Size after two added clear is zero", 0, test.size());
        assertFalse("key 0 does not exist", test.containsKey(0));
        assertFalse("key 1 does not exist", test.containsKey(1));
        assertNull("cannot get key 0", test.get(0));
        assertNull("canont get key 1", test.get(1));
        assertFalse("Object 1 does not exist", test.containsValue(testObject1));
        assertFalse("Object 2 does not exist", test.containsValue(testObject2));
    }

    @Test
    public void testContainsKey() throws Exception {
        assertFalse("No key to start", test.containsKey(0));

        test.put(0, testObject1);
        assertTrue("Key now exists", test.containsKey(0));
        assertFalse("But not other keys yet", test.containsKey(1) && test.containsKey(2));

        test.put(2, testObject2);
        assertTrue("Key 2 now exists", test.containsKey(2));
        assertFalse("But still not key 1", test.containsKey(1));

        test.put(1, testObject3);
        assertTrue("Key 1 now exists", test.containsKey(1));

        test.remove(2);
        assertFalse("Key 2 no longer exists", test.containsKey(2));

        test.remove(0);
        assertFalse("Key 0 no longer exists", test.containsKey(0));

        test.remove(1);
        assertFalse("Key 1 no longer exists", test.containsKey(1));
    }

    @Test
    public void testContainsValue() throws Exception {
        assertFalse("No values to start",
                test.containsValue(testObject1) || test.containsValue(testObject2) || test.containsValue(testObject3));

        test.put(0, testObject1);
        assertTrue("Value now exists", test.containsValue(testObject1));
        assertFalse("But not other values yet", test.containsValue(testObject2) && test.containsValue(testObject3));

        test.put(2, testObject2);
        assertTrue("Value 2 now exists", test.containsValue(testObject2));
        assertFalse("But still not value 3", test.containsValue(testObject3));

        test.put(1, testObject3);
        assertTrue("Value 3 now exists", test.containsValue(testObject3));

        test.remove(2);
        assertFalse("Value 2 no longer exists", test.containsValue(testObject2));

        test.remove(0);
        assertFalse("Value 1 no longer exists", test.containsValue(testObject1));

        test.remove(1);
        assertFalse("Value 1 no longer exists", test.containsValue(testObject3));
    }

    @Test
    public void testRemoveEldestEntry() throws Exception {

        assertFalse("By default remove is false", test.removeEldestEntry(null));

        test = new LongLinkedHashMap<Integer>() {
            @Override
            public boolean removeEldestEntry(LongMapEntry<Integer> entry) {
                return size() > 2;
            }
        };

        test.put(1, testObject1);
        test.put(2, testObject2);
        test.get(1);
        test.put(3, testObject3);

        assertFalse("Insert order: First object has been removed", test.containsKey(1));
        assertEquals("Size is now two after adding 3", 2, test.size());

        test = new LongLinkedHashMap<Integer>(10, true) {
            @Override
            public boolean removeEldestEntry(LongMapEntry<Integer> entry) {
                return size() > 2;
            }
        };

        test.put(1, testObject1);
        test.put(2, testObject2);
        test.get(1);
        test.put(3, testObject3);

        assertTrue("Access order: First object is still there", test.containsKey(1));
        assertFalse("Second object has been removed", test.containsKey(2));
        assertTrue("Third object is still there", test.containsKey(3));
        assertEquals("Size is now two after adding 3", 2, test.size());

    }

    @Test
    public void testIteratorInsertionOrder() throws Exception {
        test = new LongLinkedHashMap<Integer>();
        testInsertionOrder();
        test = new LongLinkedHashMap<Integer>(5);
        testInsertionOrder();
    }

    private void testInsertionOrder() {
        // add integers in reverse order:
        for (int i = 10; i >= 0; i--) {
            test.put(i, Integer.valueOf(i));
        }
        assertEquals(11, test.size());

        // Now get them again in the opposite order to adding them.
        for (int i = 0; i <=10; i++) {
            test.get(i);
        }

        // Should still be in the order inserted:
        int value = 10;
        for(LongMapEntry<Integer> entry : test) {
            assertEquals(value, (int) entry.getValue());
            assertEquals(value, (int) entry.getKey());
            value--;
        }
        assertEquals(-1, value);
    }

    @Test
    public void testIteratorAccessOrder() throws Exception {
        test = new LongLinkedHashMap<Integer>(true);

        // add integers in reverse order:
        for (int i = 10; i >= 0; i--) {
            test.put(i, Integer.valueOf(i));
        }
        assertEquals(11, test.size());

        // Now get them again in the opposite order to adding them.
        for (int i = 0; i <=10; i++) {
            test.get(i);
        }

        int value = 0;
        for (LongMapEntry<Integer> entry : test) {
            assertEquals(value, (int) entry.getValue());
            assertEquals(value, (int) entry.getKey());
            value++;
        }
        assertEquals(11, value);
    }

    @Test
    public void testSetValueInsertionOrder() {
        test = new LongLinkedHashMap<Integer>(false);

        // add integers in reverse order:
        for (int i = 10; i >= 0; i--) {
            test.put(i, Integer.valueOf(i));
        }
        assertEquals(11, test.size());

        for (LongMapEntry<Integer> entry : test) {
            entry.setValue(entry.getValue() + 100);
        }

        for (int i = 0; i <=10; i++) {
            assertEquals(100 + i, (int) test.get(i));
        }
    }

    @Test
    public void testSetValueAccessOrder() {
        test = new LongLinkedHashMap<Integer>(true);

        // add integers in reverse order:
        for (int i = 10; i >= 0; i--) {
            test.put(i, Integer.valueOf(i));
        }
        assertEquals(11, test.size());

        for (LongMapEntry<Integer> entry : test) {
            entry.setValue(entry.getValue() + 100);
        }

        for (int i = 0; i <=10; i++) {
            assertEquals(100 + i, (int) test.get(i));
        }
    }

    @Test
    public void testIteratorEmpty() throws Exception {
        test = new LongLinkedHashMap<Integer>();
        Iterator<LongMapEntry<Integer>> it = test.iterator();
        assertFalse(it.hasNext());
    }

    @Test(expected=NoSuchElementException.class)
    public void testIteratorNoSuchElement() throws Exception {
        test = new LongLinkedHashMap<Integer>();
        Iterator<LongMapEntry<Integer>> it = test.iterator();
        it.next();
    }

    @Test
    public void testIterateAndRemove() {
        test = new LongLinkedHashMap<Integer>(true);

        // add integers in reverse order:
        for (int i = 10; i >= 0; i--) {
            test.put(i, Integer.valueOf(i));
        }
        assertEquals(11, test.size());

        Iterator<LongMapEntry<Integer>> it = test.iterator();
        int size = 11;
        while(it.hasNext()) {
            assertEquals(size, test.size());
            it.next();
            assertEquals(size, test.size());

            it.remove();
            size--;
            assertEquals(size, test.size());
        }

    }

}