/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.sequence;

import java.util.ArrayList;
import java.util.List;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.reader.ByteReader;

/**
 * An immutable class which matches a sequence of {@link SingleByteMatcher} objects.
 *
 * @author Matt Palmer
 */
public final class SingleByteSequenceMatcher implements SequenceMatcher {

    private final List<SingleByteMatcher> matcherSequence = new ArrayList<SingleByteMatcher>();
    private final int length;


    /**
     * Constructs a SingleByteSequenceMatcher from a list of {@link SingleByteMatcher} objects.
     *
     * @param sequence A list of SingleByteMatchers to construct this sequence matcher from.
     * @throws IllegalArgumentException if the list is null or empty.
     */
    public SingleByteSequenceMatcher(final List<SingleByteMatcher> sequence) {
        if (sequence == null || sequence.isEmpty()) {
            throw new IllegalArgumentException("Null or empty sequence passed in to SingleByteSequenceMatcher.");
        }
        this.matcherSequence.addAll(sequence);
        this.length = this.matcherSequence.size();
    }


    /**
     * Constructs a SingleByteSequenceMatcher from a single {@link SingleByteMatcher} object.
     *
     * @param matcher The SingleByteMatcher to construct this sequence matcher from.
     * @throws IllegalArgumentException if the matcher is null.
     */
    public SingleByteSequenceMatcher(final SingleByteMatcher matcher) {
        if (matcher == null) {
            throw new IllegalArgumentException("Null matcher passed in to SingleByteSequenceMatcher.");
        }
        this.matcherSequence.add(matcher);
        this.length = 1;
    }


    /**
     * Constructs a SingleByteSequenceMatcher from a repeated {@link SingleByteMatcher} object.
     *
     * @param matcher The SingleByteMatcher to construct this sequence matcher from.
     * @param numberOfRepeats The number of times to repeat the SingleByteMatcher.
     * @throws IllegalArgumentException if the matcher is null or the number of repeats is less than one.
     */
    public SingleByteSequenceMatcher(final SingleByteMatcher matcher, final int numberOfMatchers) {
        if (matcher == null) {
            throw new IllegalArgumentException("Null matcher passed in to SingleByteSequenceMatcher.");
        }
        if (numberOfMatchers < 1) {
            throw new IllegalArgumentException("SingleByteSequenceMatcher requires a positive number of matchers.");
        }
        length = numberOfMatchers;
        for (int count = 0; count < numberOfMatchers; count++) {
            this.matcherSequence.add(matcher);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final ByteReader reader, final long matchFrom) {
        boolean result = true;
        final List<SingleByteMatcher> matchList = this.matcherSequence;
        final int localStop = length;
        for (int byteIndex = 0; result && byteIndex < localStop; byteIndex++) {
            final SingleByteMatcher byteMatcher = matchList.get(byteIndex);
            final byte byteRead = reader.readByte(matchFrom + byteIndex);
            result = byteMatcher.matches(byteRead);
        }
        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public SingleByteMatcher getByteMatcherForPosition(final int position) {
        return matcherSequence.get(position);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int length() {
        return length;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        StringBuilder builder = new StringBuilder();
        for (SingleByteMatcher matcher : matcherSequence) {
            builder.append(matcher.toRegularExpression(prettyPrint));
        }
        return builder.toString();
    }

}
