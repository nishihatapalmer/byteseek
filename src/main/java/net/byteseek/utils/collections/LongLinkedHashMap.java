/*
 * Copyright Matt Palmer 2015, All rights reserved.
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

import org.apache.mahout.math.map.AbstractLongObjectMap;
import org.apache.mahout.math.map.OpenLongObjectHashMap;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.apache.mahout.math.jet.math.Constants.EPSILON;


/**
 * A LinkedHashMap using primitive longs for keys.  This avoids boxing and unboxing
 * long and Long keys when accessing the linked hash map.
 * <p>
 * The implementation is compatible with Map from Java collections, but doesn't
 * implement it, as that interface assumes object keys.
 *
 * Created by Matt Palmer on 11/11/15.
 */
public class LongLinkedHashMap<T> implements Iterable<LongLinkedHashMap.MapEntry<T>> {

    private final AbstractLongObjectMap<Node<T>> map;
    private final boolean orderByAccess;
    private final DoubleLinkedList<T> list = new DoubleLinkedList<T>();

    public LongLinkedHashMap() {
        map = new OpenLongObjectHashMap<Node<T>>();
        this.orderByAccess = false;
    }

    public LongLinkedHashMap(int capacity) {
        map = new OpenLongObjectHashMap<Node<T>>(capacity);
        this.orderByAccess = false;
    }

    public LongLinkedHashMap(int capacity, float loadFactor) {
        this(capacity, loadFactor, false);
    }

    public LongLinkedHashMap(int capacity, float loadFactor, boolean orderByAccess) {
        //note: Colt load factor is \in [0, 1), whereas trove is \in [0, 100), so we'll translate.
        float loadFactorAdj = loadFactor/100;
        map  = new OpenLongObjectHashMap<Node<T>>(capacity, Math.min(0.2d, loadFactorAdj - EPSILON), loadFactorAdj);
        this.orderByAccess = orderByAccess;
    }

    /**
     * Gets the value associated with the key, or null if there is no such item.
     * <p>
     * If the LongLinkedHashMap is ordering its list of items by access order,
     * then getting an item will place it at the top of the list.
     *
     * @param key The key to get the item for.
     * @return The item associated with that key, or null if not present.
     */
    public final T get(long key) {
        final Node<T> node = map.get(key);
        if (node != null) {
            if (orderByAccess) {
                list.moveToHead(node);
            }
            return node.item;
        }
        return null;
    }

    /**
     * Puts the value into the map with the given key.  Any value which was previously associated with that
     * key is returned.  If a new key is inserted, then a check on whether to remove the eldest entry is
     * made, and if it returns true, the eldest entry is removed.
     *
     * @param key The key to use in the map.
     * @param value The value to associate with the key.
     * @return The last value associated with that key, or null if there was none.
     */
    public final T put(long key, T value) {
        final T lastValue;
        final Node<T> node = map.get(key);
        if (node == null) { // no previous key
            lastValue = null;
            map.put(key, list.add(value, key));
            checkEldestEntry();
        } else {
            lastValue = node.item;
            node.item = value; // ensure value of key is updated.
        }
        return lastValue;
    }

    /**
     * Removes a value from the map with the key supplied.
     * Returns the value which was associated with that key, or null if there was no such value.
     * @param key The key to remove the value for.
     * @return The value associated with that key, or null if there was no value for the key.
     */
    public final T remove(long key) {
        Node<T> val = map.get(key);
        if(val != null) {
            map.removeKey(key);
            return list.remove(val);
        }
        else {
            return null;
        }
    }

    /**
     * Returns the size of the map - how many items are in it.
     * @return the size of the map - how many items are in it.
     */
    public final int size() {
        return list.size;
    }

    /**
     * Returns true if the map is empty.
     * @return true if the map is empty.
     */
    public final boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * Removes all entries from the map.
     */
    public final void clear() {
        map.clear();
        list.clear();
    }

    /**
     * Returns true if the map contains the key.
     * @param key The key to check in the map.
     * @return true if the key is in the map.
     */
    public final boolean containsKey(long key) {
        return map.containsKey(key);
    }

    /**
     * Returns true if the map contains the value.
     *
     * @param value The value to check in the map.
     * @return true if the value is in the map.
     */
    public final boolean containsValue(T value) {
        return list.contains(value);
    }

