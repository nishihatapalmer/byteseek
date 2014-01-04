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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import net.byteseek.bytes.ByteUtils;
import net.byteseek.io.reader.Window;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.bytes.ByteMatcher;
import net.byteseek.matcher.bytes.OneByteMatcher;
import net.byteseek.object.ArgUtils;

/**
 * An immutable class which matches a sequence of {@link ByteMatcher}s.
 * Constructors taking a wide variety of data types are provided : bytes and byte arrays, strings, 
 * collections and arrays of ByteMatchers and ByteMatcherSequenceMatchers. Subsequences and repeats can
 * also be specified in them.
 * <p>
 * ByteMatcherSequenceMatchers can match sequences where any position in the sequence is matched by
 * any sort of byte matcher - single bytes, inverted bytes, ranges, bitmasks or arbitrary sets
 * of bytes.  For example (using the byteseek regular expression syntax):
 * <p>
 * <p/><code>
 * 'begin:' [00 ff] \d \d \d \s 'end.'
 * </code><p/>
 * <p>
 * would match a string starting with 'begin:', followed by either byte 00 or byte ff, then three digits and
 * some whitespace, followed by 'end.'.
 * <p>
 * A public static ReverseByteMatcherSequenceMatcher class is also provided, that does the same thing but matching the 
 * reverse of a sequence given to it. If constructed from a ByteMatcherSequenceMatcher, it will
 * share the underlying ByteMatcher array of the ByteMatcherSequenceMatcher, as a reverse view over the original sequence.  
 *   
 * @author Matt Palmer
 */
public final class ByteMatcherSequenceMatcher implements SequenceMatcher {

    private final int length;
    private final int startArrayIndex; // the position to start at (an inclusive value)
    private final int endArrayIndex;   // one past the actual end position (an exclusive value)
	private final ByteMatcher[] matchers;
    
	
    /****************
     * Constructors *
     ***************/

	
	/**
     * Constructs an immutable ByteMatcherSequenceMatcher from an array of bytes, which 
     * can be passed in directly as an array of bytes, or specified as a comma-separated list of bytes.
     * <p>
     * The byte array is just used as a template to construct from - 
     * it will be independent of it afterwards.
     * 
     * @param bytes The array of bytes to match.
     * @throws IllegalArgumentException if the array of bytes passed in is null or empty.
     */
    public ByteMatcherSequenceMatcher(final byte... bytes) {
        this(1, bytes, 0, bytes == null? -1 : bytes.length);
    }
    
    
    /**
     * Constructs an immutable ByteMatcherSequenceMatcher from a repeated byte.
     * 
     * @param repeats The number of bytes to repeat.
     * @param byteValue The byte value to repeat.
     *
     * @throws IllegalArgumentException If the number of repeats is less than one.
     */
    public ByteMatcherSequenceMatcher(final int repeats, final byte byteValue) {
        this(repeats, OneByteMatcher.valueOf(byteValue));
    }	

    
    /**
     * Constructs an immutable ByteMatcherSequenceMatcher from a repeated {@link ByteMatcher} object.
     * 
     * @param repeats The number of times to repeat the ByteMatcher.
     * @param matcher The ByteMatcher to construct this sequence matcher from.
     *
     * @throws IllegalArgumentException if the number of repeats is less than one,
     *                                  or the matcher is null.
     */
    public ByteMatcherSequenceMatcher(final int repeats, final ByteMatcher matcher) {
    	ArgUtils.checkPositiveInteger(repeats);
    	ArgUtils.checkNullObject(matcher);
        this.length          = repeats;
        this.startArrayIndex = 0;
        this.endArrayIndex   = length;
        this.matchers        = new ByteMatcher[length];
        Arrays.fill(this.matchers, matcher);
    }
    
    
    /**
     * Constructs an immutable ByteMatcherSequenceMatcher from a string, encoding the
     * bytes of the string using the system default Charset as an array of OneByteMatchers.
     * 
     * @param string The string whose bytes will be matched.
     * @throws IllegalArgumentException if the string is null or empty.
     */
    public ByteMatcherSequenceMatcher(final String string) {
        this(string, Charset.defaultCharset());
    }
    

