/*
 * Copyright Matt Palmer 2017, All rights reserved.
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

import net.byteseek.io.IOIterator;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.MatchResult;
import sun.plugin.dom.exception.InvalidStateException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * An iterator over search results, given a Searcher and byte array or WindowReader to search over.
 * Each iteration returns a list of MatchResults.  Some searchers can return more than one match for a single
 * position, e.g. if a multi-sequence searcher is used.
 * <p>
 * Static utility methods for finding all matches in data with a searcher are provided for convenience.
  <p>
 * Note that the List<MatchResult> returned on each iteration is the same list object each time.
 * On each iteration the list is cleared and the new results appended.  This avoids creating a new
 * list for each search iteration.  If you want to retain the MatchResults across iterations,
 * you should add them to a master list which you seperately maintain.
 * <p>
 * Design note: this class does not implement the standard Java Iterator interface.
 * This is because IOExceptions can occur during searching, and the Iterator interface does not permit
 * checked exceptions to be thrown. Rather than go around this in some way, we prefer to be familiar but not deceptive.
 */
public final class SearchIterator implements IOIterator<List<MatchResult>> {

    /* ************************
     * Static utility methods *
     **************************/

    /**
     * Finds all matches in the data using the searcher and returns a list of them.
     *
     * @param searcher The Searcher to search with.
     * @param data The data to search in.
     * @return a list of MatchResults containing all the matches in the data.
     */
    public static List<MatchResult> findAll(final Searcher searcher, final byte[] data) {
        return suppressIOException(new SearchIterator(searcher, data));
    }

    /**
     * Finds all matches in the data from the position specified and returns a list of them.
     *
     * @param searcher The Searcher to search with.
     * @param data The data to search in.
     * @param from The position to search from.
     * @return a list of MatchResults containing all the matches in the data from the from position to the end.
     */
    public static List<MatchResult> findAll(final Searcher searcher, final byte[] data, final int from) {
        return suppressIOException(new SearchIterator(searcher, data, from));
    }

    /**
     * Finds all matches in the data from and to specified positions and returns a list of them.
     * If the from position is larger than the to position, a backwards search will be used.
     *
     * @param searcher The Searcher to search with.
     * @param data The data to search in.
     * @param from The position to search from.
     * @param to The position to search to.
     * @return a List of MatchResults containing all the matches in the data between the from and to positions.
     */
    public static List<MatchResult> findAll(final Searcher searcher, final byte[] data, final int from, final int to) {
        return suppressIOException(new SearchIterator(searcher, data, from, to));
    }

    /**
     * Finds all matches in the data using the searcher and returns a list of them.
     *
     * @param searcher The Searcher to search with.
     * @param reader The data to search in.
     * @return a list of MatchResults containing all the matches in the data.
     * @throws IOException if a problem occurs reading the WindowReader.
     */
    public static List<MatchResult> findAll(final Searcher searcher, final WindowReader reader) throws IOException {
        return new SearchIterator(searcher, reader).nextAll();
    }

    /**
     * Finds all matches in the data from the position specified and returns a list of them.
     *
     * @param searcher The Searcher to search with.
     * @param reader The data to search in.
     * @param from The position to search from.
     * @return a list of MatchResults containing all the matches in the data from the from position to the end.
     * @throws IOException if a problem occurs reading the WindowReader.
     */
    public static List<MatchResult> findAll(final Searcher searcher, final WindowReader reader, final long from) throws IOException {
        return new SearchIterator(searcher, reader, from).nextAll();
    }

    /**
     * Finds all matches in the data from and to specified positions and returns a list of them.
     * If the from position is larger than the to position, a backwards search will be used.
     *
     * @param searcher The Searcher to search with.
     * @param reader The data to search in.
     * @param from The position to search from.
     * @param to The position to search to.
     * @return a List of MatchResults containing all the matches in the data between the from and to positions.
     * @throws IOException if a problem occurs reading the WindowReader.
     */
    public static List<MatchResult> findAll(final Searcher searcher, final WindowReader reader, final long from, final long to) throws IOException {
        return new SearchIterator(searcher, reader, from, to).nextAll();
    }


    /* *********
     * Members *
     ***********/

    /**
     * A private class which iterates over the search data.  We instantiate different subclasses depending on
     * whether we are searching forwards or backwards, and whether the data source is a byte array or a WindowReader.
     */
    private final BaseSearchIterator iterator;

    /**
     * Whether the iterator has yet looked to see if there are further results.
     */
    private boolean searchedForNext = false;

    /**
     * Whether there are further results.
     */
    private boolean hasNext;


    /* **************
     * Constructors *
     ****************/

