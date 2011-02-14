/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import java.util.List;
import net.domesdaybook.reader.ByteReader;

/**
 * A {@link SingleByteMatcher} which matches a byte which
 * shares any of its bits with a bitmask.
 * 
 * @author Matt Palmer
 */
public final class AnyBitMaskMatcher implements SingleByteMatcher {

    final byte mBitMaskValue;

    /**
     * Constructs an immutable AnyBitMaskMatcher.
     *
     * @param bitMaskValue The bitmaskValue to match any of its bits against.
     */
    public AnyBitMaskMatcher(final byte bitMaskValue ) {
        mBitMaskValue = bitMaskValue;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean matches(ByteReader reader, long matchFrom) {
        return matches(reader.readByte(matchFrom));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean matches(byte theByte) {
        return (theByte & mBitMaskValue) != 0;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final String toRegularExpression(boolean prettyPrint) {
        final String regEx = String.format("~%02x", (int) 0xFF & mBitMaskValue);
        return prettyPrint ? " " + regEx + " " : regEx;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] getMatchingBytes() {
        final List<Byte> bytes = ByteUtilities.getBytesMatchingAnyBitMask(mBitMaskValue);
        return ByteUtilities.toArray(bytes);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final int getNumberOfMatchingBytes() {
        return ByteUtilities.countBytesMatchingAnyBit(mBitMaskValue);
    }


}
