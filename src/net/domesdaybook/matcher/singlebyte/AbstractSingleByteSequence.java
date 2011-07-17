/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.matcher.sequence.SequenceMatcher;

/**
 *
 * @author matt
 */
public abstract class AbstractSingleByteSequence implements SingleByteMatcher {
    
 /**
     * @inheritDoc
     * 
     * Returns this for position 0, null otherwise.
     */
    @Override
    public SingleByteMatcher getByteMatcherForPosition(final int position) {
        return position == 0 ? this : null;
    }

    
    /**
     * {@inheritDoc}
     *
     * Always returns 1.
     */ 
    @Override
    public int length() {
        return 1;
    }
    

    /**
     * {@inheritDoc}
     *
     * Always returns this.
     */ 
    @Override
    public SequenceMatcher reverse() {
        return this;
    }    
    
    
}