    /**
     * Constructs a SearchIterator given a Searcher, a byte array to search in, and the positions to search between.
     * Searching is forwards unless the from position is bigger than the to position, in which case a backwards
     * search will be performed.
     * Searching will only take place within the bounds of the data, so it is safe to specify any values for the positions.
     *
     * @param searcher The Searcher to search with.
     * @param data A byte array which is the data to search in.
     * @param from The position to start searching from.  If bigger than the to position, searching will be backwards.
     * @param to The position to finish searching at.
     */
    public SearchIterator(final Searcher searcher, final byte[] data, final int from, final int to) {
        iterator = from <= to? new byteForwardIterator(searcher, data, from, to)
                             : new byteBackwardIterator(searcher, data, from, to);
    }

    /*
     * Constructs a SearchIterator given a Searcher, a WindowReader to search in, and the positions to search between.
     * Searching is forwards unless the from position is bigger than the to position, in which case a backwards
     * search will be performed.
     * Searching will only take place within the bounds of the data, so it is safe to specify any values for the positions.
     *
     * @param searcher The Searcher to search with.
     * @param reader A WindowReader which is the data to search in.
     * @param from The position to start searching from.  If bigger than the to position, searching will be backwards.
     * @param to The position to finish searching at.
     */
    public SearchIterator(final Searcher searcher, final WindowReader reader, final long from, final long to) {
        iterator = from <= to? new readerForwardIterator(searcher, reader, from, to)
                             : new readerBackwardIterator(searcher, reader, from, to);
    }

    /**
     * Constructs a SearchIterator given a Searcher, a byte array to search in, and a position to search from.
     * Searching will be forwards until the end of the byte array.
     *
     * @param searcher The Searcher to search with.
     * @param data A byte array which is the data to search in.
     * @param from The position to start searching from.  Searching will be forwards until the end of the array.
     */
    public SearchIterator(final Searcher searcher, final byte[] data, final int from) {
        this(searcher, data, from, Integer.MAX_VALUE);
    }

    /**
     * Constructs a SearchIterator given a Searcher, a WindowReader to search in, and a position to search from.
     * @param searcher The Searcher to search with.
     * @param reader A WindowReader which is the data to search in.
     * @param from The position to start searching from.  Searching will be forwards until the end of the Reader.
     */
    public SearchIterator(final Searcher searcher, final WindowReader reader, final long from) {
        this(searcher, reader, from, Long.MAX_VALUE);
    }

    /**
     * Constructs a SearchIterator given a Searcher and a byte array to search in.
     * The entire byte array will be searched forwards.
     * @param searcher The Searcher to search with.
     * @param data The data to search in.
     */
    public SearchIterator(final Searcher searcher, final byte[] data) {
        this(searcher, data, 0, Integer.MAX_VALUE);
    }

    /**
     * Constructs a SearchIterator given a Searcher and a WindowReader to search in.
     * The entire WindowReader will be searched forwards.
     * @param searcher The Searcher to search with.
     * @param reader The reader to search in.
     */
    public SearchIterator(final Searcher searcher, final WindowReader reader) {
        this(searcher, reader, 0, Long.MAX_VALUE);
    }


    /* ****************
     * Public methods *
     ******************/

    /**
     * Returns true if there are more search results available.
     *
     * @return true if there are more search results available.
     * @throws IOException If there was a problem reading the search data.
     */
    @Override
    public boolean hasNext() throws IOException {
        if (!searchedForNext) {
            hasNext = iterator.hasNext();
            searchedForNext = true;
        }
        return hasNext;
    }

    /**
     * Returns a List of MatchResults containing the next set of results.
     * Note that this implementation returns the same List each time next is called, clearing any previous results.
     * This is to avoid creating a new list on each search iteration.  If you want to retain all the MatchResults,
     * across iterations, you should add them to a Collection which you maintain separately.
     *
     * @return a List of MatchResults containing the next set of results.
     * @throws IOException If there was a problem reading the search data.
     * @throws NoSuchElementException if next() is called but there are no further results.
     */
    @Override
    public List<MatchResult> next() throws IOException {
        if (hasNext()) {
            searchedForNext = false;
            return iterator.next();
        }
        throw new NoSuchElementException("No more results for search iterator: " + iterator);
    }

    /**
     * Accumulates all the remaining next items into a single list and returns it.
     *
     * @return a list of all the remaining items.
     * @throws IOException If a problem happens reading the data.
     */
    public List<MatchResult> nextAll() throws IOException {
        final List<MatchResult> remainingResults = new ArrayList<MatchResult>();
        while (hasNext()) {
            remainingResults.addAll(next());
        }
        return remainingResults;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove is not supported for a SearchIterator.");
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + '(' + iterator + ')';
    }


    /* *****************
     * Private classes *
     *******************/

    /**
     * An abstract base class for all search iterators, implementing most of the logic.
     * Subclasses are the different search strategies - e.g. forwards in a byte array, backwards in a reader, etc.
     */
    private static abstract class BaseSearchIterator {

        protected final Searcher searcher;
        protected final long to;
        protected final List<MatchResult> results = new ArrayList<MatchResult>();

