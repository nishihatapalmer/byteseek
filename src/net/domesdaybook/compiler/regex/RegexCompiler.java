/*
 * Copyright Matt Palmer 2009-2012, All rights reserved.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.domesdaybook.automata.Automata;
import net.domesdaybook.automata.base.ByteMatcherTransitionFactory;
import net.domesdaybook.automata.regex.GlushkovRegexBuilder;
import net.domesdaybook.automata.regex.RegexBuilder;
import net.domesdaybook.compiler.AbstractCompiler;
import net.domesdaybook.compiler.CompileException;
import net.domesdaybook.parser.ParseException;
import net.domesdaybook.parser.ParseTree;
import net.domesdaybook.parser.ParseTreeType;
import net.domesdaybook.parser.ParseTreeUtils;
import net.domesdaybook.parser.Parser;
import net.domesdaybook.parser.regex.regularExpressionParser;

/**
 * A compiler which produces Non-deterministic Finite-state Automata (NFA)
 * from an expression.  
 * <p>
 * This class extends {@link AbstractCompiler}, which
 * parses the expression using an ANTLR generated parser, and turns it into
 * an abstract syntax tree.  The compiler takes the abstract syntax tree
 * and uses it to direct the construction of an NFA using various builder
 * and factory helper classes.
 * 
 * @param <T> The type of object which a match of the regular expression should return.
 * @author Matt Palmer
 */
public final class RegexCompiler<T> extends AbstractCompiler<Automata<T>> {

    private static final String MANY = "*";
    
    private final RegexBuilder<T> regexBuilder;

