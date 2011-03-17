/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.expression.compiler;

import net.domesdaybook.expression.parser.ParseException;

/**
 * An interface for compilers which compile a regular expression
 * into an object of type T.
 *
 * @author Matt Palmer
 */
public interface Compiler<T> {

    /**
     * Compiles an expression into an object of type T.
     *
     * @param expression The expression to compile.
     * @return An compiled object of type T.
     * @throws ParseException if the expression could not be parsed.
     */
    public T compile(final String expression) throws ParseException;

}
