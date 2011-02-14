/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import java.util.List;
import net.domesdaybook.reader.ByteReader;

/**
 * A {@link SingleByteMatcher} which matches a byte which
 * shares all of its bits with a bitmask.
 *
 * @author Matt Palmer
 */
public final class AllBitMaskMatcher implements SingleByteMatcher {

    final byte mBitMaskValue;


    /**
     * Constructs an immutable AllBitMaskMatcher.
     *
     * @param bitMaskValue The bitmaskValue to match all of its bits against.
     */
    public AllBitMaskMatcher(final byte bitMaskValue ) {
        mBitMaskValue = bitMaskValue;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final ByteReader reader, final long matchFrom) {
        return matches(reader.readByte(matchFrom));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte theByte) {
        final byte localbitmask = mBitMaskValue;
        return (theByte & localbitmask ) == localbitmask;
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        final String regEx = String.format("&%02x", (int) 0xFF & mBitMaskValue);
        return prettyPrint ? " " + regEx + " " : regEx;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getMatchingBytes() {
        final List<Byte> bytes = ByteUtilities.getBytesMatchingAllBitMask(mBitMaskValue);
        return ByteUtilities.toArray(bytes);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfMatchingBytes() {
        return ByteUtilities.countBytesMatchingAllBits(mBitMaskValue);
    }

}
