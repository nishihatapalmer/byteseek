/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.domesdaybook.reader;

/**
 *
 * @author matt
 */
public final class ByteArray {
    
    private final byte[] bytes;
    private final int offset;
    
    public ByteArray(final byte[] bytes, final int offset) {
        this.bytes = bytes;
        this.offset = offset;
    }
    
    public byte[] getArray() {
        return bytes;
    }
    
    public int getOffset() {
        return offset;
    }
}
