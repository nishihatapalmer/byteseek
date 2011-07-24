/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
    
    public Array(final byte[] bytes, final int offset, final int limit) {
        this.bytes = bytes;
        this.offset = offset;
        this.limit = limit;
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
