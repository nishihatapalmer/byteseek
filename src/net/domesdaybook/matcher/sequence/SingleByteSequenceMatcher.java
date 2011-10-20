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
 *  * The names of its contributors may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 * 
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
     * @param numberOfMatchers 
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
     */
    @Override
    public boolean matches(final Reader reader, final long matchPosition) throws IOException {
        final int localLength = length;
        final List<SingleByteMatcher> matchList = this.matcherSequence;        
        Window window = reader.getWindow(matchPosition);
        int checkPos = 0;
        while (window != null) {
            final int offset = reader.getWindowOffset(matchPosition + checkPos);
            final int endPos = Math.min(window.getLimit(), offset + localLength - checkPos);
            final byte[] array = window.getArray();
            for (int windowPos = offset; windowPos < endPos; windowPos++) {
                final SingleByteMatcher byteMatcher = matchList.get(checkPos++);
                if (!byteMatcher.matches(array[windowPos])) {
                    return false;
                }
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
        final int localStop = length;
        if (matchPosition + localStop < bytes.length && matchPosition >= 0) {
            final List<SingleByteMatcher> matchList = this.matcherSequence;
            for (int byteIndex = 0; byteIndex < localStop; byteIndex++) {
                final SingleByteMatcher byteMatcher = matchList.get(byteIndex);
                if (!byteMatcher.matches(bytes[matchPosition + byteIndex])) {
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
        final List<SingleByteMatcher> matchList = this.matcherSequence;
        final int localStop = length;
        for (int byteIndex = 0; byteIndex < localStop; byteIndex++) {
            final SingleByteMatcher byteMatcher = matchList.get(byteIndex);
            final byte byteRead = bytes[matchPosition + byteIndex];
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
