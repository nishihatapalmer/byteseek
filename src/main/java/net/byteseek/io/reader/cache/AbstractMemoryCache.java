/*
 * Copyright Matt Palmer 2017-2019, All rights reserved.
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

package net.byteseek.io.reader.cache;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.byteseek.io.reader.windows.Window;
import net.byteseek.io.reader.windows.WindowFactory;
import net.byteseek.utils.ArgUtils;

/**
 * This AbstractMemoryCache provides a standard read() implementation for all caches which keep their data in memory.
 * There is no great penalty for fetching a window already held in memory, so it just uses getWindow() to get all the
 * windows which are available.  Caches which do not already hold the Window in memory should not use this method,
 * as it will create a new Window object before copying it into the readInto array.  These caches should attempt to
 * copy their cached bytes directly into the readInto array rather than creating Windows unnecessarily.
 *
 * @author Matt Palmer
 */
public abstract class AbstractMemoryCache extends AbstractCache {

    @Override
    public int read(final long windowPos, final int offset, final byte[] readInto, final int readIntoPos,
                    final int maxLength) throws IOException {
        ArgUtils.checkIndexOutOfBounds(readInto.length, readIntoPos);
        final int bytesRequested = Math.min(readInto.length - readIntoPos, maxLength);
        if (bytesRequested > 0) {
            Window window = getWindow(windowPos);
            final int windowLength = window.length();
            if (window != null && offset < windowLength) { // If there's data at this position, copy it:
                int bytesToCopy = Math.min(bytesRequested, windowLength - offset);
                System.arraycopy(window.getArray(), offset, readInto, readIntoPos, bytesToCopy);

                // Now obtain more data if required from subsequent cached windows (all at offset 0 now)
                int writePosition = readIntoPos + bytesToCopy;
                final int writeLength = readIntoPos + bytesRequested;
                while (writePosition < writeLength &&
                      (window = getWindow(window.getNextWindowPosition())) != null) { // and another window to copy from:
                    bytesToCopy = Math.min(writeLength - writePosition, window.length());
                    System.arraycopy(window.getArray(), 0, readInto, writePosition, bytesToCopy);
                    writePosition += bytesToCopy;
                }
                return writePosition - readIntoPos;
            }
        }
        return 0;
    }

    @Override
    public int read(final long windowPos, final int offset, final ByteBuffer readInto) throws IOException {
        final int startRemaining = readInto.remaining();
        int bufferRemaining = startRemaining;
        if (bufferRemaining > 0) {
            Window window = getWindow(windowPos);
            if (window != null && offset < window.length()) {
                int bytesToCopy = Math.min(bufferRemaining, window.length() - offset);
                readInto.put(window.getArray(), offset, bytesToCopy);
                while ((bufferRemaining -= bytesToCopy) > 0 &&
                        (window = getWindow(window.getNextWindowPosition())) != null) {
                    bytesToCopy = Math.min(bufferRemaining, window.length());
                    readInto.put(window.getArray(), 0, bytesToCopy);
                }
            }
        }
        return startRemaining - bufferRemaining;
    }

    @Override
    public void setWindowFactory(final WindowFactory factory) {
        // memory based caches do not need to create new windows in memory.
    }

}
