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
package net.byteseek.searcher.sequence;

import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.utils.MathUtils;
import net.byteseek.utils.PowerTwoSize;
import net.byteseek.utils.collections.BytePermutationIterator;

/**
 * An abstract class providing common constants, methods and classes for search algorithms
 * which operate on q-grams rather than individual bytes, and which use the shift-add
 * hash algorithm.  It also allows for specifying the minimum and maximum size of the hash tables used
 * by the q-gram hashing algorithm, as a power of two.
 *
 * Created by matt on 09/06/17.
 */
public abstract class AbstractQgramSearcher extends AbstractFallbackSearcher {

    /**
     * Constants for default minimum and maximum index table sizes, for use by subclasses if required.
     */
    protected final static PowerTwoSize DEFAULT_MIN_INDEX_SIZE = PowerTwoSize.SIZE_32;  //PROFILE: profile defaults.
    protected final static PowerTwoSize DEFAULT_MAX_INDEX_SIZE = PowerTwoSize.SIZE_64K; //PROFILE: profile defaults.

    /**
     * The actual minimum and maximum index table size.
     */
    protected final PowerTwoSize minIndexSize;
    protected final PowerTwoSize maxIndexSize;

    /**
     * Constructor for the AbstractQgramSearcher, taking the sequence to search, and a minimum and maximum size
     * for the search index table.
     *
     * @param sequence     The sequence to search for.
     * @param minIndexSize The minimum number of elements in the search index table.
     * @param maxIndexSize The maximum number of elements in the search index table.
     */
    public AbstractQgramSearcher(final SequenceMatcher sequence, final PowerTwoSize minIndexSize, final PowerTwoSize maxIndexSize) {
        super(sequence);
        this.minIndexSize = minIndexSize;
        this.maxIndexSize = maxIndexSize;
    }

    /**
     * Determines the final size of the hash table, given the total qgrams processed, and the search index size method.
     * <p>
     * NOTE: this may vary by algorithm?  We just pick the table which is bigger than the total qgrams.
     *       different algorithms may need different hash table sizes...?
     *
     * @param totalQgrams The total qgrams which the hash table will need to support.
     * @return The size of the hash table, as a power of two.
     */
    protected int getTableSize(final int totalQgrams) {
        // Determine final size of hash table:
        final int MAX_POWER_TWO_SIZE = maxIndexSize.getPowerTwo();
        final int HASH_POWER_TWO_SIZE;
        if (minIndexSize == maxIndexSize) {                // specified by user - must use this size exactly.
            HASH_POWER_TWO_SIZE = MAX_POWER_TWO_SIZE; // total qgram processing above still useful to avoid pathological byte classes (qGramStartPos).
        } else {
            //PROFILE: Profile different values here. What effective margin do we want?  Plus one gives a good result - is plus 2 worth it?
            //TODO: Possibly allow different space/time tradeoffs (-1 = low mem, 0 = small mem, +1 = good perf, +2 = ???
            final int qGramPowerTwoSize = 1 + MathUtils.ceilLogBaseTwo(totalQgrams); // the power of two size bigger or equal to total qgrams.
            final int MIN_POWER_TWO_SIZE = minIndexSize.getPowerTwo();
            HASH_POWER_TWO_SIZE = MAX_POWER_TWO_SIZE < qGramPowerTwoSize ?
                                  MAX_POWER_TWO_SIZE : qGramPowerTwoSize > MIN_POWER_TWO_SIZE ? // but not bigger than the maximum allowed,
                                                       qGramPowerTwoSize : MIN_POWER_TWO_SIZE;  // and not smaller than the minimum allowed.
        }
        return 1 << HASH_POWER_TWO_SIZE;
    }

    /**
     * Returns a shift for the shift-add hash function given a table size and q-gram length.
     * It will return the shift which gives the same size or bigger than the table size specified,
     * up to a maximum table size or shift.
     *
     * @param hashTableSize The size of the hash table to be used.
     * @param qGramLength   The length of the q-grams being hashed.
     * @return The bit-shift to use with the shift-add hash algorithm for the given table size.
     */
    protected int getHashShift(final int hashTableSize, final int qGramLength) {
        final int MAX_SHIFT = 10; // TODO: any point shifting more than 8 bits...?
        final int MAX_TABLE_SIZE = maxIndexSize.getSize();
        for (int shift = 1; shift < MAX_SHIFT; shift++) {
            final int tableSize = 1 << (qGramLength * shift);
            if (tableSize >= hashTableSize) {
                return shift;
            }
            if (tableSize > MAX_TABLE_SIZE) {
                return shift - 1;
            }
        }
        return MAX_SHIFT;
    }

