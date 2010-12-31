/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.automata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author matt
 */
public class Utilities {



    private Utilities() {
    };

    public static String toDot(State initialState, String title) {
        StringBuilder builder = new StringBuilder();
        builder.append("digraph {\n");
        builder.append(String.format("label=\"%s\"\n", title));
        Map<State, String> stateLabels = new HashMap<State, String>();
        int numberOfStates = buildDot(initialState, stateLabels, builder, 0);
        builder.append("\n}");
        return builder.toString();
    }

    private static int buildDot(State theState, Map<State,String> stateLabels, StringBuilder builder, int labelCount) {
        String label = stateLabels.get(theState);
        if (label == null) { // if we haven't seen this state before:

            // give it a label and shape:
            label = Integer.toString(labelCount++);
            stateLabels.put(theState, label);
            String shape = theState.isFinal() ? "doublecircle" : "circle";
            builder.append(String.format("%s [label=\"%s\", shape=\"%s\"]\n", label, label, shape));

            // process its transitions:
            List<Transition> transitions = theState.getTransitions();
            for (Transition transition : transitions) {

                State toState = transition.getToState();
                String toStateLabel = stateLabels.get(toState);
                if (toStateLabel == null) {
                     labelCount = buildDot(toState, stateLabels, builder, labelCount);
                     toStateLabel = stateLabels.get(toState);
                }
                String transitionLabel = transition.toString();
                builder.append(String.format("%s->%s [label=\"%s\"]\n", label, toStateLabel,transitionLabel));
            }
        }
        return labelCount;
    }
    



}
