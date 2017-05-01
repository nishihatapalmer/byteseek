/*
 * Copyright Matt Palmer 2012, All rights reserved.
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

import net.byteseek.io.reader.cache.NoCache;
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
public class ByteArrayReader extends AbstractReader {

	private final byte[] bytes;

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
		super(bytes == null ? 0 : bytes.length, NoCache.NO_CACHE);
		ArgUtils.checkNullObject(bytes, "bytes");
		this.bytes = bytes;
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
		super(1, NoCache.NO_CACHE);
		bytes = new byte[] { byteValue };
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
		super(string == null ? 0 : string.length(), NoCache.NO_CACHE);
		ArgUtils.checkNullString(string, "string");
		ArgUtils.checkNullObject(charset, "charset");
		bytes = string.getBytes(charset);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Window createWindow(final long windowStart) throws IOException {
		return windowStart == 0? new HardWindow(bytes, 0, bytes.length) : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long length() throws IOException {
		return bytes.length;
	}

	/**
	 * Returns the byte array backing this ByteArrayReader.
	 * <p>
	 * While this exposes mutable state, the intention of this class is to wrap
	 * a byte array in a WindowReader interface, not to protect the byte array wrapped
	 * by it.
	 * 
	 * @return The byte array that this reader wraps.
	 */
	public byte[] getByteArray() {
		return bytes;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[length:" + bytes.length + ']';
	}

}
