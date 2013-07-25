/*
 * Copyright Matt Palmer 2012, All rights reserved.
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

package net.byteseek.parser.regex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.byteseek.parser.ParseException;
import net.byteseek.parser.Parser;
import net.byteseek.parser.StringParseReader;
import net.byteseek.parser.tree.ParseTree;
import net.byteseek.parser.tree.ParseTreeType;
import net.byteseek.parser.tree.node.BaseNode;
import net.byteseek.parser.tree.node.ByteNode;
import net.byteseek.parser.tree.node.ChildrenNode;
import net.byteseek.parser.tree.node.IntNode;
import net.byteseek.parser.tree.node.StringNode;

/**
 * A hand-written regular expression parser for byteseek.
 * <p>
 * The parser has no state, so it is entirely thread-safe.
 * 
 * @author Matt Palmer
 */
public class RegexParser implements Parser<ParseTree> {

	//FEATURE: set subtraction: [ 'a'-'z' -['aeiou']] 
	
	/*
	 * Private syntactic character constants
	 */
	private static final char ANY                    = '.';
	private static final char ALTERNATIVE            = '|';
	private static final char COMMENT 				= '#';
	private static final char STRING_QUOTE 			= '\'';
	private static final char CASE_INSENSITIVE_QUOTE 	= '`';
	private static final char ALL_BITMASK 			= '&';
	private static final char ANY_BITMASK 			= '~';
	private static final char SHORTHAND_ESCAPE 		= '\\';
	private static final char OPEN_SET 				= '[';
	private static final char INVERT	 				= '^';
	private static final char RANGE_SEPARATOR 		= '-';
	private static final char CLOSE_SET 				= ']';
	private static final char OPTIONAL 				= '?';
	private static final char MANY 					= '*';
	private static final char ONE_TO_MANY 			= '+';
	private static final char OPEN_REPEAT 			= '{';
	private static final char REPEAT_SEPARATOR       = ',';
	private static final char CLOSE_REPEAT 			= '}';	
	private static final char OPEN_GROUP 				= '(';
	private static final char CLOSE_GROUP 			= ')';
	
	/*
	 * Private general constants
	 */
	private static final boolean INVERTED 		= true;
	private static final boolean NOT_INVERTED	= false;

	/*
	 * Public common byte values as re-usable parse nodes.
	 */
	public static final ParseTree SPACE      		= ByteNode.valueOf((byte) ' '); 
	public static final ParseTree UNDERSCORE 		= ByteNode.valueOf((byte) '_');
	public static final ParseTree TAB				= ByteNode.valueOf((byte) '\t');
	public static final ParseTree NEWLINE			= ByteNode.valueOf((byte) '\n');
	public static final ParseTree CARRIAGE_RETURN = ByteNode.valueOf((byte) '\r');
	public static final ParseTree VERTICAL_TAB    = ByteNode.valueOf((byte) 0x0b);
	public static final ParseTree FORM_FEED	    = ByteNode.valueOf((byte) 0x0c);
	public static final ParseTree ESCAPE	        = ByteNode.valueOf((byte) 0x1e);
	
	/*
	 * Public common sets of bytes as re-usable parse nodes.
	 */
	public static final ParseTree DIGITS_RANGE, 		NOT_DIGITS_RANGE;
	public static final ParseTree LOWERCASE_RANGE, 	NOT_LOWERCASE_RANGE;
	public static final ParseTree UPPERCASE_RANGE, 	NOT_UPPERCASE_RANGE;
	public static final ParseTree WHITESPACE_SET, 	NOT_WHITESPACE_SET;
	public static final ParseTree WORD_CHAR_SET, 		NOT_WORD_CHAR_SET;
	public static final ParseTree ASCII_RANGE, 		NOT_ASCII_RANGE;
	static {
		DIGITS_RANGE 		= buildRange((byte) '0', (byte) '9', NOT_INVERTED);
		NOT_DIGITS_RANGE 	= buildRange((byte) '0', (byte) '9', INVERTED);
		LOWERCASE_RANGE 	= buildRange((byte) 'a', (byte) 'z', NOT_INVERTED);
		NOT_LOWERCASE_RANGE = buildRange((byte) 'a', (byte) 'z', INVERTED);
		UPPERCASE_RANGE		= buildRange((byte) 'A', (byte) 'A', NOT_INVERTED);
		NOT_UPPERCASE_RANGE	= buildRange((byte) 'A', (byte) 'A', INVERTED);
		ASCII_RANGE	        = buildRange((byte) 0x00, (byte) 0x7f, NOT_INVERTED);
		NOT_ASCII_RANGE	    = buildRange((byte) 0x00, (byte) 0x7f, INVERTED);
		WHITESPACE_SET	    = buildSet(SPACE, TAB, NEWLINE, CARRIAGE_RETURN);
		NOT_WHITESPACE_SET  = buildInvertedSet(SPACE, TAB, NEWLINE, CARRIAGE_RETURN);
		WORD_CHAR_SET 		= buildSet(DIGITS_RANGE, LOWERCASE_RANGE, UPPERCASE_RANGE, UNDERSCORE);
		NOT_WORD_CHAR_SET 	= buildInvertedSet(DIGITS_RANGE, LOWERCASE_RANGE, UPPERCASE_RANGE, UNDERSCORE);
	}
	
