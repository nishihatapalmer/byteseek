/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.expression.compiler.nfa;

import net.domesdaybook.automata.nfa.NfaSimpleState;

/**
 * Implements the StateBuilder interface to create
 * {@link NfaSimpleState} objects.
 *
 * @author Matt Palmer
 */
public final class SimpleStateBuilder implements StateBuilder {

    /**
     * Builds an {@link NfaState} object.
     *
     * @param isFinal Whether the state is final or not.
     * @return An {@link NfaSimpleState} object, implementing the NfaState interface.
     */
    @Override
    public NfaSimpleState build(final boolean isFinal) {
        return new NfaSimpleState(isFinal);
    }

}
