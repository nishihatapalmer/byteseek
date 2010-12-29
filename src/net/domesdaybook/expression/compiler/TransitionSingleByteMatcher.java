/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.expression.compiler;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;

/**
 *
 * @author matt
 */
public class TransitionSingleByteMatcher implements Transition, Cloneable {

    private final SingleByteMatcher matcher;
    private State toState;


    public TransitionSingleByteMatcher(final SingleByteMatcher matcher, final State toState) {
        this.matcher = matcher;
        this.toState = toState;
    }


    @Override
    public final State getStateForByte(byte theByte) {
        return matcher.matches(theByte) ? toState : null;
    }


    @Override
    public final State getToState() {
        return toState;
    }


    @Override
    public void setToState(final State toState) {
        this.toState = toState;
    }


    @Override
    public byte[] getBytes() {
        return matcher.getMatchingBytes();
    }


    @Override
    public Object clone() {
        Object clone = null;
        try {
            clone = super.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(TransitionSingleByteMatcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        return clone;
    }

    public final SingleByteMatcher getMatcher() {
        return matcher;
    }

}
