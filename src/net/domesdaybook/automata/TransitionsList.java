/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.automata;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author matt
 */
public class TransitionsList implements TransitionsCollection {

    protected final List<Transition> transitions;

    public TransitionsList() {
        this.transitions = new ArrayList<Transition>();
    }
    
    public TransitionsList(final List<Transition> transitions) {
        this.transitions = new ArrayList<Transition>(transitions);
    }

    @Override
    public void addTransition(final Transition transition) {
        transitions.add(transition);
    }

}
