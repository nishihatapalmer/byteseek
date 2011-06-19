/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.domesdaybook.automata.state;

/**
 *
 * @author matt
 */
public class SimpleAssociatedStateFactory<T> implements AssociatedStateFactory {

    @Override
    public SimpleAssociatedState<T> create(boolean isFinal) {
        return new SimpleAssociatedState<T>(isFinal);
    }
    
}
