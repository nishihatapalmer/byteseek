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

package net.domesdaybook.parser.tree;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.domesdaybook.parser.ParseException;
import net.domesdaybook.util.bytes.ByteUtilities;

/**
 * A utility class of static helper methods to use when parsing expressions.
 * 
 * @author Matt Palmer
 */
public final class ParseTreeUtils {

	///////////////
	// Constants //
	///////////////
	
	private static final String TYPE_ERROR = "Parse tree type id [&d] with description [%s] is not supported by the parser.";

	
	/////////////////
	// Constructor //
	/////////////////
	
	/**
	 * Private constructor as this is a static utility class, so should not be
	 * constructed.
	 */
	private ParseTreeUtils() {
	}

	
	///////////////////////////
	// Public static methods //
	///////////////////////////
	
	/**
	 * Returns a byte from its hexadecimal string representation.
	 * 
	 * @param hexByte
	 *            a hexadecimal representation of a byte.
	 * @return the byte encoded by the hex representation.
	 * @throws ParseException if the string cannot be parsed as a hex byte, or is null or empty.
	 */
	public static byte parseHexByte(final String hexByte) throws ParseException {
		try {
			final int value = Integer.parseInt(hexByte, 16);
			if (value < 0 || value > 255) {
				throw new ParseException("The hex string [" + hexByte +
										  "] is not a byte value between 0 and 255 inclusive: "
										  + value);
			}
			return (byte) value;
		} catch (NumberFormatException nfe) {
			throw new ParseException("Could not parse into a hex byte: [" + hexByte + "]");
		}
	}
	
	
	/**
	 * Returns the first child of a node, or throws a ParseException if
	 * there is no such child.
	 * 
	 * @param node The node to get the first child of.
	 * @return A node which is the first child of the node passed in.
	 * @throws ParseException If there is no such child node.
	 */
	public static ParseTree getFirstChild(final ParseTree node) throws ParseException {
	  final List<ParseTree> children = node.getChildren();
	  if (children.size() > 0) {
	    return children.get(0);
	  }
	  throw new ParseException("No children exist for node type: " +
	                            node.getParseTreeType().name());
	}

	
	/**
	 * Returns the first range value of a node passed in.  A range is defined by
	 * a node with two integer child nodes.  Each range value must be an integer
	 * in the range 0 to 255, as they define byte values, and the node passed in
	 * must have the type {@link net.domesdaybook.parser.tree.ParseTreeType.RANGE}.
	 * 
	 * @param rangeNode The node with two integer child nodes defining a range.
	 * @return The integer value of the first range value.
	 * @throws ParseException If a problem occurs parsing the range value,
	 *                         or the range node or value is not correct.
	 */
	public static int getFirstRangeValue(final ParseTree rangeNode) throws ParseException {
		return getRangeValue(rangeNode, 0);
	}
	
	
	/**
	 * Returns the second range value of a node passed in.  A range is defined by
	 * a node with two integer child nodes.  Each range value must be an integer
	 * in the range 0 to 255, as they define byte values, and the node passed in
	 * must have the type {@link net.domesdaybook.parser.tree.ParseTreeType.RANGE}.
	 * 
	 * @param rangeNode The node with two integer child nodes defining a range.
	 * @return The integer value of the second range value.
	 * @throws ParseException If a problem occurs parsing the range value,
	 *                         or the range node or value is not correct.
	 */
	public static int getSecondRangeValue(final ParseTree rangeNode) throws ParseException {
		return getRangeValue(rangeNode, 1);
	}
	
	
	/**
	 * Returns a collection of unique Bytes representing all the bytes covered by the inclusive
	 * range node passed in.
	 * 
	 * @param range The range node passed in.
	 * @return A collection of unique bytes representing all the bytes in a range node passed in.
	 * @throws ParseException If the node is not a range node, or does not have correct range
	 *                         values as child nodes, or if another problem occurs parsing the node.
	 */
	public static Collection<Byte> getRangeValues(final ParseTree range) throws ParseException {
		final int range1 = getFirstRangeValue(range);
		final int range2 = getSecondRangeValue(range);
		final Set<Byte> values = new LinkedHashSet<Byte>(64);
		ByteUtilities.addBytesInRange(range1, range2, values);
		return values;
	}	
	
	
	/**
	 * Returns the first repeat value of a node passed in.  A repeat is defined by
	 * a node with three child nodes.  The first node is always an integer node, defining
	 * the minimum number of repeats.  The second node can either be an integer node, 
	 * or a Many node type, defining the maximum number of repeats.  The third node
	 * is the node which must be repeated.
	 * 
	 * @param repeatNode The node defining a repeat.
	 * @return The first repeat value (minimum number of repeats) for the repeat node.
	 * @throws ParseException If the node passed in does not have type 
	 *                         {@link net.domesdaybook.parser.tree.ParseTreeType.REPEAT},
	 *                         the number of child nodes is not correct, there is no
	 *                         first repeat value, or another problem occurs parsing.
	 */
	public static int getFirstRepeatValue(final ParseTree repeatNode) throws ParseException {
		return getRepeatValue(repeatNode, 0);
	}
	
	
	/**
	 * Returns the second repeat value of a node passed in.  A repeat is defined by
	 * a node with three child nodes.  The first node is always an integer node, defining
	 * the minimum number of repeats.  The second node can either be an integer node, 
	 * or a Many node type, defining the maximum number of repeats.  The third node
	 * is the node which must be repeated.
	 * <p>
	 * If the maximum value is MANY, rather than a specified integer, then this method
	 * will return -1 to indicate that the number of repeats is unlimited.
	 * 
	 * @param repeatNode The node defining a repeat.
	 * @return The second repeat value (maximum number of repeats) for the repeat node.
	 * @throws ParseException If the node passed in does not have type 
	 *                         {@link net.domesdaybook.parser.tree.ParseTreeType.REPEAT},
	 *                         the number of child nodes is not correct, there is no
	 *                         second repeat value, or another problem occurs parsing.
	 */	
	public static int getSecondRepeatValue(final ParseTree repeatNode) throws ParseException {
		return getRepeatValue(repeatNode, 1);
	}	
	
	
	/**
	 * Returns the last child node of an ast node.
	 * 
	 * @param parentNode The node to get the last child of.
	 * @return The last child node of the node.
	 * @throws ParseException
	 */
	public static ParseTree getLastChild(final ParseTree parentNode) throws ParseException {
		final List<ParseTree> children = parentNode.getChildren();
		if (children.size() == 0) {
			throw new ParseException("Node has no children - cannot get last child node");			
		}
		return children.get(children.size() - 1);
	}
	
	
	/**
	 * Returns a collection of unique byte values matching a bitmask, where all the bits
	 * must match the bitmask.
	 * 
	 * @param allBitmask The all bitmask node.
	 * @return A collection of bytes which match the all-bits bitmask.
	 * @throws ParseException If there is no bitmask byte value or another problem occurs
	 *                         parsing the value.
	 */
	public static Collection<Byte> getAllBitmaskValues(final ParseTree allBitmask) throws ParseException {
		return ByteUtilities.getBytesMatchingAllBitMask(allBitmask.getByteValue());
	}	
	
	
	/**
	 * Returns a collection of unique byte values matching a bitmask, where any of the bits
	 * must match the bitmask.
	 * 
	 * @param allBitmask The any bitmask node.
	 * @return A collection of bytes which match the any-bits bitmask.
	 * @throws ParseException If there is no bitmask byte value or another problem occurs
	 *                         parsing the value.
	 */
	public static Collection<Byte> getAnyBitmaskValues(final ParseTree anyBitmask) throws ParseException {
		return ByteUtilities.getBytesMatchingAnyBitMask(anyBitmask.getByteValue());		
	}		

	
	/**
	 * Returns a collection of unique byte values which consist of the byte values of the 
	 * String passed in, when encoded as ISO 8859-1 bytes.
	 * 
	 * @param string The string to get the bytes for.
	 * @return A collection of bytes representing the unique bytes defined in the string passed in.
	 * @throws ParseException If the string cannot be converted to ISO 8859-1 encoding.
	 */
	public static Collection<Byte> getStringAsSet(final ParseTree string) throws ParseException {
		final Set<Byte> values = new LinkedHashSet<Byte>();
		try {
			final byte[] utf8Value = string.getTextValue().getBytes("ISO-8859-1");
			ByteUtilities.addAll(utf8Value, values);
			return values;
		} catch (UnsupportedEncodingException e) {
			throw new ParseException(e);
		}		
	}
	
	
	/**
	 * Returns a collection of unique byte values that represent all the byte values
	 * in a String when encoded as ISO 8859-1 bytes.  Any lower or upper case characters
	 * will have their counterpart added to the collection, ensuring case-insensitivity.
	 * 
	 * @param caseInsensitive The string to get a set of case insensitive byte values for.
	 * @return A collection of bytes giving all the case insensitive bytes that string might match.
	 * @throws ParseException If the string cannot be encoded in ISO 8859-1.
	 */
	public static Collection<Byte> getCaseInsensitiveStringAsSet(final ParseTree caseInsensitive) throws ParseException {
		try {
			final byte[] byteValues = caseInsensitive.getTextValue().getBytes("ISO-8859-1");
			final Set<Byte> values = new LinkedHashSet<Byte>();		
			for (int charIndex = 0; charIndex < byteValues.length; charIndex++) {
				final byte charAt = byteValues[charIndex];
				if (charAt >= 'a' && charAt <= 'z') {
					values.add((byte) Character.toUpperCase(charAt));
				} else if (charAt >= 'A' && charAt <= 'A') {
					values.add((byte) Character.toLowerCase(charAt));
				}
				values.add(charAt);
			}
			return values;
		} catch (UnsupportedEncodingException e) {
			throw new ParseException(e);
		}		
	}
	
	
	/**
	 * Calculates the value of a set given the parent set node (or inverted set
	 * node).  Sets can contain bytes, strings (case sensitive & insensitive),
	 * ranges, other sets nested inside them (both normal and inverted) and
	 * bitmasks.
	 * <p>
	 * This method does invert the set of bytes returned if the set node passed in
	 * is inverted.  If you want to calculate the values of the set as defined in 
	 * the set (regardless of whether the set node itself is inverted), then
	 * use the method {@link #getSetValues(ParseTree)}.
	 * 
	 * @param set
	 * 				The set node to calculate a set of byte values for, taking into
	 *              account whether the set node is inverted or not.
	 * @return A set of byte values defined by the node.
	 * @throws ParseException 
	 *        		If a problem occurs parsing the node.
	 */
	public static Set<Byte> calculateSetValues(final ParseTree set) throws ParseException {
		final Set<Byte> setValues = getSetValues(set);
		if (set.isValueInverted()) {
			return ByteUtilities.invertedSet(setValues);
		}
		return setValues;
	}
	
	
	/**
	 * Calculates a value of a set given the parent set node (or inverted set
	 * node). Sets can contain bytes, strings (case sensitive & insensitive),
	 * ranges, other sets nested inside them (both normal and inverted) and
	 * bitmasks.
	 * <p>
	 * This method does not invert the set bytes returned if the root set node is inverted.
	 * It preserves the bytes as-defined in the set, leaving the question of whether to
	 * invert the bytes defined in the set passed in to any clients of the code.
	 * <p>
	 * If you want the set values calculating taking into account the inversion 
	 * status of the set node itself, please call the method {@link #calculateSetValues(ParseTree)}.
	 * <p>
	 * This can be recursive procedure if sets are nested within one another.
	 * 
	 * @param set
	 *            The set node to calculate a set of byte values for.
	 * @return A set of byte values defined by the node.
	 * @throws ParseException
	 *             If a problem occurs parsing the node.
	 */
	public static Set<Byte> getSetValues(final ParseTree set)
			throws ParseException {
		final Set<Byte> setValues = new LinkedHashSet<Byte>(192);
		for (final ParseTree child : set.getChildren()) {
			switch (child.getParseTreeType()) {
				case SEQUENCE:				    // Drop through: treat all possible types of node which may hold
				case ALTERNATIVES:			    // byte value bearing children as just containers of those values.
				case SET:					    // The idea is you can pass any regular expression node into this
				case ZERO_TO_MANY:			    // function, and get the set of all byte values which *could* be
				case ONE_TO_MANY:			    // matched by that expression.
				case OPTIONAL:					setValues.addAll(calculateSetValues(child)); 					break;
				case REPEAT:					setValues.addAll(calculateSetValues(getLastChild(child))); 	break;
				case BYTE: 						setValues.add(child.getByteValue());  							break;
				case RANGE: 					setValues.addAll(getRangeValues(child));						break;
				case ALL_BITMASK:				setValues.addAll(getAllBitmaskValues(child));					break;
				case ANY_BITMASK:				setValues.addAll(getAnyBitmaskValues(child));					break;
				case STRING:					setValues.addAll(getStringAsSet(child));						break;
				case CASE_INSENSITIVE_STRING:	setValues.addAll(getCaseInsensitiveStringAsSet(child));			break;
				default: throw new ParseException(getTypeError(child));
			}
		}
		return setValues;
	}

	
	////////////////////////////
	// Private static methods //
	////////////////////////////
	
