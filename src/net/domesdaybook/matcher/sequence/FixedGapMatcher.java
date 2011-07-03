/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.sequence;

import net.domesdaybook.matcher.singlebyte.AnyMatcher;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.reader.ByteReader;

/**
 * An immutable object which matches a gap of unknown bytes.
 *
 * It always matches, even if the sequence being matched against is shorter
 * than the gap. This is true in general of all the sequence matchers, in that
 * they do not test to see if they overrun the ByteReader, or guarantee that an
 * IndexOutOfBounds exception will be thrown. In the case of the fixed gap matcher,
 * no access is made to the ByteReader at all, so no exception can ever be thrown.
 *
 * @author matt
 */
public final class FixedGapMatcher implements SequenceMatcher {

    private static final SingleByteMatcher ANY_MATCHER = new AnyMatcher();

    private final int gapLength;

   
    /**
     * Constructs a FixedGapMatcher of a given length.
     *
     * @param gapLength The length of the gap to match.
     * @throws IllegalArgumentException if the gap is less than one.
     */
    public FixedGapMatcher(final int gapLength) {
        if (gapLength < 1) {
            throw new IllegalArgumentException("FixedGapMatcher requires a gap greater than zero.");
        }
        this.gapLength = gapLength;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public SingleByteMatcher getByteMatcherForPosition(int position) {
        return ANY_MATCHER;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int length() {
        return gapLength;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toRegularExpression(boolean prettyPrint) {
        return prettyPrint ? String.format(" .{%d} ", gapLength) : String.format(".{%d}", gapLength);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final ByteReader reader, final long matchPosition) {
        return true;
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        return true;
    }    
    
    
    /**
     * {@inheritDoc}
     */
    @Override    
    public FixedGapMatcher reverse() {
        return this;
    }

}
