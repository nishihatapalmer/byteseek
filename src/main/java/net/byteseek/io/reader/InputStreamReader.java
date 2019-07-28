/*
 * Copyright Matt Palmer 2011-2019, All rights reserved.
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

package net.byteseek.io.reader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import net.byteseek.io.IOUtils;
import net.byteseek.io.reader.cache.LeastRecentlyUsedCache;
import net.byteseek.io.reader.cache.TempFileCache;
import net.byteseek.io.reader.cache.TwoLevelCache;
import net.byteseek.io.reader.cache.WindowCache;
import net.byteseek.io.reader.windows.*;
import net.byteseek.utils.ArgUtils;

/**
 * A WindowReader extending {@link AbstractCacheReader} over an {@link java.io.InputStream}
 * .
 * <p>
 * The implementation is stream-friendly, in that it does not need to know the
 * length of the stream in order to serve bytes out of it for any position in
 * the stream. If a position requested has not yet been read in the stream, then
 * the stream will be read (and the Windows encountered cached) until the
 * position requested is available in a {@link net.byteseek.io.reader.windows.HardWindow}. Note that if you
 * explicitly call the {@link #length()} method, then the stream will be read
 * until the end is encountered and a length can be determined.
 * <p>
 * By default, the InputStreamReader uses a {@link TwoLevelCache}, with a
 * {@link net.byteseek.io.reader.cache.LeastRecentlyUsedCache} as its primary cache, and a
 * {@link TempFileCache} as its secondary cache. If the input stream fits
 * entirely into the MostRecentlyUsedCache, then a temporary file will never be
 * created. The secondary cache only gets used if a Window drops out of the
 * primary cache due to exceeding its capacity.
 * <p>
 * Note that if you provide your own WindowCache to this WindowReader, then you should
 * either be sure that it is always possible to retrieve an earlier Window from
 * the cache (since it is not possible to rewind all InputStreams), or be sure
 * that in the use you make of this WindowReader, a position which is no longer
 * available in your cache will never be requested. If you request a position
 * which has already been read in the stream, but which the cache can no longer
 * provide, then a {@link net.byteseek.io.reader.windows.WindowMissingException} will be thrown (this is a
 * RuntimeException, as it indicates a programming error).
 * <p>
 * This class depends on InputStream implementations, which are unlikely to be
 * thread-safe.
 *
 * @author Matt Palmer
 */
public final class InputStreamReader extends AbstractCacheReader {

    private final InputStream stream;
    private final boolean closeStreamOnClose;
    private long nextReadPos = 0;
    private long length = UNKNOWN_LENGTH;
    private WindowFactory factory = HardWindow.FACTORY;

    /**
     * Constructs an InputStreamReader from an InputStream, using the default
     * window size of 4096 and a default capacity of 32, and a
     * {@link TwoLevelCache} with a {@link net.byteseek.io.reader.cache.LeastRecentlyUsedCache} as its primary
     * cache and a {@link TempFileCache} as the secondary cache.
     *
     * @param stream The InputStream to read from.
     * @throws IllegalArgumentException if the stream is null.
     */
    public InputStreamReader(final InputStream stream) {
        this(stream, DEFAULT_WINDOW_SIZE, DEFAULT_CAPACITY, true);
    }

    /**
     * Constructs an InputStreamReader from an InputStream, using the default
     * window size of 4096 and a default capacity of 32, and a
     * {@link TwoLevelCache} with a {@link net.byteseek.io.reader.cache.LeastRecentlyUsedCache} as its primary
     * cache and a {@link TempFileCache} as the secondary cache.
     *
     * @param stream             The InputStream to read from.
     * @param closeStreamOnClose Whether to close the underlying stream when this reader is closed.
     * @throws IllegalArgumentException if the stream is null.
     */
    public InputStreamReader(final InputStream stream, final boolean closeStreamOnClose) {
        this(stream, DEFAULT_WINDOW_SIZE, DEFAULT_CAPACITY, closeStreamOnClose);
    }


