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
public class TransitionList implements TransitionCollection {

    protected final List<Transition> transitions;

    public TransitionList() {
        this.transitions = new ArrayList<Transition>();
    }
    
    public TransitionList(final List<Transition> transitions) {
        this.transitions = new ArrayList<Transition>(transitions);
    }

    @Override
    public final void addTransition(final Transition transition) {
        transitions.add(transition);
    }

}
