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
import net.domesdaybook.matcher.multisequence.TrieMatcher;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;

/**
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
    public static double getOptimumBlockSize(final int minimumLength, final int numberOfSequences) {
        return logOfBase(ALPHABET_SIZE, 2 * minimumLength * numberOfSequences);
    }
    
    
    public static int getPossibleBlockSize(final int minimumLength, final int numberOfSequences) {
        final double optimumBlockSize = getOptimumBlockSize(minimumLength, numberOfSequences);
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
    private volatile TrieMatcher forwardTrie;
    private volatile TrieMatcher backwardTrie;
    
    private final List<SequenceMatcher> matcherList;
    private final ReversibleCompiler<TrieMatcher, Collection<SequenceMatcher>> trieCompiler;
    private final int minimumLength;
    private final int maximumLength;
    private final int blockSize;
    
    public WuManberSearcher(final Collection<SequenceMatcher> matchers) {
        this(matchers, null, 0);
    }
    
    
    public WuManberSearcher(final Collection<SequenceMatcher> matchers, 
        final ReversibleCompiler<TrieMatcher, Collection<SequenceMatcher>> compiler) {
        this(matchers, compiler, 0);
    }
    
    
    public WuManberSearcher(final Collection<SequenceMatcher> matchers, 
            final ReversibleCompiler<TrieMatcher, Collection<SequenceMatcher>> compiler,
            final int blockSize) {
        if (matchers == null || matchers.isEmpty()) {
            throw new IllegalArgumentException("Null or empty sequence matchers passed in to WuManberSearch");
        }
        this.matcherList = new ArrayList(matchers);
        
        int minimumLen = Integer.MAX_VALUE;
        int maximumLen = Integer.MIN_VALUE;
        for (final SequenceMatcher matcher : matcherList) {
            final int matcherLength = matcher.length();
            minimumLen = Math.min(minimumLen, matcherLength);
            maximumLen = Math.max(maximumLen, matcherLength);
        }
        minimumLength = minimumLen;
        maximumLength = maximumLen;
        
        if (blockSize < 1) {
            this.blockSize = getPossibleBlockSize(minimumLength, matcherList.size());
        } else {
            this.blockSize = blockSize;
        }
        
        if (compiler == null) {
            this.trieCompiler = new TrieMatcherCompiler();
        } else {
            this.trieCompiler = compiler;
        }

    }
    
    
    
    public long searchForwards(ByteReader reader, long fromPosition, long toPosition) {
        calculateForwardParameters();
        final TrieMatcher validator = forwardTrie;
        final int[] safeShifts = forwardShifts;
        
        
        
        return Searcher.NOT_FOUND;
    }

    
    public int searchForwards(byte[] bytes, int fromPosition, int toPosition) {
        calculateForwardParameters();
        final TrieMatcher validator = forwardTrie;
        final int[] safeShifts = forwardShifts;
        
        return Searcher.NOT_FOUND;
    }

    
    public long searchBackwards(ByteReader reader, long fromPosition, long toPosition) {
        calculateBackwardParameters();
        final TrieMatcher validator = backwardTrie;
        final int[] safeShifts = backwardShifts;
        
        
        return Searcher.NOT_FOUND;
    }

    
    public int searchBackwards(byte[] bytes, int fromPosition, int toPosition) {
        calculateBackwardParameters();
        final TrieMatcher validator = backwardTrie;
        final int[] safeShifts = backwardShifts;
        
        
        return Searcher.NOT_FOUND;

    }
    
    
    private void calculateForwardParameters() {
        if (forwardTrie == null) {
            forwardTrie = createTrie(Direction.REVERSED);
            if (forwardShifts == null) {
             forwardShifts = createForwardShifts();
            }
        }
    }
    
    
    private void calculateBackwardParameters() {
        if (backwardTrie == null) {
            backwardTrie = createTrie(Direction.FORWARDS);
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
                final BytePermutationIterator matchingBytes = new BytePermutationIterator(blockBytes);
                while (matchingBytes.hasNext()) {
                    
                    // Set the shift for the hash position of this permutation to be 
                    // the smaller of the existing shift and the shift for this position:
                    final int hashPos = getBlockHash(matchingBytes.next()) & hashBitMask;
                    shifts[hashPos] = Math.min(shifts[hashPos], distanceFromEnd);
                }
            }
        }
        return shifts;
    }

    
    private int[] createBackwardShifts() {
        final int defaultShift = minimumLength - blockSize + 1;        
        final int[] shifts = createDefaultShifts(defaultShift);

        //TODO: finish backward shifts.
        
        return shifts;
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
    private TrieMatcher createTrie(final Direction direction) {
        try {
            return trieCompiler.compile(matcherList, direction);
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
     * @return a pure guess at an optimal table size, an exact power of two.
     */
    private int guessOptimalTablePowerOfTwoSize() {
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
