/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

import net.domesdaybook.reader.cache.WindowCache;
import net.domesdaybook.reader.cache.WindowAllCache;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author matt
 */
public class InputStreamReader extends AbstractReader {

    private final InputStream stream;
    private long streamPos = 0;
    private long length = UNKNOWN_LENGTH;
    
    
    public InputStreamReader(final InputStream stream) {
        this(stream, DEFAULT_WINDOW_SIZE);
    }
    
    
    public InputStreamReader(final InputStream stream, final WindowCache cache) {
        this(stream, DEFAULT_WINDOW_SIZE, cache);
    }      
    
    
    public InputStreamReader(final InputStream stream, final int windowSize) {
        this(stream, windowSize, new WindowAllCache());
    }

    
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
            // This runtime exception flags a programming error, in selecting an
            // innapropriate cache algorithm to use for the access needed.
            // A reader should always be able to return a window for a valid position,
            // setting aside genuine IO Exceptions.
            final String message = "Cache failed to provide a window at position: %d when we have already read past this position, currently at: %d";
            throw new CacheFailureException(String.format(message, position, streamPos));
        }
        return window;
    }
 
    
    @Override
    Window createWindow(final long readPos) throws IOException {
        Window lastWindow = null;
        while (readPos > streamPos && length == UNKNOWN_LENGTH) {
            final byte[] bytes = new byte[windowSize];
            final int totalRead = ReadUtils.readBytes(stream, bytes);
            if (totalRead > 0) {
                lastWindow = new Window(bytes, streamPos, totalRead);  
                streamPos += totalRead;                                        
                cache.addWindow(lastWindow);
            }
            if (totalRead < windowSize) { // If we read less than the available array:
                length = streamPos; // then the length is whatever the streampos is now.
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
    public void clearCache() {
        cache.clear();
        // The cache is the only representation of the stream we can replay,
        // so clearing the cache would seem to be pretty final.
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
