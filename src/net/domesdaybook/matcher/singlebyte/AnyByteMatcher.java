/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.reader.ByteReader;

/**
 * A {@link SingleByteMatcher} which matches any byte at all.
 *
 * @author Matt Palmer
 */
public final class AnyByteMatcher implements SingleByteMatcher {

    // A static 256-element array containing all the bytes.
    private static final byte[] allBytes =  ByteUtilities.getAllByteValues();


    /**
     * Constructs an immutable AnyByteMatcher.
     */
    public AnyByteMatcher() {
    }


    /**
     * {@inheritDoc}
     *
     * Always returns true.
     */
    @Override
    public final boolean matches(byte theByte) {
        return true;
    }


    /**
     * {@inheritDoc}
     *
     * Returns a 256-element array of all the possible byte values.
     */
    @Override
    public final byte[] getMatchingBytes() {
        return allBytes;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final String toRegularExpression(boolean prettyPrint) {
        return prettyPrint ? " . " : ".";
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean matches(ByteReader reader, long matchFrom) {
        //return matches(reader.readByte(matchFrom));
        return true;
    }


    /**
     * {@inheritDoc}
     *
     * Always returns 256.
     */
    @Override
    public int getNumberOfMatchingBytes() {
        return 256;
    }

}
