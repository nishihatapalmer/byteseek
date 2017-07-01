/*
 * Copyright Matt Palmer 2009-2016, All rights reserved.
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
package net.byteseek.compiler.matcher;

import java.util.ArrayList;
import java.util.List;

import net.byteseek.matcher.bytes.*;
import net.byteseek.matcher.sequence.ByteMatcherSequenceMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.parser.ParseException;
import net.byteseek.parser.tree.ParseTree;
import net.byteseek.parser.tree.ParseTreeUtils;

/**
 * Static utilities which create ByteMatchers from ParseTree specifications, or a set of bytes.
 * <p>
 * These are split out from the actual compiler classes, as they can be re-used by different
 * several different compilers.
 *
 * @author Matt Palmer
 */
public final class MatcherCompilerUtils {

  private final static boolean NOT_YET_INVERTED = false;
  
	private MatcherCompilerUtils() {
		// Private constructor to prevent the construction of a static utility class.
	}

	/**
	 * Returns true if the combination of a ParseTree node and an inversion status
	 * is still inverted.  If the node is not inverted, then the result should be the
	 * same as the currentInversion status.  If the node itself is inverted, then the
	 * result is the opposite of the current inversion.
	 *
	 * @param node A ParseTree node to check.
	 * @param currentInversion Whether we are currently inverting results.
	 * @return The combined inversion status of the parse node and a current inversion status.
	 */
	public static boolean isInverted(final ParseTree node, final boolean currentInversion) {
		return currentInversion ^ node.isValueInverted();
	}

	/**
	 * Creates a byte matcher from a parse tree node with a byte value.
	 *
	 * @param node The ParseTree node containing a byte value.
	 * @return A ByteMatcher which matches the byte (or the inverted byte, if the node is inverted).
	 * @throws ParseException If the node does not contain a byte value.
	 */
	public static ByteMatcher createByteMatcher(final ParseTree node) throws ParseException {
	  return createByteMatcher(node, NOT_YET_INVERTED);
	}

	/**
	 * Creates a byte matcher from a parse tree node with a byte value and current inversion status.
	 *
	 * @param node The ParseTree node containing a byte value.
	 * @param currentInversion Whether we are currently inverting values.
	 * @return A ByteMatcher which matches the byte (or the inverted byte, if the combination of
	 *                       the node and the current inversion is inverted).
	 * @throws ParseException If the node does not contain a byte value.
	 */
	public static ByteMatcher createByteMatcher(final ParseTree node, final boolean currentInversion) throws ParseException {
		return isInverted(node, currentInversion)? new InvertedByteMatcher(node.getByteValue()) 
		 							 			 : OneByteMatcher.valueOf(node.getByteValue());
	}

	/**
	 * Create an Any byte matcher from a parse tree node.  It does not check the type of the node
	 * passed in.
	 *
	 * @param node The node to create the Any byte matcher from.
	 * @return An Any ByteMatcher.
	 * @throws ParseException if the node would end up inverted
	 *                        (inverted Any nodes are not allowed as they would match nothing).
	 */
	public static ByteMatcher createAnyMatcher(final ParseTree node) throws ParseException {
	  return createAnyMatcher(node, NOT_YET_INVERTED);
	}

	/**
	 * Create an Any byte matcher from a parse tree node and a current inversion status.
	 * It does not check the type of the node passed in.
	 *
	 * @param node The node to create the Any byte matcher from.
	 * @param currentInversion whether we are currently inverting.
	 * @return An Any ByteMatcher.
	 * @throws ParseException if the node would end up inverted
	 *                        (inverted Any nodes are not allowed as they would match nothing).
	 */
	@SuppressWarnings("SameReturnValue")
	public static ByteMatcher createAnyMatcher(final ParseTree node, final boolean currentInversion) throws ParseException {
		if (isInverted(node, currentInversion)) {
			throw new ParseException("Cannot invert the value of the Any matcher - matching nothing is not permitted.");
		}
		return AnyByteMatcher.ANY_BYTE_MATCHER;
	}