    /**
     * Constructs an InputStreamReader from an InputStream using a default
     * window size of 4096, and the {@link WindowCache} provided. The
     * WindowCache must ensure that it can provide any Window from a position in
     * the stream which has already been read, or you must be sure that you will
     * never request such a position if the cache cannot provide that guarantee.
     *
     * @param stream The InputStream to read from.
     * @param cache  The WindowCache to use.
     * @throws IllegalArgumentException if the stream or cache is null.
     */
    public InputStreamReader(final InputStream stream, final WindowCache cache) {
        this(stream, DEFAULT_WINDOW_SIZE, cache, true);
    }

    /**
     * Constructs an InputStreamReader from an InputStream using a default
     * window size of 4096, and the {@link WindowCache} provided. The
     * WindowCache must ensure that it can provide any Window from a position in
     * the stream which has already been read, or you must be sure that you will
     * never request such a position if the cache cannot provide that guarantee.
     *
     * @param stream             The InputStream to read from.
     * @param cache              The WindowCache to use.
     * @param closeStreamOnClose Whether to close the underlying stream when this reader is closed.	 *
     * @throws IllegalArgumentException if the stream or cache is null.
     */
    public InputStreamReader(final InputStream stream, final WindowCache cache,
                             final boolean closeStreamOnClose) {
        this(stream, DEFAULT_WINDOW_SIZE, cache, closeStreamOnClose);
    }

    /**
     * Constructs an InputStreamReader from an InputStream, using the window
     * size provided and a default capacity of 32, and a {@link TwoLevelCache}
     * with a {@link net.byteseek.io.reader.cache.LeastRecentlyUsedCache} as its primary cache and a
     * {@link TempFileCache} as the secondary cache.
     *
     * @param stream     The InputStream to read from.
     * @param windowSize The size of a Window to create from the stream.
     * @throws IllegalArgumentException if the stream is null, or the window size is less than one.
     */
    public InputStreamReader(final InputStream stream, final int windowSize) {
        this(stream, windowSize, DEFAULT_CAPACITY, true);
    }

    /**
     * Constructs an InputStreamReader from an InputStream, using the window
     * size provided and a default capacity of 32, and a {@link TwoLevelCache}
     * with a {@link net.byteseek.io.reader.cache.LeastRecentlyUsedCache} as its primary cache and a
     * {@link TempFileCache} as the secondary cache.
     *
     * @param stream             The InputStream to read from.
     * @param windowSize         The size of a Window to create from the stream.
     * @param closeStreamOnClose Whether to close the underlying stream when this reader is closed.
     * @throws IllegalArgumentException if the stream is null, or the window size is less than one.
     */
    public InputStreamReader(final InputStream stream, final int windowSize, final boolean closeStreamOnClose) {
        this(stream, windowSize, DEFAULT_CAPACITY, closeStreamOnClose);
    }

    /**
     * Constructs an InputStreamReader from an InputStream, using the window
     * size provided, the capacity provided and a {@link TwoLevelCache} with a
     * {@link net.byteseek.io.reader.cache.LeastRecentlyUsedCache} as its primary cache and a
     * {@link TempFileCache} as the secondary cache.
     *
     * @param stream     The InputStream to read from.
     * @param windowSize The size of a Window to create from the stream.
     * @param capacity   The capacity of the MostRecentlyUsedCache.
     * @throws IllegalArgumentException if the stream is null, or the window size is less than one,
     *                                  or the capacity is less than zero.
     */
    public InputStreamReader(final InputStream stream, final int windowSize,
                             final int capacity) {
        this(stream, windowSize, new TwoLevelCache(
                new LeastRecentlyUsedCache(capacity), new TempFileCache()), true);
    }

