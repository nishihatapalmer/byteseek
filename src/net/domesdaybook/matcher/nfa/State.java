/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.matcher.nfa;

import java.util.Set;

/**
 *
 * @author matt
 */
public class State {

    public Set<State> nextStates(byte b) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setIsFinal(boolean isFinal) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isFinal() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addTransition(byte aByte, State nextState) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addTransition(byte[] bytes, State nextState) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
