/*
 * Copyright Matt Palmer 2009-2019, All rights reserved.
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

import net.byteseek.utils.ArgUtils;
import net.byteseek.utils.ByteUtils;
import net.byteseek.io.reader.windows.Window;
import net.byteseek.io.reader.WindowReader;

/**
 * A class which matches all bytes except for the one provided.
 *
 * @author Matt Palmer
 */
public final class OneByteInvertedMatcher extends AbstractByteMatcher {

    private final byte byteToMiss;

    private static final class MatcherCache {

        static final OneByteInvertedMatcher[] values = new OneByteInvertedMatcher[256];

        static {
            for (int i = 0; i < 256; i++) {
                values[i] = new OneByteInvertedMatcher((byte) i);
            }
        }

    }

    /**
     * Returns the corresponding InvertedOneByteMatcher for a byte value.
     *
     * @param value the byte to get the InvertedOneByteMatcher for.
     * @return the corresponding InvertedOneByteMatcher for a byte value.
     */
    public static OneByteInvertedMatcher valueOf(final byte value) {
        return MatcherCache.values[value & 0xff];
    }

    /**
     * Returns the corresponding OneByteInvertedMatcher for a char value from a static cache.
     *
     * @param value The byte to get a byte matcher for.
     * @return The OneByteInvertedMatcher for the byte passed in.
     * @throws IllegalArgumentException if the char is not an extended ASCII byte in the range 0-255.
     */
    public static OneByteInvertedMatcher valueOf(final char value) {
        ArgUtils.checkExtendedAsciiByte(value, "value");
        return MatcherCache.values[value & 0xFF];
    }

    /**
     * Returns the corresponding OneByteInvertedMatcher for an int value from a static cache.
     *
     * @param value The byte to get a byte matcher for.
     * @return The OneByteInvertedMatcher for the byte passed in.
     * @throws IllegalArgumentException if the int is not in the range 0 to 255.
     */
    public static OneByteInvertedMatcher valueOf(final int value) {
        ArgUtils.checkIntToByteRange(0, value);
        return MatcherCache.values[value];
    }

    /**
     * Constructs an immutable OneByteInvertedMatcher.
     *
     * @param byteToMiss The only byte not to match.
     */
    public OneByteInvertedMatcher(final byte byteToMiss) {
        this.byteToMiss = byteToMiss;
    }

    /**
     * Constructs an immutable OneByteInvertedMatcher from a hex representation of a byte.
     * 
     * @param hexByte A string containing a 2-digit hex string giving the value of the byte not to match.
     * @throws IllegalArgumentException if the string is not a valid 2-digit hex byte.
     */
    public OneByteInvertedMatcher(final String hexByte) {
        this.byteToMiss = ByteUtils.byteFromHex(hexByte);
    }    

    @Override
    public boolean matches(final WindowReader reader, final long matchPosition) throws IOException{
        final Window window = reader.getWindow(matchPosition);
        return window == null? false
                : window.getByte(reader.getWindowOffset(matchPosition)) != byteToMiss;
    }

    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        return matchPosition >= 0 && matchPosition < bytes.length &&
                bytes[matchPosition] != byteToMiss;
    }    

    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchPosition) {
        return bytes[matchPosition] != byteToMiss;
    }    

    @Override
    public boolean matches(final byte theByte) {
        return theByte != byteToMiss;
    }

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

    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        return '^' + ByteUtils.byteToString(false, byteToMiss & 0xFF);
    }

    @Override
    public int getNumberOfMatchingBytes() {
        return 255;
    }

    @Override
    public int hashCode() {
        return byteToMiss;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof OneByteInvertedMatcher)) {
            return false;
        }
        return byteToMiss == ((OneByteInvertedMatcher) obj).byteToMiss;
    }

    /**
     * Returns the single byte value which will not match.
     * @return the single byte value which will not match.
     */
    public byte getNonMatchingByteValue() {
        return byteToMiss;
    }

}
