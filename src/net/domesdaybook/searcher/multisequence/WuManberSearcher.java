/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.searcher.multisequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.domesdaybook.compiler.CompileException;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.ByteReader;
import net.domesdaybook.searcher.Searcher;
import net.domesdaybook.compiler.ReversibleCompiler;
import net.domesdaybook.compiler.ReversibleCompiler.Direction;
import net.domesdaybook.compiler.multisequence.TrieMatcherCompiler;
import net.domesdaybook.matcher.multisequence.MultiSequenceMatcher;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;

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
 * @author matt
 */
public class WuManberSearcher implements Searcher {

    public static final int ALPHABET_SIZE = 256;
    
    /**
     * This formulae to suggest the optimum block size is suggested by
     * Wu and Manber.
     * 
     * @param minimumLength The minimum length of all sequences to be matched.
     * @param numberOfSequences The number of sequences to be matched.
     * @return The suggested block size for an efficient Wu Manber search.
     */
    public static double getWuManberRecommendedBlockSize(final int minimumLength, final int numberOfSequences) {
        return logOfBase(ALPHABET_SIZE, 2 * minimumLength * numberOfSequences);
    }
    
    
    /**
     * TODO: requires performance testing to determine good block sizes, when
     * varying in minimum length and number of sequences.
     * 
     * @param minimumLength
     * @param numberOfSequences
     * @return 
     */
    public static int getPossibleBlockSize(final int minimumLength, final int numberOfSequences) {
        final double optimumBlockSize = getWuManberRecommendedBlockSize(minimumLength, numberOfSequences);
        final int possibleBlockSize = (int) Math.floor(optimumBlockSize);
        return possibleBlockSize > 0 ? possibleBlockSize : 1;
    }
    
    
    private static double logOfBase(final int base, final int number) {
        return Math.log(number) / Math.log(base);
    }    
   
    
    @SuppressWarnings("VolatileArrayField")
    private volatile int[] forwardShifts;
    @SuppressWarnings("VolatileArrayField")
    private volatile int[] backwardShifts;
    private volatile MultiSequenceMatcher forwardMatcher;
    private volatile MultiSequenceMatcher backwardMatcher;
    
    private final List<SequenceMatcher> matcherList;
    //TODO: refresh memory on extends and super for generics, check Effective Java...
    private final ReversibleCompiler<? extends MultiSequenceMatcher, Collection<SequenceMatcher>> multiSequenceCompiler;
    private final int minimumLength;
    private final int maximumLength;
    private final int blockSize;
    
    
    public WuManberSearcher(final Collection<SequenceMatcher> matchers) {
        this(matchers, null, 0);
    }
    
    
    public WuManberSearcher(final Collection<SequenceMatcher> matchers, 
        final ReversibleCompiler<MultiSequenceMatcher, Collection<SequenceMatcher>> compiler) {
        this(matchers, compiler, 0);
    }
    
    
    public WuManberSearcher(final Collection<SequenceMatcher> matchers, 
            final ReversibleCompiler< MultiSequenceMatcher, Collection<SequenceMatcher>> compiler,
            final int blockSize) {
        if (matchers == null || matchers.isEmpty()) {
            throw new IllegalArgumentException("Null or empty sequence matchers passed in to WuManberSearch");
        }
        this.matcherList = new ArrayList(matchers);
        
        // Find out the minimum and maximum length of the sequences to be matched:
        int minimumLen = Integer.MAX_VALUE;
        int maximumLen = Integer.MIN_VALUE;
        for (final SequenceMatcher matcher : matcherList) {
            final int matcherLength = matcher.length();
            minimumLen = Math.min(minimumLen, matcherLength);
            maximumLen = Math.max(maximumLen, matcherLength);
        }
        minimumLength = minimumLen;
        maximumLength = maximumLen;
        
        // Figure out the block size to use:
        int possibleBlockSize = blockSize;
        if (possibleBlockSize < 1) {
            possibleBlockSize = getPossibleBlockSize(minimumLength, matcherList.size());
        }
        // Can't have a block size greater than the minimum length of a sequence 
        // to be matched.
        this.blockSize = possibleBlockSize > minimumLength ? possibleBlockSize : minimumLength;
        
        // Set the multisequence compiler to use:
        if (compiler == null) {
            this.multiSequenceCompiler = new TrieMatcherCompiler();
        } else {
            this.multiSequenceCompiler = compiler;
        }
    }
    
    
    
