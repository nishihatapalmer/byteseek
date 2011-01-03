/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.automata.transition;

import java.util.Set;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.automata.nfa.NfaState;

/**
 *
 * @author Matt Palmer
 */
public interface TransitionFactory {

    public Transition createByteTransition(final byte theByte, final State toState);

    public Transition createAllBitmaskTransition(final byte bitMask, final State toState);

    public Transition createAnyBitmaskTransition(final byte bitMask, final State toState);

    public Transition createSetTransition(final Set<Byte> byteSet, final boolean negated, final State toState);

    public Transition createAnyByteTransition(final NfaState toState);

    public Transition createCaseInsensitiveByteTransition(final char Char, final NfaState toState);

}
