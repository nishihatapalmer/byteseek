/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.searcher;

import net.domesdaybook.reader.ByteReader;

/**
 * An interface for classes that search bytes provided by a {@link ByteReader}.
 * Searching can be forwards or backwards.
 *
 * @author matt
 */
public interface Searcher {

    /**
     * A constant indicating whether a search was not successful.
     */
    public static final long NOT_FOUND = -1L;


    /**
     * Searches bytes forwards provided by a {@link ByteReader} object,
     * from the position given by fromPosition up to toPosition.
     *
     * @param reader       The byte reader giving access to the bytes being searched.
     * @param fromPosition The position to search from.
     * @param toPosition   The position to search up to.
     * @return             The position a match was found at, or NOT_FOUND (-1).
     */
    public long searchForwards(final ByteReader reader, final long fromPosition, final long toPosition);


    /**
     * Searches bytes backwards provided by a {@link ByteReader} object,
     * from the position given by fromPosition up to toPosition.
     *
     * @param reader       The byte reader giving access to the bytes being searched.
     * @param fromPosition The position to search from.
     * @param toPosition   The position to search back to.
     * @return             The position a match was found at, or NOT_FOUND (-1).
     */
    public long searchBackwards(final ByteReader reader, final long fromPosition, final long toPosition);

}
