/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

/**
 * A Runtime exception that ByteReaders can throw to indicate
 * a problem reading bytes, where a checked exception would be
 * thrown by the underlying implementation instead.
 *
 * @author Matt Palmer.
 */
public class ReadByteException extends RuntimeException {

    public ReadByteException(String message) {
        super(message);
    }

    public ReadByteException(Throwable cause) {
        super(cause);
    }

    public ReadByteException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
