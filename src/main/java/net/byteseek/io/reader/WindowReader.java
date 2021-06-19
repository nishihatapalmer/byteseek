/*
 * Copyright Matt Palmer 2009-2019, All rights reserved.
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

import net.byteseek.io.IOIterator;
import net.byteseek.io.reader.windows.Window;
import net.byteseek.io.reader.windows.WindowFactory;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * An interface for random access to bytes from an underlying byte source.
 * <p>
 * The interface supports four usage models:
 * <p>
 * <ul>
 * <li>Read a single byte at a given position
 * <li>Read bytes into a supplied array or ByteBuffer from a given position.
 * <li>Get a {@link net.byteseek.io.reader.windows.Window} onto the underlying byte source for a given
 * position.
 * <li>Get the bytes from the byte source, either as a single byte array (if it will fit), or as
 * an iterator over a sequence or byte arrays.</li>
 * </ul>
 *
 * @author Matt Palmer
 */
public interface WindowReader extends Closeable {

    /**
     * Read a byte from a given position.
     *
     * @param position The position of the byte to read.
     * @return int The byte value at the position given as an integer (0-255) If
     * there is no byte at the position, it returns -1.
     * @throws IOException if there was a problem reading the byte.
     */
    int readByte(long position) throws IOException;

    /**
     * Reads the bytes in the position given in the WindowReader directly into the supplied byte array,
     * reading up to the length of the array.  It returns the number of bytes read.
     *
     * @param position The position in the reader to read from.
     * @param readInto A byte array into which the data will be written.
     * @return The number of bytes read.
     * @throws IOException If there was a problem reading the data.
     */
    int read(long position, byte[] readInto) throws IOException;

    /**
     * Reads the bytes in the position given in the WindowReader directly into the supplied byte array,
     * at the offset given, reading at most the readLength, but potentially up to the length of the array.
     * It returns the number of bytes read, or -1 if trying to read past the end of the data source.
     *
     * @param position   The position in the reader to read from.
     * @param readInto   A byte array into which the data will be written.
     * @param offset     The offset into the byte array to start writing.
     * @param readLength The number of bytes to read into the array.
     * @return The number of bytes read, or -1 if trying to read past the end of the data source.
     * @throws IOException If there was a problem reading the data.
     */
    int read(long position, byte[] readInto, int offset, int readLength) throws IOException;

    /**
     * Reads the bytes in the position given in the WindowReader directly into the supplied ByteBuffer,
     * up to the remaining bytes in the ByteBuffer.
     * It returns the number of bytes read, or -1 if trying to read past the end of the data source.
     *
     * @param position The position in the reader to read from.
     * @param buffer   A ByteBuffer into which the data will be written.
     * @return The number of bytes read, or -1 if trying to read past the end of the data source.
     * @throws IOException If there was a problem reading or writing the data.
     */
    int read(long position, ByteBuffer buffer) throws IOException;

    /**
     * Returns a {@link net.byteseek.io.reader.windows.Window} for the given position.
     * <p>
     * The Window does not have to begin at the position specified; the Window
     * only needs to contain a byte at the position requested. Use
     * getWindowOffset() to determine the position of the byte in the Window.
     * <p>
     * A Window must only be returned if there is a legitimate byte for the
     * position requested, otherwise null must be returned. Any position less
     * than zero, or greater than or equal to the length of the reader MUST
     * return a null window,
     *
     * @param position The position of the byte to read in the underlying data.
     * @return Window an Window containing a byte array, and a startPos which
     * gives the position of the byte in the byte array.
     * @throws IOException if there was a problem reading byte into the Window.
     */
    Window getWindow(long position) throws IOException;

    /**
     * Returns the offset into a {@link Window} for a given position.
     *
     * @param position The position which you want the Window offset of.
     * @return The offset into a Window matching the position given.
     */
    int getWindowOffset(long position);

    /**
     * Returns the total length of the bytes provided by this WindowReader.
     *
     * @return long the length of the byte source accessed by the reader.
     * @throws IOException If a problem occurred trying to determine the length.
     */
    long length() throws IOException;

    /**
     * Returns true if the reader is closed.
     * A reader should throw an IOException if an attempt is made to access data when closed.
     *
     * @return true if the reader is closed.
     */
    boolean isClosed();

    /**
     * Returns an IO iterator over the Windows in the Reader.  This has the same semantics
     * as a normal Java Iterator, but any method call to it can throw an IOException.
     *
     * @return an IO iterator over the Windows in the Reader.
     */
    IOIterator<Window> windows();

    /**
     * Returns an IO iterator over all the bytes in the Reader as a sequence of byte arrays.
     *
     * @return an IO iterator over all the bytes in the Reader as a sequence of byte arrays.
     * @throws IOException If the reader is closed.
     */
    IOIterator<byte[]> bytes() throws IOException;

    /**
     * Returns an IO iterator over all the bytes in the Reader from the given position as a sequence of byte arrays.
     *
     * @return an IO iterator over all the bytes in the Reader from the given position as a sequence of byte arrays.
     * @throws IOException If the reader is closed.
     */
    IOIterator<byte[]> bytes(long fromPosition) throws IOException;

    /**
     * Returns an IO iterator over all the bytes in the Reader from the given position to the given position
     * as a sequence of byte arrays, up to the amount of data available in the reader.
     *
     * @return an IO iterator over all the bytes in the Reader from the given position to the given position as a sequence of byte arrays.
     * @throws IOException If the reader is closed.
     */
    IOIterator<byte[]> bytes(long fromPosition, long toPosition) throws IOException;

    /**
     * Returns a single byte array containing all the bytes in the reader from the given position to the given position,
     * up to the amount of data available in the reader.
     * <p><b>Warning!</b> If you request more data than can fit into a single byte array (Integer.MAX_VALUE),
     * then you will get an IllegalArgumentException.
     *
     * @param fromPosition The first position from which you want data.
     * @param toPosition The last position from which you want data.
     * @return A byte array containing all the data in the reader from the given position to the given position.
     * @throws IOException If the reader is closed, or there is a problem reading any of the bytes.
     * @throws IllegalArgumentException if the to position is smaller than the from position,
     *                                  or if the amount of data requested exceeds Integer.MAX_VALUE, as that cannot fit into a byte array.
     */
    byte[] allBytes(long fromPosition, long toPosition) throws IOException;

    /**
     * Sets the window factory the WindowReader uses to create new windows.
     *
     * @param factory The WindowFactory to use to create new Windows.
     */
    void setWindowFactory(WindowFactory factory);

}
