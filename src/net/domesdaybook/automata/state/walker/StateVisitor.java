/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.domesdaybook.automata.state.walker;

import net.domesdaybook.automata.State;

/**
 *
 * @author matt
 */
public interface StateVisitor {
    
    void visit(State state);
    
}
