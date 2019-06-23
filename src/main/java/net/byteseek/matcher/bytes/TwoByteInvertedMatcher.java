/*
 * Copyright Matt Palmer 2013-19, All rights reserved.
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
import java.util.Collection;
import java.util.Iterator;

import net.byteseek.utils.ByteUtils;
import net.byteseek.io.reader.windows.Window;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.sequence.ByteMatcherSequenceMatcher;
import net.byteseek.matcher.sequence.ByteSequenceMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.utils.ArgUtils;

/**
 * A TwoByteInvertedMatcher is a {@link ByteMatcher} which matches anything but two possible byte values.
 * <p>
 * There is also a static array of TwoByteInvertedMatchers corresonding to case insensitive ASCII alphabetic values,
 * since these are common types to match, and a static method to return an appropriate case insensitive matcher.
 *
 * @author Matt Palmer
 */
public final class TwoByteInvertedMatcher extends AbstractByteMatcher {

    private static final byte LINE_FEED = 0x0A;
    private static final byte CARRIAGE_RETURN = 0x0D;

    private static TwoByteInvertedMatcher[] caseInsensitiveInvertedMatchers;
    public static TwoByteInvertedMatcher INVERTED_LINE_BREAK;

    static {
        caseInsensitiveInvertedMatchers = new TwoByteInvertedMatcher[26];
        for (int i = 0; i < 26; i++) {
            caseInsensitiveInvertedMatchers[i] = new TwoByteInvertedMatcher((byte) (i + 'a'), (byte) (i +'A'));
        }
        INVERTED_LINE_BREAK = new TwoByteInvertedMatcher(LINE_FEED, CARRIAGE_RETURN);
    }

    /**
     * Returns the most appropriate TwoByteMatcher given the two bytes passed in.
     * If both bytes are the same value, a OneByteMatcher will be returned.
     * If both bytes together form a case insensitive ASCII matcher, then one of the predefined matchers will be returned.
     * If the bytes are a line feed / carriage return combination, then the INVERTED_LINE_BREAK matcher will be returned.
     * Otherwise, a new TwoByteInvertedMatcher will be returned.
     *
     * @param byte1 The first byte value
     * @param byte2 The second byte value
     * @return The most appropriate inverted matcher given the two byte values.
     */
    public static ByteMatcher valueOf(final byte byte1, final byte byte2) {
        if (byte1 == byte2) {
            return OneByteInvertedMatcher.valueOf(byte1);
        }
        if (isUpperCase(byte1) && isLowerCase(byte2)) {
            return caseInsensitiveInvertedMatchers[((int) byte1 & 0xFF)- 'A'];
        }
        if (isLowerCase(byte1) && isUpperCase(byte2)) {
            return caseInsensitiveInvertedMatchers[((int) byte2 & 0xFF)- 'A'];
        }
        if (isLineBreak(byte1, byte2)) {
            return INVERTED_LINE_BREAK;
        }
        return new TwoByteInvertedMatcher(byte1, byte2);
    }

    /**
     * Returns a case insensitive TwoByteInvertedMatcher given a byte value,
     * or a OneByteInvertedMatcher if the byte isn't an alphabetic ASCII value.
     *
     * @param theByte The byte to get a case insensitive matcher for.
     * @return A case insensitive matcher for that byte value.
     */
    public static ByteMatcher caseInsensitive(final byte theByte) {
        if (isUpperCase(theByte)) {
            return caseInsensitiveInvertedMatchers[((int) theByte & 0xFF) - 'A'];
        }
        if (isLowerCase(theByte)) {
            return caseInsensitiveInvertedMatchers[((int) theByte & 0xFF) - 'a'];
        }
        return OneByteInvertedMatcher.valueOf(theByte);
    }

    /**
     * Returns a case insensitive TwoByteInvertedMatcher given a char value,
     * or a OneByteInvertedMatcher if the char isn't an alphabetic ASCII value.
     *
     * @param theChar The char to get a case insensitive matcher for.
     * @return A case insensitive matcher for that byte value.
     * @throws IllegalArgumentException if the char has a value greater than 255.
     */
    public static ByteMatcher caseInsensitive(final char theChar) {
        if (isUpperCase((byte) theChar)) {
            return caseInsensitiveInvertedMatchers[theChar - 'A'];
        }
        if (isLowerCase((byte) theChar)) {
            return caseInsensitiveInvertedMatchers[theChar - 'a'];
        }
        if (theChar < 256) {
            return OneByteInvertedMatcher.valueOf((byte) theChar);
        }
        throw new IllegalArgumentException("A character must be between 0 and 255 in value, actual char was:" + (int) theChar);
    }

    /*
     * Class definition.
     */

    private final byte firstByteToNotMatch;
    private final byte secondByteToNotMatch;

    /**
     * Constructs an immutable TwoByteInvertedMatcher.
     *
     * @param firstByteToNotMatch The first byte to not match.
     * @param secondByteToNotMatch The second byte to not match.
     */
    public TwoByteInvertedMatcher(final byte firstByteToNotMatch, final byte secondByteToNotMatch) {
        this.firstByteToNotMatch = firstByteToNotMatch;
        this.secondByteToNotMatch = secondByteToNotMatch;
    }

