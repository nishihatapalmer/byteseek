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

package net.domesdaybook.automata.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.domesdaybook.automata.Automata;
import net.domesdaybook.automata.MutableAutomata;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.automata.factory.MutableStateFactory;
import net.domesdaybook.automata.factory.StateFactory;
import net.domesdaybook.automata.factory.TransitionFactory;

/**
 * This class helps to build an automata as it is invoked for each node of a regular 
 * expression parse-tree. It builds a particular kind of automata as it goes,
 * called a Glushkov automata.  
 * <p>
 * A Glushkov Non-deterministic Finite-state Automata (NFA) is an automata which
 * is constructed to have one initial state, and additionally, a state for each
 * position in the regular expression that defines a byte or set of bytes.
 * <p>
 * Each position has transitions to every other reachable position,
 * on the bytes which need to match to get to those positions.
 * <p>
 * Being Non-Deterministic means that from any given state,
 * there can be transitions to several states on the same byte value.
 * (by contrast, Deterministic Finite-state Automata (DFAs) have at most one
 * state they can transition to on a given byte value.)
 * <p>
 * Unlike the classic Thompson construction (the most common regular expression NFA)
 * Glushkov automata have no "empty" transitions - that is, transitions to another
 * state without reading a byte. The Thompson NFA uses empty transitions as a sort of 
 * permanent scaffolding to simplify constructing the automata, by wiring up states 
 * together as the need arises during construction.  
 * <p>
 * Having no empty transitions makes the automata smaller, and easier to transform
 * it further (e.g. building a DFA from it) but makes constructing it a little more
 * difficult in the first place.
 * <p>
 * The method of constructing a Glushkov automata used in this class is due to 
 * Champarnaud.  Details of the construction can be found in the paper:
 * <p>
 *   "A reexamination of the Glushkov and Thompson Constructions"
 *    by Dora Giammarresi, Jean-Luc Ponty, Derick Wood, 1998.
 *
 * @param <T> The type of the object associated with a State in the NFA.
 * @param <S> The type of object used to build a transition from.
 * 
 * @author Matt Palmer
 */
public final class GlushkovRegexBuilder<T, S> implements RegexBuilder<T, S> {

	private final TransitionFactory<T, S>	transitionFactory;
	private final StateFactory<T>		stateFactory;

	/**
	 * Constructs a GlushkovRegexBuilder using the default {@link net.domesdaybook.automata.factory.StateFactory},
	 * {@link net.domesdaybook.automata.factory.MutableStateFactory}, and the supplied 
	 * default {@link net.domesdaybook.automata.factory.TransitionFactory}.
	 * 
	 * @param transitionFactory The TransitionFactory to use when creating the NFA.
	 * @throws IllegalArgumentException if the transition factory passed in is null.
	 */
	public GlushkovRegexBuilder(final TransitionFactory<T, S> transitionFactory) {
		this(transitionFactory, null);
	}

	/**
	 * Constructs a GlushkovRegexBuilder using the supplied
	 * {@link net.domesdaybook.automata.factory.StateFactory},
	 * and the supplied 
	 * default {@link net.domesdaybook.automata.factory.TransitionFactory}.
	 * 
	 * @param transitionFactory The TransitionFactory to use when creating the NFA.
	 * @param stateFactory The StateFactory to use when creating the NFA.
	 * @throws IllegalArgumentException if the transition factory passed in is null.
	 */
	public GlushkovRegexBuilder(final TransitionFactory<T, S> transitionFactory,
			final StateFactory<T> stateFactory) {
		if (transitionFactory == null) {
			throw new IllegalArgumentException("Transition factory cannot be null.");
		}
		this.transitionFactory = transitionFactory;
		if (stateFactory == null) {
			this.stateFactory = new MutableStateFactory<T>();
		} else {
			this.stateFactory = stateFactory;
		}
	}

	/**
	 * Builds an (initial) state and a [final] state,
	 * and joins them together with a transition on the transition bytes
	 * defined by the source type.
	 *
	 * <p/><code>
	 *   b      (0) --b--> [1]
	 * </code><p/>
	 *
	 * @param transitionByte The byte to transition on.
	 * @param inverted Whether to match all other bytes, or just the one passed in.
	 * @return An automata with a transition on the byte supplied.
	 */
	@Override
	public Automata<T> buildTransitionAutomata(final S source, final boolean inverted) {
		final State<T> initialState = stateFactory.create(State.NON_FINAL);
		final State<T> finalState = stateFactory.create(State.FINAL);
		final Transition<T> transition =
		    transitionFactory.create(source, inverted, finalState);
		initialState.addTransition(transition);
		return new MutableAutomata<T>(initialState);
	}

