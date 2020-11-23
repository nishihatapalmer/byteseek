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

import net.byteseek.matcher.bytes.ByteMatcher;
import net.byteseek.matcher.sequence.ByteSequenceMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.searcher.bytes.ByteMatcherSearcher;
import net.byteseek.searcher.bytes.ByteSearcher;
import net.byteseek.searcher.sequence.SequenceSearcher;
import net.byteseek.utils.ArgUtils;

/**
 * A SequenceSearcherFactory that implements all the "no-brainer" decisions on what Searcher to use - for
 * anything that only has a length of one.  In these cases, there isn't much competition - a simple dedicated
 * searcher for them will outperform anything more complex - and use less memory too.
 * <p>
 * Subclasses must implement the createSequenceSearcher(SequenceMatcher theMatcher) method, to create an appropriate
 * sequence searcher for any sequence longer than one.
 */
public abstract class AbstractSequenceFactory implements SequenceSearcherFactory {

    @Override
    public SequenceSearcher create(final byte theByte) {
        return new ByteSearcher(theByte);
    }

    @Override
    public SequenceSearcher create(final byte[] theBytes) {
        ArgUtils.checkNullOrEmptyByteArray(theBytes, "theBytes");
        if (theBytes.length == 1) {
            return create(theBytes[0]);
        }
        // Note - a sequence of bytes with no wildcards will match equally well forwards or backwards.
        return createForwards(new ByteSequenceMatcher(theBytes));
    }

    @Override
    public SequenceSearcher create(final ByteMatcher theMatcher) {
        ArgUtils.checkNullObject(theMatcher, "theMatcher");
        if (theMatcher.getNumberOfMatchingBytes() == 1) {
            return create(theMatcher.getMatchingBytes()[0]);
        }
        return new ByteMatcherSearcher(theMatcher);
    }

    @Override
    public SequenceSearcher createForwards(final SequenceMatcher theSequence) {
        ArgUtils.checkNullObject(theSequence, "theSequence");
        if (theSequence.length() == 1) {
            return create(theSequence.getMatcherForPosition(0));
        }
        return createForwardsSequenceSearcher(theSequence);
    }

    @Override
    public SequenceSearcher createBackwards(final SequenceMatcher theSequence) {
        ArgUtils.checkNullObject(theSequence, "theSequence");
        if (theSequence.length() == 1) {
            return create(theSequence.getMatcherForPosition(0));
        }
        return createBackwardsSequenceSearcher(theSequence);
    }

    /**
     * Create a searcher for a sequence greater than one in length.
     *
     * @param theSequence The sequence greater than one in length.
     * @return A SequenceSearcher for that sequence.
     */
    protected abstract SequenceSearcher createForwardsSequenceSearcher(SequenceMatcher theSequence);

    /**
     * Create a searcher for a sequence greater than one in length.
     *
     * @param theSequence The sequence greater than one in length.
     * @return A SequenceSearcher for that sequence.
     */
    protected abstract SequenceSearcher createBackwardsSequenceSearcher(SequenceMatcher theSequence);
}
