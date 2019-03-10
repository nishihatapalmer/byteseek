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

import net.byteseek.io.IOIterator;
import net.byteseek.io.reader.cache.WindowCache;
import net.byteseek.io.reader.windows.Window;
import net.byteseek.utils.ArgUtils;

//FUTURE:
//      * If windowSize is a power of two, could use bit masking to get the
//        window offset (remainder), rather than the expensive % remainder operation.

/**
 * An abstract implementation of the WindowReader interface, which also implements
 * Iterable&lt;WindowReader&gt; to allow iterating over the Windows of a WindowReader.
 * <p>
 * It provides common Window and cache management services using a fixed Window
 * size, and a standard Window iterator {@link net.byteseek.io.reader.WindowIterator}.
 * 
 * @author Matt Palmer
 */
public abstract class AbstractCacheReader implements WindowReader {

	/**
	 * A constant indicating that there is no byte at the position requested,
	 * returned by the {@link #readByte(long)} method.
	 */
	protected final static int NO_BYTE_AT_POSITION = -1;

	/**
	 * A constant indicating that no bytes were read.
	 */
	protected final static int NO_BYTES_READ = 0;

	/**
	 * A constant indicating that the length of the reader is currently unknown.
	 */
	protected static final long UNKNOWN_LENGTH = -1;

	//TODO: profile with 8096 length windows.  would halve position maps and inter-window matching and searching.
    //      May even be more efficient for I/O these days.
	/**
	 * The default size in bytes of a Window, unless a different value is
	 * provided in the constructor.
	 */
	protected final static int DEFAULT_WINDOW_SIZE = 4096;

	/**
	 * The default number of Windows to cache, unless a different value is
	 * provided in the constructor.
	 */
	protected final static int DEFAULT_CAPACITY = 32;

	/**
	 * The size in bytes of each Window (assuming there are sufficient bytes to
	 * fill it).
	 */
	protected final int windowSize;

	/**
	 * The Window caching mechanism used by this WindowReader.
	 */
	protected final WindowCache cache;

	/**
	 * Construct the WindowReader using a default window size, using the WindowCache
	 * provided.
	 * 
	 * @param cache
	 *            The WindowCache to use.
	 * @throws IllegalArgumentException
	 *             if the WindowCache is null.
	 */
	public AbstractCacheReader(final WindowCache cache) {
		this(DEFAULT_WINDOW_SIZE, cache);
	}

	/**
	 * Constructs the WindowReader using the window size and window cache provided.
	 * 
	 * @param windowSize
	 *            The size of Window to use.
	 * @param cache
	 *            The WindowCache to use.
	 * @throws IllegalArgumentException
	 *             if the window size is less than one or the WindowCache is
	 *             null.
	 */
	public AbstractCacheReader(final int windowSize, final WindowCache cache) {
		ArgUtils.checkPositiveInteger(windowSize, "windowSize");
		ArgUtils.checkNullObject(cache, "cache");
		this.windowSize = windowSize;
		this.cache = cache;
	}

	/**
	 * Reads a byte at the given position.
	 * 
	 * @param position The position in the reader to read a byte from.
	 * @return The byte at the given position (0-255), or a negative number if
	 *         there is no byte at the position specified.
	 * @throws IOException if an error occurs reading the byte.
	 */
	@Override
	public int readByte(final long position) throws IOException {
		final Window window = getWindow(position);
		final int offset = (int) (position % (long) windowSize);
		if (window == null || offset >= window.length()) {
			return NO_BYTE_AT_POSITION;
		}
		return window.getByte(offset) & 0xFF;
	}

	@Override
	public int read(final long position, final byte[] readInto) throws IOException {
		return read(position, readInto, 0, readInto.length);
	}

