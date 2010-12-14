/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.automata.dfa;

import net.domesdaybook.automata.State;
import net.domesdaybook.automata.TransitionsSingle;

/**
 *
 * @author matt
 */
public class DfaTransitionsSingle extends TransitionsSingle implements DfaTransitionsCollection {

    public final State getStateForByte(byte b) {
        return transition.getStateForByte(b);
    }

}
