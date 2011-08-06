/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.bytes.ByteUtilities;
import net.domesdaybook.reader.ByteReader;


/**
 * A ByteMatcher is a {@link SingleByteMatcher} which matches
 * one byte value only.
 *
 * @author Matt Palmer
 */
public final class ByteMatcher extends AbstractSingleByteSequence {

    private final byte byteToMatch;


    /**
     * Constructs an immutable ByteMatcher.
     * 
     * @param byteToMatch The byte to match.
     */
    public ByteMatcher(final byte byteToMatch) {
        this.byteToMatch = byteToMatch;
    }
    
    
    /**
     * Constructs an immutable ByteMatcher from a hex representation of a byte.
     * 
     * @param hexByte 
     * @throws IllegalArgumentException if the string is not a valid 2-digit hex byte.
     */
    public ByteMatcher(final String hexByte) {
        this.byteToMatch = ByteUtilities.byteFromHex(hexByte);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final ByteReader reader, final long matchFrom) {
        return matchFrom >= 0 && matchFrom <= reader.length() &&
                reader.readByte(matchFrom) == byteToMatch;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchFrom) {
        return matchFrom >= 0 && matchFrom < bytes.length &&
                bytes[matchFrom] == byteToMatch;
    }   
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchesNoBoundsCheck(final ByteReader reader, final long matchPosition) {
        return reader.readByte(matchPosition) == byteToMatch;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchPosition) {
        return bytes[matchPosition] == byteToMatch;
    }    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte theByte) {
        return theByte == byteToMatch;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getMatchingBytes() {
        return new byte[] {byteToMatch};
    }


     /**
     * {@inheritDoc}
     */
    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        final String regex = ByteUtilities.byteToString(prettyPrint, byteToMatch & 0xFF);
        return prettyPrint? regex + " " : regex;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfMatchingBytes() {
        return 1;
    }


}
