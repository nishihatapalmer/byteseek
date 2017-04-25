package net.byteseek.utils.collections;

import org.junit.Test;

import static org.junit.Assert.*;

public class PositionHashMapTest {

    private static final Object DUMMY_OBJECT = new Object();

    @Test
    public void testSize() throws Exception {
        PositionHashMap<Object> pmap = new PositionHashMap<Object>();
        assertEquals(0, pmap.size());

        final long TOTAL_KEYS = 10;

        // Put to three keys - size should increase with each put:
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
    public void testGet() throws Exception {

    }

    @Test
    public void testPut() throws Exception {

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