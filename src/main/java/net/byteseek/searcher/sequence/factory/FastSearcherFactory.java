/*
 * Copyright Matt Palmer 2017-20, All rights reserved.
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
 */
package net.byteseek.searcher.sequence.factory;

import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.searcher.sequence.SequenceSearcher;
import net.byteseek.searcher.sequence.SubSequenceSearcher;
import net.byteseek.searcher.sequence.analyzer.BestSubsequence;
import net.byteseek.searcher.sequence.analyzer.SequenceSearchAnalyzer;
import net.byteseek.searcher.sequence.analyzer.SequenceSearchAnalyzers;
import net.byteseek.utils.ArgUtils;

/**
 * A SequenceSearcherFactory that selects the best searcher on the basis of the pattern length and complexity.
 *
 * Patterns of length 1 will get a ByteSearcher to search for a single byte value,
 * or a ByteMatcherSearcher if more than one byte can match, e.g. a range of bytes.
 *
 * Patterns longer than 1 but which fall below a set length get a search that works efficiently for all short patterns, no matter how complex they are.
 *
 * Patterns over that length are analysed further to determine the best sub-pattern factor to search for, since wildcards strongly
 * affect the performance of these search algorithms.  If there is a factor which is longer than the set length, then the long
 * search algorithm is selected, otherwise we fall back to the short pattern matcher.
 *
 * Where the best factor is not the entire sequence, the searcher will be wrapped in a searcher that matches the rest of
 * the sequence once a factor has been identified in a search, only returning results which match both the factor and the other bits.
 */
public final class FastSearcherFactory extends AbstractSequenceFactory {

    //TODO: profile and validate best combinations.

    /**
     * A SequenceSearcherFactory which selects either a SHIFT_OR_UNROLLED searcher for short patterns,
     * or a SIGNED_HORSPOOL searcher for patterns longer than 12.
     */
    public final static SequenceSearcherFactory SHIFTOR_12_THEN_SIGNED_HORSPOOL =
            new FastSearcherFactory(SearcherFactories.SHIFTOR_UNROLLED_FACTORY, SearcherFactories.SHIFTOR_UNROLLED_FACTORY,
                    SearcherFactories.SIGNED_HORSPOOL_FACTORY, SequenceSearchAnalyzers.SIGNED_HORSPOOL_ANALYZER, 12);

    /**
     * A SequenceSearcherFactory which selects either a SHIFT_OR_UNROLLED searcher for short patterns,
     * or a SIGNED_HASH_2 searcher for patterns longer than 12.
     */
    public final static SequenceSearcherFactory SHIFTOR_12_THEN_SIGNEDHASH2 =
            new FastSearcherFactory(SearcherFactories.SHIFTOR_UNROLLED_FACTORY, SearcherFactories.SHIFTOR_UNROLLED_FACTORY,
                    SearcherFactories.SIGNED_HASH2_FACTORY, SequenceSearchAnalyzers.SIGNED_HASH2_ANALYZER, 12);

    /**
     * A SequenceSearcherFactory which selects either a SHIFT_OR_UNROLLED searcher for short patterns,
     * or a SIGNED_HASH_3 searcher for patterns longer than 12.
     */
    public final static SequenceSearcherFactory SHIFTOR_12_THEN_SIGNEDHASH3 =
            new FastSearcherFactory(SearcherFactories.SHIFTOR_UNROLLED_FACTORY, SearcherFactories.SHIFTOR_UNROLLED_FACTORY,
                    SearcherFactories.SIGNED_HASH3_FACTORY, SequenceSearchAnalyzers.SIGNED_HASH3_ANALYZER, 12);


    private final SequenceSearcherFactory shortFactory;
    private final SequenceSearcherFactory wildcardFactory;
    private final SequenceSearcherFactory longFactory;
    private final SequenceSearchAnalyzer longAnalyzer;
    private final int longSize;

