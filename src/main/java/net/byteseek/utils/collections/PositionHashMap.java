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

import net.byteseek.utils.ByteUtils;

import java.util.Arrays;

/**
 * A hash map using primitive longs as keys against objects.
 * The goal is to efficiently map objects against a long value while
 * minimising the amount of garbage produced.
 * <p>
 * It uses open addressing to avoid creating additional objects.  It avoids needing
 * a separate state table by using the two smallest long values as special flags -
 * so these two numbers are not valid keys.
 * This clearly won't work for general use-cases, but it fits byteseek very well.
 *
 * Created by matt on 24/04/17.
 */
public final class PositionHashMap<T> {

    // Constants:

    //TODO: constants too small?  keys of 1, 2, 3... all get index of 0.
    private final static long HASH_MULTIPLY   = 0x3f92b101a3cd91L; // must be odd number greater than 32 bits.
    //TODO: large probe values give poor cache locality - investigate using smaller probe increments / different hash for probe.
    private final static long PROBE_MULTIPLY  = 0x9b3a62c47ed203L; // must be odd number greater than 32 bits.
    private final static int DEFAULT_CAPACITY = 32;                // default capacity under a load of 50%.
    private final static int FREE_SLOT        = 0;                 // flags that a slot is free - which Java initialises arrays to.
    private final static long ZERO_REPLACE    = Long.MIN_VALUE;    // A value to replace zero with - lets us use zero to indicate a free slot.
    private final static long REMOVED_SLOT    = ZERO_REPLACE + 1;  // flags that a slot used to contain a value, so keep looking.

    // Members:

    private long[]  keys;
    private T[]     values;
    private int     size;
    private int     tablebits;

    // Constructors:

    public PositionHashMap() {
        this(DEFAULT_CAPACITY);
    }

    public PositionHashMap(final int initialCapacity) {
        tablebits = ByteUtils.ceilLogBaseTwo(initialCapacity * 2); // number of bits in a number rounded up to highest power of two size.
        final int initialSize = 1 << tablebits; // 50% load at initial capacity. //TODO: adjust once profiled.
        keys   = new long[initialSize];
        values = (T[]) new Object[initialSize];
    }

    // Public Methods:

    /**
     * Return the number of key/value pairs in the map.
     *
     * @return the number of key/value pairs in the map.
     */
    public int size() {
        return size;
    }

    /**
     * Return true if the map is empty.
     *
     * @return true if the map is empty
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the value for a given key, or null if the key isn't in the map.
     *
     * @param key The key to find a value for.
     * @return the value for a given key, or null if the key isn't in the map.
     */
    public T get(final long key) {
        final long[] localKeys = keys;
        final int HASH_SHIFT   = 64 - tablebits;
        final long KEY_VALUE = key == FREE_SLOT? ZERO_REPLACE : key; // we use zero to indicate a free slot, so key zero is replaced.

        // Use multiply-shift hashing to get index to first look at:
        final int index = (int) ((KEY_VALUE * HASH_MULTIPLY) >>> HASH_SHIFT);

        // Have we found the key, or a free slot?
        long keyState = localKeys[index];
        if (keyState == KEY_VALUE) {
            return values[index];
        }
        if (keyState == FREE_SLOT) {
            return null;
        }

        // Either we have a removed key or a different key - probe for our next possible value.
        // Probe increment cannot be zero, and must be relatively prime to table size (power of two)
        // Use double hashing to produce probe increment and OR with 1 (all odd numbers are coprime to powers of two).
        final int PROBE_INCREMENT = (int) ((KEY_VALUE * PROBE_MULTIPLY) >>> HASH_SHIFT) | 0x01;
        final int TABLE_MASK      = localKeys.length - 1;
        int probeIndex = (index + PROBE_INCREMENT) & TABLE_MASK;
        while (probeIndex != index) { // stop once we get back to our original index value.
            keyState = localKeys[probeIndex];
            if (keyState == KEY_VALUE) {
                return values[probeIndex];
            }
            if (keyState == FREE_SLOT) {
                return null;
            }
            probeIndex = (probeIndex + PROBE_INCREMENT) & TABLE_MASK;
        }

        // Went right around the map and it wasn't found:
        return null;
    }

    /**
     * Puts a value into a map against the key.
     *
     * @param key   The key to map the value against.
     * @param value The value to map to the key.
     */
    public void put(final long key, final T value) {
        resizeIfNeeded();

        final long[] localKeys = keys;
        final int HASH_SHIFT   = 64 - tablebits;
        final long KEY_VALUE   = key == FREE_SLOT? ZERO_REPLACE : key; // we use zero to indicate a free slot, so key zero is replaced.

        // Use multiply-shift hashing to get index to first look at:
        final int index = (int) ((KEY_VALUE * HASH_MULTIPLY) >>> HASH_SHIFT);

        // If there's a free or removed slot, or we found the key value, put our value in.
        long keyState = localKeys[index];
        if (keyState == FREE_SLOT || keyState == REMOVED_SLOT) {
            localKeys[index] = KEY_VALUE;
            values[index]    = value;
            size++;
            return;
        }

        // If it's the same key, update the value (but don't increase the size):
        if (keyState == KEY_VALUE) {
            values[index] = value;
            return;
        }

        // We have a different key in this slot - probe for other possible locations.
        // Probe increment cannot be zero, and must be relatively prime to table size (power of two)
        // Use double hashing to produce probe increment and OR with 1 (all odd numbers are coprime to powers of two).
        final int PROBE_INCREMENT = (int) ((KEY_VALUE * PROBE_MULTIPLY) >>> HASH_SHIFT) | 0x01;
        final int TABLE_MASK      = localKeys.length - 1;
        int probeIndex = (index + PROBE_INCREMENT) & TABLE_MASK;
        while (probeIndex != index) { // stop once we get back to our original index value.
            keyState = localKeys[probeIndex];
            if (keyState == FREE_SLOT || keyState == REMOVED_SLOT) {
                localKeys[probeIndex] = KEY_VALUE;
                values[probeIndex]    = value;
                size++;
                return;
            }
            if (keyState == KEY_VALUE) {
                values[probeIndex] = value;
                return;
            }
            probeIndex = (probeIndex + PROBE_INCREMENT) & TABLE_MASK;
        }

        // Went right around the map and couldn't place it - this should never happen if we resize appropriately.
    }

