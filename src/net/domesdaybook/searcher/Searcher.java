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
     * Searches bytes forwards provided by a {@link ByteReader} object,
     * from the position given by fromPosition up to the end of the byte reader.
     *
     * @param reader       The byte reader giving access to the bytes being searched.
     * @param fromPosition The position to search from.
     * @return             The position a match was found at, or NOT_FOUND (-1).
     */
    public long searchForwards(final ByteReader reader, final long fromPosition);
 
    
    /**
     * Searches bytes forwards provided by a {@link ByteReader} object,
     * from the start of the {@link ByteReader} to the end, if a match is not found.
     *
     * @param reader       The byte reader giving access to the bytes being searched.
     * @return             The position a match was found at, or NOT_FOUND (-1).
     */
    public long searchForwards(final ByteReader reader);    
    
    
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
     * Searches bytes forwards provided by a byte array
     * from the position given by fromPosition up to the end of the byte array.
     *
     * @param bytes        The byte array giving access to the bytes being searched.
     * @param fromPosition The position to search from.
     * @return             The position a match was found at, or NOT_FOUND (-1).
     */
    public int searchForwards(byte[] bytes, final int fromPosition);
    
    
    /**
     * Searches bytes forwards provided by a byte array
     *
     * @param bytes        The byte array giving access to the bytes being searched.
\     * @return             The position a match was found at, or NOT_FOUND (-1).
     */
    public int searchForwards(byte[] bytes);
    
    
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
     * Searches bytes backwards provided by a {@link ByteReader} object,
     * from the position given by fromPosition up to the start of the reader.
     *
     * @param reader       The byte reader giving access to the bytes being searched.
     * @param fromPosition The position to search from.
     * @param toPosition   The position to search back to.
     * @return             The position a match was found at, or NOT_FOUND (-1).
     */
    public long searchBackwards(final ByteReader reader, final long fromPosition);
        
    
    /**
     * Searches bytes backwards provided by a {@link ByteReader} object,
     * from the end to the start.
     *
     * @param reader       The byte reader giving access to the bytes being searched.
     * @return             The position a match was found at, or NOT_FOUND (-1).
     */
    public long searchBackwards(final ByteReader reader);    
    
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
     * Searches bytes backwards provided by a byte array,
     * from the position given by fromPosition up to the start of the byte array.
     *
     * @param bytes        The byte array giving access to the bytes being searched.
     * @param fromPosition The position to search from.
     * @return             The position a match was found at, or NOT_FOUND (-1).
     */
    public int searchBackwards(final byte[] bytes, final int fromPosition);
    
   
    /**
     * Searches a byte array backwards, from the end to the start.
     *
     * @param bytes        The byte array giving access to the bytes being searched.
     * @return             The position a match was found at, or NOT_FOUND (-1).
     */
    public int searchBackwards(final byte[] bytes);    
    
    
    /**
     * Ensures that the searcher is fully prepared to search forwards.  
     * Some searchers may defer calculating all the necessary parameters
     * until the first search is made.  Calling this function ensures that
     * all preparation is complete before the first search forwards.
     * <p/>
     * Note: some implemented searchers use single-check lazy initialisation
     * with volatile fields.  This means that if multiple threads attempt to
     * search with such a searcher simultaneously without it being prepared first, 
     * it is possible that the fields may be initialised more than once
     * (maximum as many times as the number of threads using that searcher simultaneously).  
     * This will not produce an error, but would result in unnecessary calculation.
     * <p/>
     * However (assuming multiple threads are involved), it may still be more  
     * efficient to allow the occasional recalculation on first search, when
     * amortised against the cost of fully initialising a large number of search
     * objects before they are known to be needed.  If only a few search objects 
     * are used, then there may be no disadvantage to fully preparing all of them 
     * ahead of time.  
     * <p/>
     * Also note that this function is not itself guaranteed to be thread-safe,  
     * in that calling it from multiple threads may result in multiple 
     * initialisations (but should not produce an error).
     * <p/>
     * Calling this function only changes when (and possibly how many) final 
     * calculations of search parameters are made.
     * If this function is called, it should be made from a single thread before
     * allowing multiple threads to use the searcher. 
     */
    public void prepareForwards();

    
    /**
     * Ensures that the searcher is fully prepared to search backwards.  
     * Some searchers may defer calculating all the necessary parameters
     * until the first search is made.  Calling this function ensures that
     * all preparation is complete before the first search backwards.
     * <p/>
     * Note: some implemented searchers use single-check lazy initialisation
     * with volatile fields.  This means that if multiple threads attempt to
     * search with such a searcher simultaneously without it being prepared first, 
     * it is possible that the fields may be initialised more than once
     * (maximum as many times as the number of threads using that searcher simultaneously).  
     * This will not produce an error, but would result in unnecessary calculation.
     * <p/>
     * However (assuming multiple threads are involved), it may still be more  
     * efficient to allow the occasional recalculation on first search, when
     * amortised against the cost of fully initialising a large number of search
     * objects before they are known to be needed.  If only a few search objects 
     * are used, then there may be no disadvantage to fully preparing all of them 
     * ahead of time.  
     * <p/>
     * Also note that this function is not itself guaranteed to be thread-safe,  
     * in that calling it from multiple threads may result in multiple 
     * initialisations (but should not produce an error).
     * <p/>
     * Calling this function only changes when (and possibly how many) final 
     * calculations of search parameters are made.
     * If this function is called, it should be made from a single thread before
     * allowing multiple threads to use the searcher. 
     */
    public void prepareBackwards();
    

}
