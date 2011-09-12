/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

/**
 *
 * @author matt
 */
public final class Window {
    
    private final byte[] bytes;
    private final long windowPosition;
    private final int limit;
    
    /**
     * Constructs a Window using the byte array provided.
=    * 
     * @param bytes  The byte array to wrap.
     * @param windowPosition The position at which the Window starts.
     * @param offset A starting position of a slice of the array.
     * @param limit  An ending position of a slice of the array.
     */
    public Window(final byte[] bytes, final long windowPosition, final int limit) {
        if (bytes == null) {
            throw new IllegalArgumentException("Null byte array passed in to Array.");
        }
        this.bytes = bytes;  
        this.windowPosition = windowPosition;
        this.limit = limit;
    }
    
    
    
    public byte getByte(final int position) {
        return bytes[position];
    }

    
    public byte[] getArray() {
        return bytes;
    }
    
    
    public long getWindowPosition() {
        return windowPosition;
    }
    
    
    public int getLimit() {
        return limit;
    }
}