	/**
	 * Private utility method which gets the nominated range value and validates it
	 * to make sure it is between 0 and 255.  Also validates that the range node itself
	 * only has two children, and that the range node has type ParseTreeType.RANGE.
	 * 
	 * @param rangeNode The range node to get a value for.
	 * @param valueIndex The number of the range value (first: 0 or second: 1).
	 * @return The integer value of the first or second range value.
	 * @throws ParseException If a problem occurs parsing the range value, 
	 *                         or the range node or value is not correct.
	 */
	private static int getRangeValue(final ParseTree rangeNode, final int valueIndex) throws ParseException {
		if (rangeNode.getParseTreeType() != ParseTreeType.RANGE) {
			throw new ParseException("Node is not a RANGE node.  It has type: " + rangeNode.getParseTreeType());
		}
		final List<ParseTree> rangeChildren = rangeNode.getChildren();
		if (rangeChildren.size() != 2) {
			throw new ParseException("Ranges must have two integer values as child nodes." +
			                          "Actual number of children was: " + rangeChildren.size());			
		}
		final int rangeValue = rangeChildren.get(0).getIntValue();
		if (rangeValue < 0 || rangeValue > 255) {
			throw new ParseException("Range values must be between 0 and 255." +
			                          "Actual value was: " + rangeValue);
		}
		return rangeValue;		
	}
	