	/**
	 * Builds a single automata out of a list of automata,
	 * by turning them into a sequence from the start to the end of the list.
	 *
	 * <p/><pre>{@code
	 *   XYZ    (0) ----> (1) ----> (2) ----> [3]
	 *                X         Y         Z
	 * }</pre><p/>
	 * 
	 * To create an automata joining a sequence of automata together,
	 * we must transition each final state on the left to its neighbour states on the right.
	 *<p>
	 * We can achieve this by copying all of the transitions of the right hand initial
	 * state back to the final states of the automata on the left hand side.
	 * Each final state effectively becomes an initial state of the
	 * automata to its right. The original initial state of the right hand automata
	 * is effectively "cut out of the loop" and does not form part of the sequence.
	 *<p>
	 * The left hand side final states all become as final as the initial state
	 * they end up bypassing - or as non-final as that state.
	 *
	 * @param automataSequence A list of automata to be joined as a sequence.
	 * @return An automata which is a sequence of the automata in the list.
	 */
	@Override
	public Automata<T> buildSequenceAutomata(final List<Automata<T>> automataSequence) {
		// Iterate across pairs of automata, starting with 0 and 1, then 1 and
		// 2, etc,
		// adding the transitions of the initial state of the right hand
		// automata
		// to the final states of the left hand automata.
		final List<State<T>> finalStates = new ArrayList<State<T>>();
		for (int automataIndex = 1, stop = automataSequence.size(); automataIndex < stop; automataIndex++) {

			// Get the final states of the left hand side.
			final Automata<T> leftAutomata = automataSequence.get(automataIndex - 1);
			finalStates.addAll(leftAutomata.getFinalStates());

			// Get info about the right hand side initial state:
			final Automata<T> rightAutomata = automataSequence.get(automataIndex);
			final State<T> rightInitialState = rightAutomata.getInitialState();
			final boolean rightInitialStateIsFinal = rightInitialState.isFinal();

			// For each final state we currently have:
			// (1) add the transitions from the initial state of the right hand
			// side,
			// (2) set whether it is final or not based on the initial right
			// hand state.
			for (final State<T> leftFinalState : finalStates) {
				leftFinalState.addAllTransitions(rightInitialState.iterator());
				leftFinalState.setIsFinal(rightInitialStateIsFinal);
			}

			// If the right initial state was not final, then clear our list of
			// current final states. However, if the right hand *initial* state
			// was
			// final, then we carry over the current list of final states to the
			// next pair of automata to be processed.
			// This is because an automata with an *initial* final state is
			// optional,
			// meaning it can be skipped, so we have to add the transitions of
			// the
			// *next* right hand side initial state to the current set of final
			// states too (adding transitions which can skip over the current
			// optional automata).
			if (!rightInitialStateIsFinal) {
				finalStates.clear();
			}
		}
		return automataSequence.get(0);
	}

	/**
	 * To create an automata of alternative sequences. identify the initial state of
	 * each of the alternatives with each other.  In practice, retain one state and
	 * add the transitions of the other initial states to it.
	 *
	 * <p/><pre>{@code
	 * 
	 * X|Y|Z    (0) ----> [1]
	 *           |    X
	 *            ------> [2]
	 *           |    Y
	 *            ------> [3]
	 *                Z
	 * }</pre><p/>
	 *
	 * The initial state is final if any of the alternate initial states were final.
	 *
	 * @param alternateAutomata A list of alternative automata, wrapped in an object giving the initial and final states of the automata.
	 * @return An object holding the initial and final states of the alternative automata.
	 */
	@Override
	public Automata<T> buildAlternativesAutomata(final List<Automata<T>> alternateAutomata) {
		final Automata<T> initialAutomata = alternateAutomata.get(0);
		final State<T> initialState = initialAutomata.getInitialState();
		boolean isFinal = initialState.isFinal();
		for (int automataIndex = 1, stop = alternateAutomata.size(); automataIndex < stop; automataIndex++) {
			final Automata<T> nextAutomata = alternateAutomata.get(automataIndex);
			final State<T> nextInitialState = nextAutomata.getInitialState();
			isFinal |= nextInitialState.isFinal();
			initialState.addAllTransitions(nextInitialState.iterator());
		}
		initialState.setIsFinal(isFinal);
		return initialAutomata;
	}

