/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.domesdaybook.automata;

import java.util.Collection;

/**
 *
 * @author matt
 */
public interface AssociatedState<T> extends State {
    
    void addObject(T object);
    
    void removeObject(T object);
    
    Collection<T> getAssociations();
    
    void setAssociations(Collection<T> associations);
    
}
