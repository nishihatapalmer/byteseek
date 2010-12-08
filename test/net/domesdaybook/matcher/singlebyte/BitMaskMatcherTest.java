/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.reader.Bytes;
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

    Bytes bytes;

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
         BitMaskMatcher.fromExpression(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testEmptyParse() {
        BitMaskMatcher.fromExpression("");
    }


    @Test(expected=IllegalArgumentException.class)
    public void testInvalidBitMaskParse() {
        BitMaskMatcher.fromExpression("QW");
        BitMaskMatcher.fromExpression("fG");
        BitMaskMatcher.fromExpression("001");
        BitMaskMatcher.fromExpression("dead");
    }


    /**
     * Test of matchesBytes method, of class BitMaskMatcher.
     */
    @Test
    public void testMatchesBytes() {
        BitMaskMatcher instance;
        long matchFrom;
        boolean result;
        boolean expResult;

        instance = BitMaskMatcher.fromExpression("&00"); // matches nothing
        matchFrom = 2L; // 3 bytes in (first byte is at position zero)
        expResult = true; // should be false - this is intentionally to fail this test
        // to prompt me to write a lot of better tests.
        result = instance.matchesBytes(bytes, matchFrom);
        assertEquals("Test matching nothing.", expResult, result);

    }

    /**
     * Test of length method, of class BitMaskMatcher.
     */
    @Test
    public void testLength() {
        final BitMaskMatcher matcher = new BitMaskMatcher( (byte) 1 );
        final int expResult = 1;
        final int result = matcher.length();
        assertEquals("Testing length on bit mask matcher.", expResult, result);
    }


    /**
     * Test of toDroid4RegularExpression method, of class BitMaskMatcher.
     */
    @Test
    public void testtoRegularExpression() {
        BitMaskMatcher matcher = new BitMaskMatcher( (byte) 1 );

        String result = matcher.toRegularExpression(false);
        assertEquals("Testing byte value 1 to regular expression", "&01", result);

        result = matcher.toRegularExpression(true);
        assertEquals("Testing byte value 1 to pretty print regular expression", " &01 ", result);

        matcher = new BitMaskMatcher( (byte) 255 );

        result = matcher.toRegularExpression(false);
        assertEquals("Testing byte value 255 to regular expression", "&ff", result);

        result = matcher.toRegularExpression(true);
        assertEquals("Testing byte value 255 to pretty print regular expression", " &ff ", result);

    }

}