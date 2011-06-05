/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.automata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A utility class to do useful things with automata states.
 * 
 * @author Matt Palmer
 */
public class Utilities {


    private Utilities() {
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

    
    public static void buildByteToStates(final State state, Map<Byte, Set<State>> byteToTargetStates) {
        for (final Transition transition : state.getTransitions()) {
            final State transitionToState = (State) transition.getToState();
            final byte[] transitionBytes = transition.getBytes();
            for (int index = 0, stop = transitionBytes.length; index < stop; index++) {
                final Byte transitionByte = transitionBytes[index];
                Set<State> states = byteToTargetStates.get(transitionByte);
                if (states == null) {
                    states = new HashSet<State>();
                    byteToTargetStates.put(transitionByte, states);
                }
                states.add(transitionToState);
            }
        }
    }

       
    public static Map<Set<State>, Set<Byte>> getStatesToBytes(Map<Byte, Set<State>> bytesToTargetStates) {
        Map<Set<State>, Set<Byte>> statesToBytes = new HashMap<Set<State>, Set<Byte>>();

        // For each byte there is a transition on:
        for (final Byte transitionByte : bytesToTargetStates.keySet()) {

            // Get the target states for that byte:
            Set<State> targetStates = bytesToTargetStates.get(transitionByte);

            // Get the set of bytes so far for those target states:
            Set<Byte> targetStateBytes = statesToBytes.get(targetStates);
            if (targetStateBytes == null) {
                targetStateBytes = new TreeSet<Byte>();
                statesToBytes.put(targetStates, targetStateBytes);
            }
            
            // Add the transition byte to that set of bytes:
            targetStateBytes.add(transitionByte);
        }

        return statesToBytes;
    }
    
    
    public static String toDot(final State initialState, final String title) {
        final StringBuilder builder = new StringBuilder();
        builder.append("digraph {\n");
        builder.append(String.format("label=\"%s\"\n", title));
        Map<State,Integer> visitedStates = new HashMap<State,Integer>();
        buildDot(initialState, visitedStates, 0, builder);
        builder.append("\n}");
        return builder.toString();
    }


    private static int buildDot(State state, Map<State,Integer> visitedStates, int nextStateNumber, StringBuilder builder) {
        if (!visitedStates.containsKey(state)) {
            visitedStates.put(state, nextStateNumber);
            final String label = Integer.toString(nextStateNumber);
            final String shape = state.isFinal() ? "doublecircle" : "circle";
            builder.append(String.format("%s [label=\"%s\", shape=\"%s\"]\n", label, label, shape));

            // process its transitions:
            final List<Transition> transitions = state.getTransitions();
            for (Transition transition : transitions) {
                final State toState = transition.getToState();
                int processedNumber = buildDot(toState, visitedStates, nextStateNumber + 1, builder);
                nextStateNumber = processedNumber > nextStateNumber? processedNumber : nextStateNumber;
                final String toStateLabel = Integer.toString(visitedStates.get(toState));
                final String transitionLabel = transition.toString();
                builder.append(String.format("%s->%s [label=\"%s\"]\n", label, toStateLabel,transitionLabel));
            }
        }
        return nextStateNumber;
    }


}
