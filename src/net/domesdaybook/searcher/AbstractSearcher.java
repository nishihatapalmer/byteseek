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
 * 
 */


package net.domesdaybook.searcher;

import java.io.IOException;
import net.domesdaybook.reader.Reader;

/**
 * An abstract searcher implementation which implements the common convenience
 * search methods, by providing default values to the real search methods.
 * 
 * @author Matt Palmer
 */
public abstract class AbstractSearcher implements Searcher {

    /**
     * {@inheritDoc}
     */
    @Override
    public long searchForwards(final Reader reader, final long fromPosition) 
            throws IOException {
        return searchForwards(reader, fromPosition, Long.MAX_VALUE);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public long searchForwards(final Reader reader) throws IOException {
        return searchForwards(reader, 0, Long.MAX_VALUE);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public int searchForwards(final byte[] bytes, final int fromPosition) {
        return searchForwards(bytes, fromPosition, bytes.length - 1);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public int searchForwards(final byte[] bytes) {
        return searchForwards(bytes, 0, bytes.length - 1);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public long searchBackwards(final Reader reader, final long fromPosition) 
            throws IOException {
        return searchBackwards(reader, fromPosition, 0);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public long searchBackwards(final Reader reader) throws IOException {
        return searchBackwards(reader, reader.length() - 1, 0);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int searchBackwards(final byte[] bytes, final int fromPosition) {
        return searchBackwards(bytes, fromPosition, 0);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public int searchBackwards(final byte[] bytes) {
        return searchBackwards(bytes, bytes.length - 1, 0);
    }
    
    /**
     * Returns a position guaranteed to be within the length of the reader,
     * or -1 if the reader itself has a length of zero.
     *
     * @param reader The reader to acquire a valid position for.
     * @param position The position to try.
     * @return A position guaranteed to be a valid position in the reader, or -1 
     *         if the reader has a length of zero.
     * @throws IOException if the reader cannot be read from.
     */
    protected final long withinLength(final Reader reader, final long position) 
            throws IOException {
        final long positionToTry = position < 0 ? 0 : position;
        return reader.getWindow(positionToTry) != null? 
                positionToTry : reader.length() - 1;
    }
}
