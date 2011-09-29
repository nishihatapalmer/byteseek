/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

import java.io.IOException;
import java.nio.charset.Charset;
import net.domesdaybook.reader.cache.NoCache;

/**
 *
 * @author matt
 */
public class StringReader extends AbstractReader {

    private final byte[] bytes;
    private final Charset charset;
    
    
    
    public StringReader(final String string) {
        this(string, Charset.defaultCharset());
    }

    
    
    public StringReader(final String string, final String charsetName) {
        this(string, Charset.forName(charsetName));
    }
    
    
    /**
     * Does not need a cache, as we will create a single window large enough to
     * store the entire string.  The AbstractReader already holds on to the last
     * Window created, or creates it if it's not already there.  So no further
     * caching is required.
     * 
     * @param string
     * @param charset 
     */
    public StringReader(final String string, final Charset charset) {
        super(string == null? 0 : string.length(), NoCache.NO_CACHE);        
        if (string == null) {
            throw new IllegalArgumentException("Null string passed in to StringReader.");
        }
        if (charset == null) {
            throw new IllegalArgumentException("Null charset passed in to StringReader.");
        }
        this.bytes = string.getBytes(charset);
        this.charset = charset;
    }
    
    
    @Override
    Window createWindow(final long windowStart) throws IOException {
        return new Window(bytes, 0, bytes.length);
    }

    
    @Override
    public long length() throws IOException {
        return bytes.length;
    }
    
    
    public String getString() {
        return new String(bytes, charset);
    }
    
    
    public Charset getCharset() {
        return charset;
    }
    
}
