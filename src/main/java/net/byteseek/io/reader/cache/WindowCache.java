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

package net.byteseek.io.reader.cache;

import net.byteseek.io.reader.windows.Window;
import net.byteseek.io.reader.windows.WindowFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A interface for classes which cache {@link net.byteseek.io.reader.windows.Window} objects.
 * It also provides the ability to subscribe for notifications that a
 * Window object is leaving the cache, and to read bytes back from the cache into an external byte array,
 * to facilitate integration with other systems that use an InputStream style interface.
 * 
 * @author Matt Palmer
 */
public interface WindowCache {

    /**
     * Returns the {@link net.byteseek.io.reader.windows.Window} at the position specified.
     * The position must be one at which a Window object begins.  It will not return
     * a Window for a position which simply exists within a Window.  If no Window
     * exists in the cache at the exact position specified, then null is returned.
     * 
     * @param position The position at which a Window begins in the cache.
     * @return A Window for the specified starting position, or null if the Window does not exist.
     * @throws IOException if there was an IOException when getting the cached window.
     */
    Window getWindow(long position) throws IOException;

    /**
     * Adds a {@link net.byteseek.io.reader.windows.Window} to the cache.
     * 
     * @param window The Window to add to the cache.
     * @throws IOException if there was a problem adding a Window.
     */
    void addWindow(Window window) throws IOException;

    /**
     * Reads data held in the cache and copies it into the readInto byte array, up to the available bytes in the array.
     * Returns the number of bytes copied.
     * <p>
     * If the position requested is not in the cache (regardless of whether it is past the end or a negative position),
     * then the cache should not throw an exception, it should just return 0 bytes read.
     * <p>
     * @param windowPos The position of the window in the cache.
     * @param offset    The offset into the window to begin reading from.
     * @param readInto  The byte array to copy into.
     * @param readIntoPos The position in the byte array to start copying.
     * @return The number of bytes copied from the cache.
     * @throws IOException if there was a problem reading from the cache.
     */
    int read(long windowPos, int offset, byte[] readInto, int readIntoPos) throws IOException;

    /**
     * Reads data held in the cache and copies it into the readInto byte array, up to the maximum length or the available
     * bytes in the array. Returns the number of bytes copied.
     * <p>
     * If the position requested is not in the cache (regardless of whether it is past the end or a negative position),
     * then the cache should not throw an exception, it should just return 0 bytes read.
     * <p>
     * @param windowPos The position of the window in the cache.
     * @param offset    The offset into the window to begin reading from.
     * @param readInto  The byte array to copy into.
     * @param readIntoPos The position in the byte array to start copying.
     * @param maxLength      The maximum number of bytes to read.
     * @return The number of bytes copied from the cache.
     * @throws IOException if there was a problem reading from the cache.
     */
    int read(long windowPos, int offset, byte[] readInto, int readIntoPos, int maxLength) throws IOException;

    /**
     * Reads data held in the cache and copies it into the readInto ByteBuffer.
     * <p>>
     * A cache should read as much as it can into the ByteBuffer, even if this involves reading from several cached windows.
     * However, a cache does not have to do so if it is not possible to determine the next available window.
     * It is up to users of a cache to read from the next window if the availlable space has not been filled.
     * <p>
     * If the position requested is not in the cache (regardless of whether it is past the end or a negative position),
     * then the cache should not throw an exception, it should just return 0 bytes read.
     * Returns the number of bytes read into the buffer.
     *
     * @param windowPos The position of the window in the cache.
     * @param offset    The offset into the window to begin reading from.
     * @param readInto  The ByteBuffer to copy into.
     * @return          The number of bytes copied from the cache.
     * @throws IOException If there was a problem reading from the cache.
     */
    int read(long windowPos, int offset, ByteBuffer readInto) throws IOException;

    /**
     * Clears all {@link net.byteseek.io.reader.windows.Window}s from the cache.
     * @throws IOException if there was an IO problem clearing the cache.
     */
    void clear() throws IOException;

    /**
     * Subscribes a {@link WindowObserver} to this cache for notification when a
     * {@link net.byteseek.io.reader.windows.Window} leaves it.
     * 
     * @param observer The observer who wants notification that a Window is leaving the cache.
     */
    void subscribe(WindowObserver observer);

    /**
     * Unsubscribes a {@link WindowObserver} from this cache.
     * 
     * @param observer The observer who no longer wants notification that a Window is leaving the cache.
     * @return boolean True if the observer was unsubcribed from the cache.  If false, then it
     *                 is likely that the observer was never subscribed in the first place, or
     *                 has already been unsubscribed.
     */
    boolean unsubscribe(WindowObserver observer);

    /**
     * Sets the window factory the cache uses to create new windows.
     * <p>
     * Memory based caches do not typically create new windows,
     * but persistent storage caches usually need to create a new window in memory.
     *
     * @param factory The WindowFactory to use to create new Windows.
     */
    void setWindowFactory(WindowFactory factory);

    /**
     * An interface for objects which want notification when a {@link net.byteseek.io.reader.windows.Window}
     * is leaving a cache.
     */
    interface WindowObserver {
        
        /**
         * A method which is called on the WindowObserver when a {@link net.byteseek.io.reader.windows.Window}
         * leaves a cache.
         * @param window The Window which is leaving a cache.
         * @param fromCache The cache that the Window is leaving.
         * @throws IOException if an IOException occurs while freeing a window (which may involve adding a window to another cache).
         * 
         */
        void windowFree(Window window, WindowCache fromCache) throws IOException;
        
    }
    
}
