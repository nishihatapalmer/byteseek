/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */


package net.domesdaybook.compiler.multisequence;

import java.util.ArrayList;
import java.util.List;
import net.domesdaybook.compiler.CompileException;
import net.domesdaybook.compiler.Compiler;
import net.domesdaybook.compiler.sequence.SequenceMatcherCompiler;
import net.domesdaybook.matcher.sequence.SequenceMatcher;

/**
 *
 * @author matt
 */
public final class SequenceMatcherListCompiler implements Compiler<List<SequenceMatcher>, List<String>> {

    private static SequenceMatcherListCompiler defaultCompiler;
    public static List<SequenceMatcher> sequenceMatchersFrom(List<String> expressions) throws CompileException {
        defaultCompiler = new SequenceMatcherListCompiler();
        return defaultCompiler.compile(expressions);
    }
    
    
    @Override
    public List<SequenceMatcher> compile(List<String> expressions) throws CompileException {
        List<SequenceMatcher> matchers = new ArrayList<SequenceMatcher>();
        SequenceMatcherCompiler sequenceCompiler = new SequenceMatcherCompiler();
        for (String expression : expressions) {
            SequenceMatcher matcher = sequenceCompiler.compile(expression);
            matchers.add(matcher);
        }
        return matchers;
    }

}
