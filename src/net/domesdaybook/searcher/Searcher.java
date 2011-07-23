/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.searcher;

import net.domesdaybook.reader.ByteReader;

/**
 * An interface for classes that search bytes provided by a {@link ByteReader}.
 * Searching can be forwards or backwards.
 * <p>
 * Searching either returns the position at which a match was found,
 * or {@code NOT_FOUND (-1L)}.
 *
 * @author Matt Palmer
 */
public interface Searcher {

    /**
     * A constant indicating that a search was not successful.
     */
    public static final int NOT_FOUND = -1;
    
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
     * Searches bytes forwards provided by a byte array
     * from the position given by fromPosition up to toPosition.
     *
     * @param bytes        The byte array giving access to the bytes being searched.
     * @param fromPosition The position to search from.
     * @param toPosition   The position to search up to.
     * @return             The position a match was found at, or NOT_FOUND (-1).
     */
    public int searchForwards(byte[] bytes, final int fromPosition, final int toPosition);

    
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
    
    
    /**
     * Searches bytes backwards provided by a byte array,
     * from the position given by fromPosition up to toPosition.
     *
     * @param bytes        The byte array giving access to the bytes being searched.
     * @param fromPosition The position to search from.
     * @param toPosition   The position to search back to.
     * @return             The position a match was found at, or NOT_FOUND (-1).
     */
    public int searchBackwards(final byte[] bytes, final int fromPosition, final int toPosition);
    

}
