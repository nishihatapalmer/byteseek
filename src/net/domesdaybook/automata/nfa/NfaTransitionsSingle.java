/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.automata.nfa;

import java.util.HashSet;
import java.util.Set;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.TransitionsSingle;

/**
 *
 * @author matt
 */
public class NfaTransitionsSingle extends TransitionsSingle implements NfaTransitionsCollection {

    public final Set<NfaState> getStatesForByte(final byte theByte) {
        final State state = transition.getStateForByte(theByte);
        if (state != null) {
            final Set<NfaState> states = new HashSet<NfaState>();
            states.add((NfaState) state);
            return states;
         }
        return NO_STATES;
    }

}
