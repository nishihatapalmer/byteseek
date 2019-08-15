/*
 * Copyright Matt Palmer 2016-19, All rights reserved.
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
import net.byteseek.utils.ArgUtils;
import net.byteseek.utils.ByteUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * A fast matcher for arbitrary sets of bytes, backed by a bitmap held in 4 long values.
 *
 * Created by matt on 28/02/16.
 */
public final class SetBitmapMatcher extends InvertibleMatcher {

    private final int hashCode;
    private final long[] bitmask = new long[4];
    private final int numberOfMatchingBytes;

    /**
     * Constructs a SetBitmapMatcher from a collection of bytes.
     *
     * Duplicate bytes in the collection are fine - only unique byte values will be matched.
     * @param bytes A collection of bytes to match with a SetBitmapMatcher.
     */
    public SetBitmapMatcher(final Collection<? extends Byte> bytes) {
        this(bytes, false);
    }

    /**
     * Constructs a SetBitmapMatcher from an array of bytes.
     * <p>
     * Duplicate bytes in the array are fine - only unique byte values will be matched.
     *
     * @param bytes An array of bytes to match with a SetBitmapMatcher.
     */
    public SetBitmapMatcher(final byte... bytes) {
        this(false, bytes);
    }

    /**
     * Constructs a SetBitmapMatcher from a collection of bytes, and whether the set should match
     * the inverse of that set of bytes or not.
     * <p>
     * Duplicate bytes in the collection are fine - only unique byte values will be matched.
     * @param bytes A collection of bytes to match with a SetBitmapMatcher.
     * @param inverted Whether the inverse of the set of bytes should be matched.
     */
    public SetBitmapMatcher(final Collection<? extends Byte> bytes, final boolean inverted) {
        super(inverted);
        ArgUtils.checkNullOrEmptyCollection(bytes, "bytes");
        int countOfMatchingBytes = 0;
        long hash = inverted? 43 : 31;
        for (Byte b : bytes) {
            if (setBitValue(b & 0xFF)) {
                countOfMatchingBytes++;
                hash = hash * (b + 1);
            }
        }
        numberOfMatchingBytes = inverted? 256 - countOfMatchingBytes : countOfMatchingBytes;
        hashCode = (int) hash;
    }

    /**
     * Constructs a SetBitmapMatcher from an array of bytes, and whether the set should match
     * the inverse of that set of bytes or not.
     * <p>
     * Duplicate bytes in the array are fine - only unique byte values will be matched.
     *
     * @param bytes An array of bytes to match with a SetBitmapMatcher.
     * @param inverted Whether the inverse of the set of bytes should be matched.
     */
    public SetBitmapMatcher(final boolean inverted, final byte... bytes) {
        super(inverted);
        ArgUtils.checkNullOrEmptyByteArray(bytes, "bytes");
        int countOfMatchingBytes = 0;
        long hash = inverted? 43 : 31;
        for (byte b : bytes) {
            if (setBitValue(b & 0xFF)) {
                countOfMatchingBytes++;
                hash = hash * (b + 1);
            }
        }
        numberOfMatchingBytes = inverted? 256 - countOfMatchingBytes : countOfMatchingBytes;
        hashCode = (int) hash;
    }

    private boolean setBitValue(final int byteValue) {
        final int bitmaskIndex  = byteValue >>> 6;
        final long bitmaskValue = 1L << (byteValue & 0x3F);
        final long existingMask = bitmask[bitmaskIndex];
        final boolean bitAlreadySet = (existingMask & bitmaskValue) > 0;
        bitmask[bitmaskIndex] = existingMask | bitmaskValue;
        return !bitAlreadySet;
    }

    @Override
    public boolean matches(final byte theByte) {
        final int bitmaskIndex = (theByte & 0xFF) >>> 6;
        final long bitmaskValue = 1L << (theByte & 0x3F);
        return ((bitmask[bitmaskIndex] & bitmaskValue) != 0) ^ inverted;
    }

    @Override
    public byte[] getMatchingBytes() {
        final byte[] matchingBytes = new byte[numberOfMatchingBytes];
        int byteIndex = 0;
        for (int byteValue = 0; byteValue < 256; byteValue++) {
            if (matches((byte) byteValue)) {
                matchingBytes[byteIndex++] = (byte) byteValue;
            }
        }
        return matchingBytes;
    }

    @Override
    public int getNumberOfMatchingBytes() {
        return numberOfMatchingBytes;
    }

    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, int matchPosition) {
        final int byteValue     = bytes[matchPosition] & 0xFF;
        final int bitmaskIndex  = byteValue >>> 6;
        final long bitmaskValue = 1L << (byteValue & 0x3F);
        return ((bitmask[bitmaskIndex] & bitmaskValue) != 0) ^ inverted;
    }

    @Override
    public String toRegularExpression(boolean prettyPrint) {
        StringBuilder regularExpression = new StringBuilder();
        if (inverted) {
            regularExpression.append('^');
        }
        regularExpression.append('[');
        boolean firstItem = true;
        for (int byteIndex = 0; byteIndex < 256; byteIndex++) {
            final int bitmaskIndex  = byteIndex >>> 6;
            final long bitmaskValue = 1L << (byteIndex & 0x3F);
            if ((bitmask[bitmaskIndex] & bitmaskValue) != 0) {
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
    public boolean matches(final WindowReader reader, final long matchPosition) throws IOException {
        final Window window = reader.getWindow(matchPosition);
        return window != null && matches(window.getByte(reader.getWindowOffset(matchPosition)));
    }

    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        if (matchPosition >= 0 && matchPosition < bytes.length) {
            final int byteValue     = bytes[matchPosition] & 0xFF;
            final int bitmaskIndex  = byteValue >>> 6;
            final long bitmaskValue = 1L << (byteValue & 0x3F);
            return ((bitmask[bitmaskIndex] & bitmaskValue) != 0) ^ inverted;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof SetBitmapMatcher)) {
            return false;
        }
        final SetBitmapMatcher other = (SetBitmapMatcher) obj;
        return hashCode == other.hashCode &&
               inverted == other.inverted &&
               numberOfMatchingBytes == other.numberOfMatchingBytes &&
               Arrays.equals(bitmask, other.bitmask);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + toRegularExpression(false);
    }

}
