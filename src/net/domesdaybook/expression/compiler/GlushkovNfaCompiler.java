/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.expression.compiler;

import net.domesdaybook.automata.nfa.NfaMatcher;
import net.domesdaybook.automata.nfa.NfaState;
import net.domesdaybook.expression.parser.regularExpressionParser;
import org.antlr.runtime.tree.CommonTree;

/**
 *
 * @author matt
 */
public class GlushkovNfaCompiler {

    public NfaMatcher buildFromAbstractSyntaxTree(final CommonTree ast) {

       NfaState firstState = buildRecursiveGlushkov(ast);

       return null;
    }

    private NfaState buildRecursiveGlushkov(final CommonTree ast) {
         
        switch (ast.getToken().getType()) {
            case (regularExpressionParser.REPEAT): {
                
            }
        }
        return null;
    }

}
