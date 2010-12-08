/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.domesdaybook.matcher.sequence.SequenceMatcher;

/**
 *
 * @author matt
 */
public abstract class ByteClassMatcher implements SequenceMatcher {
    
    boolean negated = false;
    int numBytesInClass = 0;

    @Override
    public int length() {
        return 1; // a byte class only ever matches a single byte position.
    }
    
    public boolean isNegated() {
        return negated;
    }

    public int getNumBytesInClass() {
        return numBytesInClass;
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

    public static ByteClassMatcher fromExpression( final String byteClassSpec ) {

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




}
