/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.expression.compiler;

import net.domesdaybook.automata.nfa.NfaState;
import org.antlr.runtime.tree.CommonTree;

/**
 *
 * @author matt
 */
public interface NfaCompiler {

    public NfaState compile(final CommonTree ast);

}
