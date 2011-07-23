/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author matt
 */
public class FileOffsetArrayProvider implements OffsetArrayProvider {

    private final static String READ_ONLY = "r";
    private final static String NULL_ARGUMENTS = "Null file passed to FileByteArrayProvider";

    private int cacheBlockSize = 8192;
    
    private final RandomAccessFile file;
    private final long length;


    /**
     * Constructs an immutable FileReader.
     *
     * @param file The file to read from.
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileOffsetArrayProvider(final File file) throws FileNotFoundException {
        if (file == null) {
            throw new IllegalArgumentException(NULL_ARGUMENTS);
        }
        this.file = new RandomAccessFile(file, READ_ONLY);
        this.length = file.length();
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
     * @return A OffsetArray containing a byte array and the offset into it for a given position.
     */
    @Override
    public OffsetArray getByteArray(final long position) throws ByteReaderException {
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
                    return new OffsetArray(cacheBlock, offset);
                }
            } catch (IOException ex) {
                throw new ByteReaderException(ex);
            }
        }
        return OffsetArray.EMPTY;
    }
    
    
    
}
