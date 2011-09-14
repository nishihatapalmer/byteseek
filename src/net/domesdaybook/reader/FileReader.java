/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * A class which reads a random access file into cached byte arrays.
 * 
 * This class (like the underlying RandomAccessFile) is not thread-safe.
 * 
 * @author matt
 */
public final class FileReader extends AbstractReader {

    private final static int DEFAULT_CAPACITY = 8;
    private final static String READ_ONLY = "r";
    private final static String NULL_ARGUMENTS = "Null file passed to FileReader";
    private final static boolean TEMP_FILE = true;
    private final static boolean NOT_TEMP = false;
    
    private final File file;
    private final RandomAccessFile randomAccessFile;
    private final long length;
    private final boolean fileIsTemporary;

    /**
     * Constructs a FileReader which defaults to an array size of 4096,
     * caching the last 3 most recently used Windows.
     * 
     * @param file The file to read from.
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileReader(final File file) throws FileNotFoundException {
        this(file, DEFAULT_ARRAY_SIZE, new WindowCacheMostRecentlyUsed(DEFAULT_CAPACITY), NOT_TEMP);
    }
    

    /**
     * Constructs a FileReader which defaults to an array size of 4096
     * using the WindowCache passed in to cache ArrayWindows.
     * 
     * @param file The file to read from.
     * @param cache the cache of Windows to use.
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileReader(final File file, final WindowCache cache) throws FileNotFoundException {
        this(file, DEFAULT_ARRAY_SIZE, cache, NOT_TEMP);
    }     
    
    
    /**
     * Constructs a FileReader using the array size passed in, and caches the
     * last Window 
     * 
     * @param file The file to read from.
     * @param arraySize the size of the byte array to read from the file.
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileReader(final File file, final int arraySize) throws FileNotFoundException {
        this(file, arraySize,
             new WindowCacheMostRecentlyUsed(DEFAULT_CAPACITY), NOT_TEMP);
    }    
    
    
    /**
     * Constructs a FileReader using the array size passed in, and caches the
     * last Window 
     * 
     * @param file The file to read from.
     * @param arraySize the size of the byte array to read from the file.
     * @param capacity the number of byte arrays to cache (using a most recently used strategy).
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileReader(final File file, final int arraySize, final int capacity) throws FileNotFoundException {
        this(file, arraySize, 
             new WindowCacheMostRecentlyUsed(capacity), NOT_TEMP);
    }    
    

    /**
     * Constructs a FileReader which defaults to an array size of 4096,
     * caching the last 3 most recently used Windows.
     * 
     * @param file The file to read from.
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileReader(final InputStream in) throws FileNotFoundException, IOException {
        this(ReadUtils.createTempFile(in), DEFAULT_ARRAY_SIZE, 
             new WindowCacheMostRecentlyUsed(DEFAULT_CAPACITY), TEMP_FILE);
    }
    

    /**
     * Constructs a FileReader which defaults to an array size of 4096
     * using the WindowCache passed in to cache ArrayWindows.
     * 
     * @param file The file to read from.
     * @param cache the cache of Windows to use.
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileReader(final InputStream in, final WindowCache cache) throws FileNotFoundException, IOException {
        this(ReadUtils.createTempFile(in), DEFAULT_ARRAY_SIZE, cache, TEMP_FILE);
    }     
    
    
    /**
     * Constructs a FileReader using the array size passed in, and caches the
     * last Window 
     * 
     * @param file The file to read from.
     * @param arraySize the size of the byte array to read from the file.
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileReader(final InputStream in, final int arraySize) throws FileNotFoundException, IOException {
        this(ReadUtils.createTempFile(in), arraySize, 
             new WindowCacheMostRecentlyUsed(DEFAULT_CAPACITY), TEMP_FILE);
    }    
    
    
    /**
     * Constructs a FileReader using the array size passed in, and caches the
     * last Window 
     * 
     * @param file The file to read from.
     * @param arraySize the size of the byte array to read from the file.
     * @param capacity the number of byte arrays to cache (using a most recently used strategy).
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileReader(final InputStream in, final int arraySize, final int capacity) throws FileNotFoundException, IOException {
        this(ReadUtils.createTempFile(in), arraySize, 
             new WindowCacheMostRecentlyUsed(capacity), TEMP_FILE);
    }        
    
    
    /**
     * Constructs a FileReader which reads the file into arrays of
     * the specified size.
     *
     * @param file The file to read from.
     * @param arraySize the size of the byte array to read from the file.
     * @param cache the cache of Windows to use.
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileReader(final File file, final int arraySize,
                      final WindowCache cache, final boolean fileIsTemporary) throws FileNotFoundException {
        super(arraySize, cache);
        if (file == null) {
            throw new IllegalArgumentException(NULL_ARGUMENTS);
        }
        this.file = file;
        this.randomAccessFile = new RandomAccessFile(file, READ_ONLY);
        this.length = file.length();
        this.fileIsTemporary = fileIsTemporary;
    }    

   
    /**
     * 
     * @return The length of the file accessed by the reader.
     */
    @Override
    public long length(){
        return length;
    }

    
    @Override
    Window createWindow(final long readPos) throws ReaderException {
        // If the remaining length is smaller than the block size,
        int blockSize = arraySize;
        if (readPos + blockSize > length) {
            blockSize = (int) (length - readPos); // cut down the blocksize.
        } 

        // Read the bytes for this position and create the window for it:
        final byte[] bytes = new byte[blockSize];
        try {
            randomAccessFile.seek(readPos);
            final int totalRead = ReadUtils.readBytes(randomAccessFile, bytes);
            return new Window(bytes, readPos, totalRead);
        } catch (IOException ex) {
            throw new ReaderException(ex);
        }
    }
    
    
    @Override
    public void close() {
        super.close();    
        try {
            randomAccessFile.close();
        } catch (final IOException canDoNothing) {
        }
        if (fileIsTemporary) {
            file.delete();
        }        
    }
    
    
}
