/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.expression.compiler.nfa;

import net.domesdaybook.automata.nfa.NfaSimpleState;

/**
 *
 * @author matt
 */
public class SimpleStateBuilder implements StateBuilder {

    @Override
    public NfaSimpleState build(final boolean isFinal) {
        return new NfaSimpleState(isFinal);
    }

}
