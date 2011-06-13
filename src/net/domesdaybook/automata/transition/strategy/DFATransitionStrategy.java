/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */


package net.domesdaybook.automata.transition.strategy;

import java.util.Collection;
import java.util.Map;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.automata.TransitionStrategy;
import net.domesdaybook.object.copy.DeepCopy;

/**
 *
 * @author matt
 */
public final class DFATransitionStrategy implements TransitionStrategy {

    @Override
    public void getDistinctStatesForByte(Collection<State> states, byte value, Collection<Transition> transitions) {
        // Only no or one state can be returned for a given byte 
        // if this strategy is employed.  This allows us to break
        // immediately if we get a transition match.  This sort of strategy fits
        // with a Deterministic Finite State automaton (in which all states fit
        // this criteria).
        for (Transition transition : transitions) {
            State nextState = transition.getStateForByte(value);
            if (nextState != null) {
                states.add(nextState);
                break;
            }
        }
    }

    @Override
    public void initialise(State state) {
    }

    
    // No need for a deep copy of a stateless object - just return this.
    public TransitionStrategy deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects) {
        return this;
    }
    
}
