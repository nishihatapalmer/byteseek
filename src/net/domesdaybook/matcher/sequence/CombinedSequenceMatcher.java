/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 *
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 
 *  * Neither the "byteseek" name nor the names of its contributors 
 *    may be used to endorse or promote products derived from this software 
 *    without specific prior written permission. 
 *  
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.domesdaybook.matcher.sequence;

import java.io.IOException;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.reader.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import net.domesdaybook.reader.Window;

/**
 * An immutable sequence matcher which matches sequences of other sequence matchers.
 * For example, we could have a sequence of bytes, followed by a case insensitive
 * sequence, followed by a fixed gap, followed by a byte set sequence.
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
        for (int count = 1; count < numberOfRepeats; count++) {
            matchers.addAll(matchList);
        }
        length = indexAllSequenceMatchers();
    }



    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public boolean matches(final Reader reader, final long matchFrom) throws IOException {
        final int localLength = length;
        final List<SequenceMatcher> localList = matchers;        
        Window window = reader.getWindow(matchFrom);
        int checkPos = 0;
        int matchIndex = 0;
        while (window != null) {
            final int offset = reader.getWindowOffset(matchFrom + checkPos);
            final int endPos = Math.min(window.getLimit(), offset + localLength - checkPos);
            final byte[] array = window.getArray();
            while (offset + checkPos < endPos) {
                final SequenceMatcher matcher = localList.get(matchIndex++);
                final int matcherLength = matcher.length();
                // If our matcher fits within the current window, check using the window:
                if (offset + checkPos + matcherLength <= endPos) {
                    if (!matcher.matchesNoBoundsCheck(array, offset + checkPos)) {
                        return false;
                    }
                } else { // the matcher spans two windows, or is at the limit of the final window.
                    if (!matcher.matches(reader, matchFrom + checkPos)) {
                        return false;
                    }
                }
                checkPos += matcherLength;
            }
            if (checkPos == localLength) {
                return true;
            } else {
                window = reader.getWindow(matchFrom + checkPos);
            }
        }
        return false;
    }   
    
    
    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchFrom) {
        if (matchFrom + length < bytes.length && matchFrom >= 0) {
            int matchAt = matchFrom;
            final List<SequenceMatcher> localList = matchers;
            for (int matchIndex = 0, stop=localList.size(); matchIndex < stop; matchIndex++) {
                final SequenceMatcher matcher = localList.get(matchIndex);
                // Don't need to do a bounds check here, as we already have above.
                if (matcher.matchesNoBoundsCheck(bytes, matchAt)) {
                    matchAt += matcher.length();
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }    

    
    
    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchFrom) {
        int matchAt = matchFrom;
        final List<SequenceMatcher> localList = matchers;
        for (int matchIndex = 0, stop=localList.size(); matchIndex < stop; matchIndex++) {
            final SequenceMatcher matcher = localList.get(matchIndex);
            if (matcher.matchesNoBoundsCheck(bytes, matchAt)) {
                matchAt += matcher.length();
            } else {
                return false;
            }
        }
        return true;
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
    public SingleByteMatcher getByteMatcherForPosition(final int position) {
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
    private int indexAllSequenceMatchers() {
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
    private final static class ByteMatcherIndex {
        public final SequenceMatcher matcher;
        public final int offset;
        ByteMatcherIndex(final SequenceMatcher matcher, final int offset) {
            this.matcher = matcher;
            this.offset= offset;
        }
    }

}
