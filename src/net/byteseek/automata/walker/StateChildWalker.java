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

package net.byteseek.automata.walker;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;

import net.byteseek.automata.State;
import net.byteseek.automata.Transition;
import net.byteseek.util.collections.IdentityHashSet;

/**
 * Walks an automata from an initial {@link State}, visiting each one in the automata
 * only once.  The states are walked in a child-first order (i.e. depth-first).
 * <p>
 * Note that not all transitions may be followed using this walker, as it is possible
 * for more than one {@link Transition} to reference the same State.  The transition
 * followed will simply be the first one which references a State which has not yet
 * been visited.
 * 
 * @author Matt Palmer
 */
public final class StateChildWalker<T> implements Walker<T> {

	/**
	 * Walks an automata from the starting state, invoking the Action for
	 * each step of the walk.
	 * 
	 * @param startState the start to start from.
	 * @param action The action to take on each step of the walk.
	 */
	public static <T> void walkAutomata(final State<T> startState, final Action<T> action) {
		final Walker<T> walker = new StateChildWalker<T>();
		walker.walk(startState, action);
	}

	/**
	 * Walks an automata from the startState, invoking the {@link Action} for
	 * each step of the walk.  This method will visit each State reachable from the 
	 * start State only once, in a child-first (i.e. depth-first) order.
	 * 
	 * @param startState The state to begin walking the automata.
	 * @param action  The action to take for each step of the walk.
	 */
	@Override
	public void walk(final State<T> startState, final Action<T> action) {
		final Set<State<T>> visitedStates = new IdentityHashSet<State<T>>();
		final Deque<Step<T>> walkSteps = new ArrayDeque<Step<T>>();
		walkSteps.addFirst(new Step<T>(null, null, startState));
		while (!walkSteps.isEmpty()) {
			final Step<T> step = walkSteps.removeFirst();
			final State<T> state = step.currentState;
			if (!visitedStates.contains(state)) {
				visitedStates.add(state);
				for (final Transition<T> transition : state) {
					walkSteps.addFirst(new Step<T>(state, transition, transition.getToState()));
				}
				final boolean keepWalking = action.process(step);
				if (!keepWalking) {
					return;
				}
			}
		}
	}

}