    /**
     * Constructs a RegexCompiler, using the default {@link RegexBuilder} object.
     * The parser used will be the default parser defined in {@link AbstractCompiler}
     *
     * By default, it uses the {@link ByteMatcherTransitionFactory} and
     * the {@link net.domesdaybook.automata.base.BaseStateFactory} to make a {@link GlushkovRegexBuilder} to
     * produce the NFA.
     */
    public RegexCompiler() {
    	this(null,null);
    }

    
    /**
     * Constructs a RegexCompiler, supplying the {@link RegexBuilder} object
     * to use to construct the NFA from the parse tree.
     * The parser used will be the default parser defined in {@link AbstractCompiler}
     *
     * @param regexBuilder the NFA builder used to create an NFA from an abstract
     *        syntax tree (AST).
     */
    public RegexCompiler(final RegexBuilder<T> regexBuilder) {
        this(null, regexBuilder);
    }
    
    
    /**
     * Constructs a RegexCompiler, supplying the {@link Parser} to use.
     * The default {@link RegexBuilder} object will be used to construct the NFA
     * from the parse tree.
     * 
     * @param parser The Parser to use to produce the parse tree.
     */
    public RegexCompiler(final Parser parser) {
    	this(parser, null);
    }
    
    
    /**
     * Constructs a RegexCompiler, supplying the {@link Parser} to use, and the
     * {@link RegexBuilder} object to use to construct the NFA from the parse tree.
     * <p>
     * If the parser is null, then the parser used will be the default parser defined
     * in {@link AbstractCompiler}.  If the regexBuilder is null, then the default
     * {@link GlushkovRegexBuilder} will be used.
     * 
     * @param parser The Parser to use to produce the parse tree.
     * @param regexBuilder the NFA builder used to create an NFA from an abstract
     *        syntax tree (AST).
     */
    public RegexCompiler(final Parser parser, final RegexBuilder<T> regexBuilder) {
    	super(parser);
    	this.regexBuilder = regexBuilder == null? new GlushkovRegexBuilder<T>() : regexBuilder;
    }

    
    /**
     * Compiles a Non-deterministic Finite-state Automata (NFA) from the
     * abstract syntax tree provided by the {@link AbstractCompiler} which this
     * class extends.
     * <p>
     * It uses a {@link RegexBuilder} object to build the actual automata,
     * returning only the initial state of the final automata.
     *
     * @param ast The abstract syntax tree to compile the State automata from.
     * @return An automata recognising the expression described by the abstract syntax tree.
     * @throws CompileException If the abstract syntax tree could not be parsed.
     */
    @Override
    public Automata<T> compile(final ParseTree ast) throws CompileException {
       if (ast == null) {
           throw new CompileException("Null abstract syntax tree passed in to NfaCompiler.");
       }
       try {
           return buildAutomata(ast);
        } catch (IllegalArgumentException e) {
            throw new CompileException(e);
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Automata<T> compile(final Collection<String> expressions) throws CompileException {
        final List<Automata<T>> automataList = new ArrayList<Automata<T>>();
        for (final String expression : expressions) {
            automataList.add(compile(expression));
        }
        return regexBuilder.buildAlternativesAutomata(automataList);
    }    
    
    
    private Automata<T> buildAutomata(final ParseTree ast) throws CompileException {

        Automata<T> automata = null;

        try {
        
            switch (ast.getParseTreeType().getId()) {
    
                // recursive part of building:
                
                case (ParseTreeType.SEQUENCE_ID): {
                    final List<Automata<T>> sequenceStates = new ArrayList<Automata<T>>();
                    for (final ParseTree child : ast.getChildren()) {
                      sequenceStates.add(buildAutomata(child));
                    }
                    automata = regexBuilder.buildSequenceAutomata(sequenceStates);
                    break;
                }
    
                case (ParseTreeType.ALTERNATIVES_ID): {
                    final List<Automata<T>> alternateStates = new ArrayList<Automata<T>>();
                    for (final ParseTree child : ast.getChildren()) {
                      alternateStates.add(buildAutomata(child));
                    }
                    automata = regexBuilder.buildAlternativesAutomata(alternateStates);
                    break;
                }
    
                //TODO: nested repeats can be optimised.
                case (ParseTreeType.REPEAT_ID): {
                    final ParseTree nodeToRepeat = ast.getChildren().get(2);
                    final Automata<T> repeatedAutomata = buildAutomata(nodeToRepeat);
                    final int minRepeat = ast.getChildren().get(0).getIntValue();
                    final ParseTree maxTree = ast.getChildren().get(1);
                    if (maxTree.getParseTreeType() == ParseTreeType.INTEGER) {
                      automata = regexBuilder.buildMinToMaxAutomata(minRepeat, maxTree.getIntValue(), repeatedAutomata);
                    } else { 
                      //FIXME: test for * text value...? Or force parse into MANY object?
                      // if (MANY.equals(ParseTreeUtils.getChildStringValue(ast,1)))) {
                      automata = regexBuilder.buildMinToManyAutomata(minRepeat, repeatedAutomata);
                    }
                    break;
                }
    
                case (ParseTreeType.MANY_ID): {
                    final ParseTree zeroToManyNode = ast.getChildren().get(0);
                    final Automata<T> zeroToManyStates = buildAutomata(zeroToManyNode);
                    automata = regexBuilder.buildZeroToManyAutomata(zeroToManyStates);
                    break;
                }
    
                case (ParseTreeType.ONE_TO_MANY_ID): {
                    final ParseTree oneToManyNode = ast.getChildren().get(0);
                    final Automata<T> oneToManyStates = buildAutomata(oneToManyNode);
                    automata = regexBuilder.buildOneToManyAutomata(oneToManyStates);
                    break;
                }
    
                case (ParseTreeType.OPTIONAL_ID): {
                    final ParseTree optionalNode = ast.getChildren().get(0);
                    final Automata<T> optionalStates = buildAutomata(optionalNode);
                    automata = regexBuilder.buildOptionalAutomata(optionalStates);
                    break;
                }
    
    
                // non-recursive part of building:
    
    
                //TODO: evaluate whether bitmasks should return bitmasks or the set of bytes
                //      they match, or both.  Who should be responsible for assembling the
                //      sets of bytes..?  In one compiler the bitmask parse tree nodes do it,
                //      but here we defer to the transition builder in the regex builder....
                
                case (ParseTreeType.BYTE_ID): {
                    automata = regexBuilder.buildSingleByteAutomata(ast.getByteValue());
                    break;
                }
    
    
                case (ParseTreeType.ALL_BITMASK_ID): {
                    automata = regexBuilder.buildAllBitmaskAutomata(ast.getByteValue());
                    break;
                }
    
                case (ParseTreeType.ANY_BITMASK_ID): {
                    automata = regexBuilder.buildAnyBitmaskAutomata(ast.getByteValue());
                    break;
                }
    
    
                case (ParseTreeType.SET_ID): {
                    try {
                        automata = regexBuilder.buildSetAutomata(ast.getByteSetValue(), false);
                        break;
                    } catch (ParseException ex) {
                        throw new CompileException(ex);
                    }
                }
    
                //TODO: do we need an inverted set id?
                case (ParseTreeType.INVERTED_SET_ID): {
                    try {
                        final Set<Byte> byteSet = ParseTreeUtils.calculateSetValue(ast);
                        automata = regexBuilder.buildSetAutomata(byteSet, true);
                        break;
                    } catch (ParseException ex) {
                        throw new CompileException(ex);
                    }
                }
    
                case (ParseTreeType.ANY_ID): {
                    automata = regexBuilder.buildAnyByteAutomata();
                    break;
                }
    
    
                case (ParseTreeType.CASE_SENSITIVE_STRING_ID): {
                    automata = regexBuilder.buildCaseSensitiveStringAutomata(ast.getTextValue());
                    break;
                }
    
    
                case (regularExpressionParser.CASE_INSENSITIVE_STRING): {
                    automata = regexBuilder.buildCaseInsensitiveStringAutomata(ast.getTextValue());
                    break;
                }
    
                default: {
                    throwCompileException(ast);
                }
            }
        } catch (ParseException pe) {
          throw new CompileException(pe);
        } catch (ArrayIndexOutOfBoundsException ai) {
        	final String message = "A mandatory child node did not exist for the tree node id %d with name %s";
        	final ParseTreeType treeType = ast.getParseTreeType();
        	throw new CompileException(String.format(message, treeType.getId(), treeType.getName()), ai);
        }
        
        return automata;
    }
    
    /**
     * @param ast
     * @throws ParseException 
     */
    private void throwCompileException(ParseTree ast) throws CompileException {
      final ParseTreeType type = ast.getParseTreeType();
      final String message = String.format("Unknown parse tree type, id %d with description: %s", 
                         type.getId(), type.getDescription());
      throw new CompileException(message);
    }    

}
