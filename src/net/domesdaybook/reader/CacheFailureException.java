/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

/**
 * 
 * @author Matt Palmer.
 */
public class CacheFailureException extends RuntimeException {

    public CacheFailureException(String message) {
        super(message);
    }

    public CacheFailureException(Throwable cause) {
        super(cause);
    }

    public CacheFailureException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
