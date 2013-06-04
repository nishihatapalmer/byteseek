/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.byteseek.matcher.bytes;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.bytes.InvertibleMatcher;

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
		@Override
		public boolean matches(byte theByte) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public byte[] getMatchingBytes() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public int getNumberOfMatchingBytes() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public String toRegularExpression(boolean prettyPrint) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean matches(WindowReader reader, long matchPosition) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean matches(byte[] bytes, int matchPosition) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		/**
		 * 
		 * @param reader
		 * @param matchPosition
		 * @return
		 */
		public boolean matchesNoBoundsCheck(WindowReader reader, long matchPosition) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean matchesNoBoundsCheck(byte[] bytes, int matchPosition) {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}

}