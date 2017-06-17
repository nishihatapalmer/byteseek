/*
 * Copyright Matt Palmer 2011-17, All rights reserved.
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.MatchResult;
import net.byteseek.utils.ArgUtils;

/**
 * An iterator which iterates over a {@link net.byteseek.io.reader.WindowReader} or a
 * byte array, using a provided {@link Searcher}. Each iteration returns the
 * next set of search results, searching backwards.
 *
 * @author Matt Palmer
 */
public class BackwardSearchIterator implements Iterator<List<MatchResult>> {

	// immutable fields:
	private final byte[] bytes;
	private final WindowReader reader;
	private final long toPosition;
	private final Searcher searcher;

	// private state:
	private long searchPosition;
	private boolean searchedForNext = false;
	private List<MatchResult> matchResults = Collections.emptyList();

	//TODO: allow specifying whether results should be one past last search, or carry on from past the last match...
	//      some searches want to pick up the search after the end of a matched pattern (no overlapping matches).

	/**
	 * Constructs a BackwardSearchIterator from a {@link Searcher} and
	 * {@link net.byteseek.io.reader.WindowReader}, searching backwards from the end
	 * of the WindowReader to the start of the WindowReader.
	 * 
	 * @param searcher The Searcher to use.
	 * @param reader   The WindowReader to search in.
	 * @throws IOException  If determining the length of the WindowReader causes an error.
	 * @throws IllegalArgumentException if the Searcher or WindowReader is null.
	 */
	public BackwardSearchIterator(final Searcher searcher, final WindowReader reader) throws IOException {
		this(searcher, reader.length() - 1, 0, reader);
	}

	/**
	 * Constructs a BackwardSearchIterator from a {@link Searcher} and
	 * {@link net.byteseek.io.reader.WindowReader}, searching backwards from the
	 * position specified in the WindowReader to the start of the WindowReader.
	 * 
	 * @param searcher     The Searcher to use.
	 * @param reader       The WindowReader to search in.
	 * @param fromPosition The position to start searching backwards from.
	 * @throws IllegalArgumentException if the Searcher or WindowReader is null.
	 */
	public BackwardSearchIterator(final Searcher searcher, final WindowReader reader, final long fromPosition) {
		this(searcher, fromPosition, 0, reader);
	}

	/**
	 * Constructs a BackwardSearchIterator from a {@link Searcher} and
	 * {@link net.byteseek.io.reader.WindowReader}, searching backwards from the
	 * position specified in the WindowReader to the final position specified in the
	 * WindowReader.
	 * 
	 * @param searcher     The Searcher to use.
	 * @param fromPosition The position to start searching backwards from.
	 * @param toPosition   The final position to search up to in the WindowReader.
	 * @param reader       The WindowReader to search in.
	 * @throws IllegalArgumentException if the Searcher or WindowReader is null.
	 */
	public BackwardSearchIterator(final Searcher searcher, final long fromPosition, final long toPosition, final WindowReader reader) {
		ArgUtils.checkNullObject(searcher, "searcher");
		ArgUtils.checkNullObject(reader, "reader");
		this.searcher = searcher;
		this.reader = reader;
		this.toPosition = toPosition;
		this.bytes = null;
		this.searchPosition = fromPosition;
	}

	/**
	 * Constructs a BackwardSearchIterator from a {@link Searcher} and byte
	 * array, searching backwards from the end of the array to the start of the
	 * array.
	 * 
	 * @param searcher The Searcher to use.
	 * @param bytes    The byte array to search in.
	 * @throws IllegalArgumentException if the Searcher or byte array is null.
	 */
	public BackwardSearchIterator(final Searcher searcher, final byte[] bytes) {
		this(searcher, bytes.length - 1, 0, bytes);
	}

	/**
	 * Constructs a BackwardSearchIterator from a {@link Searcher} and byte
	 * array, searching backwards from the position specified in the byte array
	 * to the start of the array.
	 * 
	 * @param searcher     The Searcher to use.
	 * @param bytes        The byte array to search in.
	 * @param fromPosition The position to start searching backwards from.
	 * @throws IllegalArgumentException if the Searcher or byte array is null.
	 */
	public BackwardSearchIterator(final Searcher searcher, final byte[] bytes, final int fromPosition) {
		this(searcher, fromPosition, 0, bytes);
	}

	/**
	 * Constructs a BackwardSearchIterator from a {@link Searcher} and byte
	 * array, searching backwards from the position specified in the array to
	 * the final position specified.
	 * 
	 * @param searcher     The Searcher to use.
	 * @param fromPosition The position to start searching backwards from.
	 * @param toPosition   The final position to search up to in the array.
	 * @param bytes        The byte array to search in.
	 * @throws IllegalArgumentException if the Searcher or array is null.
	 */
	public BackwardSearchIterator(final Searcher searcher, final int fromPosition, final int toPosition, final byte[] bytes) {
		ArgUtils.checkNullObject(searcher, "searcher");
		ArgUtils.checkNullObject(bytes, "bytes");
		this.searcher = searcher;
		this.bytes = bytes;
		this.toPosition = toPosition;
		this.reader = null;
		this.searchPosition = fromPosition;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() {
		if (!searchedForNext) {
			try {
				matchResults = getNextMatchResults();
				searchedForNext = true;
			} catch (final IOException ex) {
				return false;
			}
		}
		return !matchResults.isEmpty();
	}

	//TODO: make more efficient next() passing in a pre-existing list of results.
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<MatchResult> next() {
		if (hasNext()) {
			searchPosition = getNextSearchPosition();
			searchedForNext = false;
			return matchResults;
		}
		throw new NoSuchElementException();
	}

	/**
	 * It is not possible to remove search results from this iterator.
	 * 
	 * @throws UnsupportedOperationException
	 *             if the method is called.
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException("Cannot remove search results.");
	}

	/**
	 * Returns the current search position in the iterator.
	 * 
	 * @return long the current search position in the iterator.
	 */
	public long getSearchPosition() {
		return searchPosition;
	}

	/**
	 * Sets the search position to use in the iterator.
	 * 
	 * @param searchPosition
	 *            The search position to search from in this iterator.
	 */
	public void setSearchPosition(final long searchPosition) {
		this.searchPosition = searchPosition;
		searchedForNext = false;
	}

	private List<MatchResult> getNextMatchResults() throws IOException {
		List<MatchResult> nextMatchingPosition = Collections.emptyList();
		if (reader != null) {
			nextMatchingPosition = searcher.searchBackwards(reader, searchPosition, toPosition);
		} else if (bytes != null) {
			nextMatchingPosition = searcher.searchBackwards(bytes, (int) searchPosition, (int) toPosition);
		}
		return nextMatchingPosition;
	}

	private long getNextSearchPosition() {
		long furthestPosition = Long.MAX_VALUE;
		for (final MatchResult result : matchResults) {
			final long resultPosition = result.getMatchPosition();
			if (resultPosition < furthestPosition) {
				furthestPosition = resultPosition;
			}
		}
		return furthestPosition - 1;
	}

}
