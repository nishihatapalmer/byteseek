/*
 * Copyright Matt Palmer 2012-2013, All rights reserved.
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
package net.byteseek.parser.tree.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.byteseek.collections.ImmutableListIterator;
import net.byteseek.parser.tree.ParseTree;
import net.byteseek.parser.tree.ParseTreeType;

/**
 * An immutable ParseTree node which has child ParseTrees.  The value of the node,
 * if any, can be inverted.
 * <p>
 * The ParseTreeType defines what kind of children the node has and how
 * to process them.  For example, a SET type will have child nodes
 * defining the set value, and the set itself can be inverted or not.
 * 
 * @author Matt Palmer.
 */
public final class ChildrenNode extends BaseNode {

	private final List<ParseTree> children;
	private final boolean inverted; 
	
	
	/**
	 * Constructs a ChildrenNode with no children and a given type.
	 * 
	 * @param type The ParseTreeType of the node.
	 */
	public ChildrenNode(final ParseTreeType type) {
		this(type, (List<ParseTree>) null, false);
	}
	
	/**
	 * Constructs a ChildrenNode with no children, a given type,
	 * and whether the value should be inverted or not.
	 * 
	 * @param type The ParseTreeType of the node.
	 * @param isInverted Whether the value of the node is inverted or not.
	 */
	public ChildrenNode(final ParseTreeType type, final boolean isInverted) {
		this(type, (List<ParseTree>) null, isInverted);
	}
	
	/**
	 * Constructs a ChildrenNode with a given type, copying the list of children passed in.
	 * <p>
	 * 
	 * @param type The ParseTreeType of this ChildrenNode.
	 * @param children The list of child ParseTrees for this ChildrenNode.
	 */
	public ChildrenNode(final ParseTreeType type, final List<ParseTree> children) {
		this(type, children, false);
	}
	
	/**
	 * Constructs an uninverted ChildrenNode with a given type, adding the parse tree passed in as a child.
	 * <p>
	 * 
	 * @param type The ParseTreeType of this ChildrenNode.
	 * @param child The ParseTree to be made the child of this node.
	 */
	public ChildrenNode(final ParseTreeType type, final ParseTree child) {
		this(type, child, false);
	}

	/**
	 * Constructs a ChildrenNode with a given type and inversion, adding the child to it.
	 * <p>
	 * 
	 * @param type The ParseTreeType of this ChildrenNode.
	 * @param children The list of child ParseTrees for this ChildrenNode.
	 */
	public ChildrenNode(final ParseTreeType type, final ParseTree child, final boolean inverted) {
		super(type);
		if (child == null) {
			this.children = new ArrayList<ParseTree>(0);
		} else {
			this.children = new ArrayList<ParseTree>(1);
			this.children.add(child);
		}
		this.inverted = inverted;
	}
	

	/**
	 * Constructs a ChildreNode, not inverted, and taking all the ParseTree
	 * parameters provided as children of the node.
	 * 
	 * @param type The type of the ChildrenNOde
	 * @param parseTrees The children of this node.
	 */
	public ChildrenNode(final ParseTreeType type, final ParseTree...parseTrees) {
		this(type, false, parseTrees);
	}

	
	
	/**
	 * Constructs a ChildreNode with the inversion status passed in, and taking all the ParseTree
	 * parameters provided as children of the node.
	 * 
	 * @param type The type of the ChildrenNOde
	 * @param inverted Whether the node is inverted or not.
	 * @param parseTrees The children of this node.
	 */
	public ChildrenNode(final ParseTreeType type, final boolean inverted, final ParseTree...parseTrees) {
		super(type);
		this.children = Arrays.asList(parseTrees);
		this.inverted = inverted;
	}
	
	
	/**
	 * Constructs a ChildrenNode with a given type, inversion status and list of child ParseTrees.
	 * If the list of child parsetrees is null, then the node is constructed with an empty list of children.
	 * 
	 * @param type The ParseTreeType of this ChildrenNode.
	 * @param children The list of child ParseTrees for this ChildrenNode.
	 * @param inverted Whether the value of this node should be inverted or not.
	 */
	public ChildrenNode(final ParseTreeType type, final List<ParseTree> children,
			   			final boolean inverted) {
		super(type);
		this.children = children == null? new ArrayList<ParseTree>(0)
									    : new ArrayList<ParseTree>(children);
		this.inverted = inverted;
	}		
	
	@Override
	public int getNumChildren() {
		return children.size();
	}
	/**
	 * 	{@inheritDoc}
	 * 
	 * @throws IndexOutOfBoundsException if the childIndex < 0 or >= the number of children.
	 */
	@Override
	public ParseTree getChild(final int childIndex) {
		return children.get(childIndex);
	}
	
	/**
	 * Returns an iterator over the children of this node.
	 * <p>
	 * Note that the iterator returned is unmodifiable, and a call to remove() on it 
	 * will throw an UnsupportedOperationException.
	 */
	@Override
	public Iterator<ParseTree> iterator() {
		return new ImmutableListIterator<ParseTree>(children);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValueInverted() {
		return inverted;
	}
	

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + getParseTreeType() + ", num children:" + children.size() + ']';  
    }

}
