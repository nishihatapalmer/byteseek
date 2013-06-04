/*
 * Copyright Matt Palmer 2009-2012, All rights reserved.
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

package net.byteseek.matcher.sequence;

import java.io.IOException;

import net.byteseek.io.reader.Window;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.bytes.ByteMatcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An immutable sequence matcher which matches sequences of other sequence matchers.
 * For example, we could have a sequence of bytes, followed by a case insensitive
 * sequence, followed by a fixed gap, followed by a byte set sequence.
 * 
 * @author Matt Palmer
 */
public final class SequenceArrayMatcher implements SequenceMatcher {

    private final SequenceMatcher[] matchers;
    private final int totalLength;

    
    /**
     * Constructs a SequenceArrayMatcher from a list of {@link SequenceMatcher} objects.
     *
     * @param matchList A list of SequenceMatchers from which to construct this SequenceArrayMatcher.
     * @throws IllegalArgumentException if the collection is null or empty.
     */
    public SequenceArrayMatcher(final Collection<? extends SequenceMatcher> matchList) {
        this(matchList, 1);
    }


    /**
     * Constructs a SequenceArrayMatcher from a repeated list of {@link SequenceMatcher} objects.
     * 
     * @param matcherCollection  A collection of (repeated) SequenceMatchers from which to construct this SequenceArrayMatcher.
     * @param numberOfRepeats The number of times to repeat the list of SequenceMatchers.
     * @throws IllegalArgumentException if the collection is null or empty, or the number to repeat is less than one.
     */
    public SequenceArrayMatcher(final Collection<? extends SequenceMatcher> matcherCollection, final int numberOfRepeats) {
        if (matcherCollection == null || matcherCollection.isEmpty()) {
            throw new IllegalArgumentException("Null or empty match list passed in to SequenceArrayMatcher.");
        }
        if (numberOfRepeats < 1) {
            throw new IllegalArgumentException("SequenceArrayMatcher requires a positive number of repeats.");
        }
        if (numberOfRepeats == 1) {
            matchers = matcherCollection.toArray(new SequenceMatcher[matcherCollection.size() * numberOfRepeats]);
            totalLength = calculateTotalLength(matchers);
        } else {
            int length = matcherCollection.size() * numberOfRepeats;
            final List<SequenceMatcher> allMatchers = new ArrayList<SequenceMatcher>(length);
            for (int count = 0; count < numberOfRepeats; count++) {
                allMatchers.addAll(matcherCollection);
            }
            matchers = matcherCollection.toArray(new SequenceMatcher[length]);
            totalLength = calculateTotalLength(matchers);
        }
    }
    
    
    private int calculateTotalLength(SequenceMatcher[] matchers) {
		int totalLength = 0;
		for (final SequenceMatcher matcher : matchers) {
			totalLength += matcher.length();
		}
		return totalLength;
	}


