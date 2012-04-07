/*
 * Copyright Matt Palmer 2012, All rights reserved.
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
package net.domesdaybook.searcher.multisequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.domesdaybook.matcher.bytes.ByteMatcher;
import net.domesdaybook.matcher.multisequence.MultiSequenceMatcher;
import net.domesdaybook.matcher.multisequence.MultiSequenceReverseMatcher;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.util.bytes.BytePermutationIterator;
import net.domesdaybook.util.bytes.ByteUtilities;
import net.domesdaybook.util.object.LazyObject;

/**
 *
 * @author Matt Palmer
 */
public abstract class AbstractWuManberSearcher extends AbstractMultiSequenceSearcher {
        
    private static int HIGHEST_POWER_OF_TWO = 1073741824;

    protected static final class SearchInfo {
        public int[] shifts;
        public MultiSequenceMatcher matcher;

        public SearchInfo(final int[] shifts, final MultiSequenceMatcher matcher) {
            this.shifts = shifts;
            this.matcher = matcher;
        }
    }

    protected final int blockSize;
    protected final LazyObject<SearchInfo> forwardInfo;
    protected final LazyObject<SearchInfo> backwardInfo;

    public AbstractWuManberSearcher(final MultiSequenceMatcher matcher, final int blockSize) {
        super(matcher);
        this.blockSize = blockSize;
        forwardInfo = new ForwardSearchInfo();
        backwardInfo = new BackwardSearchInfo();
    }

    public void prepareForwards() {
        forwardInfo.get();
    }

    public void prepareBackwards() {
        backwardInfo.get();
    }


    private List<byte[]> getBlockByteList(final int position, final SequenceMatcher matcher) {
        final List<byte[]> byteList = new ArrayList<byte[]>(blockSize);
        for (int blockIndex = position - blockSize + 1; blockIndex <= position; blockIndex++) {
            final ByteMatcher byteMatcher = matcher.getMatcherForPosition(blockIndex);
            byteList.add(byteMatcher.getMatchingBytes());
        }
        return byteList;
    }


    private static int getBlockHash(final byte[] block) {
        int hashCode = 0;
        for (final byte b : block) {
            // left shift 5 - original value = (x * 32) - x = x * 31.
            hashCode = (hashCode << 5) - hashCode + (b & 0xFF);
        }
        return hashCode;
    }


    private int[] createShiftHashTable(final int defaultShift) {
        final int possibleTableSize = guessTableSize();
        final int optimumTableSize = chooseOptimumSize(possibleTableSize);
        final int[] shifts = new int[optimumTableSize];
        Arrays.fill(shifts, defaultShift);  
        return shifts;
    }


    /**
     * This function is pure guesswork at present. 
     * 
     * We will be hashing a block of data, which could be one, two, three or
     * more bytes into a fixed table.  
     * 
     * We rely on collisions in this table to minimise the size of the table we need,
     * although each collision can reduce the possible shift for that cell.  
     * 
     * Ideally, we don't want too many cells with zero in them either. Zero means
     * that an end of a sequence hashes to that value, so we have to verify whether
     * or not a pattern matches, and we can't shift more than one afterwards. 
     * 
     * A zero occurs for the end block of each sequence, so the number of cells with
     * zero in them rises with the number of sequences (although not exactly, as some
     * will hash to the same value and some may be the same as each other).
     * 
     * Assume we want no more than 1 in 16 cells with zero in them, and
     * for low numbers of sequences we still want a table of at least 256 elements.
     * 
     * However, I think this reasoning is suspect - it's ignoring the collisions
     * somehow.  How many end sequences will probably collide...?
     * Need to profile performance given different table sizes and investigate
     * hash functions.
     * 
     * @return a pure guess at an optimal table size,
     */
    private int guessTableSize() {
        // Take a guess - we probably don't want a table of 65,536 values
        // or greater under normal circumstances...
        // It's not really the number of sequences... it's the number of permutations
        // of the end byte blocks which affect how many hash table entries you 
        // really want. Using the number of sequences is a sort of proxy for this
        // as most sequences *probably* won't have large byte classes at their end.
        // Probably.
        return 128 + (sequences.getSequenceMatchers().size() * 16);
    }


