/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

/**
 * An exception that ByteReaders can throw to indicate
 * a problem reading bytes, where a checked exception would be
 * thrown by the underlying implementation instead.
 *
 * @author Matt Palmer.
 */
public class ReaderException extends RuntimeException {

    public ReaderException(String message) {
        super(message);
    }

    public ReaderException(Throwable cause) {
        super(cause);
    }

    public ReaderException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
