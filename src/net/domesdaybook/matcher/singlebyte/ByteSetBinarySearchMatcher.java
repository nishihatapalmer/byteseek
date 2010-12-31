/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import java.util.Arrays;
import java.util.Set;
import net.domesdaybook.matcher.sequence.Utilities;
import net.domesdaybook.reader.ByteReader;

/**
 *
 * @author matt
 */
public class ByteSetBinarySearchMatcher extends NegatableMatcher implements SingleByteMatcher {

    private byte[] bytes;

    
    public ByteSetBinarySearchMatcher(final Set<Byte> bytes, final boolean negated) {
        super(negated);
        this.bytes = new byte[bytes.size()];
        int byteIndex = 0;
        for (Byte b : bytes) {
            this.bytes[byteIndex++] = b;
        }
        Arrays.sort(this.bytes);
    }

    @Override
    public final boolean matches(byte theByte) {
        return Arrays.binarySearch(bytes, theByte) >= 0 ^ negated;
    }


    @Override
    public final byte[] getMatchingBytes() {
        return bytes;
    }


    @Override
    public final int getNumberOfMatchingBytes() {
        return bytes.length;
    }


    @Override
    public final String toRegularExpression(final boolean prettyPrint) {
        StringBuilder regularExpression = new StringBuilder();
        if ( prettyPrint ) {
            regularExpression.append(' ');
        }
        regularExpression.append("[");
        if (negated) {
            regularExpression.append("^");
        }
        int byteIndex = 0;
        while (byteIndex < bytes.length) {
            int byteValue = (int) bytes[byteIndex];

            // Look for ranges of values from this position:
            int lastValue = byteValue;
            int searchIndex;
            for (searchIndex = byteIndex + 1; searchIndex < bytes.length; searchIndex++) {
                int nextValue = (int) bytes[byteIndex];
                if (nextValue == lastValue+1) {
                    lastValue = nextValue;
                } else {
                    break;
                }
            }

            // If we have a range of more than 2 contiguous positions,
            // represent this as a range of values:
            if (lastValue - byteValue > 2) {
                final String minValue = Utilities.byteValueToString(prettyPrint, byteValue);
                final String maxValue = Utilities.byteValueToString(prettyPrint, lastValue);
                regularExpression.append( String.format("%s-%s", minValue, maxValue));
                byteIndex = searchIndex + 1;
            } else { // just write out this byte.
                final String byteVal = Utilities.byteValueToString(prettyPrint, byteValue);
                regularExpression.append( byteVal );
                byteIndex++;
            }
        }
        regularExpression.append("]");
        if (prettyPrint) {
            regularExpression.append(' ');
        }
        return regularExpression.toString();
    }


    @Override
    public final boolean matches(final ByteReader reader, final long matchFrom) {
        return matches(reader.getByte(matchFrom));
    }

}
