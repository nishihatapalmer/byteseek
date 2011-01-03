/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.expression.parser;

import java.util.HashSet;
import java.util.Set;
import net.domesdaybook.matcher.singlebyte.BitUtilities;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

/**
 *
 * @author matt
 */
public class ParseUtils {

    public static final String TYPE_ERROR = "Type [%s] not supported by the compiler.";
    public static final String QUOTE = "\'";

    private ParseUtils() {
    }

    
    public static byte parseHexByte(final String hexByte) {
        return (byte) Integer.parseInt(hexByte, 16);
    }


    public static byte getHexByteValue(final Tree treeNode) {
        return parseHexByte(treeNode.getText());
    }


    public static byte getBitMaskValue(final Tree treeNode) {
        final Tree childNode = treeNode.getChild(0);
        return parseHexByte(childNode.getText());
    }


    public static int getChildIntValue(final Tree treeNode, final int childIndex) {
        final Tree childNode = treeNode.getChild(childIndex);
        return Integer.parseInt(childNode.getText(), 10);
    }


    public static String getChildStringValue(final Tree treeNode, final int childIndex) {
        return treeNode.getChild(childIndex).getText();
    }

    /**
     * Calculates a value of a set given the parent set node (or inverted set node)
     * Sets can contain bytes, strings (case sensitive & insensitive), ranges,
     * other sets nested inside them (both normal and inverted) and bitmasks.
     *
     * This can be recursive procedure if sets are nested within one another.
     *
     * @param node          The set node to calculate a set of byte values for.
     * @param cumulativeSet The set of cumulative bytes so far.
     */
    public static Set<Byte> calculateSetValue(final CommonTree node) throws ParseException {
        final Set<Byte> setValues = new HashSet<Byte>();
        for (int childIndex = 0, stop = node.getChildCount(); childIndex < stop; childIndex++) {
            final CommonTree childNode = (CommonTree) node.getChild(childIndex);
            switch (childNode.getType()) {

                // Recursively build if we have nested child sets:
                case regularExpressionParser.SET: {
                    final Set<Byte> nestedSetValues = calculateSetValue(childNode);
                    setValues.addAll(nestedSetValues);
                    break;
                }

                case regularExpressionParser.INVERTED_SET: {
                    final Set<Byte> nestedSetValues = calculateSetValue(childNode);
                    setValues.addAll(inverseOf(nestedSetValues));
                    break;
                }

                // non recursive: just build values:
                case regularExpressionParser.BYTE: {
                    setValues.add(ParseUtils.getHexByteValue(childNode));
                    break;
                }

                case regularExpressionParser.ALL_BITMASK: {
                    final byte allBitMask = ParseUtils.getBitMaskValue(childNode);
                    setValues.addAll(BitUtilities.getBytesMatchingAllBitMask(allBitMask));
                    break;
                }

                case regularExpressionParser.ANY_BITMASK: {
                    final byte allBitMask = ParseUtils.getBitMaskValue(childNode);
                    setValues.addAll(BitUtilities.getBytesMatchingAnyBitMask(allBitMask));
                    break;
                }

                case regularExpressionParser.RANGE: {
                    int minRangeValue;
                    int maxRangeValue;
                    String minRange = ParseUtils.getChildStringValue(childNode, 0);
                    String maxRange = ParseUtils.getChildStringValue(childNode, 1);
                    if (minRange.startsWith(QUOTE)) {
                        minRangeValue = (int) minRange.charAt(1);
                    } else {
                        minRangeValue = Integer.parseInt(minRange);
                    }
                    if (maxRange.startsWith(QUOTE)) {
                        maxRangeValue = (int) maxRange.charAt(1);
                    } else {
                        maxRangeValue = Integer.parseInt(maxRange);
                    }
                    if (minRangeValue > maxRangeValue) {
                        int swapTemp = minRangeValue;
                        minRangeValue = maxRangeValue;
                        maxRangeValue = swapTemp;
                    }
                    //if (minRange < 0 || maxRange > 255) {
                        //
                    //}
                    for (int rangeValue = minRangeValue; rangeValue <= maxRangeValue; rangeValue++) {
                        setValues.add((byte) rangeValue);
                    }
                    break;
                }


                case regularExpressionParser.CASE_SENSITIVE_STRING: {
                    final String stringValue = trimString(childNode.getText());
                    for (int charIndex = 0; charIndex < stringValue.length(); charIndex++ ) {
                        final char charAt = stringValue.charAt(charIndex);
                        setValues.add((byte) charAt);
                    }
                    break;
                }


                case regularExpressionParser.CASE_INSENSITIVE_STRING: {
                    final String stringValue = trimString(childNode.getText());
                    for (int charIndex = 0; charIndex < stringValue.length(); charIndex++ ) {
                        final char charAt = stringValue.charAt(charIndex);
                        if (charAt >= 'a' && charAt <= 'z') {
                            setValues.add((byte) Character.toUpperCase(charAt));

                        } else if (charAt >= 'A' && charAt <= 'A') {
                            setValues.add((byte) Character.toLowerCase(charAt));
                        }
                        setValues.add((byte) charAt);
                    }
                    break;
                }

                default: {
                    final String message = String.format(TYPE_ERROR, getTokenName(childNode));
                    throw new ParseException(message);
                }
            }
        }
        return setValues;
    }


    public static Set<Byte> inverseOf(final Set<Byte> byteSet) {
        final Set<Byte> inverseSet = new HashSet<Byte>();
        for (int value = 0; value < 256; value++) {
            if (!byteSet.contains((byte) value)) {
                inverseSet.add((byte) value);
            }
        }
        return inverseSet;
    }


    public static String trimString(final String str) {
        return str.substring(1, str.length() - 1);
    }


    public static String getTokenName(final CommonTree node) {
        return regularExpressionParser.tokenNames[node.getType()];
    }

    public static String getTypeErrorMessage(final CommonTree node) {
        return String.format(TYPE_ERROR, getTokenName(node));
    }


}
