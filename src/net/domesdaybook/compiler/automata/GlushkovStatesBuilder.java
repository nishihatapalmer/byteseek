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

import net.domesdaybook.automata.wrapper.InitialFinalStates;
import net.domesdaybook.automata.transition.TransitionFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.automata.state.BaseStateFactory;
import net.domesdaybook.automata.state.StateFactory;
import net.domesdaybook.automata.transition.SingleByteMatcherTransitionFactory;

/**
 * ----------------------------------------------------------------------------
 * 
 * What is a Glushkov builder?
 * 
 * This class builds an automata as it is invoked for each node of the regular 
 * expression parse-tree.  It builds a particular kind of automata as it goes,
 * called a Glushkov automata.  The technique of constructing a Glushkov automata
 * by traversing the parse-tree is due to Jean-Marc Champarnaud.
 * 
 * A Glushkov Non-deterministic Finite-state Automata (NFA) is an automata which
 * is constructed to have one initial state, and additionally, a state for each
 * position in the regular expression that defines a byte or set of bytes.
 *
 * Each position has transitions to every other reachable position,
 * on the bytes which need to match to get to those positions.
 *
 * Being Non-Deterministic means that from any given state,
 * there can be transitions to several states on the same byte value.
 * 
 * By constrast, Deterministic Finite-state Automata (DFAs) have at most one
 * state they can transition to on a given byte value.
 *
 * Unlike the classic Thompson construction (the most common regular expression NFA)
 * Glushkov automata have no "empty" transitions - that is, transitions to another
 * state without reading a byte. The Thompson NFA uses empty transitions as a sort of 
 * permanent scaffolding to simplify constructing the automata, by wiring up states 
 * together as the need arises during construction.  
 * 
 * Having no empty transitions makes the automata smaller, and easier to transform
 * it further (e.g. building a DFA from it) but makes constructing it a little more
 * difficult in the first place.
 *
 * This construction of a Glushkov automata is directly from the parse tree,
 * (in the same fashion as the Thompson construction), and is due to Champarnaud.
 * 
 * Details of the construction can be found in the paper:
 *
 *   "A reexamination of the Glushkov and Thompson Constructions"
 *
 *    by Dora Giammarresi, Jean-Luc Ponty, Derick Wood, 1998.
 *
 * @author Matt Palmer
 */
public final class GlushkovStatesBuilder implements StatesBuilder {

     private final TransitionFactory transitionFactory;
     private final StateFactory stateFactory;

     /**
      * 
      */
     public GlushkovStatesBuilder() {
         this(null, null);
     }
     
     /**
      * 
      * @param stateFactory
      */
     public GlushkovStatesBuilder(final StateFactory stateFactory) {
         this(null, stateFactory);
     }
         
         
     /**
      * 
      * @param transitionFactory
      */
     public GlushkovStatesBuilder(final TransitionFactory transitionFactory) {
         this(transitionFactory,null);
     }
     
     /**
      * 
      * @param transitionFactory
      * @param stateFactory
      */
     public GlushkovStatesBuilder(final TransitionFactory transitionFactory, final StateFactory stateFactory) {
         if (transitionFactory == null) {
             this.transitionFactory = new SingleByteMatcherTransitionFactory();
         } else {
             this.transitionFactory = transitionFactory;
         }
         if (stateFactory == null) {
             this.stateFactory = new BaseStateFactory();
         } else {
            this.stateFactory = stateFactory;   
         }
     }
     

    /**
    * Builds an (initial) state and a [final] state,
    * and joins them together with a transition on the transition byte.
    *
    * <p/><code>
    *   b      (0) --b--> [1]
    * </code><p/>
    *
    * @param transitionByte The byte to transition on.
    * @return An automata with a transition on the byte supplied.
    */
     public InitialFinalStates buildSingleByteStates(final byte transitionByte) {
        final InitialFinalStates states = createInitialFinalStates();
        final State finalState = states.finalStates.get(0);
        final Transition transition = transitionFactory.createByteTransition(transitionByte, finalState);
        states.initialState.addTransition(transition);
        return states;
    }