	/**
     * Constructs a SequenceArrayMatcher from an array of SequenceMatchers.
     * 
     * @param matchArray The array of SequenceMatchers to construct from.
     * @throws IllegalArgumentException if the array is null or empty.
     */
    public SequenceArrayMatcher(final SequenceMatcher[] matchArray) {
        this(matchArray, 1);
    }
    
    
    /**
     * Constructs a SequenceArrayMatcher from an array of SequenceMatcher,
     * repeated a number of times.
     * 
     * @param matchArray The array of SequenceMatchers to construct from.
     * @param numberOfRepeats The number of times to repeat the array.
     * @throws IllegalArgumentException if the array is null or empty, or the
     *         number of repeats is less than one.
     */
    public SequenceArrayMatcher(final SequenceMatcher[] matchArray, final int numberOfRepeats) {
        if (matchArray == null || matchArray.length == 0) {
            throw new IllegalArgumentException("Null or empty match array passed in to SequenceArrayMatcher.");
        }
        if (numberOfRepeats < 1) {
            throw new IllegalArgumentException("SequenceArrayMatcher requires a positive number of repeats.");
        }
        if (numberOfRepeats == 1) {
            matchers = matchArray.clone();
            totalLength = calculateTotalLength(matchers);
        } else {
            final int numberOfMatchers = matchArray.length;
            int length = numberOfMatchers * numberOfRepeats;
            matchers = new SequenceMatcher[length];
            for (int repeat = 0; repeat < numberOfRepeats; repeat++) {
                System.arraycopy(matchArray, 0, matchers, repeat * numberOfMatchers, numberOfMatchers);
            }
            totalLength = calculateTotalLength(matchers);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final WindowReader reader, final long matchPosition) throws IOException {
        final int localTotalLength = totalLength;
        final SequenceMatcher[] localArray = matchers;        
        Window window = reader.getWindow(matchPosition);
        int checkPos = 0;
        int matchIndex = 0;
        while (window != null) {
            final int offset = reader.getWindowOffset(matchPosition + checkPos);
            final int endPos = Math.min(window.length(), offset + localTotalLength - checkPos);
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
            if (checkPos == localTotalLength) {
                return true;
            }
            window = reader.getWindow(matchPosition + checkPos);
        }
        return false;
    }   
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        if (matchPosition + totalLength < bytes.length && matchPosition >= 0) {
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
        return totalLength;
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
    public ByteMatcher getMatcherForPosition(final int position) {
        if (position < 0 || position >= totalLength) {
            throw new IndexOutOfBoundsException(
                    String.format("Position %d out of bounds in sequence of length %d",
                                   position, totalLength));            
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
        
        //TODO: throw Runtime, rewrite method...?
        final String badness = "A ByteMatcher for position %d in a SequenceArrayMatcher of length %d could not be retrieved.  This should not happen; there is a bug.  Please report this to the byteseek developers.";
        throw new RuntimeException(String.format(badness, position, totalLength));
    }


    /**
     * Returns an array of {@link SequenceMatcher}s this sequence array matcher matches.
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
    public SequenceArrayMatcher reverse() {
        final SequenceMatcher[] reversed = new SequenceMatcher[matchers.length];
        int position = matchers.length - 1;
        for (final SequenceMatcher matcher : matchers) {
            reversed[position--] = matcher.reverse();
        }
        return new SequenceArrayMatcher(reversed);
    }

    
    /**
     * {@inheritDoc}
     */
    // Suppress warnings about null in this method.  Eclipse believes that the startMatcher or
    // the endMatcher could be null at the end of the loop.  This can only be true if there is a bug.
    // It should not happen otherwise, as we already test that the beginIndex and endIndex are within
    // the bounds of the SequenceArrayMatcher total length - the sum of all the SequenceMatchers in it.
    // Therefore, a failure to find a SequenceMatcher within those bounds would represent a genuine 
    // programming error somewhere, which could then result in a NullPointerException thrown in this method.
    @SuppressWarnings("null")
	@Override
    public SequenceMatcher subsequence(final int beginIndex, final int endIndex) {
        // Check it is a valid subsequence:
        if (beginIndex < 0 || endIndex > totalLength || beginIndex >= endIndex) {
            final String message = "Subsequence index %d to %d is out of bounds in a sequence of length %d";
            throw new IndexOutOfBoundsException(String.format(message, beginIndex, endIndex, totalLength));
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
        
        // Forming the new sequence array matcher:
        return new SequenceArrayMatcher(newSequence);
    }

    
    /**
     * {@inheritDoc}
     */  
    @Override
    public SequenceMatcher subsequence(final int beginIndex) {
        return subsequence(beginIndex, length());
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
        return new SequenceArrayMatcher(matchers, numberOfRepeats);
    }
    
    
    /**
     * Returns a string representation of this matcher.  The format is subject
     * to change, but it will generally return the name of the matching class
     * and a regular expression defining the bytes matched by the matcher.
     * 
     * @return A string representing this matcher.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + toRegularExpression(true) + ")";
    }    

}
