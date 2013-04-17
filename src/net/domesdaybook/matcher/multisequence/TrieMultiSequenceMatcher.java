/*
 * Copyright Matt Palmer 2009-2012, All rights reserved.
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

package net.domesdaybook.matcher.multisequence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.domesdaybook.automata.State;
import net.domesdaybook.automata.trie.Trie;
import net.domesdaybook.automata.trie.TrieFactory;
import net.domesdaybook.io.Window;
import net.domesdaybook.io.WindowReader;
import net.domesdaybook.matcher.sequence.SequenceMatcher;

/**
 * A {@link MultiSequenceMatcher} uses a {@link Trie} structure to match 
 * with. A Trie is a deterministic automata, arranged as a prefix tree of all
 * the sequences in it.  This means that no matter how many SequenceMatchers
 * are added to it (hundreds, thousands, mjllions...), to match it performs no more
 * comparisons than required for the longest sequence in the Trie (and usually less). 
 * <p>
 * This is highly time-efficient, but takes more space to hold the Trie automata 
 * states in addition to the original list of SequenceMatchers used to construct 
 * the Trie.
 * <p>
 * Note that for a very low number of SequenceMatchers, it is possible that a simpler
 * matcher, such as the {@link ListMultiSequenceMatcher} may be faster, due to lower
 * complexity and fewer objects required.
 * <p>
 * The TrieMultiSequenceMatcher is immutable, so can be safely used in multi-
 * threaded applications.
 * 
 * @author Matt Palmer
 */
public final class TrieMultiSequenceMatcher implements MultiSequenceMatcher {

	private final static TrieFactory<SequenceMatcher>	DEFAULT_TRIE_FACTORY	= new SequenceMatcherTrieFactory();

	private final Trie<SequenceMatcher>					trie;

	/**
	 * Constructs an immutable TrieMultiSequenceMatcher from a collection of {@link SequenceMatcher}s,
	 * using a default {@link SequenceMatcherTrieFactory} to create the Trie with.
	 * 
	 * @param matchers The collection of sequences to construct the TrieMultiSequenceMatcher from.
	 */
	public TrieMultiSequenceMatcher(final Collection<? extends SequenceMatcher> matchers) {
		this(DEFAULT_TRIE_FACTORY, matchers);
	}

