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
package net.byteseek.parser.tree;

/**
 * This enumeration defines the types of nodes which can appear in a {@link ParseTree}.
 * <p>
 * The JavaDoc for each enumeration type describes the contract that any class 
 * implementing the {@link net.byteseek.parser.tree.ParseTree} interface
 * should obey for a node of its enum type, the type being returned by a call to
 * {@link net.byteseek.parser.tree.ParseTree#getParseTreeType()}.
 * 
 * @author Matt Palmer
 */
public enum ParseTreeType {
	
	/////////////////////////////////////////////////
	// Value-specifying leaf node types            //
	//											   //
	// Have a well defined value, and no children  //
	/////////////////////////////////////////////////
		
	/**
	 * A BYTE type has a single byte value, which is accessible via a call to 
	 * {@link net.byteseek.parser.tree.ParseTree#getByteValue()}.  A call to
	 * {@link net.byteseek.parser.tree.ParseTree#getIntValue()} should return the
	 * integer value of the byte.
	 * <p>
	 * The value can be inverted, meaning it will match all other byte values.
	 * Calling {@link net.byteseek.parser.tree.ParseTree#isValueInverted()}
	 * tells you if the value of the node is inverted.
	 * <p>
	 * Implementations should throw a {@link net.byteseek.parser.ParseException} 
	 * if calls are made to {@link net.byteseek.parser.tree.ParseTree#getTextValue()}.
	 * <p>
	 * A BYTE type has no children, and must return an empty list of child nodes if 
	 * {@link net.byteseek.parser.tree.ParseTree#getChildren()} is called.
	 */
    BYTE,
    
    /**
     * An INTEGER type has a single integer value, which is accessible via a call to 
	 * {@link net.byteseek.parser.tree.ParseTree#getIntValue()}.
	 * <p>
	 * The value can not be inverted.
	 * Calling {@link net.byteseek.parser.tree.ParseTree#isValueInverted()}
	 * will always return false.
	 * <p>
	 * Implementations should throw a {@link net.byteseek.parser.ParseException} 
	 * if calls are made to either {@link net.byteseek.parser.tree.ParseTree#getByteValue()} or
	 * {@link net.byteseek.parser.tree.ParseTree#getTextValue()}.
	 * <p>
	 * An INTEGER type has no children, and must return an empty list of child nodes if 
	 * {@link net.byteseek.parser.tree.ParseTree#getChildren()} is called.
	 */
	INTEGER,    

	/**
	 * An ALL_BITMASK type defines a bit-mask for which all the bits must match.
	 * It has a single byte value, which is accessible via a call to
     * {@link net.byteseek.parser.tree.ParseTree#getByteValue()}.
	 * <p>
	 * The value can be inverted, meaning it will match all other byte values.
	 * Calling {@link net.byteseek.parser.tree.ParseTree#isValueInverted()}
	 * tells you if the value of the node is inverted.
	 * <p>
	 * Implementations should throw a {@link net.byteseek.parser.ParseException} 
	 * if calls are made to either {@link net.byteseek.parser.tree.ParseTree#getIntValue()} or
	 * {@link net.byteseek.parser.tree.ParseTree#getTextValue()}.
	 * <p>
	 * An ALL_BITMASK type has no children, and must return an empty list of child nodes if 
	 * {@link net.byteseek.parser.tree.ParseTree#getChildren()} is called.
	 */	
	ALL_BITMASK,

	/**
	 * An ANY_BITMASK type defines a bit-mask for which any of the bits must match.
	 * It has a single byte value, which is accessible via a call to
     * {@link net.byteseek.parser.tree.ParseTree#getByteValue()}.
	 * <p>
	 * The value can be inverted, meaning it will match all other byte values.
	 * Calling {@link net.byteseek.parser.tree.ParseTree#isValueInverted()}
	 * tells you if the value of the node is inverted.
	 * <p>
	 * Implementations should throw a {@link net.byteseek.parser.ParseException} 
	 * if calls are made to either {@link net.byteseek.parser.tree.ParseTree#getIntValue()} or
	 * {@link net.byteseek.parser.tree.ParseTree#getTextValue()}.
	 * <p>
	 * An ANY_BITMASK type has no children, and must return an empty list of child nodes if 
	 * {@link net.byteseek.parser.tree.ParseTree#getChildren()} is called.
	 */		
	ANY_BITMASK,

