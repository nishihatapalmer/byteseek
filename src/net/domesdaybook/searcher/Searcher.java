/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 
 *  * The names of its contributors may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package net.domesdaybook.searcher;

import java.io.IOException;
import java.util.List;
import net.domesdaybook.reader.Reader;

/**
 * An interface for classes that search bytes provided by a {@link Reader},
 * or on a byte array.  Searching can be forwards or backwards.
 * <p>
 * Searching either returns the position at which a match was found,
 * or a negative number indicates a match was not found.
 *
 * @author Matt Palmer
 */
public interface Searcher<T> {

    
    /**
     * Searches bytes forwards provided by a {@link Reader} object,
     * from the position given by fromPosition up to toPosition.
     *
     * @param reader       The byte reader giving access to the bytes being searched.
     * @param fromPosition The position to search from.
     * @param toPosition   The position to search up to.
     * @return             The position a match was found at, or a negative number if
     *                     no match was found.
     * @throws IOException  
     */
    public List<SearchResult<T>> searchForwards(Reader reader, long fromPosition, long toPosition)
            throws IOException;


    /**
     * Searches bytes forwards provided by a {@link Reader} object,
     * from the position given by fromPosition up to the end of the byte reader.
     *
     * @param reader       The byte reader giving access to the bytes being searched.
     * @param fromPosition The position to search from.
     * @return             The position a match was found at, or a negative number if
     *                     no match was found.
     * @throws IOException  
     */
    public List<SearchResult<T>> searchForwards(Reader reader, long fromPosition)
             throws IOException;
 
    
    /**
     * Searches bytes forwards provided by a {@link Reader} object,
     * from the start of the {@link Reader} to the end, if a match is not found.
     *
     * @param reader       The byte reader giving access to the bytes being searched.
     * @return             The position a match was found at, or a negative number if
     *                     no match was found.
     * @throws IOException  
     */
    public List<SearchResult<T>> searchForwards(Reader reader) 
            throws IOException;    
    
    
    /**
     * Searches bytes forwards provided by a byte array
     * from the position given by fromPosition up to toPosition.
     *
     * @param bytes        The byte array giving access to the bytes being searched.
     * @param fromPosition The position to search from.
     * @param toPosition   The position to search up to.
     * @return             The position a match was found at, or a negative number if
     *                     no match was found.
     */
    public List<SearchResult<T>> searchForwards(byte[] bytes, int fromPosition, int toPosition);

    
    /**
     * Searches bytes forwards provided by a byte array
     * from the position given by fromPosition up to the end of the byte array.
     *
     * @param bytes        The byte array giving access to the bytes being searched.
     * @param fromPosition The position to search from.
     * @return             The position a match was found at, or a negative number if
     *                     no match was found.
     */
    public List<SearchResult<T>> searchForwards(byte[] bytes, int fromPosition);
    
    
    /**
     * Searches bytes forwards provided by a byte array
     *
     * @param bytes        The byte array giving access to the bytes being searched.
     * \     * @return 
     * @return             The position a match was found at, or a negative number if
     *                     no match was found.
     */
    public List<SearchResult<T>> searchForwards(byte[] bytes);
    
    
    /**
     * Searches bytes backwards provided by a {@link Reader} object,
     * from the position given by fromPosition up to toPosition.
     *
     * @param reader       The byte reader giving access to the bytes being searched.
     * @param fromPosition The position to search from.
     * @param toPosition   The position to search back to.
     * @return             The position a match was found at, or a negative number if
     *                     no match was found.
     * @throws IOException  
     */
    public List<SearchResult<T>> searchBackwards(Reader reader, long fromPosition, long toPosition) 
            throws IOException;
    
    
    /**
     * Searches bytes backwards provided by a {@link Reader} object,
     * from the position given by fromPosition up to the start of the reader.
     *
     * @param reader       The byte reader giving access to the bytes being searched.
     * @param fromPosition The position to search from.
     * @return             The position a match was found at, or a negative number if
     *                     no match was found.
     * @throws IOException  
     */
    public List<SearchResult<T>> searchBackwards(Reader reader, long fromPosition) 
            throws IOException;
        
    
    /**
     * Searches bytes backwards provided by a {@link Reader} object,
     * from the end to the start.
     *
     * @param reader       The byte reader giving access to the bytes being searched.
     * @return             The position a match was found at, or a negative number if
     *                     no match was found.
     * @throws IOException  
     */
    public List<SearchResult<T>> searchBackwards(Reader reader)
            throws IOException;    
    
    /**
     * Searches bytes backwards provided by a byte array,
     * from the position given by fromPosition up to toPosition.
     *
     * @param bytes        The byte array giving access to the bytes being searched.
     * @param fromPosition The position to search from.
     * @param toPosition   The position to search back to.
     * @return             The position a match was found at, or a negative number if
     *                     no match was found.
     */
    public List<SearchResult<T>> searchBackwards(byte[] bytes, int fromPosition, int toPosition);
    

    /**
     * Searches bytes backwards provided by a byte array,
     * from the position given by fromPosition up to the start of the byte array.
     *
     * @param bytes        The byte array giving access to the bytes being searched.
     * @param fromPosition The position to search from.
     * @return             The position a match was found at, or a negative number if
     *                     no match was found.
     */
    public List<SearchResult<T>> searchBackwards(byte[] bytes, int fromPosition);
    
   
    /**
     * Searches a byte array backwards, from the end to the start.
     *
     * @param bytes        The byte array giving access to the bytes being searched.
     * @return             The position a match was found at, or a negative number if
     *                     no match was found.
     */
    public List<SearchResult<T>> searchBackwards(byte[] bytes);    
    
    
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
