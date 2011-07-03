/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
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
     * Test of isInverted method, of abstract class InvertibleMatcher.
     */
    @Test
    public void testIsInverted() {
        InvertibleMatcher matcher = new InvertibleMatcherImpl(InvertibleMatcher.INVERTED);
        assertEquals(true, matcher.isInverted());

        matcher = new InvertibleMatcherImpl(InvertibleMatcher.NOT_INVERTED);
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

        public boolean matches(byte[] bytes, int matchPosition) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

}