    /**
     * A simple data class containing the shifts for searching and the bitshift needed for the hash-multiply hash function.
     */
    protected final static class SearchInfo {
        protected final int[] table;
        protected final int shift;
        protected final int searchLength;
        protected SearchInfo(final int[] table, final int shift, final int searchLength) {
            this.table = table; // SearchInfo wraps the table created - do not defensively copy.
            this.shift = shift;
            this.searchLength = searchLength;
        }
        @Override
        public String toString() {
            return getClass().getSimpleName() + "(table size:" + (table == null? 0 : table.length) +
                                                " hash shift:" + shift +
                                                " searchLength:" + searchLength + ")";
        }
    }

    protected final static SearchInfo NO_SEARCH_INFO = new SearchInfo(null, 0, 0);

    /*
     * These should be lambdas, but our minimum java version is still at 7.
     */
    protected interface TableStrategy {
        void processTablePosition(final int[] shiftTable, final int position, final int value);
    }

    protected static final class SetValue implements TableStrategy {
        @Override
        public void processTablePosition(final int[] shiftTable, final int position, final int value) {
            shiftTable[position] = value;
        }
    }

    protected static final class OrValue implements TableStrategy {
        @Override
        public void processTablePosition(final int[] shiftTable, final int position, final int value) {
            shiftTable[position] |= value;
        }
    }

    protected static final class MakeNegative implements TableStrategy {
        @Override
        public void processTablePosition(final int[] shiftTable, final int position, final int value) {
            final int currentValue = shiftTable[position];
            if (currentValue > 0) {
                shiftTable[position] = -currentValue;
            }
        }
    }

    protected static final TableStrategy SET_VALUE     = new SetValue();
    protected static final TableStrategy OR_VALUE      = new OrValue();
    protected static final TableStrategy MAKE_NEGATIVE = new MakeNegative();

    protected int processQ2Hash(final TableStrategy strategy, final int newValue, final int[] SHIFTS,
                                final int currentHashValue, final int HASH_SHIFT,
                                final byte[] bytes0, final byte[] bytes1) {

        final int MASK = SHIFTS.length - 1;
        final long numberOfPermutations = bytes0.length * bytes1.length;
        final boolean returnHashValue;
        int hashValue = currentHashValue;
        if (numberOfPermutations == 1L) { // no permutations to worry about:
            returnHashValue = true;
            if (hashValue < 0) { // if we don't have a good last key value, calculate the first 3 elements of it:
                hashValue = bytes0[0] & 0xFF;
            }
            hashValue = (hashValue << HASH_SHIFT) + (bytes1[0] & 0xFF);
            strategy.processTablePosition(SHIFTS, hashValue & MASK, newValue);
        } else { // more than one permutation to work through.
            returnHashValue = false;
            if (hashValue >= 0) { // Then bytes1 must contain all the additional permutations - just go through them.
                hashValue = hashValue << HASH_SHIFT;
                for (final byte permutationValue : bytes1) {
                    final int permutationHash = hashValue + (permutationValue & 0xFF);
                    strategy.processTablePosition(SHIFTS, permutationHash & MASK, newValue);
                }

            } else { // permutations may exist anywhere and in more than one place, use a BytePermutationIterator:
                final BytePermutationIterator qGramPermutations = new BytePermutationIterator(bytes0, bytes1);
                while (qGramPermutations.hasNext()) {
                    // Calculate the hash value:
                    final byte[] permutationValue = qGramPermutations.next();
                    hashValue = ((permutationValue[0] & 0xFF) << HASH_SHIFT) + (permutationValue[1] & 0xFF);
                    strategy.processTablePosition(SHIFTS, hashValue & MASK, newValue);
                }
            }
        }
        return returnHashValue? hashValue & MASK : -1;
    }

