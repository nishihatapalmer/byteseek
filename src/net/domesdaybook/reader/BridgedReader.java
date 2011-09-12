/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

/**
 *
 * @author matt
 */
public final class BridgedReader implements Reader {

    private final byte[] firstArray;
    private final byte[] secondArray;
    private final int firstArrayLength;
    private final int totalLength;
    
    
    public BridgedReader(final byte[] firstArray, final byte[] secondArray) {
        if (firstArray == null || secondArray == null) {
            throw new IllegalArgumentException("An array passed in was null.");
        }
        this.firstArray = firstArray;
        this.secondArray = secondArray;
        this.firstArrayLength = firstArray.length;
        this.totalLength = firstArrayLength + secondArray.length;
    }
    
    
    /**
     * @inheritDoc
     */
    @Override
    public byte readByte(final long position) throws ReaderException {
        final int crossOver = firstArrayLength;
        try {
            return position < crossOver ?
                 firstArray[(int) position]
                : secondArray[(int) (position - crossOver)];
        } catch (IndexOutOfBoundsException ex) {
            throw new ReaderException(ex);
        }
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public long length() {
        return totalLength;
    }

    
    public Window getWindow(final long position) {
        throw new UnsupportedOperationException("No Window available for BridgedArraysReader.");
    }

    
    public void close() {
        // no underlying resources to close.
    }

    
    public void clearCache() {
        // does nothing: no cache for BridgedReader.
    }
    
}