    /**
     * Constructs an immutable ByteMatcherSequenceMatcher from a string and a Charset to use
     * to encode the bytes in the string as an array of OneByteMatchers.
     * 
     * @param string The string whose bytes will be matched
     * @param charset The Charset to encode the strings bytes in.
     * @throws IllegalArgumentException if the string is null or empty, or the
     *         Charset is null.
     */
    public ByteMatcherSequenceMatcher(final String string, final Charset charset) {
        ArgUtils.checkNullOrEmptyString(string, "string");
        ArgUtils.checkNullObject(charset, "charset");
        final byte[] bytes = string.getBytes(charset);
        this.length          = bytes.length;
        this.startArrayIndex = 0;
        this.endArrayIndex   = length;
        this.matchers        = new ByteMatcher[length];
        populateMatchers(1, bytes, 0, length);
    }

    
    /**
     * Constructs an immutable ByteMatcherSequenceMatcher using a byte array as a template,
     * repeated a number of times.
     * 
     * @param repeats The number of times to repeat the byte array
     * @param array The byte array to use as a template
     * @throws IllegalArgumentException if the number of repeats is less than one,
     *                                  or the byte array is null or empty.
     */
    public ByteMatcherSequenceMatcher(final int repeats, final byte[] array) {
    	this(repeats, array, 0, array == null? -1 : array.length);
    }
    
    
    /**
     * Constructs an immutable ByteMatcherSequenceMatcher from a subsequence of an array of bytes.
     * The subsequence is specified with a start index (inclusive) and end index (exclusive).
     * <p>
     * It will be entirely independent of the byte array passed in after construction.
     *  
     * @param array The array of bytes to match a subsequence of.
     * @param startIndex The start index to begin matching within the array, inclusive.
     * @param endIndex The end index to stop matching within the array, exclusive.
     * @throws IllegalArgumentException if the array of bytes passed in is null or empty.
     * @throws IndexOutOfBoundsException if the start or end indexes are out of bounds.
     */
    public ByteMatcherSequenceMatcher(final byte[] array, 
    		                          final int startIndex, final int endIndex) {
    	this(1, array, startIndex, endIndex);
    }
    
    
    /**
     * Constructs an immutable ByteMatcherSequenceMatcher using a byte array passed in
     * as a template, taking a subsequence of that array and repeating it a number of times.
     * 
     * @param repeats The number of times to repeat the subsequence.
     * @param array The array containing the subsequence of bytes to repeat.
     * @param startIndex The start index of the subsequence within the array, inclusive.
     * @param endIndex The end index of the subsequence within the array, exclusive.
     * @throws IllegalArgumentException if the number of repeats is less than one,
     *                                  the array is null or empty.
     * @throws IndexOutOfBoundsException if the start or end indexes are out of bounds in the array.
     */
	public ByteMatcherSequenceMatcher(final int repeats, final byte[] array, 
								      final int startIndex, final int endIndex) {
        ArgUtils.checkPositiveInteger(repeats, "repeats");
		ArgUtils.checkNullOrEmptyByteArray(array);
        ArgUtils.checkIndexOutOfBounds(array.length, startIndex, endIndex);
        this.length          = (endIndex - startIndex) * repeats;
        this.startArrayIndex = 0;
        this.endArrayIndex   = length;
        this.matchers        = new ByteMatcher[length];
        populateMatchers(repeats, array, startIndex, endIndex);
	}

	
    /**
     * Constructs an immutable ByteMatcherSequenceMatcher using an array of {@link ByteMatcher}
     * objects as a template.  The array can be specified by passing in an array, or as a comma
     * delimited list of parameters at compile time, since the ... syntax is used.
     * 
     * @param sequence An array of ByteMatchers to construct this sequence matcher from.
     * @throws IllegalArgumentException if the array is null or empty or any element of it is null.
     */
    public ByteMatcherSequenceMatcher(final ByteMatcher... sequence) {
    	this(1, sequence, 0, sequence == null? -1 : sequence.length);
    }

    
    /**
     * Constructs an immutable ByteMatcherSequenceMatcher using a repeated array of {@link ByteMatcher}
     * objects as a template.
     * 
     * @param repeats The number of times to repeat the array.
     * @param sequence An array of ByteMatchers to construct this sequence matcher from.
     * @throws IllegalArgumentException if the array is null or empty or any element of it is null.
     */
    public ByteMatcherSequenceMatcher(final int repeats, final ByteMatcher[] sequence) {
    	this(repeats, sequence, 0, sequence == null? -1 : sequence.length);
    }
    
    
    /**
     * Constructs an immutable ByteMatcherSequenceMatcher using a subsequence of an array of 
     * {@link ByteMatcher} objects as a template.
     * 
     * @param sequence An array of ByteMatchers to construct this sequence matcher from.
     * @param startIndex The start index in the sequence to start matching from, inclusive.
     * @param endIndex The end index in the sequence to end matching, exclusive.
     * @throws IllegalArgumentException if the array is null or empty, or any element of it is null.
     * @throws IndexOutOfBoundsException if the start or end index is out of bounds.
     */
    public ByteMatcherSequenceMatcher(final ByteMatcher[] sequence, 
    		                          final int startIndex, final int endIndex) {
    	this(1, sequence, startIndex, endIndex);
    }
    
    
    /**
     * Constructs an immutable ByteMatcherSequenceMatcher using a repeated subsequence of an array of 
     * {@link ByteMatcher} objects as a template.
     * 
     * @param repeats The number of times to repeat the subsequence.
     * @param sequence An array of ByteMatchers to construct this sequence matcher from.
     * @param startIndex The start index in the sequence to start matching from, inclusive.
     * @param endIndex The end index in the sequence to end matching, exclusive.
     * @throws IllegalArgumentException if the repeats are less than one, the array is null or empty, or any element of it is null.
     * @throws IndexOutOfBoundsException if the start or end index is out of bounds.
     */
    public ByteMatcherSequenceMatcher(final int repeats, final ByteMatcher[] sequence, 
    		                          final int startIndex, final int endIndex) {
    	ArgUtils.checkPositiveInteger(repeats);
    	ArgUtils.checkNullOrEmptyArrayNoNullElements(sequence);
    	ArgUtils.checkIndexOutOfBounds(sequence.length, startIndex, endIndex);
        this.length          = endIndex - startIndex; 
        this.startArrayIndex = 0;
        this.endArrayIndex   = length;
        this.matchers        = new ByteMatcher[length];
        populateMatchers(repeats, sequence, startIndex, endIndex);
    }

    
    /**
     * Constructs an immutable ByteMatcherSequenceMatcher from another repeated ByteMatcherSequenceMatcher.
     * The underlying ByteMatchers will be shared, but unless the repeat is only one, a new array will be
     * created to hold the repeated pattern.
     * 
     * @param repeats The number of times to repeat the source 
     * @param source The ByteMatcherSequenceMatcher to repeat.
     * @throws IllegalArgumentException if repeats is less than one, or if the source is null.
     */
	public ByteMatcherSequenceMatcher(final int repeats, final ByteMatcherSequenceMatcher source) {
		this(repeats, source, 0, source == null? -1 : source.length);
	}
    
    
	/**
	 * Constructs an immutable ByteMatcherSequenceMatcher from another ByteMatcherSequenceMatcher,
	 * but taking a start and end index.  The copy-constructed matcher will share the underlying
	 * matcher storage of the original, and will form a subsequence of the original.
	 *  
	 * @param matcher The ByteMatcherSequenceMatcher to construct from.
	 * @param startIndex The start index in the source matcher for this matcher to start from, inclusive.
	 * @param endIndex The end index in the source matcher for this matcher to end at, exclusive.
	 * @throws IllegalArgumentException if the matcher is null.
	 * @throws IndexOutOfBoundsException if the start or end indexes are out of bounds in the source matcher.
	 */
	public ByteMatcherSequenceMatcher(final ByteMatcherSequenceMatcher matcher, 
			                          final int startIndex, final int endIndex) {
		this(1, matcher, startIndex, endIndex);
	}
	
	
	/**
	 * Constructs an immutable ByteMatcherSequenceMatcher from another ByteMatcherSequenceMatcher,
	 * but taking a start and end index and a number of times to repeat the subsequence.
	 * The copy-constructed matcher will only share the underlying matcher storage of the original if 
	 * the number of repeats is one, otherwise a new array of ByteMatchers will be created to hold the 
	 * repetitions.
	 *  
	 * @param repeats The number of times to repeat the source subsequence.
	 * @param matcher The ByteMatcherSequenceMatcher to construct from.
	 * @param startIndex The start index in the source matcher for this matcher to start from, inclusive.
	 * @param endIndex The end index in the source matcher for this matcher to end at, exclusive.
	 * @throws IllegalArgumentException if the matcher is null.
	 * @throws IndexOutOfBoundsException if the start or end indexes are out of bounds in the source matcher.
	 */
	public ByteMatcherSequenceMatcher(final int repeats, final ByteMatcherSequenceMatcher source,
									  final int startIndex, final int endIndex) {
		ArgUtils.checkPositiveInteger(repeats);
		ArgUtils.checkNullObject(source);
		ArgUtils.checkIndexOutOfBounds(source.length, startIndex, endIndex);
		this.length              = (endIndex - startIndex) * repeats;
		if (repeats == 1) {
			this.startArrayIndex = source.startArrayIndex + startIndex;
			this.endArrayIndex   = source.startArrayIndex + endIndex;
			this.matchers        = source.matchers;
		} else {
			this.startArrayIndex = 0;
			this.endArrayIndex   = length;
			this.matchers = new ByteMatcher[length];
			populateMatchers(repeats, source, startIndex, endIndex);
		}
	}


