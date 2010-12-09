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
public class BitMaskMatcher implements SingleByteMatcher {

    byte mBitMaskValue;

    public BitMaskMatcher( final byte bitMaskValue ) {
        mBitMaskValue = bitMaskValue;
    }


    @Override
    public boolean matchesByte(byte theByte) {
        return (theByte & mBitMaskValue ) == mBitMaskValue;
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

}
