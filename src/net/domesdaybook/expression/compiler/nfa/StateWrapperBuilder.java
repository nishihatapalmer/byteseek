/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.expression.compiler.nfa;

import java.util.List;
import java.util.Set;
import net.domesdaybook.automata.transition.TransitionFactory;

/**
 *
 * @author Matt Palmer
 */
public interface StateWrapperBuilder {

    public void setTransitionFactory(final TransitionFactory transitionFactory);


    public void setStateBuilder(final StateBuilder stateBuilder);

    
    public StateWrapper buildSingleByteStates(final byte transitionByte);


    public StateWrapper buildAllBitmaskStates(final byte bitMask);

    
    public StateWrapper buildAnyBitmaskStates(final byte bitMask);


    public StateWrapper buildSequenceStates(final List<StateWrapper> sequenceStates);


    public StateWrapper buildAlternativeStates(final List<StateWrapper> alternateStates);


    public StateWrapper buildMinToMaxStates(final int minRepeat, final int maxRepeat, final StateWrapper repeatedAutomata);


    public StateWrapper buildZeroToManyStates(final StateWrapper zeroToManyStates);


    public StateWrapper buildOneToManyStates(final StateWrapper oneToManyStates);


    public StateWrapper buildMinToManyStates(final int minRepeat, final StateWrapper repeatedAutomata);


    public StateWrapper buildRepeatedStates(final int repeatNumber, final StateWrapper repeatedAutomta);

    
    public StateWrapper buildSetStates(final Set<Byte> byteSet, final boolean negated);


    public StateWrapper buildRepeatedOptionalStates(final int numberOptional, final StateWrapper optionalState);


    public StateWrapper buildOptionalStates(final StateWrapper optionalStates);


    public StateWrapper buildCaseSensitiveStringStates(final String str);


    public StateWrapper buildCaseInsensitiveStringStates(final String str);


    public StateWrapper buildAnyByteStates();

}