	/*
	 * Public static utility methods to build common parse tree nodes.
	 */
	public static final ParseTree buildRange(byte minByte, byte maxByte, boolean inverted) {
		return new ChildrenNode(ParseTreeType.RANGE, 
								 buildList(ByteNode.valueOf(minByte), ByteNode.valueOf(maxByte)), inverted);
	}
	
	public static final ParseTree buildSet(ParseTree...parseTrees) {
		return new ChildrenNode(ParseTreeType.SET, buildList(parseTrees));
	}
	
	public static final ParseTree buildInvertedSet(ParseTree...parseTrees) {
		return new ChildrenNode(ParseTreeType.SET, buildList(parseTrees), INVERTED);
	}
	
	private static final List<ParseTree> buildList(ParseTree...parseTrees) {
		return Arrays.asList(parseTrees);
	}
	

	/*
	 * Public methods
	 */
	
	@Override
	public ParseTree parse(final String expression) throws ParseException {
		if (expression == null || expression.isEmpty()) {
			throw new ParseException("Null or empty expression not allowed.");
		}
		return parseAlternatives(new StringParseReader(expression));
	}
	
	
	/*
	 * Private methods
	 */
	
	private ParseTree parseAlternatives(final StringParseReader expression) throws ParseException {
		final List<ParseTree> alternatives = new ArrayList<ParseTree>(8);
		while (!expression.atEnd() && expression.peekBehind() != CLOSE_GROUP) {
			final ParseTree sequence = parseSequence(expression);
			if (sequence != null) {
				alternatives.add(sequence);
			}
		}
		return optimisedAlternatives(alternatives, expression);
	}
	
	
	private ParseTree optimisedAlternatives(final List<ParseTree> alternatives, StringParseReader expression)
			throws ParseException {
		final int numAlternatives = alternatives.size();
		
		// If there are no alternatives, throw an error:
		if (numAlternatives == 0) {
			throw new ParseException(addContext("No alternatives were found.", expression));
		}
		
		// If there is only a single alternative, then just return the alternative directly.
		if (numAlternatives == 1) {
			return alternatives.get(0);
		}
		
		// See if there is more than one alternative that only matches sequences of length one.
		// If there is, they can be more efficiently represented as a set match for those byte values.
		final ParseTree optimisedSet = optimiseSingleByteAlternatives(alternatives);
		
		// If there are no remaining alternatives (all got put into the set), return the set directly:
		if (alternatives.size() == 0) {
			return optimisedSet;
		}
		
		// If there is now an optimised set and some remaining alternatives, add it to the list of alternatives:
		if (optimisedSet != null) {
			alternatives.add(optimisedSet);
		}
		
		// Return an alternatives type with the alternatives as children:
		return new ChildrenNode(ParseTreeType.ALTERNATIVES, alternatives);
	}
	
	
	/**
	 * Looks at a list of alternatives for alternatives that only match a sequence of length one each.
	 * If there is more than one of them, they can be collapsed into a single SET ParseTree which 
	 * matches all of the alternatives in a single set matching step.
	 * <p>
	 * If it discovers alternatives that can be optimised, it removes them from the list of alternatives
	 * passed in, and returns a SET ParseTree consisting of the alternatives which can be treated as a set.
	 * If there are no optimisations possible, it does not modify the list passed in, and returns null for
	 * the set of alternatives.
	 * 
	 * @param alternatives The list of alternatives to optimise.
	 * @return A SET ParseTree for the optimised alternatives (removing them from the original list passed in),
	 *         or null if there are no optimisations possible.
	 */
	private ParseTree optimiseSingleByteAlternatives(final List<ParseTree> alternatives) throws ParseException {
		// Find out if there is more than one alternative that matches only a sequence of length one:
		int numOptimisableAlternatives = 0;
		for (final ParseTree alternative : alternatives) { 
			if (matchesSingleByteLength(alternative)) {
				numOptimisableAlternatives++;
			}
		}
		
		// If there are, build a list of them, remove them from the original list of alternatives,
		// and return a set node of the optimisable alternatives:
		if (numOptimisableAlternatives > 1) {
			final List<ParseTree> setChildren = new ArrayList<ParseTree>(numOptimisableAlternatives);
			final Iterator<ParseTree> altIterator = alternatives.iterator();
			while (altIterator.hasNext()) {
				final ParseTree currentAlternative = altIterator.next();
				if (matchesSingleByteLength(currentAlternative)) {
					setChildren.add(currentAlternative);
					altIterator.remove();
				}
			}
			return new ChildrenNode(ParseTreeType.SET, setChildren);
		}
		
		// No optimisable alternatives: return null and don't modify the original list of alternatives:
		return null;
	}
	
	
	private boolean matchesSingleByteLength(final ParseTree node) throws ParseException {
		switch (node.getParseTreeType()) {
			case BYTE: 			
			case RANGE:
			case SET:		
			case ANY:
			case ALL_BITMASK:	
			case ANY_BITMASK: {
				return true;
			}
			case STRING:
			case CASE_INSENSITIVE_STRING: {
				return node.getTextValue().length() == 1;
			}
			default : return false;
		}
	}
	

