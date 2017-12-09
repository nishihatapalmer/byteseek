/*
 * Copyright Matt Palmer 2014-17, All rights reserved.
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
import java.io.InputStream;

/**
 * An InputStream backed by a WindowReader.  It supports mark / reset behaviour (assuming the underlying
 * WindowReader is caching data for the earlier positions).
 */
public final class ReaderInputStream extends InputStream {

    private final static int END_OF_STREAM = -1;

    private final WindowReader reader;
    private final boolean closeReaderOnClose;
    private final boolean markSupported;

    private long   pos;
    private long   mark;

    /**
     * Constructs a ReaderInputStream from a WindowReader.  By default, the
     * underlying reader will not be closed when this input stream is closed.
     *
     * @param reader The WindowReader to back the InputStream.
     * @throws IOException If the ReaderInputStream cannot acquire a window for position 0.
     * @throws java.lang.IllegalArgumentException if the reader is null.
     */
    public ReaderInputStream(final WindowReader reader) throws IOException {
        this(reader, true);
    }

    /**
     * Constructs a ReaderInputStream from a WindowReader.
     *
     * @param reader The WindowReader to back the InputStream.
     * @param closeReaderOnClose Whether the underlying reader is closed when this input stream is closed.
     * @throws IOException If the ReaderInputStream cannot acquire a window for position 0.
     * @throws java.lang.IllegalArgumentException if the reader is null.
     */
    public ReaderInputStream(final WindowReader reader, boolean closeReaderOnClose) throws IOException {
        this(reader, closeReaderOnClose, true);
    }

    /**
     * Constructs a ReaderInputStream from a WindowReader.
     *
     * @param reader The WindowReader to back the InputStream.
     * @param closeReaderOnClose Whether the underlying reader is closed when this input stream is closed
     * @param markSupported Whether the stream will support mark() and reset().                          .
     * @throws IOException If the ReaderInputStream cannot acquire a window for position 0.
     * @throws java.lang.IllegalArgumentException if the reader is null.
     */
    public ReaderInputStream(final WindowReader reader, boolean closeReaderOnClose, boolean markSupported) throws IOException {
        ArgUtils.checkNullObject(reader, "reader");
        this.reader = reader;
        this.closeReaderOnClose = closeReaderOnClose;
        this.markSupported      = markSupported;
        pos = 0L;
    }

    @Override
    public synchronized int read() throws IOException {
        if (pos < 0) {
            return END_OF_STREAM;
        }
        int byteValue = reader.readByte(pos++);
        if (byteValue < 0) {
            pos = END_OF_STREAM;
        }
        return byteValue;
    }

    @Override
    public synchronized int read(final byte[] b, final int off, final int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }
        if (pos < 0) {
            return END_OF_STREAM;
        }
        final int copied = reader.read(pos, b, off, len);
        if (copied > 0) {
            pos += copied;
            return copied;
        } else {
            pos = END_OF_STREAM;
            return END_OF_STREAM;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>Implementation note</b>
     * This implementation will return 4096 if there is data available, and zero otherwise.
     * Programs should not rely on the available estimate.  There may not be 4096 bytes
     * available - but this is supposed to be an estimate not a precise quantity.  We pick
     * this value in case implementations use it as a hint for how large a buffer they may
     * need.
     *
     * @return 4096 if there is more data, zero if there is not.
     */
    @Override
    public synchronized int available() {
        return pos > END_OF_STREAM? 4096 : 0;
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>WARNING</b>
     * Mark <b>is only partially supported</b>, and <i>only if</i> the underlying WindowReader which backs this InputStream has a
     * caching mechanism which can rewind far enough.  This is not actually guaranteed, so you must be
     * sure that your WindowReader is caching enough to support mark and reset in this stream.
     * This method <b>always return true</b> (we cannot tell whether the WindowReader will genuinely cache enough).
     * <p>
     * If an attempt is made to rewind to a previous window which is not cached, a {@link net.byteseek.io.reader.windows.WindowMissingException}
     * is thrown on a call to {@link #reset()}.
     */
    @Override
    public boolean markSupported() {
        return markSupported;
    }

    @Override
    public synchronized void mark(int readAheadLimit) {
        mark = pos;
    }

    @Override
    public synchronized void reset() throws IOException {
       if (markSupported) {
           pos = mark;
       } else {
           super.reset(); // use default InputStream behaviour - throws an IO Exception.
       }
    }

    @Override
    public synchronized long skip(final long n) throws IOException {
        if (n <= 0 || pos < 0) {
            return 0;
        }
        final int hasByte = reader.readByte(pos + n); // check if there's a byte at this position.
        final long actualSkip;
        if (hasByte < 0) { // no byte at this position - we must have gone past the end, so the length will already be known.
            actualSkip = reader.length() - pos - 1;
            pos = END_OF_STREAM;
        } else {
            actualSkip = n;
            pos += n;
        }
        return actualSkip;
    }

    @Override
    public synchronized void close() throws IOException {
        if (closeReaderOnClose) {
            reader.close();
        }
        pos = END_OF_STREAM; // no more data.
    }

    /**
     * Returns the next read pos of the input stream reader.
     * This method is provided mostly for testing purposes, and it is package protected.
     * If we have read past the end of the stream, this value will be negative.
     * @return The next read position in the stream, or -1 if there are no more bytes to consume.
     */
    synchronized long getNextReadPos() {
        return pos;
    }

}
