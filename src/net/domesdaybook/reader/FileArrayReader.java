/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;

/**
 *
 * @author matt
 */
public class FileArrayReader implements ByteReader, Iterable {

    private final static String READ_ONLY = "r";
    private final static String NULL_ARGUMENTS = "Null file passed to FileByteArrayProvider";

    private final static int DEFAULT_ARRAY_SIZE = 4096;
    private final int arraySize;
    
    private final RandomAccessFile file;
    private final long length;


    /**
     * Constructs an immutable FileReader which defaults to an array size of 4096.
     * 
     * @param file The file to read from.
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileArrayReader(final File file) throws FileNotFoundException {
        if (file == null) {
            throw new IllegalArgumentException(NULL_ARGUMENTS);
        }
        this.file = new RandomAccessFile(file, READ_ONLY);
        this.length = file.length();
        this.arraySize = DEFAULT_ARRAY_SIZE;
    }
    

    /**
     * Constructs an immutable FileReader which reads the file into arrays of
     * the specified size.
     *
     * @param file The file to read from.
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileArrayReader(final File file, int arraySize) throws FileNotFoundException {
        if (file == null) {
            throw new IllegalArgumentException(NULL_ARGUMENTS);
        }
        this.file = new RandomAccessFile(file, READ_ONLY);
        this.length = file.length();
        this.arraySize = arraySize;
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
     * Reads a byte in the file at the given position.
     *
     * @param position The position in the file to read a byte from.
     * @return The byte at the given position.
     * @throws ByteReaderException if an IOException occurs reading the file.
     */
    @Override
    public byte readByte(final long position) throws ByteReaderException {
        try {
            file.seek(position);
            return file.readByte();
        } catch (IOException ex) {
            throw new ByteReaderException(ex);
        }
    }
    
    /**
     * 
     * @return An Array containing a byte array and the offset into it for a given position.
     */
    @Override
    public Array getByteArray(final long position) throws ByteReaderException {
        if (position >= 0 && position < length) {
            try {
                int blockSize = arraySize;
                final long readPos = (position / blockSize) * blockSize;
                final int offset = (int) (position % blockSize); 
                
                // If the remaining length is smaller than the block size,
                if (readPos + blockSize > length) {
                    blockSize = (int) (length - readPos); // cut down the blocksize.
                } 
                
                final byte[] bytes = new byte[blockSize];
                file.seek(readPos);
                final int totalRead = ReadUtils.readBytes(file, bytes);
                return new Array(bytes, offset, totalRead - 1);
            } catch (IOException ex) {
                throw new ByteReaderException(ex);
            }
        }
        return Array.EMPTY;
    }

    @Override
    public Iterator<Array> iterator() {
        return new FileArrayIterator();
    }
    
    private class FileArrayIterator implements Iterator<Array> {

        private int position = 0;
        
        @Override
        public boolean hasNext() {
            return position < length;
        }

        @Override
        public Array next() {
            final Array array = getByteArray(position);
            position += arraySize;
            return array;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove Arrays from the FileArrayIterator.");
        }
    }
    
}