	/**
	 * An ANY type defines a wild-card node which matches all bytes.
	 * <p>
	 * The value can be inverted, meaning it will match no byte values.
	 * Calling {@link net.byteseek.parser.tree.ParseTree#isValueInverted()}
	 * tells you if the value of the node is inverted.  
	 * <p>
	 * Note that some compilers may throw an exception when an inverted ANY node is encountered, 
	 * if they cannot produce something which never matches anything.  
	 * All of the current compilers in the byteseek library do this. 
	 * <p>
	 * Implementations should throw a {@link net.byteseek.parser.ParseException} 
	 * if calls are made to either {@link net.byteseek.parser.tree.ParseTree#getIntValue()},
	 * {@link net.byteseek.parser.tree.ParseTree#getTextValue()} or 
	 * {@link net.byteseek.parser.tree.ParseTree#getByteValue()}.
	 * <p>
	 * An ANY type has no children, and must return an empty list of child nodes if 
	 * {@link net.byteseek.parser.tree.ParseTree#getChildren()} is called.
	 */		
	ANY,

	/**
	 * A STRING type represents a sequence of bytes, expressed as a java String value.
	 * The text value of a STRING type is accessible by a call to 
	 * {@link net.byteseek.parser.tree.ParseTree#getTextValue()}. 
	 * <p>
	 * The value can not be inverted.
	 * Calling {@link net.byteseek.parser.tree.ParseTree#isValueInverted()}
	 * will always return false.
	 * <p>
	 * The intention of this type is to make it easy to specify ASCII-style (single-byte encoded) text values
	 * as byte strings to match.  However, there is no reason why other encodings (e.g. UTF-8 or UTF-16) could
	 * not be used to convert to a byte sequence.
	 * <p>
	 * Note that matching multi-byte encoded text as bytes may not match text in the same way that
	 * a text-oriented match would.  Some multi-byte encodings allow different ways of encoding the same
	 * text; matching as a byte-sequence will only match the specific way it was decoded.
	 * However, the STRING type says nothing about what character encoding is used to convert
	 * the string of text to a sequence of bytes.  Those decisions are left to (or constrained by) the
	 * particular parsers and compilers that work with this type.
	 * <p>
	 * Implementations should throw a {@link net.byteseek.parser.ParseException} if calls are made to 
	 * {@link net.byteseek.parser.tree.ParseTree#getIntValue()} or
	 * {@link net.byteseek.parser.tree.ParseTree#getByteValue()}.  
	 * <p>
	 * A STRING type has no children, and must return an empty list of child nodes if 
	 * {@link net.byteseek.parser.tree.ParseTree#getChildren()} is called.
	 */
	STRING,

	/**
	 * A CASE_INSENSITIVE_STRING type represents a sequence of bytes, expressed as a java String value.
	 * The text value of a CASE_INSENSITIVE_STRING type is accessible by a call to 
	 * {@link net.byteseek.parser.tree.ParseTree#getTextValue()}. 
	 * <p>
	 * The value can not be inverted.
	 * Calling {@link net.byteseek.parser.tree.ParseTree#isValueInverted()}
	 * will always return false.
	 * <p>
	 * The intention of this type is to make it easy to specify ASCII-style (single-byte encoded) text values
	 * as byte strings to match, where lower and upper case will match equivalently.
	 * <p>
	 * Implementations should throw a {@link net.byteseek.parser.ParseException} if calls are made to 
	 * {@link net.byteseek.parser.tree.ParseTree#getIntValue()} or
	 * {@link net.byteseek.parser.tree.ParseTree#getByteValue()}.  
	 * <p>
	 * A STRING type has no children, and must return an empty list of child nodes if 
	 * {@link net.byteseek.parser.tree.ParseTree#getChildren()} is called.
	 */
	CASE_INSENSITIVE_STRING,
	
	
	////////////////////////////////////////////////
    // Value-specifying parent node types         //
	// 											  //
	// No direct value, but have child ParseTree  //
	// nodes that define a set of byte values.    //
	////////////////////////////////////////////////

