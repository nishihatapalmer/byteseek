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
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.automata.TransitionStrategy;
import net.domesdaybook.object.copy.DeepCopy;


/**
 * A transition strategy to use when a {@link State} has no outgoing transitions.
 * <p>
 * It should be considered an error to set this strategy on a state which does
 * have outgoing transitions (unless the intention is explicitly to ignore them for
 * some reason without removing them from the State).
 * 
 * @see net.domesdaybook.automata.State
 * @see net.domesdaybook.automata.Transition
 * @see net.domesdaybook.automata.TransitionStrategy
 * 
 * @author Matt Palmer
 */
public final class NoTransition implements TransitionStrategy {

    
    /**
     * Appends no states at all, as this strategy should be used in a State with
     * no outgoing Transitions.  If it is improperly assigned to a State which does
     * have outgoing transitions, then the Transitions will simply be ignored.
     * 
     * @param states A collection of states which can be appended to (but not by this class).
     * @param value The byte value normally used to determine which states can be appended.
     * @param transitions The transitions normally used to determine which states can be appended.
     */
    @Override
    public void appendDistinctStatesForByte(final Collection<State> states, 
                                            final byte value, 
                                            final Collection<Transition> transitions) {
        // method does nothing with a no transition strategy.
    }
    
    
    /**
     * Always returns null.
     * 
     * @param value A byte value.
     * @param transitions A collection of transitions.
     * @return null under all circumstances.
     */
    public State getFirstMatchingState(final byte value, final Collection<Transition> transitions) {
        return null;
    }
    
    
    //TODO: review - can an object safely return itself from a deepcopy method?
    //      Should it add itself to the map, even if it does return itself?
    public TransitionStrategy deepCopy(final Map<DeepCopy, DeepCopy> oldToNewObjects) {
        return this;
    }


    
}
