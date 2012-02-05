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

package net.domesdaybook.compiler.sequence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.domesdaybook.compiler.AbstractAstCompiler;
import net.domesdaybook.compiler.CompileException;
import net.domesdaybook.parser.ParseException;
import net.domesdaybook.parser.ParseUtils;
import net.domesdaybook.parser.regularExpressionParser;
import net.domesdaybook.matcher.sequence.ByteArrayMatcher;
import net.domesdaybook.matcher.sequence.CaseInsensitiveSequenceMatcher;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.matcher.sequence.SequenceMatcherArrayMatcher;
import net.domesdaybook.matcher.sequence.FixedGapMatcher;
import net.domesdaybook.matcher.sequence.ByteMatcherArrayMatcher;
import net.domesdaybook.matcher.bytes.AllBitmaskMatcher;
import net.domesdaybook.matcher.bytes.AnyBitmaskMatcher;
import net.domesdaybook.matcher.bytes.AnyByteMatcher;
import net.domesdaybook.matcher.bytes.OneByteMatcher;
import net.domesdaybook.matcher.bytes.SimpleByteMatcherFactory;
import net.domesdaybook.matcher.bytes.ByteMatcher;
import net.domesdaybook.matcher.bytes.ByteMatcherFactory;
import org.antlr.runtime.tree.CommonTree;

/**
 * A compiler which produces a {@link SequenceMatcher} from an
 * abstract syntax tree provided by the {@link AbstractAstCompiler} class,
 * which it extends.
 * <p>
 * It can handle nearly all the syntax processable by the {@link AstParser},
 * but it cannot handle any syntax which would give variable lengths to
 * match, or which would have alternative sequences of bytes,
 * as a sequence matcher can only match a single defined sequence.
 * <p>
 * In general, this means that it *cannot* handle alternatives (X|Y|Z),
 * optionality X?, variable length repeats {n-m}, 
 * and the wildcard repeats * and +.
 * <p>
 * It can handle fixed length repeats {n}.  Also, alternative sequences
 * (X|Y|Z) where each alternative is one byte long can be handled, but only
 * because they are pre-optimised by the AbstractAstCompiler class into a [set] 
 * of bytes instead of a list of alternatives, before this compiler even sees them.
 * Therefore, this may not be relied upon, as it is an artefact of an earlier
 * stage of optimisation which may or may not hold true in the future. This
 * compiler, in principle, cannot handle alternative sequences if they are
 * provided to it.
 * 
 * @author Matt Palmer
 * @see AbstractAstCompiler
 * @see SequenceMatcher
 */
public final class SequenceMatcherCompiler extends AbstractAstCompiler<SequenceMatcher> {

    private static SequenceMatcherCompiler defaultCompiler;
    
    
    /**
     * Compiles a SequenceMatcher from a byteSeek regular expression (limited to
     * syntax which produces fixed-length sequences).  It will use the default
     * {@link SimpleByteMatcherFactory} to produce matchers for sets of bytes.
     * 
     * @param expression The regular expression to compile
     * @return SequenceMatcher a SequenceMatcher matching the regular expression.
     * @throws CompileException If the expression could not be compiled into a SequenceMatcher.
     */
    public static SequenceMatcher sequenceMatcherFrom(final String expression) throws CompileException {
        defaultCompiler = new SequenceMatcherCompiler();
        return defaultCompiler.compile(expression);
    }
    
    
    /**
     * Returns a SequenceMatcher matching the array of bytes.
     * 
     * @param bytes The bytes to produce a SequenceMatcher from.
     * @return SequenceMatcher a SequenceMatcher matching the bytes.
     */
    public static SequenceMatcher sequenceMatcherFrom(final byte[] bytes) {
        return new ByteArrayMatcher(bytes);
    }
    
    
    private final ByteMatcherFactory matcherFactory;

    
    /**
     * Default constructor which uses the {@link SimpleByteMatcherFactory}
     * to produce matchers for sets of bytes.
     * 
     */
    public SequenceMatcherCompiler() {
        matcherFactory = new SimpleByteMatcherFactory();
    }

