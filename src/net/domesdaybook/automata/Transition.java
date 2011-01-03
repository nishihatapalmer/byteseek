/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.automata;

/**
 *
 * @author Matt Palmer
 */
public interface Transition extends DeepCopy {

    public State getStateForByte(final byte theByte);

    public State getToState();

    public void setToState(final State toState);

    public byte[] getBytes();

}


