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
import net.domesdaybook.matcher.sequence.SequenceMatcher;

/**
 * 
 * @author Matt Palmer
 * @see <a href="http://en.wikipedia.org/wiki/Trie">SequenceMatcherTrie structures</a>
 */
public final class SequenceMatcherTrie extends AbstractTrie<SequenceMatcher> {


    public SequenceMatcherTrie() {
        this(null, null, null);
    }
    
    
    public SequenceMatcherTrie(final Collection<? extends SequenceMatcher> sequences) {
        this(sequences, null, null);
    }        
    
    
    public SequenceMatcherTrie(final StateFactory<SequenceMatcher> stateFactory) {
        this(null, stateFactory, null);
    }
    
    
    public SequenceMatcherTrie(final Collection<? extends SequenceMatcher> sequences,
                               final StateFactory<SequenceMatcher> stateFactory) {
        this(sequences, stateFactory, null);
    }    
    
    
    public SequenceMatcherTrie(final TransitionFactory transitionFactory) {
        this(null, null, transitionFactory);
    }

    
    public SequenceMatcherTrie(final Collection<? extends SequenceMatcher> sequences,
                              final TransitionFactory transitionFactory) {
        this(sequences, null, transitionFactory);
    }
        
    
    public SequenceMatcherTrie(final StateFactory<SequenceMatcher> stateFactory, 
                               final TransitionFactory transitionFactory) {
        this(null, stateFactory, transitionFactory);
    }
    
    
    public SequenceMatcherTrie(final Collection<? extends SequenceMatcher> sequences, 
                               final StateFactory<SequenceMatcher> stateFactory, 
                               final TransitionFactory transitionFactory) {
        super(stateFactory, transitionFactory);
        if (sequences != null) {
            addAll(sequences);
        }
    }    
    
    
    @Override
    protected int getSequenceLength(final SequenceMatcher sequence) {
        return sequence.length();
    }

    @Override
    protected byte[] getBytesForPosition(final SequenceMatcher sequence, final int position) {
        return sequence.getMatcherForPosition(position).getMatchingBytes();
    }
    
}
