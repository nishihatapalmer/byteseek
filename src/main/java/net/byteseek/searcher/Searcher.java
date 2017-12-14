/*
 * Copyright Matt Palmer 2009-2017, All rights reserved.
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

package net.byteseek.searcher;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.MatchResult;

/**
 * An interface for classes that search bytes provided by a {@link WindowReader}, or on a byte array.
 * Searching can be forwards or backwards.
 * <p>
 * Two variants of the methods allow you to get a new list of search results on each search,
 * or to pass in an existing collection to which any new results are added.
 * In the latter case, the method returns the number of results added, if any.  This may
 * be more efficient if multiple searches are being performed, as it avoids the overhead of creating a
g * new list on each search.  In the former case, it's convenient to obtain a list of results immediately,
 * although it will create a new list even if there are no search results to add to it.
 *
 * @author Matt Palmer
 */
public interface Searcher {

	/**
	 * Searches bytes forwards provided by a {@link WindowReader} object, from the fromPosition to the toPosition.
	 * No error will result if either of these positions lie before or after the available data,
	 * but all valid data within those positions will be searched.
	 * <p>
	 * The number of results found is returned.
	 * The actual search results are added to the list of match results passed in.
	 *
	 * @param reader       The byte reader giving access to the bytes being searched.
	 * @param fromPosition The position to search from.
	 * @param toPosition   The position to search up to.
	 * @param results      A collection of match results which any new results will be added to.
	 * @return             The number of results found.
	 * @throws IOException if a problem occurs reading data.
	 */
	int searchForwards(WindowReader reader, long fromPosition, long toPosition, Collection<MatchResult> results) throws IOException;

	/**
	 * Searches bytes forwards provided by a {@link WindowReader} object, from the fromPosition to the toPosition,
	 * and returns a new list of match results.
	 * No error will result if either of these positions lie before or after the available data,
	 * but all valid data within those positions will be searched.
	 *
	 * @param reader       The byte reader giving access to the bytes being searched.
	 * @param fromPosition The position to search from.
	 * @param toPosition   The position to search up to.
	 * @return             A list of search results, containing the position of a match and the matching object.
	 *                     If no results are found, the list will be empty, but a new list will have been created.
	 * @throws IOException if a problem occurs reading data.
	 */
	List<MatchResult> searchForwards(WindowReader reader, long fromPosition, long toPosition) throws IOException;

	/**
	 * Searches bytes forwards provided by a {@link WindowReader} object, from the
	 * position given by fromPosition up to the end of the window reader.
	 * No error will result if either of these positions lie before or after the available data,
	 * but all valid data within those positions will be searched.
	 *
	 * @param reader       The window reader giving access to the bytes being searched.
	 * @param fromPosition The position to search from.
	 * @param results      A collection of match results which any new results will be added to.
	 * @return             The number of results found.
	 * @throws IOException if a problem occurs reading data.
	 */
	int searchForwards(WindowReader reader, long fromPosition, Collection<MatchResult> results) throws IOException;

	/**
	 * Searches bytes forwards provided by a {@link WindowReader} object, from the
	 * position given by fromPosition up to the end of the window reader.
	 *
	 * @param reader       The window reader giving access to the bytes being searched.
	 * @param fromPosition The position to search from.
	 * @return             A list of search results, containing the position of a match and the matching object.
	 *                     If no results are found, the list will be empty, but a new list will have been created.
	 * @throws IOException if a problem occurs reading data.
	 */
	List<MatchResult> searchForwards(WindowReader reader, long fromPosition) throws IOException;

	/**
	 * Searches bytes forwards provided by a {@link WindowReader} object, from the
	 * start of the {@link WindowReader} to the end, if a match is not found.
	 *
	 * @param reader  The byte reader giving access to the bytes being searched.
	 * @param results      A collection of match results which any new results will be added to.
	 * @return             The number of results found.
	 * @throws IOException if a problem occurs reading data.
	 */
	int searchForwards(WindowReader reader, Collection<MatchResult> results) throws IOException;

		/**
	 * Searches bytes forwards provided by a {@link WindowReader} object, from the
	 * start of the {@link WindowReader} to the end, if a match is not found.
	 *
	 * @param reader  The byte reader giving access to the bytes being searched.
	 * @return             A list of search results, containing the position of a match and the matching object.
	 *                     If no results are found, the list will be empty, but a new list will have been created.
	 * @throws IOException if a problem occurs reading data.
	 */
	List<MatchResult> searchForwards(WindowReader reader) throws IOException;

