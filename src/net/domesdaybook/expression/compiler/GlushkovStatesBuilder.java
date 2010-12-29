/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 * A Glushkov Non-deterministic Finite-state Automata (NFA) is an automata which
 * is constructed to have one initial state, and additionally, a state for each
 * position in the regular expression that defines a byte or set of bytes.
 *
 * Transitions exist from each position to every other reachable position,
 * on the byte or bytes of the position being transitioned to.
 *
 * Being non-deterministic means that from any given state,
 * you can have transitions to more than one other state on the same byte value.
 * By way of constrast, Deterministic Finite-state Automata (DFAs) have at most one
 * state they can transition to on a given byte value (or none, if there is no match).
 *
 * Unlike the classic Thompson construction (the most common regular expression NFA)
 * Glushkov automata have no "empty" transitions - that is, transitions to another
 * state without reading a byte.  The Thompson NFA uses empty transitions to
 * simplify constructing the automata, by wiring up states together as the need
 * arises during construction, and to make mathematically proving certain properties
 * of the automata easier.  However, they seem to have no value other than this.
 *
 * Having no empty transitions makes the automata smaller, more peformant,
 * and easier to transform it further (e.g. building a DFA from it)
 * but makes constructing it a little more difficult in the first place.
 * 
 */

package net.domesdaybook.expression.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.automata.nfa.NfaState;

/**
 *
 * @author matt
 */
public class GlushkovStatesBuilder implements NfaInitialFinalStatesBuilder {

     private final TransitionFactory transitionFactory;
     private final NfaStateBuilder builder;

     
     public GlushkovStatesBuilder(final TransitionFactory transitionFactory, NfaStateBuilder builder) {
         this.transitionFactory = transitionFactory;
         this.builder = builder;
     }


     /**
      * Build an initial state and a final state,
      * and join them together with a transition on the transition byte.
      * 
      * @param transitionByte
      * @return
      */
     public final NfaInitialFinalStates buildSingleByteStates(final byte transitionByte) {
        final NfaInitialFinalStates states = createInitialFinalStates();
        final NfaState finalState = states.finalStates.get(0);
        final Transition transition = transitionFactory.createByteTransition(transitionByte, finalState);
        states.initialState.addTransition(transition);
        return states;
    }


    
    @Override
    public final NfaInitialFinalStates buildSetStates(final Set<Byte> byteSet, final boolean negated) {
        final NfaInitialFinalStates states = createInitialFinalStates();
        final NfaState finalState = states.finalStates.get(0);
        final Transition transition = transitionFactory.createSetTransition(byteSet, true, finalState);
        states.initialState.addTransition(transition);
        return states;
    }


    @Override
    public NfaInitialFinalStates buildAnyByteStates() {
        final NfaInitialFinalStates states = createInitialFinalStates();
        final NfaState finalState = states.finalStates.get(0);
        final Transition transition = transitionFactory.createAnyByteTransition(finalState);
        states.initialState.addTransition(transition);
        return states;

    }

