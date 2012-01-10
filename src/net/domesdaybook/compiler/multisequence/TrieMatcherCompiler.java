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

package net.domesdaybook.compiler.multisequence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.domesdaybook.automata.wrapper.Trie;
import net.domesdaybook.compiler.CompileException;
import net.domesdaybook.compiler.ReversibleCompiler;
import net.domesdaybook.compiler.ReversibleCompiler.Direction;
import net.domesdaybook.matcher.multisequence.TrieMatcher;
import net.domesdaybook.matcher.sequence.ByteSequenceMatcher;
import net.domesdaybook.matcher.sequence.SequenceMatcher;

/**
 * 
 * 
 * @author Matt Palmer
 */
public final class TrieMatcherCompiler implements ReversibleCompiler<TrieMatcher, SequenceMatcher> {

    // -------------------------------------------------------------------------
    // Static utility methods to build TrieMatchers using the TrieCompiler class   
    // from the net.domesdaybook.compiler.automata package to build the Trie.
    
    
    /**
     * A default static instance of this class to use in the static utility methods.
     */
    private static TrieMatcherCompiler defaultCompiler;
    
    
    /**
     * 
     * @param expression
     * @return
     * @throws CompileException 
     */
    public static TrieMatcher trieMatcherFrom(final Collection<SequenceMatcher> expression) throws CompileException {
        return trieMatcherFrom(expression, Direction.FORWARDS);
    }
   
    
    public static TrieMatcher trieMatcherFrom(final List<byte[]> bytes) throws CompileException {
        return trieMatcherFrom(bytes, Direction.FORWARDS);
    }
    
    
    public static TrieMatcher trieMatcherFrom(final Collection<SequenceMatcher> expression,
                                              final Direction direction) throws CompileException {
        defaultCompiler = new TrieMatcherCompiler();
        return defaultCompiler.compile(expression, direction);
    }
    
    
    public static TrieMatcher trieMatcherFrom(final List<byte[]> bytes,
                                              final Direction direction) throws CompileException {
        defaultCompiler = new TrieMatcherCompiler();
        final List<SequenceMatcher> matchers = new ArrayList<SequenceMatcher>(bytes.size());
        for (final byte[] bytesToUse : bytes) {
            matchers.add(new ByteSequenceMatcher(bytesToUse));
        }
        return defaultCompiler.compile(matchers, direction);
    }    
    
    
    //--------------------------------------------------------------------------
    // Class variables and methods
    
    
    private final ReversibleCompiler<Trie<SequenceMatcher>, SequenceMatcher> compiler;
  
    
    public TrieMatcherCompiler() {
        compiler = new TrieCompiler();
    }
    
    
    public TrieMatcherCompiler(final ReversibleCompiler<Trie<SequenceMatcher>, SequenceMatcher> trieCompiler) {
        if (trieCompiler == null) {
           throw new IllegalArgumentException("Null compiler passed in to TrieMatcherCompiler.");
        }
        compiler = trieCompiler;
    }    
    
    
    @Override
    public TrieMatcher compile(final SequenceMatcher expression) throws CompileException {
        return new TrieMatcher(compiler.compile(expression, Direction.FORWARDS));
    }  
    
    
    @Override
    public TrieMatcher compile(final SequenceMatcher expression, 
                               final Direction direction) throws CompileException {
        return new TrieMatcher(compiler.compile(expression, direction));
    }  
    
    
    @Override
    public TrieMatcher compile(final Collection<SequenceMatcher> expression) throws CompileException {
        return new TrieMatcher(compiler.compile(expression, Direction.FORWARDS));
    }
    

    @Override
    public TrieMatcher compile(final Collection<SequenceMatcher> expression, 
                               final Direction direction) throws CompileException {
        return new TrieMatcher(compiler.compile(expression, direction));
    }
    
    
}
