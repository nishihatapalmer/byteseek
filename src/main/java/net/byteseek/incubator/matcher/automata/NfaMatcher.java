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

package net.byteseek.incubator.matcher.automata;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.byteseek.incubator.automata.Automata;
import net.byteseek.incubator.automata.State;
import net.byteseek.io.reader.windows.Window;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.MatchResult;

/**
 * 
 * @author Matt Palmer
 */
public final class NfaMatcher implements AutomataMatcher {

	private final Automata	automata;

	/**
	 * Constructs an NfaMatcher from an automata.
	 *
	 * @param automata The automata to match.
	 */
	public NfaMatcher(final Automata automata) {
		this.automata = automata;
	}

	@Override
	public int matches(WindowReader reader, long matchPosition, List<MatchResult> results) throws IOException {
		return 0; //TODO: implement.
	}

	/**
	 * Matches the automata at the position given in the WindowReader
	 * @param reader The reader to match in.
	 * @param matchPosition The position to match at.
	 * @return true if the matcher matches at that position.
	 * @throws IOException If there is an IO problem when reading.
	 */
	@Override
	public boolean matches(final WindowReader reader, final long matchPosition) throws IOException {
		// Setup 
		long currentPosition = matchPosition;
		Set<State> nextStates = new LinkedHashSet<State>();
		Set<State> activeStates = new LinkedHashSet<State>();
		activeStates.add(automata.getInitialState());
		Window window = reader.getWindow(currentPosition);

		//While we have a window on the data to match in:
		while (window != null) {
			final byte[] bytes = window.getArray();
			final int windowLength = window.length();
			final int windowStart = reader.getWindowOffset(currentPosition);
			int windowPos = windowStart;

			// While we have states to match:
			while (!activeStates.isEmpty() && windowPos < windowLength) {

				// See if any active states are final (a match).
				for (final State currentState : activeStates) {
					if (currentState.isFinal()) {
						return true;
					}
				}

				// No match was found, find the next distinct states to follow:
				final byte currentByte = bytes[windowPos++];
				for (final State currentState : activeStates) {
					currentState.appendNextStates(nextStates, currentByte);
				}

				// Make the next states active.  The last active set is 
				// re-used for the next states to be processed.
				final Set<State> lastActiveSet = activeStates;
				activeStates = nextStates;
				nextStates = lastActiveSet;
				nextStates.clear();
			}
			currentPosition += windowLength - windowStart;
			window = reader.getWindow(currentPosition);
		}
		return false;
	}

	@Override
	public int matches(byte[] bytes, int matchPosition, List<MatchResult> results) {
		return 0; //TODO: implement.
	}

	/**
	 * Matches the automata at the position given in the byte array.
	 *
	 * @param bytes The byte array to match in.
	 * @param matchPosition The position to match at.
	 *
	 * @return true if the automata matches at the position given in the byte array.
	 */
	@Override
	public boolean matches(final byte[] bytes, final int matchPosition) {
		// Setup
		final int length = bytes.length;
		if (matchPosition >= 0 && matchPosition < length) {
			int currentPosition = matchPosition;
			Set<State> nextStates = new LinkedHashSet<State>();
			Set<State> activeStates = new LinkedHashSet<State>();
			activeStates.add(automata.getInitialState());

			// Match automata:
			while (!activeStates.isEmpty() && currentPosition < length) {

				// See if any active states are final (a match).
				for (final State currentState : activeStates) {
					if (currentState.isFinal()) {
						return true;
					}
				}

				// No match was found, find the next distinct states to follow:
				final byte currentByte = bytes[currentPosition++];
				for (final State currentState : activeStates) {
					currentState.appendNextStates(nextStates, currentByte);
				}

				// Make the next states active.  The last active set is cleared 
				// and re-used for the next states.
				final Set<State> lastActiveSet = activeStates;
				activeStates = nextStates;
				nextStates = lastActiveSet;
				nextStates.clear();
			}
		}
		return false;
	}

	@Override
	public MatchResult firstMatch(WindowReader reader, long matchPosition) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MatchResult nextMatch(WindowReader reader, MatchResult lastMatch) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<MatchResult> allMatches(WindowReader reader, long matchPosition)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MatchResult firstMatch(byte[] bytes, int matchPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MatchResult nextMatch(byte[] bytes, MatchResult lastMatch) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<MatchResult> allMatches(byte[] bytes, int matchPosition) {
		// TODO Auto-generated method stub
		return null;
	}
	
    @Override
    public String toString() {
    	return getClass().getSimpleName() + "[automata:" + automata + ']'; 
    }

}
