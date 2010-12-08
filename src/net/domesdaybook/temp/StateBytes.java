/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.temp;

import net.domesdaybook.expression.MatchResult;
import net.domesdaybook.reader.Bytes;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.searcher.BoyerMooreHorspoolSearcher;
import net.domesdaybook.searcher.Searcher;

/**
 *
 * @author matt
 */
public class StateBytes extends State {

    private SequenceMatcher matcher;
    private Searcher searcher;


    public StateBytes(SequenceMatcher matcher) {
        this.matcher = matcher;
        searcher = new BoyerMooreHorspoolSearcher(matcher);
    }

    @Override
    public MatchResult findForwards(Bytes reader, long fromPosition, final long toPosition) {
        MatchResult result = null;
        if (fromPosition == toPosition) {
            if (matcher.matchesBytes(reader, fromPosition)){
                result = new MatchResult(fromPosition, matcher.length());
            }
        } else {
            final long matchPosition = searcher.searchForwards(reader, fromPosition, toPosition);
            if (matchPosition > -1L) {
                result = new MatchResult(matchPosition, matcher.length());
            }
        }
        return result;
    }


    @Override
    public MatchResult findBackwards(Bytes reader, long fromPosition, final long toPosition ) {
        MatchResult result = null;
        if (fromPosition == toPosition) {
            final long matchPosition = fromPosition - matcher.length() + 1;
            if (matcher.matchesBytes(reader, matchPosition)){
                result = new MatchResult(matchPosition, matcher.length());
            }
        } else {
            final long matchPosition = searcher.searchBackwards(reader, fromPosition, toPosition);
            if (matchPosition > -1L) {
                result = new MatchResult(matchPosition, matcher.length());
            }
        }
        return result;
    }

}
