/*
 * Copyright Matt Palmer 2011-2012, All rights reserved.
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

import java.util.Collection;
import net.domesdaybook.automata.StateFactory;
import net.domesdaybook.automata.TransitionFactory;


/**
 * An implementation of {@link Trie} formed of byte arrays.
 * It extends the {@link AbstractTrie} class.
 * 
 * @author Matt Palmer
 * @see <a href="http://en.wikipedia.org/wiki/Trie">Trie structures</a>
 */
public class ByteArrayTrie extends AbstractTrie<byte[]> {

    
    /**
     * Constructs a SequenceMatcherTrie with no sequences added to it initially.
     * using the default {@link net.domesdaybook.automata.StateFactory}
     * , {@link net.domesdaybook.automata.base.BaseStateFactory}, and the default
     * {@link net.domesdaybook.automata.TransitionFactory}, {@link net.domesdaybook.automata.base.ByteMatcherTransitionFactory}.
     */
    public ByteArrayTrie() {
        this(null, null, null);
    }
    
    
    /**
     * Constructs a Trie using the default {@link net.domesdaybook.automata.StateFactory}
     * , {@link net.domesdaybook.automata.base.BaseStateFactory}, and the default
     * {@link net.domesdaybook.automata.TransitionFactory}, {@link net.domesdaybook.automata.base.ByteMatcherTransitionFactory}.
     * 
     * @param sequences A collection of byte arrays to add to the Trie.
     */
    public ByteArrayTrie(final Collection<byte[]> sequences) {
        this(sequences, null, null);
    }        
    
    
     /**
     * Constructs a Trie using the supplied {@link net.domesdaybook.automata.StateFactory}
     * and the default {@link net.domesdaybook.automata.TransitionFactory}, {@link net.domesdaybook.automata.base.ByteMatcherTransitionFactory}.
     * 
     * @param stateFactory The StateFactory to use to create States for the Trie.
     */
    public ByteArrayTrie(final StateFactory<byte[]> stateFactory) {
        this(null, stateFactory, null);
    }
    
    
    /**
     * Constructs a Trie using the default {@link net.domesdaybook.automata.StateFactory}
     * , {@link net.domesdaybook.automata.base.BaseStateFactory}, and the supplied
     * {@link net.domesdaybook.automata.TransitionFactory}.
     * 
     * @param transitionFactory The TransitionFactory to use to create Transitions for the Trie.
     */
    public ByteArrayTrie(final TransitionFactory<byte[]> transitionFactory) {
        this(null, null, transitionFactory);
    }
    
    
    /**
     * Constructs a Trie using the supplied {@link net.domesdaybook.automata.StateFactory}
     * and the default {@link net.domesdaybook.automata.TransitionFactory}, {@link net.domesdaybook.automata.base.ByteMatcherTransitionFactory}.
     * 
     * @param sequences The initial collection of byte arrays to add to the Trie.
     * @param stateFactory The StateFactory to use to create States for the Trie.
     */
    public ByteArrayTrie(final Collection<byte[]> sequences,
                         final StateFactory<byte[]> stateFactory) {
        this(sequences, stateFactory, null);
    }    
    

    /**
     * Constructs a Trie using the default {@link net.domesdaybook.automata.StateFactory}
     * , {@link net.domesdaybook.automata.base.BaseStateFactory}, and the supplied
     * {@link net.domesdaybook.automata.TransitionFactory}.
     * 
     * @param sequences The initial collection of byte arrays to add to the Trie.
     * @param transitionFactory The TransitionFactory to use to create Transitions for the Trie.
     */
    public ByteArrayTrie(final Collection<byte[]> sequences,
                         final TransitionFactory<byte[]> transitionFactory) {
        this(sequences, null, transitionFactory);
    }    
    
    
    /**
     * Constructs a Trie using the supplied {@link net.domesdaybook.automata.StateFactory}
     * and {@link net.domesdaybook.automata.TransitionFactory}.
     * 
     * @param stateFactory The StateFactory to use to create States for the Trie.
     * @param transitionFactory The TransitionFactory to use to create Transitions for the Trie.
     */
    public ByteArrayTrie(final StateFactory<byte[]> stateFactory, 
                         final TransitionFactory<byte[]> transitionFactory) {
        this(null, stateFactory, transitionFactory);
    }
    
    
    /**
     * Constructs a Trie using the supplied {@link net.domesdaybook.automata.StateFactory}
     * and {@link net.domesdaybook.automata.TransitionFactory}.
     * 
     * @param sequences The initial collection of byte arrays to add to the Trie.
     * @param stateFactory The StateFactory to use to create States for the Trie.
     * @param transitionFactory The TransitionFactory to use to create Transitions for the Trie.
     */
    public ByteArrayTrie(final Collection<byte[]> sequences, 
                         final StateFactory<byte[]> stateFactory, 
                         final TransitionFactory<byte[]> transitionFactory) {
        super(stateFactory, transitionFactory);
        if (sequences != null) {
            addAll(sequences);
        }
    }
    
    
    /**
     * Returns the length of a byte array.
     * 
     * @param sequence The byte array to return the length of.
     * @return int the length of the SequenceMatcher.
     */
    @Override
    protected int getSequenceLength(final byte[] sequence) {
        return sequence.length;
    }

    
    /**
     * Returns an array of bytes containing the single byte value that exists
     * for a given position in the byte array.
     * 
     * @param sequence The byte array to get the byte value for.
     * @param position The position in the byte array to get the byte value.
     * @return A byte array containing the byte which exists in the byte array at the given position.
     */
    @Override
    protected byte[] getBytesForPosition(final byte[] sequence, int position) {
        return new byte[] {sequence[position]};
    }
    
}
