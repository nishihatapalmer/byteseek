/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.automata.nfa;

import java.util.Set;

/**
 *
 * @author matt
 */
public class NfaState {

    public Set<NfaState> nextStates(byte b) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setIsFinal(boolean isFinal) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isFinal() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addTransition(byte aByte, NfaState nextState) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addTransition(byte[] bytes, NfaState nextState) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
