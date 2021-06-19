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

import net.byteseek.matcher.bytes.AnyByteMatcher;
import net.byteseek.matcher.bytes.ByteRangeMatcher;
import net.byteseek.matcher.bytes.OneByteMatcher;
import net.byteseek.matcher.sequence.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class NumBytesAnalyzerTest {

    private final static SequenceSearchAnalyzer ANALYZER = NumBytesAnalyzer.ANALYZER;
    private final static SequenceSearchAnalyzer ANALYZER_EXTEND = NumBytesAnalyzer.ANALYZER_EXTEND;

    /**
     * A pattern just containing single bytes should always return the entire sequence as the best subsequence.
     */
    @Test
    public void testSimpleForwardsSubsequence() {
        for (int i = 2; i < 100; i++) {
            SequenceMatcher matcher = new ByteSequenceMatcher(new byte[i]);
            BestSubsequence sequence = ANALYZER.getForwardsSubsequence(matcher);
            assertEquals(0, sequence.getStartPos());
            assertEquals(matcher.length() - 1, sequence.getEndPos());

            sequence = ANALYZER_EXTEND.getForwardsSubsequence(matcher);
            assertEquals(0, sequence.getStartPos());
            assertEquals(matcher.length() - 1, sequence.getEndPos());
        }
    }

    /**
     * A pattern just containing single bytes should always return the entire sequence as the best subsequence.
     */
    @Test
    public void testSimpleBackwardsSubsequence() {
        for (int i = 2; i < 100; i++) {
            SequenceMatcher matcher = new ByteSequenceMatcher(new byte[i]);
            BestSubsequence sequence = ANALYZER.getBackwardsSubsequence(matcher);
            assertEquals(0, sequence.getStartPos());
            assertEquals(matcher.length() - 1, sequence.getEndPos());

            sequence = ANALYZER_EXTEND.getBackwardsSubsequence(matcher);
            assertEquals(0, sequence.getStartPos());
            assertEquals(matcher.length() - 1, sequence.getEndPos());
        }
    }

    /*
     * Where wildcard "ANY" matchers exist at the end of a pattern that is being searched forwards, they interfere
     * with efficient searching, so the analyzer should figure out that it should not include the wildcard as part of
     * the best subsequence to search for.
     */
    @Test
    public void testWildcardAtEndOfForwardsSearch() {
        for (int i = 3; i < 100; i++) {
            SequenceMatcher wildcard = new FixedGapMatcher(i);
            SequenceMatcher matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher(new byte[i]), wildcard);
            BestSubsequence best = ANALYZER.getForwardsSubsequence(matcher);
            assertEquals(0, best.getStartPos());
            assertEquals(matcher.length() - i - 1, best.getEndPos());

            best = ANALYZER_EXTEND.getForwardsSubsequence(matcher);
            assertEquals(0, best.getStartPos());
            assertEquals(matcher.length() - i - 1, best.getEndPos());
        }
    }

    /*
     * Where wildcard "ANY" matchers exist at the start of a pattern that is being searched forwards, they interfere
     * with efficient searching, so the analyzer should figure out that it should not include the wildcard as part of
     * the best subsequence to search for.
     */
    @Test
    public void testWildcardAtStartOfForwardsSearch() {
        for (int i = 3; i < 100; i++) {
            SequenceMatcher wildcard = new FixedGapMatcher(i);
            SequenceMatcher matcher = new SequenceSequenceMatcher(wildcard, new ByteSequenceMatcher(new byte[i]));
            BestSubsequence best = ANALYZER.getForwardsSubsequence(matcher);
            assertEquals(i, best.getStartPos());
            assertEquals(matcher.length() - 1, best.getEndPos());

            best = ANALYZER_EXTEND.getForwardsSubsequence(matcher);
            assertEquals(i, best.getStartPos());
            assertEquals(matcher.length() - 1, best.getEndPos());
        }
    }

    /*
     * Where wildcard "ANY" matchers exist at the end of a pattern that is being searched backwards, they interfere
     * with efficient searching, so the analyzer should figure out that it should not include the wildcard as part of
     * the best subsequence to search for.
     */
    @Test
    public void testWildcardAtEndOfBackwardsSearch() {
        for (int i = 3; i < 100; i++) {
            SequenceMatcher wildcard = new FixedGapMatcher(i);
            SequenceMatcher matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher(new byte[i]), wildcard);
            BestSubsequence best = ANALYZER.getBackwardsSubsequence(matcher);
            assertEquals(0, best.getStartPos());
            assertEquals(matcher.length() - i - 1, best.getEndPos());

            best = ANALYZER_EXTEND.getBackwardsSubsequence(matcher);
            assertEquals("length: " + i, 0, best.getStartPos());
            assertEquals("length: " + i, matcher.length() - i - 1, best.getEndPos());
        }
    }

    /*
     * Where wildcard "ANY" matchers exist at the start of a pattern that is being searched backwards, they interfere
     * with efficient searching, so the analyzer should figure out that it should not include the wildcard as part of
     * the best subsequence to search for.
     */
    @Test
    public void testWildcardAtStartOfBackwardsSearch() {
        for (int i = 3; i < 100; i++) {
            SequenceMatcher wildcard = new FixedGapMatcher(i);
            SequenceMatcher matcher = new SequenceSequenceMatcher(wildcard, new ByteSequenceMatcher(new byte[i]));
            BestSubsequence best = ANALYZER.getBackwardsSubsequence(matcher);
            assertEquals(i, best.getStartPos());
            assertEquals(matcher.length() - 1, best.getEndPos());

            best = ANALYZER_EXTEND.getBackwardsSubsequence(matcher);
            assertEquals(i, best.getStartPos());
            assertEquals(matcher.length() - 1, best.getEndPos());
        }
    }

    /**
     * Test a single byte best subsequence with a single wildcard.
     */
    @Test
    public void testShortWildcardForwards() {
        BestSubsequence expected = new BestSubsequence(0, 0);
        assertEquals(expected, ANALYZER.getForwardsSubsequence(new ByteMatcherSequenceMatcher(OneByteMatcher.valueOf((byte) 2), AnyByteMatcher.ANY_BYTE_MATCHER)));
        assertEquals(expected, ANALYZER_EXTEND.getForwardsSubsequence(new ByteMatcherSequenceMatcher(OneByteMatcher.valueOf((byte) 2), AnyByteMatcher.ANY_BYTE_MATCHER)));
    }

    /**
     * Test a single byte best subsequence with a single wildcard.
     */
    @Test
    public void testShortWildcardBackwards() {
        BestSubsequence expected = new BestSubsequence(0, 0);
        assertEquals(expected, ANALYZER.getBackwardsSubsequence(new ByteMatcherSequenceMatcher(OneByteMatcher.valueOf((byte) 2), AnyByteMatcher.ANY_BYTE_MATCHER)));
        assertEquals(expected, ANALYZER_EXTEND.getBackwardsSubsequence(new ByteMatcherSequenceMatcher(OneByteMatcher.valueOf((byte) 2), AnyByteMatcher.ANY_BYTE_MATCHER)));
    }

    @Test
    public void testExtendSearchForwardsAtStart() {
        for (int i = 3; i < 100; i++) {
            SequenceMatcher extend = new ByteRangeMatcher(32,127).repeat(i); // make a sequence matching more than 64 bytes in each position
            SequenceMatcher matcher = new SequenceSequenceMatcher(extend, new ByteSequenceMatcher(new byte[i]));

            // simple analyzer rejects these.
            BestSubsequence best = ANALYZER.getForwardsSubsequence(matcher);
            assertEquals(i, best.getStartPos());
            assertEquals(matcher.length() - 1, best.getEndPos());

            // extended analyser includes them into the best subsequence, since they are in the right position.
            best = ANALYZER_EXTEND.getForwardsSubsequence(matcher);
            assertEquals(0, best.getStartPos());
            assertEquals(matcher.length() - 1, best.getEndPos());
        }
    }

    @Test
    public void testNotExtendSearchForwardsAtEnd() {
        for (int i = 3; i < 100; i++) {
            SequenceMatcher extend = new ByteRangeMatcher(32,127).repeat(i); // make a sequence matching more than 64 bytes in each position
            SequenceMatcher matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher(new byte[i]), extend);

            // simple analyzer rejects these.
            BestSubsequence best = ANALYZER.getForwardsSubsequence(matcher);
            assertEquals(0, best.getStartPos());
            assertEquals(matcher.length() - 1 - i, best.getEndPos());

            // extended analyser will not include them either as they as the end of a pattern searched forwards.
            best = ANALYZER_EXTEND.getForwardsSubsequence(matcher);
            assertEquals(0, best.getStartPos());
            assertEquals(matcher.length() - 1- i, best.getEndPos());
        }
    }

    @Test
    public void testExtendSearchBackwardsAtEnd() {
        for (int i = 3; i < 100; i++) {
            SequenceMatcher extend = new ByteRangeMatcher(32,127).repeat(i); // make a sequence matching more than 64 bytes in each position
            SequenceMatcher matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher(new byte[i]), extend);
            BestSubsequence best = ANALYZER.getBackwardsSubsequence(matcher);
            assertEquals(0, best.getStartPos());
            assertEquals(matcher.length() - i - 1, best.getEndPos());

            best = ANALYZER_EXTEND.getBackwardsSubsequence(matcher);
            assertEquals("length: " + i, 0, best.getStartPos());
            assertEquals("length: " + i, matcher.length() - 1, best.getEndPos());
        }
    }

    @Test
    public void testNotExtendSearchBackwardsAtStart() {
        for (int i = 3; i < 100; i++) {
            SequenceMatcher extend = new ByteRangeMatcher(32,127).repeat(i); // make a sequence matching more than 64 bytes in each position
            SequenceMatcher matcher = new SequenceSequenceMatcher(extend, new ByteSequenceMatcher(new byte[i]));
            BestSubsequence best = ANALYZER.getBackwardsSubsequence(matcher);
            assertEquals(i, best.getStartPos());
            assertEquals(matcher.length() - 1, best.getEndPos());

            best = ANALYZER_EXTEND.getBackwardsSubsequence(matcher);
            assertEquals(i, best.getStartPos());
            assertEquals(matcher.length() - 1, best.getEndPos());
        }
    }

    /**
     * Where we have wildcards in the middle of a pattern, the best subsequence is the longest one, excluding the wildcard in the middle.
     */
    @Test
    public void testWildcardsInMiddleForwardsSearch() {
        for (int i = 3; i < 100; i++) {
            SequenceMatcher wildcard = new FixedGapMatcher(i);
            SequenceMatcher matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher(new byte[i]), wildcard, new ByteSequenceMatcher(new byte[i+1]));

            BestSubsequence best = ANALYZER.getForwardsSubsequence(matcher);
            assertEquals(i * 2, best.getStartPos());
            assertEquals(matcher.length() - 1, best.getEndPos());

            best = ANALYZER_EXTEND.getForwardsSubsequence(matcher);
            assertEquals(i * 2, best.getStartPos());
            assertEquals(matcher.length() - 1, best.getEndPos());
        }
    }

    /**
     * Where we have a set of wildcards that match a lot, but not everything in the middle, it will be excluded by the
     * default ANALYZER, but the entire pattern will be matched for the extended analyzer.
     */
    @Test
    public void testWildcardsInMiddleForwardsSearchExtended() {
        for (int i = 3; i < 100; i++) {

            // Where longest subsequence is at the end of the forwards search, it can extend backwards.
            SequenceMatcher extend = new ByteRangeMatcher(32, 127).repeat(i);
            SequenceMatcher matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher(new byte[i]), extend, new ByteSequenceMatcher(new byte[i + 1]));

            BestSubsequence best = ANALYZER.getForwardsSubsequence(matcher);
            assertEquals(i * 2, best.getStartPos());
            assertEquals(matcher.length() - 1, best.getEndPos());

            best = ANALYZER_EXTEND.getForwardsSubsequence(matcher);
            assertEquals(0, best.getStartPos());
            assertEquals(matcher.length() - 1, best.getEndPos());

            // Where longest subsequence is at the start of the forwards search, it can't extend backwards as it's already at the
            // start of the pattern.
            matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher(new byte[i + 1]), extend, new ByteSequenceMatcher(new byte[i]));

            best = ANALYZER.getForwardsSubsequence(matcher);
            assertEquals(0, best.getStartPos());
            assertEquals(i, best.getEndPos());

            best = ANALYZER_EXTEND.getForwardsSubsequence(matcher);
            assertEquals(0, best.getStartPos());
            assertEquals(i, best.getEndPos());
        }
    }

    /**
     * Where we have wildcards in the middle of a pattern, the best subsequence is the longest one, excluding the wildcard in the middle.
     */
    @Test
    public void testWildcardsInMiddleBackwardsSearch() {
        for (int i = 3; i < 100; i++) {
            SequenceMatcher wildcard = new FixedGapMatcher(i);
            SequenceMatcher matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher(new byte[i]), wildcard, new ByteSequenceMatcher(new byte[i+1]));

            BestSubsequence best = ANALYZER.getBackwardsSubsequence(matcher);
            assertEquals(i * 2, best.getStartPos());
            assertEquals(matcher.length() - 1, best.getEndPos());

            best = ANALYZER_EXTEND.getBackwardsSubsequence(matcher);
            assertEquals(i * 2, best.getStartPos());
            assertEquals(matcher.length() - 1, best.getEndPos());
        }
    }

    /**
     * Where we have a set of wildcards that match a lot, but not everything in the middle, it will be excluded by the
     * default ANALYZER.  In the case that the longest pattern is at the start, the entire subsequence will be selected
     * by the extended analyzer.  When the longest non wildcard pattern is at the end, no extension will be performed,
     * since it cannot extend backwards from the end of entire pattern.
     */
    @Test
    public void testWildcardsInMiddleBackwardsSearchExtended() {
        for (int i = 3; i < 100; i++) {
            SequenceMatcher extend = new ByteRangeMatcher(32, 127).repeat(i);

            // Longest subsequence is at the end of the sequence:
            SequenceMatcher matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher(new byte[i]), extend, new ByteSequenceMatcher(new byte[i + 1]));

            BestSubsequence best = ANALYZER.getBackwardsSubsequence(matcher);
            assertEquals(i * 2, best.getStartPos());
            assertEquals(matcher.length() - 1, best.getEndPos());

            best = ANALYZER_EXTEND.getBackwardsSubsequence(matcher);
            assertEquals(i * 2, best.getStartPos());
            assertEquals(matcher.length() - 1, best.getEndPos());

            // Longest subsequence is at the start of the sequence:
            matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher(new byte[i + 1]), extend, new ByteSequenceMatcher(new byte[i]));

            best = ANALYZER.getBackwardsSubsequence(matcher);
            assertEquals(0, best.getStartPos());
            assertEquals(  i, best.getEndPos());

            best = ANALYZER_EXTEND.getBackwardsSubsequence(matcher);
            assertEquals(0, best.getStartPos());
            assertEquals(matcher.length() - 1, best.getEndPos());
        }
    }

    @Test
    public void testHighWildcardSubsequence() {
        testNoBestSubsequence(new SequenceSequenceMatcher(
                new FixedGapMatcher(1024),
                OneByteMatcher.valueOf((byte) 10),
                AnyByteMatcher.ANY_BYTE_MATCHER,
                new FixedGapMatcher(512)), 1024, 1024, 1024, 1024);

        testNoBestSubsequence(new SequenceSequenceMatcher(
                new ByteRangeMatcher(32, 127).repeat(64),
                AnyByteMatcher.ANY_BYTE_MATCHER,
                OneByteMatcher.valueOf((byte) 10),
                new ByteRangeMatcher(32, 127).repeat(127)), 65, 65, 65, 192);
    }

    private void testNoBestSubsequence(SequenceMatcher matcher, int startPos, int endPos, int extendStart, int extendEnd) {
        BestSubsequence expected = new BestSubsequence(startPos, endPos);
        BestSubsequence extendedForward = new BestSubsequence(extendStart, endPos);
        BestSubsequence extendedBackward = new BestSubsequence(startPos, extendEnd);
        assertEquals(matcher.toString(), expected, ANALYZER.getForwardsSubsequence(matcher));
        assertEquals(matcher.toString(), extendedForward, ANALYZER_EXTEND.getForwardsSubsequence(matcher));
        assertEquals(matcher.toString(), expected, ANALYZER.getBackwardsSubsequence(matcher));
        assertEquals(matcher.toString(), extendedBackward, ANALYZER_EXTEND.getBackwardsSubsequence(matcher));
    }

}