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

import net.byteseek.parser.ParseException;
import net.byteseek.parser.tree.ParseTreeType;

/**
 * An immutable ParseTree node that has a byte value.  The value can optionally also be inverted.
 * ByteNodes have a ParseTreeType of ParseTreeType.BYTE by default, but can also be of
 * type ANY_BITMASK and ALL_BITMASK.
 * <p>
 * ByteNodes have no children.  
 * 
 * @author Matt Palmer
 *
 */
public final class ByteNode extends BaseNode {

  private final byte value;
  private final boolean inverted; 
  
  
  private static class NodeCache {
	  
	  static final ByteNode[] values = new ByteNode[256];
	  
	  static {
		  for (int i = 0; i < 256; i++) {
			  values[i] = new ByteNode((byte) (i & 0xFF));
		  }
	  }
	  
  }
  
  public static ByteNode valueOf(final byte value) {
	  return NodeCache.values[value & 0xff];
  }
  
  
  public static ByteNode valueOf(final byte value, final boolean inverted) {
	  return inverted? new ByteNode(value, inverted) : valueOf(value);
  }
  
  
  /**
   * Constructs a ByteNode with the given value.
   * 
   * @param value The value of the ByteNode.
   */
  public ByteNode(final byte value) {
    this(value, false);
  }
  
  
  /**
   * Constructs a ByteNode with the given type and value.
   * @param type The type of the ByteNode
   * @param value The byte value of the byte node.
   */
  public ByteNode(final ParseTreeType type, final byte value) {
	  this(type, value, false);
  }
  

  /**
   * Constructs a ByteNode with the given value and inversion status.
   * 
   * @param value The value of the ByteNode.
   * @param inverted Whether the value should be inverted or not.
   */
  public ByteNode(final byte value, final boolean inverted) {
    this(ParseTreeType.BYTE, value, inverted);
  }

  
  /**
   * Constructs a ByteNode with the given type, value and inversion status.
   * 
   * @param type The type of the ByteNode. Allowed values are BYTE, ANY_BITMASK and ALL_BITMASK.
   * @param value The value of the ByteNode.
   * @param inverted Whether the value should be inverted or not.
   */
  public ByteNode(final ParseTreeType type, final byte value, final boolean inverted) {
	  super(type);
	  this.value = value;
	  this.inverted = inverted;
  }

  
  /**
   * {@inheritDoc}
   */
  @Override
  public byte getByteValue() throws ParseException {
    return value;
  }
  
  
  /**
   * {@inheritDoc}
   */
  @Override
  public int getIntValue() throws ParseException {
    return value & 0xFF;
  }
  
 
  /**
   * Returns whether the value of the byte node should be inverted or not.
   * @return boolean True if the value should be inverted.
   */
  @Override
  public boolean isValueInverted() {
	return inverted;
  }
  

  @Override
  public String toString() {
	  return getClass().getSimpleName() + '[' + getParseTreeType() + ", value:" + value + " inverted: " + inverted + ']';
  }

}
