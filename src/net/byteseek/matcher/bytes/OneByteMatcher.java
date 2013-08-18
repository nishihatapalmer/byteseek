/*
 * Copyright Matt Palmer 2009-2012, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
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
 */


package net.byteseek.matcher.bytes;

import java.io.IOException;

import net.byteseek.io.reader.Window;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.sequence.ByteArrayMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.util.bytes.ByteUtilities;


/**
 * A OneByteMatcher is a {@link ByteMatcher} which matches
 * one byte value only.
 *
 * @author Matt Palmer
 */
public final class OneByteMatcher extends AbstractByteMatcher {

    private final byte byteToMatch;


    /**
     * Constructs an immutable OneByteMatcher.
     * 
     * @param byteToMatch The byte to match.
     */
    public OneByteMatcher(final byte byteToMatch) {
        this.byteToMatch = byteToMatch;
    }
    
    
    /**
     * Constructs an immutable OneByteMatcher from a hex representation of a byte.
     * 
     * @param hexByte 
     * @throws IllegalArgumentException if the string is not a valid 2-digit hex byte.
     */
    public OneByteMatcher(final String hexByte) {
        this.byteToMatch = ByteUtilities.byteFromHex(hexByte);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final WindowReader reader, final long matchPosition) throws IOException{
        final Window window = reader.getWindow(matchPosition);
        return window == null? false
               : window.getByte(reader.getWindowOffset(matchPosition)) == byteToMatch;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        return matchPosition >= 0 && matchPosition < bytes.length &&
                bytes[matchPosition] == byteToMatch;
    }   
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchPosition) {
        return bytes[matchPosition] == byteToMatch;
    }    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte theByte) {
        return theByte == byteToMatch;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getMatchingBytes() {
        return new byte[] {byteToMatch};
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        final String regex = ByteUtilities.byteToString(prettyPrint, byteToMatch & 0xFF);
        return prettyPrint? regex + " " : regex;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfMatchingBytes() {
        return 1;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override    
    public SequenceMatcher repeat(int numberOfRepeats) {
        if (numberOfRepeats < 1) {
            throw new IllegalArgumentException("Number of repeats must be at least one.");
        }
        if (numberOfRepeats == 1) {
            return this;
        }   
        return new ByteArrayMatcher(byteToMatch, numberOfRepeats);
    }    

    @Override
    public String toString() {
    	return getClass().getSimpleName() + "[byte:" + byteToMatch + ']';
    }
    
    
}
