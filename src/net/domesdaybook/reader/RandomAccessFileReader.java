/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * A very simple {@link ByteReader} which accesses bytes from a file,
 * using an underlying RandomAccessFile.
 *<p/>
 * Note: performance reading individual bytes from a RandomAccessFile
 * will be very slow, so this class is provided only for convenience.
 * <p/>
 * Also note, if an IOException occurs reading bytes from the file,
 * then a Runtime exception will be thrown in its place.
 *
 * @author Matt Palmer.
 */
public final class RandomAccessFileReader implements ByteReader, ByteArrayProvider {

    private final static String READ_ONLY = "r";
    private final static String NULL_ARGUMENTS = "Null file passed to RandomAccessFileReader";

    private int cacheBlockSize = 8192;
    
    private final RandomAccessFile file;
    private final long length;


    /**
     * Constructs an immutable RandomAccessFileReader.
     *
     * @param file The file to read from.
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public RandomAccessFileReader(final File file) throws FileNotFoundException {
        if (file == null) {
            throw new IllegalArgumentException(NULL_ARGUMENTS);
        }
        this.file = new RandomAccessFile(file, READ_ONLY);
        this.length = file.length();
    }


    /**
     * Reads a byte in the file at the given position.
     *
     * @param position The position in the file to read a byte from.
     * @return The byte at the given position.
     * @throws ByteReaderException if an IOException occurs reading the file.
     */
    @Override
    public byte readByte(long position) throws ByteReaderException {
        try {
            file.seek(position);
            return file.readByte();
        } catch (IOException ex) {
            throw new ByteReaderException(ex);
        }
    }
    
    
    /**
     * 
     * @return The length of the file accessed by the reader.
     */
    @Override
    public long length(){
        return length;
    }

    
    
    
    /**
     * 
     * @return A ByteArray containing a byte array and the offset into it for a given position.
     */
    @Override
    public ByteArray getByteArray(final long position) {
        if (position >= 0 && position < length) {
            try {
                int blockSize = cacheBlockSize;
                long readPos = position / blockSize;
                int offset = (int) (position % blockSize); 
                
                //TODO: check position calculations here...
                if (position + blockSize > length) {
                    blockSize = (int) (length - position);
                } 
                if (blockSize > 0) {
                    final byte[] cacheBlock = new byte[blockSize];
                    file.seek(readPos);
                    file.read(cacheBlock, 0, blockSize);
                    return new ByteArray(cacheBlock, offset);
                }
            } catch (IOException ex) {
                Logger.getLogger(RandomAccessFileReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ByteArray.EMPTY_ARRAY;
    }

}
