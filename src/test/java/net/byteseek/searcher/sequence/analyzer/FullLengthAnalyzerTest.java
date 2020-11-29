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

import net.byteseek.matcher.sequence.ByteSequenceMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import org.junit.Test;

import static org.junit.Assert.*;

public class FullLengthAnalyzerTest {

    private final static SequenceSearchAnalyzer ANALYZER = new FullLengthAnalyzer();

    @Test
    public void getForwardsSubsequence() {
        for (int i = 1; i < 100; i++) {
            SequenceMatcher matcher = new ByteSequenceMatcher(new byte[i]);
            BestSubsequence sequence = ANALYZER.getForwardsSubsequence(matcher);
            assertEquals(0, sequence.getStartPos());
            assertEquals(matcher.length() - 1, sequence.getEndPos());
        }
    }

    @Test
    public void getBackwardsSubsequence() {
        for (int i = 1; i < 100; i++) {
            SequenceMatcher matcher = new ByteSequenceMatcher(new byte[i]);
            BestSubsequence sequence = ANALYZER.getBackwardsSubsequence(matcher);
            assertEquals(0, sequence.getStartPos());
            assertEquals(matcher.length() - 1, sequence.getEndPos());
        }
    }
}