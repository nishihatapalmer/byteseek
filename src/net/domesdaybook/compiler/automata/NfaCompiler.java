/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.compiler.automata;

import net.domesdaybook.automata.transition.TransitionSingleByteMatcherFactory;
import net.domesdaybook.automata.transition.TransitionFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.state.StateFactory;
import net.domesdaybook.automata.state.SimpleStateFactory;
import net.domesdaybook.compiler.AstCompiler;
import net.domesdaybook.parser.ParseException;
import net.domesdaybook.parser.ParseUtils;
import net.domesdaybook.parser.regularExpressionParser;
import org.antlr.runtime.tree.CommonTree;

/**
 * A compiler which produces Non-deterministic Finite-state Automata (NFA)
 * from an expression.  This class extends {@link AstCompiler}, which
 * parses the expression using an ANTLR generated parser, and turns it into
 * an abstract syntax tree.  The compiler takes the abstract syntax tree
 * and uses it to direct the construction of an NFA using various builder
 * and factory helper classes.
 * 
 * @author Matt Palmer
 */
public final class NfaCompiler extends AstCompiler<State> {

    private static final String MANY = "*";
    
    private StateWrapperBuilder stateWrapperBuilder;

    /**
     * Constructs an NfaCompiler, using default {@link TransitionFactory},
     * {@link StateBuilder} and {@link StateWrapperBuilder} objects.
     *
     * By default, it uses the {@link TransitionSingleByteMatcherFactory} and
     * the {@link StateBuilder} to make a {@link ChamparnaudGlushkovBuilder} to
     * produce the NFA.
     */
    public NfaCompiler() {
        final TransitionFactory transitionFactory = new TransitionSingleByteMatcherFactory();
        final StateFactory stateFactory = new SimpleStateFactory();
        stateWrapperBuilder = new ChamparnaudGlushkovBuilder(transitionFactory, stateFactory);
    }

    
    /**
     * Constructs an NfaCompiler, supplying the {@link StateWrapperBuilder} object
     * to use to construct the NFA from the parse tree.
     *
     * @param stateWrapperBuilder
     */
    public NfaCompiler(final StateWrapperBuilder stateWrapperBuilder) {
        this.stateWrapperBuilder = stateWrapperBuilder;
    }


    /**
     * Sets the {@link StateWrapperBuilder} object to use to construct the NFA.
     *
     * @param stateWrapperBuilder The StateWrapperBuilder object to use to construct the NFA.
     */
    public void setStateWrapperBuilder(final StateWrapperBuilder stateWrapperBuilder) {
        this.stateWrapperBuilder = stateWrapperBuilder;
    }
    

    /**
     * Compiles a Non-deterministic Finite-state Automata (NFA) from the
     * abstract syntax tree provided by the {@link AstCompiler} which this
     * class extends.
     * <p/>
     * It uses a {@link StateWrapperBuilder} object to build the actual automata,
     * returning only the initial state of the final automata.
     *
     * @param ast The abstract syntax tree to compile the State automata from.
     * @return An automata recognising the expression described by the abstract syntax tree.
     * @throws ParseException If the abstract syntax tree could not be parsed.
     */
    @Override
    public State compile(final CommonTree ast) throws ParseException {
       if (ast == null) {
           throw new ParseException("Null abstract syntax tree passed in to NfaCompiler.");
       }
       try {
           return buildAutomata(ast).initialState;
        } catch (IllegalArgumentException e) {
            throw new ParseException(e);
        }
    }


    private StateWrapper buildAutomata(final CommonTree ast) throws ParseException {

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
                final Set<Byte> byteSet = ParseUtils.calculateSetValue(ast);
                states = stateWrapperBuilder.buildSetStates(byteSet,false);
                break;
            }


            case (regularExpressionParser.INVERTED_SET): {
                final Set<Byte> byteSet = ParseUtils.calculateSetValue(ast);
                states = stateWrapperBuilder.buildSetStates(byteSet, true);
                break;
            }

            case (regularExpressionParser.ANY): {
                states = stateWrapperBuilder.buildAnyByteStates();
                break;
            }


            case (regularExpressionParser.CASE_SENSITIVE_STRING): {
                final String str = ParseUtils.trimString(ast.getText());
                states = stateWrapperBuilder.buildCaseSensitiveStringStates(str);
                break;
            }


            case (regularExpressionParser.CASE_INSENSITIVE_STRING): {
                final String str = ParseUtils.trimString(ast.getText());
                states = stateWrapperBuilder.buildCaseInsensitiveStringStates(str);
                break;
            }

            default: {
                throw new ParseException(ParseUtils.getTypeErrorMessage(ast));
            }
        }
        return states;
    }


}
