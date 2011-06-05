/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.domesdaybook.automata.state;

import net.domesdaybook.automata.State;

/**
 *
 * @author matt
 */
public class SimpleStateFactory implements AbstractStateFactory {

    @Override
    public State create(boolean isFinal) {
        return new SimpleState(isFinal);
    }
    
}

