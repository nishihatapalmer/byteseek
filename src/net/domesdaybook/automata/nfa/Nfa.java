/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.automata.nfa;

import java.util.HashSet;
import java.util.Set;
import net.domesdaybook.reader.Bytes;

/**
 *
 * @author matt
 */
public class Nfa {

    private NfaState firstState;


    public Nfa(NfaState firstState) {
        this.firstState = firstState;
    }
    

    public boolean matches(final Bytes reader, final long fromPosition) {
        boolean matched = firstState.isFinal();

        long currentPosition = fromPosition;
        Set<NfaState> activeStates = new HashSet<NfaState>();
        activeStates.add(firstState);

        // While we haven't matched and there are still states to process:
        while (!matched && activeStates.size() > 0) {
            final byte currentByte = reader.getByte(currentPosition++);

            // for each active state, check if it is a final state,
            // and get its next states given the current byte:
            Set<NfaState> nextStates = new HashSet<NfaState>();
            for (NfaState state : activeStates ) {
                matched = matched | state.isFinal();
                nextStates.addAll(state.nextStates(currentByte));
            }

            activeStates = nextStates;
        }

        return matched;
    }

}
