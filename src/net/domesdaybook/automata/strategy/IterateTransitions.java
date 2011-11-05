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
 * 
 */


package net.domesdaybook.automata.strategy;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.automata.TransitionStrategy;
import net.domesdaybook.collections.IdentityHashSet;
import net.domesdaybook.object.copy.DeepCopy;


/**
 * This transition strategy finds all {@link Transition}s which match a given byte value,
 * and appends the unique {@link State}s referenced by those transitions to a
 * supplied collection.
 * <p>
 * This strategy may be used under two circumstances:
 * <ul>
 * <li>If more than one State can be reached on the same byte value.
 * <li>If the State has more than one Transition (but may be sub-optimal).
 * </ul>
 * The second circumstance is only a safe default to take, but may be sub-optimal. 
 * It is entirely possible for a State with more than one Transition to not 
 * be able to reach more than one State on any byte value, making it fully 
 * deterministic, rather than non-deterministic.
 * <p>
 * If you know that at most one State can be reached on any given byte value, 
 * then the {@link FirstMatchingTransition} strategy should be preferred, as this is
 * more efficient.  However, this may be expensive to determine, and so choosing this
 * strategy in the absence of this knowledge is a safe default position to take when
 * a State has more than one Transition.
 * <p>
 * This strategy is safe to use under all circumstances, but is the least
 * efficient of all the strategies in this package.  Where possible, choose a more
 * appropriate strategy.
 * 
 * @see net.domesdaybook.automata.State
 * @see net.domesdaybook.automata.Transition
 * @see net.domesdaybook.automata.TransitionStrategy
 * @see FirstMatchingTransition
 * 
 * @author Matt Palmer
 */
public final class IterateTransitions implements TransitionStrategy {

    /**
     * Appends all distinct {@link State}s to the supplied collection of states which are 
     * referenced by a {@link Transition} which matches the supplied byte value.
     * <p>
     * More than one State can be reached by the same byte value (if more than one
     * Transition matches the byte value), and the same State can be reachable more
     * than once on the same byte value (if different Transitions point to the same
     * State and overlap in the byte values they match on).
     * However, only distinct States will be appended to the collection supplied.
     * 
     * @param states The collection of states to append to.
     * @param value The byte value to find matching transitions for
     * @param transitions The collection of transitions to match the byte value against.
     */
    @Override
    public void appendDistinctStatesForByte(final Collection<State> states, 
                                            final byte value,
                                            final Collection<Transition> transitions) {
        final Set<State> matchingStates = new IdentityHashSet<State>();
        for (final Transition transition : transitions) {
            final State nextState = transition.getStateForByte(value);
            if (nextState != null && !matchingStates.contains(nextState)) {
                matchingStates.add(nextState);
                states.add(nextState);
            }
        }
    }
    
    
    
    @Override
    public State getFirstMatchingState(byte value, final Collection<Transition> transitions) {
        for (final Transition transition : transitions) {
            final State nextState = transition.getStateForByte(value);
            if (nextState != null) {
                return nextState;
            }
        } 
        return null;
    }    


    //TODO: review - is it safe to return this in a deepcopy call?
    public TransitionStrategy deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects) {
        return this;
    }

    
    
    
}