    /**
     * Constructs an InputStreamReader from an InputStream, using the window
     * size provided, the capacity provided and a {@link TwoLevelCache} with a
     * {@link net.byteseek.io.reader.cache.LeastRecentlyUsedCache} as its primary cache and a
     * {@link TempFileCache} as the secondary cache.
     *
     * @param stream             The InputStream to read from.
     * @param windowSize         The size of a Window to create from the stream.
     * @param capacity           The capacity of the MostRecentlyUsedCache.
     * @param closeStreamOnClose Whether to close the underlying stream when this reader is closed.
     * @throws IllegalArgumentException if the stream is null, or the window size is less than one,
     *                                  or the capacity is less than zero.
     */
    public InputStreamReader(final InputStream stream, final int windowSize,
                             final int capacity, final boolean closeStreamOnClose) {
        this(stream, windowSize, new TwoLevelCache(
                        new LeastRecentlyUsedCache(capacity), new TempFileCache()),
                closeStreamOnClose);
    }

    /**
     * Constructs an InputStreamReader from an InputStream, using the window
     * size provided and the {@link WindowCache} provided. The WindowCache must
     * ensure that it can provide any Window from a position in the stream which
     * has already been read, or you must be sure that you will never request
     * such a position if the cache cannot provide that guarantee.
     *
     * @param stream     The InputStream to read from.
     * @param windowSize The size of a Window to create from the stream.
     * @param cache      The WindowCache to use.
     * @throws IllegalArgumentException if the stream or cache is null, or the window size is less
     *                                  than one.
     */
    public InputStreamReader(final InputStream stream, final int windowSize,
                             final WindowCache cache) {
        this(stream, windowSize, cache, true);
    }


    /**
     * Constructs an InputStreamReader from an InputStream, using the window
     * size provided and the {@link WindowCache} provided. The WindowCache must
     * ensure that it can provide any Window from a position in the stream which
     * has already been read, or you must be sure that you will never request
     * such a position if the cache cannot provide that guarantee.
     *
     * @param stream     The InputStream to read from.
     * @param windowSize The size of a Window to create from the stream.
     * @param cache      The WindowCache to use.
     * @param closeStreamOnClose Whether to close the underlying stream when this reader is closed.
     * @throws IllegalArgumentException if the stream or cache is null, or the window size is less
     *                                  than one.
     */
    public InputStreamReader(final InputStream stream, final int windowSize,
                             final WindowCache cache, final boolean closeStreamOnClose) {
        super(windowSize, cache);
        ArgUtils.checkNullObject(stream, "stream");
        this.stream = stream;
        this.closeStreamOnClose = closeStreamOnClose;
    }

    /**
     * Returns a window onto the data for a given position. The position does
     * not have to be the beginning of a {@link net.byteseek.io.reader.windows.HardWindow} - but the Window
     * returned must include that position (if such a position exists in the
     * WindowReader).
     *
     * @param position The position in the reader for which a Window is requested.
     * @return A Window backed by a byte array onto the data for a given
     * position. If a window can't be provided for the given position,
     * null is returned.
     * @throws IOException                                           if an IO error occurred trying to create a new window.
     * @throws net.byteseek.io.reader.windows.WindowMissingException if the cache could not provide a Window for a position in the
     *                                                               stream which has already been read.
     */
    @Override
    public final Window getWindow(final long position) throws IOException {
        final Window window = super.getWindow(position); // this checks if the reader is open or not.
        if (window == null && position < nextReadPos && position >= 0) {
            // No window was returned, but the position requested has already
            // been read. This means the cache algorithm selected to use with
            // this reader cannot return an earlier position, and being a
            // stream, we can't rewind to read it again. There is nothing which can be
            // done at this point other than to throw an exception.
            final String message = "Cache failed to provide a window at position: %d but we have already read up to: %d";
            throw new WindowMissingException(String.format(message, position, nextReadPos));
        }
        return window;
    }

