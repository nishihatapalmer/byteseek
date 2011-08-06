/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.bytes.ByteUtilities;
import java.util.List;
import net.domesdaybook.reader.ByteReader;

/**
 * A {@link SingleByteMatcher} which matches a byte which
 * shares any of its bits with a bitmask.
 * 
 * @author Matt Palmer
 */
public final class BitMaskAnyBitsMatcher extends InvertibleMatcher {

    final byte mBitMaskValue;


     /**
     * Constructs an immutable BitMaskAnyBitsMatcher.
     *
     * @param bitMaskValue The bitmaskValue to match any of its bits against.
     */
    public BitMaskAnyBitsMatcher(final byte bitMaskValue) {
        super(false);
        mBitMaskValue = bitMaskValue;
    }

    /**
     * Constructs an immutable BitMaskAnyBitsMatcher.
     *
     * @param bitMaskValue The bitmaskValue to match any of its bits against.
     * @param inverted Whether the result of a match should be inverted.
     */
    public BitMaskAnyBitsMatcher(final byte bitMaskValue, boolean inverted) {
        super(inverted);
        mBitMaskValue = bitMaskValue;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final ByteReader reader, final long matchFrom) {
        return (matchFrom >= 0 && matchFrom < reader.length()) &&
                (((reader.readByte(matchFrom) & mBitMaskValue) != 0) ^ inverted);
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchFrom) {
        return (matchFrom >= 0 && matchFrom < bytes.length) &&
                (((bytes[matchFrom] & mBitMaskValue) != 0) ^ inverted);
    }    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte theByte) {
        return ((theByte & mBitMaskValue) != 0) ^ inverted;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        final String wrapper = inverted? "[^ ~%02x]" : "~%02x";
        final String regEx = String.format(wrapper, (int) 0xFF & mBitMaskValue);
        return prettyPrint ? " " + regEx + " " : regEx;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getMatchingBytes() {
        final List<Byte> bytes = ByteUtilities.getBytesMatchingAnyBitMask(mBitMaskValue);
        return ByteUtilities.toArray(bytes);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfMatchingBytes() {
        return ByteUtilities.countBytesMatchingAnyBit(mBitMaskValue);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchesNoBoundsCheck(final ByteReader reader, final long matchFrom) {
        return ((reader.readByte(matchFrom) & mBitMaskValue) != 0) ^ inverted;
    }
        
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchFrom) {
        return ((bytes[matchFrom] & mBitMaskValue) != 0) ^ inverted;
    }


}