     /**
     * Builds an (initial) state and a [final] state,
     * and joins them together with a transition on the set of bytes.
     *
     * <p/><code>
     *   [abc]   (0) --[abc]--> [1]
     * </code><p/>
     *
     * @param byteSet A set of bytes to match.
     * @param negated Whether the set of bytes should be inverted or not when matching.
     * @return An automata with a transition on the set of bytes supplied.
     */
    @Override
    public InitialFinalStates buildSetStates(final Set<Byte> byteSet, final boolean negated) {
        final InitialFinalStates states = createInitialFinalStates();
        final State finalState = states.finalStates.get(0);
        final Transition transition = transitionFactory.createSetTransition(byteSet, negated, finalState);
        states.initialState.addTransition(transition);
        return states;
    }


    /**
     * Builds an (initial) state and a [final] state
     * and joins them together with a transition on any byte.
     *
     * <p/><code>
     *   .       (0) --.--> [1]
     * </code><p/>
     *
     * @return An automata with a transition on any byte.
     */
    @Override
    public InitialFinalStates buildAnyByteStates() {
        final InitialFinalStates states = createInitialFinalStates();
        final State finalState = states.finalStates.get(0);
        final Transition transition = transitionFactory.createAnyByteTransition(finalState);
        states.initialState.addTransition(transition);
        return states;

    }

    /**
     * Build an (initial) state and a [final] state,
     * and join them together with a transition on the all bitmask byte.
     *
     * <p/><code>
     *   &m     (0) --&m--> [1]
     * </code><p/>
     *
     * @param bitMask The bitmask to transition on if all bits match.
     * @return An automata using an All bitmask transition.
     */
    public InitialFinalStates buildAllBitmaskStates(final byte bitMask) {
        final InitialFinalStates states = createInitialFinalStates();
        final State finalState = states.finalStates.get(0);
        final Transition transition = transitionFactory.createAllBitmaskTransition(bitMask, finalState);
        states.initialState.addTransition(transition);
        return states;
    }


    /**
     * Build an (initial) state and a [final] state,
     * and join them together with a transition on the any bitmask byte.
     *
     * <p/><pre>{@code
     *    ~m    (0) --~m--> [1]
     * }</pre><p/>
     *
     * @param bitMask The bitmask to transition on if any bits match.
     * @return An automata using an Any bitmask transition.
     */
    public InitialFinalStates buildAnyBitmaskStates(final byte bitMask) {
        final InitialFinalStates states = createInitialFinalStates();
        final State finalState = states.finalStates.get(0);
        final Transition transition = transitionFactory.createAnyBitmaskTransition(bitMask, finalState);
        states.initialState.addTransition(transition);
        return states;
    }

    
    /**
     * Builds a single automata out of a list of automata,
     * by turning them into a sequence from the start to the end of the list.
     *
     * <p/><pre>{@code
     *   XYZ    (0) ----> (1) ----> (2) ----> [3]
     *                X         Y         Z
     * }</pre><p/>
     * 
     * To create an automata joining a sequence of states together,
     * we must transition each state to its neighbour to the right.
     *
     * We can achieve this by copying all of the transitions of an initial
     * state back to the final states of the automata that preceded it.
     * Each final state effectively becomes an initial state of the
     * automata to its right.
     * Then we can discard the now-redundant initial state of the right hand side.
     *
     * The left hand side final states all become as final as the initial state
     * they end up replacing.
     *
     * @param sequenceStates A list of automata to be joined as a sequence.
     * @return An automata which is a sequence of the automata in the list.
     */
    @Override
    public InitialFinalStates buildSequenceStates(final List<InitialFinalStates> sequenceStates) {
        // process the sequence of states joining final states to what the next initial
        // states have transitions to.
        List<State> finalSequenceStates = new ArrayList<State>();
        for (int itemIndex = 1, stop = sequenceStates.size(); itemIndex < stop; itemIndex++) {
            final InitialFinalStates leftState = sequenceStates.get(itemIndex-1);
            final InitialFinalStates rightState = sequenceStates.get(itemIndex);
            final State initialStateToReplace = rightState.initialState;
            final List<Transition> transitionList = initialStateToReplace.getTransitions();
            final boolean initialStateIsFinal = initialStateToReplace.isFinal();
            finalSequenceStates.addAll(leftState.finalStates);
            // for each final state of the sequence,
            // add the initial transitions from the right hand side,
            // and set whether it is final or not based on the initial right hand state.
            for (State state : finalSequenceStates) {
                state.addAllTransitions(transitionList);
                state.setIsFinal(initialStateIsFinal);
            }

            if (!initialStateIsFinal) {
                finalSequenceStates = new ArrayList<State>();
            }
        }

        // Add the final states of the very last state to the sequence final states:
        final InitialFinalStates lastState = sequenceStates.get(sequenceStates.size() - 1);
        finalSequenceStates.addAll(lastState.finalStates);
        
        // Wrap it all up in a new states object representing
        // the start and final states of the sequence.
        final InitialFinalStates states = new InitialFinalStates();
        final InitialFinalStates firstState = sequenceStates.get(0);
        states.initialState = firstState.initialState;
        states.finalStates = finalSequenceStates;
        return states;
    }


