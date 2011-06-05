/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.automata;

import java.util.HashSet;
import java.util.Set;
import net.domesdaybook.automata.State;
import net.domesdaybook.matcher.Matcher;
import net.domesdaybook.reader.ByteReader;

/**
 *
 * @author Matt Palmer
 */
public class NfaMatcher implements Matcher {

    private State firstState;


    public NfaMatcher(State firstState) {
        this.firstState = firstState;
    }
    
    
    @Override
    public final boolean matches(final ByteReader reader, final long fromPosition) {
        long currentPosition = fromPosition;
        Set<State> activeStates = new HashSet<State>();
        activeStates.add(firstState);
        boolean matched = firstState.isFinal(); // almost all of them won't be.
        
        // While we haven't matched and there are still states to process:
        while (!matched && activeStates.size() > 0) {
            final byte currentByte = reader.readByte(currentPosition++);

            // for each active state, check if it is a final state,
            // and get its next states given the current byte:
            final Set<State> nextStates = new HashSet<State>();
            for (State state : activeStates ) {
                matched = matched | state.isFinal();
                state.getStatesForByte(nextStates, currentByte);
            }

            activeStates = nextStates;
        }

        return matched;
    }

}
