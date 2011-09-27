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

    private final byte[] string;
    private final Charset charset;
    
    public StringReader(final String string) {
        this(string, Charset.defaultCharset());
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
        this.string = string.getBytes(charset);
        this.charset = charset;
    }
    
    
    @Override
    Window createWindow(final long windowStart) throws IOException {
        return new Window(string, 0, string.length);
    }

    
    @Override
    public long length() throws IOException {
        return string.length;
    }
    
    
    public String getString() {
        return new String(string, charset);
    }
    
}
