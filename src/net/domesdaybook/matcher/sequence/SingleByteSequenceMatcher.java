/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.sequence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.reader.Window;

/**
 * An immutable class which matches a sequence of {@link SingleByteMatcher} objects.
 *
 * @author Matt Palmer
 */
public final class SingleByteSequenceMatcher implements SequenceMatcher {

    private final List<SingleByteMatcher> matcherSequence;
    private final int length;


    /**
     * Constructs a SingleByteSequenceMatcher from a list of {@link SingleByteMatcher} objects.
     *
     * @param sequence A list of SingleByteMatchers to construct this sequence matcher from.
     * @throws IllegalArgumentException if the list is null or empty.
     */
    public SingleByteSequenceMatcher(final Collection<SingleByteMatcher> sequence) {
        if (sequence == null || sequence.isEmpty()) {
            throw new IllegalArgumentException("Null or empty sequence passed in to SingleByteSequenceMatcher.");
        }
        this.matcherSequence = new ArrayList<SingleByteMatcher>(sequence);
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
        this.matcherSequence = new ArrayList<SingleByteMatcher>(1);
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
        this.matcherSequence = new ArrayList<SingleByteMatcher>(length);
        for (int count = 0; count < numberOfMatchers; count++) {
            this.matcherSequence.add(matcher);
        }
    }


    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public final boolean matches(final Reader reader, final long matchFrom)
            throws IOException {
        final Window window = reader.getWindow(matchFrom);
        if (window != null) {
            final int localLength = length;            
            final int offset = reader.getWindowOffset(matchFrom);
            if (offset + localLength <= window.getLimit()) {
                return matchesNoBoundsCheck(window.getArray(), offset);
            }
            if (matchFrom + localLength <= reader.length()) {
                return matchesNoBoundsCheck(reader, matchFrom);
            }
        }
        return false;
    }    
    
    
    /**
     * {@inheritDoc}
     * 
     * Note: will return false if access is outside the byte array.
     *       It will not throw an IndexOutOfBoundsException.
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchFrom) {
        final int localStop = length;
        if (matchFrom + localStop < bytes.length && matchFrom >= 0) {
            final List<SingleByteMatcher> matchList = this.matcherSequence;
            for (int byteIndex = 0; byteIndex < localStop; byteIndex++) {
                final SingleByteMatcher byteMatcher = matchList.get(byteIndex);
                final byte byteRead = bytes[matchFrom + byteIndex];
                if (!byteMatcher.matches(byteRead)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchesNoBoundsCheck(final Reader reader, final long matchFrom) 
            throws IOException {
        final List<SingleByteMatcher> matchList = this.matcherSequence;
        final int localStop = length;
        for (int byteIndex = 0; byteIndex < localStop; byteIndex++) {
            final SingleByteMatcher byteMatcher = matchList.get(byteIndex);
            final byte byteRead = reader.readByte(matchFrom + byteIndex);
            if (!byteMatcher.matches(byteRead)) {
                return false;
            }
        }
        return true;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchFrom) {
        final List<SingleByteMatcher> matchList = this.matcherSequence;
        final int localStop = length;
        for (int byteIndex = 0; byteIndex < localStop; byteIndex++) {
            final SingleByteMatcher byteMatcher = matchList.get(byteIndex);
            final byte byteRead = bytes[matchFrom + byteIndex];
            if (!byteMatcher.matches(byteRead)) {
                return false;
            }
        }
        return true;        
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
    public SingleByteSequenceMatcher reverse() {
        final List<SingleByteMatcher> newList = new ArrayList<SingleByteMatcher>(matcherSequence);
        Collections.reverse(newList);
        return new SingleByteSequenceMatcher(newList);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        final StringBuilder builder = new StringBuilder();
        for (final SingleByteMatcher matcher : matcherSequence) {
            builder.append(matcher.toRegularExpression(prettyPrint));
        }
        return builder.toString();
    }


}