        protected long pos;

        private BaseSearchIterator(final Searcher searcher, final long from, final long to) {
            this.searcher      = searcher;
            this.pos           = from;
            this.to            = to;
        }

        /**
         * Clears any previous results, searches for the next set of results, adding them to the results.
         * Returns true if there were any results.
         *
         * @return true if there were any results.
         * @throws IOException If there was a problem reading the search data.
         */
        private boolean hasNext() throws IOException {
            results.clear();
            return getNumNextResults() > 0;
        }

        /**
         * Sets the search position to the next place to search from and returns the current set of results.
         * @return The current set of results.
         */
        private List<MatchResult> next() {
            pos = nextSearchPosition(); //TODO: do we need a defined stop signal (e.g. pos is negative...?)
            return results;
        }

         /**
         * Searches in the data for the next set of results, placing them in the results List,
         * and returning the number of results added.
         *
         * @return The number of results added.
         * @throws IOException If there was a problem reading the search data.
         */
        protected abstract int getNumNextResults() throws IOException;

        /**
         * Returns the next valid search position for the iterator.
         * @return the next valid search position for the iterator.
         */
        protected abstract long nextSearchPosition();

        /**
         * Returns the search position one on from the furthest match we have when searching forwards.
         *
         * @return the search position one on from the furthest match we have when searching forwards.
         */
        protected long getNextForwardSearchPosition() {
            long furthestPosition = Long.MIN_VALUE;
            for (final MatchResult result : results) {
                final long resultPosition = result.getMatchPosition();
                if (resultPosition > furthestPosition) {
                    furthestPosition = resultPosition;
                }
            }
            return furthestPosition + 1;
        }

        /**
         * Returns the search position one behind the further match we have when searching backwards.
         *
         * @return the search position one behind the further match we have when searching backwards.
         */
        protected long getNextBackwardSearchPosition() {
            long furthestPosition = Long.MAX_VALUE;
            for (final MatchResult result : results) {
                final long resultPosition = result.getMatchPosition();
                if (resultPosition < furthestPosition) {
                    furthestPosition = resultPosition;
                }
            }
            return furthestPosition - 1;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "(searcher:" + searcher + " pos: " + pos + " to:" + to + ')';
        }
    }

    /**
     * Searches forwards over a byte array.
     */
    private final class byteForwardIterator extends BaseSearchIterator {
        private final byte[] data;
        private byteForwardIterator(final Searcher searcher, final byte[] data, final int from, final int to) {
            super(searcher, from, to);
            this.data = data;
        }
        @Override
        protected int getNumNextResults() {
            return searcher.searchForwards(data, (int) pos, (int) to, results);
        }
        @Override
        protected long nextSearchPosition() {
            return getNextForwardSearchPosition();
        }
    }

    /**
     * Searches backwards over a byte array.
     */
    private final class byteBackwardIterator extends BaseSearchIterator {
        private final byte[] data;
        private byteBackwardIterator(final Searcher searcher, final byte[] data, final int from, final int to) {
            super(searcher, from, to);
            this.data = data;
        }
        @Override
        protected int getNumNextResults() {
            return searcher.searchBackwards(data, (int) pos, (int) to, results);
        }
        @Override
        protected long nextSearchPosition() {
            return getNextBackwardSearchPosition();
        }
    }

    /**
     * Searches forwards in a WindowReader.
     */
    private final class readerForwardIterator extends BaseSearchIterator {
        private final WindowReader reader;
        private readerForwardIterator(final Searcher searcher, final WindowReader reader, final long from, final long to) {
            super(searcher, from, to);
            this.reader = reader;
        }
        @Override
        protected int getNumNextResults() throws IOException {
            return searcher.searchForwards(reader, pos, to, results);
        }
        @Override
        protected long nextSearchPosition() {
            return getNextForwardSearchPosition();
        }
    }

    /**
     * Searches backwards in a WindowReader.
     */
    private final class readerBackwardIterator extends BaseSearchIterator {
        private final WindowReader reader;
        private readerBackwardIterator(final Searcher searcher, final WindowReader reader, final long from, final long to) {
            super(searcher, from, to);
            this.reader = reader;
        }
        @Override
        protected int getNumNextResults() throws IOException {
            return searcher.searchBackwards(reader, pos, to, results);
        }
        @Override
        protected long nextSearchPosition() {
            return getNextBackwardSearchPosition();
        }
    }


    /* ****************************
     * Private convenience method *
     ******************************/

    private static List<MatchResult> suppressIOException(final SearchIterator iterator) {
        try {
            return iterator.nextAll();
        } catch (IOException cannotHappen) { // Suppress IOExceptions - only to be used where they cannot happen.
            throw new IllegalArgumentException("Programming error: should not be possible to get an IO Exception here with the SearchIterator:" + iterator, cannotHappen);
        }
    }

}
