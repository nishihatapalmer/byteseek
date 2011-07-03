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
public final class AnyMatcher implements SingleByteMatcher {

    // A static 256-element array containing all the bytes.
    private static final byte[] allBytes =  ByteUtilities.getAllByteValues();


    /**
     * Constructs an immutable AnyMatcher.
     */
    public AnyMatcher() {
    }


    /**
     * {@inheritDoc}
     *
     * Always returns true.
     */
    @Override
    public boolean matches(final byte theByte) {
        return true;
    }
    

    /**
     * {@inheritDoc}
     *
     * Returns a 256-element array of all the possible byte values.
     */
    @Override
    public byte[] getMatchingBytes() {
        return allBytes;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        return prettyPrint ? " . " : ".";
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final ByteReader reader, final long matchFrom) {
        if (matchFrom >= reader.length() || matchFrom < 0) {
            final String message 
                = String.format("Cannot access byte %d in reader of size %d", matchFrom, reader.length());
            throw new IndexOutOfBoundsException(message);
        }
        return true;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchFrom) {
        if (matchFrom >= bytes.length || matchFrom < 0) {
            final String message 
                = String.format("Cannot access byte %d in array of size %d", matchFrom, bytes.length);
            throw new IndexOutOfBoundsException(message);
        }
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
