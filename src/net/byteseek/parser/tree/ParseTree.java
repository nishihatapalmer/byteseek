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

import java.util.List;

import net.byteseek.parser.ParseException;
import net.byteseek.parser.Parser;


/**
 * An interface for an Abstract Syntax Tree (AST) produced by a {@link Parser},
 * <p>
 * Each instance of this interface represents a node in the AST.  Nodes can 
 * be roughly divided into value-carrying nodes (which represent a byte, integer
 * or text value to match), and structural nodes (which represent instructions to
 * perform operations on the child nodes or to otherwise interpret their value).
 * <p>
 * In general, value carrying nodes are leaf-nodes (which have no child nodes), and
 * structural nodes have children, but no direct value. Nodes
 * may optionally be 'inverted', in which case the value it possesses should be
 * inverted to match.  For example, a single byte value node with the value 'FF',
 * which is also inverted should match everything except 'FF'.  Note that inversion
 * doesn't make sense for integer or text nodes.
 * 
 * @author Matt Palmer
 *
 */
public interface ParseTree {

  /**
   * Returns the type of the ParseTree node. The {@link ParseTreeType} of a
   * node defines the expected implementation of the methods in 
   * this interface, which is specified in the JavaDoc for each ParseTreeType.
   * 
   * @return ParseTreeType The type of the ParseTreeNode.
   */
	public ParseTreeType getParseTreeType();

	/**
	 * Returns a byte representing the value of this node, 
	 * or throws a ParseException if no such byte value exists, or
	 * another problem occurs parsing the value.
	 * 
	 * @return A byte value of this node.
	 * @throws ParseException If no such byte value exists or another 
	 *          problem occurs parsing the value.
	 */
	public byte getByteValue() throws ParseException;

	/**
	 * Returns an integer representing the value of this node,
	 * or throws a ParseException if no such int value exists,
	 * or another problem occurs parsing the value.
	 * 
	 * @return An integer value of this node.
	 * @throws ParseException If no such integer value exists or another
	 *          problem occurs parsing the value.
	 */
	public int getIntValue() throws ParseException;

	/**
	 * Returns a String representing the value of this node,
	 * or throws a ParseException if no such String value exists,
	 * or another problem occurs parsing the value.
	 * 
	 * @return A String value of this node.
	 * @throws ParseException If no such String value exists or another
	 *          problem occurs parsing the value.
	 */
	public String getTextValue() throws ParseException;
	
	/**
	 * Returns whether the value of this node should be inverted or not.
	 * 
	 * @return boolean True if the value of this node should be inverted by a compiler.
	 */
	public boolean isValueInverted();

	/**
	 * Returns a list of child nodes for this node.  If there are no child nodes,
	 * then this method will return an empty list.
	 * <p>
	 * No assumptions should be made concerning the mutability of the list of
	 * child nodes returned (an immutable list could be returned).  Equally,
	 * no assumptions should be made as to whether the list has been defensively
	 * copied by the ParseTree or not.  It may be mutable, but an internal list.
	 * <p>
	 * Extreme care must be taken if deciding the modify a list returned by this method,
	 * and in general should not be done unless you are very sure that there will
	 * be no adverse side-effects.
	 * 
	 * @return A list of child nodes, or an empty list if there are no child nodes.
	 */
	public List<ParseTree> getChildren();

}

