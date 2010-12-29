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
public class ByteSequenceMatcher implements SequenceMatcher {

    private final byte[] byteArray;
    private final int length;


    public ByteSequenceMatcher( final byte[] byteArray ) {
        // Preconditions byteArray is not null:
        if ( byteArray == null ) {
            throw new IllegalArgumentException("Null byte array passed in to ByteHexMatcher");
        }
        this.byteArray = byteArray.clone(); // avoid mutability issues - clone byte array.
        length = byteArray.length;
    }


    @Override
    public final boolean matches(final ByteReader reader, final long matchFrom) {
        boolean result = true;
        final byte[] localArray = byteArray;
        final int localStop = length;
        for ( int byteIndex = 0; result && byteIndex < localStop; byteIndex++) {
            result = ( localArray[byteIndex] == reader.getByte( matchFrom + byteIndex ));
        }
        return result;
    }

    
    @Override
    public final int length() {
        return length;
    }


    @Override
    public final String toRegularExpression( final boolean prettyPrint ) {
        return Utilities.bytesToString(prettyPrint, byteArray);
    }


    @Override
    public final SingleByteMatcher getByteMatcherForPosition(int position) {
        return new ByteMatcher(byteArray[position]);
    }

}
