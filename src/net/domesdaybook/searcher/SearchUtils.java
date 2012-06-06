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
package net.domesdaybook.searcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.Reader;

/**
 * A static utility class holding useful methods in processing searches and
 * search results.
 * 
 * @author Matt Palmer
 */
public final class SearchUtils {


    /**
     * The constructor is private to prevent instantiation of a static utility class.
     */
    private SearchUtils() {
    }
    
    
    /**
     * Searches a byte array forwards for all matches of a {@link Searcher}.
     * 
     * @param <T> The type of object associated with a match in the Searcher.
     * @param searcher The Searcher to search with.
     * @param bytes The byte array to search in.
     * @return A list of SearchResult objects containing all matches found in the byte array.
     */
    public static <T> List<SearchResult<T>> searchAllForwards(final Searcher<T> searcher, 
                                                              final byte[] bytes) {
        final List<SearchResult<T>> results = new ArrayList<SearchResult<T>>();
        final ForwardSearchIterator<T> iterator = new ForwardSearchIterator<T>(searcher, bytes);
        while (iterator.hasNext()) {
            results.addAll(iterator.next());
        }
        return results;
    }


    /**
     * Searches a {@link net.domesdaybook.reader.Reader) forwards for all matches of a {@link Searcher}.
     * 
     * @param <T> The type of object associated with a match in the Searcher.
     * @param searcher The Searcher to search with.
     * @param reader The Reader to search in.
     * @return A list of SearchResult objects containing all matches found in the Reader.
     * @throws IOException if a problem occurred reading in the Reader.
     */
    public static <T> List<SearchResult<T>> searchAllForwards(final Searcher<T> searcher, 
                                                              final Reader reader) 
            throws IOException {
        final List<SearchResult<T>> results = new ArrayList<SearchResult<T>>();
        final ForwardSearchIterator<T> iterator = new ForwardSearchIterator<T>(searcher, reader);
        while (iterator.hasNext()) {
            results.addAll(iterator.next());
        }
        return results;
    }


    /**
     * Searches a byte array backwards for all matches of a {@link Searcher}.
     * 
     * @param <T> The type of object associated with a match in the Searcher.
     * @param searcher The Searcher to search with.
     * @param bytes The byte array to search in.
     * @return A list of SearchResult objects containing all matches found in the byte array.
     */
    public static <T> List<SearchResult<T>> searchAllBackwards(final Searcher<T> searcher,
                                                               final byte[] bytes) {
        final List<SearchResult<T>> results = new ArrayList<SearchResult<T>>();
        final BackwardSearchIterator<T> iterator = new BackwardSearchIterator<T>(searcher, bytes);
        while (iterator.hasNext()) {
            results.addAll(iterator.next());
        }
        return results;
    }


    /**
     * Searches a {@link net.domesdaybook.reader.Reader) forwards for all matches of a {@link Searcher}.
     * 
     * @param <T> The type of object associated with a match in the Searcher.
     * @param searcher The Searcher to search with.
     * @param reader The Reader to search in.
     * @return A list of SearchResult objects containing all matches found in the Reader.
     * @throws IOException if a problem occurred reading in the Reader.
     */
    public static <T> List<SearchResult<T>> searchAllBackwards(final Searcher<T> searcher, 
                                                               final Reader reader)
            throws IOException {
        final List<SearchResult<T>> results = new ArrayList<SearchResult<T>>();
        final BackwardSearchIterator<T> iterator = new BackwardSearchIterator<T>(searcher, reader);
        while (iterator.hasNext()) {
            results.addAll(iterator.next());
        }          
        return results;
    }
    
    
    /**
     * Returns a single SearchResult object from a match position and a matching object.
     * 
     * @param <T> The type of object associated with a Searcher match.
     * @param matchPosition The position the object matched at.
     * @param matchingObject The object which matched at the position.
     * @return A list containing a single SearchResult.
     */
    public static <T> List<SearchResult<T>> singleResult(final long matchPosition,
                                                         final T matchingObject) {
        final List<SearchResult<T>> result = new ArrayList<SearchResult<T>>(1);
        result.add(new SearchResult<T>(matchPosition, matchingObject));
        return result;
    }
    
    
    /**
     * Returns a list of SearchResults for multiple objects all matching at the
     * same position.
     * 
     * @param <T> The type of object associated with a Searcher match.
     * @param matchPosition The position the objects matched at.
     * @param matchingObjects The objects which matched at the position.
     * @return A list containing SearchResults for all objects at the same position.
     */
    public static <T> List<SearchResult<T>> resultsAtPosition(final long matchPosition,
            final Collection<T> matchingObjects) {
        final List<SearchResult<T>> results = new ArrayList<SearchResult<T>>(matchingObjects.size());
        for (final T matchingObject : matchingObjects) {
            results.add(new SearchResult<T>(matchPosition, matchingObject));
        }
        return results;
    }
    
    
    /**
     * Returns a list of SearchResults for multiple sequences all matching at a
     * right-aligned position.  The start of each sequence (the actual match position
     * we will report back) could in theory fall before the start of the search, or
     * even after the end of the search position.  Any sequences not falling within
     * the bounds of the search are filered out, and the others returned as matches.
     * 
     * @param backFromPosition The right-aligned position at which the sequences match.
     * @param matchingSequences The sequences which matched.
     * @param searchStart The start position of the search.
     * @param searchEnd The end position of the search.
     * @return A list of search results for all sequences which fit inside the search.
     */
    public static List<SearchResult<SequenceMatcher>> resultsBackFromPosition(
            final long backFromPosition,
            final Collection<? extends SequenceMatcher> matchingSequences,
            final long searchStart, final long searchEnd) {
        final List<SearchResult<SequenceMatcher>> results = new ArrayList<SearchResult<SequenceMatcher>>(matchingSequences.size());
        final long onePastBackFrom = backFromPosition + 1;
        for (final SequenceMatcher sequence : matchingSequences) {
            final long sequenceStartPosition = onePastBackFrom - sequence.length();
            if (sequenceStartPosition >= searchStart && sequenceStartPosition <= searchEnd) {
                results.add(new SearchResult<SequenceMatcher>(sequenceStartPosition, sequence));
            }
        }
        return results;
    }
    
    
    /**
     * Returns a type-safe empty list of SearchResults.
     * 
     * @param <T> The type of object associated with a match in the Searcher.
     * @return An empty list of SearchResult<T>.
     */
    public static <T> List<SearchResult<T>> noResults() {
        return Collections.emptyList();
    }
    

    /**
     * Returns a new list of SearchResults created from another list of SearchResults,
     * by adding a number to the match position of each SearchResult.
     * <p>
     * This is useful to translate a match relative to a Window into a match relative
     * to the entire Reader.
     * 
     * @param <T> The type of object associated with a match in the Searcher.
     * @param originalResults The original search results to add a number to.
     * @param amountToAdd The amount to add to the match position of each SearchResult.
     * @return A list of SearchResults with the match position adjusted by the amountToAdd.
     */
    public static <T> List<SearchResult<T>> addPositionToResults (
            final List<SearchResult<T>> originalResults,
            final long amountToAdd) {
        final List<SearchResult<T>> newResults = new ArrayList<SearchResult<T>>(originalResults.size());
        for (final SearchResult<T> result : originalResults) {
            newResults.add(new SearchResult<T>(result.getMatchPosition() + amountToAdd,
                                               result.getMatchingObject()));
        }
        return newResults;
    }    
    
}
