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
import net.byteseek.matcher.bytes.InvertibleMatcher;
import net.byteseek.utils.ByteUtils;
import net.byteseek.utils.StringUtils;

import java.io.IOException;

/**
 * A byte matcher which matches all the ones and zeros in a byte exactly, except for "don't care" bits.
 * A value to match is given, and also a wild mask.
 * Bits which are zero in the wild mask indicate those bits in the value we don't care about - the wild bits.
 * Bits which are one in the wild mask indicate those bits which must be the same as the value provided.
 * <p>
 * It is an invertible matcher, so you can also specify that it doesn't match the value (aside from the don't care bits).
 *
 * Created by matt on 01/07/17.
 */
public final class WildBitMatcher extends InvertibleMatcher {

    private final byte matchValue;
    private final byte wildcardMask;

    /**
     * Constructs a WildBitMatcher from a value to match and a wildMask.
     * The wildMask specifies which bits we care about in the value, and which we don't.
     * Bits set to 1 in the wildMask we care about, and must match the corresponding bit in the value.
     * Bits set to 0 in the wildMask we don't care about, and they can take either 0 or 1 in the value.
     *
     * @param value    The bits we want to match, whether zero or one.
     * @param wildMask The mask that specifies which bits we care about and which we don't.
     */
    public WildBitMatcher(final byte value, final byte wildMask) {
        this(value, wildMask, false);
    }

    /**
     * Constructs a WildBitMatcher from a value to match and a wildMask, and whether the results should be inverted.
     * The wildMask specifies which bits we care about in the value, and which we don't.
     * Bits set to 1 in the wildMask we care about, and must match the corresponding bit in the value.
     * Bits set to 0 in the wildMask we don't care about, and they can take either 0 or 1 in the value.
     *
     * @param value    The bits we want to match.
     * @param wildMask The mask that specifies which bits we care about and which we don't.
     * @param inverted Whether the matcher results are inverted or not.
     */
    public WildBitMatcher(final byte value, final byte wildMask, final boolean inverted) {
        super(inverted);
        this.matchValue = (byte) (value & wildMask);
        this.wildcardMask = wildMask;
    }

    @Override
    public boolean matches(final byte theByte) {
        return ((theByte & wildcardMask) == matchValue) ^ inverted;
    }

    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchPosition) {
        return ((bytes[matchPosition] & wildcardMask) == matchValue) ^ inverted;
    }

    @Override
    public boolean matches(final WindowReader reader, final long matchPosition) throws IOException {
        final Window window = reader.getWindow(matchPosition);
        return window == null? false
                : ((window.getByte(reader.getWindowOffset(matchPosition)) & wildcardMask) == matchValue) ^ inverted;
    }

    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        return (matchPosition >= 0 && matchPosition < bytes.length) &&
                (((bytes[matchPosition] & wildcardMask) == matchValue) ^ inverted);
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
        // Each bit which isn't set in the mask gives us an additional two possibilities we can match.
        final int numBytesMatchingMask = 1 << ByteUtils.countUnsetBits(wildcardMask);
        return inverted? 256 - numBytesMatchingMask : numBytesMatchingMask;
    }

    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        final StringBuilder builder = new StringBuilder(16);
        if (inverted) builder.append('^');
        StringUtils.appendWildByteRegex(builder, matchValue, wildcardMask);
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return ((wildcardMask & 0xFF) + 7) * // Avoid zeros in calculation (and negative numbers):
                ((matchValue & 0xFF) + 13) * // Avoid zeros in calculation (and negative numbers)
                (inverted? 43 : 31);
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof WildBitMatcher)) {
            return false;
        }
        final WildBitMatcher other = (WildBitMatcher) obj;
        return wildcardMask == other.wildcardMask &&
                matchValue == other.matchValue &&
                inverted == other.inverted;
    }
}
