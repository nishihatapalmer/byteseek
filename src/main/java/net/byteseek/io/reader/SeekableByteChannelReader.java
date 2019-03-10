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

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import net.byteseek.io.reader.cache.LeastRecentlyUsedCache;
import net.byteseek.io.reader.cache.WindowCache;
import net.byteseek.io.reader.windows.*;
import net.byteseek.utils.ArgUtils;

/**
 * A WindowReader extending {@link AbstractCacheReader} which reads a SeekableByteChannel
 * into cached byte arrays.  It also implements the SoftWindowRecovery interface,
 * which allows windows to reload their byte arrays when using SoftWindows (as the
 * garbage collector may have re-claimed their array under low memory conditions
 * previously).
 * <p>
 * This class is not thread-safe.
 *
 * @author matt
 */
public final class SeekableByteChannelReader extends AbstractCacheReader implements SoftWindowRecovery {

    private final SeekableByteChannel channel;
    private final long length;
    private WindowFactory factory = HardWindow.FACTORY;

    /**
     * Constructs a SeekableByteChannelReader which defaults to an array size of 4096, caching
     * the last 32 most recently used Windows in a {@link net.byteseek.io.reader.cache.LeastRecentlyUsedCache}
     *
     * @param channel The channel to read from.
     * @throws IOException If there is a problem getting the channel size.
     * @throws IllegalArgumentException if the channel passed in is null.
     */
    public SeekableByteChannelReader(final SeekableByteChannel channel) throws IOException {
        this(channel, DEFAULT_WINDOW_SIZE, new LeastRecentlyUsedCache(DEFAULT_CAPACITY));
    }

    /**
     * Constructs a SeekableByteChannelReader which defaults to a {@link net.byteseek.io.reader.windows.Window} size of 4096
     * using the WindowCache passed in to cache ArrayWindows.
     *
     * @param channel The channel to read from.
     * @param cache The cache of Windows to use.
     * @throws IOException If there is a problem getting the channel size.
     * @throws IllegalArgumentException if the channel passed in is null.
     */
    public SeekableByteChannelReader(final SeekableByteChannel channel, final WindowCache cache)
       throws IOException {
        this(channel, DEFAULT_WINDOW_SIZE, cache);
    }

    /**
     * Constructs a SeekableByteChannelReader using the {@link net.byteseek.io.reader.windows.Window} size passed in, and
     * caches the last 32 Windows in a {@link net.byteseek.io.reader.cache.LeastRecentlyUsedCache}.
     *
     * @param channel The channel to read from.
     * @param windowSize The size of the byte array to read from the channel.
     * @throws IOException If there is a problem getting the channel size.
     * @throws IllegalArgumentException if the channel passed in is null.
     */
    public SeekableByteChannelReader(final SeekableByteChannel channel, final int windowSize)
         throws IOException {
        this(channel, windowSize, new LeastRecentlyUsedCache(DEFAULT_CAPACITY));
    }

    /**
     * Constructs a SeekableByteChannelReader using the array size passed in, and caches the
     * last most recently used Windows up to the capacity specified in a
     * {@link net.byteseek.io.reader.cache.LeastRecentlyUsedCache}.
     *
     * @param channel The channel to read from.
     * @param windowSize the size of the byte array to read from the channel.
     * @param capacity the number of byte arrays to cache (using a most recently used strategy).
     * @throws IOException If there is a problem getting the channel size.
     * @throws IllegalArgumentException if the channel passed in is null.
     */
    public SeekableByteChannelReader(final SeekableByteChannel channel, final int windowSize, final int capacity)
        throws IOException {
        this(channel, windowSize, new LeastRecentlyUsedCache(capacity));
    }

    public SeekableByteChannelReader(final Path path) throws IOException {
        this(path, DEFAULT_WINDOW_SIZE, new LeastRecentlyUsedCache(DEFAULT_CAPACITY));
    }

    public SeekableByteChannelReader(final Path path, final int windowSize) throws IOException {
        this(path, windowSize, new LeastRecentlyUsedCache(DEFAULT_CAPACITY));
    }

    public SeekableByteChannelReader(final Path path, final int windowSize, final int capacity) throws IOException {
        this(path, windowSize, new LeastRecentlyUsedCache(capacity));
    }

    public SeekableByteChannelReader(final Path path, final WindowCache cache) throws IOException {
        this(path, DEFAULT_WINDOW_SIZE, cache);
    }

    public SeekableByteChannelReader(final Path path, final int windowSize, final WindowCache cache) throws IOException {
        this(path == null? null : Files.newByteChannel(path), windowSize, cache);
    }

    /**
     * Constructs a SeekableByteChannelReader which reads the channel into {@link net.byteseek.io.reader.windows.Window}s of the
     * specified size, using the {@link WindowCache} supplied to cache them.
     *
     * @param channel The channel to read from.
     * @param windowSize The size of the byte array to read from the channel.
     * @param cache The cache of Windows to use.
     * @throws IOException If the size of the channel cannot be determined.
     * @throws IllegalArgumentException If the channel or cache passed in is null.
     */
    public SeekableByteChannelReader(final SeekableByteChannel channel, final int windowSize, final WindowCache cache)
        throws IOException {
        super(windowSize, cache);
        ArgUtils.checkNullObject(channel, "channel");
        this.channel = channel;
        length = channel.size();
    }

    /**
     * Returns the length of the channel.
     *
     * @return The length of the channel accessed by the reader.
     */
    @Override
    public final long length() {
        return length;
    }

    @Override
    public void setWindowFactory(final WindowFactory factory) {
        ArgUtils.checkNullObject(factory, "factory");
        this.factory = factory;
    }

    @Override
    protected int readWindowBytes(long windowStart, int windowOffset, byte[] readInto, int offset, int maxLength) throws IOException {
        final long channelPos = windowStart + windowOffset;
        channel.position(channelPos);
        final ByteBuffer arrayBuffer = ByteBuffer.wrap(readInto);
        arrayBuffer.position(offset);
        final int maxBytes = Math.min(readInto.length - offset, maxLength);
        //TODO: not sure about ByteBuffer logic here with limit()
        arrayBuffer.limit(offset + maxBytes);
        return channel.read(arrayBuffer);
    }

    @Override
    protected int readWindowBytes(long windowStart, int windowOffset, ByteBuffer buffer) throws IOException {
        final long channelPos = windowStart + windowOffset;
        channel.position(channelPos);
        return channel.read(buffer);
    }

    @Override
    protected Window createWindow(final long windowStart) throws IOException {
        if (windowStart >= 0) {
            //noinspection EmptyCatchBlock
            try {
                final ByteBuffer buffer = ByteBuffer.wrap(new byte[windowSize]);
                final int totalRead = channel.position(windowStart).read(buffer);
                if (totalRead > 0) {
                    return factory.createWindow(buffer.array(), windowStart, totalRead);
                }
            } catch (final EOFException justReturnNull) { // If we hit the end of the channel when reading, returning null is correct.
            }
        }
        return null;
    }

    /**
     * Closes the underlying {@link SeekableByteChannel}, then clears any
     * cache associated with this WindowReader.
     */
    @Override
    public void close() throws IOException {
        try {
            channel.close();
        } finally {
            super.close();
        }
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + "[channel:" + channel + " length: " + length + " cache:" + cache + ']';
    }

    @Override
    public byte[] reloadWindowBytes(final Window window) throws IOException {
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[windowSize]);
        channel.position(window.getWindowPosition()).read(buffer);
        return buffer.array();
    }

}
