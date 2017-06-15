/*
 * Copyright Matt Palmer 2012, All rights reserved.
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.MatchResult;
import net.byteseek.matcher.sequence.SequenceMatcher;

/**
 * A static utility class holding useful methods in processing searches and
 * search results.
 * 
 * @author Matt Palmer
 */
public final class SearchUtils {

	/**
	 * The constructor is private to prevent instantiation of a static utility
	 * class.
	 */
	private SearchUtils() {
	}

	/**
	 * Searches a byte array forwards for all matches of a {@link Searcher}.
	 *
	 * @param searcher
	 *            The Searcher to search with.
	 * @param bytes
	 *            The byte array to search in.
	 * @return A list of SearchResult objects containing all matches found in
	 *         the byte array.
	 */
	public static  List<MatchResult> searchAllForwards(
			final Searcher searcher, final byte[] bytes) {
		final List<MatchResult> results = new ArrayList<MatchResult>();
		final ForwardSearchIterator iterator = new ForwardSearchIterator(searcher, bytes);
		while (iterator.hasNext()) {
			results.addAll(iterator.next());
		}
		return results;
	}

	/**
	 * Searches a {@link net.byteseek.io.reader.WindowReader} forwards for all
	 * matches of a {@link Searcher}.
	 *
	 * @param searcher
	 *            The Searcher to search with.
	 * @param reader
	 *            The WindowReader to search in.
	 * @return A list of SearchResult objects containing all matches found in
	 *         the WindowReader.
	 */
	public static  List<MatchResult> searchAllForwards(
			final Searcher searcher, final WindowReader reader) {
		final List<MatchResult> results = new ArrayList<MatchResult>();
		final ForwardSearchIterator iterator = new ForwardSearchIterator(searcher, reader);
		while (iterator.hasNext()) {
			results.addAll(iterator.next());
		}
		return results;
	}

	/**
	 * Searches a byte array backwards for all matches of a {@link Searcher}.
	 *
	 * @param searcher
	 *            The Searcher to search with.
	 * @param bytes
	 *            The byte array to search in.
	 * @return A list of SearchResult objects containing all matches found in
	 *         the byte array.
	 */
	public static  List<MatchResult> searchAllBackwards(
			final Searcher searcher, final byte[] bytes) {
		final List<MatchResult> results = new ArrayList<MatchResult>();
		final BackwardSearchIterator iterator = new BackwardSearchIterator(searcher, bytes);
		while (iterator.hasNext()) {
			results.addAll(iterator.next());
		}
		return results;
	}

	/**
	 * Searches a {@link net.byteseek.io.reader.WindowReader} forwards for all
	 * matches of a {@link Searcher}.
	 *
	 * @param searcher
	 *            The Searcher to search with.
	 * @param reader
	 *            The WindowReader to search in.
	 * @return A list of SearchResult objects containing all matches found in
	 *         the WindowReader.
	 * @throws IOException
	 *             if a problem occurred reading in the WindowReader.
	 */
	public static  List<MatchResult> searchAllBackwards(
			final Searcher searcher, final WindowReader reader) throws IOException {
		final List<MatchResult> results = new ArrayList<MatchResult>();
		final BackwardSearchIterator iterator = new BackwardSearchIterator(searcher, reader);
		while (iterator.hasNext()) {
			results.addAll(iterator.next());
		}
		return results;
	}

	/**
	 * Returns a single SearchResult object from a match position and a matching
	 * object.
	 *
	 * @param matchPosition
	 *            The position the object matched at.
	 * @param length THe length of the match.
	 *            The object which matched at the position.
	 * @return A list containing a single SearchResult.
	 */
	public static  List<MatchResult> singleResult(
			final long matchPosition, final int length) {
		final List<MatchResult> result = new ArrayList<MatchResult>(1);
		result.add(new MatchResult(matchPosition, length));
		return result;
	}

	/**
	 * Returns a list of SearchResults for multiple objects all matching at the
	 * same position.
	 *
	 * @param matchPosition
	 *            The position the objects matched at.
	 * @param matchingObjects
	 *            The objects which matched at the position.
	 * @return A list containing SearchResults for all objects at the same
	 *         position.
	 */
	public static  List<MatchResult> resultsAtPosition(
			final long matchPosition, final Collection matchingObjects) {
		final List<MatchResult> results = new ArrayList<MatchResult>(
				matchingObjects.size());
		//for (final T matchingObject : matchingObjects) {
		//	results.add(new MatchResult(matchPosition, 1)); //TODO: FIX THIS - WRONG!!!
		//}
		return results;
	}

	/**
	 * Returns a list of SearchResults for multiple sequences all matching at a
	 * right-aligned position. The start of each sequence (the actual match
	 * position we will report back) could in theory fall before the start of
	 * the search, or even after the end of the search position. Any sequences
	 * not falling within the bounds of the search are filered out, and the
	 * others returned as matches.
	 *
	 * @param backFromPosition
	 *            The right-aligned position at which the sequences match.
	 * @param matchingSequences
	 *            The sequences which matched.
	 * @param searchStart
	 *            The start position of the search.
	 * @param searchEnd
	 *            The end position of the search.
	 * @return A list of search results for all sequences which fit inside the
	 *         search.
	 */
	public static List<MatchResult> resultsBackFromPosition(
			final long backFromPosition,
			final Collection<? extends SequenceMatcher> matchingSequences,
			final long searchStart, final long searchEnd) {
		final List<MatchResult> results = new ArrayList<MatchResult>(
				matchingSequences.size());
		final long onePastBackFrom = backFromPosition + 1;
		for (final SequenceMatcher sequence : matchingSequences) {
			final long sequenceStartPosition = onePastBackFrom
					- sequence.length();
			if (sequenceStartPosition >= searchStart
					&& sequenceStartPosition <= searchEnd) {
				results.add(new MatchResult(
						sequenceStartPosition, sequence.length()));
			}
		}
		return results;
	}

	/**
	 * Returns a type-safe empty list of SearchResults.
	 *
	 * @return An empty list of SearchResult&lt;T&gt;.
	 */
	public static  List<MatchResult> noResults() {
		return Collections.emptyList();
	}

	/**
	 * Returns a new list of SearchResults created from another list of
	 * SearchResults, by adding a number to the match position of each
	 * SearchResult.
	 * <p>
	 * This is useful to translate a match relative to a Window into a match
	 * relative to the entire WindowReader.
	 *
	 * @param originalResults
	 *            The original search results to add a number to.
	 * @param amountToAdd
	 *            The amount to add to the match position of each SearchResult.
	 * @return A list of SearchResults with the match position adjusted by the
	 *         amountToAdd.
	 */
	public static  List<MatchResult> addPositionToResults(
			final List<MatchResult> originalResults, final long amountToAdd) {
		final int numResults = originalResults.size();
		final List<MatchResult> newResults = new ArrayList<MatchResult>(numResults);
		for (int i = 0; i < numResults; i++) {
			final MatchResult result = originalResults.get(i);
			newResults.add(new MatchResult(result.getMatchPosition() + amountToAdd, 1 )); //TODO: FIX THIS - WRONG!!!
		}
		return newResults;
	}

}
