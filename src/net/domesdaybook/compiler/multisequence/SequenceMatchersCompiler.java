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
 *  
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
 *
 * @author matt
 */
public final class SequenceMatchersCompiler implements Compiler<Collection<SequenceMatcher>, Collection<String>> {

    private static SequenceMatchersCompiler defaultCompiler;
    
    
    public static Collection<SequenceMatcher> sequenceMatchersFrom(final Collection<String> expressions) throws CompileException {
        defaultCompiler = new SequenceMatchersCompiler();
        return defaultCompiler.compile(expressions);
    }
    
    
    public static Collection<SequenceMatcher> sequenceMatchersFrom(final List<byte[]> bytes) {
        List<SequenceMatcher> matchers = new ArrayList<SequenceMatcher>(bytes.size());
        for (final byte[] bytesToUse : bytes) {
            final SequenceMatcher byteMatcher = new ByteSequenceMatcher(bytesToUse);
            matchers.add(byteMatcher);
        }     
        return matchers;
    }
    
    
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
