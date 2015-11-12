package net.byteseek.collections;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LongLinkedHashMapTest {

    private LongLinkedHashMap test;
    private Object testObject1 = new Object();
    private Object testObject2 = new Object();
    private Object testObject3 = new Object();

    @Before
    public void setup() {
        test = new LongLinkedHashMap<Object>();
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
    public void testEquals() throws Exception {
        //TODO: write test.
    }

    @Test
    public void testRemoveEldestEntry() throws Exception {

        assertFalse("By default remove is false", test.removeEldestEntry(null));

        test = new LongLinkedHashMap<Object>() {
            @Override
            public boolean removeEldestEntry(LongLinkedHashMap.MapEntry<Object> entry) {
                return size() > 2;
            }
        };

        test.put(1, testObject1);
        test.put(2, testObject2);
        test.get(1);
        test.put(3, testObject3);

        assertFalse("Insert order: First object has been removed", test.containsKey(1));
        assertEquals("Size is now two after adding 3", 2, test.size());

        test = new LongLinkedHashMap<Object>(10, 1.1f, true) {
            @Override
            public boolean removeEldestEntry(LongLinkedHashMap.MapEntry<Object> entry) {
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
    public void testIterator() throws Exception {
        //TODO: write test.
    }

}