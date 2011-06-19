/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.compiler;

import net.domesdaybook.parser.AstParser;
import net.domesdaybook.parser.ParseException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

/**
 * An abstract base class for compilers which compile an
 * expression into an object of type T using an AstParser
 * to generate a parse tree.
 * 
 * @author Matt Palmer
 */
public abstract class AstCompiler<T> implements Compiler<T, String> {

    /**
     * Turns an expression into a parse tree using an
     * {@link AstParser}.  Then it invokes the abstract compile
     * method with the resulting parse-tree, to build and return a
     * compiled object of type T.
     *
     * Classes implementing this abstract class must implement
     * the other abstract compile method.
     *
     * @param expression The expression to compile.
     * @return A compiled object of type T.
     * @throws CompileException If the expression could not be parsed.
     */
    @Override
    public T compile(String expression) throws CompileException {
        try {
            AstParser parser = new AstParser();
            Tree tree = parser.parseToAST(expression);
            CommonTree optimisedAST = (CommonTree) parser.optimiseAST(tree);
            return compile(optimisedAST);
        } catch (ParseException ex) {
            throw new CompileException(ex);
        } catch (IllegalArgumentException e) {
            throw new CompileException(e);
        }
    }

    /**
     * An abstract compile method which takes a parse tree created
     * using the ANTLR parse generator and uses it to build the
     * compiled object of type T.
     *
     * Classes implementing this base class must implement this method
     * to perform the actual compilation.
     *
     * @param ast An abstract syntax tree using the ANTLR tree class.
     * @return A compiled object of type T.
     * @throws CompileException If the abstract syntax tree could not be parsed.
     */
    public abstract T compile(final CommonTree ast) throws CompileException;

}
