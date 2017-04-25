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

import net.byteseek.utils.ArgUtils;

import java.util.Arrays;

/**
 * A very simple hash map using primitive longs as keys against objects.
 * The goal of the map is to efficiently store objects against a long value while
 * minimising the amount of garbage produced.
 * <p>
 * It uses open addressing to avoid creating additional objects, and only accepts zero or positive
 * longs as keys.  Negative values are used internally to indicate whether the slot is free or removed.
 * This clearly won't work for some use-cases, but it fits byteseek very well.
 *
 * Created by matt on 24/04/17.
 */
public final class PositionHashMap<T> {

    private final static int DEFAULT_CAPACITY = 32; // default capacity under a load of 50%.
    private final static int FREE_SLOT        = -1; // flags that a slot is free for a value.
    private final static int REMOVED_SLOT     = -2; // flags that a slot used to contain a value, so keep looking.

    private long[]  keys;
    private T[]     values;
    private int     size;

    // TODO: use a more sophisticated strategy than simple linear probing.

    public PositionHashMap() {
        this(DEFAULT_CAPACITY);
    }

    public PositionHashMap(final int initialCapacity) {
        final int initialSize = initialCapacity * 2; // 50% load at initial capacity. //TODO: adjust once profiled.
        keys   = new long[initialSize];
        Arrays.fill(keys, FREE_SLOT);
        values = (T[]) new Object[initialSize];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public T get(final long key) {
        final long[]       localKeys   = keys;   // fetch members once.
        final int length = localKeys.length;

        // TODO: hash function, remember our keys are generally powers of two.
        final int hash  = (int) (key & 0x7FFFFFFF);
        final int index = hash % length;

        // Probe the hashmap for our key from the index up to the length:
        for (int probeIndex = index; probeIndex < length; probeIndex++) {
            final long keyState = localKeys[probeIndex];
            if (keyState == key) {
                return values[index];
            }
            if (keyState == FREE_SLOT) { // can be no free slots before we find the key.
                return null;
            }
        }

        // Probe the remaining values from below the index down to zero:
        for (int probeIndex = index - 1; probeIndex >= 0; probeIndex--) {
            final long keyState = localKeys[probeIndex];
            if (keyState == key) {
                return values[index];
            }
            if (keyState == FREE_SLOT) { // can be no free slots before we find the key.
                return null;
            }
        }

        // Not found - return null.
        return null;
    }

    public boolean put(final long key, final T value) {
        resizeIfNeeded();

        final long[] localKeys = keys;   // fetch members once.
        final int    length    = localKeys.length;

        // TODO: hash function, remember our keys are generally powers of two.
        final int hash  = (int) (key & 0x7FFFFFFF);
        final int index = hash % length;

        // Probe the hashmap for our key from the index up to the length:
        for (int probeIndex = index; probeIndex < length; probeIndex++) {
            final long keyState = localKeys[probeIndex];
            if (keyState < 0) { // FREE or REMOVED - put our key/value pair in.
                keys[probeIndex] = key;
                values[probeIndex] = value;
                size++;
                return true;
            }
            if (keyState == key) { // already have key - update value.
                values[probeIndex] = value;
                return true;
            }
        }

        // Probe the remaining keys from zero up to the original index:
        for (int probeIndex = index - 1; probeIndex >= 0; probeIndex--) {
            final long keyState = localKeys[probeIndex];
            if (keyState < 0) { // FREE or REMOVED - put our key/value pair in.
                keys[probeIndex] = key;
                values[probeIndex] = value;
                size++;
                return true;
            }
            if (keyState == key) { // already have key - update value.
                values[probeIndex] = value;
                return true;
            }
        }

        // Could not find a place to put - this should not happen, there should always be free states if we resize.
        return false;
    }

    public boolean remove(final long key) {
        final long[]       localKeys   = keys;   // fetch members once.
        final int length = localKeys.length;

        // TODO: hash function, remember our keys are generally powers of two.
        final int hash  = (int) (key & 0x7FFFFFFF);
        int index = hash % length;

        // Probe the hashmap for our key from the index up to the length:
        for (int probeIndex = index; probeIndex < length; probeIndex++) {
            final long keyState = localKeys[probeIndex];
            if (keyState == key) { // found our key.
                localKeys[probeIndex] = REMOVED_SLOT;
                values[probeIndex]    = null; // avoid memory leak.
                size--;
                return true;
            }
            if (keyState == FREE_SLOT) { // key didn't exist.
                return false;
            }
        }

        // Probe the remaining keys from under the original index down to zero:
        for (int probeIndex = index - 1; probeIndex >= 0; probeIndex--) {
            final long keyState = localKeys[probeIndex];
            if (keyState == key) { // found our key.
                localKeys[probeIndex] = REMOVED_SLOT;
                values[probeIndex]    = null; // avoid memory leak.
                size--;
                return true;
            }
            if (keyState == FREE_SLOT) { // key didn't exist.
                return false;
            }
        }

        // Not found - nothing to remove.
        return false;
    }

    public void clear() {
        Arrays.fill(keys, FREE_SLOT);
        Arrays.fill(values, null);
        size = 0;
    }

    private void resizeIfNeeded() {
        final int length = keys.length;
        if (size * 2 > length) { // if we go over 50% load, resize - Wasteful of memory.
            final int    newSize   = length * 2; // double size each time...?  Wasteful of memory.
            final long[] oldKeys   = keys;
            final T[]    oldValues = values;
            final long[] newKeys   = new long[newSize];
            final T[]    newValues = (T[]) new Object[newSize];

            // Set up new keys and values, iterating over old ones to rehash them:
            Arrays.fill(newKeys, FREE_SLOT);
            for (int oldIndex = 0; oldIndex < length; oldIndex++) {
                final long currentKey = oldKeys[oldIndex];

                UPDATE: if (currentKey >= 0) {                  // if there's a key here:
                    final T currentValue = oldValues[oldIndex]; // get the value for it.

                    // Get the new hash index for the key:
                    // TODO: hash function, remember our keys are generally powers of two.
                    final int hash  = (int) (currentKey & 0x7FFFFFFF);
                    final int newIndex = hash % newSize;

                    // Find a place to put it, from the index up to the new size:
                    for (int probeIndex = newIndex; newIndex < newSize; probeIndex++) {
                        final long newKeyState = newKeys[probeIndex];
                        if (newKeyState == FREE_SLOT) { // put our key/value pair in.
                            newKeys[probeIndex]   = currentKey;
                            newValues[probeIndex] = currentValue;
                            break UPDATE; // finished updating this key.
                        }
                    }

                    // Find a place to put it, from past index down to zero :
                    for (int probeIndex = newIndex - 1; newIndex >= 0; probeIndex--) {
                        final long newKeyState = newKeys[probeIndex];
                        if (newKeyState == FREE_SLOT) { // put our key/value pair in.
                            newKeys[probeIndex]   = currentKey;
                            newValues[probeIndex] = currentValue;
                            break UPDATE; // finished updating this key.
                        }
                    }
                }
            }

            // Set the new keys and values.
            keys   = newKeys;
            values = newValues;
        }
    }


}
