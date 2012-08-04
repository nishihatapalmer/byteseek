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

package net.domesdaybook.automata.trie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.domesdaybook.automata.State;
import net.domesdaybook.automata.StateFactory;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.automata.TransitionFactory;
import net.domesdaybook.automata.base.BaseAutomata;
import net.domesdaybook.automata.base.BaseStateFactory;
import net.domesdaybook.automata.base.ByteSetMatcherTransitionFactory;
import net.domesdaybook.util.bytes.ByteUtilities;

/**
 * An abstract implementation of {@link Trie} providing most methods for
 * constructing a Trie from sequences provided to it.  It extends {@link net.domesdaybook.automata.base.BaseAutomata}.
 * <p>
 * Implementors only have to provide constructors equivalent to those in this class,
 * and override the methods {@link #getSequenceLength(java.lang.Object)} and 
 * {@link #getBytesForPosition(java.lang.Object, int)}.
 * 
 * @param <T> T The type of sequence to add to the Trie.
 * 
 * @author Matt Palmer
 */
public abstract class AbstractTrie<T> extends BaseAutomata<T> implements Trie<T> {

	private final StateFactory<T>		stateFactory;
	private final TransitionFactory<T, Collection<Byte>>	transitionFactory;

	private final List<T>				sequences;

	private int							minimumLength	= -1;
	private int							maximumLength	= 0;

	/**
	 * Constructs a Trie using the default {@link net.domesdaybook.automata.StateFactory}
	 * , {@link net.domesdaybook.automata.base.BaseStateFactory}, and the default
	 * {@link net.domesdaybook.automata.TransitionFactory}, {@link net.domesdaybook.automata.base.ByteSetMatcherTransitionFactory}.
	 */
	public AbstractTrie() {
		this(null, null);
	}

	/**
	 * Constructs a Trie using the supplied {@link net.domesdaybook.automata.StateFactory}
	 * and the default {@link net.domesdaybook.automata.TransitionFactory}, {@link net.domesdaybook.automata.base.ByteSetMatcherTransitionFactory}.
	 * 
	 * @param stateFactory The StateFactory to use to create States for the Trie.
	 */
	public AbstractTrie(final StateFactory<T> stateFactory) {
		this(stateFactory, null);
	}

	/**
	 * Constructs a Trie using the default {@link net.domesdaybook.automata.StateFactory}
	 * , {@link net.domesdaybook.automata.base.BaseStateFactory}, and the supplied
	 * {@link net.domesdaybook.automata.TransitionFactory}.
	 * 
	 * @param transitionFactory The TransitionFactory to use to create Transitions for the Trie.
	 */
	public AbstractTrie(final TransitionFactory<T, Collection<Byte>> transitionFactory) {
		this(null, transitionFactory);
	}

