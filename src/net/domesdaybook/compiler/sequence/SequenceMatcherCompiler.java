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

import net.domesdaybook.compiler.AbstractCompiler;
import net.domesdaybook.compiler.CompileException;
import net.domesdaybook.matcher.bytes.AllBitmaskMatcher;
import net.domesdaybook.matcher.bytes.AnyBitmaskMatcher;
import net.domesdaybook.matcher.bytes.AnyByteMatcher;
import net.domesdaybook.matcher.bytes.ByteMatcher;
import net.domesdaybook.matcher.bytes.ByteMatcherFactory;
import net.domesdaybook.matcher.bytes.OneByteMatcher;
import net.domesdaybook.matcher.bytes.SimpleByteMatcherFactory;
import net.domesdaybook.matcher.sequence.ByteArrayMatcher;
import net.domesdaybook.matcher.sequence.ByteMatcherArrayMatcher;
import net.domesdaybook.matcher.sequence.CaseInsensitiveSequenceMatcher;
import net.domesdaybook.matcher.sequence.FixedGapMatcher;
import net.domesdaybook.matcher.sequence.SequenceArrayMatcher;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.parser.ParseException;
import net.domesdaybook.parser.ParseTree;
import net.domesdaybook.parser.ParseTreeType;

/**
 * A compiler which produces a {@link SequenceMatcher} from an
 * abstract syntax tree provided by the {@link AbstractCompiler} class,
 * which it extends.
 * <p>
 * It can handle nearly all the syntax processable by the {@link net.domesdaybook.parser.regex.RegexParser},
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
 * @see AbstractCompiler
 * @see SequenceMatcher
 */
public final class SequenceMatcherCompiler extends AbstractCompiler<SequenceMatcher> {

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
     * Compiles an abstract syntax tree provided by the {@link AbstractCompiler} class
     * which it extends, to create a {@link SequenceMatcher} object.
     *
     * @param ast The abstract syntax tree provided by the {@link AbstractCompiler} class.
     * @return A {@link SequenceMatcher} which matches the expression defined by the ast passed in.
     */
    @Override
    public SequenceMatcher compile(final ParseTree ast) throws CompileException {
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
        return new SequenceArrayMatcher(matchers);
    }    


