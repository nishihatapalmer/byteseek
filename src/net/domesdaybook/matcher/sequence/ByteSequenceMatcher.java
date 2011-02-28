/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */


package net.domesdaybook.matcher.sequence;

import java.util.Collection;
import java.util.List;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.matcher.singlebyte.ByteMatcher;
import net.domesdaybook.reader.ByteReader;

/**
 * An immutable class which matches a sequence of bytes.
 * Since the class is immutable, it is entirely thread-safe.
 *
 * @author Matt Palmer
 */
public final class ByteSequenceMatcher implements SequenceMatcher {

    public static final int QUOTE_CHARACTER_VALUE = 39;
    public static final int START_PRINTABLE_ASCII = 32;
    public static final int END_PRINTABLE_ASCII = 126;

    private final byte[] byteArray;
    private final int length;


    /**
     * Constructs an immutable byte sequence matcher from an array of bytes.
     * The array of bytes passed in is cloned to avoid mutability
     * and concurrency issues.
     * 
     * @param byteArray The array of bytes to match.
     * @throws IllegalArgumentException if the array of bytes passed in is null.
     */
    public ByteSequenceMatcher(final byte[] byteArray ) {
        // Preconditions byteArray is not null:
        if (byteArray == null) {
            throw new IllegalArgumentException("Null byte array passed in to ByteSequenceMatcher");
        }
        this.byteArray = byteArray.clone(); // avoid mutability issues - clone byte array.
        length = byteArray.length;
    }


    /**
     * Constructs an immutable byte sequence matcher from a collection of Bytes.
     *
     * @param byteList The collection of Bytes to match.
     * @throws IllegalArgumentException if the byteList is empty or null.
     */
    public ByteSequenceMatcher(final Collection<Byte> byteList) {
        // Preconditions: list is not null and has at least one member:
        if (byteList == null || byteList.isEmpty()) {
            throw new IllegalArgumentException("Null or empty byte list passed in to ByteSequenceMatcher.");
        }
        this.byteArray = new byte[byteList.size()];
        int index = 0;
        for (Byte b : byteList) {
            this.byteArray[index++] = b;
        }
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
        for (ByteSequenceMatcher matcher : matchers) {
            totalLength += matcher.length;
        }
        this.byteArray = new byte[totalLength];
        int position = 0;
        for (ByteSequenceMatcher matcher : matchers) {
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
        for (int count = 0; count < numberOfBytes; count++) {
            this.byteArray[count] = byteValue;
        }
    }


    /**
     * Constructs an immutable byte sequence matcher from a single byte.
     *
     * @param byteValue The byte to match.
     */
    public ByteSequenceMatcher(final byte byteValue) {
        //this.byteArray = new byte[1];
        //this.byteArray[0] = byteValue;
        this.byteArray = new byte[] {byteValue};
        length = 1;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean matches(final ByteReader reader, final long matchFrom) {
        boolean result = true;
        final byte[] localArray = byteArray;
        final int localStop = length;
        for ( int byteIndex = 0; result && byteIndex < localStop; byteIndex++) {
            result = ( localArray[byteIndex] == reader.readByte( matchFrom + byteIndex ));
        }
        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final int length() {
        return length;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final String toRegularExpression( final boolean prettyPrint ) {
        return bytesToString(prettyPrint, byteArray);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final SingleByteMatcher getByteMatcherForPosition(int position) {
        return new ByteMatcher(byteArray[position]);
    }


    private String bytesToString(final boolean prettyPrint, byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        boolean inString = false;
        for (int byteIndex=0, byteLength = bytes.length; byteIndex<byteLength; byteIndex++) {
            int byteValue = 0xFF & bytes[byteIndex];
            if ( prettyPrint &&
                    byteValue >= START_PRINTABLE_ASCII &&
                    byteValue <= END_PRINTABLE_ASCII &&
                    byteValue != QUOTE_CHARACTER_VALUE) {
                final String formatString = inString ? "%c" : " '%c";
                hexString.append( String.format( formatString, (char) byteValue ));
                inString = true;
            } else {
                 final String formatString = prettyPrint? inString? "' %02x" : "%02x" : "%02x";
                hexString.append( String.format(formatString, byteValue ));
                inString = false;
            }
        }
        if (prettyPrint && inString) {
            hexString.append( "' ");
        }
        return hexString.toString();
    }

}
