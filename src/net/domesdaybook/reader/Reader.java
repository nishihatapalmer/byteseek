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


package net.domesdaybook.reader;

import java.io.Closeable;
import java.io.IOException;


/**
 * An interface for classes which can read bytes at a given position.
 * <p/>
 * Design issues: 
 * <p/>
 * 1. Should ReaderException be a checked or unchecked exception?  
 *    It is currently a RuntimeException, but it can encapsulate 
 *    sources which may throw a checked IOException (e.g. RandomAccessFile).
 *    It can also encapsulate sources which only throw RuntimExceptions,
 *     e.g. byte arrays.  
 *    Require behaviour to be consistent across all implementations for client code,
 * 
 * 
 * @author Matt Palmer
 */
public interface Reader extends Closeable {

    public static final long UNKNOWN_LENGTH = -1;
    
    /**
     * Read a byte from a given position.
     *
     * @param position The position of the byte to read.
     * @return int The byte value at the position given as an integer (0-255)
     *         If there is no byte at the position, it returns -1.
     */
    int readByte(final long position) throws IOException;

    
    /**
     * 
     * @param position The position of the byte to read in the underlying data.
     * @return Window an Window containing a byte array, and a startPos which gives
     *         the position of the byte in the byte array.
     */
    Window getWindow(final long position) throws IOException;
    
    
    int getWindowOffset(final long position);
    
    
    /**
     * @return long the length of the byte source accessed by the reader.
     */
    public long length() throws IOException;
    
    
    /**
     * Clears any cache associated with this Reader.
     */
    public void clearCache();
    
    
}
