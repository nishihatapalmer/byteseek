/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.sequence;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.matcher.singlebyte.ByteMatcher;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.reader.Window;

/**
 * An immutable class which matches ASCII text case sensitively.
 * 
 * @author Matt Palmer
 */
public final class CaseSensitiveStringMatcher implements SequenceMatcher {

    private final byte[] byteArray;
    private final int length;

    /**
     * Constructs a case sensitive matcher for a given ASCII string.
     *
     * @param caseSensitiveASCIIString The ASCII string to match case sensitively.
     */
    public CaseSensitiveStringMatcher(final String caseSensitiveASCIIString) {
        this(caseSensitiveASCIIString, 1);
    }


    /**
     * Constructs a case sensitive matcher for a number of repeated ASCII strings.
     *
     * @param caseSensitiveASCIIString The (repeated) ASCII string to match case sensitively.
     * @param numberToRepeat The number of times to repeat the ASCII string.
     * @throws IllegalArgumentException if the string is null or empty, or the
     *         number to repeat is less than one.
     */
    public CaseSensitiveStringMatcher(final String caseSensitiveASCIIString, final int numberToRepeat) {
        if (caseSensitiveASCIIString == null || caseSensitiveASCIIString.isEmpty()) {
            throw new IllegalArgumentException("Null or empty string passed in to CaseSensitiveStringMatcher.");
        }
        if (numberToRepeat < 1) {
            throw new IllegalArgumentException("CaseSensitiveStringMatcher requires a positive number of repeats.");
        }
        String caseSensitiveString = repeatString(caseSensitiveASCIIString, numberToRepeat);
        final int byteSequenceLength = caseSensitiveString.length();
        byteArray = new byte[byteSequenceLength];
        for (int byteIndex = 0; byteIndex < byteSequenceLength; byteIndex++) {
            byteArray[byteIndex] = (byte) (caseSensitiveString.charAt(byteIndex));
        }
        length = byteArray.length;
    }

    
    private String repeatString(final String stringToRepeat, final int numberToRepeat) {
        if (numberToRepeat == 1) {
            return stringToRepeat;
        }
        StringBuilder builder = new StringBuilder();
        for (int count = 0; count < numberToRepeat; count++) {
            builder.append(stringToRepeat);
        }
        return builder.toString();
    }

    
    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public final boolean matches(final Reader reader, final long matchFrom)
            throws IOException {
        final Window window = reader.getWindow(matchFrom);
        if (window != null) {
            final int localLength = length;            
            final int offset = reader.getWindowOffset(matchFrom);
            if (offset + localLength <= window.getLimit()) {
                return matchesNoBoundsCheck(window.getArray(), offset);
            }
            if (matchFrom + localLength <= reader.length()) {
                return matchesNoBoundsCheck(reader, matchFrom);
            }
        }
        return false;
    }


    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchFrom) {
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
     * {@inheritDoc}
     * 
     */
    @Override
    public boolean matchesNoBoundsCheck(final Reader reader, final long matchFrom)
            throws IOException {
        final int localLength = length;
        final byte[] localArray = byteArray;
        for (int byteIndex = 0; byteIndex < localLength; byteIndex++) {
            if (!(localArray[byteIndex] == reader.readByte(matchFrom + byteIndex))) {
                return false;
            }
        }
        return true;
    }

    
    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchFrom) {
        final int localLength = length;
        final byte[] localArray = byteArray;
        for (int byteIndex = 0; byteIndex < localLength; byteIndex++) {
            if (!(localArray[byteIndex] == bytes[matchFrom + byteIndex])) {
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
        if (prettyPrint) {
            return " '" + getCaseSensitiveString() + "' ";
        }
        return "'" + getCaseSensitiveString() + "'";
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public SingleByteMatcher getByteMatcherForPosition(final int position) {
        return new ByteMatcher(byteArray[position]);
    }

    
    /**
     *
     * @return The string this matcher matches case sensitively.
     */
    public String getCaseSensitiveString() {
        try {
            return new String(byteArray, "US-ASCII");
        } catch (UnsupportedEncodingException ex) {
            return "";
        }
    }

   
    
    /**
     * @inheritDoc
     */
    @Override
    public CaseSensitiveStringMatcher reverse() {
        final String reversed = new StringBuffer(getCaseSensitiveString()).reverse().toString();
        return new CaseSensitiveStringMatcher(reversed);
    }

}

