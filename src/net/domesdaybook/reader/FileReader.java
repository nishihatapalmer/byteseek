/*
 * Copyright Matt Palmer 2011-2012, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 
 *  * The names of its contributors may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
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
 * <p>
 * This class (like the underlying RandomAccessFile) is not thread-safe.
 * 
 * @author matt
 */
public class FileReader extends AbstractReader {

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
        this(file, DEFAULT_WINDOW_SIZE, new MostRecentlyUsedCache(DEFAULT_CAPACITY));
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
        this(file, windowSize, new MostRecentlyUsedCache(DEFAULT_CAPACITY));
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
        this(file, windowSize, new MostRecentlyUsedCache(capacity));
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
        this(new File(path), DEFAULT_WINDOW_SIZE, new MostRecentlyUsedCache(DEFAULT_CAPACITY));
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
        this(new File(path), windowSize, new MostRecentlyUsedCache(DEFAULT_CAPACITY));
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
        this(new File(path), windowSize, new MostRecentlyUsedCache(capacity));
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
     * Returns the length of the file.
     * 
     * @return The length of the file accessed by the reader.
     */
    @Override
    public final long length(){
        return length;
    }

    
    /**
     * {@inheritDoc}
     */
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
    
    
    /**
     * Closes the underlying {@link java.io.RandomAccessFile}, then 
     * clears any cache associated with this Reader.
     */    
    @Override
    public void close() throws IOException {
        try {
            randomAccessFile.close();
        } finally {
            super.close();
        }
    }
    
    
    /**
     * Returns the {@link java.io.File} object accessed by this Reader.
     * 
     * @return The File object accessed by this Reader.
     */
    public final File getFile() {
        return file;
    }
    
    
}
