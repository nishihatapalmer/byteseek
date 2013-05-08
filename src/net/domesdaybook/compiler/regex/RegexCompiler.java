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

import net.domesdaybook.automata.Automata;
import net.domesdaybook.automata.builder.GlushkovRegexBuilder;
import net.domesdaybook.automata.builder.RegexBuilder;
import net.domesdaybook.compiler.AbstractCompiler;
import net.domesdaybook.compiler.CompileException;
import net.domesdaybook.parser.ParseException;
import net.domesdaybook.parser.Parser;
import net.domesdaybook.parser.RegexParser;
import net.domesdaybook.parser.tree.ParseTree;
import net.domesdaybook.parser.tree.ParseTreeType;
import net.domesdaybook.parser.tree.ParseTreeUtils;
import net.domesdaybook.parser.tree.node.ByteNode;
import net.domesdaybook.parser.tree.node.ChildrenNode;

/**
 * A compiler which produces Non-deterministic Finite-state Automata (NFA)
 * from an expression.  
 * <p>
 * This class extends {@link AbstractCompiler}, and takes the abstract syntax tree
 * produced by the {@link Parser} and uses it to direct the construction of an NFA 
 * using a {@link RegexBuilder}, which knows how to build automata for various types
 * of AST tree node.
 * 
 * @param <T> The type of object which a match of the regular expression should return.
 * @author Matt Palmer
 */
//FIXME: the final automata states need to be associated with some objects passed in.
//       If compiling from a string expression, then associate final states with the string expression?
//       If compiling from an abstract syntax tree, then associate final states with what?  The current AST node?  The root AST node?
//       Should allow association with arbitrary objects associated with the expression itself?  Would require changes to the compiler
//       interface itself, or the passing in of a wrapped String-Association object...
//       If compiling from a collection of string expressions, then we also need to associate different final states correctly with different
//       expressions.
public final class RegexCompiler<T> extends AbstractCompiler<Automata<T>, ParseTree> {

    private static final boolean NOT_YET_INVERTED = false;
 
    private final RegexBuilder<T, ParseTree> regexBuilder;

