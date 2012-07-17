/*
 * Copyright Matt Palmer 2011-12, All rights reserved.
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import net.domesdaybook.reader.Reader;

/**
 * An iterator which iterates over a {@link net.domesdaybook.reader.Reader} or a
 * byte array, using a provided {@link Searcher}. Each iteration returns the
 * next set of search results, searching forwards.
 * 
 * @param <T>
 *            The type of object returned on a match by the Searcher.
 * @author Matt Palmer
 */
public class ForwardSearchIterator<T> implements
		Iterator<List<SearchResult<T>>> {

	// immutable fields:
	private final byte[] bytes;
	private final Reader reader;
	private final long toPosition;
	private final Searcher<T> searcher;

	// private state:
	private long searchPosition;
	private boolean searchedForNext;
	private List<SearchResult<T>> searchResults = Collections.emptyList();

	/**
	 * Constructs a ForwardSearchIterator from a {@link Searcher} and
	 * {@link net.domesdaybook.reader.Reader}, searching forwards from the end
	 * of the Reader to the end of the Reader.
	 * 
	 * @param searcher
	 *            The Searcher to use.
	 * @param reader
	 *            The Reader to search in.
	 * @throws IllegalArgumentException
	 *             if the Searcher or Reader is null.
	 */
	public ForwardSearchIterator(final Searcher<T> searcher, final Reader reader) {
		this(searcher, 0, Long.MAX_VALUE, reader);
	}

	/**
	 * Constructs a ForwardSearchIterator from a {@link Searcher} and
	 * {@link net.domesdaybook.reader.Reader}, searching forwards from the
	 * position specified in the Reader to the end of the Reader.
	 * 
	 * @param searcher
	 *            The Searcher to use.
	 * @param reader
	 *            The Reader to search in.
	 * @param fromPosition
	 *            The position to start searching forwards from.
	 * @throws IllegalArgumentException
	 *             if the Searcher or Reader is null.
	 */
	public ForwardSearchIterator(final Searcher<T> searcher,
			final Reader reader, final long fromPosition) {
		this(searcher, fromPosition, Long.MAX_VALUE, reader);
	}

	/**
	 * Constructs a ForwardSearchIterator from a {@link Searcher} and
	 * {@link net.domesdaybook.reader.Reader}, searching forwards from the
	 * position specified to the final position.
	 * 
	 * @param searcher
	 *            The Searcher to use.
	 * @param fromPosition
	 *            The position to start searching forwards from.
	 * @param toPosition
	 *            The final position to search up to in the Reader.
	 * @param reader
	 *            The Reader to search in.
	 * @throws IOException
	 *             If determining the length of the Reader causes an error.
	 * @throws IllegalArgumentException
	 *             if the Searcher or Reader is null.
	 */
	public ForwardSearchIterator(final Searcher<T> searcher,
			final long fromPosition, final long toPosition, final Reader reader) {
		if (searcher == null || reader == null) {
			throw new IllegalArgumentException("Null searcher or byte reader.");
		}
		this.searcher = searcher;
		this.reader = reader;
		this.toPosition = toPosition;
		this.bytes = null;
		this.searchPosition = fromPosition;
	}

	/**
	 * Constructs a ForwardSearchIterator from a {@link Searcher} and byte
	 * array, searching forwards from the start of the array to the end of the
	 * array.
	 * 
	 * @param searcher
	 *            The Searcher to use.
	 * @param bytes
	 *            The byte array to search in.
	 * @throws IllegalArgumentException
	 *             if the Searcher or byte array is null.
	 */
	public ForwardSearchIterator(final Searcher<T> searcher, final byte[] bytes) {
		this(searcher, 0, bytes.length - 1, bytes);
	}

	/**
	 * Constructs a ForwardSearchIterator from a {@link Searcher} and byte
	 * array, searching forwards from the position specified in the byte array
	 * to the end of the array.
	 * 
	 * @param searcher
	 *            The Searcher to use.
	 * @param bytes
	 *            The byte array to search in.
	 * @param fromPosition
	 *            The position to start searching backwards from.
	 * @throws IllegalArgumentException
	 *             if the Searcher or byte array is null.
	 */
	public ForwardSearchIterator(final Searcher<T> searcher,
			final byte[] bytes, final long fromPosition) {
		this(searcher, fromPosition, bytes.length - 1, bytes);
	}

	/**
	 * Constructs a ForwardSearchIterator from a {@link Searcher} and byte
	 * array, searching forwards from the position specified in the array to the
	 * final position specified.
	 * 
	 * @param searcher
	 *            The Searcher to use.
	 * @param fromPosition
	 *            The position to start searching forwards from.
	 * @param toPosition
	 *            The final position to search up to in the array.
	 * @param bytes
	 *            The byte array to search in.
	 * @throws IllegalArgumentException
	 *             if the Searcher or array is null.
	 */
	public ForwardSearchIterator(final Searcher<T> searcher,
			final long fromPosition, final long toPosition, final byte[] bytes) {
		if (searcher == null || bytes == null) {
			throw new IllegalArgumentException("Null searcher or byte array.");
		}
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
				searchResults = getNextSearchResults();
				searchedForNext = true;
			} catch (final IOException ex) {
				return false;
			}
		}
		return !searchResults.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SearchResult<T>> next() {
		if (hasNext()) {
			searchPosition = getNextSearchPosition();
			searchedForNext = false;
			return searchResults;
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

	private List<SearchResult<T>> getNextSearchResults() throws IOException {
		List<SearchResult<T>> nextResults = Collections.emptyList();
		if (reader != null) {
			nextResults = searcher.searchForwards(reader, searchPosition,
					toPosition);
		} else if (bytes != null) {
			nextResults = searcher.searchForwards(bytes, (int) searchPosition,
					(int) toPosition);
		}
		return nextResults;
	}

	private long getNextSearchPosition() {
		long furthestPosition = Long.MIN_VALUE;
		for (final SearchResult<T> result : searchResults) {
			final long resultPosition = result.getMatchPosition();
			if (resultPosition > furthestPosition) {
				furthestPosition = resultPosition;
			}
		}
		return furthestPosition + 1;
	}

}
