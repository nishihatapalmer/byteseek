/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.bytes.ByteUtilities;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.ByteReader;

/**
 * A {@link SingleByteMatcher} which matches any byte at all.
 *
 * @author Matt Palmer
 */
public final class AnyMatcher extends AbstractSingleByteSequence {

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
        return matchFrom >= 0 && matchFrom < reader.length();
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchFrom) {
        return matchFrom >= 0 && matchFrom < bytes.length;
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


    /**
     * {@inheritDoc}
     *
     * Always true
     */ 
    @Override
    public boolean matchesNoBoundsCheck(final ByteReader reader, final long matchPosition) {
        return true;
    }

    
    /**
     * {@inheritDoc}
     *
     * Always true
     */ 
    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchPosition) {
        return true;
    }

}
