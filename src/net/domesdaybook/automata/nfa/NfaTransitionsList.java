/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.automata.nfa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.domesdaybook.automata.Transition;

/**
 *
 * @author matt
 */
public class NfaTransitionsList implements NfaTransitionsCollection {

    private final List<Transition> transitions;


    public NfaTransitionsList() {
        this.transitions = new ArrayList<Transition>();
    }


    public NfaTransitionsList(final Collection<Transition> transitions) {
        this.transitions = new ArrayList<Transition>(transitions);
    }


    public void addTransition(final Transition transition) {
        transitions.add(transition);
    }


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


    public final int size() {
        return transitions.size();
    }


    public Collection<Transition> getTransitions() {
        return transitions;
    }

}
