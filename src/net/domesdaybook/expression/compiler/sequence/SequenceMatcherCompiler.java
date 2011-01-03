/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.expression.compiler.sequence;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.domesdaybook.expression.compiler.AstCompiler;
import net.domesdaybook.expression.parser.ParseException;
import net.domesdaybook.expression.parser.ParseUtils;
import net.domesdaybook.expression.parser.regularExpressionParser;
import net.domesdaybook.matcher.sequence.ByteSequenceMatcher;
import net.domesdaybook.matcher.sequence.CaseInsensitiveStringMatcher;
import net.domesdaybook.matcher.sequence.CaseSensitiveStringMatcher;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.matcher.sequence.CombinedSequenceMatcher;
import net.domesdaybook.matcher.sequence.SingleByteSequenceMatcher;
import net.domesdaybook.matcher.singlebyte.AllBitMaskMatcher;
import net.domesdaybook.matcher.singlebyte.AnyBitMaskMatcher;
import net.domesdaybook.matcher.singlebyte.AnyByteMatcher;
import net.domesdaybook.matcher.singlebyte.ByteSetMatcher;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import org.antlr.runtime.tree.CommonTree;

/**
 *
 * @author Matt Palmer
 */
public class SequenceMatcherCompiler extends AstCompiler<SequenceMatcher> {

    
    @Override
    public final SequenceMatcher compile(final CommonTree ast) throws ParseException {
        if (ast == null) {
            throw new IllegalArgumentException("Null abstract syntax tree passed in to SequenceMatcherCompiler.");
        }
        return buildSequence(ast);
    }


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

                List<Byte> byteValuesToJoin = new ArrayList<Byte>();
                List<SingleByteMatcher> singleByteSequence = new ArrayList<SingleByteMatcher>();
                List<SequenceMatcher> sequences = new ArrayList<SequenceMatcher>();

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
                            final String str = ParseUtils.trimString(child.getText());
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
                            addCollectedByteValues(byteValuesToJoin, sequences);
                            singleByteSequence.add(getSetMatcher(child, false));
                            break;
                        }


                        case (regularExpressionParser.INVERTED_SET): {
                            addCollectedByteValues(byteValuesToJoin, sequences);
                            singleByteSequence.add(getSetMatcher(child, true));
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
                            // Add the case insensitive matcher:
                            sequences.add(getCaseInsensitiveStringMatcher(child));
                            break;
                        }

                        default: {
                            throw new ParseException(ParseUtils.getTypeErrorMessage(ast));
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
                            : new CombinedSequenceMatcher(sequences);
                }
                break;
            }


            // Deal with sequences consisting of a single value,
            // where there is not a parent Sequence node.

            case (regularExpressionParser.BYTE): {
                matcher = new ByteSequenceMatcher(ParseUtils.getHexByteValue(ast));
                break;
            }


            case (regularExpressionParser.ALL_BITMASK): {
                matcher = new SingleByteSequenceMatcher(getAllBitmaskMatcher(ast));
                break;
            }


            case (regularExpressionParser.ANY_BITMASK): {
                matcher = new SingleByteSequenceMatcher(getAnyBitmaskMatcher(ast));
                break;
            }


            case (regularExpressionParser.SET): {
                matcher = new SingleByteSequenceMatcher(getSetMatcher(ast, false));
                break;
            }


            case (regularExpressionParser.INVERTED_SET): {
                matcher = new SingleByteSequenceMatcher(getSetMatcher(ast, true));
                break;
            }


            case (regularExpressionParser.ANY): {
                matcher = new SingleByteSequenceMatcher(getAnyByteMatcher(ast));
                break;
            }


            case (regularExpressionParser.CASE_SENSITIVE_STRING): {
                final String str = ParseUtils.trimString(ast.getText());
                matcher = new CaseSensitiveStringMatcher(str);
                break;
            }


            case (regularExpressionParser.CASE_INSENSITIVE_STRING): {
                matcher = getCaseInsensitiveStringMatcher(ast);
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
            final ByteSequenceMatcher byteMatcher = new ByteSequenceMatcher(byteValuesToJoin);
            sequences.add(byteMatcher);
            byteValuesToJoin.clear();
        }
    }

    private void addCollectedSingleByteMatchers(final List<SingleByteMatcher> matchers, final List<SequenceMatcher> sequences) {
        if (matchers.size() > 0) {
            final SingleByteSequenceMatcher matcher = new SingleByteSequenceMatcher(matchers);
            sequences.add(matcher);
            matchers.clear();
        }
    }

    private SequenceMatcher getCaseInsensitiveStringMatcher(final CommonTree ast) {
        final String str = ParseUtils.trimString(ast.getText());
        return new CaseInsensitiveStringMatcher(str);
    }


    private SingleByteMatcher getAllBitmaskMatcher(final CommonTree ast) {
        final byte bitmask = ParseUtils.getBitMaskValue(ast);
        return new AllBitMaskMatcher(bitmask);
    }

    private SingleByteMatcher getAnyBitmaskMatcher(final CommonTree ast) {
        final byte bitmask = ParseUtils.getBitMaskValue(ast);
        return new AnyBitMaskMatcher(bitmask);
    }


    private SingleByteMatcher getSetMatcher(final CommonTree ast, final boolean negated) throws ParseException {
        final Set<Byte> byteSet = ParseUtils.calculateSetValue(ast);
        return ByteSetMatcher.buildMatcher(byteSet, negated);
    }

    
    private SingleByteMatcher getAnyByteMatcher(final CommonTree ast) {
        return new AnyByteMatcher();
    }



}
