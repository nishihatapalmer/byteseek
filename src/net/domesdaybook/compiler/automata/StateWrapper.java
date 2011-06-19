/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.compiler.automata;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.domesdaybook.automata.State;
import net.domesdaybook.object.copy.DeepCopy;

/**
 * A simple class which wraps the initial and final states of an automata.
 * This is useful when building automata, as we don't have to keep
 * recalculating the final states from the automata each time we need them.
 *
 * Also implements the {@link DeepCopy} interface, so we can easily
 * make deep copies of the wrapper and its wrapped automata.
 *
 * @author Matt Palmer
 */
public final class StateWrapper implements DeepCopy {

    State initialState;
    List<State> finalStates = new ArrayList<State>();

    /**
     * A method which makes a state final or not, and ensures that
     * the internal list of final states is kept up to date.
     *
     * @param state the state to make final or not.
     * @param isFinal Whether the state should be final.
     */
    public void setIsFinal(final State state, final boolean isFinal) {
        state.setIsFinal(isFinal);
        if (isFinal) {
            if (!finalStates.contains(state)) {
                finalStates.add(state);
            }
        } else {
            finalStates.remove(state);
        }
    }


    /**
     * Makes a deep copy of this object and its wrapped automata.
     * This is a convenience method which sets up an initial map
     * before calling the main deepCopy method that performs the
     * actual copy.
     * 
     * @return A deep copy of this object and its wrapped automata.
     */
    public StateWrapper deepCopy() {
        final Map<DeepCopy, DeepCopy> oldToNewObjects = new IdentityHashMap<DeepCopy, DeepCopy>();
        return deepCopy(oldToNewObjects);
    }    


    /**
     * Makes a deep copy of this object and its wrapped automata.
     *
     * @param oldToNewObjects A map of old objects to their deep copies.
     * @return A deep copy of this object and its wrapped automata.
     */
    @Override
    public StateWrapper deepCopy(final Map<DeepCopy, DeepCopy> oldToNewObjects) {
        StateWrapper copy = new StateWrapper();
        oldToNewObjects.put(this, copy);
        if (initialState == null) {
            copy.initialState = null;
        } else {
            copy.initialState = (State) initialState.deepCopy(oldToNewObjects);
        }
        if (finalStates == null) {
            copy.finalStates = null;
        } else {
            copy.finalStates = new ArrayList<State>();
            for (State finalState : finalStates) {
                final State finalStateCopy = (State) finalState.deepCopy(oldToNewObjects);
                copy.finalStates.add(finalStateCopy);
            }
        }
        return copy;
    }
        
}