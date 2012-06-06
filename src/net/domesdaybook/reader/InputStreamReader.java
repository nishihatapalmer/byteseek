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
import java.io.IOException;
import java.io.InputStream;
import net.domesdaybook.reader.cache.MostRecentlyUsedCache;
import net.domesdaybook.reader.cache.TempFileCache;
import net.domesdaybook.reader.cache.TwoLevelCache;


/**
 * A Reader extending {@link AbstractReader} over an {@link java.io.InputStream}.
 * <p>
 * The implementation is stream-friendly, in that it does not need to know the length
 * of the stream in order to serve bytes out of it for any position in the stream.
 * If a position requested has not yet been read in the stream, then the stream
 * will be read (and the Windows encountered cached) until the position requested is
 * available in a {@link Window}.
 * Note that if you explicitly call the {@link #length()} method, then the stream
 * will be read until the end is encountered and a length can be determined.
 * <p>
 * By default, the InputStreamReader uses a {@link TwoLevelCache}, with a
 * {@link MostRecentlyUsedCache} as its primary cache, and a {@link TempFileCache} as
 * its secondary cache.  If the input stream fits entirely into the MostRecentlyUsedCache,
 * then a temporary file will never be created.  The secondary cache only gets used if
 * a Window drops out of the primary cache due to exceeding its capacity.
 * <p>
 * Note that if you provide your own WindowCache to this Reader, then you should either
 * be sure that it is always possible to retrieve an earlier Window from the cache
 * (since it is not possible to rewind all InputStreams), or be sure that in the use
 * you make of this Reader, a position which is no longer available in your cache
 * will never be requested.  If you request a position which has already been read
 * in the stream, but which the cache can no longer provide, then a {@link WindowMissingException}
 * will be thrown (this is a RuntimeException, as it indicates a programming error).
 * <p>
 * This class depends on InputStream implementations, which are unlikely to be
 * thread-safe.
 *
 * @author Matt Palmer
 */
public class InputStreamReader extends AbstractReader {

