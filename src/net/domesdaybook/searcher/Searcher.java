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
    

    /**
     * Ensures that the searcher is fully prepared to search forwards.  
     * Some searchers may defer calculating all the necessary parameters
     * until the first search is made.  Calling this function ensures that
     * all preparation is complete before the first search forwards.
     * 
     * Note: some implemented searchers use single-check lazy initialisation
     * with volatile fields.  This means that if multiple threads attempt to
     * search with such a searcher simultaneously without being prepared first, 
     * it is possible that the fields may be initialised more than once
     * (maximum as many times as the number of threads using that searcher simultaneously).  
     * This will not produce an error, but would result in unnecessary calculation;
     */
    public void prepareForwards();

    
    /**
     * Ensures that the searcher is fully prepared to search backwards.  
     * Some searchers may defer calculating all the necessary parameters
     * until the first search is made.  Calling this function ensures that
     * all preparation is complete before the first search backwards.
     * 
     * Note: some implemented searchers use single-check lazy initialisation
     * with volatile fields.  This means that if multiple threads attempt to
     * search with such a searcher simultaneously without being prepared first, 
     * it is possible that the fields may be initialised more than once
     * (maximum as many times as the number of threads using that searcher simultaneously).  
     * This will not produce an error, but would result in unnecessary calculation;
     */
    public void prepareBackwards();
    

}
