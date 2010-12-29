/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.automata.nfa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.domesdaybook.automata.DeepCopy;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;

/**
 *
 * @author matt
 */
public class NfaTransitionsSingle implements NfaTransitions {

    private Transition transition;
    private Set<NfaState> stateSet;


    public NfaTransitionsSingle(final Transition transition) {
        setTransition(transition);
    }


    public NfaTransitionsSingle(final NfaTransitionsSingle other) {
        setTransition(other.transition);
    }


    @Override
    public void addTransition(final Transition transition) {
        setTransition(transition);
    }


    @Override
    public final Set<NfaState> getStatesForByte(final byte theByte) {
        final State state = transition.getStateForByte(theByte);
        if (state != null) {
            return stateSet;
         }
        return NO_STATES;
    }


    @Override
    public final int size() {
        return 1;
    }


    @Override
    public List<Transition> getTransitions() {
        final List<Transition> result = new ArrayList<Transition>();
        result.add(transition);
        return result;
    }


    private void setTransition(final Transition transition) {
        this.transition = transition;
        stateSet = new HashSet<NfaState>();
        stateSet.add((NfaState) transition.getToState());
    }


    @Override
    public NfaTransitionsSingle deepCopy() {
        Map<DeepCopy, DeepCopy> oldToNewObjects = new HashMap<DeepCopy, DeepCopy>();
        return deepCopy(oldToNewObjects);
    }


    @Override
    public NfaTransitionsSingle deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects) {
        NfaTransitionsSingle copy = (NfaTransitionsSingle) oldToNewObjects.get(this);
        if (copy == null) {
            copy = new NfaTransitionsSingle(this);
            oldToNewObjects.put(this, copy);
            final Transition transitionCopy = (Transition) transition.deepCopy(oldToNewObjects);
            copy.addTransition(transitionCopy);
        }
        return copy;
    }

}
