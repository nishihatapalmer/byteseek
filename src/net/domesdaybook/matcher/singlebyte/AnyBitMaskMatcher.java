/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import java.util.ArrayList;
import java.util.List;
import net.domesdaybook.reader.ByteReader;

/**
 *
 * @author matt
 */
public class AnyBitMaskMatcher implements SingleByteMatcher {

    final byte mBitMaskValue;

    public AnyBitMaskMatcher( final byte bitMaskValue ) {
        mBitMaskValue = bitMaskValue;
    }

    @Override
    public final boolean matches(ByteReader reader, long matchFrom) {
        return matches(reader.getByte(matchFrom));
    }


    @Override
    public final boolean matches(byte theByte) {
        return (theByte & mBitMaskValue) > 0;
    }



    @Override
    public final String toRegularExpression(boolean prettyPrint) {
        final String regEx = String.format("~%02x", (int) 0xFF & mBitMaskValue);
        return prettyPrint ? " " + regEx + " " : regEx;
    }


    @Override
    public final byte[] getMatchingBytes() {
        final List<Byte> bytes = new ArrayList<Byte>();
        // start loop at one - any bitmask matchers can never match the zero byte.
        for (int byteIndex = 1; byteIndex < 256; byteIndex++) {
            final byte byteValue = (byte) byteIndex;
            if ((byteValue & mBitMaskValue) > 0) {
                bytes.add((byte) byteIndex);
            }
        }
        final int numBytes = bytes.size();
        final byte[] values = new byte[numBytes];
        for (int index = 0; index < numBytes; index++) {
            //int val = bytes.get(index);
            //values[index] = (byte) val;
            values[index] = bytes.get(index);
        }
        return values;
    }


    @Override
    public final int getNumberOfMatchingBytes() {

        // 00000000 - 0
        // 00000001 - 128
        // 00111111 -
        // 00111111 - zero & four other values can't match - 251;
        // 01111111 - zero can't match, 128 can't match    - 254
        // 11111110 - zero can't match, 1 can't match      - 254
        // 11111111 - all except zero                      - 255

        return BitUtilities.countBytesMatchingAnyBit(mBitMaskValue);
    }


}