    private final InputStream stream;
    private long streamPos = 0;
    private long length = UNKNOWN_LENGTH;
    
    
    /**
     * Constructs an InputStreamReader from an InputStream, using
     * the default window size of 4096 and a default capacity of 32,
     * and a {@link TwoLevelCache} with a {@link MostRecentlyUsedCache} as its
     * primary cache and a {@link TempFileCache} as the secondary cache.
     * 
     * @param stream The InputStream to read from.
     * @throws IllegalArgumentException if the stream is null.
     */
    public InputStreamReader(final InputStream stream) {
        this(stream, DEFAULT_WINDOW_SIZE, DEFAULT_CAPACITY);
    }
    
    
    /**
     * Constructs an InputStreamReader from an InputStream using a 
     * default window size of 4096, and the {@link WindowCache} provided.
     * The WindowCache must ensure that it can provide any Window
     * from a position in the stream which has already been read, or
     * you must be sure that you will never request such a position if the
     * cache cannot provide that guarantee.
     * 
     * @param stream The InputStream to read from.
     * @param cache The WindowCache to use.
     * @throws IllegalArgumentException if the stream or cache is null.
     */
    public InputStreamReader(final InputStream stream, final WindowCache cache) {
        this(stream, DEFAULT_WINDOW_SIZE, cache);
    }      
    
    
    /**
     * Constructs an InputStreamReader from an InputStream, using
     * the window size provided and a default capacity of 32,
     * and a {@link TwoLevelCache} with a {@link MostRecentlyUsedCache} as its
     * primary cache and a {@link TempFileCache} as the secondary cache.
     * 
     * @param stream The InputStream to read from.
     * @param windowSize The size of a Window to create from the stream.
     * @throws IllegalArgumentException if the stream is null, or the window size
     *         is less than one.
     */
    public InputStreamReader(final InputStream stream, final int windowSize) {
        this(stream, windowSize, DEFAULT_CAPACITY);
    }

    
    /**
     * Constructs an InputStreamReader from an InputStream, using
     * the window size provided, the capacity provided
     * and a {@link TwoLevelCache} with a {@link MostRecentlyUsedCache} as its
     * primary cache and a {@link TempFileCache} as the secondary cache.
     * 
     * @param stream The InputStream to read from.
     * @param windowSize The size of a Window to create from the stream.
     * @param capacity The capacity of the MostRecentlyUsedCache.
     * @throws IllegalArgumentException if the stream is null, or the window size
     *         is less than one, or the capacity is less than zero.
     */
    public InputStreamReader(final InputStream stream, final int windowSize, final int capacity) {
        this(stream, windowSize, 
             TwoLevelCache.create(new MostRecentlyUsedCache(capacity),
                                  new TempFileCache()));
    }
    
    
    /**
     * Constructs an InputStreamReader from an InputStream, using 
     * the window size provided and the {@link WindowCache} provided.
     * The WindowCache must ensure that it can provide any Window
     * from a position in the stream which has already been read, or
     * you must be sure that you will never request such a position if the
     * cache cannot provide that guarantee.
     * 
     * @param stream The InputStream to read from.
     * @param windowSize The size of a Window to create from the stream.
     * @param cache The WindowCache to use.
     * @throws IllegalArgumentException if the stream or cache is null, or the
     *         window size is less than one.
     */
    public InputStreamReader(final InputStream stream, final int windowSize, final WindowCache cache) { 
        super(windowSize, cache);
        if (stream == null) {
            throw new IllegalArgumentException("Stream is null.");
        }
        this.stream = stream;
    }    
    
    
    /**
     * Returns a window onto the data for a given position.  The position does not
     * have to be the beginning of a {@link Window} - but the Window returned must include
     * that position (if such a position exists in the Reader).
     * 
     * @param position The position in the reader for which a Window is requested.
     * @return A Window backed by a byte array onto the data for a given position.
     *         If a window can't be provided for the given position, null is returned.
     * @throws IOException if an IO error occurred trying to create a new window.
     * @throws WindowMissingException if the cache could not provide a Window for a
     *         position in the stream which has already been read.
     */
    @Override
    public final Window getWindow(final long position) 
            throws IOException, WindowMissingException {
        final Window window = super.getWindow(position);
        if (window == null && position < streamPos && position >= 0) {
            // No window was returned, but the position requested has already
            // been read. This means the cache algorithm selected to use with
            // this reader cannot return an earlier position, and being a stream,
            // we can't rewind to read it again.  There is nothing which can be
            // done at this point other than to throw an exception.
            final String message = "Cache failed to provide a window at position: %d but we have already read up to: %d";
            throw new WindowMissingException(String.format(message, position, streamPos));
        }
        return window;
    }
 
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Window createWindow(final long readPos) throws IOException {
        Window lastWindow = null;
        while (readPos > streamPos && length == UNKNOWN_LENGTH) {
            final byte[] bytes = new byte[windowSize];
            final int totalRead = ReadUtils.readBytes(stream, bytes);
            if (totalRead > 0) {
                lastWindow = new Window(bytes, streamPos, totalRead);  
                streamPos += totalRead;                                        
            }
            if (totalRead < windowSize) { // If we read less than the available array:
                length = streamPos;       // then the length is whatever the streampos is now.
            }
            if (readPos <= streamPos) {      // If we still haven't reached the window
                cache.addWindow(lastWindow); // for the requested position, cache it.
            }
        }
        return lastWindow;
    }    

    
    /**
     * Returns the total length of the InputStream.  
     * <p>
     * Note that calling this method will cause the entire stream to be read
     * and cached in order to determine the length.
     * 
     * @return The total length of the stream.
     * @throws IOException If any problem occurred reading the stream.
     */
    @Override
    public long length() throws IOException {
        while (length == UNKNOWN_LENGTH) {
            final byte[] bytes = new byte[windowSize];
            final int totalRead = ReadUtils.readBytes(stream, bytes);
            if (totalRead > 0) {
                final Window lastWindow = new Window(bytes, streamPos, totalRead); 
                streamPos += totalRead;                  
                cache.addWindow(lastWindow);
            }
            if (totalRead < windowSize) { // If we read less than the available array:
                length = streamPos;
            }
        }
        return length;
    }

    
    /**
     * Closes the underlying InputStream and clears any cache associated with it
     * in this Reader.
     * 
     * @throws IOException If a problem occurred closing the stream.
     */
    @Override
    public void close() throws IOException {
        try {
            stream.close();
        } finally {
            super.close();
        }
    }

    
}
