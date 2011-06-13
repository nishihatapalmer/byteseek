/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */


package net.domesdaybook.automata.state.walker;

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
public final class SiblingFirstStateWalker implements StateWalker {

    @Override
    public void walk(State state, StateVisitor visitor) {
        Set<State> visitedStates = new HashSet<State>();
        Deque<State> statesToProcess = new ArrayDeque<State>();
        statesToProcess.addFirst(state);
        while (!statesToProcess.isEmpty()) {
            final State stateToVisit = statesToProcess.removeLast();
            if (!visitedStates.contains(stateToVisit)) {
                visitedStates.add(stateToVisit);
                visitor.visit(stateToVisit);
                for (Transition transition: stateToVisit.getTransitions()) {
                    statesToProcess.addFirst(transition.getToState());
                }
            }
        }
    }
    
}
