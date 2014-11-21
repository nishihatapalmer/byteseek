/*
 * Copyright Matt Palmer 2011-2012, All rights reserved.
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
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import net.byteseek.automata.walker.Action;
import net.byteseek.automata.walker.StateChildWalker;
import net.byteseek.automata.walker.Step;
import net.byteseek.object.factory.DeepCopy;

/**
 * A mutable implementation of {@link net.byteseek.automata.Automata} interface.
 * 
 * @param <T> The type of object which can be associated with states of the automata.
 * 
 * @author Matt Palmer
 */
public class MutableAutomata<T> implements Automata<T> {

	/**
	 * The initial state of the automata.
	 */
	protected State<T>	initialState;

	/**
	 * Constructs an empty Automata with no states.
	 */
	public MutableAutomata() {
	}

	/**
	 * Constructs an Automata with an initial state.
	 * 
	 * @param initialState The initial state of the automata.
	 */
	public MutableAutomata(final State<T> initialState) {
		this.initialState = initialState;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public State<T> getInitialState() {
		return initialState;
	}

	/**
	 * Sets the initial state of this automata.
	 * 
	 * @param initialState  The initial State of this automata.
	 */
	public void setInitialState(final State<T> initialState) {
		this.initialState = initialState;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDeterministic() {
		final class MutableBoolean {
			public boolean	isDeterministic	= true;
		}
		final MutableBoolean result = new MutableBoolean();
		final Action<T> isDeterministic = new Action<T>() {
			@Override
			public boolean process(final Step<T> step) {
				result.isDeterministic = step.currentState.isDeterministic();
				// if any state is not deterministic, then the whole automata is not,
				// so stop the walk.
				return result.isDeterministic;
			}
		};
		StateChildWalker.walkAutomata(initialState, isDeterministic);
		return result.isDeterministic;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * This implementation calculates the final states dynamically by walking
	 * the automata states to find the final ones.
	 */
	@Override
	public List<State<T>> getFinalStates() {
		final List<State<T>> finalStates = new ArrayList<State<T>>();
		final Action<T> findFinalStates = new Action<T>() {
			@Override
			public boolean process(final Step<T> step) {
				if (step.currentState.isFinal()) {
					finalStates.add(step.currentState);
				}
				return true;
			}
		};
		StateChildWalker.walkAutomata(initialState, findFinalStates);
		return finalStates;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MutableAutomata<T> deepCopy() {
		final Map<DeepCopy, DeepCopy> oldToNew = new IdentityHashMap<DeepCopy, DeepCopy>();
		return deepCopy(oldToNew);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MutableAutomata<T> deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects) {
		return new MutableAutomata<T>(initialState.deepCopy(oldToNewObjects));
	}

}
