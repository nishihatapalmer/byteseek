/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
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

package net.byteseek.matcher.automata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.byteseek.automata.Automata;
import net.byteseek.automata.State;
import net.byteseek.io.reader.Window;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.MatchResult;

/**
 * 
 * @author Matt Palmer
 */
public class DfaMatcher<T> implements AutomataMatcher<T> {

	private final Automata<T>	automata;

	/**
	 * 
	 * @param automata
	 */
	public DfaMatcher(final Automata<T> automata) {
		this.automata = automata;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean matches(final WindowReader reader, final long matchPosition) throws IOException {
		// Setup
		long currentPosition = matchPosition;
		Window window = reader.getWindow(currentPosition);
		State<T> state = automata.getInitialState();
		// While we have a window on the data to match in:
		while (window != null) {
			final byte[] bytes = window.getArray();
			final int windowLength = window.length();
			final int windowStart = reader.getWindowOffset(currentPosition);
			int windowPos = windowStart;

			// While we have states to match:
			while (state != null && windowPos < windowLength) {

				// See if the active states is final (a match).
				if (state.isFinal()) {
					return true;
				}

				// No match was found, find the next state to follow:
				final byte currentByte = bytes[windowPos++];
				state = state.getNextState(currentByte);
			}
			currentPosition += windowLength - windowStart;
			window = reader.getWindow(currentPosition);
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean matches(final byte[] bytes, final int matchPosition) {
		// Setup
		final int length = bytes.length;
		if (matchPosition >= 0 && matchPosition < length) {
			int currentPosition = matchPosition;
			State<T> currentState = automata.getInitialState();

			// While there is a state to process:
			while (currentState != null && currentPosition < length) {

				// See if the next state is final (a match).
				if (currentState.isFinal()) {
					return true;
				}

				// No match was found, find the next state to follow:
				final byte currentByte = bytes[currentPosition++];
				currentState = currentState.getNextState(currentByte);
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MatchResult<T> firstMatch(final WindowReader reader, final long matchPosition)
			throws IOException {
		// Setup
		long currentPosition = matchPosition;
		Window window = reader.getWindow(currentPosition);
		State<T> state = automata.getInitialState();
		// While we have a window on the data to match in:
		while (window != null) {
			final byte[] bytes = window.getArray();
			final int windowLength = window.length();
			final int windowStart = reader.getWindowOffset(currentPosition);
			int windowPos = windowStart;

			// While we have states to match:
			while (state != null && windowPos < windowLength) {

				// See if the state is final (a match).
				if (state.isFinal()) {
					final long matchLength = currentPosition - matchPosition + windowPos
							- windowStart;
					return new DfaMatchResult<T>(matchPosition, matchLength, null, state);
				}

				// No match was found, find the next state to follow:
				final byte currentByte = bytes[windowPos++];
				state = state.getNextState(currentByte);
			}
			currentPosition += windowLength - windowStart;
			window = reader.getWindow(currentPosition);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MatchResult<T> nextMatch(final WindowReader reader, final MatchResult<T> lastMatch)
			throws IOException {
		if (lastMatch instanceof DfaMatchResult) {
			final long matchPosition = lastMatch.getMatchPosition();
			final long startPosition = matchPosition + lastMatch.getMatchLength();
			long currentPosition = startPosition;
			Window window = reader.getWindow(currentPosition);
			State<T> state = ((DfaMatchResult<T>) lastMatch).getMatchingState();
			// While we have a window on the data to match in:
			while (window != null) {
				final byte[] bytes = window.getArray();
				final int windowLength = window.length();
				final int windowStart = reader.getWindowOffset(currentPosition);
				int windowPos = windowStart;

				// While we have states to match:
				while (state != null && windowPos < windowLength) {

					// Find the next state to follow.
					final byte currentByte = bytes[windowPos++];
					state = state.getNextState(currentByte);

					// See if the state is final (a match).
					if (state != null && state.isFinal()) {
						final long matchLength = currentPosition - matchPosition + windowPos
								- windowStart;
						return new DfaMatchResult<T>(matchPosition, matchLength, null, state);
					}
				}
				currentPosition += windowLength - windowStart;
				window = reader.getWindow(currentPosition);
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<MatchResult<T>> allMatches(final WindowReader reader, final long matchPosition)
			throws IOException {
		// Setup
		long currentPosition = matchPosition;
		Window window = reader.getWindow(currentPosition);
		State<T> state = automata.getInitialState();
		Collection<MatchResult<T>> results = Collections.emptyList();
		// While we have a window on the data to match in:
		while (window != null) {
			final byte[] bytes = window.getArray();
			final int windowLength = window.length();
			final int windowStart = reader.getWindowOffset(currentPosition);
			int windowPos = windowStart;

			// While we have states to match:
			while (state != null && windowPos < windowLength) {

				// See if the active states is final (a match).
				if (state.isFinal()) {
					if (results.isEmpty()) {
						results = new ArrayList<MatchResult<T>>();
					}
					final long matchLength = currentPosition - matchPosition + windowPos
							- windowStart;
					results.add(new DfaMatchResult<T>(matchPosition, matchLength, null, state));
				}

				// No match was found, find the next state to follow:
				final byte currentByte = bytes[windowPos++];
				state = state.getNextState(currentByte);
			}
			currentPosition += windowLength - windowStart;
			window = reader.getWindow(currentPosition);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MatchResult<T> firstMatch(final byte[] bytes, final int matchPosition) {
		// Setup
		final int length = bytes.length;
		if (matchPosition >= 0 && matchPosition < length) {
			int currentPosition = matchPosition;
			State<T> currentState = automata.getInitialState();

			// While there is a state to process:
			while (currentState != null && currentPosition < length) {

				// See if the next state is final (a match).
				if (currentState.isFinal()) {
					final long matchLength = currentPosition - matchPosition;
					return new DfaMatchResult<T>(matchPosition, matchLength, null, currentState);
				}

				// No match was found, find the next state to follow:
				final byte currentByte = bytes[currentPosition++];
				currentState = currentState.getNextState(currentByte);
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MatchResult<T> nextMatch(final byte[] bytes, final MatchResult<T> lastMatch) {
		if (lastMatch instanceof DfaMatchResult) {
			// Setup
			final int length = bytes.length;
			final int matchPosition = (int) lastMatch.getMatchPosition();
			final int startPosition = (int) (matchPosition + lastMatch.getMatchLength());
			if (startPosition >= 0 && startPosition < length) {
				int currentPosition = startPosition;
				State<T> currentState = ((DfaMatchResult<T>) lastMatch).getMatchingState();

				// While there is a state to process:
				while (currentState != null && currentPosition < length) {

					// Find the next state to follow:
					final byte currentByte = bytes[currentPosition++];
					currentState = currentState.getNextState(currentByte);

					// See if the next state is final (a match).
					if (currentState != null && currentState.isFinal()) {
						final long matchLength = currentPosition - matchPosition;
						return new DfaMatchResult<T>(matchPosition, matchLength, null, currentState);
					}
				}
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<MatchResult<T>> allMatches(final byte[] bytes, final int matchPosition) {
		// Setup
		final int length = bytes.length;
		Collection<MatchResult<T>> results = Collections.emptyList();
		if (matchPosition >= 0 && matchPosition < length) {
			int currentPosition = matchPosition;
			State<T> currentState = automata.getInitialState();

			// While there is a state to process:
			while (currentState != null && currentPosition < length) {

				// See if the next state is final (a match).
				if (currentState.isFinal()) {
					if (results.isEmpty()) {
						results = new ArrayList<MatchResult<T>>();
					}
					final long matchLength = currentPosition - matchPosition;
					results.add(new DfaMatchResult<T>(matchPosition, matchLength, null,
							currentState));
				}

				// No match was found, find the next state to follow:
				final byte currentByte = bytes[currentPosition++];
				currentState = currentState.getNextState(currentByte);
			}
		}
		return results;
	}

	/**
	 * A simple, private extension of the MatchResult class which privately stashes away the
	 * last State in the automata that matched.  This enables the nextMatch() methods
	 * to carry on from where they left off, but doesn't allow external classes to access
	 * the private State.
	 * 
	 * @author Matt Palmer
	 *
	 * @param <T> The type of object associated with States in the Automata.
	 */
	private static final class DfaMatchResult<T> extends MatchResult<T> {

		private State<T>		matchingState;
		private Collection<T>	associations;

		public DfaMatchResult(final long matchPosition, final long matchLength,
				final Collection<T> matchingObjects, final State<T> matchingState) {
			super(matchPosition, matchLength, matchingObjects);
			this.matchingState = matchingState;
		}

		@Override
		public Collection<T> getMatchingObjects() {
			if (associations == null) {
				associations = matchingState.getAssociations();
			}
			return associations;
		}

		private State<T> getMatchingState() {
			return matchingState;
		}

	}

}
