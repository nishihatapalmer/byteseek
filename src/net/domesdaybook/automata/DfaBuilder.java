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

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import net.domesdaybook.automata.factory.StateFactory;
import net.domesdaybook.automata.base.BaseStateFactory;
import net.domesdaybook.automata.factory.TransitionFactory;
import net.domesdaybook.automata.base.ByteMatcherTransitionFactory;
import net.domesdaybook.automata.walker.StateChildWalker;
import net.domesdaybook.automata.walker.Step;
import net.domesdaybook.automata.walker.StepAction;
import net.domesdaybook.collections.IdentityHashSet;

/**
 *
 * @author Matt Palmer
 */
public final class DfaBuilder<T> {

    private final StateFactory<T> stateFactory;
    private final TransitionFactory transitionFactory;
    
    /**
     * 
     */
    public DfaBuilder() {
        this(null, null);
    }

    
    /**
     * 
     * @param stateFactory
     */
    public DfaBuilder(final StateFactory<T> stateFactory) {
        this(stateFactory, null);
    }

    
    /**
     * 
     * @param transitionFactory
     */
    public DfaBuilder(final TransitionFactory transitionFactory) {
        this(null, transitionFactory);
    }
    
    
    /**
     * 
     * @param nfaCompilerToUse
     * @param stateFactory
     * @param transitionFactory
     */
    public DfaBuilder(final StateFactory<T> stateFactory, 
                      final TransitionFactory transitionFactory) {
        this.stateFactory      = stateFactory == null
                               ? new BaseStateFactory<T>()
                               : stateFactory;
        this.transitionFactory = transitionFactory == null
                               ? new ByteMatcherTransitionFactory()
                               : transitionFactory;
    }


    public State<T> build(final State<T> initialState) {
        final Set<State<T>> stateSet = new IdentityHashSet<State<T>>();
        stateSet.add(initialState);
        final Map<Set<State<T>>, State<T>> nfaToDfa = new IdentityHashMap<Set<State<T>>, State<T>>();        
        return getState(stateSet, nfaToDfa);
    }
    
    
    public State build(final Collection<State<T>> initialStates) {
        return build(join(initialStates));
    }    


    private State<T> getState(final Set<State<T>> stateSet, final Map<Set<State<T>>, State<T>> stateSetsSeenSoFar) {
        // This method is called recursively -
        // if we have already built this dfa state, just return it:
        if (stateSetsSeenSoFar.containsKey(stateSet)) {
            return stateSetsSeenSoFar.get(stateSet);
        } else {
            return createState(stateSet, stateSetsSeenSoFar);
        }
    }


    private State<T> createState(final Set<State<T>> sourceStates, final Map<Set<State<T>>, State<T>> stateSetsSeenSoFar) {
        // Determine if the new Dfa state should be final:
        boolean isFinal = anyStatesAreFinal(sourceStates);

        // Create the new state and register it in our map of nfa states to dfa state.
        State<T> newState = stateFactory.create(isFinal);
        stateSetsSeenSoFar.put(sourceStates, newState);

        // Append all associations of the sourceStates to the new state.
        for (final State<T> state : sourceStates) {
            newState.addAllAssociations(state.getAssociations());
        }
        
        // Create transitions to all the new dfa states this one points to:
        createDfaTransitions(sourceStates, newState, stateSetsSeenSoFar);
        
        return newState;
    }


    private void createDfaTransitions(final Set<State<T>> stateSet, final State<T> newState,
                                     final Map<Set<State<T>>, State<T>> stateSetsSeenSoFar)  {
       // For each target nfa state set, add a transition on those bytes:
       final Map<Set<State<T>>, Set<Byte>> targetStatesToBytes = getDfaTransitionInfo(stateSet);
       for (final Map.Entry<Set<State<T>>, Set<Byte>> targetEntry : targetStatesToBytes.entrySet()) {
            // Get the set of bytes to transition on:
            final Set<Byte> transitionBytes = targetEntry.getValue();

            // Recursive: get the target DFA state for this transition.
            final State<T> targetDFAState = getState(targetEntry.getKey(), stateSetsSeenSoFar);

            // Create a transition to the target state using the bytes to transition on:
            // This places a burden on the implementor of createSetTransition to ensure it
            // returns an efficient transition, given the set of bytes passed to it.
            // Maybe should rename method or add a createOptimalTransition() method...?
            final Transition transition = transitionFactory.createSetTransition(transitionBytes, false, targetDFAState);

            // Add the transition to the source state:
            newState.addTransition(transition);
        }
    }

   
   private Map<Set<State<T>>, Set<Byte>> getDfaTransitionInfo(final Set<State<T>> sourceStates) {
        // Build a map of bytes to the target nfa states each points to:
        Map<Byte, Set<State<T>>> byteToStates = buildByteToStates(sourceStates);

        // Return a map of target nfa states to the bytes they each transition on:
        return getStatesToBytes(byteToStates);
   }
   

   private Map<Byte, Set<State<T>>> buildByteToStates(final Set<State<T>> states) {
        Map<Byte, Set<State<T>>> byteToTargetStates = new LinkedHashMap<Byte, Set<State<T>>>();
        for (final State<T> state : states) {
            buildByteToStates(state, byteToTargetStates);
        }
        return byteToTargetStates;
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
    public State join(final Collection<State<T>> automata) {
        final Iterator<State<T>> automataFirstStates = automata.iterator();
        if (automataFirstStates.hasNext()) {
            final State root = automataFirstStates.next();
            boolean isFinal = root.isFinal();            
            while (automataFirstStates.hasNext()) {
                final State<T> automataFirstState = automataFirstStates.next();
                isFinal |= automataFirstState.isFinal();
                replaceReachableReferences(automataFirstState, root);
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
    private void replaceReachableReferences(final State<T> oldState, final State<T> newState) {
        final StepAction replaceWithNewState = new StepAction() {
            @Override
            public void take(final Step step) {
                final State<T> stateToUpdate = step.currentState;
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
    private void buildByteToStates(final State<T> state, Map<Byte, Set<State<T>>> byteToTargetStates) {
        for (final Transition transition : state.getTransitions()) {
            final State<T> transitionToState = (State<T>) transition.getToState();
            final byte[] transitionBytes = transition.getBytes();
            for (int index = 0, stop = transitionBytes.length; index < stop; index++) {
                final Byte transitionByte = transitionBytes[index];
                Set<State<T>> states = byteToTargetStates.get(transitionByte);
                if (states == null) {
                    states = new IdentityHashSet<State<T>>();
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
    public Map<Set<State<T>>, Set<Byte>> getStatesToBytes(Map<Byte, Set<State<T>>> bytesToTargetStates) {
        Map<Set<State<T>>, Set<Byte>> statesToBytes = new IdentityHashMap<Set<State<T>>, Set<Byte>>();

        // For each byte there is a transition on:
        for (final Map.Entry<Byte, Set<State<T>>> transitionByte : bytesToTargetStates.entrySet()) {

            // Get the target states for that byte:
            Set<State<T>> targetStates = transitionByte.getValue();

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
    

    private boolean anyStatesAreFinal(final Set<State<T>> sourceStates) {
        for (final State<T> state : sourceStates) {
            if (state.isFinal()) {
                return true;
            }
        }
        return false;
    }

 


}
