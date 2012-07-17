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

package net.domesdaybook.parser.ast;

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
			throw new ParseException("Could not parse into a hex byte:" + hexByte);
		}
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
			final String message = "Ranges must have two integer values as child nodes.  Actual number of children was %d";
			throw new ParseException(String.format(message, rangeChildren.size()));			
		}
		final int rangeValue = rangeChildren.get(0).getIntValue();
		if (rangeValue < 0 || rangeValue > 255) {
			final String message = "Range values must be between 0 and 255.  Actual value was: %d";
			throw new ParseException(String.format(message, rangeValue));
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
		if (repeatChildren.size() != 2) {
			final String message = "Repeats must have three child nodes.  Actual number of children was %d";
			throw new ParseException(String.format(message, repeatChildren.size()));			
		}
		return repeatChildren.get(2);
	}
	
	private static int getRepeatValue(final ParseTree repeatNode, final int valueIndex) throws ParseException {
		final List<ParseTree> repeatChildren = repeatNode.getChildren();
		if (repeatChildren.size() != 2) {
			final String message = "Repeats must have three child nodes.  Actual number of children was %d";
			throw new ParseException(String.format(message, repeatChildren.size()));			
		}
		final int repeatValue = repeatChildren.get(0).getIntValue();
		if (repeatValue < 1) {
			final String message = "Repeat values must be at least one.  Actual value was: %d";
			throw new ParseException(String.format(message, repeatValue));
		}
		return repeatValue;			
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

			// Recursively build if we have nested child sets:
			case SET: {
				setValues.addAll(calculateSetValues(child));
				break;
			}

			// non recursive: just build values:
			case BYTE: {
				setValues.add(child.getByteValue());
				break;
			}
			
			case RANGE: {
				setValues.addAll(getRangeValues(child));
				break;
			}
			
			case ALL_BITMASK: {
				setValues.addAll(getAllBitmaskValues(child));
				break;
			}

			case ANY_BITMASK: {
				setValues.addAll(getAnyBitmaskValues(child));
				break;
			}
			case CASE_SENSITIVE_STRING: {
				setValues.addAll(getStringAsSet(child));
				break;
			}

			case CASE_INSENSITIVE_STRING: {
				setValues.addAll(getCaseInsensitiveStringAsSet(child));
				break;
			}

			default: {
				final ParseTreeType type = child.getParseTreeType();
				final String message = String.format(TYPE_ERROR, type, type.getDescription());
				throw new ParseException(message);
			}
			}
		}
		return setValues;
	}



}
