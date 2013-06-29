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
package net.byteseek.parser.tree.node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.byteseek.parser.tree.ParseTree;
import net.byteseek.parser.tree.ParseTreeType;

/**
 * A ParseTree node which has child ParseTrees.  The value of the node,
 * if any, can be inverted.
 * <p>
 * The ParseTreeType defines what kind of children the node has and how
 * to process them.  For example, a SET type will have child nodes
 * defining the set value, and the set itself can be inverted or not.
 * 
 * @author Matt Palmer.
 */
public class ChildrenNode extends BaseNode {

	private final List<ParseTree> children;
	private final boolean inverted; 
	
	
	/**
	 * Constructs a ChildrenNode with no children and a given type.
	 * 
	 * @param type The ParseTreeType of the node.
	 */
	public ChildrenNode(final ParseTreeType type) {
		this(type,  null, false);
	}
	
	/**
	 * Constructs a ChildrenNode with no children, a given type,
	 * and whether the value should be inverted or not.
	 * 
	 * @param type The ParseTreeType of the node.
	 * @param isInverted Whether the value of the node is inverted or not.
	 */
	public ChildrenNode(final ParseTreeType type, final boolean isInverted) {
		this(type, null, isInverted);
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
	 * Constructs a ChildrenNode with a given type, inversion status and list of child ParseTrees.
	 * 
	 * @param type The ParseTreeType of this ChildrenNode.
	 * @param children The list of child ParseTrees for this ChildrenNode.
	 * @param inverted Whether the value of this node should be inverted or not.
	 */
	public ChildrenNode(final ParseTreeType type, final List<ParseTree> children,
			   			final boolean inverted) {
		super(type);
		this.children = children == null? new ArrayList<ParseTree>(2)
									     : new ArrayList<ParseTree>(children);
		this.inverted = inverted;
	}		
	
	
	/**
	 * Returns the children of this ChildrenNode as a list of ParseTree objects.
	 * The original list held by this class is returned; it is not defensively copied.
	 * Therefore, you should not modify the list returned unless you are very sure
	 * that this is safe to do.
	 * 
	 * @return The internal list of child ParseTree objects.
	 */
	//@Override
	//public List<ParseTree> getChildren() {
	//	return children;
	//}

	
	@Override
	public int getNumChildren() {
		return children.size();
	}
	
	@Override
	public ParseTree getChild(final int childIndex) {
		if (childIndex < 0 || childIndex >= children.size()) {
			throw new IndexOutOfBoundsException("Cannot access child with index " + childIndex + " in node with " + children.size() + " children ");
		}
		return children.get(childIndex);
	}
	
	/**
	 * Adds a new child ParseTree to the list of children in this node
	 * 
	 * @param child The new child ParseTree.
	 * @return boolean Returns true or false in the same way as Collections.add specifies.
	 */
	public boolean addChild(final ParseTree child) {
		return children.add(child);
	}
	
	
	/**
	 * Removes a child ParseTree from the list of children in this node.
	 * 
	 * @param child The child ParseTree to remove.
	 * @return boolean Returns true or false in the same way as Collections.remove specifies.
	 */
	public boolean removeChild(final ParseTree child) {
		return children.remove(child);
	}

	@Override
	public Iterator<ParseTree> iterator() {
		return children.iterator();
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
