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
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import java.util.List;

import net.byteseek.bytes.ByteUtils;
import net.byteseek.io.reader.Window;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.bytes.ByteMatcher;
import net.byteseek.matcher.bytes.OneByteMatcher;
import net.byteseek.object.ArgUtils;

/**
 * An immutable class which matches a sequence of bytes.
 *
 * @author Matt Palmer
 */
public final class ByteArrayMatcher implements SequenceMatcher {
    
    private final byte[] byteArray;
    private final int startArrayIndex; // the position to start at (an inclusive value)
    private final int endArrayIndex;   // one past the actual end position (an exclusive value)

    /****************
     * Constructors *
     ***************/

    /**
     * Constructs an immutable byte sequence matcher from an array of bytes, which 
     * can be passed in directly as an array of bytes, or specified as a comma-separated list of bytes.
     * The array of bytes passed in is cloned to avoid mutability and concurrency issues.
     * 
     * @param byteArray The array of bytes to match.
     * @throws IllegalArgumentException if the array of bytes passed in is null or empty.
     */
    public ByteArrayMatcher(final byte...bytes) {
        ArgUtils.checkNullOrEmptyByteArray(bytes);
        this.byteArray = bytes.clone(); // avoid mutability issues - clone byte array.
        this.startArrayIndex = 0;
        this.endArrayIndex = byteArray.length;       	
    }

    
    /**
     * Constructor creating an immutable ByteArrayMatcher from a new byte array created
     * from the source to fit the start and end indexes supplied.  
     * A new array will be created even if the new array would be identical to the array passed in.
     * <p>
     * If you want to construct a ByteArrayMatcher which shares the underlying byte
     * arrays, then use the copy constructor passing in an existing ByteArrayMatcher: 
     * {@link #ByteArrayMatcher(ByteArrayMatcher, int, int)}. 
     *  
     * @param source A byte array containing a subsequence of bytes to use.
     * @param startIndex The start position of the source to begin from, inclusive.
     * @param endIndex The end position of the source, exclusive.
     * @throws IllegalArgumentException If the source is null or empty, the start or end index
     *         are out of bounds, or the number of repeats is less than one.
     */
    public ByteArrayMatcher(final byte[] source, final int startIndex, final int endIndex) {
    	this(1, source, startIndex, endIndex);
    }
    

    /**
     * Constructor creating an immutable ByteArrayMatcher from a new byte array created
     * to fit the start and end indexes supplied, repeated a number of times.
     * A new array will be created even if the new array would be identical to the array passed in.
     * 
     * @param numberOfRepeats The number of times to repeat the subsequence.
     * @param source A byte array containing a subsequence of bytes to repeat a number of times.
     * @param startIndex The start position of the source to begin from, inclusive.
     * @param endIndex The end position of the source, exclusive.
     * 
     * @throws IllegalArgumentException If the source is null or empty,the start or end index
     *         are out of bounds, or the number of repeats is less than one.
     */
    public ByteArrayMatcher(final int numberOfRepeats, 
                            final byte[] source, final int startIndex,
                            final int endIndex) {
        ArgUtils.checkNullOrEmptyByteArray(source);
        ArgUtils.checkIndexOutOfBounds(source.length, startIndex, endIndex);
        ArgUtils.checkPositiveInteger(numberOfRepeats, "numberOfRepeats");
        this.byteArray = ByteUtils.repeat(source, numberOfRepeats, startIndex, endIndex);
        this.startArrayIndex = 0;
        this.endArrayIndex = this.byteArray.length;
    }    
                    

