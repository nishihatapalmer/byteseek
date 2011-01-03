/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.automata.dfa;

import net.domesdaybook.automata.State;

/**
 *
 * @author Matt Palmer
 */
public interface DfaState extends State {

    public State nextState(final byte theByte);
    
}
