/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.sequence;

import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.reader.ByteReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * An immutable sequence matcher which matches sequences of other sequence matchers.
 * For example, we could have a sequence of bytes, followed by a case insensitive
 * sequence, followed by a fixed gap.
 * 
 * @author Matt Palmer
 */
public final class CombinedSequenceMatcher implements SequenceMatcher {

    private final List<SequenceMatcher> matchers;
    private final List<ByteMatcherIndex> byteMatcherForPosition = new ArrayList<ByteMatcherIndex>();
    private final int length;


    /**
     * Constructs a CombinedSequenceMatcher from a list of {@link SequenceMatcher} objects.
     *
     * @param matchList A list of SequenceMatchers from which to construct this CombinedSequenceMatcher.
     */
    public CombinedSequenceMatcher(final Collection<SequenceMatcher> matchList) {
        this(matchList, 1);
    }


    /**
     * Constructs a CombinedSequenceMatcher from a repeated list of {@link SequenceMatcher} objects.
     * @param matchList  A list of (repeated) SequenceMatchers from which to construct this CombinedSequenceMatcher.
     * @param numberOfRepeats The number of times to repeat the list of SequenceMatchers.
     * @throws IllegalArgumentException if the list is null or empty, or the number to repeat is less than one.
     */
    public CombinedSequenceMatcher(final Collection<SequenceMatcher> matchList, final int numberOfRepeats) {
        if (matchList == null || matchList.isEmpty()) {
            throw new IllegalArgumentException("Null or empty match list passed in to CombinedSequenceMatcher.");
        }
        if (numberOfRepeats < 1) {
            throw new IllegalArgumentException("CombinedSequenceMatcher requires a positive number of repeats.");
        }
        matchers = new ArrayList<SequenceMatcher>(matchList);
        for (int count = 0; count < numberOfRepeats; count++) {
            matchers.addAll(matchList);
        }
        length = calculatePositions();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(ByteReader reader, long matchFrom) {
        boolean result = true;
        long matchAt = matchFrom;
        final List<SequenceMatcher> localList=matchers;
        for ( int matchIndex = 0, stop=localList.size(); matchIndex < stop; matchIndex++ ) {
            final SequenceMatcher matcher = localList.get( matchIndex );
            if (matcher.matches(reader, matchAt)) {
                matchAt += matcher.length();
            } else {
                result = false;
                break;
            }
        }
        return result;
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
    public String toRegularExpression(boolean prettyPrint) {
        StringBuilder regularExpression = new StringBuilder();
        for ( int matcherIndex = 0, lastMatcher = matchers.size();
            matcherIndex < lastMatcher; matcherIndex++ ) {
           final SequenceMatcher matcher = matchers.get(matcherIndex);
           regularExpression.append(matcher.toRegularExpression(prettyPrint));
        }
        return regularExpression.toString();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public SingleByteMatcher getByteMatcherForPosition(int position) {
        final ByteMatcherIndex index = byteMatcherForPosition.get(position);
        final SequenceMatcher matcher = index.matcher;
        return matcher.getByteMatcherForPosition(index.offset);
    }


    /**
     *
     * @return The list of {@link SequenceMatcher} objects this combined matcher matches.
     */
    public List<SequenceMatcher> getMatchers() {
        return matchers;
    }

    /**
     * Calculates an index of which {@link SequenceMatcher} to use at which
     * position in the combined sequence matcher.
     * 
     * @return The length of the combined sequence matcher.
     */
    private int calculatePositions() {
        int len = 0;
        for ( int seqIndex = 0, stop=matchers.size(); seqIndex < stop; seqIndex++ ) {
            final SequenceMatcher matcher = matchers.get(seqIndex);
            final int numberOfBytes = matcher.length();
            for (int matcherPos = 0; matcherPos < numberOfBytes; matcherPos++) {
                ByteMatcherIndex index = new ByteMatcherIndex(matcher, matcherPos);
                byteMatcherForPosition.add(index);
            }
            len += numberOfBytes;
        }
        return len;
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public CombinedSequenceMatcher reverse() {
        final Deque<SequenceMatcher> reversed = new LinkedList<SequenceMatcher>();
        for (SequenceMatcher matcher : matchers) {
            final SequenceMatcher newMatcher = matcher.reverse();
            reversed.addFirst(newMatcher);
        }
        return new CombinedSequenceMatcher(reversed);
    }


    /**
     * A simple class to hold a SequenceMatcher and the offset into it for a
     * given position in the CombinedSequenceMatcher.
     */
    private final class ByteMatcherIndex {
        public final SequenceMatcher matcher;
        public final int offset;
        ByteMatcherIndex(final SequenceMatcher matcher, final int offset) {
            this.matcher = matcher;
            this.offset= offset;
        }
    }

}
