/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.automata.dfa;

import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;

/**
 *
 * @author Matt Palmer
 */
public interface DfaTransitionsCollection {

    public void addTransition(final Transition transition);

    public State getStateForByte(final byte b);

}
