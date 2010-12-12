/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.matcher.sequence.SequenceMatcherParser;
import net.domesdaybook.reader.ByteReader;
import java.net.URL;
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
public class BitMaskMatcherTest {

    ByteReader bytes;

    public BitMaskMatcherTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        //bytes = mByteReader.getBytes();
    }

    @After
    public void tearDown() {
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNullParse() {
         SequenceMatcherParser.AllBitmaskFromExpression(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testEmptyParse() {
        SequenceMatcherParser.AllBitmaskFromExpression("");
    }


    @Test(expected=IllegalArgumentException.class)
    public void testInvalidBitMaskParse() {
        SequenceMatcherParser.AllBitmaskFromExpression("QW");
        SequenceMatcherParser.AllBitmaskFromExpression("fG");
        SequenceMatcherParser.AllBitmaskFromExpression("001");
        SequenceMatcherParser.AllBitmaskFromExpression("dead");
    }


    /**
     * Test of matchess method, of class AllBitMaskMatcher.
     */
    @Test
    public void testmatches() {
        AllBitMaskMatcher instance;
        long matchFrom;
        boolean result;
        boolean expResult;

        instance = SequenceMatcherParser.AllBitmaskFromExpression("&00"); // matches nothing
        matchFrom = 2L; // 3 bytes in (first byte is at position zero)
        expResult = true; // should be false - this is intentionally to fail this test
        // to prompt me to write a lot of better tests.
        result = instance.matches((byte)0x00);
        assertEquals("Test matching nothing.", expResult, result);

    }


    /**
     * Test of toDroid4RegularExpression method, of class AllBitMaskMatcher.
     */
    @Test
    public void testtoRegularExpression() {
        AllBitMaskMatcher matcher = new AllBitMaskMatcher( (byte) 1 );

        String result = matcher.toRegularExpression(false);
        assertEquals("Testing byte value 1 to regular expression", "&01", result);

        result = matcher.toRegularExpression(true);
        assertEquals("Testing byte value 1 to pretty print regular expression", " &01 ", result);

        matcher = new AllBitMaskMatcher( (byte) 255 );

        result = matcher.toRegularExpression(false);
        assertEquals("Testing byte value 255 to regular expression", "&ff", result);

        result = matcher.toRegularExpression(true);
        assertEquals("Testing byte value 255 to pretty print regular expression", " &ff ", result);

    }


    /**
     * Test of matchess method, of class AllBitMaskMatcher.
     */
    @Test
    public void testNumberOfMatchingBytes() {
        AllBitMaskMatcher instance;
        long matchFrom;
        boolean result;
        boolean expResult;

        instance = new AllBitMaskMatcher((byte) 0x00);
        assertEquals("00000000", 256, instance.getNumberOfMatchingBytes());

        instance = new AllBitMaskMatcher((byte) 0xFF);
        assertEquals("11111111", 1, instance.getNumberOfMatchingBytes());

        instance = new AllBitMaskMatcher((byte) 0x7F);
        assertEquals("01111111", 2, instance.getNumberOfMatchingBytes());

        instance = new AllBitMaskMatcher((byte) 0xFE);
        assertEquals("11111110", 2, instance.getNumberOfMatchingBytes());

        instance = new AllBitMaskMatcher((byte) 0x55);
        assertEquals("01010101", 16, instance.getNumberOfMatchingBytes());

        instance = new AllBitMaskMatcher((byte) 0xAA);
        assertEquals("10101010", 16, instance.getNumberOfMatchingBytes());

    }


}