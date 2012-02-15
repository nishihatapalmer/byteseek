/*
 * Copyright Matt Palmer 2011-12, All rights reserved.
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

import java.io.IOException;
import net.domesdaybook.bytes.BytePermutationIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.matcher.multisequence.MultiSequenceMatcher;
import net.domesdaybook.matcher.bytes.ByteMatcher;
import net.domesdaybook.matcher.multisequence.MultiSequenceReverseMatcher;
import net.domesdaybook.object.LazyObject;
import net.domesdaybook.searcher.AbstractSearcher;
import net.domesdaybook.searcher.ResultUtils;
import net.domesdaybook.searcher.SearchResult;
import net.domesdaybook.searcher.Searcher;

/**
 * WuManberSearcher implements a variation of the classic multi-pattern
 * search algorithm invented by Wu and Manber.
 * <p/>
 * The Wu Manber multi-pattern search aims to locate a large number of
 * patterns, while remaining sub-linear, shifting over bytes which can't
 * match any of the patterns.
 * <p/>
 * Like Boyer-Moore-Horspool, it calculates a safe shift to make if there
 * is no match at the current position, which should be greater than one in
 * most cases - thus avoiding reading most of the bytes.  However, if shifts
 * are calculated for a large number of patterns (e.g. 1000 patterns), the
 * chances are that most shifts will tend towards one, losing the advantage
 * of this sort of searching. 
 * </p>The Wu Manber search gets around this limitation by matching on more than
 * one byte at a time.  It looks at blocks of bytes (effectively extending the 
 * available alphabet), and calculates a hash code for them.  It uses this hash
 * code to look up a safe shift in a limited size table.  E.g. for a two bytes block,
 * the table isn't 65536 in size (a direct 16 bit look up ) - we could have a 
 * smaller table, with some collisions.  The smaller the table, the greater the
 * number of collisions, and the worse performance the algorithm will probably have.
 * However, we can tune the table size to fit our requirements - the table does
 * not have to be as big as the possible permutations of a block, and we will
 * still get good performance.
 * <p/>
 * This version of the Wu Manber algorithm is simplified from the original 
 * description.  In the original, there is a hash table to give the safe shifts
 * which we also have.  However, there were additional tables also defined to
 * define for a given possible match, which of the patterns needed to be validated.
 * This is clearly an optimisation by Wu and Manber - you only need to validate a 
 * small subset of the patterns being matched at any one time.  However, it is the
 * default of this searcher to use a backwards Trie structure to validate the patterns.
 * This structure is a deterministic finite state automata, and it takes no more
 * time to validate all the patterns than it does to validate one of them.  Hence,
 * since our validator can validate all patterns in constant time, we do not need
 * the additional complexity of sub-pattern lookup tables.
 * </p>
 * If a block size of one is chosen, this algorithm is broadly the same as running
 * the BoyerMooreHorspoolSearcher (using only one pattern, of course).  With multiple
 * patterns, and a block size of one, it is equivalent to the SetHorspoolSearcher 
 * algorithm (not currently implemented). 
 * <p/>
 * There seems little point in having a block size greater than one if you are only 
 * searching a single pattern, as a higher block size is intended to mitigate the 
 * effects of the ever reducing safe shift when multiple patterns map to the same
 * single byte block.  
 * 
 * @author Matt Palmer
 */

public class WuManberSearcher extends AbstractSearcher<SequenceMatcher> {
   
    
    public static int getBlockSize(final MultiSequenceMatcher matcher) {
        return getBlockSize(matcher, 256);
    }
    
    
    public static int getBlockSize(final MultiSequenceMatcher matcher,
                                   final int alphabetSize) {
        final int minLength = matcher.getMinimumLength();
        final int numberOfSequences = matcher.getSequenceMatchers().size();
        return getBlockSize(minLength, numberOfSequences, alphabetSize);
    }
    
    
    public static int getBlockSize(final int minimumLength, 
                                   final int numberOfSequences,
                                   final int alphabetSize) {
        final double optimumBlockSize = 
                getWuManberRecommendedBlockSize(alphabetSize, minimumLength, numberOfSequences);
        final int possibleBlockSize = (int) Math.ceil(optimumBlockSize);
        final int notGreaterThanMinimumLength = minimumLength < possibleBlockSize?
                                                minimumLength : possibleBlockSize;
        return notGreaterThanMinimumLength > 1 ? notGreaterThanMinimumLength : 1;
    }    
    
    
    /**
     * This formulae to suggest the optimum block size is suggested by
     * Wu and Manber.
     * 
     * @param minimumLength The minimum length of all sequences to be matched.
     * @param numberOfSequences The number of sequences to be matched.
     * @return The suggested block size for an efficient Wu Manber search.
     */
    public static double getWuManberRecommendedBlockSize(final int alphabetSize,
                                                         final int minimumLength, 
                                                         final int numberOfSequences) {
        return logOfBase(alphabetSize, 2 * minimumLength * numberOfSequences);
    }    
    
    
    private static double logOfBase(final int base, final int number) {
        return Math.log(number) / Math.log(base);
    }       
    

