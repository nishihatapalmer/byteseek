/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.matcher.multisequence;

import java.util.ArrayList;
import java.util.List;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.ByteReader;

/**
 *
 * @author matt
 */
public class TrieMatcher implements MultiSequenceMatcher {

    private final List<SequenceMatcher> matchers;

    public TrieMatcher(List<SequenceMatcher> matchersToUse) {
        matchers = new ArrayList(matchersToUse);
    }


    @Override
    public SequenceMatcher matchingSequenceAt(ByteReader reader, long matchPosition) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean matches(ByteReader reader, long matchPosition) {
        return matchingSequenceAt(reader, matchPosition) != null;
    }

}
