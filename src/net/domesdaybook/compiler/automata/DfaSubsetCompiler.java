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

package net.domesdaybook.compiler.automata;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.automata.AutomataUtils;
import net.domesdaybook.automata.state.StateFactory;
import net.domesdaybook.automata.state.BaseStateFactory;
import net.domesdaybook.automata.transition.TransitionFactory;
import net.domesdaybook.automata.transition.SingleByteMatcherTransitionFactory;
import net.domesdaybook.collections.IdentityHashSet;
import net.domesdaybook.compiler.CompileException;
import net.domesdaybook.compiler.Compiler;

/**
 *
 * @author Matt Palmer
 */
public final class DfaSubsetCompiler implements Compiler<State<?>, State<?>> {

    private static DfaSubsetCompiler defaultCompiler;
    
    /**
     * 
     * @param nfaInitialState
     * @return
     * @throws CompileException
     */
    public static State dfaFromNfa(State nfaInitialState) throws CompileException {
        defaultCompiler = new DfaSubsetCompiler();
        return defaultCompiler.compile(nfaInitialState);
    }
    
    
    private final Compiler<State<?>, String> nfaCompiler;
    private final StateFactory StateFactory;
    private final TransitionFactory transitionFactory;
    
    
    /**
     * 
     */
    public DfaSubsetCompiler() {
        this(null, null, null);
    }

    
    /**
     * 
     * @param stateFactory
     */
    public DfaSubsetCompiler(final StateFactory stateFactory) {
        this(null, stateFactory, null);
    }

    
    /**
     * 
     * @param transitionFactory
     */
    public DfaSubsetCompiler(final TransitionFactory transitionFactory) {
        this(null, null, transitionFactory);
    }
    
    
    /**
     * 
     * @param nfaCompilerToUse
     */
    public DfaSubsetCompiler(final Compiler<State<?>, String> nfaCompilerToUse) {
        this(nfaCompilerToUse, null, null);
    }
    
    
    /**
     * 
     * @param nfaCompilerToUse
     * @param stateFactory
     */
    public DfaSubsetCompiler(final Compiler<State<?>, String> nfaCompilerToUse, final StateFactory stateFactory) { 
        this(nfaCompilerToUse, stateFactory, null);
    }
    
    
    /**
     * 
     * @param nfaCompilerToUse
     * @param transitionFactory
     */
    public DfaSubsetCompiler(final Compiler<State<?>, String> nfaCompilerToUse, final TransitionFactory transitionFactory) {
        this(nfaCompilerToUse, null, transitionFactory);
    }

    
    /**
     * 
     * @param nfaCompilerToUse
     * @param StateFactoryToUse
     * @param factoryToUse
     */
    public DfaSubsetCompiler(Compiler<State<?>, String> nfaCompilerToUse, StateFactory StateFactoryToUse, TransitionFactory factoryToUse) {
        if (nfaCompilerToUse == null) {
            nfaCompiler = new NfaCompiler();
        } else {
            nfaCompiler = nfaCompilerToUse;
        }
        if (StateFactoryToUse == null) {
            StateFactory = new BaseStateFactory();
        } else {
            StateFactory = StateFactoryToUse;
        }
        if (factoryToUse == null) {
            transitionFactory = new SingleByteMatcherTransitionFactory();
        } else {
            transitionFactory = factoryToUse;
        }
    }


    @Override
    public State compile(final State<?> nfaToTransform) {
        final Map<Set<State<?>>, State<?>> nfaToDfa = new IdentityHashMap<Set<State<?>>, State<?>>();
        final Set<State<?>> initialState = new IdentityHashSet<State<?>>();
        initialState.add(nfaToTransform);
        return getState(initialState, nfaToDfa);
    }
    
    
    @Override
    public State compile(final Collection<State<?>> automata) throws CompileException {
        return compile(AutomataUtils.join(automata));
    }    


    private State getState(final Set<State<?>> states, final Map<Set<State<?>>, State<?>> statesToDfa) {
        // This method is called recursively -
        // if we have already built this dfa state, just return it:
        if (statesToDfa.containsKey(states)) {
            return statesToDfa.get(states);
        } else {
            return createState(states, statesToDfa);
        }
    }


    private State createState(final Set<State<?>> sourceStates, final Map<Set<State<?>>, State<?>> StatesToDfa) {

        // Determine if the new Dfa state should be final:
        boolean isFinal = anyStatesAreFinal(sourceStates);

        // Create the new state and register it in our map of nfa states to dfa state.
        State<?> newState = StateFactory.create(isFinal);
        StatesToDfa.put(sourceStates, newState);

        // Create transitions to all the new dfa states this one points to:
        createDfaTransitions(sourceStates, newState, StatesToDfa);

        return newState;
    }


    private void createDfaTransitions(final Set<State<?>> sourceStates, final State<?> newState,
                                     final Map<Set<State<?>>, State<?>> StatesToDfa)  {
       // For each target nfa state set, add a transition on those bytes:
       final Map<Set<State<?>>, Set<Byte>> targetStatesToBytes = getDfaTransitionInfo(sourceStates);
       for (final Map.Entry<Set<State<?>>, Set<Byte>> targetEntry : targetStatesToBytes.entrySet()) {
            // Get the set of bytes to transition on:
            final Set<Byte> transitionBytes = targetEntry.getValue();

            // Recursive: get the target DFA state for this transition.
            final State<?> targetDFAState = getState(targetEntry.getKey(), StatesToDfa);

            // Create a transition to the target state using the bytes to transition on:
            // This places a burden on the implementor of createSetTransition to ensure it
            // returns an efficient transition, given the set of bytes passed to it.
            // Maybe should rename method or add a createOptimalTransition() method...?
            final Transition transition = transitionFactory.createSetTransition(transitionBytes, false, targetDFAState);

            // Add the transition to the source state:
            newState.addTransition(transition);
        }
    }

   
   private Map<Set<State<?>>, Set<Byte>> getDfaTransitionInfo(final Set<State<?>> sourceStates) {
        // Build a map of bytes to the target nfa states each points to:
        Map<Byte, Set<State<?>>> byteToStates = buildByteToStates(sourceStates);

        // Return a map of target nfa states to the bytes they each transition on:
        return AutomataUtils.getStatesToBytes(byteToStates);
   }
   

   private Map<Byte, Set<State<?>>> buildByteToStates(final Set<State<?>> states) {
        Map<Byte, Set<State<?>>> byteToTargetStates = new LinkedHashMap<Byte, Set<State<?>>>();
        for (final State<?> state : states) {
            AutomataUtils.buildByteToStates(state, byteToTargetStates);
        }
        return byteToTargetStates;
    }


    private boolean anyStatesAreFinal(final Set<State<?>> sourceStates) {
        for (final State<?> state : sourceStates) {
            if (state.isFinal()) {
                return true;
            }
        }
        return false;
    }

 


}
