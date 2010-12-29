/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.automata.nfa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.domesdaybook.automata.Transition;

/**
 *
 * @author matt
 */
public class NfaSimpleState implements NfaState, Cloneable {

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
        } else if (transitions.size() == 1) {
            transitions = new NfaTransitionsList(transitions.getTransitions());
        } else {
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

    /**
     *
     * @return a deep clone copy of the automata:
     */
    @Override
    public Object clone() {
        Map<NfaSimpleState,NfaSimpleState> oldToNewStates = new HashMap<NfaSimpleState,NfaSimpleState>();
        return cloneDeep(oldToNewStates);
    }
    
    private NfaSimpleState cloneDeep(Map<NfaSimpleState,NfaSimpleState> oldToNewStates) {
        NfaSimpleState clone = null;
        
        // If we've already cloned this state, return it's clone:
        if (oldToNewStates.containsKey(this)) {
            clone = oldToNewStates.get(this);
        } else { // clone this state and
            try {
                clone = (NfaSimpleState) super.clone();
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(NfaSimpleState.class.getName()).log(Level.SEVERE, null, ex);
            }
            oldToNewStates.put(this, clone);

            // Now make deep copies of all the states we have transitions to
            // and update the transitions.
            final List<Transition> cloneTransitions = clone.getTransitions();
            for (Transition transition : cloneTransitions) {
                final NfaSimpleState oldState = (NfaSimpleState) transition.getToState();
                final NfaSimpleState clonedTransitionState = oldState.cloneDeep(oldToNewStates);
                Transition clonedTransition = transition.clone();
                transition.setToState(clonedTransitionState);

            }
        }

        return clone;
    }

}
