/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.reader;

/**
 *
 * @author matt
 */
public interface Bytes {

    /**
     * Get a byte from a position
     *
     * @param fileIndex position of required byte 
     * @return the byte at position <code>fileIndex</code>
     */
    public byte getByte(final long fileIndex);

}
