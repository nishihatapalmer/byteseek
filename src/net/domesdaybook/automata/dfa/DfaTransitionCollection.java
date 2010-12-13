/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.automata.dfa;

import net.domesdaybook.automata.State;
import net.domesdaybook.automata.TransitionCollection;

/**
 *
 * @author matt
 */
public interface DfaTransitionCollection extends TransitionCollection {

    public State getStateForByte(final byte b);

}
