/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.compiler;

/**
 * A checked exception class representing the failure to parse an expression.
 * 
 * @author Matt Palmer
 */
public class CompileException extends Exception {


    /**
     *
     * @param message the error message
     */
    public CompileException(final String message) {
        super(message);
    }


    /**
     * @param cause the cause of the compile exception
     */
    public CompileException(final Throwable cause) {
        super(cause);
    }


    /**
     *
     * @param message The error message
     * @param cause The cause of the compile exception.
     */
    public CompileException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

