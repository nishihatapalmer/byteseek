/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.automata;

/**
 *
 * @author matt
 */
public class TransitionNull implements Transition {

    public State getStateForByte(byte theByte) {
        return null;
    }

    public State getToState() {
        return null;
    }

    public byte[] getBytes() {
        return null;
    }

}