	private ParseTree parseSequence(final StringParseReader expression) throws ParseException {
		final List<ParseTree> sequenceNodes = new ArrayList<ParseTree>();
		int currentChar = expression.read();
		boolean requireRangeValue = false;
		PARSE_SEQUENCE: while (currentChar >= 0) {
			if (foundQuantifiedBytes(currentChar, expression, sequenceNodes)) {
				if (requireRangeValue) {
					createRange(sequenceNodes, expression);
					requireRangeValue = false;
				}
			} else 	if (!foundWhitespaceAndComments(currentChar, expression)) {
				if (requireRangeValue) {
					throw new ParseException(addContext("A range value was expected", expression));
				}
				switch (currentChar) {
					/*
					 * Groups (i.e. another set of alternative sequences):
					 */
					case OPEN_GROUP: {
						sequenceNodes.add(parseAlternatives(expression));
						break;
					}
				
					/*
					 * Alternatives or closing the group ends this sequence.
					 */
					case ALTERNATIVE:
					case CLOSE_GROUP: {
						break PARSE_SEQUENCE;
					}		
					
					/*
					 * A range
					 */
					case RANGE_SEPARATOR: {
						requireRangeValue = true;
						break;
					}
					
					/*
					 * Unexpected character:
					 */
					default: {
						throw new ParseException(addContext("Unexpected character [" + (char) currentChar + ']', expression));
					}
				}
			}
			currentChar = expression.read();
		}
		
		if (requireRangeValue) {
			throw new ParseException(addContext("Cannot have a range without a second value.", expression));
		}
		
		if (sequenceNodes.isEmpty()) {
			throw new ParseException(addContext("Cannot have an empty sequence", expression));
		}
		return sequenceNodes.size() == 1? sequenceNodes.get(0) 
										 : new ChildrenNode(ParseTreeType.SEQUENCE, sequenceNodes);
	}
	
	private void createRange(final List<ParseTree> sequence,
						      final StringParseReader expression) throws ParseException {
		final ParseTree secondRangeValue = popLastRangeValueNode(sequence, expression);
		final ParseTree firstRangeValue  = popLastRangeValueNode(sequence, expression);
		final List<ParseTree> rangeChildren = new ArrayList<ParseTree>(2);
		rangeChildren.add(firstRangeValue);
		rangeChildren.add(secondRangeValue);
		sequence.add(new ChildrenNode(ParseTreeType.RANGE, rangeChildren));
	}
	
