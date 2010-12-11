/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.matcher.sequence;

import net.domesdaybook.matcher.Matcher;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.reader.ByteReader;

/**
 *
 * @author matt
 */
 public interface SequenceMatcher extends Matcher {

    /**
     *
     * @param position the position in the byte matcher to return a dedicated byte matcher for.
     * @return a byte matcher for a single position.
     */
    public SingleByteMatcher getByteMatcherForPosition(final int position);

    
    /*
     * @returns Returns the length of a matching byte sequence.
     */
    public int length();

    
    /* @param prettyPrint whether to pretty print the regular expression with spacing.
     * @returns Returns a string containing a regular expression of the byte matcher.
     */
    public String toRegularExpression(final boolean prettyPrint);

}
