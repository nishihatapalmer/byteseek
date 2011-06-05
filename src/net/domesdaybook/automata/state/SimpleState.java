/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.domesdaybook.automata.state;

import net.domesdaybook.automata.TransitionStrategy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.object.copy.DeepCopy;

/**
 *
 * @author matt
 */
public final class SimpleState implements State {
    
    private List<Transition> transitions;
    private boolean isFinal;
    private TransitionStrategy transitionStrategy = NO_TRANSITIONS;

    
    public SimpleState(final boolean isFinal) {
        this.isFinal = isFinal;
        this.transitions = new ArrayList<Transition>();
    }

    
    public SimpleState(final SimpleState other) {
        this.isFinal = other.isFinal;
        this.transitions = new ArrayList<Transition>(other.transitions); 
    }

    
    public SimpleState() {
        this(false);
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
    
    
    public void getStatesForByte(Collection<State> states, byte value) {
        transitionStrategy.getDistinctStatesForByte(states, value, transitions);
    }

    
    private void setBasicStrategy() {
        if (transitions.isEmpty()) {
            transitionStrategy = NO_TRANSITIONS;
        } else if (transitions.size() == 1) {
            transitionStrategy = DFA_STATE_STRATEGY;
        } else {
            // Determining whether the state satisfies a DFA strategy is expensive
            // so defer this decision to the optimise() method call.
            transitionStrategy = NFA_STATE_STRATEGY;
        }
    }
    
    
    public boolean isFinal() {
        return isFinal;
    }

    
    public List<Transition> getTransitions() {
        return new ArrayList<Transition>(this.transitions);
    }
    
    
    public SimpleState deepCopy() {
        final Map<DeepCopy, DeepCopy> oldToNewObjects = new HashMap<DeepCopy,DeepCopy>();
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