    public long searchForwards(ByteReader reader, long fromPosition, long toPosition) {
        // Get the data we need to search with:
        calculateForwardParameters();
        final MultiSequenceMatcher validator = forwardMatcher;
        final int[] safeShifts = forwardShifts;
        final int hashBitMask = safeShifts.length - 1; 
        
        // Calculate safe bounds for the search:
        final long lastPossiblePosition = reader.length() - 1;
        final long lastPosition = toPosition <= lastPossiblePosition ?
                toPosition : lastPossiblePosition;
        final int lastMinimumPosition = minimumLength - 1;
        long searchPosition = fromPosition <= lastMinimumPosition ?
                lastMinimumPosition : fromPosition + lastMinimumPosition;
        
        // Search with a block size of two (most probable useful block size).
        if (blockSize == 2) { 
            while (searchPosition <= lastPosition) {

                // Calculate the hash of the current block:
                final int firstValue = (int) (reader.readByte(searchPosition - 1) & 0xFF);
                final int blockHash = (firstValue << 5) - firstValue +
                                      ((int) (reader.readByte(searchPosition) & 0xFF));

                // Get the safe shift for this block:
                final int safeShift = safeShifts[blockHash & hashBitMask];

                // See if we have a match.  
                if (safeShift == 0) {
                    if (validator.matchesBackwards(reader, searchPosition)) {
                        return searchPosition; // a match!
                    }
                    searchPosition++; // no safe shift other than to advance one on.
                } else {
                    searchPosition += safeShift; // move on with the safe shift.
                }
            }
            
        // Search with a block size of one:
        } else if (blockSize == 1) { 
            
            while (searchPosition <= lastPosition) {

                // Get the safe shift for this byte:
                final int safeShift = safeShifts[(int) (reader.readByte(searchPosition) & 0xFF)];

                // See if we have a match.  
                if (safeShift == 0) {
                    if (validator.matchesBackwards(reader, searchPosition)) {
                        return searchPosition; // a match!
                    }
                    searchPosition++; // no safe shift other than to advance one on.
                } else {
                    searchPosition += safeShift; // move on with the safe shift.
                }
            }

        // Search with a larger block size:
        } else { 
            while (searchPosition <= lastPosition) {

                // Calculate the hash of the current block:
                int blockHash = 0;
                for (long blockPosition = searchPosition - blockSize + 1; 
                        blockPosition <= searchPosition; blockPosition++) {
                    final int value = (int) (reader.readByte(blockPosition) & 0xFF);
                    blockHash = ((blockHash << 5) - blockHash) * value;
                }

                // Get the safe shift for this block:
                final int safeShift = safeShifts[blockHash & hashBitMask];

                // See if we have a match.  
                if (safeShift == 0) {
                    if (validator.matchesBackwards(reader, searchPosition)) {
                        return searchPosition; // a match!
                    }
                    searchPosition++; // no safe shift other than to advance one on.
                } else {
                    searchPosition += safeShift; // move on with the safe shift.
                }
            }
        }
        
        return Searcher.NOT_FOUND;
    }

    
    public int searchForwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        // Get the data we need to search with:
        calculateForwardParameters();
        final MultiSequenceMatcher validator = forwardMatcher;
        final int[] safeShifts = forwardShifts;
        final int hashBitMask = safeShifts.length - 1; 
        
        // Calculate safe bounds for the search:
        final int lastPossiblePosition = bytes.length - 1;
        final int lastPosition = toPosition <= lastPossiblePosition ?
                toPosition : lastPossiblePosition;
        final int lastMinimumPosition = minimumLength - 1;
        int searchPosition = fromPosition <= lastMinimumPosition ?
                lastMinimumPosition : fromPosition + lastMinimumPosition;
        
        // Search with a block size of two (most probable useful block size).
        if (blockSize == 2) { 
            while (searchPosition <= lastPosition) {

                // Calculate the hash of the current block:
                final int firstValue = (int) (bytes[searchPosition - 1] & 0xFF);
                final int blockHash = (firstValue << 5) - firstValue +
                                      ((int) (bytes[searchPosition] & 0xFF));

                // Get the safe shift for this block:
                final int safeShift = safeShifts[blockHash & hashBitMask];

                // See if we have a match.  
                if (safeShift == 0) {
                    if (validator.matchesBackwards(bytes, searchPosition)) {
                        return searchPosition; // a match!
                    }
                    searchPosition++; // no safe shift other than to advance one on.
                } else {
                    searchPosition += safeShift; // move on with the safe shift.
                }
            }
            
        // Search with a block size of one:
        } else if (blockSize == 1) { 
            
            while (searchPosition <= lastPosition) {

               // Get the safe shift for this byte:
                final int safeShift = safeShifts[(int) (bytes[searchPosition] & 0xFF)];

                // See if we have a match.  
                if (safeShift == 0) {
                    if (validator.matchesBackwards(bytes, searchPosition)) {
                        return searchPosition; // a match!
                    }
                    searchPosition++; // no safe shift other than to advance one on.
                } else {
                    searchPosition += safeShift; // move on with the safe shift.
                }
            }

        // Search with a larger block size:
        } else { 
            while (searchPosition <= lastPosition) {

                // Calculate the hash of the current block:
                int blockHash = 0;
                for (int blockPosition = searchPosition - blockSize + 1; 
                        blockPosition <= searchPosition; blockPosition++) {
                    final int value = (int) (bytes[blockPosition] & 0xFF);
                    blockHash = ((blockHash << 5) - blockHash) * value;
                }

                // Get the safe shift for this block:
                final int safeShift = safeShifts[blockHash & hashBitMask];

                // See if we have a match.  
                if (safeShift == 0) {
                    if (validator.matchesBackwards(bytes, searchPosition)) {
                        return searchPosition; // a match!
                    }
                    searchPosition++; // no safe shift other than to advance one on.
                } else {
                    searchPosition += safeShift; // move on with the safe shift.
                }
            }
        }
        
