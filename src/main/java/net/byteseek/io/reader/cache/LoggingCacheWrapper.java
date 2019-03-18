/*
 * Copyright Matt Palmer 2019, All rights reserved.
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

import net.byteseek.io.reader.windows.Window;
import net.byteseek.io.reader.windows.WindowFactory;
import net.byteseek.utils.ArgUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A cache wrapper that just logs the method calls, parameters and return values for an underlying wrapped cache.
 * This may be useful to explore access patterns in data, to decide what the most appropriate cache strategies
 * to use are.
 */
public final class LoggingCacheWrapper implements WindowCache {

    private static final Logger logger = Logger.getLogger("net.byteseek.io.reader.cache.LoggingCacheWrapper");
    private final WindowCache wrappedCache;

    public LoggingCacheWrapper(final WindowCache cache) {
        ArgUtils.checkNullObject(cache, "cache");
        wrappedCache = cache;
    }

    @Override
    public Window getWindow(final long position) throws IOException {
        final String description = "getWindow()\t" + position;
        try {
            final Window window = wrappedCache.getWindow(position);
            logInfo(description + "\treturns:\t" + window);
            return window;
        } catch(IOException ex) {
            logError(description, ex);
            throw ex;
        }
    }

    @Override
    public void addWindow(final Window window) throws IOException {
        final String description = "addWindow()\t" + window;
        try {
            logInfo(description);
            wrappedCache.addWindow(window);
        } catch(IOException ex) {
            logError(description, ex);
            throw ex;
        }
    }

    @Override
    public int read(long windowPos, int offset, byte[] readInto, int readIntoPos) throws IOException {
        final String description = "readToArray()\twindowPos:\t" + windowPos + "\toffset:\t" + offset + "\treadIntoPos:\t" + readIntoPos;
        try {
            final int bytesRead = wrappedCache.read(windowPos, offset, readInto, readIntoPos);
            logInfo(description + "\treturns:\t" + bytesRead);
            return bytesRead;
        } catch(IOException ex) {
            logError(description, ex);
            throw ex;
        }
    }

    @Override
    public int read(long windowPos, int offset, byte[] readInto, int readIntoPos, int maxLength) throws IOException {
        final String description = "readToArrayMaxLength()\twindowPos:\t" + windowPos + "\toffset:\t" + offset + "\treadIntoPos:\t" + readIntoPos + "\tmaxLength:\t" + maxLength;
        try {
            final int bytesRead = wrappedCache.read(windowPos, offset, readInto, readIntoPos, maxLength);
            logInfo(description + "\treturns:\t" + bytesRead);
            return bytesRead;
        } catch(IOException ex) {
            logError(description, ex);
            throw ex;
        }
    }

    @Override
    public int read(long windowPos, int offset, ByteBuffer readInto) throws IOException {
        final String description = "readToByteBuffer()\twindowPos:\t" + windowPos + "\toffset:\t" + offset;
        try {
            final int bytesRead = wrappedCache.read(windowPos, offset, readInto);
            logInfo(description + "\treturns:\t" + bytesRead);
            return bytesRead;
        } catch(IOException ex) {
            logError(description, ex);
            throw ex;
        }
    }

    @Override
    public void clear() throws IOException {
        final String description = "clear()";
        try {
            logInfo(description);
            wrappedCache.clear();
        } catch(IOException ex) {
            logError(description, ex);
            throw ex;
        }
    }

    @Override
    public void subscribe(WindowObserver observer) {
        final String description = "subscribe()\tobserver:\t" + observer;
        logInfo(description);
        wrappedCache.subscribe(observer);
    }

    @Override
    public boolean unsubscribe(WindowObserver observer) {
        final String description = "unsubscribe()\tobserver:\t" + observer;
        final boolean unsubscribed = wrappedCache.unsubscribe(observer);
        logInfo(description + "\treturns:\t" + unsubscribed);
        return unsubscribed;
    }

    @Override
    public void setWindowFactory(WindowFactory factory) {
        final String description = "setWindowFactory()\tfactory:\t" + factory;
        logInfo(description);
    }

    private void logInfo(final String message) {
        logger.log(Level.INFO, "logging cache:\t" + this + "\twrapped cache:\t" + wrappedCache + "\t" + message);
    }

    private void logError(final String message, Throwable thrown) {
        logger.log(Level.SEVERE, "logging cache:\t" + this + "\twrapped cache:\t" + wrappedCache + "\t" + message, thrown);
    }
}
