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

package net.domesdaybook.parser;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashSet;
import java.util.Set;

import net.domesdaybook.parser.regex.regularExpressionParser;
import net.domesdaybook.util.bytes.ByteUtilities;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

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


	/**
	 * Returns an integer value of the specified child of the parse-tree node.
	 * The integer must be encoded in base-10, not hexadecimal or any other
	 * base.
	 * 
	 * @param treeNode
	 *            The parent node from whose children we want to extract an
	 *            integer value.
	 * @param childIndex
	 *            The index of the child to extract the integer from.
	 * @return The integer value of the specified child of the parse-tree node.
	 */
	public static int getChildIntValue(final Tree treeNode, final int childIndex) {
		final Tree childNode = treeNode.getChild(childIndex);
		return Integer.parseInt(childNode.getText(), 10);
	}

	/**
	 * Returns a string value of the specified child of the parse-tree node.
	 * 
	 * @param treeNode
	 *            The parent node from whose children we want to extract a
	 *            string value.
	 * @param childIndex
	 *            The index of the child to extract the string from.
	 * @return The string value of the specified child of the parse-tree node.
	 */
	public static String getChildStringValue(final Tree treeNode,
			final int childIndex) {
		return treeNode.getChild(childIndex).getText();
	}

	/**
	 * Gets the minimum repeat value of a repeat node in a parse-tree.
	 * 
	 * @param treeNode
	 *            the repeat node in the parse-tree.
	 * @return The minimum repeat value of the repeat node.
	 */
	public static int getMinRepeatValue(final Tree treeNode) {
		return getChildIntValue(treeNode, 0);
	}

	/**
	 * Gets the maximum repeat value of a repeat node in a parse-tree.
	 * 
	 * @param treeNode
	 *            the repeat node in the parse-tree.
	 * @return The maximum repeat value of the repeat node.
	 */
	public static int getMaxRepeatValue(final Tree treeNode) {
		return getChildIntValue(treeNode, 1);
	}

	/**
	 * Gets the node which must be repeated in the parse-tree under a parent
	 * repeat-node.
	 * 
	 * @param treeNode
	 *            the node to repeat in a repeat node.
	 * @return The node which needs to be repeated under a parent repeat node.
	 */
	public static Tree getRepeatNode(final Tree treeNode) {
		return treeNode.getChild(2);
	}

	/**
	 * Calculates a value of a set given the parent set node (or inverted set
	 * node) Sets can contain bytes, strings (case sensitive & insensitive),
	 * ranges, other sets nested inside them (both normal and inverted) and
	 * bitmasks.
	 * 
	 * This can be recursive procedure if sets are nested within one another.
	 * 
	 * @param node
	 *            The set node to calculate a set of byte values for.
	 * @return A set of byte values defined by the node.
	 * @throws ParseException
	 *             If a problem occurs parsing the node.
	 */
	public static Set<Byte> calculateSetValue(final ParseTree set)
			throws ParseException {
		final Set<Byte> setValues = new LinkedHashSet<Byte>(320);
		for (final ParseTree child : set.getChildren()) {
			switch (child.getParseTreeType().getId()) {

			// Recursively build if we have nested child sets:
			case ParseTreeType.SET_ID: {
				setValues.addAll(calculateSetValue(child));
				break;
			}

			case ParseTreeType.INVERTED_SET_ID: {
				setValues.addAll(inverseOf(calculateSetValue(child)));
				break;
			}

			// non recursive: just build values:
			case ParseTreeType.BYTE_ID: {
				setValues.add(child.getByteValue());
				break;
			}
			
			case ParseTreeType.SET_RANGE_ID:
			case ParseTreeType.ALL_BITMASK_ID:
			case ParseTreeType.ANY_BITMASK_ID:
			{
				setValues.addAll(child.getByteSetValue());
				break;
			}			
			/*
			case : {
				setValues.addAll(ByteUtilities.getBytesMatchingAllBitMask(child.getByteValue()));
				break;
			}

			 {
				setValues.addAll(ByteUtilities.getBytesMatchingAnyBitMask(child.getByteValue()));
				break;
			}
	*/
			case ParseTreeType.CASE_SENSITIVE_STRING_ID: {
				try {
					final byte[] utf8Value = child.getTextValue().getBytes("US-ASCII");
					ByteUtilities.addAll(utf8Value, setValues);
				} catch (UnsupportedEncodingException e) {
					throw new ParseException(e);
				}
				break;
			}

			case ParseTreeType.CASE_INSENSITIVE_STRING_ID: {
				final String stringValue = child.getTextValue();
				for (int charIndex = 0; charIndex < stringValue.length(); charIndex++) {
					final char charAt = stringValue.charAt(charIndex);
					if (charAt >= 'a' && charAt <= 'z') {
						setValues.add((byte) Character.toUpperCase(charAt));

					} else if (charAt >= 'A' && charAt <= 'A') {
						setValues.add((byte) Character.toLowerCase(charAt));
					}
					setValues.add((byte) charAt);
				}
				break;
			}

			default: {
				final ParseTreeType type = child.getParseTreeType();
				final String message = String.format(TYPE_ERROR, type.getId(), type.getDescription());
				throw new ParseException(message);
			}
			}
		}
		return setValues;
	}

	/**
	 * Returns a set of bytes which contains the inverse of the set of bytes
	 * passed in. All the bytes which were not in the original set will be
	 * present, and all the byte which were will not be.
	 * 
	 * @param byteSet
	 *            The set of bytes to invert.
	 * @return The inverse of the set of bytes passed in.
	 */
	public static Set<Byte> inverseOf(final Set<Byte> byteSet) {
		final Set<Byte> inverseSet = new LinkedHashSet<Byte>(320);
		for (int value = 0; value < 256; value++) {
			if (!byteSet.contains((byte) value)) {
				inverseSet.add((byte) value);
			}
		}
		return inverseSet;
	}

	/**
	 * Removes the leading and trailing character from a string. This is used to
	 * remove quotes from quoted strings.
	 * 
	 * @param str
	 *            The string to trim.
	 * @return A string without the first and last character.
	 */
	public static String unquoteString(final String str) {
		return str.substring(1, str.length() - 1);
	}

	/**
	 * Gets the string name of the type of a parse-tree node.
	 * 
	 * @param node
	 *            The node to get the type name of.
	 * @return The type name of the parse tree node.
	 */
	public static String getTokenName(final CommonTree node) {
		return regularExpressionParser.tokenNames[node.getType()];
	}

	/**
	 * Returns a "type not supported" error message for a parse-tree node.
	 * 
	 * @param node
	 *            The node to return an error message for.
	 * @return A type not supported error message for the node.
	 */
	public static String getTypeErrorMessage(final CommonTree node) {
		return String.format(TYPE_ERROR, getTokenName(node));
	}

}
