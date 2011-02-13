/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.expression.compiler.sequence;

import net.domesdaybook.matcher.singlebyte.AllBitMaskMatcher;
import net.domesdaybook.matcher.sequence.CaseInsensitiveStringMatcher;
import net.domesdaybook.matcher.sequence.CaseSensitiveStringMatcher;
import net.domesdaybook.matcher.sequence.CombinedSequenceMatcher;
import net.domesdaybook.expression.parser.ParseException;
import net.domesdaybook.matcher.sequence.ByteSequenceMatcher;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.matcher.sequence.SingleByteSequenceMatcher;
import org.antlr.runtime.tree.CommonTree;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class SequenceMatcherCompilerTest {


    private SequenceMatcherCompiler compiler = new SequenceMatcherCompiler();

    public SequenceMatcherCompilerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of compile method, of class SequenceMatcherCompiler.
     */
    @Test
    public void testBasicCompile() throws Exception {
        SequenceMatcher matcher;
        basicTests("00", 1, ByteSequenceMatcher.class);
        basicTests("01", 1, ByteSequenceMatcher.class);
        basicTests("fF", 1, ByteSequenceMatcher.class);

        basicTests("[00ff]", 1, SingleByteSequenceMatcher.class);
        basicTests("[ff00]", 1, SingleByteSequenceMatcher.class);
        basicTests("[7f80]", 1, SingleByteSequenceMatcher.class);
        basicTests("[807f]", 1, SingleByteSequenceMatcher.class);
        basicTests(" [0102]", 1, SingleByteSequenceMatcher.class);
        
        basicTests("'a string'", 8, CaseSensitiveStringMatcher.class);
        basicTests("`a string`", 8, CaseInsensitiveStringMatcher.class);
        basicTests("01 'a string' 02", 10, ByteSequenceMatcher.class);

        basicTests("0102", 2, ByteSequenceMatcher.class);
        basicTests("01 02", 2, ByteSequenceMatcher.class);
        basicTests("01fd", 2, ByteSequenceMatcher.class);


        basicTests(" [0102] &01", 2, SingleByteSequenceMatcher.class);
        basicTests(" [0102] [^ffee]", 2, SingleByteSequenceMatcher.class);


        basicTests("01fd ef   de", 4, ByteSequenceMatcher.class);

        basicTests("01fd [ef]   de", 4, ByteSequenceMatcher.class);
        
        basicTests("01fd [ef fe]   de", 4, CombinedSequenceMatcher.class);

        basicTests("01{4}", 4, ByteSequenceMatcher.class);

        // Would be better if the compiler realised it was all bytes and
        // combined them into a single bytesequencematcher class, rather
        // than wrapping the two different byte sequence matchers into a
        // combined byte sequence matcher.
        basicTests("010203{6}", 8, CombinedSequenceMatcher.class);

        basicTests("[fffe]", 1, SingleByteSequenceMatcher.class);
        basicTests("[fffe]{5}", 5, SingleByteSequenceMatcher.class);
        
        //FIXME: This test really dies:
        //basicTests("(0102){2}", 4, ByteSequenceMatcher.class);

    }


    private SequenceMatcher basicTests(String expression, int length, Class matcherClass) {
        SequenceMatcher matcher = null;
        try {
            matcher = compiler.compile(expression);
            assertEquals("length of " + expression, length, matcher.length());
            assertEquals("class of " + expression, matcherClass, matcher.getClass());
        } catch (ParseException ex) {
            fail(expression + " " + ex.getMessage());
        }
        return matcher;
    }


}