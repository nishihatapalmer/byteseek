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
 * @author Matt Palmer
 */
public class Automata {


    private Automata() {
    };


    public static List<State> getFinalStates(final State initialState) {
        Set<State> visitedStates = new HashSet<State>();
        List<State> finalStates = new ArrayList<State>();
        getAllFinalStates(initialState, visitedStates, finalStates);
        return finalStates;
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


    public static String toDot(final State initialState, final String title) {
        final StringBuilder builder = new StringBuilder();
        builder.append("digraph {\n");
        builder.append(String.format("label=\"%s\"\n", title));
        Set<State> visitedStates = new HashSet<State>();
        buildDot(initialState, visitedStates, builder);
        builder.append("\n}");
        return builder.toString();
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
