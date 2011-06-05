/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.domesdaybook.automata;

import java.util.Collection;
import java.util.Map;
import net.domesdaybook.object.copy.DeepCopy;

/**
 *
 * @author matt
 */
public interface TransitionStrategy extends DeepCopy {
    
    void initialise(State state);
    
    void getDistinctStatesForByte(Collection<State> states, byte value, Collection<Transition> transitions);

    TransitionStrategy deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects);
    
}
