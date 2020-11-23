package net.byteseek.searcher.sequence;

import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.MatchResult;
import net.byteseek.matcher.sequence.SequenceMatcher;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class SubsequenceSearcher extends AbstractSequenceSearcher {

    private final SequenceMatcher subSequence;
    private final SequenceSearcher searcher;
    private final SequenceMatcher leftMatch;
    private final SequenceMatcher rightMatch;
    private final int leftOffset;
    private final int rightOffset;

    public SubsequenceSearcher(final SequenceMatcher subSequence, final SequenceSearcher searcher,
                               final SequenceMatcher leftMatch, final SequenceMatcher rightMatch) {
        super(subSequence);
        this.subSequence = subSequence;
        this.searcher = searcher;
        this.leftMatch = leftMatch;
        this.rightMatch = rightMatch;
        this.leftOffset = leftMatch == null? 0 : leftMatch.length();
        this.rightOffset = rightMatch == null? 0 : rightMatch.length();
    }


    @Override
    protected int getSequenceLength() {
        return subSequence.length();
    }

    //TODO; need to keep looping within the search sequence.

    @Override
    public long searchSequenceForwards(WindowReader reader, long fromPosition, long toPosition) throws IOException {
        long result = searcher.searchSequenceForwards(reader, fromPosition + leftOffset, toPosition - rightOffset);
        if (result >= 0) {
            if (leftMatch != null) {

            }
        }
        return result;
    }

    @Override
    public int searchSequenceForwards(byte[] bytes, int fromPosition, int toPosition) {
    }

    @Override
    public long searchSequenceBackwards(WindowReader reader, long fromPosition, long toPosition) throws IOException {
    }

    @Override
    public int searchSequenceBackwards(byte[] bytes, int fromPosition, int toPosition) {
    }

    @Override
    public void prepareForwards() {
        searcher.prepareForwards();
    }

    @Override
    public void prepareBackwards() {
        searcher.prepareBackwards();
    }
}
