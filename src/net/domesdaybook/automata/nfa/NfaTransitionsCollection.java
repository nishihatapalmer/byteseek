/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.automata.nfa;

import java.util.HashSet;
import java.util.Set;
import net.domesdaybook.automata.TransitionsCollection;

/**
 *
 * @author matt
 */
public interface NfaTransitionsCollection extends TransitionsCollection {

    public final static Set<NfaState> NO_STATES = new HashSet<NfaState>();

    public Set<NfaState> getStatesForByte(final byte theByte);

}
