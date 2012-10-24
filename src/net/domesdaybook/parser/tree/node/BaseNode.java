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

package net.domesdaybook.parser.tree.node;

import java.util.Collections;
import java.util.List;

import net.domesdaybook.parser.ParseException;
import net.domesdaybook.parser.tree.ParseTree;
import net.domesdaybook.parser.tree.ParseTreeType;


public class BaseNode implements ParseTree {

  private final ParseTreeType type;
  private final boolean inverted; 
  
  
  public BaseNode(final ParseTreeType type) {
    this(type, false);
  }
  
  public BaseNode(final ParseTreeType type, final boolean inverted) {
    this.type = type;
    this.inverted = inverted;
  }
  
  @Override
  public ParseTreeType getParseTreeType() {
    return type;
  }

  @Override
  public byte getByteValue() throws ParseException {
    throw new ParseException("No byte value is available.");
  }

  @Override
  public int getIntValue() throws ParseException {
    throw new ParseException("No int value is available.");
  }

  @Override
  public String getTextValue() throws ParseException {
    throw new ParseException("No text value is available.");
  }

  @Override
  public boolean isValueInverted() throws ParseException {
    return inverted;
  }
  
  @Override
  public List<ParseTree> getChildren() {
    return Collections.emptyList();
  }

}
