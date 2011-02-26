/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.expression.compiler;

import net.domesdaybook.expression.parser.ParseException;

/**
 *
 * @author Matt Palmer
 */
public interface Compiler<T> {

    /**
     * Compiles an expression into the object type T.
     *
     * @param expression The expression to compile.
     * @return An compiled object of type T.
     * @throws ParseException if the expression could not be parsed.
     */
    public T compile(final String expression) throws ParseException;

}
