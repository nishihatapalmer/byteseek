/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.automata.transition;

import java.util.Map;
import net.domesdaybook.object.copy.DeepCopy;
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
    
    
    public TransitionSingleByteMatcher(TransitionSingleByteMatcher other, final State toState) {
        this.matcher = other.matcher;
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
    public byte[] getBytes() {
        return matcher.getMatchingBytes();
    }


    @Override
    public TransitionSingleByteMatcher deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects) {
        TransitionSingleByteMatcher transitionCopy = (TransitionSingleByteMatcher) oldToNewObjects.get(this);
        if (transitionCopy == null) {
            oldToNewObjects.put(this, this); // put in a placeholder mapping to prevent an infinite loop.
            final State copyState = (State) toState.deepCopy(oldToNewObjects);
            transitionCopy = new TransitionSingleByteMatcher(this, copyState);
            oldToNewObjects.put(this, transitionCopy); // now put the real transition in.
        }
        return transitionCopy;
    }
    

    public final SingleByteMatcher getMatcher() {
        return matcher;
    }

    @Override
    public String toString() {
        return matcher.toRegularExpression(true);
    }

}
