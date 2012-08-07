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
import net.domesdaybook.matcher.sequence.BytesToSequencesMatcherFactory;
import net.domesdaybook.matcher.sequence.CaseInsensitiveSequenceMatcher;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.matcher.sequence.SequenceMatcherFactory;
import net.domesdaybook.parser.ParseException;
import net.domesdaybook.parser.Parser;
import net.domesdaybook.parser.regex.RegexParser;
import net.domesdaybook.parser.tree.ParseTree;
import net.domesdaybook.parser.tree.ParseTreeType;
import net.domesdaybook.parser.tree.ParseTreeUtils;
import net.domesdaybook.parser.tree.node.StructuralNode;

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
     * {@link SetAnalysisByteMatcherFactory} to produce matchers for sets of bytes.
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
     * to produce matchers for sets of bytes, and the parser defined in 
     * AbstractCompiler to produce the abstract syntax tree.
     * 
     */
    public SequenceMatcherCompiler() {
       this(null, null);
    }

    /**
     * Constructor which uses the provided {@link ByteMatcherFactory} to
     * produce matchers for sets of bytes, and the parser defined in 
     * AbstractCompiler to produce the abstract syntax tree.
     * 
     * @param factoryToUse The ByteMatcherFactory used to produce matchers
     * for sets of bytes.
     */
    public SequenceMatcherCompiler(final ByteMatcherFactory factoryToUse) {
        this(null, factoryToUse);
    }
    

    /**
     * Constructor which uses the provided {@link Parser} to produce the abstract
     * syntax tree, and the default {@SimpleByteMatcherFactory} to build the byte
     * matchers.
     * 
     * @param parser The parser to use to produce the abstract syntax tree.
     */    
    public SequenceMatcherCompiler(final Parser<ParseTree> parser) {
        this(parser, null);
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
    
    //TODO: add constructors which take a sequenceMatcherFactory as a parameter.
    
    
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
		   final ByteMatcherFactory byteFactoryToUse,
    		   final SequenceMatcherFactory sequenceFactoryToUse) {
        super(parser == null? new RegexParser() : parser);
        byteMatcherFactory = byteFactoryToUse != null? 
        					 byteFactoryToUse :  new SetAnalysisByteMatcherFactory();
    	sequenceMatcherFactory = sequenceFactoryToUse != null? 
    			                 sequenceFactoryToUse :  new BytesToSequencesMatcherFactory();
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
    	final List<SequenceMatcher> sequenceList = buildSequenceList(ast, new ArrayList<SequenceMatcher>());
    	return sequenceMatcherFactory.create(sequenceList);
    }
    
    
    /**
     * Parses the ParseTree node passed in, building a list of sequence matchers from it.
     * 
     * @param matcherTree The abstract syntax tree to parse.
     * @param sequenceList A sequence matcher list to append to.
     * @return A list of sequence matchers in the order specified by the ParseTree.
     * @throws ParseException If there is a problem parsing the parse tree.
     * @throws NullPointerException if the parse tree or sequence list are null.
     */
    protected List<SequenceMatcher> buildSequenceList(final ParseTree matcherTree,
    										  		   final List<SequenceMatcher> sequenceList)
    										  		   throws ParseException {
    	switch (matcherTree.getParseTreeType()) {
    		case BYTE:           			addByteMatcher(                 	matcherTree, sequenceList); break;
    		case ANY:                     	addAnyMatcher(						matcherTree, sequenceList); break;
    		case ALL_BITMASK:   			addAllBitmaskMatcher(				matcherTree, sequenceList); break;
    		case ANY_BITMASK:   			addAnyBitmaskMatcher(				matcherTree, sequenceList); break;
    		case RANGE:       				addRangeMatcher(					matcherTree, sequenceList); break;
    		case CASE_SENSITIVE_STRING:   	addStringMatcher(					matcherTree, sequenceList); break;
    		case CASE_INSENSITIVE_STRING: 	addCaseInsensitiveStringMatcher(	matcherTree, sequenceList); break;
    		case SEQUENCE:          		addSequenceMatcher(					matcherTree, sequenceList); break;
    		case REPEAT:          			addRepeatedSequence(				matcherTree, sequenceList); break;
    		case SET: case ALTERNATIVES: 	addSetMatcher(						matcherTree, sequenceList); break;
    		
    		default: throw new ParseException(getTypeErrorMessage(matcherTree));
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
		return new StructuralNode(ParseTreeType.SEQUENCE, expressions, NOT_YET_INVERTED);
    }    
	
	
	private String getTypeErrorMessage(final ParseTree ast) {
		final ParseTreeType type = ast.getParseTreeType();
		return String.format("Unknown type, id %d with description: %s", 
		                       type, type.getDescription());
	}    	
	
    
    /*
    protected SequenceMatcher doCompileOld(final ParseTree ast) throws ParseException {

        SequenceMatcher matcher = null;

        switch (ast.getParseTreeType()) {

            // Deals with sequences of values, where a sequence node
            // has an ordered list of child nodes.
            // Processing is complicated by the need to optimise the
            // resulting sequences.  We could just build a ByteMatcherArrayMatcher
            // (consisting of an array of ByteMatchers), but this is not optimal
            // if, for instance, we just have simple list of bytes, for which a
            // ByteArrayMatcher would be more appropriate.

            case SEQUENCE: {

                final List<Byte> byteValuesToJoin = new ArrayList<Byte>();
                final List<ByteMatcher> singleByteSequence = new ArrayList<ByteMatcher>();
                final List<SequenceMatcher> sequences = new ArrayList<SequenceMatcher>();
                for (final ParseTree child : ast.getChildren()) {

                    switch (child.getParseTreeType()) {

                        // Bytes and case sensitive strings are just byte values,
                        // so we join them into a list of values as we go,
                        // building the final matcher when we run out of bytes
                        // or case sensitive strings to process.
                        case BYTE: {
                            addCollectedByteMatchers(singleByteSequence, sequences);
                            byteValuesToJoin.add(child.getByteValue());
                            break;
                        }


                        case CASE_SENSITIVE_STRING: {
                            addCollectedByteMatchers(singleByteSequence, sequences);
                            final String str = child.getTextValue();
                            for (int charIndex = 0, end = str.length(); charIndex < end; charIndex++) {
                                final byte byteValue = (byte) str.charAt(charIndex);
                                byteValuesToJoin.add(byteValue);
                            }
                            break;
                        }


                        // bitmasks, sets and any bytes are multiple-valued byte matchers:

                        case ALL_BITMASK: {
                            addCollectedByteValues(byteValuesToJoin, sequences);
                            singleByteSequence.add(getAllBitmaskMatcher(child));
                            break;
                        }

                        
                        case ANY_BITMASK: {
                            addCollectedByteValues(byteValuesToJoin, sequences);
                            singleByteSequence.add(getAnyBitmaskMatcher(child));
                            break;
                        }


                        case SET: {
                            final ByteMatcher bytematch = getSetMatcher(child, false);
                            if (bytematch.getNumberOfMatchingBytes() == 1) {
                                final byte[] matchingBytes = bytematch.getMatchingBytes();                            	
                                addCollectedByteMatchers(singleByteSequence, sequences);
                                byteValuesToJoin.add(matchingBytes[0]);
                            } else {
                                addCollectedByteValues(byteValuesToJoin, sequences);
                                singleByteSequence.add(bytematch);
                            }
                            break;
                        }


                        case INVERTED_SET: {
                            final ByteMatcher bytematch = getSetMatcher(child, true);
                            final byte[] matchingBytes = bytematch.getMatchingBytes();
                            if (matchingBytes.length == 1) {
                                addCollectedByteMatchers(singleByteSequence, sequences);
                                byteValuesToJoin.add(matchingBytes[0]);
                            } else {
                                addCollectedByteValues(byteValuesToJoin, sequences);
                                singleByteSequence.add(bytematch);
                            }
                            break;
                        }


                        case ANY: {
                            addCollectedByteValues(byteValuesToJoin, sequences);
                            singleByteSequence.add(AnyByteMatcher.ANY_BYTE_MATCHER);
                            break;
                        }


                        // case insensitive strings are already sequences of values:

                        case CASE_INSENSITIVE_STRING: {
                            // Add any bytes or singlebytematchers to the sequences.
                            // There cannot be both bytes and singlebytematchers
                            // outstanding to be collected, as they both ensure
                            // this as they are built.
                            // so the order of adding them here does not matter.
                            addCollectedByteValues(byteValuesToJoin, sequences);
                            addCollectedByteMatchers(singleByteSequence, sequences);
                            sequences.add(getCaseInsensitiveStringMatcher(child));
                            break;
                        }

                        //FIXME: don't test for instances of particular classes,
                        //       figure out what the criteria is in another way.
                        case REPEAT: {
                            SequenceMatcher sequence = getFixedRepeatMatcher(child);
                            // if the sequence consists entirely of single byte matches,
                            // just add them to the byte values to join.
                            if (sequence instanceof ByteArrayMatcher) {
                                addCollectedByteMatchers(singleByteSequence, sequences);
                                for (int position = 0; position < sequence.length(); position++) {
                                    final byte value = sequence.getMatcherForPosition(position).getMatchingBytes()[0];
                                    byteValuesToJoin.add(value);
                                }
                            } else if (sequence instanceof ByteMatcherArrayMatcher) {
                                addCollectedByteValues(byteValuesToJoin, sequences);
                                for (int position = 0; position < sequence.length(); position++) {
                                    final ByteMatcher aMatcher = sequence.getMatcherForPosition(position);
                                    singleByteSequence.add(aMatcher);
                                }
                            } else {
                                addCollectedByteValues(byteValuesToJoin, sequences);
                                addCollectedByteMatchers(singleByteSequence, sequences);
                                sequences.add(sequence);
                            }
                            break;
                        }

                        default: {
                          throwParseException(ast);
                        }
                    }
                }
                
                // Add any remaining bytes or byte matchers to the sequences.
                // There cannot be both bytes and byte matchers
                // outstanding to be collected, as they both ensure this as they are built,
                // so the order of adding them here does not matter.
                addCollectedByteValues(byteValuesToJoin, sequences);
                addCollectedByteMatchers(singleByteSequence, sequences);

                // If we only have a single sequence matcher, just return that
                // otherwise, build a sequence array matcher from our list of sequences.
                // of different sequence matchers:
                matcher = sequences.size() == 1
                        ? sequences.get(0)
                        : new SequenceArrayMatcher(sequences);
                break;
            }


            // Deal with sequences consisting of a single value,
            // where there is not a parent Sequence node.

            case BYTE: {
                matcher = AstCompilerUtils.createByteMatcher(ast, NOT_INVERTED);
                // new OneByteMatcher(ast.getByteValue());
                break;
            }


            case ALL_BITMASK: {
                matcher = AstCompilerUtils.createAllBitmaskMatcher(ast, NOT_INVERTED);
                	//getAllBitmaskMatcher(ast);
                break;
            }


            case ANY_BITMASK: {
                matcher = AstCompilerUtils.createAnyBitmaskMatcher(ast, NOT_INVERTED);
                	//getAnyBitmaskMatcher(ast);
                break;
            }


            case SET: {
                matcher = AstCompilerUtils.createMatcherFromSet(ast, NOT_INVERTED, matcherFactory);
                	//getSetMatcher(ast, false);
                break;
            }


            case ANY: {
                matcher = AstCompilerUtils.getAnyMatcher(ast, NOT_INVERTED);
                //createAnyByteMatcher.ANY_BYTE_MATCHER;
                break;
            }

            
            case CASE_SENSITIVE_STRING: {
                matcher = new ByteArrayMatcher(ast.getTextValue());
                break;
            }


            case CASE_INSENSITIVE_STRING: {
                matcher = new CaseInsensitiveSequenceMatcher(ast.getTextValue());
                break;
            }
            

            case REPEAT: {
                matcher = getFixedRepeatMatcher(ast);
                break;
            }
            
            
            default: {
                throwParseException(ast);
            }
        }
        return matcher;
    }


    private void addCollectedByteValues(final List<Byte> byteValuesToJoin, final List<SequenceMatcher> sequences) {
        if (byteValuesToJoin.size() > 0) {
            final ByteArrayMatcher byteMatcher = new ByteArrayMatcher(byteValuesToJoin);
            sequences.add(byteMatcher);
            byteValuesToJoin.clear();
        }
    }

    
    private void addCollectedByteMatchers(final List<ByteMatcher> matchers, final List<SequenceMatcher> sequences) {
        if (matchers.size() == 1) {
            sequences.add(matchers.get(0));
        } else if (matchers.size() > 0) {
            final ByteMatcherArrayMatcher matcher = new ByteMatcherArrayMatcher(matchers);
            sequences.add(matcher);
        }
        matchers.clear();
    }

    
    private SequenceMatcher getCaseInsensitiveStringMatcher(final ParseTree ast) throws ParseException {
        return new CaseInsensitiveSequenceMatcher(ast.getTextValue());
    }


    private ByteMatcher getAllBitmaskMatcher(final ParseTree ast) throws ParseException {
        return new AllBitmaskMatcher(ast.getByteValue());
    }

    
    private ByteMatcher getAnyBitmaskMatcher(final ParseTree ast) throws ParseException {
        return new AnyBitmaskMatcher(ast.getByteValue());
    }


    private ByteMatcher getSetMatcher(final ParseTree ast, final boolean inverted) throws ParseException {
        //TODO: do we need inverted now the inverted set is already inverting the bytes it matches?
        return matcherFactory.create(ast.getByteSetValue(), inverted);
    }

    
    private SequenceMatcher getFixedRepeatMatcher(final ParseTree ast) throws ParseException {
        final List<ParseTree> repeatChildren = ast.getChildren();
        final int minRepeat = repeatChildren.get(0).getIntValue();
        final int maxRepeat = repeatChildren.get(1).getIntValue();
        if (minRepeat == maxRepeat) {
            final ParseTree repeatedNode = repeatChildren.get(2);
            SequenceMatcher matcher = null;
            switch (repeatedNode.getParseTreeType()) {

                case ANY: {
                    matcher = maxRepeat == 1? AnyByteMatcher.ANY_BYTE_MATCHER
                                            : new FixedGapMatcher(maxRepeat);
                    break;
                }


                case BYTE: {
                    matcher = maxRepeat == 1? new OneByteMatcher(repeatedNode.getByteValue()) 
                                            : new ByteArrayMatcher(repeatedNode.getByteValue(), maxRepeat);
                    break;
                }


                case SET: {
                    final ByteMatcher set = getSetMatcher(repeatedNode, false);
                    matcher = maxRepeat == 1? set : new ByteMatcherArrayMatcher(set, maxRepeat);
                    break;
                }


                case INVERTED_SET: {
                    final ByteMatcher set = getSetMatcher(repeatedNode, true);
                    matcher = maxRepeat == 1? set : new ByteMatcherArrayMatcher(set, maxRepeat);
                    break;
                }


                case ANY_BITMASK: {
                    final ByteMatcher anyB = getAnyBitmaskMatcher(repeatedNode);
                    matcher = maxRepeat == 1? anyB : new ByteMatcherArrayMatcher(anyB, maxRepeat);
                    break;
                }

                
                case ALL_BITMASK: {
                    final ByteMatcher allB = getAllBitmaskMatcher(repeatedNode);
                    matcher = maxRepeat == 1? allB : new ByteMatcherArrayMatcher(allB, maxRepeat);                  
                    break;
                }

                case CASE_SENSITIVE_STRING: {
                    matcher = new ByteArrayMatcher(repeatString(repeatedNode.getTextValue(), maxRepeat));
                    break;
                }


                case CASE_INSENSITIVE_STRING: {
                    matcher = new CaseInsensitiveSequenceMatcher(repeatedNode.getTextValue(), maxRepeat);
                    break;
                }


                case SEQUENCE: {
                    matcher = maxRepeat == 1? doCompile(repeatedNode)
                                            : doCompile(repeatedNode).repeat(maxRepeat);
                    break;
                }

                default: {
                    throwParseException(repeatedNode);
                }
            }
            return matcher;
        }
        throw new ParseException("Sequences can only contain repeats of a fixed length {n}");
    }

    private String repeatString(final String stringToRepeat, final int numberToRepeat) {
        if (numberToRepeat == 1) {
            return stringToRepeat;
        }
        final StringBuilder builder = new StringBuilder(stringToRepeat.length() * numberToRepeat);
        for (int count = 0; count < numberToRepeat; count++) {
            builder.append(stringToRepeat);
        }
        return builder.toString();
    }        

    
    /**
     * @param ast
     * @throws ParseException 
     */
    /*
    private void throwParseException(ParseTree ast) throws ParseException {
      final ParseTreeType type = ast.getParseTreeType();
      final String message = String.format("Unknown type, id %d with description: %s", 
                         type, type.getDescription());
      throw new ParseException(message);
    }
    */

    
    
}
