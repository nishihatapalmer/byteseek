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

    private final NfaTransitionsCollection transitions;
    private final boolean isFinal;
    private final int stateId;


    public NfaStateImpl(final int stateId, final boolean isFinal) {
        this.stateId = stateId;
        this.isFinal = isFinal;
        this.transitions = new NfaTransitionsList();
    }


    @Override
    public final void addTransition(final Transition transition) {
        transitions.addTransition(transition);
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
    public final int getId() {
        return stateId;
    }

}
