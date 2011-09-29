/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 * http://www.opensource.org/licenses/BSD-3-Clause
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

package net.domesdaybook.matcher.multisequence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.domesdaybook.automata.AssociatedState;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.wrapper.Trie;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.Reader;

/**
 *
 * @author matt
 */
public final class TrieMatcher implements MultiSequenceMatcher {

    private final Trie trie;

    
    public TrieMatcher(final Trie trie) {
        if (trie == null) {
            throw new IllegalArgumentException("Null Trie passed in to TrieMatcher.");
        }
        this.trie = trie;
    }


    /**
     * @inheritDoc
     */
    @Override
    public Collection<SequenceMatcher> allMatches(final Reader reader, 
            final long matchPosition) throws IOException {
        final List<SequenceMatcher> result = new ArrayList<SequenceMatcher>();
        final long noOfBytes = reader.length();
        final int minimumLength = trie.getMinimumLength();
        if (matchPosition >= minimumLength - 1 && matchPosition + minimumLength < noOfBytes) {
            final List<State> currentStates = new ArrayList<State>(1); // only ever zero or one state.
            currentStates.add(trie.getInitialState());
            long currentPosition = matchPosition;
            while (!currentStates.isEmpty() && currentPosition < noOfBytes) {
                final State currentState = currentStates.get(0);
                currentStates.clear();
                final byte currentByte = reader.readByte(currentPosition++);
                currentState.appendNextStatesForByte(currentStates, currentByte);
                for (final State state : currentStates) {
                    if (state.isFinal()) {
                        result.addAll(getAssociations(state));
                    }
                }
            }
        }
        return result;
    }

    
    /**
     * @inheritDoc
     * 
     */
    @Override    
    public Collection<SequenceMatcher> allMatches(final byte[] bytes, final int matchPosition) {
        final List<SequenceMatcher> result = new ArrayList<SequenceMatcher>();        
        final int noOfBytes = bytes.length;
        final int minimumLength = trie.getMinimumLength();
        if (matchPosition >= minimumLength - 1 && matchPosition + minimumLength < noOfBytes) {
            final List<State> currentStates = new ArrayList<State>(1); // only ever zero or one state.
            currentStates.add(trie.getInitialState());
            int currentPosition = matchPosition;
            while (!currentStates.isEmpty() && currentPosition < noOfBytes) {
                final State currentState = currentStates.get(0);
                currentStates.clear();
                final byte currentByte = bytes[currentPosition++];
                currentState.appendNextStatesForByte(currentStates, currentByte);
                for (final State state : currentStates) {
                    if (state.isFinal()) {
                        result.addAll(getAssociations(state));
                    }
                }
            }
        }
        return result;
    }    
    
    
    /**
     * @inheritDoc
     * 
     */
    @Override  
    public Collection<SequenceMatcher> allMatchesBackwards(final Reader reader, 
            final long matchPosition) throws IOException {
        final List<SequenceMatcher> result = new ArrayList<SequenceMatcher>();
        final long noOfBytes = reader.length();
        final int minimumLength = trie.getMinimumLength();
        if (matchPosition >= minimumLength - 1 && matchPosition + minimumLength < noOfBytes) {
            final List<State> currentStates = new ArrayList<State>(1); // only ever zero or one state.
            currentStates.add(trie.getInitialState());
            long currentPosition = matchPosition;
            while (!currentStates.isEmpty() && currentPosition >= 0) {
                final State currentState = currentStates.get(0);
                currentStates.clear();
                final byte currentByte = reader.readByte(currentPosition--);
                currentState.appendNextStatesForByte(currentStates, currentByte);
                for (final State state : currentStates) {
                    if (state.isFinal()) {
                        result.addAll(getAssociations(state));
                    }
                }
            }
        }
        return result;
    }

    
    /**
     * @inheritDoc
     * 
     */
    @Override  
    public Collection<SequenceMatcher> allMatchesBackwards(final byte[] bytes, final int matchPosition) {
        final List<SequenceMatcher> result = new ArrayList<SequenceMatcher>();        
        final int noOfBytes = bytes.length;
        final int minimumLength = trie.getMinimumLength();        
        if (matchPosition >= minimumLength - 1 && matchPosition + minimumLength < noOfBytes) {
            final List<State> currentStates = new ArrayList<State>(1); // only ever zero or one state.
            currentStates.add(trie.getInitialState());
            int currentPosition = matchPosition;
            while (!currentStates.isEmpty() && currentPosition >= 0) {
                final State currentState = currentStates.get(0);
                currentStates.clear();
                final byte currentByte = bytes[currentPosition--];
                currentState.appendNextStatesForByte(currentStates, currentByte);
                for (final State state : currentStates) {
                    if (state.isFinal()) {
                        result.addAll(getAssociations(state));
                    }
                }
            }
        }
        return result;
    }
    
    
    