    /**
     * Removes a key/value pair from the map with the provided key.
     *
     * @param key The key to remove the key/value pair for.
     * @return true if the key was removed, false if it wasn't in the map.
     */
    public boolean remove(final long key) {
        final long[] localKeys = keys;
        final int HASH_SHIFT   = 64 - tablebits;
        final long KEY_VALUE   = key == FREE_SLOT? ZERO_REPLACE : key; // we use zero to indicate a free slot, so key zero is replaced.

        // Use multiply-shift hashing to get index to first look at:
        final int index = (int) ((KEY_VALUE * HASH_MULTIPLY) >>> HASH_SHIFT);

        long keyState = localKeys[index];
        if (keyState == KEY_VALUE) { // found our key.
            localKeys[index] = REMOVED_SLOT;
            values[index]    = null;
            size--;
            return true;
        }

        if (keyState == FREE_SLOT) { // key didn't exist
            return false;
        }

        // We have a different key - probe for our key:
        // Probe increment cannot be zero, and must be relatively prime to table size (power of two)
        // Use double hashing to produce probe increment and OR with 1 (all odd numbers are coprime to powers of two).
        final int PROBE_INCREMENT = (int) ((KEY_VALUE * PROBE_MULTIPLY) >>> HASH_SHIFT) | 0x01;
        final int TABLE_MASK      = localKeys.length - 1;
        int probeIndex = (index + PROBE_INCREMENT) & TABLE_MASK;
        while (probeIndex != index) { // stop once we get back to our original index value.
            keyState = localKeys[probeIndex];
            if (keyState == KEY_VALUE) { // found our key.
                localKeys[probeIndex] = REMOVED_SLOT;
                values[probeIndex]    = null;
                size--;
                return true;
            }

            if (keyState == FREE_SLOT) { // key didn't exist
                return false;
            }

            probeIndex = (probeIndex + PROBE_INCREMENT) & TABLE_MASK;
        }

        // Went right around the map and couldn't find it - key didn't exist.
        return false;
    }

    /**
     * Clears the map.
     */
    public void clear() {
        Arrays.fill(keys, FREE_SLOT);
        Arrays.fill(values, null);
        size = 0;
    }

    /*
     * Private methods.
     */


    private void resizeIfNeeded() {
        final int length = keys.length;
        if (size * 2 > length) { // if we go over 50% load, resize - 70% better...?
            tablebits++; // one more bit.
            final int HASH_SHIFT   = 64 - tablebits;
            final int    newSize   = length * 2; // double size each time...?  Wasteful of memory.
            final long[] oldKeys   = keys;
            final T[]    oldValues = values;
            final long[] newKeys   = new long[newSize];
            final T[]    newValues = (T[]) new Object[newSize];

            // Set up new keys and values, iterating over old ones to rehash them:
            for (int oldIndex = 0; oldIndex < length; oldIndex++) {
                final long currentKey = oldKeys[oldIndex];

                UPDATE: if (currentKey != FREE_SLOT && currentKey != REMOVED_SLOT) { // if there's a key here:

                    // Get the new index for this key:
                    final int newIndex = (int) ((currentKey * HASH_MULTIPLY) >>> HASH_SHIFT);

                    // If there's a free slot, put our key/value pair in it:
                    if (newKeys[newIndex] == FREE_SLOT) {
                        newKeys[newIndex]   = currentKey;
                        newValues[newIndex] = oldValues[oldIndex];
                        break UPDATE;
                    }

                    // Probe for a place to put it:
                    final int PROBE_INCREMENT = (int) ((currentKey * PROBE_MULTIPLY) >>> HASH_SHIFT) | 0x01;
                    final int TABLE_MASK      = newSize - 1;
                    int probeIndex = (newIndex + PROBE_INCREMENT) & TABLE_MASK;
                    while (probeIndex != newIndex) { // stop once we get back to our original index value.

                        // If there's a free slot, put our key/value pair in it:
                        if (newKeys[probeIndex] == FREE_SLOT) {
                            newKeys[probeIndex]   = currentKey;
                            newValues[probeIndex] = oldValues[oldIndex];
                            break UPDATE;
                        }

                        probeIndex = (probeIndex + PROBE_INCREMENT) & TABLE_MASK;
                    }
                }
            }

            // Set the new keys and values.
            keys   = newKeys;
            values = newValues;
        }
    }


    /*
     * Package protected methods to allow for more testing.
     */
    int getTableSize() {
        return keys.length;
    }

    int getTableBits() {
        return tablebits;
    }



}
