/*
 * Copyright Matt Palmer 2011-2012, All rights reserved.
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

import net.byteseek.matcher.multisequence.MultiSequenceMatcher;

/**
 * WuManberUtils implements some simple methods to suggest an optimal block
 * size for use with the Wu-Manber search algorithms.
 * 
 * @author Matt Palmer
 */

public final class WuManberUtils {
   
	private WuManberUtils() {
	}

    /**
     * Suggest a safe block size for a MultiSequenceMatcher, using a default alphabet
     * size of 256 (all byte values).
     * 
     * @param matcher The MultiSequenceMatcher to suggest a block size for.
     * @return A suggested block size for WuManber searching.
     */
    public static int suggestBlockSize(final MultiSequenceMatcher matcher) {
        return suggestBlockSize(256, matcher);
    }
    
    
    /**
     * Suggest a safe block size for a MultiSequenceMatcher given an alphabet size.
     * For example, if searching for DNA sequences, the alphabet size is only 4
     * (ACGT), so you should specify 4 for the alphabet size.  This will influence
     * the optimal block size chosen.
     * 
     * @param alphabetSize The size of the alphabet in the text you are searching in.
     * @param matcher The MultiSequenceMatcher to suggest a block size for.
     * @return A suggested block size for WuManber searching.
     */
    public static int suggestBlockSize(final int alphabetSize,
                                       final MultiSequenceMatcher matcher) {
        return suggestBlockSize(alphabetSize,
                                matcher.getMinimumLength(),
                                matcher.getSequenceMatchers().size());
    }
    
    
    /**
     * Suggest a safe block size for a given alphabet size, the minimum length of
     * the sequences you want to match, and the number of sequences you want to
     * match.
     * 
     * @param alphabetSize The size of the alphabet in the text you are searching in.
     * @param minimumLength The minimum length of all the sequences you want to search for.
     * @param numberOfSequences The number of sequences you want to search for.
     * @return A suggested block size for WuManber searching.
     */
    public static int suggestBlockSize(final int alphabetSize,
                                       final int minimumLength, 
                                       final int numberOfSequences) {
        return getSafeBlockSize(minimumLength, (int) Math.ceil(
                calculatePossibleBlockSize(alphabetSize,
                                           minimumLength, 
                                           numberOfSequences)));
    }    
    
    
    /**
     * Given the minimum length of all sequences to be searched for, and a possible
     * block size, return a safe block size.  The block size cannot be greater than the
     * minimum length of the sequences, and must be at least one.
     * 
     * @param minimumLength The minimum length of all the sequences you want to search for.
     * @param possibleBlockSize A suggested block size.
     * @return A safe block size given the minimum length and possible block size.
     */
    public static int getSafeBlockSize(final int minimumLength, final int possibleBlockSize) {
        final int notGreaterThanMinimumLength = minimumLength < possibleBlockSize?
                                                minimumLength : possibleBlockSize;
        return notGreaterThanMinimumLength > 1 ? notGreaterThanMinimumLength : 1;        
    }
    
    
    /**
     * This formulae to calculate the optimum block size is given by
     * Wu and Manber.  It may not always return a safe block size, but it will give
     * a reasonable indication of a good block size to use.
     * 
     * @param alphabetSize  The size of the alphabet in the text you are searching in.
     * @param minimumLength The minimum length of all sequences to be matched.
     * @param numberOfSequences The number of sequences to be matched.
     * @return The suggested block size for an efficient Wu Manber search.
     */
    public static double calculatePossibleBlockSize(final int alphabetSize,
                                                    final int minimumLength, 
                                                    final int numberOfSequences) {
        return logOfBase(alphabetSize, 2 * minimumLength * numberOfSequences);
    }    
    
    
    /**
     * Returns the log of a number to a given base.
     * 
     * @param base The base of the number
     * @param number The number to return the log to that base.
     * @return The log of a number to a given base.
     */
    private static double logOfBase(final int base, final int number) {
        return Math.log(number) / Math.log(base);
    }    
    

}
