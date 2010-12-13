/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.automata.nfa;

import java.util.Set;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.TransitionCollection;

/**
 *
 * @author matt
 */
public interface NfaTransitionCollection extends TransitionCollection {

    public Set<State> getStatesForByte(final byte theByte);

}
