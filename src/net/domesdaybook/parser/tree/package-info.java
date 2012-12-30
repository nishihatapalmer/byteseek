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
 
 /**
  * net.domesdaybook.parser.tree is a package containing an interface for an 
  * Abstract Syntax Tree, and a sub-package containing an implementation of it.
  * A static utility class {@link ParseTreeUtils} simplifies the parsing of some of 
  * the abstract syntax tree structure and values. 
  * <p>
  * The {@link ParseTree} interface defines a tree node which can have child tree nodes,
  * and which may return a byte, integer or text value.  Byte values and nodes with children
  * may also be inverted, which means they should match all other values than the one they define.
  * <p>
  * In addition, an enumeration {@link ParseTreeType} defines all the different types of
  * ParseTree node. These range from simple nodes like BYTE, having a byte value, to
  * SET, which may have many child nodes defining a set of bytes, to imperative nodes
  * like REPEAT or ONE_TO_MANY.  Their structure is defined in the JavaDoc for each 
  * enumeration value.
  */
 package net.domesdaybook.parser.tree;