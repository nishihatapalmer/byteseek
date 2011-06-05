/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.multisequence;

import java.util.ArrayList;
import java.util.List;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.ByteReader;

/**
 * A very simple MultiSequenceMatcher which simply tries all of the
 *  sequence matchers in turn, returning the first matching sequence, if any.
 *
 * @author Matt Palmer.
 */
public final class NaiveMultiSequenceMatcher implements MultiSequenceMatcher {

    private final List<SequenceMatcher> matchers;

    
    public NaiveMultiSequenceMatcher(List<SequenceMatcher> matchersToUse) {
        matchers = new ArrayList(matchersToUse);
    }


    @Override
    public SequenceMatcher matchingSequenceAt(ByteReader reader, long matchPosition) {
        SequenceMatcher result = null;
        for (SequenceMatcher sequence : matchers) {
            if (sequence.matches(reader, matchPosition)) {
                result = sequence;
                break;
            }
        }
        return result;
    }


    @Override
    public boolean matches(ByteReader reader, long matchPosition) {
        return matchingSequenceAt(reader, matchPosition) != null;
    }
    
}
