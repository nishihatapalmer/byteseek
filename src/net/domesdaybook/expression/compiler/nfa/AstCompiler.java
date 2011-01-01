/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.expression.compiler.nfa;

import net.domesdaybook.automata.transition.TransitionSingleByteMatcherFactory;
import net.domesdaybook.automata.transition.TransitionFactory;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.domesdaybook.automata.nfa.NfaState;
import net.domesdaybook.expression.parser.ParseUtils;
import net.domesdaybook.expression.parser.regularExpressionParser;
import net.domesdaybook.matcher.singlebyte.BitUtilities;
import org.antlr.runtime.tree.CommonTree;

/**
 *
 * @author matt
 */
public class AstCompiler implements Compiler {

    private static final String MANY = "*";
    private static final String QUOTE = "\'";

    private TransitionFactory transitionFactory = new TransitionSingleByteMatcherFactory();
    private StateBuilder stateBuilder = new SimpleStateBuilder();
    private StateWrapperBuilder stateWrapperBuilder = new ChamparnaudGlushkovBuilder(transitionFactory, stateBuilder);

    @Override
    public NfaState compile(final CommonTree ast) {
       initialiseStateWrapperBuilder();
       return buildAutomata(ast).initialState;
    }


    public void setTransitionFactory(final TransitionFactory transitionFactory) {
        this.transitionFactory = transitionFactory;
    }


    public void setStateBuilder(final StateBuilder stateBuilder) {
        this.stateBuilder = stateBuilder;
    }

    
    public void setStateWrapperBuilder(final StateWrapperBuilder stateWrapperBuilder) {
        this.stateWrapperBuilder = stateWrapperBuilder;
    }

    
    private void initialiseStateWrapperBuilder() {
       stateWrapperBuilder.setTransitionFactory(transitionFactory);
       stateWrapperBuilder.setStateBuilder(stateBuilder);
    }


    private StateWrapper buildAutomata(final CommonTree ast) {

        StateWrapper states = null;

        switch (ast.getToken().getType()) {

            // recursive part of building:
            
            case (regularExpressionParser.SEQUENCE): {
                List<StateWrapper> sequenceStates = new ArrayList<StateWrapper>();
                for (int childIndex = 0, stop = ast.getChildCount(); childIndex < stop; childIndex++) {
                    final CommonTree child = (CommonTree) ast.getChild(childIndex);
                    final StateWrapper childAutomata = buildAutomata(child);
                    sequenceStates.add(childAutomata);
                }
                states = stateWrapperBuilder.buildSequenceStates(sequenceStates);
                break;
            }


            case (regularExpressionParser.ALT): {
                List<StateWrapper> alternateStates = new ArrayList<StateWrapper>();
                for (int childIndex = 0, stop = ast.getChildCount(); childIndex < stop; childIndex++) {
                    final CommonTree child = (CommonTree) ast.getChild(childIndex);
                    final StateWrapper childAutomata = buildAutomata(child);
                    alternateStates.add(childAutomata);
                }
                states = stateWrapperBuilder.buildAlternativeStates(alternateStates);
                break;
            }

            //TODO: nested repeats can be optimised.
            case (regularExpressionParser.REPEAT): {
                final CommonTree nodeToRepeat = (CommonTree) ast.getChild(2);
                final StateWrapper repeatedAutomata = buildAutomata(nodeToRepeat);
                final int minRepeat = ParseUtils.getChildIntValue(ast, 0);
                if (MANY.equals(ParseUtils.getChildStringValue(ast,1))) {
                    states = stateWrapperBuilder.buildMinToManyStates(minRepeat, repeatedAutomata);
                } else {
                    final int maxRepeat = ParseUtils.getChildIntValue(ast, 1);
                    states = stateWrapperBuilder.buildMinToMaxStates(minRepeat, maxRepeat, repeatedAutomata);
                }
                break;
            }


            case (regularExpressionParser.MANY): {
                final CommonTree zeroToManyNode = (CommonTree) ast.getChild(0);
                final StateWrapper zeroToManyStates = buildAutomata(zeroToManyNode);
                states = stateWrapperBuilder.buildZeroToManyStates(zeroToManyStates);
                break;
            }


            case (regularExpressionParser.PLUS): {
                final CommonTree oneToManyNode = (CommonTree) ast.getChild(0);
                final StateWrapper oneToManyStates = buildAutomata(oneToManyNode);
                states = stateWrapperBuilder.buildOneToManyStates(oneToManyStates);
                break;
            }


            case (regularExpressionParser.QUESTION_MARK): {
                final CommonTree optionalNode = (CommonTree) ast.getChild(0);
                final StateWrapper optionalStates = buildAutomata(optionalNode);
                states = stateWrapperBuilder.buildOptionalStates(optionalStates);
                break;
            }


            // non-recursive part of building:


            case (regularExpressionParser.BYTE): {
                final byte transitionByte = ParseUtils.getHexByteValue(ast);
                states = stateWrapperBuilder.buildSingleByteStates(transitionByte);
                break;
            }


            case (regularExpressionParser.ALL_BITMASK): {
                final byte transitionByte = ParseUtils.getBitMaskValue(ast);
                states = stateWrapperBuilder.buildAllBitmaskStates(transitionByte);
                break;
            }

            case (regularExpressionParser.ANY_BITMASK): {
                final byte transitionByte = ParseUtils.getBitMaskValue(ast);
                states = stateWrapperBuilder.buildAnyBitmaskStates(transitionByte);
                break;
            }


            case (regularExpressionParser.SET): {
                final Set<Byte> byteSet = calculateSetValue(ast);
                states = stateWrapperBuilder.buildSetStates(byteSet,false);
                break;
            }


            case (regularExpressionParser.INVERTED_SET): {
                final Set<Byte> byteSet = calculateSetValue(ast);
                states = stateWrapperBuilder.buildSetStates(byteSet, true);
                break;
            }

            case (regularExpressionParser.ANY): {
                states = stateWrapperBuilder.buildAnyByteStates();
                break;
            }


            case (regularExpressionParser.CASE_SENSITIVE_STRING): {
                final String str = trimString(ast.getText());
                states = stateWrapperBuilder.buildCaseSensitiveStringStates(str);
                break;
            }


            case (regularExpressionParser.CASE_INSENSITIVE_STRING): {
                final String str = trimString(ast.getText());
                states = stateWrapperBuilder.buildCaseInsensitiveStringStates(str);
                break;
            }

        }
        return states;
    }


    private String trimString(final String str) {
        return str.substring(1, str.length() - 1);
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
    private Set<Byte> calculateSetValue(final CommonTree node) {
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
            }
        }
        return setValues;
    }

    
    private Set<Byte> inverseOf(final Set<Byte> byteSet) {
        final Set<Byte> inverseSet = new HashSet<Byte>();
        for (int value = 0; value < 256; value++) {
            if (!byteSet.contains((byte) value)) {
                inverseSet.add((byte) value);
            }
        }
        return inverseSet;
    }


}
