/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.expression.compiler;

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
public abstract class AstToNfaCompiler implements NfaCompiler {

    private static final String ANY_STRING = regularExpressionParser.tokenNames[regularExpressionParser.ANY];
    
    private TransitionFactory transitionFactory = new TransitionSingleByteMatcherFactory();
    private NfaStateBuilder builder = new NfaSimpleStateBuilder();
    
    @Override
    public NfaState compile(final CommonTree ast) {
       return buildAutomata(ast).initialState;
    }


    public void setTransitionFactory(final TransitionFactory factory) {
        this.transitionFactory = factory;
    }


    public void setNfaStateBuilder(final NfaStateBuilder builder) {
        this.builder = builder;
    }


    private NfaInitialFinalStates buildAutomata(final CommonTree ast) {

        NfaInitialFinalStates states = null;
        final NfaInitialFinalStatesBuilder stateBuilder = new GlushkovStatesBuilder(transitionFactory, builder);

        switch (ast.getToken().getType()) {

            // recursive part of building:
            
            case (regularExpressionParser.SEQUENCE): {
                List<NfaInitialFinalStates> sequenceStates = new ArrayList<NfaInitialFinalStates>();
                for (int childIndex = 0, stop = ast.getChildCount(); childIndex < stop; childIndex++) {
                    final CommonTree child = (CommonTree) ast.getChild(childIndex);
                    final NfaInitialFinalStates childAutomata = buildAutomata(child);
                    sequenceStates.add(childAutomata);
                }
                states = stateBuilder.buildSequenceStates(sequenceStates);
                break;
            }


            case (regularExpressionParser.ALT): {
                List<NfaInitialFinalStates> alternateStates = new ArrayList<NfaInitialFinalStates>();
                for (int childIndex = 0, stop = ast.getChildCount(); childIndex < stop; childIndex++) {
                    final CommonTree child = (CommonTree) ast.getChild(childIndex);
                    final NfaInitialFinalStates childAutomata = buildAutomata(child);
                    alternateStates.add(childAutomata);
                }
                states = stateBuilder.buildAlternativeStates(alternateStates);
                break;
            }


            case (regularExpressionParser.REPEAT): {
                final CommonTree nodeToRepeat = (CommonTree) ast.getChild(2);
                final NfaInitialFinalStates repeatedAutomata = buildAutomata(nodeToRepeat);
                final int minRepeat = ParseUtils.getChildIntValue(ast, 0);
                if (ANY_STRING.equals(ParseUtils.getChildStringValue(ast,1))) {
                    states = stateBuilder.buildMinToManyStates(minRepeat, repeatedAutomata);
                } else {
                    final int maxRepeat = ParseUtils.getChildIntValue(ast, 1);
                    states = stateBuilder.buildMinToMaxStates(minRepeat, maxRepeat, repeatedAutomata);
                }
                break;
            }


            case (regularExpressionParser.MANY): {
                final CommonTree zeroToManyNode = (CommonTree) ast.getChild(0);
                final NfaInitialFinalStates zeroToManyStates = buildAutomata(zeroToManyNode);
                states = stateBuilder.buildZeroToManyStates(zeroToManyStates);
                break;
            }


            case (regularExpressionParser.PLUS): {
                final CommonTree oneToManyNode = (CommonTree) ast.getChild(0);
                final NfaInitialFinalStates oneToManyStates = buildAutomata(oneToManyNode);
                states = stateBuilder.buildOneToManyStates(oneToManyStates);
                break;
            }


            case (regularExpressionParser.QUESTION_MARK): {
                final CommonTree optionalNode = (CommonTree) ast.getChild(0);
                final NfaInitialFinalStates optionalStates = buildAutomata(optionalNode);
                states = stateBuilder.buildOptionalStates(optionalStates);
                break;
            }


            // non-recursive part of building:


            case (regularExpressionParser.BYTE): {
                final byte transitionByte = ParseUtils.getHexByteValue(ast);
                states = stateBuilder.buildSingleByteStates(transitionByte);
                break;
            }


            case (regularExpressionParser.BITMASK): {
                final byte transitionByte = ParseUtils.getBitMaskValue(ast);
                states = stateBuilder.buildAllBitmaskStates(transitionByte);
                break;
            }


            case (regularExpressionParser.SET): {
                final Set<Byte> byteSet = calculateSetValue(ast);
                states = stateBuilder.buildSetStates(byteSet,false);
                break;
            }


            case (regularExpressionParser.INVERTED_SET): {
                final Set<Byte> byteSet = calculateSetValue(ast);
                states = stateBuilder.buildSetStates(byteSet, true);
                break;
            }

            case (regularExpressionParser.ANY): {
                states = stateBuilder.buildAnyByteStates();
            }


            case (regularExpressionParser.CASE_SENSITIVE_STRING): {
                final String str = ast.getText();
                states = stateBuilder.buildCaseSensitiveStringStates(str);
                break;
            }


            case (regularExpressionParser.CASE_INSENSITIVE_STRING): {
                final String str = ast.getText();
                states = stateBuilder.buildCaseInsensitiveStringStates(str);
                break;
            }

            //FIXME: need to implement ANY BItMASKS in the parser.
            //case (regularExpressionParser.BITMASK): {
            //    states = getAnyBitmaskStates(ast);
            //    break;
            //}

        }
        return states;
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

                case regularExpressionParser.BITMASK: {
                    final byte allBitMask = ParseUtils.getBitMaskValue(childNode);
                    setValues.addAll(BitUtilities.getBytesMatchingAllBitMask(allBitMask));
                    break;
                }

                case regularExpressionParser.RANGE: {
                    int minRange = ParseUtils.getChildIntValue(childNode, 0);
                    int maxRange = ParseUtils.getChildIntValue(childNode, 1);
                    if (minRange > maxRange) {
                        int swapTemp = minRange;
                        minRange = maxRange;
                        maxRange = swapTemp;
                    }
                    //if (minRange < 0 || maxRange > 255) {
                        //
                    //}
                    for (int rangeValue = minRange; rangeValue <= maxRange; rangeValue++) {
                        setValues.add((byte) rangeValue);
                    }
                }

                
                case regularExpressionParser.CASE_SENSITIVE_STRING: {
                    final String stringValue = childNode.getText();
                    for (int charIndex = 0; charIndex < stringValue.length(); charIndex++ ) {
                        final char charAt = stringValue.charAt(charIndex);
                        setValues.add((byte) charAt);
                    }
                }


                case regularExpressionParser.CASE_INSENSITIVE_STRING: {
                    final String stringValue = childNode.getText();
                    for (int charIndex = 0; charIndex < stringValue.length(); charIndex++ ) {
                        final char charAt = stringValue.charAt(charIndex);
                        if (charAt >= 'a' && charAt <= 'z') {
                            setValues.add((byte) Character.toUpperCase(charAt));

                        } else if (charAt >= 'A' && charAt <= 'A') {
                            setValues.add((byte) Character.toLowerCase(charAt));
                        }
                        setValues.add((byte) charAt);
                    }
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