	/**
	 * Searches bytes forwards provided by a byte array from the position given
	 * by fromPosition up to toPosition.
	 *
	 * @param bytes        The byte array giving access to the bytes being searched.
	 * @param fromPosition The position to search from.
	 * @param toPosition   The position to search up to.
	 * @param results      A collection of match results which any new results will be added to.
	 * @return             The number of results found.
	 */
	int searchForwards(byte[] bytes, int fromPosition, int toPosition, Collection<MatchResult> results);

	/**
	 * Searches bytes forwards provided by a byte array from the position given
	 * by fromPosition up to toPosition.
	 *
	 * @param bytes        The byte array giving access to the bytes being searched.
	 * @param fromPosition The position to search from.
	 * @param toPosition   The position to search up to.
	 * @return             A list of search results, containing the position of a match and the matching object.
	 *                     If no results are found, the list will be empty, but a new list will have been created.
	 */
	List<MatchResult> searchForwards(byte[] bytes, int fromPosition, int toPosition);

	/**
	 * Searches bytes forwards provided by a byte array from the position given
	 * by fromPosition up to the end of the byte array.
	 *
	 * @param bytes        The byte array giving access to the bytes being searched.
	 * @param fromPosition The position to search from.
	 * @param results      A collection of match results which any new results will be added to.
	 * @return             The number of results found.
	 */
	int searchForwards(byte[] bytes, int fromPosition, Collection<MatchResult> results);

	/**
	 * Searches bytes forwards provided by a byte array from the position given
	 * by fromPosition up to the end of the byte array.
	 *
	 * @param bytes        The byte array giving access to the bytes being searched.
	 * @param fromPosition The position to search from.
	 * @return             A list of search results, containing the position of a match and the matching object.
	 *                     If no results are found, the list will be empty, but a new list will have been created.
	 */
	List<MatchResult> searchForwards(byte[] bytes, int fromPosition);

	/**
	 * Searches bytes forwards provided by a byte array
	 *
	 * @param bytes        The byte array giving access to the bytes being searched.
	 * @param results      A collection of match results which any new results will be added to.
	 * @return             The number of results found.
	 */
	int searchForwards(byte[] bytes, Collection<MatchResult> results);

	/**
	 * Searches bytes forwards provided by a byte array
	 *
	 * @param bytes The byte array giving access to the bytes being searched.
	 * @return             A list of search results, containing the position of a match and the matching object.
	 *                     If no results are found, the list will be empty, but a new list will have been created.
	 */
	List<MatchResult> searchForwards(byte[] bytes);

	/**
	 * Searches bytes backwards provided by a {@link WindowReader} object, from the
	 * position given by fromPosition up to toPosition.
	 *
	 * @param reader       The byte reader giving access to the bytes being searched.
	 * @param fromPosition The position to search from.
	 * @param toPosition   The position to search back to.
	 * @param results      A collection of match results which any new results will be added to.
	 * @return             The number of results found.
	 * @throws IOException if a problem occurs reading the data.
	 */
	int searchBackwards(WindowReader reader, long fromPosition, long toPosition, Collection<MatchResult> results) throws IOException;

	/**
	 * Searches bytes backwards provided by a {@link WindowReader} object, from the
	 * position given by fromPosition up to toPosition.
	 *
	 * @param reader       The byte reader giving access to the bytes being searched.
	 * @param fromPosition The position to search from.
	 * @param toPosition   The position to search back to.
	 * @return             A list of search results, containing the position of a match and the matching object.
	 *                     If no results are found, the list will be empty, but a new list will have been created.
	 * @throws IOException if a problem occurs reading the data.
	 */
	List<MatchResult> searchBackwards(WindowReader reader, long fromPosition, long toPosition) throws IOException;

	/**
	 * Searches bytes backwards provided by a {@link WindowReader} object, from the
	 * position given by fromPosition up to the start of the reader.
	 *
	 * @param reader       The byte reader giving access to the bytes being searched.
	 * @param fromPosition The position to search from.
	 * @param results      A collection of match results which any new results will be added to.
	 * @return             The number of results found.
	 * @throws IOException if a problem occurs reading the data.
	 */
	int searchBackwards(WindowReader reader, long fromPosition, Collection<MatchResult> results) throws IOException;

	/**
	 * Searches bytes backwards provided by a {@link WindowReader} object, from the
	 * position given by fromPosition up to the start of the reader.
	 *
	 * @param reader       The byte reader giving access to the bytes being searched.
	 * @param fromPosition The position to search from.
	 * @return             A list of search results, containing the position of a match and the matching object.
	 *                     If no results are found, the list will be empty, but a new list will have been created.
	 * @throws IOException if a problem occurs reading the data.
	 */
	List<MatchResult> searchBackwards(WindowReader reader, long fromPosition) throws IOException;

