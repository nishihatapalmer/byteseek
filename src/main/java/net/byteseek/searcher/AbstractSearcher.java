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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.MatchResult;

/**
 * An abstract searcher implementation which provides default implementations of
 * many of the overloaded search methods, providing default values to the real
 * search methods, and wrapping search results in lists if the version which returns a new list is selected.
 * Implementations only have to implement the four main methods for forwards and backwards searching over byte arrays
 * and WindowReaders.
 *
 * @author Matt Palmer
 */
public abstract class AbstractSearcher implements Searcher {

	/**
	 * Constants to indicate how many results were found.
	 */
	protected final static int ONE_RESULT_FOUND = 1;
	protected final static int NO_RESULTS_FOUND = 0;

	 /*************************************
	  * Wrap search results in a new list *
	  *************************************/

	@Override
	public List<MatchResult> searchForwards(final WindowReader reader,
											final long fromPosition, long toPosition) throws IOException {
		final List<MatchResult> results = new ArrayList<MatchResult>(1); // most of the time we'll have one or no results.
		searchForwards(reader, fromPosition, toPosition, results);
		return results;
	}

	@Override
	public List<MatchResult> searchForwards(final byte[] bytes, final int fromPosition, final int toPosition) {
		final List<MatchResult> results = new ArrayList<MatchResult>(1); // most of the time we'll have one or no results.
		searchForwards(bytes, fromPosition, toPosition, results);
		return results;
	}


	@Override
	public List<MatchResult> searchBackwards(final WindowReader reader,
											 final long fromPosition, final long toPosition) throws IOException {
		final List<MatchResult> results = new ArrayList<MatchResult>(1); // most of the time we'll have one or no results.
		searchBackwards(reader, fromPosition, toPosition, results);
		return results;
	}

	@Override
	public List<MatchResult> searchBackwards(final byte[] bytes, final int fromPosition, final int toPosition) {
		final List<MatchResult> results = new ArrayList<MatchResult>(1); // most of the time we'll have one or no results.
		searchBackwards(bytes, fromPosition, toPosition, results);
		return results;
	}


	/**************************************************
	 * Choose defaults for overridden search methods. *
	 **************************************************/

	@Override
	public int searchForwards(final WindowReader reader, final long fromPosition, final Collection<MatchResult> results) throws IOException {
		return searchForwards(reader, fromPosition, Long.MAX_VALUE, results);
	}

	@Override
	public List<MatchResult> searchForwards(final WindowReader reader, final long fromPosition) throws IOException {
		return searchForwards(reader, fromPosition, Long.MAX_VALUE);
	}

	@Override
	public int searchForwards(final WindowReader reader, final Collection<MatchResult> results) throws IOException {
		return searchForwards(reader, 0, Long.MAX_VALUE, results);
	}

	@Override
	public List<MatchResult> searchForwards(final WindowReader reader) throws IOException {
		return searchForwards(reader, 0, Long.MAX_VALUE);
	}

	@Override
	public int searchForwards(final byte[] bytes, final int fromPosition,final Collection<MatchResult> results) {
		return searchForwards(bytes, fromPosition, bytes.length - 1, results);
	}

	@Override
	public List<MatchResult> searchForwards(final byte[] bytes, final int fromPosition) {
		return searchForwards(bytes, fromPosition, bytes.length - 1);
	}

	@Override
	public int searchForwards(final byte[] bytes, final Collection<MatchResult> results) {
		return searchForwards(bytes, 0, bytes.length - 1, results);
	}

	@Override
	public List<MatchResult> searchForwards(final byte[] bytes) {
		return searchForwards(bytes, 0, bytes.length - 1);
	}

	@Override
	public int searchBackwards(final WindowReader reader, final long fromPosition, final Collection<MatchResult> results) throws IOException {
		return searchBackwards(reader, fromPosition, 0, results);
	}

	@Override
	public List<MatchResult> searchBackwards(final WindowReader reader, final long fromPosition) throws IOException {
		return searchBackwards(reader, fromPosition, 0);
	}

	@Override
	public int searchBackwards(final WindowReader reader, final Collection<MatchResult> results) throws IOException {
		return searchBackwards(reader, reader.length() - 1, 0, results);
	}

	@Override
	public List<MatchResult> searchBackwards(final WindowReader reader) throws IOException {
		return searchBackwards(reader, reader.length() - 1, 0);
	}

	@Override
	public int searchBackwards(final byte[] bytes, final int fromPosition, final Collection<MatchResult> results) {
		return searchBackwards(bytes, fromPosition, 0, results);
	}

	@Override
	public List<MatchResult> searchBackwards(final byte[] bytes, final int fromPosition) {
		return searchBackwards(bytes, fromPosition, 0);
	}

	@Override
	public int searchBackwards(final byte[] bytes, final Collection<MatchResult> results) {
		return searchBackwards(bytes, bytes.length - 1, 0, results);
	}

	@Override
	public List<MatchResult> searchBackwards(final byte[] bytes) {
		return searchBackwards(bytes, bytes.length - 1, 0);
	}

	/**
	 * Returns a position guaranteed to be within the length of the reader, or
	 * -1 if the reader itself has a length of zero.
	 * <p>
	 * It is constructed to avoid determining the overall length of the reader,
	 * except as a last resort. This is to be as stream-friendly as possible
	 * while guaranteeing that the position returned is within the reader
	 * (unless the reader itself has a length of zero, in which case -1 will be
	 * returned).
	 * <p>
	 * It achieves this by trying to read data from the position given
	 * //TODO: why adjust to zero if position is negative...?  negative position will have no data, and isn't that the right result?
	 * (adjusted to be zero if the position was negative). If there is data
	 * there, then the position is returned. If there is no data at that
	 * position, then the position is beyond the length of the data, hence the
	 * last position is returned (length - 1). If this is a stream, then the act
	 * of determining that the position has no data has already read in the
	 * entire stream, so accessing the length at this point makes no difference,
	 * as all that data would have to be loaded anyway in order to operate at
	 * the extreme end of the stream.
	 * 
	 * @param reader
	 *            The reader to acquire a valid position for.
	 * @param position
	 *            The position to try.
	 * @return A position guaranteed to be a valid position in the reader, or -1
	 *         if the reader has a length of zero.
	 * @throws IOException
	 *             if the reader cannot be read from.
	 */

	protected long withinLength(final WindowReader reader, final long position)
			throws IOException {
		final long positionToTry = position > 0 ? position : 0;
		return reader.getWindow(positionToTry) != null ? positionToTry : reader.length() - 1;
	}

}
