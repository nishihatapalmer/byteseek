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

package net.domesdaybook.compiler.matcher;

import java.util.ArrayList;
import java.util.List;

import net.domesdaybook.compiler.AbstractCompiler;
import net.domesdaybook.compiler.CompileException;
import net.domesdaybook.matcher.bytes.ByteMatcherFactory;
import net.domesdaybook.matcher.bytes.SetAnalysisByteMatcherFactory;
import net.domesdaybook.matcher.sequence.ByteArrayMatcher;
import net.domesdaybook.matcher.sequence.CaseInsensitiveSequenceMatcher;
import net.domesdaybook.matcher.sequence.OptimisingSequenceMatcherFactory;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.matcher.sequence.SequenceMatcherFactory;
import net.domesdaybook.parser.ParseException;
import net.domesdaybook.parser.Parser;
import net.domesdaybook.parser.regex.RegexParser;
import net.domesdaybook.parser.tree.ParseTree;
import net.domesdaybook.parser.tree.ParseTreeType;
import net.domesdaybook.parser.tree.ParseTreeUtils;
import net.domesdaybook.parser.tree.node.ChildrenNode;

/**
 * A compiler which produces a {@link SequenceMatcher} from an
 * abstract syntax tree provided by a {@link Parser}.
 * <p>
 * It can handle nearly all the syntax processable by the {@link net.domesdaybook.parser.regex.RegexParser},
 * but it cannot handle any syntax which would give variable lengths to
 * match, or which would have alternative sequences of bytes,
 * as a sequence matcher can only match a single defined sequence.
 * <p>
 * This means that it *cannot* handle alternatives (X|Y|Z),
 * optionality X?, variable length repeats {n-m}, 
 * and the wildcard repeats * and +.  It can handle fixed length repeats {n}.  
 * 
 * @author Matt Palmer
 */
public class SequenceMatcherCompiler extends AbstractCompiler<SequenceMatcher, ParseTree> {

	// Protected constants:

	protected static final boolean NOT_YET_INVERTED = false;
	
	protected static SequenceMatcherCompiler defaultCompiler;
    
    /**
     * Compiles a SequenceMatcher from a byteSeek regular expression (limited to
     * syntax which produces fixed-length sequences).  It will use the default
     * {@link SetAnalysisByteMatcherFactory} to produce matchers for sets of bytes, a
     * {@link OptimisingSequenceMatcherFactory} to join lists of sequences into a 
     * sequence matcher, and a {@link RegexParser} to parse the expression into an abstract
     * syntax tree.
     * 
     * @param expression The regular expression to compile
     * @return SequenceMatcher a SequenceMatcher matching the regular expression.
     * @throws CompileException If the expression could not be compiled into a SequenceMatcher.
     */
    public static SequenceMatcher compileFrom(final String expression) throws CompileException {
        defaultCompiler = new SequenceMatcherCompiler();
        return defaultCompiler.compile(expression);
    }
    
    
    protected final ByteMatcherFactory byteMatcherFactory;
    protected final SequenceMatcherFactory sequenceMatcherFactory;

    
    /**
     * Default constructor which uses the {@link SetAnalysisByteMatcherFactory}
     * to produce matchers for sets of bytes, and a {@link RegexParser} to produce
     * the abstract syntax tree.  It also uses the {@link OptimisingSequenceMatcherFactory}
     * to produce sequences from lists of sequence matchers.
     * 
     */
    public SequenceMatcherCompiler() {
       this(null, null, null);
    }

    /**
     * Constructor which uses the provided {@link ByteMatcherFactory} to
     * produce matchers for sets of bytes, and a {@link RegexParser} to produce 
     * the abstract syntax tree. It also uses the  {@link OptimisingSequenceMatcherFactory}
     * to produce sequences from lists of sequence matchers.
     * 
     * @param factoryToUse The ByteMatcherFactory used to produce matchers
     * for sets of bytes.
     */
    public SequenceMatcherCompiler(final ByteMatcherFactory factoryToUse) {
        this(null, factoryToUse, null);
    }
    
    
    /**
     * Constructor which uses the provided {@link SequenceMatcherFactory} to
     * produce a sequence from a list of sequences, and a {@link RegexParser}
     * to produce the abstract syntax tree. It also uses the
     * {@link SetAnalysisByteMatcherFactory} to produce matchers from sets of bytes.
     * 
     * @param factoryToUse The SequenceMatcherFactory to produce sequences from lists of sequences.
     */
    public SequenceMatcherCompiler(final SequenceMatcherFactory factoryToUse) {
        this(null, null, factoryToUse);
    }   

    
    /**
     * Constructor which uses the provided {@link SequenceMatcherFactory} to
     * produce a sequence from a list of sequences, and the provided
     * {@link ByteMatcherFactory} to produce matchers from sets of bytes.  The
     * parser used will be the {@link RegexParser}.
     * 
     * @param byteFactory The ByteMatcherFactory to produce matchers from sets of bytes.
     * @param sequenceFactory The SequenceMatcherFactory to produce sequences from lists of sequences.
     */
    public SequenceMatcherCompiler(final ByteMatcherFactory byteFactory,
    								final SequenceMatcherFactory sequenceFactory) {
        this(null, byteFactory, sequenceFactory);
    }   
    
    

