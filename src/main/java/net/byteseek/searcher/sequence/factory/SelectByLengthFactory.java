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
package net.byteseek.searcher.sequence.factory;

import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.searcher.sequence.SequenceSearcher;
import net.byteseek.searcher.sequence.ShiftOrUnrolledSearcher;
import net.byteseek.searcher.sequence.SignedHash2Searcher;
import net.byteseek.utils.ArgUtils;

/**
 * A SequenceSearcherFactory that selects the best searcher on the basis of the pattern length.
 */
public final class SelectByLengthFactory extends AbstractSequenceFactory {

    @Override
    protected SequenceSearcher createSequenceSearcher(final SequenceMatcher theSequence) {
        if (theSequence.length() < 12) { //PROFILE: validate this position with profling.  It's *roughly* right, but should be checked.
            return new ShiftOrUnrolledSearcher(theSequence);
        }
        //PROFILE: validate that this is the best choice in general with profiling.  Qgram filtering is also fast, and signed and unrolledHorspool.
        return new SignedHash2Searcher(theSequence);
    }
}
