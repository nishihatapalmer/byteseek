/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.expression.compiler;

import net.domesdaybook.automata.nfa.NfaSimpleState;
import net.domesdaybook.automata.nfa.NfaState;

/**
 *
 * @author matt
 */
public class NfaSimpleStateBuilder implements NfaStateBuilder {

    @Override
    public NfaState build(final boolean isFinal) {
        return new NfaSimpleState(isFinal);
    }

}
