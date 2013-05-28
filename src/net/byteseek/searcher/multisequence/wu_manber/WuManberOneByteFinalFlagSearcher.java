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

package net.byteseek.searcher.multisequence.wu_manber;

import java.io.IOException;

import net.byteseek.io.Window;
import net.byteseek.io.WindowReader;
import net.byteseek.matcher.bytes.ByteMatcher;
import net.byteseek.matcher.multisequence.MultiSequenceMatcher;
import net.byteseek.matcher.multisequence.MultiSequenceReverseMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.searcher.SearchResult;
import net.byteseek.searcher.SearchUtils;
import net.byteseek.searcher.Searcher;
import net.byteseek.searcher.multisequence.AbstractMultiSequenceSearcher;
import net.byteseek.util.bytes.BytePermutationIterator;
import net.byteseek.util.bytes.ByteUtilities;
import net.byteseek.util.object.LazyObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

//import net.byteseek.searcher.ProxySearcher;

/**
 * TO BE FINISHED.
 * 
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
 * of this sort of searching - and probably slower than just naively searching
 * one byte at a time, due to the additional overhead of finding the shifts.
 * </p>
 * The Wu Manber search gets around this limitation by matching on more than
 * one byte at a time.  It looks at blocks of bytes (effectively extending the 
 * available alphabet), and calculates a hash code for them.  It uses this hash
 * code to look up a safe shift in a limited size table.  For example, for a two-byte ,
 * block the table doesn't have to be 65536 in size (a direct 16 bit look up ) -
 * we could have a smaller table, with some collisions.  The smaller the table, 
 * the greater the number of collisions, and the worse performance the algorithm 
 * will probably have.  However, we can tune the table size to fit our requirements - 
 * the table does not have to be as big as the possible permutations of a block,
 * and we will still get good performance on average.
 *  * </p>
 * If a block size of one is chosen, this algorithm is broadly the same as running
 * the BoyerMooreHorspoolSearcher (using only one pattern, of course).  With multiple
 * patterns, and a block size of one, it is equivalent to the SetHorspoolSearcher 
 * algorithm (not currently implemented). 
 * <p/>
 * There seems little point in having a block size greater than one if you are only 
 * searching a single pattern, as a higher block size is intended to mitigate the 
 * effects of the ever reducing safe shift when multiple patterns map to the same
 * single byte block.  With only one pattern (or even just a few patterns)
 * no additional benefit is gained by extending the alphabet into hash blocks.
 * <p/>
 * This version of Wu Manber is only a partial implementation of the algorithm
 * described in the original paper, which specifies a particular method of verifying
 * whether a sequence has actually matched.  Byteseek allows any form of 
 * MultiSequenceMatcher to be plugged in for the verification stage.  If the 
 * {@link net.byteseek.matcher.multisequence.HashMultiSequenceMatcher} is used with this searcher, then the combination
 * is comparable to the entire original algorithm.  In practice, other matchers can
 * provide better performance or use less memory, depending on requirements.
 * 
 * @author Matt Palmer
 */
