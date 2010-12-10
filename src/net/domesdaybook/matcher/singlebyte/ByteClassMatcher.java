/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

/**
 *
 * @author matt
 */
public abstract class ByteClassMatcher implements SingleByteMatcher {
    
    protected boolean negated = false;
    protected int numBytesInClass = 0;

    public ByteClassMatcher(final boolean negated) {
        this.negated = negated;
    }

    public final boolean isNegated() {
        return negated;
    }

    public final int getNumBytesInClass() {
        return numBytesInClass;
    }

}
