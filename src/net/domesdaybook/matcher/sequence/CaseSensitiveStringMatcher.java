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
 * @author Matt Palmer
 */
public final class CaseSensitiveStringMatcher implements SequenceMatcher {

    private final byte[] byteArray;
    private final String caseSensitiveString;
    private final int length;

    public CaseSensitiveStringMatcher(final String caseSensitiveASCIIString) {
        this(caseSensitiveASCIIString, 1);
    }

    public CaseSensitiveStringMatcher(final String caseSensitiveASCIIString, final int numberToRepeat) {
        if (caseSensitiveASCIIString == null || caseSensitiveASCIIString.isEmpty()) {
            throw new IllegalArgumentException("Null or empty string passed in to CaseSensitiveStringMatcher.");
        }
        if (numberToRepeat < 1) {
            throw new IllegalArgumentException("CaseSensitiveStringMatcher requires a positive number of repeats.");
        }
        caseSensitiveString = repeatString(caseSensitiveASCIIString, numberToRepeat);
        final int byteSequenceLength = caseSensitiveString.length();
        byteArray = new byte[byteSequenceLength];
        for (int byteIndex = 0; byteIndex < byteSequenceLength; byteIndex++) {
            byteArray[byteIndex] = (byte) (caseSensitiveString.charAt(byteIndex));
        }
        length = byteArray.length;
    }

    
    private String repeatString(final String stringToRepeat, final int numberToRepeat) {
        if (numberToRepeat == 1) {
            return stringToRepeat;
        }
        StringBuilder builder = new StringBuilder();
        for (int count = 0; count < numberToRepeat; count++) {
            builder.append(stringToRepeat);
        }
        return builder.toString();
    }


    @Override
    public final boolean matches(final ByteReader reader, final long matchFrom) {
        boolean result = true;
        final byte[] localArray = byteArray;
        final int localStop = length;
        for (int byteIndex = 0; result && byteIndex < localStop; byteIndex++) {
            result = localArray[byteIndex] == reader.readByte( matchFrom + byteIndex);
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

    
    public String getCaseSensitiveString() {
        return caseSensitiveString;
    }


}

