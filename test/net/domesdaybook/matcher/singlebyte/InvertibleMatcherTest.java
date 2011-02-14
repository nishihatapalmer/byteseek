/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.reader.ByteReader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class InvertibleMatcherTest {

    public InvertibleMatcherTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of isInverted method, of class InvertibleMatcher.
     */
    @Test
    public void testIsInverted() {
        InvertibleMatcher matcher = new InvertibleMatcherImpl(true);
        assertEquals(true, matcher.isInverted());

        matcher = new InvertibleMatcherImpl(false);
        assertEquals(false, matcher.isInverted());
    }

    public class InvertibleMatcherImpl extends InvertibleMatcher {

        public InvertibleMatcherImpl(boolean inverted) {
            super(inverted);
        }

        public boolean matches(byte theByte) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public byte[] getMatchingBytes() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int getNumberOfMatchingBytes() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String toRegularExpression(boolean prettyPrint) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public boolean matches(ByteReader reader, long matchPosition) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

}