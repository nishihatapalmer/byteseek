/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.compiler.singlebyte;

import java.util.Set;
import net.domesdaybook.compiler.AbstractAstCompiler;
import net.domesdaybook.compiler.CompileException;
import net.domesdaybook.parser.ParseException;
import net.domesdaybook.parser.ParseUtils;
import net.domesdaybook.parser.regularExpressionParser;
import net.domesdaybook.matcher.singlebyte.BitMaskAllBitsMatcher;
import net.domesdaybook.matcher.singlebyte.BitMaskAnyBitsMatcher;
import net.domesdaybook.matcher.singlebyte.AnyMatcher;
import net.domesdaybook.matcher.singlebyte.ByteMatcher;
import net.domesdaybook.matcher.singlebyte.CaseInsensitiveByteMatcher;
import net.domesdaybook.matcher.singlebyte.SimpleSingleByteMatcherFactory;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcherFactory;
import org.antlr.runtime.tree.CommonTree;

/**
 * A compiler which produces a {@link SingleByteMatcher} from an
 * abstract syntax tree provided by the {@link AbstractAstCompiler} class,
 * which it extends.
 *
 * It can only handle syntax which would result in a single byte being
 * matched.  This means hex bytes, any byte (.), all bitmasks (&),
 * any bitmasks (~),single-character case sensitive and insensitive
 * strings, and sets of bytes [].
 *
 * It can handle alternative sequences (X|Y|Z) where each alternative
 * is one byte long, but only because they are pre-optimised by the
 * AbstractAstCompiler class into a [set] of bytes instead of a list of alternatives,
 * before this compiler even sees them.  Any alternative sequences provided
 * directly to this compiler will result in a CompileException.
 * 
 * @author matt
 */
public final class SingleByteCompiler extends AbstractAstCompiler<SingleByteMatcher> {

    private static SingleByteCompiler defaultCompiler;
    public SingleByteMatcher matcherFrom(String expression) throws CompileException {
        defaultCompiler = new SingleByteCompiler();
        return defaultCompiler.compile(expression);
    }
    
    private final SingleByteMatcherFactory matcherFactory;

    public SingleByteCompiler() {
        matcherFactory = new SimpleSingleByteMatcherFactory();
    }
    
    public SingleByteCompiler(SingleByteMatcherFactory factoryToUse) {
        matcherFactory = factoryToUse;
    }

    /**
     * Compiles an abstract syntax tree provided by the {@link AbstractAstCompiler} class
     * which it extends, to create a {@SingleByteMatcher} object.
     *
     * @param ast The abstract syntax tree provided by the {@link AbstractAstCompiler} class.
     * @return A {@link SingleByteMatcher} which matches the expression defined by the ast passed in.
     * @throws ParseException If the ast could not be parsed.
     */
    @Override
    public SingleByteMatcher compile(CommonTree ast) throws CompileException {
        if (ast == null) {
            throw new CompileException("Null abstract syntax tree passed in to SingleByteCompiler.");
        }
        try {
            return buildSingleByte(ast);
        } catch (IllegalArgumentException e) {
            throw new CompileException(e);
        } catch (ParseException ex) {
            throw new CompileException(ex);
        }
    }


    /**
     * Performs the actual compilation of a single byte matcher from an abstract syntax tree.
     *
     * @param ast The abstract syntax tree to compile.
     * @return A SingleByteMatcher representing the expression.
     * @throws ParseException If the ast could not be parsed.
     */
    private SingleByteMatcher buildSingleByte(CommonTree ast) throws ParseException {

        SingleByteMatcher matcher = null;

        switch (ast.getToken().getType()) {

            case (regularExpressionParser.BYTE): {
                matcher = new ByteMatcher(ParseUtils.getHexByteValue(ast));
                break;
            }


            case (regularExpressionParser.ALL_BITMASK): {
                final byte bitmask = ParseUtils.getBitMaskValue(ast);
                matcher = new BitMaskAllBitsMatcher(bitmask);
                break;
            }


            case (regularExpressionParser.ANY_BITMASK): {
                final byte bitmask = ParseUtils.getBitMaskValue(ast);
                matcher = new BitMaskAnyBitsMatcher(bitmask);
                break;
            }


            case (regularExpressionParser.SET): {
                final Set<Byte> byteSet = ParseUtils.calculateSetValue(ast);
                matcher = matcherFactory.create(byteSet, false);
                break;
            }


            case (regularExpressionParser.INVERTED_SET): {
                final Set<Byte> byteSet = ParseUtils.calculateSetValue(ast);
                matcher = matcherFactory.create(byteSet, true);
                break;
            }


            case (regularExpressionParser.ANY): {
                matcher = new AnyMatcher();
                break;
            }


            case (regularExpressionParser.CASE_SENSITIVE_STRING): {
                final String str = ParseUtils.trimString(ast.getText());
                if (str.length() != 1) {
                    throw new ParseException("String must be one character to parse into a single byte matcher.");
                }
                final byte value = (byte) str.charAt(0);
                matcher = new ByteMatcher(value);
                break;
            }


            case (regularExpressionParser.CASE_INSENSITIVE_STRING): {
                final String str = ParseUtils.trimString(ast.getText());
                if (str.length() != 1) {
                    throw new ParseException("String must be one character to parse into a single byte matcher.");
                }
                matcher = new CaseInsensitiveByteMatcher(str.charAt(0));
                break;
            }

            default: {
                throw new ParseException(ParseUtils.getTypeErrorMessage(ast));
            }
        }
        return matcher;
    }

}
