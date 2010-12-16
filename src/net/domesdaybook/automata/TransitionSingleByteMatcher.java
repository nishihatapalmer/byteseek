/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.automata;

import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;

/**
 *
 * @author matt
 */
public class TransitionSingleByteMatcher implements Transition {

    private final SingleByteMatcher matcher;
    private final State toState;


    public TransitionSingleByteMatcher(final SingleByteMatcher matcher, final State toState) {
        this.matcher = matcher;
        this.toState = toState;
    }


    public final State getStateForByte(byte theByte) {
        return matcher.matches(theByte) ? toState : null;
    }


    public final State getToState() {
        return toState;
    }

    
    public byte[] getBytes() {
        return matcher.getMatchingBytes();
    }


    public final SingleByteMatcher getMatcher() {
        return matcher;
    }

}
