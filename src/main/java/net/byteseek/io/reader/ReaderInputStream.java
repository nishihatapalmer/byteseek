/*
 * Copyright Matt Palmer 2014-15, All rights reserved.
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

import net.byteseek.io.reader.windows.Window;
import net.byteseek.utils.ArgUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * An InputStream backed by a WindowReader.
 */
public final class ReaderInputStream extends InputStream {

    private final WindowReader reader;
    private final boolean closeReaderOnClose;
    private final boolean markSupported;

    private long   pos;
    private long   mark;
    private Window currentWindow;
    private int currentWindowLength;
    private byte[] currentArray;
    private int currentArrayPos;


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
        setWindowForPosition(0L);
    }


    @Override
    public synchronized int read() throws IOException {
        if (pos < 0) {
            return -1;
        }

        final int readResult = currentArray[currentArrayPos] & 0xFF;
        addStreamPosition(1);
        return readResult;
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
            return -1;
        }

        int available = currentWindowLength - currentArrayPos;
        if (available >= len) { // buffer copy is completely inside current window.
            System.arraycopy(currentArray, currentArrayPos, b, off, len);
            addStreamPosition(len);
            return len;
        } else { // buffer copy may span more than one window (if there are more windows available...)
            int copied = 0;
            while (copied < len) {
                System.arraycopy(currentArray, currentArrayPos, b, off + copied, available);
                copied          += available;
                addStreamPosition(available);
                if (currentWindow == null) { // no more windows available...
                    break;
                }
                final int remaining = len - copied;
                available = currentWindowLength > remaining ? remaining : currentWindowLength;
            }
            return copied;
        }
    }

    @Override
    public synchronized int available() {
        return pos > -1? currentWindowLength - currentArrayPos : 0;
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
           setWindowForPosition(mark);
       } else {
           super.reset(); // use default InputStream behaviour - throws an IO Exception.
       }
    }

    @Override
    public synchronized long skip(long n) throws IOException {
        if (n <= 0 || pos < 0) {
            return 0;
        }
        final long oldPos = pos;
        setWindowForPosition(pos + n);
        return currentWindow == null ? reader.length() - oldPos : n;
    }

    @Override
    public synchronized void close() throws IOException {
        if (closeReaderOnClose) {
            reader.close();
            setNoMoreData();
        }
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

    private void setWindowForPosition(final long newPos) throws IOException {
        currentWindow = reader.getWindow(newPos);
        if (currentWindow == null) {
            setNoMoreData();
        } else {
            pos                 = newPos;
            currentWindowLength = currentWindow.length();
            currentArray        = currentWindow.getArray();
            currentArrayPos     = reader.getWindowOffset(newPos);
        }
    }

    private void addStreamPosition(int moveBy) throws IOException {
        currentArrayPos += moveBy;
        pos             += moveBy;
        if (currentArrayPos >= currentWindowLength) {
            setWindowForPosition(pos);
        }
    }

    private void setNoMoreData() {
        pos                 = -1;
        currentWindowLength = 0;
        currentArray        = null;
        currentArrayPos     = 0;
    }

}
