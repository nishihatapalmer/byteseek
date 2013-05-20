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

package net.byteseek.automata;

import java.util.Map;

import net.byteseek.util.object.DeepCopy;

/**
 * A Transition is a link to a {@link State} which can be followed if a given
 * byte value matches.
 * <p>
 * The link is uni-directional, as a Transition only knows the single State it
 * can transition to on a given byte. It has no knowledge of the State it
 * transitions from.
 * 
 * @author Matt Palmer
 * @see State
 */
public interface Transition<T> extends DeepCopy {

	/**
	 * Returns a {@link State} if the byte supplied matches this Transition. If
	 * the byte does not match, null is returned.
	 * 
	 * @param theByte
	 *            The byte value we would like to transition on.
	 * @return State The State to transition to for this byte, or null if the
	 *         byte does not match.
	 */
	public State<T> getStateForByte(byte theByte);

	/**
	 * Returns the {@link State} this transition links to.
	 * 
	 * @return State the state this transition links to.
	 */
	public State<T> getToState();

	/**
	 * Returns an array of all the bytes which this transition can match.
	 * <p>
	 * Implementors guarantee that modifying the array contents will not affect the transition.
	 * 
	 * @return byte[] An array of bytes on which this transition will match.
	 */
	public byte[] getBytes();

	/**
	 * Creates a new Transition from this transition, but pointing at a new State.
	 * The new Transition will transition on the same bytes as this transition.
	 * 
	 * @param newState The new state to point to.
	 * @return A new transition which points to a new state but transitions on the same byte values.
	 */
	public Transition<T> newTransition(State<T> newState);

	/**
	 * This method is inherited from the {@link DeepCopy} interface, and is
	 * redeclared here with a return type of Transition (rather than DeepCopy),
	 * to make using the method easier.
	 * 
	 * @param oldToNewObjects
	 *            A map of the original objects to their new deep copies.
	 * @return Transition A deep copy of this Transition and any States and
	 *         Transitions reachable from this Transition.
	 */
	@Override
	public Transition<T> deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects);

}
