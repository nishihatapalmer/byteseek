/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.automata.nfa;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.domesdaybook.objects.DeepCopy;
import net.domesdaybook.automata.Transition;

/**
 *
 * @author Matt Palmer
 */
public interface NfaTransitions extends DeepCopy {

    public final static Set<NfaState> NO_STATES = new HashSet<NfaState>();

    public void addTransition(final Transition transition);

    public Set<NfaState> getStatesForByte(final byte theByte);

    public List<Transition> getTransitions();

    public int size();

    public NfaTransitions deepCopy(final Map<DeepCopy, DeepCopy> oldToNewObjects);
}
