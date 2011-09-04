/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */


package net.domesdaybook.matcher.sequence;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.matcher.singlebyte.ByteMatcher;
import net.domesdaybook.bytes.ByteUtilities;
import net.domesdaybook.reader.ByteReader;

/**
 * An immutable class which matches a sequence of bytes.
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
    public ByteSequenceMatcher(final byte[] byteArray) {
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
        for (final Byte b : byteList) {
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
        // Preconditions: list is not null and has at least one member:
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
        // Preconditions: at least one byte to repeat.
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
        this.byteArray = new byte[] {byteValue};
        length = 1;
    }

    
    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public final boolean matches(final ByteReader reader, final long matchFrom) {
        final int localLength = length;
        if (matchFrom + localLength < reader.length() && matchFrom >= 0) {
            final byte[] localArray = byteArray;
            for (int byteIndex = 0; byteIndex < localLength; byteIndex++) {
                if (!(localArray[byteIndex] == reader.readByte(matchFrom + byteIndex))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    /**
     * 
     * {@inheritDoc}
     * 
     */
    @Override
    public final boolean matches(final byte[] bytes, final int matchFrom) {
        final int localLength = length;
        if (matchFrom + localLength < bytes.length && matchFrom >= 0) {
            final byte[] localArray = byteArray;
            for (int byteIndex = 0; byteIndex < localLength; byteIndex++) {
                if (!(localArray[byteIndex] == bytes[matchFrom + byteIndex])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }    
    
    
    /**
     * @inheritDoc
     */
    @Override
    public boolean matchesNoBoundsCheck(final ByteReader reader, final long matchPosition) {
        final int localLength = length;
        final byte[] localArray = byteArray;
        for (int byteIndex = 0; byteIndex < localLength; byteIndex++) {
            if (!(localArray[byteIndex] == reader.readByte(matchPosition + byteIndex))) {
                return false;
            }
        }
        return true;
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchPosition) {
        final int localLength = length;
        final byte[] localArray = byteArray;
        for (int byteIndex = 0; byteIndex < localLength; byteIndex++) {
            if (!(localArray[byteIndex] == bytes[matchPosition + byteIndex])) {
                return false;
            }
        }
        return true;
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
    public final String toRegularExpression(final boolean prettyPrint) {
        return bytesToString(prettyPrint, byteArray);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final SingleByteMatcher getByteMatcherForPosition(final int position) {
        return new ByteMatcher(byteArray[position]);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override    
    public ByteSequenceMatcher reverse() {
        final byte[] reverseArray = ByteUtilities.reverseArray(byteArray);
        return new ByteSequenceMatcher(reverseArray);
    }
    

    private String bytesToString(final boolean prettyPrint, final byte[] bytes) {
        final StringBuilder hexString = new StringBuilder();
        boolean inString = false;
        for (int byteIndex = 0, byteLength = bytes.length;
            byteIndex < byteLength; byteIndex++) {
            final int byteValue = 0xFF & bytes[byteIndex];
            if (prettyPrint &&
                    byteValue >= START_PRINTABLE_ASCII &&
                    byteValue <= END_PRINTABLE_ASCII &&
                    byteValue != QUOTE_CHARACTER_VALUE) {
                final String formatString = inString ? "%c" : " '%c";
                hexString.append(String.format(formatString, (char) byteValue));
                inString = true;
            } else {
                final String formatString = prettyPrint? inString? "' %02x" : "%02x" : "%02x";
                hexString.append(String.format(formatString, byteValue));
                inString = false;
            }
        }
        if (prettyPrint && inString) {
            hexString.append("' ");
        }
        return hexString.toString();
    }


}
