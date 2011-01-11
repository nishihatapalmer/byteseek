/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.expression.compiler.nfa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.domesdaybook.copy.DeepCopy;
import net.domesdaybook.automata.nfa.NfaState;

/**
 *
 * @author Matt Palmer
 */
public class StateWrapper implements DeepCopy {

    NfaState initialState;
    List<NfaState> finalStates = new ArrayList<NfaState>();

    public void setIsFinal(final NfaState state, final boolean isFinal) {
        state.setIsFinal(isFinal);
        if (isFinal) {
            if (!finalStates.contains(state)) {
                finalStates.add(state);
            }
        } else {
            finalStates.remove(state);
        }
    }

        
    public final StateWrapper deepCopy() {
        final Map<DeepCopy, DeepCopy> oldToNewObjects = new HashMap<DeepCopy, DeepCopy>();
        return deepCopy(oldToNewObjects);
    }    

    
    @Override
    public final StateWrapper deepCopy(final Map<DeepCopy, DeepCopy> oldToNewObjects) {
        StateWrapper copy = new StateWrapper();
        oldToNewObjects.put(this, copy);
        if (initialState == null) {
            copy.initialState = null;
        } else {
            copy.initialState = initialState.deepCopy(oldToNewObjects);
        }
        if (finalStates == null) {
            copy.finalStates = null;
        } else {
            copy.finalStates = new ArrayList<NfaState>();
            for (NfaState finalState : finalStates) {
                final NfaState finalStateCopy = finalState.deepCopy(oldToNewObjects);
                copy.finalStates.add(finalStateCopy);
            }
        }
        return copy;
    }
        
}