	/**
	 * Searches bytes backwards provided by a {@link WindowReader} object, from the
	 * end to the start.
	 *
	 * @param reader The byte reader giving access to the bytes being searched.
	 * @param results      A collection of match results which any new results will be added to.
	 * @return             The number of results found.
	 * @throws IOException if a problem occurs reading the data.
	 */
	int searchBackwards(WindowReader reader, Collection<MatchResult> results) throws IOException;

	/**
	 * Searches bytes backwards provided by a {@link WindowReader} object, from the
	 * end to the start.
	 *
	 * @param reader The byte reader giving access to the bytes being searched.
	 * @return             A list of search results, containing the position of a match and the matching object.
	 *                     If no results are found, the list will be empty, but a new list will have been created.
	 * @throws IOException if a problem occurs reading the data.
	 */
	List<MatchResult> searchBackwards(WindowReader reader) throws IOException;

	/**
	 * Searches bytes backwards provided by a byte array, from the position
	 * given by fromPosition up to toPosition.
	 *
	 * @param bytes        The byte array giving access to the bytes being searched.
	 * @param fromPosition The position to search from.
	 * @param toPosition   The position to search back to.
	 * @param results      A collection of match results which any new results will be added to.
	 * @return             The number of results found.
	 */
	int searchBackwards(byte[] bytes, int fromPosition, int toPosition, Collection<MatchResult> results);

	/**
	 * Searches bytes backwards provided by a byte array, from the position
	 * given by fromPosition up to toPosition.
	 *
	 * @param bytes        The byte array giving access to the bytes being searched.
	 * @param fromPosition The position to search from.
	 * @param toPosition   The position to search back to.
	 * @return             A list of search results, containing the position of a match and the matching object.
	 *                     If no results are found, the list will be empty, but a new list will have been created.
	 */
	List<MatchResult> searchBackwards(byte[] bytes, int fromPosition, int toPosition);

	/**
	 * Searches bytes backwards provided by a byte array, from the position
	 * given by fromPosition up to the start of the byte array.
	 *
	 * @param bytes        The byte array giving access to the bytes being searched.
	 * @param fromPosition The position to search from.
	 * @param results      A collection of match results which any new results will be added to.
	 * @return             The number of results found.
	 */
	int searchBackwards(byte[] bytes, int fromPosition, Collection<MatchResult> results);

	/**
	 * Searches bytes backwards provided by a byte array, from the position
	 * given by fromPosition up to the start of the byte array.
	 *
	 * @param bytes        The byte array giving access to the bytes being searched.
	 * @param fromPosition The position to search from.
	 * @return             A list of search results, containing the position of a match and the matching object.
	 *                     If no results are found, the list will be empty, but a new list will have been created.
	 */
	List<MatchResult> searchBackwards(byte[] bytes, int fromPosition);

	/**
	 * Searches a byte array backwards, from the end to the start.
	 *
	 * @param bytes The byte array giving access to the bytes being searched.
	 * @param results      A collection of match results which any new results will be added to.
	 * @return             The number of results found.
	 */
	int searchBackwards(byte[] bytes, Collection<MatchResult> results);

	/**
	 * Searches a byte array backwards, from the end to the start.
	 *
	 * @param bytes The byte array giving access to the bytes being searched.
	 * @return             A list of search results, containing the position of a match and the matching object.
	 *                     If no results are found, the list will be empty, but a new list will have been created.
	 */
	List<MatchResult> searchBackwards(byte[] bytes);

	/**
	 * Ensures that the searcher is fully prepared to search forwards by examining the pattern to search with.
     * Many searchers defer calculating the various data structures they need to search with until a search is
     * actually performed.  Calling this method performs those calculations, if they are not already calculated.
     * <p>
     * It is not necessary to call this before searching, a searcher will always ensure it is prepared. 
     * This method is mostly useful to benchmark how long preparation takes versus searching. 
	 */
	void prepareForwards();

	/**
	 * Ensures that the searcher is fully prepared to search backwards by examining the pattern to search with.
     * Many searchers defer calculating the various data structures they need to search with until a search is
     * actually performed.  Calling this method performs those calculations, if they are not already calculated.
     * <p>
     * It is not necessary to call this before searching, a searcher will always ensure it is prepared. 
     * This method is mostly useful to benchmark how long preparation takes versus searching. 
	 */
	void prepareBackwards();

}