	/**
	 * A RANGE type defines a contiguous, inclusive range of byte values.
	 * <p>
	 * The value can be inverted, meaning it will match bytes outside of the range.
	 * Calling {@link net.byteseek.parser.tree.ParseTree#isValueInverted()}
	 * tells you if the value of the range is inverted.  
	 * <p>
	 * Implementations should throw a {@link net.byteseek.parser.ParseException} 
	 * if calls are made to either {@link net.byteseek.parser.tree.ParseTree#getIntValue()},
	 * {@link net.byteseek.parser.tree.ParseTree#getTextValue()} or 
	 * {@link net.byteseek.parser.tree.ParseTree#getByteValue()}.
	 * <p>
	 * FIXME: now has two int nodes in the range 0 - 255.  The syntax that produces
	 * a range is independant of its meaning.
	 *  
	 * It has two child INTEGER nodes defining an inclusive range of values between 0 and 255.  
	 * <p><blockquote><pre><code>
	 *    RANGE
	 *     /&nbsp;&nbsp;&nbsp;&nbsp;\
	 *  INTEGER INTEGER
	 *   32        254
	 * </code></pre></blockquote><p><p>
	 * The range does not have to be specified in any particular order - a bigger value 
	 * can appear before a smaller one, or vice versa.  
	 */
	RANGE,
	
	/**
	 * A SET type defines an arbitrary set of byte values to match.
	 * Values are provided by the union of all byte values specified by the children of the set node.
	 * <p>
	 * The value can be inverted, meaning it will only match bytes not in the set.
	 * Calling {@link net.byteseek.parser.tree.ParseTree#isValueInverted()}
	 * tells you if the value of the set is inverted.  
	 * <p>
	 * Implementations should throw a {@link net.byteseek.parser.ParseException} 
	 * if calls are made to either {@link net.byteseek.parser.tree.ParseTree#getIntValue()},
	 * {@link net.byteseek.parser.tree.ParseTree#getTextValue()} or 
	 * {@link net.byteseek.parser.tree.ParseTree#getByteValue()}.
	 * <p> 
	 * It has one or more child nodes defining the value of the set. These child nodes can be
	 * of any type which ultimately define some byte values, i.e. all other value-specifying
	 * node types except INTEGER, and including other sets.  For example:
	 * <p><blockquote><pre><code>
	 * SET
	 *  |__ BYTE (0x20)
	 *  |__ BYTE (0x09)
	 *  |__ BYTE (0x0d)
	 *  |__ SET
	 *  |    |__ RANGE (0x41) - (0x61)
	 *  |    |__ BYTE  (0x7f)
	 *  |__ STRING ('£$%&')
	 * </code></pre></blockquote><p><p>
	 * A set with no children has no bytes it can match, unless inverted, in which case it is
     * functionally equivalent to the ANY type.
	 * Some compilers may throw an exception if asked to produce a matcher that never matches anything.
	 * All of the compilers in the byteseek library will do this.
	 */
	SET,
	
	
	/////////////////////////////////////////////
	// Imperative parent node types            //
	//                                         //
	// Has no byte values itself.              //
	// Cannot be inverted.                     //
	// Specifies what to do with child nodes.  //
	/////////////////////////////////////////////
	
	/**
	 * A SEQUENCE type defines an ordered sequence of other nodes to match.
	 * The nodes to match are the ordered list of child nodes of the SEQUENCE, and they
	 * can be of any other type of node, including other sequences.
	 * <p>
	 * Note that in regular expression syntax trees, it is common to use a tree structure 
	 * of left/right nodes, where sequences are represented as a deeply nested sequence of
	 * Join nodes.  For example, the sequence 'abcd' would appear as a tree structure like 
	 * this (using J to mean a left/right join node):
	 * <p><blockquote><pre><code>
	 *     J
	 *    / \
	 *   a   J
	 *      / \
	 *     b   J
	 *        / \
	 *       c   d 
	 * </code></pre></blockquote><p>
	 * Using a SEQUENCE node like a join node above will still work fine, since sequences can contain
	 * other sequences. However, since we allow ParseTrees to have multiple children in an ordered list, 
	 * rather than only a left and a right child node, the SEQUENCE node can represent the same more directly as:
	 * <p><blockquote><pre><code>
	 *   SEQUENCE
	 *   |&nbsp;&nbsp;|&nbsp;&nbsp;|&nbsp;&nbsp;|
	 *   a b c d
	 * </code></pre></blockquote><p><p>
	 * A sequence can not be inverted, hence calls to {@link net.byteseek.parser.tree.ParseTree#isValueInverted()}
	 * must always return false.
	 * <p>
	 * Since sequences do not have a direct value, implementations should also throw a  
	 * {@link net.byteseek.parser.ParseException}
	 * if calls are made to either {@link net.byteseek.parser.tree.ParseTree#getIntValue()},
	 * {@link net.byteseek.parser.tree.ParseTree#getTextValue()} or 
	 * {@link net.byteseek.parser.tree.ParseTree#getByteValue()}.
	 * <p>
	 * The child nodes of a sequence are the ParseTrees to match in the order they appear.
	 * There must be at least one child node, although note that a sequence with a single child node
	 * can be directly replaced by that child node.
	 */
	SEQUENCE,


