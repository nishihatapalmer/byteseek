/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.automata.nfa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.domesdaybook.objects.DeepCopy;
import net.domesdaybook.automata.Transition;

/**
 *
 * @author Matt Palmer
 */
public class NfaSimpleState implements NfaState {

    private NfaTransitions transitions;
    private boolean isFinal;
    private String label;

    public NfaSimpleState(final String label, final boolean isFinal) {
        this.label = label;
        this.isFinal = isFinal;
        // guard against null pointer exception in nextStates if a
        // transition is never added - always initialised to something which
        // will give correct results (i.e. a list with no members):
        this.transitions = new NfaTransitionsList();
    }

    public NfaSimpleState(final NfaSimpleState other) {
        this.label = other.label;
        this.isFinal = other.isFinal;
        this.transitions = other.transitions; // shared transitions object?
    }

    
    public NfaSimpleState() {
        this("", false);
    }


    public NfaSimpleState(final String label) {
        this(label, false);
    }


    public NfaSimpleState(final boolean isFinal) {
        this("", isFinal);
    }


    @Override
    public final void addTransition(final Transition transition) {
        if (transitions.size() == 0) {
            transitions = new NfaTransitionsSingle(transition);
        } else {
            if (transitions.size() == 1) {
                transitions = new NfaTransitionsList(transitions.getTransitions());
            }
            transitions.addTransition(transition);
        }
    }

    
    @Override
    public final void addAllTransitions(final List<Transition> transitions) {
        for (Transition transition : transitions) {
            addTransition(transition);
        }
    }


    @Override
    public final Set<NfaState> nextStates(final byte theByte) {
        return transitions.getStatesForByte(theByte);
    }


    @Override
    public final boolean isFinal() {
        return isFinal;
    }

    
    @Override
    public void setIsFinal(final boolean isFinal) {
        this.isFinal = isFinal;
    }


    @Override
    public final String getLabel() {
        return label;
    }

    
    @Override
    public List<Transition> getTransitions() {
        return transitions.getTransitions();
    }


    @Override
    public NfaSimpleState deepCopy() {
        final Map<DeepCopy, DeepCopy> oldToNewObjects = new HashMap<DeepCopy,DeepCopy>();
        return deepCopy(oldToNewObjects);
    }


    @Override
    public NfaSimpleState deepCopy(final Map<DeepCopy, DeepCopy> oldToNewObjects) {
        NfaSimpleState copy = (NfaSimpleState) oldToNewObjects.get(this);
        if (copy == null) {
            copy = new NfaSimpleState(this);
            oldToNewObjects.put(this, copy);
            copy.transitions = copy.transitions.deepCopy(oldToNewObjects);
        }
        return copy;
    }

    @Override
    public final void setLabel(String label) {
        this.label = label;
    }

}
