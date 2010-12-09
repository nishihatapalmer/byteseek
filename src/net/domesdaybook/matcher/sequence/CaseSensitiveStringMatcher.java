/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.matcher.sequence;

import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.matcher.singlebyte.ByteMatcher;
import net.domesdaybook.reader.Bytes;

/**
 *
 * @author matt
 */
public class CaseSensitiveStringMatcher implements SequenceMatcher {


    private final byte[] mByteArray;
    private final String caseSensitiveString;
    private final int mByteSequenceLength;

    public CaseSensitiveStringMatcher( final String caseSensitiveASCIIString ) {
        // Build the byte sequence:
        caseSensitiveString = caseSensitiveASCIIString;
        final int byteSequenceLength = caseSensitiveASCIIString.length();
        mByteArray = new byte[byteSequenceLength];
        for (int byteIndex = 0; byteIndex < byteSequenceLength; byteIndex++) {
            mByteArray[byteIndex] = (byte) (caseSensitiveASCIIString.charAt(byteIndex));
        }
        mByteSequenceLength = mByteArray.length;
    }

    @Override
    public final boolean matchesBytes(final Bytes reader, final long matchFrom) {
        boolean result = true;
        final byte[] localArray = mByteArray;
        final int localStop = mByteSequenceLength;
        for ( int byteIndex = 0; result && byteIndex < localStop; byteIndex++) {
            result = ( localArray[byteIndex] == reader.getByte( matchFrom + byteIndex ));
        }
        return result;
    }

    @Override
    public final int length() {
        return mByteSequenceLength;
    }


    @Override
    public final String toRegularExpression( final boolean prettyPrint ) {
        if (prettyPrint) {
            return " '" + caseSensitiveString + "' ";
        }
        return "'" + caseSensitiveString + "'";
    }


    @Override
    public SingleByteMatcher getByteMatcherForPosition(int position) {
        return new ByteMatcher(mByteArray[position]);
    }


}