    /**
     * Build an initial state and a final state,
     * and join them together with a transition on the bitmask byte.
     *
     * @param bitMask
     * @return
     */
    public NfaInitialFinalStates buildAllBitmaskStates(final byte bitMask) {
        final NfaInitialFinalStates states = createInitialFinalStates();
        final NfaState finalState = states.finalStates.get(0);
        final Transition transition = transitionFactory.createAllBitmaskTransition(bitMask, finalState);
        states.initialState.addTransition(transition);
        return states;
    }

    
    /**
     * To create an automata joining a sequence of states together,
     * we must transition each state to its neighbour to the right.
     * We can achieve this by making each of the final transitions of a state
     * (with another state to its right to join to) copy all of the transitions
     * of the right-hand-side initial state.   Each final state effectively
     * becomes the initial state of the automata to its right.
     * Then we can discard the now-redundant initial state of the right hand side.
     * The left hand side final states all become as final as the initial state
     * they end up replacing.
     *
     * @param sequenceStates A list of initial / final states of automata to be joined.
     * @return An object holding the initial and final states of the sequence automata.
     */
    @Override
    public NfaInitialFinalStates buildSequenceStates(List<NfaInitialFinalStates> sequenceStates) {
        // process the sequence of states joining final states to what the next initial
        // states have transitions to.
        final List<NfaState> finalSequenceStates = new ArrayList<NfaState>();
        for (int itemIndex = 1, stop = sequenceStates.size(); itemIndex < stop; itemIndex++) {
            final NfaInitialFinalStates leftState = sequenceStates.get(itemIndex-1);
            final NfaInitialFinalStates rightState = sequenceStates.get(itemIndex);
            final NfaState initialStateToReplace = rightState.initialState;
            final List<Transition> transitionList = initialStateToReplace.getTransitions();
            final boolean initialStateIsFinal = initialStateToReplace.isFinal();
            
            // for each final state of the left hand side, 
            // add the initial transitions from the right hand side,
            // and set whether it is final or not based on the initial right hand state.
            for (NfaState state : leftState.finalStates) {
                state.addAllTransitions(transitionList);
                state.setIsFinal(initialStateIsFinal);
                if (initialStateIsFinal) {
                    finalSequenceStates.add(state);
                }
            }

            // Set the initial state of the right hand side to null,
            // allowing it to be garbage collected.
            // rightState.initialState = null;
        }

        // Add the final states of the very last state to the sequence final states:
        final NfaInitialFinalStates lastState = sequenceStates.get(sequenceStates.size() - 1);
        finalSequenceStates.addAll(lastState.finalStates);
        
        // Wrap it all up in a new states object representing
        // the start and final states of the sequence.
        final NfaInitialFinalStates states = new NfaInitialFinalStates();
        final NfaInitialFinalStates firstState = sequenceStates.get(0);
        states.initialState = firstState.initialState;
        states.finalStates = finalSequenceStates;
        return states;
    }


    /**
     * To create an automata of alternative sequences. identify the initial state of
     * each of the alternatives with each other.  In practice, retain one state and
     * add the transitions of the other initial states to it.
     *
     * The initial state is final if any of the alternate initial states were final.
     *
     * @param alternateStates A list of alternative automata, wrapped in an object giving the initial and final states of the automata.
     * @return An object holding the initial and final states of the alternative automata.
     */
    @Override
    public NfaInitialFinalStates buildAlternativeStates(List<NfaInitialFinalStates> alternateStates) {
       
        // Merge all the initial states of the alternatives, so we can
        // transition into any of them from a single initial state.  
        // If any of the initial states being merged is final, then the final merged state is final.
        // Also build a list of the sum of all the final states in all the alternatives.
        final NfaInitialFinalStates firstOption = alternateStates.get(0);
        final NfaState initialState = firstOption.initialState;
        final List<NfaState> finalStates = new ArrayList<NfaState>(firstOption.finalStates);
        boolean anyMergedStateIsFinal = initialState.isFinal();
        for (int alternateIndex = 1, stop = alternateStates.size(); alternateIndex < stop; alternateIndex++) {
            final NfaInitialFinalStates altStates = alternateStates.get(alternateIndex);
            final NfaState mergeState = altStates.initialState;
            anyMergedStateIsFinal = anyMergedStateIsFinal | mergeState.isFinal();
            initialState.addAllTransitions(mergeState.getTransitions());
            finalStates.addAll(altStates.finalStates);
        }

        // Wrap it all up in a states object and set whether the initial state is final:
        final NfaInitialFinalStates states = new NfaInitialFinalStates();
        states.initialState = initialState;
        states.finalStates = finalStates;
        states.setIsFinal(initialState, anyMergedStateIsFinal);
        return states;
    }