    protected int processQ3Hash(final TableStrategy strategy, final int newValue, final int[] SHIFTS,
                                final int currentHashValue, final int HASH_SHIFT,
                                final byte[] bytes0, final byte[] bytes1, final byte[] bytes2) {
        final int MASK = SHIFTS.length - 1;
        final boolean returnHashValue;
        int hashValue = currentHashValue;
        // Process the qgram permutations as efficiently as possible:
        final long numberOfPermutations = bytes0.length * bytes1.length * bytes2.length;
        if (numberOfPermutations == 1L) { // no permutations to worry about:
            returnHashValue = true;
            if (hashValue < 0) { // if we don't have a good last hash value, calculate the first 2 elements of it:
                hashValue = ((bytes0[0] & 0xFF) << HASH_SHIFT) + (bytes1[0] & 0xFF);
            }
            hashValue = (hashValue << HASH_SHIFT) +(bytes2[0] & 0xFF);
            strategy.processTablePosition(SHIFTS, hashValue & MASK, newValue);
        } else { // more than one permutation to work through.
            returnHashValue = false;
            if (hashValue >= 0) { // Then bytes2 must contain all the additional permutations - just go through them.
                hashValue = hashValue << HASH_SHIFT;
                for (final byte permutationValue : bytes2) {
                    final int permutationHash = hashValue + (permutationValue & 0xFF);
                    strategy.processTablePosition(SHIFTS, permutationHash & MASK, newValue);
                }
            } else { // permutations may exist anywhere and in more than one place, use a BytePermutationIterator:
                final BytePermutationIterator qGramPermutations = new BytePermutationIterator(bytes0, bytes1, bytes2);
                while (qGramPermutations.hasNext()) {
                    // Calculate the hash value:
                    final byte[] permutationValue = qGramPermutations.next();
                    hashValue = (((permutationValue[0] & 0xFF) << HASH_SHIFT) +
                                  (permutationValue[1] & 0xFF) << HASH_SHIFT) +
                                  (permutationValue[2] & 0xFF);
                    strategy.processTablePosition(SHIFTS, hashValue & MASK, newValue);
                }
            }
        }
        return returnHashValue? (hashValue & MASK) : -1;
    }


    protected int processQ4Hash(final TableStrategy strategy, final int newValue, final int[] SHIFTS,
                                final int currentHashValue, final int HASH_SHIFT,
                                final byte[] bytes0, final byte[] bytes1, final byte[] bytes2, final byte[] bytes3) {
        final long numberOfPermutations = bytes0.length * bytes1.length * bytes2.length * bytes3.length;
        final int MASK = SHIFTS.length - 1;
        final boolean returnHashValue;
        int hashValue = currentHashValue;
        if (numberOfPermutations == 1L) { // no permutations to worry about:
            returnHashValue = true;
            if (hashValue < 0) { // if we don't have a good last key value, calculate the first 3 elements of it:
                hashValue =                             (bytes0[0] & 0xFF);
                hashValue = (hashValue << HASH_SHIFT) + (bytes1[0] & 0xFF);
                hashValue = (hashValue << HASH_SHIFT) + (bytes2[0] & 0xFF);
            }
            hashValue = ((hashValue << HASH_SHIFT) + (bytes3[0] & 0xFF));
            strategy.processTablePosition(SHIFTS, hashValue & MASK, newValue);
        } else { // more than one permutation to work through.
            returnHashValue = false; // after processing the permutations, we don't have a single last key value.
            if (hashValue >= 0) { // Then bytes3 must contain all the additional permutations - just go through them.
                hashValue = hashValue << HASH_SHIFT;
                for (final byte permutationValue : bytes3) {
                    final int permutationHash = hashValue + (permutationValue & 0xFF);
                    strategy.processTablePosition(SHIFTS, permutationHash & MASK, newValue);
                }
            } else { // permutations may exist anywhere and in more than one place, use a BytePermutationIterator:
                final BytePermutationIterator qGramPermutations = new BytePermutationIterator(bytes0, bytes1, bytes2, bytes3);
                while (qGramPermutations.hasNext()) {
                    // Calculate the hash value:
                    final byte[] permutationValue = qGramPermutations.next();
                    hashValue =                             (permutationValue[0] & 0xFF);
                    hashValue = (hashValue << HASH_SHIFT) + (permutationValue[1] & 0xFF);
                    hashValue = (hashValue << HASH_SHIFT) + (permutationValue[2] & 0xFF);
                    hashValue = (hashValue << HASH_SHIFT) + (permutationValue[3] & 0xFF);
                    strategy.processTablePosition(SHIFTS, hashValue & MASK, newValue);
                }
            }
        }
        return returnHashValue? (hashValue & MASK) : -1;
    }
}
