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

import net.byteseek.utils.ByteUtils;
import net.byteseek.io.reader.windows.Window;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.sequence.ByteSequenceMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.utils.ArgUtils;

/**
 * A OneByteMatcher is a {@link ByteMatcher} which matches one byte value only.
 *
 * @author Matt Palmer
 */
public final class OneByteMatcher extends AbstractByteMatcher {

    private final byte byteToMatch;

    private static final class MatcherCache {
  	  
  	  static final OneByteMatcher[] values = new OneByteMatcher[256];
  	  
  	  static {
  		  for (int i = 0; i < 256; i++) {
  			  values[i] = new OneByteMatcher((byte) i);
  		  }
  	  }
  	  
    }

    /**
     * Returns the corresponding OneByteMatcher for a byte value from a static cache.
     *
     * @param value The byte to get a byte matcher for.
     * @return The OneByteMatcher for the byte passed in.
     */
    public static OneByteMatcher valueOf(final byte value) {
  	  return MatcherCache.values[value & 0xff];
    }

    /**
     * Returns the corresponding OneByteMatcher for a char value from a static cache.
     *
     * @param value The byte to get a byte matcher for.
     * @return The OneByteMatcher for the byte passed in.
     * @throws IllegalArgumentException if the char is not an extended ASCII byte in the range 0-255.
     */
    public static OneByteMatcher valueOf(final char value) {
        ArgUtils.checkExtendedAsciiByte(value, "value");
        return MatcherCache.values[value & 0xFF];
    }

    /**
     * Returns the corresponding OneByteMatcher for an int value from a static cache.
     *
     * @param value The byte to get a byte matcher for.
     * @return The OneByteMatcher for the byte passed in.
     * @throws IllegalArgumentException if the int is not in the range 0 to 255.
     */
    public static OneByteMatcher valueOf(final int value) {
        ArgUtils.checkIntToByteRange(0, value);
        return MatcherCache.values[value];
    }
    
    /**
     * Constructs an immutable OneByteMatcher.
     * 
     * @param byteToMatch The byte to match.
     */
    public OneByteMatcher(final byte byteToMatch) {
        this.byteToMatch = byteToMatch;
    }

    /**
     * Constructs an immutable OneByteMatcher from a hex representation of a byte.
     * 
     * @param hexByte A 2 hex digit respreentation of a byte.
     * @throws IllegalArgumentException if the string is not a valid 2-digit hex byte.
     */
    public OneByteMatcher(final String hexByte) {
        this.byteToMatch = ByteUtils.byteFromHex(hexByte);
    }

    @Override
    public boolean matches(final WindowReader reader, final long matchPosition) throws IOException{
        final Window window = reader.getWindow(matchPosition);
        return window == null? false
               : window.getByte(reader.getWindowOffset(matchPosition)) == byteToMatch;
    }

    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        return matchPosition >= 0 && matchPosition < bytes.length &&
                bytes[matchPosition] == byteToMatch;
    }   

    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchPosition) {
        return bytes[matchPosition] == byteToMatch;
    }    

    @Override
    public boolean matches(final byte theByte) {
        return theByte == byteToMatch;
    }

    @Override
    public byte[] getMatchingBytes() {
        return new byte[] {byteToMatch};
    }

    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        return ByteUtils.byteToString(prettyPrint, byteToMatch & 0xFF);
    }

    @Override
    public int getNumberOfMatchingBytes() {
        return 1;
    }

    @Override    
    public SequenceMatcher repeat(int numberOfRepeats) {
        ArgUtils.checkPositiveInteger(numberOfRepeats, "numberOfRepeats");
        if (numberOfRepeats == 1) {
            return this;
        }   
        return new ByteSequenceMatcher(byteToMatch, numberOfRepeats);
    }

    @Override
    public int hashCode() {
        return byteToMatch;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof OneByteMatcher)) {
            return false;
        }
        final OneByteMatcher other = (OneByteMatcher) obj;
        return byteToMatch == other.byteToMatch;
    }

    public byte getByteValue() {
        return byteToMatch;
    }

}
