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
public class ByteSequenceMatcher implements SequenceMatcher {

    private final byte[] mByteArray;
    //private final byte[] mBuffer;
    private final int mByteSequenceLength;

    ByteSequenceMatcher( final byte[] byteArray ) {
        // Preconditions byteArray is not null:
        if ( byteArray == null ) {
            throw new IllegalArgumentException("Null byte array passed in to ByteHexMatcher");
        }
        mByteArray = byteArray.clone(); // avoid mutability issues - clone byte array.
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
  /*
        theIDFile.getBytes( matchFrom, mByteSequenceLength, mBuffer);
        return java.util.Arrays.equals( mByteArray, mBuffer );
  */
    }

    @Override
    public final int length() {
        return mByteSequenceLength;
    }


   // Utility method to parse a hex byte string into a ByteValueSequenceMatcher.
    public static ByteSequenceMatcher fromExpression( final String hexByteString ) {
     // Preconditions: not null, empty and is an even number of chars
        if ( hexByteString == null || hexByteString.isEmpty() ) {
            throw new IllegalArgumentException("Null or empty hexByteSequence.");
        }
        final int stringLength = hexByteString.length();
        if ( stringLength % 2 != 0) {
            throw new IllegalArgumentException("Odd number of chars in hex byte string.");
        }

        // Build the byte sequence:
        final int byteSequenceLength = stringLength / 2;
        byte[] theBytes = new byte[byteSequenceLength];
        try {
            for (int byteIndex = 0; byteIndex < byteSequenceLength; byteIndex++) {
                // Will throw a NumberFormatException if it doesn't find a hex byte.
                final int byteVal = Integer.parseInt(hexByteString.substring(2 * byteIndex, 2 * (byteIndex + 1)), 16);
                theBytes[byteIndex] = (byte) (byteVal);
            }
        }
        catch ( NumberFormatException formatEx ) {
            throw new IllegalArgumentException("Hex bytes not specified properly in hex byte string.");
        }

        return new ByteSequenceMatcher( theBytes );
    }

    @Override
    public final String toRegularExpression( final boolean prettyPrint ) {
        return Utilities.bytesToString(prettyPrint, mByteArray);
    }


    @Override
    public SingleByteMatcher getByteMatcherForPosition(int position) {
        return new ByteMatcher(mByteArray[position]);
    }

}
