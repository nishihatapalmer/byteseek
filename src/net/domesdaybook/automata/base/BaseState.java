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

package net.domesdaybook.automata.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.util.collections.IdentityHashSet;
import net.domesdaybook.util.object.DeepCopy;


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
public class BaseState<T> implements State<T> {
    
    
    private List<Transition> transitions;
    private List<T> associations;    
    private boolean isFinal;

    
    // Constructors
    
    /**
     * The default constructor for BaseState, as a non-final state.
     */
    public BaseState() {
        this(State.NON_FINAL);
    }
    
    
    /**
     * A constructor for BaseState taking a parameter determining whether the
     * state is final or not.
     * 
     * @param isFinal Whether the state is final or not.
     */
    public BaseState(final boolean isFinal) {
        this.isFinal = isFinal;
        this.transitions = Collections.EMPTY_LIST; // new ArrayList<Transition>(1);
        this.associations = Collections.EMPTY_LIST; // = new ArrayList<T>(0);
    }

    
    /**
     * A copy constructor for BaseState from another state.
     * 
     * @param other The other State to copy from.
     * @throws NullPointerException if the State passed in is null.
     */
    public BaseState(final State<T> other) {
        this.isFinal = other.isFinal();
        final List<Transition> otherTransitions = other.getTransitions();
        if (otherTransitions != null && otherTransitions.size() > 0) {
            this.transitions = new ArrayList<Transition>(otherTransitions);
        } else {
            this.transitions = Collections.EMPTY_LIST;
        }
        final Collection<T> otherAssoc = other.getAssociations();
        if (otherAssoc != null) {
            this.associations = new ArrayList<T>(otherAssoc);
        } else {
            this.associations = Collections.EMPTY_LIST;
        }
    }

    
    // Methods
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void addTransition(final Transition transition) {
        if (transitions.isEmpty()) {
            transitions = new ArrayList<Transition>(1);
        }
        transitions.add(transition);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void addAllTransitions(final List<Transition> transitions) {
        if (transitions.isEmpty()) {
            this.transitions = new ArrayList<Transition>(transitions.size());
        }        
        transitions.addAll(transitions);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean removeTransition(final Transition transition) {
        boolean wasRemoved = transitions.remove(transition);
        if (transitions.isEmpty()) {
            transitions = Collections.EMPTY_LIST;
        }
        return wasRemoved;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override        
    public void clearTransitions() {
        transitions = Collections.EMPTY_LIST;
    }
    
    
    /**
     * @inheritDoc
     */
    @Override
    public final void appendNextStates(final Collection<State<T>> states, final byte value) {
       final Set<State<T>> matchingStates = new IdentityHashSet<State<T>>();
        for (final Transition transition : transitions) {
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
        for (final Transition transition : transitions) {
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
    public final void setIsFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }    

    
    /**
     * {@inheritDoc}
     */
    public final List<Transition> getTransitions() {
        if (transitions.isEmpty()) {
            return transitions;
        }
        return new ArrayList<Transition>(transitions);
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
    public void addAssociation(final Object association) {
        if (associations.isEmpty()) {
            associations = new ArrayList<T>(1);
        }
        associations.add((T) association);
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAllAssociations(Collection<T> associations) {
        if (associations.isEmpty()) {
            associations = new ArrayList<T>(associations.size());
        }
        this.associations.addAll(associations);
    }    

    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAssociation(final Object association) {
        final boolean wasRemoved = associations.remove((T) association);
        if (associations.isEmpty()) {
            associations = Collections.EMPTY_LIST;
        }
        return wasRemoved;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setAssociations(final Collection<T> associations) {
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
        associations = Collections.EMPTY_LIST;
    }    

    
    /**
     * This is a convenience method, providing the initial map to:
     * <CODE>deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects)</CODE>
     *
     * @return BaseState a deep copy of this object.
     * @see #deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects)
     */
    @Override
    public BaseState<T> deepCopy() {
        final Map<DeepCopy, DeepCopy> oldToNewObjects = new IdentityHashMap<DeepCopy,DeepCopy>();
        return deepCopy(oldToNewObjects);
    }

    
    /**
     * This method is inherited from the {@link DeepCopy} interface,
     * and is redeclared here with a return type of BaseState (rather than DeepCopy),
     * to make using the method easier.
     *
     * @param oldToNewObjects A map of the original objects to their new deep copies.
     * @return BaseState A deep copy of this BaseState and any Transitions and States
     *         reachable from this State.
     */
    @Override
    public BaseState<T> deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects) {
        BaseState<T> stateCopy = (BaseState<T>)oldToNewObjects.get(this);
        if (stateCopy == null) {
            stateCopy = new BaseState(this.isFinal);
            oldToNewObjects.put(this, stateCopy);
            for (Transition transition : transitions) {
                final Transition transitionCopy = transition.deepCopy(oldToNewObjects);
                stateCopy.transitions.add(transitionCopy);
            }
        }
        return stateCopy;
    }






   
}
