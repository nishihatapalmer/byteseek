/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.multisequence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.domesdaybook.automata.AssociatedState;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.wrapper.Trie;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.ByteReader;

/**
 *
 * @author matt
 */
public final class TrieMatcher implements MultiSequenceMatcher {

    final Trie trie;

    public TrieMatcher(final Trie trie) {
        if (trie == null) {
            throw new IllegalArgumentException("Null Trie passed in to TrieMatcher.");
        }
        this.trie = trie;
    }


    /**
     * @inheritDoc
     * 
     * Note: will return false if access is outside the byte reader.
     *       It will not throw an IndexOutOfBoundsException.
     */
    @Override
    public List<SequenceMatcher> allMatches(final ByteReader reader, final long matchPosition) {
        final List<SequenceMatcher> result = new ArrayList<SequenceMatcher>();
        final long noOfBytes = reader.length();
        if (matchPosition >= 0 && matchPosition + trie.getMinimumLength() < noOfBytes) {
            final List<State> currentStates = new ArrayList<State>();
            currentStates.add(trie.getInitialState());
            long currentPosition = matchPosition;
            while (!currentStates.isEmpty() && currentPosition < noOfBytes) {
                final State currentState = currentStates.get(0);
                currentStates.clear();
                final byte currentByte = reader.readByte(currentPosition++);
                currentState.appendNextStatesForByte(currentStates, currentByte);
                for (final State state : currentStates) {
                    if (state.isFinal()) {
                        final AssociatedState<SequenceMatcher> trieState = (AssociatedState<SequenceMatcher>) state;
                        result.addAll(trieState.getAssociations());
                    }
                }
            }
        }
        return result;
    }

    
    /**
     * @inheritDoc
     * 
     * Note: will return false if access is outside the byte reader.
     *       It will not throw an IndexOutOfBoundsException.
     */
    @Override
    public SequenceMatcher anyMatch(final ByteReader reader, final long matchPosition) {
        final long noOfBytes = reader.length();
        if (matchPosition >= 0 && matchPosition + trie.getMinimumLength() < noOfBytes) {
            final List<State> currentStates = new ArrayList<State>();
            currentStates.add(trie.getInitialState());
            long currentPosition = matchPosition;
            while (!currentStates.isEmpty() && currentPosition < noOfBytes) {
                final State currentState = currentStates.get(0);
                currentStates.clear();
                final byte currentByte = reader.readByte(currentPosition++);
                currentState.appendNextStatesForByte(currentStates, currentByte);
                for (final State state : currentStates) {
                    if (state.isFinal()) {
                        final AssociatedState<SequenceMatcher> trieState = (AssociatedState<SequenceMatcher>) state;
                        return trieState.getAssociations().iterator().next();
                    }
                }
            }
        }
        return null;
    }
    

    /**
     * @inheritDoc
     * 
     * Note: will return false if access is outside the byte reader.
     *       It will not throw an IndexOutOfBoundsException.
     */
    @Override    
    public boolean matches(final ByteReader reader, final long matchPosition) {
        return anyMatch(reader, matchPosition) != null;
    }

    
    /**
     * @inheritDoc
     * 
     * Note: will return false if access is outside the byte array.
     *       It will not throw an IndexOutOfBoundsException.
     */
    @Override    
    public Collection<SequenceMatcher> allMatches(final byte[] bytes, final int matchPosition) {
        final int noOfBytes = bytes.length;
        final List<SequenceMatcher> result = new ArrayList<SequenceMatcher>();
        if (matchPosition >= 0 && matchPosition + trie.getMinimumLength() < noOfBytes) {
            final List<State> currentStates = new ArrayList<State>();
            currentStates.add(trie.getInitialState());
            int currentPosition = matchPosition;
            while (!currentStates.isEmpty() && currentPosition < noOfBytes) {
                final State currentState = currentStates.get(0);
                currentStates.clear();
                final byte currentByte = bytes[currentPosition++];
                currentState.appendNextStatesForByte(currentStates, currentByte);
                for (final State state : currentStates) {
                    if (state.isFinal()) {
                        final AssociatedState<SequenceMatcher> trieState = (AssociatedState<SequenceMatcher>) state;
                        result.addAll(trieState.getAssociations());
                    }
                }
            }
        }
        return result;
    }

    
    /**
     * @inheritDoc
     * 
     * Note: will return false if access is outside the byte array.
     *       It will not throw an IndexOutOfBoundsException.
     */
    @Override    
    public SequenceMatcher anyMatch(final byte[] bytes, final int matchPosition) {
        final int noOfBytes = bytes.length;
        if (matchPosition >= 0 && matchPosition + trie.getMinimumLength() < noOfBytes) {
            final List<State> currentStates = new ArrayList<State>();
            currentStates.add(trie.getInitialState());
            int currentPosition = matchPosition;
            while (!currentStates.isEmpty() && currentPosition < noOfBytes) {
                final State currentState = currentStates.get(0);
                currentStates.clear();
                final byte currentByte = bytes[currentPosition++];
                currentState.appendNextStatesForByte(currentStates, currentByte);
                for (final State state : currentStates) {
                    if (state.isFinal()) {
                        final AssociatedState<SequenceMatcher> trieState = (AssociatedState<SequenceMatcher>) state;
                        return trieState.getAssociations().iterator().next();
                    }
                }
            }
        }
        return null;
    }
    
    
    /**
     * @inheritDoc
     * 
     * Note: will return false if access is outside the byte array.
     *       It will not throw an IndexOutOfBoundsException.
     */
    @Override     
    public boolean matches(final byte[] bytes, final int matchPosition) {
        return anyMatch(bytes, matchPosition) != null;
    }

}