	/**
	 * Private utility method which gets the nominated repeat value and validates it
	 * to make sure it is either a positive integer, or a MANY node.  Also validates that
	 * the repeat node itself has three children, and that the repeat node has type
	 * ParseTreetype.REPEAT.
	 * 
	 * @param repeatNode The repeat node to get a repeat value for.
	 * @param valueIndex the number of the repeat value (min:0, max: 1).
	 * @return An integer value of the min or max repeat.  If the max repeat is unlimited,
	 *          then -1 will be returned.
	 * @throws ParseException If the repeat node does not have type ParseTreeType.REPEAT,
	 *                         it does not have three children, or an integer value supplied
	 *                         is not positive.
	 */
	//FIXME: this utility method is probably useless now that we have explicit REPEAT, REPEAT_MIN_TO_MANY and
	//       REPEAT_MIN_TO_MAX nodes.  Need to evaluate which of the utility methods are needed after refactoring
	//       compilers to use the new repeat nodes.
	private static int getRepeatValue(final ParseTree repeatNode, final int valueIndex) throws ParseException {
		if (repeatNode.getParseTreeType() != ParseTreeType.REPEAT) {
			throw new ParseException("Node is not a REPEAT node.  It has type: " + repeatNode.getParseTreeType());
		}
		final List<ParseTree> repeatChildren = repeatNode.getChildren();
		if (repeatChildren.size() != 3) {
			throw new ParseException("Repeats must have three child nodes. " +
			                          "Actual number of children was: " +repeatChildren.size());			
		}
		final ParseTree repeatValue = repeatChildren.get(valueIndex);
		if (repeatValue.getParseTreeType() == ParseTreeType.INTEGER) {
		    final int intValue = repeatValue.getIntValue();
		    if (intValue < 1) {
		      throw new ParseException("Repeat integer values must be at least one. " +
		                                "Actual value was: " + intValue);
		    }
		    return intValue;
		}
		return -1; 
	}
	
	
	/**
	 * Returns a nicely formatted type error message given a node.
	 * 
	 * @param node The node to get a type error message for.
	 * @return A type error message for that node.
	 */
	private static String getTypeError(final ParseTree node) {
		final ParseTreeType type = node.getParseTreeType();
		return String.format(TYPE_ERROR, type, type.getDescription());
	}
	
}
