/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
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


package net.domesdaybook.matcher.singlebyte;

import java.io.IOException;
import net.domesdaybook.bytes.ByteUtilities;
import net.domesdaybook.reader.Reader;

/**
 * A {@link SingleByteMatcher} which matches any byte at all.
 *
 * @author Matt Palmer
 */
public final class AnyMatcher extends AbstractSingleByteSequence {

    // A static 256-element array containing all the bytes.
    private static final byte[] allBytes =  ByteUtilities.getAllByteValues();


    /**
     * Constructs an immutable AnyMatcher.
     */
    public AnyMatcher() {
    }


    /**
     * {@inheritDoc}
     *
     * Always returns true.
     */
    @Override
    public boolean matches(final byte theByte) {
        return true;
    }
    

    /**
     * {@inheritDoc}
     *
     * Returns a 256-element array of all the possible byte values.
     */
    @Override
    public byte[] getMatchingBytes() {
        return allBytes;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        return prettyPrint ? " . " : ".";
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final Reader reader, final long matchPosition) throws IOException {
        return reader.readByte(matchPosition) >= 0;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchFrom) {
        return matchFrom >= 0 && matchFrom < bytes.length;
    }    


    /**
     * {@inheritDoc}
     *
     * Always returns 256.
     */
    @Override
    public int getNumberOfMatchingBytes() {
        return 256;
    }

    
    /**
     * {@inheritDoc}
     *
     * Always true
     */ 
    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchPosition) {
        return true;
    }

}
