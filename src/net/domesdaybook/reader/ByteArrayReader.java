/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

/**
 * An immutable {@link ByteReader} which reads bytes from an
 * underlying byte array.
 *
 * @author matt
 */
public final class ByteArrayReader implements ByteReader {

    private final byte[] bytes;


    /**
     * Constructs an immutable ByteArrayReader.
     * 
     * @param bytes The byte array to read from.
     */
    public ByteArrayReader(final byte[] bytes) {
        this.bytes = bytes;
    }
    
    
    /**
     * {@inheritDoc}
     * 
     * Note: the position is cast from a {@code long} to an {@code int},
     * as arrays can only be indexed by integers.
     */
    public byte readByte(long position) {
        return bytes[(int) position];
    }


    /**
     * @return long The length of the byte array.
     */
    public long length() {
        return bytes.length;
    }

}
