/*
 * Copyright Matt Palmer 2009-2019, All rights reserved.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.byteseek.parser.ParseException;
import net.byteseek.parser.Parser;
import net.byteseek.utils.ArgUtils;

/**
 * An abstract base class for compilers which compile a String expression into
 * an object of type T using a {@link Parser} to generate an abstract syntax
 * tree of type S.
 * 
 * @param <T> The type of object to build.
 * @param <S> The syntax to compile from using a Parser&lt;S&gt;
 * 
 * @author Matt Palmer
 */
public abstract class AbstractCompiler<T, S> implements Compiler<T, S> {

	private final Parser<S> parser;

	protected AbstractCompiler(final Parser<S> parser) {
		ArgUtils.checkNullObject(parser, "parser");
		this.parser = parser;
	}

	/**
	 * Turns an expression into a parse tree using a {@link Parser}. Then
	 * it invokes the abstract compile method with the resulting parse-tree, to
	 * build and return a compiled object of type T.
	 * <p>
	 * Classes implementing this abstract class must implement the other
	 * abstract compile method.
	 * 
	 * @param expression
	 *            The expression to compile.
	 * @return A compiled object of type T.
	 * @throws CompileException
	 *             If a problem occurs during compilation.
	 */
	@Override
	public T compile(final String expression) throws CompileException {
		try {
			return compile(parser.parse(expression));
		} catch (final ParseException pex) {
			throw new CompileException(pex.getMessage(), pex);
		}
	}

	@Override
	public T compile(final Collection<String> expressions) throws CompileException {
		List<S> parsedExpressions = new ArrayList<S>(expressions.size());
		String currentExpression = "";
		try {
			// Build the syntax trees for each of the expressions:
			for (final String expression : expressions) {
				currentExpression = expression;
				parsedExpressions.add(parser.parse(expression));
			}

			// Place them all under an alternatives node:
			final S joinedTrees = joinExpressions(parsedExpressions);

			// Compile the resulting single syntax tree:
			return compile(joinedTrees);

		} catch (ParseException pex) {
			throw new CompileException(pex.getMessage(), pex);
		}
	}

	/**
	 * A compile method which takes a parse tree and uses it to build the
	 * compiled object of type T.
	 * <p>
	 * Classes implementing this base class must implement this method to
	 * perform the actual compilation.
	 * 
	 * @param ast An abstract syntax tree
	 * @return A compiled object of type T.
	 * @throws CompileException
	 *             If a problem occurred during compilation.
	 */
	@Override
	public T compile(final S ast) throws CompileException {
		if (ast == null) {
			throw new CompileException("Null abstract syntax tree passed in.");
		}
		try {
			return doCompile(ast);
		} catch (IllegalArgumentException e) {
			throw new CompileException(e.getMessage(), e);
		}
	}

	/**
	 * An abstract compile method which takes a parse tree and uses it to 
	 * build the compiled object of type T.
	 * <p>
	 * Classes implementing this base class must implement this method to
	 * perform the actual compilation.
	 * 
	 * @param ast An abstract syntax tree
	 * @return A compiled object of type T.
	 * @throws ParseException
	 *             If a problem occurred during parsing.
	 * @throws CompileException
	 *             If a problem occurred during compilation.
	 */
	protected abstract T doCompile(S ast) throws CompileException;

	/**
	 * Joins a list of experssions into a single abstract syntax tree.
	 *
	 * @param expressions A list of expressions to join together.
	 * @return S Another abstract syntax tree containing the join of all the trees passed in.
	 * @throws CompileException An exception which occurs during compilation of the expressions.
	 */
	protected abstract S joinExpressions(List<S> expressions) throws CompileException;

}
