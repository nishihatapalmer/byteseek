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
public class AnyByteMatcher implements SingleByteMatcher {

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
     * Always returns a 256-element array of all the possible byte values.
     */
    @Override
    public final byte[] getMatchingBytes() {
        byte[] bytes = new byte[256];
        for (int count = 255; count >= 0; count--) {
            bytes[count] = (byte) count;
        }
        return bytes;
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
        return matches(reader.readByte(matchFrom));
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
