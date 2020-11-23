package net.byteseek.searcher.sequence.analyzer;

import net.byteseek.matcher.sequence.SequenceMatcher;

public class FullLengthAnalyzer implements SequenceSearchAnalyzer {

    public static final SequenceSearchAnalyzer ANALYZER = new FullLengthAnalyzer();

    @Override
    public BestSubsequence getForwardsSubsequence(SequenceMatcher theSequence) {
        return new BestSubsequence(0, theSequence.length() - 1);
    }

    @Override
    public BestSubsequence getBackwardsSubsequence(SequenceMatcher theSequence) {
        return new BestSubsequence(0, theSequence.length() - 1);
    }
}
