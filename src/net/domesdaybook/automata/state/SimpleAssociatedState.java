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
 *  * Neither the "byteseek" name nor the names of its contributors 
 *    may be used to endorse or promote products derived from this software 
 *    without specific prior written permission. 
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
import net.domesdaybook.automata.AssociatedState;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.object.copy.DeepCopy;

/**
 *
 * @author matt
 */
public class SimpleAssociatedState<T> implements AssociatedState {
    
    private List<Transition> transitions;
    private boolean isFinal;
    private TransitionStrategy transitionStrategy = NO_TRANSITION;
    private List<T> associations;
    
    public SimpleAssociatedState(final boolean isFinal) {
        this.isFinal = isFinal;
        this.transitions = new ArrayList<Transition>();
    }

    
    public SimpleAssociatedState(final SimpleAssociatedState other) {
        this.isFinal = other.isFinal;
        this.transitions = new ArrayList<Transition>(other.transitions); 
    }

    
    public SimpleAssociatedState() {
        this(State.NON_FINAL);
    }

    
    @Override
    public void addTransition(Transition transition) {
        this.transitions.add(transition);
    }

    
    @Override
    public void addAllTransitions(List<Transition> transitions) {
        this.transitions.addAll(transitions);
        setBasicStrategy();
    }

    
    @Override
    public void removeTransition(Transition transition) {
        this.transitions.remove(transition);
        setBasicStrategy();
    }
    
    
    @Override
    public void appendNextStatesForByte(Collection<State> states, byte value) {
        transitionStrategy.getDistinctStatesForByte(states, value, transitions);
    }

    
    private void setBasicStrategy() {
        if (transitions.isEmpty()) {
            transitionStrategy = NO_TRANSITION;
        } else if (transitions.size() == 1) {
            transitionStrategy = FIRST_MATCHING_TRANSITION;
        } else {
            // Determining whether the state satisfies a DFA strategy is expensive
            // so defer this decision to the optimise() method call.
            transitionStrategy = ALL_MATCHING_TRANSITIONS;
        }
    }
    
    @Override
    public boolean isFinal() {
        return isFinal;
    }

    @Override
    public List<Transition> getTransitions() {
        return new ArrayList<Transition>(this.transitions);
    }
    
    
    @Override
    public SimpleAssociatedState<T> deepCopy() {
        final Map<DeepCopy, DeepCopy> oldToNewObjects = new IdentityHashMap<DeepCopy,DeepCopy>();
        return deepCopy(oldToNewObjects);
    }

    @Override
    public SimpleAssociatedState<T> deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects) {
        SimpleAssociatedState<T> stateCopy = (SimpleAssociatedState<T>) oldToNewObjects.get(this);
        if (stateCopy == null) {
            stateCopy = new SimpleAssociatedState<T>(this.isFinal);
            oldToNewObjects.put(this, stateCopy);
            for (Transition transition : transitions) {
                final Transition transitionCopy = transition.deepCopy(oldToNewObjects);
                stateCopy.transitions.add(transitionCopy);
            }
            stateCopy.setTransitionStrategy(transitionStrategy.deepCopy(oldToNewObjects));
            stateCopy.setAssociations(associations); // does not deep copy associations.
        }
        return stateCopy;
    }

    
    @Override
    public void setIsFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    
    @Override
    public void setTransitionStrategy(TransitionStrategy strategy) {
        this.transitionStrategy = strategy;
        this.transitionStrategy.initialise(this);
    }

    
    @Override
    public TransitionStrategy getTransitionStrategy() {
        return transitionStrategy;
    }

    
    @Override
    public Collection<T> getAssociations() {
        return associations;
    }

    
    @Override
    public void addObject(Object object) {
        if (associations == null) {
            associations = new ArrayList<T>(1);
        }
        associations.add((T) object);
    }

    
    @Override
    public void removeObject(Object object) {
        if (associations != null) {
            associations.remove((T) object);
            if (associations.isEmpty()) {
                associations = null;
            }
        }
    }

    
    @Override
    public void setAssociations(Collection associations) {
        if (associations == null) {
            this.associations = null;
        } else {
            this.associations = new ArrayList<T>(associations);
        }
    }
    
   
}

