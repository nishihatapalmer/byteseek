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

package net.domesdaybook.matcher.multisequence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.trie.Trie;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.reader.Window;

/**
 *
 * @author matt
 */
public final class TrieSequenceMatcher implements MultiSequenceMatcher {

    private final Trie<SequenceMatcher> trie;

    
    /**
     * 
     * @param trie
     */
    public TrieSequenceMatcher(final Trie<SequenceMatcher> trie) {
        if (trie == null) {
            throw new IllegalArgumentException("Null Trie passed in to TrieMatcher.");
        }
        this.trie = trie;
    }


    /**
     * @throws IOException 
     * @inheritDoc
     */
    @Override
    public Collection<SequenceMatcher> allMatches(final Reader reader, 
            final long matchPosition) throws IOException {
        final List<SequenceMatcher> result = new ArrayList<SequenceMatcher>();
        State<SequenceMatcher> state = trie.getInitialState();
        long currentPosition = matchPosition;
        Window window = reader.getWindow(matchPosition);
        while (window != null) {
            final int windowLength = window.length();
            final byte[] array = window.getArray();
            int windowPosition = reader.getWindowOffset(currentPosition);
            while (windowPosition < windowLength) {
                final byte currentByte = array[windowPosition++];
                state = state.getNextState(currentByte);
                if (state == null) {
                    return result;
                }
                if (state.isFinal()) {
                    result.addAll(state.getAssociations());
                }
            }
            currentPosition += windowLength;
            window = reader.getWindow(matchPosition);
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
            State<SequenceMatcher> state  = trie.getInitialState();
            int currentPosition = matchPosition;
            while (state != null && currentPosition < noOfBytes) {
                final byte currentByte = bytes[currentPosition++];
                state = state.getNextState(currentByte);
                if (state != null && state.isFinal()) {
                    result.addAll(state.getAssociations());
                }
            }
        }
        return result;
    }    
    
    
    /**
     * @throws IOException 
     * @inheritDoc
     * 
     */
    @Override  
    public Collection<SequenceMatcher> allMatchesBackwards(final Reader reader, 
            final long matchPosition) throws IOException {
        final List<SequenceMatcher> result = new ArrayList<SequenceMatcher>();
        State<SequenceMatcher> state = trie.getInitialState();
        long currentPosition = matchPosition;
        Window window = reader.getWindow(matchPosition);
        while (window != null) {
            final int windowLength = window.length();
            final byte[] array = window.getArray();
            int windowPosition = reader.getWindowOffset(currentPosition);
            while (windowPosition >= 0) {
                final byte currentByte = array[windowPosition--];
                state = state.getNextState(currentByte);
                if (state == null) {
                    return result;
                }
                if (state.isFinal()) {
                    result.addAll(state.getAssociations());
                }
            }
            currentPosition -= windowLength;
            window = reader.getWindow(matchPosition);
        }
        return result;        
    }

    
    /**
     * @param bytes 
     * @inheritDoc
     * 
     */
    @Override  
    public Collection<SequenceMatcher> allMatchesBackwards(final byte[] bytes, final int matchPosition) {
        final List<SequenceMatcher> result = new ArrayList<SequenceMatcher>();        
        final int noOfBytes = bytes.length;
        final int minimumLength = trie.getMinimumLength();
        if (matchPosition >= minimumLength - 1 && matchPosition + minimumLength < noOfBytes) {
            State<SequenceMatcher> state  = trie.getInitialState();
            int currentPosition = matchPosition;
            while (state != null && currentPosition >= 0) {
                final byte currentByte = bytes[currentPosition--];
                state = state.getNextState(currentByte);
                if (state != null && state.isFinal()) {
                    result.addAll(state.getAssociations());
                }
            }
        }
        return result;        
    }
    
    
    
    /**
     * @throws IOException 
     * @inheritDoc
     */
    @Override
    public SequenceMatcher firstMatch(final Reader reader, final long matchPosition) 
            throws IOException {
        State<SequenceMatcher> state = trie.getInitialState();
        long currentPosition = matchPosition;
        Window window = reader.getWindow(matchPosition);
        while (window != null) {
            final int windowLength = window.length();
            final byte[] array = window.getArray();
            int windowPosition = reader.getWindowOffset(currentPosition);
            while (windowPosition < windowLength) {
                final byte currentByte = array[windowPosition++];
                state = state.getNextState(currentByte);
                if (state == null) {
                    return null;
                }
                if (state.isFinal()) {
                    return getFirstAssociation(state);
                }
            }
            currentPosition += windowLength;
            window = reader.getWindow(matchPosition);
        }
        return null;
    }
    
    
    /**
     * @inheritDoc
     * 
     */
    @Override    
    public SequenceMatcher firstMatch(final byte[] bytes, final int matchPosition) {
        if (matchPosition >= 0) {
            final int noOfBytes = bytes.length;
            State state = trie.getInitialState();
            int currentPosition = matchPosition;
            while (state != null && currentPosition < noOfBytes) {
                final byte currentByte = bytes[currentPosition++];
                state = state.getNextState(currentByte);
                if (state != null && state.isFinal()) {
                    return getFirstAssociation(state);
                }            
            }
        }
        return null;
    }    
    
    
    /**
     * @throws IOException 
     * @inheritDoc
     * 
     */
    @Override  
    public SequenceMatcher firstMatchBackwards(final Reader reader, final long matchPosition)
            throws IOException {
        State state = trie.getInitialState();
        long currentPosition = matchPosition;
        Window window = reader.getWindow(matchPosition);
        while (window != null) {
            final int windowLength = window.length();
            final byte[] array = window.getArray();
            int windowPosition = reader.getWindowOffset(currentPosition);
            while (windowPosition >= 0) {
                final byte currentByte = array[windowPosition--];
                state = state.getNextState(currentByte);
                if (state == null) {
                    return null;
                }
                if (state.isFinal()) {
                    return getFirstAssociation(state);
                }
            }
            currentPosition -= windowLength;
            window = reader.getWindow(matchPosition);
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
        if (matchPosition < noOfBytes) {
            State state = trie.getInitialState();
            int currentPosition = matchPosition;
            while (state != null && currentPosition >= 0) {
                final byte currentByte = bytes[currentPosition--];
                state = state.getNextState(currentByte);
                if (state != null && state.isFinal()) {
                    return getFirstAssociation(state);
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
     * @throws IOException 
     * @inheritDoc
     * 
     */
    @Override 
    public boolean matchesBackwards(final Reader reader, final long matchPosition) 
            throws IOException {
        return firstMatchBackwards(reader, matchPosition) != null;
    }

    
    /**
     * @param bytes 
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
    
    
    private SequenceMatcher getFirstAssociation(final State<SequenceMatcher> state) {
        final Collection<SequenceMatcher> associations = state.getAssociations();
        if (associations != null) {
            final Iterator<SequenceMatcher> sequence = associations.iterator();
            if (sequence.hasNext()) {
                return sequence.next();
            }
        }
        return null;
    }
   

}
