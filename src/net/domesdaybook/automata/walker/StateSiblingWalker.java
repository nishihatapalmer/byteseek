/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */


package net.domesdaybook.automata.walker;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;

/**
 *
 * @author matt
 */
public final class StateSiblingWalker implements Walker {

    @Override
    public void walk(final State startState, final StepObserver observer) {
        final Set<State> visitedStates = new HashSet<State>();
        final Deque<Step> walkSteps = new ArrayDeque<Step>();
        walkSteps.addFirst(new Step(null, null, startState));
        while (!walkSteps.isEmpty()) {
            final Step step = walkSteps.removeLast();
            final State state = step.toState;
            if (!visitedStates.contains(state)) {
                visitedStates.add(state);
                for (final Transition transition: state.getTransitions()) {
                    walkSteps.addFirst(
                       new Step(state, transition, transition.getToState()));
                }
                observer.process(step);                
            }
        }
    }
    
}
