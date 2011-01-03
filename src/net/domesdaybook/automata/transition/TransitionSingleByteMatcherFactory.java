/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.automata.transition;

import net.domesdaybook.automata.transition.TransitionSingleByteMatcher;
import net.domesdaybook.automata.transition.TransitionFactory;
import java.util.Set;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.automata.nfa.NfaState;
import net.domesdaybook.matcher.singlebyte.AllBitMaskMatcher;
import net.domesdaybook.matcher.singlebyte.AnyBitMaskMatcher;
import net.domesdaybook.matcher.singlebyte.AnyByteMatcher;
import net.domesdaybook.matcher.singlebyte.ByteMatcher;
import net.domesdaybook.matcher.singlebyte.ByteSetMatcher;
import net.domesdaybook.matcher.singlebyte.CaseInsensitiveByteMatcher;

/**
 *
 * @author Matt Palmer
 */
public class TransitionSingleByteMatcherFactory implements TransitionFactory {

    @Override
    public final Transition createByteTransition(final byte theByte, final State toState) {
        return new TransitionSingleByteMatcher(new ByteMatcher(theByte), toState);
    }

    @Override
    public final Transition createAllBitmaskTransition(final byte bitMask, final State toState) {
        return new TransitionSingleByteMatcher(new AllBitMaskMatcher(bitMask), toState);
    }

    @Override
    public final Transition createAnyBitmaskTransition(final byte bitMask, final State toState) {
        return new TransitionSingleByteMatcher(new AnyBitMaskMatcher(bitMask), toState);
    }
    
    @Override
    public final Transition createSetTransition(final Set<Byte> byteSet, final boolean negated, final State toState) {
        return new TransitionSingleByteMatcher(ByteSetMatcher.buildMatcher(byteSet, negated), toState);
    }

    @Override
    public final Transition createAnyByteTransition(NfaState toState) {
        return new TransitionSingleByteMatcher(new AnyByteMatcher(), toState);
    }

    @Override
    public final Transition createCaseInsensitiveByteTransition(final char Char, final NfaState toState) {
        return new TransitionSingleByteMatcher(new CaseInsensitiveByteMatcher(Char), toState);
    }

    





}
