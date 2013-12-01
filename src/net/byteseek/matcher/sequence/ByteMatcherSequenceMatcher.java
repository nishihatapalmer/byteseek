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
import java.util.Collection;
import java.util.Collections;
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
public final class ByteMatcherSequenceMatcher implements SequenceMatcher {



	private final ByteMatcher[] matchers;
    private final int length;

    
    /****************
     * Constructors *
     ***************/

    /**
     * Constructs an immutable ByteMatcherSequenceMatcher from an array of bytes, which 
     * can be passed in directly as an array of bytes, or specified as a comma-separated list of bytes.
     * The array of bytes passed in is cloned to avoid mutability and concurrency issues.
     * 
     * @param byteArray The array of bytes to match.
     * @throws IllegalArgumentException if the array of bytes passed in is null or empty.
     */
    public ByteMatcherSequenceMatcher(final byte...bytes) {
        ArgUtils.checkNullOrEmptyByteArray(bytes);
        this.length = bytes.length;
        this.matchers = new ByteMatcher[length];
        populateMatchers(bytes, 0, length);
    }


	private void populateMatchers(final byte[] bytes, final int startIndex, final int endIndex) {
		int matcherPosition = 0;
		for (int position = startIndex; position < endIndex; position++) {
        	matchers[matcherPosition++] = OneByteMatcher.valueOf(bytes[position]);
        }
	}

    
    public ByteMatcherSequenceMatcher(final byte[] array, final int startIndex, final int endIndex) {
        ArgUtils.checkNullOrEmptyByteArray(array);
        ArgUtils.checkIndexOutOfBounds(array.length, startIndex, endIndex);
        this.length = endIndex - startIndex;
        this.matchers = new ByteMatcher[length];
        populateMatchers(array, startIndex, endIndex);
    }

    
	public ByteMatcherSequenceMatcher(final int repeats, final byte[] array, final int startIndex, final int endIndex) {
        ArgUtils.checkNullOrEmptyByteArray(array);
        ArgUtils.checkIndexOutOfBounds(array.length, startIndex, endIndex);
        ArgUtils.checkPositiveInteger(repeats, "numberOfRepeats");
        final byte[] repeated = ByteUtils.repeat(repeats, array, startIndex, endIndex);
        this.length = repeated.length;
        this.matchers = new ByteMatcher[length];
        populateMatchers(repeated, 0, length);
	}


	public ByteMatcherSequenceMatcher(final List<ByteMatcherSequenceMatcher> list) {
		ArgUtils.checkNullOrEmptyCollectionNoNullElements(list);
		int totalLength = 0;
		for (final ByteMatcherSequenceMatcher sequence : list) {
			totalLength += sequence.length();
		}
		this.length = totalLength;
		this.matchers = new ByteMatcher[length];
		int matcherPos = 0;
		for (final ByteMatcherSequenceMatcher sequence : list) {
			for (final ByteMatcher matcher : sequence) {
				matchers[matcherPos++] = matcher;
			}
		}
	}


	public ByteMatcherSequenceMatcher(final ByteMatcherSequenceMatcher matcher, int startIndex, int endIndex) {
		ArgUtils.checkNullObject(matcher);
		ArgUtils.checkIndexOutOfBounds(matcher.length(), startIndex, endIndex);
		this.matchers = matcher.matchers;
		this.length = endIndex - startIndex; //This is a bug - we need to start at the start Index of the original matchers array.
		//TODO: must use start and end indexes in this.
	}
    
	
    /**
     * Constructs an immutable ByteMatcherSequenceMatcher from a repeated byte.
     *
     * @param byteValue The byte value to repeat.
     * @param numberOfBytes The number of bytes to repeat.
     * @throws IllegalArgumentException If the number of bytes is less than one.
     */
    public ByteMatcherSequenceMatcher(final byte byteValue, final int numberOfBytes) {
        ArgUtils.checkPositiveInteger(numberOfBytes);
        this.length = numberOfBytes;
        this.matchers = new ByteMatcher[length];
        Arrays.fill(this.matchers, OneByteMatcher.valueOf(byteValue));
    }

    
    /**
     * Constructs a ByteMatcherSequenceMatcher from a collection of {@link ByteMatcher} objects.
     * <p>
     * You should use a collection which gives a definite order to its elements, such as a List,
     * or a LinkedHashMap.
     *
     * @param sequence A list of SingleByteMatchers to construct this sequence matcher from.
     * @throws IllegalArgumentException if the list is null or empty or any elements in the collection are null.
     */
    public ByteMatcherSequenceMatcher(final Collection<? extends ByteMatcher> sequence) {
        ArgUtils.checkNullOrEmptyCollectionNoNullElements(sequence);
        matchers = sequence.toArray(new ByteMatcher[0]);
        this.length = this.matchers.length;
    }

    
    /**
     * Constructs a ByteMatcherSequenceMatcher from an array of {@link ByteMatcher}
     * objects.
     * 
     * @param sequence An array of SingleByteMatchers to construct this sequence matcher from.
     * @throws IllegalArgumentException if the array is null or empty or any element of it is null.
     */
    public ByteMatcherSequenceMatcher(final ByteMatcher[] sequence) {
        ArgUtils.checkNullOrEmptyArrayNoNullElements(sequence);
        this.matchers = sequence.clone();
        this.length = this.matchers.length;
    }
    
    
    /**
     * Constructs a ByteMatcherSequenceMatcher from a single {@link ByteMatcher} object.
     *
     * @param matcher The ByteMatcher to construct this sequence matcher from.
     * @throws IllegalArgumentException if the matcher is null.
     */
    public ByteMatcherSequenceMatcher(final ByteMatcher matcher) {
        ArgUtils.checkNullObject(matcher);
        this.matchers = new ByteMatcher[] {matcher};
        this.length = 1;
    }