@SuppressWarnings("all") // code is entirely unfinished - do not use yet.
 public class WuManberOneByteFinalFlagSearcher {
   

    /**
     * 
     * @param matcher
     * @return
     */
    public static int getBlockSize(final MultiSequenceMatcher matcher) {
        return getBlockSize(matcher, 256);
    }
    
    
    /**
     * 
     * @param matcher
     * @param alphabetSize
     * @return
     */
    public static int getBlockSize(final MultiSequenceMatcher matcher,
                                   final int alphabetSize) {
        final int minLength = matcher.getMinimumLength();
        final int numberOfSequences = matcher.getSequenceMatchers().size();
        return getBlockSize(minLength, numberOfSequences, alphabetSize);
    }
    
    
    /**
     * 
     * @param minimumLength
     * @param numberOfSequences
     * @param alphabetSize
     * @return
     */
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
     * @param alphabetSize  
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
    
    
    private static Searcher<SequenceMatcher> createSearchInstance(
            final MultiSequenceMatcher matcher, final int blockSize) {
        return new OneByteBlockSearcher(matcher);
        /*
        switch (blockSize) {
            case 1:  return new OneByteBlockSearcher(sequences);
            case 2:  return new TwoByteBlockSearcher(sequences);
            default: return new ManyByteBlockSearcher(sequences, blockSize);
        }
         * 
         */
    }    
    
    
    /**
     * 
     * @param matcher 
     */
    public WuManberOneByteFinalFlagSearcher(final MultiSequenceMatcher matcher) {
        this(matcher, getBlockSize(matcher));
    }    
    
    
    /**
     * 
     * @param matcher 
     * @param blockSize 
     */
    public WuManberOneByteFinalFlagSearcher(final MultiSequenceMatcher matcher, final int blockSize) {
        //super(createSearchInstance(matcher, blockSize));
    }
    
    
    /**
     * 
     */
    public static final class SearchInfo {
        /**
         * 
         */
        public int[] shifts;
        /**
         * 
         */
        public MultiSequenceMatcher matcher;
        
        /**
         * 
         * @param shifts
         * @param matcher
         */
        public SearchInfo(final int[] shifts, final MultiSequenceMatcher matcher) {
            this.shifts = shifts;
            this.matcher = matcher;
        }
    }
    
    
    /**
     * 
     */
    public static abstract class AbstractWuManberSearcher extends AbstractMultiSequenceSearcher {
        
        private static int HIGHEST_POWER_OF_TWO = 1073741824;
        
        /**
         * 
         */
        protected final int blockSize;
        /**
         * 
         */
        protected final LazyObject<SearchInfo> forwardInfo;
        /**
         * 
         */
        protected final LazyObject<SearchInfo> backwardInfo;
        
        /**
         * 
         * @param matcher
         * @param blockSize
         */
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


        //TODO: profile these searchers to empirically investigate performance / space trade-offs.
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


        
        /**
         * 
         */
        protected class ForwardSearchInfo extends LazyObject<SearchInfo> {

            /**
             * 
             * @return
             */
            @Override
            protected SearchInfo create() {
                return new SearchInfo(getShifts(), getMatcher());
            }
            
            private int[] getShifts() {
                final int defaultShift = sequences.getMinimumLength() - blockSize + 1;        
                final int[] shifts = createShiftHashTable(defaultShift);
                // (relies on shifts being a size which is a power of two):
                final int hashBitMask = shifts.length - 1; 

                // For each sequence in our list, find the safe shifts:
                for (final SequenceMatcher sequence : sequences.getSequenceMatchers()) {
                    final int matcherLength = sequence.length();

                    // For each block from the minimum length of all sequences from the end of this sequence,
                    // up to the second to last position in the sequence:
                    final int firstBlockEndPosition = matcherLength - sequences.getMinimumLength() + blockSize - 1; 
                    for (int blockEndPosition = firstBlockEndPosition; blockEndPosition < matcherLength - 1; blockEndPosition++) {
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
                
                // For each sequence in the list, flag the last position bytes
                // by making the shift negative for each permutation of them:
                for (final SequenceMatcher sequence : sequences.getSequenceMatchers()) {
                    final List<byte[]> blockBytes = getBlockByteList(sequence.length() - 1, sequence);
                    final BytePermutationIterator permutation = new BytePermutationIterator(blockBytes);
                    while (permutation.hasNext()) {
                        final int hashPos = getBlockHash(permutation.next()) & hashBitMask;
                        int currentShift = shifts[hashPos];
                        if (currentShift > 0) {
                            shifts[hashPos] = -currentShift;
                        }
                    }
                }
                
                return shifts;
            }
            
            private MultiSequenceMatcher getMatcher() {
                return new MultiSequenceReverseMatcher(sequences);
            }

        }

        //FIXME: this is just a copy of the forward search info at present.
        //       must be changed to reflect distance from start of strings
        /**
         * 
         */
        protected class BackwardSearchInfo extends LazyObject<SearchInfo> {

            /**
             * 
             * @return
             */
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

                    // For each block up to the minimum length of all sequences,
                    // from the second position (the first will be flagged separately):
                    for (int blockEndPosition = blockSize; blockEndPosition < minLength; blockEndPosition++) {

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
                
                
                // For each sequence in the list, flag the first position bytes
                // by making the shift negative for each permutation of them:
                for (final SequenceMatcher sequence : sequences.getSequenceMatchers()) {
                    final List<byte[]> blockBytes = getBlockByteList(0, sequence);
                    final BytePermutationIterator permutation = new BytePermutationIterator(blockBytes);
                    while (permutation.hasNext()) {
                        final int hashPos = getBlockHash(permutation.next()) & hashBitMask;
                        int currentShift = shifts[hashPos];
                        if (currentShift > 0) {
                            shifts[hashPos] = -currentShift;
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
    
    
    /**
     * 
     */
    public static final class OneByteBlockSearcher extends AbstractWuManberSearcher {

        /**
         * 
         * @param matcher
         */
        public OneByteBlockSearcher(final MultiSequenceMatcher matcher) {
            super(matcher, 1);
        }
        
        public List<SearchResult<SequenceMatcher>> searchForwards(final byte[] bytes, 
                final int fromPosition, final int toPosition) {
            // Get info needed to search with:
            final SearchInfo info = forwardInfo.get();
            final int[] safeShifts = info.shifts;
            final MultiSequenceMatcher backMatcher = info.matcher;
            
            // Calculate safe bounds for the search:
            final int lastPossiblePosition = bytes.length - 1;
            final int lastToPosition = toPosition + sequences.getMaximumLength() - 1;
            final int lastPosition = lastToPosition < lastPossiblePosition ?
                                     lastToPosition : lastPossiblePosition;
            final int minimumPosition = sequences.getMinimumLength() - 1;
            int searchPosition = fromPosition > 0 ?
                                 fromPosition + minimumPosition : minimumPosition;
            
            // Search forwards:
            while (searchPosition <= lastPosition) {

                // Get the safe shift for this byte:
                final int safeShift = safeShifts[bytes[searchPosition] & 0xFF];

                // Is there a possible match?
                if (safeShift < 0) {

                    // A negative shift - see if we have any matches:
                    final Collection<SequenceMatcher> matches =
                            backMatcher.allMatchesBackwards(bytes, searchPosition);
                    if (!matches.isEmpty()) {
                        
                        // See if any of the matches are within the bounds of the search:
                        final List<SearchResult<SequenceMatcher>> results = 
                            SearchUtils.resultsBackFromPosition(searchPosition, matches, 
                                                                fromPosition, toPosition);
                        if (!results.isEmpty()) {
                            return results;
                        }
                    }
                    
                    // Shift forwards by the shift.  
                    searchPosition -= safeShift; // subtract, as the shift is negative.                  
                } else {
                    searchPosition += safeShift; // add, as the shift is positive.
                } 
            }
            
            return SearchUtils.noResults();
        }
        
        
        @Override
        protected List<SearchResult<SequenceMatcher>> doSearchForwards(final WindowReader reader, 
                final long fromPosition, final long toPosition) throws IOException {
            // Get info needed to search with:
            final SearchInfo info = forwardInfo.get();
            final int[] safeShifts = info.shifts;
            final MultiSequenceMatcher backMatcher = info.matcher;

            // Initialise window search:
            final long finalPosition = toPosition + sequences.getMaximumLength() - 1;
            long searchPosition = fromPosition + sequences.getMinimumLength() - 1;        
            
            // While there is a window to search in:
            Window window;            
            while (searchPosition <= finalPosition &&
                   (window = reader.getWindow(searchPosition)) != null) {

                // Initialise array search:
                final byte[] array = window.getArray();
                final int arrayStartPosition = reader.getWindowOffset(searchPosition);
                final int arrayEndPosition = window.length() - 1;
                final long distanceToEnd = finalPosition - window.getWindowPosition();     
                final int lastSearchPosition = distanceToEnd < arrayEndPosition?
                                         (int) distanceToEnd : arrayEndPosition;
                int arraySearchPosition = arrayStartPosition;            

                long DEBUG_SEARCH = searchPosition;
                
                // Search forwards in this array:
                while (arraySearchPosition <= lastSearchPosition) {

                    final int safeShift = safeShifts[array[arraySearchPosition] & 0xFF];
                    if (safeShift < 0) {
                        // see if we have a match:
                        final long matchEndPosition = searchPosition + arraySearchPosition - arrayStartPosition;
                        final Collection<SequenceMatcher> matches =
                                backMatcher.allMatchesBackwards(reader, matchEndPosition);
                        if (!matches.isEmpty()) {
                            // Convert the matches into search results, filtering on the ends of the search:
                            final List<SearchResult<SequenceMatcher>> results = 
                                SearchUtils.resultsBackFromPosition(matchEndPosition, matches, 
                                                                    fromPosition, toPosition);
                            if (!results.isEmpty()) {
                                return results;
                            }
                        }
                        arraySearchPosition -= safeShift;
                        DEBUG_SEARCH -= safeShift;
                    } else {
                        arraySearchPosition += safeShift;
                        DEBUG_SEARCH += safeShift;
                    } 
                } 

                // No match was found in this array - calculate the current search position:
                searchPosition += arraySearchPosition - arrayStartPosition;
            }

            return SearchUtils.noResults();   
        }

        @Override
        protected List<SearchResult<SequenceMatcher>> doSearchBackwards(final WindowReader reader, 
                final long fromPosition, final long toPosition) throws IOException {
            // Get the objects needed to search:
            final SearchInfo info = backwardInfo.get();
            final int[] safeShifts = info.shifts;
            final MultiSequenceMatcher verifier = info.matcher;        

            // Initialise window search:
            long searchPosition = fromPosition;

            // Search backwards across the windows:
            Window window;            
            while (searchPosition >= toPosition &&
                   (window = reader.getWindow(searchPosition)) != null) {

                // Initialise the window search:
                final byte[] array = window.getArray();
                final int arrayStartPosition = reader.getWindowOffset(searchPosition);   
                final long distanceToEnd = toPosition - window.getWindowPosition();
                final int lastSearchPosition = distanceToEnd > 0?
                                         (int) distanceToEnd : 0;
                int arraySearchPosition = arrayStartPosition;

                // Search using the byte array for shifts, using the WindowReader
                // for verifiying the sequence with the sequences:          
                while (arraySearchPosition >= lastSearchPosition) {

                    final int safeShift = safeShifts[array[arraySearchPosition] & 0xFF];
                    if (safeShift < 0) {

                        // The first byte matched - verify the rest of the sequences.
                        final long startMatchPosition = searchPosition + arraySearchPosition - arrayStartPosition;
                        final Collection<SequenceMatcher> matches = verifier.allMatches(reader, startMatchPosition);
                        if (!matches.isEmpty()) {
                            return SearchUtils.resultsAtPosition(startMatchPosition, matches); // match found.
                        }
                        arraySearchPosition += safeShift; // no match, shift back.
                    } else { // No match was found - shift backward by the shift for the current byte:
                        arraySearchPosition -= safeShift;
                    }
                }

                // No match was found in this array - calculate the current search position:
                searchPosition -= (arrayStartPosition - arraySearchPosition);
            }

            return SearchUtils.noResults();
        }

        
        public List<SearchResult<SequenceMatcher>> searchBackwards(final byte[] bytes, 
                final int fromPosition, final int toPosition) {
            // Get info needed to search with:
            final SearchInfo info = backwardInfo.get();
            final int[] safeShifts = info.shifts;
            final MultiSequenceMatcher verifier = info.matcher;
            
            // Calculate safe bounds for the search:
            final int lastPosition = toPosition > 0 ?
                                     toPosition : 0;
            final int firstPossiblePosition = bytes.length - 1;
            int searchPosition = fromPosition < firstPossiblePosition ?
                                 fromPosition : firstPossiblePosition;
            
            // Search forwards:
            while (searchPosition >= lastPosition) {

                // Get the safe shift for this byte:
                final int safeShift = safeShifts[bytes[searchPosition] & 0xFF];

                // Is there a possible match?
                if (safeShift < 0) {

                    // A negative shift - see if we have any matches:
                    final Collection<SequenceMatcher> matches =
                            verifier.allMatches(bytes, searchPosition);
                    if (!matches.isEmpty()) {
                        return SearchUtils.resultsAtPosition(searchPosition, matches);
                    }
                    
                    // No matches, shift backwards:
                    searchPosition += safeShift; // add, as the shift is negative.
                } else { 
                    searchPosition -= safeShift; // subtract, as the shift is positive.
                }
            }
            return SearchUtils.noResults();
        }
    }
    

}