	/**
	 * Constructs an immutable ByteMatcherSequenceMatcher from an array of ByteMatcherSequenceMatchers.
	 * If the array only contains a single ByteMatcherSequenceMatcher, then they will share the underlying
	 * array of ByteMatchers.  If there are more than one, new storage is created to hold them. 
	 * 
	 * @param matchers The array of ByteMatcherSequenceMatchers.
	 * @throws IllegalArgumentException if the array of matchers is null or empty, or if any of the 
	 *                                  elements of the array are null.
	 */
    public ByteMatcherSequenceMatcher(final ByteMatcherSequenceMatcher... matchers) {
		ArgUtils.checkNullOrEmptyArrayNoNullElements(matchers);
		if (matchers.length == 1) {
			final ByteMatcherSequenceMatcher theMatcher = matchers[0];
			this.length          = theMatcher.length;
			this.startArrayIndex = theMatcher.startArrayIndex;
			this.endArrayIndex   = theMatcher.endArrayIndex;
			this.matchers        = theMatcher.matchers;
		} else {
			this.length          = countTotalLength(matchers);
	        this.startArrayIndex = 0;
	        this.endArrayIndex   = length;
			this.matchers        = new ByteMatcher[length];
			populateMatchers(matchers);
		}
    }

	
	/**
	 * Constructs an immutable ByteMatcherSequenceMatcher from an array of other SequenceMatchers,
	 * joining all the ByteMatchers within them into a single matcher.  The resulting matcher is independent of 
	 * the array passed in.
	 * 
	 * @param matchers The array  of SequenceMatchers to join into a single ByteMatcherSequenceMatcher.
	 * @throws IllegalArgumentException if the array is null, empty or contains null elements.
	 */
    
