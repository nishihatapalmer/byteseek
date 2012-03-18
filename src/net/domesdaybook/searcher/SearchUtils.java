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
 *
 * @author Matt Palmer
 */
public final class SearchUtils {


    
    private SearchUtils() {
    }
    
    
    public static List<SearchResult> searchAllForwards(final Searcher searcher, final byte[] bytes) {
        final List<SearchResult> results = new ArrayList<SearchResult>();
        final ForwardSearchIterator iterator = new ForwardSearchIterator(searcher, bytes);
        while (iterator.hasNext()) {
            results.addAll(iterator.next());
        }
        return results;
    }


    public static List<SearchResult> searchAllForwards(final Searcher searcher, final Reader reader) throws IOException {
        final List<SearchResult> results = new ArrayList<SearchResult>();
        final ForwardSearchIterator iterator = new ForwardSearchIterator(searcher, reader);
        while (iterator.hasNext()) {
            results.addAll(iterator.next());
        }
        return results;
    }


    public static List<SearchResult> searchAllBackwards(final Searcher searcher, final byte[] bytes) {
        final List<SearchResult> results = new ArrayList<SearchResult>();
        final BackwardSearchIterator iterator = new BackwardSearchIterator(searcher, bytes);
        while (iterator.hasNext()) {
            results.addAll(iterator.next());
        }
        return results;
    }


    public static List<SearchResult> searchAllBackwards(final Searcher searcher, final Reader reader) throws IOException {
        final List<SearchResult> results = new ArrayList<SearchResult>();
        final BackwardSearchIterator iterator = new BackwardSearchIterator(searcher, reader);
        while (iterator.hasNext()) {
            results.addAll(iterator.next());
        }          
        return results;
    }
    
    
    public static <T> List<SearchResult<T>> singleResult(final long matchPosition,
        final T matchingObject) {
        final List<SearchResult<T>> result = new ArrayList<SearchResult<T>>(1);
        result.add(new SearchResult(matchPosition, matchingObject));
        return result;
    }
    
    
    public static <T> List<SearchResult<T>> resultsAtPosition(final long matchPosition,
            final Collection<T> matchingObjects) {
        final List<SearchResult<T>> results = new ArrayList<SearchResult<T>>(matchingObjects.size());
        for (final T matchingObject : matchingObjects) {
            results.add(new SearchResult<T>(matchPosition, matchingObject));
        }
        return results;
    }
    
    
    public static List<SearchResult<SequenceMatcher>> resultsFromPosition(
            final long searchPosition, 
            final Collection<SequenceMatcher> matchingSequences, 
            final long furthestLimit) {
        final List<SearchResult<SequenceMatcher>> results = new ArrayList<SearchResult<SequenceMatcher>>(matchingSequences.size());
        for (final SequenceMatcher sequence : matchingSequences) {
            final long finalPosition = searchPosition + sequence.length() - 1;
            if (finalPosition <= furthestLimit) {
                results.add(new SearchResult<SequenceMatcher>(searchPosition, sequence));
            }
        }
        return results;
    }
    
    
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
    
    
    public static <T> List<SearchResult<T>> noResults() {
        return Collections.emptyList();
    }
    
    
    public static <T> List<SearchResult<T>> subtractPositionFromResults (
            final List<SearchResult<T>> originalResults,
            final long amountToSubtract) {
        final List<SearchResult<T>> newResults = new ArrayList<SearchResult<T>>(originalResults.size());
        for (final SearchResult<T> result : originalResults) {
            newResults.add(new SearchResult<T>(result.getMatchPosition() - amountToSubtract,
                                               result.getMatchingObject()));
        }
        return newResults;
    }
    
    
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
