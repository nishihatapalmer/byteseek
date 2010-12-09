/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.matcher.sequence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.domesdaybook.matcher.sequence.ByteSequenceMatcher;
import net.domesdaybook.matcher.sequence.CaseInsensitiveStringMatcher;
import net.domesdaybook.matcher.sequence.CaseSensitiveStringMatcher;
import net.domesdaybook.matcher.sequence.CombinedSequenceMatcher;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.matcher.singlebyte.BitMaskMatcher;
import net.domesdaybook.matcher.singlebyte.ByteClassMatcher;
import net.domesdaybook.matcher.singlebyte.ByteClassRangeMatcher;
import net.domesdaybook.matcher.singlebyte.ByteClassSetMatcher;
import net.domesdaybook.matcher.singlebyte.ByteMatcher;

/**
 *
 * @author matt
 */
public class MatcherSequenceParser {

    public static SequenceMatcher fromExpression(final String expression) {
        return new CombinedSequenceMatcher(matchListFromExpression(expression));
    }

    private static List<SequenceMatcher> matchListFromExpression(final String byteSequenceSpec) {
        List<SequenceMatcher> matchers = new ArrayList<SequenceMatcher>();
        int stringPos = 0;
        final int byteSequenceLength = byteSequenceSpec.length();
        while ( stringPos < byteSequenceLength ) {
            final String currentChar = byteSequenceSpec.substring( stringPos, stringPos + 1 );
            SequenceMatcher matcher = null;

            // byte class?
            if (currentChar.equals("[")) { // Is it a byte class?
                final int endSquareBracketPos = getClosingSetPosition(byteSequenceSpec, stringPos + 1);
                if (endSquareBracketPos > 0) {
                    final String byteClassSpec = byteSequenceSpec.substring(stringPos, endSquareBracketPos+1);
                    matcher = byteClassFromExpression(byteClassSpec);
                    stringPos = endSquareBracketPos + 1;
                } else {
                    throw new IllegalArgumentException( "No closing square bracket for byte class.");
                }
            }


            // ASCII case-sensitive string?
            else if (currentChar.equals("'")) {
                final int closingQuote = byteSequenceSpec.indexOf("'", stringPos + 1);
                if (closingQuote > 0 ) {
                    final String caseSensitiveASCIIString = byteSequenceSpec.substring(stringPos+1,closingQuote);
                    matcher = new CaseSensitiveStringMatcher(caseSensitiveASCIIString);
                    stringPos = closingQuote + 1;
                } else {
                    throw new IllegalArgumentException( "No closing quote ' for case-sensitive string.");
                }
            }

            // ASCII case-insensitive string?
            else if (currentChar.equals("`")) {
                final int closingQuote = byteSequenceSpec.indexOf("`", stringPos + 1);
                if ( closingQuote > 0 ) {
                    final String caseInsensitiveASCIIString = byteSequenceSpec.substring(stringPos+1,closingQuote);
                    matcher = new CaseInsensitiveStringMatcher(caseInsensitiveASCIIString);
                    stringPos = closingQuote + 1;
                } else {
                    throw new IllegalArgumentException( "No closing quote ` for case-insensitive string.");
                }
            }


            // bitmask?
            else if (currentChar.equals("&")) {
                if ( stringPos + 2 < byteSequenceLength ) {
                    final String hexBitMask = byteSequenceSpec.substring( stringPos, stringPos + 3);
                    matcher = bitmaskFromExpression(hexBitMask);
                    stringPos += 3;
                } else {
                    throw new IllegalArgumentException( "No hex byte specified for & bit mask.");
                }
            }


            // hex bytes
            else { // might be a hex byte or sequence of hex bytes:
                // locate the end of the hex byte sequence:
                final int lastHexBytePos = getLastHexBytePosition(byteSequenceSpec, stringPos);
                if (lastHexBytePos > stringPos) {
                    final String hexBytes = byteSequenceSpec.substring(stringPos, lastHexBytePos + 1);
                    matcher = byteSequenceFromExpression(hexBytes);
                    stringPos = lastHexBytePos + 1;
                } else {
                    throw new IllegalArgumentException("Hex bytes not specified.");
                }
            }

            if ( matcher != null) {
                matchers.add(matcher);
            }
        }
        return matchers;
    }