    /**
     * Copy constructor creating an immutable sub-sequence of a ByteArrayMatcher passed in, 
     * and backed by the original byte array of the ByteArrayMatcher passed in.
     * 
     * @param source The ByteArrayMatcher to create a subsequence from.
     * @param startIndex The start position of the source to begin from.
     * @param endIndex The end position of the source, which is one greater than
     *                 the last position to match in the source array.
     * @throws IllegalArgumentException If the source is null, the start index is
     *         greater or equal to the end index, the start index is greater or
     *         equal to the length of the source, or the end index is greater than
     *         the length of the source.
     */
    public ByteArrayMatcher(final ByteArrayMatcher source, 
                            final int startIndex, final int endIndex) {
        ArgUtils.checkNullObject(source);
        ArgUtils.checkIndexOutOfBounds(source.length(), startIndex, endIndex);
        this.byteArray = source.byteArray;
        this.startArrayIndex = source.startArrayIndex + startIndex;
        this.endArrayIndex = source.startArrayIndex + endIndex;
    }
    
    
    /**
     * A constructor which creates a ByteArrayMatcher matching the 
     * array contained by a ReverseMatcher, but now matching forwards.
     * 
     * @param toReverse The ReverseMatcher to construct this ByteArrayMatcher from.
     * @throws IllegalArgumentException if a null ReverseMatcher is passed in.
     */
    public ByteArrayMatcher(final ReverseMatcher toReverse) {
        ArgUtils.checkNullObject(toReverse);
        this.byteArray= toReverse.byteArray;
        this.startArrayIndex = toReverse.startArrayIndex;
        this.endArrayIndex = toReverse.endArrayIndex;
    }
    
    
    /**
     * Constructs an immutable byte sequence matcher from a list of other
     * ByteArrayMatchers.  The final sequence to match is the sequence of
     * bytes defined by joining all the bytes in the other ByteArrayMatcher's
     * together in the order they appear in the list.
     *
     * @param matchers The list of ByteArrayMatchers to join.
     * @throws IllegalArgumentException if the matcher list is null or empty, or
     *         any of the ByteArrayMatchers in the list are null.
     */
    public ByteArrayMatcher(final List<ByteArrayMatcher> matchers) {
    	ArgUtils.checkNullOrEmptyCollectionNoNullElements(matchers);
        int totalLength = 0;
        for (final ByteArrayMatcher matcher : matchers) {
            totalLength += matcher.endArrayIndex;
        }
        this.byteArray = new byte[totalLength];
        int position = 0;
        for (final ByteArrayMatcher matcher : matchers) {
            System.arraycopy(matcher.byteArray, 0, this.byteArray, position, matcher.endArrayIndex);
            position += matcher.endArrayIndex;
        }
        this.startArrayIndex = 0;
        this.endArrayIndex = totalLength;
    }


    /**
     * Constructs an immutable byte sequence matcher from a repeated byte.
     *
     * @param byteValue The byte value to repeat.
     * @param numberOfBytes The number of bytes to repeat.
     * @throws IllegalArgumentException If the number of bytes is less than one.
     */
    public ByteArrayMatcher(final byte byteValue, final int numberOfBytes) {
        ArgUtils.checkPositiveInteger(numberOfBytes);
        this.byteArray = new byte[numberOfBytes];
        Arrays.fill(this.byteArray, byteValue);
        this.startArrayIndex = 0;
        this.endArrayIndex = numberOfBytes;
    }


    /**
     * Constructs an immutable byte sequence matcher from a single byte.
     *
     * @param byteValue The byte to match.
     */
    public ByteArrayMatcher(final byte byteValue) {
        this(byteValue, 1);
    }

    
    
    /**
     * Constructs an immutable ByteArrayMatcher from a string, encoding the
     * bytes of the string using the system default Charset.
     * 
     * @param string The string whose bytes will be matched.
     * @throws IllegalArgumentException if the string is null or empty.
     */
    public ByteArrayMatcher(final String string) {
        this(string, Charset.defaultCharset());
    }
    

