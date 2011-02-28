/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.sequence;

import net.domesdaybook.matcher.Matcher;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;

/**
 * An extension to the {@link Matcher} interface to support sequences.
 *
 * @author Matt Palmer
 */
 public interface SequenceMatcher extends Matcher {

    /**
     * Returns a {@link SingleByteMatcher} which matches all the bytes at
     * the requested position in the sequence.
     *
     * @param position The position in the byte matcher to return a dedicated byte matcher for.
     * @return A SingleByteMatcher for the position in the sequence provided.
     */
    public SingleByteMatcher getByteMatcherForPosition(final int position);


    /**
     * Gets the length of the matching sequence.
     *
     * @return Returns the length of a matching byte sequence.
     */
    public int length();

    
    /**
     * Returns a regular expression representation of the matching sequence.
     * 
     * @param prettyPrint whether to pretty print the regular expression with spacing.
     * @return A string containing a regular expression of the byte matcher.
     */
    public String toRegularExpression(final boolean prettyPrint);

}
