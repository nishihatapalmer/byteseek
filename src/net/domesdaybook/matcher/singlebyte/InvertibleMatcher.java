/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

/**
 * An abstract base class for SingleByteMatchers which can invert
 * the bytes the SingleByteMatcher is provided with.  For example,
 * if a SingleByteMatcher had a rule to match all the odd bytes, but
 * it was inverted, it would match all the even bytes.
 *
 * When extending this base class, careful attention must be paid to
 * the other interface methods, in particular getNumberOfMatchingBytes() and
 * getMatchingBytes(), as it is easy to forget to invert the number and set
 * of bytes returned by those methods, if the instance happens to be inverted.
 *
 * @author Matt Palmer
 */
public abstract class InvertibleMatcher implements SingleByteMatcher {
    
    protected final boolean inverted;

    
    /**
     * Constructs an InvertibleMatcher.
     * 
     * @param inverted Whether the matcher bytes are inverted or not.
     */
    public InvertibleMatcher(final boolean inverted) {
        this.inverted = inverted;
    }


    /**
     *
     * @return Whether the matcher bytes are inverted or not.
     */
    public final boolean isInverted() {
        return inverted;
    }

}
