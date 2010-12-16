/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.automata.nfa;

import java.util.Set;
import net.domesdaybook.automata.Transition;

/**
 *
 * @author matt
 */
public class NfaStateImpl implements NfaState {

    private NfaTransitionsCollection transitions;
    private final boolean isFinal;
    private final String stateId;


    public NfaStateImpl(final String stateId, final boolean isFinal) {
        this.stateId = stateId;
        this.isFinal = isFinal;
        this.transitions = new NfaTransitionsList();
    }


    @Override
    public final void addTransition(final Transition transition) {
        if (transitions == null) {
            transitions = new NfaTransitionsSingle(transition);
        } else if (transitions.size() == 1) {
            transitions = new NfaTransitionsList(transitions.getTransitions());
        } else {
            transitions.addTransition(transition);
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
    public final String getId() {
        return stateId;
    }

}
