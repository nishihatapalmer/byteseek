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

package net.domesdaybook.compiler.bytes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.domesdaybook.compiler.AbstractAstCompiler;
import net.domesdaybook.compiler.CompileException;
import net.domesdaybook.parser.ParseException;
import net.domesdaybook.parser.ParseUtils;
import net.domesdaybook.parser.regularExpressionParser;
import net.domesdaybook.matcher.bytes.AllBitmaskMatcher;
import net.domesdaybook.matcher.bytes.AnyBitmaskMatcher;
import net.domesdaybook.matcher.bytes.AnyByteMatcher;
import net.domesdaybook.matcher.bytes.OneByteMatcher;
import net.domesdaybook.util.bytes.ByteUtilities;
import net.domesdaybook.matcher.bytes.CaseInsensitiveByteMatcher;
import net.domesdaybook.matcher.bytes.InvertedByteMatcher;
import net.domesdaybook.matcher.bytes.SimpleByteMatcherFactory;
import net.domesdaybook.matcher.bytes.ByteMatcher;
import net.domesdaybook.matcher.bytes.ByteMatcherFactory;
import org.antlr.runtime.tree.CommonTree;


/**
 * A compiler which produces a {@link ByteMatcher} from an
 * abstract syntax tree provided by the {@link AbstractAstCompiler} class,
 * which it extends.
 *
 * It can only handle syntax which would result in a single byte being
 * matched.  This means hex bytes, any byte (.), all bit-masks (&),
 * any bit-masks (~),single-character case sensitive and insensitive
 * strings, and sets of bytes [].
 *
 * It can handle alternative sequences (X|Y|Z) where each alternative
 * is one byte long, but only because they are pre-optimised by the
 * AbstractAstCompiler class into a [set] of bytes instead of a list of alternatives,
 * before this compiler even sees them.  Any alternative sequences provided
 * directly to this compiler will result in a CompileException.
 * 
 * @author Matt Palmer
 */
public final class ByteMatcherCompiler extends AbstractAstCompiler<ByteMatcher> {

    // Private constants:
    
    private static final boolean NOT_INVERTED = false;
    private static final boolean INVERTED = true;
    
    
    // Static fields and utility methods:
    
    private static ByteMatcherCompiler defaultCompiler;
    private static ByteMatcherFactory defaultFactory;
    
    
    /**
     * Compiles a {@link ByteMatcher} from the expression (assuming the syntax
     * provided results in a match for a single byte), using a {@link ByteMatcherCompiler}.
     * 
     * @param expression The regular expression syntax
     * @return ByteMatcher A ByteMatcher which matches a byte according to the expression.
     * @throws CompileException If a ByteMatcher cannot be produced from the expression.
     */
    public static ByteMatcher matcherFrom(final String expression) throws CompileException {
        defaultCompiler = new ByteMatcherCompiler();
        return defaultCompiler.compile(expression);
    }
    
    
    /**
     * Returns a {@link OneByteMatcher} which matches the byte provided.
     * Equivalent to <code>new OneByteMatcher(aByte)</code>
     * 
     * @param aByte The byte to match.
     * @return OneByteMatcher a ByteMatcher which matches a single byte value.
     */
    public static ByteMatcher matcherFrom(final byte aByte) {
        return new OneByteMatcher(aByte);
    }
    
    
    /**
     * Returns an {@link InvertedByteMatcher} which matches everything but the
     * byte provided.  Equivalent to <code>new InvertedByteMatcher(aByte)</code>
     * 
     * @param aByte The byte which should not match.
     * @return InvertedByteMatcher a matcher which matches everything but the byte provided.
     */
    public static ByteMatcher invertedMatcherFrom(final byte aByte) {
        return new InvertedByteMatcher(aByte);
    }
    
    
    /**
     * Returns a {@link ByteMatcher} which optimally matches the set of 
     * bytes provided in the array.  It uses a @link SimpleByteMatcherFactory}
     * to produce an appropriate matcher.
     * 
     * @param bytes An array of bytes containing the values the ByteMatcher must match.
     * @return ByteMatcher a matcher which matches the byte values in the array provided.
     */
    public static ByteMatcher matcherFrom(final byte[] bytes) {
        defaultFactory = new SimpleByteMatcherFactory();
        final Set<Byte> byteSet = ByteUtilities.toSet(bytes);
        return defaultFactory.create(byteSet, NOT_INVERTED);
    }
    
    
    /**
     * Returns a {@link ByteMatcher} which optimally matches the inverted set of 
     * bytes provided in the array.  It uses a @link SimpleByteMatcherFactory}
     * to produce an appropriate matcher.
     * 
     * @param bytes An array of bytes containing the values the ByteMatcher
     *              must not match.
     * @return ByteMatcher a matcher which matches all byte values other 
     *         than those in the array provided.
     */
    public static ByteMatcher invertedMatcherFrom(final byte[] bytes) {
        defaultFactory = new SimpleByteMatcherFactory();
        final Set<Byte> byteSet = ByteUtilities.toSet(bytes);
        return defaultFactory.create(byteSet, INVERTED);
    }
    
    
    // Fields:
    