    /**
     * To create an automata of alternative sequences. identify the initial state of
     * each of the alternatives with each other.  In practice, retain one state and
     * add the transitions of the other initial states to it.
     *
     * <p/><pre>{@code
     * 
     * X|Y|Z    (0) ----> [1]
     *           |    X
     *            ------> [2]
     *           |    Y
     *            ------> [3]
     *                Z
     * }</pre><p/>
     *
     * The initial state is final if any of the alternate initial states were final.
     *
     * @param alternateStates A list of alternative automata, wrapped in an object giving the initial and final states of the automata.
     * @return An object holding the initial and final states of the alternative automata.
     */
    @Override
    public InitialFinalStates buildAlternativeStates(final List<InitialFinalStates> alternateStates) {
       
        // Merge all the initial states of the alternatives, so we can
        // transition into any of them from a single initial state.  
        // If any of the initial states being merged is final, then the final merged state is final.
        // Also build a list of the sum of all the final states in all the alternatives.
        final List<State> finalStates = new ArrayList<State>();
        State initialState = stateFactory.create(State.NON_FINAL);
        boolean anyInitialStateIsFinal = false;
        for (int alternateIndex = 0, stop = alternateStates.size(); alternateIndex < stop; alternateIndex++) {
            final InitialFinalStates altStates = alternateStates.get(alternateIndex);
            final State alternateInitialState = altStates.initialState;
            anyInitialStateIsFinal = anyInitialStateIsFinal | alternateInitialState.isFinal();
            initialState.addAllTransitions(alternateInitialState.getTransitions());
            finalStates.addAll(altStates.finalStates);
        }

        // Wrap it all up in a states object and set whether the initial state is final:
        final InitialFinalStates states = new InitialFinalStates();
        states.initialState = initialState;
        states.finalStates = finalStates;
        states.setIsFinal(initialState, anyInitialStateIsFinal);
        return states;
    }


    /**
     * Wires the automaton provided to be repeated zero to many times.
     * This is done by making the initial state final,
     * allowing for an instant match to cover the zero case.
     *
     * <p/><pre>{@code
     *  X*     [0] ----> [1] __
     *              X    ^    |                
     *                   |____|                     
     *                      X
     *
     * (XY)*  [0] ----> (1) ----> [2] __
     *              X    ^     Y        |
     *                   |______________|
     *                         X
     * }</pre><p/>
     *
     * Additionally, all of the final states loop back to the states that
     * the initial state points to, allowing for as many repetitions as needed.
     * It is the same as building one to many states, except the initial state
     * is set to final to cover the zero case.
     * 
     * @param zeroToManyStates An automata to repeat zero to many times.
     * @return An automata which will repeat zero to many times.
     */
    @Override
    public InitialFinalStates buildZeroToManyStates(final InitialFinalStates zeroToManyStates) {
        final State initialState = zeroToManyStates.initialState;
        final List<Transition> intialTransitions = initialState.getTransitions();
        final List<State> finalStates = zeroToManyStates.finalStates;
        for (State state : finalStates) {
            state.addAllTransitions(intialTransitions);
        }
        zeroToManyStates.setIsFinal(initialState, State.FINAL);
        return zeroToManyStates;
    }
    

    /**
     * Wires the automaton provided to be repeated one to many times.
     * This is done by making all the final states loop back to the states
     * that the initial state points to, allowing for as many repetitions as needed.
     *
     * <p/><pre>{@code
     *   X+     [0) ----> [1] __    
     *                X    ^    |      
     *                     |____|                     
     *                        X
     *
     * (XY)+  (0) ----> (1) ----> [2]__
     *              X    ^     Y       |
     *                   |_____________|
     *                         X
     * }</pre><p/>
     *
     * It is the same as zeroToManyStates, except that the initial state is not made final.
     *
     * @param oneToManyStates
     * @return
     */
    @Override
    public InitialFinalStates buildOneToManyStates(final InitialFinalStates oneToManyStates) {
        final State initialState = oneToManyStates.initialState;
        final List<Transition> intialTransitions = initialState.getTransitions();
        final List<State> finalStates = oneToManyStates.finalStates;
        for (State state : finalStates) {
            state.addAllTransitions(intialTransitions);
        }
        return oneToManyStates;
    }


