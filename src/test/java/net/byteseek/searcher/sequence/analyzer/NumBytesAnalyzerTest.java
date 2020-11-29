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

    /*
     * A pattern of length one is rejected as being good for the default analyzers.
     * The cut-off length is configurable if you instantiate your own NumBytesAnalyzer with different parameters.
     */
    @Test
    public void testLengthTooLowForwards() {
        SequenceMatcher matcher = new ByteSequenceMatcher(new byte[1]);
        BestSubsequence sequence = ANALYZER.getForwardsSubsequence(matcher);
        assertNull(sequence);

        sequence = ANALYZER_EXTEND.getForwardsSubsequence(matcher);
        assertNull(sequence);
    }

    /*
     * A pattern of length one is rejected as being good for the default analyzers.
     * The cut-off length is configurable if you instantiate your own NumBytesAnalyzer with different parameters.
     */
    @Test
    public void testLengthTooLowBackwards() {
        SequenceMatcher matcher = new ByteSequenceMatcher(new byte[1]);
        BestSubsequence sequence = ANALYZER.getBackwardsSubsequence(matcher);
        assertNull(sequence);

        sequence = ANALYZER_EXTEND.getBackwardsSubsequence(matcher);
        assertNull(sequence);
    }

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
     * The default analyzer won't accept there is a best subsequence if the length after removing wildcards is less
     * than the cut off (default is 2).
     * This is configurable if you instantiate a different NumBytesAnalyzer.
     */
    @Test
    public void testShortWildcardForwards() {
        assertNull(ANALYZER.getForwardsSubsequence(new ByteMatcherSequenceMatcher(OneByteMatcher.valueOf((byte) 2), AnyByteMatcher.ANY_BYTE_MATCHER)));
        assertNull(ANALYZER_EXTEND.getForwardsSubsequence(new ByteMatcherSequenceMatcher(OneByteMatcher.valueOf((byte) 2), AnyByteMatcher.ANY_BYTE_MATCHER)));
    }

    /**
     * The default analyzer won't accept there is a best subsequence if the length after removing wildcards is less
     * than the cut off (default is 2).
     * This is configurable if you instantiate a different NumBytesAnalyzer.
     */
    @Test
    public void testShortWildcardBackwards() {
        assertNull(ANALYZER.getBackwardsSubsequence(new ByteMatcherSequenceMatcher(OneByteMatcher.valueOf((byte) 2), AnyByteMatcher.ANY_BYTE_MATCHER)));
        assertNull(ANALYZER_EXTEND.getBackwardsSubsequence(new ByteMatcherSequenceMatcher(OneByteMatcher.valueOf((byte) 2), AnyByteMatcher.ANY_BYTE_MATCHER)));
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

}