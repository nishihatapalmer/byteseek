/*
 * Copyright Matt Palmer 2011, All rights reserved.
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
 * 
 */

package net.domesdaybook.bytes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This class iterates through all the permutations of byte strings which can be 
 * produced from a list of byte arrays.  For example, given three arrays: 
 * <code>{2},  {3, 4, 5}, {6, 7}</code>, we get the following permutations:
 * <ul>
 * <li>{2, 3, 6}
 * <li>{2, 3, 7}
 * <li>{2, 4, 6}
 * <li>{2, 4, 7}
 * <li>{2, 5, 6}
 * <li>{2, 5, 7}
 * </ul>
 * It is not thread-safe, as it maintains state as it iterates.
 * In addition, for efficiency the byte array returned by next() is always the 
 * same underlying byte array, so each call to next() modifies the values in it.
 * Do not rely on the array returned by next() remaining the same across iterations.
 * If you need to maintain access to the permutation values, you should copy the
 * array returned by next().
 * 
 * @author Matt Palmer
 */
public class BytePermutationIterator implements Iterator<byte[]> {

    private final List<byte[]> byteArrays;
    private final int[] permutationState;
    private final int length;
    private final byte[] permutation;

    
    /**
     * Constructor for the iterator.
     * 
     * @param byteArrays The list of byte arrays to produce permutations for
     * @throws IllegalArgumentException if either the list of arrays is null, or
     *         any of the byte arrays in the list are null or empty.
     */
    public BytePermutationIterator(final List<byte[]> byteArrays) {
        if (byteArrays == null) {
            throw new IllegalArgumentException("Null byteArrays passed in to PermutationIterator.");
        }
        this.byteArrays = new ArrayList<byte[]>(byteArrays);
        for (final byte[] array : this.byteArrays) {
            if (array == null || array.length == 0) {
                throw new IllegalArgumentException("Null or empty byte array passed in to PermutationIterator.");
            }
        }
        this.length = byteArrays.size();
        this.permutationState = new int[length];
        this.permutation = new byte[length];
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        return permutationState[0] < byteArrays.get(0).length;
    }

    
    /**
     * Returns the next permutation of the list of byte arrays as a byte array.
     * <p>
     * Note: the values of the byte array returned are correct in this iteration.
     * However, it is always the same underlying byte array. If you need a record
     * of the byte arrays returned, you must copy them into new ones.
     * 
     * @throws NoSuchElementException if there are no more permutations.
     */
    @Override
    public byte[] next() {
        if (hasNext()) {
            buildCurrentPermutation();
            buildNextPermutationState();
            return permutation.clone();
        } else {
            throw new NoSuchElementException("No more permutations available for the byte arrays.");
        }
    }

    
    /**
     * The remove operation is unsupported in the byte permutation iterator, as it
     * is not possible to remove logical permutations!
     * 
     * @throws UnsupportedOperationException if this method is called.
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Permutation iterator cannot remove generated permutations.");
    }

    
    private void buildCurrentPermutation() {
        final byte[] localperm = permutation;
        for (int arrayIndex = 0; arrayIndex < length; arrayIndex++) {
            final int permutationIndex = permutationState[arrayIndex];
            final byte[] array = byteArrays.get(arrayIndex);
            localperm[arrayIndex] = array[permutationIndex];
        }
    }

    
    private void buildNextPermutationState() {
        boolean finished = false;
        int stateIndex = length - 1;
        while (!finished) {
            final byte[] array = byteArrays.get(stateIndex);

            // Get a next possible state of the permutation:
            final int state = permutationState[stateIndex] + 1;
            
            // We're now done if there are still more bytes to process in the current
            // state, or if we're at the first state (can't go back any more states)
            finished = state < array.length || stateIndex == 0;
            
            // If we're done, set the current permutation state to our new state:
            if (finished) {
                permutationState[stateIndex] = state;
            } else { 
                // We overflowed the current state - reset it back to zero and
                // go back a state to try again.
                permutationState[stateIndex] = 0;
                stateIndex--;
            }
        }
    }
}
