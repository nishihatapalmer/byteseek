/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

/**
 *
 * @author matt
 */
public abstract class NegatableMatcher implements SingleByteMatcher {
    
    protected boolean negated = false;

    public NegatableMatcher(final boolean negated) {
        this.negated = negated;
    }

    public final boolean isNegated() {
        return negated;
    }

}