    /**
     * Constructor which uses the provided {@link ByteMatcherFactory} to
     * produce matchers for sets of bytes
     * 
     * @param factoryToUse The ByteMatcherFactory used to produce matchers
     * for sets of bytes.
     */
    public SequenceMatcherCompiler(final ByteMatcherFactory factoryToUse) {
        matcherFactory = factoryToUse;
    }


    /**
     * Compiles an abstract syntax tree provided by the {@link AbstractAstCompiler} class
     * which it extends, to create a {@SequenceMatcher} object.
     *
     * @param ast The abstract syntax tree provided by the {@link AbstractAstCompiler} class.
     * @return A {@link SequenceMatcher} which matches the expression defined by the ast passed in.
     */
    @Override
    public SequenceMatcher compile(final CommonTree ast) throws CompileException {
        if (ast == null) {
            throw new CompileException("Null abstract syntax tree passed in to SequenceMatcherCompiler.");
        }
        try {
            return buildSequence(ast);
        } catch (IllegalArgumentException e) {
            throw new CompileException(e);
        } catch (ParseException ex) {
            throw new CompileException(ex);
        }
    }
    
    
    @Override
    public SequenceMatcher compile(Collection<String> expressions) throws CompileException {
        final List<SequenceMatcher> matchers = new ArrayList<SequenceMatcher>();
        for (final String expression : expressions) {
            matchers.add(compile(expression));
        }
        return new SequenceMatcherArrayMatcher(matchers);
    }    


