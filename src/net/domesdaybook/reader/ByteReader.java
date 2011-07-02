/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

/**
 * An interface for classes which can read bytes at a given position.
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
    public long length() throws ByteReaderException;
    
}
