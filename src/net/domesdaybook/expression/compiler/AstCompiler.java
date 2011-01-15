/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.expression.compiler;

import net.domesdaybook.expression.parser.AstParser;
import net.domesdaybook.expression.parser.ParseException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

/**
 *
 * @author Matt Palmer
 */
public abstract class AstCompiler<T> implements Compiler<T> {

    protected static final String MANY = "*";

    @Override
    public T compile(String expression) throws ParseException {
        try {
            AstParser parser = new AstParser();
            Tree tree = parser.parseToAST(expression);
            CommonTree optimisedAST = (CommonTree) parser.optimiseAST(tree);
            return compile(optimisedAST);
        } catch (IllegalArgumentException e) {
            throw new ParseException(e);
        }
    }

    
    public abstract T compile(final CommonTree ast) throws ParseException;

}
