/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.multisequence;

import java.util.Collection;
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
     * Returns all the SequenceMatcher objects which matched.
     * Should never return null - always returns a collection, even if empty.
     *
     * @param reader The {@link ByteReader} to read from.
     * @param matchPosition The position to test for a match.
     * @return A collection of matching SequenceMatchers or an empty collection if none matched.
     */
    Collection<SequenceMatcher> allMatches(final ByteReader reader, final long matchPosition);

    
    /**
     * Returns the first matching sequence, or null if no sequence matched.
     * 
     * @param reader The {@link ByteReader} to read from.
     * @param matchPosition matchPosition The position to test for a match.
     * @return The SequenceMatcher which matched at that position, or null if none matched.
     */
    SequenceMatcher firstMatch(final ByteReader reader, final long matchPosition);
  
    
    /**
     * Returns all the SequenceMatcher objects which matched.
     * Should never return null - always returns a collection, even if empty.
     *
     * @param bytes The byte array to read from.
     * @param matchPosition The position to test for a match.
     * @return A collection of matching SequenceMatchers or an empty collection if none matched.
     */
    Collection<SequenceMatcher> allMatches(final byte[] bytes, final int matchPosition);

    
    /**
     * Returns the first matching sequence, or null if no sequence matched.
     * 
     * @param bytes The byte array to read from.
     * @param matchPosition matchPosition The position to test for a match.
     * @return The SequenceMatcher which matched at that position, or null if none matched.
     */
    SequenceMatcher firstMatch(final byte[] bytes, final int matchPosition);    
}
