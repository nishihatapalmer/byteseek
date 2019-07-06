/*
 * Copyright Matt Palmer 2009-2019, All rights reserved.
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
import java.util.*;

import net.byteseek.io.reader.windows.Window;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.bytes.ByteMatcher;
import net.byteseek.utils.ArgUtils;

/**
 * An immutable sequence matcher which matches sequences of other sequence matchers.
 * This could even involve sequences of other SequenceSequenceMatchers arbitrarily
 * nested, and of different lengths.
 * For example, we could have a sequence of bytes, followed by a case insensitive
 * sequence, followed by a fixed gap, followed by a byte set sequence.
 *
 * @author Matt Palmer
 */
public final class SequenceSequenceMatcher extends AbstractSequenceMatcher {

    private final int hashCode;
    private final int[] startPositions;
    private final SequenceMatcher[] matchers;

    private final int totalLength;


    /**
     * Constructs a SequenceSequenceMatcher from a list of {@link SequenceMatcher} objects.
     *
     * @param matchList A list of SequenceMatchers from which to construct this SequenceSequenceMatcher.
     * @throws IllegalArgumentException if the collection is null or empty.
     */
    public SequenceSequenceMatcher(final List<? extends SequenceMatcher> matchList) {
        this(1, matchList);
    }


    /**
     * Constructs a SequenceSequenceMatcher from a repeated list of {@link SequenceMatcher} objects.
     *
     * @param numberOfRepeats   The number of times to repeat the list of SequenceMatchers.
     * @param matcherCollection A collection of (repeated) SequenceMatchers from which to construct this SequenceSequenceMatcher.
     * @throws IllegalArgumentException if the collection is null or empty or contains null elements,
     *                                  or the number to repeat is less than one.
     */
    public SequenceSequenceMatcher(final int numberOfRepeats, final List<? extends SequenceMatcher> matcherCollection) {
        ArgUtils.checkNullOrEmptyCollectionNoNullElements(matcherCollection);
        ArgUtils.checkPositiveInteger(numberOfRepeats);
        if (numberOfRepeats == 1) {
            matchers = matcherCollection.toArray(new SequenceMatcher[matcherCollection.size() * numberOfRepeats]);
            startPositions = new int[matchers.length];
            totalLength = calculateTotalLength(matchers);
        } else {
            int length = matcherCollection.size() * numberOfRepeats;
            final List<SequenceMatcher> allMatchers = new ArrayList<SequenceMatcher>(length);
            for (int count = 0; count < numberOfRepeats; count++) {
                allMatchers.addAll(matcherCollection);
            }
            matchers = allMatchers.toArray(new SequenceMatcher[length]);
            startPositions = new int[matchers.length];
            totalLength = calculateTotalLength(matchers);
        }
        this.hashCode = calculateHash();
    }


    /**
     * Constructs a SequenceSequenceMatcher from an array of SequenceMatchers.
     *
     * @param matchers The array of SequenceMatchers to construct from.
     * @throws IllegalArgumentException if the array is null or empty.
     */
    public SequenceSequenceMatcher(final SequenceMatcher... matchers) {
        this(1, matchers);
    }


