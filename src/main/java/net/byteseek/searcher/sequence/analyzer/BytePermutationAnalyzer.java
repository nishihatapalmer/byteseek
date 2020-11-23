package net.byteseek.searcher.sequence.analyzer;

import net.byteseek.matcher.bytes.ByteMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;

public class BytePermutationAnalyzer implements SequenceSearchAnalyzer {

    public static final SequenceSearchAnalyzer ANALYZER2 = new BytePermutationAnalyzer(2, 128, false);
    public static final SequenceSearchAnalyzer ANALYZER2_EXTEND = new BytePermutationAnalyzer(2, 128, true);
    public static final SequenceSearchAnalyzer ANALYZER3 = new BytePermutationAnalyzer(3, 128, false);
    public static final SequenceSearchAnalyzer ANALYZER3_EXTEND = new BytePermutationAnalyzer(3, 128,true);

    private final int qGramSize;
    private final int threshold;
    private final boolean extendSequence;

    public BytePermutationAnalyzer(final int qGramSize, final int threshold, final boolean extendSequence) {
        this.qGramSize = qGramSize;
        this.threshold = threshold;
        this.extendSequence = extendSequence;
    }

    @Override
    public BestSubsequence getForwardsSubsequence(SequenceMatcher theSequence) {
        final BestSubsequence bestSubsequence = getBestSubsequence(theSequence);
        if (extendSequence && bestSubsequence != null && bestSubsequence.startPos > 0) {
            // Now extend the longest good sequence backwards until the start, or we hit an ANY match (.):
            // This is because longer sequences match faster, and if there are any bytes that could result in a longer
            // match, it's probably worth including them at the start of the subsequence (when searching forwards):
            final int extendedStart = findSequenceStartPos(theSequence, bestSubsequence.startPos - 1, 4096); //TOD: validate thresholds.
            return new BestSubsequence(extendedStart, bestSubsequence.endPos);
        }
        return bestSubsequence;
    }

    @Override
    public BestSubsequence getBackwardsSubsequence(SequenceMatcher theSequence) {
        final BestSubsequence bestSubsequence = getBestSubsequence(theSequence);
        if (extendSequence && bestSubsequence != null && bestSubsequence.endPos + 1 < theSequence.length()) {
            // Now extend the longest good sequence forwards until the end, or we hit an ANY match (.):
            // This is because longer sequences match faster, and if there are any bytes that could result in a longer
            // match, it's probably worth including them at the end of the subsequence (when searching backwards):
            final int extendedEnd = findBackwardsSequenceEndPos(theSequence, bestSubsequence.endPos + 1, 4096);
            return new BestSubsequence(bestSubsequence.startPos, extendedEnd);
        }
        return bestSubsequence;
    }

    public BestSubsequence getBestSubsequence(SequenceMatcher theSequence) {
        final int THRESHOLD = threshold;
        final int MIN_LENGTH = qGramSize;
        final int LENGTH = theSequence.length();
        int position = LENGTH - 1;
        int bestEnd = -1;
        int bestStart = -1;
        int bestLength = -1;
        while (position >=0 ) {
            int sequenceEndPos = findSequenceEndPos(theSequence, position, THRESHOLD);
            if (sequenceEndPos == -1) {
                break; // stop searching, no good end pos exists.
            }
            int sequenceStartPos = findSequenceStartPos(theSequence, sequenceEndPos - 1, THRESHOLD);
            final int SEQUENCE_LENGTH = sequenceEndPos - sequenceStartPos + 1;
            if (SEQUENCE_LENGTH > bestLength && SEQUENCE_LENGTH >= MIN_LENGTH) {
                bestStart = sequenceStartPos;
                bestEnd = sequenceEndPos;
                bestLength = SEQUENCE_LENGTH;
            }
            position = Math.min(sequenceStartPos - 1, sequenceEndPos - 1); // start looking again just behind the last qgram we checked.
        }
        // If we did not find any good subsequence, return null - there is no good subsequence to search for.
        if (bestLength == -1 || bestStart > bestEnd) {
            return null;
        }
        return new BestSubsequence(bestStart, bestEnd);
    }

