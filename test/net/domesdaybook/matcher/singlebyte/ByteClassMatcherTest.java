/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.matcher.sequence.SequenceMatcherParser;
import net.domesdaybook.reader.ByteReader;
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
public class ByteClassMatcherTest {

    ByteReader bytes;

    public ByteClassMatcherTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        //bytes = ???
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of matchess method, of class NegatableMatcher.
     */
    @Test
    public void testmatchess() {

        fail("The test case is a prototype.");
    }

    @Test
    public void testmatches() {
        fail("The test case is a prototype.");
    }

    // Tests for illegal arguments passed to parser:

   @Test(expected=IllegalArgumentException.class)
    public void testNullParse() {
        SequenceMatcherParser.byteClassFromExpression(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testEmptyParse() {
        SequenceMatcherParser.byteClassFromExpression("");
    }


    @Test
    public void testEmptyClassParse() {
        NegatableMatcher result = SequenceMatcherParser.byteClassFromExpression("[]");
        assertEquals("empty byte class returns null matcher", null, result );
    }

    @Test
    public void testEmptyNegatedClassParse() {
        NegatableMatcher result = SequenceMatcherParser.byteClassFromExpression("[!]");
        assertEquals("empty negated byte class returns null matcher", null, result );
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNoStartingSquareBracketParse() {
        SequenceMatcherParser.byteClassFromExpression("00]");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNoEndingSquareBracketParse() {
         SequenceMatcherParser.byteClassFromExpression("[1F:2B");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidHexMinByteParse() {
        SequenceMatcherParser.byteClassFromExpression("[QW]");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNoColonForRangeParse() {
        SequenceMatcherParser.byteClassFromExpression("[1A-1C]");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidHexMaxByteParse() {
        SequenceMatcherParser.byteClassFromExpression("[1A:Y]");
    }



    /**
     * Test of parseByteClass method, of class NegatableMatcher.
     */
    @Test
    public void testParseByteClass() {

        // Test the number of bytes parsed into the class:

        // Test the simplest case of a single byte
        // (don't really need a byte class for this but it is valid)
        NegatableMatcher matcher = SequenceMatcherParser.byteClassFromExpression( "[01]" );
        assertEquals( "Testing parsing one hex byte gives one byte value to match", 1, matcher.getNumberOfMatchingBytes());

        // Test two different bytes using different case for hex:
        matcher = SequenceMatcherParser.byteClassFromExpression( "[03e1]" );
        assertEquals( "Testing parsing two hex bytes '03e1' gives two byte values to match", 2, matcher.getNumberOfMatchingBytes());
        matcher = SequenceMatcherParser.byteClassFromExpression( "[dead]" );
        assertEquals( "Testing parsing two hex bytes 'dead' gives two byte values to match", 2, matcher.getNumberOfMatchingBytes());
        matcher = SequenceMatcherParser.byteClassFromExpression( "[DeAd]" );
        assertEquals( "Testing parsing two hex bytes 'DeAd' gives two byte values to match", 2, matcher.getNumberOfMatchingBytes());

        // Test the same byte specified twice (valid spec but redundant):
        matcher = SequenceMatcherParser.byteClassFromExpression( "[FFFF]");
        assertEquals( "Testing parsing two equal bytes 'FFFF' gives one byte value to match", 1, matcher.getNumberOfMatchingBytes());
        

        // Test parsing of negation [! ...] of a byte class:

        matcher = SequenceMatcherParser.byteClassFromExpression( "[!00]" );

        assertEquals( "Testing for negation of a single byte class", true, matcher.isNegated() );
        assertEquals( "Testing for number of bytes in negated single byte class", 255, matcher.getNumberOfMatchingBytes());

        matcher = SequenceMatcherParser.byteClassFromExpression( "[02]" );
        assertEquals( "Testing for no negation of a single byte class", false, matcher.isNegated() );

        matcher = SequenceMatcherParser.byteClassFromExpression( "[!00010203:88dead]" );
        assertEquals( "Testing for negation of a multiple byte class", true, matcher.isNegated() );
        assertEquals( "Testing for number of bytes in negated 139 byte class", 117, matcher.getNumberOfMatchingBytes());

        matcher = SequenceMatcherParser.byteClassFromExpression("[02:040709ffee77:78]");
        assertEquals( "Testing for no negation of a multiple byte class", false, matcher.isNegated() );
        assertEquals( "Testing for number of bytes in 10 byte class", 10, matcher.getNumberOfMatchingBytes());
    }


    /**
     * Test of toRegularExpression method, of class NegatableMatcher.
     */
    @Test
    public void testToRegularExpression() {

        fail("The test case is a prototype.");
    }

}