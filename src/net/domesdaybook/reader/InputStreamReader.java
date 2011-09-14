/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author matt
 */
public class InputStreamReader extends AbstractReader {

    private final InputStream stream;
    private long streamPos = 0;
    private long length = -1;
    
    
    public InputStreamReader(final InputStream stream) {
        this(stream, DEFAULT_ARRAY_SIZE);
    }
    
    
    public InputStreamReader(final InputStream stream, final WindowCache cache) {
        this(stream, DEFAULT_ARRAY_SIZE, cache);
    }      
    
    
    public InputStreamReader(final InputStream stream, final int arraySize) {
        this(stream, arraySize, new WindowAllCache());
    }

    
    public InputStreamReader(final InputStream stream, final int arraySize, final WindowCache cache) { 
        super(arraySize, cache);
        this.stream = stream;
    }    
    
 
    @Override
    Window createWindow(final long readPos) {
        Window lastWindow = null;
        try {
            while (readPos > streamPos) {
                final byte[] bytes = new byte[arraySize];
                final int totalRead = ReadUtils.readBytes(stream, bytes);
                if (totalRead > 0) {
                    lastWindow = new Window(bytes, streamPos, totalRead);  
                    streamPos += totalRead;                                        
                    cache.addWindow(lastWindow);
                } else if (totalRead <= 0) { // If no further bytes can be read
                    streamPos++; // advance the stream pos past the last byte.
                }
                if (totalRead < arraySize) { // If we read less than the available array:
                    length = streamPos; // then the length is whatever the streampos is now.
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
                    final Window lastWindow = new Window(bytes, streamPos, totalRead); 
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
        super.close();
        try {
            stream.close();
        } catch (IOException canDoNothing) {
        }
    }

   
    
}
