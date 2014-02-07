/*
 * Copyright Matt Palmer 2009-2014, All rights reserved.
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import net.byteseek.bytes.ByteUtils;
import net.byteseek.io.reader.Window;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.bytes.ByteMatcher;
import net.byteseek.object.ArgUtils;

/**
 * An immutable sequence matcher which matches sequences of other sequence matchers.
 * For example, we could have a sequence of bytes, followed by a case insensitive
 * sequence, followed by a fixed gap, followed by a byte set sequence.
 * 
 * @author Matt Palmer
 */
public final class SequenceSequenceMatcher implements SequenceMatcher {

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
     * @param numberOfRepeats The number of times to repeat the list of SequenceMatchers.
     * @param matcherCollection  A collection of (repeated) SequenceMatchers from which to construct this SequenceSequenceMatcher.
     * 
     * @throws IllegalArgumentException if the collection is null or empty or contains null elements,
     *                                  or the number to repeat is less than one.
     */
    public SequenceSequenceMatcher(final int numberOfRepeats, final List<? extends SequenceMatcher> matcherCollection) {
        ArgUtils.checkNullOrEmptyCollectionNoNullElements(matcherCollection);
        ArgUtils.checkPositiveInteger(numberOfRepeats);
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
    
    
	/**
     * Constructs a SequenceSequenceMatcher from an array of SequenceMatchers.
     * 
     * @param matchArray The array of SequenceMatchers to construct from.
     * @throws IllegalArgumentException if the array is null or empty.
     */
    public SequenceSequenceMatcher(final SequenceMatcher...matchers) {
        this(1, matchers);
    }
    
    
    /**
     * Constructs a SequenceSequenceMatcher from an array of SequenceMatcher,
     * repeated a number of times.
     * @param numberOfRepeats The number of times to repeat the array.
     * @param matchArray The array of SequenceMatchers to construct from.
     * 
     * @throws IllegalArgumentException if the array is null or empty, or the
     *         number of repeats is less than one.
     */
    public SequenceSequenceMatcher(final int numberOfRepeats, final SequenceMatcher... matchArray) {
        ArgUtils.checkNullOrEmptyArrayNoNullElements(matchArray);
        ArgUtils.checkPositiveInteger(numberOfRepeats);
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
     * 
     * @throws NullPointerException if the WindowReader is null.
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
        /*
    	final StringBuilder regularExpression = new StringBuilder();
        boolean firstMatcher = true;
        for (final SequenceMatcher matcher : matchers) {
        	if (prettyPrint && !firstMatcher) {
        		regularExpression.append(' ');
        	}
           regularExpression.append(matcher.toRegularExpression(prettyPrint));
           firstMatcher = false;
        }
        return regularExpression.toString();
        */
        final StringBuilder builder = new StringBuilder(prettyPrint? totalLength * 4 : totalLength * 3);
        boolean singleByte = false;
        boolean appended = false;
        final List<Byte> singleBytes = new ArrayList<Byte>();
        for (int index = 0; index < totalLength; index++) {
        	final ByteMatcher matcher = getMatcherForPosition(index);
        	if (matcher.getNumberOfMatchingBytes() == 1) {
        		singleByte = true;
        		singleBytes.add(Byte.valueOf(matcher.getMatchingBytes()[0]));
        	} else {
        		if (singleByte) {
        			builder.append(ByteUtils.bytesToString(prettyPrint, singleBytes));
        			appended = true;
        			singleBytes.clear();
        			singleByte = false;
        		}
        		if (prettyPrint && appended) {
        			builder.append(' ');
        		}
        		builder.append(matcher.toRegularExpression(prettyPrint));
        		appended = true;
        	}
        }
		if (singleByte) {
    		if (prettyPrint && appended) {
    			builder.append(' ');
    		}
			builder.append(ByteUtils.bytesToString(prettyPrint, singleBytes));
		}
        return builder.toString();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ByteMatcher getMatcherForPosition(final int position) {
    	ArgUtils.checkIndexOutOfBounds(totalLength,  position);
        int currentEndPosition = 0;
        for (final SequenceMatcher matcher : matchers) {
            final int matcherLength = matcher.length();
            currentEndPosition += matcherLength;
            if (position < currentEndPosition) {
            	final int matcherOffset = position - (currentEndPosition - matcherLength); 
                return matcher.getMatcherForPosition(matcherOffset);
            }
        }
        final String badness = "A ByteMatcher for position %d in a SequenceSequenceMatcher of length %d could not be retrieved.  This should not happen; there is a bug.  Please report this to the byteseek developers.";
        throw new RuntimeException(String.format(badness, position, totalLength));
    }


    /**
     * Returns a cloned array of {@link SequenceMatcher}s this sequence array matcher matches.
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
    public SequenceSequenceMatcher reverse() {
        final SequenceMatcher[] reversed = new SequenceMatcher[matchers.length];
        int position = matchers.length - 1;
        for (final SequenceMatcher matcher : matchers) {
            reversed[position--] = matcher.reverse();
        }
        return new SequenceSequenceMatcher(reversed);
    }

    
    /**
     * {@inheritDoc}
     */
    // Suppress warnings about null in this method.  Eclipse believes that the startMatcher or
    // the endMatcher could be null at the end of the loop.  This can only be true if there is a bug.
    // It should not happen otherwise, as we already test that the beginIndex and endIndex are within
    // the bounds of the SequenceSequenceMatcher total length - the sum of all the SequenceMatchers in it.
    // Therefore, a failure to find a SequenceMatcher within those bounds would represent a genuine 
    // programming error somewhere, which could then result in a NullPointerException thrown in this method.
    @SuppressWarnings("null")
	@Override
    public SequenceMatcher subsequence(final int beginIndex, final int endIndex) {
        ArgUtils.checkIndexOutOfBounds(totalLength, beginIndex, endIndex);
        
        // Locate info about the start and ending matchers for these index positions:
        SequenceMatcher startMatcher = null, endMatcher = null;
        int startOffset = 0, endLimit = 0;
        int startIndex = 0, lastIndex = 0;
        int currentEndPosition = 0;
        int startPosition = beginIndex;
        int endPosition = endIndex - 1;
        for (int matcherIndex = 0; matcherIndex < matchers.length; matcherIndex++) {
            final SequenceMatcher matcher = matchers[matcherIndex];
            final int matcherLength = matcher.length();
            currentEndPosition += matcherLength;
            if (startPosition < currentEndPosition) {
                startMatcher = matcher;
                startOffset = beginIndex - (currentEndPosition - matcherLength);
                startIndex = matcherIndex;
                startPosition = Integer.MAX_VALUE;
            }
            if (endPosition < currentEndPosition) {
                endMatcher = matcher;
                endLimit = endIndex - (currentEndPosition - matcherLength);
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
        return new SequenceSequenceMatcher(newSequence);
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
    public SequenceMatcher repeat(final int numberOfRepeats) {
    	ArgUtils.checkPositiveInteger(numberOfRepeats);
        if (numberOfRepeats == 1) {
            return this;
        }        
        return new SequenceSequenceMatcher(numberOfRepeats, matchers);
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

    

	@Override
	public Iterator<ByteMatcher> iterator() {
		return new SequenceSequenceIterator();
	}    

	
	
    private int calculateTotalLength(final SequenceMatcher[] matchers) {
		int totalLength = 0;
		for (final SequenceMatcher matcher : matchers) {
			totalLength += matcher.length();
		}
		return totalLength;
	}
    
    
	
	private class SequenceSequenceIterator implements Iterator<ByteMatcher> {

		private int position;
		
		@Override
		public boolean hasNext() {
			return position < totalLength;
		}

		@Override
		public ByteMatcher next() {
			if (hasNext()) {
				return getMatcherForPosition(position++);
			}
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Cannot remove byte matchers from SequenceSequenceMatchers");
		}
		
	}


}
