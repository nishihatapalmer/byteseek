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
 *  * The names of its contributors may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
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
 */

package net.domesdaybook.automata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import net.domesdaybook.automata.walker.StateChildWalker;
import net.domesdaybook.automata.walker.Step;
import net.domesdaybook.automata.walker.StepAction;
import net.domesdaybook.collections.IdentityHashSet;

/**
 * A utility class to do useful things with automata.
 * 
 * @author Matt Palmer
 */
public final class AutomataUtils {


    private AutomataUtils() {
    };

    


    /**
     * Returns a list of all the final states in the automata, given the initial
     * state of the automata.
     * 
     * @param initialState The initial state of the automata.
     * @return A list of the final states in the automata.
     */
    public static List<State> getFinalStates(final State initialState) {
        Set<State> visitedStates = new IdentityHashSet<State>();
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
    
    
    /**
     * This function joins all the automata into a single automata,
     * by adding all the transitions and associations of all the states after 
     * the first in to the first state in the collection, and ensuring that
     * any references to the other states are updated to point to the
     * first state.
     * <o>
     * If any of the first states are final, then the state returned will
     * also be final.
     * 
     * @param automata
     * @return 
     */
    public static State join(final Collection<State<?>> automata) {
        final Iterator<State<?>> automataFirstStates = automata.iterator();
        if (automataFirstStates.hasNext()) {
            final State root = automataFirstStates.next();
            boolean isFinal = root.isFinal();            
            while (automataFirstStates.hasNext()) {
                final State<?> automataFirstState = automataFirstStates.next();
                isFinal |= automataFirstState.isFinal();
                replaceState(automataFirstState, root);
                root.addAllTransitions(automataFirstState.getTransitions());
                root.addAllAssociations(automataFirstState.getAssociations());
            }
            root.setIsFinal(isFinal);
            return root;
        }
        return null;
    }
    
    
    /**
     * This function replaces all references to an old State with references 
     * to the new state in the entire automata reachable from the oldState 
     * passed in.
     * 
     * @param oldState
     * @param newState
     * @return 
     */
    public static void replaceState(final State<?> oldState, final State<?> newState) {
        final StepAction replaceWithNewState = new StepAction() {
            @Override
            public void take(final Step step) {
                final State<?> stateToUpdate = step.currentState;
                for (final Transition transition : stateToUpdate.getTransitions()) {
                    if (transition.getToState() == oldState) {
                        transition.setToState(newState);
                    }
                }
            }
        };
        StateChildWalker.walkAutomata(oldState, replaceWithNewState);
    }
    

    /**
     * Builds a map of bytes to the states which can be reached by them from a
     * given state.
     * 
     * @param state The state to build the map from.
     * @param byteToTargetStates The map of byte to states in which the results are placed.
     */
    public static void buildByteToStates(final State<?> state, Map<Byte, Set<State<?>>> byteToTargetStates) {
        for (final Transition transition : state.getTransitions()) {
            final State<?> transitionToState = (State<?>) transition.getToState();
            final byte[] transitionBytes = transition.getBytes();
            for (int index = 0, stop = transitionBytes.length; index < stop; index++) {
                final Byte transitionByte = transitionBytes[index];
                Set<State<?>> states = byteToTargetStates.get(transitionByte);
                if (states == null) {
                    states = new IdentityHashSet<State<?>>();
                    byteToTargetStates.put(transitionByte, states);
                }
                states.add(transitionToState);
            }
        }
    }

       
    /**
     * Given a map of the bytes to the states which can be reached by them, this
     * method returns the reversed map of the sets of states to the sets of bytes 
     * required to reach them.  The map is many-to-many (sets of states to sets of
     * bytes) because a set of states can be reached by more than one byte.
     * 
     * @param bytesToTargetStates The map of bytes to states reachable by them.
     * @return A map of the set of states to the set of bytes required to reach that set of states.
     */
    public static Map<Set<State<?>>, Set<Byte>> getStatesToBytes(Map<Byte, Set<State<?>>> bytesToTargetStates) {
        Map<Set<State<?>>, Set<Byte>> statesToBytes = new IdentityHashMap<Set<State<?>>, Set<Byte>>();

        // For each byte there is a transition on:
        for (final Map.Entry<Byte, Set<State<?>>> transitionByte : bytesToTargetStates.entrySet()) {

            // Get the target states for that byte:
            Set<State<?>> targetStates = transitionByte.getValue();

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
    
    
    /**
     * Builds a text representation of the automata in Graphviz dot format.
     * http://www.graphviz.org/
     * <p/>
     * Graphviz can then render the automata using a variety of graph layout 
     * algorithms, outputting the render to many common formats.
     *
     * @param initialState
     * @param title
     * @return
     */
    public static String toDot(final State<?> initialState, final String title) {
        final StringBuilder builder = new StringBuilder();
        builder.append("digraph {\n");
        String onelineTitle = title.replaceAll("\\s", " ");
        builder.append(String.format("label=\"%s\"\n", onelineTitle));
        Map<State<?>,Integer> visitedStates = new IdentityHashMap<State<?>,Integer>();
        buildDot(initialState, visitedStates, 0, builder);
        builder.append("\n}");
        return builder.toString();
    }


    private static int buildDot(State<?> state, Map<State<?>,Integer> visitedStates, int nextStateNumber, StringBuilder builder) {
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
