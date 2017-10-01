/*
 * Copyright Matt Palmer 2017, All rights reserved.
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
package net.byteseek.incubator.matcher.bytes;

import net.byteseek.io.reader.WindowReader;
import net.byteseek.io.reader.windows.Window;
import net.byteseek.matcher.bytes.InvertibleMatcher;
import net.byteseek.utils.ByteUtils;

import java.io.IOException;

//TODO: Add 0x syntax for hex and 0b syntax for binary and _ for wildcard bits/nibbles to the parser.

//TODO: Can we also combine this with AnyBitmask and AllBitmask matchers..?  &9_ ...?  ~_A

//TODO: write tests for this class.

/**
 * A byte matcher which matches all the ones and zeros in a byte exactly, except for "don't care" bits.
 * A value to match is given, and also a wild mask.  We only care about matching bits which have a 1 set in the wild mask.
 * <p>
 * It is an invertible matcher, so you can also specify that it doesn't match the value (aside from the don't care bits).
 *
 * Created by matt on 01/07/17.
 */
public final class WildBitMatcher extends InvertibleMatcher {

    private final byte matchValue;
    private final byte wildcardMask;

    public WildBitMatcher(final byte value, final byte wildMask) {
        this(value, wildMask, false);
    }

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
    public byte[] getMatchingBytes() {
        final byte[] matchingBytes = new byte[getNumberOfMatchingBytes()];
        int matchPos = 0;
        for (int i = 0; i < 256; i++) {
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
        return inverted? 256 - (1 << ByteUtils.countUnsetBits(wildcardMask))
                       : 1 << ByteUtils.countUnsetBits(wildcardMask);
    }

    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchPosition) {
        return ((bytes[matchPosition] & wildcardMask) == matchValue) ^ inverted;
    }

    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        switch (wildcardMask) {
            case -16: { // 0xF0 - first nibble of a hex byte:
                return inverted? String.format("^%x_", matchValue >>> 4) : String.format("%x_", matchValue >>> 4);
            }
            case 15: { // 0x0F - last nibble of a hex byte:
                return inverted? String.format("^_%x", matchValue) : String.format("_%x", matchValue);
            }
            default: { // some other bitmask - build a binary string from the value, putting _ where the bitmask is zero.
                final StringBuilder regex = new StringBuilder(11);
                if (inverted) regex.append('^');
                regex.append('0').append('b');
                for (int bitpos = 7; bitpos >= 0; bitpos--) {
                    final int bitposMask = 1 << bitpos;
                    if ((wildcardMask & bitposMask) == bitposMask) {
                        regex.append((matchValue & bitposMask) == bitposMask? '1' : '0');
                    } else {
                        regex.append('_');
                    }
                }
                return regex.toString();
            }
        }
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
    public String toString() {
        return getClass().getSimpleName() + "(" + toRegularExpression(true) + ")";
    }
}