    /**
     * Finds the first qgram which matches FEWER bytes than the threshold value, and returns the end of that qgram.
     *
     * @param theSequence The sequence to find a good subsequence for searching in.
     * @param posToSearchBackFrom The position to search in the sequence back from.
     * @param threshhold The number of bytes matching at a position under which we accept a good search subsequence.
     * @return The position of the end of a qgram that matches fewer bytes than the threshold, or -1 if no such position exists.
     */
    private int findSequenceEndPos(SequenceMatcher theSequence, int posToSearchBackFrom, int threshhold) {
        final int QGRAM_THRESHOLD = threshhold * qGramSize;
        for (int pos = posToSearchBackFrom; pos >= qGramSize - 1; pos--) {
            int bytePermutationsMatched = 1;
            for (int qGramPos = pos; qGramPos > pos - qGramSize; qGramPos--) {
                final ByteMatcher matcherForPos = theSequence.getMatcherForPosition(qGramPos);
                bytePermutationsMatched *= matcherForPos.getNumberOfMatchingBytes();
            }
            if (bytePermutationsMatched < QGRAM_THRESHOLD) {
                return pos; // return the end of the qgram we found.
            }
        }
        return -1;
    }

    /**
     * Finds the first qgram which matches MORE bytes than the threshold value, and returns the position after it.
     *
     * @param theSequence The sequence to find a good subsequence for searching in.
     * @param posToSearchBackFrom The position to search in the sequence back from.
     * @param threshhold The number of bytes matching at a position under which we accept a good search subsequence.
     * @return The position of the start of the qgram after a qgram that exceeds the threshold, or 0 if they are all within the threshold.
     */
    private int findSequenceStartPos(SequenceMatcher theSequence, int posToSearchBackFrom, int threshhold) {
        final int QGRAM_THRESHOLD = threshhold * qGramSize;
        for (int pos = posToSearchBackFrom; pos >= qGramSize - 1; pos--) {
            int bytePermutationsMatched = 1;
            for (int qGramPos = pos; qGramPos > pos - qGramSize; qGramPos--) {
                final ByteMatcher matcherForPos = theSequence.getMatcherForPosition(qGramPos);
                bytePermutationsMatched *= matcherForPos.getNumberOfMatchingBytes();
            }
            if (bytePermutationsMatched > QGRAM_THRESHOLD) {
                return pos - qGramSize + 2; // return the position after the start of the qgram that exceeded the threshold.
            }
        }
        return 0;
    }

    /**
     * Finds the first qgram which matches MORE bytes than the threshold value, and returns the position after it.
     *
     * @param theSequence The sequence to find a good subsequence for searching in.
     * @param posToSearchFowardsFrom The position to search in the sequence back from.
     * @param threshhold The number of bytes matching at a position under which we accept a good search subsequence.
     * @return The position of the start of the qgram after a qgram that exceeds the threshold, or 0 if they are all within the threshold.
     */
    private int findBackwardsSequenceEndPos(SequenceMatcher theSequence, int posToSearchFowardsFrom, int threshhold) {
        final int QGRAM_THRESHOLD = threshhold * qGramSize;
        final int LENGTH = theSequence.length();
        final int SEARCH_START = posToSearchFowardsFrom < qGramSize - 1? qGramSize - 1 : posToSearchFowardsFrom;
        for (int pos = SEARCH_START; pos < LENGTH; pos++) {
            int bytePermutationsMatched = 1;
            for (int qGramPos = pos; qGramPos >= pos - qGramSize; qGramPos--) {
                final ByteMatcher matcherForPos = theSequence.getMatcherForPosition(qGramPos);
                bytePermutationsMatched *= matcherForPos.getNumberOfMatchingBytes();
            }
            if (bytePermutationsMatched > QGRAM_THRESHOLD) {
                return pos - qGramSize + 2; // return the position after the start of the qgram that exceeded the threshold.
            }
        }
        return LENGTH - 1;
    }
}