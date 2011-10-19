/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
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


package net.domesdaybook.compiler.multisequence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.domesdaybook.compiler.CompileException;
import net.domesdaybook.compiler.Compiler;
import net.domesdaybook.compiler.sequence.SequenceMatcherCompiler;
import net.domesdaybook.matcher.sequence.ByteSequenceMatcher;
import net.domesdaybook.matcher.sequence.SequenceMatcher;

/**
 * Compiles a collection of String regular expressions into a collection of 
 * {@link SequenceMatcher}s.
 * <p>
 * Unlike most byteSeek compilers, this compiler does not produce a single 
 * output which can match something.  It is really a convenience class to
 * simplify compiling a list of SequenceMatchers from a list of expressions.
 * 
 * @author Matt Palmer
 */
public final class SequenceMatchersCompiler implements Compiler<Collection<SequenceMatcher>, Collection<String>> {

    private static SequenceMatchersCompiler defaultCompiler;

    /**
     * A static utility method to produce a collection of {@link SequenceMatcher}s 
     * from a collection of strings containing byteSeek regular expressions
     * defining each sequence.
     * 
     * @param expressions A collection of Strings containing byteSeek regular expressions.
     * @return A collection of SequenceMatchers which match the expressions.
     * @throws CompileException if the expressions could not be compiled.
     */
    public static Collection<SequenceMatcher> sequenceMatchersFrom(final Collection<String> expressions) throws CompileException {
        defaultCompiler = new SequenceMatchersCompiler();
        return defaultCompiler.compile(expressions);
    }
    
    
    /**
     * A static utility method to produce a collection of {@link SequenceMatcher}s
     * from a list of byte arrays.
     * 
     * @param bytes A list of byte arrays
     * @return A collection of SequenceMatchers which match the list of byte arrays.
     */
    public static Collection<SequenceMatcher> sequenceMatchersFrom(final List<byte[]> bytes) {
        final List<SequenceMatcher> matchers = new ArrayList<SequenceMatcher>(bytes.size());
        for (final byte[] bytesToUse : bytes) {
            final SequenceMatcher byteMatcher = new ByteSequenceMatcher(bytesToUse);
            matchers.add(byteMatcher);
        }     
        return matchers;
    }
    
    
    /**
     * Compiles a collection of strings containing byteSeek regular expressions
     * into a list of {@link SequenceMatcher}s which match those expressions.
     * <p>
     * Note that the regular expressions must be confined to syntax which produces
     * fixed length sequences.  No syntax which would produce a variable length
     * sequence can be compiled.  For example, you cannot specify quantifiers such
     * as ? (zero to one), * (zero to many) or + (one to many) in the expressions.
     * 
     * @param expressions A collection of strings containing byteSeek regular expressions.
     * @return a list of SequenceMatchers which match the expressions.
     * @throws CompileException if the expressions could not be compiled.
     */
    @Override
    public Collection<SequenceMatcher> compile(final Collection<String> expressions) throws CompileException {
        final Collection<SequenceMatcher> matchers = new ArrayList<SequenceMatcher>();
        final SequenceMatcherCompiler sequenceCompiler = new SequenceMatcherCompiler();
        for (final String expression : expressions) {
            final SequenceMatcher matcher = sequenceCompiler.compile(expression);
            matchers.add(matcher);
        }
        return matchers;
    }

}