    /**
     * Picks a hash table size which is a power of two. 
     * <p>
     * Ensures that the hash table is at least 1 in size, and that it is 
     * not higher than a hash table containing all possible byte values for
     * the number of bytes in a block.  For example, if the block size is 2,
     * there is no point having a hash table size greater than 65536, as that
     * has an entry for each unique value of a block.
     * 
     * @param suggestedSize The size of hash table suggested.
     * @return An optimum hash table size for the suggested size.
     */
    private int chooseOptimumSize(final int suggestedSize) {
        final int positiveSize = suggestedSize > 1?
                                 suggestedSize : 1;
        final int possibleSize = ByteUtilities.isPowerOfTwo(positiveSize)?
                  positiveSize : ByteUtilities.nextHighestPowerOfTwo(positiveSize);
        final int maxSize = getMaxTableSize();
        return possibleSize < maxSize?
               possibleSize : maxSize;
    }


    private int getMaxTableSize() {
        switch (blockSize) {
            case 1: return 256;
            case 2: return 65536;   
            case 3: return 16777216;
        }
        return HIGHEST_POWER_OF_TWO;
    }


    protected class ForwardSearchInfo extends LazyObject<SearchInfo> {

        @Override
        protected SearchInfo create() {
            return new SearchInfo(getShifts(), getMatcher());
        }

        private int[] getShifts() {
            final int defaultShift = sequences.getMinimumLength() - blockSize + 1;        
            final int[] shifts = createShiftHashTable(defaultShift);
            // (relies on shifts being a size which is a power of two):
            final int hashBitMask = shifts.length - 1; 

            // For each sequence in our list:
            for (final SequenceMatcher sequence : sequences.getSequenceMatchers()) {
                final int matcherLength = sequence.length();

                // For each block up to the end of the sequence, starting
                // the minimum length of all sequences back from the end.
                final int firstBlockEndPosition = matcherLength - sequences.getMinimumLength() + blockSize - 1; 
                for (int blockEndPosition = firstBlockEndPosition; blockEndPosition < matcherLength; blockEndPosition++) {
                    final int distanceFromEnd = matcherLength - blockEndPosition - 1;

                    // For each possible permutation of bytes in a block:
                    final List<byte[]> blockBytes = getBlockByteList(blockEndPosition, sequence);
                    final BytePermutationIterator permutation = new BytePermutationIterator(blockBytes);
                    while (permutation.hasNext()) {

                        // Set the shift for the hash position of this permutation to be 
                        // the smaller of the existing shift and current distance from the end:
                        final int hashPos = getBlockHash(permutation.next()) & hashBitMask;
                        final int currentShift = shifts[hashPos];
                        if (distanceFromEnd < currentShift) {
                            shifts[hashPos] = distanceFromEnd;
                        }
                    }
                }
            }
            return shifts;
        }

        private MultiSequenceMatcher getMatcher() {
            return new MultiSequenceReverseMatcher(sequences);
        }

    }


    protected class BackwardSearchInfo extends LazyObject<SearchInfo> {

        @Override
        protected SearchInfo create() {
            return new SearchInfo(getShifts(), getMatcher());
        }

        private int[] getShifts() {
            final int minLength = sequences.getMinimumLength();
            final int defaultShift = minLength - blockSize + 1;        
            final int[] shifts = createShiftHashTable(defaultShift);
            // (relies on shifts being a size which is a power of two):
            final int hashBitMask = shifts.length - 1; 

            // For each sequence in our list:
            for (final SequenceMatcher sequence : sequences.getSequenceMatchers()) {

                // For each block up to the minimum length of all sequences:
                for (int blockEndPosition = blockSize - 1; 
                         blockEndPosition < minLength; blockEndPosition++) {

                    final int distanceToStart = blockEndPosition - blockSize + 1;
                    // For each possible permutation of bytes in a block:
                    final List<byte[]> blockBytes = getBlockByteList(blockEndPosition, sequence);
                    final BytePermutationIterator permutation = new BytePermutationIterator(blockBytes);
                    while (permutation.hasNext()) {

                        // Set the shift for the hash position of this permutation to be 
                        // the smaller of the current shift and the distance from the end:
                        final int hashPos = getBlockHash(permutation.next()) & hashBitMask;
                        final int currentShift = shifts[hashPos];
                        if (distanceToStart < currentShift) {
                            shifts[hashPos] = distanceToStart;
                        }
                    }
                }
            }
            return shifts;
        }

        private MultiSequenceMatcher getMatcher() {
            return sequences;
        }
    }
        
}
