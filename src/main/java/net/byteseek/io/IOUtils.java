/*
 * Copyright Matt Palmer 2011-2017, All rights reserved.
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
import java.util.ArrayList;
import java.util.List;

/**
 * A static utility package containing some useful methods for reading and
 * writing bytes using InputStreams, OutputStreams, and RandomAccessFiles.
 * 
 * @author M\tt Palmer
 */
public final class IOUtils {

	private static final int DEFAULT_BUFFER_SIZE = 4096;

	/**
	 * Private constructor to prevent instantiating a utility class.
	 */
	private IOUtils() {
	}

	/**
	 * Reads the bytes in a file into a single byte array and returns it.
	 *
	 * @param file The file to read.
	 * @return A byte array containing the file contents, of the same size as the file.
	 * @throws IOException If the file size is greater than INTEGER.MAX_VALUE, which is the
	 *                     largest byte array that can be created.  Also if the number of bytes
	 *                     read from the file does not match the size of the file, or if
	 *                     any other IOException occurs during opening or reading the file.
	 */
	public static byte[] readEntireFile(final File file) throws IOException {
		final long size = file.length();
		if (size > Integer.MAX_VALUE) {
			throw new IOException("File " + file + " of size " + size + " is greater than the maximum byte array size.");
		}
		final byte[] result = new byte[(int) size];
		final int read;
		try (FileInputStream fileStream = new FileInputStream(file)) {
			read = readBytes(fileStream, result);
		}
		if (read != size) {
			throw new IOException("ERROR: Only read " + read + " of " + size + " byte in file " + file);
		}
		return result;
	}

	/**
	 * Reads an input stream and returns a single array of bytes containing the stream contents.
	 * <p>
	 * The implementation builds a list of byte array buffers, then copies them into a single
	 * byte array.  This will use a bit more than twice as much memory as the length of the stream.
	 * <p>
	 * The input stream is not closed when finished reading - that remains the responsibility of the
	 * code which passes it in.
	 *
	 * @param input The input stream to read.
	 * @return A byte array containing the stream contents.
	 * @throws IOException If there is any problem reading from the InputStream.
	 */
	public static byte[] readEntireStream(final InputStream input) throws IOException {
		final int BUFFER_SIZE = 4096;

		// Read the stream into a list of byte buffers and calculate the length:
		// Only place byte arrays the size of the buffer on to the list; retain the
		// last buffer and how many bytes it represents.
		final List<byte[]> bufferList = new ArrayList<byte[]>();
		byte[] buffer = new byte[BUFFER_SIZE];
		int totalRead = 0;
		int bytesRead;
		while ((bytesRead = readBytes(input, buffer)) == BUFFER_SIZE) {
			totalRead += BUFFER_SIZE;
			bufferList.add(buffer);
			buffer = new byte[BUFFER_SIZE];
		}
		totalRead += bytesRead;

		// Write the byte buffers into a single byte array, now we know the length.
		// Process the list first, which are always the size of byte buffers,
		// then add the final buffer, which may be smaller than that.
		final byte[] result = new byte[totalRead];
		final int numBuffers = bufferList.size();
		int totalWritten = 0;
		for (int bufferIndex = 0; bufferIndex < numBuffers; bufferIndex++) {
			final byte[] bufferToWrite = bufferList.get(bufferIndex);
			System.arraycopy(bufferToWrite, 0, result, totalWritten, BUFFER_SIZE);
			totalWritten += BUFFER_SIZE;
		}
		System.arraycopy(buffer, 0, result, totalWritten, bytesRead);

		return result;
	}

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
	 * Writes the contents of an array of bytes into a
	 * {@link java.io.RandomAccessFile}.
	 * 
	 * @param output The RandomAccessFile to write the bytes into.
	 * @param bytes The array of bytes to be written.
	 * @param atPosition The position to write the bytes into.
	 * @throws IOException If a problem occurs writing the bytes into the RandomAccessFile.
	 */
	public static void writeBytes(final RandomAccessFile output, final byte[] bytes, final long atPosition) throws IOException {
		output.seek(atPosition);
		output.write(bytes);
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
	 * @param tempDir The directory to create the temporary file in.
	 * @return The temporary file created.
	 * @throws IOException If an error occurs creating the temporary file.
	 */
	public static File createTempFile(File tempDir) throws IOException {
		return File.createTempFile("byteseek", ".tmp", tempDir);
	}

	/**
	 * Creates a temporary file in the default temporary file area, with the
	 * filename prefix "byteseek" and a filename extension of ".tmp". The
	 * temporary file will have the contents of the {@link java.io.InputStream}
	 * written to it, using a default buffer size of 4096.
	 * 
	 * @param in
	 *            The InputStream to copy.
	 * @return A temporary file containing the contents of the InputStream.
	 * @throws IOException
	 *             If an error occurs creating the temporary file.
	 */
	public static File createTempFile(final InputStream in) throws IOException {
		return createTempFile(in, DEFAULT_BUFFER_SIZE);
	}

	/**
	 * Creates a temporary file in the default temporary file area, with the
	 * filename prefix "byteseek" and a filename extension of ".tmp". The
	 * temporary file will have the contents of the {@link java.io.InputStream}
	 * written to it, using the buffer size provided.
	 * 
	 * @param in
	 *            The InputStream to copy.
	 * @param bufferSize
	 *            The size of the buffer to use when copying the stream.
	 * @return A temporary file containing the contents of the InputStream.
	 * @throws IOException
	 *             If an error occurs creating the temporary file.
	 */
	public static File createTempFile(final InputStream in, final int bufferSize)
			throws IOException {
		final File tempFile = createTempFile();
		final FileOutputStream out = new FileOutputStream(tempFile);
		copyStream(in, out, bufferSize);
		out.close();
		return tempFile;
	}

	/**
	 * Copies the contents of an {@link java.io.InputStream} to an
	 * {@link java.io.OutputStream}, using the default buffer size of 4096.
	 * 
	 * @param in
	 *            The InputStream to copy.
	 * @param out
	 *            The OutputStream to copy to.
	 * @throws IOException
	 *             If a problem occurs copying the InputStream to the
	 *             OutputStream.
	 */
	public static void copyStream(final InputStream in, final OutputStream out)
			throws IOException {
		copyStream(in, out, DEFAULT_BUFFER_SIZE);
	}

	/**
	 * Copies the contents of an {@link java.io.InputStream} to an
	 * {@link java.io.OutputStream}, using buffer size provided.
	 * 
	 * @param in
	 *            The InputStream to copy.
	 * @param out
	 *            The OutputStream to copy to.
	 * @param bufferSize
	 *            The size of the buffer to use when copying the stream.
	 * @throws IOException
	 *             If a problem occurs copying the InputStream to the
	 *             OutputStream.
	 */
	public static void copyStream(final InputStream in, final OutputStream out,
			final int bufferSize) throws IOException {
		final byte[] buffer = new byte[bufferSize];
		int byteRead;
		while ((byteRead = in.read(buffer)) >= 0) {
			out.write(buffer, 0, byteRead);
		}
	}

}
