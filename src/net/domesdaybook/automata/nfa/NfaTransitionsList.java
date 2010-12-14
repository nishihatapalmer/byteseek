/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.automata.nfa;

import java.util.HashSet;
import java.util.Set;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.automata.TransitionsList;

/**
 *
 * @author matt
 */
public class NfaTransitionsList extends TransitionsList implements NfaTransitionsCollection {
    
    @Override
    public final Set<NfaState> getStatesForByte(final byte theByte) {
        final Set<NfaState> states = new HashSet<NfaState>();
        for (Transition transition : transitions) {
            final NfaState stateForByte = (NfaState) transition.getStateForByte(theByte);
            if (stateForByte != null) {
                states.add(stateForByte);
            }
        }
        return states;
    }

}
