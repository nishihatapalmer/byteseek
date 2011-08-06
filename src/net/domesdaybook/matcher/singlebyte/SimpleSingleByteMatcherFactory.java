/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */


package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.bytes.ByteUtilities;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author matt
 */
public class SimpleSingleByteMatcherFactory implements SingleByteMatcherFactory {

    private static final String ILLEGAL_ARGUMENTS = "Null or empty Byte set passed in to ByteSetMatcher.";
    private static final int BINARY_SEARCH_THRESHOLD = 16;

    
    /**
     * Builds an optimal matcher from a set of bytes.
     *
     * <p>If the set is a single, non-inverted byte, then a {@link ByteMatcher}
     * is returned. If the values lie in a contiguous range, then a
     * {@link ByteRangeMatcher} is returned.  If the number of bytes in the
     * set are below a threshold value (16), then a {@link ByteSetBinarySearchMatcher}
     * is returned, otherwise a {@link ByteSetBitSetMatcher} is returned.
     *
     * @param setValues The set of byte values to match.
     * @param matchInverse   Whether the set values are inverted or not
     * @return A SingleByteMatcher which is optimal for that set of bytes.
     * @throws {@link IllegalArgumentException} if the set is null or empty.
     */
    @Override
    public SingleByteMatcher create(Set<Byte> bytes, boolean matchInverse) {
        if (bytes == null || bytes.isEmpty()) {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENTS);
        }
        // Produce the (possibly inverted) set of bytes:
        final  Set<Byte> values = matchInverse? ByteUtilities.invertedSet(bytes) : bytes;

        // See if some obvious byte matchers apply:
        SingleByteMatcher result = getSimpleCases(values);
        if (result == null) {
            
            // Now check to see if any of the invertible matchers 
            // match our set of bytes:
            result = getInvertibleCases(values, false);
            if (result == null) {

                // They didn't match the set of bytes, but since we have invertible
                // matchers, does the inverse set match any of them?
                final Set<Byte> invertedValues = matchInverse? bytes : ByteUtilities.invertedSet(bytes);
                result = getInvertibleCases(invertedValues, true);
                if (result == null) {

                    // Fall back on a standard set, defined as passed in.
                    result = new ByteSetBitSetMatcher(bytes, matchInverse);
                }
            }
        }
        return result;
    }

    
    private SingleByteMatcher getInvertibleCases(Set<Byte> bytes, boolean isInverted) {
        SingleByteMatcher result = getBitmaskMatchers(bytes, isInverted);
        if (result == null) {
            result = getRangeMatchers(bytes, isInverted);
            if (result == null) {
                 result = getBinarySearchMatcher(bytes, isInverted);
            }
        }
        return result;
    }


    private SingleByteMatcher getSimpleCases(Set<Byte> values) {
        SingleByteMatcher result = null;
        switch (values.size()) {
            case 0: {
                // matches no bytes at all - AnyBitmaskMatcher with a mask of zero never matches anything.
                // Or: should throw exception - matcher can never match anything.
                result = new BitMaskAnyBitsMatcher((byte) 0, false);
                break;
            }

            case 1: {
                for (Byte byteToMatch : values) {
                    result = new ByteMatcher(byteToMatch);
                    break;
                }
                break;
            }

            case 2: {
                result = getCaseInsensitiveMatcher(values);
                break;
            }

            case 255: {
                for (byte byteValue = Byte.MIN_VALUE; byteValue < Byte.MAX_VALUE; byteValue++) {
                    if (!values.contains(byteValue)) {
                        result = new InvertedByteMatcher(byteValue);
                        break;
                    }
                }
                break;
            }
            
            case 256: {
                result = new AnyMatcher();
                break;
            }

            default: {
                result = null;
            }
        }
       return result;
    }


    private SingleByteMatcher getBitmaskMatchers(Set<Byte> values, boolean isInverted) {
        SingleByteMatcher result = null;
        // Determine if the bytes in the set can be matched by a bitmask:
        Byte bitmask = ByteUtilities.getAllBitMaskForBytes(values);
        if (bitmask != null) {
             result = new BitMaskAllBitsMatcher(bitmask, isInverted);
        } else {
             bitmask = ByteUtilities.getAnyBitMaskForBytes(values);
             if (bitmask != null) {
                result = new BitMaskAnyBitsMatcher(bitmask, isInverted);
             }
        }
        return result;
    }


    private SingleByteMatcher getRangeMatchers(Set<Byte> values, boolean isInverted) {
        SingleByteMatcher result = null;
        // Determine if all the values lie in a single range:
        List<Integer> byteValues = getSortedByteValues(values);
        int lastValuePosition = byteValues.size() - 1;
        int firstValue = byteValues.get(0);
        int lastValue = byteValues.get(lastValuePosition);
        if (lastValue - firstValue == lastValuePosition) {  // values lie in a contiguous range
            result = new ByteRangeMatcher(firstValue, lastValue, isInverted);
        }
        return result;
    }

    
    private SingleByteMatcher getBinarySearchMatcher(Set<Byte> values, boolean isInverted) {
        SingleByteMatcher result = null;
        // if there aren't very many values, use a BinarySearchMatcher:
        if (values.size() < BINARY_SEARCH_THRESHOLD) { // small number of bytes in set - use binary searcher:
            result = new ByteSetBinarySearchMatcher(values, isInverted);
        }
        return result;
    }


    private SingleByteMatcher getCaseInsensitiveMatcher(Set<Byte> values) {
        SingleByteMatcher result = null;
        Character caseChar = getCaseChar(values);
        if (caseChar != null) {
            result = new CaseInsensitiveByteMatcher(caseChar);
        }
        return result;
    }

    
    private Character getCaseChar(Set<Byte> values) {
        Character result = null;
        final int size = values.size();
        if (size == 2) {
            Iterator<Byte> iterator = values.iterator();
            int val1 = iterator.next() & 0xFF;
            int val2 = iterator.next() & 0xFF;
            if (isSameCharDifferentCase(val1, val2)) {
                result = new Character((char) val1);
            }
        }
        return result;
    }

    
    private boolean isSameCharDifferentCase(int val1, int val2) {
        boolean result = false;
        if (isAlphaChar(val1) && isAlphaChar(val2)) {
            result = Math.abs(val1-val2) == 32;
        }
        return result;
    }

    
    private boolean isAlphaChar(int val) {
        return ((val >= 65 && val <= 90) || (val >= 97 && val <= 122));
    }

    private static List<Integer> getSortedByteValues(Set<Byte> byteSet) {
        final List<Integer> sortedByteValues = new ArrayList<Integer>();
        for (Byte b : byteSet) {
            sortedByteValues.add((int) b.byteValue() & 0xFF);
        }
        Collections.sort(sortedByteValues);
        return sortedByteValues;
    }

}