    /**
     * Makes the automata passed in optional.
     * It does this by making the initial state final.
     *
     * <p/><pre>{@code
     *     X?    [0] ----> [1]
     *                 X
     * }</pre><p/>
     *
     * @param optionalStates
     * @return
     */
    @Override
    public InitialFinalStates buildOptionalStates(final InitialFinalStates optionalStates) {
        optionalStates.setIsFinal(optionalStates.initialState, State.FINAL);
        return optionalStates;
    }


    /**
     * Builds an automata which repeats a minimum number of times, up to many times.
     * It achieves this by first building a repeated automata for the minimum number of
     * times, then it builds a zero-to-many automata. Finally, it joins both automata
     * together as a sequence.
     *
     * <p/><pre>{@code
     *
     * X{2-*}  (0) ----> (1) ----> [2] ----> [3] __
     *               X         X         X    ^    |
     *                                        |____|
     *                                           X
     * }</pre><p/>
     *
     * @param minRepeat The minimum number of times to repeat.
     * @param repeatedAutomata The automata to repeat.
     * @return An automata which repeats a minimum number of times, followed by many times.
     */
    @Override
    public InitialFinalStates buildMinToManyStates(final int minRepeat, final InitialFinalStates repeatedAutomata) {
        InitialFinalStates states = null;
        if (minRepeat == 0) {
            states = buildZeroToManyStates(repeatedAutomata);
        } else if (minRepeat > 0) {
            final InitialFinalStates repeatStates = buildRepeatedStates(minRepeat, repeatedAutomata);
            final InitialFinalStates zeroToManyStates = buildZeroToManyStates(repeatedAutomata.deepCopy());
            states = joinStates(repeatStates, zeroToManyStates);
        }
        return states;
    }


    /**
     * Builds an automata which repeats from a minimum number of times, up to a
     * maximum number of times.  It achieves this by first building a repeated
     * automata for the minimum number of times.  Then it builds repeated optional
     * automata for the difference between the min and max repeats.  Finally, it
     * joins both automata together as a sequence.  If the minimum number of repeats
     * is zero, it just builds up to the maximum number of repeated optional states.
     *
     * <p/><pre>{@code
     *
     * X{2-4}  (0) ----> (1) ----> [2] ----> [3] ----> [4]
     *               X         X         X         X
     *                      
     * }</pre><p/>
     *
     * @param minRepeat
     * @param maxRepeat
     * @param repeatedAutomata
     * @return
     */
    @Override
    public InitialFinalStates buildMinToMaxStates(final int minRepeat, final int maxRepeat, final InitialFinalStates repeatedAutomata) {
        InitialFinalStates states = null;
        // If min repeat is zero, then we have up to max optional repeated states:
        if (minRepeat == 0) {
            states = buildRepeatedOptionalStates(maxRepeat, repeatedAutomata);
        } else { // we have some required repeated states:
            states = buildRepeatedStates(minRepeat, repeatedAutomata);
            // possibly followed by (max - min) optional repeated states:
            if (maxRepeat > minRepeat) {
                final InitialFinalStates optionalStates = buildRepeatedOptionalStates(maxRepeat - minRepeat, repeatedAutomata);
                states = joinStates(states, optionalStates);
            }
        }
        return states;
    }


    /**
     * Builds an automata by repeating an automata which is made optional.
     * It achieves this by making a deep copy of the automata for each repeat,
     * which is made optional, and joining them together as a sequence.
     *
     * <p/><pre>{@code
     *
     * (X?){3}   [0] ----> [1] ----> [2] ----> [3]
     *                 X         X         X
     * 
     * }</pre><p/>
     *
     * @param numberOptional the number of optional automata to repeat.
     * @param optionalState The automata to repeat.
     * @return An automata which repeats a source automata optionally a number of times.
     */
    @Override
    public InitialFinalStates buildRepeatedOptionalStates(final int numberOptional, final InitialFinalStates optionalState) {
        final List<InitialFinalStates> optionalStates = new ArrayList<InitialFinalStates>();
        for (int count = 0; count < numberOptional; count++) {
            final InitialFinalStates optStates = buildOptionalStates(optionalState.deepCopy());
            optionalStates.add(optStates);
        }
        return buildSequenceStates(optionalStates);
    }