    /**
     * Performs the actual compilation of a sequence from an abstract syntax tree.
     *
     * @param ast The abstract syntax tree to compile.
     * @return A SequenceMatcher representing the expression.
     * @throws ParseException If the ast could not be parsed.
     */
    private SequenceMatcher buildSequence(final ParseTree ast) throws ParseException {

        SequenceMatcher matcher = null;

        switch (ast.getParseTreeType().getId()) {

            // Deals with sequences of values, where a sequence node
            // has an ordered list of child nodes.
            // Processing is complicated by the need to optimise the
            // resulting sequences.  We could just build a ByteMatcherArrayMatcher
            // (consisting of an array of ByteMatchers), but this is not optimal
            // if, for instance, we just have simple list of bytes, for which a
            // ByteArrayMatcher would be more appropriate.

            case (ParseTreeType.SEQUENCE_ID): {

                final List<Byte> byteValuesToJoin = new ArrayList<Byte>();
                final List<ByteMatcher> singleByteSequence = new ArrayList<ByteMatcher>();
                final List<SequenceMatcher> sequences = new ArrayList<SequenceMatcher>();
                for (final ParseTree child : ast.getChildren()) {

                    switch (child.getParseTreeType().getId()) {

                        // Bytes and case sensitive strings are just byte values,
                        // so we join them into a list of values as we go,
                        // building the final matcher when we run out of bytes
                        // or case sensitive strings to process.
                        case (ParseTreeType.BYTE_ID): {
                            addCollectedSingleByteMatchers(singleByteSequence, sequences);
                            byteValuesToJoin.add(child.getByteValue());
                            break;
                        }


                        case (ParseTreeType.CASE_SENSITIVE_STRING_ID): {
                            addCollectedSingleByteMatchers(singleByteSequence, sequences);
                            final String str = child.getTextValue();
                            for (int charIndex = 0, end = str.length(); charIndex < end; charIndex++) {
                                final byte byteValue = (byte) str.charAt(charIndex);
                                byteValuesToJoin.add(byteValue);
                            }
                            break;
                        }


                        // bitmasks, sets and any bytes are multiple-valued single byte matchers:

                        case (ParseTreeType.ALL_BITMASK_ID): {
                            addCollectedByteValues(byteValuesToJoin, sequences);
                            singleByteSequence.add(getAllBitmaskMatcher(child));
                            break;
                        }

                        
                        case (ParseTreeType.ANY_BITMASK_ID): {
                            addCollectedByteValues(byteValuesToJoin, sequences);
                            singleByteSequence.add(getAnyBitmaskMatcher(child));
                            break;
                        }


                        case (ParseTreeType.SET_ID): {
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


                        case (ParseTreeType.INVERTED_SET_ID): {
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


                        case (ParseTreeType.ANY_ID): {
                            addCollectedByteValues(byteValuesToJoin, sequences);
                            singleByteSequence.add(AnyByteMatcher.ANY_BYTE_MATCHER);
                            break;
                        }


                        // case insensitive strings are already sequences of values:

                        case (ParseTreeType.CASE_INSENSITIVE_STRING_ID): {
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

                        case (ParseTreeType.REPEAT_ID): {
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
                          throwParseException(ast);
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
                        : new SequenceArrayMatcher(sequences);
                break;
            }


            // Deal with sequences consisting of a single value,
            // where there is not a parent Sequence node.

            case (ParseTreeType.BYTE_ID): {
                matcher = new OneByteMatcher(ast.getByteValue());
                break;
            }


            case (ParseTreeType.ALL_BITMASK_ID): {
                matcher = getAllBitmaskMatcher(ast);
                break;
            }


            case (ParseTreeType.ANY_BITMASK_ID): {
                matcher = getAnyBitmaskMatcher(ast);
                break;
            }


            case (ParseTreeType.SET_ID): {
                matcher = getSetMatcher(ast, false);
                break;
            }


            case (ParseTreeType.INVERTED_SET_ID): {
                matcher = getSetMatcher(ast, true);
                break;
            }


            case (ParseTreeType.ANY_ID): {
                matcher = AnyByteMatcher.ANY_BYTE_MATCHER;
                break;
            }

            
            case (ParseTreeType.REPEAT_ID): {
                matcher = getFixedRepeatMatcher(ast);
                break;
            }


            case (ParseTreeType.CASE_SENSITIVE_STRING_ID): {
                matcher = new ByteArrayMatcher(ast.getTextValue());
                break;
            }


            case (ParseTreeType.CASE_INSENSITIVE_STRING_ID): {
                matcher = new CaseInsensitiveSequenceMatcher(ast.getTextValue());
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

    
    private void addCollectedSingleByteMatchers(final List<ByteMatcher> matchers, final List<SequenceMatcher> sequences) {
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
        int minRepeat = repeatChildren.get(0).getIntValue();
        int maxRepeat = repeatChildren.get(1).getIntValue();
        if (minRepeat == maxRepeat) {
            ParseTree repeatedNode = repeatChildren.get(2);
            SequenceMatcher matcher = null;
            switch (repeatedNode.getParseTreeType().getId()) {

                case (ParseTreeType.ANY_ID): {
                    matcher = new FixedGapMatcher(maxRepeat);
                    break;
                }


                case (ParseTreeType.BYTE_ID): {
                    matcher = new ByteArrayMatcher(repeatedNode.getByteValue(), maxRepeat);
                    break;
                }


                case (ParseTreeType.SET_ID): {
                    matcher = new ByteMatcherArrayMatcher(getSetMatcher(repeatedNode, false), maxRepeat);
                    break;
                }


                case (ParseTreeType.INVERTED_SET_ID): {
                    matcher = new ByteMatcherArrayMatcher(getSetMatcher(repeatedNode, true), maxRepeat);
                    break;
                }


                case (ParseTreeType.ANY_BITMASK_ID): {
                    matcher = new ByteMatcherArrayMatcher(getAnyBitmaskMatcher(repeatedNode), maxRepeat);
                    break;
                }

                
                case (ParseTreeType.ALL_BITMASK_ID): {
                    matcher = new ByteMatcherArrayMatcher(getAllBitmaskMatcher(repeatedNode), maxRepeat);
                    break;
                }

                case (ParseTreeType.CASE_SENSITIVE_STRING_ID): {
                    matcher = new ByteArrayMatcher(repeatString(repeatedNode.getTextValue(), maxRepeat));
                    break;
                }


                case (ParseTreeType.CASE_INSENSITIVE_STRING_ID): {
                    matcher = new CaseInsensitiveSequenceMatcher(repeatedNode.getTextValue(), maxRepeat);
                    break;
                }


                case (ParseTreeType.SEQUENCE_ID): {
                    matcher = buildSequence(repeatedNode).repeat(maxRepeat);
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
    private void throwParseException(ParseTree ast) throws ParseException {
      final ParseTreeType type = ast.getParseTreeType();
      final String message = String.format("Unknown type, id %d with description: %s", 
                         type.getId(), type.getDescription());
      throw new ParseException(message);
    }

    
    
}
