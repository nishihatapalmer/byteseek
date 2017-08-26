/*
 * Copyright Matt Palmer 2009-2013, All rights reserved.
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

package net.byteseek.incubator.matcher.automata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.byteseek.incubator.automata.Automata;
import net.byteseek.incubator.automata.State;
import net.byteseek.io.reader.windows.Window;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.MatchResult;


/**
 * 
 * @author Matt Palmer
 */
public class DfaMatcher<T> implements AutomataMatcher {

	private final Automata<T>	automata;

	/**
	 * Constructs a matcher for deterministic finite state automata.
	 *
	 * @param automata The deterministic automata to match
	 */
	public DfaMatcher(final Automata<T> automata) {
		this.automata = automata;
	}

	@Override
	public int matches(WindowReader reader, long matchPosition, Collection<MatchResult> results) throws IOException {
		return 0; //TODO: implement.
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

	@Override
	public int matches(byte[] bytes, int matchPosition, Collection<MatchResult> results) {
		return 0; //TODO: implement.
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
	public MatchResult firstMatch(final WindowReader reader, final long matchPosition)
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
					return new DfaMatchResult(matchPosition, matchLength, state);
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
	public MatchResult nextMatch(final WindowReader reader, final MatchResult lastMatch)
			throws IOException {
		if (lastMatch instanceof DfaMatchResult) {
			final long matchPosition = lastMatch.getMatchPosition();
			final long startPosition = matchPosition + lastMatch.getMatchLength();
			long currentPosition = startPosition;
			Window window = reader.getWindow(currentPosition);
			State<T> state = ((DfaMatchResult) lastMatch).getMatchingState();
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
						return new DfaMatchResult(matchPosition, matchLength, state);
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
	public Collection<MatchResult> allMatches(final WindowReader reader, final long matchPosition)
			throws IOException {
		// Setup
		long currentPosition = matchPosition;
		Window window = reader.getWindow(currentPosition);
		State<T> state = automata.getInitialState();
		Collection<MatchResult> results = Collections.emptyList();
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
						results = new ArrayList<MatchResult>();
					}
					final long matchLength = currentPosition - matchPosition + windowPos
							- windowStart;
					results.add(new DfaMatchResult(matchPosition, matchLength, state));
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
	public MatchResult firstMatch(final byte[] bytes, final int matchPosition) {
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
					return new DfaMatchResult(matchPosition, matchLength, currentState);
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
	public MatchResult nextMatch(final byte[] bytes, final MatchResult lastMatch) {
		if (lastMatch instanceof DfaMatchResult) {
			// Setup
			final int length = bytes.length;
			final int matchPosition = (int) lastMatch.getMatchPosition();
			final int startPosition = (int) (matchPosition + lastMatch.getMatchLength());
			if (startPosition >= 0 && startPosition < length) {
				int currentPosition = startPosition;
				State<T> currentState = ((DfaMatchResult) lastMatch).getMatchingState();

				// While there is a state to process:
				while (currentState != null && currentPosition < length) {

					// Find the next state to follow:
					final byte currentByte = bytes[currentPosition++];
					currentState = currentState.getNextState(currentByte);

					// See if the next state is final (a match).
					if (currentState != null && currentState.isFinal()) {
						final long matchLength = currentPosition - matchPosition;
						return new DfaMatchResult(matchPosition, matchLength, currentState);
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
	public Collection<MatchResult> allMatches(final byte[] bytes, final int matchPosition) {
		// Setup
		final int length = bytes.length;
		Collection<MatchResult> results = Collections.emptyList();
		if (matchPosition >= 0 && matchPosition < length) {
			int currentPosition = matchPosition;
			State<T> currentState = automata.getInitialState();

			// While there is a state to process:
			while (currentState != null && currentPosition < length) {

				// See if the next state is final (a match).
				if (currentState.isFinal()) {
					if (results.isEmpty()) {
						results = new ArrayList<MatchResult>();
					}
					final long matchLength = currentPosition - matchPosition;
					results.add(new DfaMatchResult(matchPosition, matchLength, currentState));
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
	private static final class DfaMatchResult<T> extends MatchResult {


		private State<T>			matchingState;
		public DfaMatchResult(final long matchPosition, 
				              final long matchLength,
				              final State<T> matchingState) {
			super(matchPosition, matchLength);
			this.matchingState = matchingState;
		}

		//public Collection<T> getMatchingObjects() {
		//	return matchingState.getAssociations();
		//}
		
		private State<T> getMatchingState() {
			return matchingState;
		}

	}
	
    @Override
    public String toString() {
    	return getClass().getSimpleName() + "[automata:" + automata + ']'; 
    }

}
