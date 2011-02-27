/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.expression.compiler.singlebyte;

import java.util.Set;
import net.domesdaybook.expression.compiler.AstCompiler;
import net.domesdaybook.expression.parser.ParseException;
import net.domesdaybook.expression.parser.ParseUtils;
import net.domesdaybook.expression.parser.regularExpressionParser;
import net.domesdaybook.matcher.singlebyte.AllBitMaskMatcher;
import net.domesdaybook.matcher.singlebyte.AnyBitMaskMatcher;
import net.domesdaybook.matcher.singlebyte.AnyByteMatcher;
import net.domesdaybook.matcher.singlebyte.ByteMatcher;
import net.domesdaybook.matcher.singlebyte.ByteSetMatcher;
import net.domesdaybook.matcher.singlebyte.CaseInsensitiveByteMatcher;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import org.antlr.runtime.tree.CommonTree;

/**
 *
 * @author matt
 */
public final class SingleByteCompiler extends AstCompiler<SingleByteMatcher> {

    @Override
    public SingleByteMatcher compile(CommonTree ast) throws ParseException {
        if (ast == null) {
            throw new ParseException("Null abstract syntax tree passed in to SingleByteCompiler.");
        }
        try {
            return buildSingleByte(ast);
        } catch (IllegalArgumentException e) {
            throw new ParseException(e);
        }
    }

    
    private SingleByteMatcher buildSingleByte(CommonTree ast) throws ParseException {

        SingleByteMatcher matcher = null;

        switch (ast.getToken().getType()) {

            case (regularExpressionParser.BYTE): {
                matcher = new ByteMatcher(ParseUtils.getHexByteValue(ast));
                break;
            }


            case (regularExpressionParser.ALL_BITMASK): {
                final byte bitmask = ParseUtils.getBitMaskValue(ast);
                matcher = new AllBitMaskMatcher(bitmask);
                break;
            }


            case (regularExpressionParser.ANY_BITMASK): {
                final byte bitmask = ParseUtils.getBitMaskValue(ast);
                matcher = new AnyBitMaskMatcher(bitmask);
                break;
            }


            case (regularExpressionParser.SET): {
                final Set<Byte> byteSet = ParseUtils.calculateSetValue(ast);
                matcher = ByteSetMatcher.buildOptimalMatcher(byteSet, false);
                break;
            }


            case (regularExpressionParser.INVERTED_SET): {
                final Set<Byte> byteSet = ParseUtils.calculateSetValue(ast);
                matcher = ByteSetMatcher.buildOptimalMatcher(byteSet, true);
                break;
            }


            case (regularExpressionParser.ANY): {
                matcher = new AnyByteMatcher();
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