    /**
     * @inheritDoc
     */
    @Override
    public SequenceMatcher firstMatch(final Reader reader, final long matchPosition) 
            throws IOException {
        final long noOfBytes = reader.length();
        final int minimumLength = trie.getMinimumLength();
        if (matchPosition >= minimumLength - 1 && matchPosition + minimumLength < noOfBytes) {
            final List<State> currentStates = new ArrayList<State>(1); // only ever zero or one state.
            currentStates.add(trie.getInitialState());
            long currentPosition = matchPosition;
            while (!currentStates.isEmpty() && currentPosition < noOfBytes) {
                final State currentState = currentStates.get(0);
                currentStates.clear();
                final byte currentByte = reader.readByte(currentPosition++);
                currentState.appendNextStatesForByte(currentStates, currentByte);
                for (final State state : currentStates) {
                    if (state.isFinal()) {
                        return getFirstAssociation(state);
                    }
                }
            }
        }
        return null;
    }
    
    
    /**
     * @inheritDoc
     * 
     */
    @Override    
    public SequenceMatcher firstMatch(final byte[] bytes, final int matchPosition) {
        final int noOfBytes = bytes.length;
        final int minimumLength = trie.getMinimumLength();
        if (matchPosition >= minimumLength - 1 && matchPosition + minimumLength < noOfBytes) {
            final List<State> currentStates = new ArrayList<State>(1); // only ever zero or one state.
            currentStates.add(trie.getInitialState());
            int currentPosition = matchPosition;
            while (!currentStates.isEmpty() && currentPosition < noOfBytes) {
                final State currentState = currentStates.get(0);
                currentStates.clear();
                final byte currentByte = bytes[currentPosition++];
                currentState.appendNextStatesForByte(currentStates, currentByte);
                for (final State state : currentStates) {
                    if (state.isFinal()) {
                        return getFirstAssociation(state);
                    }
                }
            }
        }
        return null;
    }    
    
    
    /**
     * @inheritDoc
     * 
     */
    @Override  
    public SequenceMatcher firstMatchBackwards(final Reader reader, final long matchPosition)
            throws IOException {
        final long noOfBytes = reader.length();
        final int minimumLength = trie.getMinimumLength();
        if (matchPosition >= minimumLength - 1 && matchPosition + minimumLength < noOfBytes) {
            final List<State> currentStates = new ArrayList<State>(1); // only ever zero or one state.
            currentStates.add(trie.getInitialState());
            long currentPosition = matchPosition;
            while (!currentStates.isEmpty() && currentPosition < noOfBytes) {
                final State currentState = currentStates.get(0);
                currentStates.clear();
                final byte currentByte = reader.readByte(currentPosition--);
                currentState.appendNextStatesForByte(currentStates, currentByte);
                for (final State state : currentStates) {
                    if (state.isFinal()) {
                        return getFirstAssociation(state);
                    }
                }
            }
        }
        return null;
    }

    
    /**
     * @inheritDoc
     * 
     */
    @Override  
    public SequenceMatcher firstMatchBackwards(final byte[] bytes, final int matchPosition) {
        final int noOfBytes = bytes.length;
        final int minimumLength = trie.getMinimumLength();
        if (matchPosition >= minimumLength - 1 && matchPosition + minimumLength < noOfBytes) {
            final List<State> currentStates = new ArrayList<State>(1); // only ever zero or one state.
            currentStates.add(trie.getInitialState());
            int currentPosition = matchPosition;
            while (!currentStates.isEmpty() && currentPosition < noOfBytes) {
                final State currentState = currentStates.get(0);
                currentStates.clear();
                final byte currentByte = bytes[currentPosition--];
                currentState.appendNextStatesForByte(currentStates, currentByte);
                for (final State state : currentStates) {
                    if (state.isFinal()) {
                        return getFirstAssociation(state);
                    }
                }
            }
        }
        return null;
    }    
    
    
    /**
     * @inheritDoc
     * 
     */
    @Override    
    public boolean matches(final Reader reader, final long matchPosition) 
            throws IOException {
        return firstMatch(reader, matchPosition) != null;
    }

      
    /**
     * @inheritDoc
     * 
     */
    @Override     
    public boolean matches(final byte[] bytes, final int matchPosition) {
        return firstMatch(bytes, matchPosition) != null;
    }

    
    /**
     * @inheritDoc
     * 
     */
    @Override 
    public boolean matchesBackwards(Reader reader, long matchPosition) 
            throws IOException {
        return firstMatchBackwards(reader, matchPosition) != null;
    }

    
    /**
     * @inheritDoc
     * 
     */
    @Override 
    public boolean matchesBackwards(final byte[] bytes, final int matchPosition) {
        return firstMatchBackwards(bytes, matchPosition) != null;
    }
    
    
    /**
     * @inheritDoc
     * 
     */
    @Override 
    public int getMinimumLength() {
        return trie.getMinimumLength();
    }

    
    /**
     * @inheritDoc
     * 
     */
    @Override 
    public int getMaximumLength() {
        return trie.getMaximumLength();
    }
    
    
    private SequenceMatcher getFirstAssociation(final State state) {
        final AssociatedState<SequenceMatcher> trieState = (AssociatedState<SequenceMatcher>) state;
        return trieState.getAssociations().iterator().next();
    }
    
    
    private Collection<SequenceMatcher> getAssociations(final State state) {
        final AssociatedState<SequenceMatcher> trieState = (AssociatedState<SequenceMatcher>) state;
        return trieState.getAssociations();
    }

}
