/*
 * Copyright Matt Palmer 2012-17, All rights reserved.
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
import java.nio.charset.Charset;
import java.util.Arrays;

import net.byteseek.io.IOIterator;
import net.byteseek.io.reader.windows.HardWindow;
import net.byteseek.io.reader.windows.Window;
import net.byteseek.utils.ArgUtils;

/**
 * Provides a reader interface over an array of bytes.
 * <p>
 * If constructed from a byte array, the source array is not copied - the reader
 * just wraps the byte array passed in.
 * 
 * @author Matt Palmer
 */

public final class ByteArrayReader implements WindowReader {

	//TODO: allow this reader to operate over an Iterator of byte arrays, rather than being limited to a single byte array.
	//      max flexibility for users - if they happen to have the byte arrays, and can provide them in an order,
	//      this lets the reader interface take care of matching across them.

	//TODO: allow a start / end index to be specified on the supplied byte array.

	private static final int NO_BYTE_AT_POSITION = -1;

	private final Window windowBytes;

	/**
	 * Constructs a ByteArrayReader from an array of bytes.
	 * <p>
	 * The array passed in is not copied - the reader just wraps it to provide a
	 * reader interface over it.
	 * 
	 * @param bytes
	 *            The byte array to wrap in a reader interface.
	 */
	public ByteArrayReader(final byte[] bytes) {
		ArgUtils.checkNullObject(bytes, "bytes");
		this.windowBytes = new HardWindow(bytes, 0, bytes.length);
	}

	/**
	 * Constructs a ByteArrayReader from a single byte value.
	 * <p>
	 * A new array is created containing a single byte.
	 * 
	 * @param byteValue
	 *            The byte value to wrap in a WindowReader interface.
	 */
	public ByteArrayReader(final byte byteValue) {
		this.windowBytes = new HardWindow(new byte[] { byteValue }, 0, 1);
	}

	/**
	 * Constructs a ByteArrayReader from a {@link java.lang.String}, using the
	 * platform default {@link java.nio.charset.Charset} to encode the bytes of
	 * the String.
	 *
	 * @param string
	 *            The String to read using the platform specific charset encoding.
	 */
	public ByteArrayReader(final String string) {
		this(string, Charset.defaultCharset());
	}

	/**
	 * Constructs a ByteArrayReader from a {@link java.lang.String}, using the
	 * supplied {@link java.nio.charset.Charset} to encode the bytes of the
	 * String.
	 *
	 * @param string
	 *            The String to read
	 * @param charsetName
	 *            The name of the Charset to use when encoding the bytes of the String.
	 * @throws java.nio.charset.UnsupportedCharsetException If the charset name is not supported.
	 */
	public ByteArrayReader(final String string, final String charsetName) {
		this(string, Charset.forName(charsetName));
	}

	/**
	 * Constructs a ByteArrayReader from a {@link java.lang.String}, using the
	 * supplied {@link java.nio.charset.Charset} to encode the bytes of the
	 * String.
	 *
	 * @param string The string to read from.
	 * @param charset The charset to use to convert the string to bytes.
	 */
	public ByteArrayReader(final String string, final Charset charset) {
		ArgUtils.checkNullString(string, "string");
		ArgUtils.checkNullObject(charset, "charset");
		final byte[] bytes = string.getBytes(charset);
		this.windowBytes = new HardWindow(bytes, 0, bytes.length);
	}


	@Override
	public int readByte(final long position) throws IOException {
		return (position >= 0 && position < windowBytes.length())? windowBytes.getByte((int) position) & 0xFF : NO_BYTE_AT_POSITION;
	}

	@Override
	public int read(final long position, final byte[] readInto) throws IOException {
		return read(position, readInto, 0, readInto.length);
	}

	@Override
	public int read(final long position, final byte[] readInto, final int offset, final int readLength) throws IOException {
		final int windowLength = windowBytes.length();
		if (position < 0 || position >= windowLength) {
			return NO_BYTE_AT_POSITION;
		}

		// Calculate the amount it's safe to copy from the reader to the destination array:
		final int safeReadIntoLength = Math.min(readLength, readInto.length - offset);
        final int copyLength = Math.min(safeReadIntoLength, windowLength - (int) position);

        // Copy that amount of data over from the window to the readInto array, and return the amount copied:
        final byte[] array = windowBytes.getArray();
		System.arraycopy(array, (int) position, readInto, offset, copyLength);
		return copyLength;
	}

	@Override
	public Window getWindow(final long position) {
		return (position >= 0 && position < windowBytes.length())? windowBytes : null;
	}

	@Override
	public int getWindowOffset(final long position) {
		return windowBytes.length() > 0? (int) (position % (long) windowBytes.length()) : 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long length() {
		return windowBytes.length();
	}


	@Override
	public String toString() {
		return getClass().getSimpleName() + "[length:" + windowBytes.length() + ']';
	}

	@Override
	public void close() {
		// nothing to close.
	}

	@Override
	public IOIterator<Window> iterator() {
		return new WindowIterator(this);
	}
}
