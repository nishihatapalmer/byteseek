/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.sequence;

import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.matcher.singlebyte.ByteMatcher;
import net.domesdaybook.reader.ByteReader;

/**
 *
 * @author matt
 */
public class CaseSensitiveStringMatcher implements SequenceMatcher {

    private final byte[] byteArray;
    private final String caseSensitiveString;
    private final int length;

    public CaseSensitiveStringMatcher( final String caseSensitiveASCIIString ) {
        if (caseSensitiveASCIIString == null || caseSensitiveASCIIString.isEmpty()) {
            throw new IllegalArgumentException("Null or empty string passed in to CaseSensitiveStringMatcher.");
        }
        caseSensitiveString = caseSensitiveASCIIString;
        final int byteSequenceLength = caseSensitiveASCIIString.length();
        byteArray = new byte[byteSequenceLength];
        for (int byteIndex = 0; byteIndex < byteSequenceLength; byteIndex++) {
            byteArray[byteIndex] = (byte) (caseSensitiveASCIIString.charAt(byteIndex));
        }
        length = byteArray.length;
    }


    @Override
    public final boolean matches(final ByteReader reader, final long matchFrom) {
        boolean result = true;
        final byte[] localArray = byteArray;
        final int localStop = length;
        for (int byteIndex = 0; result && byteIndex < localStop; byteIndex++) {
            result = localArray[byteIndex] == reader.getByte( matchFrom + byteIndex);
        }
        return result;
    }


    @Override
    public final int length() {
        return length;
    }


    @Override
    public final String toRegularExpression( final boolean prettyPrint ) {
        if (prettyPrint) {
            return " '" + caseSensitiveString + "' ";
        }
        return "'" + caseSensitiveString + "'";
    }


    @Override
    public final SingleByteMatcher getByteMatcherForPosition(int position) {
        return new ByteMatcher(byteArray[position]);
    }


}

