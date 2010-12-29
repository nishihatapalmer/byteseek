/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
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
public class NfaTransitionsList implements NfaTransitions {

    private final List<Transition> transitions;


    public NfaTransitionsList() {
        this.transitions = new ArrayList<Transition>();
    }


    public NfaTransitionsList(final Collection<Transition> transitions) {
        this.transitions = new ArrayList<Transition>(transitions);
    }


    @Override
    public void addTransition(final Transition transition) {
        transitions.add(transition);
    }


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


    @Override
    public final int size() {
        return transitions.size();
    }


    @Override
    public List<Transition> getTransitions() {
        return transitions;
    }

}
