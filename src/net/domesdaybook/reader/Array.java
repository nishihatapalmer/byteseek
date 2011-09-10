/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

/**
 *
 * @author matt
 */
public final class Array {
    
    public final static Array EMPTY = new Array(new byte[] {}, -1, -1);
    
    private final byte[] bytes;
    private final int offset;
    private final int limit;
    
    /**
     * Constructs an Array using the byte array provided.
=    * 
     * @param bytes  The byte array to wrap.
     * @param offset A starting position of a slice of the array.
     * @param limit  An ending position of a slice of the array.
     */
    public Array(final byte[] bytes, final int offset, final int limit) {
        if (bytes == null) {
            throw new IllegalArgumentException("Null byte array passed in to Array.");
        }
        this.bytes = bytes;        
        this.offset = offset;
        this.limit = limit;
    }
    
    
    
    /**
     * Constructs an Array using the byte array of the Array passed in, and the
     * startPos and stopPos provided.
     * 
     * @param array
     * @param offset
     * @param limit 
     */
    public Array(final Array array, final int offset, final int limit) {
        this(array.bytes, offset, limit);
    }
    
    
    public byte[] getArray() {
        return bytes;
    }
    
    
    public int getOffset() {
        return offset;
    }
    
    
    public int getLimit() {
        return limit;
    }
}
