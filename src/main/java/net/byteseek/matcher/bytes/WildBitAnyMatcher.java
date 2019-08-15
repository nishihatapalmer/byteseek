/*
 * Copyright Matt Palmer 2017-19, All rights reserved.
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

import net.byteseek.io.reader.WindowReader;
import net.byteseek.io.reader.windows.Window;
import net.byteseek.utils.ByteUtils;
import net.byteseek.utils.StringUtils;

import java.io.IOException;

/**
 * A byte matcher which matches any of the ones and zeros in a byte exactly, except for "don't care" bits.
 * A value to match is given, and also a wild mask.
 * Bits which are zero in the wild mask indicate those bits in the value we don't care about - the wild bits.
 * Bits which are one in the wild mask indicate those bits of which at least one must be the same as the value provided.
 * <p>
 * It is an invertible matcher, so you can also specify that it doesn't match the value (aside from the don't care bits).
 *
 * Created by matt on 01/07/17.
 */
public final class WildBitAnyMatcher extends InvertibleMatcher {

    private final byte noMatchValue;
    private final byte wildcardMask;

    /**
     * Constructs a WildBitAnyMatcher from a byte to match, along with a wildMask that specifies which bits
     * we don't care about in the byte to match, and which we do.  A zero bit in the wildMask means we don't
     * care about the value of that bit in the value, a one means we need at least one of the corresponding
     * value bits to match.
     *
     * @param value    The bits to match.
     * @param wildMask The bits we don't care about matching, zero meaning we don't care about that bit in the value.
     */
    public WildBitAnyMatcher(final byte value, final byte wildMask) {
        this(value, wildMask, false);
    }

    /**
     * Constructs a WildBitAnyMatcher from a byte to match, along with a wildMask that specifies which bits
     * we don't care about in the byte to match, and which we do.  A zero bit in the wildMask means we don't
     * care about the value of that bit in the value, a one means we need at least one of the corresponding
     * value bits to match.
     *
     * @param value    The bits to match.
     * @param wildMask The bits we don't care about matching, zero meaning we don't care about that bit in the value.
     * @param inverted Whether the matcher results are inverted or not.
     */
    public WildBitAnyMatcher(final byte value, final byte wildMask, final boolean inverted) {
        super(inverted);
        // The bitwise inverse of the value we specified is the only value we can't match (doesn't have any of the bits of the value).
        this.noMatchValue = (byte) ((~value) & wildMask);
        this.wildcardMask = wildMask;
    }

    @Override
    public boolean matches(final byte theByte) {
        // a wildcard mask of zero means we don't care about any bit values - matches everything.
        return wildcardMask == 0? !inverted : ((theByte & wildcardMask) != noMatchValue) ^ inverted;
    }

    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchPosition) {
        // a wildcard mask of zero means we don't care about any bit values - matches everything.
        return wildcardMask == 0? !inverted : ((bytes[matchPosition] & wildcardMask) != noMatchValue) ^ inverted;
    }

    @Override
    public boolean matches(final WindowReader reader, final long matchPosition) throws IOException {
        final Window window = reader.getWindow(matchPosition);
        return window == null?       false
                : wildcardMask == 0? !inverted  // a wildcard mask of zero means we don't care about any bit values - matches everything.
                : ((window.getByte(reader.getWindowOffset(matchPosition)) & wildcardMask) != noMatchValue) ^ inverted;
    }

    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        return (matchPosition >= 0 && matchPosition < bytes.length) &&
                // a wildcard mask of zero means we don't care about any bit values - matches everything.
                (wildcardMask == 0? !inverted : ((bytes[matchPosition] & wildcardMask) != noMatchValue) ^ inverted);
    }

    @Override
    public byte[] getMatchingBytes() {
        final int numBytes = getNumberOfMatchingBytes();
        final byte[] matchingBytes = new byte[numBytes];
        for (int i = 0, matchPos = 0; matchPos < numBytes && i < 256; i++) {
            final byte possibleByte = (byte) i;
            if (matches(possibleByte)) {
                matchingBytes[matchPos++] = possibleByte;
            }
        }
        return matchingBytes;
    }

    @Override
    public int getNumberOfMatchingBytes() {
        final byte mask = wildcardMask;
        final int numBytesMatchingMask = mask == 0? 256 : 256 - (1 << ByteUtils.countUnsetBits(mask));
        return inverted? 256 - numBytesMatchingMask : numBytesMatchingMask;
    }

    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        final StringBuilder builder = new StringBuilder(16);
        if (inverted) builder.append('^');
        builder.append("~");
        StringUtils.appendWildByteRegex(builder, (byte) (~noMatchValue), wildcardMask);
        return builder.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof WildBitAnyMatcher)) {
            return false;
        }
        final WildBitAnyMatcher other = (WildBitAnyMatcher) obj;
        return wildcardMask == other.wildcardMask &&
                noMatchValue == other.noMatchValue &&
                inverted == other.inverted;
    }

    @Override
    public int hashCode() {
        return ((wildcardMask & 0xFF) + 7) * // Avoid zeros in calculation (and negative numbers):
               ((noMatchValue & 0xFF) + 13) * // Avoid zeros in calculation (and negative numbers)
                (inverted? 43 : 31);
    }
}
