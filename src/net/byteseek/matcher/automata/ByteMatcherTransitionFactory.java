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

import java.util.Collection;

import net.byteseek.automata.State;
import net.byteseek.automata.Transition;
import net.byteseek.automata.factory.TransitionFactory;
import net.byteseek.matcher.bytes.ByteMatcherFactory;
import net.byteseek.matcher.bytes.SetAnalysisByteMatcherFactory;

/**
 * An implementation of {@link TransitionFactory} which creates 
 * {@link ByteMatcherTransition}s from a collection of Bytes,
 * using a {link ByteMatcherFactory} to create the appropriate type of
 * ByteMatcher for the transition from the collection of bytes.
 * 
 * @author Matt Palmer
 */
public final class ByteMatcherTransitionFactory<T> implements TransitionFactory<T, Collection<Byte>> {

    private final ByteMatcherFactory matcherFactory;

    
    /** 
     * Default constructor which used an underlying {@link SetAnalysisByteMatcherFactory}
     * to create {@link net.byteseek.matcher.bytes.ByteMatcher}s based on sets of bytes.
     */
    public ByteMatcherTransitionFactory() {
        matcherFactory = new SetAnalysisByteMatcherFactory();
    }

    
    /**
     * Constructor which uses the supplied {@link ByteMatcherFactory} to
     * create {@link net.byteseek.matcher.bytes.ByteMatcher}s based on sets of bytes.
     * 
     * @param factoryToUse The factory to create transitions based on sets of bytes.
     */
    public ByteMatcherTransitionFactory(final ByteMatcherFactory factoryToUse) {
        matcherFactory = factoryToUse;
    }
    
    /**
     * Creates a transition on a match to any bytes in the set of bytes supplied (or
     * the inverse set, if that is specified).
     * <p>
     * The underlying {@link net.byteseek.matcher.bytes.ByteMatcher} used is created by the 
     * {@link ByteMatcherFactory}, which attempts to optimise what sort
     * of matcher is used for the set of bytes provided.
     * 
     * @param byteSet The set of bytes to be matched (or their inverse if specified)
     * @param inverted Whether the inverse of the set of bytes should be matched instead.
     * @param toState The state to link to
     * @return Transition a transition which matches on the set of bytes (or their
     *         inverse), to the state supplied.
     */
    @Override
    public final Transition<T> create(final Collection<Byte> byteSet, final boolean inverted, final State<T> toState) {
        return new ByteMatcherTransition<T>(matcherFactory.create(byteSet, inverted), toState);
    }

}
