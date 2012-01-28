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
import net.domesdaybook.bytes.ByteUtilities;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.reader.Window;

/**
 * An immutable class which matches a sequence of bytes.
 *
 * @author Matt Palmer
 */
public final class ByteSequenceMatcher implements SequenceMatcher {

    private final byte[] byteArray;
    private final int length;


    /**
     * Constructs an immutable byte sequence matcher from an array of bytes.
     * The array of bytes passed in is cloned to avoid mutability
     * and concurrency issues.
     * 
     * @param byteArray The array of bytes to match.
     * @throws IllegalArgumentException if the array of bytes passed in is null or empty.
     */
    public ByteSequenceMatcher(final byte[] byteArray) {
        if (byteArray == null || byteArray.length == 0) {
            throw new IllegalArgumentException("Null or empty byte array passed in to ByteSequenceMatcher");
        }
        this.byteArray = byteArray.clone(); // avoid mutability issues - clone byte array.
        this.length = byteArray.length;     
    }

    
    /**
     * Constructs an immutable byte sequence matcher from a collection of Bytes.
     *
     * @param byteList The collection of Bytes to match.
     * @throws IllegalArgumentException if the byteList is empty or null.
     */
    public ByteSequenceMatcher(final Collection<Byte> byteList) {
        if (byteList == null || byteList.isEmpty()) {
            throw new IllegalArgumentException("Null or empty byte list passed in to ByteSequenceMatcher.");
        }
        this.byteArray = ByteUtilities.toArray(byteList);
        length = byteArray.length;
    }


    /**
     * Constructs an immutable byte sequence matcher from a list of other
     * ByteSequenceMatchers.  The final sequence to match is the sequence of
     * bytes defined by joining all the bytes in the other ByteSequenceMatcher's
     * together in the order they appear in the list.
     *
     * @param matchers The list of ByteSequenceMatchers to join.
     * @throws IllegalArgumentException if the matcher list is null or empty.
     */
    public ByteSequenceMatcher(final List<ByteSequenceMatcher> matchers) {
        if (matchers == null || matchers.isEmpty()) {
            throw new IllegalArgumentException("Null or empty matcher list passed in to ByteSequenceMatcher.");
        }
        int totalLength = 0;
        for (final ByteSequenceMatcher matcher : matchers) {
            totalLength += matcher.length;
        }
        this.byteArray = new byte[totalLength];
        int position = 0;
        for (final ByteSequenceMatcher matcher : matchers) {
            System.arraycopy(matcher.byteArray, 0, this.byteArray, position, matcher.length);
            position += matcher.length;
        }
        length = totalLength;
    }


    /**
     * Constructs an immutable byte sequence matcher from a repeated byte.
     *
     * @param byteValue The byte value to repeat.
     * @param numberOfBytes The number of bytes to repeat.
     * @throws IllegalArgumentException If the number of bytes is less than one.
     */
    public ByteSequenceMatcher(final byte byteValue, final int numberOfBytes) {
        if (numberOfBytes < 1) {
            throw new IllegalArgumentException("ByteSequenceMatcher requires a positive number of bytes.");
        }
        length = numberOfBytes;
        this.byteArray = new byte[numberOfBytes];
        Arrays.fill(this.byteArray, byteValue);
    }


    /**
     * Constructs an immutable byte sequence matcher from a single byte.
     *
     * @param byteValue The byte to match.
     */
    public ByteSequenceMatcher(final byte byteValue) {
        this(byteValue, 1);
    }

    
    
    /**
     * Constructs an immutable ByteSequenceMatcher from a string, encoding the
     * bytes of the string using the system default Charset.
     * 
     * @param string The string whose bytes will be matched.
     */
    public ByteSequenceMatcher(final String string) {
        this(string, Charset.defaultCharset());
    }
    

    /**
     * Constructs an immutable ByteSequenceMatcher from a repeated string, 
     * encoding the bytes of the string using the default Charset.
     * 
     * @param string
     * @param charsetName
     * @param numberOfRepeats 
     * @throws UnsupportedCharsetException
     *         If no support for the named charset is available
     *         in this instance of the Java virtual machine
     */
    public ByteSequenceMatcher(final String string, final String charsetName) {
        this(string, Charset.forName(charsetName));
    }
    
    
    /**
     * Constructs a ByteSequenceMatcher from a string and a Charset to use
     * to encode the bytes in the string.
     * 
     * @param string The string whose bytes will be matched
     * @param charset The charset to encode the strings bytes in.
     * @param numberOfRepeats the number of times to repeat the string.
     * @throws IllegalArgumentException if the string is null or empty, or the
     *         Charset is null, or the numberOfRepeats is less than one.
     */
    public ByteSequenceMatcher(final String string, final Charset charset) {
        if (string == null || string.isEmpty()) {
            throw new IllegalArgumentException("Null or empty string passed in to ByteSequenceMatcher constructor");
        }
        if (charset == null) {
            throw new IllegalArgumentException("Null charset passed in to ByteSequenceMatcher constructor.");
        }
        this.byteArray = string.getBytes(charset);
        this.length = byteArray.length;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final Reader reader, final long matchPosition)
            throws IOException {
        final int localLength = length;
        final byte[] localArray = byteArray;          
        Window window = reader.getWindow(matchPosition);
        int checkPos = 0;
        while (window != null) {
            final int offset = reader.getWindowOffset(matchPosition + checkPos);
            final int endPos = Math.min(window.length(), offset + localLength - checkPos);
            final byte[] array = window.getArray();
            for (int windowPos = offset; windowPos < endPos; windowPos++) {
                if (array[windowPos] != localArray[checkPos++]) {
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
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        if (matchPosition + length <= bytes.length && matchPosition >= 0) {
            int position = matchPosition;
            final byte[] localArray = byteArray;
            for (final byte value : localArray) {
                if (value != bytes[position++]) {
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
        final byte[] localArray = byteArray;        
        for (final byte value : localArray) {
            if (value != bytes[position++]) {
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
        return ByteUtilities.bytesToString(prettyPrint, byteArray);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ByteMatcher getMatcherForPosition(final int position) {
        return new OneByteMatcher(byteArray[position]);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override    
    public ByteSequenceMatcher reverse() {
        final byte[] reverseArray = ByteUtilities.reverseArray(byteArray);
        return new ByteSequenceMatcher(reverseArray);
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
            return new OneByteMatcher(byteArray[beginIndex]);
        }
        return new ByteSequenceMatcher(Arrays.copyOfRange(byteArray, beginIndex, endIndex));
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
        return new ByteSequenceMatcher(ByteUtilities.repeat(byteArray, numberOfRepeats));
    }
    
}
