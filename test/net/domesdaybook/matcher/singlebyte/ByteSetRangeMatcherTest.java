/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.matcher.singlebyte;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class ByteSetRangeMatcherTest {

    public ByteSetRangeMatcherTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }


    /**
     * Test of matches method, of class ByteRangeMatcher.
     */
    @Test
    public void testMatches_byte() {
        ByteRangeMatcher matcher = new ByteRangeMatcher(0, 0, InvertibleMatcher.NOT_INVERTED);
        validateMatcher(matcher, 0, 0, InvertibleMatcher.NOT_INVERTED);




    }

    /**
     * Test of toRegularExpression method, of class ByteRangeMatcher.
     */
    @Test
    public void testToRegularExpression() {

    }

    /**
     * Test of getMatchingBytes method, of class ByteRangeMatcher.
     */
    @Test
    public void testGetMatchingBytes() {

    }

    /**
     * Test of getNumberOfMatchingBytes method, of class ByteRangeMatcher.
     */
    @Test
    public void testGetNumberOfMatchingBytes() {

    }

    private void validateMatcher(ByteRangeMatcher matcher, int i, int i0, boolean INVERTED) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}