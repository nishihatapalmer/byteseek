/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.expression.compiler;

import net.domesdaybook.expression.parser.ParseException;

/**
 *
 * @author matt
 */
public interface Compiler<T> {

    public T compile(final String expression) throws ParseException;

}