    /**
     * Wires the automaton provided to be repeated zero to many times.
     * This is done by making the initial state final,
     * allowing for an instant match to cover the zero case.
     * Additionally, all of the final states loop back to the states that
     * the initial state points to, allowing for as many repetitions as needed.
     * It is the same as building one to many states, except the initial state
     * is set to final to cover the zero case.
     * @param zeroToManyStates
     * @return
     */
    @Override
    public NfaInitialFinalStates buildZeroToManyStates(NfaInitialFinalStates zeroToManyStates) {
        final NfaState initialState = zeroToManyStates.initialState;
        final List<Transition> intialTransitions = initialState.getTransitions();
        final List<NfaState> finalStates = zeroToManyStates.finalStates;
        for (NfaState state : finalStates) {
            state.addAllTransitions(intialTransitions);
        }
        zeroToManyStates.setIsFinal(initialState, State.FINAL);
        return zeroToManyStates;
    }
    

    /**
     * Wires the automaton provided to be repeated zero to many times.
     * This is done by making all the final states loop back to the states
     * that the initial state points to, allowing for as many repetitions as needed.
     * It is the same as zeroToManyStates, except that the initial state is not made final.
     * @param oneToManyStates
     * @return
     */
    @Override
    public NfaInitialFinalStates buildOneToManyStates(NfaInitialFinalStates oneToManyStates) {
        final NfaState initialState = oneToManyStates.initialState;
        final List<Transition> intialTransitions = initialState.getTransitions();
        final List<NfaState> finalStates = oneToManyStates.finalStates;
        for (NfaState state : finalStates) {
            state.addAllTransitions(intialTransitions);
        }
        return oneToManyStates;
    }


    /**
     * Makes the automata passed in optional.
     * It does this by making the initial state final.
     * @param optionalStates
     * @return
     */
    @Override
    public NfaInitialFinalStates buildOptionalStates(NfaInitialFinalStates optionalStates) {
        optionalStates.setIsFinal(optionalStates.initialState, State.FINAL);
        return optionalStates;
    }



    @Override
    public NfaInitialFinalStates buildMinToManyStates(int minRepeat, NfaInitialFinalStates repeatedAutomata) {

        // sequence:
        //   min to max states: 0 to min
        //   zero to many states

        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public NfaInitialFinalStates buildMinToMaxStates(int minRepeat, int maxRepeat, NfaInitialFinalStates repeatedAutomata) {
        // sequence:
        //   0 to min states:
        //   min to max optional states:

        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public NfaInitialFinalStates buildCaseSensitiveStringStates(String str) {
        final NfaInitialFinalStates states = new NfaInitialFinalStates();
        final NfaState firstState = builder.build(State.NON_FINAL);
        NfaState lastState = firstState;
        for (int index = 0, stop = str.length(); index < stop; index++) {
            final byte transitionByte = (byte) str.charAt(index);
            final NfaState transitionToState = builder.build(State.NON_FINAL);
            final Transition transition = transitionFactory.createByteTransition(transitionByte, transitionToState);
            lastState.addTransition(transition);
            lastState = transitionToState;
        }
        states.initialState = firstState;
        states.setIsFinal(lastState, State.FINAL);
        return states;
    }


    @Override
    public NfaInitialFinalStates buildCaseInsensitiveStringStates(String str) {
        final NfaInitialFinalStates states = new NfaInitialFinalStates();
        final NfaState firstState = builder.build(State.NON_FINAL);
        NfaState lastState = firstState;
        for (int index = 0, stop = str.length(); index < stop; index++) {
            final char transitionChar = str.charAt(index);
            Transition transition;
            final NfaState transitionToState = builder.build(State.NON_FINAL);
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



    private NfaInitialFinalStates createInitialFinalStates() {
        final NfaInitialFinalStates states = new NfaInitialFinalStates();
        states.initialState = builder.build(State.NON_FINAL);
        states.finalStates = new ArrayList<NfaState>();
        states.finalStates.add(builder.build(State.FINAL));
        return states;
    }

}
