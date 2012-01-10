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

package net.domesdaybook.automata.walker;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.collections.IdentityHashSet;

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
public final class StateChildWalker implements Walker {

    public static void walkAutomata(final State startState, final StepTaker taker) {
        final Walker walker = new StateChildWalker();
        walker.walk(startState, taker);
    }
    
    
    /**
     * Walks an automata from the startState, invoking the {@link StepTaker} for
     * each step of the walk.  This method will visit each State reachable from the 
     * start State only once, in a child-first (i.e. depth-first) order.
     * 
     * @param startState The state to begin walking the automata.
     * @param observer The observer to invoke for each step of the walk.
     */
    @Override
    public void walk(final State startState, final StepTaker taker) {
        final Set<State> visitedStates = new IdentityHashSet<State>();
        final Deque<Step> walkSteps = new ArrayDeque<Step>();
        walkSteps.addFirst(new Step(null, null, startState));
        while (!walkSteps.isEmpty()) {
            final Step step = walkSteps.removeFirst();
            final State<?> state = step.currentState;
            if (!visitedStates.contains(state)) {
                visitedStates.add(state);
                for (final Transition transition: state.getTransitions()) {
                    walkSteps.addFirst(
                        new Step(state, transition, transition.getToState()));
                }
                taker.take(step);                
            }
        }
    }
    
}
