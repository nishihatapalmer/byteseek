/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
