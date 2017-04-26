package net.byteseek.utils.collections;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
    }

    @Test
    public void testIsEmpty() throws Exception {
        PositionHashMap<Object> pmap = new PositionHashMap<Object>();
        assertTrue(pmap.isEmpty());

        pmap.put(0, new Object());
        assertFalse(pmap.isEmpty());
    }

    @Test
    public void testPutGet() throws Exception {
        PositionHashMap<Object> pmap = new PositionHashMap<Object>();
        Map<Long,Object> compareMap = new HashMap<Long,Object>();

        final int TOTAL_VALS = 2000;
        final Random rand = new Random(0);

        // Put different objects into random keys in both compareMap and positionMap:
        for (int num = 0; num < TOTAL_VALS; num++) {
            long randKey;
            do {
                randKey = rand.nextLong();
            } while (randKey <= Long.MIN_VALUE + 1); // two smallest keys not allowed.
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

        // Now replace the objects in the pmap against the same keys:
        for (Long key : compareMap.keySet()) {
            pmap.put(key, new Object());
        }

        assertEquals(size, pmap.size()); // size shouldn't have changed.

        // iterate over the keys in the compare map and ensure that the position map has different objects for the same key value:
        for (Long key : compareMap.keySet()) {
            Object pobj = pmap.get(key);
            Object cobj = compareMap.get(key);
            assertNotEquals(pobj, cobj);
        }

    }


    @Test
    public void testRemove() throws Exception {

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
    }

    @Test
    public void testGetTableSize() throws Exception {

    }

    @Test
    public void testGetTableBits() throws Exception {

    }
}