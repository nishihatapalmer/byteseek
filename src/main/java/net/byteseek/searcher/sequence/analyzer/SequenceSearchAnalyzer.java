package net.byteseek.searcher.sequence.analyzer;

import net.byteseek.matcher.sequence.SequenceMatcher;

public interface SequenceSearchAnalyzer {

    BestSubsequence getForwardsSubsequence(SequenceMatcher theSequence);
    BestSubsequence getBackwardsSubsequence(SequenceMatcher theSequence);

}