    private final Searcher<SequenceMatcher> realSearcher;
    
    
    /**
     * 
     * @param matcher 
     */
    public WuManberSearcher(final MultiSequenceMatcher matcher) {
        this(matcher, getBlockSize(matcher));
    }    
    
    
    /**
     * 
     * @param matcher
     * @param blockSize 
     */
    public WuManberSearcher(final MultiSequenceMatcher matcher, final int blockSize) {
        realSearcher = createSearchInstance(matcher, blockSize);
    }
    
    
    private Searcher<SequenceMatcher> createSearchInstance(
            final MultiSequenceMatcher matcher, final int blockSize) {
        switch (blockSize) {
            case 1:  return new WuManberOneByteSearcher(matcher);
            case 2:  return new WuManberTwoByteSearcher(matcher);
            default: return new WuManberMultiByteSearcher(matcher, blockSize);
        }
    }
    
    
   /**
     * @inheritDoc
     */
    @Override
    public void prepareForwards() {
        realSearcher.prepareForwards();
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public void prepareBackwards() {
        realSearcher.prepareBackwards();
    }

    
    @Override
    public List<SearchResult<SequenceMatcher>> searchForwards(Reader reader, long searchPosition, long lastSearchPosition) throws IOException {
        return realSearcher.searchForwards(reader, searchPosition, lastSearchPosition);
    }
    

    @Override
    public List<SearchResult<SequenceMatcher>> searchBackwards(Reader reader, long searchPosition, long lastSearchPosition) throws IOException {
        return realSearcher.searchBackwards(reader, searchPosition, lastSearchPosition);
    }
    

    @Override
    public List<SearchResult<SequenceMatcher>> searchForwards(byte[] bytes, int fromPosition, int toPosition) {
        return realSearcher.searchForwards(bytes, fromPosition, toPosition);
    }
    

    @Override
    public List<SearchResult<SequenceMatcher>> searchBackwards(byte[] bytes, int fromPosition, int toPosition) {
        return realSearcher.searchBackwards(bytes, fromPosition, toPosition);
    }
    
    
    /**
     * 
     */
    public static abstract class WuManberBase extends AbstractMultiSequenceSearcher {
        
        protected final int blockSize;
        protected final LazyObject<SearchInfo> forwardInfo;
        protected final LazyObject<SearchInfo> backwardInfo;
        
        public WuManberBase(final MultiSequenceMatcher matcher, final int blockSize) {
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
                hashCode = (hashCode << 5) - hashCode + ((int) b & 0xFF);
            }
            return hashCode;
        }
        
        
        private int[] createShiftHashTable(final int defaultShift) {
            final int optimumTableSize = guessOptimalTablePowerOfTwoSize();
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
         * @return a pure guess at an optimal table size, an exact power of two, or
         *         256 if the block size is one.
         */
        private int guessOptimalTablePowerOfTwoSize() {
            // with a block size of 1, we only have a single byte value, with 256
            // distinct values.  
            if (blockSize == 1) {
                return 256; 
            }
            // Otherwise, take a guess - we probably don't want a table of 65,536 values
            // or greater under normal circumstances...
            final int numberOfSequences = matcher.getSequenceMatchers().size();
            final int smallestTableSize = 192 + (numberOfSequences * 16);
            final int powerOfTwo = Integer.highestOneBit(smallestTableSize);
            return 1 << (powerOfTwo + 1);
        } 

        
        protected class ForwardSearchInfo extends LazyObject<SearchInfo> {

            @Override
            protected SearchInfo create() {
                return new SearchInfo(getShifts(), getMatcher());
            }
            
            private int[] getShifts() {
                final int defaultShift = matcher.getMinimumLength() - blockSize + 1;        
                final int[] shifts = createShiftHashTable(defaultShift);
                // (relies on shifts being a size which is a power of two):
                final int hashBitMask = shifts.length - 1; 

                // For each sequence in our list:
                for (final SequenceMatcher sequence : matcher.getSequenceMatchers()) {
                    final int matcherLength = sequence.length();

                    // For each block up to the end of the sequence, starting
                    // the minimum length of all sequences back from the end.
                    final int startPos = matcherLength - defaultShift + 1;  
                    for (int position = startPos; position < matcherLength; position++) {
                        final int distanceFromEnd = matcherLength - position - 1;

                        // For each possible permutation of bytes in a block:
                        final List<byte[]> blockBytes = getBlockByteList(position, sequence);
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
                return new MultiSequenceReverseMatcher(matcher);
            }

        }

        //FIXME: this is just a copy of the forward search info at present.
        //       must be changed to reflect distance from start of strings
        protected class BackwardSearchInfo extends LazyObject<SearchInfo> {

            @Override
            protected SearchInfo create() {
                return new SearchInfo(getShifts(), getMatcher());
            }
            
            private int[] getShifts() {
                final int minLength = matcher.getMinimumLength();
                final int defaultShift = minLength - blockSize + 1;        
                final int[] shifts = createShiftHashTable(defaultShift);
                // (relies on shifts being a size which is a power of two):
                final int hashBitMask = shifts.length - 1; 

                // For each sequence in our list:
                for (final SequenceMatcher sequence : matcher.getSequenceMatchers()) {

                    // For each block up to the minimum length of all sequences:
                    for (int position = blockSize - 1; position < minLength; position++) {

                        // For each possible permutation of bytes in a block:
                        final List<byte[]> blockBytes = getBlockByteList(position, sequence);
                        final BytePermutationIterator permutation = new BytePermutationIterator(blockBytes);
                        while (permutation.hasNext()) {

                            // Set the shift for the hash position of this permutation to be 
                            // the smaller of the current shift and the distance from the end:
                            final int hashPos = getBlockHash(permutation.next()) & hashBitMask;
                            final int currentShift = shifts[hashPos];
                            if (position < currentShift) {
                                shifts[hashPos] = position;
                            }
                        }
                    }
                }
                return shifts;
            }
            
            private MultiSequenceMatcher getMatcher() {
                return matcher;
            }
        }
        
    }
    
    
    public static final class SearchInfo {
        public int[] shifts;
        public MultiSequenceMatcher matcher;
        
        public SearchInfo(final int[] shifts, final MultiSequenceMatcher matcher) {
            this.shifts = shifts;
            this.matcher = matcher;
        }
    }
    
    
    public static final class WuManberOneByteSearcher extends WuManberBase {

        private WuManberOneByteSearcher(final MultiSequenceMatcher matcher) {
            super(matcher, 1);
        }
        
        @Override
        protected List<SearchResult<SequenceMatcher>> doSearchForwards(Reader reader, long searchPosition, long lastSearchPosition) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        protected List<SearchResult<SequenceMatcher>> doSearchBackwards(Reader reader, long searchPosition, long lastSearchPosition) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public List<SearchResult<SequenceMatcher>> searchForwards(final byte[] bytes, 
                final int fromPosition, final int toPosition) {
            // Get info needed to search with:
            final SearchInfo info = forwardInfo.get();
            final int[] safeShifts = info.shifts;
            final MultiSequenceMatcher backMatcher = info.matcher;
            
            // Calculate safe bounds for the search:
            final int lastPossiblePosition = bytes.length - 1;
            final int lastPosition = toPosition < lastPossiblePosition ?
                                     toPosition : lastPossiblePosition;
            final int lastMinimumPosition = matcher.getMinimumLength() - 1;
            int searchPosition = fromPosition > 0 ?
                                 fromPosition + lastMinimumPosition : lastMinimumPosition;
            
            // Search forwards:
            while (searchPosition <= lastPosition) {

                // Get the safe shift for this byte:
                final int safeShift = safeShifts[bytes[searchPosition] & 0xFF];

                // Can we shift safely?
                if (safeShift == 0) {
                    
                    // No safe shift - see if we have any matches:
                    final Collection<SequenceMatcher> matches =
                            backMatcher.allMatchesBackwards(bytes, searchPosition);
                    if (!matches.isEmpty()) {
                        
                        // See if any of the matches are within the bounds of the search:
                        final List<SearchResult<SequenceMatcher>> results = 
                            ResultUtils.resultsBackFromPosition(searchPosition, matches, fromPosition);
                        if (!results.isEmpty()) {
                            return results;
                        }
                    }
                    searchPosition++; // no safe shift other than to advance one on.
                    
                } else { // we have a safe shift, move on:
                    searchPosition += safeShift; 
                }
            }
            
            return ResultUtils.noResults();
        }
        

        public List<SearchResult<SequenceMatcher>> searchBackwards(byte[] bytes, int fromPosition, int toPosition) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
    
    
    public static final class WuManberTwoByteSearcher extends WuManberBase {

        private WuManberTwoByteSearcher(final MultiSequenceMatcher matcher) {
            super(matcher, 2);
            if (matcher.getMinimumLength() < 2) {
                throw new IllegalArgumentException("A minimum sequence length of at least two is required.");
            }
        }        
        
        @Override
        protected List<SearchResult<SequenceMatcher>> doSearchForwards(Reader reader, long searchPosition, long lastSearchPosition) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        protected List<SearchResult<SequenceMatcher>> doSearchBackwards(Reader reader, long searchPosition, long lastSearchPosition) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        
        public List<SearchResult<SequenceMatcher>> searchForwards(final byte[] bytes, 
                final int fromPosition, final int toPosition) {
            
            // Get info needed to search with:
            final SearchInfo info = forwardInfo.get();
            final int[] safeShifts = info.shifts;
            final int hashBitMask = safeShifts.length - 1; // safe shifts is a power of two size.
            final MultiSequenceMatcher backMatcher = info.matcher;
            
            // Calculate safe bounds for the search:
            final int lastPossiblePosition = bytes.length - 1;
            final int lastPosition = toPosition < lastPossiblePosition ?
                                     toPosition : lastPossiblePosition;
            final int lastMinimumPosition = matcher.getMinimumLength() - 1;
            int searchPosition = fromPosition > 0 ?
                                 fromPosition + lastMinimumPosition : lastMinimumPosition;
            
            // Search forwards:
            while (searchPosition <= lastPosition) {

                // Calculate the hash of the current block:
                final int firstValue = bytes[searchPosition - 1] & 0xFF;
                final int blockHash = (firstValue << 5) - firstValue +
                                      (bytes[searchPosition] & 0xFF);

                // Get the safe shift for this block:
                final int safeShift = safeShifts[blockHash & hashBitMask];

                // Can we shift safely?
                if (safeShift == 0) {
                    
                    // No safe shift - see if we have any matches:
                    final Collection<SequenceMatcher> matches =
                            backMatcher.allMatchesBackwards(bytes, searchPosition);
                    if (!matches.isEmpty()) {
                        
                        // See if any of the matches are within the bounds of the search:
                        final List<SearchResult<SequenceMatcher>> results = 
                            ResultUtils.resultsBackFromPosition(searchPosition, matches, fromPosition);
                        if (!results.isEmpty()) {
                            return results;
                        }
                    }
                    searchPosition++; // no safe shift other than to advance one on.
                    
                } else { // we have a safe shift, move on:
                    searchPosition += safeShift; 
                }
            }
            return ResultUtils.noResults();
        }

        
        public List<SearchResult<SequenceMatcher>> searchBackwards(byte[] bytes, int fromPosition, int toPosition) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
    
    
    public static final class WuManberMultiByteSearcher extends WuManberBase {

        private WuManberMultiByteSearcher(final MultiSequenceMatcher matcher,
                                          final int blockSize) {
            super(matcher, blockSize);
        }        
        
        @Override
        protected List<SearchResult<SequenceMatcher>> doSearchForwards(Reader reader, long searchPosition, long lastSearchPosition) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        protected List<SearchResult<SequenceMatcher>> doSearchBackwards(Reader reader, long searchPosition, long lastSearchPosition) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public List<SearchResult<SequenceMatcher>> searchForwards(byte[] bytes, int fromPosition, int toPosition) {
            // Get info needed to search with:
            final SearchInfo info = forwardInfo.get();
            final int[] safeShifts = info.shifts;
            final int hashBitMask = safeShifts.length - 1; // safe shifts is a power of two size.
            final MultiSequenceMatcher backMatcher = info.matcher;
            
            // Calculate safe bounds for the search:
            final int lastPossiblePosition = bytes.length - 1;
            final int lastPosition = toPosition < lastPossiblePosition ?
                                     toPosition : lastPossiblePosition;
            final int lastMinimumPosition = matcher.getMinimumLength() - 1;
            int searchPosition = fromPosition > 0 ?
                                 fromPosition + lastMinimumPosition : lastMinimumPosition;
               
            
            // Search forwards:
            while (searchPosition <= lastPosition) {

                // Calculate the hash of the current block:
                int blockHash = 0;
                for (int blockPosition =  searchPosition - blockSize + 1; 
                         blockPosition <= searchPosition; blockPosition++) {
                    final int value = bytes[blockPosition] & 0xFF;
                    blockHash = ((blockHash << 5) - blockHash) * value;
                }

                // Get the safe shift for this block:
                final int safeShift = safeShifts[blockHash & hashBitMask];

                // Can we shift safely?
                if (safeShift == 0) {
                    
                    // No safe shift - see if we have any matches:
                    final Collection<SequenceMatcher> matches =
                            backMatcher.allMatchesBackwards(bytes, searchPosition);
                    if (!matches.isEmpty()) {
                        
                        // See if any of the matches are within the bounds of the search:
                        final List<SearchResult<SequenceMatcher>> results = 
                            ResultUtils.resultsBackFromPosition(searchPosition, matches, fromPosition);
                        if (!results.isEmpty()) {
                            return results;
                        }
                    }
                    searchPosition++; // no safe shift other than to advance one on.
                    
                } else { // we have a safe shift, move on:
                    searchPosition += safeShift; 
                }
            }
            return ResultUtils.noResults();
        }

        public List<SearchResult<SequenceMatcher>> searchBackwards(byte[] bytes, int fromPosition, int toPosition) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
    

}
