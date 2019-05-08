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

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class PositionHashMapTest {

    private static final Object DUMMY_OBJECT = new Object();

    @Test
    public void testSize() throws Exception {
        PositionHashMap<Object> pmap = new PositionHashMap<Object>();
        assertEquals(0, pmap.size());

        final long TOTAL_KEYS = 1000;

        // Put TOTAL_KEYS keys - size should increase with each put:
        for (long keyNum = 1; keyNum <= TOTAL_KEYS; keyNum++) {
            pmap.put(keyNum, DUMMY_OBJECT);
            assertEquals(keyNum, pmap.size());
        }

        // Put to same keys - shouldn't increase size, just update value.
        for (long keyNum = 1; keyNum <= TOTAL_KEYS; keyNum++) {
            pmap.put(keyNum, DUMMY_OBJECT);
            assertEquals(TOTAL_KEYS, pmap.size());
        }

        Map<Integer, Integer> x = new HashMap<Integer, Integer>();
    }

    @Test
    public void testIsEmpty() throws Exception {
        PositionHashMap<Object> pmap = new PositionHashMap<Object>();
        assertTrue(pmap.isEmpty());

        pmap.put(0, new Object());
        assertFalse(pmap.isEmpty());

        pmap.remove(0);
        assertTrue(pmap.isEmpty());
        assertEquals(0, pmap.size());
    }

    @Test
    public void testPutGet() throws Exception {
        PositionHashMap<Object> pmap = new PositionHashMap<Object>();
        Map<Long,Object> compareMap = new HashMap<Long,Object>();

        final int TOTAL_VALS = 2000;
        final Random rand = new Random(0);

        // Put different objects into random keys in both compareMap and positionMap:
        for (int num = 0; num < TOTAL_VALS; num++) {
            long randKey = rand.nextLong() & Long.MAX_VALUE;
            Object newObject = new Object();
            pmap.put(randKey, newObject);
            compareMap.put(randKey, newObject);
        }

        // ensure they have recorded the same number of elements before proceeding.
        assertEquals(pmap.size(), compareMap.size());

        // iterate over the keys in the compare map and ensure that the position map has the same objects for the same key value:
        for (Long key : compareMap.keySet()) {
            Object pobj = pmap.get(key);
            Object cobj = compareMap.get(key);
            assertEquals(pobj, cobj);
        }

        final int size = pmap.size();

        // Check updating existing keys:

        // Now replace the objects in the pmap against the same keys:
        for (Long key : compareMap.keySet()) {
            pmap.put(key, new Object());
        }

        // size shouldn't have changed.
        assertEquals(size, pmap.size());

        // iterate over the keys in the compare map and ensure that the position map has different objects for the same key value:
        for (Long key : compareMap.keySet()) {
            Object pobj = pmap.get(key);
            Object cobj = compareMap.get(key);
            assertNotEquals(pobj, cobj);
        }

    }

    @Test
    public void testIterator() throws Exception {
        PositionHashMap<Integer> test = new PositionHashMap<Integer>();
        Set<Integer> expected = new HashSet<Integer>();

        // add integers in reverse order:
        for (int i = 10; i >= 0; i--) {
            test.put(i, Integer.valueOf(i));
            expected.add(i);
        }
        assertEquals(11, test.size());

        for (LongMapEntry<Integer> entry : test) {
            assertEquals(entry.getKey(), (int) entry.getValue());
            expected.remove(entry.getValue());
        }
        assertEquals(0, expected.size());
    }

    @Test
    public void testIterateAndRemove() {
        PositionHashMap<Integer> test = new PositionHashMap<Integer>();

        // add integers in reverse order:
        for (int i = 10; i >= 0; i--) {
            test.put(i, Integer.valueOf(i));
        }
        assertEquals(11, test.size());

        Iterator<LongMapEntry<Integer>> it = test.iterator();
        int size = 11;
        while(it.hasNext()) {
            assertEquals(size, test.size());
            LongMapEntry entry = it.next();
            assertEquals(size, test.size());

            assertTrue(test.containsKey(entry.getKey()));
            it.remove();
            assertFalse(test.containsKey(entry.getKey()));
            size--;
            assertEquals(size, test.size());
        }

    }

    @Test(expected=IllegalStateException.class)
    public void testIteratorRemoveWithoutNext() {
        PositionHashMap<Integer> pmap = new PositionHashMap<Integer>();
        pmap.put(0L, Integer.valueOf(0));
        Iterator<LongMapEntry<Integer>> it = pmap.iterator();
        it.remove();
    }

    @Test(expected=NoSuchElementException.class)
    public void testNextAfterFinishedIterating() {
        PositionHashMap<Integer> pmap = new PositionHashMap<Integer>();
        pmap.put(0L, Integer.valueOf(0));
        pmap.put(1L, Integer.valueOf(0));
        pmap.put(2L, Integer.valueOf(0));
        pmap.put(3L, Integer.valueOf(0));

        Iterator<LongMapEntry<Integer>> it = pmap.iterator();
        while (it.hasNext()) {it.next();}
        it.next();
    }

    @Test
    public void testRemove() throws Exception {
        PositionHashMap<Object> pmap = new PositionHashMap<Object>();
        Map<Long,Object> compareMap = new HashMap<Long,Object>();

        final int TOTAL_VALS = 2000;
        final Random rand = new Random(0);

        // Put different objects into random keys in both compareMap and positionMap:
        for (int num = 0; num < TOTAL_VALS; num++) {
            long randKey = rand.nextLong() & Long.MAX_VALUE; // ensure not negative.
            Object newObject = new Object();
            pmap.put(randKey, newObject);
            compareMap.put(randKey, newObject);
        }

        // ensure they have recorded the same number of elements before proceeding.
        int size = pmap.size();
        assertEquals(compareMap.size(), size);

        // iterate over the keys in the compare map and remove each key from the PositionHashMap.
        for (Long key : compareMap.keySet()) {
            pmap.remove(key);
            size--;
            assertEquals(size, pmap.size());
        }

        assertTrue(pmap.isEmpty());
    }

    @Test
    public void testIteratorToString() {
        PositionHashMap<Integer> test = new PositionHashMap<Integer>();
        Iterator<LongMapEntry<Integer>> it = test.iterator();
        assertTrue(it.toString().contains(it.getClass().getSimpleName()));
        assertTrue(it.toString().contains(test.getClass().getSimpleName()));
    }

    @Test
    public void testMapEntryToString() {
        PositionHashMap<Integer> test = new PositionHashMap<Integer>();
        test.put(0, Integer.valueOf(0));
        Iterator<LongMapEntry<Integer>> it = test.iterator();
        LongMapEntry<Integer> entry = it.next();
        assertTrue(entry.toString().contains(entry.getClass().getSimpleName()));
        assertTrue(entry.toString().contains("key"));
        assertTrue(entry.toString().contains("value"));
    }

    @Test
    public void testRemoveNonExistentObject() {
        PositionHashMap<Integer> test = new PositionHashMap<Integer>();

        // add integers in reverse order:
        for (int i = 10; i >= 0; i--) {
            test.put(i, Integer.valueOf(i));
        }
        assertEquals(11, test.size());

        assertNull(test.remove(11));
        assertEquals(11, test.size());
    }

    @Test
    public void testClear() throws Exception {
        PositionHashMap<Object> pmap = new PositionHashMap<Object>();
        pmap.put(0, new Object());
        assertFalse(pmap.isEmpty());

        pmap.clear();
        assertTrue(pmap.isEmpty());

        pmap.put(0, new Object());
        pmap.put(1, new Object());
        assertFalse(pmap.isEmpty());

        pmap.clear();
        assertTrue(pmap.isEmpty());
        assertEquals(0, pmap.size());
    }

    @Test
    public void testMapEntrySetValue() {
        PositionHashMap<Integer> test = new PositionHashMap<Integer>();

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

}