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
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.domesdaybook.matcher.bytes.ByteMatcher;
import net.domesdaybook.matcher.bytes.OneByteMatcher;
import net.domesdaybook.util.bytes.ByteUtilities;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.reader.Window;

/**
 * An immutable class which matches a sequence of bytes.
 *
 * @author Matt Palmer
 */
public final class ByteArrayMatcher implements SequenceMatcher {

    private final byte[] byteArray;
    private final int startIndex;
    private final int endIndex;

    /****************
     * Constructors *
     ***************/

    /**
     * Constructs an immutable byte sequence matcher from an array of bytes.
     * The array of bytes passed in is cloned to avoid mutability
     * and concurrency issues.
     * 
     * @param byteArray The array of bytes to match.
     * @throws IllegalArgumentException if the array of bytes passed in is null or empty.
     */
    public ByteArrayMatcher(final byte[] byteArray) {
        if (byteArray == null || byteArray.length == 0) {
            throw new IllegalArgumentException("Null or empty byte array passed in to ByteArrayMatcher constructor.");
        }
        this.byteArray = byteArray.clone(); // avoid mutability issues - clone byte array.
        this.startIndex = 0;
        this.endIndex = byteArray.length;   
    }

    
    /**
     * Copy constructor creating an immutable sub-sequence of another ByteArrayMatcher, 
     * backed by the original byte array.
     * 
     * @param source The ByteArrayMatcher to create a subsequence from.
     * @param startIndex The start position of the source to begin from.
     * @param endIndex The end position of the source, which is one greater than
     *                 the last position to match in the source array.
     */
    public ByteArrayMatcher(final ByteArrayMatcher source, 
                            final int startIndex, final int endIndex) {
        if (source == null) {
            throw new IllegalArgumentException("Null ByteArrayMatcher source passed in to ByteArrayMatcher constructor.");
        }
        if (startIndex >= endIndex) {
            final String message = "The start %d is past the end %d for source %s";
            throw new IllegalArgumentException(String.format(message, startIndex, endIndex - 1, source));
        }
        if (startIndex >= source.length()) {
            final String message = "Start position %d is past the end of the source %s, length = %d.";
            throw new IllegalArgumentException(String.format(message, startIndex, source, source.length()));
        }
        if (endIndex > source.length()) {
            final String message = "The end %d is past the end the source %s, length = %d";
            throw new IllegalArgumentException(String.format(message, endIndex, source, source.length()));
        }
        this.byteArray = source.byteArray;
        this.startIndex = source.startIndex + startIndex;
        this.endIndex = source.startIndex + endIndex;
    }
    

    /**
     * Constructor creating an immutable ByteArrayMatcher from another ByteArrayMatcher,
     * backed by a new byte array containing the subsequence defined by the start
     * and end indexes, repeated a number of times.
     * 
     * @param source The ByteArrayMatcher to create a subsequence from.
     * @param startIndex The start position of the source to begin from.
     * @param endIndex The end position of the source, which is one greater than
     *                 the last position to match in the source array.
     */
    public ByteArrayMatcher(final byte[] source, 
                            final int startIndex, final int endIndex,
                            final int numberOfRepeats) {
        if (source == null) {
            throw new IllegalArgumentException("Null ByteArrayMatcher source passed in to ByteArrayMatcher constructor.");
        }
        if (startIndex > endIndex) {
            final String message = "The start %d is past the end %d for source";
            throw new IllegalArgumentException(String.format(message, startIndex, endIndex));
        }
        if (startIndex > source.length - 1) {
            final String message = "Start position %d is past the end of the source, length = %d.";
            throw new IllegalArgumentException(String.format(message, startIndex, source.length));
        }
        if (endIndex > source.length) {
            final String message = "The end %d is past the end the source, length = %d";
            throw new IllegalArgumentException(String.format(message, endIndex, source.length));
        }
        this.byteArray = ByteUtilities.repeat(source, startIndex, endIndex, numberOfRepeats);
        this.startIndex = 0;
        this.endIndex = this.byteArray.length;
    }    
                    
    
    public ByteArrayMatcher(final ReverseMatcher toReverse) {
        this.byteArray= toReverse.byteArray;
        this.startIndex = toReverse.startIndex;
        this.endIndex = toReverse.endIndex;
    }
    
    
    /**
     * Constructs an immutable byte sequence matcher from a collection of Bytes.
     *
     * @param byteList The collection of Bytes to match.
     * @throws IllegalArgumentException if the byteList is empty or null.
     */
    public ByteArrayMatcher(final Collection<Byte> byteList) {
        if (byteList == null || byteList.isEmpty()) {
            throw new IllegalArgumentException("Null or empty byte list passed in to ByteSequenceMatcher.");
        }
        this.byteArray = ByteUtilities.toArray(byteList);
        this.startIndex = 0;
        this.endIndex = byteArray.length;
    }


