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
 * A {@link TransitionStrategy} which finds the first {@link Transition} that
 * matches the supplied byte, and appends the single {@link State} that Transition 
 * points to, or appends no State if no Transition matches the supplied byte.
 * <p>
 * This strategy is appropriate to use in two circumstances:
 * <ul>
 * <li>Where the State has only one outgoing transition.
 * <li>Where the State never links to more than one State for a given byte 
 * </ul>
 * In fact, the first circumstance is just a special case of the second one, although
 * it is an easy circumstance to understand and identify.  In each case, the State 
 * is behaving as if it were in a Deterministic Finite State Automata (DFA), 
 * so the FirstMatchingTransition will find the only possible link for a given byte,
 * if one exists at all.
 * <p>
 * Note that this strategy can still be used within a Non-deterministic Finite
 * State Automata (NFA), as long as the State conforms to one of the two
 * circumstances above (in effect, the State is a DFA embedded in an NFA).
 * <p>
 * However, if this strategy is used where more than one State can be reached
 * on a given byte, then this strategy will not append all of those States,
 * only the first one it finds, which is almost certainly an error.
 * 
 * @see net.domesdaybook.automata.State
 * @see net.domesdaybook.automata.Transition
 * @see net.domesdaybook.automata.TransitionStrategy
 * 
 * @author Matt Palmer
 */
public final class FirstMatchingTransition implements TransitionStrategy {

    
    /**
     * Appends the first {@link State} for which a {@link Transition} exists on
     * the given byte, or appends nothing if there is no matching Transition.
     * <p>
     * If there are other possible Transitions which could have matched the byte,
     * and hence additional states which could be appended, they will be ignored.
     * This strategy should only be used when you are sure there will only ever be
     * a single state which can be reached on a given byte.
     * 
     * @param states The collection of states to append to.
     * @param value The byte value to try to find a transition on.
     * @param transitions The collection of transitions to match against the byte value.
     */
    @Override
    public void appendDistinctStatesForByte(final Collection<State> states, 
                                            final byte value, 
                                            final Collection<Transition> transitions) {
        for (final Transition transition : transitions) {
            final State nextState = transition.getStateForByte(value);
            if (nextState != null) {
                states.add(nextState);
                break;
            }
        }
    }

    
    //TODO: review - is it safe to return this in a deepcopy call?
    @Override
    public TransitionStrategy deepCopy(final Map<DeepCopy, DeepCopy> oldToNewObjects) {
        return this;
    }
    
}
