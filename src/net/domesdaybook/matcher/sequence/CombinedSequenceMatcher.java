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

    private class ByteMatcherIndex {
        public SequenceMatcher matcher;
        public int offset;
        public ByteMatcherIndex(final SequenceMatcher matcher, final int offset) {
            this.matcher = matcher;
            this.offset= offset;
        }
    }

    private List<SequenceMatcher> matchers = new ArrayList<SequenceMatcher>();
    private List<ByteMatcherIndex> byteMatcherForPosition;
    private int length;

    public CombinedSequenceMatcher(List<SequenceMatcher> matchList) {
        matchers.addAll(matchList);
        calculatePositions();
    }
    
    @Override
    public boolean matchesBytes(Bytes reader, long matchFrom) {
       boolean result = true;
        long matchAt = matchFrom;
        final List<SequenceMatcher> localList=matchers;
        for ( int matchIndex = 0, stop=localList.size(); matchIndex < stop; matchIndex++ ) {
            final SequenceMatcher matcher = localList.get( matchIndex );
            if (matcher.matchesBytes(reader, matchAt)) {
                matchAt += matcher.length();
            } else {
                result = false;
                break;
            }
        }
        return result;
    }


    @Override
    public int length() {
        return length;
    }

    @Override
    public String toRegularExpression(boolean prettyPrint) {
        StringBuilder regularExpression = new StringBuilder();
        for ( int matcherIndex = 0, lastMatcher = matchers.size();
            matcherIndex < lastMatcher; matcherIndex++ ) {
           final SequenceMatcher matcher = matchers.get(matcherIndex);
           regularExpression.append( matcher.toRegularExpression( prettyPrint ) );
        }
        return regularExpression.toString();
    }

    private void calculatePositions() {
        length = 0;
        byteMatcherForPosition = new ArrayList<ByteMatcherIndex>();
        for ( int seqIndex = 0, stop=matchers.size(); seqIndex < stop; seqIndex++ ) {
            final SequenceMatcher matcher = matchers.get(seqIndex);
            final int numberOfBytes = matcher.length();
            for (int matcherPos = 0; matcherPos < numberOfBytes; matcherPos++) {
                ByteMatcherIndex index = new ByteMatcherIndex(matcher,matcherPos);
                byteMatcherForPosition.add(index);
            }
            length += numberOfBytes;
        }
    }



    @Override
    public final SingleByteMatcher getByteMatcherForPosition(int position) {
        final ByteMatcherIndex index = byteMatcherForPosition.get(position);
        final SequenceMatcher matcher = index.matcher;
        return matcher.getByteMatcherForPosition(index.offset);
    }


    public List<SequenceMatcher> getMatchers() {
        return matchers;
    }

}