    /**
     * Constructs a SequenceSequenceMatcher from an array of SequenceMatcher,
     * repeated a number of times.
     *
     * @param numberOfRepeats The number of times to repeat the array.
     * @param matchArray      The array of SequenceMatchers to construct from.
     * @throws IllegalArgumentException if the array is null or empty, or the
     *                                  number of repeats is less than one.
     */
    public SequenceSequenceMatcher(final int numberOfRepeats, final SequenceMatcher... matchArray) {
        ArgUtils.checkNullOrEmptyArrayNoNullElements(matchArray);
        ArgUtils.checkPositiveInteger(numberOfRepeats);
        if (numberOfRepeats == 1) {
            matchers = matchArray.clone();
            startPositions = new int[matchers.length];
            totalLength = calculateTotalLength(matchers);
        } else {
            final int numberOfMatchers = matchArray.length;
            int length = numberOfMatchers * numberOfRepeats;
            matchers = new SequenceMatcher[length];
            startPositions = new int[matchers.length];
            for (int repeat = 0; repeat < numberOfRepeats; repeat++) {
                System.arraycopy(matchArray, 0, matchers, repeat * numberOfMatchers, numberOfMatchers);
            }
            totalLength = calculateTotalLength(matchers);
        }
        this.hashCode = calculateHash();
    }


    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if the WindowReader is null.
     */
    @Override
    public boolean matches(final WindowReader reader, final long matchPosition) throws IOException {
        final int localTotalLength = totalLength;
        final SequenceMatcher[] localArray = matchers;
        Window window = reader.getWindow(matchPosition);
        int matchPos = 0;
        int matcherIndex = 0;
        // While we have data to read from:
        while (window != null) {
            final int windowStartMatchPos = matchPos;
            final int offset = reader.getWindowOffset(matchPosition + matchPos);
            final int endArrayPos = Math.min(window.length(), offset + localTotalLength - matchPos);
            //final long lastMatchingPosition = window.getWindowPosition() + endArrayPos - 1;
            final byte[] array = window.getArray();
            int arrayCheckPos = offset + matchPos - windowStartMatchPos;
            // While our current matcher starts within the current window 
            while (arrayCheckPos < endArrayPos) {
                final SequenceMatcher matcher = localArray[matcherIndex++];
                final int matcherLength = matcher.length();
                //final int arrayCheckPos = offset + matchPos - windowStartMatchPos;
                // If our matcher fits within the current window, check using the window array:
                if (arrayCheckPos + matcherLength <= endArrayPos) {
                    if (!matcher.matchesNoBoundsCheck(array, arrayCheckPos)) {
                        return false;
                    }
                } else { // the matcher spans two windows, or is at the limit of the final window.
                    if (!matcher.matches(reader, matchPosition + matchPos)) {
                        return false;
                    }
                }
                matchPos += matcherLength;
                arrayCheckPos += matcherLength;
            }
            if (matchPos == localTotalLength) {
                return true;
            }
            window = reader.getWindow(matchPosition + matchPos);
        }
        return false;
    }


    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if the byte array is null.
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        if (matchPosition + totalLength <= bytes.length && matchPosition >= 0) {
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
     *
     * @throws NullPointerException if the byte array is null.
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

    @Override
    public int length() {
        return totalLength;
    }

    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        final StringBuilder regularExpression = new StringBuilder();
        boolean firstMatcher = true;
        for (final SequenceMatcher matcher : matchers) {
            if (!firstMatcher && prettyPrint) {
                regularExpression.append(' ');
            }
            regularExpression.append(matcher.toRegularExpression(prettyPrint));
            firstMatcher = false;
        }
        return regularExpression.toString();
    }

    @Override
    public ByteMatcher getMatcherForPosition(final int position) {
        ArgUtils.checkIndexOutOfBounds(totalLength, position);
        int matcherIndex = Arrays.binarySearch(startPositions, position);
        final int offset;
        if (matcherIndex >= 0) {
            offset = 0;
        } else {
            matcherIndex = -(matcherIndex + 2);
            offset = position - startPositions[matcherIndex];
        }
        return matchers[matcherIndex].getMatcherForPosition(offset);
    }

    @Override
    public int getNumBytesAtPosition(final int position) {
        ArgUtils.checkIndexOutOfBounds(totalLength, position);
        int matcherIndex = Arrays.binarySearch(startPositions, position);
        final int offset;
        if (matcherIndex >= 0) {
            offset = 0;
        } else {
            matcherIndex = -(matcherIndex + 2);
            offset = position - startPositions[matcherIndex];
        }
        return matchers[matcherIndex].getNumBytesAtPosition(offset);
    }

    @Override
    public SequenceSequenceMatcher reverse() {
        final SequenceMatcher[] reversed = new SequenceMatcher[matchers.length];
        int position = matchers.length - 1;
        for (final SequenceMatcher matcher : matchers) {
            reversed[position--] = matcher.reverse();
        }
        return new SequenceSequenceMatcher(reversed);
    }

