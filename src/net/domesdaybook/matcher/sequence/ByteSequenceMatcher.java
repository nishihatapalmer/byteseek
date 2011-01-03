/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */


package net.domesdaybook.matcher.sequence;

import java.util.List;
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


    public ByteSequenceMatcher(final byte[] byteArray ) {
        // Preconditions byteArray is not null:
        if ( byteArray == null ) {
            throw new IllegalArgumentException("Null byte array passed in to ByteSequenceMatcher");
        }
        this.byteArray = byteArray.clone(); // avoid mutability issues - clone byte array.
        length = byteArray.length;
    }

    
    public ByteSequenceMatcher(final List<Byte> byteList) {
        // Preconditions: list is not null and has at least one member:
        if (byteList == null || byteList.isEmpty()) {
            throw new IllegalArgumentException("Null or empty byte list passed in to ByteSequenceMatcher.");
        }
        this.byteArray = new byte[byteList.size()];
        int index = 0;
        for (Byte b : byteList) {
            this.byteArray[index++] = b;
        }
        length = byteArray.length;
    }


    public ByteSequenceMatcher(final byte byteValue) {
        this.byteArray = new byte[1];
        this.byteArray[0] = byteValue;
        length = 1;
    }


    @Override
    public final boolean matches(final ByteReader reader, final long matchFrom) {
        boolean result = true;
        final byte[] localArray = byteArray;
        final int localStop = length;
        for ( int byteIndex = 0; result && byteIndex < localStop; byteIndex++) {
            result = ( localArray[byteIndex] == reader.readByte( matchFrom + byteIndex ));
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
