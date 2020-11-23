/*
 * Copyright Matt Palmer 2020, All rights reserved.
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
package net.byteseek.searcher.sequence.analyzer;

import net.byteseek.matcher.sequence.SequenceMatcher;

/**
 * Analyzes a SequenceMatcher to determine the best subsequence to search for.
 * Different implementations should exist for different search algorithms, as they have different criteria
 * for the best subsequence to search for.
 * <p>
 * A best subsequence can be determined for searching both forwards and backwards, as they can be different for
 * some search algorithms.
 */
public interface SequenceSearchAnalyzer {

    /**
     * Provides the best subsequence to search for when searching forwards.
     * @param theSequence the seaquence to find the best subsequence in.
     * @return the best subsequence to search for when searching forwards.
     */
    BestSubsequence getForwardsSubsequence(SequenceMatcher theSequence);

    /**
     * Provides the best subsequence to search for when searching backwards.
     * @param theSequence the seaquence to find the best subsequence in.
     * @return the best subsequence to search for when searching backwards.
     */
    BestSubsequence getBackwardsSubsequence(SequenceMatcher theSequence);

}
