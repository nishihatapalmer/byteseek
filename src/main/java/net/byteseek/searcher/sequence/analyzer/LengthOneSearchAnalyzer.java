package net.byteseek.searcher.sequence.analyzer;

import net.byteseek.matcher.sequence.SequenceMatcher;

public class LengthOneSearchAnalyzer implements SequenceSearchAnalyzer {

    public static SequenceSearchAnalyzer ANALYZER = new LengthOneSearchAnalyzer();

    private static final BestSubsequence BEST_LENGTH_ONE = new BestSubsequence(0, 0);

    @Override
    public BestSubsequence getForwardsSubsequence(SequenceMatcher theSequence) {
        if (theSequence.length() == 1) {
            return BEST_LENGTH_ONE;
        }
        return null;
    }

    @Override
    public BestSubsequence getBackwardsSubsequence(SequenceMatcher theSequence) {
        if (theSequence.length() == 1) {
            return BEST_LENGTH_ONE;
        }
        return null;
    }
}
