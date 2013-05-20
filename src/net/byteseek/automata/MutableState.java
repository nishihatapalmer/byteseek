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

package net.byteseek.automata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import net.byteseek.util.collections.IdentityHashSet;
import net.byteseek.util.object.DeepCopy;

/**
 * An implementation of the {@link State} interface.
 * Transitions are managed internally as a list of Transitions, as are any 
 * associations with the state.
 * <p>
 * Note that this implementation of State relies on the fact that Transitions are
 * immutable, to make determining the deterministic state efficient.  If transitions
 * which are not deterministic are used, then either whenever the transition is modified,
 * the State it belongs to must have its deterministic status invalidated by calling
 * {@ink #invalidateDeterministicStatus()}, or by sub-classing this State and overriding
 * the {@link #isDeterministic} method.
 * <p>
 * It is intentionally not a final class, allowing other States to inherit from 
 * this implementation.
 * 
 * @param <T> The type of object which can be associated with this state.
 * 
 * @see net.byteseek.automata.State
 * @see net.byteseek.automata.Transition
 * @author Matt Palmer
 */
public class MutableState<T> implements State<T> {

	private List<Transition<T>>	transitions;
	private List<T>				associations;
	private boolean				isFinal;
	private Boolean				isDeterministic;

	//////////////////
	// Constructors //
	/////////////////

	/**
	 * The default constructor for MutableState, as a non-final state.
	 */
	public MutableState() {
		this(State.NON_FINAL);
	}

	/**
	 * A constructor for MutableState taking a parameter determining whether the
	 * state is final or not.
	 * 
	 * @param isFinal Whether the state is final or not.
	 */
	public MutableState(final boolean isFinal) {
		this.isFinal = isFinal;
		this.transitions = Collections.emptyList();
		this.associations = Collections.emptyList(); // = new ArrayList<T>(0);
	}

	/**
	 * A copy constructor for MutableState from another state.
	 * 
	 * @param other The other State to copy from.
	 * @throws IllegalArgumentException if the State passed in is null.
	 */
	public MutableState(final State<T> other) {
		if (other == null) {
			throw new IllegalArgumentException(
					"Other state passed in to copy constructor was null.");
		}
		this.isFinal = other.isFinal();
		final List<Transition<T>> otherTransitions = other.getTransitions();
		if (otherTransitions != null && otherTransitions.size() > 0) {
			this.transitions = new ArrayList<Transition<T>>(otherTransitions);
		} else {
			this.transitions = Collections.emptyList();
		}
		final Collection<T> otherAssoc = other.getAssociations();
		if (otherAssoc != null) {
			this.associations = new ArrayList<T>(otherAssoc);
		} else {
			this.associations = Collections.emptyList();
		}
	}

