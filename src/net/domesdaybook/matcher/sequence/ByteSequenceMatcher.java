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
 *
 * @author Matt Palmer
 */
public final class ByteSequenceMatcher implements SequenceMatcher {

    public static final int QUOTE_CHARACTER_VALUE = 39;
    public static final int START_PRINTABLE_ASCII = 32;
    public static final int END_PRINTABLE_ASCII = 126;

    private final byte[] byteArray;
    private final int length;


    public ByteSequenceMatcher(final byte[] byteArray ) {
        // Preconditions byteArray is not null:
        if ( byteArray == null ) {
            throw new IllegalArgumentException("Null byte array passed in to ByteSequenceMatcher");
        }
        this.byteArray = byteArray.clone(); // avoid mutability issues - clone byte array.
        length = byteArray.length;
    }

    
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


    public ByteSequenceMatcher(final List<ByteSequenceMatcher> matchers) {
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


    public ByteSequenceMatcher(final byte byteValue, final int numberOfBytes) {
        if (numberOfBytes < 0) {
            throw new IllegalArgumentException("ByteSequenceMatcher requires a positive number of bytes.");
        }
        length = numberOfBytes;
        this.byteArray = new byte[numberOfBytes];
        for (int count = 0; count < numberOfBytes; count++) {
            this.byteArray[count] = byteValue;
        }
    }

    public ByteSequenceMatcher(final byte byteValue) {
        this.byteArray = new byte[1];
        this.byteArray[0] = byteValue;
        length = 1;
    }


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

    
    @Override
    public final int length() {
        return length;
    }


    @Override
    public final String toRegularExpression( final boolean prettyPrint ) {
        return bytesToString(prettyPrint, byteArray);
    }


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
