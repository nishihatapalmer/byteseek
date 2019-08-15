/*
 * Copyright Matt Palmer 2009-2019, All rights reserved.
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
 */

package net.byteseek.matcher.bytes;

import java.io.IOException;

import net.byteseek.utils.ByteUtils;
import net.byteseek.io.reader.windows.Window;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.utils.StringUtils;

/**
 * A {@link ByteMatcher} which matches a byte which shares any of its bits with a bitmask.
 *
 * @deprecated The {@link WildBitAnyMatcher} class is a more general solution, as it allows you
 *             to specify which bits are "don't care" bits - the others can match either zero or one.
 *             This class only allows you specify "1" bits of which any must match.
 *
 * <p>
 * <b>Note</b> This class will return a regular expression which is compatible with v3 syntax,
 * and matches the same bytes as the AnyBitmaskMatcher.  Therefore, if you serialise this class as a regular
 * expression, and then parse and compile the regular expression, you will get a different ByteMatcher.
 * 
 * @author Matt Palmer
 */
public final class AnyBitmaskMatcher extends InvertibleMatcher {

    private final byte mBitMaskValue;

    /**
     * Constructs an immutable AnyBitmaskMatcher.
     *
     * @param bitMaskValue The bitmaskValue to match any of its bits against.
     */
    public AnyBitmaskMatcher(final byte bitMaskValue) {
        super(false);
        mBitMaskValue = bitMaskValue;
    }

    /**
     * Constructs an immutable AnyBitmaskMatcher.
     *
     * @param bitMaskValue The bitmaskValue to match any of its bits against.
     * @param inverted Whether the result of a match should be inverted.
     */
    public AnyBitmaskMatcher(final byte bitMaskValue, final boolean inverted) {
        super(inverted);
        mBitMaskValue = bitMaskValue;
    }

    @Override
    public boolean matches(final WindowReader reader, final long matchPosition) throws IOException {
        final Window window = reader.getWindow(matchPosition);
        return window == null? false
               : ((window.getByte(reader.getWindowOffset(matchPosition)) & mBitMaskValue) != 0) ^ inverted;
    }

    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        return (matchPosition >= 0 && matchPosition < bytes.length) &&
                (((bytes[matchPosition] & mBitMaskValue) != 0) ^ inverted);
    }    

    @Override
    public boolean matches(final byte theByte) {
        return ((theByte & mBitMaskValue) != 0) ^ inverted;
    }

    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        final StringBuilder builder = new StringBuilder();
        if (mBitMaskValue == 0) { // bitmask value of 0 matches nothing - it's a horrid edge case for AnyBitmaskMatchers.
            builder.append(inverted? "__" : "^__"); // if inverted, match everthing, otherwise match nothing.
        } else {
            if (inverted) builder.append('^');
            builder.append('~');
            StringUtils.appendWildByteRegex(builder, mBitMaskValue, mBitMaskValue);
        }
        return builder.toString();
    }

    @Override
    public byte[] getMatchingBytes() {
        return inverted? 
                ByteUtils.getBytesNotMatchingAnyBitMask(mBitMaskValue) :
                ByteUtils.getBytesMatchingAnyBitMask(mBitMaskValue);
    }

    @Override
    public int getNumberOfMatchingBytes() {
        return inverted? 
        		256 - ByteUtils.countBytesMatchingAnyBit(mBitMaskValue) :
                ByteUtils.countBytesMatchingAnyBit(mBitMaskValue);
    }

    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchPosition) {
        return ((bytes[matchPosition] & mBitMaskValue) != 0) ^ inverted;
    }

    @Override
    public int hashCode() {
        return mBitMaskValue * (inverted? 43 : 31);
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof AnyBitmaskMatcher)) {
            return false;
        }
        final AnyBitmaskMatcher other = (AnyBitmaskMatcher) obj;
        return mBitMaskValue == other.mBitMaskValue && inverted == other.inverted;
    }

}
