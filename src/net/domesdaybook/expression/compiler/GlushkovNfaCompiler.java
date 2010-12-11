/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.expression.compiler;

import net.domesdaybook.automata.nfa.Nfa;
import net.domesdaybook.automata.nfa.State;
import net.domesdaybook.expression.parser.regularExpressionParser;
import org.antlr.runtime.tree.CommonTree;

/**
 *
 * @author matt
 */
public class GlushkovNfaCompiler {

    public Nfa buildFromAbstractSyntaxTree(final CommonTree ast) {

       State firstState = buildRecursiveGlushkov(ast);

       return null;
    }

    private State buildRecursiveGlushkov(final CommonTree ast) {
         
        switch (ast.getToken().getType()) {
            case (regularExpressionParser.REPEAT): {
                
            }
        }
        return null;
    }

}
