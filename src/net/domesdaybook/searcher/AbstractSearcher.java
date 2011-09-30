/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 *
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


package net.domesdaybook.searcher;

import java.io.IOException;
import net.domesdaybook.reader.Reader;

/**
 *
 * @author matt
 */
public abstract class AbstractSearcher implements Searcher {

    /**
     * @inheritDoc
     */
    @Override
    public long searchForwards(final Reader reader, final long fromPosition) 
            throws IOException {
        return searchForwards(reader, fromPosition, Long.MAX_VALUE);
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public long searchForwards(final Reader reader) 
            throws IOException {
        return searchForwards(reader, 0, Long.MAX_VALUE);
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public int searchForwards(final byte[] bytes, final int fromPosition) {
        return searchForwards(bytes, fromPosition, bytes.length - 1);
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public int searchForwards(final byte[] bytes) {
        return searchForwards(bytes, 0, bytes.length - 1);
    }


    /**
     * @inheritDoc
     */
    @Override
    public long searchBackwards(final Reader reader, final long fromPosition) 
            throws IOException {
        return searchBackwards(reader, fromPosition, 0);
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public long searchBackwards(final Reader reader) 
            throws IOException {
        return searchBackwards(reader, reader.length() - 1, 0);
    }


    /**
     * @inheritDoc
     */
    @Override
    public int searchBackwards(final byte[] bytes, final int fromPosition) {
        return searchBackwards(bytes, fromPosition, 0);
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public int searchBackwards(final byte[] bytes) {
        return searchBackwards(bytes, bytes.length - 1, 0);
    }
    
}
