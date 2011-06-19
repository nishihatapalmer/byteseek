/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.multisequence;

import java.util.ArrayList;
import java.util.List;
import net.domesdaybook.automata.AssociatedState;
import net.domesdaybook.automata.State;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.ByteReader;

/**
 *
 * @author matt
 */
public final class TrieMatcher implements MultiSequenceMatcher {

    final AssociatedState<SequenceMatcher> initialTrieState;

    public TrieMatcher(AssociatedState<SequenceMatcher> initialState) {
        if (initialState == null) {
            throw new IllegalArgumentException("Null state passed in to TrieMatcher.");
        }
        initialTrieState = initialState;
    }


    @Override
    public List<SequenceMatcher> matchingSequencesAt(final ByteReader reader, final long matchPosition) {
        final List<SequenceMatcher> result = new ArrayList<SequenceMatcher>();
        List<State> currentStates = new ArrayList<State>();
        currentStates.add(initialTrieState);
        long currentPosition = matchPosition;
        while (!currentStates.isEmpty()) {
            final State currentState = currentStates.get(0);
            currentStates.clear();
            final byte currentByte = reader.readByte(currentPosition++);
            currentState.getStatesForByte(currentStates, currentByte);
            for (State state : currentStates) {
                if (state.isFinal()) {
                    final AssociatedState<SequenceMatcher> trieState = (AssociatedState<SequenceMatcher>) state;
                    result.addAll(trieState.getAssociations());
                }
            }
        }
        return result;
    }

    
    @Override
    public boolean matches(final ByteReader reader, final long matchPosition) {
        return !matchingSequencesAt(reader, matchPosition).isEmpty();
    }

}
