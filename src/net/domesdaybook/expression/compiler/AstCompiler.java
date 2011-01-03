/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.expression.compiler;

import java.util.HashSet;
import java.util.Set;
import net.domesdaybook.expression.parser.AstParser;
import net.domesdaybook.expression.parser.ParseException;
import net.domesdaybook.expression.parser.ParseUtils;
import net.domesdaybook.expression.parser.regularExpressionParser;
import net.domesdaybook.matcher.singlebyte.BitUtilities;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

/**
 *
 * @author matt
 */
public abstract class AstCompiler<T> implements Compiler<T> {

    protected static final String MANY = "*";
    protected static final String QUOTE = "\'";
    protected static final String TYPE_ERROR = "Type [%s] not supported by the compiler.";

    @Override
    public T compile(String expression) throws ParseException {
        AstParser parser = new AstParser();
        Tree tree = parser.parseToAST(expression);
        CommonTree optimisedAST = (CommonTree) parser.optimiseAST(tree);
        return compile(optimisedAST);
    }

    
    public abstract T compile(final CommonTree ast);


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
    protected final Set<Byte> calculateSetValue(final CommonTree node) {
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
                    throw new IllegalArgumentException(message);
                }
            }
        }
        return setValues;
    }


    protected final Set<Byte> inverseOf(final Set<Byte> byteSet) {
        final Set<Byte> inverseSet = new HashSet<Byte>();
        for (int value = 0; value < 256; value++) {
            if (!byteSet.contains((byte) value)) {
                inverseSet.add((byte) value);
            }
        }
        return inverseSet;
    }


    protected final String trimString(final String str) {
        return str.substring(1, str.length() - 1);
    }

    
    protected final String getTokenName(final CommonTree node) {
        return regularExpressionParser.tokenNames[node.getType()];
    }

}
