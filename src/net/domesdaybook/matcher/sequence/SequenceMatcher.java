/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.matcher.sequence;

import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.reader.Bytes;

/**
 *
 * @author matt
 */
 public interface SequenceMatcher {


    /* matches an entire sequence of bytes or not.
     * @returns whether the byte matcher matched a sequence of bytes or not.
    */
    public boolean matchesBytes(final Bytes reader, final long matchFrom);

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
