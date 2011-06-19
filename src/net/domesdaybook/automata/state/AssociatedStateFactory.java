/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.domesdaybook.automata.state;

import net.domesdaybook.automata.AssociatedState;

/**
 *
 * @author matt
 */
public interface AssociatedStateFactory<T> {

    
    /**
     * Builds an {@link NfaState} object.
     *
     * @param isFinal Whether the state is final or not.
     * @return An object implementing the NfaState interface.
     */
    public AssociatedState<T> create(final boolean isFinal);       
    
}
