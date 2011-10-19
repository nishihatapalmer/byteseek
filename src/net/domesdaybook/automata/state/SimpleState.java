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
 *  
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

package net.domesdaybook.automata.state;

import net.domesdaybook.automata.TransitionStrategy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.object.copy.DeepCopy;


/**
 * A simple implementation of the {@link State} interface with no added extras.
 * Transitions are managed internally as a simple list of Transitions.  
 * <p>
 * It is intentionally not a final class, allowing other States to inherit from 
 * this implementation.
 * 
 * @see net.domesdaybook.automata.State
 * @see net.domesdaybook.automata.Transition
 * @author Matt Palmer
 */
public class SimpleState implements State {
    
    private List<Transition> transitions;
    private boolean isFinal;
    private TransitionStrategy transitionStrategy = NO_TRANSITION;

    
    /**
     * The default constructor for SimpleState, as a non-final state.
     */
    public SimpleState() {
        this(State.NON_FINAL);
    }
    
    
    /**
     * A constructor for SimpleState taking a parameter determining whether the
     * state is final or not.
     * 
     * @param isFinal Whether the state is final or not.
     */
    public SimpleState(final boolean isFinal) {
        this.isFinal = isFinal;
        this.transitions = new ArrayList<Transition>();
    }

    
    /**
     * A copy constructor for SimpleState from another state.
     * 
     * @param other The other State to copy from.
     */
    public SimpleState(final State other) {
        this.isFinal = other.isFinal();
        this.transitions = new ArrayList<Transition>(other.getTransitions()); 
    }

    
    /**
     * Adds a transition to this State.
     * <p>
     * It also changes the transition strategy based on the following simple heuristic:
     * <ul>
     * <li>If there is only one transition after adding, then the {@link FirstMatchingTransition} strategy is used.
     * <li>If there is more than one transition after adding, then the {@link AllMatchingTransitions} strategy is used.
     * </ul>
     * This will change any prior strategy set.  If you want to set a custom strategy
     * for this State, do so after adding any transitions you wish to add.
     * 
     * @param transition The transition to add to this State.
     * @see net.domesdaybook.automata.Transition
     * @see net.domesdaybook.automata.strategy.FirstMatchingTransition
     * @see net.domesdaybook.automata.strategy.AllMatchingTransitions
     */
    @Override
    public final void addTransition(final Transition transition) {
        this.transitions.add(transition);
        setBasicTransitionStrategy();
    }

    
    /**
     * Adds all the transitions in the list to this State, preserving any
     * previous transitions which were already attached to this state.
     * <p>
     * It also changes the transition strategy based on the following simple heuristic:
     * <ul>
     * <li>If there is only one transition after adding, then the {@link FirstMatchingTransition} strategy is used.
     * <li>If there is more than one transition after adding, then the {@link AllMatchingTransitions} strategy is used.
     * </ul>
     * This will change any prior strategy set.  If you want to set a custom strategy
     * for this State, do so after adding any transitions you wish to add.
     * @param transitions 
     * @see net.domesdaybook.automata.Transition
     * @see net.domesdaybook.automata.strategy.FirstMatchingTransition
     * @see net.domesdaybook.automata.strategy.AllMatchingTransitions
     */
    @Override
    public final void addAllTransitions(final List<Transition> transitions) {
        this.transitions.addAll(transitions);
        setBasicTransitionStrategy();
    }

    
    /**
     * Removes the transition from this State.
     * <p>
     * It also changes the transition strategy based on the following simple heuristic:
     * <ul>
     * <li>If there are no transitions after removing, then the {@link NoTransition} strategy is used.
     * <li>If there is only one transition after removing, then the {@link FirstMatchingTransition} strategy is used.
     * <li>If there is more than one transition after removing, then the {@link AllMatchingTransitions} strategy is used.
     * </ul>
     * This will change any prior strategy set.  If you want to set a custom strategy
     * for this State, do so after adding or removing any other transitions.
     * 
     * @param transition The transition to add to this State.
     * @return boolean Whether the transition was in the State.
     * @see net.domesdaybook.automata.Transition
     * @see net.domesdaybook.automata.TransitionStrategy
     * @see net.domesdaybook.automata.strategy.FirstMatchingTransition
     * @see net.domesdaybook.automata.strategy.AllMatchingTransitions
     * @see net.domesdaybook.automata.strategy.NoTransition
     */ 
    @Override
    public final boolean removeTransition(final Transition transition) {
        final boolean result = transitions.remove(transition);
        setBasicTransitionStrategy();
        return result;
    }
    
    
    /**
     * @inheritDoc
     */
    @Override
    public final void appendNextStatesForByte(final Collection<State> states, byte value) {
        transitionStrategy.appendDistinctStatesForByte(states, value, transitions);
    }

    
    /**
     * Sets a basic transition strategy based on the following simple heuristic:
     * <ul>
     * <li>If there are no transitions, then the {@link NoTransition} strategy is used.
     * <li>If there is only one transition, then the {@link FirstMatchingTransition} strategy is used.
     * <li>If there is more than one transition, then the {@link AllMatchingTransitions} strategy is used.
     * </ul>
     * 
     * @see net.domesdaybook.automata.TransitionStrategy
     * @see net.domesdaybook.automata.strategy.FirstMatchingTransition
     * @see net.domesdaybook.automata.strategy.AllMatchingTransitions
     * @see net.domesdaybook.automata.strategy.NoTransition
     */
    private void setBasicTransitionStrategy() {
        if (transitions.isEmpty()) {
            transitionStrategy = NO_TRANSITION;
        } else if (transitions.size() == 1) {
            transitionStrategy = FIRST_MATCHING_TRANSITION;
        } else {
            transitionStrategy = ALL_MATCHING_TRANSITIONS;
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isFinal() {
        return isFinal;
    }

    
    /**
     * Returns the transitions currently set in this State.  The list returned
     * is newly created.
     * 
     * @return A new ArrayList of transitions.
     * @see net.domesdaybook.automata.Transition
     */
    public final List<Transition> getTransitions() {
        return new ArrayList<Transition>(this.transitions);
    }
    
    
    /**
     * This is a convenience method, providing the initial map to:
     * <CODE>deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects)</CODE>
     *
     * @return SimpleState a deep copy of this object.
     * @see #deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects)
     */
    @Override
    public SimpleState deepCopy() {
        final Map<DeepCopy, DeepCopy> oldToNewObjects = new IdentityHashMap<DeepCopy,DeepCopy>();
        return deepCopy(oldToNewObjects);
    }

    
    /**
     * This method is inherited from the {@link DeepCopy} interface,
     * and is redeclared here with a return type of SimpleState (rather than DeepCopy),
     * to make using the method easier.
     *
     * @param oldToNewObjects A map of the original objects to their new deep copies.
     * @return SimpleState A deep copy of this SimpleState and any Transitions and States
     *         reachable from this State.
     */
    @Override
    public SimpleState deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects) {
        SimpleState stateCopy = (SimpleState) oldToNewObjects.get(this);
        if (stateCopy == null) {
            stateCopy = new SimpleState(this.isFinal);
            oldToNewObjects.put(this, stateCopy);
            for (Transition transition : transitions) {
                final Transition transitionCopy = transition.deepCopy(oldToNewObjects);
                stateCopy.transitions.add(transitionCopy);
            }
            stateCopy.setTransitionStrategy(transitionStrategy.deepCopy(oldToNewObjects));
        }
        return stateCopy;
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
    public final void setTransitionStrategy(TransitionStrategy strategy) {
        this.transitionStrategy = strategy;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public final TransitionStrategy getTransitionStrategy() {
        return transitionStrategy;
    }

   
}