    /**
     * Constructs an immutable ByteArrayMatcher from a repeated string, 
     * encoding the bytes of the string using the charset specified by name.
     * 
     * @param string The string whose bytes will be matched.
     * @param charsetName The name of the Charset to use to encode the bytes of the string.
     * @throws IllegalArgumentException if the string is null or empty, or the charsetName
     *         is null.
     * @throws IllegalCharsetNameException if the name of the charset is illegal.
     * @throws UnsupportedCharsetException if the charset is not supported.
     */
    public ByteArrayMatcher(final String string, final String charsetName) {
        this(string, Charset.forName(charsetName));
    }
    
    
    /**
     * Constructs a ByteArrayMatcher from a string and a Charset to use
     * to encode the bytes in the string.
     * 
     * @param string The string whose bytes will be matched
     * @param charset The Charset to encode the strings bytes in.
     * @throws IllegalArgumentException if the string is null or empty, or the
     *         Charset is null.
     */
    public ByteArrayMatcher(final String string, final Charset charset) {
        ArgUtils.checkNullOrEmptyString(string, "string");
        ArgUtils.checkNullObject(charset, "charset");
        this.byteArray = string.getBytes(charset);
        this.startArrayIndex = 0;
        this.endArrayIndex = byteArray.length;
    }
    
    
    /***********
     * Methods *
     **********/
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final WindowReader reader, final long matchPosition)
            throws IOException {
        final byte[] matchArray = byteArray;          
        final int matchStart = startArrayIndex;
        final int matchEnd = endArrayIndex;
        final int matchLength = matchEnd - matchStart;
        Window window = reader.getWindow(matchPosition);
        int matchPos = matchStart;
        int bytesMatchedSoFar = 0;
        while (window != null) {
            final byte[] source = window.getArray();    
            final int offset = reader.getWindowOffset(matchPosition + bytesMatchedSoFar);
            final int finalWindowIndex = window.length();
            final int finalMatchIndex = offset + matchLength - bytesMatchedSoFar;
            final int sourceEnd = finalWindowIndex < finalMatchIndex?
                                  finalWindowIndex : finalMatchIndex;
            for (int sourcePos = offset; sourcePos < sourceEnd; sourcePos++) {
                if (source[sourcePos] != matchArray[matchPos++]) {
                    return false;
                }
            }
            if (matchPos >= matchEnd) {
                return true;
            }
            bytesMatchedSoFar = matchPos - matchStart;
            window = reader.getWindow(matchPosition + bytesMatchedSoFar);
        }
        return false;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        if (matchPosition + endArrayIndex - startArrayIndex <= bytes.length && matchPosition >= 0) {
            final byte[] matchArray = byteArray;
            final int endingIndex = endArrayIndex;
            int position = matchPosition;            
            for (int matchIndex = startArrayIndex; matchIndex < endingIndex; matchIndex++) {
                if (matchArray[matchIndex] != bytes[position++]) {
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
        int position = matchPosition;
        final byte[] matchArray = byteArray;   
        final int endingIndex = endArrayIndex;
        for (int matchIndex = startArrayIndex; matchIndex < endingIndex; matchIndex++) {
            if (matchArray[matchIndex] != bytes[position++]) {
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
        return endArrayIndex - startArrayIndex;
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

    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        return ByteUtils.bytesToString(prettyPrint, byteArray, startArrayIndex, endArrayIndex);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ByteMatcher getMatcherForPosition(final int position) {
        return OneByteMatcher.valueOf(byteArray[position + startArrayIndex]);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override    
    public SequenceMatcher reverse() {
        return new ReverseMatcher(this);
    }

    
    /**
     * {@inheritDoc}
     */    
    @Override
    public SequenceMatcher subsequence(final int beginIndex, final int endIndex) {
        ArgUtils.checkIndexOutOfBounds(length(), beginIndex, endIndex);
        final int subsequenceLength = endIndex - beginIndex;
        if (subsequenceLength == 1) {
            return OneByteMatcher.valueOf(byteArray[startArrayIndex + beginIndex]);
        }
        if (subsequenceLength == length()) {
            return this;
        }
        return new ByteArrayMatcher(this, beginIndex, endIndex);
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
        return new ByteArrayMatcher(numberOfRepeats, byteArray, startArrayIndex, endArrayIndex);
    }
    
    
    
    ////////////////////////////////////////////////////////////////////////////
    //                                ReverseMatcher                          //        
    ////////////////////////////////////////////////////////////////////////////
    
    
    /**
     * A ReverseMatcher is a matcher which matches the reverse order of bytes in a byte array.
     * 
     */
    public static final class ReverseMatcher implements SequenceMatcher {
         
         private final byte[] byteArray;
         private final int startArrayIndex; // the position to start in the array (inclusive)
         private final int endArrayIndex;   // one past the last position in the array (exclusive)
         
         
         /**
          * Constructs a ReverseMatcher from an original ByteArrayMatcher.
          * 
          * @param toReverse The ByteArrayMatcher to construct a ReverseMatcher from.
          * @throws IllegalArgumentException if the ByteArrayMatcher is null.
          */
         public ReverseMatcher(final ByteArrayMatcher toReverse) {
             ArgUtils.checkNullObject(toReverse);
             this.byteArray = toReverse.byteArray;
             this.startArrayIndex = toReverse.startArrayIndex;
             this.endArrayIndex = toReverse.endArrayIndex;
         }
         
         
         /**
          * Constructs a ReverseMatcher directly from a byte array.  The byte array
          * is cloned, making the ReverseMatcher immutable.
          * 
          * @param array The array to clone and construct a ReverseMatcher from.
          * @throws IllegalArgumentException if the byte array is null or empty.
          */
         public ReverseMatcher(final byte[] array) {
        	 ArgUtils.checkNullOrEmptyByteArray(array);
             this.byteArray = array.clone();
             this.startArrayIndex = 0;
             this.endArrayIndex = array.length;
         }
         
         
        /**
         * Copy constructor creating an immutable sub-sequence of another ReverseMatcher, 
         * backed by the original byte array, but otherwise behaving as if the array
         * had been reversed.  In particular, start indexes and end indexes should be
         * interpreted with that in mind - they reference the byte array as if it
         * was actually reversed.  Translation to the underlying byte array indexes
         * is done automatically and transparently.
         * 
         * @param source The ByteArrayMatcher to create a subsequence from.
         * @param startIndex The start position of the source to begin from, inclusive.
         * @param endIndex The end position of the source, exclusive.
         * @throws IllegalArgumentException if the source is null, or the start or end index are out of bounds.
         */
        public ReverseMatcher(final ReverseMatcher source, 
                              final int startIndex, final int endIndex) {
            ArgUtils.checkNullObject(source);
            ArgUtils.checkIndexOutOfBounds(source.length(), startIndex, endIndex);
            this.byteArray = source.byteArray;
            this.startArrayIndex = source.startArrayIndex + source.length() - endIndex;
            this.endArrayIndex = source.endArrayIndex - startIndex;
        }         
               
        
        /**
         * Constructs a ReverseMatcher from a source byte array, a start index
         * and an end index, repeated a number of times.
         * 
         * @param source The source array to construct a ReverseMatcher from.
         * @param startIndex The first position in the source array to repeat from, inclusive.
         * @param endIndex The endIndex in the source array to repeat up to, exclusive.
         * @param numberOfRepeats The number of times to repeat the source array bytes.
         * @throws IllegalArgumentException if the source is null or empty, the start index
         *        is greater than or equal to the end index, the start index is
         *        greater than or equal to the source length, or the end index is
         *        greater than the source length, or the number of repeats is less than one.
         */
        public ReverseMatcher(final byte[] source,
                              final int startIndex, final int endIndex,
                              final int numberOfRepeats) {
            ArgUtils.checkNullOrEmptyByteArray(source);
            ArgUtils.checkIndexOutOfBounds(source.length, startIndex, endIndex);
            ArgUtils.checkPositiveInteger(numberOfRepeats, "numberOfRepeats");
            this.byteArray = ByteUtils.repeat(source, startIndex, endIndex, numberOfRepeats);
            this.startArrayIndex = 0;
            this.endArrayIndex = this.byteArray.length;            
        }
        
        //FIXME: infinite loop when crossing windows.
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean matches(final WindowReader reader, final long matchPosition)
                throws IOException {
            final int matchStart = startArrayIndex;
            final int matchLength = endArrayIndex - startArrayIndex;
            final int matchEnd = endArrayIndex - 1;
            final byte[] matchArray = byteArray;          
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
                    if (source[sourcePos] != matchArray[matchPos--]) {
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


        /**
         * {@inheritDoc}
         */
        @Override
        public boolean matches(final byte[] bytes, final int matchPosition) {
            if (matchPosition + length() <= bytes.length && matchPosition >= 0) {
                final byte[] matchArray = byteArray;
                final int endingIndex = startArrayIndex;
                int position = matchPosition;            
                for (int matchIndex = endArrayIndex - 1; matchIndex >= endingIndex; matchIndex--) {
                    if (matchArray[matchIndex] != bytes[position++]) {
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
            int position = matchPosition;
            final byte[] matchArray = byteArray;   
            final int endingIndex = startArrayIndex;
            for (int matchIndex = endArrayIndex - 1; matchIndex >= endingIndex; matchIndex--) {
                if (matchArray[matchIndex] != bytes[position++]) {
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
            return endArrayIndex - startArrayIndex;
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
            return getClass().getSimpleName() + "[" + toRegularExpression(true) + ']';
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public String toRegularExpression(final boolean prettyPrint) {
            //FIXME: can we have a reverseBytesToString method instead...?
            //       current method creates a new byte array just to print the bytes out.
            return ByteUtils.bytesToString(prettyPrint, 
                    ByteUtils.reverseArraySubsequence(byteArray, startArrayIndex, endArrayIndex));
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public ByteMatcher getMatcherForPosition(final int position) {
            ArgUtils.checkIndexOutOfBounds(length(), position);
            return OneByteMatcher.valueOf(byteArray[endArrayIndex - 1 - position]);
        }


        /**
         * {@inheritDoc}
         */
        @Override    
        public SequenceMatcher reverse() {
            return new ByteArrayMatcher(this);
        }

        
        /**
         * {@inheritDoc}
         */    
        @Override
        public SequenceMatcher subsequence(final int beginIndex, final int endIndex) {
            ArgUtils.checkIndexOutOfBounds(length(), beginIndex, endIndex);
            final int subsequenceLength = endIndex - beginIndex;
            if (subsequenceLength == length()) {
                return this;
            }
            if (subsequenceLength == 1) {
                return OneByteMatcher.valueOf(byteArray[this.endArrayIndex - beginIndex - 1]);
            }
            return new ReverseMatcher(this, beginIndex, endIndex);
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
            return new ReverseMatcher(byteArray, startArrayIndex, endArrayIndex, numberOfRepeats);
        }
         
    }
    
}
