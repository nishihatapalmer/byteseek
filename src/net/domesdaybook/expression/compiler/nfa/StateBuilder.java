/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.expression.compiler.nfa;

import net.domesdaybook.automata.nfa.NfaState;

/**
 * A builder/factory class used to create NfaStates where required.
 *
 * This allows us to plug in different StateBuilders if we need different
 * types of NfaState in different circumstances.
 *
 * @author Matt Palmer
 */
public interface StateBuilder {

    /**
     * Builds an {@link NfaState} object.
     *
     * @param isFinal Whether the state is final or not.
     * @return An object implementing the NfaState interface.
     */
    public NfaState build(final boolean isFinal);

}
