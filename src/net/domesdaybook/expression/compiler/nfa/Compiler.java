/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.expression.compiler.nfa;

import net.domesdaybook.automata.nfa.NfaState;
import org.antlr.runtime.tree.CommonTree;

/**
 *
 * @author matt
 */
public interface Compiler {

    public NfaState compile(final CommonTree ast);

}
