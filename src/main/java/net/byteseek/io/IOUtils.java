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

package net.byteseek.io;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * A static utility package containing some useful methods for reading and
 * writing bytes using InputStreams, OutputStreams, and RandomAccessFiles.
 * 
 * @author M\tt Palmer
 */
public final class IOUtils {

	/**
	 * Reads bytes from an {@link java.io.InputStream} into the byte array,
	 * until the byte array is filled or there are no more bytes in the stream.
	 * <p>
	 * Returns the total number of bytes read into the array.
	 * 
	 * @param input
	 *            The InputStream to read from.
	 * @param bytes
	 *            The byte array to fill.
	 * @return int The total number of bytes read.
	 * @throws IOException
	 *             If a problem occurs reading from the InputStream.
	 */
	public static int readBytes(final InputStream input, final byte[] bytes) throws IOException {
		final int blockSize = bytes.length;
		int totalRead = 0;
		while (totalRead < blockSize) {
			final int read = input.read(bytes, totalRead, blockSize - totalRead);
			if (read == -1) {
				break;
			}
			totalRead += read;
		}
		return totalRead;
	}

	/**
	 * Reads bytes from a {@link java.io.RandomAccessFile} into the byte array,
	 * starting from the current seek position of the RandomAccessFile, until
	 * the byte array is filled or there are no more bytes in the
	 * RandomAccessFile.
	 * <p>
	 * Returns the total number of bytes read into the array.
	 * 
	 * @param input
	 *            The RandomAccessFile to read from.
	 * @param bytes
	 *            The byte array to fill.
	 * @return int The total number of bytes read.
	 * @throws IOException
	 *             If a problem occurs reading from the RandomAccessFile.
	 */
	public static int readBytes(final RandomAccessFile input, final byte[] bytes) throws IOException {
		final int blockSize = bytes.length;
		int totalRead = 0;
		while (totalRead < blockSize) {
			final int read = input.read(bytes, totalRead, blockSize - totalRead);
			if (read == -1) {
				break;
			}
			totalRead += read;
		}
		return totalRead;
	}

	/**
	 * Reads bytes from a {@link java.io.RandomAccessFile} into the byte array,
	 * starting from the position provided in the RandomAccessFile, until the
	 * byte array is filled or there are no more bytes in the RandomAccessFile.
	 * <p>
	 * Returns the total number of bytes read into the array.
	 * 
	 * @param input The RandomAccessFile to read from.
	 * @param fromPosition The position to begin reading from in the RandomAccessFile.
	 * @param bytes The byte array to fill.
	 * @return int The total number of bytes read.
	 * @throws IOException If a problem occurs reading from the RandomAccessFile.
	 */
	public static int readBytes(final RandomAccessFile input, final long fromPosition, final byte[] bytes) throws IOException {
		final int blockSize = bytes.length;
		int totalRead = 0;
		input.seek(fromPosition);
		while (totalRead < blockSize) {
			final int read = input.read(bytes, totalRead, blockSize - totalRead);
			if (read == -1) {
				break;
			}
			totalRead += read;
		}
		return totalRead;
	}

	/**
	 * Read bytes from a file channel at a position into a ByteBuffer.
     *
	 * @param input The FileChannel to read from.
	 * @param fromPosition The position in the file channel to read from.
	 * @param bytes  The ByteBuffer to read into.
	 * @return The number of bytes read.
	 * @throws IOException If there was a problem reading from the FileChannel or into the ByteBuffer.
	 */
	public static int readBytes(final FileChannel input, final long fromPosition, final ByteBuffer bytes) throws IOException {
		final int bytesToRead = bytes.remaining();
		int totalRead = 0;
		input.position(fromPosition);
		while (totalRead < bytesToRead) {
			int bytesRead = input.read(bytes);
			if (bytesRead < 1) {
				break;
			}
			totalRead += bytesRead;
		}
		return totalRead;
	}

	/**
	 * Reads bytes from a {@link java.io.RandomAccessFile} into the byte array,
	 * starting from the position provided in the RandomAccessFile, until the
	 * byte array is filled or there are no more bytes in the RandomAccessFile.
	 * <p>
	 * Returns the total number of bytes read into the array.
	 *
	 * @param input The RandomAccessFile to read from.
	 * @param fromPosition The position to begin reading from in the RandomAccessFile.
	 * @param bytes The byte array to write to.
	 * @param bytePos The position in the array to write to.
	 * @param length  The number of bytes to read.
	 * @return int The total number of bytes read.
	 * @throws IOException If a problem occurs reading from the RandomAccessFile.
	 */
	public static int readBytes(final RandomAccessFile input, final long fromPosition,
							    final byte[] bytes, final int bytePos, final int length) throws IOException {
		final int availableArrayLength = bytes.length - bytePos;
		final int blockSize = length < availableArrayLength? length : availableArrayLength;
		int totalRead = 0;
		input.seek(fromPosition);
		while (totalRead < blockSize) {
			final int read = input.read(bytes, bytePos + totalRead, blockSize - totalRead);
			if (read == -1) {
				break;
			}
			totalRead += read;
		}
		return totalRead;
	}

	/**
	 * Creates a temporary file in the default temporary file area, with the
	 * filename prefix "byteseek" and a filename extension of ".tmp".
	 * 
	 * @return The temporary file created.
	 * @throws IOException
	 *             If an error occurs creating the temporary file.
	 */
	public static File createTempFile() throws IOException {
		return File.createTempFile("byteseek", ".tmp");
	}

	/**
	 * Creates a temporary file in the directory specified with the
	 * filename prefix "byteseek" and a filename extension of ".tmp".
	 *
	 * @param tempDir The directory to create the temporary file in, or null if you want the default temp dir location.
	 * @return The temporary file created.
	 * @throws IOException If an error occurs creating the temporary file, or the tempDir provided is not a directory.
	 */
	public static File createTempFile(final File tempDir) throws IOException {
		return File.createTempFile("byteseek", ".tmp", tempDir);
	}

}
