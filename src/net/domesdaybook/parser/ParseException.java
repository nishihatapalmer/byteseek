/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.parser;

/**
 * A checked exception class representing the failure to parse an expression.
 * 
 * @author Matt Palmer
 */
public class ParseException extends Exception {


    private static final long serialVersionUID = -4081239885659052145L;

    
    /**
     *
     * @param message the error message
     */
    public ParseException(final String message) {
        super(message);
    }


    /**
     * @param cause the cause of the parse exception
     */
    public ParseException(final Throwable cause) {
        super(cause);
    }


    /**
     *
     * @param message The error message
     * @param cause The cause of the parse exception.
     */
    public ParseException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

