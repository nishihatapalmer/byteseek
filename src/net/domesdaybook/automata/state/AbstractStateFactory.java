/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.automata.state;

import net.domesdaybook.automata.State;

/**
 *
 * @author matt
 */
public interface AbstractStateFactory {
    
    /**
     * Builds an {@link NfaState} object.
     *
     * @param isFinal Whether the state is final or not.
     * @return An object implementing the NfaState interface.
     */
    public State create(final boolean isFinal);    
    
}
