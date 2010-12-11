/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.matcher.sequence;

import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.reader.Bytes;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author matt
 */
public class CombinedSequenceMatcher implements SequenceMatcher {

    private final List<SequenceMatcher> matchers = new ArrayList<SequenceMatcher>();
    private final List<ByteMatcherIndex> byteMatcherForPosition = new ArrayList<ByteMatcherIndex>();
    private final int length;

    private final class ByteMatcherIndex {
        public final SequenceMatcher matcher;
        public final int offset;
        ByteMatcherIndex(final SequenceMatcher matcher, final int offset) {
            this.matcher = matcher;
            this.offset= offset;
        }
    }
    
    public CombinedSequenceMatcher(final List<SequenceMatcher> matchList) {
        matchers.addAll(matchList);
        length = calculatePositions();
    }

    private int calculatePositions() {
        int len = 0;
        for ( int seqIndex = 0, stop=matchers.size(); seqIndex < stop; seqIndex++ ) {
            final SequenceMatcher matcher = matchers.get(seqIndex);
            final int numberOfBytes = matcher.length();
            for (int matcherPos = 0; matcherPos < numberOfBytes; matcherPos++) {
                ByteMatcherIndex index = new ByteMatcherIndex(matcher,matcherPos);
                byteMatcherForPosition.add(index);
            }
            len += numberOfBytes;
        }
        return len;
    }


    @Override
    public final boolean matches(Bytes reader, long matchFrom) {
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


    @Override
    public final int length() {
        return length;
    }

    
    @Override
    public final String toRegularExpression(boolean prettyPrint) {
        StringBuilder regularExpression = new StringBuilder();
        for ( int matcherIndex = 0, lastMatcher = matchers.size();
            matcherIndex < lastMatcher; matcherIndex++ ) {
           final SequenceMatcher matcher = matchers.get(matcherIndex);
           regularExpression.append(matcher.toRegularExpression(prettyPrint));
        }
        return regularExpression.toString();
    }


    @Override
    public final SingleByteMatcher getByteMatcherForPosition(int position) {
        final ByteMatcherIndex index = byteMatcherForPosition.get(position);
        final SequenceMatcher matcher = index.matcher;
        return matcher.getByteMatcherForPosition(index.offset);
    }


    // package private getter for unit testing only.
    List<SequenceMatcher> getMatchers() {
        return matchers;
    }

}
