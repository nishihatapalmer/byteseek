/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
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
