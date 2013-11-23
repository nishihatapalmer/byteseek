/*
 * Copyright Matt Palmer 2009-2013, All rights reserved.
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.byteseek.io.reader.Window;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.bytes.ByteMatcher;
import net.byteseek.object.ArgUtils;

/**
 * An immutable class which matches a sequence of {@link ByteMatcher} objects.
 * This allows us to match sequences where any position in the sequence could
 * match multiple bytes, for example (using the byteseek regular expression syntax):
 * 
 * <p/><code>
 * 'begin:' [00 ff] \d \d \d \s 'end.'
 * </code><p/>
 * 
 * would match a string starting with 'begin:', followed by either byte 00 or byte ff, then three digits and
 * some whitespace, followed by 'end.'.
 * 
 * @author Matt Palmer
 */
public final class ByteMatcherArrayMatcher implements SequenceMatcher {

    private final ByteMatcher[] matchers;
    private final int length;

    /****************
     * Constructors *
     ***************/

    /**
     * Constructs a ByteMatcherArrayMatcher from a collection of {@link ByteMatcher} objects.
     * <p>
     * You should use a collection which gives a definite order to its elements, such as a List,
     * or a LinkedHashMap.
     *
     * @param sequence A list of SingleByteMatchers to construct this sequence matcher from.
     * @throws IllegalArgumentException if the list is null or empty or any elements in the collection are null.
     */
    public ByteMatcherArrayMatcher(final Collection<? extends ByteMatcher> sequence) {
        ArgUtils.checkNullOrEmptyCollectionNoNullElements(sequence);
        matchers = sequence.toArray(new ByteMatcher[0]);
        this.length = this.matchers.length;
    }

    
    /**
     * Constructs a ByteMatcherArrayMatcher from an array of {@link ByteMatcher}
     * objects.
     * 
     * @param sequence An array of SingleByteMatchers to construct this sequence matcher from.
     * @throws IllegalArgumentException if the array is null or empty or any element of it is null.
     */
    public ByteMatcherArrayMatcher(final ByteMatcher[] sequence) {
        ArgUtils.checkNullOrEmptyArrayNoNullElements(sequence);
        this.matchers = sequence.clone();
        this.length = this.matchers.length;
    }
    
    
    /**
     * Constructs a ByteMatcherArrayMatcher from a single {@link ByteMatcher} object.
     *
     * @param matcher The ByteMatcher to construct this sequence matcher from.
     * @throws IllegalArgumentException if the matcher is null.
     */
    public ByteMatcherArrayMatcher(final ByteMatcher matcher) {
        ArgUtils.checkNullObject(matcher);
        this.matchers = new ByteMatcher[] {matcher};
        this.length = 1;
    }


    /**
     * Constructs a ByteMatcherArrayMatcher from a repeated {@link ByteMatcher} object.
     *
     * @param matcher The ByteMatcher to construct this sequence matcher from.
     * @param numberOfMatchers 
     * @throws IllegalArgumentException if the matcher is null or the number of repeats is less than one.
     */
    public ByteMatcherArrayMatcher(final ByteMatcher matcher, final int numberOfMatchers) {
        ArgUtils.checkNullObject(matcher);
        ArgUtils.checkPositiveInteger(numberOfMatchers);
        length = numberOfMatchers;
        this.matchers = new ByteMatcher[length];
        Arrays.fill(this.matchers, matcher);
    }


    /**
     * {@inheritDoc}
     * 
     * @throws NullPointerException if the WindowReader passed in is null.
     */
    @Override
    public boolean matches(final WindowReader reader, final long matchPosition) throws IOException {
        final int localLength = length;
        final ByteMatcher[] matchList = this.matchers;        
        Window window = reader.getWindow(matchPosition);
        int checkPos = 0;
        while (window != null) {
            final int offset = reader.getWindowOffset(matchPosition + checkPos);
            final int endPos = Math.min(window.length(), offset + localLength - checkPos);
            final byte[] array = window.getArray();
            for (int windowPos = offset; windowPos < endPos; windowPos++) {
                final ByteMatcher byteMatcher = matchList[checkPos++];
                if (!byteMatcher.matches(array[windowPos])) {
                    return false;
                }
            }
            if (checkPos == localLength) {
                return true;
            }
            window = reader.getWindow(matchPosition + checkPos);
        }
        return false;
    }    
    
    
    /**
     * {@inheritDoc}
     * 
     * @throws NullPointerException if the byte array passed in is null.
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        if (matchPosition + length < bytes.length && matchPosition >= 0) {
            int position = matchPosition;
            final ByteMatcher[] localMatchers = matchers;
            for (final ByteMatcher matcher : localMatchers) {
                if (!matcher.matches(bytes[position++])) {
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
     * @throws NullPointerException if the byte array passed in is null.
     */
    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchPosition) {
        int position = matchPosition;
        final ByteMatcher[] localMatchers = matchers;
        for (final ByteMatcher matcher : localMatchers) {
            if (!matcher.matches(bytes[position++])) {
                return false;
            }
        }
        return true;
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteMatcher getMatcherForPosition(final int position) {
        return matchers[position];
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
    public ByteMatcherArrayMatcher reverse() {
        final List<ByteMatcher> newList = Arrays.asList(matchers);
        Collections.reverse(newList);
        return new ByteMatcherArrayMatcher(newList);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        final StringBuilder builder = new StringBuilder(length * 4);
        for (final ByteMatcher matcher : matchers) {
            builder.append(matcher.toRegularExpression(prettyPrint));
        }
        return builder.toString();
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public SequenceMatcher subsequence(final int beginIndex, final int endIndex) {
        if (beginIndex < 0 || endIndex > length || beginIndex >= endIndex) {
            final String message = "Subsequence index %d to %d is out of bounds in a sequence of length %d";
            throw new IndexOutOfBoundsException(String.format(message, beginIndex, endIndex, length));
        }
        if (endIndex - beginIndex == 1) {
            return matchers[beginIndex];
        }
        return new ByteMatcherArrayMatcher(Arrays.copyOfRange(matchers, beginIndex, endIndex));
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
     * 
     * @throws IllegalArgumentException if the number of repeats is less than one.
     */
    @Override
    public SequenceMatcher repeat(int numberOfRepeats) {
        if (numberOfRepeats < 1) {
            throw new IllegalArgumentException("Number of repeats must be at least one.");
        }
        if (numberOfRepeats == 1) {
            return this;
        }
        return new ByteMatcherArrayMatcher(repeatMatchers(numberOfRepeats));
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
        return getClass().getSimpleName() + '[' + toRegularExpression(true) + ']';
    }
    
    
    
    private ByteMatcher[] repeatMatchers(final int numberOfRepeats) {
        final int repeatSize = matchers.length;
        final ByteMatcher[] repeated = new ByteMatcher[repeatSize * numberOfRepeats];
        for (int repeat = 0; repeat < numberOfRepeats; repeat++) {
            System.arraycopy(matchers, 0, repeated, repeat * repeatSize, repeatSize);
        }
        return repeated;
    }


}