	private ParseTree popLastRangeValueNode(List<ParseTree> sequence,
										StringParseReader expression) throws ParseException {
		// Pop the last node of the sequence list:
		final ParseTree rangeValue = popLastNode(sequence, expression);

		// Process the range node syntax (could be defined as a BYTE or a single character STRING)
		return getRangeValueAsByteNode(rangeValue, expression);
	}

	
	/**
	 * Removes the last ParseTree node from a List of ParseTrees.
	 */
	private ParseTree popLastNode(List<ParseTree> sequence,
							 StringParseReader expression) throws ParseException {
		if (sequence.isEmpty()) {
			throw new ParseException(addContext("Tried to remove the last node in a sequence, but it was empty", expression));
		}
		return sequence.remove(sequence.size() - 1);
	}

	
	/**
	 * Returns a range value as a BYTE node.
	 * 
	 * A range value is normally defined as a BYTE node.  Syntactic sugar is also provided
	 * which interprets a STRING containing a single character with a value between 0 and
	 * 255 as a byte value, allowing the specification of range values like this 'a'-'z'.
	 */
	private ParseTree getRangeValueAsByteNode(ParseTree rangeValue,
											 StringParseReader expression) throws ParseException {
		// Test for an un-inverted BYTE node - if found, return it directly.
		if (rangeValue.getParseTreeType() == ParseTreeType.BYTE &&
			rangeValue.isValueInverted()  == false) {
			return rangeValue;
		}
		
		// Syntactic sugar: if the node is a STRING type with a single character in it,
		//                  then treat the char value as if it were a byte value.  This
		//                  lets the user specify 'a'-'z' for a range.  or 'a'-FF, for that matter.
		if (rangeValue.getParseTreeType() == ParseTreeType.STRING &&
			rangeValue.getTextValue().length() == 1 &&
			rangeValue.isValueInverted()       == false) {
			final char charValue = rangeValue.getTextValue().charAt(0);
			if (charValue > 255) {
				throw new ParseException(addContext("Only characters with values from 0 to 255 are permitted to define a byte range value", expression));
			}
			return ByteNode.valueOf((byte) charValue);
		}

		// A type which is not recognised - throw an error:
		throw new ParseException(addContext("A range value must be a single non-inverted byte or a non-inverted single character string.", expression));
	}
			
	
	private boolean foundWhitespaceAndComments(final int currentChar,
												 final StringParseReader expression) {
		switch (currentChar) {
			case ' '		:	case '\t':	case '\r':	case '\n': {
				return true;
			}
			case COMMENT	: {
				expression.readPastChar('\n');
				return true;
			}
			default: return false;
		}
	}
	
	
	private boolean foundBytes(final int currentChar,
							     final StringParseReader expression,
							     final List<ParseTree> nodes) throws ParseException {
		boolean inverted = false;
		int charToMatch = currentChar;
		if (charToMatch == INVERT) {
			inverted = true;
			charToMatch = expression.read();
		}
		final ParseTree node = matchBytes(charToMatch, expression, inverted);
		if (node != null) {
			nodes.add(node);
		}
		return node != null;
	}
	
	
	private boolean foundQuantifiedBytes(final int currentChar, 
										   final StringParseReader expression, 
									       final List<ParseTree> nodes) throws ParseException {
		boolean inverted = false;
		int charToMatch = currentChar;
		if (charToMatch == INVERT) {
			inverted = true;
			charToMatch = expression.read();
		}
		ParseTree node = matchBytes(charToMatch, expression, inverted);
		if (node != null) {
			nodes.add(node);
		} else {
			node = matchQuantifiers(charToMatch, expression);
			if (node != null) {
				checkQuantifiable(nodes, expression);
				makeLastNodeAChildOf(node, nodes, expression);
			}
		}
		return node != null;
	}
	
	private void checkQuantifiable(final List<ParseTree> nodes,
						            final StringParseReader expression) throws ParseException {
		if (nodes.size() == 0) {
			throw new ParseException(addContext("Nothing to quantify", expression));
		}
		final ParseTreeType type = nodes.get(nodes.size() - 1).getParseTreeType();
		switch (type) {
			case BYTE: 			case RANGE:			case SET:		case ANY:
			case SEQUENCE:		case ALTERNATIVES:	case STRING:	case CASE_INSENSITIVE_STRING:
			case ALL_BITMASK:	case ANY_BITMASK: {
				return;
			}
		}
		throw new ParseException(addContext("The type: " + type + " is not quantifiable", expression));
	}
	
