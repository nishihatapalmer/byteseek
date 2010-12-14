/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.automata.dfa;

import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.automata.TransitionsList;

/**
 *
 * @author matt
 */
public class DfaTransitionsList extends TransitionsList implements DfaTransitionsCollection {

    @Override
    public State getStateForByte(byte b) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /*
    @Override
    public void addTransition(final Transition transition) {
        // a dfa can only transition to a single state on a given byte:

    }
    
    private boolean hasTransitionToState(final State state) {
        final int stateId = state.getId();
        for (Transition transition : transitions) {
            if (transition.getToState().getId() == stateId) {
                return true;
            }
        }
        return false;
    }
     * 
     */

}