	/////////////
	// Methods //
	/////////////

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Iterator<Transition<T>> iterator() {
		return new TransitionIterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final List<Transition<T>> getTransitions() {
		if (transitions.isEmpty()) {
			return transitions;
		}
		return new ArrayList<Transition<T>>(transitions);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void addTransition(final Transition<T> transition) {
		if (transitions.isEmpty()) {
			transitions = new ArrayList<Transition<T>>(1);
		}
		transitions.add(transition);
		isDeterministic = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void addAllTransitions(final List<Transition<T>> transitionList) {
		if (transitions.isEmpty()) {
			this.transitions = new ArrayList<Transition<T>>(transitionList.size());
		}
		transitions.addAll(transitionList);
		isDeterministic = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void addAllTransitions(final Iterator<Transition<T>> transitionIterator) {
		if (transitions.isEmpty()) {
			// Guess a fairly small size for the list given most states don't have many transitions.
			this.transitions = new ArrayList<Transition<T>>(3);
		}
		while (transitionIterator.hasNext()) {
			transitions.add(transitionIterator.next());
		}
		isDeterministic = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean removeTransition(final Transition<T> transition) {
		if (!transitions.isEmpty()) {
			boolean wasRemoved = transitions.remove(transition);
			if (transitions.isEmpty()) {
				transitions = Collections.emptyList();
			}
			isDeterministic = null;
			return wasRemoved;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean replaceTransition(final Transition<T> oldTransition,
			final Transition<T> newTransition) {
		if (!transitions.isEmpty()) {
			boolean wasRemoved = transitions.remove(oldTransition);
			if (wasRemoved) {
				transitions.add(newTransition);
				isDeterministic = null;
				return true;
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearTransitions() {
		transitions = Collections.emptyList();
		isDeterministic = null;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public final void appendNextStates(final Collection<State<T>> states, final byte value) {
		final Set<State<T>> matchingStates = new IdentityHashSet<State<T>>();
		for (final Transition<T> transition : transitions) {
			final State<T> nextState = transition.getStateForByte(value);
			if (nextState != null && !matchingStates.contains(nextState)) {
				matchingStates.add(nextState);
				states.add(nextState);
			}
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public final State<T> getNextState(final byte value) {
		for (final Transition<T> transition : transitions) {
			final State<T> nextState = transition.getStateForByte(value);
			if (nextState != null) {
				return nextState;
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isFinal() {
		return isFinal;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDeterministic() {
		if (transitions.size() > 1) {
			if (isDeterministic == null) {
				final Map<Byte, State<T>> bytesToStates = new HashMap<Byte, State<T>>(128);
				for (Transition<T> transition : transitions) {
					final byte[] matchingBytes = transition.getBytes();
					final State<T> toState = transition.getToState();
					for (byte b : matchingBytes) {
						final State<T> existingState = bytesToStates.get(b);
						if (existingState != toState) {
							if (existingState != null) {
								isDeterministic = Boolean.FALSE;
								return false;
							}
							bytesToStates.put(b, toState);
						}
					}
				}
				isDeterministic = Boolean.TRUE;
			}
			return isDeterministic;
		}
		return true;
	}

	/**
	 * Calling this method invalidates the deterministic status of this State,
	 * causing it to be re-calculated if {@link #isDeterministic} is called.
	 */
	public final void invalidateDeterministicStatus() {
		isDeterministic = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setIsFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<T> getAssociations() {
		if (associations.isEmpty()) {
			return associations;
		}
		return new ArrayList<T>(associations);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<T> associationIterator() {
		return associations.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addAssociation(final T association) {
		if (associations.isEmpty()) {
			associations = new ArrayList<T>(1);
		}
		associations.add(association);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addAllAssociations(Collection<? extends T> associationsToAdd) {
		if (associations.isEmpty()) {
			associations = new ArrayList<T>(associationsToAdd.size());
		}
		this.associations.addAll(associationsToAdd);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addAllAssociations(Iterator<T> associationIterator) {
		if (associations.isEmpty()) {
			associations = new ArrayList<T>(2);
		}
		while (associationIterator.hasNext()) {
			associations.add(associationIterator.next());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeAssociation(final Object association) {
		final boolean wasRemoved = associations.remove(association);
		if (associations.isEmpty()) {
			associations = Collections.emptyList();
		}
		return wasRemoved;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAssociations(final Collection<? extends T> associations) {
		if (this.associations.isEmpty()) {
			this.associations = new ArrayList<T>(associations.size());
		}
		this.associations.addAll(associations);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearAssociations() {
		associations = Collections.emptyList();
	}

	/**
	 * This is a convenience method, providing the initial map to:
	 * <CODE>deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects)</CODE>
	 *
	 * @return MutableState a deep copy of this object.
	 * @see #deepCopy(java.util.Map) 
	 */
	@Override
	public MutableState<T> deepCopy() {
		return deepCopy(new IdentityHashMap<DeepCopy, DeepCopy>());
	}

	/**
	 * This method is inherited from the {@link DeepCopy} interface,
	 * and is redeclared here with a return type of MutableState (rather than DeepCopy),
	 * to make using the method easier.
	 *
	 * @param oldToNewObjects A map of the original objects to their new deep copies.
	 * @return MutableState A deep copy of this MutableState and any Transitions and States
	 *         reachable from this State.
	 */
	@Override
	public MutableState<T> deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects) {
		@SuppressWarnings("unchecked")
		// if there is a copy of this in the map, it will be of the same type.
		MutableState<T> stateCopy = (MutableState<T>) oldToNewObjects.get(this);
		if (stateCopy == null) {
			stateCopy = new MutableState<T>(this.isFinal);
			oldToNewObjects.put(this, stateCopy);
			for (Transition<T> transition : transitions) {
				final Transition<T> transitionCopy = transition.deepCopy(oldToNewObjects);
				stateCopy.transitions.add(transitionCopy);
			}
		}
		return stateCopy;
	}

	/**
	 * An iterator over the transitions that ensures that if {@link #remove()} is called,
	 * then the deterministic flag in State is properly updated, by calling its instance
	 * method to remove the Transition.
	 * 
	 * @author Matt Palmer
	 */
	private final class TransitionIterator implements Iterator<Transition<T>> {

		private int		index;
		private boolean	removed;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			return index < transitions.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Transition<T> next() {
			removed = false;
			if (hasNext()) {
				return transitions.get(index++);
			}
			throw new NoSuchElementException("There are no more transitions in the state.");
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			int elementToRemove = index - 1;
			if (elementToRemove >= 0 && !removed) {
				removeTransition(transitions.get(elementToRemove));
				removed = true;
			}
			throw new IllegalStateException(
					"Next has not been called or remove has already been called.");
		}

	}

}
