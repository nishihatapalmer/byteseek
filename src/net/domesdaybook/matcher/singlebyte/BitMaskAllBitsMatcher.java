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
public final class BitMaskAllBitsMatcher extends InvertibleMatcher implements SingleByteMatcher {

    final byte mBitMaskValue;

    
    /**
     * Constructs an immutable BitMaskAllBitsMatcher.
     *
     * @param bitMaskValue The bitmaskValue to match all of its bits against.
     */
    public BitMaskAllBitsMatcher(final byte bitMaskValue) {
        super(false);
        mBitMaskValue = bitMaskValue;
    }

    
    /**
     * Constructs an immutable BitMaskAllBitsMatcher.
     *
     * @param bitMaskValue The bitmaskValue to match all of its bits against.
     */
    public BitMaskAllBitsMatcher(final byte bitMaskValue, final boolean inverted) {
        super(inverted);
        mBitMaskValue = bitMaskValue;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final ByteReader reader, final long matchFrom) {
        final byte localbitmask = mBitMaskValue;
        return (matchFrom >= 0 && matchFrom < reader.length()) &&
               (((reader.readByte(matchFrom) & localbitmask) == localbitmask) ^ inverted);
    }

 
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchFrom) {
        final byte localbitmask = mBitMaskValue;
        return (matchFrom >= 0 && matchFrom < bytes.length) &&
               (((bytes[matchFrom] & localbitmask) == localbitmask) ^ inverted);
    }    
    

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte theByte) {
        final byte localbitmask = mBitMaskValue;
        return ((theByte & localbitmask ) == localbitmask) ^ inverted;
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        final String wrapper = inverted? "[^ &%02x]" : "&%02x";
        final String regEx = String.format(wrapper, (int) 0xFF & mBitMaskValue);
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
