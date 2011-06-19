/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */


package net.domesdaybook.automata.transition;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.automata.TransitionStrategy;
import net.domesdaybook.object.copy.DeepCopy;

/**
 *
 * @author matt
 */
public final class NFATransitionStrategy implements TransitionStrategy {

    @Override
    public void getDistinctStatesForByte(Collection<State> states, byte value, Collection<Transition> transitions) {
        // Do not know whether there will be:
        // (1) more than one state for the same byte value, or
        // (2) whether the same state will appear more than once against the
        //     same byte value. 
        // Ensure that only distinct states are returned which match the byte value.
        Set<State> matchingStates = new HashSet<State>();
        for (Transition transition : transitions) {
            State nextState = transition.getStateForByte(value);
            if (nextState != null && !matchingStates.contains(nextState)) {
                matchingStates.add(nextState);
                states.add(nextState);
            }
        }
    }

    @Override
    public void initialise(State state) {
    }

    public TransitionStrategy deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects) {
        return this;
    }
    
}
