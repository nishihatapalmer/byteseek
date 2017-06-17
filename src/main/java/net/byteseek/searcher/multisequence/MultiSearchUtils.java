package net.byteseek.searcher.multisequence;

import net.byteseek.matcher.MatchResult;
import net.byteseek.matcher.sequence.SequenceMatcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by matt on 17/06/17.
 */
public class MultiSearchUtils {

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
    public static List<MatchResult> resultsAtPosition(
            final long matchPosition, final Collection matchingObjects) {
        final List<MatchResult> results = new ArrayList<MatchResult>(matchingObjects.size());
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
    public static  List<MatchResult> addPositionToResults(final List<MatchResult> originalResults, final long amountToAdd) {
        final int numResults = originalResults.size();
        final List<MatchResult> newResults = new ArrayList<MatchResult>(numResults);
        for (int i = 0; i < numResults; i++) {
            final MatchResult result = originalResults.get(i);
            newResults.add(new MatchResult(result.getMatchPosition() + amountToAdd, 1 )); //TODO: FIX THIS - WRONG!!!
        }
        return newResults;
    }
}
