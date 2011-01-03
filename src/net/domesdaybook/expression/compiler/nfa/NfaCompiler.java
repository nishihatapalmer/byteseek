/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.expression.compiler.nfa;

import net.domesdaybook.automata.transition.TransitionSingleByteMatcherFactory;
import net.domesdaybook.automata.transition.TransitionFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.domesdaybook.automata.nfa.NfaState;
import net.domesdaybook.expression.compiler.AstCompiler;
import net.domesdaybook.expression.parser.ParseUtils;
import net.domesdaybook.expression.parser.regularExpressionParser;
import org.antlr.runtime.tree.CommonTree;

/**
 *
 * @author matt
 */
public class NfaCompiler extends AstCompiler<NfaState> {

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

            default: {
                final String message = String.format(TYPE_ERROR, getTokenName(ast));
                throw new IllegalArgumentException(message);
            }
        }
        return states;
    }


}
