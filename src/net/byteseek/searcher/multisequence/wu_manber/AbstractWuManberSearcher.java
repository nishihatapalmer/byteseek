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
package net.byteseek.searcher.multisequence.wu_manber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.byteseek.bytes.BytePermutationIterator;
import net.byteseek.bytes.ByteUtils;
import net.byteseek.matcher.bytes.ByteMatcher;
import net.byteseek.matcher.multisequence.HashMultiSequenceMatcher;
import net.byteseek.matcher.multisequence.MultiSequenceMatcher;
import net.byteseek.matcher.multisequence.MultiSequenceReverseMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.object.factory.DoubleCheckImmutableLazyObject;
import net.byteseek.object.factory.LazyObject;
import net.byteseek.object.factory.ObjectFactory;
import net.byteseek.searcher.multisequence.AbstractMultiSequenceSearcher;
import net.byteseek.searcher.multisequence.set_horspool.SetHorspoolSearcher;

/**
 * This abstract class calculates the search information for a Wu-Manber style
 * multi-sequence searcher.  It extends {@link AbstractMultiSequenceSearcher},
 * taking a {@link MultiSequenceMatcher} containing the sequences to search for,
 * and which provides the matching capability to verify a match.
 * <p>
 * A true Wu-Manber style search would use the {@link HashMultiSequenceMatcher}
 * class as its matcher, which has a good time-space trade-off.  However, you can 
 * use any MultiSequenceMatcher in this searcher, for different trade-offs.
 * <p>
 * This style of search is very fast for large numbers of sequences, although its
 * speed is heavily constrained by the minimum length of the sequences.  Very short
 * sequences slow it down a lot, as it can only skip ahead in the search by at most
 * the minimum length of all the sequences (or it might skip over a possible short match).
 * <p>
 * It is similar in principle to the {@link SetHorspoolSearcher}, in that it calculates
 * a table of safe shifts it can make working from the ends of the sequences (not the start).
 * It scans along, shifting across the WindowReader or byte array until it finds a shift of zero, 
 * when it has to verify whether there is genuinely a match at that position.
 * <p>
 * However, it performs much better for large numbers of sequences than Set Horspool.
 * Set Horspool suffers with large numbers of sequences, because as the number of 
 * sequences to be matched rises, the chances that any given byte value will exist
 * near the end in one of them approaches 100%, making the effective shifts
 * very small (or just one - the smallest safe shift possible).  Wu Manber avoids this
 * fate by working on blocks of bytes, making the effective alphabet much bigger and
 * reducing the chances of a collision in the shift table.
 * <p>
 * To avoid having to have a huge shift table, it uses a hash table to store its shifts,
 * which can be tuned to be any size (trading off speed against space).  When Wu-Manber
 * is used with a block size of only one byte, then it is effectively equivalent to the
 * Set Horspool searcher.  With larger block sizes, the hash table is bigger than that of
 * Set Horspool, so again there is a time-space trade-off.
 * <p>
 * The concrete sub-classes of this abstract class implement the Wu-Manber search algorithm
 * for different block sizes, in order to make each variation as efficient as possible:
 * <ul>
 * <li>A block size of one {@link WuManberOneByteSearcher}
 * <li>A block size of two {@link WuManberTwoByteSearcher}
 * <li>Block sizes greater than two {@link WuManberMultiByteSearcher} - NOT YET FULLY IMPLEMENTED.
 * </ul>
 * You can use the utility methods defined in {@link WuManberUtils} to determine
 * an appropriate block size to use.
 * 
 * @see <a href="http://webglimpse.net/pubs/TR94-17.pdf">Wu-Manber paper (PDF)</a>
 * @author Matt Palmer
 */
public abstract class AbstractWuManberSearcher extends AbstractMultiSequenceSearcher {
        
    private static int HIGHEST_POWER_OF_TWO = 1073741824;

    /**
     * A class holding the search information used in the Wu-Manber search.
     */
    protected static final class SearchInfo {
        
        /**
         * The hash-table of safe shifts.  This table will be constructed
         * to be a power of two, making the hash calculation easy by masking
         * the hash values by length - 1.  A shift of zero in this table
         * indicates that we may have a potential match.
         */
        public final int[] shifts;
        
        /**
         * The MultiSequenceMatcher to use to verify a match.
         */
        public final MultiSequenceMatcher matcher;

        
        /**
         * Constructs a SearchInfo objecxt with the shifts and matcher to use
         * when searching.
         * 
         * @param shifts The hash-table containing the safe shifts.
         * @param matcher The matcher to use to verify whether a match exists.
         */
        public SearchInfo(final int[] shifts, final MultiSequenceMatcher matcher) {
            this.shifts = shifts;
            this.matcher = matcher;
        }
    }

