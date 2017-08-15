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
 */
package net.byteseek.searcher.sequence;

import net.byteseek.matcher.bytes.ByteMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.searcher.bytes.ByteMatcherSearcher;
import net.byteseek.searcher.bytes.ByteSearcher;
import net.byteseek.utils.ArgUtils;

public class SequenceSearcherSimpleFactory implements SequenceSearcherFactory {

    public final static SequenceSearcherFactory DEFAULT_FACTORY = new SequenceSearcherSimpleFactory();

    @Override
    public SequenceSearcher create(final byte theByte) {
        return new ByteSearcher(theByte);
    }

    @Override
    public SequenceSearcher create(final ByteMatcher theMatcher) {
        ArgUtils.checkNullObject(theMatcher, "theMatcher");
        if (theMatcher.getNumberOfMatchingBytes() == 1) {
            return new ByteSearcher(theMatcher.getMatchingBytes()[0]);
        }
        return new ByteMatcherSearcher(theMatcher);
    }

    @Override
    public SequenceSearcher create(final SequenceMatcher theSequence) {
        ArgUtils.checkNullObject(theSequence, "theSequence");
        final int sequenceLength = theSequence.length();
        if (sequenceLength == 1) {
            create(theSequence.getMatcherForPosition(0));
        }
        if (sequenceLength < 12) { //TODO: validate this position:
            return new ShiftOrUnrolledSearcher(theSequence);
        }
        //TODO: validate that this is the best choice in general with profiling.  Qgram filtering is also fast.
        return new SignedHash2Searcher(theSequence);
    }
}
