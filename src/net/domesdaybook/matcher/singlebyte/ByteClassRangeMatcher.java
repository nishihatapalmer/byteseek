/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.matcher.sequence.Utilities;
import net.domesdaybook.reader.Bytes;

/**
 *
 * @author matt
 *
 * Only matches a single byte value-range at a time - not a sequence of them.
 * In practice, this isn't a major problem - we don't even have a single signature
 * that even uses them at present - provided for completeness, not optimisation.
 */
public class ByteClassRangeMatcher extends ByteClassMatcher implements SingleByteMatcher {

    final private int mMinByteValue; // use int as a byte is signed, but we need values from 0 to 255
    final private int mMaxByteValue; // use int as a byte is signed, but we need values from 0 to 255

    public ByteClassRangeMatcher( final int minValue, final int maxValue, final boolean negated ) {
        // Preconditions - minValue & maxValue >= 0 and <= 255.  MinValue <= MaxValue
        if (minValue > maxValue || minValue < 0 || minValue > 255 || maxValue < 0 || maxValue > 255 ) {
            throw new IllegalArgumentException("minimum or maximum values wrong way round or not between 0 and 255.");
        }
        mMinByteValue = minValue;
        mMaxByteValue = maxValue;
        this.negated = negated;
        if (negated) {
            this.numBytesInClass = 255 - mMaxByteValue + mMinByteValue;
        } else {
            this.numBytesInClass = mMaxByteValue - mMinByteValue + 1;
        }
    }


    @Override
    public final boolean matchesBytes(final Bytes reader, final long matchFrom) {
        // Since bytes are signed 8-bit values, but we need the value in the range
        // of 0-255, we cast the byte to an int, after boolean ANDing it with 255.
        // This ensures it is treated bit-wise (as if the 8th bit was not a signing bit).
        final int theByte = (int) ( reader.getByte(matchFrom) & 0xFF);
        final boolean insideRange = ( theByte >= mMinByteValue && theByte <= mMaxByteValue );
        // If inside the range, return 1 byte matched, otherwise 0 bytes matched.
        // Reverse this logic if negating (using bitwise XOR boolean logic).
        return ( insideRange ^ negated );
    }

    @Override
    public boolean matchesByte(byte theByte) {
        final boolean insideRange = ( theByte >= mMinByteValue && theByte <= mMaxByteValue );
        // If inside the range, return 1 byte matched, otherwise 0 bytes matched.
        // Reverse this logic if negating (using bitwise XOR boolean logic).
        return ( insideRange ^ negated );
    }


    @Override
    public final String toRegularExpression( final boolean prettyPrint ) {
        final StringBuffer regularExpression = new StringBuffer();
        if (prettyPrint) {
            regularExpression.append(" ");
        }
        regularExpression.append( "[" );
        if ( negated ) {
            regularExpression.append( "!" );
        }
        final String minValue = Utilities.byteValueToString(prettyPrint, mMinByteValue);
        final String maxValue = Utilities.byteValueToString(prettyPrint, mMaxByteValue);
        regularExpression.append( String.format( "%s:%s]", minValue, maxValue ));
        if (prettyPrint) {
            regularExpression.append(" ");
        }
        return regularExpression.toString();
    }

    @Override
    public byte[] getMatchingBytes() {
        byte[] values = new byte[numBytesInClass];
        if (negated) {
            int byteIndex = 0;
            for (int value = 0; value < mMinByteValue; value++) {
                values[byteIndex++] = (byte) value;
            }
            for (int value = mMaxByteValue+1; value < 256; value++) {
                values[byteIndex++] = (byte) value;
            }
        } else {
            int byteIndex = 0;
            for (int value = mMinByteValue; value <= mMaxByteValue; value++) {
                values[byteIndex++] = (byte) value;
            }
        }
        return values;
    }

    @Override
    public SingleByteMatcher getByteMatcherForPosition(int position) {
        return this;
    }



}
