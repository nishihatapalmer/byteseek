/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */


package net.domesdaybook.compiler.multisequence;

import java.util.ArrayList;
import java.util.Collection;
import net.domesdaybook.compiler.CompileException;
import net.domesdaybook.compiler.Compiler;
import net.domesdaybook.compiler.sequence.SequenceMatcherCompiler;
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