	/**
	 * A REPEAT node repeats another node a fixed number of times.  The number of times is given by 
	 * the first child node of the repeat node, which is an INTEGER node.  The second child node of
	 * the repeat node is the node to repeat, and this can be any other type of node.
	 * For example, a node that repeats the byte 0x09 seven times is represented like this:
	 * <p><blockquote><pre><code>
	 * REPEAT
	 *  |__ INTEGER (7)
	 *  |__ BYTE (0x09)
	 * </code></pre></blockquote><p><p>
	 * <p>
	 * A repeat node can not be inverted, hence calls to {@link net.byteseek.parser.tree.ParseTree#isValueInverted()}
	 * must always return false.
	 * <p>
	 * Since repeats do not have a direct value, implementations should also throw a  
	 * {@link net.byteseek.parser.ParseException}
	 * if calls are made to either {@link net.byteseek.parser.tree.ParseTree#getIntValue()},
	 * {@link net.byteseek.parser.tree.ParseTree#getTextValue()} or 
	 * {@link net.byteseek.parser.tree.ParseTree#getByteValue()} or
	 * {@link net.byteseek.parser.tree.ParseTree#getIntValue()}.
 	 */
	REPEAT,
	
	/**
	 * A REPEAT_MIN_TO_MANY node repeats another node at least a fixed number of times, but can repeat more than this.
	 * The minimum number of times is given by the first child node of the repeat node, which is an INTEGER node.  
	 * The second child node of the repeat node is the node to repeat, and this can be any other type of node.
	 * For example, a node that repeats the byte 0x09  at least seven times is represented like this:
	 * <p><blockquote><pre><code>
	 * REPEAT
	 *  |__ INTEGER (7)
	 *  |__ BYTE (0x09)
	 * </code></pre></blockquote><p><p>
	 * <p>
	 * A REPEAT_MIN_TO_MANY node can not be inverted, hence calls to {@link net.byteseek.parser.tree.ParseTree#isValueInverted()}
	 * must always return false.
	 * <p>
	 * Since REPEAT_MIN_TO_MANY nodes do not have a direct value, implementations should also throw a  
	 * {@link net.byteseek.parser.ParseException}
	 * if calls are made to either {@link net.byteseek.parser.tree.ParseTree#getIntValue()},
	 * {@link net.byteseek.parser.tree.ParseTree#getTextValue()} or 
	 * {@link net.byteseek.parser.tree.ParseTree#getByteValue()} or
	 * {@link net.byteseek.parser.tree.ParseTree#getIntValue()}.
 	 */
	REPEAT_MIN_TO_MANY,