    private static int getLastHexBytePosition(final String sequence, int fromPosition) {
        int searchPosition = fromPosition;
        while (searchPosition < sequence.length()) {
            final Character currentChar = sequence.charAt(searchPosition);
            if ((currentChar >= '0' && currentChar <= '9') ||
                (currentChar >= 'a' && currentChar <= 'f') ||
                (currentChar >= 'A' && currentChar <= 'F')) {
                searchPosition += 1;
            } else {
                break;
            }
        }
        return searchPosition - 1;
    }

    private static int getClosingSetPosition(final String sequence, int fromPosition) {
        int searchPosition = fromPosition;
        boolean closingTagFound = false;
        int openingTags = 1;
        boolean inCaseSensitiveString = false;
        boolean inCaseInsensitiveString = false;
        while (searchPosition < sequence.length()) {
            final Character currentChar = sequence.charAt(searchPosition);
            if (inCaseSensitiveString) {
                inCaseSensitiveString = !currentChar.equals('\'');
            } else if (inCaseInsensitiveString) {
                inCaseInsensitiveString = !currentChar.equals('`');
            } else if (currentChar.equals('\'')) {
                inCaseSensitiveString = true;
            } else if (currentChar.equals('`')) {
                inCaseInsensitiveString = true;
            } else if (currentChar.equals('[')) {
                openingTags += 1;
            } else if (currentChar.equals(']')) {
                openingTags -= 1;
                closingTagFound = openingTags == 0;
                break;
            }
            searchPosition += 1;
        }
        return closingTagFound ? searchPosition : -1;
    }

    // Utility method to parse a hex byte string into a ByteValueSequenceMatcher.
    public static ByteSequenceMatcher byteSequenceFromExpression(final String hexByteString) {
     // Preconditions: not null, empty and is an even number of chars
        if ( hexByteString == null || hexByteString.isEmpty() ) {
            throw new IllegalArgumentException("Null or empty hexByteSequence.");
        }
        final int stringLength = hexByteString.length();
        if ( stringLength % 2 != 0) {
            throw new IllegalArgumentException("Odd number of chars in hex byte string.");
        }

        // Build the byte sequence:
        final int byteSequenceLength = stringLength / 2;
        byte[] theBytes = new byte[byteSequenceLength];
        try {
            for (int byteIndex = 0; byteIndex < byteSequenceLength; byteIndex++) {
                // Will throw a NumberFormatException if it doesn't find a hex byte.
                final int byteVal = Integer.parseInt(hexByteString.substring(2 * byteIndex, 2 * (byteIndex + 1)), 16);
                theBytes[byteIndex] = (byte) (byteVal);
            }
        }
        catch ( NumberFormatException formatEx ) {
            throw new IllegalArgumentException("Hex bytes not specified properly in hex byte string.");
        }

        return new ByteSequenceMatcher(theBytes);
    }

    private static int parseHexByte( final String hexByte ) {
        final int result;
        final String errorMessage = "Value specified is not a hex byte.";
        try {
            result = Integer.parseInt(hexByte, 16);
            if ( result < 0 || result > 255 ) {
                throw new IllegalArgumentException( errorMessage );
            }
        }
        catch ( NumberFormatException nf ) {
            throw new IllegalArgumentException( errorMessage );
        }
        return result;
    }

