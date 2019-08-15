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
 * A TwoByteMatcher is a {@link ByteMatcher} which matches two possible byte values.
 * <p>
 * There is also a static array of TwoByteMatchers corresonding to case insensitive ASCII alphabetic values,
 * since these are common types to match, and a static method to return an appropriate case insensitive matcher.
 *
 * @author Matt Palmer
 */
public final class TwoByteMatcher extends AbstractByteMatcher {

    private static TwoByteMatcher[] caseInsensitiveMatchers;
    public static TwoByteMatcher LINE_BREAK;

    static {
        caseInsensitiveMatchers = new TwoByteMatcher[26];
        for (int i = 0; i < 26; i++) {
            caseInsensitiveMatchers[i] = new TwoByteMatcher((byte) (i + 'a'), (byte) (i + 'A'));
        }
        LINE_BREAK = new TwoByteMatcher(ByteUtils.LINE_FEED, ByteUtils.CARRIAGE_RETURN);
    }

    /**
     * Returns an appropriate ByteMatcher given two bytes to match.
     * If both bytes are the same, then a OneByteMatcher is returned.
     * If both bytes are a case insensitive ASCII char, then a static case insensitive matcher is returned.
     * If both bytes form a line break / carriage return, then a static line break matcher is returned.
     * @param byte1 The first byte to match
     * @param byte2 The second byte to match
     * @return an appropriate ByteMatcher given two bytes to match.
     */
    public static ByteMatcher valueOf(final byte byte1, final byte byte2) {
        if (byte1 == byte2) {
            return OneByteMatcher.valueOf(byte1);
        }
        if (ByteUtils.isCaseInsensitive(byte1, byte2)) {
            return caseInsensitiveMatchers[Math.min(byte1, byte2) - 'A'];
        }
        if (ByteUtils.isLineBreak(byte1, byte2)) {
            return LINE_BREAK;
        }
        return new TwoByteMatcher(byte1, byte2);
    }

    /**
     * Returns a case insensitive TwoByteMather given a byte value, or a OneByteMatcher if the byte isn't
     * an alphabetic ASCII value.
     *
     * @param theByte The byte to get a case insensitive matcher for.
     * @return A case insensitive matcher for that byte value.
     */
    public static ByteMatcher caseInsensitive(final byte theByte) {
        if (ByteUtils.isUpperCase(theByte)) {
            return caseInsensitiveMatchers[((int) theByte & 0xFF) - 'A'];
        }
        if (ByteUtils.isLowerCase(theByte)) {
            return caseInsensitiveMatchers[((int) theByte & 0xFF) - 'a'];
        }
        return OneByteMatcher.valueOf(theByte);
    }

    /**
     * Returns a case insensitive TwoByteMatcher given a char value, or a OneByteMatcher if the char isn't an
     * alphabetic ASCII value.
     *
     * @param theChar The char to get a case insensitive matcher for.
     * @return A case insensitive matcher for that byte value.
     * @throws IllegalArgumentException if the char has a value greater than 255.
     */
    public static ByteMatcher caseInsensitive(final char theChar) {
        if (theChar >= 'A' && (theChar <= 'Z')) {
            return caseInsensitiveMatchers[theChar - 'A'];
        }
        if (theChar >= 'a' && (theChar <= 'z')) {
            return caseInsensitiveMatchers[theChar - 'a'];
        }
        if (theChar < 256) {
            return OneByteMatcher.valueOf((byte) theChar);
        }
        throw new IllegalArgumentException("A character must be between 0 and 255 in value, actual char was:" + (int) theChar);
    }

    /*
     * Class definition.
     */

    private final byte firstByteToMatch;
    private final byte secondByteToMatch;

    /**
     * Constructs an immutable TwoByteMatcher.
     * 
     * @param firstByteToMatch The first byte to match.
     * @param secondByteToMatch The second byte to match.
     */
    public TwoByteMatcher(final byte firstByteToMatch, final byte secondByteToMatch) {
        this.firstByteToMatch  = firstByteToMatch;
        this.secondByteToMatch = secondByteToMatch;
    }

    /**
     * Constructs an immutable TwoByteMatcher from hex representations of the bytes.
     * 
     * @param firstHexByte The first byte as a hex string.
     * @param secondHexByte The second byte as a hex string. 
     * @throws IllegalArgumentException if the string is not a valid 2-digit hex byte.
     */
    public TwoByteMatcher(final String firstHexByte, final String secondHexByte) {
        this.firstByteToMatch  = ByteUtils.byteFromHex(firstHexByte);
        this.secondByteToMatch = ByteUtils.byteFromHex(secondHexByte);
    }

    /**
     * Constructs an immutable TwoByteMatcher from a collection of bytes.
     * The collection must have two bytes in it.
     * 
     * @param twoBytes The collection of bytes to construct from.
     * @throws IllegalArgumentException if the collection of bytes is null, has null elements or
     *         does not have exactly two bytes in it.
     */
    public TwoByteMatcher(final Collection<Byte> twoBytes) {
    	ArgUtils.checkCollectionSizeNoNullElements(twoBytes, 2);
    	final Iterator<Byte> byteIterator = twoBytes.iterator();
    	this.firstByteToMatch = byteIterator.next();
    	this.secondByteToMatch = byteIterator.next();
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
        return windowByte == firstByteToMatch || windowByte == secondByteToMatch;
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
        	return theByte == firstByteToMatch || theByte == secondByteToMatch;
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
    	return theByte == firstByteToMatch || theByte == secondByteToMatch;
    }    

    @Override
    public boolean matches(final byte theByte) {
        return theByte == firstByteToMatch || theByte == secondByteToMatch;
    }

    @Override
    public byte[] getMatchingBytes() {
        return (firstByteToMatch != secondByteToMatch)? new byte[] {firstByteToMatch, secondByteToMatch}
    												  : new byte[] {firstByteToMatch};
    }

    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        if (getNumberOfMatchingBytes() == 1) {
            return String.format("%02x", firstByteToMatch & 0xFF);
        }
        final String format = prettyPrint? "%02x %02x" : "%02x%02x";
    	return '[' + String.format(format, firstByteToMatch  & 0xFF,
                                           secondByteToMatch & 0xFF) + ']';
    }

    @Override
    public int getNumberOfMatchingBytes() {
        return (firstByteToMatch != secondByteToMatch)? 2 : 1;
    }

    @Override    
    public SequenceMatcher repeat(int numberOfRepeats) {
        ArgUtils.checkPositiveInteger(numberOfRepeats);
        if (numberOfRepeats == 1) {
            return this;
        }
        if (getNumberOfMatchingBytes() == 1) {
        	return new ByteSequenceMatcher(firstByteToMatch, numberOfRepeats);
        }
        return new ByteMatcherSequenceMatcher(numberOfRepeats, this);
    }

    @Override
    public int hashCode() {
        return firstByteToMatch * secondByteToMatch;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof TwoByteMatcher)) {
            return false;
        }
        final TwoByteMatcher other = (TwoByteMatcher) obj;
        return (firstByteToMatch == other.firstByteToMatch && secondByteToMatch == other.secondByteToMatch) ||
                (firstByteToMatch == other.secondByteToMatch && secondByteToMatch == other.firstByteToMatch);
    }

}
