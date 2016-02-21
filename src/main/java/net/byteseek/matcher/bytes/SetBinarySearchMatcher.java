/*
 * Copyright Matt Palmer 2009-2016, All rights reserved.
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

import net.byteseek.bytes.ByteUtils;
import net.byteseek.io.reader.windows.Window;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.utils.ArgUtils;

import java.util.Arrays;
import java.util.Set;



/**
 * A SetBinarySearchMatcher is a {@link ByteMatcher} which
 * uses a binary search to determine whether a given byte is in the
 * set of bytes.  This makes it more memory efficient than the {@link SetBitsetMatcher} class,
 * at the expense of slightly more time to match.
 *
 * @author Matt Palmer
 */
public final class SetBinarySearchMatcher extends InvertibleMatcher {

    private final byte[] bytesToMatch;

    /**
     * Constructs an immutable SetBinarySearchMatcher.
     * 
     * @param bytes The Set of bytes to match.
     * @param inverted Whether the set of bytes is inverted or not.
     */
    public SetBinarySearchMatcher(final Set<Byte> bytes, final boolean inverted) {
        super(inverted);
        ArgUtils.checkNullOrEmptyCollection(bytes, "bytes");
        this.bytesToMatch = ByteUtils.toArray(bytes);
        Arrays.sort(this.bytesToMatch);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte theByte) {
        return (Arrays.binarySearch(bytesToMatch, theByte) >= 0) ^ inverted;
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final WindowReader reader, final long matchPosition) throws IOException{
        final Window window = reader.getWindow(matchPosition);
        return window == null? false
               : ((Arrays.binarySearch(bytesToMatch, 
                       window.getByte(reader.getWindowOffset(matchPosition))) >= 0) ^ inverted);
    }    

    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        return (Arrays.binarySearch(bytes, bytes[matchPosition]) >= 0) ^ inverted;
    }     
    

    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchPosition) {
        return (Arrays.binarySearch(bytes, bytes[matchPosition]) >= 0) ^ inverted;
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getMatchingBytes() {
        if (inverted) {
            final byte[] invertedValues = new byte[getNumberOfMatchingBytes()];
            int byteIndex = 0;
            for (int value = 0; value < 256; value++) {
                if (matches((byte) value)) {
                    invertedValues[byteIndex++] = (byte) value;
                }
            }
            return invertedValues;
        }
        return bytesToMatch.clone();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfMatchingBytes() {
        return inverted ? 256 - bytesToMatch.length : bytesToMatch.length;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        StringBuilder regularExpression = new StringBuilder();
        if (inverted) {
            regularExpression.append('^');
        }
        regularExpression.append('[').append(ByteUtils.bytesToString(prettyPrint, bytesToMatch)).append(']');
        return regularExpression.toString();
    }
    
    @Override
    public String toString() {
    	return getClass().getSimpleName() + "[bytes:" + ByteUtils.toList(bytesToMatch) + 
    										" inverted: " + inverted + ']';
    }

}