	/**
	 * A REPEAT_MIN_TO_MAX node repeats another node at least a fixed number of times, but can repeat up to a maximum
	 * number of times.  The minimum number of times is given by the first child node of the repeat node, which is an INTEGER node.
	 * The maximum number of times is given by the second child node of the repeat node, which is an INTEGER node.  
	 * The third child node of the repeat node is the node to repeat, and this can be any other type of node.
	 * For example, a node that repeats the byte 0x09  at least seven times and no more than 12 times is represented like this:
	 * <p><blockquote><pre><code>
	 * REPEAT
	 *  |__ INTEGER (7)
	 *  |__ INTEGER (12)  
	 *  |__ BYTE (0x09)
	 * </code></pre></blockquote><p><p>
	 * <p>
	 * A REPEAT_MIN_TO_MAX node can not be inverted, hence calls to {@link net.byteseek.parser.tree.ParseTree#isValueInverted()}
	 * must always return false.
	 * <p>
	 * Since REPEAT_MIN_TO_MAX nodes do not have a direct value, implementations should also throw a  
	 * {@link net.byteseek.parser.ParseException}
	 * if calls are made to either {@link net.byteseek.parser.tree.ParseTree#getIntValue()},
	 * {@link net.byteseek.parser.tree.ParseTree#getTextValue()} or 
	 * {@link net.byteseek.parser.tree.ParseTree#getByteValue()} or
	 * {@link net.byteseek.parser.tree.ParseTree#getIntValue()}.
 	 */
	REPEAT_MIN_TO_MAX,
	
	
	/**
	 * An ALTERNATIVES type specifies that each of its children should be treated as alternatives.
	 * <p>
	 * The value can not be inverted. 
	 * Calling {@link net.byteseek.parser.tree.ParseTree#isValueInverted()}
	 * will always return false.
	 * <p>
	 * Since an alternatives node has no direct value, they should also throw this exception if any of 
	 * {@link net.byteseek.parser.tree.ParseTree#getIntValue()},
	 * {@link net.byteseek.parser.tree.ParseTree#getTextValue()} or 
	 * {@link net.byteseek.parser.tree.ParseTree#getByteValue()}. are called.
	 * <p>
	 * The child nodes of an ALTERNATIVES node are the set of ParseTrees to match, any of which can match.
	 * No particular order is implied by being in a list of children.
	 * An ALTERNATIVES node must have at least one child.
	 */
	ALTERNATIVES,
	
	/**
	 * A ZERO_TO_MANY type specifies that its single child node should appear zero to many times.
	 * <p>
	 * The value can not be inverted.
	 * Calling {@link net.byteseek.parser.tree.ParseTree#isValueInverted()}
	 * will always return false.
	 * <p> 
	 * Since a ZERO_TO_MANY node has no direct value, they should also throw this exception if any of 
	 * {@link net.byteseek.parser.tree.ParseTree#getIntValue()},
	 * {@link net.byteseek.parser.tree.ParseTree#getTextValue()} or 
	 * {@link net.byteseek.parser.tree.ParseTree#getByteValue()}. are called.
	 * <p> 
	 * A ZERO_TO_MANY node has a single child node, which is the ParseTree to be matched from zero to many times.
	 * A ParseException should be thrown if a ZERO_TO_MANY node has anything other than a single child node.
	 */
	ZERO_TO_MANY,

	/**
	 * A ONE_TO_MANY type specifies that its single child node should appear one to many times.
	 * <p>
	 * The value can not be inverted.
	 * Calling {@link net.byteseek.parser.tree.ParseTree#isValueInverted()}
	 * will always return false.
	 * <p> 
	 * Since a ONE_TO_MANY node has no direct value, they should also throw this exception if any of 
	 * {@link net.byteseek.parser.tree.ParseTree#getIntValue()},
	 * {@link net.byteseek.parser.tree.ParseTree#getTextValue()} or 
	 * {@link net.byteseek.parser.tree.ParseTree#getByteValue()}. are called.
	 * <p> 
	 * A ONE_TO_MANY node has a single child node, which is the ParseTree to be matched from one to many times.
	 * A ParseException should be thrown if a ONE_TO_MANY node has anything other than a single child node.
	 */	
	ONE_TO_MANY,
	
	/**
	 * An OPTIONAL type specifies that its single child node may or may not appear.  
	 * This is functionally equivalent to being repeated zero to one times.
	 * <p>
	 * The value can not be inverted.
	 * Calling {@link net.byteseek.parser.tree.ParseTree#isValueInverted()}
	 * will always return false.
	 * <p> 
	 * Since an OPTIONAL node has no direct value, they should also throw this exception if any of 
	 * {@link net.byteseek.parser.tree.ParseTree#getIntValue()},
	 * {@link net.byteseek.parser.tree.ParseTree#getTextValue()} or 
	 * {@link net.byteseek.parser.tree.ParseTree#getByteValue()}. are called.
	 * <p> 
	 * An OPTIONAL node has a single child node, which is the ParseTree to be matched from zero to one times.
	 * A ParseException should be thrown if an OPTIONAL node has anything other than a single child node.
	 */		
  	OPTIONAL;
    

}

