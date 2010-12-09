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

    public ByteSequenceMatcher( final byte[] byteArray ) {
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


    @Override
    public final String toRegularExpression( final boolean prettyPrint ) {
        return Utilities.bytesToString(prettyPrint, mByteArray);
    }


    @Override
    public SingleByteMatcher getByteMatcherForPosition(int position) {
        return new ByteMatcher(mByteArray[position]);
    }

}