    /**
     * Constructs an immutable TwoByteInvertedMatcher from hex representations of the bytes.
     *
     * @param firstHexByte The first byte as a hex string.
     * @param secondHexByte The second byte as a hex string.
     * @throws IllegalArgumentException if the string is not a valid 2-digit hex byte.
     */
    public TwoByteInvertedMatcher(final String firstHexByte, final String secondHexByte) {
        this.firstByteToNotMatch = ByteUtils.byteFromHex(firstHexByte);
        this.secondByteToNotMatch = ByteUtils.byteFromHex(secondHexByte);
    }

    /**
     * Constructs an immutable TwoByteInvertedMatcher from a collection of bytes.
     * The collection must have two bytes in it.
     *
     * @param twoBytes The collection of bytes to construct from.
     * @throws IllegalArgumentException if the collection of bytes is null, has null elements or
     *         does not have exactly two bytes in it.
     */
    public TwoByteInvertedMatcher(final Collection<Byte> twoBytes) {
        ArgUtils.checkCollectionSizeNoNullElements(twoBytes, 2);
        final Iterator<Byte> byteIterator = twoBytes.iterator();
        this.firstByteToNotMatch = byteIterator.next();
        this.secondByteToNotMatch = byteIterator.next();
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if the WindowReader passed in is null.
     */
    @Override
    public boolean matches(final WindowReader reader, final long matchPosition) throws IOException{
        final Window window = reader.getWindow(matchPosition);
        if (window == null) {
            return false;
        }
        final byte windowByte = window.getByte(reader.getWindowOffset(matchPosition));
        return !(windowByte == firstByteToNotMatch || windowByte == secondByteToNotMatch);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if the byte array passed in is null.
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        if (matchPosition >= 0 && matchPosition < bytes.length) {
            final byte theByte = bytes[matchPosition];
            return !(theByte == firstByteToNotMatch || theByte == secondByteToNotMatch);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if the byte array passed in is null.
     */
    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchPosition) {
        final byte theByte = bytes[matchPosition];
        return !(theByte == firstByteToNotMatch || theByte == secondByteToNotMatch);
    }

    @Override
    public boolean matches(final byte theByte) {
        return !(theByte == firstByteToNotMatch || theByte == secondByteToNotMatch);
    }

    @Override
    public byte[] getMatchingBytes() {
        final byte[] matchingBytes = new byte[getNumberOfMatchingBytes()];
        int matchIndex = 0;
        for (int byteValue = 0; byteValue < 256; byteValue++) {
            final byte theByte = (byte) (byteValue & 0xFF);
            if (!(theByte == firstByteToNotMatch || theByte == secondByteToNotMatch)) {
                matchingBytes[matchIndex++] = theByte;
            }
        }
        return matchingBytes;
    }

    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        if (getNumberOfMatchingBytes() == 1) {
            return String.format("^%02x", firstByteToNotMatch & 0xFF);
        }
        final String format = prettyPrint? "^[%02x %02x]" : "^[%02x%02x]";
        return String.format(format, firstByteToNotMatch & 0xFF, secondByteToNotMatch & 0xFF);
    }

    @Override
    public int getNumberOfMatchingBytes() {
        return (firstByteToNotMatch != secondByteToNotMatch)? 254 : 255;
    }

    @Override
    public SequenceMatcher repeat(int numberOfRepeats) {
        ArgUtils.checkPositiveInteger(numberOfRepeats);
        if (numberOfRepeats == 1) {
            return this;
        }
        if (getNumberOfMatchingBytes() == 255) {
            return new ByteMatcherSequenceMatcher(numberOfRepeats, OneByteInvertedMatcher.valueOf(firstByteToNotMatch));
        }
        return new ByteMatcherSequenceMatcher(numberOfRepeats, this);
    }

    @Override
    public int hashCode() {
        return firstByteToNotMatch * secondByteToNotMatch;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof TwoByteInvertedMatcher)) {
            return false;
        }
        final TwoByteInvertedMatcher other = (TwoByteInvertedMatcher) obj;
        return !( (firstByteToNotMatch == other.firstByteToNotMatch && secondByteToNotMatch == other.secondByteToNotMatch) ||
                  (firstByteToNotMatch == other.secondByteToNotMatch && secondByteToNotMatch == other.firstByteToNotMatch) );
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + String.format("%02x", firstByteToNotMatch & 0xFF) + ' ' +
                String.format("%02x", secondByteToNotMatch & 0xFF) + ')';
    }

    private static boolean isUpperCase(final byte theByte) {
        return ((theByte & 0xFF) >= 'A' && (theByte & 0xFF) <= 'Z');
    }

    private static boolean isLowerCase(final byte theByte) {
        return ((theByte & 0xFF) >= 'a' && (theByte & 0xFF) <= 'z');
    }

    private static boolean isLineBreak(final byte byte1, final byte byte2) {
        return ((byte1 == LINE_FEED && byte2 == CARRIAGE_RETURN) ||
                (byte1 == CARRIAGE_RETURN && byte2 == LINE_FEED));
    }

}