    public ByteMatcherSequenceMatcher(final SequenceMatcher... matchers) {
		ArgUtils.checkNullOrEmptyArrayNoNullElements(matchers);
		this.length          = countTotalLength(matchers);
        this.startArrayIndex = 0;
        this.endArrayIndex   = length;
		this.matchers        = new ByteMatcher[length];
		populateMatchers(matchers); 
    }
    
    
	/**
	 * Constructs an immutable ByteMatcherSequenceMatcher from a list of other SequenceMatchers,
	 * joining all the ByteMatchers within them into a single matcher.  The resulting matcher is independent of 
	 * the list passed in.
	 * 
	 * @param list The list of SequenceMatchers to join into a single ByteMatcherSequenceMatcher.
	 * @throws IllegalArgumentException if the list is null, empty or contains null elements.
	 */
	public ByteMatcherSequenceMatcher(final List<? extends SequenceMatcher> list) {
		ArgUtils.checkNullOrEmptyCollectionNoNullElements(list);
		this.length          = countTotalLength(list);
        this.startArrayIndex = 0;
        this.endArrayIndex   = length;
		this.matchers        = new ByteMatcher[length];
		populateMatchers(list);
	}
	
	
	/**
	 * Constructs a ByteMatcherSequenceMatcher from a ReverseByteMatcherSequenceMatcher.
	 * The matcher will match the opposite sequence to the ReverseByteMatcherSequenceMatcher, but
	 * will share the underlying array of ByteMatchers.  It will be a forward-matching view over the
	 * underlying sequence of matchers.
	 * 
	 * @param matcher The ReverseByteMatcherSequenceMatcher to construct from.
	 * @throws IllegalArgumentException if the matcher is null.
	 */
	public ByteMatcherSequenceMatcher(final ReverseByteMatcherSequenceMatcher matcher) {
		ArgUtils.checkNullObject(matcher);
		this.length          = matcher.length;
        this.startArrayIndex = matcher.startArrayIndex;
        this.endArrayIndex   = matcher.endArrayIndex;
		this.matchers        = matcher.matchers; 
	}
    
    
    /******************
     * Public methods *
     ******************/
    

