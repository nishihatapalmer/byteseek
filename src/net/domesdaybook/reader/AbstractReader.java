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

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.domesdaybook.reader.cache.WindowCache;

/**
 * An abstract implementation of the Reader interface, which also implements
 * Iterable<Window> to allow iterating over the Windows of a Reader.
 * <p>
 * It provides common Window and cache management services using a fixed Window
 * size, and a standard Window iterator
 * {@link net.domesdaybook.reader.AbstractReader.WindowIterator}.
 * 
 * @author Matt Palmer
 */
public abstract class AbstractReader implements Reader, Iterable<Window> {

	/**
	 * A constant indicating that there is no byte at the position requested,
	 * returned by the {@link #readByte(long)} method.
	 */
	protected final static int NO_BYTE_AT_POSITION = -1;

	/**
	 * A constant indicating that the length of the reader is currently unknown.
	 */
	protected static final long UNKNOWN_LENGTH = -1;

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
	 * The Window caching mechanism used by this Reader.
	 */
	protected final WindowCache cache;

	/**
	 * The last window acquired in this Reader using the
	 * {@link #getWindow(long)} method. Positions to read from are quite likely
	 * to be consecutive or close to the previous byte read from. Recording the
	 * last window therefore avoids the need to look it up in the cache if the
	 * required position is still inside the last Window.
	 */
	private Window lastWindow;

	/**
	 * Construct the Reader using a default window size, using the WindowCache
	 * provided.
	 * 
	 * @param cache
	 *            The WindowCache to use.
	 * @throws IllegalArgumentException
	 *             if the WindowCache is null.
	 */
	public AbstractReader(final WindowCache cache) {
		this(DEFAULT_WINDOW_SIZE, cache);
	}

	/**
	 * Constructs the Reader using the window size and window cache provided.
	 * 
	 * @param windowSize
	 *            The size of Window to use.
	 * @param cache
	 *            The WindowCache to use.
	 * @throws IllegalArgumentException
	 *             if the window size is less than one or the WindowCache is
	 *             null.
	 */
	public AbstractReader(final int windowSize, final WindowCache cache) {
		if (windowSize < 1) {
			throw new IllegalArgumentException(
					"Window size must be at least one.");
		}
		if (cache == null) {
			throw new IllegalArgumentException("Window cache cannot be null.");
		}
		this.windowSize = windowSize;
		this.cache = cache;
	}

	/**
	 * Reads a byte in the file at the given position.
	 * 
	 * @param position
	 *            The position in the reader to read a byte from.
	 * @return The byte at the given position (0-255), or a negative number if
	 *         there is no byte at the position specified.
	 * @throws IOException
	 *             if an error occurs reading the byte.
	 */
	@Override
	public int readByte(final long position) throws IOException {
		final Window window = getWindow(position);
		final int offset = (int) position % windowSize;
		if (window == null || offset >= window.length()) {
			return NO_BYTE_AT_POSITION;
		}
		return window.getByte(offset) & 0xFF;
	}

	/**
	 * Returns a window onto the data for a given position. The position does
	 * not have to be the beginning of a Window - but the Window returned must
	 * include that position (if such a position exists in the Reader).
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
		if (position >= 0) {
			Window window = null;
			final int offset = (int) position % windowSize;
			final long windowStart = position - offset;
			if (lastWindow != null
					&& lastWindow.getWindowPosition() == windowStart) {
				window = lastWindow;
			} else {
				window = cache.getWindow(windowStart);
				if (window != null) {
					lastWindow = window;
				} else {
					window = createWindow(windowStart);
					if (window != null) {
						lastWindow = window;
						cache.addWindow(window);
					}
				}
			}
			// Finally, if the position requested is outside the window limit,
			// don't return a window. The position itself is invalid, even
			// though
			// that position is part of a window which has valid positions.
			if (window != null && offset >= window.length()) {
				window = null;
			}
			return window;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Window> iterator() {
		return new WindowIterator();
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
		return (int) position % windowSize;
	}

	/**
	 * An abstract method which must create a {@link Window} for the position
	 * given. Returns null if a Window cannot be provided for the position
	 * provided.
	 * 
	 * @param windowStart
	 *            The position in the Reader at which the Window should begin.
	 * @return A Window beginning at the position given. If no Window can be
	 *         created at the position given (e.g. the position is negative, or
	 *         past the end of the underlying byte source), then this method
	 *         MUST return null.
	 * @throws IOException
	 *             If the Reader has an issue reading the bytes required for a
	 *             valid Window.
	 */
	abstract Window createWindow(final long windowStart) throws IOException;

	/**
	 * An iterator of {@link Window}s over a {@link Reader}.
	 */
	private class WindowIterator implements Iterator<Window> {

		private int position = 0;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			try {
				return getWindow(position) != null;
			} catch (final IOException ex) {
				return false;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Window next() {
			try {
				final Window window = getWindow(position);
				if (window != null) {
					position += windowSize;
					return window;
				}
			} catch (final IOException throwNoSuchElementExceptionInstead) {
			}
			throw new NoSuchElementException();
		}

		/**
		 * Always throws UnsupportedOperationException. It is not possible to
		 * remove a Window from a Reader.
		 * 
		 * @throws UnsupportedOperationException
		 *             Always throws this exception.
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException(
					"Cannot remove a window from a reader.");
		}
	}

}
