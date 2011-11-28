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
 * 
 */

package net.domesdaybook.automata.wrapper;

import net.domesdaybook.automata.State;

/**
 * A simple immutable wrapper around a {@link State} for automata which are
 * Trie structures. It also provides some simple information about the Trie structure itself
 * which is derivable when it is constructed, including:
 * <ul>
 * <li>Minimum length of a sequence in the Trie.
 * <li>Maximum length of a sequence in the Trie.
 * </ul>
 * Note: while the wrapper itself is immutable, it cannot be guaranteed that the states 
 * of the automata are also immutable.
 * 
 * @author Matt Palmer
 * @see net.domesdaybook.automata.State
 * @see <a href="http://en.wikipedia.org/wiki/Trie">Trie structures</a>
 */
public final class Trie<T> {
    
    private final State<T> initialState;
    private final int maximumLength;
    private final int minimumLength;
    
    
    /**
     * Constructor for a Trie.
     * 
     * @param firstState The initial state of the Trie structure.
     * @param minLength The minimum length of a sequence in the Trie.
     * @param maxLength The maximum length of a sequence in the Trie.
     */
    public Trie(final State<T> firstState, final int minLength, final int maxLength) {
        this.initialState = firstState;
        this.minimumLength = minLength;
        this.maximumLength = maxLength;
    }
    
    
    /**
     * Returns the first {@link State} of the Trie structure.
     * 
     * @return State The first state of the Trie.
     */
    public State getInitialState() {
        return initialState;
    }
    
    
    /**
     * Returns the minimum length of the sequences in the Trie.
     * 
     * @return int the minimum length of the sequences in the Trie.
     */
    public int getMinimumLength() {
        return minimumLength;
    }
    
    
    /**
     * Returns the maximum length of the sequences in the Trie.
     * 
     * @return int the maximum length of the sequences in the Trie.
     */
    public int getMaximumLength() {
        return maximumLength;
    }
    
}