    @Override
    public SequenceMatcher subsequence(final int beginIndex, final int endIndex) {
        ArgUtils.checkIndexOutOfBounds(totalLength, beginIndex, endIndex);

        // Find the starting matcher and offset into it:
        int beginMatcherIndex, beginOffset = 0;
        beginMatcherIndex = Arrays.binarySearch(startPositions, beginIndex);
        if (beginMatcherIndex < 0) {
            beginMatcherIndex = -(beginMatcherIndex + 2);
            beginOffset       = beginIndex - startPositions[beginMatcherIndex];
        }
        SequenceMatcher startMatcher = matchers[beginMatcherIndex];

        // Find the ending matcher and offset into it.
        // The position of the matcher we want is one less than the endIndex, as it uses an exclusive index.
        // The final end offset must be 1 greater than the last matcher, as it is an exclusive index.
        int endMatcherIndex, endOffset = 1, endPosition = endIndex - 1;
        endMatcherIndex = Arrays.binarySearch(startPositions, endPosition);
        if (endMatcherIndex < 0) {
            endMatcherIndex = -(endMatcherIndex + 2);
            endOffset       = endPosition - startPositions[endMatcherIndex] + 1;
        }
        SequenceMatcher endMatcher = matchers[endMatcherIndex];

        // If there's only one matcher involved, then return a subsequence of it:
        final int numberOfSequenceMatchers = endMatcherIndex - beginMatcherIndex + 1;
        if (numberOfSequenceMatchers == 1) {
            return startMatcher.subsequence(beginOffset, endOffset);
        }

        // Otherwise, build a new SequenceMatcher from the components:
        final SequenceMatcher[] newSequence = new SequenceMatcher[numberOfSequenceMatchers];

        // Add the first matcher:
        newSequence[0] = startMatcher.subsequence(beginOffset, startMatcher.length());

        // Add the last matcher:
        newSequence[numberOfSequenceMatchers - 1] = endMatcher.subsequence(0, endOffset);

        // Any other matchers in the middle are copied across:
        if (numberOfSequenceMatchers > 2) {
            System.arraycopy(matchers, beginMatcherIndex + 1, newSequence, 1, numberOfSequenceMatchers - 2);
        }

        // Forming the new sequence array matcher:
        return new SequenceSequenceMatcher(newSequence);
    }

    @Override
    public SequenceMatcher subsequence(final int beginIndex) {
        return subsequence(beginIndex, length());
    }

    @Override
    public SequenceMatcher repeat(final int numberOfRepeats) {
        ArgUtils.checkPositiveInteger(numberOfRepeats);
        if (numberOfRepeats == 1) {
            return this;
        }
        return new SequenceSequenceMatcher(numberOfRepeats, matchers);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Note: The fundamental sequence of ByteMatchers must be identical for two SequenceSequenceMatchers
     * to be considered equal.  However, we don't care if those ByteMatchers are split up internally in
     * a different way inside each SequenceSequenceMatcher, as this makes no difference to
     * their behaviour.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof SequenceSequenceMatcher) {
            final SequenceSequenceMatcher other = (SequenceSequenceMatcher) obj;
            if (hashCode == other.hashCode && totalLength == other.totalLength) {
                final Iterator<ByteMatcher> thisIterator = iterator();
                final Iterator<ByteMatcher> otherIterator = other.iterator();
                while (thisIterator.hasNext()) {
                    if (!thisIterator.next().equals(otherIterator.next())) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
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
        return getClass().getSimpleName() + '(' + toRegularExpression(true) + ')';
    }

    @Override
    public Iterator<ByteMatcher> iterator() {
        return new SequenceSequenceIterator();
    }


    private int calculateHash() {
        long hash = 31;
        for (final ByteMatcher matcher : this) {
            hash = hash * matcher.hashCode();
        }
        return (int) hash;
    }

    private int calculateTotalLength(final SequenceMatcher[] matchers) {
        int length = 0;
        int matcherIndex = 0;
        for (final SequenceMatcher matcher : matchers) {
            startPositions[matcherIndex++] = length;
            length += matcher.length();
        }
        return length;
    }

    private class SequenceSequenceIterator implements Iterator<ByteMatcher> {

        private int position = 0;
        private int matcherIndex = 0;
        private Iterator<ByteMatcher> iterator = matchers[0].iterator();

        @Override
        public boolean hasNext() {
            return position < totalLength;
        }

        @Override
        public ByteMatcher next() {
            if (hasNext()) {
                if (!iterator.hasNext()) {
                    // Note: this should never result in an ArrayIndexOutOfBoundsException,
                    // unless the calculation of the total length is incorrect, which would be a bug.
                    iterator = matchers[++matcherIndex].iterator();
                }

                position++;
                return iterator.next();
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove byte matchers from SequenceSequenceMatchers");
        }

    }


}
