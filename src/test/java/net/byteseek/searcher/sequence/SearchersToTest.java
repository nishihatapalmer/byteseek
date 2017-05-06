package net.byteseek.searcher.sequence;

import net.byteseek.matcher.sequence.SequenceMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matt on 06/05/17.
 */
public class SearchersToTest {

    public List<SequenceSearcher<SequenceMatcher>> searchers;


    public void createSearchers(String sequence) {
        createSearchers(sequence.getBytes());
    }

    public void createSearchers(byte[] sequence) {
        searchers = new ArrayList<SequenceSearcher<SequenceMatcher>>();
        searchers.add(new SequenceMatcherSearcher(sequence));
        searchers.add(new SundayQuickSearcher(sequence));
        searchers.add(new HorspoolSearcher(sequence));
        searchers.add(new UnrolledHorspoolSearcher(sequence));
        searchers.add(new SignedHorspoolSearcher(sequence));
        searchers.add(new ShiftOrSearcher(sequence));
        searchers.add(new QgramFilter4Searcher(sequence));
    }
}
