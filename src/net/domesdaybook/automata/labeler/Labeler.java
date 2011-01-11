/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.automata.labeler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;

/**
 *
 * @author Matt Palmer.
 */
public final class Labeler {

    private Labeler() {
    }

    public static void labelStates(final State state) {
        labelStates(state, 0, "");
    }

    public static void labelStates(final State state, final String name) {
        labelStates(state, 0, name);
    }

    public static void labelStates(final State state, final int count) {
       labelStates(state, count, "");
    }

    public static void labelStates(final State state, final int count, final String name) {
        StateLabeler labeler = new StateCountLabeler(count, name);
        labelStates(state, labeler);
    }

    public static void labelStates(final State state, final StateLabeler labeler) {
        Set<State> visitedStates = new HashSet<State>();
        labelAllStates(state, labeler, visitedStates);
    }

    private static void labelAllStates(final State state, final StateLabeler labeler, final Set<State> visitedStates) {
        if (!visitedStates.contains(state)) {
            visitedStates.add(state);
            labeler.label(state);
            final List<Transition> transitions = state.getTransitions();
            for (Transition transition: transitions) {
                labelAllStates(transition.getToState(), labeler, visitedStates);
            }
        }
    }


}
