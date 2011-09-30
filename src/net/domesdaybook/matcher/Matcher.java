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

package net.domesdaybook.matcher;

import java.io.IOException;
import net.domesdaybook.reader.Reader;

/**
 * An interface for classes that can match bytes from a given position.
 * <p>
 * Design notes:
 * <p/>
 * 1. Provide matching routines for both Reader objects and byte arrays.
 *    The reason for this is to allow more efficient matching directly on byte
 *    arrays, rather than every byte access going through a readByte function call.
 * <p/>
 *    Profiling has shown that these can be among the most frequent method calls,
 *    and even tiny changes in their performance lead to be big impacts on overall
 *    client code performance.
 * <p/>
 *    The downside is that this may lead to code duplication in the matchers.
 *
 * 
 * @author Matt Palmer
 */
public interface Matcher {

    /**
     * Returns whether there is a match or not at the given position in a Reader.
     * 
     * @param reader The {@link Reader} to read from.
     * @param matchPosition The position to try to match at.
     * @return Whether there is a match at the given position.
     */
    public boolean matches(final Reader reader, final long matchPosition) throws IOException;
    
    
    /**
     * Returns whether there is a match or not at the given position in a byte array.
     * 
     * @param bytes An array of bytes to read from.
     * @param matchPosition The position to try to match at.
     * @return Whether there is a match at the given position.
     */
    public boolean matches(final byte[] bytes, final int matchPosition);
}
