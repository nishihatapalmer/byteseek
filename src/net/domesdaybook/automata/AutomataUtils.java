/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 
 *  * Neither the "byteseek" name nor the names of its contributors 
 *    may be used to endorse or promote products derived from this software 
 *    without specific prior written permission. 
 *  
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.domesdaybook.automata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A utility class to do useful things with automata.
 * 
 * @author Matt Palmer
 */
public class AutomataUtils {


    private AutomataUtils() {
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
        Map<Set<State>, Set<Byte>> statesToBytes = new IdentityHashMap<Set<State>, Set<Byte>>();

        // For each byte there is a transition on:
        for (final Map.Entry<Byte, Set<State>> transitionByte : bytesToTargetStates.entrySet()) {

            // Get the target states for that byte:
            Set<State> targetStates = transitionByte.getValue();

            // Get the set of bytes so far for those target states:
            Set<Byte> targetStateBytes = statesToBytes.get(targetStates);
            if (targetStateBytes == null) {
                targetStateBytes = new TreeSet<Byte>();
                statesToBytes.put(targetStates, targetStateBytes);
            }
            
            // Add the transition byte to that set of bytes:
            targetStateBytes.add(transitionByte.getKey());
        }

        return statesToBytes;
    }
    
    
    public static String toDot(final State initialState, final String title) {
        final StringBuilder builder = new StringBuilder();
        builder.append("digraph {\n");
        String onelineTitle = title.replaceAll("\\s", " ");
        builder.append(String.format("label=\"%s\"\n", onelineTitle));
        Map<State,Integer> visitedStates = new IdentityHashMap<State,Integer>();
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
