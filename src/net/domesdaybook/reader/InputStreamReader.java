/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author matt
 */
public class InputStreamReader implements Reader {

    private final static int DEFAULT_ARRAY_SIZE = 4096;
   
    private final int arraySize;
    private final InputStream stream;
    private final WindowCache cache;    
    private long streamPos = 0;
    private long length = -1;
    
    
    //TODO: provide new constructors for different settings.
    public InputStreamReader(final InputStream stream) {
        this.stream = stream;
        this.cache = new WindowCacheAll();
        arraySize = DEFAULT_ARRAY_SIZE;
    }
    
    
    @Override
    public byte readByte(final long position) throws ReaderException {
        final Window window = getWindow(position);
        if (window == null) {
            throw new ReaderException("No bytes can be read from this position:" + position);
        }
        return window.getByte((int) (position % arraySize));
    }

    
    @Override
    public Window getWindow(final long position) throws ReaderException {
        if (position >= 0) {
            final int blockSize = arraySize;
            final long readPos = (position / blockSize) * blockSize;
            Window window = cache.getWindow(readPos);            
            if (window == null) {
                window = createWindow(readPos);
            }
            return window;
        }
        return null;
    }
    
    
    private Window createWindow(final long readPos) {
        Window lastWindow = null;
        try {
            while (readPos > streamPos) {
                final byte[] bytes = new byte[arraySize];
                final int totalRead = ReadUtils.readBytes(stream, bytes);
                if (totalRead > 0) {
                    lastWindow = new Window(bytes, streamPos, totalRead);  
                    streamPos += totalRead;                                        
                    cache.addWindow(lastWindow);
                }
                if (totalRead < arraySize) { // If we read less than the available array:
                    length = streamPos;
                }
            }
        } catch (IOException io) {
            throw new ReaderException(io);
        }
        return lastWindow;
    }    

    
    @Override
    public long length() {
        try {
            while (length < 0) {
                final byte[] bytes = new byte[arraySize];
                final int totalRead = ReadUtils.readBytes(stream, bytes);
                if (totalRead > 0) {
                    Window lastWindow = new Window(bytes, streamPos, totalRead); 
                    streamPos += totalRead;                  
                    cache.addWindow(lastWindow);
                }
                if (totalRead < arraySize) { // If we read less than the available array:
                    length = streamPos;
                }
            }
        } catch (IOException io) {
            throw new ReaderException(io);
        }
        return length;
    }

    
    @Override
    public void clearCache() {
        // The cache is the only representation of the stream we can replay,
        // so we don't clear it, as it's not really a cache anymore.
    }

    
    @Override
    public void close() {
        try {
            stream.close();
        } catch (IOException canDoNothing) {
        }
    }
   
    
}
