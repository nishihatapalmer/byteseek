/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.automata.nfa;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.domesdaybook.automata.Transition;

/**
 *
 * @author matt
 */
public interface NfaTransitionsCollection {

    public final static Set<NfaState> NO_STATES = new HashSet<NfaState>();

    public void addTransition(final Transition transition);

    public Set<NfaState> getStatesForByte(final byte theByte);

    public Collection<Transition> getTransitions();

    public int size();
}
