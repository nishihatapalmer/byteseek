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
 * 
 */

package net.domesdaybook.automata.transition;

import java.util.Set;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;


/**
 * A factory for {@link Transition} objects with a variety of construction methods
 * allowing different methods of matching the bytes being transitioned on.
 * 
 * <p>
 * It has specific factory methods for creating a Transition on:
 * <ul>
 * <li>A single byte value
 * <li>A transition based on matching all bits in a bitmask.
 * <li>A transition based on matching any bits in a bitmask.
 * <li>A transition based on matching any of the bytes in a set of bytes (or the inverse of that set)
 * <li>A transition based on matching a case-insensitive ASCII character.
 * </ul>
 * 
 * @author Matt Palmer
 * @see net.domesdaybook.automata.Transition
 */
public interface TransitionFactory {

    
    /**
     * Creates a {@link Transition} to a given {@link State} on a match to the byte provided.
     * 
     * @param theByte The byte on which this transition can match.
     * @param toState The State which this transition goes to.
     * @return Transition A transition to the State on the byte value provided.
     */
    public Transition createByteTransition(byte theByte, State toState);

    
    /**
     * Creates a {@link Transition} to a given {@link State} on a match to all the bits in
     * a bitmask.
     * <p>
     * Note that a bitmask of zero will match everything, as the matching rule is
     * that, given a byte b: <code>b & bitmask == bitmask</code>
     * 
     * @param bitMask The bitmask for which all set bits must match.
     * @param toState The State which this transition goes to.
     * @return Transition a transition to the State on matching all bits set in the bitmask provided.
     */
    public Transition createAllBitmaskTransition(byte bitMask, State toState);

    
    /**
     * Creates a {@link Transition} to a given {@link State} on a match to any of the bits in
     * a bitmask.
     * <p>
     * Note that a bitmask of zero will not match anything, as the matching rule is
     * that, given a byte b: <code>b & bitmask > 0</code>.
     * 
     * @param bitMask The bitmask for which any set bits can match.
     * @param toState The State which this transition goes to.
     * @return Transition a transition to the State on matching any bits set in the bitmask provided.
     */
    public Transition createAnyBitmaskTransition(byte bitMask, State toState);

    
    /**
     * Creates a {@link Transition} to a given {@link State} on a match to any of the
     * bytes in a set of bytes (or the inverse of that set).
     * 
     * @param byteSet The set of bytes which a given byte should match (or the inverted set if specified below)
     * @param inverted Whether the set of bytes to be matched should be inverted.
     * @param toState The State which this transition goes to.
     * @return Transition a transition to the State on matching any of the bytes in the
     *                    set of bytes (or the inverted set) provided.
     */
    public Transition createSetTransition(Set<Byte> byteSet, boolean inverted, State toState);

    
    /**
     * Creates a {@link Transition} to a given {@link State} which always matches.
     * 
     * @param toState The State which this transition goes to.
     * @return Transition a transition to the State which always matches.
     */
    public Transition createAnyByteTransition(State toState);

    
    /**
     * Creates a {@link Transition} to a given {@link State} on a case-insensitive
     * match to an ASCII character.
     * 
     * @param Char The ASCII character to match case-insensitively.
     * @param toState THe state which this transition goes to.
     * @return Transition a transition to the State which matches bytes as if they
     *         were ASCII text case-insensitively.
     */
    public Transition createCaseInsensitiveByteTransition(char Char, State toState);

}
