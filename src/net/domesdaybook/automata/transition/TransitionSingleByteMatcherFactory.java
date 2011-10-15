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
 *
 * @author Matt Palmer
 */
public class TransitionSingleByteMatcherFactory implements TransitionFactory {

    private final SingleByteMatcherFactory matcherFactory;


    public TransitionSingleByteMatcherFactory() {
        matcherFactory = new SimpleSingleByteMatcherFactory();
    }

    public TransitionSingleByteMatcherFactory(SingleByteMatcherFactory factoryToUse) {
        matcherFactory = factoryToUse;
    }


    @Override
    public final Transition createByteTransition(final byte theByte, final State toState) {
        return new TransitionSingleByteMatcher(new ByteMatcher(theByte), toState);
    }

    @Override
    public final Transition createAllBitmaskTransition(final byte bitMask, final State toState) {
        return new TransitionSingleByteMatcher(new BitMaskAllBitsMatcher(bitMask), toState);
    }

    @Override
    public final Transition createAnyBitmaskTransition(final byte bitMask, final State toState) {
        return new TransitionSingleByteMatcher(new BitMaskAnyBitsMatcher(bitMask), toState);
    }
    
    @Override
    public final Transition createSetTransition(final Set<Byte> byteSet, final boolean inverted, final State toState) {
        return new TransitionSingleByteMatcher(matcherFactory.create(byteSet, inverted), toState);
    }

    @Override
    public final Transition createAnyByteTransition(State toState) {
        return new TransitionSingleByteMatcher(new AnyMatcher(), toState);
    }

    @Override
    public final Transition createCaseInsensitiveByteTransition(final char Char, final State toState) {
        return new TransitionSingleByteMatcher(new CaseInsensitiveByteMatcher(Char), toState);
    }

}