	private ParseTree matchBytes(final int currentChar, 
								  final StringParseReader expression,
								  final boolean inverted) throws ParseException {
		switch (currentChar) {
			case '0': 	case '1':	case '2':	case '3':	case '4':	
			case '5': 	case '6':	case '7':	case '8':	case '9':
			case 'a': 	case 'b':	case 'c':	case 'd':	case 'e':	case 'f':	
			case 'A':	case 'B':	case 'C':	case 'D':	case 'E':	case 'F': {
				return ByteNode.valueOf(expression.readHexByte(currentChar), inverted);
			}
											
			case ANY: {
				return BaseNode.ANY_NODE;
			}
			
			case ALL_BITMASK: {
				return new ByteNode(ParseTreeType.ALL_BITMASK, expression.readHexByte(), inverted);
			}
			
			case ANY_BITMASK: {
				return new ByteNode(ParseTreeType.ANY_BITMASK, expression.readHexByte(), inverted);
			}
			
			case STRING_QUOTE:	{
				String stringValue = expression.readString(STRING_QUOTE);
				if (stringValue.isEmpty()) {
					throw new ParseException(addContext("Strings cannot be empty", expression));
				}
				return new StringNode(stringValue);
			}
			
			case CASE_INSENSITIVE_QUOTE: {
				String stringValue = expression.readString(CASE_INSENSITIVE_QUOTE);
				if (stringValue.isEmpty()) {
					throw new ParseException(addContext("Strings cannot be empty", expression));
				}
				return new StringNode(stringValue, ParseTreeType.CASE_INSENSITIVE_STRING);
			}
						               							   
			case SHORTHAND_ESCAPE:	{
				return parseShorthand(expression, inverted);
			}
			
			case OPEN_SET: 	{
				return parseSet(expression, inverted);
			}
			
			default: return null;
		}
	}
	
	
	private ParseTree parseShorthand(final StringParseReader expression,
						              final boolean inverted) throws ParseException {
		final int character = expression.read();
		switch (character) {
			case 't': return inverted? ByteNode.valueOf((byte) '\t', INVERTED) 	: TAB;
			case 'n': return inverted? ByteNode.valueOf((byte) '\n', INVERTED) 	: NEWLINE;
			case 'r': return inverted? ByteNode.valueOf((byte) '\r', INVERTED) 	: CARRIAGE_RETURN;
			case 'v': return inverted? ByteNode.valueOf((byte) 0x0b, INVERTED) 	: VERTICAL_TAB;
			case 'f': return inverted? ByteNode.valueOf((byte) 0x0c, INVERTED) 	: FORM_FEED;
			case 'e': return inverted? ByteNode.valueOf((byte) 0x1e, INVERTED)	: ESCAPE;
			case 'd': return inverted? NOT_DIGITS_RANGE    						: DIGITS_RANGE;
			case 'D': return inverted? DIGITS_RANGE        						: NOT_DIGITS_RANGE;
			case 'w': return inverted? NOT_WORD_CHAR_SET  						: WORD_CHAR_SET;
			case 'W': return inverted? WORD_CHAR_SET       						: NOT_WORD_CHAR_SET;
			case 's': return inverted? NOT_WHITESPACE_SET  						: WHITESPACE_SET;
			case 'S': return inverted? WHITESPACE_SET      						: NOT_WHITESPACE_SET;
			case 'l': return inverted? NOT_LOWERCASE_RANGE 						: LOWERCASE_RANGE;
			case 'L': return inverted? LOWERCASE_RANGE     						: NOT_LOWERCASE_RANGE;
			case 'u': return inverted? NOT_UPPERCASE_RANGE 						: UPPERCASE_RANGE;
			case 'U': return inverted? UPPERCASE_RANGE    						: NOT_UPPERCASE_RANGE;
			case 'i': return inverted? NOT_ASCII_RANGE     						: ASCII_RANGE;
			case 'I': return inverted? ASCII_RANGE         						: NOT_ASCII_RANGE;
			default: throw new ParseException(addContext("Unexpected shorthand character [" + 
														   (char) character + ']', expression));
		}
	}
	
	
	private ParseTree parseSet(final StringParseReader expression,
				                final boolean inverted ) throws ParseException {
		final List<ParseTree> setNodes = new ArrayList<ParseTree>();
		int currentChar = expression.read();
		boolean requireRangeValue = false;
		PARSE_SET: while (currentChar >= 0) {
			if (foundBytes(currentChar, expression, setNodes)) {
				if (requireRangeValue) {
					createRange(setNodes, expression);
					requireRangeValue = false;
				}
			} else if (!foundWhitespaceAndComments(currentChar, expression)) {
				if (currentChar == CLOSE_SET) {
					break PARSE_SET;
				}
				if (currentChar == RANGE_SEPARATOR) {
					requireRangeValue = true;
				} else {
					throw new ParseException(addContext("Unexpected character [" + (char) currentChar + ']', expression));
				}
			}
			currentChar = expression.read();
		}
		
		if (requireRangeValue) {
			throw new ParseException(addContext("Cannot have a range without a second value.", expression));
		}
		if (currentChar < 0) {
			throw new ParseException(addContext("The expression ended without closing the set", expression));
		}
		if (setNodes.isEmpty()) {
			throw new ParseException(addContext("Cannot have an empty set", expression));
		}
		
		return new ChildrenNode(ParseTreeType.SET, setNodes, inverted);
	}
	
	
	private ParseTree matchQuantifiers(final int currentChar, 
										final StringParseReader expression) throws ParseException {
		switch (currentChar) {
			case MANY: 			return new ChildrenNode(ParseTreeType.ZERO_TO_MANY);
			case ONE_TO_MANY: 	return new ChildrenNode(ParseTreeType.ONE_TO_MANY);
			case OPEN_REPEAT: 	return parseRepeat(expression);
			case OPTIONAL: 		return new ChildrenNode(ParseTreeType.OPTIONAL);
			default:			return null;
		}
	}
	

