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

package net.domesdaybook.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import net.domesdaybook.io.cache.WindowCache;

/**
 * A class which extends {@link FileReader} to create a temporary file from an
 * InputStream on construction, and to delete the temporary file when the WindowReader
 * is closed.
 * 
 * @author Matt Palmer
 */
public final class TempFileReader extends FileReader {

	/**
	 * Constructs a TempFileReader from an {@link java.io.InputStream), creating
	 * a temporary file with a filename prefix of "byteseek" and extension of
	 * ".tmp". The default buffer size of 4096 will be used to copy the
	 * InputStream into the temporary file.
	 * <p> A default window size of 4096 will be used when creating
	 * {@link Window} objects, and a default capacity of 32 will be used for the
	 * {@link net.domesdaybook.io.cache.MostRecentlyUsedCache}.
	 * 
	 * @param stream
	 *            The InputStream to copy into the temporary file.
	 * @throws IOException
	 *             If a problem occurs creating the temp file or copying the
	 *             contents of the InputStream into it.
	 */
	TempFileReader(final InputStream stream) throws IOException {
		this(IOUtils.createTempFile(stream));
	}

	/**
	 * Constructs a TempFileReader from an {@link java.io.InputStream), creating
	 * a temporary file with a filename prefix of "byteseek" and extension of
	 * ".tmp". The default buffer size of 4096 will be used to copy the
	 * InputStream into the temporary file.
	 * <p> The supplied window size will be used when creating {@link Window}
	 * objects. and a default capacity of 32 will be used for the
	 * {@link net.domesdaybook.io.cache.MostRecentlyUsedCache}.
	 * 
	 * @param stream
	 *            The InputStream to copy into the temporary file.
	 * @param windowSize
	 *            the size of Windows to create when accessing the WindowReader.
	 * @throws IOException
	 *             If a problem occurs creating the temp file or copying the
	 *             contents of the InputStream into it.
	 */
	TempFileReader(final InputStream stream, final int windowSize)
			throws IOException {
		this(IOUtils.createTempFile(stream), windowSize);
	}

	/**
	 * Constructs a TempFileReader from an {@link java.io.InputStream), creating
	 * a temporary file with a filename prefix of "byteseek" and extension of
	 * ".tmp". The default buffer size of 4096 will be used to copy the
	 * InputStream into the temporary file.
	 * <p> The supplied window size will be used when creating {@link Window}
	 * objects. and the supplied capacity will be used for the
	 * {@link net.domesdaybook.io.cache.MostRecentlyUsedCache}.
	 * 
	 * @param stream
	 *            The InputStream to copy into the temporary file.
	 * @param windowSize
	 *            the size of Windows to create when accessing the WindowReader.
	 * @param capacity
	 *            The maximum number of Windows to cache.
	 * @throws IOException
	 *             If a problem occurs creating the temp file or copying the
	 *             contents of the InputStream into it.
	 */
	TempFileReader(final InputStream stream, final int windowSize,
			final int capacity) throws IOException {
		this(IOUtils.createTempFile(stream), windowSize, capacity);
	}

	/**
	 * Constructs a TempFileReader from an {@link java.io.InputStream), creating
	 * a temporary file with a filename prefix of "byteseek" and extension of
	 * ".tmp". The default buffer size of 4096 will be used to copy the
	 * InputStream into the temporary file.
	 * <p> The supplied {@link net.domesdaybook.io.cache.WindowCache} will
	 * be used to cache {@link Window} objects. A default size of 4096 will be
	 * used to create the Windows.
	 * 
	 * @param stream
	 *            The InputStream to copy into the temporary file.
	 * @param cache
	 *            The WindowCache to use to cache Windows.
	 * @throws IOException
	 *             If a problem occurs creating the temp file or copying the
	 *             contents of the InputStream into it.
	 */
	TempFileReader(final InputStream stream, final WindowCache cache)
			throws IOException {
		this(IOUtils.createTempFile(stream), cache);
	}

	/**
	 * Constructs a TempFileReader from an {@link java.io.InputStream), creating
	 * a temporary file with a filename prefix of "byteseek" and extension of
	 * ".tmp". The default buffer size of 4096 will be used to copy the
	 * InputStream into the temporary file.
	 * <p> The supplied {@link net.domesdaybook.io.cache.WindowCache} will
	 * be used to cache {@link Window} objects. The supplied window size will be
	 * used to create the Windows.
	 * 
	 * @param stream
	 *            The InputStream to copy into the temporary file.
	 * @param windowSize
	 *            The size of the Windows to create when accessing the WindowReader.
	 * @param cache
	 *            The WindowCache to use to cache Windows.
	 * @throws IOException
	 *             If a problem occurs creating the temp file or copying the
	 *             contents of the InputStream into it.
	 */
	TempFileReader(final InputStream stream, final int windowSize,
			final WindowCache cache) throws IOException {
		this(IOUtils.createTempFile(stream), windowSize, cache);
	}

	private TempFileReader(final File tempFile) throws FileNotFoundException {
		super(tempFile);
	}

	private TempFileReader(final File tempFile, final int windowSize)
			throws FileNotFoundException {
		super(tempFile, windowSize);
	}

	private TempFileReader(final File tempFile, final int windowSize,
			final int capacity) throws FileNotFoundException {
		super(tempFile, windowSize, capacity);
	}

	private TempFileReader(final File tempFile, final WindowCache cache)
			throws FileNotFoundException {
		super(tempFile, cache);
	}

	private TempFileReader(final File tempFile, final int windowSize,
			final WindowCache cache) throws FileNotFoundException {
		super(tempFile, windowSize, cache);
	}

	/**
	 * Closes the underlying RandomAccessFile backing this TempFileReader, and
	 * clears any cache associated with it. It then attempts to delete the
	 * temporary file.
	 * 
	 * @throws IOException
	 *             If the temporary file could not be closed or deleted.
	 */
	@Override
	public void close() throws IOException {
		boolean fileDeleted = false;
		final File file = getFile();
		try {
			super.close(); // ensure the inherited random access file is closed
							// first
		} finally {
			fileDeleted = file.delete();
		}
		if (!fileDeleted) {
			throw new IOException("Could not delete the temporary file:"
					+ file.getAbsolutePath());
		}
	}

}
