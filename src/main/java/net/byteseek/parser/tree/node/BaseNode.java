/*
 * Copyright Matt Palmer 2012-2019, All rights reserved.
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

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.byteseek.parser.ParseException;
import net.byteseek.parser.ParseInfo;
import net.byteseek.parser.tree.ParseTree;
import net.byteseek.parser.tree.ParseTreeType;

/**
 * An immutable base implementation of ParseTree, that only has a type.
 * This implementation is not particularly useful to instantiate directly, although that is not
 * prohibited.  This class is intended to be sub-classed, with particular types of nodes implementing
 * their own specific functionality, with this class providing default behaviour.
 * <p>
 * Asking for a byte, int or text value will throw a {@link ParseException}.  Subclasses which
 * provide those values need only override the specific method for their type of value.
 * It will always provide an empty list of child nodes, and false if asked if the value is inverted.
 *
 * @author Matt Palmer
 */
public class BaseNode implements ParseTree {

    private final int position;
    private final String expression;
    private final ParseTreeType type;

    public static final ParseTree ANY_NODE = new BaseNode(ParseTreeType.ANY);

    /**
     * Constructs a base node of the given type.
     *
     * @param type The type of the parse tree node.
     */
    public BaseNode(final ParseTreeType type) {
        this(ParseInfo.NO_INFO, type);
    }

    /**
     * Constructs a base node of the given type.
     *
     * @param info ParseInfo about where in a string the parsing is taking place.
     * @param type The type of the parse tree node.
     */
    public BaseNode(final ParseInfo info, final ParseTreeType type) {
        this.type = type;
        this.expression = info.getString();
        this.position = info.getPosition();
    }

    @Override
    public ParseTreeType getParseTreeType() {
        return type;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Always throws a {@link ParseException}.
     */
    @Override
    public byte getByteValue() throws ParseException {
        throw new ParseException("No byte value is available for the node " + this, this);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Always throws a {@link ParseException}.
     */
    @Override
    public int getIntValue() throws ParseException {
        throw new ParseException("No int value is available for the node " + this, this);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Always throws a {@link ParseException}.
     */
    @Override
    public String getTextValue() throws ParseException {
        throw new ParseException("No text value is available for the node " + this, this);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Always throws a {@link ParseException}.
     */
    @Override
    public Charset getTextEncoding() throws ParseException {
        throw new ParseException("No charset encoding is available for the node " + this, this);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Always returns false.
     */
    @Override
    public boolean isValueInverted() {
        return false;
    }

    @Override
    public int getNumChildren() {
        return 0;
    }

    @Override
    public ParseTree getChild(final int childIndex) {
        throw new IndexOutOfBoundsException("There are no children in the node: " + this);
    }

    @Override
    public List<ParseTree> getChildren() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public String getString() {
        return expression;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + type + ')';
    }

    @Override
    public Iterator<ParseTree> iterator() {
        return Collections.<ParseTree>emptyList().iterator();
    }

}
