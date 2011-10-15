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
 *
 * @author matt
 */
public class SimpleState implements State {
    
    private List<Transition> transitions;
    private boolean isFinal;
    private TransitionStrategy transitionStrategy = NO_TRANSITION;

    
    public SimpleState(final boolean isFinal) {
        this.isFinal = isFinal;
        this.transitions = new ArrayList<Transition>();
    }

    
    public SimpleState(final SimpleState other) {
        this.isFinal = other.isFinal;
        this.transitions = new ArrayList<Transition>(other.transitions); 
    }

    
    public SimpleState() {
        this(State.NON_FINAL);
    }


    public void addTransition(Transition transition) {
        this.transitions.add(transition);
    }

    
    public void addAllTransitions(List<Transition> transitions) {
        this.transitions.addAll(transitions);
        setBasicStrategy();
    }

    
    public void removeTransition(Transition transition) {
        this.transitions.remove(transition);
        setBasicStrategy();
    }
    
    
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
    
    
    public boolean isFinal() {
        return isFinal;
    }

    
    public List<Transition> getTransitions() {
        return new ArrayList<Transition>(this.transitions);
    }
    
    
    public SimpleState deepCopy() {
        final Map<DeepCopy, DeepCopy> oldToNewObjects = new IdentityHashMap<DeepCopy,DeepCopy>();
        return deepCopy(oldToNewObjects);
    }

    
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

   
}