    /**
     * Constructor which uses the provided {@link Parser} to produce the abstract
     * syntax tree, and the default {@SimpleByteMatcherFactory} to build the byte
     * matchers.
     * 
     * @param parser The parser to use to produce the abstract syntax tree.
     */    
    public SequenceMatcherCompiler(final Parser<ParseTree> parser) {
        this(parser, null, null);
    }
    
    /**
     * Constructor which uses the provided {@link ByteMatcherFactory} to
     * produce matchers for sets of bytes, and the provided {@link Parser} to
     * product the abstract syntax tree.
     * <p>
     * If the parser is null, then the parser used will be the default parser defined
     * in {@link AbstractCompiler}.  If the factory is null, then the default
     * {@link SetAnalysisByteMatcherFactory} will be used.
     * 
     * @param parser The parser to use to produce the abstract syntax tree. 
     * @param factoryToUse The ByteMatcherFactory used to produce matchers
     * for sets of bytes.
     */    
    public SequenceMatcherCompiler(final Parser<ParseTree> parser, 
    							   final ByteMatcherFactory byteFactoryToUse) {
        this(parser, byteFactoryToUse, null);
    }
    
    /**
     * Constructor which uses the provided {@link SequenceMatcherFactory} to
     * produce matchers for lists of sequences, and the provided {@link Parser} to
     * produce the abstract syntax tree.  It uses the default {@link ByteMatcherFactory}
     * to produce byte matchers for sets of bytes.
     * <p>
     * If the parser is null, then the parser used will be the default parser defined
     * in {@link AbstractCompiler}.  If the factory is null, then the default
     * {@link SetAnalysisByteMatcherFactory} will be used.
     * 
     * @param parser The parser to use to produce the abstract syntax tree. 
     * @param sequenceFactoryToUse The SequenceMatcherFactory used to produce sequences from a list of sequences.
     * for sets of bytes.
     */    
    public SequenceMatcherCompiler(final Parser<ParseTree> parser, 
    							    final SequenceMatcherFactory sequenceFactoryToUse) {
        this(parser, null, sequenceFactoryToUse);
    }    
    
    /**
     * Constructor which uses the provided {@link ByteMatcherFactory} to
     * produce matchers for sets of bytes, and the provided {@link Parser} to
     * product the abstract syntax tree.
     * <p>
     * If the parser is null, then the parser used will be the {@link RegexParser}.
     * If the byte matcher factory is null, then a {@link SetAnalysisByteMatcherFactory} will be used.
     * If the sequence matcher factory is null, then a {@link OptimisingSequenceMatcherFactory} will be used.
     * 
     * @param parser The parser to use to produce the abstract syntax tree. 
     * @param byteFactoryToUse The ByteMatcherFactory used to produce matchers from a set of bytes
     * @param sequenceFactoryToUse The SequenceMatcherFactory used to produce sequences from a list of sequences.
     */    
    public SequenceMatcherCompiler(final Parser<ParseTree> parser,
    								final ByteMatcherFactory byteFactoryToUse,
    								final SequenceMatcherFactory sequenceFactoryToUse) {
        super(parser == null? new RegexParser() : parser);
        byteMatcherFactory = byteFactoryToUse != null? 
        					 byteFactoryToUse :  new SetAnalysisByteMatcherFactory();
    	sequenceMatcherFactory = sequenceFactoryToUse != null? 
    			                 sequenceFactoryToUse :  new OptimisingSequenceMatcherFactory();
    }    


    /**
     * Builds the ParseTree node into a list of sequence matchers. 
     * Then it uses a SequenceMatcherFactory to produce a single SequenceMatcher from the list.
     *
     * @param ast The abstract syntax tree to compile.
     * @return A SequenceMatcher representing the expression.
     * @throws ParseException If the ast could not be parsed.
     */
    protected SequenceMatcher doCompile(final ParseTree ast) throws ParseException {
    	return sequenceMatcherFactory.create(buildSequenceList(ast, new ArrayList<SequenceMatcher>()));
    }
    
    
    /**
     * Parses the ParseTree node passed in, building a list of sequence matchers from it.
     * 
     * @param matcherNode The abstract syntax tree to parse.
     * @param sequenceList A sequence matcher list to append to.
     * @return A list of sequence matchers in the order specified by the ParseTree.
     * @throws ParseException If there is a problem parsing the parse tree.
     * @throws NullPointerException if the parse tree or sequence list are null.
     */
    protected List<SequenceMatcher> buildSequenceList(final ParseTree matcherNode,
    										  		   final List<SequenceMatcher> sequenceList)
    										  		   throws ParseException {
    	switch (matcherNode.getParseTreeType()) {
    		case BYTE:           			addByteMatcher(                 	matcherNode, sequenceList); break;
    		case ANY:                     	addAnyMatcher(						matcherNode, sequenceList); break;
    		case ALL_BITMASK:   			addAllBitmaskMatcher(				matcherNode, sequenceList); break;
    		case ANY_BITMASK:   			addAnyBitmaskMatcher(				matcherNode, sequenceList); break;
    		case RANGE:       				addRangeMatcher(					matcherNode, sequenceList); break;
    		case STRING:   					addStringMatcher(					matcherNode, sequenceList); break;
    		case CASE_INSENSITIVE_STRING: 	addCaseInsensitiveStringMatcher(	matcherNode, sequenceList); break;
    		case SEQUENCE:          		addSequenceMatcher(					matcherNode, sequenceList); break;
    		case REPEAT:          			addRepeatedSequence(				matcherNode, sequenceList); break;
    		case SET: 						// drop through - sets and alternatives are both treated as sets.
    		case ALTERNATIVES: 				addSetMatcher(						matcherNode, sequenceList); break;
    		
    		default: throw new ParseException(getTypeErrorMessage(matcherNode));
    	}
    	return sequenceList;
    }


