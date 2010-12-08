/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.reader.Bytes;
import java.util.ArrayList;
import java.util.List;
import net.domesdaybook.matcher.sequence.SequenceMatcher;

/**
 *
 * @author matt
 */
public class BitMaskMatcher implements SequenceMatcher, SingleByteMatcher {

    byte mBitMaskValue;

    BitMaskMatcher( final byte bitMaskValue ) {
        mBitMaskValue = bitMaskValue;
    }

    @Override
    public boolean matchesBytes(Bytes reader, long matchFrom) {
        final byte theByte = reader.getByte(matchFrom);
        return (theByte & mBitMaskValue ) == mBitMaskValue;
    }

    @Override
    public boolean matchesByte(byte theByte) {
        return (theByte & mBitMaskValue ) == mBitMaskValue;
    }
    

    @Override
    public int length() {
        return 1; // bit masks are always one byte.
    }

    public static BitMaskMatcher fromExpression( final String hexBitMask ) {

        // Preconditions: not null or empty, begins and ends with square brackets:
        if ( hexBitMask == null || hexBitMask.isEmpty() ||
             !(hexBitMask.startsWith("&")) && hexBitMask.length() == 3) {
            throw new IllegalArgumentException("Invalid bitmask.");
        }
        
        BitMaskMatcher matcher = null;
        try {
            final byte value  = (byte) ( 0xFF & Integer.parseInt(hexBitMask.substring(1),16));
            matcher = new BitMaskMatcher( value );
        }
        catch ( NumberFormatException num ) {
            throw new IllegalArgumentException( "Bit mask not specified as & hex byte.");
        }
        return matcher;
    }

    @Override
    public String toRegularExpression(boolean prettyPrint) {
        String regEx = String.format("&%02x", (int) 0xFF & mBitMaskValue);;
        if ( prettyPrint ) {
            regEx = " " + regEx + " ";
        }
        return regEx;
    }

    @Override
    public byte[] getMatchingBytes() {
        List<Integer> bytes = new ArrayList<Integer>();
        for (int byteIndex = 0; byteIndex < 256; byteIndex++) {
            final byte byteValue = (byte) byteIndex;
            if ((byteValue & mBitMaskValue) == mBitMaskValue) {
                bytes.add(byteIndex);
            }
        }
        byte[] values = new byte[bytes.size()];
        for (int index = 0; index < bytes.size(); index++) {
            int val = bytes.get(index);
            values[index] = (byte) val;
        }
        return values;
    }

    @Override
    public SingleByteMatcher getByteMatcherForPosition(int position) {
        return this;
    }



}
