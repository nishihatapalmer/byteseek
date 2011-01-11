/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.automata.nfa;

import java.util.Map;
import java.util.Set;
import net.domesdaybook.copy.DeepCopy;
import net.domesdaybook.automata.State;

/**
 *
 * @author Matt Palmer
 */
public interface NfaState extends State {

    public Set<NfaState> nextStates(final byte theByte);

    public void setIsFinal(final boolean isFinal);

    public NfaState deepCopy(final Map<DeepCopy, DeepCopy> oldToNewObjects);
}
