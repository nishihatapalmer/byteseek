/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.expression.compiler;

import java.util.List;
import net.domesdaybook.automata.nfa.NfaState;

/**
 *
 * @author matt
 */
public class NfaInitialFinalStates {

        NfaState initialState;
        List<NfaState> finalStates;

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
        
}