    @Override
    protected Window createWindow(final long windowPos) throws IOException {
        Window window = null;
        while (nextReadPos <= windowPos && length == UNKNOWN_LENGTH) {
            final byte[] bytes = new byte[windowSize];
            final int totalRead = IOUtils.readBytes(stream, bytes);
            if (totalRead > 0) {
                window = factory.createWindow(bytes, nextReadPos, totalRead);
                nextReadPos += totalRead;
                if (windowPos >= nextReadPos) {   // If we still haven't reached the window
                    cache.addWindow(window); // for the requested position, cache it, as we'll go around again.
                }
            }
            if (totalRead < windowSize) { // If we read less than the available array:
                length = nextReadPos;     // then the length is whatever the nextReadPos is now.
            }
        }
        if (windowPos >= nextReadPos) { // If we didn't manage to get to the window position, we can't return one.
            window = null;
        }
        return window;
    }

    /**
     * Returns the total length of the InputStream.
     * <p>
     * Note that calling this method will cause the entire stream to be read and
     * cached in order to determine the length.
     *
     * @return The total length of the stream.
     * @throws IOException If any problem occurred reading the stream.
     */
    @Override
    public long length() throws IOException {
        ensureOpen();
        while (length == UNKNOWN_LENGTH) {
            final byte[] bytes = new byte[windowSize];
            final int totalRead = IOUtils.readBytes(stream, bytes);
            if (totalRead > 0) {
                final long currentReadPos = nextReadPos;
                nextReadPos += totalRead; // update next readpos before adding to cache, which can throw.
                cache.addWindow(factory.createWindow(bytes, currentReadPos, totalRead));
            }
            // If we read less than the available array, we know the length.
            if (totalRead < windowSize) {
                length = nextReadPos;
            }
        }
        return length;
    }

    @Override
    public void setWindowFactory(final WindowFactory factory) {
        ArgUtils.checkNullObject(factory, "factory");
        this.factory = factory;
    }

    @Override
    protected int readWindowBytes(final long windowStart, final int windowOffset,
                                  final byte[] readInto, final int offset, final int maxLength) throws IOException {
        final Window window = getWindow(windowStart); // this will force read of stream to this window and cache the results.
        if (window != null) {
            final int bytesToCopy = Math.min(maxLength, window.length() - windowOffset);
            System.arraycopy(window.getArray(), windowOffset, readInto, offset, bytesToCopy);
            return bytesToCopy;
        }
        return INVALID_POSITION;
    }

    @Override
    protected int readWindowBytes(final long windowStart, final int windowOffset, final ByteBuffer buffer) throws IOException {
        final Window window = getWindow(windowStart); // this will force read of stream to this window and cache the results.
        if (window != null) {
            final int bytesToCopy = Math.min(buffer.remaining(), window.length() - windowOffset);
            buffer.put(window.getArray(), windowOffset, bytesToCopy);
            return bytesToCopy;
        }
        return INVALID_POSITION;
    }

    /**
     * Calling this method reads the remaining data in the stream.
     * This can be useful to ensure that the stream has been fully cached.
     * It returns the length of the stream.
     * <p>
     * The same result can be achieved by just calling length().
     * However, that does not communicate the intention to read the stream,
     * and it is not obvious that this will happen by calling length().
     *
     * @return The length of the stream.
     * @throws IOException If there was a problem reading the stream.
     */
    public long readEntireStream() throws IOException {
        return length(); // calling length() forces a read of the remaining stream, if any.
    }

    /**
     * Closes the underlying InputStream and clears any cache associated with it
     * in this WindowReader.
     *
     * @throws IOException If a problem occurred closing the stream.
     */
    @Override
    public void close() throws IOException {
        if (!closed) {
            try {
                if (closeStreamOnClose) {
                    stream.close();
                }
            } finally {
                super.close();
            }
        }
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + "(stream:" + stream + " cache:" + cache + ')';
    }

}
