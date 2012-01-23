/*
 * Copyright Matt Palmer 2012, All rights reserved.
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

package net.domesdaybook.automata.trie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.domesdaybook.automata.BaseAutomata;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.automata.state.BaseStateFactory;
import net.domesdaybook.automata.state.StateFactory;
import net.domesdaybook.automata.transition.SingleByteMatcherTransitionFactory;
import net.domesdaybook.automata.transition.TransitionFactory;
import net.domesdaybook.bytes.ByteUtilities;

/**
 *
 * @author Matt Palmer
 */
public abstract class AbstractTrie<T> extends BaseAutomata<T> implements Trie<T> {

    private final StateFactory<T> stateFactory;
    private final TransitionFactory transitionFactory;
    
    private final List<T> sequences;
    
    private int minimumLength = -1;
    private int maximumLength = 0;
    
    public AbstractTrie() {
        this(new BaseStateFactory<T>(), null);
    }
    
    
    public AbstractTrie(final StateFactory<T> stateFactory) {
        this(stateFactory, null);
    }
    
    
    public AbstractTrie(final TransitionFactory transitionFactory) {
        this(new BaseStateFactory(), transitionFactory);
    }
    
    
    public AbstractTrie(final StateFactory<T> stateFactory, 
                        final TransitionFactory transitionFactory) {
        super(stateFactory.create(State.NON_FINAL));
        this.stateFactory = stateFactory;
        if (transitionFactory == null) {
            this.transitionFactory = new SingleByteMatcherTransitionFactory();
        } else {
            this.transitionFactory = transitionFactory;
        }
        this.sequences = new ArrayList<T>();
    }
    
    
    public int getMinimumLength() {
        return minimumLength == -1 ? 0 : minimumLength;
    }

    
    public int getMaximumLength() {
        return maximumLength;
    }
    
    
    public Collection<T> getSequences() {
        return new ArrayList<T>(sequences);
    }
    
    
    public void add(final T sequence) {
        List<State<T>> currentStates = new ArrayList<State<T>>();
        currentStates.add(initialState);
        final int length = getSequenceLength(sequence);
        for (int position = 0; position < length; position++) {
            final byte[] matchingBytes = getBytesForPosition(sequence, position);
            final boolean isFinal = position == length - 1;
            currentStates = nextStates(currentStates, matchingBytes, isFinal);
        }
        for (final State<T> finalState : currentStates) {
            finalState.addAssociation(sequence);
        }
        setMinMaxLength(length);
        sequences.add(sequence);
    }
    
    
    public void addAll(final Collection<? extends T> sequences) {
        for (final T sequence : sequences) {
            add(sequence);
        }
    }
    
    
    public void addReversed(final T sequence) {
        List<State<T>> currentStates = new ArrayList<State<T>>();
        currentStates.add(initialState);
        final int length = getSequenceLength(sequence);
        for (int position = length - 1; position >= 0; position--) {
            final byte[] matchingBytes = getBytesForPosition(sequence, position);
            final boolean isFinal = position == 0;
            currentStates = nextStates(currentStates, matchingBytes, isFinal);
        }
        for (final State<T> finalState : currentStates) {
            finalState.addAssociation(sequence);
        }
        setMinMaxLength(length);
        sequences.add(sequence);        
    }  
    

    public void addAllReversed(final Collection<? extends T> sequences) {
        for (final T sequence : sequences) {
            addReversed(sequence);
        }
    }    
    
    
    protected abstract int getSequenceLength(T sequence);
    
    
    protected abstract byte[] getBytesForPosition(T sequence, int position);
    
    
    
    private void setMinMaxLength(final int length) {
        if (length > maximumLength) {
            maximumLength = length;
        }
        if (length < minimumLength || minimumLength == -1) {
            minimumLength = length;
        }
    }

    
    /**
     * 
     * @param currentStates
     * @param bytes
     * @param isFinal
     * @return
     */
    private List<State<T>> nextStates(final List<State<T>> currentStates, 
                                      final byte[] bytes, 
                                      final boolean isFinal) {
        final List<State<T>> nextStates = new ArrayList<State<T>>();
        final Set<Byte> allBytesToTransitionOn = ByteUtilities.toSet(bytes);
        for (final State currentState : currentStates) {
            // make a defensive copy of the transitions of the current state:
            final List<Transition> transitions = new ArrayList<Transition>(currentState.getTransitions());
            final Set<Byte> bytesToTransitionOn = new HashSet<Byte>(allBytesToTransitionOn);
            for (final Transition transition : transitions) {
                
                final Set<Byte> originalTransitionBytes = ByteUtilities.toSet(transition.getBytes());
                final int originalTransitionBytesSize = originalTransitionBytes.size();
                final Set<Byte> bytesInCommon = ByteUtilities.subtract(originalTransitionBytes, bytesToTransitionOn);
                
                // If the existing transition is the same or a subset of the new transition bytes:
                final int numberOfBytesInCommon = bytesInCommon.size();
                if (numberOfBytesInCommon == originalTransitionBytesSize) {
                    
                    final State toState = transition.getToState();
                    
                    // Ensure that the state is final if necessary:
                    if (isFinal) {
                        toState.setIsFinal(true);
                    }
                    
                    // Add this state to the states we have to process next.
                    nextStates.add((State<T>) toState);
                    
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
                    nextStates.add((State<T>) newToState);
                    
                    // Remove the original transition from the current state:
                    currentState.removeTransition(transition);
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
                final State<T> newState = stateFactory.create(isFinal);
                final Transition newTransition = transitionFactory.createSetTransition(bytesToTransitionOn, false, newState);
                currentState.addTransition(newTransition);
                nextStates.add(newState);
            }
        }
        
        return nextStates;
    }
    
    
}
