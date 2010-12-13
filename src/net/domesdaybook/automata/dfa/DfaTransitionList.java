/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.automata.dfa;

import net.domesdaybook.automata.State;
import net.domesdaybook.automata.TransitionList;

/**
 *
 * @author matt
 */
public class DfaTransitionList extends TransitionList implements DfaTransitionCollection {

    @Override
    public State getStateForByte(byte b) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
