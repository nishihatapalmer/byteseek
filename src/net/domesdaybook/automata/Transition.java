/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.automata;

/**
 *
 * @author matt
 */
public interface Transition {

    public State getStateForByte(final byte theByte);

    public State getToState();

    public byte[] getBytes();

}
