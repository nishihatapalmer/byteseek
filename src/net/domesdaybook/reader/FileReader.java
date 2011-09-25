/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

import net.domesdaybook.reader.cache.WindowCache;
import net.domesdaybook.reader.cache.MostRecentlyUsedCache;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * A class which reads a random access file into cached byte arrays.
 * 
 * This class (like the underlying RandomAccessFile) is not thread-safe.
 * 
 * @author matt
 */
public class FileReader extends AbstractReader {

    private final static int DEFAULT_CAPACITY = 8;
    private final static String READ_ONLY = "r";
    private final static String NULL_ARGUMENTS = "Null file passed to FileReader";
    
    private final File file;
    private final RandomAccessFile randomAccessFile;
    private final long length;
    
    
    /**
     * Constructs a FileReader which defaults to an array size of 4096,
     * caching the last 8 most recently used Windows.
     * 
     * @param file The file to read from.
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileReader(final File file) throws FileNotFoundException {
        this(file, DEFAULT_WINDOW_SIZE, 
             new MostRecentlyUsedCache(DEFAULT_CAPACITY));
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
        this(file, DEFAULT_WINDOW_SIZE, cache);
    }     
    
    
    /**
     * Constructs a FileReader using the array size passed in, and caches the
     * last 8 Windows.
     * 
     * @param file The file to read from.
     * @param windowSize the size of the byte array to read from the file.
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileReader(final File file, final int windowSize) throws FileNotFoundException {
        this(file, windowSize,
             new MostRecentlyUsedCache(DEFAULT_CAPACITY));
    }    
    
    
    /**
     * Constructs a FileReader using the array size passed in, and caches the
     * last most recently used Windows up to the capacity specified.
     * 
     * @param file The file to read from.
     * @param windowSize the size of the byte array to read from the file.
     * @param capacity the number of byte arrays to cache (using a most recently used strategy).
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileReader(final File file, final int windowSize, final int capacity) throws FileNotFoundException {
        this(file, windowSize, 
             new MostRecentlyUsedCache(capacity));
    }   
    

    /**
     * Constructs a FileReader which defaults to an array size of 4096,
     * caching the last 8 most recently used Windows.
     * 
     * @param path The path of the file to read from.
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileReader(final String path) throws FileNotFoundException {
        this(new File(path), DEFAULT_WINDOW_SIZE, 
             new MostRecentlyUsedCache(DEFAULT_CAPACITY));
    }
    

    /**
     * Constructs a FileReader which defaults to an array size of 4096
     * using the WindowCache passed in to cache Windows.
     * 
     * @param path The path of the file to read from.
     * @param cache the cache of Windows to use.
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileReader(final String path, final WindowCache cache) throws FileNotFoundException {
        this(new File(path), DEFAULT_WINDOW_SIZE, cache);
    }     
    
    
    /**
     * Constructs a FileReader using the array size passed in, and caches the
     * last Window 
     * 
     * @param path The path of the file to read from.
     * @param windowSize the size of the byte array to read from the file.
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileReader(final String path, final int windowSize) throws FileNotFoundException {
        this(new File(path), windowSize, 
             new MostRecentlyUsedCache(DEFAULT_CAPACITY));
    }    
    
    
    /**
     * Constructs a FileReader using the array size passed in, and caches the
     * last Window 
     * 
     * @param path The path of the file to read from.
     * @param windowSize the size of the byte array to read from the file.
     * @param capacity the number of byte arrays to cache (using a most recently used strategy).
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileReader(final String path, final int windowSize, final int capacity) throws FileNotFoundException {
        this(new File(path), windowSize, 
             new MostRecentlyUsedCache(capacity));
    }      
    

    public FileReader(final FileReader from) throws FileNotFoundException {
        this(from.file, from.windowSize, from.cache.newInstance());
    }
    
    
    public FileReader(final FileReader from, final int windowSize) throws FileNotFoundException {
        this(from.file, windowSize, from.cache.newInstance());
    }  
    
    
    public FileReader(final FileReader from, final int windowSize,
                  final WindowCache cache) throws FileNotFoundException {
        this(from.file, windowSize, cache);
    }  
    
       
    
    /**
     * Constructs a FileReader which reads the file into arrays of
     * the specified size.
     *
     * @param file The file to read from.
     * @param windowSize the size of the byte array to read from the file.
     * @param cache the cache of Windows to use.
     * @throws FileNotFoundException If the file does not exist.
     * @throws IllegalArgumentException if the file passed in is null.
     */
    public FileReader(final File file, final int windowSize,
                      final WindowCache cache) throws FileNotFoundException {
        super(windowSize, cache);
        if (file == null) {
            throw new IllegalArgumentException(NULL_ARGUMENTS);
        }
        this.file = file;
        this.randomAccessFile = new RandomAccessFile(file, READ_ONLY);
        this.length = file.length();
    }    

   
    /**
     * 
     * @return The length of the file accessed by the reader.
     */
    @Override
    public final long length(){
        return length;
    }

    
    @Override
    final Window createWindow(final long windowStart) throws IOException {
        try {
            randomAccessFile.seek(windowStart);
            final byte[] bytes = new byte[windowSize];            
            final int totalRead = ReadUtils.readBytes(randomAccessFile, bytes);
            if (totalRead > 0) {
                return new Window(bytes, windowStart, totalRead);
            }
        } catch (final EOFException justReturnNull) {
        }
        return null;
    }
    
    
    @Override
    public void close() throws IOException {
        try {
            randomAccessFile.close();
        } finally {
            super.close();
        }
    }
    
    
    public final File getFile() {
        return file;
    }
    
    
}