	/**
     * {@inheritDoc}
     * 
     * @throws NullPointerException if the WindowReader passed in is null.
     */
    @Override
    public boolean matches(final WindowReader reader, final long matchPosition) throws IOException {
        final int localLength = length;
        final int matchStart  = startArrayIndex;
        final int matchEnd    = endArrayIndex;
        final ByteMatcher[] matchList = this.matchers;        
        Window window = reader.getWindow(matchPosition);
        int checkPos = matchStart;
        int bytesMatchedSoFar = 0;
        while (window != null) {
            final int offset = reader.getWindowOffset(matchPosition + bytesMatchedSoFar);
            final int endPos = Math.min(window.length(), offset + localLength - bytesMatchedSoFar);
            final byte[] array = window.getArray();
            for (int windowPos = offset; windowPos < endPos; windowPos++) {
                final ByteMatcher byteMatcher = matchList[checkPos++];
                if (!byteMatcher.matches(array[windowPos])) {
                    return false;
                }
            }
            if (checkPos >= matchEnd) {
                return true;
            }
            bytesMatchedSoFar = checkPos - matchStart;
            window = reader.getWindow(matchPosition + bytesMatchedSoFar);
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
        if (matchPosition + length <= bytes.length && matchPosition >= 0) {
            int position = matchPosition;
            final ByteMatcher[] localMatchers = matchers;
            final int endIndex = endArrayIndex;
            for (int matcherPosition = startArrayIndex; matcherPosition < endIndex; matcherPosition++) {
                if (!localMatchers[matcherPosition].matches(bytes[position++])) {
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
        final int endIndex = endArrayIndex;
        for (int matcherPosition = startArrayIndex; matcherPosition < endIndex; matcherPosition++) {
            if (!localMatchers[matcherPosition].matches(bytes[position++])) {
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
    	ArgUtils.checkIndexOutOfBounds(length, position);
        return matchers[startArrayIndex + position];
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
    public SequenceMatcher reverse() {
    	return new ReverseByteMatcherSequenceMatcher(this);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        final StringBuilder builder = new StringBuilder(prettyPrint? length * 4 : length * 3);
        boolean singleByte = false;
        final List<Byte> singleBytes = new ArrayList<Byte>();
        for (int index = startArrayIndex; index < endArrayIndex; index++) {
        	final ByteMatcher matcher = matchers[index];
        	if (matcher.getNumberOfMatchingBytes() == 1) {
        		singleByte = true;
        		singleBytes.add(Byte.valueOf(matcher.getMatchingBytes()[0]));
        	} else {
        		if (singleByte) {
        			builder.append(ByteUtils.bytesToString(prettyPrint, singleBytes));
        			singleBytes.clear();
        			singleByte = false;
        		}
        		builder.append(matcher.toRegularExpression(prettyPrint));
        	}
        }
		if (singleByte) {
			builder.append(ByteUtils.bytesToString(prettyPrint, singleBytes));
		}
        return builder.toString();
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public SequenceMatcher subsequence(final int beginIndex, final int endIndex) {
    	ArgUtils.checkIndexOutOfBounds(length, beginIndex, endIndex);
        final int subsequenceLength = endIndex - beginIndex;
        if (subsequenceLength == length) {
            return this;
        }
    	if (subsequenceLength == 1) {
            return matchers[startArrayIndex + beginIndex];
        }
        return new ByteMatcherSequenceMatcher(this, beginIndex, endIndex);
    }

    
    /**
     * {@inheritDoc}
     */  
    @Override
    public SequenceMatcher subsequence(final int beginIndex) {
        return subsequence(beginIndex, length);
    }            
    
    
    /**
     * {@inheritDoc}
     * 
     * @throws IllegalArgumentException if the number of repeats is less than one.
     */
    @Override
    public SequenceMatcher repeat(final int numberOfRepeats) {
        ArgUtils.checkPositiveInteger(numberOfRepeats);
        if (numberOfRepeats == 1) {
            return this;
        }
        return new ByteMatcherSequenceMatcher(repeatMatchers(numberOfRepeats));
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
    
    
    /*******************
     * Private methods *
     *******************/

	private void populateMatchers(final int repeats, final byte[] bytes, 
			                      final int startIndex, final int endIndex) {
		final int subsequenceLength = endIndex - startIndex;
		final int totalLength = subsequenceLength * repeats;
		for (int position = 0; position < totalLength; position++) {
			final int arrayIndex = startIndex + (position % subsequenceLength);
        	matchers[position] = OneByteMatcher.valueOf(bytes[arrayIndex]);
        }
	}

	
	private void populateMatchers(final int repeats, final ByteMatcher[] byteMatchers, 
                                  final int startIndex, final int endIndex) {
		final int subsequenceLength = endIndex - startIndex;
		final int totalLength = subsequenceLength * repeats;
		for (int position = 0; position < totalLength; position++) {
			final int arrayIndex = startIndex + (position % subsequenceLength);
			matchers[position] = byteMatchers[arrayIndex];
		}
	}

	
	private void populateMatchers(final int repeats, final ByteMatcherSequenceMatcher source,
								  final int startIndex, final int endIndex) {
		final int subsequenceLength = endIndex - startIndex;
		final int totalLength = subsequenceLength * repeats;
		final int sourceStartIndex = source.startArrayIndex + startIndex;
		for (int position = 0; position < totalLength; position++) {
			final int sourceIndex = sourceStartIndex + (position % subsequenceLength);
        	matchers[position] = source.matchers[sourceIndex];
        }
	}

	
	private void populateMatchers(final List<? extends SequenceMatcher> list) {
		int matcherPos = 0;
		for (final SequenceMatcher sequence : list) {
			for (final ByteMatcher matcher : sequence) {
				matchers[matcherPos++] = matcher;
			}
		}
	}
	
	private void populateMatchers(final SequenceMatcher[] list) {
		int matcherPos = 0;
		for (final SequenceMatcher sequence : list) {
			for (final ByteMatcher matcher : sequence) {
				matchers[matcherPos++] = matcher;
			}
		}
	}

    
    private ByteMatcher[] repeatMatchers(final int numberOfRepeats) {
        final int repeatSize = matchers.length;
        final ByteMatcher[] repeated = new ByteMatcher[repeatSize * numberOfRepeats];
        for (int repeat = 0; repeat < numberOfRepeats; repeat++) {
            System.arraycopy(matchers, 0, repeated, repeat * repeatSize, repeatSize);
        }
        return repeated;
    }
    
    
	private int countTotalLength(final List<? extends SequenceMatcher> list) {
		int totalLength = 0;
		for (final SequenceMatcher sequence : list) {
			totalLength += sequence.length();
		}
		return totalLength;
	}
	
	
	private int countTotalLength(final SequenceMatcher[] matchers) {
		int totalLength = 0;
		for (final SequenceMatcher sequence : matchers) {
			totalLength += sequence.length();
		}
		return totalLength;
	}
    
    
	@Override
	public Iterator<ByteMatcher> iterator() {
		return new ByteMatcherSequenceIterator();
	}

	private class ByteMatcherSequenceIterator implements Iterator<ByteMatcher> {

		int position = startArrayIndex;
		
		@Override
		public boolean hasNext() {
			return position < endArrayIndex;
		}

		@Override
		public ByteMatcher next() {
			if (hasNext()) {
				return matchers[position++];
			}
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Cannot remove matchers from a ByteMatcherSequenceMatcher");
		}
		
	}

    
    
    public static class ReverseByteMatcherSequenceMatcher implements SequenceMatcher {

    	private final ByteMatcher[] matchers;
        private final int startArrayIndex; // the position to start at (an inclusive value)
        private final int endArrayIndex;   // one past the actual end position (an exclusive value)
        private final int length;
    	
        /****************
         * Constructors *
         ***************/
        
        public ReverseByteMatcherSequenceMatcher(final byte[] array) {
			ArgUtils.checkNullOrEmptyByteArray(array);
			this.length          = array.length;
			this.startArrayIndex = 0;
			this.endArrayIndex   = length;
			this.matchers        = new ByteMatcher[length];
			populateMatchers(array, 0, length);
		}

        
		public ReverseByteMatcherSequenceMatcher(final ReverseByteMatcherSequenceMatcher original, 
												 final int startIndex, final int endIndex) {
			ArgUtils.checkNullObject(original);
			ArgUtils.checkIndexOutOfBounds(original.length(), startIndex, endIndex);
			this.length          = endIndex - startIndex;
			this.startArrayIndex = original.startArrayIndex + original.length - endIndex;
			this.endArrayIndex   = original.endArrayIndex - startIndex;
			this.matchers        = original.matchers;
		}

		
		public ReverseByteMatcherSequenceMatcher(final int numberOfRepeats,
												 final ReverseByteMatcherSequenceMatcher original) {
			this(numberOfRepeats, original, 0, original.length);
		}
		

		public ReverseByteMatcherSequenceMatcher(final int numberOfRepeats,
												 final ReverseByteMatcherSequenceMatcher original, 
												 final int startIndex, final int endIndex) {
			ArgUtils.checkNullObject(original);
			ArgUtils.checkPositiveInteger(numberOfRepeats);
			ArgUtils.checkIndexOutOfBounds(original.length(), startIndex, endIndex);
			this.length          = (endIndex - startIndex) * numberOfRepeats;
			if (numberOfRepeats == 1) {
				this.matchers = original.matchers;
				this.startArrayIndex = original.startArrayIndex + startIndex;
				this.endArrayIndex   = original.startArrayIndex + endIndex;
			} else {
				this.matchers = repeatReverseMatchers(numberOfRepeats, original.matchers, startIndex, endIndex);
				this.startArrayIndex = 0;
				this.endArrayIndex   = length;
			}
		}

		
		public ReverseByteMatcherSequenceMatcher(final ByteMatcherSequenceMatcher forwardMatcher) {
			ArgUtils.checkNullObject(forwardMatcher);
			this.length          = forwardMatcher.length;
			this.matchers        = forwardMatcher.matchers;
			this.startArrayIndex = forwardMatcher.startArrayIndex;
			this.endArrayIndex   = forwardMatcher.endArrayIndex;
		}

		
		public ReverseByteMatcherSequenceMatcher(final int repeats, final byte[] array,
												 final int startIndex, final int endIndex) {
	        ArgUtils.checkNullOrEmptyByteArray(array);
	        ArgUtils.checkIndexOutOfBounds(array.length, startIndex, endIndex);
	        ArgUtils.checkPositiveInteger(repeats, "numberOfRepeats");
	        final byte[] repeated = ByteUtils.repeat(repeats, array, startIndex, endIndex);
	        this.length = repeated.length;
	        this.startArrayIndex = 0;
	        this.endArrayIndex   = length;
	        this.matchers = new ByteMatcher[length];
	        populateMatchers(repeated, 0, length);
		}

		public ReverseByteMatcherSequenceMatcher(final byte byteValue) {
			this.matchers        = new ByteMatcher[] {OneByteMatcher.valueOf(byteValue)};
			this.length          = 1;
			this.startArrayIndex = 0;
			this.endArrayIndex   = 1;
		}

		
	    /******************
	     * Public methods *
	     ******************/
		
		
		@Override
		public boolean matches(WindowReader reader, long matchPosition) throws IOException {
            final int matchStart = startArrayIndex;
            final int matchLength = endArrayIndex - startArrayIndex;
            final int matchEnd = endArrayIndex - 1;
            final ByteMatcher[] matchArray = matchers;          
            Window window = reader.getWindow(matchPosition);
            int matchPos = matchEnd;
            int bytesMatchedSoFar = 0;
            while (window != null) {
                final byte[] source = window.getArray();            
                final int offset = reader.getWindowOffset(matchPosition + bytesMatchedSoFar);
                final int finalWindowIndex = window.length();
                final int finalMatchIndex = offset + matchLength - bytesMatchedSoFar;
                final int sourceEnd = finalWindowIndex < finalMatchIndex?
                                      finalWindowIndex : finalMatchIndex;
                for (int sourcePos = offset; sourcePos < sourceEnd; sourcePos++) {
                    if (!matchArray[matchPos--].matches(source[sourcePos])) {
                        return false;
                    }
                }
                if (matchPos < matchStart) {
                    return true;
                }
                bytesMatchedSoFar = matchEnd - matchPos;
                window = reader.getWindow(matchPosition + bytesMatchedSoFar);
            }
            return false;
		}

		
		@Override
		public boolean matches(byte[] bytes, int matchPosition) {
            if (matchPosition + length() <= bytes.length && matchPosition >= 0) {
                final ByteMatcher[] matchArray = matchers;
                final int endingIndex = startArrayIndex;
                int position = matchPosition;            
                for (int matchIndex = endArrayIndex - 1; matchIndex >= endingIndex; matchIndex--) {
                    if (!matchArray[matchIndex].matches(bytes[position++])) {
                        return false;
                    }
                }
                return true;
            }
            return false;
		}

		
		@Override
		public boolean matchesNoBoundsCheck(byte[] bytes, int matchPosition) {
            int position = matchPosition;
            final ByteMatcher[] matchArray = matchers;   
            final int endingIndex = startArrayIndex;
            for (int matchIndex = endArrayIndex - 1; matchIndex >= endingIndex; matchIndex--) {
                if (!matchArray[matchIndex].matches(bytes[position++])) {
                    return false;
                }
            }
            return true;
		}

		
		@Override
		public ByteMatcher getMatcherForPosition(final int position) {
			ArgUtils.checkIndexOutOfBounds(length, position);
			return matchers[endArrayIndex - position - 1];
		}
		
		
		@Override
		public int length() {
			return length;
		}
		

		@Override
		public SequenceMatcher reverse() {
			return new ByteMatcherSequenceMatcher(this);
		}

		
		@Override
		public SequenceMatcher subsequence(final int beginIndex, final int endIndex) {
            ArgUtils.checkIndexOutOfBounds(length(), beginIndex, endIndex);
            final int subsequenceLength = endIndex - beginIndex;
            if (subsequenceLength == length) {
                return this;
            }
            if (subsequenceLength == 1) {
                return matchers[endArrayIndex - beginIndex - 1];
            }
            return new ReverseByteMatcherSequenceMatcher(this, beginIndex, endIndex);
		}

		
		@Override
		public SequenceMatcher subsequence(final int beginIndex) {
			return subsequence(beginIndex, length);
		}
		

		@Override
		public SequenceMatcher repeat(final int numberOfRepeats) {
			ArgUtils.checkPositiveInteger(numberOfRepeats);
			if (numberOfRepeats == 1) {
				return this;
			}
			return new ReverseByteMatcherSequenceMatcher(numberOfRepeats, this);
		}
		

		@Override
		public String toRegularExpression(boolean prettyPrint) {
	        final StringBuilder builder = new StringBuilder(prettyPrint? length * 4 : length * 3);
	        boolean singleByte = false;
	        final List<Byte> singleBytes = new ArrayList<Byte>();
	        for (int index = endArrayIndex - 1; index >= startArrayIndex; index--) {
	        	final ByteMatcher matcher = matchers[index];
	        	if (matcher.getNumberOfMatchingBytes() == 1) {
	        		singleByte = true;
	        		singleBytes.add(Byte.valueOf(matcher.getMatchingBytes()[0]));
	        	} else {
	        		if (singleByte) {
	        			builder.append(ByteUtils.bytesToString(prettyPrint, singleBytes));
	        			singleBytes.clear();
	        			singleByte = false;
	        		}
	        		builder.append(matcher.toRegularExpression(prettyPrint));
	        	}
	        }
			if (singleByte) {
				builder.append(ByteUtils.bytesToString(prettyPrint, singleBytes));
			}
	        return builder.toString();
		}

		
		@Override
		public String toString() {
			return "ReverseByteMatcherSequenceMatcher." + getClass().getSimpleName() + '[' + toRegularExpression(true) + ']';
		}
		
		
		@Override
		public Iterator<ByteMatcher> iterator() {
			return new ReverseByteMatcherSequenceMatcherIterator();
		}

		private class ReverseByteMatcherSequenceMatcherIterator implements Iterator<ByteMatcher> {

			int position = endArrayIndex;
			
			@Override
			public boolean hasNext() {
				return position > startArrayIndex;
			}

			@Override
			public ByteMatcher next() {
				if (hasNext()) {
					return matchers[--position];
				}
				throw new NoSuchElementException();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("Cannot remove byte matchers from a ReverseBVyteMatcherSequenceMatcher");
				
			}
			
		}
		
		private void populateMatchers(final byte[] bytes, final int startIndex, final int endIndex) {
			int matcherPosition = 0;
			for (int position = startIndex; position < endIndex; position++) {
	        	matchers[matcherPosition++] = OneByteMatcher.valueOf(bytes[position]);
	        }
		}

	    private ByteMatcher[] repeatReverseMatchers(final int numberOfRepeats,
	    									 final ByteMatcher[] matchersToRepeat,
	    									 final int startIndex, final int endIndex) {
	        final int repeatSize = endIndex - startIndex;
	        final ByteMatcher[] repeated = new ByteMatcher[repeatSize * numberOfRepeats];
	        for (int repeat = 0; repeat < numberOfRepeats; repeat++) {
	            System.arraycopy(matchersToRepeat, startIndex, repeated, repeat * repeatSize, repeatSize);
	        }
	        return repeated;
	    }

		
	}

}
