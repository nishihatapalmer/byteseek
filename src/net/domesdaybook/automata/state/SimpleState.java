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

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.collections.IdentityHashSet;
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
public class SimpleState<T> implements State<T> {
    
    private final List<Transition> transitions;
    private final List<T> associations;    
    private boolean isFinal;

    
    // Constructors
    
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
        this.transitions = new ArrayList<Transition>(1);
        this.associations = new ArrayList<T>(0);
    }

    
    /**
     * A copy constructor for SimpleState from another state.
     * 
     * @param other The other State to copy from.
     * @throws NullPointerException if the State passed in is null.
     */
    public SimpleState(final State other) {
        this.isFinal = other.isFinal();
        this.transitions = new ArrayList<Transition>(other.getTransitions());
        this.associations = new ArrayList<T>(other.getNumberOfAssociations());
        other.appendAssociations(this.associations);
    }

    
    // Methods
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void addTransition(final Transition transition) {
        transitions.add(transition);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void addAllTransitions(final List<Transition> transitions) {
        this.transitions.addAll(transitions);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean removeTransition(final Transition transition) {
        return transitions.remove(transition);
    }
    
    
    /**
     * @inheritDoc
     */
    @Override
    public final void appendNextStates(final Collection<State> states, final byte value) {
       final Set<State> matchingStates = new IdentityHashSet<State>();
        for (final Transition transition : transitions) {
            final State nextState = transition.getStateForByte(value);
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
    public final State getNextState(final byte value) {
        for (final Transition transition : transitions) {
            final State nextState = transition.getStateForByte(value);
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
    public final void setIsFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }    

    
    /**
     * {@inheritDoc}
     */
    public final List<Transition> getTransitions() {
        return new ArrayList<Transition>(this.transitions);
    }
    
    
   
    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfAssociations() {
        return associations.size();
    }    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void appendAssociations(Collection<T> toCollection) {
        toCollection.addAll(associations);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addObject(final Object object) {
        associations.add((T) object);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeObject(final Object object) {
        return associations.remove((T) object);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setAssociations(final Collection<T> associations) {
        this.associations.clear();
        this.associations.addAll(associations);
    }
    

    /**
     * This is a convenience method, providing the initial map to:
     * <CODE>deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects)</CODE>
     *
     * @return SimpleState a deep copy of this object.
     * @see #deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects)
     */
    @Override
    public SimpleState<T> deepCopy() {
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
    public SimpleState<T> deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects) {
        SimpleState<T> stateCopy = (SimpleState<T>)oldToNewObjects.get(this);
        if (stateCopy == null) {
            stateCopy = new SimpleState(this.isFinal);
            oldToNewObjects.put(this, stateCopy);
            for (Transition transition : transitions) {
                final Transition transitionCopy = transition.deepCopy(oldToNewObjects);
                stateCopy.transitions.add(transitionCopy);
            }
        }
        return stateCopy;
    }



   
}