    /**
     * Performs the actual compilation of a sequence from an abstract syntax tree.
     *
     * @param ast The abstract syntax tree to compile.
     * @return A SequenceMatcher representing the expression.
     * @throws ParseException If the ast could not be parsed.
     */
    private SequenceMatcher buildSequence(final CommonTree ast) throws ParseException {

        SequenceMatcher matcher = null;

        switch (ast.getToken().getType()) {

            // Deals with sequences of values, where a sequence node
            // has an ordered list of child nodes.
            // Processing is complicated by the need to optimise the
            // resulting sequences.  We could just build a singlebytesequencematcher
            // (consisting of a list of singleByteMatchers), but this is not optimal
            // if, for instance, we just have simple list of bytes, for which a
            // bytesequencematcher would be more appropriate.

            case (regularExpressionParser.SEQUENCE): {

                final List<Byte> byteValuesToJoin = new ArrayList<Byte>();
                final List<ByteMatcher> singleByteSequence = new ArrayList<ByteMatcher>();
                final List<SequenceMatcher> sequences = new ArrayList<SequenceMatcher>();

                for (int childIndex = 0, stop = ast.getChildCount(); childIndex < stop; childIndex++) {
                    final CommonTree child = (CommonTree) ast.getChild(childIndex);

                    switch (child.getToken().getType()) {

                        // Bytes and case sensitive strings are just byte values,
                        // so we join them into a list of values as we go,
                        // building the final matcher when we run out of bytes
                        // or case sensitive strings to process.
                        case (regularExpressionParser.BYTE): {
                            addCollectedSingleByteMatchers(singleByteSequence, sequences);
                            byteValuesToJoin.add(ParseUtils.getHexByteValue(child));
                            break;
                        }


                        case (regularExpressionParser.CASE_SENSITIVE_STRING): {
                            addCollectedSingleByteMatchers(singleByteSequence, sequences);
                            final String str = ParseUtils.unquoteString(child.getText());
                            for (int charIndex = 0, end = str.length(); charIndex < end; charIndex++) {
                                final byte byteValue = (byte) str.charAt(charIndex);
                                byteValuesToJoin.add((byte) byteValue);
                            }
                            break;
                        }


                        // bitmasks, sets and any bytes are multiple-valued single byte matchers:

                        case (regularExpressionParser.ALL_BITMASK): {
                            addCollectedByteValues(byteValuesToJoin, sequences);
                            singleByteSequence.add(getAllBitmaskMatcher(child));
                            break;
                        }

                        
                        case (regularExpressionParser.ANY_BITMASK): {
                            addCollectedByteValues(byteValuesToJoin, sequences);
                            singleByteSequence.add(getAnyBitmaskMatcher(child));
                            break;
                        }


                        case (regularExpressionParser.SET): {
                            final ByteMatcher bytematch = getSetMatcher(child, false);
                            if (bytematch instanceof OneByteMatcher) {
                                addCollectedSingleByteMatchers(singleByteSequence, sequences);
                                byteValuesToJoin.add(bytematch.getMatchingBytes()[0]);
                            } else {
                                addCollectedByteValues(byteValuesToJoin, sequences);
                                singleByteSequence.add(bytematch);
                            }
                            break;
                        }


                        case (regularExpressionParser.INVERTED_SET): {
                            final ByteMatcher bytematch = getSetMatcher(child, true);
                            if (bytematch instanceof OneByteMatcher) {
                                addCollectedSingleByteMatchers(singleByteSequence, sequences);
                                byteValuesToJoin.add(bytematch.getMatchingBytes()[0]);
                            } else {
                                addCollectedByteValues(byteValuesToJoin, sequences);
                                singleByteSequence.add(bytematch);
                            }
                            break;
                        }


                        case (regularExpressionParser.ANY): {
                            addCollectedByteValues(byteValuesToJoin, sequences);
                            singleByteSequence.add(getAnyByteMatcher(child));
                            break;
                        }


                        // case insensitive strings are already sequences of values:

                        case (regularExpressionParser.CASE_INSENSITIVE_STRING): {
                            // Add any bytes or singlebytematchers to the sequences.
                            // There cannot be both bytes and singlebytematchers
                            // outstanding to be collected, as they both ensure
                            // this as they are built.
                            // so the order of adding them here does not matter.
                            addCollectedByteValues(byteValuesToJoin, sequences);
                            addCollectedSingleByteMatchers(singleByteSequence, sequences);
                            sequences.add(getCaseInsensitiveStringMatcher(child));
                            break;
                        }

                        case (regularExpressionParser.REPEAT): {
                            SequenceMatcher sequence = getFixedRepeatMatcher(child);
                            if (sequence instanceof ByteArrayMatcher) {
                                addCollectedSingleByteMatchers(singleByteSequence, sequences);
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
                                addCollectedSingleByteMatchers(singleByteSequence, sequences);
                                sequences.add(getFixedRepeatMatcher(child));
                            }
                            break;
                        }

                        default: {
                            throw new ParseException(ParseUtils.getTypeErrorMessage(ast));
                        }
                    }

                }
                // Add any remaining bytes or singlebytematchers to the sequences.
                // There cannot be both bytes and singlebytematchers
                // outstanding to be collected, as they both ensure this as they are built,
                // so the order of adding them here does not matter.
                addCollectedByteValues(byteValuesToJoin, sequences);
                addCollectedSingleByteMatchers(singleByteSequence, sequences);

                // If we only have a single sequence matcher, just return that
                // otherwise, build a combined sequence matcher from our list
                // of different sequence matchers:
                matcher = sequences.size() == 1
                        ? sequences.get(0)
                        : new SequenceMatcherArrayMatcher(sequences);
                break;
            }


            // Deal with sequences consisting of a single value,
            // where there is not a parent Sequence node.

            case (regularExpressionParser.BYTE): {
                matcher = new OneByteMatcher(ParseUtils.getHexByteValue(ast));
                break;
            }


            case (regularExpressionParser.ALL_BITMASK): {
                matcher = getAllBitmaskMatcher(ast);
                break;
            }


            case (regularExpressionParser.ANY_BITMASK): {
                matcher = getAnyBitmaskMatcher(ast);
                break;
            }


            case (regularExpressionParser.SET): {
                matcher = getSetMatcher(ast, false);
                break;
            }


            case (regularExpressionParser.INVERTED_SET): {
                matcher = getSetMatcher(ast, true);
                break;
            }


            case (regularExpressionParser.ANY): {
                matcher = getAnyByteMatcher(ast);
                break;
            }

            
            case (regularExpressionParser.REPEAT): {
                matcher = getFixedRepeatMatcher(ast);
                break;
            }


            case (regularExpressionParser.CASE_SENSITIVE_STRING): {
                final String str = ParseUtils.unquoteString(ast.getText());
                matcher = new ByteArrayMatcher(str);
                break;
            }


            case (regularExpressionParser.CASE_INSENSITIVE_STRING): {
                final String str = ParseUtils.unquoteString(ast.getText());
                matcher = new CaseInsensitiveSequenceMatcher(str);
                break;
            }

            default: {
                throw new ParseException(ParseUtils.getTypeErrorMessage(ast));
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

    
    private void addCollectedSingleByteMatchers(final List<ByteMatcher> matchers, final List<SequenceMatcher> sequences) {
        if (matchers.size() == 1) {
            sequences.add(matchers.get(0));
        } else if (matchers.size() > 0) {
            final ByteMatcherArrayMatcher matcher = new ByteMatcherArrayMatcher(matchers);
            sequences.add(matcher);
        }
        matchers.clear();
    }

    
    private SequenceMatcher getCaseInsensitiveStringMatcher(final CommonTree ast) {
        final String str = ParseUtils.unquoteString(ast.getText());
        return new CaseInsensitiveSequenceMatcher(str);
    }


    private ByteMatcher getAllBitmaskMatcher(final CommonTree ast) {
        final byte bitmask = ParseUtils.getBitMaskValue(ast);
        return new AllBitmaskMatcher(bitmask);
    }

    
    private ByteMatcher getAnyBitmaskMatcher(final CommonTree ast) {
        final byte bitmask = ParseUtils.getBitMaskValue(ast);
        return new AnyBitmaskMatcher(bitmask);
    }


    private ByteMatcher getSetMatcher(final CommonTree ast, final boolean inverted) throws ParseException {
        final Set<Byte> byteSet = ParseUtils.calculateSetValue(ast);
        return matcherFactory.create(byteSet, inverted);
    }

    
    private ByteMatcher getAnyByteMatcher(final CommonTree ast) {
        return new AnyByteMatcher();
    }


    private SequenceMatcher getFixedRepeatMatcher(final CommonTree ast) throws ParseException {
        int minRepeat = ParseUtils.getMinRepeatValue(ast);
        int maxRepeat = ParseUtils.getMaxRepeatValue(ast);
        if (minRepeat == maxRepeat) {
            CommonTree repeatedNode = (CommonTree) ParseUtils.getRepeatNode(ast);
            SequenceMatcher matcher = null;
            switch (repeatedNode.getType()) {

                case (regularExpressionParser.ANY): {
                    matcher = new FixedGapMatcher(maxRepeat);
                    break;
                }


                case (regularExpressionParser.BYTE): {
                    matcher = new ByteArrayMatcher(ParseUtils.getHexByteValue(repeatedNode), maxRepeat);
                    break;
                }


                case (regularExpressionParser.SET): {
                    matcher = new ByteMatcherArrayMatcher(getSetMatcher(repeatedNode, false), maxRepeat);
                    break;
                }


                case (regularExpressionParser.INVERTED_SET): {
                    matcher = new ByteMatcherArrayMatcher(getSetMatcher(repeatedNode, true), maxRepeat);
                    break;
                }


                case (regularExpressionParser.ANY_BITMASK): {
                    matcher = new ByteMatcherArrayMatcher(getAnyBitmaskMatcher(repeatedNode), maxRepeat);
                    break;
                }

                
                case (regularExpressionParser.ALL_BITMASK): {
                    matcher = new ByteMatcherArrayMatcher(getAllBitmaskMatcher(repeatedNode), maxRepeat);
                    break;
                }

                case (regularExpressionParser.CASE_SENSITIVE_STRING): {
                    final String str = ParseUtils.unquoteString(repeatedNode.getText());
                    matcher = new ByteArrayMatcher(repeatString(str, maxRepeat));
                    break;
                }


                case (regularExpressionParser.CASE_INSENSITIVE_STRING): {
                    final String str = ParseUtils.unquoteString(repeatedNode.getText());
                    matcher = new CaseInsensitiveSequenceMatcher(str, maxRepeat);
                    break;
                }


                case (regularExpressionParser.SEQUENCE): {
                    matcher = buildSequence(repeatedNode).repeat(maxRepeat);
                    break;
                }

                default: {
                    throw new ParseException(ParseUtils.getTypeErrorMessage(repeatedNode));
                }

            }
            return matcher;
        } else {
            throw new ParseException("Sequences can only contain repeats of a fixed length {n}");
        }
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

}
