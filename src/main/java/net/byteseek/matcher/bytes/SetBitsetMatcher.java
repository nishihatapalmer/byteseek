/*
 * Copyright Matt Palmer 2009-2017, All rights reserved.
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

import net.byteseek.utils.ByteUtils;
import net.byteseek.io.reader.windows.Window;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.utils.ArgUtils;

import java.util.BitSet;
import java.util.Collection;

/**
 * A SetBitsetMatcher is a {@link ByteMatcher} which
 * matches an arbitrary set of bytes.
 * <p>
 * It uses a BitSet as the underlying representation of the entire set of bytes,
 * so is not memory efficient for small numbers of sets of bytes.
 *
 * @author Matt Palmer
 */
public final class SetBitsetMatcher extends InvertibleMatcher {

    private final BitSet byteValues = new BitSet(256);

    /**
     * Constructs a SetBitsetMatcher from a set of bytes.
     *
     * @param values A collection of bytes
     * @param inverted Whether matching is on the set of bytes or their inverse.
     */
    public SetBitsetMatcher(final Collection<Byte> values, final boolean inverted) {
        super(inverted);
        ArgUtils.checkNullOrEmptyCollection(values, "values");
        for (final Byte b : values) {
            byteValues.set(b & 0xFF);
        }
    }

    @Override
    public boolean matches(final WindowReader reader, final long matchPosition) throws IOException{
        final Window window = reader.getWindow(matchPosition);
        return window != null && (byteValues.get(window.getByte(reader.getWindowOffset(matchPosition)) & 0xFF) ^ inverted);
    }  

    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        return (matchPosition >= 0 && matchPosition < bytes.length) &&
                (byteValues.get(bytes[matchPosition] & 0xFF) ^ inverted);
    }  

    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchPosition) {
        return byteValues.get(bytes[matchPosition] & 0xFF) ^ inverted;
    }

    @Override
    public boolean matches(final byte theByte) {
        return byteValues.get(theByte & 0xFF) ^ inverted;
    }

    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        StringBuilder regularExpression = new StringBuilder();
        if (inverted) {
            regularExpression.append('^');
        }
        regularExpression.append('[');
        boolean firstItem = true;
        for (int byteIndex = 0; byteIndex < 256; byteIndex++) {
            if (byteValues.get(byteIndex)) {
                if (prettyPrint && !firstItem) {
                    regularExpression.append(' ');
                }
                regularExpression.append(ByteUtils.byteToString(prettyPrint, byteIndex));
                firstItem = false;
            }
        }
        regularExpression.append(']');
        return regularExpression.toString();
    }

    @Override
    public byte[] getMatchingBytes() {
        final byte[] values = new byte[getNumberOfMatchingBytes()];
        int byteIndex = 0;
        for (int value = 0; value < 256; value++) {
            if (byteValues.get(value) ^ inverted) {
                values[byteIndex++] = (byte) value;
            }
        }
        return values;
    }

    @Override
    public int getNumberOfMatchingBytes() {
        return inverted ? 256 - byteValues.cardinality() : byteValues.cardinality();
    }

    @Override
    public String toString() {
    	return getClass().getSimpleName() + "[bitset:" + byteValues + 
    										" inverted: " + inverted + ']';
    }

}