    /**
     * Constructs a RegexCompiler, using the default {@link RegexBuilder} object.
     * The parser used will be the default parser defined in {@link AbstractCompiler}
     */
    public RegexCompiler() {
    	this(null, null);
    }

    
    /**
     * Constructs a RegexCompiler, supplying the {@link RegexBuilder} object
     * to use to construct the NFA from the parse tree.
     * The parser used will be the default parser defined in {@link AbstractCompiler}
     *
     * @param regexBuilder the NFA builder used to create an NFA from an abstract
     *        syntax tree (AST).
     */
    public RegexCompiler(final RegexBuilder<T, ParseTree> regexBuilder) {
        this(null, regexBuilder);
    }
    
    
    /**
     * Constructs a RegexCompiler, supplying the {@link Parser} to use.
     * The default {@link RegexBuilder} object will be used to construct the NFA
     * from the parse tree.
     * 
     * @param parser The Parser to use to produce the parse tree.
     */
    public RegexCompiler(final Parser<ParseTree> parser) {
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
     *        syntax tree defined through a ParseTree interface..
     */
    public RegexCompiler(final Parser<ParseTree> parser, final RegexBuilder<T, ParseTree> regexBuilder) {
    	super(parser == null? new RegexParser() : parser);
    	this.regexBuilder = regexBuilder != null? regexBuilder
    	    : new GlushkovRegexBuilder<T, ParseTree>(
    	          new ParseTreeTransitionFactory<T>());
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
    
    
    protected Automata<T> doCompile(final ParseTree ast) throws CompileException, ParseException {
        switch (ast.getParseTreeType()) {
            case BYTE:						// Drop through - bytes, bitmasks (all and any), sets, ranges and any bytes
            case ALL_BITMASK:				// are all just transitions on some specification of byte values we
            case ANY_BITMASK:				// can make a transition on.
            case SET:
            case RANGE:
            case ANY:						return createTransitionAutomata(ast);
            case STRING:					return createStringAutomata(ast);
            case CASE_INSENSITIVE_STRING:	return createCaseInsensitiveStringAutomata(ast);
            case SEQUENCE:					return createSequenceAutomata(ast);
            case ALTERNATIVES:				return createAlternativesAutomata(ast);
            case REPEAT:					return createRepeatedAutomata(ast);
            case REPEAT_MIN_TO_MAX:			return createRepeatMinToMaxAutomata(ast);
            case REPEAT_MIN_TO_MANY:		return createRepeatMinToManyAutomata(ast);
            case ZERO_TO_MANY:				return createZeroToManyAutomata(ast);
            case ONE_TO_MANY:				return createOneToManyAutomata(ast);
            case OPTIONAL:					return createOptionalAutomata(ast);
            
            default: throw new CompileException(getTypeErrorMessage(ast));
        }
    }


    private Automata<T> createTransitionAutomata(final ParseTree ast) {
      return regexBuilder.buildTransitionAutomata(ast, NOT_YET_INVERTED);
    }    
    

    private Automata<T> createStringAutomata(final ParseTree ast) throws ParseException {
        return regexBuilder.buildSequenceAutomata(getByteAutomataList(ast.getTextValue()));
    }    
    

    private Automata<T> createCaseInsensitiveStringAutomata(final ParseTree ast) throws ParseException {
        return regexBuilder.buildSequenceAutomata(getCaseInsensitiveAutomataList(ast.getTextValue()));
    }


    private Automata<T> createSequenceAutomata(final ParseTree ast) throws ParseException, CompileException {
      return regexBuilder.buildSequenceAutomata(compileChildren(ast));
    }    

    
    private Automata<T> createAlternativesAutomata(final ParseTree ast) throws ParseException, CompileException {
    	//TODO: optimise alternatives which match single bytes into one set of bytes to match.
    	//      Or should this be done here?  Possibly a global automata optimisation?
    	//      e.g. any automata which results in more than one transition to the same
    	//      state can collapse those into a single transition encompassing the set of
    	//      all transition values.  
    	return regexBuilder.buildAlternativesAutomata(compileChildren(ast));
    }

    
    private Automata<T> createOptionalAutomata(final ParseTree ast) throws CompileException, ParseException {
      return regexBuilder.buildOptionalAutomata(compileFirstChild(ast));
    }

    
    private Automata<T> createOneToManyAutomata(final ParseTree ast) throws CompileException, ParseException {
      return regexBuilder.buildOneToManyAutomata(compileFirstChild(ast));
    }


    private Automata<T> createZeroToManyAutomata(final ParseTree ast) throws CompileException, ParseException {
      return regexBuilder.buildZeroToManyAutomata(compileFirstChild(ast));
    }


    private Automata<T> createRepeatedAutomata(final ParseTree ast) throws CompileException, ParseException {
      final Automata<T> automata = doCompile(ParseTreeUtils.getLastChild(ast));
      final int minRepeat = ParseTreeUtils.getFirstRepeatValue(ast);
      return regexBuilder.buildMinToMaxAutomata(minRepeat, minRepeat, automata);
    }

    
    private Automata<T> createRepeatMinToMaxAutomata(final ParseTree ast) throws CompileException, ParseException {
      final Automata<T> automata = doCompile(ParseTreeUtils.getLastChild(ast));
      final int minRepeat = ParseTreeUtils.getFirstRepeatValue(ast);
      final int maxRepeat = ParseTreeUtils.getSecondRepeatValue(ast);
      return regexBuilder.buildMinToMaxAutomata(minRepeat, maxRepeat, automata);
    }
    

    private Automata<T> createRepeatMinToManyAutomata(final ParseTree ast) throws CompileException, ParseException {
      final Automata<T> automata = doCompile(ParseTreeUtils.getLastChild(ast));
      final int minRepeat = ParseTreeUtils.getFirstRepeatValue(ast);
      return regexBuilder.buildMinToManyAutomata(minRepeat, automata);
    }    
    
    
    protected ParseTree joinExpressions(final List<ParseTree> expressions)
        throws ParseException, CompileException {
      return new ChildrenNode(ParseTreeType.ALTERNATIVES, expressions);
    }

    
    private Automata<T> compileFirstChild(final ParseTree ast) throws CompileException, ParseException {
      return doCompile(ParseTreeUtils.getFirstChild(ast));
    }

    private List<Automata<T>> compileChildren(final ParseTree ast) throws CompileException, ParseException {
      final List<Automata<T>> automataList = new ArrayList<Automata<T>>();
      for (final ParseTree child : ast.getChildren()) {
        automataList.add(doCompile(child));
      }      
      return automataList;
    }
    
    
    private List<Automata<T>> getByteAutomataList(final String fromString) {
      final List<Automata<T>> automataList = new ArrayList<Automata<T>>(fromString.length());
      final ByteNode byteValue = new ByteNode((byte) 0);
      for (int i = 0; i < fromString.length(); i++) {
        byteValue.setByteValue((byte) fromString.charAt(i));
        automataList.add(createTransitionAutomata(byteValue));
      }      
      return automataList;
    }
    
    
    private List<Automata<T>> getCaseInsensitiveAutomataList(final String fromString) {
      final List<Automata<T>> automataList = new ArrayList<Automata<T>>(fromString.length());
      final ParseTree set = createTwoByteSet();
      final ByteNode lowerByte = (ByteNode) set.getChildren().get(0);
      final ByteNode upperByte = (ByteNode) set.getChildren().get(1);
      for (int i = 0; i < fromString.length(); i++) {
        final char character = fromString.charAt(i);
        final byte upper = (byte) Character.toUpperCase(character);
        final byte lower = (byte) Character.toLowerCase(character);
        lowerByte.setByteValue(lower);
        if (lower == upper) {
          automataList.add(createTransitionAutomata(lowerByte));
        } else {
          upperByte.setByteValue(upper);
          automataList.add(createTransitionAutomata(set));
        }
      }      
      return automataList;      
    }

    
    private ParseTree createTwoByteSet() {
      final ChildrenNode twoByteSet = new ChildrenNode(ParseTreeType.SET);
      twoByteSet.addChild(new ByteNode((byte) 0));
      twoByteSet.addChild(new ByteNode((byte) 0));
      return twoByteSet;
    }
    
    
    private String getTypeErrorMessage(final ParseTree ast) {
        final ParseTreeType type = ast.getParseTreeType();
        return String.format("Unknown parse tree type %s with description: %s", 
                              type, type.getDescription());
    }
    
}
