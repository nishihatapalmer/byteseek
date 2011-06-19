/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.domesdaybook.automata.state;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.domesdaybook.automata.State;

/**
 *
 * @author matt
 */
public class StateAssociation<T> {
    
    private Map<State, List<T>> associations = new IdentityHashMap<State, List<T>>();
    
    public void addAssociation(T association, final State toState) {
        
    }
    
    public List<T> getAssociations(final State forState) {
        return associations.get(forState);
    }
    
}
