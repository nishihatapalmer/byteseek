/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.matcher.sequence;

import net.domesdaybook.expression.compiler.MatcherSequenceCompiler;
import net.domesdaybook.reader.Bytes;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import net.domesdaybook.matcher.singlebyte.ByteClassRangeMatcher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class ByteSequenceMatcherTest {

    private Bytes mByteReader;

    public ByteSequenceMatcherTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        // get bytes
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of parseSequenceIntoMatchers method, of class ByteMatcher.
     */
    @Test
    public void testParseSequenceIntoMatchers() {
        String byteSequenceSpec = "";
        List expResult = new ArrayList();
        CombinedSequenceMatcher sequence;

        List result = ((CombinedSequenceMatcher) MatcherSequenceCompiler.fromExpression(byteSequenceSpec)).getMatchers();
        assertEquals("Testing a blank sequence.", expResult, result);

        byteSequenceSpec = "0A";
        result = ((CombinedSequenceMatcher) MatcherSequenceCompiler.fromExpression(byteSequenceSpec)).getMatchers();
        assertEquals("Parsing a single hex byte produces one element in the matcher list.", 1, result.size());

        SequenceMatcher matcher = (SequenceMatcher) result.get(0);
        assertEquals("Parsing a single hex byte produces a ByteStringMatcher.", ByteSequenceMatcher.class, matcher.getClass());

        byteSequenceSpec = "[0A]";
        result = ((CombinedSequenceMatcher) MatcherSequenceCompiler.fromExpression(byteSequenceSpec)).getMatchers();
        assertEquals("Parsing a single hex byte range produces one element in the matcher list.", 1, result.size());

        matcher = (SequenceMatcher) result.get(0);
        assertEquals("Parsing a single hex byte range produces a ByteRangeMatcher.", ByteClassRangeMatcher.class, matcher.getClass());

        byteSequenceSpec = "0A1F2B3C";
        result = ((CombinedSequenceMatcher) MatcherSequenceCompiler.fromExpression(byteSequenceSpec)).getMatchers();
        assertEquals("Parsing four hex bytes produces one element in the matcher list.", 1, result.size());

        matcher = (SequenceMatcher) result.get(0);
        assertEquals("Parsing four hex bytes produces a ByteStringMatcher.", ByteSequenceMatcher.class, matcher.getClass());

        byteSequenceSpec = "[0A][1F][2B][3C]";
        result = ((CombinedSequenceMatcher) MatcherSequenceCompiler.fromExpression(byteSequenceSpec)).getMatchers();
        assertEquals("Parsing four single hex byte ranges produces four elements in the matcher list.", 4, result.size());

        for ( int matchIndex = 0; matchIndex < 4; matchIndex++) {
            matcher = (SequenceMatcher) result.get(matchIndex);
            assertEquals("Parsing four hex byte ranges produces a ByteRangeMatcher.", ByteClassRangeMatcher.class, matcher.getClass());
        }

        byteSequenceSpec = "010203[0A][1F][2B]0543[3C]";
        result = ((CombinedSequenceMatcher) MatcherSequenceCompiler.fromExpression(byteSequenceSpec)).getMatchers();
        assertEquals("Parsing a mixed sequence of hex bytes and byte ranges has 6 elements.", 6, result.size());

        matcher = (SequenceMatcher) result.get(0);
        assertEquals("Parsing a mixed sequence of hex bytes and byte ranges produces a ByteStringMatcher.", ByteSequenceMatcher.class, matcher.getClass());
        assertEquals("Parsing a mixed sequence of hex bytes and byte ranges produces a ByteStringMatcher of length 3.", 3, matcher.length());
        matcher = (SequenceMatcher) result.get(1);
        assertEquals("Parsing a mixed sequence of hex bytes and byte ranges produces a ByteRangeMatcher.", ByteClassRangeMatcher.class, matcher.getClass());
        matcher = (SequenceMatcher) result.get(2);
        assertEquals("Parsing a mixed sequence of hex bytes and byte ranges produces a ByteRangeMatcher.", ByteClassRangeMatcher.class, matcher.getClass());
        matcher = (SequenceMatcher) result.get(3);
        assertEquals("Parsing a mixed sequence of hex bytes and byte ranges produces a ByteRangeMatcher.", ByteClassRangeMatcher.class, matcher.getClass());
        matcher = (SequenceMatcher) result.get(4);
        assertEquals("Parsing a mixed sequence of hex bytes and byte ranges produces a ByteStringMatcher.", ByteSequenceMatcher.class, matcher.getClass());
        assertEquals("Parsing a mixed sequence of hex bytes and byte ranges produces a ByteStringMatcher of length 2.", 2, matcher.length());
        matcher = (SequenceMatcher) result.get(5);
        assertEquals("Parsing a mixed sequence of hex bytes and byte ranges produces a ByteRangeMatcher.", ByteClassRangeMatcher.class, matcher.getClass());

    }

}