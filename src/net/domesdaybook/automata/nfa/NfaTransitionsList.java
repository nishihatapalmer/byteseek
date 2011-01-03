/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.automata.nfa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.domesdaybook.automata.DeepCopy;
import net.domesdaybook.automata.Transition;

/**
 *
 * @author Matt Palmer
 */
public class NfaTransitionsList implements NfaTransitions {

    private final List<Transition> transitions;


    public NfaTransitionsList() {
        this.transitions = new ArrayList<Transition>();
    }


    public NfaTransitionsList(final Collection<Transition> transitions) {
        this.transitions = new ArrayList<Transition>(transitions);
    }

    
    public NfaTransitionsList(final NfaTransitionsList other) {
        this.transitions = new ArrayList<Transition>(other.transitions);
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


    public NfaTransitionsList deepCopy() {
        Map<DeepCopy, DeepCopy> oldToNewObjects = new HashMap<DeepCopy, DeepCopy>();
        return deepCopy(oldToNewObjects);
    }


    public NfaTransitionsList deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects) {
        NfaTransitionsList copy = (NfaTransitionsList) oldToNewObjects.get(this);
        if (copy == null) {
            copy = new NfaTransitionsList();
            oldToNewObjects.put(this,copy);
            for (Transition transition : transitions) {
                final Transition transitionCopy = (Transition) transition.deepCopy(oldToNewObjects);
                copy.transitions.add(transitionCopy);
            }
        }
        return copy;
    }

}
