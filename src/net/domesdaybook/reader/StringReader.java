/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 * http://www.opensource.org/licenses/BSD-3-Clause
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
 *  * Neither the "byteseek" name nor the names of its contributors 
 *    may be used to endorse or promote products derived from this software 
 *    without specific prior written permission. 
 *  
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
