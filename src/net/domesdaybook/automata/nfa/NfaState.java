/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.automata.nfa;

import java.util.Set;
import net.domesdaybook.automata.State;

/**
 *
 * @author matt
 */
public interface NfaState extends State {

    public Set<State> nextStates(final byte theByte);

}
