/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.automata.nfa;

import java.util.HashSet;
import java.util.Set;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.automata.TransitionList;

/**
 *
 * @author matt
 */
public class NfaTransitionList extends TransitionList implements NfaTransitionCollection {
    
    @Override
    public final Set<State> getStatesForByte(final byte theByte) {
        final Set<State> states = new HashSet<State>();
        for (Transition transition : transitions) {
            final State stateForByte = transition.getStateForByte(theByte);
            if (stateForByte != null) {
                states.add(stateForByte);
            }
        }
        return states;
    }

}
