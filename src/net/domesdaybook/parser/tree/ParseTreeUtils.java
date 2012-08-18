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

	/**
     * 
     */
	public static final String TYPE_ERROR = "Parse tree type id [&d] with description [%s] is not supported by the parser.";
	/**
     * 
     */
	public static final String QUOTE = "\'";

	private ParseTreeUtils() {
	}

	/**
	 * Returns a byte from its hexadecimal string representation.
	 * 
	 * @param hexByte
	 *            a hexadecimal representation of a byte.
	 * @return the byte encoded by the hex representation.
	 * @throws ParseException if the string cannot be parsed.
	 */
	public static byte parseHexByte(final String hexByte) throws ParseException {
		try {
			return (byte) Integer.parseInt(hexByte, 16);
		} catch (NumberFormatException nfe) {
			throw new ParseException("Could not parse into a hex byte: " + hexByte);
		}
	}
	
	public static ParseTree getFirstChild(final ParseTree node) throws ParseException {
	  final List<ParseTree> children = node.getChildren();
	  if (children.size() > 0) {
	    return children.get(0);
	  }
	  throw new ParseException("No children exist for node type: " +
	                           node.getParseTreeType().name());
	}

	public static int getFirstRangeValue(final ParseTree rangeNode) throws ParseException {
		return getRangeValue(rangeNode, 0);
	}
	
	public static int getSecondRangeValue(final ParseTree rangeNode) throws ParseException {
		return getRangeValue(rangeNode, 1);
	}	
	
	private static int getRangeValue(final ParseTree rangeNode, final int valueIndex) throws ParseException {
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
	
	public static int getFirstRepeatValue(final ParseTree repeatNode) throws ParseException {
		return getRepeatValue(repeatNode, 0);
	}
	
	public static int getSecondRepeatValue(final ParseTree repeatNode) throws ParseException {
		return getRepeatValue(repeatNode, 1);
	}	
	
	public static ParseTree getNodeToRepeat(final ParseTree repeatNode) throws ParseException {
		final List<ParseTree> repeatChildren = repeatNode.getChildren();
		if (repeatChildren.size() != 3) {
			throw new ParseException("Repeats must have three child nodes. " +
			                          "Actual number of children was: " + repeatChildren.size());			
		}
		return repeatChildren.get(2);
	}
	
	private static int getRepeatValue(final ParseTree repeatNode, final int valueIndex) throws ParseException {
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
		return -1; //FIXME: need to test for MANY node, which doesn't exist at the moment...	
		//          But this function should return a negative number for a many node,
		//          and throw a ParseException if the node isn't an integer or a many node.
	}
	
	
	public static Collection<Byte> getRangeValues(final ParseTree range) throws ParseException {
		final int range1 = getFirstRangeValue(range);
		final int range2 = getSecondRangeValue(range);
		final Set<Byte> values = new LinkedHashSet<Byte>(64);
		ByteUtilities.addBytesInRange(range1, range2, values);
		return values;
	}	
	
	
	public static Collection<Byte> getAllBitmaskValues(final ParseTree allBitmask) throws ParseException {
		return ByteUtilities.getBytesMatchingAllBitMask(allBitmask.getByteValue());
	}	
	
	
	public static Collection<Byte> getAnyBitmaskValues(final ParseTree anyBitmask) throws ParseException {
		return ByteUtilities.getBytesMatchingAnyBitMask(anyBitmask.getByteValue());		
	}		

	
	public static Collection<Byte> getStringAsSet(final ParseTree string) throws ParseException {
		final Set<Byte> values = new LinkedHashSet<Byte>();
		try {
			final byte[] utf8Value = string.getTextValue().getBytes("US-ASCII");
			ByteUtilities.addAll(utf8Value, values);
			return values;
		} catch (UnsupportedEncodingException e) {
			throw new ParseException(e);
		}		
	}
	
	
	public static Collection<Byte> getCaseInsensitiveStringAsSet(final ParseTree caseInsensitive) throws ParseException {
		final Set<Byte> values = new LinkedHashSet<Byte>();
		final String stringValue = caseInsensitive.getTextValue();
		for (int charIndex = 0; charIndex < stringValue.length(); charIndex++) {
			final char charAt = stringValue.charAt(charIndex);
			if (charAt >= 'a' && charAt <= 'z') {
				values.add((byte) Character.toUpperCase(charAt));

			} else if (charAt >= 'A' && charAt <= 'A') {
				values.add((byte) Character.toLowerCase(charAt));
			}
			values.add((byte) charAt);
		}
		return values;
	}
	
	
	public static Set<Byte> calculateSetValues(final ParseTree set) throws ParseException {
		final Set<Byte> setValues = getSetValues(set);
		if (set.isValueInverted()) {
			return ByteUtilities.invertedSet(setValues);
		}
		return setValues;
	}
	
	
	/**
	 * Calculates a value of a set given the parent set node (or inverted set
	 * node) Sets can contain bytes, strings (case sensitive & insensitive),
	 * ranges, other sets nested inside them (both normal and inverted) and
	 * bitmasks.
	 * <p>
	 * This method does not invert the set bytes returned if the root set node is inverted.
	 * It preserves the bytes as-defined in the set, leaving the question of whether to
	 * invert the bytes defined in the set passed in to any clients of the code.
	 * 
	 * This can be recursive procedure if sets are nested within one another.
	 * 
	 * @param node
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
				case SEQUENCE:
				case ALTERNATIVES:
				case SET:						setValues.addAll(calculateSetValues(child)); 			break;
				case BYTE: 						setValues.add(child.getByteValue());  					break;
				case RANGE: 					setValues.addAll(getRangeValues(child));				break;
				case ALL_BITMASK:				setValues.addAll(getAllBitmaskValues(child));			break;
				case ANY_BITMASK:				setValues.addAll(getAnyBitmaskValues(child));			break;
				case CASE_SENSITIVE_STRING:		setValues.addAll(getStringAsSet(child));				break;
				case CASE_INSENSITIVE_STRING:	setValues.addAll(getCaseInsensitiveStringAsSet(child));	break;
	
				default: throw new ParseException(getTypeError(child));
			}
		}
		return setValues;
	}

	
	private static String getTypeError(final ParseTree node) {
		final ParseTreeType type = node.getParseTreeType();
		return String.format(TYPE_ERROR, type, type.getDescription());
	}
	
	
	/**
	 * Applies three optimisations to a parse tree:
	 * 
	 * 1) All single byte alternatives directly replaced by a set:
	 *    A list of alternatives each of which matches only a single byte is turned directly into a set of bytes,
	 *    losing the original alternatives node entirely.
	 *    For example, ALT(01 | 02 | 03) is the same as Set{01 02 03}.
	 * 2) Some single byte alternatives merged into a child set:
	 *    A list of alternatives where only some of them match a single byte turns only those alternatives into 
	 *    a single set of bytes under the original alternatives node.
	 *    For example, ALT(01 | 'a sequence' | 02 03 04) is the same as ALT(Set{01 02 03 04} | 'a sequence')
	 * 3) Nested sequences, sets or alternatives:
	 *    A sequence, set or alternatives node whose parent is another node of the same type can simply
	 *    add its children to its parent in place of itself (assuming they have the same inversion).
	 *    For example a Set{01 Set{02 03} 04} is the same as the simpler Set{01 02 03 04}, or
	 *    a Sequence['w', Sequence['x', 'y'], 'z'] is the same as Sequence['w', 'x', 'y', 'z']
	 * 
	 * @param node The node to optimise.
	 * @return A node which is optimised (including optimising its children).
	 */
	public ParseTree optimiseTree(ParseTree node) {
		//TODO: do we even need to optimise at the parse tree level?
		//       The alternatives -> set optimisations are useful, but a compiler could do that from the parse 
		//       tree directly, where it made sense to do so.
		//       Optimising the nesting only optimises the look of the parse tree, since a
		//       compiler should be able to deal with such nesting in any case (as optimisation is not guaranteed
		//       by all parsers).  
		
		//NOTE:  'optimising' alternatives into sets is now handled automatically by the bytematcher and
		//       sequencematcher compilers directly and more efficiently, by just processing the children of
		//       an alternatives node as if it was a set node. 
		// 
		//       The regex compiler will still have to do something different here.
		// 
		//       As far as nested sequences, sets or alternatives go, these are already directly compiled
		//       by processing the parse tree.  The only good effect of doing it here is to produce nicer
		//       parse trees for display, but manipulating the parse tree before compilation is probably no
		//       faster and is possibly slower than just compiling the values directly.  The only exception
		//       might be if it were more efficient to match something represented by the structure of the
		//       parse tree (e.g. a set of two ranges) rather than collapsing all the values up into the root set.
		
	}
	
	
//	/**
//	 * Returns an integer value of the specified child of the parse-tree node.
//	 * The integer must be encoded in base-10, not hexadecimal or any other
//	 * base.
//	 * 
//	 * @param treeNode
//	 *            The parent node from whose children we want to extract an
//	 *            integer value.
//	 * @param childIndex
//	 *            The index of the child to extract the integer from.
//	 * @return The integer value of the specified child of the parse-tree node.
//	 */
//	public static int getChildIntValue(final Tree treeNode, final int childIndex) {
//		final Tree childNode = treeNode.getChild(childIndex);
//		return Integer.parseInt(childNode.getText(), 10);
//	}
//
//	/**
//	 * Returns a string value of the specified child of the parse-tree node.
//	 * 
//	 * @param treeNode
//	 *            The parent node from whose children we want to extract a
//	 *            string value.
//	 * @param childIndex
//	 *            The index of the child to extract the string from.
//	 * @return The string value of the specified child of the parse-tree node.
//	 */
//	public static String getChildStringValue(final Tree treeNode,
//			final int childIndex) {
//		return treeNode.getChild(childIndex).getText();
//	}
//
//	/**
//	 * Gets the minimum repeat value of a repeat node in a parse-tree.
//	 * 
//	 * @param treeNode
//	 *            the repeat node in the parse-tree.
//	 * @return The minimum repeat value of the repeat node.
//	 */
//	public static int getMinRepeatValue(final Tree treeNode) {
//		return getChildIntValue(treeNode, 0);
//	}
//
//	/**
//	 * Gets the maximum repeat value of a repeat node in a parse-tree.
//	 * 
//	 * @param treeNode
//	 *            the repeat node in the parse-tree.
//	 * @return The maximum repeat value of the repeat node.
//	 */
//	public static int getMaxRepeatValue(final Tree treeNode) {
//		return getChildIntValue(treeNode, 1);
//	}
//
//	/**
//	 * Gets the node which must be repeated in the parse-tree under a parent
//	 * repeat-node.
//	 * 
//	 * @param treeNode
//	 *            the node to repeat in a repeat node.
//	 * @return The node which needs to be repeated under a parent repeat node.
//	 */
//	public static Tree getRepeatNode(final Tree treeNode) {
//		return treeNode.getChild(2);
//	}	
	

}
