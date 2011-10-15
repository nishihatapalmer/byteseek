/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 
 *  * The names of its contributors may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 * 
 *  
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
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
    private final State toState;


    public TransitionSingleByteMatcher(final SingleByteMatcher matcher, final State toState) {
        this.matcher = matcher;
        this.toState = toState;
    }
    
    
    public TransitionSingleByteMatcher(final TransitionSingleByteMatcher other, final State toState) {
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
