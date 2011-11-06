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

import net.domesdaybook.automata.TransitionFactory;
import java.util.Set;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.matcher.singlebyte.BitMaskAllBitsMatcher;
import net.domesdaybook.matcher.singlebyte.BitMaskAnyBitsMatcher;
import net.domesdaybook.matcher.singlebyte.AnyMatcher;
import net.domesdaybook.matcher.singlebyte.ByteMatcher;
import net.domesdaybook.matcher.singlebyte.CaseInsensitiveByteMatcher;
import net.domesdaybook.matcher.singlebyte.SimpleSingleByteMatcherFactory;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcherFactory;

/**
 * An implementation of {@link TransitionFactory} which creates 
 * {@link TransitionSingleByteMatcher}s.
 * <p>
 * Where the requirement for a matcher is unambiguous, the factory will
 * create an appropriate underlying {@link SingleByteMatcher} directly.
 * <p>
 * Where a set of bytes is required for a transition, it uses a 
 * {link SingleByteMatcherFactory} to create the appropriate type of
 * SingleByteMatcher for the transition.
 * 
 * @author Matt Palmer
 */
public class TransitionSingleByteMatcherFactory implements TransitionFactory {

    private final SingleByteMatcherFactory matcherFactory;

    
    /** 
     * Default constructor which used an underlying {@link SimpleSingleByteMatcherFactory}
     * to create {@link SingleByteMatcher}s based on sets of bytes.
     */
    public TransitionSingleByteMatcherFactory() {
        matcherFactory = new SimpleSingleByteMatcherFactory();
    }

    
    /**
     * Constructor which uses the supplied {@link SingleByteMatcherFactory} to
     * create {@link SingleByteMatcher}s based on sets of bytes.
     * 
     * @param factoryToUse The factory to create transitions based on sets of bytes.
     */
    public TransitionSingleByteMatcherFactory(final SingleByteMatcherFactory factoryToUse) {
        matcherFactory = factoryToUse;
    }


    /**
     * Creates a transition on a single byte using an underlying {@link ByteMatcher}.
     * 
     * @param theByte The byte to transition on.
     * @param toState The state to link to
     * @return Transition a transition which transitions to the given state on the given byte.
     */
    @Override
    public final Transition createByteTransition(final byte theByte, final State toState) {
        return new TransitionSingleByteMatcher(new ByteMatcher(theByte), toState);
    }

    
    /**
     * Creates a transition on a match to all bits of a bitmask, based on an underlying
     * {@link BitMaskAllBitsMatcher} object.
     * <p>
     * Note that a bitmask of zero will match everything, as the matching rule is
     * that, given a byte b: <code>b & bitmask == bitmask</code>
     * 
     * @param bitMask The bitmask which all bits must match.
     * @param toState The state to link to.
     * @return Transition a transition which transitions given a match with all bits of the
     *                    bitmask to the state supplied.
     */
    @Override
    public final Transition createAllBitmaskTransition(final byte bitMask, final State toState) {
        return new TransitionSingleByteMatcher(new BitMaskAllBitsMatcher(bitMask), toState);
    }

    
    /**
     * Creates a transition on a match to any bits of a bitmask, based on an underlying
     * {@link BitMaskAnyBitsMatcher} object.
     * <p>
     * Note that a bitmask of zero will not match anything, as the matching rule is
     * that, given a byte b: <code>b & bitmask > 0</code>
     * 
     * @param bitMask The bitmask which any bits must match.
     * @param toState The state to link to.
     * @return Transition a transition which transitions given a match with any bits of the
     *                     bitmask to the state supplied.
     */
    @Override
    public final Transition createAnyBitmaskTransition(final byte bitMask, final State toState) {
        return new TransitionSingleByteMatcher(new BitMaskAnyBitsMatcher(bitMask), toState);
    }
    
    
    /**
     * Creates a transition on a match to any bytes in the set of bytes supplied (or
     * the inverse set, if that is specified).
     * <p>
     * The underlying {@link SingleByteMatcher} used is created by the 
     * {@link SingleByteMatcherFactory}, which attempts to optimise what sort
     * of matcher is used for the set of bytes provided.
     * 
     * @param byteSet The set of bytes to be matched (or their inverse if specified)
     * @param inverted Whether the inverse of the set of bytes should be matched instead.
     * @param toState The state to link to
     * @return Transition a transition which matches on the set of bytes (or their
     *         inverse), to the state supplied.
     */
    @Override
    public final Transition createSetTransition(final Set<Byte> byteSet, final boolean inverted, final State toState) {
        return new TransitionSingleByteMatcher(matcherFactory.create(byteSet, inverted), toState);
    }

    
    /**
     * Creates a transition which matches any byte at all.
     * 
     * @param toState The state to link to
     * @return Transition a transition which always matches, going to the state supplied.
     */
    @Override
    public final Transition createAnyByteTransition(State toState) {
        return new TransitionSingleByteMatcher(new AnyMatcher(), toState);
    }

    
    /**
     * Creates a transition which matches on a case-insensitive comparison to the
     * bytes as if they were ASCII characters.
     * 
     * @param Char The character to match case-insensitively.
     * @param toState The state to link to.
     * @return Transition a transition which matches bytes as if they were ASCII text
     *                    case insensitively, to the state supplied.
     */
    @Override
    public final Transition createCaseInsensitiveByteTransition(final char Char, final State toState) {
        return new TransitionSingleByteMatcher(new CaseInsensitiveByteMatcher(Char), toState);
    }

}