	/**
	 * Creates an all bitmask byte matcher from a parse tree node with a byte value.
	 *
	 * @param node The ParseTree node containing a byte value.
	 * @return A ByteMatcher which matches all bits in the byte (or the inverted byte, if the combination of
	 *                       the node and the current inversion is inverted).
	 * @throws ParseException If the node does not contain a byte value.
	 */
	public static ByteMatcher createAllBitmaskMatcher(final ParseTree node) throws ParseException {
	  return createAllBitmaskMatcher(node, NOT_YET_INVERTED);
	}

	/**
	 * Creates an all bitmask byte matcher from a parse tree node with a byte value and current inversion status.
	 *
	 * @param node The ParseTree node containing a byte value.
	 * @param currentInversion Whether we are currently inverting values.
	 * @return A ByteMatcher which matches all bits in the byte (or the inverted byte, if the combination of
	 *                       the node and the current inversion is inverted).
	 * @throws ParseException If the node does not contain a byte value.
	 */
	public static ByteMatcher createAllBitmaskMatcher(final ParseTree node, final boolean currentInversion) throws ParseException {
		return new AllBitmaskMatcher(node.getByteValue(), isInverted(node, currentInversion));
	}

	/**
	 * Creates an any bitmask byte matcher from a parse tree node with a byte value.
	 *
	 * @param node The ParseTree node containing a byte value.
	 * @return A ByteMatcher which matches any bits in the byte (or the inverted byte, if the combination of
	 *                       the node and the current inversion is inverted).
	 * @throws ParseException If the node does not contain a byte value.
	 */
	public static ByteMatcher createAnyBitmaskMatcher(final ParseTree node) throws ParseException {
	  return createAnyBitmaskMatcher(node, NOT_YET_INVERTED);
	}

	/**
	 * Creates an any bitmask byte matcher from a parse tree node with a byte value and current inversion status.
	 *
	 * @param node The ParseTree node containing a byte value.
	 * @param currentInversion Whether we are currently inverting values.
	 * @return A ByteMatcher which matches any bits in the byte (or the inverted byte, if the combination of
	 *                       the node and the current inversion is inverted).
	 * @throws ParseException If the node does not contain a byte value.
	 */
	public static ByteMatcher createAnyBitmaskMatcher(final ParseTree node, final boolean currentInversion) throws ParseException {
		return new AnyBitmaskMatcher(node.getByteValue(), isInverted(node, currentInversion));
	}

	/**
	 * Creates a range byte matcher from a parse tree node with a type of ParseTreeType.RANGE
	 * and two child nodes containing byte values.
	 *
	 * @param node The ParseTree node containing range byte values as child nodes.
	 * @return A ByteMatcher which matches a range of byte values (or the inverse if
	 *                       the node and the current inversion is inverted).
	 * @throws ParseException If the node does not contain two child nodes with byte values, or
	 *                        the type of the node is not ParseTreeType.RANGE.
	 */
	public static ByteMatcher createRangeMatcher(final ParseTree node) throws ParseException {
	  return createRangeMatcher(node, NOT_YET_INVERTED);
	}

	/**
	 * Creates a range byte matcher from a parse tree node with a type of ParseTreeType.RANGE
	 * and two child nodes containing byte values.
	 * The current inversion status and the inversion of the node is combined.
	 *
	 * @param node The ParseTree node containing range byte values as child nodes.
	 * @param currentInversion Whether we are currently inverting values.
	 * @return A ByteMatcher which matches a range of byte values (or the inverse if
	 *                       the node and the current inversion is inverted).
	 * @throws ParseException If the node does not contain two child nodes with byte values, or
	 *                        the type of the node is not ParseTreeType.RANGE.
	 */
	public static ByteMatcher createRangeMatcher(final ParseTree node, final boolean currentInversion) throws ParseException {
		return new ByteRangeMatcher(ParseTreeUtils.getFirstRangeValue(node),
									ParseTreeUtils.getSecondRangeValue(node), 
									isInverted(node, currentInversion));
	}

