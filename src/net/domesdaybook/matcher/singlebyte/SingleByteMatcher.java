/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.matcher.sequence.SequenceMatcher;

/**
 * An interface for classes which match a single provided byte.
 * This does not mean that a SingleByteMatcher can only match a single
 * byte value - for example, it could match a single byte against all the
 * odd bytes, or a set of bytes, or a range of bytes.
 * <p>
 * All implementations of this interface should be immutable.
 * This allows them to be safely shared amongst other classes and threads.
 * 
 * @author Matt Palmer
 */
public interface SingleByteMatcher extends SequenceMatcher {

    /*
     * Implementations of this method should strive to be as efficient as possible.
     *
     * @return Whether a given byte matches the byte matcher.
     */
    boolean matches(final byte theByte);

    
    /**
     * Implementations of this method can be calculated dynamically,
     * and may not be efficient if called repeatedly.
     *
     * @return An array of all the bytes that this byte matcher could match.
     */
    byte[] getMatchingBytes();


    /**
     * Implementations of this method can be calculated dynamically,
     * and may not be efficient if called repeatedly.
     *
     * @return The number of bytes this byte matcher will match.
     */
    int getNumberOfMatchingBytes();
    

    /**
     * Implementations of this method can be calculated dynamically,
     * and may not be efficient if called repeatedly.
     *
     * @return A string representation of a regular expression for this matcher.
     */
    String toRegularExpression(final boolean prettyPrint);

}
