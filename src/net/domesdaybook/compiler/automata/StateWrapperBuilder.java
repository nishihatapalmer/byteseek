/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.compiler.automata;

import java.util.List;
import java.util.Set;

/**
 * An interface for classes which build automata, 
 * wrapped in {@link StateWrapper} objects.
 *
 * @author Matt Palmer
 */
public interface StateWrapperBuilder {

    /**
     * Builds a simple automata with a transition on a single byte value.
     *
     * @param transitionByte The byte value to transition on.
     * @return An automata with a transition on the transitionByte.
     */
    public StateWrapper buildSingleByteStates(final byte transitionByte);


    /**
     * Builds a simple automata with a transition on a bitmask,
     * where all bits must match.
     *
     * @param bitMask The bitmask to match all bits of.
     * @return An automata with a transition on the all-bits bitmask.
     */
    public StateWrapper buildAllBitmaskStates(final byte bitMask);


    /**
     * Builds a simple automata with a transition on a bitmask,
     * where any of the bits can match.
     *
     * @param bitMask The bitmask to match any bits of.
     * @return An automata with a transition on the any-bits bitmask.
     */
    public StateWrapper buildAnyBitmaskStates(final byte bitMask);


    /**
     * Builds an automata by joining the list of automata passed in
     * as a sequence.
     *
     * @param sequenceStates A list of automata to join into a sequence.
     * @return An automata which is a sequence of all the automata passed in.
     */
    public StateWrapper buildSequenceStates(final List<StateWrapper> sequenceStates);


    /**
     * Builds an automata by joining the list of automata passed in
     * as a set of alternatives.
     *
     * @param alternateStates A list of automata to join into a set of alternatives.
     * @return An automata which allows for a set of alternatives.
     */
    public StateWrapper buildAlternativeStates(final List<StateWrapper> alternateStates);


    /**
     * Builds an automata which repeats the automata passed in from a minimum
     * number of repeats up to a maximum number of repeats.
     *
     * @param minRepeat The minimum number of repeats for the automata.
     * @param maxRepeat The maximum number of repeats for the automata.
     * @param repeatedAutomata The automata to repeat.
     * @return An automata which repeats from min to max times.
     */
    public StateWrapper buildMinToMaxStates(final int minRepeat, final int maxRepeat, final StateWrapper repeatedAutomata);


    /**
     * Builds an automata which repeats from zero to many times.
     *
     * @param zeroToManyStates The automata to repeat from zero to many times.
     * @return An automata which repeats from zero to many times.
     */
    public StateWrapper buildZeroToManyStates(final StateWrapper zeroToManyStates);


    /**
     * Builds an automata which repeats from one to many times.
     *
     * @param zeroToManyStates The automata to repeat from one to many times.
     * @return An automata which repeats from one to many times.
     */
    public StateWrapper buildOneToManyStates(final StateWrapper oneToManyStates);


    /**
     * Builds an automata which repeats from a minimum number of times to many.
     *
     * @param minRepeat The minimum number of repeats.
     * @param repeatedAutomata The automata which can repeat from min to many times.
     * @return An automata which repeats from a minimum number of times to many.
     */
    public StateWrapper buildMinToManyStates(final int minRepeat, final StateWrapper repeatedAutomata);


    /**
     * Builds an automata which repeats a given automata a defined number of times.
     *
     * @param repeatNumber The number of times to repeat the automata.
     * @param repeatedAutomta The automata to repeat.
     * @return An automata which repeats the automata passed in a defined number of times.
     */
    public StateWrapper buildRepeatedStates(final int repeatNumber, final StateWrapper repeatedAutomta);


    /**
     * Builds a simple automata with a transition on the set of bytes passed in,
     * (which may be negated).
     *
     * @param byteSet The set of bytes to transition on.
     * @param negated Whether the set of bytes should be inverted or not.
     * @return An automata which transitions on the (negated) set of bytes passed in.
     */
    public StateWrapper buildSetStates(final Set<Byte> byteSet, final boolean negated);


    /**
     * Builds an automata consisting of a defined number of optional automata.
     *
     * @param numberOptional The number of optional automata to repeat.
     * @param optionalState The automata to create repeat optionally.
     * @return An automata which optionally repeats the automata passed in a defined number of times.
     */
    public StateWrapper buildRepeatedOptionalStates(final int numberOptional, final StateWrapper optionalState);


    /**
     * Builds an automata which is optional.
     *
     * @param optionalStates The automata to make optional.
     * @return An automata which is an optional version of the automata passed in.
     */
    public StateWrapper buildOptionalStates(final StateWrapper optionalStates);


    /**
     * Builds an automata which transitions on the string passed in,
     * matching the bytes as ASCII characters case sensitively.
     *
     * @param str The ASCII string to build the automata from.
     * @return An automata which matches the string passed in case sensitively.
     */
    public StateWrapper buildCaseSensitiveStringStates(final String str);

    
    /**
     * Builds an automata which transitions on the string passed in,
     * matching the bytes as ASCII characters case insensitively.
     *
     * @param str The ASCII string to build the automata from.
     * @return An automata which matches the string passed in case insensitively.
     */
    public StateWrapper buildCaseInsensitiveStringStates(final String str);


    /**
     * Builds a simple automata with a transition on any byte.
     *
     * @return An automata which transitions on any byte.
     */
    public StateWrapper buildAnyByteStates();

}