	/**
	 * Creates a matcher from a set node of type ParseTreeType.SET.  The child nodes contain the values
	 * in the set.  If the set only contains a single value which could match, then a simpler
	 * matcher will be returned (e.g. a set with a single byte in it will return a byte matcher).
	 *
	 * @param node A parse node of type ParseTreeType.SET with child nodes containing the set values.
	 * @param matcherFactory A factory to create matchers from a set of byte values.
	 * @return A byte matcher which matches the bytes specified in the set (or their inverse if the set node is inverted).
	 * @throws ParseException If the node is not of the correct type.
	 */
	public static ByteMatcher createMatcherFromSet(final ParseTree node,
	                                                final ByteMatcherFactory matcherFactory) throws ParseException {
	  return createMatcherFromSet(node, NOT_YET_INVERTED, matcherFactory);
	}

	/**
	 * Creates a matcher from a set node of type ParseTreeType.SET.  The child nodes contain the values
	 * in the set.  If the set only contains a single value which could match, then a simpler
	 * matcher will be returned (e.g. a set with a single byte in it will return a byte matcher).
	 * The inversion status of the resulting matcher is determined from the inversion of the node
	 * and the current inversion status.
	 *
	 * @param node A parse node of type ParseTreeType.SET with child nodes containing the set values.
	 * @param currentInversion Whether we are currently inverting values.
	 * @param matcherFactory A factory to create matchers from a set of byte values.
	 * @return A byte matcher which matches the bytes specified in the set (or their inverse if the set node is inverted).
	 * @throws ParseException If the node is not of the correct type.
	 */
	public static ByteMatcher createMatcherFromSet(final ParseTree node,
												   final boolean currentInversion,
												   final ByteMatcherFactory matcherFactory) throws ParseException {
		final boolean isInverted = isInverted(node, currentInversion);
		// If the set only has one element that matches something, it may be something we can directly represent
		// with a dedicated matcher, rather than using the slower matcherFactory (as we
		// are forced to calculate the set of byte values in the set to use it).
		if (node.getNumChildren() == 1) {
			final ParseTree singleElement = node.getChild(0);
			switch (singleElement.getParseTreeType()) {
				case BYTE: 			return createByteMatcher(singleElement, isInverted);
				case ANY:  			return createAnyMatcher(singleElement, isInverted);
				case ALL_BITMASK: 	return createAllBitmaskMatcher(singleElement, isInverted);
				case ANY_BITMASK: 	return createAnyBitmaskMatcher(singleElement, isInverted);
				case RANGE: 		return createRangeMatcher(singleElement, isInverted);
				case SET:			return createMatcherFromSet(singleElement, isInverted, matcherFactory);
			}
		}
		// Not a simple set - build the bytes in the set and ask the matcher factory for a matcher.
		return matcherFactory.create(ParseTreeUtils.getSetValues(node), isInverted);
	}

	/**
	 * Creates a case insensitive sequence of matchers given a string.
	 *
	 * @param string The string to build the matchers from.
	 * @return A sequence of matchers matching the string case insensitively.
	 */
	public static SequenceMatcher createCaseInsensitiveMatcher(final String string) {
		return ByteMatcherSequenceMatcher.caseInsensitive(string);
	}

	//TODO: doesn't need to be here - only usage is now a test.  Move test to TwoByteMatcher and get rid of this code here.
	/**
	 * Returns an ASCII case insensitive byte matcher given a char.
	 *
	 * @param caseChar The character to get a case insensitive byte matcher for.
	 * @return A byte matcher which matchers the character case insensitively.
	 * @throws IllegalArgumentException if the character has a value greater than 255.
	 */
	public static ByteMatcher createCaseInsensitiveMatcher(final char caseChar) {
		return TwoByteMatcher.caseInsensitive(caseChar);
	}
	
	
}
