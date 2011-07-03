/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.automata.wrapper;

import net.domesdaybook.automata.State;

/**
 *
 * @author matt
 */
public final class Trie {
    
    private final State initialState;
    private final int maximumLength;
    private final int minimumLength;
    
    
    public Trie(final State firstState, final int minLength, final int maxLength) {
        this.initialState = firstState;
        this.minimumLength = minLength;
        this.maximumLength = maxLength;
    }
    
    
    public State getInitialState() {
        return initialState;
    }
    
    
    public int getMinimumLength() {
        return minimumLength;
    }
    
    
    public int getMaximumLength() {
        return maximumLength;
    }
    
}