    /**
     * Creates a FastSearcherFactory given a factory for short sequences, a factory for long sequences,
     * the minimum size of a long sequence, and an analyzer which provides efficient subsequences for the long searcher.
     *
     * @param shortFactory The searcher factory to use for short sequences.
     * @param wildcardFactory The searcher factory to use for sequences containing high numbers of wildcards.
     * @param longFactory The searcher factory to use for long sequences.
     * @param longAnalyzer the search analzyer for the long searcher to determine the best searchable subsequence.
     * @param longSize The smallest size of a long sequence.
     * @throws IllegalArgumentException if the objects are null, or the integer parameters are less than one.
     */
    public FastSearcherFactory(final SequenceSearcherFactory shortFactory,
                               final SequenceSearcherFactory wildcardFactory,
                               final SequenceSearcherFactory longFactory,
                               final SequenceSearchAnalyzer longAnalyzer,
                               final int longSize) {
        ArgUtils.checkNullObject(shortFactory, "shortFactory");
        ArgUtils.checkNullObject(wildcardFactory, "wildcardFactory");
        ArgUtils.checkNullObject(longFactory,"longFactory");
        ArgUtils.checkNullObject(longAnalyzer, "longAnalyzer");
        ArgUtils.checkPositiveInteger(longSize, "longSize");
        this.shortFactory = shortFactory;
        this.wildcardFactory = wildcardFactory;
        this.longFactory = longFactory;
        this.longAnalyzer = longAnalyzer;
        this.longSize = longSize;
    }

    @Override
    protected SequenceSearcher createForwardsSequenceSearcher(final SequenceMatcher theSequence) {
        // If it's a short pattern, use the short factory:
        if (theSequence.length() < longSize) {
            return shortFactory.createForwards(theSequence);
        }

        // Find the best subsequence in the sequence to search for with a long searcher:
        final BestSubsequence bestSubsequence = longAnalyzer.getForwardsSubsequence(theSequence);

        // If no good subsequence exists for a long search exists, use the wildcard factory.
        // The only reason a reasonably long sequence has no good subsequence is because it is composed of
        // wildcards that match a lot of bytes in most positions.
        if (bestSubsequence == null) {
            return wildcardFactory.createForwards(theSequence);
        }

        // If the best subsequence is the entire sequence, then search for all of it with the long searcher
        if (bestSubsequence.length() == theSequence.length()) {
            return longFactory.createForwards(theSequence);
        }

        // Return a searcher that looks for the best subsequence, then matches the bits which aren't part of the subsequence.
        // If the subsequence is a short pattern, use the short factory, otherwise use the long factory.
        final SequenceMatcher subSequence = theSequence.subsequence(bestSubsequence.getStartPos(), bestSubsequence.getEndPos() + 1);
        final SequenceMatcher leftMatcher = bestSubsequence.getStartPos() > 0?
                theSequence.subsequence(0, bestSubsequence.getStartPos()) : null;
        final SequenceMatcher rightMatcher = bestSubsequence.getEndPos() + 1 < theSequence.length()?
                theSequence.subsequence(bestSubsequence.getEndPos() + 1, theSequence.length()) : null;
        return new SubSequenceSearcher(subSequence, bestSubsequence.length() < longSize ? shortFactory : longFactory, leftMatcher, rightMatcher);
    }

    @Override
    protected SequenceSearcher createBackwardsSequenceSearcher(final SequenceMatcher theSequence) {
        // If we're short, use the short factory:
        if (theSequence.length() < longSize) {
            return shortFactory.createBackwards(theSequence);
        }

        // Find the best subsequence to use for the long matcher:
        final BestSubsequence bestSubsequence = longAnalyzer.getBackwardsSubsequence(theSequence);

        // If no good subsequence for a long search exists, use the wildcard factory.
        // The only reason a reasonably long sequence has no good subsequence is because it is composed of
        // wildcards that match a lot of bytes in most positions.
        if (bestSubsequence == null) {
            return wildcardFactory.createBackwards(theSequence);
        }

        // If the best subsequence is the entire sequence, then search for all of it with the long searcher
        if (bestSubsequence.length() == theSequence.length()) {
            return longFactory.createBackwards(theSequence);
        }

        // Return a searcher that looks for the best subsequence, then matches the bits which aren't part of the subsequence.
        // If the subsequence is a short pattern, use the short factory, otherwise use the long factory.
        final SequenceMatcher subSequence = theSequence.subsequence(bestSubsequence.getStartPos(), bestSubsequence.getEndPos() + 1);
        final SequenceMatcher leftMatcher = bestSubsequence.getStartPos() > 0?
                theSequence.subsequence(0, bestSubsequence.getStartPos()) : null;
        final SequenceMatcher rightMatcher = bestSubsequence.getEndPos() + 1 < theSequence.length()?
                theSequence.subsequence(bestSubsequence.getEndPos() + 1, theSequence.length()) : null;
        return new SubSequenceSearcher(subSequence, bestSubsequence.length() < longSize ? shortFactory : longFactory, leftMatcher, rightMatcher);
    }

}
