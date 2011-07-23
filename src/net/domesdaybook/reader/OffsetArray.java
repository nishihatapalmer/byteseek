/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.domesdaybook.reader;

/**
 *
 * @author matt
 */
public final class OffsetArray {
    
    public final static OffsetArray EMPTY = new OffsetArray(new byte[] {}, 0);
    
    private final byte[] bytes;
    private final int offset;
    
    public OffsetArray(final byte[] bytes, final int offset) {
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
