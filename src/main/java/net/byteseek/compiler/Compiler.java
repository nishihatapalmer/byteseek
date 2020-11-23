/*
 * Copyright Matt Palmer 2009-2013, All rights reserved.
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

package net.byteseek.compiler;

import java.util.Collection;

/**
 * An interface for compilers which compile a String expression 
 * into an object of type T.
 * 
 * @param <T> The type of the object to compile to.
 * @param <S> The abstract syntax tree to compile from using a Parser&lt;S&gt;
 *            
 * @author Matt Palmer
 */
public interface Compiler<T, S> {

	/**
	 * Compiles an expression into an object of type T.
	 * 
	 * @param expression
	 *            The expression to compile.
	 * @return A compiled object of type T.
	 * @throws CompileException
	 *             if an object could not be compiled.
	 */
	public T compile(String expression) throws CompileException;

	/**
	 * Compiles a collection of expressions into a single object of type T.
	 * 
	 * @param expressions
	 *            A collection of expressions to compile.
	 * @return A compiled object of type T
	 * @throws CompileException
	 *             if an object could not be compiled.
	 */
	public T compile(Collection<String> expressions) throws CompileException;

	/**
	 * Compiles a parsed syntax into an object of type T.
	 *
	 * @param ast Abstract syntax tree - the parsed representation of the expression.
	 *            The expression to compile.
	 * @return A compiled object of type T.
	 * @throws CompileException
	 *             if an object could not be compiled.
	 */
	public T compile(final S ast) throws CompileException;


}
