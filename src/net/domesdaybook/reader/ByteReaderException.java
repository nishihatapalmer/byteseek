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
public class ByteReaderException extends RuntimeException {

    public ByteReaderException(String message) {
        super(message);
    }

    public ByteReaderException(Throwable cause) {
        super(cause);
    }

    public ByteReaderException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
