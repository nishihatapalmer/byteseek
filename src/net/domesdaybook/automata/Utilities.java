/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.automata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author matt
 */
public class Utilities {


    private Utilities() {
    };


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

    public static List<State> getFinalStates(final State initialState) {
        Set<State> visitedStates = new HashSet<State>();
        List<State> finalStates = new ArrayList<State>();
        getAllFinalStates(initialState, visitedStates, finalStates);
        return finalStates;
    }

    
    public static String toDot(final State initialState, final String title) {
        final StringBuilder builder = new StringBuilder();
        builder.append("digraph {\n");
        builder.append(String.format("label=\"%s\"\n", title));
        Set<State> visitedStates = new HashSet<State>();
        buildDot(initialState, visitedStates, builder);
        //Map<State, String> stateLabels = new HashMap<State, String>();
        //int numberOfStates = buildDot(initialState, stateLabels, builder, 0);
        builder.append("\n}");
        return builder.toString();
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

    private static void getAllFinalStates(final State state, final Set<State> visitedStates, final List<State> finalStates) {
        if (!visitedStates.contains(state)) {
            visitedStates.add(state);
            if (state.isFinal()) {
                finalStates.add(state);
            }
            final List<Transition> transitions = state.getTransitions();
            for (Transition transition: transitions) {
                getAllFinalStates(transition.getToState(), visitedStates, finalStates);
            }
        }
    }


    private static void buildDot(State state, Set<State> visitedStates, StringBuilder builder) {
        if (!visitedStates.contains(state)) {
            visitedStates.add(state);
            final String label = state.getLabel();
            final String shape = state.isFinal() ? "doublecircle" : "circle";
            builder.append(String.format("%s [label=\"%s\", shape=\"%s\"]\n", label, label, shape));

            // process its transitions:
            final List<Transition> transitions = state.getTransitions();
            for (Transition transition : transitions) {
                final State toState = transition.getToState();
                buildDot(toState, visitedStates, builder);
                final String toStateLabel = toState.getLabel();
                final String transitionLabel = transition.toString();
                builder.append(String.format("%s->%s [label=\"%s\"]\n", label, toStateLabel,transitionLabel));
            }
        }
    }


}
