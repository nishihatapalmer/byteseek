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
 */

package net.domesdaybook.automata.base;

import java.util.Map;
import net.domesdaybook.util.object.DeepCopy;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.matcher.bytes.ByteMatcher;

/**
 * A mutable implementation of {@link Transition} which matches bytes using a
 * {@link ByteMatcher}.
 * <p>
 * This implementation is immutable, so is thread-safe.  It is possible to
 * get the internal ByteMatcher used, but implementations of this interface
 * should also be immutable.
 * 
 * @author Matt Palmer
 */
public class ByteMatcherTransition implements Transition {

    private final ByteMatcher matcher;
    private State toState;


    /**
     * Constructor for the ByteMatcherTransition taking the {@link ByteMatcher}
     * to use and the {@link State} this transition links to.
     * 
     * @param matcher The ByteMatcher to use to match bytes for this transition.
     * @param toState The state this transition links to.
     */
    public ByteMatcherTransition(final ByteMatcher matcher, final State toState) {
        this.matcher = matcher;
        this.toState = toState;
    }
   
    
    /**
     * Copy constructor for the ByteMatcherTransition, taking another
     * ByteMatcherTransition to copy from, and another {@link State} to link to.
     * <p>
     * Since instances of this class are immutable, an identical copy of an instance
     * of this class will always be identical to the original, making a copy constructor
     * essentially useless.
     * <p>
     * This is really a convenience constructor, which copies the matcher out of 
     * an existing ByteMatcherTransition, but specifies a different State to 
     * link to.  It is equivalent to:
     * <code>ByteMatcherTransition(other.getMatcher(), someState);</code>
     * 
     * @param other The ByteMatcherTransition to copy the matcher from.
     * @param toState The State that this transition links to.
     */
    public ByteMatcherTransition(final ByteMatcherTransition other, final State toState) {
        this.matcher = other.matcher;
        this.toState = toState;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final State getStateForByte(byte theByte) {
        return matcher.matches(theByte) ? toState : null;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public final State getToState() {
        return toState;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getBytes() {
        return matcher.getMatchingBytes();
    }


    /**
     * This method is inherited from the {@link DeepCopy} interface,
     * and is redeclared here with a return type of ByteMatcherTransition
     * (rather than DeepCopy), to make using the method easier.
     *
     * @param oldToNewObjects A map of the original objects to their new deep copies.
     * @return Transition A deep copy of this ByteMatcherTransition and any 
     *                    States and Transitions reachable from this Transition.
     */

    @Override
    public ByteMatcherTransition deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects) {
        ByteMatcherTransition transitionCopy = (ByteMatcherTransition) oldToNewObjects.get(this);
        if (transitionCopy == null) {
            oldToNewObjects.put(this, this); // put in a placeholder mapping to prevent an infinite loop.
            final State copyState = (State) toState.deepCopy(oldToNewObjects);
            transitionCopy = new ByteMatcherTransition(this, copyState);
            oldToNewObjects.put(this, transitionCopy); // now put the real transition in.
        }
        return transitionCopy;
    }
    

    /**
     * Returns the ByteMatcher used in this Transition.
     * 
     * @return ByteMatcher the matcher used in this Transition.
     */
    public final ByteMatcher getMatcher() {
        return matcher;
    }
    

    /**
     * Returns a regular-expression representation of the underlying
     * ByteMatcher, in byte-seek syntax.
     * 
     * @return String a byteSeek regular expression representation of this Transition.
     */
    @Override
    public String toString() {
        return matcher.toRegularExpression(true);
    }

    
    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public void setToState(final State stateToPointAt) {
        this.toState = stateToPointAt;
    }

}
