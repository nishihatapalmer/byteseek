/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.matcher.sequence.Utilities;
import net.domesdaybook.reader.ByteReader;

/**
 *
 * @author matt
 *
 * Only matches a single byte value-range at a time - not a sequence of them.
 * In practice, this isn't a major problem - we don't even have a single signature
 * that even uses them at present - provided for completeness, not optimisation.
 */
public final class ByteClassRangeMatcher extends ByteClassMatcher implements SingleByteMatcher {

    private final int minByteValue; // use int as a byte is signed, but we need values from 0 to 255
    private final int maxByteValue; // use int as a byte is signed, but we need values from 0 to 255


    public ByteClassRangeMatcher(final int minValue, final int maxValue, final boolean negated ) {
        super(negated);
        // Preconditions - minValue & maxValue >= 0 and <= 255.  MinValue <= MaxValue
        if (minValue > maxValue || minValue < 0 || minValue > 255 || maxValue < 0 || maxValue > 255 ) {
            throw new IllegalArgumentException("minimum or maximum values wrong way round or not between 0 and 255.");
        }
        minByteValue = minValue;
        maxByteValue = maxValue;
        if (negated) {
            this.numBytesInClass = 255 - maxByteValue + minByteValue;
        } else {
            this.numBytesInClass = maxByteValue - minByteValue + 1;
        }
    }


    @Override
    public boolean matches(ByteReader reader, long matchFrom) {
        return matches(reader.getByte(matchFrom));
    }
    

    @Override
    public final boolean matches(byte theByte) {
        final boolean insideRange = (theByte >= minByteValue & theByte <= maxByteValue);
        return insideRange ^ negated;
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
        final String minValue = Utilities.byteValueToString(prettyPrint, minByteValue);
        final String maxValue = Utilities.byteValueToString(prettyPrint, maxByteValue);
        regularExpression.append( String.format( "%s:%s]", minValue, maxValue ));
        if (prettyPrint) {
            regularExpression.append(" ");
        }
        return regularExpression.toString();
    }

    @Override
    public final byte[] getMatchingBytes() {
        byte[] values = new byte[numBytesInClass];
        if (negated) {
            int byteIndex = 0;
            for (int value = 0; value < minByteValue; value++) {
                values[byteIndex++] = (byte) value;
            }
            for (int value = maxByteValue+1; value < 256; value++) {
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

  
}