    /**is
     * Returns an iterator over the HashMap, returning the entries in their
     * current order (whether insertion order, or access order).
     * <p>
     * The iterator supports object removal, and the LongObject entry allows
     * you to see the key, the value, and to change the current value.
     *
     * @return An iterator over the HashMap in the appropriate order.
     */
    @Override
    public Iterator<MapEntry<T>> iterator() {
        return new MapEntryIterator();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof LongLinkedHashMap &&
               list.equals(((LongLinkedHashMap)o).list);
    }

    /**
     * Determines whether the eldest entry should be removed.
     * By default, it returns false.  Override this method to
     * return true when you want to remove the eldest entry.
     *
     * @param entry The entry which could be removed..
     * @return Whether it should be removed or not - always false in this implementation.
     */
    protected boolean removeEldestEntry(MapEntry<T> entry) {
        return false;
    }

    private void checkEldestEntry() {
        final Node<T> first = list.firstNode();
        if (first != null && removeEldestEntry(first)) {
            map.removeKey(first.key);
            list.remove(first);
        }
    }

    /**
     * An interface for objects which are map entries in the LongLinkedHashMap.
     *
     * @param <T> The type of value contained in the map.
     */
    public interface MapEntry<T> {
        long getKey();

        T getValue();

        T setValue(T newValue);
    }


    /**
     * An iterator over the map entries in the LongLinkedHashMap.
     */
    private class MapEntryIterator implements Iterator<MapEntry<T>> {

        private Node<T> current;

        public MapEntryIterator() {
            current = LongLinkedHashMap.this.list.tail;
        }

        @Override
        public boolean hasNext() {
            return current.next != null;
        }

        @Override
        public MapEntry<T> next() {
            if (current.next != null) {
                current = current.next;
                return current;
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            if (current.previous != null) {  // the tail node can't be removed.
                LongLinkedHashMap.this.remove(current.key);
            }
        }
    }


    /**
     * Simple double linked list to use in the linked hash map.
     *
     * @param <T> The type of item stored in the LongLinkedHashMap.
     */
    private static final class DoubleLinkedList<T> {
        private final Node<T> tail = new Node<T>();
        private Node<T> head;
        private int size;

        public DoubleLinkedList() {
            head = tail;
        }

        public boolean equals(Object o) {
            if (o instanceof DoubleLinkedList) {
                final DoubleLinkedList other = (DoubleLinkedList) o;
                if (size == other.size) {
                    Node otherNode = other.tail.next;
                    for (Node<T> node = tail.next; node != null; node = node.next) {
                        if (!node.equals(otherNode)) {
                            return false;
                        }
                        otherNode = otherNode.next;
                    }
                    return true;
                }
            }
            return false;
        }

        public int size() {
            return size;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        public void clear() {
            tail.next = null;
            head = tail;
            size = 0;
        }

        public boolean contains(T item) {
            for (Node<T> node = tail.next; node != null; node = node.next) {
                if (node.item.equals(item)) {
                    return true;
                }
            }
            return false;
        }

        public Node<T> add(T item, long key) {
            return add(new Node<T>(item, key));
        }

        @SuppressWarnings("ObjectEquality")
        public T remove(Node<T> node) {
            if (node != null) {
                node.previous.next = node.next;
                if (node.next != null) {
                    node.next.previous = node.previous;
                }
                if (node == head) {
                    head = node.previous;
                }
                node.previous = null;
                node.next = null;
                size--;
                return node.item;
            }
            return null;
        }

        @SuppressWarnings("ObjectEquality")
        public void moveToHead(Node<T> node) {
            if (node != head) {
                remove(node);
                add(node);
            }
        }

        public Node<T> firstNode() {
            return tail.next;
        }

        private Node<T> add(Node<T> node) {
            head.next = node;
            node.previous = head;
            head = node;
            size++;
            return node;
        }

    }


    /**
     * Simple node to use in the double linked list and associated hash map.
     * It implements MapEntry, so we can use it directly to iterate over the LongLinkedHashMap.
     *
     * @param <T> The type of item stored in the LongLinkedHashMap.
     */
    private static final class Node<T> implements MapEntry<T> {
        private T       item;
        private long    key;
        private Node<T> previous;
        private Node<T> next;

        public Node() {}

        public Node(T item, long key) {
            this.item = item;
            this.key  = key;
        }

        public boolean equals(Node otherNode) {
            return key != otherNode.key || !item.equals(otherNode.item);
        }

        @Override
        public long getKey() {
            return key;
        }

        @Override
        public T getValue() {
            return item;
        }

        @Override
        public T setValue(T newValue) {
            final T lastValue = item;
            item = newValue;
            return lastValue;
        }
    }


}
