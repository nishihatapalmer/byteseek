/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.sequence;

import net.domesdaybook.matcher.singlebyte.AnyByteMatcher;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.reader.ByteReader;

/**
 * Matches a gap of unknown bytes - always matches.
 *
 * @author matt
 */
public final class FixedGapMatcher implements SequenceMatcher {

    private final int gapLength;

    public FixedGapMatcher(final int gapLength) {
        if (gapLength < 1) {
            throw new IllegalArgumentException("FixedGapMatcher requires a gap greater than zero.");
        }
        this.gapLength = gapLength;
    }

    @Override
    public SingleByteMatcher getByteMatcherForPosition(int position) {
        return new AnyByteMatcher();
    }

    @Override
    public int length() {
        return gapLength;
    }

    @Override
    public String toRegularExpression(boolean prettyPrint) {
        return prettyPrint ? String.format(" .{%d} ", gapLength) : String.format(".{%d}", gapLength);
    }

    @Override
    public boolean matches(ByteReader reader, long matchPosition) {
        return true;
    }

}
