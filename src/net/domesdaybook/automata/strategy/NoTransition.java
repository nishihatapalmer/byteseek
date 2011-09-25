/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */


package net.domesdaybook.automata.strategy;

import java.util.Collection;
import java.util.Map;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.automata.TransitionStrategy;
import net.domesdaybook.object.copy.DeepCopy;

/**
 *
 * @author matt
 */
public final class NoTransition implements TransitionStrategy {

    @Override
    public void getDistinctStatesForByte(Collection<State> states, byte value, Collection<Transition> transitions) {
    }

    @Override
    public void initialise(State state) {
    }

    public TransitionStrategy deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects) {
        return this;
    }
    
}