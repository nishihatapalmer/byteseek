/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.expression.compiler;

import java.util.List;
import java.util.Set;

/**
 *
 * @author matt
 */
public interface NfaInitialFinalStatesBuilder {


    public NfaInitialFinalStates buildSingleByteStates(final byte transitionByte);


    public NfaInitialFinalStates buildAllBitmaskStates(final byte bitMask);


    public NfaInitialFinalStates buildSequenceStates(final List<NfaInitialFinalStates> sequenceStates);


    public NfaInitialFinalStates buildAlternativeStates(final List<NfaInitialFinalStates> alternateStates);


    public NfaInitialFinalStates buildMinToMaxStates(final int minRepeat, final int maxRepeat, final NfaInitialFinalStates repeatedAutomata);


    public NfaInitialFinalStates buildZeroToManyStates(final NfaInitialFinalStates zeroToManyStates);


    public NfaInitialFinalStates buildOneToManyStates(final NfaInitialFinalStates oneToManyStates);


    public NfaInitialFinalStates buildMinToManyStates(final int minRepeat, final NfaInitialFinalStates repeatedAutomata);


    public NfaInitialFinalStates buildSetStates(final Set<Byte> byteSet, final boolean negated);

    
    public NfaInitialFinalStates buildOptionalStates(final NfaInitialFinalStates optionalStates);


    public NfaInitialFinalStates buildCaseSensitiveStringStates(final String str);


    public NfaInitialFinalStates buildCaseInsensitiveStringStates(final String str);


    public NfaInitialFinalStates buildAnyByteStates();

}
