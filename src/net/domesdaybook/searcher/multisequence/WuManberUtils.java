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

import net.domesdaybook.matcher.multisequence.MultiSequenceMatcher;

/**
 * WuManberUtils implements a variation of the classic multi-pattern
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
 * {@link HashMultiSequenceMatcher} is used with this searcher, then the combination
 * is comparable to the entire original algorithm.  In practice, other matchers can
 * provide better performance or use less memory, depending on requirements.
 * 
 * @author Matt Palmer
 */

public class WuManberUtils {
   

    public static int suggestBlockSize(final MultiSequenceMatcher matcher) {
        return suggestBlockSize(256, matcher);
    }
    
    
    public static int suggestBlockSize(final int alphabetSize,
                                       final MultiSequenceMatcher matcher) {
        return suggestBlockSize(alphabetSize,
                                matcher.getMinimumLength(),
                                matcher.getSequenceMatchers().size());
    }
    
    
    public static int suggestBlockSize(final int alphabetSize,
                                       final int minimumLength, 
                                       final int numberOfSequences) {
        return getSafeBlockSize(minimumLength, (int) Math.ceil(
                calculatePossibleBlockSize(alphabetSize,
                                           minimumLength, 
                                           numberOfSequences)));
    }    
    
    
    public static int getSafeBlockSize(final int minimumLength, final int possibleBlockSize) {
        final int notGreaterThanMinimumLength = minimumLength < possibleBlockSize?
                                                minimumLength : possibleBlockSize;
        return notGreaterThanMinimumLength > 1 ? notGreaterThanMinimumLength : 1;        
    }
    
    
    /**
     * This formulae to calculate the optimum block size is suggested by
     * Wu and Manber.
     * 
     * @param minimumLength The minimum length of all sequences to be matched.
     * @param numberOfSequences The number of sequences to be matched.
     * @return The suggested block size for an efficient Wu Manber search.
     */
    public static double calculatePossibleBlockSize(final int alphabetSize,
                                                    final int minimumLength, 
                                                    final int numberOfSequences) {
        return logOfBase(alphabetSize, 2 * minimumLength * numberOfSequences);
    }    
    
    
    private static double logOfBase(final int base, final int number) {
        return Math.log(number) / Math.log(base);
    }    
    

}
