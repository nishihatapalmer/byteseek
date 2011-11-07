/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 *
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
 * 
 */


package net.domesdaybook.compiler.multisequence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.automata.state.SimpleStateFactory;
import net.domesdaybook.automata.StateFactory;
import net.domesdaybook.automata.TransitionFactory;
import net.domesdaybook.automata.transition.SimpleTransitionFactory;
import net.domesdaybook.automata.wrapper.Trie;
import net.domesdaybook.compiler.CompileException;
import net.domesdaybook.compiler.ReversibleCompiler;
import net.domesdaybook.compiler.ReversibleCompiler.Direction;
import net.domesdaybook.matcher.sequence.ByteSequenceMatcher;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.bytes.ByteUtilities;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;


/**
 * Compiles a collection of sequence matchers into a Trie automata.
 * 
 * @author matt
 */
public final class TrieCompiler implements ReversibleCompiler<Trie, SequenceMatcher> {

    private static TrieCompiler defaultCompiler;
    
    
    /**
     * 
     * @param sequences
     * @return
     * @throws CompileException
     */
    public static Trie trieFrom(final Collection<SequenceMatcher> sequences) throws CompileException {
        return trieFrom(sequences, Direction.FORWARDS);
    }
    
    
    /**
     * 
     * @param bytes
     * @return
     * @throws CompileException
     */
    public static Trie trieFrom(final List<byte[]> bytes) throws CompileException {
        return trieFrom(bytes, Direction.FORWARDS);
    }
    
    
    /**
     * 
     * @param sequences
     * @param direction
     * @return
     * @throws CompileException
     */
    public static Trie trieFrom(final Collection<SequenceMatcher> sequences, final Direction direction) throws CompileException {
        defaultCompiler = new TrieCompiler();
        return defaultCompiler.compile(sequences, direction);
    }
    
    /**
     * 
     * @param bytes
     * @param direction
     * @return
     * @throws CompileException
     */
    public static Trie trieFrom(final List<byte[]> bytes, final Direction direction) throws CompileException {
        defaultCompiler = new TrieCompiler();
        final List<SequenceMatcher> matchers = new ArrayList<SequenceMatcher>(bytes.size());
        for (final byte[] bytesToUse : bytes) {
            matchers.add(new ByteSequenceMatcher(bytesToUse));
        }
        return defaultCompiler.compile(matchers, direction);
    }

    
    
    
    private final StateFactory<SequenceMatcher> stateFactory;
    private final TransitionFactory transitionFactory;
    

    /**
     * 
     */
    public TrieCompiler() {
        this(null, null);
    }

    
    /**
     * 
     * @param stateFactory
     */
    public TrieCompiler(final StateFactory<SequenceMatcher> stateFactory) {
        this(stateFactory, null);
    }

   
    /**
     * 
     * @param transitionFactory
     */
    public TrieCompiler(final TransitionFactory transitionFactory) {
        this(null, transitionFactory);
    }
   

    /**
     * 
     * @param stateFactory
     * @param transitionFactory
     */
    public TrieCompiler(final StateFactory<SequenceMatcher> stateFactory, 
                        final TransitionFactory transitionFactory) {
        if (stateFactory == null) {
            this.stateFactory = new SimpleStateFactory<SequenceMatcher>();
        } else {
            this.stateFactory = stateFactory;
        }
        if (transitionFactory == null) {
            this.transitionFactory = new SimpleTransitionFactory();
        } else {
            this.transitionFactory = transitionFactory;
        }
    }


   
    @Override
    public Trie compile(final SequenceMatcher matcher) throws CompileException {
        final Collection<SequenceMatcher> matchers = new ArrayList<SequenceMatcher>(1);
        matchers.add(matcher);
        return compile(matchers, Direction.FORWARDS);
    }    
    
    
    @Override
    public Trie compile(final SequenceMatcher matcher, 
                        final Direction direction) throws CompileException {
        final Collection<SequenceMatcher> matchers = new ArrayList<SequenceMatcher>(1);
        matchers.add(matcher);
        return compile(matchers, direction);        
    }   
    

    @Override
    public Trie compile(final Collection<SequenceMatcher> matchers) throws CompileException {
        return compile(matchers, Direction.FORWARDS);
    }       
    
    
    @Override
    public final Trie compile(final Collection<SequenceMatcher> sequences, 
                              final Direction direction) throws CompileException {
        State<SequenceMatcher> initialState = stateFactory.create(State.NON_FINAL);
        int minLength = Integer.MAX_VALUE;
        int maxLength = 0;
        for (final SequenceMatcher sequence : sequences) {
            if (direction == Direction.FORWARDS) {
                addSequence(sequence, initialState);
            } else {
                addReversedSequence(sequence, initialState);
            }
            final int len = sequence.length();
            if (len < minLength) {
                minLength = len;
            }
            if (len > maxLength) {
                maxLength = len;
            }
        }
        return new Trie(initialState, minLength, maxLength);
    }

    
    private void addSequence(final SequenceMatcher sequence, 
                             final State<SequenceMatcher> initialState) {
        List<State<SequenceMatcher>> currentStates = new ArrayList<State<SequenceMatcher>>();
        currentStates.add(initialState);
        final int lastPosition = sequence.length() - 1;
        for (int position = 0; position <= lastPosition; position++) {
            final SingleByteMatcher byteMatcher = sequence.getByteMatcherForPosition(position);
            currentStates = nextStates(currentStates, byteMatcher, position == lastPosition);
        }
        for (final State<SequenceMatcher> finalState : currentStates) {
            finalState.addAssociation(sequence);
        }
    }
    
    
    private void addReversedSequence(final SequenceMatcher sequence, 
                                     final State<SequenceMatcher> initialState) {
        List<State<SequenceMatcher>> currentStates = new ArrayList<State<SequenceMatcher>>();
        currentStates.add(initialState);
        final int lastPosition = sequence.length() - 1;
        for (int position = lastPosition; position >= 0; position--) {
            final SingleByteMatcher byteMatcher = sequence.getByteMatcherForPosition(position);
            currentStates = nextStates(currentStates, byteMatcher, position == 0);
        }
        for (final State<SequenceMatcher> finalState : currentStates) {
            finalState.addAssociation(sequence);
        }
    }      

    
    /**
     * 
     * @param currentStates
     * @param bytes
     * @param isFinal
     * @return
     */
    protected final List<State<SequenceMatcher>> nextStates(final List<State<SequenceMatcher>> currentStates, 
                                                            final SingleByteMatcher bytes, 
                                                            final boolean isFinal) {
        final List<State<SequenceMatcher>> nextStates = new ArrayList<State<SequenceMatcher>>();
        final Set<Byte> allBytesToTransitionOn = ByteUtilities.toSet(bytes.getMatchingBytes());
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
                    nextStates.add((State<SequenceMatcher>) toState);
                    
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
                    nextStates.add((State<SequenceMatcher>) newToState);
                    
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
                final State<SequenceMatcher> newState = stateFactory.create(isFinal);
                final Transition newTransition = transitionFactory.createSetTransition(bytesToTransitionOn, false, newState);
                currentState.addTransition(newTransition);
                nextStates.add(newState);
            }
        }
        
        return nextStates;
    }

    
    
}
