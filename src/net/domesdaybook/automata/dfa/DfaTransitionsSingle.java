/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.automata.dfa;

import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;

/**
 *
 * @author matt
 */
public class DfaTransitionsSingle implements DfaTransitionsCollection {

    private Transition transition;

    public DfaTransitionsSingle(final Transition transition) {
        this.transition = transition;
    }

    public void addTransition(final Transition transition) {
        this.transition = transition;
    }


    public final State getStateForByte(byte b) {
        return transition.getStateForByte(b);
    }

}
