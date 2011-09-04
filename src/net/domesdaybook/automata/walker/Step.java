/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.domesdaybook.automata.walker;

import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;

/**
 *
 * @author matt
 */
public class Step {
    
    public State fromState;
    public Transition onTransition;
    public State toState;
    
    public Step(final State fromState, final Transition onTransition, final State toState) {
        this.fromState = fromState;
        this.onTransition = onTransition;
        this.toState = toState;
    }
    
}
