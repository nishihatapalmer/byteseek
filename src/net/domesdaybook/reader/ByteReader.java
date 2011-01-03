/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

/**
 * An interface for classes which can read bytes at a given position.
 * 
 * @author matt
 */
public interface ByteReader {

    /**
     * Get a byte from a position
     *
     * @param position the position of the byte to read.
     * @return the byte at position <code>fileIndex</code>
     */
    public byte getByte(final long position);

}
