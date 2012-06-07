/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
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

package net.domesdaybook.compiler;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

import net.domesdaybook.parser.AstParser;
import net.domesdaybook.parser.ParseException;

/**
 * An abstract base class for compilers which compile a String expression into
 * an object of type T using an {@link AstParser} to generate the parse tree.
 * 
 * @param <T>
 *            The type of object to build.
 * 
 * @author Matt Palmer
 */
public abstract class AbstractAstCompiler<T> implements Compiler<T> {

	/**
	 * Turns an expression into a parse tree using an {@link AstParser}. Then it
	 * invokes the abstract compile method with the resulting parse-tree, to
	 * build and return a compiled object of type T.
	 * <p>
	 * Classes implementing this abstract class must implement the other
	 * abstract compile method.
	 * 
	 * @param expression
	 *            The expression to compile.
	 * @return A compiled object of type T.
	 * @throws CompileException
	 *             If the expression could not be parsed.
	 */
	@Override
	public T compile(final String expression) throws CompileException {
		try {
			final AstParser parser = new AstParser();
			final Tree tree = parser.parseToAST(expression);
			final CommonTree optimisedAST = (CommonTree) parser
					.optimiseAST(tree);
			return compile(optimisedAST);
		} catch (final ParseException ex) {
			throw new CompileException(ex);
		} catch (final IllegalArgumentException e) {
			throw new CompileException(e);
		}
	}

	/**
	 * An abstract compile method which takes a parse tree created using the
	 * ANTLR parse generator and uses it to build the compiled object of type T.
	 * <p>
	 * Classes implementing this base class must implement this method to
	 * perform the actual compilation.
	 * 
	 * @param ast
	 *            An abstract syntax tree using the ANTLR tree class.
	 * @return A compiled object of type T.
	 * @throws CompileException
	 *             If the abstract syntax tree could not be parsed.
	 */
	public abstract T compile(final CommonTree ast) throws CompileException;

}