        return Searcher.NOT_FOUND;
    }

    
    public long searchBackwards(ByteReader reader, long fromPosition, long toPosition) {
        calculateBackwardParameters();
        final MultiSequenceMatcher validator = backwardMatcher;
        final int[] safeShifts = backwardShifts;
        
        
        return Searcher.NOT_FOUND;
    }

    
    public int searchBackwards(byte[] bytes, int fromPosition, int toPosition) {
        calculateBackwardParameters();
        final MultiSequenceMatcher validator = backwardMatcher;
        final int[] safeShifts = backwardShifts;
        
        
        return Searcher.NOT_FOUND;

    }
    
    
    private void calculateForwardParameters() {
        if (forwardMatcher == null) {
            forwardMatcher = compileMultiSequenceMatcher(Direction.REVERSED);
            if (forwardShifts == null) {
             forwardShifts = createForwardShifts();
            }
        }
    }
    
    
    private void calculateBackwardParameters() {
        if (backwardMatcher == null) {
            backwardMatcher = compileMultiSequenceMatcher(Direction.FORWARDS);
            if (backwardShifts == null) {
                backwardShifts = createBackwardShifts();
            }
        }
    }

    
    private int[] createForwardShifts() {
        final int defaultShift = minimumLength - blockSize + 1;        
        final int[] shifts = createDefaultShifts(defaultShift);
        // (relies on shifts being a size which is a power of two):
        final int hashBitMask = shifts.length - 1; 
        
        // For each sequence in our list:
        for (final SequenceMatcher sequence : matcherList) {
            final int matcherLength = sequence.length();
            
            // Look for shifts which would be smaller than the default shift,
            // as the minimum of the two is taken in any case.
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
                    shifts[hashPos] = Math.min(shifts[hashPos], distanceFromEnd);
                }
            }
        }
        return shifts;
    }

    
    private int[] createBackwardShifts() {
        throw new UnsupportedOperationException("Operation not yet supported");
    }
    
    
    private int[] createDefaultShifts(final int defaultShift) {
        final int optimumTableSize = guessOptimalTablePowerOfTwoSize();
        final int[] shifts = new int[optimumTableSize];
        Arrays.fill(shifts, defaultShift);  
        return shifts;
    }

    
    /*
     * Keep hitting this issue of an implementation of an interface that wants
     * to throw a checked exception. Should we throw something else instead?
     */
    private MultiSequenceMatcher compileMultiSequenceMatcher(final Direction direction) {
        try {
            return multiSequenceCompiler.compile(matcherList, direction);
        } catch (CompileException ex) {
            return null; 
        }
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
        final int numberOfSequences = matcherList.size();
        final int smallestTableSize = 192 + (numberOfSequences * 16);
        final int nextHighestPowerOfTwo = (int) Math.ceil(logOfBase(2, smallestTableSize));
        return 1 << nextHighestPowerOfTwo;
    }

    
    private List<byte[]> getBlockByteList(final int position, final SequenceMatcher matcher) {
        final List<byte[]> byteList = new ArrayList<byte[]>(blockSize);
        for (int blockIndex = position - blockSize + 1; blockIndex <= position; blockIndex++) {
            final SingleByteMatcher byteMatcher = matcher.getByteMatcherForPosition(blockIndex);
            byteList.add(byteMatcher.getMatchingBytes());
        }
        return byteList;
    }

    
    private int getBlockHash(final byte[] block) {
        int hashCode = 0;
        for (final byte b : block) {
            // left shift 5 - original value = (x * 32) - x = x * 31.
            hashCode = (hashCode << 5) - hashCode + ((int) b & 0xFF);
        }
        return hashCode;
    }




}