	/**
	 * Wires the automaton provided to be repeated zero to many times.
	 * This is done by making the initial state final,
	 * allowing for an instant match to cover the zero case.
	 *
	 * <p/><pre>{@code
	 *  X*     [0] ----> [1] __
	 *              X    ^    |                
	 *                   |____|                     
	 *                      X
	 *
	 * (XY)*  [0] ----> (1) ----> [2] __
	 *              X    ^     Y        |
	 *                   |______________|
	 *                         X
	 * }</pre><p/>
	 *
	 * Additionally, all of the final states loop back to the states that
	 * the initial state points to, allowing for as many repetitions as needed.
	 * It is the same as building one to many states, except the initial state
	 * is set to final to cover the zero case.
	 * 
	 * @param zeroToMany The automata to repeat zero to many times.
	 * @return An automata which will repeat zero to many times.
	 */
	@Override
	public Automata<T> buildZeroToManyAutomata(final Automata<T> zeroToMany) {
		final State<T> initialState = zeroToMany.getInitialState();
		final Collection<State<T>> finalStates = zeroToMany.getFinalStates();
		for (final State<T> state : finalStates) {
			state.addAllTransitions(initialState.iterator());
		}
		initialState.setIsFinal(true);
		return zeroToMany;
	}

	/**
	 * Wires the automaton provided to be repeated one to many times.
	 * This is done by making all the final states loop back to the states
	 * that the initial state points to, allowing for as many repetitions as needed.
	 *
	 * <p/><pre>{@code
	 *   X+     [0) ----> [1] __    
	 *                X    ^    |      
	 *                     |____|                     
	 *                        X
	 *
	 * (XY)+  (0) ----> (1) ----> [2]__
	 *              X    ^     Y       |
	 *                   |_____________|
	 *                         X
	 * }</pre><p/>
	 *
	 * It is the same as zeroToManyStates, except that the initial state is not made final.
	 *
	 * @param oneToMany The automata to repeat one to many times.
	 * @return An Automata which repeats the automata passed in one to many times.
	 */
	@Override
	public Automata<T> buildOneToManyAutomata(final Automata<T> oneToMany) {
		final State<T> initialState = oneToMany.getInitialState();
		final Collection<State<T>> finalStates = oneToMany.getFinalStates();
		for (final State<T> state : finalStates) {
			state.addAllTransitions(initialState.iterator());
		}
		return oneToMany;
	}

	/**
	 * Makes the automata passed in optional.
	 * It does this by making the initial state final.
	 *
	 * <p/><pre>{@code
	 *     X?    [0] ----> [1]
	 *                 X
	 * }</pre><p/>
	 *
	 * @param optional THe automata to make optional.
	 * @return An Automata which is optional.
	 */
	@Override
	public Automata<T> buildOptionalAutomata(final Automata<T> optional) {
		optional.getInitialState().setIsFinal(true);
		return optional;
	}

	/**
	 * Builds an automata which repeats a minimum number of times, up to many times.
	 * It achieves this by first building a repeated automata for the minimum number of
	 * times, then it builds a zero-to-many automata. Finally, it joins both automata
	 * together as a sequence.
	 *
	 * <p/><pre>{@code
	 *
	 * X{2-*}  (0) ----> (1) ----> [2] ----> [3] __
	 *               X         X         X    ^    |
	 *                                        |____|
	 *                                           X
	 * }</pre><p/>
	 *
	 * @param minRepeat The minimum number of times to repeat.
	 * @param repeatedAutomata The automata to repeat.
	 * @return An automata which repeats a minimum number of times, followed by many times.
	 */
	@Override
	public Automata<T> buildMinToManyAutomata(final int minRepeat,
			final Automata<T> repeatedAutomata) {
		Automata<T> automata = null;
		if (minRepeat == 0) {
			automata = buildZeroToManyAutomata(repeatedAutomata);
		} else if (minRepeat > 0) {
			final Automata<T> repeatStates = buildRepeatedAutomata(minRepeat, repeatedAutomata);
			final Automata<T> zeroToManyStates = buildZeroToManyAutomata(repeatedAutomata
					.deepCopy());
			automata = joinAutomata(repeatStates, zeroToManyStates);
		}
		return automata;
	}

