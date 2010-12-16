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
    private final int stateId;


    public NfaStateImpl(final int stateId, final boolean isFinal) {
        this.stateId = stateId;
        this.isFinal = isFinal;
        this.transitions = new NfaTransitionsList();
    }


    public final void addTransition(final Transition transition) {
        if (transitions == null) {
            transitions = new NfaTransitionsSingle(transition);
        } else if (transitions.size() == 1) {
            transitions = new NfaTransitionsList(transitions.getTransitions());
        } else {
            transitions.addTransition(transition);
        }
    }

    
    public final Set<NfaState> nextStates(final byte theByte) {
        return transitions.getStatesForByte(theByte);
    }


    public final boolean isFinal() {
        return isFinal;
    }

    
    public final int getId() {
        return stateId;
    }

}
