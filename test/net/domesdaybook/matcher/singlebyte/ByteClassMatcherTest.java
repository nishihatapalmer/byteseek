/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.reader.Bytes;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
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

    Bytes bytes;

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
     * Test of matchesBytes method, of class ByteClassMatcher.
     */
    @Test
    public void testMatchesBytes() {

        fail("The test case is a prototype.");
    }

    @Test
    public void testMatchesByte() {
        fail("The test case is a prototype.");
    }

    // Tests for illegal arguments passed to parser:

   @Test(expected=IllegalArgumentException.class)
    public void testNullParse() {
        ByteClassMatcher.fromExpression(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testEmptyParse() {
        ByteClassMatcher.fromExpression("");
    }


    @Test
    public void testEmptyClassParse() {
        SequenceMatcher result = ByteClassMatcher.fromExpression("[]");
        assertEquals("empty byte class returns null matcher", null, result );
    }

    @Test
    public void testEmptyNegatedClassParse() {
        SequenceMatcher result = ByteClassMatcher.fromExpression("[!]");
        assertEquals("empty negated byte class returns null matcher", null, result );
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNoStartingSquareBracketParse() {
        ByteClassMatcher.fromExpression("00]");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNoEndingSquareBracketParse() {
         ByteClassMatcher.fromExpression("[1F:2B");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidHexMinByteParse() {
        ByteClassMatcher.fromExpression("[QW]");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNoColonForRangeParse() {
        ByteClassMatcher.fromExpression("[1A-1C]");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidHexMaxByteParse() {
        ByteClassMatcher.fromExpression("[1A:Y]");
    }

    /**
     * Test of length method, of class ByteClassMatcher.
     */
    @Test
    public void testLength() {
        SequenceMatcher matcher = ByteClassMatcher.fromExpression("[00]");
        assertEquals( "Testing for a length of one with a single byte class", 1, matcher.length());

        matcher = ByteClassMatcher.fromExpression("[00010203:88dead]");
        assertEquals( "Testing for a length of one with a multiple byte class", 1, matcher.length());
    }


    /**
     * Test of parseByteClass method, of class ByteClassMatcher.
     */
    @Test
    public void testParseByteClass() {

        // Test the number of bytes parsed into the class:

        // Test the simplest case of a single byte
        // (don't really need a byte class for this but it is valid)
        ByteClassMatcher matcher = ByteClassMatcher.fromExpression( "[01]" );
        assertEquals( "Testing parsing one hex byte gives one byte value to match", 1, matcher.getNumBytesInClass());

        // Test two different bytes using different case for hex:
        matcher = ByteClassMatcher.fromExpression( "[03e1]" );
        assertEquals( "Testing parsing two hex bytes '03e1' gives two byte values to match", 2, matcher.getNumBytesInClass());
        matcher = ByteClassMatcher.fromExpression( "[dead]" );
        assertEquals( "Testing parsing two hex bytes 'dead' gives two byte values to match", 2, matcher.getNumBytesInClass());
        matcher = ByteClassMatcher.fromExpression( "[DeAd]" );
        assertEquals( "Testing parsing two hex bytes 'DeAd' gives two byte values to match", 2, matcher.getNumBytesInClass());

        // Test the same byte specified twice (valid spec but redundant):
        matcher = ByteClassMatcher.fromExpression( "[FFFF]");
        assertEquals( "Testing parsing two equal bytes 'FFFF' gives one byte value to match", 1, matcher.getNumBytesInClass());
        

        // Test parsing of negation [! ...] of a byte class:

        matcher = ByteClassMatcher.fromExpression( "[!00]" );
        assertEquals( "Testing for negation of a single byte class", true, matcher.isNegated() );
        assertEquals( "Testing for number of bytes in negated single byte class", 255, matcher.numBytesInClass);

        matcher = ByteClassMatcher.fromExpression( "[02]" );
        assertEquals( "Testing for no negation of a single byte class", false, matcher.isNegated() );

        matcher = ByteClassMatcher.fromExpression( "[!00010203:88dead]" );
        assertEquals( "Testing for negation of a multiple byte class", true, matcher.isNegated() );
        assertEquals( "Testing for number of bytes in negated 139 byte class", 117, matcher.numBytesInClass);

        matcher = ByteClassMatcher.fromExpression("[02:040709ffee77:78]");
        assertEquals( "Testing for no negation of a multiple byte class", false, matcher.isNegated() );
        assertEquals( "Testing for number of bytes in 10 byte class", 10, matcher.numBytesInClass);
    }


    // Test use outside of file bounds:

    @Test(expected=IndexOutOfBoundsException.class)
    public void testErrorOnMatchesBytesOutsideFile() {
        final ByteClassMatcher instance = ByteClassMatcher.fromExpression("[01]");
        instance.matchesBytes(bytes, 100000000L);
    }    

    /**
     * Test of toRegularExpression method, of class ByteClassMatcher.
     */
    @Test
    public void testToRegularExpression() {

        fail("The test case is a prototype.");
    }

}