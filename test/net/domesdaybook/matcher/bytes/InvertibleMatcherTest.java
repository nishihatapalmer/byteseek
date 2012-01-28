/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.bytes;

import net.domesdaybook.matcher.bytes.InvertibleMatcher;
import net.domesdaybook.reader.Reader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class InvertibleMatcherTest {

    /**
     * 
     */
    public InvertibleMatcherTest() {
    }

    /**
     * 
     * @throws Exception
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    /**
     * 
     * @throws Exception
     */
    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of isInverted method, of abstract class InvertibleMatcher.
     */
    @Test
    public void testIsInverted() {
        InvertibleMatcher matcher = new InvertibleMatcherImpl(InvertibleMatcher.INVERTED);
        assertEquals(true, matcher.isInverted());

        matcher = new InvertibleMatcherImpl(InvertibleMatcher.NOT_INVERTED);
        assertEquals(false, matcher.isInverted());
    }

    /**
     * 
     */
    public static class InvertibleMatcherImpl extends InvertibleMatcher {

        /**
         * 
         * @param inverted
         */
        public InvertibleMatcherImpl(boolean inverted) {
            super(inverted);
        }

        /**
         * 
         * @param theByte
         * @return
         */
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

        public boolean matches(Reader reader, long matchPosition) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public boolean matches(byte[] bytes, int matchPosition) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        /**
         * 
         * @param reader
         * @param matchPosition
         * @return
         */
        public boolean matchesNoBoundsCheck(Reader reader, long matchPosition) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public boolean matchesNoBoundsCheck(byte[] bytes, int matchPosition) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

}