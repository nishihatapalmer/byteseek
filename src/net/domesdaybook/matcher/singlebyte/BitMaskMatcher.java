/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import java.util.ArrayList;
import java.util.List;
import net.domesdaybook.reader.Bytes;

/**
 *
 * @author matt
 */
public class BitMaskMatcher implements SingleByteMatcher {

    final byte mBitMaskValue;

    public BitMaskMatcher( final byte bitMaskValue ) {
        mBitMaskValue = bitMaskValue;
    }

    @Override
    public boolean matches(Bytes reader, long matchFrom) {
        return matches(reader.getByte(matchFrom));
    }


    @Override
    public final boolean matches(byte theByte) {
        return (theByte & mBitMaskValue ) == mBitMaskValue;
    }
    


    @Override
    public final String toRegularExpression(boolean prettyPrint) {
        String regEx = String.format("&%02x", (int) 0xFF & mBitMaskValue);
        if ( prettyPrint ) {
            regEx = " " + regEx + " ";
        }
        return regEx;
    }

    @Override
    public final byte[] getMatchingBytes() {
        List<Byte> bytes = new ArrayList<Byte>();
        for (int byteIndex = 0; byteIndex < 256; byteIndex++) {
            final byte byteValue = (byte) byteIndex;
            if ((byteValue & mBitMaskValue) == mBitMaskValue) {
                bytes.add((byte) byteIndex);
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
    public final int getNumberOfMatchingBytes() {
        return 1 << (8-Utilities.countSetBits(mBitMaskValue));
    }





}
