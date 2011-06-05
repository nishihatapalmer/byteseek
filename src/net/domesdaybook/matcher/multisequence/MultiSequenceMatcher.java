/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.multisequence;

import net.domesdaybook.matcher.Matcher;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.ByteReader;

/**
 * An interface for matchers which can match more than one
 * {@link SequenceMatcher} at the same time.
 *
 * @author Matt Palmer
 */
public interface MultiSequenceMatcher extends Matcher {

    /**
     * Returns the SequenceMatcher object which matched, or null if none matched.
     *
     * @param reader The {@link ByteReader} to read from.
     * @param matchPosition The position to test for a match.
     * @return The SequenceMatcher which matched at that position, or null if none matched.
     */
    SequenceMatcher matchingSequenceAt(final ByteReader reader, final long matchPosition);

}
