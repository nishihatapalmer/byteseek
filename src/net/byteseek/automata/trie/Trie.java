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

package net.byteseek.automata.trie;

import java.util.Collection;

import net.byteseek.automata.Automata;

/**
 * An interface for Trie automata, extending the {@link net.byteseek.automata.Automata} interface.
 * 
 * @param <T> The type of sequence from which the Trie is built and which are associated to the final states of the Trie.
 * @author Matt Palmer
 */
public interface Trie<T> extends Automata<T> {
    
    
    /**
     * Adds a sequence of type T to the Trie object.
     * 
     * @param sequence The sequence to add to the Trie.
     */
    public void add(T sequence);
    
    
    /**
     * Adds all the sequences of type T in the collection to the Trie object.
     * 
     * @param sequences The sequences to add to the Trie.
     */
    public void addAll(Collection<? extends T> sequences);    
    
    
    /**
     * Adds the reversed sequence of type T to the Trie.
     * 
     * @param sequence The sequence to reverse and add to the Trie.
     */
    public void addReversed(T sequence);    
    
    
    /**
     * Adds all the reversed sequences of type T in the collection to the Trie object.
     * @param sequences The sequences to reverse and add to the Trie.
     */
    public void addAllReversed(Collection<? extends T> sequences);
    
    
    /**
     * Gets the minimum length of the sequences in the Trie.
     * 
     * @return int The minimum length of the sequences in the Trie.
     */
    public int getMinimumLength();
    
    
    /**
     * Gets the maximum length of the sequences in the Trie.
     * 
     * @return int The maximum length of the sequences in the Trie.
     */
    public int getMaximumLength();
    
    
    /**
     * Returns a collection of all the sequences in the Trie.
     * 
     * @return Collection<T> A collection of all the sequences in the Trie.
     */
    public Collection<T> getSequences();
    
}
