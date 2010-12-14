/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.automata.dfa;

import net.domesdaybook.automata.State;
import net.domesdaybook.automata.TransitionsCollection;

/**
 *
 * @author matt
 */
public interface DfaTransitionsCollection extends TransitionsCollection {

    public State getStateForByte(final byte b);

}