    /**
     * The block size to use in the Wu-Manber search.
     */
    protected final int blockSize;
    
    
    /**
     * A factory for a lazily instantiated SearchInfo object containing the 
     * information needed to search forwards.
     */
    protected final LazyObject<SearchInfo> forwardInfo;
    
    
    /**
     * A factory for a lazily instantiated SearchInfo object containing the 
     * information needed to search backwards.
     */
    protected final LazyObject<SearchInfo> backwardInfo;

    
    /**
     * Constructs an abstract WuManberSearcher from a {@link MultiSequenceMatcher} and 
     * a block size.
     * 
     * @param matcher A MultiSequenceMatcher containing the sequences to search for.
     * @param blockSize The block size of the Wu-Manber searcher.
     */
    public AbstractWuManberSearcher(final MultiSequenceMatcher matcher, final int blockSize) {
        super(matcher);
        this.blockSize = blockSize;
        forwardInfo  = new DoubleCheckImmutableLazyObject<SearchInfo>(new ForwardInfoFactory());
        backwardInfo = new DoubleCheckImmutableLazyObject<SearchInfo>(new BackwardSearchInfo());
    }

    
    /**
     * Forces the initialisation of the forward search info.
     */
    @Override
    public void prepareForwards() {
        forwardInfo.get();
    }

    
    /**
     * Forces the initialisation of the backward search info.
     */
    @Override
    public void prepareBackwards() {
        backwardInfo.get();
    }


    /**
     * For a given SequenceMatcher, builds a list of the byte values for a block.
     * 
     * @param position The end of the block to get the byte values for.
     * @param matcher The SequenceMatcher to build the list of byte arrays for.
     * @return A list of byte arrays containing the matching byte values for a block in the SequenceMatcher.
     */
    private List<byte[]> getBlockByteList(final int position, final SequenceMatcher matcher) {
        final List<byte[]> byteList = new ArrayList<byte[]>(blockSize);
        for (int blockIndex = position - blockSize + 1; blockIndex <= position; blockIndex++) {
            final ByteMatcher byteMatcher = matcher.getMatcherForPosition(blockIndex);
            byteList.add(byteMatcher.getMatchingBytes());
        }
        return byteList;
    }


    /**
     * Given a block as a byte array, calculate its block hash value.
     * 
     * @param block The block to calculate a hash value for.
     * @return The hash value of a block.
     */
    private static int getBlockHash(final byte[] block) {
        int hashCode = 0;
        for (final byte b : block) {
            // left shift 5 - original value = (x * 32) - x = x * 31.
            hashCode = (hashCode << 5) - hashCode + (b & 0xFF);
        }
        return hashCode;
    }


    /**
     * Creates a hash table of the optimum size and fills it with the default shift value
     * 
     * @param defaultShift The default shift to use (the minimum length of all the sequences).
     * @return A shift table filled with the default shift value.
     */
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
        final int possibleSize = ByteUtils.isPowerOfTwo(positiveSize)?
                  positiveSize : ByteUtils.nextHighestPowerOfTwo(positiveSize);
        final int maxSize = getMaxTableSize();
        return possibleSize < maxSize?
               possibleSize : maxSize;
    }


    /**
     * Returns the maximum possible table size for a given block size.
     * For block sizes greater than three, default to the highest positive
     * power of two possible in an integer.
     * 
     * @return int The maximum possible table size for a given block size.
     */
    private int getMaxTableSize() {
        switch (blockSize) {
            case 1: return 256;       // 2 ^ 8
            case 2: return 65536;     // 2 ^ 16
            case 3: return 16777216;  // 2 ^ 24
        }
        return HIGHEST_POWER_OF_TWO;
    }


    /**
     * A class implemented ObjectFactory<SearchInfo>, which calculates the shift
     * and matcher to use when searching forwards with the Wu-Manber search
     * algorithm.
     */
    protected class ForwardInfoFactory implements ObjectFactory<SearchInfo> {

        /**
         * Creates and returns the forward search information.
         * 
         * @return SearchInfo The information needed to search forwards.
         */
        @Override
        public SearchInfo create() {
            return new SearchInfo(getShifts(), getMatcher());
        }

        /**
         * Creates the safe shifts for forward searching 
         * 
         * @return int[] the safe shifts for forward searching.
         */
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

        
        /**
         * Creates the MultiSequenceMatcher used for forward searching.
         * When searching forwards, we proceed from the right-aligned end of all
         * the sequences, so we use a {@link MultiSequenceReverseMatcher} to
         * match backwards from the ends of all the sequences, translating any 
         * matches back into the original sequences which would have matched forwards.
         * 
         * @return A MultiSequenceReverseMatcher created from the original sequences.
         */
        private MultiSequenceMatcher getMatcher() {
            return new MultiSequenceReverseMatcher(sequences);
        }

    }


    /**
     * A class implementing ObjectFactory<SearchInfo>, which calculates the shift
     * and matcher to use when searching backwards with the Wu-Manber search
     * algorithm.
     */
    protected class BackwardSearchInfo implements ObjectFactory<SearchInfo> {

        /**
         * Creates and returns the backward search information.
         * 
         * @return SearchInfo The information needed to search backwards.
         */
        @Override
        public SearchInfo create() {
            return new SearchInfo(getShifts(), getMatcher());
        }

        
        /**
         * Creates the safe shifts for backward searching 
         * 
         * @return int[] the safe shifts for backward searching.
         */        
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

        /**
         * When searching backwards, searching proceeds from the start of the 
         * sequences aligned to the left, so the original MultiSequenceMatcher
         * provided to this abstract parent class on construction will work fine.
         * 
         * @return The original MultiSequenceMatcher provided on construction of the parent class.
         */
        private MultiSequenceMatcher getMatcher() {
            return sequences;
        }
    }
        
}
