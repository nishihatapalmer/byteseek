/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 *
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
 * 
 */

package net.domesdaybook.compiler;

/**
 * A compiler which can compile expressions of type E into objects of type T
 * in either a forwards or reverse direction.
 * <p>
 * For example, if compiling a list of strings into a Trie structure, you can
 * either compile the Trie from the start of each string (forwards), or produce
 * a reversed Trie by compiling from the end of each string (reversed).
 * 
 * @author Matt Palmer
 */
public interface ReversibleCompiler<T, E> extends Compiler<T, E> {
    
    /**
     * Simple enum for the direction of compilation.
     */
    public static enum Direction {
        FORWARDS,
        REVERSED
    }
    
    
    /**
     * Compiles an expression into an object of type T in the direction specified.
     *
     * @param expression The expression to compile.
     * @param reversed whether to reverse the expressions before compiling.
     * @return An compiled object of type T.
     * @throws CompileException if the expression could not be compiled.
     */
    public T compile(E expression, Direction direction) throws CompileException;
    
}