    private final ByteMatcherFactory matcherFactory;

    
    // Constructors:
    
    /**
     * Constructs a ByteMatcherCompiler using a {@link SimpleByteMatcherFactory}
     * to construct optimal matchers for sets of bytes.
     * 
     */
    public ByteMatcherCompiler() {
        matcherFactory = new SimpleByteMatcherFactory();
    }
    
    
    /**
     * Constructs a ByteMatcherCompiler using the provided factory
     * to construct optimal matchers for sets of bytes.
     * 
     * @param factoryToUse The factory used to create optimal matchers for sets of bytes.
     */
    public ByteMatcherCompiler(final ByteMatcherFactory factoryToUse) {
        matcherFactory = factoryToUse;
    }
    

    /**
     * Compiles an abstract syntax tree provided by the {@link AbstractAstCompiler} class
     * which it extends, to create a {@ByteMatcher} object.
     *
     * @param ast The abstract syntax tree provided by the {@link AbstractAstCompiler} class.
     * @return A {@link ByteMatcher} which matches the expression defined by the ast passed in.
     */
    @Override
    public ByteMatcher compile(final CommonTree ast) throws CompileException {
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
     * Compiles a ByteMatcher which matches all of the bytes in the expressions.
     * It simply
     * 
     * @param expressions
     * @return
     * @throws CompileException 
     */
    public ByteMatcher compile(Collection<String> expressions) throws CompileException {
        final Set<Byte> bytesToMatch = new HashSet<Byte>();
        for (final String expression : expressions) {
            final byte[] matchingBytes = compile(expression).getMatchingBytes();
            bytesToMatch.addAll(ByteUtilities.toList(matchingBytes));
        }
        return matcherFactory.create(bytesToMatch, NOT_INVERTED);
    }    


    /**
     * Performs the actual compilation of a single byte matcher from an abstract syntax tree.
     *
     * @param ast The abstract syntax tree to compile.
     * @return A ByteMatcher representing the expression.
     * @throws ParseException If the ast could not be parsed.
     */
    private ByteMatcher buildSingleByte(CommonTree ast) throws ParseException {

        ByteMatcher matcher = null;

        switch (ast.getToken().getType()) {

            case (regularExpressionParser.BYTE): {
                matcher = new OneByteMatcher(ParseUtils.getHexByteValue(ast));
                break;
            }


            case (regularExpressionParser.ALL_BITMASK): {
                final byte bitmask = ParseUtils.getBitMaskValue(ast);
                matcher = new AllBitmaskMatcher(bitmask);
                break;
            }


            case (regularExpressionParser.ANY_BITMASK): {
                final byte bitmask = ParseUtils.getBitMaskValue(ast);
                matcher = new AnyBitmaskMatcher(bitmask);
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
                matcher = new AnyByteMatcher();
                break;
            }


            case (regularExpressionParser.CASE_SENSITIVE_STRING): {
                final String str = ParseUtils.unquoteString(ast.getText());
                if (str.length() != 1) {
                    throw new ParseException("String must be one character to parse into a single byte matcher.");
                }
                final byte value = (byte) str.charAt(0);
                matcher = new OneByteMatcher(value);
                break;
            }


            case (regularExpressionParser.CASE_INSENSITIVE_STRING): {
                final String str = ParseUtils.unquoteString(ast.getText());
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
