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

package net.byteseek.io;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import net.byteseek.io.cache.NoCache;

/**
 * A {@link WindowReader} which gives access to the bytes of a String, either using
 * the default platform encoding, or using a specific
 * {@link java.nio.charset.Charset}.
 * <p>
 * This WindowReader is thread-safe, as it is immutable.
 * 
 * @author Matt Palmer
 */
public class StringReader extends AbstractReader {

	private final byte[] bytes;
	private final Charset charset;

	/**
	 * Constructs a StringReader from a {@link java.lang.String}, using the
	 * platform default {@link java.nio.charset.Charset} to encode the bytes of
	 * the String.
	 * 
	 * @param string
	 *            The String to read using the platform specific charset
	 *            encoding.
	 */
	public StringReader(final String string) {
		this(string, Charset.defaultCharset());
	}

	/**
	 * Constructs a StringReader from a {@link java.lang.String}, using the
	 * supplied {@link java.nio.charset.Charset} to encode the bytes of the
	 * String.
	 * 
	 * @param string
	 *            The String to read
	 * @param charsetName
	 *            The name of the Charset to use when encoding the bytes of the
	 *            String.
	 * @throws UnsupportedCharsetException
	 *             If the charset name is not supported.
	 */
	public StringReader(final String string, final String charsetName) {
		this(string, Charset.forName(charsetName));
	}

	/**
	 * Does not need a cache, as we will create a single window large enough to
	 * store the entire string. The AbstractReader already holds on to the last
	 * Window created, or creates it if it's not already there. So no further
	 * caching is required.
	 * 
	 * @param string
	 * @param charset
	 */
	public StringReader(final String string, final Charset charset) {
		super(string == null ? 0 : string.length(), NoCache.NO_CACHE);
		if (string == null) {
			throw new IllegalArgumentException(
					"Null string passed in to StringReader.");
		}
		if (charset == null) {
			throw new IllegalArgumentException(
					"Null charset passed in to StringReader.");
		}
		bytes = string.getBytes(charset);
		this.charset = charset;
	}

	/**
	 * Creates a new Window every time it is called, consisting of a Window onto
	 * the entire byte array that encodes the String.
	 */
	@Override
	Window createWindow(final long windowStart) throws IOException {
		return new Window(bytes, 0, bytes.length);
	}

	/**
	 * Returns the length of the bytes that encode the String. This may not be
	 * the same length as the number of characters in the original String.
	 * <p>
	 * It will never throw an IOException, although other Readers may.
	 */
	@Override
	public long length() throws IOException {
		return bytes.length;
	}

	/**
	 * Returns a new String based on the byte encoding and Charset used.
	 * 
	 * @return A new String that replicates the original String used to
	 *         construct this WindowReader.
	 */
	public String getString() {
		return new String(bytes, charset);
	}

	/**
	 * Returns the {@link java.nio.charset.Charset} used to encode the bytes in
	 * this WindowReader.
	 * 
	 * @return The Charset used to encode the bytes in this WindowReader.
	 */
	public Charset getCharset() {
		return charset;
	}

}