    public static ByteClassMatcher byteClassFromExpression(final String byteClassSpec) {

        // Preconditions: not null or empty, begins and ends with square brackets:
        if ( byteClassSpec == null || byteClassSpec.isEmpty() ||
             !(byteClassSpec.startsWith("[") && byteClassSpec.endsWith("]")) ) {
            throw new IllegalArgumentException("Invalid byte class specification - missing end square bracket.");
        }

        // Check for class negation:
        boolean negated;
        int valuePos = 1;
        if (byteClassSpec.charAt(valuePos) == '!' ) {
            negated = true;
            valuePos++;
        } else {
            negated = false;
        }

        // Build a set of all byte values to match in the class:
        Set<Integer> classValues = new HashSet(256);
        final int lastSequencePosition = byteClassSpec.length()-2; // don't go past closing square bracket.
        while ( valuePos < lastSequencePosition ) {

            // Get a hex byte:
            final String hexbyte = byteClassSpec.substring(valuePos, valuePos+2);
            int byteValue = parseHexByte( hexbyte );
            valuePos += 2;

            // If we haven't yet reached the end of the sequence:
            if ( valuePos < lastSequencePosition) {

                // See if there is a range specified next:
                if ( byteClassSpec.substring(valuePos, valuePos+1).equals(":") ) {
                    valuePos++; // move past the colon

                    // If there's at least room for another 2-char hex byte:
                    if ( valuePos < lastSequencePosition ) {

                        // Get the other part of the range:
                        final String maxHexByte = byteClassSpec.substring(valuePos, valuePos+2);
                        valuePos += 2;
                        int maxByteValue = parseHexByte( maxHexByte );

                        // Make sure the minimum and maximum values are correctly ordered:
                        if ( byteValue > maxByteValue ) {
                            int tempValue = byteValue;
                            byteValue = maxByteValue;
                            maxByteValue = tempValue;
                        }

                        // add all values in the range to the byte class:
                        for ( int value = byteValue; value <= maxByteValue; value++) {
                            classValues.add( value );
                        }
                    } else {
                        throw new IllegalArgumentException("Invalid byte range specification - missing maximum range.");
                    }
                } else { // just add the byte class value:
                    classValues.add(byteValue);
                } // CAREFUL: can't collapse the above and below adds - the logic will break.
            } else { // just add the byte class value:
                classValues.add(byteValue);
            }
        }

        // Now create a sorted list of the possible byte values:
        ByteClassMatcher result = null;
        List<Integer> sortedValues = new ArrayList<Integer>(classValues);
        if (sortedValues.size() > 0) {
            Collections.sort(sortedValues);

            // Determine if all the values lie in a single range:
            final int lastValuePosition = sortedValues.size() -1;
            final int firstValue = sortedValues.get(0);
            final int lastValue = sortedValues.get(lastValuePosition);
            if (lastValue - firstValue == lastValuePosition) {
                // values lie in a contiguous range - the biggest minus the smallest is equal to
                // the length of the (zero-indexed) list.
                result = new ByteClassRangeMatcher(firstValue, lastValue, negated);
            } else { // values do not lie in a contiguous range.  Need a byte class matcher:
                result = new ByteClassSetMatcher(sortedValues, negated);
            }
        }
        return result;
    }


    public static ByteMatcher byteFromExpression(final String expression) {
        if (expression.length() !=2 ) {
            throw new IllegalArgumentException("Byte value must be two hex characters");
        }
        try {
            final int byteVal = Integer.parseInt(expression, 16);
            return new ByteMatcher((byte) (byteVal));
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Expression is not a valid 2 character hex byte.");
        }

    }


    public static BitMaskMatcher bitmaskFromExpression( final String hexBitMask ) {

        // Preconditions: not null or empty, begins and ends with square brackets:
        if ( hexBitMask == null || hexBitMask.isEmpty() ||
             !(hexBitMask.startsWith("&")) && hexBitMask.length() == 3) {
            throw new IllegalArgumentException("Invalid bitmask.");
        }

        BitMaskMatcher matcher = null;
        try {
            final byte value  = (byte) ( 0xFF & Integer.parseInt(hexBitMask.substring(1),16));
            matcher = new BitMaskMatcher( value );
        }
        catch ( NumberFormatException num ) {
            throw new IllegalArgumentException( "Bit mask not specified as & hex byte.");
        }
        return matcher;
    }


}
