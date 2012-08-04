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
package net.domesdaybook.parser.tree;

import java.util.List;

import net.domesdaybook.parser.ParseException;
import net.domesdaybook.parser.Parser;


/**
 * An interface for an Abstract Syntax Tree (AST) produced by a {@link Parser},
 * <p>
 * Each instance of this interface represents a node in the AST.  Nodes can 
 * be roughly divided into value-carrying nodes (which represent a byte value
 * or values to match), and structural nodes (which represent instructions to
 * perform operations on the child nodes or to otherwise interpret their value).
 * <p>
 * In general, value carrying nodes are leaf-nodes (which have no child nodes), and
 * structural nodes have children, but no direct value.  A value carrying node 
 * may optionally be 'inverted', in which case the value it possesses should be
 * inverted to match.  For example, a single byte value node with the value 'FF',
 * which is also inverted should match everything except 'FF'.
 * 
 * @author Matt Palmer
 *
 */
public interface ParseTree {

  /**
   * Returns the type of the ParseTree node.  Each type of node 
   * must specify the expected implementation of the other methods in 
   * this interface, in its JavaDoc.
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
	 *         problem occurs parsing the value.
	 */
	public byte getByteValue() throws ParseException;

	/**
	 * Returns an integer representing the value of this node,
	 * or throws a ParseException if no such byte value exists,
	 * or another problem occurs parsing the value.
	 * 
	 * @return An integer value of this node.
	 * @throws ParseException If not such integer value exists or another
	 *         problem occurs parsing the value.
	 */
	public int getIntValue() throws ParseException;

	public String getTextValue() throws ParseException;
	
	public boolean isValueInverted() throws ParseException;

	public List<ParseTree> getChildren();

}