	@Override
    public int read(final long position, final byte[] readInto, final int offset, final int maxLength) throws IOException {
	    // Basic sanity tests:
	    if (position < 0) {
	        return NO_BYTE_AT_POSITION;
        }
	    ArgUtils.checkIndexOutOfBounds(readInto.length, offset);

        // Calculate safe bounds:
        final int arrayBytesAvailable = readInto.length - offset;
        final long readerBytesPossible = Long.MAX_VALUE - position;
        final int bytesPossible = (int) Math.min(readerBytesPossible, arrayBytesAvailable);
        final int maxBytesToCopy = Math.min(bytesPossible, maxLength);

        // Copy data into the byte array from the cache first, then the reader.
        // A cache may copy more than one Window if it can (although it doesn't have to).
        // The reader will only read a single Window at a time, on the grounds that the next
        // Window may be held in the cache instead, which will either be faster, or will be
        // the only method available to return the data (e.g. a temp file cache over a stream).
        int bytesCopied = 0;
        final int localWindowSize = windowSize;
	    while (bytesCopied < maxBytesToCopy) {

	        // Find the window our position falls in:
            final long currentPos = position + bytesCopied;
	        final long windowStart = currentPos % localWindowSize;
	        final int windowOffset = (int) (currentPos - windowStart);

	        // Read bytes from the cache at that window position:
	        final int bytesRemaining = maxBytesToCopy - bytesCopied;
            final int cacheBytesRead = cache.read(windowStart, windowOffset, readInto, offset + bytesCopied, bytesRemaining);

            // If the cache doesn't have the bytes for this window, read it from the reader instead:
            if (cacheBytesRead == 0) {
                final int readerBytesRead = readWindowBytes(windowStart, windowOffset, readInto,
                                                            offset + bytesCopied, maxBytesToCopy); //TODO: max bytes to copy not correct.

                // If we get negative bytes from the reader, we're at the end of the data source.
                if (readerBytesRead < 0) {
                    // If we copied no bytes and we're at the end, return -1, otherwise return how many bytes were copied.
                    return bytesCopied == 0? NO_BYTE_AT_POSITION : bytesCopied;
                }

                // Defensive programming - avoid potential infinite loop.
                // If the cache or the reader doesn't have any bytes, and it's not the end of the data source,
                // something terrible must have happened.  Raise an IOException:
                if (readerBytesRead == 0) {
                    //TODO: think about this some more - how could this happen, could an InputStreamReader read no
                    //      bytes while putting them in the cache, and assume you could just ask for the bytes from
                    //      the cache on the next round the loop?  If so, have to avoid infinite loop situation...
                }
                bytesCopied += readerBytesRead;
            } else {
                bytesCopied += cacheBytesRead;
            }



            //TODO: what if both return zero bytes read?  infinite loop.
        }
        return bytesCopied;
    }

    protected abstract int readWindowBytes(long windowStart, int windowOffset, byte[] readInto, int toArrayPos, int length);


	/**
	 * Returns a window onto the data for a given position. The position does
	 * not have to be the beginning of a Window - but the Window returned must
	 * include that position (if such a position exists in the WindowReader).
	 * 
	 * @param position
	 *            The position in the reader for which a Window is requested.
	 * @return A Window backed by a byte array onto the data for a given
	 *         position. If a window can't be provided for the given position,
	 *         null is returned.
	 * @throws IOException
	 *             if an IO error occurred trying to create a new window.
	 */
	@Override
	public Window getWindow(final long position) throws IOException {
		if (position < 0) {
			return null;
		}

		final int offset = (int) (position % (long) windowSize);
		final long windowStart = position - offset;
        Window window = cache.getWindow(windowStart);
        if (window == null) {
            window = createWindow(windowStart);
            if (window != null) {
                cache.addWindow(window);
            }
		}

		// Finally, if the position requested is outside the window limit,
		// don't return a window. The position itself is invalid, even though
		// that position is part of a window which has valid positions.
		if (window != null && offset >= window.length()) {
			window = null;
		}
		return window;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IOIterator<Window> iterator() {
		return new WindowIterator(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws IOException {
		cache.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getWindowOffset(final long position) {
		return (int) (position % (long) windowSize);
	}

	/**
	 * An abstract method which must create a {@link Window} for the position
	 * given. Returns null if a Window cannot be provided for the position
	 * provided.
	 * 
	 * @param windowStart
	 *            The position in the WindowReader at which the Window should begin.
	 * @return A Window beginning at the position given. If no Window can be
	 *         created at the position given (e.g. the position is negative, or
	 *         past the end of the underlying byte source), then this method
	 *         MUST return null.
	 * @throws IOException
	 *             If the WindowReader has an issue reading the bytes required for a
	 *             valid Window.
	 */
	protected abstract Window createWindow(final long windowStart) throws IOException;

}
