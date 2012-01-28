/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
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
 *  * The names of its contributors may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
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
 */

package net.domesdaybook.compiler.regex;

import net.domesdaybook.automata.regex.GlushkovRegexBuilder;
import net.domesdaybook.automata.regex.RegexBuilder;
import java.util.Collection;
import net.domesdaybook.automata.base.ByteMatcherTransitionFactory;
import net.domesdaybook.automata.TransitionFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.domesdaybook.automata.Automata;
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
public final class RegexCompiler<T> extends AbstractAstCompiler<Automata<T>> {

    private static final String MANY = "*";
    
    private final RegexBuilder<T> automataBuilder;

    /**
     * Constructs an RegexCompiler, using default {@link TransitionFactory},
     * {@link StateBuilder} and {@link RegexBuilder} objects.
     *
     * By default, it uses the {@link ByteMatcherTransitionFactory} and
     * the {@link StateBuilder} to make a {@link GlushkovRegexBuilder} to
     * produce the NFA.
     */
    public RegexCompiler() {
        automataBuilder = new GlushkovRegexBuilder<T>();
    }

    
    /**
     * Constructs an RegexCompiler, supplying the {@link RegexBuilder} object
     * to use to construct the NFA from the parse tree.
     *
     * @param stateWrapperBuilder
     */
    public RegexCompiler(final RegexBuilder<T> stateWrapperBuilder) {
        this.automataBuilder = stateWrapperBuilder;
    }

    

    /**
     * Compiles a Non-deterministic Finite-state Automata (NFA) from the
     * abstract syntax tree provided by the {@link AbstractAstCompiler} which this
     * class extends.
     * <p/>
     * It uses a {@link RegexBuilder} object to build the actual automata,
     * returning only the initial state of the final automata.
     *
     * @param ast The abstract syntax tree to compile the State automata from.
     * @return An automata recognising the expression described by the abstract syntax tree.
     * @throws CompileException If the abstract syntax tree could not be parsed.
     */
    @Override
    public Automata<T> compile(final CommonTree ast) throws CompileException {
       if (ast == null) {
           throw new CompileException("Null abstract syntax tree passed in to NfaCompiler.");
       }
       try {
           return buildAutomata(ast);
        } catch (IllegalArgumentException e) {
            throw new CompileException(e);
        }
    }
    
    
    @Override
    public Automata<T> compile(final Collection<String> expressions) throws CompileException {
        final List<Automata<T>> automataList = new ArrayList<Automata<T>>();
        for (final String expression : expressions) {
            automataList.add(compile(expression));
        }
        return automataBuilder.buildAlternativesAutomata(automataList);
    }    
    
    
    private Automata<T> buildAutomata(final CommonTree ast) throws CompileException {

        Automata<T> automata = null;

        switch (ast.getToken().getType()) {

            // recursive part of building:
            
            case (regularExpressionParser.SEQUENCE): {
                final List<Automata<T>> sequenceStates = new ArrayList<Automata<T>>();
                for (int childIndex = 0, stop = ast.getChildCount(); childIndex < stop; childIndex++) {
                    final CommonTree child = (CommonTree) ast.getChild(childIndex);
                    final Automata<T> childAutomata = buildAutomata(child);
                    sequenceStates.add(childAutomata);
                }
                automata = automataBuilder.buildSequenceAutomata(sequenceStates);
                break;
            }


            case (regularExpressionParser.ALT): {
                final List<Automata<T>> alternateStates = new ArrayList<Automata<T>>();
                for (int childIndex = 0, stop = ast.getChildCount(); childIndex < stop; childIndex++) {
                    final CommonTree child = (CommonTree) ast.getChild(childIndex);
                    final Automata<T> childAutomata = buildAutomata(child);
                    alternateStates.add(childAutomata);
                }
                automata = automataBuilder.buildAlternativesAutomata(alternateStates);
                break;
            }

            //TODO: nested repeats can be optimised.
            case (regularExpressionParser.REPEAT): {
                final CommonTree nodeToRepeat = (CommonTree) ast.getChild(2);
                final Automata<T> repeatedAutomata = buildAutomata(nodeToRepeat);
                final int minRepeat = ParseUtils.getChildIntValue(ast, 0);
                if (MANY.equals(ParseUtils.getChildStringValue(ast,1))) {
                    automata = automataBuilder.buildMinToManyAutomata(minRepeat, repeatedAutomata);
                } else {
                    final int maxRepeat = ParseUtils.getChildIntValue(ast, 1);
                    automata = automataBuilder.buildMinToMaxAutomata(minRepeat, maxRepeat, repeatedAutomata);
                }
                break;
            }


            case (regularExpressionParser.MANY): {
                final CommonTree zeroToManyNode = (CommonTree) ast.getChild(0);
                final Automata<T> zeroToManyStates = buildAutomata(zeroToManyNode);
                automata = automataBuilder.buildZeroToManyAutomata(zeroToManyStates);
                break;
            }


            case (regularExpressionParser.PLUS): {
                final CommonTree oneToManyNode = (CommonTree) ast.getChild(0);
                final Automata<T> oneToManyStates = buildAutomata(oneToManyNode);
                automata = automataBuilder.buildOneToManyAutomata(oneToManyStates);
                break;
            }


            case (regularExpressionParser.QUESTION_MARK): {
                final CommonTree optionalNode = (CommonTree) ast.getChild(0);
                final Automata<T> optionalStates = buildAutomata(optionalNode);
                automata = automataBuilder.buildOptionalAutomata(optionalStates);
                break;
            }


            // non-recursive part of building:


            case (regularExpressionParser.BYTE): {
                final byte transitionByte = ParseUtils.getHexByteValue(ast);
                automata = automataBuilder.buildSingleByteAutomata(transitionByte);
                break;
            }


            case (regularExpressionParser.ALL_BITMASK): {
                final byte transitionByte = ParseUtils.getBitMaskValue(ast);
                automata = automataBuilder.buildAllBitmaskAutomata(transitionByte);
                break;
            }

            case (regularExpressionParser.ANY_BITMASK): {
                final byte transitionByte = ParseUtils.getBitMaskValue(ast);
                automata = automataBuilder.buildAnyBitmaskAutomata(transitionByte);
                break;
            }


            case (regularExpressionParser.SET): {
                try {
                    final Set<Byte> byteSet = ParseUtils.calculateSetValue(ast);
                    automata = automataBuilder.buildSetAutomata(byteSet,false);
                    break;
                } catch (ParseException ex) {
                    throw new CompileException(ex);
                }
            }


            case (regularExpressionParser.INVERTED_SET): {
                try {
                    final Set<Byte> byteSet = ParseUtils.calculateSetValue(ast);
                    automata = automataBuilder.buildSetAutomata(byteSet, true);
                    break;
                } catch (ParseException ex) {
                    throw new CompileException(ex);
                }
            }

            case (regularExpressionParser.ANY): {
                automata = automataBuilder.buildAnyByteAutomata();
                break;
            }


            case (regularExpressionParser.CASE_SENSITIVE_STRING): {
                final String str = ParseUtils.unquoteString(ast.getText());
                automata = automataBuilder.buildCaseSensitiveStringAutomata(str);
                break;
            }


            case (regularExpressionParser.CASE_INSENSITIVE_STRING): {
                final String str = ParseUtils.unquoteString(ast.getText());
                automata = automataBuilder.buildCaseInsensitiveStringAutomata(str);
                break;
            }

            default: {
                throw new CompileException(ParseUtils.getTypeErrorMessage(ast));
            }
        }
        
        return automata;
    }

}
