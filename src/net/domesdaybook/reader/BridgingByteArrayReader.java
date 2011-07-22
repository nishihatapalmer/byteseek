/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.domesdaybook.reader;

/**
 *
 * @author matt
 */
public class BridgingByteArrayReader implements ByteReader {

    private final byte[] firstArray;
    private final byte[] secondArray;
    private final int firstArrayLength;
    private final int totalLength;
    
    public BridgingByteArrayReader(final byte[] firstArray, final byte[] secondArray) {
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
    public byte readByte(final long position) throws ByteReaderException {
        final int crossOver = firstArrayLength;
        try {
            return position < crossOver ?
                 firstArray[(int) position]
                : secondArray[(int) (position - crossOver)];
        } catch (IndexOutOfBoundsException ex) {
            throw new ByteReaderException(ex);
        }
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public long length() {
        return totalLength;
    }
    
}
