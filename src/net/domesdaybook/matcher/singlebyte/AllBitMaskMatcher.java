/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import java.util.List;
import net.domesdaybook.reader.ByteReader;

/**
 *
 * @author matt
 */
public class AllBitMaskMatcher implements SingleByteMatcher {

    final byte mBitMaskValue;

    public AllBitMaskMatcher( final byte bitMaskValue ) {
        mBitMaskValue = bitMaskValue;
    }

    @Override
    public final boolean matches(ByteReader reader, long matchFrom) {
        return matches(reader.getByte(matchFrom));
    }


    @Override
    public final boolean matches(byte theByte) {
        return (theByte & mBitMaskValue ) == mBitMaskValue;
    }
    


    @Override
    public final String toRegularExpression(boolean prettyPrint) {
        final String regEx = String.format("&%02x", (int) 0xFF & mBitMaskValue);
        return prettyPrint ? " " + regEx + " " : regEx;
    }

    
    @Override
    public final byte[] getMatchingBytes() {
        final List<Byte> bytes = BitUtilities.getBytesMatchingAllBitMask(mBitMaskValue);
        final int numBytes = bytes.size();
        final byte[] values = new byte[numBytes];
        for (int index = 0; index < numBytes; index++) {
            values[index] = bytes.get(index);
        }
        return values;
    }


    @Override
    public final int getNumberOfMatchingBytes() {
        return BitUtilities.countBytesMatchingAllBits(mBitMaskValue);
    }


}
