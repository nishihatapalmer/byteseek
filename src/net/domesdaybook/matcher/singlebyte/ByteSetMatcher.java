/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.domesdaybook.matcher.sequence.Utilities;
import net.domesdaybook.reader.ByteReader;

/**
 *
 * @author matt
 */
public final class ByteSetMatcher extends NegatableMatcher implements SingleByteMatcher {

    private final BitSet byteValues = new BitSet(256);

    public ByteSetMatcher(Set<Byte> values, boolean negated) {
        super(negated);
        for (Byte b : values) {
            byteValues.set(b);
        }
    }


   /**
     *
     * @param setValues The set of byte values to match.
     * @param negated   Whether the set values are negated or not
     * @return A SingleByteMatcher which is optimal for that set of bytes.
     */
    public static SingleByteMatcher buildMatcher(Set<Byte> setValues, boolean negated) {
        SingleByteMatcher result = null;

        if (setValues.size() == 1 && !negated) {
            for (Byte byteToMatch : setValues) {
                result = new ByteMatcher(byteToMatch);
            }
        } else if (setValues.size() > 0) {

            // Determine if all the values lie in a single range:
            final List<Byte> bytes = new ArrayList<Byte>(setValues);
            Collections.sort(bytes);
            final int lastValuePosition = bytes.size() -1;
            final int firstValue = bytes.get(0);
            final int lastValue = bytes.get(lastValuePosition);
            if (lastValue - firstValue == lastValuePosition) {  // values lie in a contiguous range
                result = new ByteSetRangeMatcher(firstValue, lastValue, negated);
            } else  // values do not lie in a contiguous range.
            if (bytes.size() < 16) { // small number of bytes in set - use binary searcher:
                result = new ByteSetBinarySearchMatcher(setValues, negated);
            } else {
                result = new ByteSetMatcher(setValues, negated);
            }
        }

        return result;
    }


    @Override
    public final boolean matches(ByteReader reader, long matchFrom) {
        return matches(reader.getByte(matchFrom));
    }  

    @Override
    public final boolean matches(byte theByte) {
        return byteValues.get(theByte) ^ negated;
    }

    @Override
    public final String toRegularExpression(boolean prettyPrint) {
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
    public final byte[] getMatchingBytes() {
        byte[] values = new byte[getNumberOfMatchingBytes()];
        int byteIndex = 0;
        for (int value = 0; value < 256; value++) {
            if (byteValues.get(value) ^ negated) {
                values[byteIndex++] = (byte) value;
            }
        }
        return values;
    }



    @Override
    public final int getNumberOfMatchingBytes() {
        return negated ? 256 - byteValues.cardinality() : byteValues.cardinality();
    }

}
