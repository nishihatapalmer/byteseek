/*
 * Copyright Matt Palmer 2011, All rights reserved.
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
import net.domesdaybook.automata.factory.StateFactory;
import net.domesdaybook.automata.factory.TransitionFactory;


/**
 *
 * @author Matt Palmer
 */
public class ByteArrayTrie extends AbstractTrie<byte[]> {

    
    public ByteArrayTrie() {
        this(null, null, null);
    }
    
    
    public ByteArrayTrie(final Collection<byte[]> sequences) {
        this(sequences, null, null);
    }        
    
    
    public ByteArrayTrie(final StateFactory<byte[]> stateFactory) {
        this(null, stateFactory, null);
    }
    
    
    public ByteArrayTrie(final Collection<byte[]> sequences,
                         final StateFactory<byte[]> stateFactory) {
        this(sequences, stateFactory, null);
    }    
    
    
    public ByteArrayTrie(final TransitionFactory transitionFactory) {
        this(null, null, transitionFactory);
    }
    
    
    public ByteArrayTrie(final Collection<byte[]> sequences,
                         final TransitionFactory transitionFactory) {
        this(sequences, null, transitionFactory);
    }    
    
    
    public ByteArrayTrie(final StateFactory<byte[]> stateFactory, 
                         final TransitionFactory transitionFactory) {
        this(null, stateFactory, transitionFactory);
    }
    
    
    public ByteArrayTrie(final Collection<byte[]> sequences, 
                         final StateFactory<byte[]> stateFactory, 
                         final TransitionFactory transitionFactory) {
        super(stateFactory, transitionFactory);
        if (sequences != null) {
            addAll(sequences);
        }
    }
    
    
    @Override
    protected int getSequenceLength(final byte[] sequence) {
        return sequence.length;
    }

    
    @Override
    protected byte[] getBytesForPosition(final byte[] sequence, int position) {
        return new byte[] {sequence[position]};
    }
    
}
