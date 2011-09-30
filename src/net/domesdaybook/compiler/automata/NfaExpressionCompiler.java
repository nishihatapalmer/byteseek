/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 *
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 
 *  * Neither the "byteseek" name nor the names of its contributors 
 *    may be used to endorse or promote products derived from this software 
 *    without specific prior written permission. 
 *  
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.domesdaybook.compiler.automata;

import net.domesdaybook.automata.wrapper.InitialFinalStates;
import net.domesdaybook.automata.transition.TransitionSingleByteMatcherFactory;
import net.domesdaybook.automata.transition.TransitionFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.state.StateFactory;
import net.domesdaybook.automata.state.SimpleStateFactory;
import net.domesdaybook.compiler.AbstractAstCompiler;
import net.domesdaybook.compiler.CompileException;
import net.domesdaybook.parser.ParseException;
import net.domesdaybook.parser.ParseUtils;
import net.domesdaybook.parser.regularExpressionParser;
import org.antlr.runtime.tree.CommonTree;

/**
 * A compiler which produces Non-deterministic Finite-state Automata (NFA)
 * from an expression.  This class extends {@link AbstractAstCompiler}, which
 * parses the expression using an ANTLR generated parser, and turns it into
 * an abstract syntax tree.  The compiler takes the abstract syntax tree
 * and uses it to direct the construction of an NFA using various builder
 * and factory helper classes.
 * 
 * @author Matt Palmer
 */
public final class NfaExpressionCompiler extends AbstractAstCompiler<State> {

    private static NfaExpressionCompiler defaultCompiler;
    public static State nfaFrom(String expression) throws CompileException {
        defaultCompiler = new NfaExpressionCompiler();
        return defaultCompiler.compile(expression);
    }    
    
    private static final String MANY = "*";
    
    private final InitialFinalStatesBuilder stateWrapperBuilder;

    /**
     * Constructs an NfaExpressionCompiler, using default {@link TransitionFactory},
     * {@link StateBuilder} and {@link InitialFinalStatesBuilder} objects.
     *
     * By default, it uses the {@link TransitionSingleByteMatcherFactory} and
     * the {@link StateBuilder} to make a {@link ChamparnaudGlushkovBuilder} to
     * produce the NFA.
     */
    public NfaExpressionCompiler() {
        stateWrapperBuilder = new ChamparnaudGlushkovBuilder();
    }

    
    /**
     * Constructs an NfaExpressionCompiler, supplying the {@link InitialFinalStatesBuilder} object
     * to use to construct the NFA from the parse tree.
     *
     * @param stateWrapperBuilder
     */
    public NfaExpressionCompiler(final InitialFinalStatesBuilder stateWrapperBuilder) {
        this.stateWrapperBuilder = stateWrapperBuilder;
    }

    

    /**
     * Compiles a Non-deterministic Finite-state Automata (NFA) from the
     * abstract syntax tree provided by the {@link AbstractAstCompiler} which this
     * class extends.
     * <p/>
     * It uses a {@link InitialFinalStatesBuilder} object to build the actual automata,
     * returning only the initial state of the final automata.
     *
     * @param ast The abstract syntax tree to compile the State automata from.
     * @return An automata recognising the expression described by the abstract syntax tree.
     * @throws CompileException If the abstract syntax tree could not be parsed.
     */
    @Override
    public State compile(final CommonTree ast) throws CompileException {
       if (ast == null) {
           throw new CompileException("Null abstract syntax tree passed in to NfaCompiler.");
       }
       try {
           return buildAutomata(ast).initialState;
        } catch (IllegalArgumentException e) {
            throw new CompileException(e);
        }
    }


    private InitialFinalStates buildAutomata(final CommonTree ast) throws CompileException {

        InitialFinalStates states = null;

        switch (ast.getToken().getType()) {

            // recursive part of building:
            
            case (regularExpressionParser.SEQUENCE): {
                List<InitialFinalStates> sequenceStates = new ArrayList<InitialFinalStates>();
                for (int childIndex = 0, stop = ast.getChildCount(); childIndex < stop; childIndex++) {
                    final CommonTree child = (CommonTree) ast.getChild(childIndex);
                    final InitialFinalStates childAutomata = buildAutomata(child);
                    sequenceStates.add(childAutomata);
                }
                states = stateWrapperBuilder.buildSequenceStates(sequenceStates);
                break;
            }


            case (regularExpressionParser.ALT): {
                List<InitialFinalStates> alternateStates = new ArrayList<InitialFinalStates>();
                for (int childIndex = 0, stop = ast.getChildCount(); childIndex < stop; childIndex++) {
                    final CommonTree child = (CommonTree) ast.getChild(childIndex);
                    final InitialFinalStates childAutomata = buildAutomata(child);
                    alternateStates.add(childAutomata);
                }
                states = stateWrapperBuilder.buildAlternativeStates(alternateStates);
                break;
            }

            //TODO: nested repeats can be optimised.
            case (regularExpressionParser.REPEAT): {
                final CommonTree nodeToRepeat = (CommonTree) ast.getChild(2);
                final InitialFinalStates repeatedAutomata = buildAutomata(nodeToRepeat);
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
                final InitialFinalStates zeroToManyStates = buildAutomata(zeroToManyNode);
                states = stateWrapperBuilder.buildZeroToManyStates(zeroToManyStates);
                break;
            }


            case (regularExpressionParser.PLUS): {
                final CommonTree oneToManyNode = (CommonTree) ast.getChild(0);
                final InitialFinalStates oneToManyStates = buildAutomata(oneToManyNode);
                states = stateWrapperBuilder.buildOneToManyStates(oneToManyStates);
                break;
            }


            case (regularExpressionParser.QUESTION_MARK): {
                final CommonTree optionalNode = (CommonTree) ast.getChild(0);
                final InitialFinalStates optionalStates = buildAutomata(optionalNode);
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
                try {
                    final Set<Byte> byteSet = ParseUtils.calculateSetValue(ast);
                    states = stateWrapperBuilder.buildSetStates(byteSet,false);
                    break;
                } catch (ParseException ex) {
                    throw new CompileException(ex);
                }
            }


            case (regularExpressionParser.INVERTED_SET): {
                try {
                    final Set<Byte> byteSet = ParseUtils.calculateSetValue(ast);
                    states = stateWrapperBuilder.buildSetStates(byteSet, true);
                    break;
                } catch (ParseException ex) {
                    throw new CompileException(ex);
                }
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
                throw new CompileException(ParseUtils.getTypeErrorMessage(ast));
            }
        }
        return states;
    }


}
