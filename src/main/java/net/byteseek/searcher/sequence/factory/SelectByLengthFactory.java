/*
 * Copyright Matt Palmer 2017-19, All rights reserved.
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
import net.byteseek.utils.ArgUtils;

/**
 * A SequenceSearcherFactory that selects the best searcher on the basis of the pattern length only.
 * It does not attempt to analyse the patterns in any way to see if other searchers may be better,
 * e.g. in a low alphabet situation or where the pattern is complex.
 */
public final class SelectByLengthFactory extends AbstractSequenceFactory {

    /**
     * A SequenceSearcherFactory which selects the best searcher on the basis of the length of the pattern to match.
     * In most cases this should give fairly good performance, but may perform poorly on low alphabet searches,
     * e.g. on DNA, or where the pattern to match contains large byte sets towards the end of the pattern.
     */
    //TODO: profile or validate this as a good choice - length of 12 and the two options.
    //        where does it not perform well?  what tweaks should we make for a default using this strategy?
    public final static SequenceSearcherFactory SHIFTOR_THEN_SIGNEDHASH =
            new SelectByLengthFactory(SearcherFactories.SHIFTOR_UNROLLED_FACTORY,
                    SearcherFactories.SIGNED_HASH2_FACTORY, 12);

    private final int longSize;
    private final SequenceSearcherFactory shortFactory;
    private final SequenceSearcherFactory longFactory;

    /**
     * Creates a SelectByLengthFactory given the factory for short sequences, the factory for long sequences,
     * and the size of a long sequence.
     *
     * @param shortFactory The searcher factory to use for short sequences.
     * @param longFactory The searcher factory to use for long sequences.
     * @param longSize The smallest size of a long sequence.
     */
    public SelectByLengthFactory(final SequenceSearcherFactory shortFactory,
                                 final SequenceSearcherFactory longFactory,
                                 final int longSize) {
        ArgUtils.checkNullObject(shortFactory, "shortFactory");
        ArgUtils.checkNullObject(longFactory,"longFactory");
        this.shortFactory = shortFactory;
        this.longFactory = longFactory;
        this.longSize = longSize;
    }

    @Override
    protected SequenceSearcher createSequenceSearcher(final SequenceMatcher theSequence) {
        if (theSequence.length() < longSize) {
            return shortFactory.create(theSequence);
        }
        return longFactory.create(theSequence);
    }
}
