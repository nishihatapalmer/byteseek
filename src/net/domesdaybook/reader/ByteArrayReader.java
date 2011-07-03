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

    private final static String NULL_ARGUMENTS = "Null byte array passed in to ByteArrayReader.";
    
    private final byte[] bytes;


    /**
     * Constructs an immutable ByteArrayReader.
     * 
     * @param bytes The byte array to read from.
     * @throws IllegalArgumentException if the byte array passed in is null.
     */
    public ByteArrayReader(final byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException(NULL_ARGUMENTS);
        }
        this.bytes = bytes;
    }
    
    
    /**
     * {@inheritDoc}
     * 
     * Note: the position is cast from a {@code long} to an {@code int},
     * as arrays can only be indexed by integers.
     * @throws ByteReaderException if an attempt is made to read outside the array.
     */
    @Override
    public byte readByte(long position) throws ByteReaderException {
        try {
            return bytes[(int) position];
        } catch (IndexOutOfBoundsException ex) {
            throw new ByteReaderException(ex);
        }
    }


    /**
     * @return long The length of the byte array.
     */
    public long length() {
        return bytes.length;
    }

}
