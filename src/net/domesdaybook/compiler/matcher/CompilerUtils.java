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

import java.util.List;

import net.domesdaybook.matcher.bytes.AllBitmaskMatcher;
import net.domesdaybook.matcher.bytes.AnyBitmaskMatcher;
import net.domesdaybook.matcher.bytes.AnyByteMatcher;
import net.domesdaybook.matcher.bytes.ByteMatcher;
import net.domesdaybook.matcher.bytes.ByteMatcherFactory;
import net.domesdaybook.matcher.bytes.ByteRangeMatcher;
import net.domesdaybook.matcher.bytes.InvertedByteMatcher;
import net.domesdaybook.matcher.bytes.OneByteMatcher;
import net.domesdaybook.parser.ParseException;
import net.domesdaybook.parser.tree.ParseTree;
import net.domesdaybook.parser.tree.ParseTreeUtils;

/**
 * @author Matt Palmer
 *
 */
public final class CompilerUtils {

  private final static boolean NOT_YET_INVERTED = false;
  
	private CompilerUtils() {
		// Private constructor to prevent the construction of a static utility class.
	}

	public static boolean isInverted(final ParseTree node, final boolean currentInversion) throws ParseException {
		return currentInversion ^ node.isValueInverted();
	}
	
	public static ByteMatcher createByteMatcher(final ParseTree node) throws ParseException {
	  return createByteMatcher(node, NOT_YET_INVERTED);
	}
	
	public static ByteMatcher createByteMatcher(final ParseTree node, final boolean currentInversion) throws ParseException {
		return isInverted(node, currentInversion)? new InvertedByteMatcher(node.getByteValue()) 
		 							 			 : new OneByteMatcher(node.getByteValue());
	}
	
	public static ByteMatcher createAnyMatcher(final ParseTree node) throws ParseException {
	  return createAnyMatcher(node, NOT_YET_INVERTED);
	}
	
	public static ByteMatcher createAnyMatcher(final ParseTree node, final boolean currentInversion) throws ParseException {
		if (isInverted(node, currentInversion)) {
			throw new ParseException("Cannot invert the value of the Any matcher - matching nothing is not permitted.");
		}
		return AnyByteMatcher.ANY_BYTE_MATCHER;
	}	
	
	public static ByteMatcher createAllBitmaskMatcher(final ParseTree node) throws ParseException {
	  return createAllBitmaskMatcher(node, NOT_YET_INVERTED);
	}
	
	public static ByteMatcher createAllBitmaskMatcher(final ParseTree node, final boolean currentInversion) throws ParseException {
		return new AllBitmaskMatcher(node.getByteValue(), isInverted(node, currentInversion));
	}	
	
	public static ByteMatcher createAnyBitmaskMatcher(final ParseTree node) throws ParseException {
	  return createAnyBitmaskMatcher(node, NOT_YET_INVERTED);
	}
	
	public static ByteMatcher createAnyBitmaskMatcher(final ParseTree node, final boolean currentInversion) throws ParseException {
		return new AnyBitmaskMatcher(node.getByteValue(), isInverted(node, currentInversion));
	}		
	
	public static ByteMatcher createRangeMatcher(final ParseTree node) throws ParseException {
	  return createRangeMatcher(node, NOT_YET_INVERTED);
	}
	
	public static ByteMatcher createRangeMatcher(final ParseTree node, final boolean currentInversion) throws ParseException {
		return new ByteRangeMatcher(ParseTreeUtils.getFirstRangeValue(node),
									ParseTreeUtils.getSecondRangeValue(node), 
									isInverted(node, currentInversion));
	}	
	
	public static ByteMatcher createMatcherFromSet(final ParseTree node,
	                                                final ByteMatcherFactory matcherFactory) throws ParseException {
	  return createMatcherFromSet(node, NOT_YET_INVERTED, matcherFactory);
	}
	
	public static ByteMatcher createMatcherFromSet(final ParseTree node, 
												   final boolean currentInversion,
												   final ByteMatcherFactory matcherFactory) throws ParseException {
		final boolean isInverted = isInverted(node, currentInversion);
		final List<ParseTree> setElements = node.getChildren();
		// If the set only has one element, it may be something we can directly represent
		// with a dedicated matcher, rather than using the slower matcherFactory (as we
		// are forced to calculate the set of byte values in the set to use it).
		if (setElements.size() == 1) {
			final ParseTree singleElement = setElements.get(0);
			switch (singleElement.getParseTreeType()) {
				case BYTE: 			return createByteMatcher(singleElement, isInverted);
				case ANY:  			return createAnyMatcher(singleElement, isInverted);
				case ALL_BITMASK: 	return createAllBitmaskMatcher(singleElement, isInverted);
				case ANY_BITMASK: 	return createAnyBitmaskMatcher(singleElement, isInverted);
				case RANGE: 		return createRangeMatcher(singleElement, isInverted);
				//FIXME: inversion of set doesn't look right - ignores current inversion status.
				case SET:			return createMatcherFromSet(singleElement, singleElement.isValueInverted(), matcherFactory);
			}
		}
		// Not a simple set - build the bytes in the set and ask the matcher factory for a matcher.
		return matcherFactory.create(ParseTreeUtils.getSetValues(node), isInverted);
	}
	
	public static ByteMatcher createSetMatcherFromString(final ParseTree node,
	                                                  final ByteMatcherFactory matcherFactory) throws ParseException {
	  return createSetMatcherFromString(node, NOT_YET_INVERTED, matcherFactory);
	}
	
	public static ByteMatcher createSetMatcherFromString(final ParseTree node, 
													  final boolean currentInversion,
													  final ByteMatcherFactory matcherFactory) throws ParseException {
		return matcherFactory.create(ParseTreeUtils.getStringAsSet(node), 
									 isInverted(node, currentInversion));
	}
	
	public static ByteMatcher createSetMatcherFromCaseInsensitiveString(final ParseTree node,
	                                                          final ByteMatcherFactory matcherFactory) throws ParseException {
	  return createSetMatcherFromCaseInsensitiveString(node, NOT_YET_INVERTED, matcherFactory);
	}
	
	public static ByteMatcher createSetMatcherFromCaseInsensitiveString(final ParseTree node,
																	 final boolean currentInversion,
																	 final ByteMatcherFactory matcherFactory) throws ParseException {
		return matcherFactory.create(ParseTreeUtils.getCaseInsensitiveStringAsSet(node),
									 isInverted(node, currentInversion));
	}	
	
	
}
