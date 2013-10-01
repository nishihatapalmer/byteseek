/*
 * Copyright Matt Palmer 2009-2012, All rights reserved.
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

package net.byteseek.matcher.automata;

import java.util.Map;

import net.byteseek.automata.State;
import net.byteseek.automata.Transition;
import net.byteseek.matcher.bytes.ByteMatcher;
import net.byteseek.object.factory.DeepCopy;

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
public class ByteMatcherTransition<T> implements Transition<T> {

	private final ByteMatcher	matcher;
	private final State<T>		toState;

	/**
	 * Constructor for the ByteMatcherTransition taking the {@link ByteMatcher}
	 * to use and the {@link State} this transition links to.
	 * 
	 * @param matcher The ByteMatcher to use to match bytes for this transition.
	 * @param toState The state this transition links to.
	 */
	public ByteMatcherTransition(final ByteMatcher matcher, final State<T> toState) {
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
	public ByteMatcherTransition(final ByteMatcherTransition<T> other, final State<T> toState) {
		this.matcher = other.matcher;
		this.toState = toState;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final State<T> getStateForByte(final byte theByte) {
		return matcher.matches(theByte) ? toState : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final State<T> getToState() {
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
	 * {@inheritDoc}
	 */
	@Override
	public Transition<T> newTransition(final State<T> newState) {
		return new ByteMatcherTransition<T>(matcher, newState);
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
	public ByteMatcherTransition<T> deepCopy(final Map<DeepCopy, DeepCopy> oldToNewObjects) {
		@SuppressWarnings("unchecked")
		// if there is an object copy of this in the map, it will be of the same type.
		ByteMatcherTransition<T> transitionCopy = (ByteMatcherTransition<T>) oldToNewObjects
				.get(this);
		if (transitionCopy == null) {
			oldToNewObjects.put(this, this); // put in a placeholder mapping to prevent an infinite loop.
			final State<T> copyState = toState.deepCopy(oldToNewObjects);
			transitionCopy = new ByteMatcherTransition<T>(this, copyState);
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


    @Override
    public String toString() {
    	return getClass().getSimpleName() + "[matcher:" + matcher + 
    										" to state: " + toState + ']';
    }
}