	/**
	 * Builds an automata which repeats from a minimum number of times, up to a
	 * maximum number of times.  It achieves this by first building a repeated
	 * automata for the minimum number of times.  Then it builds repeated optional
	 * automata for the difference between the min and max repeats.  Finally, it
	 * joins both automata together as a sequence.  If the minimum number of repeats
	 * is zero, it just builds up to the maximum number of repeated optional states.
	 *
	 * <p/><pre>{@code
	 *
	 * X{2-4}  (0) ----> (1) ----> [2] ----> [3] ----> [4]
	 *               X         X         X         X
	 *                      
	 * }</pre><p/>
	 *
	 * @param minRepeat The minimum number of times to repeat.
	 * @param maxRepeat The maximum number of times to repeat.
	 * @param repeatedAutomata The automata to repeat.
	 * @return An automata which repeats at least the minimum numer of times up
	 *         to the maximum number of times specified.
	 */
	@Override
	public Automata<T> buildMinToMaxAutomata(final int minRepeat, final int maxRepeat,
			final Automata<T> repeatedAutomata) {
		Automata<T> automata = null;
		// If min repeat is zero, then we have up to max optional repeated
		// states:
		if (minRepeat == 0) {
			automata = buildRepeatedOptionalAutomata(maxRepeat, repeatedAutomata);
		} else { // we have some required repeated states:
			automata = buildRepeatedAutomata(minRepeat, repeatedAutomata);
			// possibly followed by (max - min) optional repeated states:
			if (maxRepeat > minRepeat) {
				final Automata<T> optionalStates = buildRepeatedOptionalAutomata(maxRepeat
						- minRepeat, repeatedAutomata);
				automata = joinAutomata(automata, optionalStates);
			}
		}
		return automata;
	}

	/**
	 * Builds an automata by repeating an automata which is made optional.
	 * It achieves this by making a deep copy of the automata for each repeat,
	 * which is made optional, and joining them together as a sequence.
	 *
	 * <p/><pre>{@code
	 *
	 * (X?){3}   [0] ----> [1] ----> [2] ----> [3]
	 *                 X         X         X
	 * 
	 * }</pre><p/>
	 *
	 * @param numberOptional the number of optional automata to repeat.
	 * @param optional The automata to repeat.
	 * @return An automata which repeats a source automata optionally a number of times.
	 */
	@Override
	public Automata<T> buildRepeatedOptionalAutomata(final int numberOptional,
			final Automata<T> optional) {
		final List<Automata<T>> automataList = new ArrayList<Automata<T>>();
		for (int count = 0; count < numberOptional; count++) {
			final Automata<T> optStates = buildOptionalAutomata(optional.deepCopy());
			automataList.add(optStates);
		}
		return buildSequenceAutomata(automataList);
	}

	/**
	 * Builds an automata by repeating an automata a number of times.
	 * It achieves this by making a deep copy of the automata for each repeat,
	 * and joining them together as a sequence.
	 *
	 * <p/><pre>{@code
	 *
	 * X{3}   (0) ----> (1) ----> (2) ----> [3]
	 *              X         X         X
	 *
	 * }</pre><p/>
	 *
	 * @param repeatNumber The number of times to repeat the automata.
	 * @param repeatedAutomata The automata to repeat.
	 * @return An automata which repeats the number of times specified.
	 */
	@Override
	public Automata<T> buildRepeatedAutomata(final int repeatNumber,
			final Automata<T> repeatedAutomata) {
		List<Automata<T>> automataList = new ArrayList<Automata<T>>();
		for (int count = 0; count < repeatNumber; count++) {
			final Automata<T> newAutomata = repeatedAutomata.deepCopy();
			automataList.add(newAutomata);
		}
		return buildSequenceAutomata(automataList);
	}

	private Automata<T> joinAutomata(final Automata<T> leftAutomata, final Automata<T> rightAutomata) {
		final List<Automata<T>> joinedAutomata = new ArrayList<Automata<T>>(2);
		joinedAutomata.add(leftAutomata);
		joinedAutomata.add(rightAutomata);
		return buildSequenceAutomata(joinedAutomata);
	}

}
