/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
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
 *  * The names of its contributors may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
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
 */

package net.domesdaybook.matcher.sequence;

import java.io.IOException;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.reader.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.domesdaybook.reader.Window;

/**
 * An immutable sequence matcher which matches sequences of other sequence matchers.
 * For example, we could have a sequence of bytes, followed by a case insensitive
 * sequence, followed by a fixed gap, followed by a byte set sequence.
 * 
 * @author Matt Palmer
 */
public final class SequenceOfSequencesMatcher implements SequenceMatcher {

    private final SequenceMatcher[] matchers;
    private final int length;

    
    /**
     * Constructs a SequenceOfSequencesMatcher from a list of {@link SequenceMatcher} objects.
     *
     * @param matchList A list of SequenceMatchers from which to construct this SequenceOfSequencesMatcher.
     */
    public SequenceOfSequencesMatcher(final Collection<SequenceMatcher> matchList) {
        this(matchList, 1);
    }


    /**
     * Constructs a SequenceOfSequencesMatcher from a repeated list of {@link SequenceMatcher} objects.
     * 
     * @param matchList  A list of (repeated) SequenceMatchers from which to construct this SequenceOfSequencesMatcher.
     * @param numberOfRepeats The number of times to repeat the list of SequenceMatchers.
     * @throws IllegalArgumentException if the list is null or empty, or the number to repeat is less than one.
     */
    public SequenceOfSequencesMatcher(final Collection<SequenceMatcher> matchList, final int numberOfRepeats) {
        if (matchList == null || matchList.isEmpty()) {
            throw new IllegalArgumentException("Null or empty match list passed in to CombinedSequenceMatcher.");
        }
        if (numberOfRepeats < 1) {
            throw new IllegalArgumentException("CombinedSequenceMatcher requires a positive number of repeats.");
        }
        if (numberOfRepeats == 1) {
            matchers = matchList.toArray(new SequenceMatcher[matchList.size() * numberOfRepeats]);
            length = matchers.length;
        } else {
            length = matchList.size() * numberOfRepeats;
            final List<SequenceMatcher> allMatchers = new ArrayList<SequenceMatcher>(length);
            for (int count = 0; count < numberOfRepeats; count++) {
                allMatchers.addAll(matchList);
            }
            matchers = matchList.toArray(new SequenceMatcher[length]);
        }
    }
    
    
    public SequenceOfSequencesMatcher(final SequenceMatcher[] matchArray) {
        this(matchArray, 1);
    }
    
    
    public SequenceOfSequencesMatcher(final SequenceMatcher[] matchArray, final int numberOfRepeats) {
        if (matchArray == null || matchArray.length == 0) {
            throw new IllegalArgumentException("Null or empty match array passed in to CombinedSequenceMatcher.");
        }
        if (numberOfRepeats < 1) {
            throw new IllegalArgumentException("CombinedSequenceMatcher requires a positive number of repeats.");
        }
        if (numberOfRepeats == 1) {
            matchers = matchArray.clone();
            length = matchers.length;
        } else {
            final int numberOfMatchers = matchArray.length;
            length = numberOfMatchers * numberOfRepeats;
            matchers = new SequenceMatcher[length];
            for (int repeat = 0; repeat < numberOfRepeats; repeat++) {
                System.arraycopy(matchArray, 0, matchers, repeat * numberOfMatchers, numberOfMatchers);
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final Reader reader, final long matchPosition) throws IOException {
        final int localLength = length;
        final SequenceMatcher[] localArray = matchers;        
        Window window = reader.getWindow(matchPosition);
        int checkPos = 0;
        int matchIndex = 0;
        while (window != null) {
            final int offset = reader.getWindowOffset(matchPosition + checkPos);
            final int endPos = Math.min(window.length(), offset + localLength - checkPos);
            final byte[] array = window.getArray();
            while (offset + checkPos < endPos) {
                final SequenceMatcher matcher = localArray[matchIndex++];
                final int matcherLength = matcher.length();
                // If our matcher fits within the current window, check using the window:
                if (offset + checkPos + matcherLength <= endPos) {
                    if (!matcher.matchesNoBoundsCheck(array, offset + checkPos)) {
                        return false;
                    }
                } else { // the matcher spans two windows, or is at the limit of the final window.
                    if (!matcher.matches(reader, matchPosition + checkPos)) {
                        return false;
                    }
                }
                checkPos += matcherLength;
            }
            if (checkPos == localLength) {
                return true;
            } else {
                window = reader.getWindow(matchPosition + checkPos);
            }
        }
        return false;
    }   
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        if (matchPosition + length < bytes.length && matchPosition >= 0) {
            int matchAt = matchPosition;
            final SequenceMatcher[] localMatchers = matchers;
            for (final SequenceMatcher matcher : localMatchers) {
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
     */
    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchPosition) {
        int matchAt = matchPosition;
        final SequenceMatcher[] localMatchers = matchers;
        for (final SequenceMatcher matcher : localMatchers) {
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
        final StringBuilder regularExpression = new StringBuilder();
        for (final SequenceMatcher matcher : matchers) {
           regularExpression.append(matcher.toRegularExpression(prettyPrint));
        }
        return regularExpression.toString();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public SingleByteMatcher getMatcherForPosition(final int position) {
        if (position < 0 || position >= length) {
            throw new IndexOutOfBoundsException(
                    String.format("Position %d out of bounds in sequence of length %d",
                                   position, length));            
        }
        int currentPosition = 0;
        for (final SequenceMatcher matcher : matchers) {
            final int matcherLength = matcher.length();
            currentPosition += matcherLength;
            if (position <= currentPosition) {
                final int matcherOffset = position + matcherLength - currentPosition;
                return matcher.getMatcherForPosition(matcherOffset);
            }
        }
        return null; // This should never happen - unsure whether to throw runtimeexception or not.
    }


    /**
     * Returns an array of {@link SequenceMatcher}s this combined matcher matches.
     * 
     * @return An array of SequenceMatchers.
     */
    public SequenceMatcher[] getMatchers() {
        return matchers.clone();
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public SequenceOfSequencesMatcher reverse() {
        final SequenceMatcher[] reversed = new SequenceMatcher[matchers.length];
        int position = matchers.length - 1;
        for (final SequenceMatcher matcher : matchers) {
            reversed[position--] = matcher.reverse();
        }
        return new SequenceOfSequencesMatcher(reversed);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public SequenceMatcher subsequence(final int beginIndex, final int endIndex) {
        // Check it is a valid subsequence:
        if (beginIndex < 0 || endIndex > length || beginIndex >= endIndex) {
            final String message = "Subsequence index %d to %d is out of bounds in a sequence of length %d";
            throw new IndexOutOfBoundsException(String.format(message, beginIndex, endIndex, length));
        }
        
        // Locate info about the start and ending matchers for these index positions:
        SequenceMatcher startMatcher = null, endMatcher = null;
        int startOffset = 0, endLimit = 0;
        int startIndex = 0, lastIndex = 0;
        int currentPosition = 0;
        int startPosition = beginIndex;
        int endPosition = endIndex - 1;
        for (int matcherIndex = 0; matcherIndex < matchers.length; matcherIndex++) {
            final SequenceMatcher matcher = matchers[matcherIndex];
            final int matcherLength = matcher.length();
            currentPosition += matcherLength;
            if (startPosition <= currentPosition) {
                startMatcher = matcher;
                startOffset = beginIndex - (currentPosition - matcherLength);
                startIndex = matcherIndex;
                startPosition = Integer.MAX_VALUE;
            }
            if (endPosition <= currentPosition) {
                endMatcher = matcher;
                endLimit = endIndex - (currentPosition - matcherLength) + 1;
                lastIndex = matcherIndex;
                break;
            }
        }
        
        // If there's only one matcher involved, then return a subsequence of it:
        if (startMatcher == endMatcher) {
            return startMatcher.subsequence(startOffset, endLimit);
        }
        
        // Otherwise, get the possibly truncated start and ending matchers:
        final int newSize = lastIndex - startIndex + 1;
        final SequenceMatcher[] newSequence = new SequenceMatcher[newSize];
        newSequence[0] = startMatcher.subsequence(startOffset, startMatcher.length());
        newSequence[newSize - 1] = endMatcher.subsequence(0, endLimit);
        
        // Any other matchers in the middle are copied across:
        if (newSize > 2) {
            System.arraycopy(matchers, startIndex + 1, newSequence, 1, newSize - 2);
        }
        
        // Forming the new combined sequence matcher:
        return new SequenceOfSequencesMatcher(newSequence);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override    
    public SequenceMatcher repeat(int numberOfRepeats) {
        if (numberOfRepeats < 1) {
            throw new IllegalArgumentException("Number of repeats must be at least one.");
        }
        if (numberOfRepeats == 1) {
            return this;
        }        
        return new SequenceOfSequencesMatcher(matchers, numberOfRepeats);
    }

}
