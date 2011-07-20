/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.automata;

import java.util.ArrayList;
import java.util.List;
import net.domesdaybook.automata.State;
import net.domesdaybook.reader.ByteReader;

/**
 *
 * @author Matt Palmer
 */
//public class NfaMatcher implements Matcher {
public class NfaMatcher {

    private final State firstState;


    public NfaMatcher(State firstState) {
        this.firstState = firstState;
    }
    
    
//    @Override
    public final boolean matches(final ByteReader reader, final long fromPosition) {
        // If the first state is final, this will always match.
        // Most Nfas won't have a first state which matches, but this is possible
        // For example, the expression "A?" matches A, or nothing at all.  
        // It can make sense for an Nfa to have an initial final state, but only 
        // if the matching algorithm is trying to locate all possible matches,
        // in which case you don't stop matching until there are no further options.
        
        //FIX: can have duplicate states from nfa or dfa's.
        //     An nfa can return more than one state for the same byte,
        //     and we are processing more than one state in any case.
        
        if (!firstState.isFinal()) {
                
            final List<State> nextStates = new ArrayList<State>();                
            List<State> currentStates = new ArrayList<State>();
            currentStates.add(firstState);
            long currentPosition = fromPosition;            
            while (!currentStates.isEmpty()) {
                
                // Get the next byte to match on:
                final byte currentByte = reader.readByte(currentPosition++);

                // Get the next set of active states from the current states:
                nextStates.clear();
                for (State currentState : currentStates) {
                    if (currentState.isFinal()) {
                        return true;
                    }
                    currentState.appendNextStatesForByte(nextStates, currentByte);
                }

                currentStates = nextStates;
            }
        }
        return firstState.isFinal();
    }
}

