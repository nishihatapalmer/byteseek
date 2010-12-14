/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.automata;

/**
 *
 * @author matt
 */
public class TransitionsSingle implements TransitionsCollection {

    protected Transition transition;

    public TransitionsSingle() {
        transition = new TransitionNull();
    }

    public TransitionsSingle(final Transition transition) {
        this.transition = transition;
    }

    public void addTransition(final Transition transition) {
        this.transition = transition;
    }

}