	/**
     * @param ast
     * @param sequenceList
     * @throws ParseException
     */
    private void addRepeatedSequence(final ParseTree ast,
                                     final List<SequenceMatcher> sequenceList)
        throws ParseException {
      final int timesToRepeat = ParseTreeUtils.getFirstRepeatValue(ast);
      final SequenceMatcher sequenceToRepeat = doCompile(ParseTreeUtils.getNodeToRepeat(ast));
      for (int count = 1; count <= timesToRepeat; count++) {
      	sequenceList.add(sequenceToRepeat);
      }
    }

    /**
     * @param ast
     * @param sequenceList
     * @throws ParseException
     */
    private void addSequenceMatcher(final ParseTree ast,
                                    final List<SequenceMatcher> sequenceList)
        throws ParseException {
      for (final ParseTree child : ast.getChildren()) {
      	buildSequenceList(child, sequenceList);
      }
    }

    /**
     * @param ast
     * @param sequenceList
     * @throws ParseException
     */
    private void addCaseInsensitiveStringMatcher(final ParseTree ast,
                                                 final List<SequenceMatcher> sequenceList)
        throws ParseException {
      sequenceList.add(new CaseInsensitiveSequenceMatcher(ast.getTextValue()));
    }

    /**
     * @param ast
     * @param sequenceList
     * @throws ParseException
     */
    private void addStringMatcher(final ParseTree ast,
                                  final List<SequenceMatcher> sequenceList)
        throws ParseException {
      sequenceList.add(new ByteArrayMatcher(ast.getTextValue()));
    }

    /**
     * @param ast
     * @param sequenceList
     * @throws ParseException
     */
    private void addSetMatcher(final ParseTree ast,
                               final List<SequenceMatcher> sequenceList)
        throws ParseException {
      sequenceList.add(CompilerUtils.createMatcherFromSet(ast, byteMatcherFactory));
    }

    /**
     * @param ast
     * @param sequenceList
     * @throws ParseException
     */
    private void addRangeMatcher(final ParseTree ast,
                                 final List<SequenceMatcher> sequenceList)
        throws ParseException {
      sequenceList.add(CompilerUtils.createRangeMatcher(ast));
    }

    /**
     * @param ast
     * @param sequenceList
     * @throws ParseException
     */
    private void addAnyBitmaskMatcher(final ParseTree ast,
                                      final List<SequenceMatcher> sequenceList)
        throws ParseException {
      sequenceList.add(CompilerUtils.createAnyBitmaskMatcher(ast));
    }

    /**
     * @param ast
     * @param sequenceList
     * @throws ParseException
     */
    private void addAllBitmaskMatcher(final ParseTree ast,
                                      final List<SequenceMatcher> sequenceList)
        throws ParseException {
      sequenceList.add(CompilerUtils.createAllBitmaskMatcher(ast));
    }

    /**
     * @param ast
     * @param sequenceList
     * @throws ParseException
     */
    private void addAnyMatcher(final ParseTree ast,
                               final List<SequenceMatcher> sequenceList)
        throws ParseException {
      sequenceList.add(CompilerUtils.createAnyMatcher(ast));
    }

    /**
     * @param ast
     * @param sequenceList
     * @throws ParseException
     */
    private void addByteMatcher(final ParseTree ast,
                                final List<SequenceMatcher> sequenceList)
        throws ParseException {
      sequenceList.add(CompilerUtils.createByteMatcher(ast));
    }
    
    
	@Override
	protected ParseTree joinExpressions(List<ParseTree> expressions) throws ParseException, CompileException {
		return new ChildrenNode(ParseTreeType.SEQUENCE, expressions, NOT_YET_INVERTED);
    }    
	
	
	private String getTypeErrorMessage(final ParseTree ast) {
		final ParseTreeType type = ast.getParseTreeType();
		return String.format("Unknown type, id %d with description: %s", 
		                       type, type.getDescription());
	}    	
	
   
}
