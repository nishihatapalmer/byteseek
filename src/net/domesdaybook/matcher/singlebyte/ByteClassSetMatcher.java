/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import java.util.BitSet;
import java.util.List;
import net.domesdaybook.matcher.sequence.Utilities;

/**
 *
 * @author matt
 */
public final class ByteClassSetMatcher extends ByteClassMatcher implements SingleByteMatcher {

    private final BitSet byteValues = new BitSet(256);

    public ByteClassSetMatcher(List<Integer> sortedValues, boolean negated) {
        super(negated);
        for (int valueIndex = 0; valueIndex < sortedValues.size(); valueIndex++) {
            final int byteValue = sortedValues.get(valueIndex);
            byteValues.set(byteValue);
        }
        if (negated) {
            this.numBytesInClass = 256 - sortedValues.size();
        } else {
            this.numBytesInClass = sortedValues.size();
        }
    }

    @Override
    public boolean matchesByte(byte theByte) {
        return byteValues.get(theByte) ^ negated;
    }

    @Override
    public String toRegularExpression(boolean prettyPrint) {
        StringBuilder regularExpression = new StringBuilder();
        if ( prettyPrint ) {
            regularExpression.append(' ');
        }
        regularExpression.append("[");
        if ( negated ) {
            regularExpression.append("!");
        }
        int firstBitSetPosition = byteValues.nextSetBit(0);
        while ( firstBitSetPosition >= 0 && firstBitSetPosition < 256 ) {
            int lastBitSetPosition = byteValues.nextClearBit(firstBitSetPosition)-1;
            // If the next clear position doesn't exist, then all remaining values are set:
            if ( lastBitSetPosition < 0 ) {
                lastBitSetPosition = 255;
            }
            // If we have a range of more than 2 contiguous set positions,
            // represent this as a range of values:
            if ( lastBitSetPosition - firstBitSetPosition > 2 ) {
                final String minValue = Utilities.byteValueToString(prettyPrint, firstBitSetPosition);
                final String maxValue = Utilities.byteValueToString(prettyPrint, lastBitSetPosition);
                regularExpression.append( String.format("%s:%s", minValue, maxValue));
            } else { // less than 2 contiguous set positions - just write out a single byte:
                final String byteVal = Utilities.byteValueToString(prettyPrint, firstBitSetPosition);
                regularExpression.append( byteVal );
                lastBitSetPosition = firstBitSetPosition;
            }
            firstBitSetPosition = byteValues.nextSetBit(lastBitSetPosition+1);
        }
        regularExpression.append("]");
        if ( prettyPrint ) {
            regularExpression.append(' ');
        }
        return regularExpression.toString();
    }

    @Override
    public byte[] getMatchingBytes() {
        byte[] values = new byte[numBytesInClass];
        int byteIndex = 0;
        for (int value = 0; value < 256; value++) {
            if (byteValues.get(value) ^ negated) {
                values[byteIndex++] = (byte) value;
            }
        }
        return values;
    }

}