    /**
     * Constructs a ByteMatcherSequenceMatcher from a repeated {@link ByteMatcher} object.
     *
     * @param matcher The ByteMatcher to construct this sequence matcher from.
     * @param repeats The number of times to repeat the ByteMatcher.
     * @throws IllegalArgumentException if the matcher is null or the number of repeats is less than one.
     */
    public ByteMatcherSequenceMatcher(final ByteMatcher matcher, final int repeats) {
        ArgUtils.checkNullObject(matcher);
        ArgUtils.checkPositiveInteger(repeats);
        this.length = repeats;
        this.matchers = new ByteMatcher[length];
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
     * Constructs a ByteMatcherSequenceMatcher from a string and a Charset to use
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
        this.length = bytes.length;
        this.matchers = new ByteMatcher[length];
        populateMatchers(bytes, 0, length);
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
    public ByteMatcherSequenceMatcher reverse() {
        final List<ByteMatcher> newList = Arrays.asList(matchers);
        Collections.reverse(newList);
        return new ByteMatcherSequenceMatcher(newList);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        final StringBuilder builder = new StringBuilder(length * 4);
        boolean singleByte = false;
        List<Byte> singleBytes = new ArrayList<Byte>();
        for (final ByteMatcher matcher : matchers) {
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
			singleBytes.clear();
			singleByte = false;
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
        return new ByteMatcherSequenceMatcher(Arrays.copyOfRange(matchers, beginIndex, endIndex));
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
    
    private ByteMatcher[] repeatMatchers(final int numberOfRepeats) {
        final int repeatSize = matchers.length;
        final ByteMatcher[] repeated = new ByteMatcher[repeatSize * numberOfRepeats];
        for (int repeat = 0; repeat < numberOfRepeats; repeat++) {
            System.arraycopy(matchers, 0, repeated, repeat * repeatSize, repeatSize);
        }
        return repeated;
    }
    
	@Override
	public Iterator<ByteMatcher> iterator() {
		return new ByteMatcherSequenceIterator();
	}

	private class ByteMatcherSequenceIterator implements Iterator<ByteMatcher> {

		int position = 0;
		
		@Override
		public boolean hasNext() {
			return position < length;
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

		public ReverseByteMatcherSequenceMatcher(byte[] array) {
			// TODO Auto-generated constructor stub
		}

		public ReverseByteMatcherSequenceMatcher(ReverseByteMatcherSequenceMatcher original, int i, int j) {
			// TODO Auto-generated constructor stub
		}

		public ReverseByteMatcherSequenceMatcher(ByteMatcherSequenceMatcher byteMatcherSequenceMatcher) {
			// TODO Auto-generated constructor stub
		}

		public ReverseByteMatcherSequenceMatcher(int i, Object object, int j, int k) {
			// TODO Auto-generated constructor stub
		}

		public ReverseByteMatcherSequenceMatcher(byte b) {
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean matches(WindowReader reader, long matchPosition) throws IOException {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean matches(byte[] bytes, int matchPosition) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public ByteMatcher getMatcherForPosition(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean matchesNoBoundsCheck(byte[] bytes, int matchPosition) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public int length() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public SequenceMatcher reverse() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SequenceMatcher subsequence(int beginIndex, int endIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SequenceMatcher subsequence(int beginIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SequenceMatcher repeat(int numberOfRepeats) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String toRegularExpression(boolean prettyPrint) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Iterator<ByteMatcher> iterator() {
			// TODO Auto-generated method stub
			return null;
		}

	}

}
