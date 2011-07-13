/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher;

import net.domesdaybook.reader.ByteReader;

/**
 * An interface for classes that can match bytes from a given position.
 * <p>
 * Design notes:
 * <p/>
 * 1. Provide matching routines for both ByteReader objects and byte arrays.
 *    The reason for this is to allow more efficient matching directly on byte
 *    arrays, rather than every byte access going through a readByte function call.
 * <p/>
 *    Profiling has shown that these can be among the most frequent method calls,
 *    and even tiny changes in their performance lead to be big impacts on overall
 *    client code performance.
 * <p/>
 *    The downside is that this may lead to code duplication in the matchers.
 *
 * 
 * @author Matt Palmer
 */
public interface Matcher {

    /**
     * Returns whether there is a match or not at the given position in a ByteReader.
     * 
     * @param reader The {@link ByteReader} to read from.
     * @param matchPosition The position to try to match at.
     * @return Whether there is a match at the given position.
     */
    public boolean matches(final ByteReader reader, final long matchPosition);
    
    
    /**
     * Returns whether there is a match or not at the given position in a byte array.
     * 
     * @param bytes An array of bytes to read from.
     * @param matchPosition The position to try to match at.
     * @return Whether there is a match at the given position.
     */
    public boolean matches(final byte[] bytes, final int matchPosition);
}
