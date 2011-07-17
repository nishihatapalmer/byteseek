/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.compiler;

/**
 *
 * @author matt
 */
public interface ReversibleCompiler<T, E> extends Compiler<T, E> {
    
    public static enum Direction {
        FORWARDS,
        REVERSED
    }
    
    /**
     * Compiles an expression into an object of type T.
     *
     * @param expression The expression to compile.
     * @param reversed whether to reverse the expressions before compiling.
     * @return An compiled object of type T.
     * @throws ParseException if the expression could not be parsed.
     */
    public T compile(final E expression, final Direction direction) throws CompileException;
    
    
    
}
