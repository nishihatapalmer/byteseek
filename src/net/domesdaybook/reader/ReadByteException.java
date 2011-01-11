/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

    public ReadByteException(Exception ex) {
        super(ex);
    }

    public ReadByteException(String message) {
        super(message);
    }
    
}
