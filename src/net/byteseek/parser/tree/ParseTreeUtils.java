/*
 * Copyright Matt Palmer 2009-2013, All rights reserved.
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

package net.byteseek.parser.tree;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.byteseek.parser.ParseException;
import net.byteseek.util.bytes.ByteUtils;
import net.byteseek.util.object.ObjectUtils;

/**
 * A utility class of static helper methods to use when parsing expressions.
 * 
 * @author Matt Palmer
 */
public final class ParseTreeUtils {

	///////////////
	// Constants //
	///////////////
	
	private static final String TYPE_ERROR = "Parse tree type [%s] is not supported by the parser.";

	
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
	 * Returns the first child of a node, or throws a ParseException if
	 * there is no such child.
	 * 
	 * @param node The node to get the first child of.
	 * @return A node which is the first child of the node passed in.
	 * @throws ParseException If there is no such child node.
	 * @throws IllegalArgumentException if node is null.
	 */
	public static ParseTree getFirstChild(final ParseTree node) throws ParseException {
		ObjectUtils.checkNullObject(node);
		if (node.getNumChildren() > 0) {
			return node.getChild(0);
		}
		throw new ParseException("No children exist for node type: " +
	                              node.getParseTreeType().name());
	}

	/**
	 * Returns the last child node of a node.
	 * 
	 * @param parentNode The node to get the last child of.
	 * @return The last child node of the node.
	 * @throws ParseException if the node has no children.
	 * @throws IllegalArgumentException if the parentNode is null.
	 */
	public static ParseTree getLastChild(final ParseTree parentNode) throws ParseException {
		ObjectUtils.checkNullObject(parentNode);
		final int numChildren = parentNode.getNumChildren();
		if (numChildren == 0) {
			throw new ParseException("Node has no children - cannot get last child node [" + parentNode + ']');			
		}
		return parentNode.getChild(numChildren - 1);
	}
	
	
	/**
	 * Returns the first child from the specified childIndex of the given type, 
	 * or -1 if no such child exists, or the childIndex is out of the bounds of the parent node.
	 * 
	 * @param parentNode The node to search for children of a type in.
	 * @param childIndex The index to begin searching from.
	 * @param type The type of the child to look for.
	 * @return The index of the child within the parent node, or -1 if no such child exists.
	 * @throws IllegalArgumentException if the parentNode is null.
	 */
	public static int getChildIndexOfType(final ParseTree parentNode, final int from, final ParseTreeType type) {
		ObjectUtils.checkNullObject(parentNode);
		final int numChildren = parentNode.getNumChildren();
		if (from < numChildren && from >= 0) {
			for (int searchIndex = from; searchIndex < numChildren; searchIndex++) {
				if (parentNode.getChild(searchIndex).getParseTreeType() == type) {
					return searchIndex;
				}
			}
		}
		return -1;
	}
	
	
	/**
	 * Returns the first range value of a node passed in.  A range is defined by
	 * a node with two integer child nodes.  Each range value must be an integer
	 * in the range 0 to 255, as they define byte values, and the node passed in
	 * must have the type {@link net.byteseek.parser.tree.ParseTreeType.RANGE}.
	 * 
	 * @param rangeNode The node with two integer child nodes defining a range.
	 * @return The integer value of the first range value.
	 * @throws ParseException If a problem occurs parsing the range value,
	 *                         or the range node or value is not correct.
	 * @throws IllegalArgumentException if the rangeNode passed in is null.
	 */
	public static int getFirstRangeValue(final ParseTree rangeNode) throws ParseException {
		return getRangeValue(rangeNode, 0);
	}
	
	
	/**
	 * Returns the second range value of a node passed in.  A range is defined by
	 * a node with two integer child nodes.  Each range value must be an integer
	 * in the range 0 to 255, as they define byte values, and the node passed in
	 * must have the type {@link net.byteseek.parser.tree.ParseTreeType.RANGE}.
	 * 
	 * @param rangeNode The node with two integer child nodes defining a range.
	 * @return The integer value of the second range value.
	 * @throws ParseException If a problem occurs parsing the range value,
	 *                         or the range node or value is not correct.
	 * @throws IllegalArgumentException if the rangeNode passed in is null.
	 */
	public static int getSecondRangeValue(final ParseTree rangeNode) throws ParseException {
		return getRangeValue(rangeNode, 1);
	}
	
	
	/**
	 * Adds the bytes specified by a BYTE node (inverted or not) to a collection
	 * of bytes.
	 * 
	 * @param byteNode The BYTE node
	 * @param bytes The collection of bytes to add the byte or bytes to.
	 * @throws ParseException If the node does not contain a byte value.
	 * @throws IllegalArgumentException if the byteNode or the set of bytes are null.
	 */
	public static void addByteValues(final ParseTree byteNode, final Set<Byte> bytes) throws ParseException {
		ObjectUtils.checkNullObject(byteNode, " parameter:byteNode");
		ObjectUtils.checkNullCollection(bytes, " parameter: bytes");
		if (byteNode.isValueInverted()) {
			ByteUtils.addInvertedByteValues(byteNode.getByteValue(), bytes);
		} else {
			bytes.add(byteNode.getByteValue());
		}
	}
	
	
	/**
	 * Adds the bytes defined in a range ParseTree node to a Set<Byte> passed in.
	 * 
	 * @param range The range node passed in.
	 * @param byteSet The set of bytes to add the byte range to.
	 * @throws ParseException If the node is not a range node, or does not have correct range
	 *                         values as child nodes, or if another problem occurs parsing the node.
	 * @throws IllegalArgumentException if the range or the collection passed in are null.
	 */
	public static void addRangeBytes(final ParseTree range, Set<Byte> byteSet) throws ParseException {
		ObjectUtils.checkNullObject(range, " parameter:range");
		ObjectUtils.checkNullCollection(byteSet, " parameter:byteSet");
		if (range.isValueInverted()) {
		    ByteUtils.addBytesNotInRange(getFirstRangeValue(range), getSecondRangeValue(range), byteSet);
		} else {
			ByteUtils.addBytesInRange(getFirstRangeValue(range), getSecondRangeValue(range), byteSet);
		}
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
	 *                         {@link net.byteseek.parser.tree.ParseTreeType.REPEAT},
	 *                         {@link net.byteseek.parser.tree.ParseTreeType.REPEAT_MIN_TO_MANY},
	 *                         {@link net.byteseek.parser.tree.ParseTreeType.REPEAT_MIN_TO_MAX},
     *                         there is no first repeat value.
     * @throws IllegalArgumentException if the repeatNode passed in is null.
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
	 * 
	 * @param repeatNode The node defining a repeat.
	 * @return The second repeat value (maximum number of repeats) for the repeat node.
	 * @throws ParseException If the node passed in does not have type 
	 *                         {@link net.byteseek.parser.tree.ParseTreeType.REPEAT},
	 *                         {@link net.byteseek.parser.tree.ParseTreeType.REPEAT_MIN_TO_MAX} or
	 *                         {@link net.byteseek.parser.tree.ParseTreeType.REPEAT_MIN_TO_MAX}, or
	 *                         there is no second repeat value.
     * @throws IllegalArgumentException if the repeatNode passed in is null.
	 */	
	public static int getSecondRepeatValue(final ParseTree repeatNode) throws ParseException {
		return getRepeatValue(repeatNode, 1);
	}	
	
	
	/**
	 * Adds a collection of unique byte values matching a bitmask, where all the bits
	 * must match the bitmask.
	 * This method does not check whether the type of node passed in is actually an
	 * ALL_BITMASK.  As long as there is a byte value to the node, it will be processed
	 * as if it is an ALL_BITMASK.
	 * 
	 * @param allBitmask The all bitmask node.
	 * @param set A set of bytes to add the values to.
	 * @throws ParseException If there is no bitmask byte value or another problem occurs
	 *                         parsing the value.
	 * @throws IllegalArgumentException if the allBitmask or bytes passed in are null.
	 */
	public static void addBytesMatchingAllBitmask(final ParseTree allBitmask,
												  final Collection<Byte> bytes) throws ParseException {
		ObjectUtils.checkNullObject(allBitmask, "parameter: allBitmask");
		ObjectUtils.checkNullCollection(bytes, "parameter: bytes");
		if (allBitmask.isValueInverted()) {
			ByteUtils.addBytesNotMatchingAllBitMask(allBitmask.getByteValue(), bytes);
		} else {
			ByteUtils.addBytesMatchingAllBitMask(allBitmask.getByteValue(), bytes);
		}
	}	
	
	
	/**
	 * Adds a collection of unique byte values matching a bitmask, where any of the bits
	 * must match the bitmask (or not if the bitmask node is inverted).
	 * 
	 * This method does not check whether the type of node passed in is actually an
	 * ANY_BITMASK.  As long as there is a byte value to the node, it will be processed
	 * as if it is an ANY_BITMASK.
 	 *
	 * @param allBitmask The any bitmask node.
	 * @return A collection of bytes which match the any-bits bitmask.
	 * @throws ParseException If there is no bitmask byte value or another problem occurs
	 *                         parsing the value.
	 * @throws IllegalArgumentException if the anyBitmask or bytes passed in are null.
	 */
	public static void addBytesMatchingAnyBitmask(final ParseTree anyBitmask,
												  final Collection<Byte> bytes) throws ParseException {
		ObjectUtils.checkNullObject(anyBitmask, "parameter: anyBitmask");
		ObjectUtils.checkNullCollection(bytes, "parameter: bytes");
		if (anyBitmask.isValueInverted()) {
			ByteUtils.addBytesNotMatchingAnyBitMask(anyBitmask.getByteValue(), bytes);
		} else {
			ByteUtils.addBytesMatchingAnyBitMask(anyBitmask.getByteValue(), bytes);
		}
	}		

	
	/**
	 * Adds the bytes from a string when encoded as ISO 8859-1 bytes to a collection of bytes.
	 * 
	 * @param string The text node to get the bytes for.
	 * @param bytes The collection of Bytes to add the string bytes to.
	 * @throws ParseException If the ISO 8859-1 encoding is not supported.
	 * @throws IllegalArgumentException if the string parsetree or the collection of bytes are null.
	 */
	public static void addStringBytes(final ParseTree string,
									  final Collection<Byte> bytes) throws ParseException {
		ObjectUtils.checkNullObject(string, "parameter:string");
		ByteUtils.addStringBytes(string.getTextValue(), bytes);
	}
	
	
	/**
	 * Adds the bytes that represent all the byte values in a String when encoded as ISO 8859-1 bytes.
	 * Any lower or upper case characters will have their counterpart added to the collection, 
	 * ensuring case-insensitivity.
	 * 
	 * @param caseInsensitive The text node to get a set of case insensitive byte values for.
	 * @return A collection of bytes giving all the case insensitive bytes that string might match.
	 * @throws ParseException If the ISO 8859-1 encoding is not supported.
	 * @throws IllegalArgumentException if the string parsetree or the collection of bytes are null.
	 */
	public static void addCaseInsensitiveStringBytes(final ParseTree caseInsensitive,
													 final Collection<Byte> bytes) throws ParseException {
		ObjectUtils.checkNullObject(caseInsensitive, "parameter:caseInsensitive");
		ByteUtils.addCaseInsensitiveStringBytes(caseInsensitive.getTextValue(), bytes);
	}
	
	
	/**
	 * Calculates the set of Bytes specified by the parent set node passed in.
	 * Sets can contain bytes, strings (case sensitive & insensitive),
	 * ranges, other sets nested inside them (both normal and inverted) and
	 * bitmasks.
	 * <p>
	 * This method does not invert the set bytes returned if the set node passed in is inverted.
	 * It preserves the bytes as-defined in the set, leaving the question of whether to
	 * invert the bytes defined in the set passed in to any clients of the code.
	 * However, note that any nested sets will have their inversion status taken into account,
	 * as this affects the byte content of the parent set.  For example, a straight set containing
	 * a nested set: [01 ^[01]] would be equivalent to a set containing all the bytes.
	 * <p>
	 * If you want the set values calculating taking into account the inversion 
	 * status of the set node itself, please call the method {@link #calculateSetValues(ParseTree)}.
	 * <p>
	 * This can be recursive procedure if sets are nested within one another.
	 * 
	 * @param set The set node to calculate a set of byte values for.
	 * @return A set of byte values defined by the node.
	 * @throws ParseException If a problem occurs parsing the node.
	 * @throws IllegalArgumentException if the set passed in is null.
	 */
	public static Set<Byte> getSetValues(final ParseTree set)
			throws ParseException {
		ObjectUtils.checkNullObject(set);
		final Set<Byte> setValues = new HashSet<Byte>(64);
		for (final ParseTree valueNode : set) {
			switch (valueNode.getParseTreeType()) {
				case SET:						addSetValues(valueNode, setValues);
												break;
				case BYTE:						addByteValues(valueNode, setValues); 						
												break;
				case RANGE: 					addRangeBytes(valueNode, setValues);
												break;
				case ALL_BITMASK:				addBytesMatchingAllBitmask(valueNode, setValues);					
												break;
				case ANY_BITMASK:				addBytesMatchingAnyBitmask(valueNode, setValues);
												break;
				case STRING:					addStringBytes(valueNode, setValues);						
												break;
				case CASE_INSENSITIVE_STRING:	addCaseInsensitiveStringBytes(valueNode, setValues);			
												break;
				case ANY:						ByteUtils.addAllBytes(setValues);
											    break;
				default: 						throw new ParseException(getTypeError(valueNode));
			}
			if (setValues.size() == 256) {
				break; // don't bother adding more values if we're already at the maximum set size for Bytes.
			}
		}
		return setValues;
	}

	
	/**
	 * Adds all the set values defined by the set node passed in to a collection of Byte.
	 * If the set node is inverted, then the bytes added will be inverted from the values
	 * defined under the set node. 
	 * 
	 * @param setNode The set node defining the set of bytes to add.
	 * @param bytes The collection of bytes to add the bytes to.
	 * @throws ParseException If the byte set nodes do not define a valid set.
	 * @throws IllegalArgumentException if the setNode or bytes collection passed in are null.
	 */
	public static void addSetValues(final ParseTree setNode, 
									final Collection<Byte> bytes) throws ParseException {
		ObjectUtils.checkNullCollection(bytes, "parameter:bytes");
		bytes.addAll(calculateSetValues(setNode));		
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
	 * @param set  The set node to calculate a set of byte values for, taking into
	 *             account whether the set node is inverted or not.
	 * @return A set of byte values defined by the node.
	 * @throws ParseException If a problem occurs parsing the node.
	 * @throws IllegalArgumentException if the set passed in is null.
	 */
	public static Set<Byte> calculateSetValues(final ParseTree set) throws ParseException {
		ObjectUtils.checkNullObject(set);
		final Set<Byte> setValues = getSetValues(set);
		if (set.isValueInverted()) {
			return ByteUtils.invertedSet(setValues);
		}
		return setValues;
	}
	
	
	////////////////////////////
	// Private static methods //
	////////////////////////////
	
	/**
	 * Private utility method which gets the BYTE nominated range value,
	 * and returns it as an integer between 0 and 255.
	 * Also validates that the range node itself only has two children, 
	 * and that the range node has type ParseTreeType.RANGE.
	 * 
	 * @param rangeNode The range node to get a value for.
	 * @param valueIndex The number of the range value (first: 0 or second: 1).
	 * @return The integer value of the first or second range value.
	 * @throws ParseException If a problem occurs parsing the range value, 
	 *                         or the range node or value is not correct.
	 * @throws IllegalArgumentException if the rangeNode passed in is null.
	 */
	private static int getRangeValue(final ParseTree rangeNode, final int valueIndex) throws ParseException {
		ObjectUtils.checkNullObject(rangeNode);
		if (rangeNode.getParseTreeType() != ParseTreeType.RANGE) {
			throw new ParseException("Node is not a RANGE node.  It has type: " + rangeNode.getParseTreeType());
		}
		final int numChildren = rangeNode.getNumChildren();
		if (numChildren != 2) {
			throw new ParseException("Ranges must have two BYTE values as child nodes. " +
			                         "Actual number of children was: " + numChildren);			
		}
		final int rangeValue = rangeNode.getChild(valueIndex).getByteValue();
		return rangeValue & 0xFF;		
	}
	

	/**
	 * Private utility method which gets the nominated repeat value and validates it
	 * to make sure it is either a positive integer, or a MANY node.  Also validates that
	 * the repeat node itself has three children, and that the repeat node has type
	 * ParseTreetype.REPEAT.
	 * 
	 * @param repeatNode The repeat node to get a repeat value for.
	 * @param valueIndex the number of the repeat value (min:0, max: 1).
	 * @return An integer value of the min or max repeat.  
	 * @throws ParseException If the repeat node does not have type ParseTreeType.REPEAT,
	 * 						  ParseTreeType.REPEAT_MIN_TO_MANY, ParseTreeType.REPEAT_MIN_TO_MAX,	
	 *                        or an integer value supplied is not greater than zero.
	 * @throws IllegalArgumentException if the repeatNode passed in is null.
	 */
	private static int getRepeatValue(final ParseTree repeatNode, final int valueIndex) throws ParseException {
		ObjectUtils.checkNullObject(repeatNode);
		if (repeatNode.getParseTreeType() != ParseTreeType.REPEAT &&
			repeatNode.getParseTreeType() != ParseTreeType.REPEAT_MIN_TO_MANY &&
			repeatNode.getParseTreeType() != ParseTreeType.REPEAT_MIN_TO_MAX) {
			throw new ParseException("Node is not a REPEAT, REPEAT_MIN_TO_MANY or REPEAT_MIN_TO_MAX node.  It has type: " + repeatNode.getParseTreeType());
		}
		final ParseTree repeatValue = repeatNode.getChild(valueIndex);
	    final int intValue = repeatValue.getIntValue();
	    if (intValue < 1) {
	      throw new ParseException("Repeat integer values must be at least one. " +
	                                "Actual value was: " + intValue);
	    }
	    return intValue;
	}
	
	
	/**
	 * Returns a nicely formatted type error message given a node.
	 * 
	 * @param node The node to get a type error message for.
	 * @return A type error message for that node.
	 */
	private static String getTypeError(final ParseTree node) {
		return String.format(TYPE_ERROR, node.getParseTreeType());
	}
	
}
