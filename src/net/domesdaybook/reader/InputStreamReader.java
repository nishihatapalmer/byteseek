/*
 * Copyright Matt Palmer 2011, All rights reserved.
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

package net.domesdaybook.reader;

import net.domesdaybook.reader.windowcache.CacheFailureException;
import net.domesdaybook.reader.windowcache.WindowCache;
import java.io.IOException;
import java.io.InputStream;
import net.domesdaybook.reader.windowcache.MostRecentlyUsedCache;
import net.domesdaybook.reader.windowcache.TempFileCache;
import net.domesdaybook.reader.windowcache.TwoLevelCache;

/**
 *
 * @author Matt Palmer
 */
public class InputStreamReader extends AbstractReader {

    private final InputStream stream;
    private long streamPos = 0;
    private long length = UNKNOWN_LENGTH;
    
    
    /**
     * 
     * @param stream
     */
    public InputStreamReader(final InputStream stream) {
        this(stream, DEFAULT_WINDOW_SIZE, DEFAULT_CAPACITY);
    }
    
    
    /**
     * 
     * @param stream
     * @param cache
     */
    public InputStreamReader(final InputStream stream, final WindowCache cache) {
        this(stream, DEFAULT_WINDOW_SIZE, cache);
    }      
    
    
    /**
     * 
     * @param stream
     * @param windowSize
     */
    public InputStreamReader(final InputStream stream, final int windowSize) {
        this(stream, windowSize, DEFAULT_CAPACITY);
    }

    
    /**
     * 
     * @param stream
     * @param windowSize
     * @param capacity 
     */
    public InputStreamReader(final InputStream stream, final int windowSize, final int capacity) {
        this(stream, windowSize, 
             TwoLevelCache.create(new MostRecentlyUsedCache(capacity),
                                  new TempFileCache(windowSize)));
    }
    
    
    /**
     * 
     * @param stream
     * @param windowSize
     * @param cache
     */
    public InputStreamReader(final InputStream stream, final int windowSize, final WindowCache cache) { 
        super(windowSize, cache);
        this.stream = stream;
    }    
    
    
    @Override
    public final Window getWindow(final long position) 
            throws IOException, CacheFailureException {
        final Window window = super.getWindow(position);
        if (window == null && position < streamPos && position >= 0) {
            // No window was returned, but the position requested has already
            // been read. This means the cache algorithm selected to use with
            // this reader cannot return an earlier position, and being a stream,
            // we can't rewind to read it again.  There is nothing which can be
            // done at this point other than to throw an exception.
            final String message = "Cache failed to provide a window at position: %d when we have already read past this position, currently at: %d";
            throw new CacheFailureException(String.format(message, position, streamPos));
        }
        return window;
    }
 
    
    @Override
    protected Window createWindow(final long readPos) throws IOException {
        Window lastWindow = null;
        while (readPos > streamPos && length == UNKNOWN_LENGTH) {
            final byte[] bytes = new byte[windowSize];
            final int totalRead = ReadUtils.readBytes(stream, bytes);
            if (totalRead > 0) {
                lastWindow = new Window(bytes, streamPos, totalRead);  
                streamPos += totalRead;                                        
            }
            if (totalRead < windowSize) { // If we read less than the available array:
                length = streamPos;       // then the length is whatever the streampos is now.
            }
            if (readPos <= streamPos) {      // If we still haven't reached the window
                cache.addWindow(lastWindow); // for the requested position, cache it.
            }
        }
        return lastWindow;
    }    

    
    @Override
    public long length() throws IOException {
        while (length == UNKNOWN_LENGTH) {
            final byte[] bytes = new byte[windowSize];
            final int totalRead = ReadUtils.readBytes(stream, bytes);
            if (totalRead > 0) {
                final Window lastWindow = new Window(bytes, streamPos, totalRead); 
                streamPos += totalRead;                  
                cache.addWindow(lastWindow);
            }
            if (totalRead < windowSize) { // If we read less than the available array:
                length = streamPos;
            }
        }
        return length;
    }

    
    @Override
    public void close() throws IOException {
        try {
            stream.close();
        } finally {
            super.close();
        }
    }

    
}
