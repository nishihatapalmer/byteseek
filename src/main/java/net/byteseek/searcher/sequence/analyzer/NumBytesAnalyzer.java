package net.byteseek.searcher.sequence.analyzer;

import net.byteseek.matcher.bytes.ByteMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;

public class NumBytesAnalyzer implements SequenceSearchAnalyzer {

    public static final SequenceSearchAnalyzer ANALYZER = new NumBytesAnalyzer(2, 64, false);
    public static final SequenceSearchAnalyzer ANALYZER_EXTEND = new NumBytesAnalyzer(2, 64, true);

    private final int threshold;
    private final int minLength;
    private final boolean extendSequence;

    /**
     * Constructs a NumBytesAnalyzer with the specified min length for a pattern and threshold number of bytes
     * to match in each part of the sequence.
     *
     * @param minLength The minimum length a subsequence can be.
     * @param threshold The maximum number of bytes which can match at any position in a good subsequence.
     */
    public NumBytesAnalyzer(final int minLength, final int threshold, final boolean extendSequence) {
        this.minLength = minLength;
        this.threshold = threshold;
        this.extendSequence = extendSequence;
    }

    @Override
    public BestSubsequence getForwardsSubsequence(final SequenceMatcher theSequence) {
        final BestSubsequence bestSubsequence = getBestSubsequence(theSequence);
        if (extendSequence && bestSubsequence != null && bestSubsequence.startPos > 0) {
            // Now extend the longest good sequence backwards until the start, or we hit an ANY match (.):
            // This is because longer sequences match faster, and if there are any bytes that could result in a longer
            // match, it's probably worth including them at the start of the subsequence (when searching forwards):
            final int extendedStart = findSequenceStartPos(theSequence, bestSubsequence.startPos - 1, 256);
            return new BestSubsequence(extendedStart, bestSubsequence.endPos);
        }
        return bestSubsequence;
    }

    @Override
    public BestSubsequence getBackwardsSubsequence(final SequenceMatcher theSequence) {
        final BestSubsequence bestSubsequence = getBestSubsequence(theSequence);
        if (extendSequence && bestSubsequence != null && bestSubsequence.endPos + 1 < theSequence.length()) {
            // Now extend the longest good sequence forwards until the end, or we hit an ANY match (.):
            // This is because longer sequences match faster, and if there are any bytes that could result in a longer
            // match, it's probably worth including them at the end of the subsequence (when searching backwards):
            final int extendedEnd = findBackwardsSequenceEndPos(theSequence, bestSubsequence.endPos + 1, 256);
            return new BestSubsequence(bestSubsequence.startPos, extendedEnd);
        }
        return bestSubsequence;
    }

    private BestSubsequence getBestSubsequence(final SequenceMatcher theSequence) {
        final int THRESHOLD = threshold;
        final int MIN_LENGTH = minLength;
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
     * Finds the first matcher which matches FEWER OR EQUAL bytes than the threshold value, and returns that byte position,
     * or -1 if no such byte matcher was found.
     *
     * @param theSequence The sequence to find a good subsequence for searching in.
     * @param posToSearchBackFrom The position to search in the sequence back from.
     * @param threshhold The number of bytes matching at a position under which we accept a good search subsequence.
     * @return The position of the end of a qgram that matches fewer bytes than the threshold, or -1 if no such position exists.
     */
    private int findSequenceEndPos(final SequenceMatcher theSequence, final int posToSearchBackFrom, final int threshhold) {
        for (int pos = posToSearchBackFrom; pos >= 0; pos--) {
            final ByteMatcher matcherForPos = theSequence.getMatcherForPosition(pos);
            if (matcherForPos.getNumberOfMatchingBytes() <= threshhold) {
                return pos;
            }
        }
        return -1;
    }

    /**
     * Finds the first byte which matches MORE than the threshold value, and returns the position after it,
     * or zero if no such byte exists.
     *
     * @param theSequence The sequence to find a good subsequence for searching in.
     * @param posToSearchBackFrom The position to search in the sequence back from.
     * @param threshold The number of bytes matching at a position under which we accept a good search subsequence.
     * @return The position after the first byte that exceeds the threshold, or 0 if they are all within the threshold.
     */
    private int findSequenceStartPos(final SequenceMatcher theSequence, final int posToSearchBackFrom, final int threshold) {
        for (int pos = posToSearchBackFrom; pos >= 0; pos--) {
            final ByteMatcher matcherForPos = theSequence.getMatcherForPosition(pos);
            if (matcherForPos.getNumberOfMatchingBytes() > threshold) {
                return pos + 1; // return the position after the byte matcher that exceeded the threshold.
            }
        }
        return 0;
    }

    /**
     * Finds the first byte which matches MORE than the threshold value, and returns the position before it,
     * or the last position in the sequence if no such byte exists.
     *
     * @param theSequence The sequence to find a good subsequence for searching in.
     * @param posToSearchFowardsFrom The position to search in the sequence back from.
     * @param threshold The number of bytes matching at a position under which we accept a good search subsequence.
     * @return The position after the first byte that exceeds the threshold, or 0 if they are all within the threshold.
     */
    private int findBackwardsSequenceEndPos(final SequenceMatcher theSequence, final int posToSearchFowardsFrom, final int threshold) {
        for (int pos = posToSearchFowardsFrom; pos < theSequence.length(); pos++) {
            final ByteMatcher matcherForPos = theSequence.getMatcherForPosition(pos);
            if (matcherForPos.getNumberOfMatchingBytes() > threshold) {
                return pos - 1;
            }
        }
        return theSequence.length() - 1;
    }
}