    /**
     * Constructs an immutable byte sequence matcher from a list of other
     * ByteSequenceMatchers.  The final sequence to match is the sequence of
     * bytes defined by joining all the bytes in the other ByteArrayMatcher's
     * together in the order they appear in the list.
     *
     * @param matchers The list of ByteSequenceMatchers to join.
     * @throws IllegalArgumentException if the matcher list is null or empty.
     */
    public ByteArrayMatcher(final List<ByteArrayMatcher> matchers) {
        if (matchers == null || matchers.isEmpty()) {
            throw new IllegalArgumentException("Null or empty matcher list passed in to ByteSequenceMatcher.");
        }
        int totalLength = 0;
        for (final ByteArrayMatcher matcher : matchers) {
            totalLength += matcher.endIndex;
        }
        this.byteArray = new byte[totalLength];
        int position = 0;
        for (final ByteArrayMatcher matcher : matchers) {
            System.arraycopy(matcher.byteArray, 0, this.byteArray, position, matcher.endIndex);
            position += matcher.endIndex;
        }
        this.startIndex = 0;
        this.endIndex = totalLength;
    }


    /**
     * Constructs an immutable byte sequence matcher from a repeated byte.
     *
     * @param byteValue The byte value to repeat.
     * @param numberOfBytes The number of bytes to repeat.
     * @throws IllegalArgumentException If the number of bytes is less than one.
     */
    public ByteArrayMatcher(final byte byteValue, final int numberOfBytes) {
        if (numberOfBytes < 1) {
            throw new IllegalArgumentException("ByteSequenceMatcher requires a positive number of bytes.");
        }
        this.byteArray = new byte[numberOfBytes];
        Arrays.fill(this.byteArray, byteValue);
        this.startIndex = 0;
        this.endIndex = numberOfBytes;
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
     */
    public ByteArrayMatcher(final String string) {
        this(string, Charset.defaultCharset());
    }
    

    /**
     * Constructs an immutable ByteArrayMatcher from a repeated string, 
     * encoding the bytes of the string using the default Charset.
     * 
     * @param string
     * @param charsetName
     * @param numberOfRepeats 
     * @throws UnsupportedCharsetException
     *         If no support for the named charset is available
     *         in this instance of the Java virtual machine
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
     * @param numberOfRepeats the number of times to repeat the string.
     * @throws IllegalArgumentException if the string is null or empty, or the
     *         Charset is null, or the numberOfRepeats is less than one.
     */
    public ByteArrayMatcher(final String string, final Charset charset) {
        if (string == null || string.isEmpty()) {
            throw new IllegalArgumentException("Null or empty string passed in to ByteSequenceMatcher constructor");
        }
        if (charset == null) {
            throw new IllegalArgumentException("Null charset passed in to ByteSequenceMatcher constructor.");
        }
        this.byteArray = string.getBytes(charset);
        this.startIndex = 0;
        this.endIndex = byteArray.length;
    }
    
    
    /***********
     * Methods *
     **********/
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final Reader reader, final long matchPosition)
            throws IOException {
        final byte[] matchArray = byteArray;          
        final int matchStart = startIndex;
        final int matchEnd = endIndex;
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
        if (matchPosition + endIndex - startIndex <= bytes.length && matchPosition >= 0) {
            final byte[] matchArray = byteArray;
            final int endingIndex = endIndex;
            int position = matchPosition;            
            for (int matchIndex = startIndex; matchIndex < endingIndex; matchIndex++) {
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
        final int endingIndex = endIndex;
        for (int matchIndex = startIndex; matchIndex < endingIndex; matchIndex++) {
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
        return endIndex - startIndex;
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

    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        return ByteUtilities.bytesToString(prettyPrint, byteArray, startIndex, endIndex);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ByteMatcher getMatcherForPosition(final int position) {
        return new OneByteMatcher(byteArray[position + startIndex]);
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
        if (beginIndex < 0 || endIndex > length() || beginIndex >= endIndex) {
            final String message = "Subsequence index %d to %d is out of bounds in a sequence of length %d";
            throw new IndexOutOfBoundsException(String.format(message, beginIndex, endIndex, length()));
        }
        final int subsequenceLength = endIndex - beginIndex;
        if (subsequenceLength == 1) {
            return new OneByteMatcher(byteArray[beginIndex]);
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
        if (numberOfRepeats < 1) {
            throw new IllegalArgumentException("Number of repeats must be at least one.");
        }
        if (numberOfRepeats == 1) {
            return this;
        }
        return new ByteArrayMatcher(byteArray, startIndex, endIndex, numberOfRepeats);
    }
    
    
    
    ///////////////////////////////////////////////////////////////////////////////////
    //                                   ReverseMatcher
    ///////////////////////////////////////////////////////////////////////////////////
    
    
    
    public static final class ReverseMatcher implements SequenceMatcher {
         
         private final byte[] byteArray;
         private final int startIndex;
         private final int endIndex;
         
         
         public ReverseMatcher(final ByteArrayMatcher toReverse) {
             this.byteArray = toReverse.byteArray;
             this.startIndex = toReverse.startIndex;
             this.endIndex = toReverse.endIndex;
         }
         
         
         public ReverseMatcher(final byte[] array) {
             if (array == null) {
                 throw new IllegalArgumentException("Null array passed in to constructor.");
             }
             this.byteArray = array.clone();
             this.startIndex = 0;
             this.endIndex = array.length;
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
         * @param startIndex The start position of the source to begin from.
         * @param endIndex The end position of the source, which is one greater than
         *                 the last position to match in the source array.
         */
        public ReverseMatcher(final ReverseMatcher source, 
                              final int startIndex, final int endIndex) {
            if (source == null) {
                throw new IllegalArgumentException("Null ReverseMatcher passed in to constructor.");
            }
            if (startIndex >= endIndex) {
                final String message = "The start %d is past the end %d for source %s";
                throw new IllegalArgumentException(String.format(message, startIndex, endIndex - 1, source));
            }
            if (startIndex >= source.length()) {
                final String message = "Start position %d is past the end of the source %s, length = %d.";
                throw new IllegalArgumentException(String.format(message, startIndex, source, source.length()));
            }
            if (endIndex > source.length()) {
                final String message = "The endIndex %d is past the end of the source %s, length = %d";
                throw new IllegalArgumentException(String.format(message, endIndex, source, source.length()));
            }
            this.byteArray = source.byteArray;
            this.startIndex = source.startIndex + endIndex - startIndex - 1;
            this.endIndex = source.endIndex - startIndex;
        }         
               
        
        public ReverseMatcher(final byte[] source,
                              final int startIndex, final int endIndex,
                              final int numberOfRepeats) {
            if (source == null) {
                throw new IllegalArgumentException("Null ReverseMatcher passed in to constructor.");
            }
            if (startIndex > endIndex) {
                final String message = "The start %d is past the end %d for source";
                throw new IllegalArgumentException(String.format(message, startIndex, endIndex));
            }
            if (startIndex > source.length - 1) {
                final String message = "Start position %d is past the end of the source, length = %d.";
                throw new IllegalArgumentException(String.format(message, startIndex, source.length));
            }
            if (endIndex > source.length) {
                final String message = "The endIndex %d is past the end of the source, length = %d";
                throw new IllegalArgumentException(String.format(message, endIndex, source.length));
            }
            this.byteArray = ByteUtilities.repeat(source, startIndex, endIndex,
                                                  numberOfRepeats);
            this.startIndex = 0;
            this.endIndex = this.byteArray.length;            
        }
        
        //FIXME: infinite loop when crossing windows.
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean matches(final Reader reader, final long matchPosition)
                throws IOException {
            final int matchStart = startIndex;
            final int matchLength = endIndex - startIndex;
            final int matchEnd = endIndex - 1;
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
                final int endingIndex = startIndex;
                int position = matchPosition;            
                for (int matchIndex = endIndex - 1; matchIndex >= endingIndex; matchIndex--) {
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
            final int endingIndex = startIndex;
            for (int matchIndex = endIndex - 1; matchIndex >= endingIndex; matchIndex--) {
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
            return endIndex - startIndex;
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
            return "ByteArrayMatcher$" + getClass().getSimpleName() + "(" + toRegularExpression(true) + ")";
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public String toRegularExpression(final boolean prettyPrint) {
            //FIXME: can we have a reverseBytesToString method instead...?
            //       current method creates a new byte array just to print the bytes out.
            return ByteUtilities.bytesToString(prettyPrint, 
                    ByteUtilities.reverseArraySubsequence(byteArray, startIndex, endIndex));
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public ByteMatcher getMatcherForPosition(final int position) {
            if (position < 0 || position >= length()) {
                final String message = "Position %d is out of bounds in matcher of length %d";
                throw new ArrayIndexOutOfBoundsException(String.format(message, position, length()));
            }
            return new OneByteMatcher(byteArray[endIndex - 1 - position]);
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
            if (beginIndex < 0 || endIndex > length() || beginIndex >= endIndex) {
                final String message = "Subsequence index %d to %d is out of bounds in a sequence of length %d";
                throw new IndexOutOfBoundsException(String.format(message, beginIndex, endIndex, length()));
            }
            final int subsequenceLength = endIndex - beginIndex;
            if (subsequenceLength == 1) {
                return new OneByteMatcher(byteArray[beginIndex]);
            }
            if (subsequenceLength == length()) {
                return this;
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
            if (numberOfRepeats < 1) {
                throw new IllegalArgumentException("Number of repeats must be at least one.");
            }
            if (numberOfRepeats == 1) {
                return this;
            }
            return new ReverseMatcher(byteArray, startIndex, endIndex, numberOfRepeats);
        }
         
    }
    
}
