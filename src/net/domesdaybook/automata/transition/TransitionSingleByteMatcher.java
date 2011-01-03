/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.automata.transition;

import java.util.Map;
import net.domesdaybook.automata.DeepCopy;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;

/**
 *
 * @author Matt Palmer
 */
public class TransitionSingleByteMatcher implements Transition {

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
    public TransitionSingleByteMatcher deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects) {
        TransitionSingleByteMatcher copy = (TransitionSingleByteMatcher) oldToNewObjects.get(this);
        if (copy == null) {
            copy = new TransitionSingleByteMatcher(matcher, toState);
            oldToNewObjects.put(this, copy);
            final State copyState = toState.deepCopy(oldToNewObjects);
            copy.setToState(copyState);
        }
        return copy;
    }
    

    public final SingleByteMatcher getMatcher() {
        return matcher;
    }

    @Override
    public String toString() {
        return matcher.toRegularExpression(true);
    }

}