	private ParseTree parseRepeat(final StringParseReader expression) throws ParseException {
		final int firstValue = expression.readInt();
		int nextToken = expression.read();
		if (nextToken == CLOSE_REPEAT) {
			if (firstValue == 0) {
				throw new ParseException(addContext("Single repeat value cannot be zero", expression));
			}
			final List<ParseTree> intValueList = new ArrayList<ParseTree>(1);
			intValueList.add(new IntNode(firstValue));
			return new ChildrenNode(ParseTreeType.REPEAT, intValueList);
		}
		if (nextToken == REPEAT_SEPARATOR) {
			ParseTree repeatNode;
			if (expression.peekAhead() == MANY) {
				expression.read();
				final List<ParseTree> intValueList = new ArrayList<ParseTree>(1);
				intValueList.add(new IntNode(firstValue));
				repeatNode = new ChildrenNode(ParseTreeType.REPEAT_MIN_TO_MANY, intValueList);
			} else {
				final List<ParseTree> values = new ArrayList<ParseTree>(3);
				values.add(new IntNode(firstValue));
				values.add(new IntNode(expression.readInt()));
  			    repeatNode = new ChildrenNode(ParseTreeType.REPEAT_MIN_TO_MAX, values);
			}
			nextToken = expression.read();
			if (nextToken == CLOSE_REPEAT) {
				return repeatNode;
			}
			throw new ParseException(addContext("No closing } for repeat instruction " + repeatNode, expression));
		}
		throw new ParseException(addContext("No closing } for repeat instruction with firstValue " + firstValue, expression));
	}
	
	
	private void makeLastNodeAChildOf(final ParseTree parentNode, final List<ParseTree> sequence,
									  final StringParseReader expression) throws ParseException {
		if (sequence.size() > 0) {
			final ParseTree lastNode = sequence.remove(sequence.size() - 1);
			parentNode.addChild(lastNode);
			sequence.add(parentNode);
		} else {
			throw new ParseException(addContext("Needed the last node in a sequence to make a child of " + parentNode + 
			    					         	 " but the sequence is empty.", expression));
		}
	}


	private String addContext(String description, StringParseReader expression) {
		return description + ".  Error occurred at position [" +
				expression.getPosition() +
				"] in expression [" + expression + ']';
	}
	
}
