/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.automata.nfa;

import java.util.HashSet;
import java.util.Set;
import net.domesdaybook.matcher.Matcher;
import net.domesdaybook.reader.ByteReader;

/**
 *
 * @author matt
 */
public class NfaMatcher implements Matcher {

    private NfaState firstState;


    public NfaMatcher(NfaState firstState) {
        this.firstState = firstState;
    }
    
    @Override
    public final boolean matches(final ByteReader reader, final long fromPosition) {
        long currentPosition = fromPosition;
        Set<NfaState> activeStates = new HashSet<NfaState>();
        activeStates.add(firstState);
        boolean matched = firstState.isFinal(); // almost all of them won't be.
        
        // While we haven't matched and there are still states to process:
        while (!matched && activeStates.size() > 0) {
            final byte currentByte = reader.getByte(currentPosition++);

            // for each active state, check if it is a final state,
            // and get its next states given the current byte:
            final Set<NfaState> nextStates = new HashSet<NfaState>();
            for (NfaState state : activeStates ) {
                matched = matched | state.isFinal();
                nextStates.addAll(state.nextStates(currentByte));
            }

            activeStates = nextStates;
        }

        return matched;
    }

}