    /**
     * Builds an automata by repeating an automata a number of times.
     * It achieves this by making a deep copy of the automata for each repeat,
     * and joining them together as a sequence.
     *
     * <p/><pre>{@code
     *
     * X{3}   (0) ----> (1) ----> (2) ----> [3]
     *              X         X         X
     *
     * }</pre><p/>
     *
     * @param repeatNumber
     * @param repeatedAutomata
     * @return
     */
    @Override
    public InitialFinalStates buildRepeatedStates(final int repeatNumber, final InitialFinalStates repeatedAutomata) {
        List<InitialFinalStates> repeatStates = new ArrayList<InitialFinalStates>();
        for (int count = 0; count < repeatNumber; count++) {
            final InitialFinalStates newState = repeatedAutomata.deepCopy();
            repeatStates.add(newState);
        }
        return buildSequenceStates(repeatStates);
    }


    /**
     * Builds an automata which matches an ASCII string case sensitively.
     *
     * <p/><pre>{@code
     *
     * 'abc'   (0) --a--> (1) --b--> (2) --c--> [3]
     *
     * }</pre><p/>
     *
     * @param str The ASCII string to match case sensitively.
     * @return An automata which matches an ASCII string case sensitively.
     */
    @Override
    public InitialFinalStates buildCaseSensitiveStringStates(final String str) {
        final InitialFinalStates states = new InitialFinalStates();
        final State firstState = stateFactory.create(State.NON_FINAL);
        State lastState = firstState;
        for (int index = 0, stop = str.length(); index < stop; index++) {
            final byte transitionByte = (byte) str.charAt(index);
            final State transitionToState = stateFactory.create(State.NON_FINAL);
            final Transition transition = transitionFactory.createByteTransition(transitionByte, transitionToState);
            lastState.addTransition(transition);
            lastState = transitionToState;
        }
        states.initialState = firstState;
        states.setIsFinal(lastState, State.FINAL);
        return states;
    }


    /**
     * Builds an automata which matches an ASCII string case insensitively.
     *
     * <p/><pre>{@code
     *
     * `abc`   (0) --[aA]--> (1) --[bB]--> (2) --[cC]--> [3]
     *
     * }</pre><p/>
     *
     * @param str The ASCII string to match case insensitively.
     * @return An automata which matches an ASCII string case insensitively.
     */
    @Override
    public InitialFinalStates buildCaseInsensitiveStringStates(final String str) {
        final InitialFinalStates states = new InitialFinalStates();
        final State firstState = stateFactory.create(State.NON_FINAL);
        State lastState = firstState;
        for (int index = 0, stop = str.length(); index < stop; index++) {
            final char transitionChar = str.charAt(index);
            Transition transition;
            final State transitionToState = stateFactory.create(State.NON_FINAL);
            if ((transitionChar >= 'A' && transitionChar <= 'Z') ||
                (transitionChar >= 'a' && transitionChar <= 'z')) {
                transition = transitionFactory.createCaseInsensitiveByteTransition(transitionChar, transitionToState);
            } else {
                final byte transitionByte = (byte) transitionChar;
                transition = transitionFactory.createByteTransition(transitionByte, transitionToState);
            }
            lastState.addTransition(transition);
            lastState = transitionToState;
        }
        states.initialState = firstState;
        states.setIsFinal(lastState, State.FINAL);
        return states;
    }



    private InitialFinalStates createInitialFinalStates() {
        final InitialFinalStates states = new InitialFinalStates();
        states.initialState = stateFactory.create(State.NON_FINAL);
        states.finalStates = new ArrayList<State>();
        states.finalStates.add(stateFactory.create(State.FINAL));
        return states;
    }

    
    private InitialFinalStates joinStates(final InitialFinalStates leftState, final InitialFinalStates rightState) {
        final List<InitialFinalStates> joinedAutomata = new ArrayList<InitialFinalStates>();
        joinedAutomata.add(leftState);
        joinedAutomata.add(rightState);
        return buildSequenceStates(joinedAutomata);
    }

}