	/**
	 * Constructs an immutable TrieMultiSequenceMatcher from a collection of {@link SequenceMatcher}s,
	 * using the {@link TrieFactory} provided to build the Trie.
	 * 
	 * @param factory The factory to create a {@link Trie} with.
	 * @param matchers The collection of sequences to construct the TrieMultiSequenceMatcher from.
	 */
	public TrieMultiSequenceMatcher(final TrieFactory<SequenceMatcher> factory,
			final Collection<? extends SequenceMatcher> matchers) {
		if (factory == null) {
			throw new IllegalArgumentException("Null factory passed in to TrieMultiSequenceMatcher");
		}
		if (matchers == null || matchers.isEmpty()) {
			throw new IllegalArgumentException(
					"Null or empty SequenceMatcher collection passed in to TrieMultiSequenceMatcher.");
		}
		this.trie = factory.create(matchers);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<SequenceMatcher> allMatches(final WindowReader reader, final long matchPosition)
			throws IOException {
		List<SequenceMatcher> result = Collections.emptyList();
		State<SequenceMatcher> state = trie.getInitialState();
		long currentPosition = matchPosition;
		Window window = reader.getWindow(matchPosition);
		while (window != null) {
			final int windowLength = window.length();
			final byte[] array = window.getArray();
			int windowPosition = reader.getWindowOffset(currentPosition);
			while (windowPosition < windowLength) {
				final byte currentByte = array[windowPosition++];
				state = state.getNextState(currentByte);
				if (state == null) {
					return result;
				}
				if (state.isFinal()) {
					final Collection<SequenceMatcher> matching = state.getAssociations();
					if (result.isEmpty()) {
						result = new ArrayList<SequenceMatcher>(matching.size() * 2);
					}
					result.addAll(matching);
				}
			}
			currentPosition += windowLength;
			window = reader.getWindow(matchPosition);
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<SequenceMatcher> allMatches(final byte[] bytes, final int matchPosition) {
		List<SequenceMatcher> result = Collections.emptyList();
		final int noOfBytes = bytes.length;
		final int minimumLength = trie.getMinimumLength();
		if (matchPosition >= minimumLength - 1 && matchPosition + minimumLength < noOfBytes) {
			State<SequenceMatcher> state = trie.getInitialState();
			int currentPosition = matchPosition;
			while (state != null && currentPosition < noOfBytes) {
				final byte currentByte = bytes[currentPosition++];
				state = state.getNextState(currentByte);
				if (state != null && state.isFinal()) {
					final Collection<SequenceMatcher> matching = state.getAssociations();
					if (result.isEmpty()) {
						result = new ArrayList<SequenceMatcher>(matching.size() * 2);
					}
					result.addAll(matching);
				}
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<SequenceMatcher> allMatchesBackwards(final WindowReader reader,
			final long matchPosition) throws IOException {
		List<SequenceMatcher> result = Collections.emptyList();
		State<SequenceMatcher> state = trie.getInitialState();
		long currentPosition = matchPosition;
		Window window = reader.getWindow(matchPosition);
		while (window != null) {
			final int windowLength = window.length();
			final byte[] array = window.getArray();
			int windowPosition = reader.getWindowOffset(currentPosition);
			while (windowPosition >= 0) {
				final byte currentByte = array[windowPosition--];
				state = state.getNextState(currentByte);
				if (state == null) {
					return result;
				}
				if (state.isFinal()) {
					final Collection<SequenceMatcher> matching = state.getAssociations();
					if (result.isEmpty()) {
						result = new ArrayList<SequenceMatcher>(matching.size() * 2);
					}
					result.addAll(matching);
				}
			}
			currentPosition -= windowLength;
			window = reader.getWindow(matchPosition);
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<SequenceMatcher> allMatchesBackwards(final byte[] bytes,
			final int matchPosition) {
		List<SequenceMatcher> result = Collections.emptyList();
		final int noOfBytes = bytes.length;
		final int minimumLength = trie.getMinimumLength();
		if (matchPosition >= minimumLength - 1 && matchPosition + minimumLength < noOfBytes) {
			State<SequenceMatcher> state = trie.getInitialState();
			int currentPosition = matchPosition;
			while (state != null && currentPosition >= 0) {
				final byte currentByte = bytes[currentPosition--];
				state = state.getNextState(currentByte);
				if (state != null && state.isFinal()) {
					final Collection<SequenceMatcher> matching = state.getAssociations();
					if (result.isEmpty()) {
						result = new ArrayList<SequenceMatcher>(matching.size() * 2);
					}
					result.addAll(matching);
				}
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SequenceMatcher firstMatch(final WindowReader reader, final long matchPosition)
			throws IOException {
		State<SequenceMatcher> state = trie.getInitialState();
		long currentPosition = matchPosition;
		Window window = reader.getWindow(matchPosition);
		while (window != null) {
			final int windowLength = window.length();
			final byte[] array = window.getArray();
			int windowPosition = reader.getWindowOffset(currentPosition);
			while (windowPosition < windowLength) {
				final byte currentByte = array[windowPosition++];
				state = state.getNextState(currentByte);
				if (state == null) {
					return null;
				}
				if (state.isFinal()) {
					return getFirstAssociation(state);
				}
			}
			currentPosition += windowLength;
			window = reader.getWindow(matchPosition);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SequenceMatcher firstMatch(final byte[] bytes, final int matchPosition) {
		if (matchPosition >= 0) {
			final int noOfBytes = bytes.length;
			State<SequenceMatcher> state = trie.getInitialState();
			int currentPosition = matchPosition;
			while (state != null && currentPosition < noOfBytes) {
				final byte currentByte = bytes[currentPosition++];
				state = state.getNextState(currentByte);
				if (state != null && state.isFinal()) {
					return getFirstAssociation(state);
				}
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SequenceMatcher firstMatchBackwards(final WindowReader reader, final long matchPosition)
			throws IOException {
		State<SequenceMatcher> state = trie.getInitialState();
		long currentPosition = matchPosition;
		Window window = reader.getWindow(matchPosition);
		while (window != null) {
			final int windowLength = window.length();
			final byte[] array = window.getArray();
			int windowPosition = reader.getWindowOffset(currentPosition);
			while (windowPosition >= 0) {
				final byte currentByte = array[windowPosition--];
				state = state.getNextState(currentByte);
				if (state == null) {
					return null;
				}
				if (state.isFinal()) {
					return getFirstAssociation(state);
				}
			}
			currentPosition -= windowLength;
			window = reader.getWindow(matchPosition);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SequenceMatcher firstMatchBackwards(final byte[] bytes, final int matchPosition) {
		final int noOfBytes = bytes.length;
		if (matchPosition < noOfBytes) {
			State<SequenceMatcher> state = trie.getInitialState();
			int currentPosition = matchPosition;
			while (state != null && currentPosition >= 0) {
				final byte currentByte = bytes[currentPosition--];
				state = state.getNextState(currentByte);
				if (state != null && state.isFinal()) {
					return getFirstAssociation(state);
				}
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean matches(final WindowReader reader, final long matchPosition) throws IOException {
		return firstMatch(reader, matchPosition) != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean matches(final byte[] bytes, final int matchPosition) {
		return firstMatch(bytes, matchPosition) != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean matchesBackwards(final WindowReader reader, final long matchPosition)
			throws IOException {
		return firstMatchBackwards(reader, matchPosition) != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean matchesBackwards(final byte[] bytes, final int matchPosition) {
		return firstMatchBackwards(bytes, matchPosition) != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMinimumLength() {
		return trie.getMinimumLength();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMaximumLength() {
		return trie.getMaximumLength();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MultiSequenceMatcher reverse() {
		return new TrieMultiSequenceMatcher(MultiSequenceUtils.reverseMatchers(trie.getSequences()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MultiSequenceMatcher newInstance(Collection<? extends SequenceMatcher> sequences) {
		return new TrieMultiSequenceMatcher(sequences);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SequenceMatcher> getSequenceMatchers() {
		return new ArrayList<SequenceMatcher>(trie.getSequences());
	}

	/**
	 * Returns a string representation of this matcher.  The format is subject
	 * to change, but it will generally return the name of the matching class
	 * and regular expressions defining the sequences matched by the matcher.
	 * 
	 * @return A string representing this matcher.
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + trie.getSequences() + ")";
	}

	/**
	 * Returns the SequenceMatcher which happens to be the first one associated
	 * with an automata State.  A State may be associated with zero to many
	 * SequenceMatchers.  This is to support the firstMatch functions.
	 * 
	 * @param state The State to get the first associated SequenceMatcher from.
	 * @return The first associated SequenceMatcher, or null if there are none.
	 */
	private SequenceMatcher getFirstAssociation(final State<SequenceMatcher> state) {
		final Iterator<SequenceMatcher> associationIterator = state.associationIterator();
		if (associationIterator.hasNext()) {
			return associationIterator.next();
		}
		return null;
	}

}