	/**
	 * Constructs a Trie using the supplied {@link net.domesdaybook.automata.StateFactory}
	 * and {@link net.domesdaybook.automata.TransitionFactory}.
	 * 
	 * @param stateFactory The StateFactory to use to create States for the Trie.
	 * @param transitionFactory The TransitionFactory to use to create Transitions for the Trie.
	 */
	public AbstractTrie(final StateFactory<T> stateFactory,
			final TransitionFactory<T, Collection<Byte>> transitionFactory) {
		this.stateFactory = stateFactory != null ? stateFactory : new BaseStateFactory<T>();
		this.transitionFactory = transitionFactory != null ? transitionFactory
				: new ByteSetMatcherTransitionFactory<T>();
		this.sequences = new ArrayList<T>();
		setInitialState(this.stateFactory.create(State.NON_FINAL));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDeterministic() {
		return true; // A Trie is always deterministic.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMinimumLength() {
		return minimumLength == -1 ? 0 : minimumLength;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMaximumLength() {
		return maximumLength;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<T> getSequences() {
		return new ArrayList<T>(sequences);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final T sequence) {
		List<State<T>> currentStates = new ArrayList<State<T>>();
		currentStates.add(initialState);
		final int length = getSequenceLength(sequence);
		for (int position = 0; position < length; position++) {
			final byte[] matchingBytes = getBytesForPosition(sequence, position);
			final boolean isFinal = position == length - 1;
			currentStates = nextStates(currentStates, matchingBytes, isFinal);
		}
		for (final State<T> finalState : currentStates) {
			finalState.addAssociation(sequence);
		}
		setMinMaxLength(length);
		sequences.add(sequence);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addAll(final Collection<? extends T> sequencesToAdd) {
		for (final T sequence : sequencesToAdd) {
			add(sequence);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addReversed(final T sequence) {
		List<State<T>> currentStates = new ArrayList<State<T>>();
		currentStates.add(initialState);
		final int length = getSequenceLength(sequence);
		for (int position = length - 1; position >= 0; position--) {
			final byte[] matchingBytes = getBytesForPosition(sequence, position);
			final boolean isFinal = position == 0;
			currentStates = nextStates(currentStates, matchingBytes, isFinal);
		}
		for (final State<T> finalState : currentStates) {
			finalState.addAssociation(sequence);
		}
		setMinMaxLength(length);
		sequences.add(sequence);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addAllReversed(final Collection<? extends T> sequencesToAdd) {
		for (final T sequence : sequencesToAdd) {
			addReversed(sequence);
		}
	}

	/**
	 * Returns the length of a sequence of type T.
	 * 
	 * @param sequence The sequence to return the length of.
	 * @return int the length of the sequence.
	 */
	protected abstract int getSequenceLength(T sequence);

	/**
	 * Returns an array of bytes which represent the possible bytes in the
	 * sequence of type T at a given position.
	 * 
	 * @param sequence The sequence of type T.
	 * @param position The position in the sequence.
	 * @return byte[] an array of bytes which exist in the sequence at the position.
	 */
	protected abstract byte[] getBytesForPosition(T sequence, int position);

	private void setMinMaxLength(final int length) {
		if (length > maximumLength) {
			maximumLength = length;
		}
		if (length < minimumLength || minimumLength == -1) {
			minimumLength = length;
		}
	}

	/**
	 * Returns the next states which must be processed when adding an array of
	 * bytes to the Trie to the list of states passed in.
	 * 
	 * @param currentStates The currently active states in the Trie.
	 * @param bytes The byte values which must be added to each of those states.
	 * @param isFinal Whether the new states must be final.
	 * @return A list of states forming the next states to be processed in the Trie.
	 */
	private List<State<T>> nextStates(final List<State<T>> currentStates, final byte[] bytes,
			final boolean isFinal) {
		final List<State<T>> nextStates = new ArrayList<State<T>>();
		final Set<Byte> allBytesToTransitionOn = ByteUtilities.toSet(bytes);
		for (final State<T> currentState : currentStates) {
			final List<Transition<T>> stateTransitions = new ArrayList<Transition<T>>(currentState.getTransitions());
			final Set<Byte> bytesToTransitionOn = new HashSet<Byte>(allBytesToTransitionOn);
			for (final Transition<T> transition : stateTransitions) {

				final Set<Byte> originalTransitionBytes = ByteUtilities
						.toSet(transition.getBytes());
				final int originalTransitionBytesSize = originalTransitionBytes.size();
				final Set<Byte> bytesInCommon = ByteUtilities.subtract(originalTransitionBytes,
						bytesToTransitionOn);

				// If the existing transition is the same or a subset of the new
				// transition bytes:
				final int numberOfBytesInCommon = bytesInCommon.size();
				if (numberOfBytesInCommon == originalTransitionBytesSize) {

					final State<T> toState = transition.getToState();

					// Ensure that the state is final if necessary:
					if (isFinal) {
						toState.setIsFinal(true);
					}

					// Add this state to the states we have to process next.
					nextStates.add(toState);

				} else if (numberOfBytesInCommon > 0) {
					// Only some bytes are in common.
					// We will have to split the existing transition to
					// two states, and recreate the transitions to them:
					final State<T> originalToState = transition.getToState();
					if (isFinal) {
						originalToState.setIsFinal(true);
					}
					final State<T> newToState = originalToState.deepCopy();

					// Add a transition to the bytes which are not in common:
					final Transition<T> bytesNotInCommonTransition = transitionFactory
							.create(originalTransitionBytes, false, originalToState);
					currentState.addTransition(bytesNotInCommonTransition);

					// Add a transition to the bytes in common:
					final Transition<T> bytesInCommonTransition = transitionFactory
							.create(bytesInCommon, false, newToState);
					currentState.addTransition(bytesInCommonTransition);

					// Add the bytes in common state to the next states to
					// process:
					nextStates.add(newToState);

					// Remove the original transition from the current state:
					currentState.removeTransition(transition);
				}

				// If we have no further bytes to process, just break out.
				final int numberOfBytesLeft = bytesToTransitionOn.size();
				if (numberOfBytesLeft == 0) {
					break;
				}
			}

			// If there are any bytes left over, create a transition to a new
			// state:
			final int numberOfBytesLeft = bytesToTransitionOn.size();
			if (numberOfBytesLeft > 0) {
				final State<T> newState = stateFactory.create(isFinal);
				final Transition<T> newTransition = transitionFactory.create(
						bytesToTransitionOn, false, newState);
				currentState.addTransition(newTransition);
				nextStates.add(newState);
			}
		}

		return nextStates;
	}

}
