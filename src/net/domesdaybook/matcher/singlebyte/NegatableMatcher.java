/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

/**
 *
 * @author matt
 */
public abstract class NegatableMatcher implements SingleByteMatcher {
    
    protected final boolean negated;

    
    public NegatableMatcher(final boolean negated) {
        this.negated = negated;
    }


    public final boolean isNegated() {
        return negated;
    }

}
