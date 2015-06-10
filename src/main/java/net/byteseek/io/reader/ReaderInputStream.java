/*
 * Copyright Matt Palmer 2014, All rights reserved.
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
 * An InputStream backed by a WindowReader.
 */
public final class ReaderInputStream extends InputStream {

    private final WindowReader reader;

    private long   pos;
    private long   mark;
    private Window currentWindow;
    private int currentWindowLength;
    private byte[] currentArray;
    private int currentArrayPos;
    private boolean closeReaderOnClose;


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
        ArgUtils.checkNullObject(reader, "reader");
        this.reader = reader;
        this.closeReaderOnClose = closeReaderOnClose;
        setPos(0L);
    }

    @Override
    public synchronized int read() throws IOException {
        if (pos > -1) {
            final int readResult = currentArray[currentArrayPos++] & 0xFF;
            checkWindowPos();
            return readResult;
        }
        return -1;
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

        if (pos > -1) {
            int available = currentWindowLength - currentArrayPos;
            if (available >= len) { // buffer copy is completely inside current window.
                System.arraycopy(currentArray, currentArrayPos, b, off, len);
                currentArrayPos += len;
                checkWindowPos();
                return len;
            } else { // buffer copy may span more than one window (if there are more windows available...)
                int copied = 0;
                while (copied < len) {
                    System.arraycopy(currentArray, currentArrayPos, b, off + copied, available);
                    copied          += available;
                    currentArrayPos += available;
                    checkWindowPos();
                    if (currentWindow == null) { // no more windows available...
                        break;
                    }
                    final int remaining = len - copied;
                    available = currentWindowLength > remaining ? remaining : currentWindowLength;
                }
                return copied;
            }
        }
        return -1;
    }

    @Override
    public synchronized int available() throws IOException {
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
     * If an attempt is made to rewind to a previous window which is not cached, a {@link net.byteseek.io.reader.WindowMissingException}
     * is thrown on a call to {@link #reset()}.
     */
    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public synchronized void mark(int readAheadLimit) {
        mark = pos + currentArrayPos;
    }

    @Override
    public synchronized void reset() throws IOException {
       setPos(mark);
    }

    @Override
    public synchronized void close() throws IOException {
        if (closeReaderOnClose) {
            reader.close();
        }
    }

    private void setPos(final long newPos) throws IOException {
        currentWindow = reader.getWindow(newPos);
        if (currentWindow != null) {
            pos                 = newPos;
            currentWindowLength = currentWindow.length();
            currentArray        = currentWindow.getArray();
            currentArrayPos     = reader.getWindowOffset(newPos);
        } else {
            pos                 = -1;
            currentWindowLength = 0;
            currentArray        = null;
            currentArrayPos     = 0;
        }
    }

    private void checkWindowPos() throws IOException {
        if (currentArrayPos >= currentWindowLength) {
            pos += currentArrayPos;
            setPos(pos);
        }
    }

}
