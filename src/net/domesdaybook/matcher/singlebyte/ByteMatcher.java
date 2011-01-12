/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.reader.ByteReader;


/**
 * A ByteMatcher is a {@link SingleByteMatcher} which matches
 * one byte value only.
 *
 * @author Matt Palmer
 */
public final class ByteMatcher implements SingleByteMatcher {

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
    public final boolean matches(final byte theByte) {
        return theByte == byteToMatch;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] getMatchingBytes() {
        return new byte[] {byteToMatch};
    }


     /**
     * {@inheritDoc}
     */
    @Override
    public final String toRegularExpression(boolean prettyPrint) {
        final String regex = ByteUtilities.byteToString(prettyPrint, byteToMatch & 0xFF);
        return prettyPrint? regex + " " : regex;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final int getNumberOfMatchingBytes() {
        return 1;
    }

}
