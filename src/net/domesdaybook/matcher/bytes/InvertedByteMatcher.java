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


package net.domesdaybook.matcher.bytes;

import java.io.IOException;

import net.domesdaybook.io.Window;
import net.domesdaybook.io.WindowReader;
import net.domesdaybook.util.bytes.ByteUtilities;

/**
 * A class which matches all bytes except for the one provided.
 *
 * @author Matt Palmer
 */
public final class InvertedByteMatcher extends AbstractByteMatcher {

    private final byte byteToMiss;


    /**
     * Constructs an immutable InvertedByteMatcher.
     *
     * @param byteToMiss The only byte not to match.
     */
    public InvertedByteMatcher(final byte byteToMiss) {
        this.byteToMiss = byteToMiss;
    }
    
    
    /**
     * Constructs an immutable InvertedByteMatcher from a hex representation of a byte.
     * 
     * @param hexByte A string containing a 2-digit hex string giving the value of the byte not to match.
     * @throws IllegalArgumentException if the string is not a valid 2-digit hex byte.
     */
    public InvertedByteMatcher(final String hexByte) {
        this.byteToMiss = ByteUtilities.byteFromHex(hexByte);
    }    


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final WindowReader reader, final long matchPosition) throws IOException{
        final Window window = reader.getWindow(matchPosition);
        return window == null? false
                : window.getByte(reader.getWindowOffset(matchPosition)) != byteToMiss;
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        return matchPosition >= 0 && matchPosition < bytes.length &&
                bytes[matchPosition] != byteToMiss;
    }    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchPosition) {
        return bytes[matchPosition] != byteToMiss;
    }    

    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte theByte) {
        return theByte != byteToMiss;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getMatchingBytes() {
        byte[] matchingBytes = new byte[255];
        int byteIndex = 0;
        for (int byteValue = 0; byteValue < 256; byteValue++) {
            final byte theByte = (byte) byteValue;
            if (theByte != byteToMiss) {
                matchingBytes[byteIndex++] = theByte;
            }
        }
        return matchingBytes;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        final StringBuilder builder = new StringBuilder();
        builder.append(prettyPrint? " [^ " : "[^");
        builder.append(ByteUtilities.byteToString(prettyPrint, byteToMiss & 0xFF));
        builder.append(prettyPrint? " ] " : ']');
        return builder.toString();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfMatchingBytes() {
        return 255;
    }


}
