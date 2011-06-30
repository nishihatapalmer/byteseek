/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */


package net.domesdaybook.compiler.automata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.domesdaybook.automata.AssociatedState;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.automata.state.AssociatedStateFactory;
import net.domesdaybook.automata.state.SimpleAssociatedStateFactory;
import net.domesdaybook.automata.transition.TransitionFactory;
import net.domesdaybook.automata.transition.TransitionSingleByteMatcherFactory;
import net.domesdaybook.compiler.CompileException;
import net.domesdaybook.compiler.Compiler;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.matcher.singlebyte.ByteUtilities;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;


/**
 * Compiles a list of sequence matchers into a Trie automata.
 * 
 * @author matt
 */
public final class TrieCompiler implements Compiler<AssociatedState<SequenceMatcher>, List<SequenceMatcher>> {

    private static TrieCompiler defaultCompiler;
    public static AssociatedState<SequenceMatcher> trieFrom(List<SequenceMatcher> sequences) throws CompileException {
        defaultCompiler = new TrieCompiler();
        return defaultCompiler.compile(sequences);
    }

    
    private final AssociatedStateFactory<SequenceMatcher> stateFactory;
    private final TransitionFactory transitionFactory;
    

    public TrieCompiler() {
        this(null, null);
    }

    
    public TrieCompiler(AssociatedStateFactory<SequenceMatcher> stateFactory) {
        this(stateFactory, null);
    }

   
    public TrieCompiler(TransitionFactory transitionFactory) {
        this(null, transitionFactory);
    }
   

    public TrieCompiler(AssociatedStateFactory<SequenceMatcher> stateFactory, TransitionFactory transitionFactory) {
        if (stateFactory == null) {
            this.stateFactory = new SimpleAssociatedStateFactory<SequenceMatcher>();
        } else {
            this.stateFactory = stateFactory;
        }
        if (transitionFactory == null) {
            this.transitionFactory = new TransitionSingleByteMatcherFactory();
        } else {
            this.transitionFactory = transitionFactory;
        }
    }

    
    @Override
    public AssociatedState<SequenceMatcher> compile(List<SequenceMatcher> sequences) throws CompileException {
        AssociatedState<SequenceMatcher> initialState = stateFactory.create(State.NON_FINAL);
        for (SequenceMatcher sequence : sequences) {
            addSequence(sequence, initialState);
        }
        return initialState;
    }

    
    private void addSequence(SequenceMatcher sequence, AssociatedState<SequenceMatcher> initialState) {
        List<AssociatedState<SequenceMatcher>> currentStates = new ArrayList<AssociatedState<SequenceMatcher>>();
        currentStates.add(initialState);
        final int lastPosition = sequence.length() - 1;
        for (int position = 0; position <= lastPosition; position++) {
            final SingleByteMatcher byteMatcher = sequence.getByteMatcherForPosition(position);
            currentStates = nextStates(currentStates, byteMatcher, position == lastPosition);
        }
        for (AssociatedState<SequenceMatcher> finalState : currentStates) {
            finalState.addObject(sequence);
        }
    }

    
    private List<AssociatedState<SequenceMatcher>> nextStates(List<AssociatedState<SequenceMatcher>> currentStates, SingleByteMatcher bytes, boolean isFinal) {
        final List<AssociatedState<SequenceMatcher>> nextStates = new ArrayList<AssociatedState<SequenceMatcher>>();
        final Set<Byte> allBytesToTransitionOn = ByteUtilities.toSet(bytes.getMatchingBytes());
        for (final State currentState : currentStates) {
            // make a defensive copy of the transitions of the current state:
            final List<Transition> transitions = new ArrayList<Transition>(currentState.getTransitions());
            final Set<Byte> bytesToTransitionOn = new HashSet<Byte>(allBytesToTransitionOn);
            for (final Transition transition : transitions) {
                
                final Set<Byte> originalTransitionBytes = ByteUtilities.toSet(transition.getBytes());
                final int originalTransitionBytesSize = originalTransitionBytes.size();
                final Set<Byte> bytesInCommon = subtract(originalTransitionBytes, bytesToTransitionOn);
                
                // If the existing transition is the same or a subset of the new transition bytes:
                final int numberOfBytesInCommon = bytesInCommon.size();
                if (numberOfBytesInCommon == originalTransitionBytesSize) {
                    
                    final State toState = transition.getToState();
                    
                    // Ensure that the state is final if necessary:
                    if (isFinal) {
                        toState.setIsFinal(true);
                    }
                    
                    // Add this state to the states we have to process next.
                    nextStates.add((AssociatedState<SequenceMatcher>) toState);
                    
                } else if (numberOfBytesInCommon > 0) {
                    // Only some bytes are in common.  
                    // We will have to split the existing transition to
                    // two states, and recreate the transitions to them:
                    final State originalToState = transition.getToState();
                    if (isFinal) {
                        originalToState.setIsFinal(true);
                    }
                    final State newToState = originalToState.deepCopy();                    
                    
                    // Add a transition to the bytes which are not in common:
                    final Transition bytesNotInCommonTransition = transitionFactory.createSetTransition(originalTransitionBytes, false, originalToState);
                    currentState.addTransition(bytesNotInCommonTransition);
                    
                    // Add a transition to the bytes in common:
                    final Transition bytesInCommonTransition = transitionFactory.createSetTransition(bytesInCommon, false, newToState);
                    currentState.addTransition(bytesInCommonTransition);
                   
                    // Add the bytes in common state to the next states to process:
                    nextStates.add((AssociatedState<SequenceMatcher>) newToState);
                    
                    // Clean up and optimise the current state:
                    currentState.removeTransition(transition);
                    currentState.setTransitionStrategy(State.DFA_STATE_STRATEGY);
                }
                
                // If we have no further bytes to process, just break out.
                final int numberOfBytesLeft = bytesToTransitionOn.size();
                if (numberOfBytesLeft == 0) {
                    break;
                }                
            }
            
            // If there are any bytes left over, create a transition to a new state:
            final int numberOfBytesLeft = bytesToTransitionOn.size();
            if (numberOfBytesLeft > 0) {
                final AssociatedState<SequenceMatcher> newState = stateFactory.create(isFinal);
                final Transition newTransition = transitionFactory.createSetTransition(bytesToTransitionOn, false, newState);
                currentState.addTransition(newTransition);
                nextStates.add(newState);
            }
        }
        
        return nextStates;
    }

    
    
    private Set<Byte> subtract(final Set<Byte> bytes, final Set<Byte> fromSet) {
        final Set<Byte> bytesInCommon = new LinkedHashSet<Byte>();
        final Iterator<Byte> byteIterator = bytes.iterator();
        while (byteIterator.hasNext()) {
            final Byte theByte = byteIterator.next();
            if (fromSet.remove(theByte)) {
                bytesInCommon.add(theByte);
                byteIterator.remove();
            }
        }
        return bytesInCommon;
    }
    

}
