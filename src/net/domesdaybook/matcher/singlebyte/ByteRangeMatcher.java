/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
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

package net.domesdaybook.matcher.singlebyte;

import java.io.IOException;
import net.domesdaybook.bytes.ByteUtilities;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.reader.Window;

/**
 * An immutable {@link SingleByteMatcher} which matches a range of bytes, 
 * or bytes outside the range if inverted.
 * 
 * @author Matt Palmer
 *
 */
public final class ByteRangeMatcher extends InvertibleMatcher {

    private final static String ILLEGAL_ARGUMENTS = "Values must be between 0 and 255 inclusive: min=%d max=%d";

    private final int minByteValue; // use int as a byte is signed, but we need values from 0 to 255
    private final int maxByteValue; // use int as a byte is signed, but we need values from 0 to 255

    /**
     * Constructs an immutable {@link SingleByteMatcher} which matches a range of bytes.
     * If the minimum value is greater than the maximum value, then the values are reversed.
     *
     * @param minValue The minimum value to match.
     * @param maxValue The maximum value to match.
     * @param inverted If true, the matcher matches values outside the range given.
     * @throws {@link IllegalArgumentException} if the values are not between 0-255.
     */
    public ByteRangeMatcher(final int minValue, final int maxValue, final boolean inverted) {
        super(inverted);
        // Preconditions - minValue & maxValue >= 0 and <= 255.  MinValue <= MaxValue
        if (minValue < 0 || minValue > 255 || maxValue < 0 || maxValue > 255 ) {
            final String error = String.format(ILLEGAL_ARGUMENTS, minValue, maxValue);
            throw new IllegalArgumentException(error);
        }
        if (minValue > maxValue) {
            minByteValue = maxValue;
            maxByteValue = minValue;
        } else {
            minByteValue = minValue;
            maxByteValue = maxValue;
        }
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final Reader reader, final long matchFrom) 
            throws IOException{
        final Window window = reader.getWindow(matchFrom);
        if (window != null) {
            final int byteValue = window.getByte(reader.getWindowOffset(matchFrom)) & 0xFF;
            final boolean insideRange = byteValue >= minByteValue && byteValue <= maxByteValue;
            return insideRange ^ inverted;
        }
        return false;
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchFrom) {
        if (matchFrom >= 0 && matchFrom < bytes.length) {
            final int byteValue = bytes[matchFrom] & 0xFF;
            final boolean insideRange = byteValue >= minByteValue && byteValue <= maxByteValue;
            return insideRange ^ inverted;
        }
        return false;
    }    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchPosition) {
        final int byteValue = bytes[matchPosition] & 0xFF;
        final boolean insideRange = byteValue >= minByteValue && byteValue <= maxByteValue;
        return insideRange ^ inverted;
    }    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte theByte) {
        final int byteValue = theByte & 0xFF;
        final boolean insideRange = (byteValue >= minByteValue && byteValue <= maxByteValue);
        return insideRange ^ inverted;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        final StringBuffer regularExpression = new StringBuffer();
        if (prettyPrint) {
            regularExpression.append(" ");
        }
        regularExpression.append( "[" );
        if (inverted) {
            regularExpression.append( "^" );
        }
        final String minValue = ByteUtilities.byteToString(prettyPrint, minByteValue);
        final String maxValue = ByteUtilities.byteToString(prettyPrint, maxByteValue);
        regularExpression.append( String.format( "%s-%s]", minValue, maxValue ));
        if (prettyPrint) {
            regularExpression.append(" ");
        }
        return regularExpression.toString();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getMatchingBytes() {
        byte[] values = new byte[getNumberOfMatchingBytes()];
        if (inverted) {
            int byteIndex = 0;
            for (int value = 0; value < minByteValue; value++) {
                values[byteIndex++] = (byte) value;
            }
            for (int value = maxByteValue + 1; value < 256; value++) {
                values[byteIndex++] = (byte) value;
            }
        } else {
            int byteIndex = 0;
            for (int value = minByteValue; value <= maxByteValue; value++) {
                values[byteIndex++] = (byte) value;
            }
        }
        return values;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfMatchingBytes() {
        return inverted ? 255 - maxByteValue + minByteValue
                        : maxByteValue - minByteValue + 1;
    }
  
}
