/*
 * Copyright Matt Palmer 2018, All rights reserved.
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

import net.byteseek.utils.ArgUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.SeekableByteChannel;

/**
 * A class which adapts a WindowReader to the SeekableByteChannel interface.
 * It wraps an underlying WindowReader and maintains a position on it.
 * Data in the WindowReader can be read out using the read() methods.
 * Any attempt to write or truncate the data will result in a
 * NonWritableChannelException being thrown.
 * <p>
 * <b>Warning</b>
 * This class is not thread safe - do not create multiple instances over the
 * same WindowReader and run them in different threads.
 */
public final class WindowSeekableByteChannel implements SeekableByteChannel {

    private final static boolean CLOSE_READER_ON_CLOSE = true;
    private final WindowReader reader;
    private final boolean closeReaderOnClose;
    private boolean isClosed;
    private long position;

    /**
     * Constructs a WindowSeekableByteChannel given a WindowReader.
     *
     * @param reader The WindowReader to be adapted to the SeekableByteChannel interface.
     */
    public WindowSeekableByteChannel(final WindowReader reader) {
        this(reader, CLOSE_READER_ON_CLOSE);
    }

    /**
     * Constructs a WindowSeekableByteChannel given a WindowReader, and whether to
     * close the underlying reader when this SeekableByteChannel is closed.
     *
     * @param reader The WindowReader to be adapted to the SeekableByteChannel interface.
     * @param closeReaderOnClose if true, the underlying reader is closed when the WindowSeekableByteChannel is closed.
     */
    public WindowSeekableByteChannel(final WindowReader reader, final boolean closeReaderOnClose) {
        ArgUtils.checkNullObject(reader, "reader");
        this.reader = reader;
        this.closeReaderOnClose = closeReaderOnClose;
    }

    @Override
    public int read(final ByteBuffer dst) throws IOException {
        ensureOpen();
        final int bytesRead = reader.read(position, dst);
        // If at the end of the reader, -1 will be returned for bytesread.
        if (bytesRead > 0) {
            position += bytesRead;
        }
        return bytesRead;
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>Note</b>This always throws NonWritableChannelException - you cannot write to a WindowReader.
     * </p>
     */
    @Override
    public int write(ByteBuffer src) throws IOException {
        throw new NonWritableChannelException();
    }

    @Override
    public long position() throws IOException {
        ensureOpen();
        return position;
    }

    @Override
    public WindowSeekableByteChannel position(final long newPosition) throws IOException {
        ArgUtils.checkNotNegative(newPosition);
        ensureOpen();
        position = newPosition;
        return this;
    }

    @Override
    public long size() throws IOException {
        ensureOpen();
        return reader.length();
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>Note</b>This always throws NonWritableChannelException - you cannot truncate a WindowReader.
     * </p>
     */
    @Override
    public WindowSeekableByteChannel truncate(long size) throws IOException {
        throw new NonWritableChannelException();
    }

    @Override
    public boolean isOpen() {
        return !isClosed;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Does not close the underlying WindowReader when this channel closes.
     * More than one channel could wrap the same underlying WindowReader.
     */
    @Override
    public void close() throws IOException {
        isClosed = true;
        if (closeReaderOnClose) {
            reader.close();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(reader: " + reader + " position: " + position + " isClosed: " + isClosed + ')';
    }

    private void ensureOpen() throws ClosedChannelException {
        if (isClosed) {
            throw new ClosedChannelException();
        }
    }
}
