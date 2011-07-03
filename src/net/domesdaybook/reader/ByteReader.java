/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 * 
 */


package net.domesdaybook.reader;

/**
 * An interface for classes which can read bytes at a given position.
 * <p/>
 * Design issues: 
 * <p/>
 * 1. Should ByteReaderException be a checked or unchecked exception?  
 *    It is currently a RuntimeException, but it can encapsulate 
 *    sources which may throw a checked IOException (e.g. RandomAccessFile).
 *    It can also encapsulate sources which only throw RuntimExceptions,
 *     e.g. byte arrays.  
 *    Require behaviour to be consistent across all implementations for client code,
 * 
 * 
 * @author Matt Palmer
 */
public interface ByteReader {

    /**
     * Read a byte from a given position.
     *
     * @param position The position of the byte to read.
     * @return byte The byte at the position given.
     */
    public byte readByte(final long position) throws ByteReaderException;

    
    /**
     * @return long the length of the byte source accessed by the reader.
     */
    public long length();
    
}
